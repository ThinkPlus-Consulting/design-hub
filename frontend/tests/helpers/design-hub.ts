import { APIRequestContext, expect, Page } from '@playwright/test';
import { backendBaseUrl } from './backend';

const SCREEN_BUTTON_SELECTOR = '[data-testid^="screen-"][data-module][data-design-status]';

export interface BackendScreen {
  surfaceId: string;
  label: string;
  module: string;
  designStatus: 'COMPLETE' | 'SPECIFIED' | 'NOT_STARTED';
  roleKeys: string[];
  storyRefs: string[];
  gaps?: Array<unknown>;
}

export interface BackendTouchpoint {
  touchpointId: string;
  surfaceId: string;
}

export interface BackendInteraction {
  interactionId: string;
  surfaceId: string;
  permission: string | null;
  apiCalls: string[];
  confirmationCode: string | null;
}

export interface BackendJourneyStep {
  stepId: string;
  interactionRef: string | null;
}

export interface BackendJourney {
  journeyId: string;
  title: string;
  steps: BackendJourneyStep[];
}

async function getJson<T>(request: APIRequestContext, path: string): Promise<T> {
  const response = await request.get(`${backendBaseUrl}${path}`);
  expect(response.ok(), `Expected ${path} to return 2xx`).toBeTruthy();
  return response.json() as Promise<T>;
}

export async function fetchScreens(request: APIRequestContext): Promise<BackendScreen[]> {
  return getJson<BackendScreen[]>(request, '/api/v1/design-hub/screens');
}

export async function fetchTouchpoints(request: APIRequestContext): Promise<BackendTouchpoint[]> {
  return getJson<BackendTouchpoint[]>(request, '/api/v1/design-hub/touchpoints');
}

export async function fetchInteractions(request: APIRequestContext): Promise<BackendInteraction[]> {
  return getJson<BackendInteraction[]>(request, '/api/v1/design-hub/interactions');
}

export async function fetchJourneys(request: APIRequestContext): Promise<BackendJourney[]> {
  return getJson<BackendJourney[]>(request, '/api/v1/design-hub/journeys');
}

export async function gotoAndWaitForData(page: Page): Promise<void> {
  await page.goto('/');
  await expect(page.getByTestId('design-hub-root')).toBeVisible();
  await expect(page.getByTestId('screen-list')).toBeVisible();
  await expect(page.locator(SCREEN_BUTTON_SELECTOR).first()).toBeVisible();
}

export async function selectScreen(page: Page, surfaceId: string): Promise<void> {
  await page.getByTestId(`screen-${surfaceId}`).click();
  await expect(page.getByTestId('prop-surfaceId')).toHaveText(surfaceId);
}

export async function getVisibleScreenMetadata(page: Page): Promise<Array<{ surfaceId: string; module: string; designStatus: string }>> {
  return page.locator(SCREEN_BUTTON_SELECTOR).evaluateAll((elements) =>
    elements.map((element) => ({
      surfaceId: (element.getAttribute('data-testid') ?? '').replace(/^screen-/, ''),
      module: element.getAttribute('data-module') ?? '',
      designStatus: element.getAttribute('data-design-status') ?? '',
    }))
  );
}

export async function getFirstVisibleScreen(page: Page): Promise<{ surfaceId: string; label: string }> {
  const first = page.locator(SCREEN_BUTTON_SELECTOR).first();
  const surfaceId = ((await first.getAttribute('data-testid')) ?? '').replace(/^screen-/, '');
  const label = (await page.getByTestId(`screen-label-${surfaceId}`).textContent())?.trim() ?? '';
  return { surfaceId, label };
}

export async function readNumericTestId(page: Page, testId: string): Promise<number> {
  const raw = (await page.getByTestId(testId).textContent())?.trim() ?? '';
  const value = Number.parseInt(raw, 10);
  expect(Number.isNaN(value), `Expected ${testId} to contain a number, got "${raw}"`).toBeFalsy();
  return value;
}

export function toStatusLabel(status: BackendScreen['designStatus']): string {
  switch (status) {
    case 'COMPLETE':
      return 'Complete';
    case 'SPECIFIED':
      return 'Specified';
    case 'NOT_STARTED':
      return 'Not Started';
    default:
      return status;
  }
}
