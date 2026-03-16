export type EffectType = 'navigate' | 'redirect' | 'filter' | 'mutation' | 'toast' | 'close-overlay' | 'open-drawer' | 'stream-start' | 'stream-stop';
export type TargetMode = 'static' | 'role-based' | 'resolved';

export interface Effect {
  type: EffectType;
  target: string | null;
  targetMode: TargetMode;
  resolutionRule?: string;
  defaultTarget?: string;
}

export interface InteractionOutcomes {
  success: string | null;
  error: string | null;
  loading: string | null;
}

export interface Interaction {
  interactionId: string;
  surfaceId: string;
  element: string;
  trigger: string;
  permission: string | null;
  personaIds: string[];
  roleKeys: string[];
  effects: Effect[];
  apiCalls: string[];
  outcomes: InteractionOutcomes;
  confirmationCode: string | null;
  journeyStepRefs: string[];
  sourceRefs: string[];
}
