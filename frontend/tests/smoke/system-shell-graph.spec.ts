import { expect, test, type Locator, type Page } from '@playwright/test';
import { backendBaseUrl } from '../helpers/backend';

test('system shell graph workspace renders live seeded data', async ({ page, request }) => {
  const graphResponse = await request.get(`${backendBaseUrl}/api/v1/system-shell-graph/graph`);
  expect(graphResponse.ok()).toBeTruthy();

  const graph = (await graphResponse.json()) as {
    graphScope?: string;
    nodes?: Array<unknown>;
    relationships?: Array<unknown>;
  };
  expect(graph.graphScope).toBe('SYSTEM_FRONTEND_GRAPH');
  expect(graph.nodes?.length ?? 0).toBeGreaterThan(0);
  expect(graph.relationships?.length ?? 0).toBeGreaterThan(0);

  await page.goto('/');

  await expect(page.getByRole('heading', { name: 'Frontend' })).toBeVisible();
  await expect(page.getByRole('button', { name: 'X-Ray Agent' })).toBeVisible();
  await expect(page.getByRole('button', { name: 'Tree expansion state' })).toBeVisible();
  await expect(page.getByRole('heading', { name: 'ObjectsLogic' })).toBeVisible();
});

test('tenant list grid view section exposes tenant card instances in the graph', async ({ request }) => {
  const graphResponse = await request.get(`${backendBaseUrl}/api/v1/system-shell-graph/graph`);
  expect(graphResponse.ok()).toBeTruthy();

  const graph = (await graphResponse.json()) as {
    nodes?: Array<{ id?: string; family?: string; name?: string }>;
    relationships?: Array<{ fromId?: string; toId?: string; relationshipType?: string }>;
  };

  const gridSection = (graph.nodes ?? []).find(
    (node) => node.family === 'Section' && node.name === 'Grid View Section',
  );
  expect(gridSection?.id).toBeTruthy();

  const gridChildIds = new Set(
    (graph.relationships ?? [])
      .filter((relationship) =>
        relationship.fromId === gridSection?.id && relationship.relationshipType === 'HAS_COMPONENT')
      .map((relationship) => relationship.toId)
      .filter((id): id is string => Boolean(id)),
  );

  const gridChildNames = new Set(
    (graph.nodes ?? [])
      .filter((node) => node.id && gridChildIds.has(node.id))
      .map((node) => node.name)
      .filter((name): name is string => Boolean(name)),
  );

  expect(gridChildNames).toEqual(
    new Set([
      'Tenant Card - Acme Corp',
      'Tenant Avatar',
      'Tenant Name',
      'Tenant Short Name',
      'Tenant Status Badge',
      'Tenant Type Badge',
      'Tenant Health Badge',
      'Tenant Stats Summary',
      'Tenant Card - Northwind Trading',
      'Tenant Card - Meridian Health',
      'Tenant Card - Pinnacle Labs',
      'Tenant Card - Vanguard Group',
      'Tenant Card - Cascade Solutions',
      'Tenant Card - Blue Horizon',
      'Tenant Card - Summit Financial',
      'Tenant Card - RedBridge Consulting',
      'Tenant Card - GreenField Energy',
      'Tenant Card - Atlas Logistics',
      'Tenant Card - Nova Healthcare',
      'Tenant Card - Stellar Dynamics',
      'Tenant Card - Ironclad Security',
      'Tenant Card - Pacific Ventures',
    ]),
  );
});

