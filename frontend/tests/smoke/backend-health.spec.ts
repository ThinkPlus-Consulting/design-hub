import { expect, test } from '@playwright/test';
import { backendBaseUrl } from '../helpers/backend';

test.describe.configure({ mode: 'serial' });

test('backend actuator reports UP', async ({ request }) => {
  const response = await request.get(`${backendBaseUrl}/actuator/health`);
  expect(response.ok()).toBeTruthy();

  const payload = (await response.json()) as { status?: string };
  expect(payload.status).toBe('UP');
});
