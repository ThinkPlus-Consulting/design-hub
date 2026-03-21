import { VerificationSnapshot } from '../../../models';

export const VERIFICATION_SNAPSHOT: VerificationSnapshot = {
  generatedAt: '2026-03-20',
  checks: [
    {
      key: 'build',
      label: 'Angular production build',
      status: 'PASS',
      command: 'npm run build',
      detail: 'Verified on Node 25.8.1. The existing bundle-budget warning remains, but the build completes cleanly.',
      scope: 'frontend workspace',
    },
    {
      key: 'e2e',
      label: 'Playwright semantic and UI suite',
      status: 'PASS',
      command: 'npm run test:e2e',
      detail:
        '44 Chromium scenarios currently cover shell, delivery, traceability, benchmark, verification, channel, persona-first journey, architecture, automation, drift, visual, mobile interaction, keyboard accessibility, external sync breadth, and Arabic/RTL shell plus graph-backed detail behavior against the live backend.',
      scope: 'frontend/tests',
    },
    {
      key: 'token-audit',
      label: 'EMSIST token audit',
      status: 'PASS',
      command: 'npm run check:design-tokens',
      detail:
        'The Design Hub UI surface and broader frontend source tree are checked for hardcoded color drift, with raw color literals allowed only in approved theme token-source files, and required EMSIST ThinkPLUS root tokens verified in src/styles.scss.',
      scope: 'frontend/src with approved token-source exceptions',
    },
  ],
};
