import { DOCUMENT } from '@angular/common';
import { Injectable, computed, effect, inject, signal } from '@angular/core';
import { AppLocale, TRANSLATIONS, TranslationKey } from './translations';

const STORAGE_KEY = 'design-hub.locale';

@Injectable({ providedIn: 'root' })
export class LocaleService {
  private readonly document = inject(DOCUMENT);

  readonly locale = signal<AppLocale>(this.resolveInitialLocale());
  readonly direction = computed(() => (this.locale() === 'ar' ? 'rtl' : 'ltr'));
  readonly localeOptions = [
    { value: 'en' as const, labelKey: 'locale.en' as const },
    { value: 'ar' as const, labelKey: 'locale.ar' as const },
  ];

  constructor() {
    effect(() => {
      const locale = this.locale();
      const direction = this.direction();

      this.document.documentElement.lang = locale;
      this.document.documentElement.dir = direction;

      try {
        window.localStorage.setItem(STORAGE_KEY, locale);
      } catch {
        // Ignore unavailable storage and keep runtime locale in memory.
      }
    });
  }

  setLocale(locale: AppLocale): void {
    this.locale.set(locale);
  }

  isActive(locale: AppLocale): boolean {
    return this.locale() === locale;
  }

  t(key: TranslationKey): string {
    return TRANSLATIONS[this.locale()][key] ?? TRANSLATIONS.en[key];
  }

  format(key: TranslationKey, params: Record<string, string | number>): string {
    return Object.entries(params).reduce(
      (message, [name, value]) => message.replaceAll(`{${name}}`, String(value)),
      this.t(key)
    );
  }

  private resolveInitialLocale(): AppLocale {
    try {
      const stored = window.localStorage.getItem(STORAGE_KEY);

      if (stored === 'en' || stored === 'ar') {
        return stored;
      }
    } catch {
      // Ignore unavailable storage and fall back to browser preferences.
    }

    return window.navigator.language.toLowerCase().startsWith('ar') ? 'ar' : 'en';
  }
}
