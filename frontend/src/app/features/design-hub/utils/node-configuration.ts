import { SystemShellGraphNode } from '../models/graph.types';

export function parseNodeConfiguration<T>(
  node: SystemShellGraphNode | null | undefined,
): T | null {
  const configurationJson = node?.configurationJson?.trim();
  if (!configurationJson) {
    return null;
  }

  try {
    const parsed = JSON.parse(configurationJson) as unknown;
    return typeof parsed === 'object' && parsed !== null ? (parsed as T) : null;
  } catch {
    return null;
  }
}
