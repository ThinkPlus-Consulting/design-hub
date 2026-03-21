import { expect, test } from '@playwright/test';
import { fetchDeliveryStories, gotoAndWaitForData } from '../helpers/design-hub';

test.beforeEach(async ({ page }) => {
  await gotoAndWaitForData(page);
});

test('delivery tab renders grouped stories and detail diagnostics', async ({ page, request }) => {
  const stories = await fetchDeliveryStories(request);
  expect(stories.length, 'Expected seeded delivery stories to be available').toBeGreaterThan(0);

  const candidate = stories.find((story) => story.screens.length > 0) ?? stories[0];
  const externalCandidate = stories.find((story) => story.externalArtifacts.length > 0) ?? candidate;

  await page.getByTestId('tab-delivery').click();
  await expect(page.getByTestId('delivery-panel')).toBeVisible();
  await expect(page.locator('[data-testid^="delivery-group-"]').first()).toBeVisible();

  await page.getByTestId(`delivery-story-${candidate.storyId}`).click();
  await expect(page.getByTestId('delivery-detail')).toBeVisible();
  await expect(page.getByTestId('delivery-story-id')).toHaveText(candidate.storyId);
  await expect(page.getByTestId('delivery-readiness')).toBeVisible();

  if (candidate.screens.length > 0) {
    await expect(page.getByTestId('delivery-screens')).toContainText(candidate.screens[0].surfaceId);
  }

  await page.getByTestId(`delivery-story-${externalCandidate.storyId}`).click();
  const externalArtifact = externalCandidate.externalArtifacts[0];
  await expect(page.getByTestId(`delivery-external-${externalArtifact.externalId}`)).toBeVisible();
  await page.getByTestId(`delivery-external-${externalArtifact.externalId}`).click();
  await expect(page.getByTestId('delivery-external-detail')).toBeVisible();
  await expect(page.getByTestId('delivery-external-detail')).toContainText(externalArtifact.key ?? externalArtifact.externalId);
  await expect(page.getByTestId('delivery-external-custom-fields')).toBeVisible();
});
