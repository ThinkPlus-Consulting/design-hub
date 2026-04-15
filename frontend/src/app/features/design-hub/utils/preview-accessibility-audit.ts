export type AccessibilityConformanceLevel = 'A' | 'AA' | 'AAA';

export interface AccessibilityAuditIssue {
  key: string;
  level: AccessibilityConformanceLevel;
  rule: string;
  message: string;
  graphObjectId: string | null;
  elementTag: string;
}

interface RgbaColor {
  r: number;
  g: number;
  b: number;
  a: number;
}

const INTERACTIVE_SELECTOR = [
  'button',
  'a[href]',
  'input:not([type="hidden"])',
  'select',
  'textarea',
  '[role="button"]',
  '[role="link"]',
  '[role="checkbox"]',
  '[role="radio"]',
  '[role="switch"]',
  '[role="tab"]',
].join(', ');

const TEXT_CANDIDATE_SELECTOR = [
  'h1',
  'h2',
  'h3',
  'h4',
  'h5',
  'h6',
  'p',
  'span',
  'div',
  'button',
  'a',
  'label',
  'strong',
  'small',
  'li',
  'td',
  'th',
].join(', ');

export function auditPreviewAccessibility(
  root: ParentNode | null,
  targetLevel: AccessibilityConformanceLevel,
  resolveGraphObjectId: (element: HTMLElement) => string | null = nearestGraphObjectId,
): AccessibilityAuditIssue[] {
  if (!root) {
    return [];
  }

  const issues: AccessibilityAuditIssue[] = [];
  const seen = new Set<string>();

  const pushIssue = (
    level: AccessibilityConformanceLevel,
    rule: string,
    message: string,
    element: HTMLElement,
  ): void => {
    const graphObjectId = resolveGraphObjectId(element);
    const key = `${level}|${rule}|${graphObjectId ?? ''}|${message}`;
    if (seen.has(key)) {
      return;
    }

    seen.add(key);
    issues.push({
      key,
      level,
      rule,
      message,
      graphObjectId,
      elementTag: element.tagName.toLowerCase(),
    });
  };

  for (const element of Array.from(root.querySelectorAll<HTMLElement>(INTERACTIVE_SELECTOR))) {
    if (!isVisible(element)) {
      continue;
    }

    if (!resolveAccessibleName(element)) {
      pushIssue(
        'A',
        'Accessible Name',
        `${describeElement(element)} is missing an accessible name.`,
        element,
      );
    }
  }

  for (const image of Array.from(root.querySelectorAll<HTMLImageElement>('img'))) {
    if (!isVisible(image)) {
      continue;
    }

    if (image.getAttribute('aria-hidden') === 'true' || image.getAttribute('role') === 'presentation') {
      continue;
    }

    if (!image.hasAttribute('alt')) {
      pushIssue(
        'A',
        'Image Alternative',
        `${describeElement(image)} is missing an alt attribute.`,
        image,
      );
    }
  }

  if (targetLevel !== 'A') {
    for (const element of Array.from(root.querySelectorAll<HTMLElement>(TEXT_CANDIDATE_SELECTOR))) {
      if (!isVisible(element)) {
        continue;
      }

      const directText = directTextContent(element);
      if (!directText) {
        continue;
      }

      const computedStyle = getComputedStyle(element);
      const foreground = parseColor(computedStyle.color);
      const background = resolveBackgroundColor(element, root);
      if (!foreground || !background) {
        continue;
      }

      const ratio = contrastRatio(foreground, background);
      const threshold = contrastThreshold(computedStyle, targetLevel);
      if (ratio >= threshold) {
        continue;
      }

      pushIssue(
        targetLevel,
        'Text Contrast',
        `${describeElement(element)} has contrast ${ratio.toFixed(2)}:1 between text ${formatColor(foreground)} and background ${formatColor(background)}; requires ${threshold}:1 for ${targetLevel}.`,
        element,
      );
    }
  }

  return issues.sort((left, right) =>
    left.level.localeCompare(right.level)
    || left.rule.localeCompare(right.rule)
    || (left.graphObjectId ?? '').localeCompare(right.graphObjectId ?? ''),
  );
}

function isVisible(element: HTMLElement): boolean {
  if (element.hidden || element.getAttribute('aria-hidden') === 'true') {
    return false;
  }

  const style = getComputedStyle(element);
  if (style.display === 'none' || style.visibility === 'hidden' || Number(style.opacity) === 0) {
    return false;
  }

  const rect = element.getBoundingClientRect();
  return rect.width > 0 && rect.height > 0;
}

