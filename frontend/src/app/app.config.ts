import { ApplicationConfig, inject, Injector, provideAppInitializer, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(),
    provideAnimationsAsync(),
    provideAppInitializer(async () => {
      const injector = inject(Injector);
      const [{ PrimeNG }, { DefaultPrimePreset }] = await Promise.all([
        import('primeng/config'),
        import('./core/theme/default-preset'),
      ]);
      const primeng = injector.get(PrimeNG);

      primeng.setConfig({
        ripple: false,
        theme: {
          preset: DefaultPrimePreset,
          options: {
            prefix: 'p',
            darkModeSelector: '.app-dark-mode',
            cssLayer: {
              name: 'primeng',
              order: 'primeng',
            },
          },
        },
      });
    }),
  ],
};
