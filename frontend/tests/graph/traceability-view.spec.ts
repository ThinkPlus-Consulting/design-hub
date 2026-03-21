import { expect, test } from '@playwright/test';
import {
  fetchDeliveryStories,
  fetchStoryTraceability,
  gotoAndWaitForData,
} from '../helpers/design-hub';

test.beforeEach(async ({ page }) => {
  await gotoAndWaitForData(page);
});

test('traceability tab renders the live upstream and downstream story graph', async ({ page, request }) => {
  const stories = await fetchDeliveryStories(request);
  expect(stories.length, 'Expected seeded delivery stories to be available').toBeGreaterThan(0);

  const candidate = stories[0];
  const traceability = await fetchStoryTraceability(request, candidate.storyId);

  await page.getByTestId('tab-delivery').click();
  await page.getByTestId(`delivery-story-${candidate.storyId}`).click();
  await page.getByTestId('tab-traceability').click();

  await expect(page.getByTestId('traceability-panel')).toBeVisible();
  await expect(page.getByTestId('traceability-story-id')).toHaveText(candidate.storyId);
  await expect(page.getByTestId('traceability-node-story')).toContainText(candidate.storyId);

  if (traceability.objective) {
    await expect(page.getByTestId('traceability-node-objective')).toContainText(traceability.objective.id);
  }

  if (traceability.screens.length > 0) {
    await expect(page.getByTestId('traceability-screens')).toContainText(traceability.screens[0].id);
  }

  if (traceability.missingSpineSegments.length > 0) {
    await expect(page.getByTestId('traceability-missing')).toContainText(traceability.missingSpineSegments[0]);
  }
});
