import { expect, Page, test } from '@playwright/test';
import { prepareVisualPage, selectBaselineContext } from '../helpers/visual';

async function stabilizeAutomationPanel(page: Page): Promise<void> {
  await page.getByTestId('automation-pack-id').evaluate((element) => {
    element.textContent = 'PACK-US-AI-090 · v1 · Stable visual capture';
  });
}

async function prepareDetailBaseline(page: Page, options?: { locale?: 'en' | 'ar' }): Promise<void> {
  await prepareVisualPage(page, options);
  await selectBaselineContext(page);
}

test('delivery detail panel matches the baseline', async ({ page }) => {
  await prepareDetailBaseline(page);
  await expect(page.getByTestId('detail-panel')).toHaveScreenshot('detail-panel-delivery.png', {
    animations: 'disabled',
  });
});

test('traceability detail panel matches the baseline', async ({ page }) => {
  await prepareDetailBaseline(page);
  await page.getByTestId('tab-traceability').click();
  await expect(page.getByTestId('detail-panel')).toHaveScreenshot('detail-panel-traceability.png', {
    animations: 'disabled',
  });
});

test('automation detail panel matches the baseline', async ({ page }) => {
  await prepareDetailBaseline(page);
  await page.getByTestId('tab-automation').click();
  await stabilizeAutomationPanel(page);
  await expect(page.getByTestId('detail-panel')).toHaveScreenshot('detail-panel-automation.png', {
    animations: 'disabled',
  });
});

test('verification detail panel matches the baseline', async ({ page }) => {
  await prepareDetailBaseline(page);
  await page.getByTestId('tab-verification').click();
  await expect(page.getByTestId('detail-panel')).toHaveScreenshot('detail-panel-verification.png', {
    animations: 'disabled',
  });
});

test('delivery detail panel matches the Arabic RTL baseline', async ({ page }) => {
  await prepareDetailBaseline(page, { locale: 'ar' });
  await expect(page.getByTestId('detail-panel')).toHaveScreenshot('detail-panel-delivery-ar.png', {
    animations: 'disabled',
  });
});

test.describe('mobile', () => {
  test.use({
    viewport: { width: 390, height: 844 },
    isMobile: true,
    hasTouch: true,
  });

  test('delivery detail panel matches the mobile baseline', async ({ page }) => {
    await prepareDetailBaseline(page);
    await expect(page.getByTestId('detail-panel')).toHaveScreenshot('detail-panel-delivery-mobile.png', {
      animations: 'disabled',
    });
  });

  test('delivery detail panel matches the Arabic RTL mobile baseline', async ({ page }) => {
    await prepareDetailBaseline(page, { locale: 'ar' });
    await expect(page.getByTestId('detail-panel')).toHaveScreenshot('detail-panel-delivery-mobile-ar.png', {
      animations: 'disabled',
    });
  });
});
