import type { FullConfig } from '@playwright/test';
import { ensureBackendUp } from './helpers/backend';

async function globalSetup(_config: FullConfig): Promise<void> {
  await ensureBackendUp();
}

export default globalSetup;
