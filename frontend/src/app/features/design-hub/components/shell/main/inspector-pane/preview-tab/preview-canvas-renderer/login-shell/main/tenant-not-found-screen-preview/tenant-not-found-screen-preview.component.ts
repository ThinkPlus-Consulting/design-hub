import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { TenantNotFoundScreenContentConfig } from '../../../../../../../../../models/preview-content.types';
import { parseNodeConfiguration } from '../../../../../../../../../utils/node-configuration';
import { PreviewIdentityBound } from '../../../../../../../../../utils/preview-identity-bound';
import { childByName, componentByName } from '../../../../../../../../../utils/preview-structure';

@Component({
  selector: 'app-tenant-not-found-screen-preview',
  standalone: true,
  templateUrl: './tenant-not-found-screen-preview.component.html',
  styleUrl: './tenant-not-found-screen-preview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TenantNotFoundScreenPreviewComponent extends PreviewIdentityBound {
  readonly selectedGuid = input<string | null>(null);
  readonly screenObjectId = input<string | null>(null);
  private readonly screenNode = computed(() => this.state.nodeById(this.screenObjectId()));
  private readonly content = computed(() =>
    parseNodeConfiguration<TenantNotFoundScreenContentConfig>(this.screenNode()),
  );
  private readonly slots = computed(() => {
    const screen = this.screenNode();
    const welcome = childByName(this.state, screen?.id, 'Screen Header Section');
    const logo = childByName(this.state, screen?.id, 'Logo Section');
    const notFound = childByName(this.state, screen?.id, 'Tenant Not Found Section');

    return {
      screen,
      welcome,
      welcomeTitle: componentByName(this.state, welcome?.id, 'Screen Title'),
      welcomeSubtitle: componentByName(this.state, welcome?.id, 'Screen Subtitle'),
      logo,
      logoWordmark: componentByName(this.state, logo?.id, 'SVG Logo Renderer'),
      notFound,
      notFoundTitle: componentByName(this.state, notFound?.id, 'Not Found Title'),
      notFoundText: componentByName(this.state, notFound?.id, 'Not Found Description'),
      notFoundAction: componentByName(this.state, notFound?.id, 'Back To Login Action'),
    };
  });

  welcomeTitle(): string {
    return this.content()?.welcomeTitle ?? '';
  }

  welcomeSubtitle(): string {
    return this.content()?.welcomeSubtitle ?? '';
  }

  logoWordmark(): string {
    return this.content()?.logoWordmark ?? '';
  }

  notFoundTitle(): string {
    return this.content()?.notFoundTitle ?? '';
  }

  notFoundText(): string {
    return this.content()?.notFoundText ?? '';
  }

  notFoundActionLabel(): string {
    return this.content()?.notFoundActionLabel ?? '';
  }

  protected slotNode(slot: string | null | undefined) {
    const normalizedSlot = slot?.trim();
    if (!normalizedSlot) {
      return null;
    }

    return this.slots()[normalizedSlot as keyof ReturnType<typeof this.slots>] ?? null;
  }

  protected selectedPreviewGuid(): string | null {
    return this.selectedGuid();
  }
}
