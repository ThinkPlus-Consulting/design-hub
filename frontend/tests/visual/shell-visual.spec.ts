import { expect, test } from '@playwright/test';
import { prepareVisualPage } from '../helpers/visual';

test.describe('visual shell baselines', () => {
  test('desktop shell matches the baseline', async ({ page }) => {
    await prepareVisualPage(page);
    await expect(page).toHaveScreenshot('design-hub-shell-desktop.png', {
      animations: 'disabled',
      fullPage: true,
    });
  });

  test('desktop shell matches the Arabic RTL baseline', async ({ page }) => {
    await prepareVisualPage(page, { locale: 'ar' });
    await expect(page).toHaveScreenshot('design-hub-shell-desktop-ar.png', {
      animations: 'disabled',
      fullPage: true,
    });
  });

  test.describe('mobile', () => {
    test.use({ viewport: { width: 390, height: 844 } });

    test('mobile shell matches the baseline', async ({ page }) => {
      await prepareVisualPage(page);
      await expect(page).toHaveScreenshot('design-hub-shell-mobile.png', {
        animations: 'disabled',
        fullPage: true,
      });
    });

    test('mobile shell matches the Arabic RTL baseline', async ({ page }) => {
      await prepareVisualPage(page, { locale: 'ar' });
      await expect(page).toHaveScreenshot('design-hub-shell-mobile-ar.png', {
        animations: 'disabled',
        fullPage: true,
      });
    });
  });
});
