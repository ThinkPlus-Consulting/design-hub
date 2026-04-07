import { SystemShellGraphNode } from '../models/system-shell-graph.model';

export class PreviewDomResolver {
  constructor(
    private readonly root: ParentNode | null,
    private readonly nodeMap: Map<string, SystemShellGraphNode>,
    private readonly nodeIdMap: Map<string, SystemShellGraphNode>,
    private readonly nodeGuidMap: Map<string, SystemShellGraphNode>,
    private readonly componentTargetIdMap: ReadonlyMap<string, string>,
  ) {}

  resolveObjectIdFromEvent(target: EventTarget | null): string | null {
    const element = this.findBoundElement(target);
    const sourceObjectId = element?.getAttribute('source-object-id')?.trim() ?? null;
    if (sourceObjectId) {
      return sourceObjectId;
    }

    const guid = element?.getAttribute('guid')?.trim() ?? null;
    return guid ? this.nodeGuidMap.get(guid)?.id ?? null : null;
  }

  resolveGuidFromEvent(target: EventTarget | null): string | null {
    const element = this.findBoundElement(target);
    const guid = element?.getAttribute('guid')?.trim() ?? null;
    if (guid) {
      return guid;
    }

    const sourceObjectId = element?.getAttribute('source-object-id')?.trim() ?? null;
    return sourceObjectId ? this.nodeIdMap.get(sourceObjectId)?.guid ?? null : null;
  }

  findElementForObjectId(objectId: string | null): HTMLElement | null {
    if (!objectId || !this.root) {
      return null;
    }

    return this.findExactElement(objectId);
  }

  findElementForGuid(guid: string | null): HTMLElement | null {
    if (!guid || !this.root) {
      return null;
    }

    return this.root.querySelector<HTMLElement>(`[guid="${guid}"]`) ?? null;
  }

  resolveRenderableObjectId(requestedObjectId: string | null): string | null {
    if (!requestedObjectId || !this.root) {
      return null;
    }

    if (this.findExactElement(requestedObjectId)) {
      return requestedObjectId;
    }

    const node = this.nodeIdMap.get(requestedObjectId);
    const targetObjectId = node?.family === 'Component' ? this.componentTargetIdMap.get(requestedObjectId) ?? null : null;
    if (targetObjectId && this.findExactElement(targetObjectId)) {
      return targetObjectId;
    }

    const descendantObjectId = this.findClosestRenderedDescendantObjectId(requestedObjectId);
    if (descendantObjectId) {
      return descendantObjectId;
    }

    return this.findClosestRenderedAncestorObjectId(requestedObjectId);
  }

  private findExactElement(objectId: string): HTMLElement | null {
    const node = this.nodeIdMap.get(objectId);
    const guid = node?.guid?.trim();
    if (guid) {
      return this.findElementForGuid(guid);
    }

    const sourceObjectId = node?.id?.trim();
    return sourceObjectId
      ? this.root?.querySelector<HTMLElement>(`[source-object-id="${sourceObjectId}"]`) ?? null
      : null;
  }

  private findClosestRenderedDescendantObjectId(requestedObjectId: string): string | null {
    const descendants = this.renderedObjectIds()
      .filter((objectId) => {
        const node = this.nodeIdMap.get(objectId);
        const requestedNode = this.nodeIdMap.get(requestedObjectId);
        return !!node?.code && !!requestedNode?.code && node.code.startsWith(`${requestedNode.code}.`);
      })
      .sort((left, right) => {
        const leftCode = this.nodeIdMap.get(left)?.code ?? '';
        const rightCode = this.nodeIdMap.get(right)?.code ?? '';
        return this.codeDepth(leftCode) - this.codeDepth(rightCode) || leftCode.localeCompare(rightCode);
      });

    return descendants[0] ?? null;
  }

  private findClosestRenderedAncestorObjectId(requestedObjectId: string): string | null {
    let cursor = this.nodeIdMap.get(requestedObjectId)?.code ?? null;
    if (!cursor) {
      return null;
    }

    while (cursor.includes('.')) {
      cursor = cursor.slice(0, cursor.lastIndexOf('.'));
      const ancestor = this.nodeMap.get(cursor);
      if (ancestor?.id && this.findExactElement(ancestor.id)) {
        return ancestor.id;
      }
    }

    return null;
  }

  private renderedObjectIds(): string[] {
    if (!this.root) {
      return [];
    }

    return Array.from(this.root.querySelectorAll<HTMLElement>('[source-object-id]'))
      .map((element) => element.getAttribute('source-object-id')?.trim() ?? '')
      .filter((objectId): objectId is string => !!objectId);
  }

  private codeDepth(code: string): number {
    return code.split('.').length;
  }

  private findBoundElement(target: EventTarget | null): HTMLElement | null {
    return target instanceof Element
      ? target.closest<HTMLElement>('[source-object-id], [guid]')
      : target instanceof Node
        ? target.parentElement?.closest<HTMLElement>('[source-object-id], [guid]') ?? null
        : null;
  }
}
