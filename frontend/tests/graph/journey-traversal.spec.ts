import { expect, test } from '@playwright/test';
import {
  fetchPersonas,
  fetchJourneys,
  fetchJourneyTraversal,
  fetchPersonaTraversal,
  getFirstVisibleScreen,
  gotoAndWaitForData,
  selectScreen,
} from '../helpers/design-hub';

test.beforeEach(async ({ page }) => {
  await gotoAndWaitForData(page);
});

test('journeys tab expands real journey steps', async ({ page, request }) => {
  const journeys = await fetchJourneys(request);
  const journey = journeys.find((candidate) => candidate.steps.length > 0 && Boolean(candidate.personaId));

  expect(journey, 'Expected seeded data to contain a journey with steps and persona context').toBeTruthy();
  const traversal = await fetchJourneyTraversal(request, journey!.journeyId);

  await page.getByTestId('tab-journeys').click();
  await page.getByTestId(`persona-select-${journey!.personaId}`).click();
  await page.getByTestId(`journey-select-${journey!.journeyId}`).click();

  await expect(page.getByTestId('journey-traversal-title')).toHaveText(traversal.title);
  await expect(page.getByTestId('journey-steps')).toBeVisible();
  await expect(page.locator(`[data-testid^="journey-traversal-step-"]`)).toHaveCount(traversal.steps.length);
});

test('journeys tab renders linked persona context from the graph traversal endpoints', async ({ page, request }) => {
  const journeys = await fetchJourneys(request);
  const journey = journeys.find((candidate) => Boolean(candidate.personaId));

  expect(journey, 'Expected seeded data to contain a journey with persona context').toBeTruthy();
  const traversal = await fetchJourneyTraversal(request, journey!.journeyId);
  expect(traversal.persona, 'Expected graph traversal to include a linked persona').toBeTruthy();
  const persona = await fetchPersonaTraversal(request, traversal.persona!.id);

  await page.getByTestId('tab-journeys').click();
  await page.getByTestId(`persona-select-${journey!.personaId}`).click();
  await page.getByTestId(`journey-select-${journey!.journeyId}`).click();

  await expect(page.getByTestId('journey-persona-id')).toContainText(persona.personaId);
  await expect(page.getByTestId('journey-persona-channels')).toContainText(persona.channelReach[0]?.displayName ?? '');
  await expect(page.getByTestId('journey-persona-journeys')).toContainText(persona.journeys[0]?.title ?? '');
});

test('journeys tab supports persona-first entry into linked journeys', async ({ page, request }) => {
  const personas = await fetchPersonas(request);
  const personaSummary = personas.find((candidate) => candidate.journeyCount > 0);

  expect(personaSummary, 'Expected seeded data to contain a persona with journey coverage').toBeTruthy();
  const persona = await fetchPersonaTraversal(request, personaSummary!.personaId);
  expect(persona.journeys.length, 'Expected persona traversal to include linked journeys').toBeGreaterThan(0);
  const journeyTraversal = await fetchJourneyTraversal(request, persona.journeys[0].journeyId);

  await page.getByTestId('tab-journeys').click();
  await page.getByTestId(`persona-select-${personaSummary!.personaId}`).click();

  await expect(page.getByTestId('persona-hero-name')).toHaveText(persona.name);
  await expect(page.getByTestId('journey-persona-id')).toContainText(persona.personaId);
  await expect(page.getByTestId('journey-list')).toContainText(persona.journeys[0].title);
  await expect(page.getByTestId('journey-traversal-title')).toHaveText(journeyTraversal.title);
});

test('crosscutting tab renders selected screen metadata', async ({ page }) => {
  const { surfaceId } = await getFirstVisibleScreen(page);

  await selectScreen(page, surfaceId);
  await page.getByTestId('tab-crosscutting').click();

  await expect(page.getByTestId('crosscutting-panel')).toBeVisible();
  await expect(page.getByTestId('xc-wcag')).not.toHaveText('');
});
