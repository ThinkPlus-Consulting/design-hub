import { expect, test } from '@playwright/test';
import {
  fetchBusinessArchitecture,
  fetchBusinessCapabilities,
  gotoAndWaitForData,
} from '../helpers/design-hub';

test.beforeEach(async ({ page }) => {
  await gotoAndWaitForData(page);
});

test('architecture tab renders business capability traversal', async ({ page, request }) => {
  const capabilities = await fetchBusinessCapabilities(request);
  const capability = capabilities.find((candidate) => candidate.applicationCount > 0) ?? capabilities[0];

  expect(capability, 'Expected seeded data to contain at least one business capability').toBeTruthy();
  const architecture = await fetchBusinessArchitecture(request, capability!.capabilityId);

  await page.getByTestId('tab-architecture').click();
  await page.getByTestId(`architecture-capability-${capability!.capabilityId}`).click();

  await expect(page.getByTestId('architecture-title')).toHaveText(architecture.name);
  await expect(page.getByTestId('architecture-processes')).toContainText(architecture.processes[0]?.displayName ?? '');
  await expect(page.getByTestId('architecture-applications')).toContainText(architecture.applications[0]?.displayName ?? '');
  await expect(page.getByTestId('architecture-features')).toContainText(architecture.features[0]?.displayName ?? '');
  await expect(page.getByTestId('architecture-organizations')).toContainText(architecture.organizations[0]?.name ?? '');
});
