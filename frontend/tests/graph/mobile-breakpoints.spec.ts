import { expect, test } from '@playwright/test';
import {
  fetchScreens,
  fetchInfrastructureArchitecture,
  fetchInfrastructureDeployments,
  fetchJourneyTraversal,
  fetchPersonas,
  fetchPersonaTraversal,
  gotoAndWaitForData,
  selectScreen,
} from '../helpers/design-hub';

test.use({
  viewport: { width: 390, height: 844 },
  isMobile: true,
  hasTouch: true,
});

test.describe('mobile breakpoint coverage', () => {
  test.describe.configure({ mode: 'serial' });

  test.beforeEach(async ({ page }) => {
    await gotoAndWaitForData(page);
  });

  test('persona-first journey explorer remains usable on mobile', async ({ page, request }) => {
    const personas = await fetchPersonas(request);
    const personaSummary = personas.find((candidate) => candidate.journeyCount > 0);

    expect(personaSummary, 'Expected seeded data to contain a persona with mobile-testable journey coverage').toBeTruthy();
    const persona = await fetchPersonaTraversal(request, personaSummary!.personaId);
    const journeyTraversal = await fetchJourneyTraversal(request, persona.journeys[0].journeyId);

    await page.getByTestId('tab-journeys').click();
    await page.getByTestId(`persona-select-${personaSummary!.personaId}`).click();
    await page.getByTestId(`journey-select-${journeyTraversal.journeyId}`).click();

    await expect(page.getByTestId('persona-hero-name')).toHaveText(persona.name);
    await expect(page.getByTestId('journey-traversal-title')).toHaveText(journeyTraversal.title);
    await expect(page.locator('[data-testid^="journey-traversal-step-"]')).toHaveCount(journeyTraversal.steps.length);
  });

  test('infrastructure architecture explorer remains usable on mobile', async ({ page, request }) => {
    const deployments = await fetchInfrastructureDeployments(request);
    const deployment = deployments[0];

    expect(deployment, 'Expected seeded data to contain at least one deployment').toBeTruthy();
    const architecture = await fetchInfrastructureArchitecture(request, deployment!.deploymentId);

    await page.getByTestId('tab-architecture').click();
    await page.getByTestId('architecture-view-infrastructure').click();
    await page.getByTestId(`architecture-infrastructure-${deployment!.deploymentId}`).click();

    await expect(page.getByTestId('architecture-infrastructure-title')).toHaveText(architecture.name);
    await expect(page.getByTestId('architecture-infrastructure-components')).toContainText(
      architecture.components[0]?.displayName ?? ''
    );
    await expect(page.getByTestId('architecture-infrastructure-nodes')).toContainText(
      architecture.infrastructureNodes[0]?.name ?? ''
    );
  });

  test('graph-backed screen detail and delivery remain usable on mobile', async ({ page, request }) => {
    const screens = await fetchScreens(request);
    const screen = screens.find((candidate) => candidate.storyRefs.length > 0 && candidate.roleKeys.length > 0);

    expect(screen, 'Expected seeded data to contain a mobile-testable screen with stories and roles').toBeTruthy();
    await selectScreen(page, screen!.surfaceId);

    await expect(page.getByTestId('prop-surfaceId')).toHaveText(screen!.surfaceId);
    await expect(page.getByTestId(`role-chip-${screen!.roleKeys[0]}`)).toBeVisible();

    await page.getByTestId('tab-delivery').click();
    await expect(page.getByTestId(`delivery-story-${screen!.storyRefs[0]}`)).toBeVisible();
    await page.getByTestId(`delivery-story-${screen!.storyRefs[0]}`).click();

    await expect(page.getByTestId('detail-context-label')).toHaveText('Story:');
    await expect(page.getByTestId('delivery-detail')).toContainText(screen!.storyRefs[0]);
  });
});