test('tenant list preview screen exposes graph identity for the mounted branch', async ({ page }) => {
  await page.goto('/');
  await disableDevServerOverlay(page);

  await expandActiveTree(page);

  const tenantListScreenObjectId = await objectIdForNode(page, 'Screen', 'View Tenant List');
  expect(tenantListScreenObjectId).toBeTruthy();

  await selectGraphObjectId(page, tenantListScreenObjectId!);
  await page.getByRole('tab', { name: 'Preview' }).click();
  await expect.poll(() => displayedScreenObjectId(page)).toBe(tenantListScreenObjectId);

  const tenantList = page.locator('[data-testid="tenant-list"]');
  await expect(tenantList).toBeVisible();
  await expect(tenantList).toHaveAttribute('source-object-id', /.+/);
  await expect(tenantList).toHaveAttribute('guid', /.+/);

  for (const testId of ['filter-bar', 'input-search-tenants', 'view-toggle', 'btn-create-tenant']) {
    const control = page.locator(`[data-testid="${testId}"]`);
    await expect(control).toBeVisible();
    await expect(control).toHaveAttribute('source-object-id', /.+/);
    await expect(control).toHaveAttribute('guid', /.+/);
  }

  const tenantGrid = page.locator('[data-testid="tenant-grid"]');
  const tenantTable = page.locator('[data-testid="tenant-table"]');
  const emptyState = page.locator('[data-testid="empty-state"]');

  if (await tenantGrid.isVisible().catch(() => false)) {
    const visibleTenantCards = [
      'acme-corp',
      'northwind-trading',
      'meridian-health',
      'pinnacle-labs',
      'vanguard-group',
      'cascade-solutions',
      'blue-horizon',
      'summit-financial',
      'redbridge-consulting',
      'greenfield-energy',
    ];

    for (const shortName of visibleTenantCards) {
      const card = page.locator(`[data-testid="tenant-card-${shortName}"]`);
      await expect(card).toBeVisible();
      await expect(card).toHaveAttribute('source-object-id', /.+/);
      await expect(card).toHaveAttribute('guid', /.+/);
    }
    return;
  }

  if (await tenantTable.isVisible().catch(() => false)) {
    await expect(tenantTable).toHaveAttribute('source-object-id', /.+/);
    await expect(tenantTable).toHaveAttribute('guid', /.+/);
    return;
  }

  await expect(emptyState).toBeVisible();
  await expect(emptyState).toHaveAttribute('source-object-id', /.+/);
  await expect(emptyState).toHaveAttribute('guid', /.+/);
});

test('design hub shell and pane geometry stays fixed while workspace content scrolls and inspector tabs switch', async ({ page }) => {
  await page.goto('/');
  await disableDevServerOverlay(page);

  const header = page.locator('[data-inspect-id="design-hub-shell-header"]');
  const breadcrumb = page.locator('[data-inspect-id="design-hub-shell-breadcrumb"]');
  const footer = page.locator('[data-inspect-id="design-hub-shell-footer"]');
  const treeScrollRegion = page.locator('.ssg-side-menu-tree-section');
  const treeExpansionToggle = page.getByRole('button', { name: 'Tree expansion state' });

  await expect(header).toBeVisible();
  await expect(breadcrumb).toBeVisible();
  await expect(footer).toBeVisible();

  await expect.poll(() => page.evaluate(() => window.scrollY)).toBe(0);
  await page.evaluate(() => window.scrollTo(0, 500));
  await expect.poll(() => page.evaluate(() => window.scrollY)).toBe(0);

  if (!(await treeScrollRegion.evaluate((element) => element.scrollHeight > element.clientHeight))) {
    await treeExpansionToggle.click();
  }

  await expect.poll(() =>
    treeScrollRegion.evaluate((element) => element.scrollHeight > element.clientHeight),
  ).toBe(true);

  const before = await shellChromeMetrics(page);

  await treeScrollRegion.evaluate((element) => {
    element.scrollTop = Math.max(240, Math.floor(element.scrollHeight / 3));
  });

  await expect.poll(() =>
    treeScrollRegion.evaluate((element) => element.scrollTop),
  ).toBeGreaterThan(0);

  const afterScroll = await shellChromeMetrics(page);
  expect(afterScroll).toEqual(before);

  await page.getByRole('tab', { name: 'Preview' }).click();
  const afterPreview = await shellChromeMetrics(page);
  expect(afterPreview).toEqual(before);

  await page.getByRole('tab', { name: 'Issues' }).click();
  const afterIssues = await shellChromeMetrics(page);
  expect(afterIssues).toEqual(before);

  await page.getByRole('tab', { name: 'Overview' }).click();
  const afterOverview = await shellChromeMetrics(page);
  expect(afterOverview).toEqual(before);
});

async function previewHover(locator: Locator): Promise<void> {
  await locator.dispatchEvent('mousemove');
}

async function previewClick(locator: Locator): Promise<void> {
  await locator.dispatchEvent('click');
}

