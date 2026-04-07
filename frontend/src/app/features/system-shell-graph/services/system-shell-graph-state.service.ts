import { Injectable, computed, inject, signal } from '@angular/core';
import { TreeNode } from 'primeng/api';
import { firstValueFrom } from 'rxjs';
import { SystemShellGraphApiService } from './system-shell-graph-api.service';
import {
  SystemShellGraphNode,
  SystemShellGraphRelationship,
  SystemShellGraphResponse,
  SystemShellTreeNodeData,
} from '../models/system-shell-graph.model';

@Injectable()
export class SystemShellGraphStateService {
  private readonly api = inject(SystemShellGraphApiService);

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

  readonly nodeMap = computed(() => {
    const graph = this.graph();
    return new Map(graph?.nodes.map((node) => [node.code, node]) ?? []);
  });

  readonly nodeIdMap = computed(() => {
    const graph = this.graph();
    return new Map(
      (graph?.nodes ?? [])
        .filter((node) => !!node.id)
        .map((node) => [node.id as string, node]),
    );
  });

  readonly nodeHierarchyMap = computed(() => {
    const graph = this.graph();
    return new Map(
      (graph?.nodes ?? [])
        .filter((node) => !!node.hierarchyCode)
        .map((node) => [node.hierarchyCode as string, node]),
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

  async load(preferredObjectId?: string | null): Promise<void> {
    this.loading.set(true);
    this.error.set(null);

    try {
      const graph = await firstValueFrom(this.api.getGraph());
      const tree = this.buildTree(graph);
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

  private buildTree(graph: SystemShellGraphResponse): TreeNode<SystemShellTreeNodeData>[] {
    const nodeMap = new Map(
      graph.nodes
        .filter((node) => !!node.id)
        .map((node) => [node.id as string, node]),
    );
    const nodeById = new Map(
      graph.nodes
        .filter((node) => !!node.id)
        .map((node) => [node.id as string, node]),
    );
    const outgoing = this.buildOutgoingMap(graph.relationships);
    const applications = Array.from(nodeById.values())
      .filter((node) => node.layer === 'instance' && node.family === 'Application')
      .sort((left, right) => this.compareGraphNodes(left, right));

    return applications.map((application) => this.buildSubtree(application.id as string, nodeMap, outgoing, [], null));
  }

  private buildSubtree(
    objectId: string,
    nodeMap: Map<string, SystemShellGraphNode>,
    outgoing: Map<string, SystemShellGraphRelationship[]>,
    path: string[],
    parentNode: SystemShellGraphNode | null,
  ): TreeNode<SystemShellTreeNodeData> {
    const node = nodeMap.get(objectId);
    if (!node) {
      throw new Error(`Missing graph node ${objectId}`);
    }

    const children = (outgoing.get(objectId) ?? [])
      .filter((edge) => !path.includes(edge.toId))
      .sort((left, right) => this.compareGraphNodes(nodeMap.get(left.toId), nodeMap.get(right.toId)))
      .map((edge) => this.buildSubtree(edge.toId, nodeMap, outgoing, [...path, objectId], node));

    return {
      key: node.id ?? objectId,
      label: node.name,
      expanded: false,
      selectable: true,
      data: {
        kind: 'graph',
        code: node.code,
        label: node.name,
        family: node.family,
        layer: node.layer,
        objectId: node.id ?? null,
        guid: node.guid ?? null,
        domTargetGuid: node.family === 'Application'
          ? null
          : node.family === 'Component'
          ? parentNode?.guid ?? null
          : node.guid ?? null,
        assetType: node.assetType,
      },
      children,
    };
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

  private buildOutgoingMap(relationships: SystemShellGraphRelationship[]) {
    const outgoing = new Map<string, SystemShellGraphRelationship[]>();

    for (const relationship of relationships) {
      if (!['HAS_SHELL', 'HAS_SCREEN', 'HAS_SECTION', 'HAS_ELEMENT', 'HAS_COMPONENT'].includes(relationship.relationshipType)) {
        continue;
      }

      const list = outgoing.get(relationship.fromId) ?? [];
      list.push(relationship);
      outgoing.set(relationship.fromId, list);
    }

    return outgoing;
  }

  private compareGraphNodes(left: SystemShellGraphNode | undefined, right: SystemShellGraphNode | undefined): number {
    const familyPriority: Record<string, number> = {
      Application: 5,
      Shell: 10,
      Screen: 20,
      Section: 30,
      Element: 40,
      Component: 50,
    };

    const leftRank = left ? familyPriority[left.family] ?? 999 : 999;
    const rightRank = right ? familyPriority[right.family] ?? 999 : 999;
    if (leftRank !== rightRank) {
      return leftRank - rightRank;
    }

    const leftHierarchy = left?.hierarchyCode ?? '';
    const rightHierarchy = right?.hierarchyCode ?? '';
    if (leftHierarchy !== rightHierarchy) {
      return leftHierarchy.localeCompare(rightHierarchy);
    }

    const leftName = left?.name ?? '';
    const rightName = right?.name ?? '';
    if (leftName !== rightName) {
      return leftName.localeCompare(rightName);
    }

    return (left?.id ?? '').localeCompare(right?.id ?? '');
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
