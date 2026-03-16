import { expect, test } from '@playwright/test';
import { gotoAndWaitForData, readNumericTestId } from '../helpers/design-hub';

test.beforeEach(async ({ page }) => {
  await gotoAndWaitForData(page);
});

test('app shell renders with live seeded data', async ({ page }) => {
  await expect(page.getByTestId('design-hub-root')).toBeVisible();
  await expect(page.getByTestId('sidebar')).toBeVisible();
  await expect(page.getByTestId('flow-canvas')).toBeVisible();
  await expect(page.getByTestId('detail-panel')).toBeVisible();

  await expect
    .poll(async () => readNumericTestId(page, 'stat-total'))
    .toBeGreaterThan(0);

  const totalScreens = await readNumericTestId(page, 'stat-total');
  expect(totalScreens).toBeGreaterThan(0);

  const screenCount = await page.locator('[data-testid^="screen-"][data-module][data-design-status]').count();
  expect(screenCount).toBeGreaterThan(0);

  const nodeCount = await page.locator('[data-testid^="node-"]').count();
  expect(nodeCount).toBeGreaterThan(0);

  await expect(page.getByTestId('zoom-level')).toHaveText(/\d+%/);
});
