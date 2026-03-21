import { expect, Page } from '@playwright/test';
import { gotoAndWaitForData, setLocale } from './design-hub';

const VISUAL_TEST_CSS = `
  *,
  *::before,
  *::after {
    animation-duration: 0s !important;
    animation-delay: 0s !important;
    transition-duration: 0s !important;
    transition-delay: 0s !important;
    caret-color: transparent !important;
  }

  html {
    scroll-behavior: auto !important;
  }
`;

export async function prepareVisualPage(page: Page, options?: { locale?: 'en' | 'ar' }): Promise<void> {
  await gotoAndWaitForData(page);
  if (options?.locale) {
    await setLocale(page, options.locale);
  }
  await page.addStyleTag({ content: VISUAL_TEST_CSS });
  await page.evaluate(async () => {
    if (document.activeElement instanceof HTMLElement) {
      document.activeElement.blur();
    }

    await document.fonts.ready;
  });
}

export async function selectBaselineContext(page: Page): Promise<void> {
  await expect(page.getByTestId('screen-SCR-LM-DICT')).toBeVisible();
  await page.getByTestId('screen-SCR-LM-DICT').click();
  await page.getByTestId('tab-delivery').click();
  await expect(page.getByTestId('delivery-story-US-AI-090')).toBeVisible();
  await page.getByTestId('delivery-story-US-AI-090').click();
}
