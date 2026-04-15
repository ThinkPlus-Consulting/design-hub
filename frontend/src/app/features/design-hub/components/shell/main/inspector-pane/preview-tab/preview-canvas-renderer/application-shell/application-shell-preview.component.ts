import {
  ChangeDetectionStrategy,
  Component,
  computed,
  input,
} from "@angular/core";
import { MenuItem } from "primeng/api";
import { AvatarModule } from "primeng/avatar";
import { BadgeModule } from "primeng/badge";
import { BreadcrumbModule } from "primeng/breadcrumb";
import { ButtonModule } from "primeng/button";
import { DividerModule } from "primeng/divider";
import { ImageModule } from "primeng/image";
import {
  ApplicationShellContentConfig,
  TenantFactsheetScreenContentConfig,
  TenantListScreenContentConfig,
} from "../../../../../../../models/preview-content.types";
import { ShellBackgroundConfig, SystemShellGraphNode } from "../../../../../../../models/graph.types";
import { PreviewIdentityBound } from '../../../../../../../utils/preview-identity-bound';
import { childAt, childByName, componentAt } from '../../../../../../../utils/preview-structure';
import { parseNodeConfiguration } from '../../../../../../../utils/node-configuration';
import { TenantFactsheetScreenPreviewComponent } from "./tenant-factsheet-screen-preview/tenant-factsheet-screen-preview.component";
import { TenantListScreenPreviewComponent } from "./tenant-list-screen-preview/tenant-list-screen-preview.component";

