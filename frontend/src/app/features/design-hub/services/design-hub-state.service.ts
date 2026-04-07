import { Injectable, inject, signal, computed } from '@angular/core';
import { catchError, forkJoin, of } from 'rxjs';
import { DesignHubApiService } from './design-hub-api.service';
import {
  AgentPack,
  ApplicationArchitecture,
  ApplicationSummary,
  ArchitectureView,
  BusinessArchitecture,
  BusinessCapabilitySummary,
  ChannelSummary,
  ChannelTraversal,
  DataArchitecture,
  DataArchitectureObjectSummary,
  DeliveryStory,
  ExternalArtifactDetail,
  ExternalParityAudit,
  ExternalSyncJobResult,
  ExternalSyncSourceStatus,
  GraphBenchmark,
  InfrastructureArchitecture,
  InfrastructureDeploymentSummary,
  JourneyTraversal,
  ObjectDefinitionDetail,
  ObjectDefinitionSummary,
  PersonaSummary,
  PersonaTraversal,
  ReadinessDiagnostics,
  Screen,
  DesignHubStats,
  DisplayOptions,
  DetailTab,
  RoleSummary,
  StoryTraceability,
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
  readonly channels = signal<ChannelSummary[]>([]);
  readonly personas = signal<PersonaSummary[]>([]);
  readonly businessCapabilities = signal<BusinessCapabilitySummary[]>([]);
  readonly applicationSummaries = signal<ApplicationSummary[]>([]);
  readonly dataObjectSummaries = signal<DataArchitectureObjectSummary[]>([]);
  readonly infrastructureDeployments = signal<InfrastructureDeploymentSummary[]>([]);
  readonly interactions = signal<Interaction[]>([]);
  readonly journeys = signal<Journey[]>([]);
  readonly deliveryStories = signal<DeliveryStory[]>([]);
  readonly selectedAgentPack = signal<AgentPack | null>(null);
  readonly storyTraceability = signal<StoryTraceability | null>(null);
  readonly benchmark = signal<GraphBenchmark | null>(null);
  readonly selectedBusinessArchitecture = signal<BusinessArchitecture | null>(null);
  readonly selectedApplicationArchitecture = signal<ApplicationArchitecture | null>(null);
  readonly selectedDataArchitecture = signal<DataArchitecture | null>(null);
  readonly selectedInfrastructureArchitecture = signal<InfrastructureArchitecture | null>(null);
  readonly selectedChannelTraversal = signal<ChannelTraversal | null>(null);
  readonly selectedJourneyTraversal = signal<JourneyTraversal | null>(null);
  readonly selectedPersonaTraversal = signal<PersonaTraversal | null>(null);
  readonly selectedScreenReadiness = signal<ReadinessDiagnostics | null>(null);
  readonly selectedStoryReadiness = signal<ReadinessDiagnostics | null>(null);
  readonly selectedExternalArtifact = signal<ExternalArtifactDetail | null>(null);
  readonly externalParityAudit = signal<ExternalParityAudit | null>(null);
  readonly externalSyncSourceStatuses = signal<ExternalSyncSourceStatus[]>([]);
  readonly externalSyncJobs = signal<ExternalSyncJobResult[]>([]);
  readonly stats = signal<DesignHubStats | null>(null);
  readonly objectDefinitions = signal<ObjectDefinitionSummary[]>([]);
  readonly selectedObjectDefinition = signal<ObjectDefinitionDetail | null>(null);
  readonly loading = signal(true);

  // --- Filter signals ---
  readonly selectedModule = signal<string>('all');
  readonly selectedDesignStatus = signal<string>('all');
  readonly searchTerm = signal('');
  readonly selectedScreenId = signal<string | null>(null);
  readonly selectedArchitectureView = signal<ArchitectureView>('business');
  readonly selectedBusinessCapabilityId = signal<string | null>(null);
  readonly selectedApplicationId = signal<string | null>(null);
  readonly selectedDataObjectId = signal<string | null>(null);
  readonly selectedDeploymentId = signal<string | null>(null);
  readonly selectedChannelCode = signal<string | null>(null);
  readonly selectedDeliveryStoryId = signal<string | null>(null);
  readonly selectedExternalArtifactId = signal<string | null>(null);
  readonly selectedExternalSyncHistorySource = signal<string>('ALL');
  readonly selectedPersonaId = signal<string | null>(null);
  readonly selectedJourneyId = signal<string | null>(null);
  readonly selectedBenchmarkNodeType = signal<string | null>(null);
  readonly selectedObjectDefinitionType = signal<string | null>(null);
  readonly definitionSearchTerm = signal('');

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

  readonly selectedBusinessCapability = computed(() => {
    const id = this.selectedBusinessCapabilityId();
    return id ? this.businessCapabilities().find((capability) => capability.capabilityId === id) ?? null : null;
  });

  readonly selectedApplicationSummary = computed(() => {
    const id = this.selectedApplicationId();
    return id ? this.applicationSummaries().find((application) => application.applicationId === id) ?? null : null;
  });

  readonly selectedDataObjectSummary = computed(() => {
    const id = this.selectedDataObjectId();
    return id ? this.dataObjectSummaries().find((object) => object.objectId === id) ?? null : null;
  });

  readonly selectedInfrastructureDeployment = computed(() => {
    const id = this.selectedDeploymentId();
    return id ? this.infrastructureDeployments().find((deployment) => deployment.deploymentId === id) ?? null : null;
  });

  readonly selectedDeliveryStory = computed(() => {
    const id = this.selectedDeliveryStoryId();
    return id ? this.deliveryStories().find((story) => story.storyId === id) ?? null : null;
  });

  readonly selectedChannel = computed(() => {
    const code = this.selectedChannelCode();
    return code ? this.channels().find((channel) => channel.channelCode === code) ?? null : null;
  });

  readonly selectedJourney = computed(() => {
    const id = this.selectedJourneyId();
    return id ? this.journeys().find((journey) => journey.journeyId === id) ?? null : null;
  });

  readonly selectedPersonaSummary = computed(() => {
    const id = this.selectedPersonaId();
    return id ? this.personas().find((persona) => persona.personaId === id) ?? null : null;
  });

  readonly selectedBenchmarkType = computed(() => {
    const benchmark = this.benchmark();
    if (!benchmark) {
      return null;
    }

    const selectedNodeType = this.selectedBenchmarkNodeType();
    return benchmark.types.find((type) => type.nodeType === selectedNodeType) ?? benchmark.types[0] ?? null;
  });

  readonly selectedScreenRoles = computed<RoleSummary[]>(() => {
    const screen = this.selectedScreen();
    return screen?.roles ?? [];
  });

  readonly selectedScreenStories = computed<UserStorySummary[]>(() => {
    const screen = this.selectedScreen();
    return screen?.stories ?? [];
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

  readonly filteredObjectDefinitions = computed(() => {
    const search = this.definitionSearchTerm().trim().toLowerCase();
    if (!search) {
      return this.objectDefinitions();
    }

    return this.objectDefinitions().filter((definition) =>
      definition.displayName.toLowerCase().includes(search)
      || definition.label.toLowerCase().includes(search)
      || definition.category.toLowerCase().includes(search)
      || definition.type.toLowerCase().includes(search)
    );
  });

  // --- Actions ---
  loadObjectDefinitions(): void {
    this.loading.set(true);
    this.api.getObjectDefinitions()
      .pipe(catchError(() => of([] as ObjectDefinitionSummary[])))
      .subscribe({
        next: (definitions) => {
          this.objectDefinitions.set(definitions);
          const selectedType = this.selectedObjectDefinitionType() ?? definitions[0]?.type ?? null;
          this.selectedObjectDefinitionType.set(selectedType);
          this.loadObjectDefinitionDetail(selectedType);
          this.loading.set(false);
        },
        error: (err) => {
          console.error('Failed to load object definitions', err);
          this.loading.set(false);
        },
      });
  }

  loadAll(): void {
    this.loading.set(true);
    forkJoin({
      screens: this.api.getScreens(),
      roles: this.api.getRoles().pipe(catchError(() => of([] as RoleSummary[]))),
      stories: this.api.getStories().pipe(catchError(() => of([] as UserStorySummary[]))),
      channels: this.api.getChannels().pipe(catchError(() => of([] as ChannelSummary[]))),
      personas: this.api.getPersonas().pipe(catchError(() => of([] as PersonaSummary[]))),
      businessCapabilities: this.api.getBusinessCapabilities().pipe(catchError(() => of([] as BusinessCapabilitySummary[]))),
      applications: this.api.getApplications().pipe(catchError(() => of([] as ApplicationSummary[]))),
      dataObjects: this.api.getDataObjects().pipe(catchError(() => of([] as DataArchitectureObjectSummary[]))),
      deployments: this.api.getInfrastructureDeployments().pipe(catchError(() => of([] as InfrastructureDeploymentSummary[]))),
      deliveryStories: this.api.getDeliveryStories().pipe(catchError(() => of([] as DeliveryStory[]))),
      touchpoints: this.api.getTouchpoints().pipe(catchError(() => of([] as Touchpoint[]))),
      interactions: this.api.getInteractions().pipe(catchError(() => of([] as Interaction[]))),
      journeys: this.api.getJourneys().pipe(catchError(() => of([] as Journey[]))),
      stats: this.api.getStats().pipe(catchError(() => of(null))),
    }).subscribe({
      next: (data) => {
        this.screens.set(data.screens);
        this.roles.set(data.roles);
        this.stories.set(data.stories);
        this.channels.set(data.channels);
        this.personas.set(data.personas);
        this.businessCapabilities.set(data.businessCapabilities);
        this.applicationSummaries.set(data.applications);
        this.dataObjectSummaries.set(data.dataObjects);
        this.infrastructureDeployments.set(data.deployments);
        this.deliveryStories.set(data.deliveryStories);
        this.touchpoints.set(data.touchpoints);
        this.interactions.set(data.interactions);
        this.journeys.set(data.journeys);
        this.stats.set(data.stats);
        const selectedBusinessCapabilityId = this.selectedBusinessCapabilityId() ?? data.businessCapabilities[0]?.capabilityId ?? null;
        const selectedApplicationId = this.selectedApplicationId() ?? data.applications[0]?.applicationId ?? null;
        const selectedDataObjectId = this.selectedDataObjectId() ?? data.dataObjects[0]?.objectId ?? null;
        const selectedDeploymentId = this.selectedDeploymentId() ?? data.deployments[0]?.deploymentId ?? null;
        const selectedChannelCode = this.selectedChannelCode() ?? data.channels[0]?.channelCode ?? null;
        const selectedStoryId = this.selectedDeliveryStoryId() ?? data.deliveryStories[0]?.storyId ?? null;
        const selectedExternalArtifactId = this.resolveExternalArtifactId(
          selectedStoryId,
          data.deliveryStories,
          this.selectedExternalArtifactId()
        );
        const selectedPersonaId = this.resolveInitialPersonaId(data.personas, data.journeys);
        const selectedJourneyId = this.resolveJourneyIdForPersona(selectedPersonaId, data.journeys, this.selectedJourneyId());
        this.selectedBusinessCapabilityId.set(selectedBusinessCapabilityId);
        this.selectedApplicationId.set(selectedApplicationId);
        this.selectedDataObjectId.set(selectedDataObjectId);
        this.selectedDeploymentId.set(selectedDeploymentId);
        this.selectedChannelCode.set(selectedChannelCode);
        this.selectedDeliveryStoryId.set(selectedStoryId);
        this.selectedExternalArtifactId.set(selectedExternalArtifactId);
        this.selectedPersonaId.set(selectedPersonaId);
        this.selectedJourneyId.set(selectedJourneyId);
        this.loadBusinessArchitecture(selectedBusinessCapabilityId);
        this.loadApplicationArchitecture(selectedApplicationId);
        this.loadDataArchitecture(selectedDataObjectId);
        this.loadInfrastructureArchitecture(selectedDeploymentId);
        this.loadChannelTraversal(selectedChannelCode);
        this.loadTraceabilityStory(selectedStoryId);
        this.loadStoryReadiness(selectedStoryId);
        this.loadAgentPack(selectedStoryId);
        this.loadExternalArtifact(selectedExternalArtifactId);
        this.loadPersonaTraversal(selectedPersonaId);
        this.loadJourneyTraversal(selectedJourneyId);
        this.loadScreenReadiness(this.selectedScreenId());
        this.loading.set(false);
        this.loadDeferredSummaries();
      },
      error: (err) => {
        console.error('Failed to load design-hub data', err);
        this.loading.set(false);
      },
    });
  }

  private loadDeferredSummaries(): void {
    forkJoin({
      benchmark: this.api.getBenchmark().pipe(catchError(() => of(null))),
      externalParityAudit: this.api.getExternalParityAudit().pipe(catchError(() => of(null))),
      externalSyncSourceStatuses: this.api.getExternalSyncSourceStatuses().pipe(catchError(() => of([] as ExternalSyncSourceStatus[]))),
      externalSyncJobs: this.api.getExternalSyncJobs(12, this.externalSyncHistorySourceFilter()).pipe(catchError(() => of([] as ExternalSyncJobResult[]))),
    }).subscribe({
      next: (data) => {
        this.benchmark.set(data.benchmark);
        this.externalParityAudit.set(data.externalParityAudit);
        this.externalSyncSourceStatuses.set(data.externalSyncSourceStatuses);
        this.externalSyncJobs.set(data.externalSyncJobs);
        if (data.benchmark && !this.selectedBenchmarkNodeType() && data.benchmark.types.length > 0) {
          this.selectedBenchmarkNodeType.set(data.benchmark.types[0].nodeType);
        }
      },
      error: () => {
        // Individual requests are already guarded, but keep the deferred flow isolated from the core UI state.
      },
    });
  }

  selectScreen(surfaceId: string | null): void {
    this.selectedScreenId.set(surfaceId);
    this.loadScreenReadiness(surfaceId);
    if (surfaceId) {
      this.activeTab.set('detail');
    }
  }

  selectBusinessCapability(capabilityId: string | null): void {
    this.selectedBusinessCapabilityId.set(capabilityId);
    this.loadBusinessArchitecture(capabilityId);
  }

  selectApplication(applicationId: string | null): void {
    this.selectedApplicationId.set(applicationId);
    this.loadApplicationArchitecture(applicationId);
  }

  selectDataObject(objectId: string | null): void {
    this.selectedDataObjectId.set(objectId);
    this.loadDataArchitecture(objectId);
  }

  selectInfrastructureDeployment(deploymentId: string | null): void {
    this.selectedDeploymentId.set(deploymentId);
    this.loadInfrastructureArchitecture(deploymentId);
  }

  selectArchitectureView(view: ArchitectureView): void {
    this.selectedArchitectureView.set(view);
    if (view === 'business') {
      const capabilityId = this.selectedBusinessCapabilityId() ?? this.businessCapabilities()[0]?.capabilityId ?? null;
      this.selectedBusinessCapabilityId.set(capabilityId);
      this.loadBusinessArchitecture(capabilityId);
      return;
    }

    if (view === 'application') {
      const applicationId = this.selectedApplicationId() ?? this.applicationSummaries()[0]?.applicationId ?? null;
      this.selectedApplicationId.set(applicationId);
      this.loadApplicationArchitecture(applicationId);
      return;
    }

    const objectId = this.selectedDataObjectId() ?? this.dataObjectSummaries()[0]?.objectId ?? null;
    if (view === 'data') {
      this.selectedDataObjectId.set(objectId);
      this.loadDataArchitecture(objectId);
      return;
    }

    const deploymentId = this.selectedDeploymentId() ?? this.infrastructureDeployments()[0]?.deploymentId ?? null;
    this.selectedDeploymentId.set(deploymentId);
    this.loadInfrastructureArchitecture(deploymentId);
  }

  selectChannel(channelCode: string | null): void {
    this.selectedChannelCode.set(channelCode);
    this.loadChannelTraversal(channelCode);
  }

  selectDeliveryStory(storyId: string | null): void {
    this.selectedDeliveryStoryId.set(storyId);
    this.loadTraceabilityStory(storyId);
    this.loadStoryReadiness(storyId);
    this.loadAgentPack(storyId);
    const externalArtifactId = this.resolveExternalArtifactId(storyId, this.deliveryStories(), this.selectedExternalArtifactId());
    this.selectExternalArtifact(externalArtifactId);
  }

  selectExternalArtifact(externalId: string | null): void {
    this.selectedExternalArtifactId.set(externalId);
    this.loadExternalArtifact(externalId);
  }

  selectPersona(personaId: string | null): void {
    this.selectedPersonaId.set(personaId);
    this.loadPersonaTraversal(personaId);

    const journeyId = this.resolveJourneyIdForPersona(personaId, this.journeys(), this.selectedJourneyId());
    this.selectedJourneyId.set(journeyId);
    this.loadJourneyTraversal(journeyId);
  }

  selectJourney(journeyId: string | null): void {
    this.selectedJourneyId.set(journeyId);
    this.loadJourneyTraversal(journeyId);
  }

  focusStory(storyId: string): void {
    this.selectDeliveryStory(storyId);
    this.activeTab.set('delivery');
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

  selectObjectDefinition(type: string | null): void {
    this.selectedObjectDefinitionType.set(type);
    this.loadObjectDefinitionDetail(type);
  }

  setDefinitionSearchTerm(value: string): void {
    this.definitionSearchTerm.set(value);
  }

  setSelectedBenchmarkNodeType(nodeType: string): void {
    this.selectedBenchmarkNodeType.set(nodeType);
  }

  setSelectedExternalSyncHistorySource(sourceSystem: string): void {
    this.selectedExternalSyncHistorySource.set(sourceSystem);
    this.refreshExternalSyncOperations();
  }

  refreshExternalSyncSourceStatuses(): void {
    this.refreshExternalSyncOperations();
  }

  refreshExternalSyncOperations(): void {
    forkJoin({
      statuses: this.api.getExternalSyncSourceStatuses().pipe(catchError(() => of([] as ExternalSyncSourceStatus[]))),
      jobs: this.api.getExternalSyncJobs(12, this.externalSyncHistorySourceFilter()).pipe(catchError(() => of([] as ExternalSyncJobResult[]))),
    }).subscribe(({ statuses, jobs }) => {
      this.externalSyncSourceStatuses.set(statuses);
      this.externalSyncJobs.set(jobs);
    });
  }

  private externalSyncHistorySourceFilter(): string | undefined {
    const selected = this.selectedExternalSyncHistorySource();
    return selected === 'ALL' ? undefined : selected;
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

  private loadTraceabilityStory(storyId: string | null): void {
    if (!storyId) {
      this.storyTraceability.set(null);
      return;
    }

    this.api.getStoryTraceability(storyId)
      .pipe(catchError(() => of(null)))
      .subscribe((traceability) => {
        this.storyTraceability.set(traceability);
      });
  }

  private loadAgentPack(storyId: string | null): void {
    if (!storyId) {
      this.selectedAgentPack.set(null);
      return;
    }

    this.api.getAgentPack(storyId)
      .pipe(catchError(() => of(null)))
      .subscribe((pack) => {
        this.selectedAgentPack.set(pack);
      });
  }

  private loadScreenReadiness(surfaceId: string | null): void {
    if (!surfaceId) {
      this.selectedScreenReadiness.set(null);
      return;
    }

    this.api.getScreenReadiness(surfaceId)
      .pipe(catchError(() => of(null)))
      .subscribe((readiness) => {
        this.selectedScreenReadiness.set(readiness);
      });
  }

  private loadStoryReadiness(storyId: string | null): void {
    if (!storyId) {
      this.selectedStoryReadiness.set(null);
      return;
    }

    this.api.getStoryReadiness(storyId)
      .pipe(catchError(() => of(null)))
      .subscribe((readiness) => {
        this.selectedStoryReadiness.set(readiness);
      });
  }

  private loadExternalArtifact(externalId: string | null): void {
    if (!externalId) {
      this.selectedExternalArtifact.set(null);
      return;
    }

    this.api.getExternalArtifact(externalId)
      .pipe(catchError(() => of(null)))
      .subscribe((artifact) => {
        this.selectedExternalArtifact.set(artifact);
      });
  }

  private loadJourneyTraversal(journeyId: string | null): void {
    if (!journeyId) {
      this.selectedJourneyTraversal.set(null);
      return;
    }

    this.api.getJourneyTraversal(journeyId)
      .pipe(catchError(() => of(null)))
      .subscribe((journeyTraversal) => {
        this.selectedJourneyTraversal.set(journeyTraversal);
        const personaId = journeyTraversal?.persona?.id ?? this.resolvePersonaIdForJourney(journeyId, this.journeys());
        this.selectedPersonaId.set(personaId);

        if (!personaId) {
          this.selectedPersonaTraversal.set(null);
          return;
        }

        if (this.selectedPersonaTraversal()?.personaId === personaId) {
          return;
        }

        this.loadPersonaTraversal(personaId);
      });
  }

  private loadBusinessArchitecture(capabilityId: string | null): void {
    if (!capabilityId) {
      this.selectedBusinessArchitecture.set(null);
      return;
    }

    this.api.getBusinessArchitecture(capabilityId)
      .pipe(catchError(() => of(null)))
      .subscribe((architecture) => {
        this.selectedBusinessArchitecture.set(architecture);
      });
  }

  private loadApplicationArchitecture(applicationId: string | null): void {
    if (!applicationId) {
      this.selectedApplicationArchitecture.set(null);
      return;
    }

    this.api.getApplicationArchitecture(applicationId)
      .pipe(catchError(() => of(null)))
      .subscribe((architecture) => {
        this.selectedApplicationArchitecture.set(architecture);
      });
  }

  private loadDataArchitecture(objectId: string | null): void {
    if (!objectId) {
      this.selectedDataArchitecture.set(null);
      return;
    }

    this.api.getDataArchitecture(objectId)
      .pipe(catchError(() => of(null)))
      .subscribe((architecture) => {
        this.selectedDataArchitecture.set(architecture);
      });
  }

  private loadInfrastructureArchitecture(deploymentId: string | null): void {
    if (!deploymentId) {
      this.selectedInfrastructureArchitecture.set(null);
      return;
    }

    this.api.getInfrastructureArchitecture(deploymentId)
      .pipe(catchError(() => of(null)))
      .subscribe((architecture) => {
        this.selectedInfrastructureArchitecture.set(architecture);
      });
  }

  private loadObjectDefinitionDetail(type: string | null): void {
    if (!type) {
      this.selectedObjectDefinition.set(null);
      return;
    }

    this.api.getObjectDefinition(type)
      .pipe(catchError(() => of(null)))
      .subscribe((definition) => {
        this.selectedObjectDefinition.set(definition);
      });
  }

  private loadChannelTraversal(channelCode: string | null): void {
    if (!channelCode) {
      this.selectedChannelTraversal.set(null);
      return;
    }

    this.api.getChannelTraversal(channelCode)
      .pipe(catchError(() => of(null)))
      .subscribe((channelTraversal) => {
        this.selectedChannelTraversal.set(channelTraversal);
      });
  }

  private loadPersonaTraversal(personaId: string | null): void {
    if (!personaId) {
      this.selectedPersonaTraversal.set(null);
      return;
    }

    this.api.getPersonaTraversal(personaId)
      .pipe(catchError(() => of(null)))
      .subscribe((personaTraversal) => {
        this.selectedPersonaTraversal.set(personaTraversal);
      });
  }

  private resolveInitialPersonaId(personas: PersonaSummary[], journeys: Journey[]): string | null {
    const currentJourneyPersonaId = this.resolvePersonaIdForJourney(this.selectedJourneyId(), journeys);
    return this.selectedPersonaId() ?? currentJourneyPersonaId ?? personas[0]?.personaId ?? journeys.find((journey) => journey.personaId)?.personaId ?? null;
  }

  private resolveJourneyIdForPersona(
    personaId: string | null,
    journeys: Journey[],
    preferredJourneyId: string | null
  ): string | null {
    if (preferredJourneyId) {
      const preferredJourney = journeys.find((journey) => journey.journeyId === preferredJourneyId);
      if (preferredJourney && (!personaId || preferredJourney.personaId === personaId)) {
        return preferredJourneyId;
      }
    }

    if (!personaId) {
      return preferredJourneyId ?? journeys[0]?.journeyId ?? null;
    }

    return journeys.find((journey) => journey.personaId === personaId)?.journeyId ?? null;
  }

  private resolveExternalArtifactId(
    storyId: string | null,
    stories: DeliveryStory[],
    preferredExternalArtifactId: string | null
  ): string | null {
    const selectedStory = storyId ? stories.find((story) => story.storyId === storyId) ?? null : null;
    if (!selectedStory || selectedStory.externalArtifacts.length === 0) {
      return null;
    }

    if (preferredExternalArtifactId && selectedStory.externalArtifacts.some((artifact) => artifact.externalId === preferredExternalArtifactId)) {
      return preferredExternalArtifactId;
    }

    return selectedStory.externalArtifacts[0]?.externalId ?? null;
  }

  private resolvePersonaIdForJourney(journeyId: string | null, journeys: Journey[]): string | null {
    if (!journeyId) {
      return null;
    }

    return journeys.find((journey) => journey.journeyId === journeyId)?.personaId ?? null;
  }
}
