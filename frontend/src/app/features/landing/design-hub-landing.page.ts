import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-design-hub-landing-page',
  standalone: true,
  imports: [RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <main class="landing-shell" data-testid="landing-root">
      <section class="landing-hero">
        <div class="landing-hero__copy">
          <p class="landing-hero__eyebrow">Design Hub</p>
          <h1>Choose the view you want to open.</h1>
          <p class="landing-hero__intro">
            Start in the system shell to inspect the screen map, or open the object definition
            catalog to work from the metamodel first.
          </p>
        </div>

        <div class="landing-hero__grid">
          <a class="landing-card" routerLink="/system-shell" data-testid="landing-link-system-shell">
            <span class="landing-card__kicker">Workspace</span>
            <h2>System Shell</h2>
            <p>
              Open the screen canvas, sidebar filters, and detail panel to inspect the live flow.
            </p>
            <span class="landing-card__cta">Open system shell</span>
          </a>

          <a class="landing-card" routerLink="/object-definitions" data-testid="landing-link-object-definitions">
            <span class="landing-card__kicker">Metamodel</span>
            <h2>Object Definitions</h2>
            <p>
              Browse definitions, attributes, relationships, and instances before drilling into a screen.
            </p>
            <span class="landing-card__cta">Open object definitions</span>
          </a>
        </div>
      </section>
    </main>
  `,
  styles: [`
    :host {
      display: block;
      min-height: 100vh;
      background:
        radial-gradient(circle at top left, color-mix(in srgb, var(--tp-primary-light) 52%, transparent), transparent 34%),
        radial-gradient(circle at bottom right, color-mix(in srgb, var(--tp-primary) 18%, transparent), transparent 36%),
        linear-gradient(160deg, color-mix(in srgb, var(--tp-white) 72%, var(--tp-bg)), var(--tp-bg));
    }

    .landing-shell {
      min-height: 100vh;
      padding: clamp(1.5rem, 4vw, 3rem);
      display: grid;
      place-items: center;
    }

    .landing-hero {
      width: min(1080px, 100%);
      display: grid;
      gap: clamp(1.5rem, 3vw, 2.5rem);
      padding: clamp(1.5rem, 3vw, 2.5rem);
      border: 1px solid color-mix(in srgb, var(--tp-border) 28%, transparent);
      border-radius: 1.5rem;
      background:
        linear-gradient(135deg, color-mix(in srgb, var(--tp-white) 92%, transparent), color-mix(in srgb, var(--tp-surface) 96%, transparent));
      box-shadow:
        0 28px 60px color-mix(in srgb, var(--tp-primary-darkest) 12%, transparent),
        inset 0 1px 0 color-mix(in srgb, var(--tp-white) 82%, transparent);
    }

    .landing-hero__copy {
      display: grid;
      gap: 0.9rem;
      max-width: 42rem;
    }

    .landing-hero__eyebrow,
    .landing-card__kicker {
      font-size: 0.74rem;
      font-weight: 800;
      letter-spacing: 0.14em;
      text-transform: uppercase;
      color: var(--tp-primary-dark);
    }

    .landing-hero h1 {
      font-size: clamp(2.2rem, 6vw, 4.4rem);
      line-height: 0.96;
      color: var(--tp-primary-darkest);
      max-width: 12ch;
    }

    .landing-hero__intro {
      font-size: 1.02rem;
      line-height: 1.7;
      color: color-mix(in srgb, var(--tp-text) 88%, transparent);
      max-width: 42rem;
    }

    .landing-hero__grid {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: 1rem;
    }

    .landing-card {
      display: grid;
      gap: 0.95rem;
      min-height: 18rem;
      padding: 1.4rem;
      border-radius: 1.25rem;
      text-decoration: none;
      color: inherit;
      border: 1px solid color-mix(in srgb, var(--tp-border) 24%, transparent);
      background:
        linear-gradient(180deg, color-mix(in srgb, var(--tp-white) 90%, transparent), color-mix(in srgb, var(--tp-surface) 96%, transparent));
      box-shadow: 0 14px 34px color-mix(in srgb, var(--tp-primary-darkest) 7%, transparent);
      transition:
        transform 0.18s ease,
        box-shadow 0.18s ease,
        border-color 0.18s ease;
    }

    .landing-card:hover,
    .landing-card:focus-visible {
      transform: translateY(-3px);
      border-color: color-mix(in srgb, var(--tp-primary) 36%, transparent);
      box-shadow: 0 18px 38px color-mix(in srgb, var(--tp-primary-darkest) 12%, transparent);
      outline: none;
    }

    .landing-card h2 {
      font-size: 1.65rem;
      color: var(--tp-primary-darkest);
    }

    .landing-card p {
      font-size: 0.98rem;
      line-height: 1.7;
      color: color-mix(in srgb, var(--tp-text) 88%, transparent);
      max-width: 28rem;
    }

    .landing-card__cta {
      margin-top: auto;
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      font-weight: 700;
      color: var(--tp-primary-dark);
    }

    .landing-card__cta::after {
      content: '->';
      font-size: 1rem;
      line-height: 1;
    }

    @media (max-width: 760px) {
      .landing-shell {
        padding: 1rem;
      }

      .landing-hero__grid {
        grid-template-columns: 1fr;
      }

      .landing-card {
        min-height: auto;
      }
    }
  `],
})
export class DesignHubLandingPage {}
