export interface EntryMode {
  channelId: string;
  mechanism: string;
}

export interface Touchpoint {
  touchpointId: string;
  label: string;
  surfaceId: string;
  personaIds: string[];
  roleKeys: string[];
  entryModes: EntryMode[];
  journeyStepRefs: string[];
  sourceRefs: string[];
}
