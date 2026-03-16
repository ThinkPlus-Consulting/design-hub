import { expect, test } from '@playwright/test';
import { fetchJourneys, getFirstVisibleScreen, gotoAndWaitForData, selectScreen } from '../helpers/design-hub';

test.beforeEach(async ({ page }) => {
  await gotoAndWaitForData(page);
});

test('journeys tab expands real journey steps', async ({ page, request }) => {
  const journeys = await fetchJourneys(request);
  const journey = journeys.find((candidate) => candidate.steps.length > 0);

  expect(journey, 'Expected seeded data to contain a journey with steps').toBeTruthy();

  await page.getByTestId('tab-journeys').click();
  await page.getByTestId(`journey-toggle-${journey!.journeyId}`).click();

  await expect(page.getByTestId('journey-steps')).toBeVisible();
  await expect(page.locator(`[data-testid^="journey-step-"]`)).toHaveCount(journey!.steps.length);
});

test('crosscutting tab renders selected screen metadata', async ({ page }) => {
  const { surfaceId } = await getFirstVisibleScreen(page);

  await selectScreen(page, surfaceId);
  await page.getByTestId('tab-crosscutting').click();

  await expect(page.getByTestId('crosscutting-panel')).toBeVisible();
  await expect(page.getByTestId('xc-wcag')).not.toHaveText('');
});
