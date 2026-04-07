export type DesignStatus = 'COMPLETE' | 'SPECIFIED' | 'NOT_STARTED';
export type PrototypeStatus = 'PROTOTYPED' | 'NOT_STARTED';
export type DeliveryStatus = 'INTEGRATED' | 'TESTED' | 'NOT_STARTED';
export type GapType = 'warning' | 'info' | 'error';

export interface CrossCutting {
  wcag: string;
  responsive: boolean;
  roleAdaptive: boolean;
  deepLinkable: boolean;
  loadingStates: boolean;
  messageRegistryCount: number;
}

export interface Gap {
  type: GapType;
  severity: string;
  desc: string;
}

export interface ContentElement {
  element: string;
  type: string;
  description: string;
}

export interface RoleSummary {
  roleKey: string;
  displayName: string;
  roleGroup: string | null;
  copyRestricted: boolean;
  sortOrder: number | null;
  screenCount: number;
  touchpointCount: number;
  interactionCount: number;
  journeyCount: number;
}

export interface UserStorySummary {
  storyId: string;
  label: string;
  module: string | null;
  domain: string | null;
  storyNumber: string | null;
  screenCount: number;
  externalWorkflowState: string | null;
  externalPriority: string | null;
  externalOwner: string | null;
  externalLabels: string[];
  externalRefs: string[];
}

export interface ScreenLegacy {
  stories: string[];
  errorCodes: string[];
  confirmations: string[];
  emptyState: boolean;
  transitions: string[];
  gaps: Gap[];
  content: ContentElement[];
}

export interface Screen {
  surfaceId: string;
  label: string;
  module: string;
  routePath: string | null;
  storyRefs: string[];
  stories: UserStorySummary[];
  uxSpecRef: string;
  roleKeys: string[];
  roles: RoleSummary[];
  personaIds: string[];
  designStatus: DesignStatus;
  prototypeStatus: PrototypeStatus;
  deliveryStatus: DeliveryStatus;
  crossCutting: CrossCutting;
  gapRefs: string[];
  sourceRefs: string[];
  notes?: string;
  _legacy: ScreenLegacy;
}

export type DetailTab =
  | 'detail'
  | 'inventory'
  | 'touchpoints'
  | 'interactions'
  | 'journeys'
  | 'architecture'
  | 'delivery'
  | 'automation'
  | 'traceability'
  | 'benchmark'
  | 'verification'
  | 'crosscutting';

export interface DisplayOptions {
  showTransitions: boolean;
  showGaps: boolean;
  showErrorCodes: boolean;
  showDialogs: boolean;
  showEmptyStates: boolean;
}

export interface DesignHubStats {
  totalScreens: number;
  completeCount: number;
  specifiedCount: number;
  notStartedCount: number;
  totalGaps: number;
  coveragePercent: number;
}
