import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Screen, DesignHubStats, RoleSummary, UserStorySummary } from '../../../models';
import { Touchpoint } from '../../../models';
import { Interaction } from '../../../models';
import { Journey } from '../../../models';

interface RawGap {
  type?: 'warning' | 'info' | 'error';
  severity?: string;
  description?: string | null;
}

interface RawContentElement {
  element?: string;
  type?: string;
  description?: string;
}

interface RawScreenTransition {
  surfaceId?: string;
}

interface RawScreen {
  surfaceId?: string;
  label?: string;
  module?: string;
  routePath?: string | null;
  storyRefs?: string[];
  stories?: RawUserStory[];
  roleKeys?: string[];
  roles?: RawRole[];
  personaIds?: string[];
  designStatus?: 'COMPLETE' | 'SPECIFIED' | 'NOT_STARTED';
  prototypeStatus?: 'PROTOTYPED' | 'NOT_STARTED';
  deliveryStatus?: 'INTEGRATED' | 'TESTED' | 'NOT_STARTED';
  wcag?: string;
  responsive?: boolean;
  roleAdaptive?: boolean;
  deepLinkable?: boolean;
  loadingStates?: boolean;
  messageRegistryCount?: number;
  notes?: string | null;
  gaps?: RawGap[];
  contentElements?: RawContentElement[];
  transitionsTo?: RawScreenTransition[];
}

interface RawEntryMode {
  channelId?: string;
  mechanism?: string;
}

interface RawTouchpoint {
  touchpointId?: string;
  label?: string;
  surfaceId?: string;
  personaIds?: string[];
  roleKeys?: string[];
  entryModes?: RawEntryMode[];
}

interface RawEffect {
  type?: Interaction['effects'][number]['type'];
  target?: string | null;
  targetMode?: Interaction['effects'][number]['targetMode'];
  resolutionRule?: string;
  defaultTarget?: string;
}

interface RawInteraction {
  interactionId?: string;
  surfaceId?: string;
  element?: string;
  trigger?: string;
  permission?: string | null;
  confirmationCode?: string | null;
  personaIds?: string[];
  roleKeys?: string[];
  apiCalls?: string[];
  effects?: RawEffect[];
}

interface RawJourneyStep {
  stepId?: string;
  interactionRef?: string | null;
  label?: string;
  preCondition?: string;
  postCondition?: string;
  orderIndex?: number;
}

interface RawJourney {
  journeyId?: string;
  title?: string;
  personaId?: string | null;
  roleKey?: string | null;
  goalStatement?: string;
  sourceRefs?: string[];
  designStatus?: Journey['designStatus'];
  prototypeStatus?: Journey['prototypeStatus'];
  deliveryStatus?: Journey['deliveryStatus'];
  steps?: RawJourneyStep[];
}

interface RawStats {
  totalScreens?: number;
  designComplete?: number;
  designSpecified?: number;
  designNotStarted?: number;
  designCompletePercent?: number;
}

interface RawRole {
  roleKey?: string;
  displayName?: string;
  roleGroup?: string | null;
  sortOrder?: number | null;
  screenCount?: number;
  touchpointCount?: number;
  interactionCount?: number;
  journeyCount?: number;
}

interface RawUserStory {
  storyId?: string;
  label?: string;
  module?: string | null;
  domain?: string | null;
  storyNumber?: string | null;
  screenCount?: number;
}

