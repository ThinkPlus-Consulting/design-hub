import { Injectable, inject, signal, computed } from '@angular/core';
import { catchError, forkJoin, of } from 'rxjs';
import { DesignHubApiService } from './design-hub-api.service';
import {
  Screen,
  DesignHubStats,
  DisplayOptions,
  DetailTab,
  RoleSummary,
  UserStorySummary,
} from '../../../models';
import { Touchpoint } from '../../../models';
import { Interaction } from '../../../models';
import { Journey } from '../../../models';

@Injectable()
export class DesignHubStateService {
  private readonly api = inject(DesignHubApiService);

  // --- Data signals (loaded from API) ---
  readonly screens = signal<Screen[]>([]);
  readonly roles = signal<RoleSummary[]>([]);
  readonly stories = signal<UserStorySummary[]>([]);
  readonly touchpoints = signal<Touchpoint[]>([]);
  readonly interactions = signal<Interaction[]>([]);
  readonly journeys = signal<Journey[]>([]);
  readonly stats = signal<DesignHubStats | null>(null);
  readonly loading = signal(true);

  // --- Filter signals ---
  readonly selectedModule = signal<string>('all');
  readonly selectedDesignStatus = signal<string>('all');
  readonly searchTerm = signal('');
  readonly selectedScreenId = signal<string | null>(null);

  // --- Display options ---
  readonly showTransitions = signal(true);
  readonly showGaps = signal(true);
  readonly displayOptions = signal<DisplayOptions>({
    showTransitions: true,
    showGaps: true,
    showErrorCodes: false,
    showDialogs: false,
    showEmptyStates: false,
  });

  // --- Canvas signals ---
  readonly zoom = signal<number>(1);
  readonly panX = signal<number>(0);
  readonly panY = signal<number>(0);
  readonly pageZoom = signal<number>(1);

  // --- UI state ---
  readonly activeTab = signal<DetailTab>('detail');

  // --- Computed signals ---
  readonly filteredScreens = computed(() => {
    let result = this.screens();
    const mod = this.selectedModule();
    const status = this.selectedDesignStatus();
    const search = this.searchTerm().toLowerCase();

    if (mod !== 'all') {
      result = result.filter((s) => s.module === mod);
    }
    if (status !== 'all') {
      result = result.filter((s) => s.designStatus === status);
    }
    if (search) {
      result = result.filter(
        (s) =>
          s.label.toLowerCase().includes(search) ||
          s.surfaceId.toLowerCase().includes(search)
      );
    }
    return result;
  });

  readonly selectedScreen = computed(() => {
    const id = this.selectedScreenId();
    return id ? this.screens().find((s) => s.surfaceId === id) ?? null : null;
  });

  readonly selectedScreenRoles = computed<RoleSummary[]>(() => {
    const screen = this.selectedScreen();
    if (!screen) {
      return [];
    }

    if (screen.roles.length > 0) {
      return screen.roles;
    }

    const roleByKey = new Map(this.roles().map((role) => [role.roleKey, role]));
    return screen.roleKeys.map((roleKey) => roleByKey.get(roleKey) ?? this.fallbackRole(roleKey));
  });

  readonly selectedScreenStories = computed<UserStorySummary[]>(() => {
    const screen = this.selectedScreen();
    if (!screen) {
      return [];
    }

    if (screen.stories.length > 0) {
      return screen.stories;
    }

    const storyById = new Map(this.stories().map((story) => [story.storyId, story]));
    return screen.storyRefs.map((storyId) => storyById.get(storyId) ?? this.fallbackStory(storyId));
  });

  readonly modules = computed(() => {
    const mods = [...new Set(this.screens().map((s) => s.module))].sort();
    return ['all', ...mods];
  });

  readonly computedStats = computed<DesignHubStats>(() => {
    const all = this.screens();
    const total = all.length;
    const completeCount = all.filter((s) => s.designStatus === 'COMPLETE').length;
    const specifiedCount = all.filter((s) => s.designStatus === 'SPECIFIED').length;
    const notStartedCount = all.filter((s) => s.designStatus === 'NOT_STARTED').length;
    const totalGaps = all.reduce((sum, s) => sum + (s._legacy?.gaps.length ?? 0), 0);
    const coveragePercent = total > 0 ? Math.round((completeCount / total) * 100) : 0;

    return { totalScreens: total, completeCount, specifiedCount, notStartedCount, totalGaps, coveragePercent };
  });

  // --- Actions ---
  loadAll(): void {
    this.loading.set(true);
    forkJoin({
      screens: this.api.getScreens(),
      roles: this.api.getRoles().pipe(catchError(() => of([] as RoleSummary[]))),
      stories: this.api.getStories().pipe(catchError(() => of([] as UserStorySummary[]))),
      touchpoints: this.api.getTouchpoints().pipe(catchError(() => of([] as Touchpoint[]))),
      interactions: this.api.getInteractions().pipe(catchError(() => of([] as Interaction[]))),
      journeys: this.api.getJourneys().pipe(catchError(() => of([] as Journey[]))),
      stats: this.api.getStats().pipe(catchError(() => of(null))),
    }).subscribe({
      next: (data) => {
        this.screens.set(data.screens);
        this.roles.set(data.roles);
        this.stories.set(data.stories);
        this.touchpoints.set(data.touchpoints);
        this.interactions.set(data.interactions);
        this.journeys.set(data.journeys);
        this.stats.set(data.stats);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load design-hub data', err);
        this.loading.set(false);
      },
    });
  }

  selectScreen(surfaceId: string | null): void {
    this.selectedScreenId.set(surfaceId);
    if (surfaceId) {
      this.activeTab.set('detail');
    }
  }

  setModuleFilter(module: string): void {
    this.selectedModule.set(module);
  }

  setDesignStatusFilter(status: string): void {
    this.selectedDesignStatus.set(status);
  }

  setActiveTab(tab: DetailTab): void {
    this.activeTab.set(tab);
  }

  toggleDisplayOption(key: keyof DisplayOptions): void {
    this.displayOptions.update((opts) => ({
      ...opts,
      [key]: !opts[key],
    }));
  }

  setZoom(z: number): void {
    this.zoom.set(Math.max(0.2, Math.min(3, z)));
  }

  setPageZoom(z: number): void {
    this.pageZoom.set(Math.max(0.7, Math.min(2, z)));
  }

  resetPageZoom(): void {
    this.pageZoom.set(1);
  }

  setPan(x: number, y: number): void {
    this.panX.set(x);
    this.panY.set(y);
  }

  resetView(): void {
    this.zoom.set(1);
    this.panX.set(0);
    this.panY.set(0);
  }

  private fallbackRole(roleKey: string): RoleSummary {
    return {
      roleKey,
      displayName: roleKey
        .toLowerCase()
        .split('_')
        .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
        .join(' '),
      roleGroup: null,
      sortOrder: null,
      screenCount: 0,
      touchpointCount: 0,
      interactionCount: 0,
      journeyCount: 0,
    };
  }

  private fallbackStory(storyId: string): UserStorySummary {
    return {
      storyId,
      label: storyId,
      module: null,
      domain: null,
      storyNumber: null,
      screenCount: 0,
    };
  }
}
