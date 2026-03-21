import { expect, test } from '@playwright/test';
import { gotoAndWaitForData } from '../helpers/design-hub';

test.beforeEach(async ({ page }) => {
  await gotoAndWaitForData(page);
});

test('verification controls support keyboard activation', async ({ page }) => {
  await page.locator('[data-testid^="screen-"]').first().click();
  await page.getByTestId('tab-delivery').click();
  await page.locator('[data-testid^="delivery-story-"]').first().click();

  const verificationTab = page.getByTestId('tab-verification');
  await verificationTab.focus();
  await expect(verificationTab).toBeFocused();
  await page.keyboard.press('Enter');

  await expect(page.getByTestId('verification-panel')).toBeVisible();

  const azurePoll = page.getByTestId('verification-poll-azure_devops');
  await azurePoll.focus();
  await expect(azurePoll).toBeFocused();
  await page.keyboard.press('Enter');

  await expect(page.getByTestId('verification-poll-feedback-azure_devops')).toContainText(/SKIPPED|SUCCESS/);

  const azureFilter = page.getByTestId('verification-history-filter-azure_devops');
  await azureFilter.focus();
  await expect(azureFilter).toBeFocused();
  await page.keyboard.press('Space');

  await expect(azureFilter).toHaveAttribute('aria-pressed', 'true');
  await expect(page.getByTestId('verification-sync-history')).toContainText('Azure DevOps · POLL');
});