async function selectedGraphObjectId(page: Page): Promise<string | null> {
  return page.evaluate(() => {
    const component = window.ng.getComponent(document.querySelector('app-design-hub-page'));
    const workspace = component?.workspace ?? component?.['workspace'];
    return workspace?.state.selectedGraphNode()?.id ?? null;
  });
}

async function previewHoveredObjectId(page: Page): Promise<string | null> {
  return page.evaluate(() => {
    const component = window.ng.getComponent(document.querySelector('app-design-hub-page'));
    const workspace = component?.workspace ?? component?.['workspace'];
    return workspace?.previewHoveredObjectId() ?? null;
  });
}

async function displayedShellObjectId(page: Page): Promise<string | null> {
  return page.evaluate(() => {
    const component = window.ng.getComponent(document.querySelector('app-design-hub-page'));
    const workspace = component?.workspace ?? component?.['workspace'];
    return workspace?.displayedPreviewShellObjectId() ?? null;
  });
}

async function displayedScreenObjectId(page: Page): Promise<string | null> {
  return page.evaluate(() => {
    const component = window.ng.getComponent(document.querySelector('app-design-hub-page'));
    const workspace = component?.workspace ?? component?.['workspace'];
    return workspace?.displayedPreviewScreenObjectId() ?? null;
  });
}

async function selectGraphObjectId(page: Page, objectId: string): Promise<void> {
  await page.evaluate((targetObjectId) => {
    const component = window.ng.getComponent(document.querySelector('app-design-hub-page'));
    const workspace = component?.workspace ?? component?.['workspace'];
    workspace?.state.selectObjectId(targetObjectId);
  }, objectId);
}

async function hoverGraphObjectId(page: Page, objectId: string): Promise<void> {
  await page.evaluate((targetObjectId) => {
    const component = window.ng.getComponent(document.querySelector('app-design-hub-page'));
    const workspace = component?.workspace ?? component?.['workspace'];
    const guid = workspace?.state.nodeIdMap().get(targetObjectId)?.guid?.trim() ?? null;
    workspace?.hoveredPreviewGuid.set(guid);
  }, objectId);
}

async function expandActiveTree(page: Page): Promise<void> {
  const treeExpansionToggle = page.getByRole('button', { name: 'Tree expansion state' });
  await expect(treeExpansionToggle).toBeVisible();
  if ((await treeExpansionToggle.getAttribute('aria-pressed')) !== 'true') {
    await treeExpansionToggle.click();
  }
}

async function disableDevServerOverlay(page: Page): Promise<void> {
  await page.locator('#webpack-dev-server-client-overlay').evaluateAll((elements) => {
    for (const element of elements) {
      const overlay = element as HTMLElement;
      overlay.style.display = 'none';
      overlay.style.pointerEvents = 'none';
    }
  });
}

async function elementHeight(locator: Locator): Promise<number> {
  return locator.evaluate((element) => element.getBoundingClientRect().height);
}

async function computedStyleProperty(locator: Locator, property: string): Promise<string> {
  return locator.evaluate(
    (element, styleProperty) => window.getComputedStyle(element).getPropertyValue(styleProperty).trim(),
    property,
  );
}

