import { expect, test } from '@playwright/test';
import {
  fetchScreens,
  gotoAndWaitForData,
  readNumericTestId,
  toStatusLabel,
} from '../helpers/design-hub';

test.beforeEach(async ({ page }) => {
  await gotoAndWaitForData(page);
});

async function getSidebarScreenMetadata(page: import('@playwright/test').Page): Promise<Array<{ surfaceId: string; module: string; designStatus: string }>> {
  return page
    .getByTestId('screen-list')
    .locator('button[data-testid^="screen-"]:visible')
    .evaluateAll((elements) =>
      elements.map((element) => ({
        surfaceId: (element.getAttribute('data-testid') ?? '').replace(/^screen-/, ''),
        module: element.getAttribute('data-module') ?? '',
        designStatus: element.getAttribute('data-design-status') ?? '',
      }))
    );
}

test('module filter narrows the visible screen list', async ({ page, request }) => {
  const screens = await fetchScreens(request);
  const totalScreens = screens.length;
  const moduleCounts = new Map<string, number>();

  for (const screen of screens) {
    moduleCounts.set(screen.module, (moduleCounts.get(screen.module) ?? 0) + 1);
  }

  const moduleEntry = [...moduleCounts.entries()].find(([, count]) => count > 0 && count < totalScreens);
  expect(moduleEntry, 'Expected at least one module that is a subset of the full screen list').toBeTruthy();

  const [moduleName, moduleCount] = moduleEntry!;
  await page.getByTestId('module-filter').getByRole('button', { name: moduleName }).click();

  await expect(page.getByTestId('screen-list')).toContainText(`Screens (${moduleCount})`);
  const visibleScreens = await getSidebarScreenMetadata(page);
  expect(visibleScreens).toHaveLength(moduleCount);
  expect(visibleScreens.every((screen) => screen.module === moduleName)).toBeTruthy();
});

test('status filter narrows the visible screen list', async ({ page, request }) => {
  const screens = await fetchScreens(request);
  const totalScreens = screens.length;
  const statusCounts = new Map<string, number>();

  for (const screen of screens) {
    statusCounts.set(screen.designStatus, (statusCounts.get(screen.designStatus) ?? 0) + 1);
  }

  const statusEntry = [...statusCounts.entries()].find(([, count]) => count > 0 && count < totalScreens);
  expect(statusEntry, 'Expected at least one design status that is a subset of the full screen list').toBeTruthy();

  const [status, statusCount] = statusEntry as [typeof screens[number]['designStatus'], number];
  await page.getByTestId('status-filter').getByRole('button', { name: toStatusLabel(status) }).click();

  await expect(page.getByTestId('screen-list')).toContainText(`Screens (${statusCount})`);
  const visibleScreens = await getSidebarScreenMetadata(page);
  expect(visibleScreens).toHaveLength(statusCount);
  expect(visibleScreens.every((screen) => screen.designStatus === status)).toBeTruthy();
});

test('search filter narrows and restores the visible list', async ({ page, request }) => {
  const screens = await fetchScreens(request);
  const target = screens.find((screen) => screen.label.trim().length >= 4) ?? screens[0];

  expect(target, 'Expected seeded data to contain at least one screen').toBeTruthy();

  const searchTerm = target!.label.slice(0, 4);
  await page.getByTestId('search-input').fill(searchTerm);
  await expect(page.getByTestId(`screen-${target!.surfaceId}`)).toBeVisible();

  await page.getByTestId('search-input').fill('definitely-not-a-real-screen');
  await expect(page.getByTestId('screen-list-empty')).toBeVisible();

  await page.getByTestId('search-input').fill('');
  const totalCount = await readNumericTestId(page, 'stat-total');
  await expect(page.getByTestId('screen-list')).toContainText(`Screens (${totalCount})`);
  const restoredCount = (await getSidebarScreenMetadata(page)).length;
  expect(restoredCount).toBe(totalCount);
});
