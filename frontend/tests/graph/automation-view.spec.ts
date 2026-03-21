import { expect, test } from '@playwright/test';
import { fetchAgentPack, fetchDeliveryStories, gotoAndWaitForData } from '../helpers/design-hub';

test.beforeEach(async ({ page }) => {
  await gotoAndWaitForData(page);
});

test('automation tab renders the story agent pack export', async ({ page, request }) => {
  const stories = await fetchDeliveryStories(request);
  expect(stories.length, 'Expected seeded delivery stories to be available').toBeGreaterThan(0);

  const storiesWithPacks = await Promise.all(
    stories.map(async (story) => ({
      story,
      pack: await fetchAgentPack(request, story.storyId),
    }))
  );
  const completeStories = storiesWithPacks.filter((entry) => entry.pack.completeness.complete);
  const incompleteStory = storiesWithPacks.find((entry) => !entry.pack.completeness.complete);

  expect(completeStories.length, 'Expected at least one complete agent pack in the seeded slice').toBeGreaterThan(0);
  expect(incompleteStory, 'Expected at least one incomplete agent pack in the seeded slice').toBeTruthy();

  await page.getByTestId('tab-delivery').click();

  for (const { story, pack } of storiesWithPacks) {
    await page.getByTestId('tab-delivery').click();
    await page.getByTestId(`delivery-story-${story.storyId}`).click();
    await page.getByTestId('tab-automation').click();

    await expect(page.getByTestId('automation-panel')).toBeVisible();
    await expect(page.getByTestId('automation-pack-id')).toContainText(pack.packId);
    await expect(page.getByTestId('automation-completeness')).toContainText(
      pack.completeness.complete ? 'Complete' : 'Needs work'
    );
    await expect(page.getByTestId('automation-readiness-score')).toContainText(
      String(pack.completeness.readinessScore)
    );

    if (pack.completeness.missingConcerns.length > 0) {
      await expect(page.getByTestId('automation-readiness')).toContainText(pack.completeness.missingConcerns[0]);
    }

    if (pack.components.length > 0) {
      await expect(page.getByTestId(`automation-component-${pack.components[0].id}`)).toContainText(
        pack.components[0].displayName
      );
    }

    if (pack.codeTargets.length > 0) {
      await expect(page.getByTestId(`automation-code-target-${pack.codeTargets[0].id}`)).toContainText(
        pack.codeTargets[0].displayName
      );
    }

    if (pack.testCases.length > 0) {
      await expect(page.getByTestId('automation-tests')).toContainText(pack.testCases[0].displayName);
    }

    await expect(page.getByTestId('automation-applications')).toBeVisible();
    await expect(page.getByTestId('automation-policies')).toBeVisible();
    await expect(page.getByTestId('automation-conventions')).toBeVisible();
    await expect(page.getByTestId('automation-quality-constraints')).toBeVisible();

    if (pack.applications.length > 0) {
      await expect(page.getByTestId(`automation-application-${pack.applications[0].id}`)).toContainText(
        pack.applications[0].name ?? pack.applications[0].id
      );
    }

    if (pack.policies.length > 0) {
      await expect(page.getByTestId(`automation-policy-${pack.policies[0].id}`)).toContainText(
        pack.policies[0].name ?? pack.policies[0].id
      );
    }
  }
});