async function shellChromeMetrics(page: Page): Promise<{
  headerTop: number;
  headerHeight: number;
  breadcrumbTop: number;
  breadcrumbHeight: number;
  mainTop: number;
  mainHeight: number;
  footerTop: number;
  footerHeight: number;
  footerBottom: number;
  treeHeaderTop: number;
  treeHeaderHeight: number;
  treeSubheaderTop: number;
  treeSubheaderHeight: number;
  treeBodyTop: number;
  treeBodyHeight: number;
  inspectorHeaderTop: number;
  inspectorHeaderHeight: number;
  inspectorSubheaderTop: number;
  inspectorSubheaderHeight: number;
  inspectorBodyTop: number;
  inspectorBodyHeight: number;
  windowScrollY: number;
}> {
  return page.evaluate(() => {
    const header = document.querySelector('[data-inspect-id="design-hub-shell-header"]');
    const breadcrumb = document.querySelector('[data-inspect-id="design-hub-shell-breadcrumb"]');
    const main = document.querySelector('.design-hub-page-shell__main-slot');
    const footer = document.querySelector('[data-inspect-id="design-hub-shell-footer"]');
    const treeHeader = document.querySelector('[data-inspect-id="design-hub-tree-pane"] .ssg-panel-slot--header');
    const treeSubheader = document.querySelector('[data-inspect-id="design-hub-tree-pane"] .ssg-panel-slot--subheader');
    const treeBody = document.querySelector('[data-inspect-id="design-hub-tree-pane"] .ssg-panel-slot--body');
    const inspectorHeader = document.querySelector('[data-inspect-id="design-hub-inspector-pane"] .ssg-panel-slot--header');
    const inspectorSubheader = document.querySelector('[data-inspect-id="design-hub-inspector-pane"] .ssg-panel-slot--subheader');
    const inspectorBody = document.querySelector('[data-inspect-id="design-hub-inspector-pane"] .ssg-panel-slot--body');

    if (
      !(header instanceof HTMLElement) ||
      !(breadcrumb instanceof HTMLElement) ||
      !(main instanceof HTMLElement) ||
      !(footer instanceof HTMLElement) ||
      !(treeHeader instanceof HTMLElement) ||
      !(treeSubheader instanceof HTMLElement) ||
      !(treeBody instanceof HTMLElement) ||
      !(inspectorHeader instanceof HTMLElement) ||
      !(inspectorSubheader instanceof HTMLElement) ||
      !(inspectorBody instanceof HTMLElement)
    ) {
      throw new Error('Design hub shell chrome not found');
    }

    const headerRect = header.getBoundingClientRect();
    const breadcrumbRect = breadcrumb.getBoundingClientRect();
    const mainRect = main.getBoundingClientRect();
    const footerRect = footer.getBoundingClientRect();
    const treeHeaderRect = treeHeader.getBoundingClientRect();
    const treeSubheaderRect = treeSubheader.getBoundingClientRect();
    const treeBodyRect = treeBody.getBoundingClientRect();
    const inspectorHeaderRect = inspectorHeader.getBoundingClientRect();
    const inspectorSubheaderRect = inspectorSubheader.getBoundingClientRect();
    const inspectorBodyRect = inspectorBody.getBoundingClientRect();
    const round = (value: number) => Math.round(value * 100) / 100;
    return {
      headerTop: round(headerRect.top),
      headerHeight: round(headerRect.height),
      breadcrumbTop: round(breadcrumbRect.top),
      breadcrumbHeight: round(breadcrumbRect.height),
      mainTop: round(mainRect.top),
      mainHeight: round(mainRect.height),
      footerTop: round(footerRect.top),
      footerHeight: round(footerRect.height),
      footerBottom: round(footerRect.bottom),
      treeHeaderTop: round(treeHeaderRect.top),
      treeHeaderHeight: round(treeHeaderRect.height),
      treeSubheaderTop: round(treeSubheaderRect.top),
      treeSubheaderHeight: round(treeSubheaderRect.height),
      treeBodyTop: round(treeBodyRect.top),
      treeBodyHeight: round(treeBodyRect.height),
      inspectorHeaderTop: round(inspectorHeaderRect.top),
      inspectorHeaderHeight: round(inspectorHeaderRect.height),
      inspectorSubheaderTop: round(inspectorSubheaderRect.top),
      inspectorSubheaderHeight: round(inspectorSubheaderRect.height),
      inspectorBodyTop: round(inspectorBodyRect.top),
      inspectorBodyHeight: round(inspectorBodyRect.height),
      windowScrollY: round(window.scrollY),
    };
  });
}

async function childObjectId(page: Page, parentObjectId: string, family: string, name: string): Promise<string | null> {
  return page.evaluate(({ parentId, childFamily, childName }) => {
    const component = window.ng.getComponent(document.querySelector('app-design-hub-page'));
    const workspace = component?.workspace ?? component?.['workspace'];
    const relationships = workspace?.state.outgoingRelationshipMap().get(parentId) ?? [];
    const child = relationships
      .map((relationship) => workspace?.state.nodeIdMap().get(relationship.toId) ?? null)
      .find((node) => node?.family === childFamily && node?.name === childName);
    return child?.id ?? null;
  }, { parentId: parentObjectId, childFamily: family, childName: name });
}

