import { expect, test } from '@playwright/test';
import {
  fetchInteractions,
  fetchScreens,
  fetchTouchpoints,
  gotoAndWaitForData,
  selectScreen,
} from '../helpers/design-hub';

test.beforeEach(async ({ page }) => {
  await gotoAndWaitForData(page);
});

test('selected screen renders linked stories and roles', async ({ page, request }) => {
  const screens = await fetchScreens(request);
  const screen = screens.find((candidate) => candidate.storyRefs.length > 0 && candidate.roleKeys.length > 0);

  expect(screen, 'Expected seeded data to contain a screen with stories and roles').toBeTruthy();
  await selectScreen(page, screen!.surfaceId);

  await expect(page.getByTestId('prop-roles')).not.toHaveText(/^(None)?$/);
  await expect(page.getByTestId('story-item').first()).toBeVisible();
});

test('selected screen renders gap records when present', async ({ page, request }) => {
  const screens = await fetchScreens(request);
  const screen = screens.find((candidate) => (candidate.gaps?.length ?? 0) > 0);

  expect(screen, 'Expected seeded data to contain a screen with gaps').toBeTruthy();
  await selectScreen(page, screen!.surfaceId);

  await expect(page.getByTestId('gap-item').first()).toBeVisible();
});

test('interaction and touchpoint tabs render linked graph data', async ({ page, request }) => {
  const touchpoints = await fetchTouchpoints(request);
  const interactions = await fetchInteractions(request);
  const candidate = touchpoints.find((touchpoint) =>
    interactions.some((interaction) => interaction.surfaceId === touchpoint.surfaceId)
  );

  expect(candidate, 'Expected seeded data to contain a screen with touchpoints and interactions').toBeTruthy();
  await selectScreen(page, candidate!.surfaceId);

  await page.getByTestId('tab-interactions').click();
  await expect(page.locator('[data-testid^="ix-"]').first()).toBeVisible();

  await page.getByTestId('tab-touchpoints').click();
  await expect(page.locator('[data-testid^="tp-"]').first()).toBeVisible();
});
