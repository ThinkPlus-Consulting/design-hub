import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { ShellBackgroundConfig, SystemShellGraphNode } from '../../../../../../../models/graph.types';
import { PreviewIdentityBound } from '../../../../../../../utils/preview-identity-bound';
import { childByName } from '../../../../../../../utils/preview-structure';
import { LoginShellBreadcrumbSlotComponent } from './breadcrumb/login-shell-breadcrumb-slot.component';
import { LoginShellFooterSlotComponent } from './footer/login-shell-footer-slot.component';
import { LoginShellHeaderSlotComponent } from './header/login-shell-header-slot.component';
import { LoginShellMainPreviewComponent } from './main/login-shell-main-preview.component';

@Component({
  selector: 'app-login-shell-preview',
  standalone: true,
  imports: [
    LoginShellHeaderSlotComponent,
    LoginShellBreadcrumbSlotComponent,
    LoginShellMainPreviewComponent,
    LoginShellFooterSlotComponent,
  ],
  templateUrl: './login-shell-preview.component.html',
  styleUrl: './login-shell-preview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginShellPreviewComponent extends PreviewIdentityBound {
  readonly selectedGuid = input<string | null>(null);
  readonly backgroundConfig = input<ShellBackgroundConfig | null>(null);
  readonly shellObjectId = input<string | null>(null);
  readonly activeScreenObjectId = input<string | null>(null);

  private readonly shellNode = computed(() => this.state.nodeById(this.shellObjectId()));
  private readonly shellSlots = computed(() => {
    const shell = this.shellNode();
    return {
      shell,
      header: childByName(this.state, shell?.id, 'Header Container'),
      breadcrumb: childByName(this.state, shell?.id, 'Breadcrumb Container'),
      main: childByName(this.state, shell?.id, 'Main Container'),
      footer: childByName(this.state, shell?.id, 'Footer Container'),
    };
  });

  readonly shellFrameWidth = computed(() =>
    this.cssLength(this.shellNode()?.width, 'var(--ssg-shell-stack-width, 520px)'),
  );
  readonly shellRowGap = computed(() =>
    this.cssLength(this.shellNode()?.rowGap ?? this.shellNode()?.gap, '12px'),
  );
  readonly shellPaddingLeft = computed(() => this.cssLength(this.shellNode()?.paddingLeft, '24px'));
  readonly shellPaddingRight = computed(() =>
    this.cssLength(this.shellNode()?.paddingRight, this.shellPaddingLeft()),
  );
  readonly headerHeight = computed(() => this.existingSlotHeight(this.shellSlots().header, '72px'));
  readonly breadcrumbHeight = computed(() => this.existingSlotHeight(this.shellSlots().breadcrumb, '36px'));
  readonly footerHeight = computed(() => this.existingSlotHeight(this.shellSlots().footer, '72px'));
  readonly mainHeight = computed(() =>
    `calc(100% - ${this.headerHeight()} - ${this.breadcrumbHeight()} - ${this.footerHeight()} - (${this.shellRowGap()} * 3))`,
  );

  backgroundColorStyle(): string {
    return this.backgroundConfig()?.backgroundColorStyle ?? 'var(--tp-primary)';
  }

  backgroundPatternOpacity(): string {
    const opacity = this.backgroundConfig()?.backgroundPatternOpacity;
    return typeof opacity === 'number' ? String(opacity) : '0.13';
  }

  backgroundPatternImage(): string {
    if (!this.backgroundConfig()?.backgroundPatternKey) {
      return 'none';
    }

    return `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='120' height='120' viewBox='0 0 120 120'%3E%3Cg fill='none' stroke='%23FAF8F5' stroke-width='1.2'%3E%3Cpath d='M60 8L70 30L92 20L82 42L112 60L82 78L92 100L70 90L60 112L50 90L28 100L38 78L8 60L38 42L28 20L50 30Z'/%3E%3Cpath d='M50 30L70 30L82 42L82 78L70 90L50 90L38 78L38 42Z'/%3E%3Cpath d='M60 38L74 60L60 82L46 60Z'/%3E%3Crect x='50' y='50' width='20' height='20' transform='rotate(45 60 60)'/%3E%3Cpath d='M0 0L10 22L22 10Z M120 0L110 22L98 10Z M0 120L10 98L22 110Z M120 120L110 98L98 110Z'/%3E%3Cpath d='M60 0L70 10L60 20L50 10Z M60 100L70 110L60 120L50 110Z M0 60L10 50L20 60L10 70Z M100 60L110 50L120 60L110 70Z'/%3E%3Cpath d='M22 10L38 42 M98 10L82 42 M22 110L38 78 M98 110L82 78'/%3E%3Cpath d='M10 22L42 38 M10 98L42 78 M110 22L78 38 M110 98L78 78'/%3E%3C/g%3E%3C/svg%3E")`;
  }

  protected slotNode(slot: string | null | undefined): SystemShellGraphNode | null {
    const normalizedSlot = slot?.trim();
    if (!normalizedSlot) {
      return null;
    }

    return this.shellSlots()[normalizedSlot as keyof ReturnType<typeof this.shellSlots>] ?? null;
  }

  protected selectedPreviewGuid(): string | null {
    return this.selectedGuid();
  }

  private existingSlotHeight(slotNode: SystemShellGraphNode | null, fallback: string): string {
    return slotNode ? this.slotHeight(slotNode, fallback) : '0px';
  }

  private slotHeight(slotNode: SystemShellGraphNode | null, fallback: string): string {
    return this.cssLength(slotNode?.height ?? slotNode?.minHeight, fallback);
  }

  private cssLength(value: string | null | undefined, fallback: string): string {
    const normalizedValue = value?.trim();
    return normalizedValue ? normalizedValue : fallback;
  }
}
