import { expect, test } from '@playwright/test';
import {
  fetchApplicationArchitecture,
  fetchApplications,
  gotoAndWaitForData,
} from '../helpers/design-hub';

test.beforeEach(async ({ page }) => {
  await gotoAndWaitForData(page);
});

test('architecture tab renders application topology traversal', async ({ page, request }) => {
  const applications = await fetchApplications(request);
  const application = applications.find((candidate) => candidate.componentCount > 0) ?? applications[0];

  expect(application, 'Expected seeded data to contain at least one application').toBeTruthy();
  const architecture = await fetchApplicationArchitecture(request, application!.applicationId);

  await page.getByTestId('tab-architecture').click();
  await page.getByTestId('architecture-view-application').click();
  await page.getByTestId(`architecture-application-${application!.applicationId}`).click();

  await expect(page.getByTestId('detail-context-name')).toHaveText(architecture.name);
  await expect(page.getByTestId('architecture-application-title')).toHaveText(architecture.name);
  await expect(page.getByTestId('architecture-application-components')).toContainText(
    architecture.components[0]?.name ?? ''
  );
  await expect(page.getByTestId('architecture-application-apis')).toContainText(
    architecture.apis[0]?.displayName ?? ''
  );
  await expect(page.getByTestId('architecture-application-features')).toContainText(
    architecture.features[0]?.displayName ?? ''
  );
  await expect(page.getByTestId('architecture-application-dependencies')).toContainText(
    architecture.dependencies[0]?.name ?? ''
  );
});
