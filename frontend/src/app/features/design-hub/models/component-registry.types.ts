export interface ComponentRegistryDefinition {
  objectType: string;
  assetType: string;
  assetName: string;
  description: string | null;
  id: string | null;
  status: string | null;
  packageName: string | null;
  packageExport: string | null;
  packageVersion: string | null;
  iconPackage: string | null;
  themePackage: string | null;
  defaultInstanceId: string | null;
}

export interface ComponentRegistryInstance {
  objectType: string;
  assetType: string;
  assetName: string;
  name: string | null;
  description: string | null;
  id: string | null;
  status: string | null;
  definitionId: string | null;
  packageName: string | null;
  packageExport: string | null;
  packageVersion: string | null;
  iconPackage: string | null;
  themePackage: string | null;
  targetObjectId: string | null;
  targetObjectName: string | null;
  targetObjectType: string | null;
  configuration: Record<string, unknown>;
}

export interface ComponentRegistryInstanceUpdateRequest {
  name: string | null;
  description: string | null;
  status: string | null;
  targetObjectId: string | null;
  configuration: Record<string, unknown>;
}
