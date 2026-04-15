import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { MessageModule } from 'primeng/message';
import { ShellBackgroundConfig } from '../../../../../../../../models/graph.types';
import { LoginScreenContentConfig } from '../../../../../../../../models/preview-content.types';
import { parseNodeConfiguration } from '../../../../../../../../utils/node-configuration';
import { PreviewIdentityBound } from '../../../../../../../../utils/preview-identity-bound';
import { childByName, componentByName } from '../../../../../../../../utils/preview-structure';

@Component({
  selector: 'app-login-screen-preview',
  standalone: true,
  imports: [MessageModule],
  templateUrl: './login-screen-preview.component.html',
  styleUrl: './login-screen-preview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginScreenPreviewComponent extends PreviewIdentityBound {
  readonly selectedGuid = input<string | null>(null);
  readonly backgroundConfig = input<ShellBackgroundConfig | null>(null);
  readonly screenObjectId = input<string | null>(null);
  private readonly screenNode = computed(() => this.state.nodeById(this.screenObjectId()));
  private readonly content = computed(() =>
    parseNodeConfiguration<LoginScreenContentConfig>(this.screenNode()),
  );
  private readonly slots = computed(() => {
    const screen = this.screenNode();
    const welcome = childByName(this.state, screen?.id, 'Welcome Container');
    const logo = childByName(this.state, screen?.id, 'Logo Container');
    const authRegion = childByName(this.state, screen?.id, 'Login Card');
    const tenantHeader = childByName(this.state, authRegion?.id, 'Tenant Selection Header Section');
    const tenantContent = childByName(this.state, authRegion?.id, 'Tenant Selection Content Section');
    const authMethodHeader = childByName(this.state, authRegion?.id, 'Authentication Methods Header Section');
    const authStatusContainer = childByName(this.state, authRegion?.id, 'Status Message Section');
    const noAuthContainer = childByName(this.state, authRegion?.id, 'No Auth Section');
    const providerHeader = childByName(this.state, authRegion?.id, 'Provider Header Section');
    const usernameField = childByName(this.state, authRegion?.id, 'User Name Field Section');
    const passwordField = childByName(this.state, authRegion?.id, 'Password Field Section');
    const signInBar = childByName(this.state, authRegion?.id, 'Sign In Action Section');

    return {
      screen,
      welcome,
      welcomeTitle: componentByName(this.state, welcome?.id, 'Screen Title'),
      welcomeSubtitle: componentByName(this.state, welcome?.id, 'Screen Subtitle'),
      logo,
      logoImage: componentByName(this.state, logo?.id, 'SVG Logo Renderer'),
      authRegion,
      tenantStep: tenantHeader,
      tenantHeader,
      tenantIndicator: componentByName(this.state, tenantHeader?.id, 'Tenant Selection Step Indicator'),
      tenantTitle: componentByName(this.state, tenantHeader?.id, 'Tenant Selection Step Title'),
      tenantContent,
      tenantDropdown: componentByName(this.state, tenantContent?.id, 'Tenant Registry Searchable Dropdown'),
      tenantRemember: componentByName(this.state, tenantContent?.id, 'Remember Tenant Selection Checkbox'),
      authMethodStep: authMethodHeader,
      authMethodHeader,
      authMethodIndicator: componentByName(this.state, authMethodHeader?.id, 'Authentication Methods Step Indicator'),
      authMethodTitle: componentByName(this.state, authMethodHeader?.id, 'Authentication Methods Step Title'),
      authStatusContainer,
      authBanner: componentByName(this.state, authStatusContainer?.id, 'Status Banner'),
      providerSelection: authRegion,
      noAuthContainer,
      noAuthTitle: componentByName(this.state, noAuthContainer?.id, 'No Auth Title'),
      noAuthText: componentByName(this.state, noAuthContainer?.id, 'No Auth Description'),
      providerCard: providerHeader,
      providerHeader,
      providerLogo: componentByName(this.state, providerHeader?.id, 'Auth Logo'),
      providerName: componentByName(this.state, providerHeader?.id, 'Auth Method Name'),
      providerRemember: componentByName(this.state, providerHeader?.id, 'Remember Me Selection'),
      providerExpand: componentByName(this.state, providerHeader?.id, 'Expand Action'),
      providerVariant: usernameField,
      usernameField,
      usernameLabel: componentByName(this.state, usernameField?.id, 'User Name Label'),
      usernameInput: componentByName(this.state, usernameField?.id, 'User Name Input'),
      passwordField,
      passwordLabel: componentByName(this.state, passwordField?.id, 'Password Label'),
      passwordInput: componentByName(this.state, passwordField?.id, 'Password Input'),
      passwordToggle: componentByName(this.state, passwordField?.id, 'Password Visibility Toggle'),
      signInBar,
      signInAction: componentByName(this.state, signInBar?.id, 'Sign In Action'),
    };
  });

  backgroundColorStyle(): string {
    return this.backgroundConfig()?.backgroundColorStyle ?? 'var(--tp-primary)';
  }

  backgroundPatternOpacity(): string {
    const opacity = this.backgroundConfig()?.backgroundPatternOpacity;
    return typeof opacity === 'number' ? String(opacity) : '0.13';
  }

  showPattern(): boolean {
    const patternKey = this.backgroundConfig()?.backgroundPatternKey;
    return patternKey ? true : this.backgroundConfig() === null;
  }

  welcomeTitle(): string {
    return this.content()?.welcomeTitle ?? '';
  }

  welcomeSubtitle(): string {
    return this.content()?.welcomeSubtitle ?? '';
  }

  logoSrc(): string {
    return this.content()?.logoSrc ?? '';
  }

  logoAlt(): string {
    return this.content()?.logoAlt ?? '';
  }

  tenantStepIndicator(): string {
    return this.content()?.tenantStepIndicator ?? '';
  }

  tenantStepTitle(): string {
    return this.content()?.tenantStepTitle ?? '';
  }

  tenantSelectionLabel(): string {
    return this.content()?.tenantSelectionLabel ?? '';
  }

  tenantSearchAriaLabel(): string {
    return this.content()?.tenantSearchAriaLabel ?? '';
  }

  tenantSearchPlaceholder(): string {
    return this.content()?.tenantSearchPlaceholder ?? '';
  }

  rememberTenantSelectionLabel(): string {
    return this.content()?.rememberTenantSelectionLabel ?? '';
  }

  authMethodStepIndicator(): string {
    return this.content()?.authMethodStepIndicator ?? '';
  }

  authMethodStepTitle(): string {
    return this.content()?.authMethodStepTitle ?? '';
  }

  authBanner(): string {
    return this.content()?.authBanner ?? '';
  }

  noAuthTitle(): string {
    return this.content()?.noAuthTitle ?? '';
  }

  noAuthText(): string {
    return this.content()?.noAuthText ?? '';
  }

  providerLogoText(): string {
    return this.content()?.providerLogoText ?? '';
  }

  providerName(): string {
    return this.content()?.providerName ?? '';
  }

  rememberMeLabel(): string {
    return this.content()?.rememberMeLabel ?? '';
  }

  expandActionLabel(): string {
    return this.content()?.expandActionLabel ?? '';
  }

  usernameLabel(): string {
    return this.content()?.usernameLabel ?? '';
  }

  usernameValue(): string {
    return this.content()?.usernameValue ?? '';
  }

  usernamePlaceholder(): string {
    return this.content()?.usernamePlaceholder ?? '';
  }

  passwordLabel(): string {
    return this.content()?.passwordLabel ?? '';
  }

  passwordValue(): string {
    return this.content()?.passwordValue ?? '';
  }

  passwordPlaceholder(): string {
    return this.content()?.passwordPlaceholder ?? '';
  }

  passwordToggleAriaLabel(): string {
    return this.content()?.passwordToggleAriaLabel ?? '';
  }

  passwordToggleTitle(): string {
    return this.content()?.passwordToggleTitle ?? '';
  }

  signInLabel(): string {
    return this.content()?.signInLabel ?? '';
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
