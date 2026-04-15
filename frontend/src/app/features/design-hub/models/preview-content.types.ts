export type TenantType = 'MASTER' | 'REGULAR' | 'DOMINANT';

export type TenantStatus = 'ACTIVE' | 'SUSPENDED' | 'PROVISIONING' | 'PROVISIONING_FAILED';

export type HealthStatus = 'HEALTHY' | 'DEGRADED' | 'UNHEALTHY';

export interface TenantStats {
  readonly users: number;
  readonly agents: number;
  readonly types: number;
}

export interface TenantSummary {
  readonly id: string;
  readonly name: string;
  readonly shortName: string;
  readonly type: TenantType;
  readonly status: TenantStatus;
  readonly health: HealthStatus | null;
  readonly stats: TenantStats;
  readonly logoUrl: string | null;
}

export interface SelectOption<T> {
  readonly label: string;
  readonly value: T;
}

export interface TenantListViewModeConfig {
  readonly gridLabel: string;
  readonly tableLabel: string;
}

export interface TenantListFiltersConfig {
  readonly typePlaceholder: string;
  readonly statusPlaceholder: string;
  readonly clearLabel: string;
  readonly emptyStateActionLabel: string;
}

export interface TenantListActionsConfig {
  readonly filterToggleAriaLabel: string;
  readonly createLabel: string;
}

export interface TenantListEmptyStateConfig {
  readonly title: string;
  readonly noResultsMessage: string;
  readonly noTenantsMessage: string;
}

export interface TenantListStatsLabelsConfig {
  readonly users: string;
  readonly agents: string;
  readonly types: string;
}

export interface TenantListTableColumnsConfig {
  readonly tenant: string;
  readonly type: string;
  readonly status: string;
  readonly health: string;
  readonly users: string;
  readonly agents: string;
}

export interface TenantListScreenContentConfig {
  readonly title: string;
  readonly searchAriaLabel: string;
  readonly searchPlaceholder: string;
  readonly viewToggleAriaLabel: string;
  readonly viewMode: TenantListViewModeConfig;
  readonly filters: TenantListFiltersConfig;
  readonly actions: TenantListActionsConfig;
  readonly emptyState: TenantListEmptyStateConfig;
  readonly typeOptions: readonly SelectOption<TenantType | null>[];
  readonly statusOptions: readonly SelectOption<TenantStatus | null>[];
  readonly statsLabels: TenantListStatsLabelsConfig;
  readonly tableColumns: TenantListTableColumnsConfig;
  readonly healthUnknownLabel: string;
  readonly tenants: readonly TenantSummary[];
}

export type FactsheetTab =
  | 'users'
  | 'branding'
  | 'integrations'
  | 'dictionary'
  | 'agents'
  | 'studio'
  | 'audit'
  | 'health'
  | 'license';

export interface TabDefinition {
  readonly value: FactsheetTab;
  readonly label: string;
  readonly icon: string;
  readonly count: number | null;
}

export interface KpiChip {
  readonly label: string;
  readonly value: string;
  readonly icon: string;
}

export interface TenantFactsheet {
  readonly id: string;
  readonly name: string;
  readonly shortName: string;
  readonly type: TenantType;
  readonly status: TenantStatus;
  readonly health: HealthStatus;
  readonly logoUrl: string | null;
  readonly kpis: readonly KpiChip[];
}

export type LifecycleAction = 'suspend' | 'reactivate';

export interface LifecycleActionDef {
  readonly action: LifecycleAction;
  readonly label: string;
  readonly icon: string;
  readonly severity: 'danger' | 'warn' | 'success' | 'primary' | 'secondary';
}

export interface TenantUser {
  readonly id: string;
  readonly name: string;
  readonly email: string;
  readonly role: string;
  readonly status: 'Active' | 'Invited' | 'Disabled';
  readonly lastActive: string;
}

export interface Integration {
  readonly id: string;
  readonly name: string;
  readonly protocol: 'OIDC' | 'SAML' | 'LDAP';
  readonly enabled: boolean;
  readonly icon: string;
}

export interface DictionaryEntry {
  readonly id: string;
  readonly objectType: string;
  readonly attributeCount: number;
  readonly origin: 'Seeded' | 'Custom';
}

export interface AgentCard {
  readonly id: string;
  readonly name: string;
  readonly status: 'Deployed' | 'Draft';
  readonly skillCount: number;
}

export interface AuditEntry {
  readonly id: string;
  readonly timestamp: string;
  readonly actor: string;
  readonly action: string;
  readonly target: string;
}

export interface HealthCheck {
  readonly id: string;
  readonly service: string;
  readonly status: HealthStatus;
  readonly lastChecked: string;
  readonly icon: string;
}

export interface TenantLicenseAllocation {
  readonly licenseType: 'Tenant' | 'Admin' | 'User' | 'Viewer';
  readonly allocated: number;
  readonly assigned: number;
  readonly available: number;
}

export interface TenantLicenseSummary {
  readonly status: 'Active' | 'Expiring Soon' | 'Expired';
  readonly validFrom: string;
  readonly validUntil: string;
  readonly allocations: readonly TenantLicenseAllocation[];
}

export interface UsersSectionContentConfig {
  readonly searchAriaLabel: string;
  readonly searchPlaceholder: string;
  readonly filterToggleAriaLabel: string;
  readonly tableColumns: {
    readonly name: string;
    readonly email: string;
    readonly role: string;
    readonly status: string;
    readonly lastActive: string;
  };
}

export interface BrandingSectionContentConfig {
  readonly title: string;
  readonly description: string;
  readonly swatchTitle: string;
  readonly swatchLabels: readonly string[];
  readonly logoUploadTitle: string;
  readonly logoUploadPlaceholder: string;
  readonly previewLabel: string;
  readonly publishLabel: string;
}

