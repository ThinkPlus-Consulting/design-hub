import { expect, test } from '@playwright/test';
import {
  fetchDataArchitecture,
  fetchDataObjects,
  gotoAndWaitForData,
} from '../helpers/design-hub';

test.beforeEach(async ({ page }) => {
  await gotoAndWaitForData(page);
});

test('architecture tab renders data-object mapping traversal', async ({ page, request }) => {
  const objects = await fetchDataObjects(request);
  const object = objects.find((candidate) => candidate.mappedEntityCount > 0) ?? objects[0];

  expect(object, 'Expected seeded data to contain at least one data object').toBeTruthy();
  const architecture = await fetchDataArchitecture(request, object!.objectId);

  await page.getByTestId('tab-architecture').click();
  await page.getByTestId('architecture-view-data').click();
  await page.getByTestId(`architecture-data-${object!.objectId}`).click();

  await expect(page.getByTestId('detail-context-name')).toHaveText(architecture.name);
  await expect(page.getByTestId('architecture-data-title')).toHaveText(architecture.name);
  await expect(page.getByTestId('architecture-data-entities')).toContainText(
    architecture.entities[0]?.name ?? ''
  );
  await expect(page.getByTestId('architecture-data-flows')).toContainText(
    architecture.flows[0]?.name ?? ''
  );
  await expect(page.getByTestId('architecture-data-apis')).toContainText(
    architecture.apis[0]?.displayName ?? ''
  );
  await expect(page.getByTestId('architecture-data-children')).toContainText(
    architecture.children[0]?.displayName ?? ''
  );
});
