import { Injectable, computed, inject, signal } from '@angular/core';
import { TreeNode } from 'primeng/api';
import { firstValueFrom } from 'rxjs';
import { DesignHubApiService } from './design-hub-api.service';
import {
  SystemShellGraphNode,
  SystemShellGraphRelationship,
  SystemShellGraphResponse,
  SystemShellGraphTreeNode,
  SystemShellTreeNodeData,
} from '../models/graph.types';

const STRUCTURAL_RELATIONSHIP_TYPES = ['HAS_SHELL', 'HAS_SCREEN', 'HAS_SECTION', 'HAS_COMPONENT'] as const;

@Injectable()
export class DesignHubStateService {
  private readonly api = inject(DesignHubApiService);

  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly graph = signal<SystemShellGraphResponse | null>(null);
  readonly tree = signal<TreeNode<SystemShellTreeNodeData>[]>([]);
  readonly selectedTreeNode = signal<TreeNode<SystemShellTreeNodeData> | null>(null);
  readonly selectedObjectId = signal<string | null>(null);
  readonly selectedGraphNode = computed<SystemShellGraphNode | null>(() => {
    const objectId = this.selectedObjectId();
    if (!objectId) {
      return null;
    }

    return this.nodeIdMap().get(objectId) ?? null;
  });

  readonly nodeIdMap = computed(() => {
    const graph = this.graph();
    return new Map(
      (graph?.nodes ?? [])
        .filter((node) => !!node.id)
        .map((node) => [node.id as string, node]),
    );
  });

  readonly nodeGuidMap = computed(() => {
    const graph = this.graph();
    return new Map(
      (graph?.nodes ?? [])
        .filter((node) => !!node.guid)
        .map((node) => [node.guid as string, node]),
    );
  });

  readonly outgoingRelationshipMap = computed(() => {
    const graph = this.graph();
    const outgoing = new Map<string, SystemShellGraphRelationship[]>();

    for (const relationship of graph?.relationships ?? []) {
      const list = outgoing.get(relationship.fromId) ?? [];
      list.push(relationship);
      outgoing.set(relationship.fromId, list);
    }

    return outgoing;
  });

  readonly incomingRelationshipMap = computed(() => {
    const graph = this.graph();
    const incoming = new Map<string, SystemShellGraphRelationship[]>();

    for (const relationship of graph?.relationships ?? []) {
      const list = incoming.get(relationship.toId) ?? [];
      list.push(relationship);
      incoming.set(relationship.toId, list);
    }

    return incoming;
  });

  async load(preferredObjectId?: string | null): Promise<void> {
    this.loading.set(true);
    this.error.set(null);

    try {
      const graph = await firstValueFrom(this.api.getGraph());
      const tree = this.toPrimeTree(graph.navigationTree ?? []);
      this.graph.set(graph);
      const initialNode = (preferredObjectId ? this.findTreeNodeByObjectId(tree, preferredObjectId) : null)
        ?? this.findTreeNodeByFamily(tree, 'Application')
        ?? tree[0]
        ?? null;
      const expandedTree = initialNode?.data?.objectId
        ? this.expandPathToObjectId(tree, initialNode.data.objectId)
        : tree;
      const selectedNode = initialNode?.data?.objectId
        ? this.findTreeNodeByObjectId(expandedTree, initialNode.data.objectId)
        : initialNode;
      this.tree.set(expandedTree);
      this.selectedTreeNode.set(selectedNode);
      this.selectedObjectId.set(selectedNode?.data?.objectId ?? null);
    } catch (error) {
      this.error.set(error instanceof Error ? error.message : 'Unable to load the system screen graph.');
    } finally {
      this.loading.set(false);
    }
  }

  selectNode(node: TreeNode<SystemShellTreeNodeData> | null): void {
    this.selectedTreeNode.set(node);
    this.selectedObjectId.set(node?.data?.kind === 'graph' ? node.data.objectId : null);
  }

  expandAll(): void {
    this.tree.set(this.toggleExpanded(this.tree(), true));
  }

  collapseAll(): void {
    this.tree.set(this.toggleExpanded(this.tree(), false));
  }

  private toPrimeTree(nodes: SystemShellGraphTreeNode[]): TreeNode<SystemShellTreeNodeData>[] {
    return nodes.map((node) => ({
      key: node.key,
      label: node.label,
      expanded: !!node.expanded,
      selectable: node.selectable,
      data: {
        kind: node.data.kind,
        label: node.data.label,
        family: node.data.family,
        layer: node.data.layer,
        objectId: node.data.objectId ?? null,
        guid: node.data.guid ?? null,
        domTargetGuid: node.data.domTargetGuid ?? null,
        assetType: node.data.assetType ?? null,
      },
      children: this.toPrimeTree(node.children ?? []),
    }));
  }

  selectObjectId(objectId: string | null): void {
    if (!objectId) {
      this.selectedTreeNode.set(null);
      this.selectedObjectId.set(null);
      return;
    }

    const expandedTree = this.expandPathToObjectId(this.tree(), objectId);
    const treeNode = this.findTreeNodeByObjectId(expandedTree, objectId);
    this.tree.set(expandedTree);
    this.selectedTreeNode.set(treeNode);
    this.selectedObjectId.set(treeNode?.data?.objectId ?? objectId);
  }

  selectGuid(guid: string | null): void {
    const node = guid ? this.nodeGuidMap().get(guid) ?? null : null;
    this.selectObjectId(node?.id ?? null);
  }

  nodeById(objectId: string | null | undefined): SystemShellGraphNode | null {
    const normalizedObjectId = objectId?.trim();
    return normalizedObjectId ? this.nodeIdMap().get(normalizedObjectId) ?? null : null;
  }

