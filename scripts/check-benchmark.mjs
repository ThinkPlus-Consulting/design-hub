import { writeFile } from 'node:fs/promises';
import http from 'node:http';
import https from 'node:https';

const benchmarkUrl = process.env.BENCHMARK_URL ?? 'http://localhost:8091/api/v1/graph/benchmark';
const outputPath = process.env.BENCHMARK_OUTPUT_PATH;

function readNumber(name, fallback) {
  const raw = process.env[name];
  if (!raw) {
    return fallback;
  }

  const value = Number(raw);
  if (Number.isNaN(value)) {
    throw new Error(`Environment variable ${name} must be numeric, received: ${raw}`);
  }

  return value;
}

const thresholds = {
  minOverall: readNumber('BENCHMARK_MIN_OVERALL', 90),
  minCoveredNodeTypes: readNumber('BENCHMARK_MIN_COVERED_NODE_TYPES', 12),
  minTotalNodes: readNumber('BENCHMARK_MIN_TOTAL_NODES', 120),
  minAttributeDepth: readNumber('BENCHMARK_MIN_ATTRIBUTE_DEPTH', 90),
  minRelationshipCoverage: readNumber('BENCHMARK_MIN_RELATIONSHIP_COVERAGE', 85),
  minSourceTraceability: readNumber('BENCHMARK_MIN_SOURCE_TRACEABILITY', 100),
  minQueryability: readNumber('BENCHMARK_MIN_QUERYABILITY', 100),
};

function fail(message) {
  console.error(message);
  process.exit(1);
}

function fetchJson(urlString) {
  const url = new URL(urlString);
  const client = url.protocol === 'https:' ? https : http;

  return new Promise((resolve, reject) => {
    const request = client.request(
      url,
      {
        method: 'GET',
        headers: {
          accept: 'application/json',
        },
      },
      (response) => {
        const chunks = [];
        response.setEncoding('utf8');
        response.on('data', (chunk) => chunks.push(chunk));
        response.on('end', () => {
          const body = chunks.join('');

          if (!response.statusCode || response.statusCode < 200 || response.statusCode >= 300) {
            reject(new Error(`Benchmark endpoint ${urlString} returned HTTP ${response.statusCode ?? 'unknown'}.`));
            return;
          }

          try {
            resolve(JSON.parse(body));
          } catch (error) {
            reject(new Error(`Benchmark endpoint ${urlString} returned invalid JSON.`));
          }
        });
      },
    );

    request.on('error', reject);
    request.end();
  });
}

function findDimension(payload, name) {
  return payload.summary?.dimensions?.find((dimension) => dimension.dimension === name);
}

try {
  const payload = await fetchJson(benchmarkUrl);

  if (outputPath) {
    await writeFile(outputPath, `${JSON.stringify(payload, null, 2)}\n`, 'utf8');
  }

  if (!payload?.summary || !Array.isArray(payload.summary.dimensions)) {
    fail(`Benchmark endpoint ${benchmarkUrl} returned an unexpected payload shape.`);
  }

  const summary = payload.summary;
  const dimensions = {
    attributeDepth: findDimension(payload, 'attributeDepth'),
    relationshipCoverage: findDimension(payload, 'relationshipCoverage'),
    sourceTraceability: findDimension(payload, 'sourceTraceability'),
    queryability: findDimension(payload, 'queryability'),
  };

  const failures = [];

  if (summary.overallScore < thresholds.minOverall) {
    failures.push(`overallScore ${summary.overallScore.toFixed(1)} < ${thresholds.minOverall.toFixed(1)}`);
  }

  if (summary.coveredNodeTypes < thresholds.minCoveredNodeTypes) {
    failures.push(`coveredNodeTypes ${summary.coveredNodeTypes} < ${thresholds.minCoveredNodeTypes}`);
  }

  if (summary.totalNodes < thresholds.minTotalNodes) {
    failures.push(`totalNodes ${summary.totalNodes} < ${thresholds.minTotalNodes}`);
  }

  for (const [name, dimension] of Object.entries(dimensions)) {
    if (!dimension) {
      failures.push(`missing dimension ${name}`);
      continue;
    }

    if (dimension.status !== 'GREEN') {
      failures.push(`${name} status ${dimension.status} != GREEN`);
    }
  }

  if (dimensions.attributeDepth && dimensions.attributeDepth.score < thresholds.minAttributeDepth) {
    failures.push(
      `attributeDepth ${dimensions.attributeDepth.score.toFixed(1)} < ${thresholds.minAttributeDepth.toFixed(1)}`,
    );
  }

  if (
    dimensions.relationshipCoverage &&
    dimensions.relationshipCoverage.score < thresholds.minRelationshipCoverage
  ) {
    failures.push(
      `relationshipCoverage ${dimensions.relationshipCoverage.score.toFixed(1)} < ${thresholds.minRelationshipCoverage.toFixed(1)}`,
    );
  }

  if (
    dimensions.sourceTraceability &&
    dimensions.sourceTraceability.score < thresholds.minSourceTraceability
  ) {
    failures.push(
      `sourceTraceability ${dimensions.sourceTraceability.score.toFixed(1)} < ${thresholds.minSourceTraceability.toFixed(1)}`,
    );
  }

  if (dimensions.queryability && dimensions.queryability.score < thresholds.minQueryability) {
    failures.push(`queryability ${dimensions.queryability.score.toFixed(1)} < ${thresholds.minQueryability.toFixed(1)}`);
  }

  console.log(
    [
      `Benchmark gate summary`,
      `overall=${summary.overallScore.toFixed(1)}`,
      `coveredNodeTypes=${summary.coveredNodeTypes}`,
      `totalNodes=${summary.totalNodes}`,
      `attributeDepth=${dimensions.attributeDepth?.score.toFixed(1) ?? 'n/a'}`,
      `relationshipCoverage=${dimensions.relationshipCoverage?.score.toFixed(1) ?? 'n/a'}`,
      `sourceTraceability=${dimensions.sourceTraceability?.score.toFixed(1) ?? 'n/a'}`,
      `queryability=${dimensions.queryability?.score.toFixed(1) ?? 'n/a'}`,
    ].join(' | '),
  );

  if (failures.length > 0) {
    fail(`Benchmark integrity gate failed:\n- ${failures.join('\n- ')}`);
  }
} catch (error) {
  fail(`Benchmark integrity gate failed: ${error instanceof Error ? error.message : String(error)}`);
}
