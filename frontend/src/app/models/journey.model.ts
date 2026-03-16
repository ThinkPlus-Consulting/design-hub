import { DesignStatus, PrototypeStatus, DeliveryStatus } from './screen.model';

export interface JourneyStep {
  stepId: string;
  interactionRef: string | null;
  label: string;
  preCondition: string;
  postCondition: string;
}

export interface Journey {
  journeyId: string;
  title: string;
  personaId: string | null;
  roleKey: string | null;
  goalStatement: string;
  steps: JourneyStep[];
  sourceRefs: string[];
  designStatus: DesignStatus;
  prototypeStatus: PrototypeStatus;
  deliveryStatus: DeliveryStatus;
}
