import { expect, test } from '@playwright/test';
import {
  fetchInfrastructureArchitecture,
  fetchInfrastructureDeployments,
  gotoAndWaitForData,
} from '../helpers/design-hub';

test.beforeEach(async ({ page }) => {
  await gotoAndWaitForData(page);
});

test('architecture tab renders infrastructure deployment traversal', async ({ page, request }) => {
  const deployments = await fetchInfrastructureDeployments(request);
  const deployment = deployments.find((candidate) => candidate.componentCount > 0) ?? deployments[0];

  expect(deployment, 'Expected seeded data to contain at least one deployment').toBeTruthy();
  const architecture = await fetchInfrastructureArchitecture(request, deployment!.deploymentId);

  await page.getByTestId('tab-architecture').click();
  await page.getByTestId('architecture-view-infrastructure').click();
  await page.getByTestId(`architecture-infrastructure-${deployment!.deploymentId}`).click();

  await expect(page.getByTestId('detail-context-name')).toHaveText(architecture.name);
  await expect(page.getByTestId('architecture-infrastructure-title')).toHaveText(architecture.name);
  await expect(page.getByTestId('architecture-infrastructure-components')).toContainText(
    architecture.components[0]?.displayName ?? ''
  );
  await expect(page.getByTestId('architecture-infrastructure-nodes')).toContainText(
    architecture.infrastructureNodes[0]?.name ?? ''
  );
  await expect(page.getByTestId('architecture-infrastructure-applications')).toContainText(
    architecture.applications[0]?.displayName ?? ''
  );
});