async function objectIdForNode(page: Page, family: string, name: string): Promise<string | null> {
  return page.evaluate(({ targetFamily, targetName }) => {
    const component = window.ng.getComponent(document.querySelector('app-design-hub-page'));
    const workspace = component?.workspace ?? component?.['workspace'];
    const node = (workspace?.state.graph()?.nodes ?? []).find(
      (candidate) => candidate.layer === 'instance' && candidate.family === targetFamily && candidate.name === targetName,
    );
    return node?.id ?? null;
  }, { targetFamily: family, targetName: name });
}

test('shell frame nodes stay synchronized between tree and preview canvas', async ({ page }) => {
  await page.goto('/');
  await disableDevServerOverlay(page);

  await expandActiveTree(page);
  await page.getByRole('tab', { name: 'Preview' }).click();

  const previewCanvas = page.locator('.ssg-preview-canvas');
  const shell = previewCanvas.locator('[data-inspect-id="app-shell"]');
  const header = previewCanvas.locator('[data-inspect-id="shell-header-container"]');
  const main = previewCanvas.locator('[data-inspect-id="shell-main-container"]');
  const footer = previewCanvas.locator('[data-inspect-id="shell-footer-container"]');

  await expect(page.locator('[data-inspect-id="app-shell"]')).toHaveCount(1);
  await expect(page.locator('[data-inspect-id="shell-header-container"]')).toHaveCount(1);
  await expect(page.locator('[data-inspect-id="shell-main-container"]')).toHaveCount(1);
  await expect(page.locator('[data-inspect-id="shell-footer-container"]')).toHaveCount(1);
  await expect(shell).toBeVisible();
  await expect(header).toBeVisible();
  await expect(main).toBeVisible();
  await expect(footer).toBeAttached();
  await expect.poll(() => elementHeight(header)).toBeGreaterThan(10);

  const shellObjectId = await displayedShellObjectId(page);
  expect(shellObjectId).toBeTruthy();
  const headerObjectId = await childObjectId(page, shellObjectId!, 'Container', 'Header Container');
  const mainObjectId = await childObjectId(page, shellObjectId!, 'Container', 'Main Container');
  const footerObjectId = await childObjectId(page, shellObjectId!, 'Container', 'Footer Container');
  expect(headerObjectId).toBeTruthy();
  expect(mainObjectId).toBeTruthy();
  expect(footerObjectId).toBeTruthy();

  await selectGraphObjectId(page, shellObjectId!);
  await expect(shell).toHaveClass(/\bssg-focused\b/);
  await expect.poll(() => computedStyleProperty(shell, 'outline-style')).toBe('solid');

  await selectGraphObjectId(page, shellObjectId!);
  await expect(shell).toHaveClass(/\bssg-focused\b/);
  await expect.poll(() => computedStyleProperty(shell, 'outline-style')).toBe('solid');

  await selectGraphObjectId(page, headerObjectId!);
  await expect(header).toHaveClass(/\bssg-focused\b/);
  await expect.poll(() => computedStyleProperty(header, 'outline-style')).toBe('solid');

  await selectGraphObjectId(page, mainObjectId!);
  await expect(main).toHaveClass(/\bssg-focused\b/);
  await expect.poll(() => computedStyleProperty(main, 'outline-style')).toBe('solid');

  await selectGraphObjectId(page, footerObjectId!);
  await expect(footer).toHaveClass(/\bssg-focused\b/);
  await expect.poll(() => computedStyleProperty(footer, 'outline-style')).toBe('solid');

  await selectGraphObjectId(page, mainObjectId!);
  await hoverGraphObjectId(page, headerObjectId!);
  await expect(header).toHaveClass(/\bssg-hovered\b/);

  await selectGraphObjectId(page, headerObjectId!);
  await hoverGraphObjectId(page, mainObjectId!);
  await expect(main).toHaveClass(/\bssg-hovered\b/);

  await selectGraphObjectId(page, mainObjectId!);
  await hoverGraphObjectId(page, footerObjectId!);
  await expect(footer).toHaveClass(/\bssg-hovered\b/);

  await previewHover(header);
  await expect.poll(() => previewHoveredObjectId(page)).toBe(headerObjectId);

  await previewHover(main);
  await expect.poll(() => previewHoveredObjectId(page)).toBe(mainObjectId);

  await footer.scrollIntoViewIfNeeded();
  await previewHover(footer);
  await expect.poll(() => previewHoveredObjectId(page)).toBe(footerObjectId);

  await previewClick(header);
  await expect.poll(() => selectedGraphObjectId(page)).toBe(headerObjectId);

  await previewClick(main);
  await expect.poll(() => selectedGraphObjectId(page)).toBe(mainObjectId);

  await footer.scrollIntoViewIfNeeded();
  await previewClick(footer);
  await expect.poll(() => selectedGraphObjectId(page)).toBe(footerObjectId);
});

