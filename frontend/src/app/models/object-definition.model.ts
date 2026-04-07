import { GraphNodeReference } from './graph.model';

export interface ObjectDefinitionSummary {
  type: string;
  label: string;
  displayName: string;
  category: string;
  tier: string;
  benchmarkable: boolean;
  instanceCount: number;
  relationshipTypeCount: number;
}

export interface ObjectDefinitionAttribute {
  name: string;
  type: string;
  required: boolean;
  description: string;
  constraints: string;
}

export interface ObjectDefinitionRelationship {
  name: string;
  direction: string;
  target: string;
  cardinality: string;
  required: boolean;
  severity: string;
  implementation: string;
}

export interface ObjectDefinitionDetail {
  type: string;
  label: string;
  displayName: string;
  category: string;
  tier: string;
  benchmarkable: boolean;
  purpose: string;
  implementationStatus: string;
  aliases: string[];
  attributes: ObjectDefinitionAttribute[];
  relationships: ObjectDefinitionRelationship[];
  instanceCount: number;
  instances: GraphNodeReferenceWithSummary[];
}

export interface GraphNodeReferenceWithSummary extends GraphNodeReference {
  module?: string | null;
  domain?: string | null;
  routePath?: string | null;
  relationCount?: number;
}
