import { SystemShellGraphNode } from '../models/graph.types';
import { DesignHubStateService } from '../services/design-hub-state.service';

export function orderedStructuralChildren(
  state: DesignHubStateService,
  objectId: string | null | undefined,
): SystemShellGraphNode[] {
  return state.orderedChildrenOf(objectId, ['HAS_SHELL', 'HAS_SCREEN', 'HAS_SECTION', 'HAS_COMPONENT']);
}

export function firstChildByFamily(
  state: DesignHubStateService,
  objectId: string | null | undefined,
  family: string,
): SystemShellGraphNode | null {
  return orderedStructuralChildren(state, objectId).find((node) => node.family === family) ?? null;
}

export function childAt(
  state: DesignHubStateService,
  objectId: string | null | undefined,
  index: number,
  family?: string,
): SystemShellGraphNode | null {
  const children = family
    ? orderedStructuralChildren(state, objectId).filter((node) => node.family === family)
    : orderedStructuralChildren(state, objectId);
  return children[index] ?? null;
}

export function childByName(
  state: DesignHubStateService,
  objectId: string | null | undefined,
  name: string,
  family?: string,
): SystemShellGraphNode | null {
  return orderedStructuralChildren(state, objectId)
    .find((node) => node.name === name && (!family || node.family === family)) ?? null;
}

export function componentByName(
  state: DesignHubStateService,
  objectId: string | null | undefined,
  name: string,
): SystemShellGraphNode | null {
  return childByName(state, objectId, name, 'Component');
}

export function componentAt(
  state: DesignHubStateService,
  objectId: string | null | undefined,
  index: number,
): SystemShellGraphNode | null {
  return childAt(state, objectId, index, 'Component');
}