export interface SectionWithSearchAndViewModeConfig {
  readonly searchAriaLabel: string;
  readonly searchPlaceholder: string;
  readonly filterToggleAriaLabel: string;
  readonly viewToggleAriaLabel: string;
  readonly viewMode: {
    readonly gridLabel: string;
    readonly tableLabel: string;
  };
}

export interface DictionarySectionContentConfig extends SectionWithSearchAndViewModeConfig {
  readonly tableColumns: {
    readonly objectType: string;
    readonly attributeCount: string;
    readonly origin: string;
  };
}

export interface AgentsSectionContentConfig {
  readonly title: string;
  readonly description: string;
  readonly skillsSuffix: string;
}

export interface StudioSectionContentConfig {
  readonly searchAriaLabel: string;
  readonly searchPlaceholder: string;
  readonly openStudioLabel: string;
  readonly configuredProcessesText: string;
  readonly helperText: string;
}

export interface AuditSectionContentConfig extends SectionWithSearchAndViewModeConfig {
  readonly tableColumns: {
    readonly timestamp: string;
    readonly actor: string;
    readonly action: string;
    readonly target: string;
  };
}

export interface LicenseSectionContentConfig {
  readonly title: string;
  readonly description: string;
  readonly summaryTitle: string;
  readonly validityPrefix: string;
  readonly validityConnector: string;
  readonly metrics: {
    readonly allocated: string;
    readonly assigned: string;
    readonly available: string;
  };
  readonly tableColumns: {
    readonly licenseType: string;
    readonly allocated: string;
    readonly assigned: string;
    readonly available: string;
  };
}

export interface TenantFactsheetScreenContentConfig {
  readonly title: string;
  readonly tenant: TenantFactsheet;
  readonly tabs: readonly TabDefinition[];
  readonly lifecycleActions: Partial<Record<TenantStatus, LifecycleActionDef | null>>;
  readonly actions: {
    readonly backLabel: string;
    readonly editLabel: string;
    readonly inviteUserLabel: string;
  };
  readonly usersSection: UsersSectionContentConfig;
  readonly brandingSection: BrandingSectionContentConfig;
  readonly integrationsSection: SectionWithSearchAndViewModeConfig & {
    readonly enabledLabel: string;
    readonly disabledLabel: string;
  };
  readonly dictionarySection: DictionarySectionContentConfig;
  readonly agentsSection: AgentsSectionContentConfig;
  readonly studioSection: StudioSectionContentConfig;
  readonly auditSection: AuditSectionContentConfig;
  readonly healthSection: SectionWithSearchAndViewModeConfig;
  readonly licenseSection: LicenseSectionContentConfig;
  readonly users: readonly TenantUser[];
  readonly integrations: readonly Integration[];
  readonly dictionary: readonly DictionaryEntry[];
  readonly agents: readonly AgentCard[];
  readonly auditLog: readonly AuditEntry[];
  readonly healthChecks: readonly HealthCheck[];
  readonly license: TenantLicenseSummary;
}

export interface ApplicationShellContentConfig {
  readonly skipLinkLabel: string;
  readonly menuToggleAriaLabel: string;
  readonly menuIconSrc: string;
  readonly logoAriaLabel: string;
  readonly logoSrc: string;
  readonly logoAlt: string;
  readonly pageIndicatorIconSrc: string;
  readonly homeLabel: string;
  readonly notificationAriaLabel: string;
  readonly notificationIconSrc: string;
  readonly notificationCount: string;
  readonly helpAriaLabel: string;
  readonly helpIconSrc: string;
  readonly languageLabel: string;
  readonly languageAriaLabel: string;
  readonly languageFlagSrc: string;
  readonly userMenuAriaLabel: string;
  readonly userAvatarLabel: string;
  readonly breadcrumbTenantRegistryLabel: string;
  readonly pageTitles: {
    readonly tenantList: string;
    readonly tenantFactsheet: string;
  };
}

export interface LoginScreenContentConfig {
  readonly welcomeTitle: string;
  readonly welcomeSubtitle: string;
  readonly logoSrc: string;
  readonly logoAlt: string;
  readonly tenantStepIndicator: string;
  readonly tenantStepTitle: string;
  readonly tenantSelectionLabel: string;
  readonly tenantSearchAriaLabel: string;
  readonly tenantSearchPlaceholder: string;
  readonly rememberTenantSelectionLabel: string;
  readonly authMethodStepIndicator: string;
  readonly authMethodStepTitle: string;
  readonly authBanner: string;
  readonly noAuthTitle: string;
  readonly noAuthText: string;
  readonly providerLogoText: string;
  readonly providerName: string;
  readonly rememberMeLabel: string;
  readonly expandActionLabel: string;
  readonly usernameLabel: string;
  readonly usernameValue: string;
  readonly usernamePlaceholder: string;
  readonly passwordLabel: string;
  readonly passwordValue: string;
  readonly passwordPlaceholder: string;
  readonly passwordToggleAriaLabel: string;
  readonly passwordToggleTitle: string;
  readonly signInLabel: string;
}

export interface MfaScreenContentConfig {
  readonly title: string;
  readonly description: string;
  readonly banner: string;
  readonly otpValue: string;
  readonly otpAriaLabel: string;
  readonly backLabel: string;
  readonly verifyLabel: string;
}

export interface TenantNotFoundScreenContentConfig {
  readonly welcomeTitle: string;
  readonly welcomeSubtitle: string;
  readonly logoWordmark: string;
  readonly notFoundTitle: string;
  readonly notFoundText: string;
  readonly notFoundActionLabel: string;
}