  guidForObjectId(objectId: string | null | undefined): string | null {
    return this.nodeById(objectId)?.guid?.trim() ?? null;
  }

  private expandPathToObjectId(
    nodes: TreeNode<SystemShellTreeNodeData>[],
    objectId: string,
  ): TreeNode<SystemShellTreeNodeData>[] {
    return nodes.map((node) => {
      const childNodes = node.children ? this.expandPathToObjectId(node.children, objectId) : undefined;
      const matchesSelf = node.data?.objectId === objectId;
      const hasMatchingDescendant = !!childNodes?.some((child) => this.nodeContainsObjectId(child, objectId));

      return {
        ...node,
        expanded: matchesSelf || hasMatchingDescendant ? true : node.expanded,
        children: childNodes,
      };
    });
  }

  private nodeContainsObjectId(node: TreeNode<SystemShellTreeNodeData>, objectId: string): boolean {
    if (node.data?.objectId === objectId) {
      return true;
    }

    return !!node.children?.some((child) => this.nodeContainsObjectId(child, objectId));
  }

  private compareGraphNodes(left: SystemShellGraphNode | undefined, right: SystemShellGraphNode | undefined): number {
    const familyPriority: Record<string, number> = {
      Application: 5,
      Shell: 10,
      Screen: 20,
      Container: 30,
      Section: 35,
      Component: 40,
    };

    const leftRank = left ? familyPriority[left.family] ?? 999 : 999;
    const rightRank = right ? familyPriority[right.family] ?? 999 : 999;
    if (leftRank !== rightRank) {
      return leftRank - rightRank;
    }

    const shellContainerOrder: Record<string, number> = {
      'Header Container': 10,
      'Breadcrumb Container': 20,
      'Main Container': 30,
      'Footer Container': 40,
    };
    const leftShellOrder = left ? shellContainerOrder[left.name ?? ''] ?? 999 : 999;
    const rightShellOrder = right ? shellContainerOrder[right.name ?? ''] ?? 999 : 999;
    if (leftShellOrder !== rightShellOrder) {
      return leftShellOrder - rightShellOrder;
    }

    const leftSortOrder = left?.sortOrder ?? Number.MAX_SAFE_INTEGER;
    const rightSortOrder = right?.sortOrder ?? Number.MAX_SAFE_INTEGER;
    if (leftSortOrder !== rightSortOrder) {
      return leftSortOrder - rightSortOrder;
    }

    const leftName = left?.name ?? '';
    const rightName = right?.name ?? '';
    if (leftName !== rightName) {
      return leftName.localeCompare(rightName);
    }

    return (left?.id ?? '').localeCompare(right?.id ?? '');
  }

  orderedChildrenOf(
    objectId: string | null | undefined,
    relationshipTypes: readonly string[] = STRUCTURAL_RELATIONSHIP_TYPES,
  ): SystemShellGraphNode[] {
    const normalizedObjectId = objectId?.trim();
    if (!normalizedObjectId) {
      return [];
    }

    const allowedTypes = new Set(relationshipTypes);
    const nodeIdMap = this.nodeIdMap();
    return (this.outgoingRelationshipMap().get(normalizedObjectId) ?? [])
      .filter((relationship) => allowedTypes.has(relationship.relationshipType))
      .map((relationship) => nodeIdMap.get(relationship.toId) ?? null)
      .filter((node): node is SystemShellGraphNode => !!node)
      .sort((left, right) => this.compareGraphNodes(left, right));
  }

  orderedParentsOf(
    objectId: string | null | undefined,
    relationshipTypes: readonly string[] = STRUCTURAL_RELATIONSHIP_TYPES,
  ): SystemShellGraphNode[] {
    const normalizedObjectId = objectId?.trim();
    if (!normalizedObjectId) {
      return [];
    }

    const allowedTypes = new Set(relationshipTypes);
    const nodeIdMap = this.nodeIdMap();
    return (this.incomingRelationshipMap().get(normalizedObjectId) ?? [])
      .filter((relationship) => allowedTypes.has(relationship.relationshipType))
      .map((relationship) => nodeIdMap.get(relationship.fromId) ?? null)
      .filter((node): node is SystemShellGraphNode => !!node)
      .sort((left, right) => this.compareGraphNodes(left, right));
  }

  firstChildOf(
    objectId: string | null | undefined,
    predicate: (node: SystemShellGraphNode) => boolean,
    relationshipTypes: readonly string[] = STRUCTURAL_RELATIONSHIP_TYPES,
  ): SystemShellGraphNode | null {
    return this.orderedChildrenOf(objectId, relationshipTypes).find(predicate) ?? null;
  }

  private toggleExpanded(nodes: TreeNode<SystemShellTreeNodeData>[], expanded: boolean): TreeNode<SystemShellTreeNodeData>[] {
    return nodes.map((node) => ({
      ...node,
      expanded,
      children: node.children ? this.toggleExpanded(node.children, expanded) : undefined,
    }));
  }

  private findTreeNodeByObjectId(nodes: TreeNode<SystemShellTreeNodeData>[], objectId: string): TreeNode<SystemShellTreeNodeData> | null {
    for (const node of nodes) {
      if (node.data?.objectId === objectId) {
        return node;
      }

      if (node.children?.length) {
        const child = this.findTreeNodeByObjectId(node.children, objectId);
        if (child) {
          return child;
        }
      }
    }

    return null;
  }

  private findTreeNodeByFamily(nodes: TreeNode<SystemShellTreeNodeData>[], family: string): TreeNode<SystemShellTreeNodeData> | null {
    for (const node of nodes) {
      if (node.data?.family === family) {
        return node;
      }

      if (node.children?.length) {
        const child = this.findTreeNodeByFamily(node.children, family);
        if (child) {
          return child;
        }
      }
    }

    return null;
  }
}
