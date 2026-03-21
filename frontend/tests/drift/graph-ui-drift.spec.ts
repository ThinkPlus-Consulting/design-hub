import { expect, test } from '@playwright/test';
import {
  fetchDeliveryStories,
  fetchScreenReadiness,
  fetchScreens,
  fetchStoryTraceability,
  gotoAndWaitForData,
} from '../helpers/design-hub';

test.beforeEach(async ({ page }) => {
  await gotoAndWaitForData(page);
});

test('screen detail stays aligned with graph-backed role and story links', async ({ page, request }) => {
  const screens = await fetchScreens(request);
  const candidate = screens.find((screen) => screen.roleKeys.length > 0 && screen.storyRefs.length > 0) ?? screens[0];

  await page.getByTestId(`screen-${candidate.surfaceId}`).click();
  await expect(page.getByTestId('prop-surfaceId')).toHaveText(candidate.surfaceId);

  for (const roleKey of candidate.roleKeys) {
    await expect(page.getByTestId(`role-chip-${roleKey}`)).toBeVisible();
  }

  const storyCards = page.locator('[data-testid="story-item"]');
  await expect(storyCards).toHaveCount(candidate.storyRefs.length);

  for (const storyId of candidate.storyRefs) {
    await expect(page.getByTestId('screen-detail')).toContainText(storyId);
  }
});

test('delivery detail counts stay aligned with the delivery aggregate', async ({ page, request }) => {
  const stories = await fetchDeliveryStories(request);
  const candidate = stories.find((story) => story.screens.length > 0 || story.apis.length > 0) ?? stories[0];

  await page.getByTestId('tab-delivery').click();
  await page.getByTestId(`delivery-story-${candidate.storyId}`).click();

  await expect(page.getByTestId('delivery-detail')).toContainText(candidate.storyId);
  await expect(page.getByTestId('delivery-screens').locator('.delivery__card')).toHaveCount(candidate.screens.length);
  await expect(page.getByTestId('delivery-apis').locator('.delivery__card')).toHaveCount(candidate.apis.length);
});

test('traceability and verification panels stay aligned with backend diagnostics', async ({ page, request }) => {
  const stories = await fetchDeliveryStories(request);
  const screens = await fetchScreens(request);
  const story = stories.find((candidate) => candidate.screens.length > 0) ?? stories[0];
  const screen = screens.find((candidate) => candidate.surfaceId === story.screens[0]?.surfaceId) ?? screens[0];
  const traceability = await fetchStoryTraceability(request, story.storyId);
  const screenReadiness = await fetchScreenReadiness(request, screen.surfaceId);

  await page.getByTestId(`screen-${screen.surfaceId}`).click();
  await page.getByTestId('tab-delivery').click();
  await page.getByTestId(`delivery-story-${story.storyId}`).click();

  await page.getByTestId('tab-traceability').click();
  await expect(page.getByTestId('traceability-node-story')).toContainText(traceability.story.id);
  await expect(page.getByTestId('traceability-screens')).toContainText(traceability.screens[0]?.id ?? screen.surfaceId);

  await page.getByTestId('tab-verification').click();
  await expect(page.getByTestId('verification-story-readiness')).toContainText(story.storyId);
  await expect(page.getByTestId('verification-screen-readiness')).toContainText(screenReadiness.artifactId);
  await expect(page.getByTestId('verification-screen-readiness')).toContainText(screenReadiness.completenessLevel);
});