function resolveAccessibleName(element: HTMLElement): string {
  const ariaLabel = element.getAttribute('aria-label')?.trim();
  if (ariaLabel) {
    return ariaLabel;
  }

  const labelledBy = element.getAttribute('aria-labelledby');
  if (labelledBy) {
    const text = labelledBy
      .split(/\s+/)
      .map((id) => element.ownerDocument.getElementById(id)?.textContent ?? '')
      .join(' ')
      .trim();
    if (text) {
      return normalizeText(text);
    }
  }

  if (element instanceof HTMLImageElement) {
    return element.alt.trim();
  }

  if (element instanceof HTMLInputElement) {
    if (['submit', 'reset', 'button'].includes(element.type)) {
      return element.value.trim();
    }

    const associatedLabel = labelForControl(element);
    if (associatedLabel) {
      return associatedLabel;
    }
  }

  if (element instanceof HTMLTextAreaElement || element instanceof HTMLSelectElement) {
    const associatedLabel = labelForControl(element);
    if (associatedLabel) {
      return associatedLabel;
    }
  }

  const wrappingLabel = element.closest('label');
  if (wrappingLabel) {
    const text = normalizeText(wrappingLabel.textContent ?? '');
    if (text) {
      return text;
    }
  }

  const innerText = normalizeText(element.innerText || element.textContent || '');
  if (innerText) {
    return innerText;
  }

  return element.getAttribute('title')?.trim() ?? '';
}

function labelForControl(control: HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement): string {
  if (!control.id) {
    return '';
  }

  const selector = `label[for="${escapeAttributeValue(control.id)}"]`;
  const label = control.ownerDocument.querySelector<HTMLLabelElement>(selector);
  return normalizeText(label?.textContent ?? '');
}

function directTextContent(element: HTMLElement): string {
  return normalizeText(
    Array.from(element.childNodes)
      .filter((node) => node.nodeType === Node.TEXT_NODE)
      .map((node) => node.textContent ?? '')
      .join(' '),
  );
}

function resolveBackgroundColor(element: HTMLElement, root: ParentNode): RgbaColor | null {
  const rootElement = root instanceof HTMLElement ? root : element.ownerDocument.body;
  const layers = [
    ...collectAncestryBackgroundLayers(element, rootElement),
    ...collectBackdropBackgroundLayers(element, rootElement),
  ];
  const fallback = parseColor(getComputedStyle(rootElement).backgroundColor);
  if (fallback && fallback.a > 0) {
    layers.push(fallback);
  }

  let composite: RgbaColor = { r: 255, g: 255, b: 255, a: 1 };
  for (let index = layers.length - 1; index >= 0; index -= 1) {
    composite = compositeColor(layers[index]!, composite);
  }

  return composite;
}

function collectAncestryBackgroundLayers(element: HTMLElement, rootElement: HTMLElement): RgbaColor[] {
  const layers: RgbaColor[] = [];
  let current: HTMLElement | null = element;
  while (current) {
    const parsed = parseColor(getComputedStyle(current).backgroundColor);
    if (parsed && parsed.a > 0) {
      layers.push(parsed);
    }

    if (current === rootElement) {
      break;
    }

    current = current.parentElement;
  }

  return layers;
}

function collectBackdropBackgroundLayers(element: HTMLElement, rootElement: HTMLElement): RgbaColor[] {
  const view = element.ownerDocument.defaultView;
  if (!view?.document?.elementsFromPoint) {
    return [];
  }

  const rect = element.getBoundingClientRect();
  if (rect.width <= 0 || rect.height <= 0) {
    return [];
  }

  const x = clampViewportCoordinate(rect.left + (rect.width / 2), view.innerWidth);
  const y = clampViewportCoordinate(rect.top + (rect.height / 2), view.innerHeight);
  const ownershipChain = collectOwnershipChain(element, rootElement);
  const layers: RgbaColor[] = [];

  for (const candidate of view.document.elementsFromPoint(x, y)) {
    if (!(candidate instanceof HTMLElement) || ownershipChain.has(candidate)) {
      continue;
    }

    const parsed = parseColor(getComputedStyle(candidate).backgroundColor);
    if (!parsed || parsed.a <= 0) {
      continue;
    }

    layers.push(parsed);
    if (parsed.a >= 0.99) {
      break;
    }
  }

  return layers;
}

function collectOwnershipChain(element: HTMLElement, rootElement: HTMLElement): Set<HTMLElement> {
  const chain = new Set<HTMLElement>();
  let current: HTMLElement | null = element;
  while (current) {
    chain.add(current);
    if (current === rootElement) {
      break;
    }

    current = current.parentElement;
  }

  return chain;
}

function parseColor(value: string): RgbaColor | null {
  const trimmed = value.trim().toLowerCase();
  if (!trimmed || trimmed === 'transparent') {
    return null;
  }

  const match = trimmed.match(/^rgba?\(([^)]+)\)$/);
  if (match) {
    const parts = match[1]
      .split(',')
      .map((part) => Number.parseFloat(part.trim()))
      .filter((part) => !Number.isNaN(part));
    if (parts.length < 3) {
      return null;
    }

    return {
      r: clampChannel(parts[0] ?? 0),
      g: clampChannel(parts[1] ?? 0),
      b: clampChannel(parts[2] ?? 0),
      a: Math.max(0, Math.min(1, parts[3] ?? 1)),
    };
  }

  const srgbMatch = trimmed.match(/^color\(srgb\s+([^\s]+)\s+([^\s]+)\s+([^\s/]+)(?:\s*\/\s*([^)]+))?\)$/);
  if (!srgbMatch) {
    return null;
  }

  return {
    r: clampChannel(parseSrgbChannel(srgbMatch[1])),
    g: clampChannel(parseSrgbChannel(srgbMatch[2])),
    b: clampChannel(parseSrgbChannel(srgbMatch[3])),
    a: parseAlphaChannel(srgbMatch[4]),
  };
}

