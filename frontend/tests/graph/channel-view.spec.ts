import { expect, test } from '@playwright/test';
import {
  fetchChannels,
  fetchChannelTraversal,
  gotoAndWaitForData,
} from '../helpers/design-hub';

test.beforeEach(async ({ page }) => {
  await gotoAndWaitForData(page);
});

test('channel view renders channel coverage, touchpoints, and persona reach', async ({ page, request }) => {
  const channels = await fetchChannels(request);
  const channel = channels.find((candidate) => candidate.touchpointCount > 0) ?? channels[0];

  expect(channel, 'Expected seeded data to contain at least one channel').toBeTruthy();
  const traversal = await fetchChannelTraversal(request, channel!.channelCode);

  await page.getByTestId('tab-touchpoints').click();
  await page.getByTestId(`channel-select-${channel!.channelCode}`).click();

  await expect(page.getByTestId('channel-title')).toHaveText(traversal.displayName);
  await expect(page.getByTestId('channel-touchpoints')).toContainText(traversal.touchpoints[0]?.label ?? '');
  await expect(page.getByTestId('channel-screens')).toContainText(traversal.screens[0]?.displayName ?? '');

  if (traversal.personaReach.length > 0) {
    await expect(page.getByTestId('channel-personas')).toContainText(traversal.personaReach[0].displayName);
  } else {
    await expect(page.getByTestId('channel-personas')).toContainText('No personas reachable yet.');
  }
});
