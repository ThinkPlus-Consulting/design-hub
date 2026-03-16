export const backendBaseUrl = process.env.PLAYWRIGHT_BACKEND_URL ?? 'http://localhost:8091';

const HEALTHCHECK_PATH = '/actuator/health';

function sleep(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

export async function ensureBackendUp(timeoutMs = 15_000): Promise<void> {
  const startedAt = Date.now();
  let lastFailure = 'unreachable';

  while (Date.now() - startedAt < timeoutMs) {
    try {
      const response = await fetch(`${backendBaseUrl}${HEALTHCHECK_PATH}`);
      const payload = (await response.json()) as { status?: string };

      if (response.ok && payload.status === 'UP') {
        return;
      }

      lastFailure = `HTTP ${response.status} payload ${JSON.stringify(payload)}`;
    } catch (error) {
      lastFailure = error instanceof Error ? error.message : String(error);
    }

    await sleep(1_000);
  }

  throw new Error(
    `Backend precondition failed at ${backendBaseUrl}${HEALTHCHECK_PATH}. `
    + `Start the Spring backend on port 8091 before running Playwright. `
    + `Last failure: ${lastFailure}`
  );
}
