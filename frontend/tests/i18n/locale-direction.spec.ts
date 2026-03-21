import { expect, test } from '@playwright/test';
import { fetchScreens, gotoAndWaitForData, selectScreen, setLocale } from '../helpers/design-hub';

test.describe('localization and RTL groundwork', () => {
  test.beforeEach(async ({ page }) => {
    await gotoAndWaitForData(page);
  });

  test('switching to Arabic updates shell labels and document direction', async ({ page }) => {
    await setLocale(page, 'ar');

    await expect(page.getByTestId('sidebar-title')).toHaveText('مركز التصميم');
    await expect(page.getByTestId('sidebar-search-icon')).toHaveText('بحث');
    await expect(page.getByTestId('module-filter')).toContainText('الوحدة');
    await expect(page.getByTestId('detail-context-label')).toHaveText('الشاشة:');
    await expect(page.getByTestId('tab-delivery')).toHaveText('التسليم');
  });

  test('selected locale persists across reloads', async ({ page }) => {
    await setLocale(page, 'ar');
    await page.reload();
    await expect(page.getByTestId('design-hub-root')).toBeVisible();
    await expect(page.getByTestId('locale-option-ar')).toHaveAttribute('aria-pressed', 'true');
    await expect(page.getByTestId('sidebar-title')).toHaveText('مركز التصميم');
    await expect
      .poll(async () => page.evaluate(() => document.documentElement.dir))
      .toBe('rtl');
  });

  test('switching to Arabic preserves the graph-backed detail workflow', async ({ page, request }) => {
    const screens = await fetchScreens(request);
    const screen = screens.find((candidate) => candidate.storyRefs.length > 0 && candidate.roleKeys.length > 0);

    expect(screen, 'Expected seeded data to contain a screen with graph-backed stories and roles').toBeTruthy();
    await selectScreen(page, screen!.surfaceId);

    await expect(page.getByTestId('prop-surfaceId')).toHaveText(screen!.surfaceId);
    await expect(page.getByTestId('story-item').first()).toBeVisible();

    await setLocale(page, 'ar');

    await expect(page.getByTestId('detail-context-label')).toHaveText('الشاشة:');
    await expect(page.getByTestId('detail-context-name')).toHaveText(screen!.label);
    await expect(page.getByTestId('tab-delivery')).toHaveText('التسليم');
    await expect(page.getByTestId('tab-traceability')).toHaveText('التتبع');
    await expect(page.getByTestId('tab-verification')).toHaveText('التحقق');
    await expect(page.getByTestId('prop-surfaceId')).toHaveText(screen!.surfaceId);
    await expect(page.getByTestId('story-item').first()).toBeVisible();
  });
});
