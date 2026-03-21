import { expect, test } from '@playwright/test';
import {
  fetchChannels,
  fetchChannelTraversal,
  fetchInteractions,
  fetchScreens,
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
  const interactions = await fetchInteractions(request);
  const channels = await fetchChannels(request);
  const candidate = channels.find((channel) => channel.touchpointCount > 0) ?? channels[0];

  expect(candidate, 'Expected seeded data to contain a channel with touchpoints').toBeTruthy();
  const traversal = await fetchChannelTraversal(request, candidate!.channelCode);
  const targetScreenId = traversal.screens[0]?.id
    ?? traversal.touchpoints[0]?.targetScreen?.id
    ?? interactions[0]?.surfaceId;

  expect(targetScreenId, 'Expected a screen to be reachable for interaction verification').toBeTruthy();
  await selectScreen(page, targetScreenId!);

  await page.getByTestId('tab-interactions').click();
  await expect(page.locator('[data-testid^="ix-"]').first()).toBeVisible();

  await page.getByTestId('tab-touchpoints').click();
  await page.getByTestId(`channel-select-${candidate!.channelCode}`).click();
  await expect(page.getByTestId('channel-title')).toHaveText(traversal.displayName);
  await expect(page.locator('[data-testid^="tp-"]').first()).toBeVisible();
});
