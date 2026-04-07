import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { ShellBackgroundConfig } from '../../models/system-shell-graph.model';
import { SystemShellGraphStateService } from '../../services/system-shell-graph-state.service';
import { AdministrationScreenPreviewComponent } from '../administration-screen-preview/administration-screen-preview.component';
import { LoginScreenPreviewComponent } from '../login-screen-preview/login-screen-preview.component';
import { MfaScreenPreviewComponent } from '../mfa-screen-preview/mfa-screen-preview.component';
import { TenantFactsheetScreenPreviewComponent } from '../tenant-factsheet-screen-preview/tenant-factsheet-screen-preview.component';
import { TenantListScreenPreviewComponent } from '../tenant-list-screen-preview/tenant-list-screen-preview.component';
import { TenantNotFoundScreenPreviewComponent } from '../tenant-not-found-screen-preview/tenant-not-found-screen-preview.component';

interface AppShellNavItem {
  readonly id: string;
  readonly label: string;
}

@Component({
  selector: 'app-app-shell-preview',
  standalone: true,
  imports: [
    AdministrationScreenPreviewComponent,
    LoginScreenPreviewComponent,
    MfaScreenPreviewComponent,
    TenantFactsheetScreenPreviewComponent,
    TenantListScreenPreviewComponent,
    TenantNotFoundScreenPreviewComponent,
  ],
  templateUrl: './app-shell-preview.component.html',
  styleUrl: './app-shell-preview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AppShellPreviewComponent {
  private readonly state = inject(SystemShellGraphStateService);
  readonly selectedGuid = input<string | null>(null);
  readonly backgroundConfig = input<ShellBackgroundConfig | null>(null);
  readonly shellCode = input<string>('SHL01');
  readonly activeScreenCode = input<string>('SHL01.SCN01');
  protected readonly navItems: readonly AppShellNavItem[] = [
    { id: 'administration', label: 'Administration' },
    { id: 'tenants', label: 'Tenants' },
  ];

  isFocused(graphCode: string): boolean {
    return this.selectedGuid() === this.guid(graphCode);
  }

  sourceObjectId(graphCode: string | null | undefined): string | null {
    return this.resolveNode(graphCode)?.id?.trim() ?? null;
  }

  guid(graphCode: string | null | undefined): string | null {
    return this.resolveNode(graphCode)?.guid?.trim() ?? null;
  }

  backgroundColorStyle(): string {
    return this.backgroundConfig()?.backgroundColorStyle ?? 'var(--tp-bg)';
  }

  backgroundPatternOpacity(): string {
    const opacity = this.backgroundConfig()?.backgroundPatternOpacity;
    if (this.shellCode() === 'SHL01') {
      const resolvedOpacity = opacity === null || opacity === undefined ? 0.13 : opacity;
      return String(Math.max(resolvedOpacity, 0.22));
    }

    return opacity === null || opacity === undefined ? 'var(--tp-pattern-opacity, 0.08)' : String(opacity);
  }

  backgroundPatternImage(): string {
    if (!this.backgroundConfig()?.backgroundPatternKey) {
      return 'none';
    }

    if (this.shellCode() === 'SHL01') {
      return `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='120' height='120' viewBox='0 0 120 120'%3E%3Cg fill='none' stroke='%23FAF8F5' stroke-width='1.35'%3E%3Cpath d='M60 8L70 30L92 20L82 42L112 60L82 78L92 100L70 90L60 112L50 90L28 100L38 78L8 60L38 42L28 20L50 30Z'/%3E%3Cpath d='M50 30L70 30L82 42L82 78L70 90L50 90L38 78L38 42Z'/%3E%3Cpath d='M60 38L74 60L60 82L46 60Z'/%3E%3Crect x='50' y='50' width='20' height='20' transform='rotate(45 60 60)'/%3E%3Cpath d='M0 0L10 22L22 10Z M120 0L110 22L98 10Z M0 120L10 98L22 110Z M120 120L110 98L98 110Z'/%3E%3Cpath d='M60 0L70 10L60 20L50 10Z M60 100L70 110L60 120L50 110Z M0 60L10 50L20 60L10 70Z M100 60L110 50L120 60L110 70Z'/%3E%3Cpath d='M22 10L38 42 M98 10L82 42 M22 110L38 78 M98 110L82 78'/%3E%3Cpath d='M10 22L42 38 M10 98L42 78 M110 22L78 38 M110 98L78 78'/%3E%3C/g%3E%3C/svg%3E")`;
    }

    return `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='120' height='120' viewBox='0 0 120 120'%3E%3Cg fill='none' stroke='%23428177' stroke-width='1.2'%3E%3Cpath d='M60 8L70 30L92 20L82 42L112 60L82 78L92 100L70 90L60 112L50 90L28 100L38 78L8 60L38 42L28 20L50 30Z'/%3E%3Cpath d='M50 30L70 30L82 42L82 78L70 90L50 90L38 78L38 42Z'/%3E%3Cpath d='M60 38L74 60L60 82L46 60Z'/%3E%3Crect x='50' y='50' width='20' height='20' transform='rotate(45 60 60)'/%3E%3Cpath d='M0 0L10 22L22 10Z M120 0L110 22L98 10Z M0 120L10 98L22 110Z M120 120L110 98L98 110Z'/%3E%3Cpath d='M60 0L70 10L60 20L50 10Z M60 100L70 110L60 120L50 110Z M0 60L10 50L20 60L10 70Z M100 60L110 50L120 60L110 70Z'/%3E%3Cpath d='M22 10L38 42 M98 10L82 42 M22 110L38 78 M98 110L82 78'/%3E%3Cpath d='M10 22L42 38 M10 98L42 78 M110 22L78 38 M110 98L78 78'/%3E%3C/g%3E%3C/svg%3E")`;
  }

  headerContainerCode(): string {
    return `${this.shellCode()}.SEC01`;
  }

  mainContainerCode(): string {
    return `${this.shellCode()}.SEC02`;
  }

  footerContainerCode(): string {
    return `${this.shellCode()}.SEC03`;
  }

  hasShellHeaderContent(): boolean {
    return this.shellCode() !== 'SHL01';
  }

  pageTitle(): string {
    switch (this.activeScreenCode()) {
      case 'SHL01.SCN01':
        return 'Login';
      case 'SHL01.SCN02':
        return 'Verification';
      case 'SHL01.SCN03':
        return 'Tenant Not Found';
      case 'SHL02.SCN01':
        return 'Administration';
      case 'SHL02.SCN02':
        return 'Tenant Registry';
      case 'SHL02.SCN03':
        return 'Tenant Factsheet';
      default:
        return 'Application';
    }
  }

  pageSubtitle(): string {
    switch (this.activeScreenCode()) {
      case 'SHL01.SCN01':
        return 'Tenant and sign-in flow';
      case 'SHL01.SCN02':
        return 'One-time verification flow';
      case 'SHL01.SCN03':
        return 'Tenant resolution failure state';
      case 'SHL02.SCN01':
        return 'Administrative landing workspace';
      case 'SHL02.SCN02':
        return 'Registry and onboarding workspace';
      case 'SHL02.SCN03':
        return 'Tenant administration workspace';
      default:
        return 'Unified application shell';
    }
  }

  isNavItemActive(itemId: string): boolean {
    switch (itemId) {
      case 'administration':
        return this.activeScreenCode() === 'SHL02.SCN01';
      case 'tenants':
        return this.activeScreenCode() === 'SHL02.SCN02' || this.activeScreenCode() === 'SHL02.SCN03';
      default:
        return false;
    }
  }

  private resolveNode(graphCode: string | null | undefined) {
    const normalizedCode = graphCode?.trim();
    if (!normalizedCode) {
      return null;
    }

    return this.state.nodeHierarchyMap().get(normalizedCode) ?? this.state.nodeMap().get(normalizedCode) ?? null;
  }
}