function parseSrgbChannel(value: string | undefined): number {
  const normalized = (value ?? '').trim();
  if (!normalized) {
    return 0;
  }

  if (normalized.endsWith('%')) {
    return (Number.parseFloat(normalized) / 100) * 255;
  }

  const parsed = Number.parseFloat(normalized);
  if (Number.isNaN(parsed)) {
    return 0;
  }

  return parsed <= 1 ? parsed * 255 : parsed;
}

function parseAlphaChannel(value: string | undefined): number {
  const normalized = (value ?? '').trim();
  if (!normalized) {
    return 1;
  }

  if (normalized.endsWith('%')) {
    return Math.max(0, Math.min(1, Number.parseFloat(normalized) / 100));
  }

  const parsed = Number.parseFloat(normalized);
  return Number.isNaN(parsed) ? 1 : Math.max(0, Math.min(1, parsed));
}

function contrastThreshold(style: CSSStyleDeclaration, level: AccessibilityConformanceLevel): number {
  const fontSize = Number.parseFloat(style.fontSize);
  const fontWeight = Number.parseInt(style.fontWeight, 10);
  const isBold = Number.isFinite(fontWeight) ? fontWeight >= 700 : /bold/i.test(style.fontWeight);
  const isLargeText = fontSize >= 24 || (fontSize >= 18.66 && isBold);

  if (level === 'AAA') {
    return isLargeText ? 4.5 : 7;
  }

  return isLargeText ? 3 : 4.5;
}

function contrastRatio(foreground: RgbaColor, background: RgbaColor): number {
  const fg = compositeOnWhite(foreground);
  const bg = compositeOnWhite(background);
  const fgLuminance = relativeLuminance(fg);
  const bgLuminance = relativeLuminance(bg);
  const lighter = Math.max(fgLuminance, bgLuminance);
  const darker = Math.min(fgLuminance, bgLuminance);
  return (lighter + 0.05) / (darker + 0.05);
}

function compositeColor(foreground: RgbaColor, background: RgbaColor): RgbaColor {
  const alpha = foreground.a + background.a * (1 - foreground.a);
  if (alpha <= 0) {
    return { r: 255, g: 255, b: 255, a: 0 };
  }

  return {
    r: Math.round(((foreground.r * foreground.a) + (background.r * background.a * (1 - foreground.a))) / alpha),
    g: Math.round(((foreground.g * foreground.a) + (background.g * background.a * (1 - foreground.a))) / alpha),
    b: Math.round(((foreground.b * foreground.a) + (background.b * background.a * (1 - foreground.a))) / alpha),
    a: alpha,
  };
}

function relativeLuminance(color: RgbaColor): number {
  const toLinear = (channel: number): number => {
    const value = channel / 255;
    return value <= 0.03928 ? value / 12.92 : ((value + 0.055) / 1.055) ** 2.4;
  };

  return 0.2126 * toLinear(color.r) + 0.7152 * toLinear(color.g) + 0.0722 * toLinear(color.b);
}

function compositeOnWhite(color: RgbaColor): RgbaColor {
  if (color.a >= 0.99) {
    return { ...color, a: 1 };
  }

  return {
    r: Math.round((color.r * color.a) + (255 * (1 - color.a))),
    g: Math.round((color.g * color.a) + (255 * (1 - color.a))),
    b: Math.round((color.b * color.a) + (255 * (1 - color.a))),
    a: 1,
  };
}

function nearestGraphObjectId(element: HTMLElement): string | null {
  return element.closest<HTMLElement>('[source-object-id]')?.getAttribute('source-object-id')?.trim() ?? null;
}

function describeElement(element: HTMLElement): string {
  const accessibleName = resolveAccessibleName(element);
  if (accessibleName) {
    return `"${truncate(accessibleName)}"`;
  }

  const directText = directTextContent(element);
  if (directText) {
    return `"${truncate(directText)}"`;
  }

  return `<${element.tagName.toLowerCase()}>`;
}

function normalizeText(value: string): string {
  return value.replace(/\s+/g, ' ').trim();
}

function truncate(value: string, maxLength = 48): string {
  return value.length <= maxLength ? value : `${value.slice(0, maxLength - 1)}…`;
}

function escapeAttributeValue(value: string): string {
  return value.replace(/["\\]/g, '\\$&');
}

function formatColor(color: RgbaColor): string {
  return color.a >= 0.99
    ? `rgb(${color.r}, ${color.g}, ${color.b})`
    : `rgba(${color.r}, ${color.g}, ${color.b}, ${Number(color.a.toFixed(2))})`;
}

function clampChannel(value: number): number {
  return Math.max(0, Math.min(255, Math.round(value)));
}

function clampViewportCoordinate(value: number, limit: number): number {
  if (!Number.isFinite(value) || !Number.isFinite(limit) || limit <= 1) {
    return 0;
  }

  return Math.max(0, Math.min(limit - 1, value));
}