@Component({
  selector: "app-application-shell-preview",
  standalone: true,
  imports: [
    AvatarModule,
    BadgeModule,
    BreadcrumbModule,
    ButtonModule,
    DividerModule,
    ImageModule,
    TenantFactsheetScreenPreviewComponent,
    TenantListScreenPreviewComponent,
  ],
  templateUrl: "./application-shell-preview.component.html",
  styleUrl: "./application-shell-preview.component.scss",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApplicationShellPreviewComponent extends PreviewIdentityBound {
  readonly selectedGuid = input<string | null>(null);
  readonly backgroundConfig = input<ShellBackgroundConfig | null>(null);
  readonly shellObjectId = input<string | null>(null);
  readonly activeScreenObjectId = input<string | null>(null);
  private readonly shellNode = computed(() => this.state.nodeById(this.shellObjectId()));
  private readonly screenNode = computed(() => this.state.nodeById(this.activeScreenObjectId()));
  private readonly shellContent = computed(
    () => parseNodeConfiguration<ApplicationShellContentConfig>(this.shellNode()),
  );
  private readonly tenantListContent = computed(
    () => parseNodeConfiguration<TenantListScreenContentConfig>(this.screenNode()),
  );
  private readonly tenantFactsheetContent = computed(
    () => parseNodeConfiguration<TenantFactsheetScreenContentConfig>(this.screenNode()),
  );
  private readonly shellSlots = computed(() => {
    const shell = this.shellNode();
    const header = childByName(this.state, shell?.id, 'Header Container', 'Container');
    const main = childByName(this.state, shell?.id, 'Main Container', 'Container');
    const footer = childByName(this.state, shell?.id, 'Footer Container', 'Container');
    const breadcrumb = childByName(this.state, shell?.id, 'Breadcrumb Container', 'Container');
    const headerLeft = childAt(this.state, header?.id, 0);
    const headerRight = childAt(this.state, header?.id, 1);

    return {
      shell,
      header,
      headerLeft,
      headerMenuToggle: componentAt(this.state, headerLeft?.id, 0),
      headerLogoLink: componentAt(this.state, headerLeft?.id, 1),
      headerLeftDivider: componentAt(this.state, headerLeft?.id, 2),
      headerPageIndicator: componentAt(this.state, headerLeft?.id, 3),
      headerRight,
      headerNotificationTrigger: componentAt(this.state, headerRight?.id, 0),
      headerNotificationBadge: componentAt(this.state, headerRight?.id, 1),
      headerHelpAction: componentAt(this.state, headerRight?.id, 2),
      headerLanguageSwitcher: componentAt(this.state, headerRight?.id, 3),
      headerRightDivider: componentAt(this.state, headerRight?.id, 4),
      headerUserAvatar: componentAt(this.state, headerRight?.id, 5),
      breadcrumb,
      breadcrumbTrail: componentAt(this.state, breadcrumb?.id, 0),
      main,
      footer,
    };
  });
  private readonly activeShellScreens = computed(() =>
    this.state
      .orderedChildrenOf(this.shellSlots().main?.id, ['HAS_SCREEN'])
      .filter((node) => node.family === 'Screen'),
  );
  readonly activeScreenRenderer = computed(() => {
    const normalizedName = this.screenNode()?.name?.trim().toLowerCase() ?? '';
    switch (normalizedName) {
      case 'view tenant list':
        return 'tenant-list';
      case 'tenant fact sheet':
        return 'tenant-factsheet';
      default:
        return 'unknown';
    }
  });
  readonly shellBreadcrumbHome = computed<MenuItem | undefined>(() => {
    if (!this.shellSlots().breadcrumb) {
      return undefined;
    }

    const homeScreen = this.activeShellScreens()[0];
    const currentScreenId = this.screenNode()?.id ?? null;
    return {
      icon: "pi pi-home",
      label: this.shellContent()?.homeLabel ?? '',
      disabled: !!homeScreen?.id && homeScreen.id === currentScreenId,
      command: () => this.selectObject(homeScreen?.id ?? null),
    };
  });
  readonly shellBreadcrumbItems = computed<MenuItem[]>(() => {
    const tenantRegistryLabel = this.shellContent()?.breadcrumbTenantRegistryLabel ?? '';
    switch (this.activeScreenRenderer()) {
      case "tenant-list":
        return tenantRegistryLabel ? [{ label: tenantRegistryLabel, disabled: true }] : [];
      case "tenant-factsheet":
        return [
          {
            label: tenantRegistryLabel,
            command: () => this.selectObject(this.activeShellScreens()[0]?.id ?? null),
          },
          { label: this.tenantFactsheetContent()?.tenant?.name ?? '', disabled: true },
        ].filter((item) => !!item.label);
      default:
        return [];
    }
  });
  readonly shellFrameWidth = computed(() => this.cssLength(this.shellNode()?.width, '1200px'));
  readonly shellRowGap = computed(() => this.cssLength(this.shellNode()?.rowGap ?? this.shellNode()?.gap, '16px'));
  readonly shellColumnGap = computed(() => this.cssLength(this.shellNode()?.columnGap, '0px'));
  readonly shellPaddingLeft = computed(() => this.cssLength(this.shellNode()?.paddingLeft, '24px'));
  readonly shellPaddingRight = computed(() => this.cssLength(this.shellNode()?.paddingRight, this.shellPaddingLeft()));
  readonly headerHeight = computed(() => this.slotHeight(this.shellSlots().header, '0px'));
  readonly breadcrumbHeight = computed(() => this.slotHeight(this.shellSlots().breadcrumb, '0px'));
  readonly footerHeight = computed(() => this.slotHeight(this.shellSlots().footer, '0px'));
  readonly mainHeight = computed(() =>
    this.slotCssValue(
      this.shellSlots().main,
      'height',
      `calc(100% - ${this.headerHeight()} - ${this.breadcrumbHeight()} - ${this.footerHeight()} - (${this.shellRowGap()} * 3))`,
    ),
  );

  backgroundColorStyle(): string {
    return this.backgroundConfig()?.backgroundColorStyle ?? "var(--tp-bg)";
  }

  backgroundPatternOpacity(): string {
    const opacity = this.backgroundConfig()?.backgroundPatternOpacity;
    return opacity === null || opacity === undefined
      ? "var(--tp-pattern-opacity, 0.08)"
      : String(opacity);
  }

  backgroundPatternImage(): string {
    if (!this.backgroundConfig()?.backgroundPatternKey) {
      return "none";
    }

    return `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='120' height='120' viewBox='0 0 120 120'%3E%3Cg fill='none' stroke='%23428177' stroke-width='1.2'%3E%3Cpath d='M60 8L70 30L92 20L82 42L112 60L82 78L92 100L70 90L60 112L50 90L28 100L38 78L8 60L38 42L28 20L50 30Z'/%3E%3Cpath d='M50 30L70 30L82 42L82 78L70 90L50 90L38 78L38 42Z'/%3E%3Cpath d='M60 38L74 60L60 82L46 60Z'/%3E%3Crect x='50' y='50' width='20' height='20' transform='rotate(45 60 60)'/%3E%3Cpath d='M0 0L10 22L22 10Z M120 0L110 22L98 10Z M0 120L10 98L22 110Z M120 120L110 98L98 110Z'/%3E%3Cpath d='M60 0L70 10L60 20L50 10Z M60 100L70 110L60 120L50 110Z M0 60L10 50L20 60L10 70Z M100 60L110 50L120 60L110 70Z'/%3E%3Cpath d='M22 10L38 42 M98 10L82 42 M22 110L38 78 M98 110L82 78'/%3E%3Cpath d='M10 22L42 38 M10 98L42 78 M110 22L78 38 M110 98L78 78'/%3E%3C/g%3E%3C/svg%3E")`;
  }

  hasShellHeaderContent(): boolean {
    return this.state.orderedChildrenOf(this.shellSlots().header?.id, ['HAS_SECTION', 'HAS_COMPONENT']).length > 0;
  }

  showShellBreadcrumb(): boolean {
    return !!this.shellSlots().breadcrumb;
  }

  showShellBreadcrumbContent(): boolean {
    return this.shellBreadcrumbItems().length > 0;
  }

  pageTitle(): string {
    switch (this.activeScreenRenderer()) {
      case "tenant-list":
        return this.shellContent()?.pageTitles?.tenantList
          ?? this.tenantListContent()?.title
          ?? this.screenNode()?.name?.trim()
          ?? "Application";
      case "tenant-factsheet":
        return this.shellContent()?.pageTitles?.tenantFactsheet
          ?? this.tenantFactsheetContent()?.title
          ?? this.screenNode()?.name?.trim()
          ?? "Application";
      default:
        return this.screenNode()?.name?.trim() || "Application";
    }
  }

  skipLinkLabel(): string {
    return this.shellContent()?.skipLinkLabel ?? '';
  }

  menuToggleAriaLabel(): string {
    return this.shellContent()?.menuToggleAriaLabel ?? '';
  }

  menuIconSrc(): string {
    return this.shellContent()?.menuIconSrc ?? '';
  }

  logoAriaLabel(): string {
    return this.shellContent()?.logoAriaLabel ?? '';
  }

  logoSrc(): string {
    return this.shellContent()?.logoSrc ?? '';
  }

  logoAlt(): string {
    return this.shellContent()?.logoAlt ?? '';
  }

  pageIndicatorIconSrc(): string {
    return this.shellContent()?.pageIndicatorIconSrc ?? '';
  }

  notificationAriaLabel(): string {
    return this.shellContent()?.notificationAriaLabel ?? '';
  }

  notificationIconSrc(): string {
    return this.shellContent()?.notificationIconSrc ?? '';
  }

  notificationCount(): string {
    return this.shellContent()?.notificationCount ?? '';
  }

  helpAriaLabel(): string {
    return this.shellContent()?.helpAriaLabel ?? '';
  }

  helpIconSrc(): string {
    return this.shellContent()?.helpIconSrc ?? '';
  }

  languageLabel(): string {
    return this.shellContent()?.languageLabel ?? '';
  }

  languageAriaLabel(): string {
    return this.shellContent()?.languageAriaLabel ?? '';
  }

  languageFlagSrc(): string {
    return this.shellContent()?.languageFlagSrc ?? '';
  }

  userMenuAriaLabel(): string {
    return this.shellContent()?.userMenuAriaLabel ?? '';
  }

  userAvatarLabel(): string {
    return this.shellContent()?.userAvatarLabel ?? '';
  }

  private slotHeight(slotNode: SystemShellGraphNode | null, fallback: string): string {
    return this.slotCssValue(slotNode, 'height', this.slotCssValue(slotNode, 'minHeight', fallback));
  }

  private slotCssValue(
    slotNode: SystemShellGraphNode | null,
    attribute: 'height' | 'minHeight',
    fallback: string,
  ): string {
    return this.cssLength(slotNode?.[attribute], fallback);
  }

  private cssLength(value: string | null | undefined, fallback: string): string {
    const normalizedValue = value?.trim();
    return normalizedValue ? normalizedValue : fallback;
  }

  protected slotNode(slot: string | null | undefined) {
    const normalizedSlot = slot?.trim();
    if (!normalizedSlot) {
      return null;
    }

    return this.shellSlots()[normalizedSlot as keyof ReturnType<typeof this.shellSlots>] ?? null;
  }

  protected selectedPreviewGuid(): string | null {
    return this.selectedGuid();
  }

  private selectObject(objectId: string | null): void {
    if (!objectId) {
      return;
    }

    this.state.selectObjectId(objectId);
  }
}
