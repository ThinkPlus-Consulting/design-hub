import { expect, test } from '@playwright/test';
import {
  fetchDeliveryStories,
  fetchExternalParityAudit,
  fetchExternalSyncJobs,
  fetchExternalSyncSourceStatuses,
  fetchScreenReadiness,
  fetchScreens,
  fetchStoryReadiness,
  gotoAndWaitForData,
} from '../helpers/design-hub';

test.beforeEach(async ({ page }) => {
  await gotoAndWaitForData(page);
});

test('verification tab renders audit evidence and live readiness diagnostics', async ({ page, request }) => {
  const screens = await fetchScreens(request);
  const stories = await fetchDeliveryStories(request);
  const parityAudit = await fetchExternalParityAudit(request);
  const sourceStatuses = await fetchExternalSyncSourceStatuses(request);
  const syncJobs = await fetchExternalSyncJobs(request, { limit: 12 });

  expect(screens.length, 'Expected seeded screens to be available').toBeGreaterThan(0);
  expect(stories.length, 'Expected seeded delivery stories to be available').toBeGreaterThan(0);
  expect(sourceStatuses.length, 'Expected external sync source statuses to be available').toBeGreaterThan(0);

  const story = stories.find((candidate) => candidate.externalArtifacts.length > 0) ?? stories[0];
  const screen = screens.find((candidate) => candidate.surfaceId === story.screens[0]?.surfaceId) ?? screens[0];
  const screenReadiness = await fetchScreenReadiness(request, screen.surfaceId);
  const storyReadiness = await fetchStoryReadiness(request, story.storyId);
  const prettySourceLabel = (sourceSystem: string) => (sourceSystem === 'AZURE_DEVOPS' ? 'Azure DevOps' : 'Jira');

  await page.getByTestId(`screen-${screen.surfaceId}`).click();
  await page.getByTestId('tab-delivery').click();
  await page.getByTestId(`delivery-story-${story.storyId}`).click();
  await page.getByTestId('tab-verification').click();

  await expect(page.getByTestId('verification-panel')).toBeVisible();
  await expect(page.getByTestId('verification-check-token-audit')).toContainText('PASS');
  await expect(page.getByTestId('verification-check-build')).toContainText('npm run build');
  await expect(page.getByTestId('verification-story-readiness')).toContainText(storyReadiness.artifactId);
  await expect(page.getByTestId('verification-story-readiness')).toContainText(storyReadiness.completenessLevel);
  await expect(page.getByTestId('verification-screen-readiness')).toContainText(screenReadiness.artifactId);
  await expect(page.getByTestId('verification-screen-readiness')).toContainText(screenReadiness.completenessLevel);
  await expect(page.getByTestId('verification-external-parity')).toBeVisible();
  await expect(page.getByTestId('verification-external-parity')).toContainText(
    `${parityAudit.summary.totalArtifacts} artifacts across ${parityAudit.summary.trackedFields} tracked fields`
  );
  await expect(page.getByTestId('verification-external-parity')).toContainText(String(parityAudit.summary.hierarchyArtifacts));
  await expect(page.getByTestId('verification-external-parity')).toContainText(String(parityAudit.summary.dependencyArtifacts));
  await expect(page.getByTestId('verification-external-parity')).toContainText(
    `${parityAudit.summary.relatedArtifacts} / ${parityAudit.summary.duplicateArtifacts}`
  );
  for (const system of parityAudit.systems) {
    await expect(page.getByTestId('verification-external-parity')).toContainText(system.system);
    await expect(page.getByTestId('verification-external-parity')).toContainText(String(system.artifactCount));
  }
  await expect(page.getByTestId('verification-external-parity')).toContainText(
    parityAudit.fields[0]?.field.replace(/([A-Z])/g, ' $1').replace(/^./, (value) => value.toUpperCase()) ?? ''
  );
  await expect(page.getByTestId('verification-external-sync')).toBeVisible();
  for (const source of sourceStatuses) {
    const syncCard = page.getByTestId(`verification-external-sync-${source.sourceSystem.toLowerCase()}`);
    await expect(syncCard).toContainText(prettySourceLabel(source.sourceSystem));
    await expect(syncCard).toContainText(/Configured endpoint|Scheduler|Latest persisted job/);
    if (source.latestJob) {
      await expect(syncCard).toContainText(source.latestJob.status);
      if (source.latestJob.requestedBy) {
        await expect(syncCard).toContainText(source.latestJob.requestedBy);
      }
    }
  }
  const syncHistory = page.getByTestId('verification-sync-history');
  for (const sourceSystem of new Set(syncJobs.map((job) => job.sourceSystem))) {
    await expect(syncHistory).toContainText(prettySourceLabel(sourceSystem));
  }

  const jiraSyncCard = page.getByTestId('verification-external-sync-jira');
  await page.getByTestId('verification-poll-jira').click();
  await expect(page.getByTestId('verification-poll-feedback-jira')).toContainText(/SKIPPED|SUCCESS/);
  await expect(jiraSyncCard).toContainText('Requested by: ui-verification');
  await expect(syncHistory).toContainText('requestedBy=ui-verification');

  const azureSyncCard = page.getByTestId('verification-external-sync-azure_devops');
  await page.getByTestId('verification-poll-azure_devops').click();
  await expect(page.getByTestId('verification-poll-feedback-azure_devops')).toContainText(/SKIPPED|SUCCESS/);
  await expect(azureSyncCard).toContainText('Requested by: ui-verification');

  await page.getByTestId('verification-history-filter-azure_devops').click();
  await expect(syncHistory).toContainText('Azure DevOps · POLL');
  await expect(syncHistory).not.toContainText('Jira · POLL');
});
