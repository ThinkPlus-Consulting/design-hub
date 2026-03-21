import { expect, test } from '@playwright/test';
import { fetchBenchmark, gotoAndWaitForData } from '../helpers/design-hub';

function slug(value: string): string {
  return value.replace(/([a-z])([A-Z])/g, '$1-$2').toLowerCase();
}

function formatNodeType(value: string): string {
  return value
    .replace(/([A-Z])/g, ' $1')
    .replace(/^./, (first) => first.toUpperCase())
    .trim();
}

test.beforeEach(async ({ page }) => {
  await gotoAndWaitForData(page);
});

test('benchmark tab renders summary coverage and node type drilldown', async ({ page, request }) => {
  const benchmark = await fetchBenchmark(request);
  expect(benchmark.types.length, 'Expected benchmark types to be available').toBeGreaterThan(0);

  const selectedType = benchmark.types[1] ?? benchmark.types[0];

  await page.getByTestId('tab-benchmark').click();

  await expect(page.getByTestId('benchmark-panel')).toBeVisible();
  await expect(page.getByTestId('benchmark-overall-score')).toHaveText(
    benchmark.summary.overallScore.toFixed(1)
  );

  await page.getByTestId(`benchmark-type-${slug(selectedType.nodeType)}`).click();
  await expect(page.getByTestId('benchmark-type-detail')).toContainText(
    formatNodeType(selectedType.nodeType)
  );
  await expect(page.getByTestId('benchmark-recommendations')).toBeVisible();
});
