import { expect, test } from '@playwright/test';
import { backendBaseUrl } from '../helpers/backend';

test('system shell graph workspace renders live seeded data', async ({ page, request }) => {
  const graphResponse = await request.get(`${backendBaseUrl}/api/v1/system-shell-graph/graph`);
  expect(graphResponse.ok()).toBeTruthy();

  const graph = (await graphResponse.json()) as {
    graphScope?: string;
    nodes?: Array<unknown>;
    relationships?: Array<unknown>;
  };
  expect(graph.graphScope).toBe('SYSTEM_FRONTEND_GRAPH');
  expect(graph.nodes?.length ?? 0).toBeGreaterThan(0);
  expect(graph.relationships?.length ?? 0).toBeGreaterThan(0);

  await page.goto('/');

  await expect(page.getByRole('heading', { name: 'Frontend' })).toBeVisible();
  await expect(page.getByRole('button', { name: 'X-Ray Agent' })).toBeVisible();
  await expect(page.getByRole('button', { name: 'Expand all' })).toBeVisible();
  await expect(page.getByRole('heading', { name: 'ObjectsLogic' })).toBeVisible();
});
