import { inject } from '@angular/core';
import { SystemShellGraphNode } from '../models/graph.types';
import { DesignHubStateService } from '../services/design-hub-state.service';

export abstract class PreviewIdentityBound {
  protected readonly state = inject(DesignHubStateService);

  protected abstract slotNode(slot: string | null | undefined): SystemShellGraphNode | null;
  protected abstract selectedPreviewGuid(): string | null;

  sourceObjectId(slot: string | null | undefined): string | null {
    return this.slotNode(slot)?.id?.trim() ?? null;
  }

  guid(slot: string | null | undefined): string | null {
    return this.slotNode(slot)?.guid?.trim() ?? null;
  }

  isFocused(slot: string): boolean {
    return this.selectedPreviewGuid() === this.guid(slot);
  }
}