test('application shell frame selection keeps a stable preview context', async ({ page }) => {
  await page.goto('/');
  await disableDevServerOverlay(page);

  await expandActiveTree(page);
  await page.getByRole('tab', { name: 'Preview' }).click();

  const previewCanvas = page.locator('.ssg-preview-canvas');
  const header = previewCanvas.locator('[data-inspect-id="shell-header-container"]');
  const breadcrumb = previewCanvas.locator('[data-inspect-id="shell-breadcrumb-container"]');
  const main = previewCanvas.locator('[data-inspect-id="shell-main-container"]');
  const footer = previewCanvas.locator('[data-inspect-id="shell-footer-container"]');

  const shellObjectId = await objectIdForNode(page, 'Shell', 'application-shell');
  const factsheetScreenObjectId = await objectIdForNode(page, 'Screen', 'Tenant Fact Sheet');
  expect(shellObjectId).toBeTruthy();
  expect(factsheetScreenObjectId).toBeTruthy();
  const headerObjectId = await childObjectId(page, shellObjectId!, 'Container', 'Header Container');
  const mainObjectId = await childObjectId(page, shellObjectId!, 'Container', 'Main Container');
  const footerObjectId = await childObjectId(page, shellObjectId!, 'Container', 'Footer Container');
  const breadcrumbObjectId = await childObjectId(page, shellObjectId!, 'Container', 'Breadcrumb Container');
  expect(headerObjectId).toBeTruthy();
  expect(mainObjectId).toBeTruthy();
  expect(footerObjectId).toBeTruthy();
  expect(breadcrumbObjectId).toBeTruthy();

  await selectGraphObjectId(page, factsheetScreenObjectId!);
  await expect.poll(() => displayedShellObjectId(page)).toBe(shellObjectId);
  await expect.poll(() => displayedScreenObjectId(page)).toBe(factsheetScreenObjectId);
  await expect(header).toBeVisible();
  await expect(breadcrumb).toBeVisible();
  await expect(main).toBeVisible();
  await expect(footer).toBeAttached();

  await selectGraphObjectId(page, headerObjectId!);
  await expect.poll(() => displayedShellObjectId(page)).toBe(shellObjectId);
  await expect.poll(() => displayedScreenObjectId(page)).toBe(factsheetScreenObjectId);
  await expect(header).toBeVisible();
  await expect(breadcrumb).toBeVisible();

  await selectGraphObjectId(page, breadcrumbObjectId!);
  await expect.poll(() => displayedShellObjectId(page)).toBe(shellObjectId);
  await expect.poll(() => displayedScreenObjectId(page)).toBe(factsheetScreenObjectId);
  await expect(breadcrumb).toBeVisible();
  await expect(main).toBeVisible();

  await selectGraphObjectId(page, mainObjectId!);
  await expect.poll(() => displayedShellObjectId(page)).toBe(shellObjectId);
  await expect.poll(() => displayedScreenObjectId(page)).toBe(factsheetScreenObjectId);
  await expect(main).toBeVisible();

  await selectGraphObjectId(page, footerObjectId!);
  await expect.poll(() => displayedShellObjectId(page)).toBe(shellObjectId);
  await expect.poll(() => displayedScreenObjectId(page)).toBe(factsheetScreenObjectId);
  await expect(footer).toBeAttached();

  await selectGraphObjectId(page, shellObjectId!);
  await expect.poll(() => displayedShellObjectId(page)).toBe(shellObjectId);
  await expect.poll(() => displayedScreenObjectId(page)).toBe(factsheetScreenObjectId);
  await expect(header).toBeVisible();
  await expect(breadcrumb).toBeVisible();
  await expect(main).toBeVisible();
});