@Injectable({ providedIn: 'root' })
export class DesignHubApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/api/v1/design-hub`;

  getScreens(): Observable<Screen[]> {
    return this.http
      .get<RawScreen[]>(`${this.baseUrl}/screens`)
      .pipe(map((screens) => screens.map((screen) => this.adaptScreen(screen))));
  }

  getScreen(surfaceId: string): Observable<Screen> {
    return this.http
      .get<RawScreen>(`${this.baseUrl}/screens/${surfaceId}`)
      .pipe(map((screen) => this.adaptScreen(screen)));
  }

  getFilteredScreens(module?: string, status?: string): Observable<Screen[]> {
    let params = new HttpParams();
    if (module && module !== 'all') {
      params = params.set('module', module);
    }
    if (status && status !== 'all') {
      params = params.set('status', status);
    }
    return this.http
      .get<RawScreen[]>(`${this.baseUrl}/screens/filtered`, { params })
      .pipe(map((screens) => screens.map((screen) => this.adaptScreen(screen))));
  }

  getTouchpoints(): Observable<Touchpoint[]> {
    return this.http
      .get<RawTouchpoint[]>(`${this.baseUrl}/touchpoints`)
      .pipe(map((touchpoints) => touchpoints.map((touchpoint) => this.adaptTouchpoint(touchpoint))));
  }

  getInteractions(): Observable<Interaction[]> {
    return this.http
      .get<RawInteraction[]>(`${this.baseUrl}/interactions`)
      .pipe(map((interactions) => interactions.map((interaction) => this.adaptInteraction(interaction))));
  }

  getInteractionsBySurface(surfaceId: string): Observable<Interaction[]> {
    return this.http
      .get<RawInteraction[]>(`${this.baseUrl}/interactions/by-screen/${surfaceId}`)
      .pipe(map((interactions) => interactions.map((interaction) => this.adaptInteraction(interaction))));
  }

  getJourneys(): Observable<Journey[]> {
    return this.http
      .get<RawJourney[]>(`${this.baseUrl}/journeys`)
      .pipe(map((journeys) => journeys.map((journey) => this.adaptJourney(journey))));
  }

  getRoles(): Observable<RoleSummary[]> {
    return this.http
      .get<RawRole[]>(`${this.baseUrl}/roles`)
      .pipe(map((roles) => roles.map((role) => this.adaptRole(role))));
  }

  getStories(): Observable<UserStorySummary[]> {
    return this.http
      .get<RawUserStory[]>(`${this.baseUrl}/stories`)
      .pipe(map((stories) => stories.map((story) => this.adaptStory(story))));
  }

  getStats(): Observable<DesignHubStats> {
    return this.http
      .get<RawStats>(`${this.baseUrl}/stats`)
      .pipe(
        map((stats) => ({
          totalScreens: stats.totalScreens ?? 0,
          completeCount: stats.designComplete ?? 0,
          specifiedCount: stats.designSpecified ?? 0,
          notStartedCount: stats.designNotStarted ?? 0,
          totalGaps: 0,
          coveragePercent: stats.designCompletePercent ?? 0,
        }))
      );
  }

  saveNotes(surfaceId: string, text: string): Observable<Screen> {
    return this.http
      .put<RawScreen>(`${this.baseUrl}/screens/${surfaceId}/notes`, { text })
      .pipe(map((screen) => this.adaptScreen(screen)));
  }

  getNotes(surfaceId: string): Observable<{ text: string }> {
    return this.http.get<{ text: string }>(`${this.baseUrl}/screens/${surfaceId}/notes`);
  }

  private adaptScreen(screen: RawScreen): Screen {
    return {
      surfaceId: screen.surfaceId ?? '',
      label: screen.label ?? '',
      module: screen.module ?? '',
      routePath: screen.routePath ?? null,
      storyRefs: screen.storyRefs ?? [],
      stories: (screen.stories ?? []).map((story) => this.adaptStory(story)),
      uxSpecRef: '',
      roleKeys: screen.roleKeys ?? [],
      roles: (screen.roles ?? []).map((role) => this.adaptRole(role)),
      personaIds: screen.personaIds ?? [],
      designStatus: screen.designStatus ?? 'NOT_STARTED',
      prototypeStatus: screen.prototypeStatus ?? 'NOT_STARTED',
      deliveryStatus: screen.deliveryStatus ?? 'NOT_STARTED',
      crossCutting: {
        wcag: screen.wcag ?? 'N/A',
        responsive: screen.responsive ?? false,
        roleAdaptive: screen.roleAdaptive ?? false,
        deepLinkable: screen.deepLinkable ?? false,
        loadingStates: screen.loadingStates ?? false,
        messageRegistryCount: screen.messageRegistryCount ?? 0,
      },
      gapRefs: [],
      sourceRefs: [],
      notes: screen.notes ?? undefined,
      _legacy: {
        stories: screen.storyRefs ?? [],
        errorCodes: [],
        confirmations: [],
        emptyState: false,
        transitions: (screen.transitionsTo ?? [])
          .map((transition) => transition.surfaceId)
          .filter((surfaceId): surfaceId is string => Boolean(surfaceId)),
        gaps: (screen.gaps ?? []).map((gap) => ({
          type: gap.type ?? 'info',
          severity: gap.severity ?? '',
          desc: gap.description ?? '',
        })),
        content: (screen.contentElements ?? []).map((content) => ({
          element: content.element ?? '',
          type: content.type ?? '',
          description: content.description ?? '',
        })),
      },
    };
  }

  private adaptTouchpoint(touchpoint: RawTouchpoint): Touchpoint {
    return {
      touchpointId: touchpoint.touchpointId ?? '',
      label: touchpoint.label ?? '',
      surfaceId: touchpoint.surfaceId ?? '',
      personaIds: touchpoint.personaIds ?? [],
      roleKeys: touchpoint.roleKeys ?? [],
      entryModes: (touchpoint.entryModes ?? []).map((entryMode) => ({
        channelId: entryMode.channelId ?? 'unknown',
        mechanism: entryMode.mechanism ?? '',
      })),
      journeyStepRefs: [],
      sourceRefs: [],
    };
  }

  private adaptInteraction(interaction: RawInteraction): Interaction {
    return {
      interactionId: interaction.interactionId ?? '',
      surfaceId: interaction.surfaceId ?? '',
      element: interaction.element ?? '',
      trigger: interaction.trigger ?? '',
      permission: interaction.permission ?? null,
      personaIds: interaction.personaIds ?? [],
      roleKeys: interaction.roleKeys ?? [],
      effects: (interaction.effects ?? []).map((effect) => ({
        type: effect.type ?? 'toast',
        target: effect.target ?? effect.defaultTarget ?? null,
        targetMode: effect.targetMode ?? 'static',
        resolutionRule: effect.resolutionRule,
        defaultTarget: effect.defaultTarget,
      })),
      apiCalls: interaction.apiCalls ?? [],
      outcomes: {
        success: null,
        error: null,
        loading: null,
      },
      confirmationCode: interaction.confirmationCode ?? null,
      journeyStepRefs: [],
      sourceRefs: [],
    };
  }

  private adaptJourney(journey: RawJourney): Journey {
    return {
      journeyId: journey.journeyId ?? '',
      title: journey.title ?? '',
      personaId: journey.personaId ?? null,
      roleKey: journey.roleKey ?? null,
      goalStatement: journey.goalStatement ?? '',
      sourceRefs: journey.sourceRefs ?? [],
      designStatus: journey.designStatus ?? 'NOT_STARTED',
      prototypeStatus: journey.prototypeStatus ?? 'NOT_STARTED',
      deliveryStatus: journey.deliveryStatus ?? 'NOT_STARTED',
      steps: [...(journey.steps ?? [])]
        .sort((a, b) => (a.orderIndex ?? 0) - (b.orderIndex ?? 0))
        .map((step) => ({
          stepId: step.stepId ?? '',
          interactionRef: step.interactionRef ?? null,
          label: step.label ?? '',
          preCondition: step.preCondition ?? '',
          postCondition: step.postCondition ?? '',
        })),
    };
  }

  private adaptRole(role: RawRole): RoleSummary {
    return {
      roleKey: role.roleKey ?? '',
      displayName: role.displayName ?? role.roleKey ?? '',
      roleGroup: role.roleGroup ?? null,
      sortOrder: role.sortOrder ?? null,
      screenCount: role.screenCount ?? 0,
      touchpointCount: role.touchpointCount ?? 0,
      interactionCount: role.interactionCount ?? 0,
      journeyCount: role.journeyCount ?? 0,
    };
  }

  private adaptStory(story: RawUserStory): UserStorySummary {
    return {
      storyId: story.storyId ?? '',
      label: story.label ?? story.storyId ?? '',
      module: story.module ?? null,
      domain: story.domain ?? null,
      storyNumber: story.storyNumber ?? null,
      screenCount: story.screenCount ?? 0,
    };
  }
}
