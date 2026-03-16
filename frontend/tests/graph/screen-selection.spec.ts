import { expect, test } from '@playwright/test';
import { getFirstVisibleScreen, gotoAndWaitForData, selectScreen } from '../helpers/design-hub';

test.beforeEach(async ({ page }) => {
  await gotoAndWaitForData(page);
});

test('selecting a screen updates detail context and canvas selection', async ({ page }) => {
  const { surfaceId, label } = await getFirstVisibleScreen(page);

  await selectScreen(page, surfaceId);

  await expect(page.getByTestId('detail-context-name')).toHaveText(label);
  await expect(page.getByTestId('prop-design')).toBeVisible();
  await expect(page.locator(`[data-testid="node-${surfaceId}"]`)).toHaveClass(/canvas__node--selected/);

  await page.getByTestId('detail-close').click();
  await expect(page.getByTestId('no-selection')).toBeVisible();
});
