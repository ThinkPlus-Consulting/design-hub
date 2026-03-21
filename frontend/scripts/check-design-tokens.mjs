import { readFile, readdir } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const currentDir = path.dirname(fileURLToPath(import.meta.url));
const rootDir = path.resolve(currentDir, '..');

const approvedTokenSourceFiles = new Set([
  'src/styles.scss',
  'src/app/core/theme/default-preset.scss',
  'src/app/core/theme/default-preset.ts',
]);

const requiredRootTokens = [
  '--tp-primary',
  '--tp-primary-dark',
  '--tp-primary-light',
  '--tp-warning',
  '--tp-danger',
  '--tp-surface',
  '--tp-bg',
  '--tp-text',
  '--tp-text-dark',
  '--tp-text-muted',
  '--tp-border',
  '--tp-focus-ring',
  '--tp-elevation-default',
  '--tp-elevation-hover',
  '--tp-touch-target-min-size',
  '--tp-white',
  '--tp-primary-bg',
  '--tp-primary-bg-hover',
  '--tp-danger-bg',
  '--tp-danger-border',
  '--tp-toast-success-bg',
  '--tp-toast-warn-bg',
  '--tp-warning-dark',
  '--nm-radius',
  '--nm-shadow-dark',
  '--nm-shadow-light',
];

const colorPatterns = [
  { label: 'hex color', regex: /#[0-9a-fA-F]{3,8}\b/g },
  { label: 'rgb color', regex: /\brgba?\(/g },
  { label: 'hsl color', regex: /\bhsla?\(/g },
];

async function collectRelativeFiles(directory) {
  const entries = await readdir(directory, { withFileTypes: true });
  const files = await Promise.all(
    entries.map(async (entry) => {
      const entryPath = path.join(directory, entry.name);
      if (entry.isDirectory()) {
        return collectRelativeFiles(entryPath);
      }

      return [path.relative(rootDir, entryPath).split(path.sep).join('/')];
    })
  );

  return files.flat().sort();
}

function lineNumberForIndex(content, index) {
  return content.slice(0, index).split('\n').length;
}

const uiAuditRoot = path.join(rootDir, 'src/app/features/design-hub');
const auditedUiFiles = (await collectRelativeFiles(uiAuditRoot)).filter(
  (relativeFile) => relativeFile.endsWith('.component.ts') || relativeFile.endsWith('.page.ts')
);
const frontendSourceFiles = (await collectRelativeFiles(path.join(rootDir, 'src'))).filter(
  (relativeFile) => relativeFile.endsWith('.ts') || relativeFile.endsWith('.scss')
);
const guardedSourceFiles = frontendSourceFiles.filter((relativeFile) => !approvedTokenSourceFiles.has(relativeFile));
const issues = [];

for (const relativeFile of guardedSourceFiles) {
  const filePath = path.join(rootDir, relativeFile);
  const content = await readFile(filePath, 'utf8');

  for (const pattern of colorPatterns) {
    for (const match of content.matchAll(pattern.regex)) {
      issues.push({
        file: relativeFile,
        line: lineNumberForIndex(content, match.index ?? 0),
        label: pattern.label,
        value: match[0],
      });
    }
  }
}

const stylesPath = path.join(rootDir, 'src/styles.scss');
const stylesContent = await readFile(stylesPath, 'utf8');
const missingTokens = requiredRootTokens.filter((token) => !stylesContent.includes(token));

if (issues.length > 0 || missingTokens.length > 0) {
  console.error('Design token audit failed.');

  if (issues.length > 0) {
    console.error('\nHardcoded color usage detected outside approved token source files:');
    for (const issue of issues) {
      console.error(`- ${issue.file}:${issue.line} ${issue.label} ${issue.value}`);
    }
  }

  if (missingTokens.length > 0) {
    console.error('\nMissing required root tokens in src/styles.scss:');
    for (const token of missingTokens) {
      console.error(`- ${token}`);
    }
  }

  process.exit(1);
}

console.log('Design token audit passed.');
console.log(`- audited Design Hub UI files: ${auditedUiFiles.length}`);
console.log(`- guarded frontend source files: ${guardedSourceFiles.length}`);
console.log(`- approved token source files: ${approvedTokenSourceFiles.size}`);
console.log(`- required root tokens present: ${requiredRootTokens.length}/${requiredRootTokens.length}`);
console.log('- hardcoded color values outside approved token source files: 0');
