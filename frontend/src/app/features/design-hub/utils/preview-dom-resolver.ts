import { SystemShellGraphNode } from '../models/graph.types';

export class PreviewDomResolver {
  constructor(
    private readonly root: ParentNode | null,
    private readonly nodeIdMap: Map<string, SystemShellGraphNode>,
    private readonly nodeGuidMap: Map<string, SystemShellGraphNode>,
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

  private findBoundElement(target: EventTarget | null): HTMLElement | null {
    return target instanceof Element
      ? target.closest<HTMLElement>('[source-object-id], [guid]')
      : target instanceof Node
        ? target.parentElement?.closest<HTMLElement>('[source-object-id], [guid]') ?? null
        : null;
  }
}
