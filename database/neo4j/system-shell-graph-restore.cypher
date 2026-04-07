// Restores the system-shell-graph Neo4j dataset exported from this repository.
// Generated automatically. Re-import with: cat database/neo4j/system-shell-graph-restore.cypher | docker exec -i design-hub-neo4j cypher-shell -u neo4j -p password
MATCH (n:SystemShellGraphNode) DETACH DELETE n;

CREATE (n0:SystemShellGraphNode:ComponentDefinition:Definition);
SET n0 += {assetName: 'Accordion', assetSource: 'PrimeNG', assetType: 'accordion', code: 'CD.ACCORDION', description: 'Accordion groups a collection of contents in tabs.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.ACCORDION', id: '92cd6d8c-2801-3f20-bb17-13d9009c25a7', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-accordion.mjs', layer: 'definition', name: 'Accordion', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n1:SystemShellGraphNode:ComponentDefinition:Definition);
SET n1 += {assetName: 'AutoComplete', assetSource: 'PrimeNG', assetType: 'autocomplete', code: 'CD.AUTOCOMPLETE', description: 'AutoComplete is an input component that provides real-time suggestions when being typed.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.AUTOCOMPLETE', id: '6c2cfb89-beae-303f-9695-3d86028bf855', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-autocomplete.mjs', layer: 'definition', name: 'AutoComplete', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n2:SystemShellGraphNode:ComponentDefinition:Definition);
SET n2 += {assetName: 'Avatar', assetSource: 'PrimeNG', assetType: 'avatar', code: 'CD.AVATAR', description: 'Avatar represents people using icons, labels and images.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.AVATAR', id: '846e2ac5-cfb8-39e8-b21b-06fe03df9576', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-avatar.mjs', layer: 'definition', name: 'Avatar', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n3:SystemShellGraphNode:ComponentDefinition:Definition);
SET n3 += {assetName: 'Badge', assetSource: 'PrimeNG', assetType: 'badge', code: 'CD.BADGE', description: 'Badge is a small status indicator for another element.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.BADGE', id: 'e0f0bcbf-ed83-338d-b1c4-1c5dd72dade5', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-badge.mjs', layer: 'definition', name: 'Badge', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n4:SystemShellGraphNode:ComponentDefinition:Definition);
SET n4 += {assetName: 'BlockUI', assetSource: 'PrimeNG', assetType: 'blockui', code: 'CD.BLOCKUI', description: 'BlockUI can either block other components or the whole page.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.BLOCKUI', id: '08b1b0ab-0277-3ebf-b1a8-6c947af8ec62', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-blockui.mjs', layer: 'definition', name: 'BlockUI', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n5:SystemShellGraphNode:ComponentDefinition:Definition);
SET n5 += {assetName: 'Breadcrumb', assetSource: 'PrimeNG', assetType: 'breadcrumb', code: 'CD.BREADCRUMB', description: 'Breadcrumb provides contextual information about page hierarchy.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.BREADCRUMB', id: '06eb1bec-1398-3cfa-bc9d-f182673f56b4', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-breadcrumb.mjs', layer: 'definition', name: 'Breadcrumb', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n6:SystemShellGraphNode:ComponentDefinition:Definition);
SET n6 += {assetName: 'Button', assetSource: 'PrimeNG', assetType: 'button', code: 'CD.BUTTON', description: 'Button is an extension to standard button element with icons and theming.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.BUTTON', id: '53c9b821-141f-3af9-b31b-f1a5fb24e82c', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-button.mjs', layer: 'definition', name: 'Button', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n7:SystemShellGraphNode:ComponentDefinition:Definition);
SET n7 += {assetName: 'Card', assetSource: 'PrimeNG', assetType: 'card', code: 'CD.CARD', description: 'Card is a flexible container component.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.CARD', id: 'e0c8a384-04e4-3eed-b206-ac9c4127ae19', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-card.mjs', layer: 'definition', name: 'Card', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n8:SystemShellGraphNode:ComponentDefinition:Definition);
SET n8 += {assetName: 'Carousel', assetSource: 'PrimeNG', assetType: 'carousel', code: 'CD.CAROUSEL', description: 'Carousel is a content slider featuring various customization options.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.CAROUSEL', id: '3b6bcc00-bdf0-3030-a5b4-0d74961a1a03', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-carousel.mjs', layer: 'definition', name: 'Carousel', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n9:SystemShellGraphNode:ComponentDefinition:Definition);
SET n9 += {assetName: 'CascadeSelect', assetSource: 'PrimeNG', assetType: 'cascadeselect', code: 'CD.CASCADESELECT', description: 'CascadeSelect displays a nested structure of options.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.CASCADESELECT', id: '7170633e-6fdf-3e23-9a4c-198d266585d9', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-cascadeselect.mjs', layer: 'definition', name: 'CascadeSelect', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n10:SystemShellGraphNode:ComponentDefinition:Definition);
SET n10 += {assetName: 'Chart', assetSource: 'PrimeNG', assetType: 'chart', code: 'CD.CHART', description: 'Chart components are based on Charts.js 3.3.2+, an open source HTML5 based charting library.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.CHART', id: 'f0ca1586-6b05-305e-910d-c721a6c6bcdc', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-chart.mjs', layer: 'definition', name: 'Chart', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n11:SystemShellGraphNode:ComponentDefinition:Definition);
SET n11 += {assetName: 'Checkbox', assetSource: 'PrimeNG', assetType: 'checkbox', code: 'CD.CHECKBOX', description: 'Checkbox is an extension to standard checkbox element with theming.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.CHECKBOX', id: 'ffc77adf-dd6c-3585-93a6-cfb5598bb1eb', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-checkbox.mjs', layer: 'definition', name: 'Checkbox', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n12:SystemShellGraphNode:ComponentDefinition:Definition);
SET n12 += {assetName: 'Chip', assetSource: 'PrimeNG', assetType: 'chip', code: 'CD.CHIP', description: 'Chip represents entities using icons, labels and images.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.CHIP', id: '5f56eac7-e9ff-3204-81de-52f78c05aaf6', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-chip.mjs', layer: 'definition', name: 'Chip', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n13:SystemShellGraphNode:ComponentDefinition:Definition);
SET n13 += {assetName: 'ColorPicker', assetSource: 'PrimeNG', assetType: 'colorpicker', code: 'CD.COLORPICKER', description: 'ColorPicker is an input component to select a color.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.COLORPICKER', id: 'e7bb4afc-d63f-3d47-a9b5-6249390a4be8', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-colorpicker.mjs', layer: 'definition', name: 'ColorPicker', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n14:SystemShellGraphNode:ComponentDefinition:Definition);
SET n14 += {assetName: 'ConfirmDialog', assetSource: 'PrimeNG', assetType: 'confirmdialog', code: 'CD.CONFIRMDIALOG', description: 'ConfirmDialog is backed by a service utilizing Observables to display confirmation windows easily that can be shared by multiple actions on the same component.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.CONFIRMDIALOG', id: '836bf64c-27f0-36fc-a131-619a87a0deab', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-confirmdialog.mjs', layer: 'definition', name: 'ConfirmDialog', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n15:SystemShellGraphNode:ComponentDefinition:Definition);
SET n15 += {assetName: 'ConfirmPopup', assetSource: 'PrimeNG', assetType: 'confirmpopup', code: 'CD.CONFIRMPOPUP', description: 'ConfirmPopup displays a confirmation overlay displayed relatively to its target.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.CONFIRMPOPUP', id: '41d899a9-9e82-3a41-946d-c3e8ec467350', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-confirmpopup.mjs', layer: 'definition', name: 'ConfirmPopup', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n16:SystemShellGraphNode:ComponentDefinition:Definition);
SET n16 += {assetName: 'ContextMenu', assetSource: 'PrimeNG', assetType: 'contextmenu', code: 'CD.CONTEXTMENU', description: 'ContextMenu displays an overlay menu on right click of its target.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.CONTEXTMENU', id: 'ece8f45f-78fc-301b-8b7d-6daac6d24296', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-contextmenu.mjs', layer: 'definition', name: 'ContextMenu', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n17:SystemShellGraphNode:ComponentDefinition:Definition);
SET n17 += {assetName: 'DataView', assetSource: 'PrimeNG', assetType: 'dataview', code: 'CD.DATAVIEW', description: 'DataView displays data in grid grid-cols-12 gap-4 or list layout with pagination and sorting features.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.DATAVIEW', id: '9cf5e715-aaa0-30b8-8617-90d8dec5edf8', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-dataview.mjs', layer: 'definition', name: 'DataView', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n18:SystemShellGraphNode:ComponentDefinition:Definition);
SET n18 += {assetName: 'DatePicker', assetSource: 'PrimeNG', assetType: 'datepicker', code: 'CD.DATEPICKER', description: 'DatePicker is an input component to select a date.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.DATEPICKER', id: '23a225de-14c6-3eeb-bcd1-fd0e8bfaf980', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-datepicker.mjs', layer: 'definition', name: 'DatePicker', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n19:SystemShellGraphNode:ComponentDefinition:Definition);
SET n19 += {assetName: 'Dialog', assetSource: 'PrimeNG', assetType: 'dialog', code: 'CD.DIALOG', description: 'Dialog is a container to display content in an overlay window.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.DIALOG', id: 'f1bb6526-0ef7-3edb-ae93-eab6123291b7', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-dialog.mjs', layer: 'definition', name: 'Dialog', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n20:SystemShellGraphNode:ComponentDefinition:Definition);
SET n20 += {assetName: 'Divider', assetSource: 'PrimeNG', assetType: 'divider', code: 'CD.DIVIDER', description: 'Divider is used to separate contents.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.DIVIDER', id: '879f49fe-cd4d-3c3a-ac92-aff0d89ef1bc', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-divider.mjs', layer: 'definition', name: 'Divider', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n21:SystemShellGraphNode:ComponentDefinition:Definition);
SET n21 += {assetName: 'Dock', assetSource: 'PrimeNG', assetType: 'dock', code: 'CD.DOCK', description: 'Dock is a navigation component consisting of menuitems.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.DOCK', id: 'fe84fc96-b916-38b1-be60-d18c8152c027', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-dock.mjs', layer: 'definition', name: 'Dock', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n22:SystemShellGraphNode:ComponentDefinition:Definition);
SET n22 += {assetName: 'Drag and Drop', assetSource: 'PrimeNG', assetType: 'dragdrop', code: 'CD.DRAGDROP', description: 'pDraggable and pDroppable directives apply drag-drop behaviors to any element.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.DRAGDROP', id: 'e080c84d-36c3-3490-bac7-2658407796ed', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-dragdrop.mjs', layer: 'definition', name: 'Drag and Drop', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n23:SystemShellGraphNode:ComponentDefinition:Definition);
SET n23 += {assetName: 'Drawer', assetSource: 'PrimeNG', assetType: 'drawer', code: 'CD.DRAWER', description: 'Drawer is a container component displayed as an overlay.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.DRAWER', id: 'abdc81ca-c6f6-3dc2-bc86-775ce5d629e2', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-drawer.mjs', layer: 'definition', name: 'Drawer', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n24:SystemShellGraphNode:ComponentDefinition:Definition);
SET n24 += {assetName: 'Dynamic Dialog', assetSource: 'PrimeNG', assetType: 'dynamicdialog', code: 'CD.DYNAMICDIALOG', description: 'Dialogs can be created dynamically with any component as the content using a DialogService.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.DYNAMICDIALOG', id: '2aba0323-81a0-39bf-8131-fb78dcc1d571', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-dynamicdialog.mjs', layer: 'definition', name: 'Dynamic Dialog', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n25:SystemShellGraphNode:ComponentDefinition:Definition);
SET n25 += {assetName: 'Editor', assetSource: 'PrimeNG', assetType: 'editor', code: 'CD.EDITOR', description: 'Editor is rich text editor component based on Quill.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.EDITOR', id: '51aa5584-1cb9-3aa8-b6e5-b903181805d2', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-editor.mjs', layer: 'definition', name: 'Editor', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n26:SystemShellGraphNode:ComponentDefinition:Definition);
SET n26 += {assetName: 'Fieldset', assetSource: 'PrimeNG', assetType: 'fieldset', code: 'CD.FIELDSET', description: 'Fieldset is a grouping component with a content toggle feature.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.FIELDSET', id: 'f0cdff32-2cf7-3ade-ac3c-ad698ef9c4af', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-fieldset.mjs', layer: 'definition', name: 'Fieldset', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n27:SystemShellGraphNode:ComponentDefinition:Definition);
SET n27 += {assetName: 'FileUpload', assetSource: 'PrimeNG', assetType: 'fileupload', code: 'CD.FILEUPLOAD', description: 'FileUpload is an advanced uploader with dragdrop support, multi file uploads, auto uploading, progress tracking and validations.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.FILEUPLOAD', id: '54d981e8-7d65-3dc4-a97a-4d1d5a21c69b', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-fileupload.mjs', layer: 'definition', name: 'FileUpload', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n28:SystemShellGraphNode:ComponentDefinition:Definition);
SET n28 += {assetName: 'Float Label', assetSource: 'PrimeNG', assetType: 'floatlabel', code: 'CD.FLOATLABEL', description: 'FloatLabel appears on top of the input field when focused.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.FLOATLABEL', id: '80e71bb6-7c38-359c-87ae-967b8c461edc', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-floatlabel.mjs', layer: 'definition', name: 'Float Label', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n29:SystemShellGraphNode:ComponentDefinition:Definition);
SET n29 += {assetName: 'Fluid', assetSource: 'PrimeNG', assetType: 'fluid', code: 'CD.FLUID', description: 'Fluid is a layout component to make descendant components span full width of their container.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.FLUID', id: '30ccda27-0380-33c8-ba43-701e336cca0e', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-fluid.mjs', layer: 'definition', name: 'Fluid', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n30:SystemShellGraphNode:ComponentDefinition:Definition);
SET n30 += {assetName: 'Focus Trap', assetSource: 'PrimeNG', assetType: 'focustrap', code: 'CD.FOCUSTRAP', description: 'Focus Trap keeps focus within a certain DOM element while tabbing.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.FOCUSTRAP', id: '8322e64f-1e34-3c12-a62e-aa4fd1b9e67e', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-focustrap.mjs', layer: 'definition', name: 'Focus Trap', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n31:SystemShellGraphNode:ComponentDefinition:Definition);
SET n31 += {assetName: 'Gallery', assetSource: 'PrimeNG', assetType: 'galleria', code: 'CD.GALLERIA', description: 'Galleria is an advanced content gallery component.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.GALLERIA', id: '118748f2-7599-3aae-b72b-10891861178a', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-galleria.mjs', layer: 'definition', name: 'Gallery', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n32:SystemShellGraphNode:ComponentDefinition:Definition);
SET n32 += {assetName: 'IconField', assetSource: 'PrimeNG', assetType: 'iconfield', code: 'CD.ICONFIELD', description: 'IconField wraps an input and an icon.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.ICONFIELD', id: 'e764662a-c693-377a-a301-504fa418b711', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-iconfield.mjs', layer: 'definition', name: 'IconField', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n33:SystemShellGraphNode:ComponentDefinition:Definition);
SET n33 += {assetName: 'Ifta Label', assetSource: 'PrimeNG', assetType: 'iftalabel', code: 'CD.IFTALABEL', description: 'IftaLabel is used to create infield top aligned labels.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.IFTALABEL', id: 'd81b6f8f-f516-35e8-a907-2013d29b9c9a', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-iftalabel.mjs', layer: 'definition', name: 'Ifta Label', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n34:SystemShellGraphNode:ComponentDefinition:Definition);
SET n34 += {assetName: 'Image', assetSource: 'PrimeNG', assetType: 'image', code: 'CD.IMAGE', description: 'Displays an image with preview and tranformation options.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.IMAGE', id: '48e0ca59-dd2c-3844-91e0-3a6d4e436c21', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-image.mjs', layer: 'definition', name: 'Image', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n35:SystemShellGraphNode:ComponentDefinition:Definition);
SET n35 += {assetName: 'ImageCompare', assetSource: 'PrimeNG', assetType: 'imagecompare', code: 'CD.IMAGECOMPARE', description: 'Compare two images side by side with a slider.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.IMAGECOMPARE', id: 'a4bc07b4-e008-3863-a621-5355f1f21007', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-imagecompare.mjs', layer: 'definition', name: 'ImageCompare', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n36:SystemShellGraphNode:ComponentDefinition:Definition);
SET n36 += {assetName: 'Inplace', assetSource: 'PrimeNG', assetType: 'inplace', code: 'CD.INPLACE', description: 'Inplace provides an easy to do editing and display at the same time where clicking the output displays the actual content.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.INPLACE', id: 'a9e4fcdb-afc0-3f85-ae35-7211aee53c36', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-inplace.mjs', layer: 'definition', name: 'Inplace', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n37:SystemShellGraphNode:ComponentDefinition:Definition);
SET n37 += {assetName: 'InputGroup', assetSource: 'PrimeNG', assetType: 'inputgroup', code: 'CD.INPUTGROUP', description: 'Text, icon, buttons and other content can be grouped next to an input.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.INPUTGROUP', id: 'eb66880a-094b-3374-b8b7-6b77da0d82b3', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-inputgroup.mjs', layer: 'definition', name: 'InputGroup', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n38:SystemShellGraphNode:ComponentDefinition:Definition);
SET n38 += {assetName: 'InputMask', assetSource: 'PrimeNG', assetType: 'inputmask', code: 'CD.INPUTMASK', description: 'InputMask component is used to enter input in a certain format such as numeric, date, currency and phone.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.INPUTMASK', id: 'b5a4b3b1-1de0-399b-8b95-d76acb94f0fb', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-inputmask.mjs', layer: 'definition', name: 'InputMask', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n39:SystemShellGraphNode:ComponentDefinition:Definition);
SET n39 += {assetName: 'InputNumber', assetSource: 'PrimeNG', assetType: 'inputnumber', code: 'CD.INPUTNUMBER', description: 'InputNumber is an input component to provide numerical input.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.INPUTNUMBER', id: '86674425-d7af-3148-a09e-4cd1110853e8', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-inputnumber.mjs', layer: 'definition', name: 'InputNumber', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n40:SystemShellGraphNode:ComponentDefinition:Definition);
SET n40 += {assetName: 'Otp Input', assetSource: 'PrimeNG', assetType: 'inputotp', code: 'CD.INPUTOTP', description: 'Input Otp is used to enter one time passwords.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.INPUTOTP', id: 'd5e475da-f681-3fc9-a408-5400e93a27f3', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-inputotp.mjs', layer: 'definition', name: 'Otp Input', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n41:SystemShellGraphNode:ComponentDefinition:Definition);
SET n41 += {assetName: 'InputText', assetSource: 'PrimeNG', assetType: 'inputtext', code: 'CD.INPUTTEXT', description: 'InputText is an extension to standard input element with theming and keyfiltering.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.INPUTTEXT', id: 'b0f8866b-983a-3e04-bae5-9804bd2dd7f6', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-inputtext.mjs', layer: 'definition', name: 'InputText', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n42:SystemShellGraphNode:ComponentDefinition:Definition);
SET n42 += {assetName: 'KeyFilter', assetSource: 'PrimeNG', assetType: 'keyfilter', code: 'CD.KEYFILTER', description: 'KeyFilter is a directive to restrict individual key strokes. In order to restrict the whole input, use InputNumber or InputMask instead.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.KEYFILTER', id: 'e6768373-6a52-355d-9238-591ff54606e4', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-keyfilter.mjs', layer: 'definition', name: 'KeyFilter', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n43:SystemShellGraphNode:ComponentDefinition:Definition);
SET n43 += {assetName: 'Knob', assetSource: 'PrimeNG', assetType: 'knob', code: 'CD.KNOB', description: 'Knob is a form component to define number inputs with a dial.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.KNOB', id: 'fca3acd9-2ebc-3b73-b00d-7393de826af3', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-knob.mjs', layer: 'definition', name: 'Knob', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n44:SystemShellGraphNode:ComponentDefinition:Definition);
SET n44 += {assetName: 'Listbox', assetSource: 'PrimeNG', assetType: 'listbox', code: 'CD.LISTBOX', description: 'Listbox is used to select one or more values from a list of items.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.LISTBOX', id: '8dfc97ab-3293-3429-9c9b-50a6450cd726', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-listbox.mjs', layer: 'definition', name: 'Listbox', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n45:SystemShellGraphNode:ComponentDefinition:Definition);
SET n45 += {assetName: 'MegaMenu', assetSource: 'PrimeNG', assetType: 'megamenu', code: 'CD.MEGAMENU', description: 'MegaMenu is navigation component that displays submenus together.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.MEGAMENU', id: '5b376a6f-7ef0-30a1-b060-da16266b6077', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-megamenu.mjs', layer: 'definition', name: 'MegaMenu', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n46:SystemShellGraphNode:ComponentDefinition:Definition);
SET n46 += {assetName: 'Menu', assetSource: 'PrimeNG', assetType: 'menu', code: 'CD.MENU', description: 'Menu is a navigation / command component that supports dynamic and static positioning.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.MENU', id: '55ae06bf-8f9e-3ba5-b8fd-432823d10be0', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-menu.mjs', layer: 'definition', name: 'Menu', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n47:SystemShellGraphNode:ComponentDefinition:Definition);
SET n47 += {assetName: 'Menubar', assetSource: 'PrimeNG', assetType: 'menubar', code: 'CD.MENUBAR', description: 'Menubar is a horizontal menu component.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.MENUBAR', id: '96bbf592-f6d7-3f11-84fd-9e7a154093ec', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-menubar.mjs', layer: 'definition', name: 'Menubar', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n48:SystemShellGraphNode:ComponentDefinition:Definition);
SET n48 += {assetName: 'Message', assetSource: 'PrimeNG', assetType: 'message', code: 'CD.MESSAGE', description: 'Message component is used to display inline messages.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.MESSAGE', id: 'fc765086-7acc-3f17-a607-ebfb88194ad9', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-message.mjs', layer: 'definition', name: 'Message', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n49:SystemShellGraphNode:ComponentDefinition:Definition);
SET n49 += {assetName: 'MeterGroup', assetSource: 'PrimeNG', assetType: 'metergroup', code: 'CD.METERGROUP', description: 'MeterGroup displays scalar measurements within a known range.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.METERGROUP', id: 'd3765758-d69f-3784-a671-43bb866384d0', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-metergroup.mjs', layer: 'definition', name: 'MeterGroup', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n50:SystemShellGraphNode:ComponentDefinition:Definition);
SET n50 += {assetName: 'MultiSelect', assetSource: 'PrimeNG', assetType: 'multiselect', code: 'CD.MULTISELECT', description: 'MultiSelect is used to select multiple items from a collection.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.MULTISELECT', id: 'b3aa82e8-760e-3d76-b289-1be9ac66dc1a', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-multiselect.mjs', layer: 'definition', name: 'MultiSelect', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n51:SystemShellGraphNode:ComponentDefinition:Definition);
SET n51 += {assetName: 'OrderList', assetSource: 'PrimeNG', assetType: 'orderlist', code: 'CD.ORDERLIST', description: 'OrderList is used to sort a collection.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.ORDERLIST', id: 'ed539de6-6558-339e-8b59-f49364a3cc4d', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-orderlist.mjs', layer: 'definition', name: 'OrderList', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n52:SystemShellGraphNode:ComponentDefinition:Definition);
SET n52 += {assetName: 'Organization Chart', assetSource: 'PrimeNG', assetType: 'organizationchart', code: 'CD.ORGANIZATIONCHART', description: 'OrganizationChart visualizes hierarchical organization data.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.ORGANIZATIONCHART', id: '01a8da18-5187-3ab0-8424-c5bc43239cb9', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-organizationchart.mjs', layer: 'definition', name: 'Organization Chart', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n53:SystemShellGraphNode:ComponentDefinition:Definition);
SET n53 += {assetName: 'Paginator', assetSource: 'PrimeNG', assetType: 'paginator', code: 'CD.PAGINATOR', description: 'Paginator displays data in paged format and provides navigation between pages.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.PAGINATOR', id: '0db066b3-875c-3827-a212-b76f48c68c85', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-paginator.mjs', layer: 'definition', name: 'Paginator', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n54:SystemShellGraphNode:ComponentDefinition:Definition);
SET n54 += {assetName: 'Panel', assetSource: 'PrimeNG', assetType: 'panel', code: 'CD.PANEL', description: 'Panel is a container component with an optional content toggle feature.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.PANEL', id: '45938028-a36f-3357-89b6-afd375dda567', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-panel.mjs', layer: 'definition', name: 'Panel', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n55:SystemShellGraphNode:ComponentDefinition:Definition);
SET n55 += {assetName: 'PanelMenu', assetSource: 'PrimeNG', assetType: 'panelmenu', code: 'CD.PANELMENU', description: 'PanelMenu is a hybrid of Accordion and Tree components.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.PANELMENU', id: 'f20b08f1-2abb-35b4-a05e-8cacae5f7471', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-panelmenu.mjs', layer: 'definition', name: 'PanelMenu', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n56:SystemShellGraphNode:ComponentDefinition:Definition);
SET n56 += {assetName: 'Password', assetSource: 'PrimeNG', assetType: 'password', code: 'CD.PASSWORD', description: 'Password displays strength indicator for password fields.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.PASSWORD', id: '99865877-9e14-323c-bd0a-e566703b5be3', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-password.mjs', layer: 'definition', name: 'Password', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n57:SystemShellGraphNode:ComponentDefinition:Definition);
SET n57 += {assetName: 'PickList', assetSource: 'PrimeNG', assetType: 'picklist', code: 'CD.PICKLIST', description: 'PickList is used to reorder items between different lists.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.PICKLIST', id: '6db6a62f-f421-364a-b111-24cc95da7936', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-picklist.mjs', layer: 'definition', name: 'PickList', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n58:SystemShellGraphNode:ComponentDefinition:Definition);
SET n58 += {assetName: 'Popover', assetSource: 'PrimeNG', assetType: 'popover', code: 'CD.POPOVER', description: 'Popover is a container component that can overlay other components on page.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.POPOVER', id: '1844856c-e140-3f23-98ac-3628aba384cb', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-popover.mjs', layer: 'definition', name: 'Popover', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n59:SystemShellGraphNode:ComponentDefinition:Definition);
SET n59 += {assetName: 'ProgressBar', assetSource: 'PrimeNG', assetType: 'progressbar', code: 'CD.PROGRESSBAR', description: 'ProgressBar is a process status indicator.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.PROGRESSBAR', id: '246a1c4a-0ad3-39e9-b5b9-40fec14753e1', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-progressbar.mjs', layer: 'definition', name: 'ProgressBar', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n60:SystemShellGraphNode:ComponentDefinition:Definition);
SET n60 += {assetName: 'ProgressSpinner', assetSource: 'PrimeNG', assetType: 'progressspinner', code: 'CD.PROGRESSSPINNER', description: 'ProgressSpinner is a process status indicator.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.PROGRESSSPINNER', id: '0d7369b3-2c0a-35cb-b9ce-221a3e4717d8', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-progressspinner.mjs', layer: 'definition', name: 'ProgressSpinner', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n61:SystemShellGraphNode:ComponentDefinition:Definition);
SET n61 += {assetName: 'RadioButton', assetSource: 'PrimeNG', assetType: 'radiobutton', code: 'CD.RADIOBUTTON', description: 'RadioButton is an extension to standard radio button element with theming.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.RADIOBUTTON', id: '8604bb10-6063-3177-8c06-da668e7a4089', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-radiobutton.mjs', layer: 'definition', name: 'RadioButton', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n62:SystemShellGraphNode:ComponentDefinition:Definition);
SET n62 += {assetName: 'Rating', assetSource: 'PrimeNG', assetType: 'rating', code: 'CD.RATING', description: 'Rating component is a star based selection input.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.RATING', id: 'e3783c44-1bd0-3c56-9611-b0c6065bafab', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-rating.mjs', layer: 'definition', name: 'Rating', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n63:SystemShellGraphNode:ComponentDefinition:Definition);
SET n63 += {assetName: 'Ripple', assetSource: 'PrimeNG', assetType: 'ripple', code: 'CD.RIPPLE', description: 'Ripple directive adds ripple effect to the host element.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.RIPPLE', id: '46c24f59-1e5e-3063-a52f-2cce26becc93', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-ripple.mjs', layer: 'definition', name: 'Ripple', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n64:SystemShellGraphNode:ComponentDefinition:Definition);
SET n64 += {assetName: 'Virtual Scroller', assetSource: 'PrimeNG', assetType: 'scroller', code: 'CD.SCROLLER', description: 'VirtualScroller is a performance-approach to handle huge data efficiently.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.SCROLLER', id: '6d66315e-ba67-35e2-95be-10dcaf7bac17', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-scroller.mjs', layer: 'definition', name: 'Virtual Scroller', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n65:SystemShellGraphNode:ComponentDefinition:Definition);
SET n65 += {assetName: 'Scroll Panel', assetSource: 'PrimeNG', assetType: 'scrollpanel', code: 'CD.SCROLLPANEL', description: 'ScrollPanel is a cross browser, lightweight and skinnable alternative to native browser scrollbar.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.SCROLLPANEL', id: 'bd368f30-8213-3f0b-8afb-08ff9ef3bbf6', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-scrollpanel.mjs', layer: 'definition', name: 'Scroll Panel', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n66:SystemShellGraphNode:ComponentDefinition:Definition);
SET n66 += {assetName: 'Scroll Top', assetSource: 'PrimeNG', assetType: 'scrolltop', code: 'CD.SCROLLTOP', description: 'ScrollTop gets displayed after a certain scroll position and used to navigates to the top of the page quickly.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.SCROLLTOP', id: 'a8be1419-941c-3eda-b5db-1594babb7a65', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-scrolltop.mjs', layer: 'definition', name: 'Scroll Top', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n67:SystemShellGraphNode:ComponentDefinition:Definition);
SET n67 += {assetName: 'Select', assetSource: 'PrimeNG', assetType: 'select', code: 'CD.SELECT', description: 'Select is used to choose an item from a collection of options.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.SELECT', id: 'fd6628d0-824d-37e8-9be9-36a21789ae01', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-select.mjs', layer: 'definition', name: 'Select', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n68:SystemShellGraphNode:ComponentDefinition:Definition);
SET n68 += {assetName: 'SelectButton', assetSource: 'PrimeNG', assetType: 'selectbutton', code: 'CD.SELECTBUTTON', description: 'SelectButton is used to choose single or multiple items from a list using buttons.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.SELECTBUTTON', id: 'c4b9a7ec-0132-3e82-9edf-73fad26b4aa2', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-selectbutton.mjs', layer: 'definition', name: 'SelectButton', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n69:SystemShellGraphNode:ComponentDefinition:Definition);
SET n69 += {assetName: 'Skeleton', assetSource: 'PrimeNG', assetType: 'skeleton', code: 'CD.SKELETON', description: 'Skeleton is a placeholder to display instead of the actual content.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.SKELETON', id: '50069122-bc0c-3834-baaf-8e2c7de0b46f', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-skeleton.mjs', layer: 'definition', name: 'Skeleton', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n70:SystemShellGraphNode:ComponentDefinition:Definition);
SET n70 += {assetName: 'Slider', assetSource: 'PrimeNG', assetType: 'slider', code: 'CD.SLIDER', description: 'Slider is a component to provide input with a drag handle.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.SLIDER', id: 'aa379f66-4088-357d-a057-d2728e741afe', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-slider.mjs', layer: 'definition', name: 'Slider', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n71:SystemShellGraphNode:ComponentDefinition:Definition);
SET n71 += {assetName: 'Speed Dial', assetSource: 'PrimeNG', assetType: 'speeddial', code: 'CD.SPEEDDIAL', description: 'SpeedDial is a floating button with a popup menu.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.SPEEDDIAL', id: 'd059c33e-0949-3f46-82ca-8cd88ae0df30', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-speeddial.mjs', layer: 'definition', name: 'Speed Dial', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n72:SystemShellGraphNode:ComponentDefinition:Definition);
SET n72 += {assetName: 'SplitButton', assetSource: 'PrimeNG', assetType: 'splitbutton', code: 'CD.SPLITBUTTON', description: 'SplitButton groups a set of commands in an overlay with a default action item.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.SPLITBUTTON', id: '69e42655-94bf-399b-a31c-b9312ced9f0f', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-splitbutton.mjs', layer: 'definition', name: 'SplitButton', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n73:SystemShellGraphNode:ComponentDefinition:Definition);
SET n73 += {assetName: 'Splitter', assetSource: 'PrimeNG', assetType: 'splitter', code: 'CD.SPLITTER', description: 'Splitter is utilized to separate and resize panels.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.SPLITTER', id: 'cf97852e-9c58-3ccb-81b3-25b521119be5', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-splitter.mjs', layer: 'definition', name: 'Splitter', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n74:SystemShellGraphNode:ComponentDefinition:Definition);
SET n74 += {assetName: 'Stepper', assetSource: 'PrimeNG', assetType: 'stepper', code: 'CD.STEPPER', description: 'The Stepper component displays a wizard-like workflow by guiding users through the multi-step progression.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.STEPPER', id: '062e97d4-af51-3965-bd31-1cbf080a00bd', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-stepper.mjs', layer: 'definition', name: 'Stepper', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n75:SystemShellGraphNode:ComponentDefinition:Definition);
SET n75 += {assetName: 'StyleClass', assetSource: 'PrimeNG', assetType: 'styleclass', code: 'CD.STYLECLASS', description: 'StyleClass manages css classes declaratively to during enter/leave animations or just to toggle classes on an element.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.STYLECLASS', id: '651fc284-87d1-385f-bc67-cf7b5f93f8f9', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-styleclass.mjs', layer: 'definition', name: 'StyleClass', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n76:SystemShellGraphNode:ComponentDefinition:Definition);
SET n76 += {assetName: 'Table', assetSource: 'PrimeNG', assetType: 'table', code: 'CD.TABLE', description: 'Table displays data in tabular format.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.TABLE', id: '096c3aa8-bef6-3d90-aa35-dfa6da953718', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-table.mjs', layer: 'definition', name: 'Table', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n77:SystemShellGraphNode:ComponentDefinition:Definition);
SET n77 += {assetName: 'Tabs', assetSource: 'PrimeNG', assetType: 'tabs', code: 'CD.TABS', description: 'Tabs is a container component to group content with tabs.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.TABS', id: '7751ab5e-c320-3bc8-bf5e-b5a1eaa2a3f9', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-tabs.mjs', layer: 'definition', name: 'Tabs', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n78:SystemShellGraphNode:ComponentDefinition:Definition);
SET n78 += {assetName: 'Tag', assetSource: 'PrimeNG', assetType: 'tag', code: 'CD.TAG', description: 'Tag component is used to categorize content.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.TAG', id: 'c61e12d9-eeeb-3da3-96b3-a9c4b15acf83', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-tag.mjs', layer: 'definition', name: 'Tag', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n79:SystemShellGraphNode:ComponentDefinition:Definition);
SET n79 += {assetName: 'Terminal', assetSource: 'PrimeNG', assetType: 'terminal', code: 'CD.TERMINAL', description: '', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.TERMINAL', id: '5479bdca-0a71-3c89-b577-8dbae968737a', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-terminal.mjs', layer: 'definition', name: 'Terminal', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n80:SystemShellGraphNode:ComponentDefinition:Definition);
SET n80 += {assetName: 'Textarea', assetSource: 'PrimeNG', assetType: 'textarea', code: 'CD.TEXTAREA', description: 'Textarea adds styling and autoResize functionality to standard textarea element.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.TEXTAREA', id: 'f3b5177a-5a81-3eab-bf69-06650de7550e', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-textarea.mjs', layer: 'definition', name: 'Textarea', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n81:SystemShellGraphNode:ComponentDefinition:Definition);
SET n81 += {assetName: 'TieredMenu', assetSource: 'PrimeNG', assetType: 'tieredmenu', code: 'CD.TIEREDMENU', description: 'TieredMenu displays submenus in nested overlays.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.TIEREDMENU', id: '03254c2f-13cb-371b-96f2-566cac632577', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-tieredmenu.mjs', layer: 'definition', name: 'TieredMenu', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n82:SystemShellGraphNode:ComponentDefinition:Definition);
SET n82 += {assetName: 'Timeline', assetSource: 'PrimeNG', assetType: 'timeline', code: 'CD.TIMELINE', description: 'Timeline visualizes a series of chained events.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.TIMELINE', id: '0fc29465-66b4-3fc9-9761-7ee1a05ba4ea', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-timeline.mjs', layer: 'definition', name: 'Timeline', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n83:SystemShellGraphNode:ComponentDefinition:Definition);
SET n83 += {assetName: 'Toast', assetSource: 'PrimeNG', assetType: 'toast', code: 'CD.TOAST', description: 'Toast is used to display messages in an overlay.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.TOAST', id: '96f95651-fa6f-3752-8bf9-6b00ef1922c5', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-toast.mjs', layer: 'definition', name: 'Toast', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n84:SystemShellGraphNode:ComponentDefinition:Definition);
SET n84 += {assetName: 'ToggleButton', assetSource: 'PrimeNG', assetType: 'togglebutton', code: 'CD.TOGGLEBUTTON', description: 'ToggleButton is used to select a boolean value using a button.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.TOGGLEBUTTON', id: '70fe87d8-9bdc-3639-a186-1a2ecdb3d92a', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-togglebutton.mjs', layer: 'definition', name: 'ToggleButton', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n85:SystemShellGraphNode:ComponentDefinition:Definition);
SET n85 += {assetName: 'ToggleSwitch', assetSource: 'PrimeNG', assetType: 'toggleswitch', code: 'CD.TOGGLESWITCH', description: 'ToggleSwitch is used to select a boolean value.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.TOGGLESWITCH', id: '97d32c7a-5b7d-331f-af0b-4ca311e17f8e', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-toggleswitch.mjs', layer: 'definition', name: 'ToggleSwitch', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n86:SystemShellGraphNode:ComponentDefinition:Definition);
SET n86 += {assetName: 'Toolbar', assetSource: 'PrimeNG', assetType: 'toolbar', code: 'CD.TOOLBAR', description: 'Toolbar is a grouping component for buttons and other content.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.TOOLBAR', id: '922fa413-2614-3a2b-aaed-03d44c8049b2', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-toolbar.mjs', layer: 'definition', name: 'Toolbar', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n87:SystemShellGraphNode:ComponentDefinition:Definition);
SET n87 += {assetName: 'Tooltip', assetSource: 'PrimeNG', assetType: 'tooltip', code: 'CD.TOOLTIP', description: 'Tooltip directive provides advisory information for a component. Tooltip is integrated within various PrimeNG components.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.TOOLTIP', id: 'b3f522d7-9040-3c80-a0d2-ae9de1593e2a', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-tooltip.mjs', layer: 'definition', name: 'Tooltip', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n88:SystemShellGraphNode:ComponentDefinition:Definition);
SET n88 += {assetName: 'Tree', assetSource: 'PrimeNG', assetType: 'tree', code: 'CD.TREE', description: 'Tree is used to display hierarchical data.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.TREE', id: '97aa87b2-0b16-3167-8058-2b1ed1949d19', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-tree.mjs', layer: 'definition', name: 'Tree', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n89:SystemShellGraphNode:ComponentDefinition:Definition);
SET n89 += {assetName: 'TreeSelect', assetSource: 'PrimeNG', assetType: 'treeselect', code: 'CD.TREESELECT', description: 'TreeSelect is a form component to choose from hierarchical data.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.TREESELECT', id: '6f61a601-35ab-3111-84cc-6eea4499f9a2', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-treeselect.mjs', layer: 'definition', name: 'TreeSelect', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n90:SystemShellGraphNode:ComponentDefinition:Definition);
SET n90 += {assetName: 'TreeTable', assetSource: 'PrimeNG', assetType: 'treetable', code: 'CD.TREETABLE', description: 'TreeTable is used to display hierarchical data in tabular format.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_COMPONENT_REGISTRY', hierarchyCode: 'CD.TREETABLE', id: '8236c19a-eca1-3b1c-8ec0-4d1903140fe2', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-treetable.mjs', layer: 'definition', name: 'TreeTable', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n91:SystemShellGraphNode:Application:Instance);
SET n91 += {backgroundImagePath: '', code: 'APP01', description: 'Top-level frontend application object that owns the shell structure and shared presentation hierarchy.', domain: 'frontend', family: 'Application', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'APP01', id: '8fbe2770-742b-341f-8a0e-1282bb24f137', layer: 'instance', name: 'ObjectsLogic', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n92:SystemShellGraphNode:Blocker:Instance);
SET n92 += {backgroundImagePath: '', blockerType: 'data', blockingEffect: 'redirect_outcome', code: 'BL01', description: 'The resolved tenant does not exist or has been deleted.', domain: 'business', family: 'Blocker', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'BL01', id: '08f04b9f-db24-3c10-9f9d-adeb232c1c35', layer: 'instance', name: 'TENANT_NOT_FOUND', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active'};

CREATE (n93:SystemShellGraphNode:Blocker:Instance);
SET n93 += {backgroundImagePath: '', blockerType: 'access', blockingEffect: 'prevent_execution', code: 'BL02', description: 'The tenant is suspended and access is blocked before login can continue.', domain: 'business', family: 'Blocker', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'BL02', id: '90499c22-af3d-33b3-9aa9-fdeddab8ec12', layer: 'instance', name: 'TENANT_SUSPENDED', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/03-Authenticate-User.bpmn', status: 'active'};

CREATE (n94:SystemShellGraphNode:Blocker:Instance);
SET n94 += {backgroundImagePath: '', blockerType: 'access', blockingEffect: 'prevent_execution', code: 'BL03', description: 'The tenant license is expired and normal sign-in is blocked.', domain: 'business', family: 'Blocker', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'BL03', id: 'a2404263-f7c7-3a6c-8416-76fe8fa1f8d6', layer: 'instance', name: 'TENANT_LICENSE_EXPIRED', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/03-Authenticate-User.bpmn', status: 'active'};

CREATE (n95:SystemShellGraphNode:Blocker:Instance);
SET n95 += {backgroundImagePath: '', blockerType: 'policy', blockingEffect: 'prevent_execution', code: 'BL04', description: 'The tenant access state blocks normal sign-in within the login screen.', domain: 'business', family: 'Blocker', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'BL04', id: '96bfaeaa-f969-3c6b-8956-e89628742f9a', layer: 'instance', name: 'TENANT_ACCESS_BLOCKED', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/03-Authenticate-User.bpmn', status: 'active'};

CREATE (n96:SystemShellGraphNode:Blocker:Instance);
SET n96 += {backgroundImagePath: '', blockerType: 'business', blockingEffect: 'prevent_execution', code: 'BL05', description: 'No active authentication methods are available for the resolved tenant.', domain: 'business', family: 'Blocker', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'BL05', id: 'f04a7be9-ed67-337b-9584-b2d8f36a731a', layer: 'instance', name: 'NO_AUTH_METHODS', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/03-Authenticate-User.bpmn', status: 'active'};

CREATE (n97:SystemShellGraphNode:Blocker:Instance);
SET n97 += {backgroundImagePath: '', blockerType: 'business', blockingEffect: 'prevent_completion', code: 'BL06', description: 'Primary authentication failed because the supplied credentials were invalid.', domain: 'business', family: 'Blocker', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'BL06', id: 'ddac67d6-00d4-3095-8ad1-f8a0b5b47be4', layer: 'instance', name: 'INVALID_CREDENTIALS', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/03-Authenticate-User.bpmn', status: 'active'};

CREATE (n98:SystemShellGraphNode:Blocker:Instance);
SET n98 += {backgroundImagePath: '', blockerType: 'technical', blockingEffect: 'prevent_completion', code: 'BL07', description: 'The external authentication provider did not return a successful login outcome.', domain: 'business', family: 'Blocker', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'BL07', id: '5c6ccf6c-4117-31db-a678-196c1983bc8b', layer: 'instance', name: 'AUTH_PROVIDER_FAILURE', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/03-Authenticate-User.bpmn', status: 'active'};

CREATE (n99:SystemShellGraphNode:Blocker:Instance);
SET n99 += {backgroundImagePath: '', blockerType: 'business', blockingEffect: 'prevent_completion', code: 'BL08', description: 'The MFA challenge failed because the supplied verification code was invalid.', domain: 'business', family: 'Blocker', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'BL08', id: '67fc2335-493a-3699-8a12-4ad893f8c6c4', layer: 'instance', name: 'INVALID_MFA_CODE', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/03-Authenticate-User.bpmn', status: 'active'};

CREATE (n100:SystemShellGraphNode:BusinessRule:Instance);
SET n100 += {backgroundImagePath: '', code: 'BR01', conditionExpression: 'tenant_selected = true', description: 'Tenant resolution must complete before authentication options can be determined.', domain: 'business', executionEffect: 'require_step', family: 'BusinessRule', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'BR01', id: 'eef5557f-0087-3cf9-93b7-5942b1ea3cb8', layer: 'instance', name: 'Tenant Resolution Required', ruleScope: 'journey_step', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active'};

CREATE (n101:SystemShellGraphNode:BusinessRule:Instance);
SET n101 += {backgroundImagePath: '', code: 'BR02', conditionExpression: 'tenant_exists = true AND tenant_access_state = \'allowed\'', description: 'Tenant-level access state must allow the login journey to continue.', domain: 'business', executionEffect: 'raise_blocker', family: 'BusinessRule', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'BR02', id: '22b5e394-c073-3a08-a1b6-060fa8947921', layer: 'instance', name: 'Tenant Access State Must Permit Authentication', ruleScope: 'journey_step', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active'};

CREATE (n102:SystemShellGraphNode:BusinessRule:Instance);
SET n102 += {backgroundImagePath: '', code: 'BR03', conditionExpression: 'auth_methods_count > 0', description: 'Provider selection can proceed only when at least one authentication method is available.', domain: 'business', executionEffect: 'raise_blocker', family: 'BusinessRule', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'BR03', id: 'b6f4d295-c504-3b56-addf-4ef1cbea193f', layer: 'instance', name: 'Available Authentication Methods Required', ruleScope: 'journey_step', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active'};

CREATE (n103:SystemShellGraphNode:BusinessRule:Instance);
SET n103 += {backgroundImagePath: '', code: 'BR04', conditionExpression: 'selected_provider_type IN [\'credential\', \'redirect\']', description: 'Credential and redirect providers follow different authentication flows.', domain: 'business', executionEffect: 'allow_step', family: 'BusinessRule', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'BR04', id: '1ea8bfee-9fb5-3c1b-a906-b9959cd53131', layer: 'instance', name: 'Selected Provider Type Governs Sign-In Path', ruleScope: 'journey_step', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active'};

CREATE (n104:SystemShellGraphNode:BusinessRule:Instance);
SET n104 += {backgroundImagePath: '', code: 'BR05', conditionExpression: 'primary_authentication_succeeded = true AND mfa_required = true', description: 'A successful primary sign-in may still require the MFA screen.', domain: 'business', executionEffect: 'require_step', family: 'BusinessRule', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'BR05', id: '815dd396-f4cb-3861-8ad8-72d687ccafb2', layer: 'instance', name: 'MFA Required After Successful Primary Authentication', ruleScope: 'journey_step', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active'};

CREATE (n105:SystemShellGraphNode:BusinessRule:Instance);
SET n105 += {backgroundImagePath: '', code: 'BR06', conditionExpression: 'authentication_succeeded = true', description: 'A fully authenticated user is redirected into the destination application screen and landing page.', domain: 'business', executionEffect: 'redirect_outcome', family: 'BusinessRule', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'BR06', id: '8e39b334-8450-3d08-b8bd-4be03c5846d8', layer: 'instance', name: 'Successful Authentication Redirects User To Landing', ruleScope: 'journey_step', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active'};

CREATE (n106:SystemShellGraphNode:Instance:Journey);
SET n106 += {backgroundImagePath: '', code: 'J01', description: 'Authenticate user and authorize access to the landing page.', domain: 'business', family: 'Journey', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'J01', id: '97d00136-ff5b-3ffd-8e73-1b35f08ed403', layer: 'instance', name: 'Authenticate User', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active'};

CREATE (n107:SystemShellGraphNode:Instance:JourneyStep);
SET n107 += {backgroundImagePath: '', code: 'J01.JS01', description: 'Open the login screen and expose the baseline sign-in structure.', domain: 'business', executionMethod: 'mandatory', family: 'JourneyStep', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'J01.JS01', id: '8b146bc7-35a4-3e27-bf94-f8e0cd1aa29a', layer: 'instance', name: 'Open Login Screen', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active', stepOrder: 1};

CREATE (n108:SystemShellGraphNode:Instance:JourneyStep);
SET n108 += {backgroundImagePath: '', code: 'J01.JS02', description: 'Capture the required tenant before provider resolution proceeds.', domain: 'business', executionMethod: 'mandatory', family: 'JourneyStep', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'J01.JS02', id: 'f0386650-7a67-3e9a-91af-665085d5d2fd', layer: 'instance', name: 'Select Tenant', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active', stepOrder: 2};

CREATE (n109:SystemShellGraphNode:Instance:JourneyStep);
SET n109 += {backgroundImagePath: '', code: 'J01.JS03', description: 'Resolve available authentication methods and tenant access-state blockers.', domain: 'business', executionMethod: 'mandatory', family: 'JourneyStep', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'J01.JS03', id: '28700589-3a29-3568-9906-8c6f2fd412d5', layer: 'instance', name: 'Resolve Tenant Authentication Methods', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active', stepOrder: 3};

CREATE (n110:SystemShellGraphNode:Instance:JourneyStep);
SET n110 += {backgroundImagePath: '', code: 'J01.JS04', description: 'Expose and select an available provider in the login screen.', domain: 'business', executionMethod: 'mandatory', family: 'JourneyStep', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'J01.JS04', id: 'cdab22ce-ca14-32d5-9d83-806960af72ff', layer: 'instance', name: 'Select Authentication Provider', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active', stepOrder: 4};

CREATE (n111:SystemShellGraphNode:Instance:JourneyStep);
SET n111 += {backgroundImagePath: '', code: 'J01.JS05', description: 'Submit credentials or continue through the provider redirect path.', domain: 'business', executionMethod: 'mandatory', family: 'JourneyStep', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'J01.JS05', id: '0fccffd5-b8e8-3b2e-b3da-e2ccfd0f27fe', layer: 'instance', name: 'Submit Credentials Or Redirect Sign-In', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/03-Authenticate-User.bpmn', status: 'active', stepOrder: 5};

CREATE (n112:SystemShellGraphNode:Instance:JourneyStep);
SET n112 += {backgroundImagePath: '', code: 'J01.JS06', description: 'Open the MFA screen when primary authentication succeeds and MFA is required.', domain: 'business', executionMethod: 'conditional', family: 'JourneyStep', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'J01.JS06', id: '51c1d403-d37b-329a-b167-bc38088f289d', layer: 'instance', name: 'Open MFA Screen', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/03-Authenticate-User.bpmn', status: 'active', stepOrder: 6};

CREATE (n113:SystemShellGraphNode:Instance:JourneyStep);
SET n113 += {backgroundImagePath: '', code: 'J01.JS07', description: 'Capture and validate the MFA code before issuing the final redirect.', domain: 'business', executionMethod: 'conditional', family: 'JourneyStep', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'J01.JS07', id: '3e19d6e7-019e-3d12-9adc-f75c54f3e22d', layer: 'instance', name: 'Verify MFA Challenge', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/03-Authenticate-User.bpmn', status: 'active', stepOrder: 7};

CREATE (n114:SystemShellGraphNode:Instance:JourneyStep);
SET n114 += {backgroundImagePath: '', code: 'J01.JS08', description: 'Redirect the authenticated user to the target application screen and landing page.', domain: 'business', executionMethod: 'conditional', family: 'JourneyStep', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'J01.JS08', id: 'dbd6a72e-7dc9-376a-9c6d-acafdd74ab77', layer: 'instance', name: 'Redirect To Landing Page', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/03-Authenticate-User.bpmn', status: 'active', stepOrder: 8};

CREATE (n115:SystemShellGraphNode:Instance:Persona);
SET n115 += {backgroundImagePath: '', code: 'PER.USER', description: 'Primary login actor.', domain: 'business', family: 'Persona', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'PER.USER', id: '620ec739-bcd3-34b3-9d35-7d6b57813be9', layer: 'instance', name: 'User', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active'};

CREATE (n116:SystemShellGraphNode:Instance:Shell);
SET n116 += {backgroundColorStyle: 'var(--tp-primary)', backgroundImagePath: '', backgroundPatternKey: 'emsist-shell-pattern', backgroundPatternOpacity: 0.13, backgroundType: 'color_pattern', code: 'SHL01', description: 'Login shell that owns the dedicated header, main, and footer containers for unauthenticated screens.', domain: 'frontend', family: 'Shell', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '30b319ef-2726-363b-bce2-1f0c709d3ae8', hierarchyCode: 'SHL01', id: '55a0d6a0-d51c-3708-b085-da27323b78d9', layer: 'instance', name: 'login-shell', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n117:SystemShellGraphNode:Instance:Screen);
SET n117 += {backgroundImagePath: '', code: 'SHL01.SCN01', description: 'Primary unauthenticated login screen.', domain: 'frontend', family: 'Screen', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'af8c6be1-dadf-3c80-8be3-3bde46257303', hierarchyCode: 'SHL01.SCN01', id: 'd038ec86-c3e8-3a3c-831e-6f33bb56e5c9', layer: 'instance', name: 'Login Screen', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n118:SystemShellGraphNode:Instance:Section);
SET n118 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Top welcome copy container for the login screen.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'ddf4348e-99ad-38d1-a34c-7438791ebc9e', hierarchyCode: 'SHL01.SCN01.SEC01', id: 'fdc776b7-6153-357f-b544-25bd1e0e3693', layer: 'instance', name: 'Welcome Container', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n119:SystemShellGraphNode:Element:Instance);
SET n119 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC01.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'title', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'ac4ed163-8e3e-3eac-a217-654abca47823', hierarchyCode: 'SHL01.SCN01.SEC01.ELT01', id: '2d056f21-881e-3600-8f57-eac7b506dec3', layer: 'instance', name: 'Screen Title', primeComponent: 'Message', renderMode: 'static', semanticLevel: 'H1', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n120:SystemShellGraphNode:ComponentInstance:Instance);
SET n120 += {assetName: 'Message', assetType: 'message', code: 'SHL01.SCN01.SEC01.ELT01.CP01.MESSAGE', configurationJson: '{}', definitionCode: 'CD.MESSAGE', description: 'Editable instance configuration for the Message component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '2ba23981-f9d8-3b10-ac0a-8ce96947170e', hierarchyCode: 'SHL01.SCN01.SEC01.ELT01.CP01.MESSAGE', id: '6689bdac-53fc-34d4-b4c1-833b9f81a682', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-message.mjs', layer: 'instance', name: 'Screen Title Message Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n121:SystemShellGraphNode:Element:Instance);
SET n121 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC01.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'title', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '5fc08ae6-67bc-3def-b39b-775325280e9c', hierarchyCode: 'SHL01.SCN01.SEC01.ELT02', id: '4114e985-f34c-3d58-8020-f80c236cff8f', layer: 'instance', name: 'Screen Subtitle', primeComponent: 'Message', renderMode: 'static', semanticLevel: 'H2', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n122:SystemShellGraphNode:ComponentInstance:Instance);
SET n122 += {assetName: 'Message', assetType: 'message', code: 'SHL01.SCN01.SEC01.ELT02.CP01.MESSAGE', configurationJson: '{}', definitionCode: 'CD.MESSAGE', description: 'Editable instance configuration for the Message component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '9a9f6bbf-39ba-30a5-9f93-48e35ec44861', hierarchyCode: 'SHL01.SCN01.SEC01.ELT02.CP01.MESSAGE', id: '66f58b75-c0ab-371a-8f19-1e10e5d159a3', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-message.mjs', layer: 'instance', name: 'Screen Subtitle Message Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n123:SystemShellGraphNode:Instance:Section);
SET n123 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Left-side brand logo container for the login screen.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '67d5c5fa-ec27-3e63-869f-a3715a564ecb', hierarchyCode: 'SHL01.SCN01.SEC02', id: 'be5f6081-80ba-3155-be12-8258d3d91cdd', layer: 'instance', name: 'Logo Container', renderMode: 'static', repeatable: false, sectionType: 'logo', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n124:SystemShellGraphNode:Element:Instance);
SET n124 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC02.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'visual', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '0eb4c6dc-8a30-3178-85f1-3149e00fe959', hierarchyCode: 'SHL01.SCN01.SEC02.ELT01', id: '31a05ca7-833d-3802-9b56-74115726a12b', layer: 'instance', name: 'SVG Logo Renderer', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n125:SystemShellGraphNode:Instance:Section);
SET n125 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03', controlSource: 'none', defaultState: 'visible', description: 'Primary login card surface inside SHL01.SCN01.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'd6eb7a64-6161-30e4-88ab-ad2401c53c61', hierarchyCode: 'SHL01.SCN01.SEC03', id: 'acbfbd39-c1c4-3d9b-a1d1-23b1d296ad28', layer: 'instance', name: 'Login Card', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n126:SystemShellGraphNode:Instance:Section);
SET n126 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Tenant selection step region.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '68739f97-0d6f-3df6-a141-d2fe4450b227', hierarchyCode: 'SHL01.SCN01.SEC03.SEC01', id: 'ee3243ad-d3b2-3e47-9fd4-db67761bda7d', layer: 'instance', name: 'Tenant Selection Step Section', renderMode: 'static', repeatable: false, sectionType: 'step', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n127:SystemShellGraphNode:Instance:Section);
SET n127 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC01.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Tenant step header group.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'b6150cca-5294-325d-910a-ae840d871271', hierarchyCode: 'SHL01.SCN01.SEC03.SEC01.SEC01', id: '150e85fc-518e-35e5-b3c6-77439e7eb2f8', layer: 'instance', name: 'Tenant Selection Header Section', renderMode: 'static', repeatable: false, sectionType: 'header', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n128:SystemShellGraphNode:Element:Instance);
SET n128 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC01.SEC01.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'indicator', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '7bd9d859-1845-369b-8283-e2d9241fe022', hierarchyCode: 'SHL01.SCN01.SEC03.SEC01.SEC01.ELT01', id: 'b3b4044a-976a-3d19-af02-79eef68283fb', layer: 'instance', name: 'Tenant Selection Step Indicator', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n129:SystemShellGraphNode:Element:Instance);
SET n129 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC01.SEC01.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'title', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '0e9c1ba4-4b1f-3d06-9aa6-b4d0acab87e1', hierarchyCode: 'SHL01.SCN01.SEC03.SEC01.SEC01.ELT02', id: '2b89cd6b-7111-3243-84f2-e3bd5b771f96', layer: 'instance', name: 'Tenant Selection Step Title', renderMode: 'static', semanticLevel: 'H2', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n130:SystemShellGraphNode:Instance:Section);
SET n130 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC01.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Tenant selection controls.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '16c892d8-47e3-3e46-a8b5-275881fa21d2', hierarchyCode: 'SHL01.SCN01.SEC03.SEC01.SEC02', id: 'a3f6d88c-ab59-313a-928e-0e577af9dd20', layer: 'instance', name: 'Tenant Selection Content Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n131:SystemShellGraphNode:Element:Instance);
SET n131 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC01.SEC02.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '8ade6f9f-cc33-3572-bd2d-0d511772460e', hierarchyCode: 'SHL01.SCN01.SEC03.SEC01.SEC02.ELT01', id: 'fb012152-2398-31d2-a809-026d7fd1b341', layer: 'instance', name: 'Tenant Registry Searchable Dropdown', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-space-*']};

CREATE (n132:SystemShellGraphNode:Element:Instance);
SET n132 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC01.SEC02.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'checkbox', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '5961dd59-0d2f-3356-9c9e-bd13d2291495', hierarchyCode: 'SHL01.SCN01.SEC03.SEC01.SEC02.ELT02', id: 'f2dc8398-aefd-370e-9337-de3b667a88ec', layer: 'instance', name: 'Remember Tenant Selection Checkbox', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-space-*']};

CREATE (n133:SystemShellGraphNode:Instance:Section);
SET n133 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Authentication method step region.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'c6d855d5-ea49-3787-bd6e-34848aff0b74', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02', id: '445d1a8e-7a67-3995-8f99-08acdd144ab1', layer: 'instance', name: 'Authentication Methods Step Section', renderMode: 'static', repeatable: false, sectionType: 'step', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n134:SystemShellGraphNode:Instance:Section);
SET n134 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Authentication step header group.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '2b6acdd5-9b94-3c70-956a-d8b7ff809964', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC01', id: 'c32ca4f9-57dc-3bdf-8943-a98b1cf3afa6', layer: 'instance', name: 'Authentication Methods Header Section', renderMode: 'static', repeatable: false, sectionType: 'header', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n135:SystemShellGraphNode:Element:Instance);
SET n135 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC01.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'indicator', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'a10f7c70-7c58-3772-9494-0e3c6bba707a', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC01.ELT01', id: 'd4d3a07b-d822-3e7f-a7d8-9fa2fd4c744b', layer: 'instance', name: 'Authentication Methods Step Indicator', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n136:SystemShellGraphNode:Element:Instance);
SET n136 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC01.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'title', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '152473d9-ff96-3dcb-ab79-e203f15226be', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC01.ELT02', id: '46c616dc-def9-3c88-9e08-803fd4d0045e', layer: 'instance', name: 'Authentication Methods Step Title', renderMode: 'static', semanticLevel: 'H2', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n137:SystemShellGraphNode:Instance:Section);
SET n137 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC02', controlSource: 'validation_rule_set', defaultState: 'hidden', description: 'Status and feedback region.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '646a6718-d7a3-3fb7-b82d-513ddd41b3a4', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC02', id: '15eb55ed-94b0-30f7-a6e8-3d08c8f972c3', layer: 'instance', name: 'Status Message Section', renderMode: 'conditional', repeatable: false, sectionType: 'message', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n138:SystemShellGraphNode:Element:Instance);
SET n138 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC02.ELT01', controlSource: 'validation_rule_set', defaultState: 'hidden', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'banner', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '9b42f5b5-4970-368f-8937-b1f023b8da43', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC02.ELT01', id: 'd85b7a1e-c1b8-3fbc-81df-5a579b7926b5', layer: 'instance', name: 'Status Banner', renderMode: 'conditional', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n139:SystemShellGraphNode:Instance:Section);
SET n139 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03', controlSource: 'none', defaultState: 'visible', description: 'Provider selection and no-auth outcomes.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '36463e81-5df6-3df3-baef-2fbd860bd70f', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03', id: 'b7f1ede6-ab67-33f1-a29a-04ecdcf90b90', layer: 'instance', name: 'Provider Selection Section', renderMode: 'static', repeatable: false, sectionType: 'provider', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n140:SystemShellGraphNode:Instance:Section);
SET n140 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC01', controlSource: 'validation_rule_set', defaultState: 'hidden', description: 'No-auth outcome container.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '57f25865-514b-377b-9ce8-0439efa9e372', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC01', id: 'c299d0b3-ecea-31de-9cb7-96b4e74278fa', layer: 'instance', name: 'No Auth Section', renderMode: 'conditional', repeatable: false, sectionType: 'outcome', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n141:SystemShellGraphNode:Element:Instance);
SET n141 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC01.ELT01', controlSource: 'validation_rule_set', defaultState: 'hidden', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'title', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'f4423225-a18f-3260-9131-705c3c84a960', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC01.ELT01', id: 'b0be738d-fbd3-3735-b693-eeb567fd464c', layer: 'instance', name: 'No Auth Title', renderMode: 'conditional', semanticLevel: 'H4', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n142:SystemShellGraphNode:Element:Instance);
SET n142 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC01.ELT02', controlSource: 'validation_rule_set', defaultState: 'hidden', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'text', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '351a5670-b3d3-3d82-afa2-deb7084e8378', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC01.ELT02', id: 'dae9a298-0909-393c-8a3f-715fbc40a653', layer: 'instance', name: 'No Auth Description', renderMode: 'conditional', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n143:SystemShellGraphNode:Instance:Section);
SET n143 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Repeatable provider option container.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'db864978-f938-380d-abd3-75e557f5a711', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02', id: '1350326a-4410-35d8-9da8-4a3d7750a271', layer: 'instance', name: 'Auth Provider Section', renderMode: 'static', repeatable: true, sectionType: 'provider', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n144:SystemShellGraphNode:Instance:Section);
SET n144 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Provider header row.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'd25f127f-d00f-36ef-9c0f-96554ccddecf', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01', id: 'c35085f3-a7fd-3633-ad67-834abf5e3388', layer: 'instance', name: 'Provider Header Section', renderMode: 'static', repeatable: false, sectionType: 'header', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n145:SystemShellGraphNode:Element:Instance);
SET n145 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'visual', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '853ca26e-5ec9-30e4-a72c-42b804031c0a', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT01', id: 'faad0d86-8ab4-39b8-a59a-a1967af20dce', layer: 'instance', name: 'Auth Logo', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-primary-*', '--tp-space-*']};

CREATE (n146:SystemShellGraphNode:Element:Instance);
SET n146 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '05d98f1f-b502-3c58-84aa-3aaf7abe3fe3', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT02', id: '8baeaf4b-dfba-363b-8418-f40299c6ce44', layer: 'instance', name: 'Auth Method Name', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n147:SystemShellGraphNode:Element:Instance);
SET n147 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT03', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '59708f76-0ae3-38c0-b387-bde7955191e6', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT03', id: '132de7db-bb9b-3fb4-97aa-d20950f1fba7', layer: 'instance', name: 'Remember Me Selection', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-space-*']};

CREATE (n148:SystemShellGraphNode:Element:Instance);
SET n148 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT04', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'button', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'f2b85915-fb7f-3788-8aea-8558ed2a6abf', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT04', id: 'e39ad09e-9942-37b8-8195-086ecce1416a', layer: 'instance', name: 'Expand Action', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-space-*']};

CREATE (n149:SystemShellGraphNode:Instance:Section);
SET n149 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Provider-specific variant body.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '71013af8-2de4-3091-b51a-990ceefac9e3', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02', id: '50334901-3bd2-3955-96c1-4ea8ec7e72c1', layer: 'instance', name: 'Provider Variant Section', renderMode: 'static', repeatable: false, sectionType: 'provider', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n150:SystemShellGraphNode:Instance:Section);
SET n150 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Credential username field group.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '3ffc6125-16bf-33bc-91fb-b339decec959', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01', id: 'a6ea76b7-bca1-3b12-9033-f11c95f59910', layer: 'instance', name: 'User Name Field Section', renderMode: 'static', repeatable: false, sectionType: 'field', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n151:SystemShellGraphNode:Element:Instance);
SET n151 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01.ELT01', controlSource: 'validation_rule_set', defaultState: 'hidden', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '3fd46237-a719-3435-b294-44bb34cdedb8', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01.ELT01', id: '30ae0a4b-683e-3ca5-94fc-2f8cbd6ff7ec', layer: 'instance', name: 'User Name Label', renderMode: 'conditional', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n152:SystemShellGraphNode:Element:Instance);
SET n152 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01.ELT02', controlSource: 'validation_rule_set', defaultState: 'hidden', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'input', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'f5a7e6fb-c455-3ba4-b648-16c3fadcb063', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01.ELT02', id: '936b220d-4e3b-391a-aaf4-6ed97497990f', layer: 'instance', name: 'User Name Input', renderMode: 'conditional', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-space-*']};

CREATE (n153:SystemShellGraphNode:Instance:Section);
SET n153 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Credential password field group.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '601811e1-9b6b-3a90-82e8-1f8515898a39', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02', id: '6d13b429-6f5f-3c48-a5d5-61f277bcd0d9', layer: 'instance', name: 'Password Field Section', renderMode: 'static', repeatable: false, sectionType: 'field', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n154:SystemShellGraphNode:Element:Instance);
SET n154 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02.ELT01', controlSource: 'validation_rule_set', defaultState: 'hidden', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'cd888a00-e5ca-397b-9d7e-81802bab759b', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02.ELT01', id: 'b6de6ccd-bb40-3f5e-bd03-cd23801d0ddb', layer: 'instance', name: 'Password Label', renderMode: 'conditional', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n155:SystemShellGraphNode:Element:Instance);
SET n155 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02.ELT02', controlSource: 'validation_rule_set', defaultState: 'hidden', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'input', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'bbd35762-4d76-3f38-abba-e2209d62d49b', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02.ELT02', id: '30161d1b-9094-303b-b295-71a7c026095f', layer: 'instance', name: 'Password Input', renderMode: 'conditional', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-space-*']};

CREATE (n156:SystemShellGraphNode:Element:Instance);
SET n156 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02.ELT03', controlSource: 'validation_rule_set', defaultState: 'hidden', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'toggle', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'f2eb141c-19ef-35fe-b359-8fc278f0d34e', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02.ELT03', id: '9099ccd9-9c88-374f-8ca1-0fff75d72900', layer: 'instance', name: 'Password Visibility Toggle', renderMode: 'conditional', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-space-*']};

CREATE (n157:SystemShellGraphNode:Instance:Section);
SET n157 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC03', controlSource: 'none', defaultState: 'visible', description: 'Provider submit action group.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '15016a1b-f5ac-3b95-a234-18442bb48510', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC03', id: '4da1b5f0-d950-35e1-b72c-6df55daa10a4', layer: 'instance', name: 'Sign In Action Section', renderMode: 'static', repeatable: false, sectionType: 'action_bar', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n158:SystemShellGraphNode:Element:Instance);
SET n158 += {backgroundImagePath: '', code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC03.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'button', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '29582056-3ff6-33e0-a29f-6f67b3b6232d', hierarchyCode: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC03.ELT01', id: '3c177173-0c4d-3ddb-ae90-944be8c915f0', layer: 'instance', name: 'Sign In Action', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-space-*']};

CREATE (n159:SystemShellGraphNode:Instance:ValidationRuleSet);
SET n159 += {backgroundImagePath: '', code: 'SHL01.SCN01.VRS01', description: 'Controls runtime UI state inside the login screen.', domain: 'frontend', family: 'ValidationRuleSet', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'SHL01.SCN01.VRS01', id: '153ea17e-d516-35ac-a931-5f43096a9aeb', layer: 'instance', name: 'Login Validation Rule Set', ruleSetScope: 'screen', ruleSetType: 'screen_runtime', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active'};

CREATE (n160:SystemShellGraphNode:Instance:ValidationRule);
SET n160 += {actionType: 'show', backgroundImagePath: '', code: 'SHL01.SCN01.VRS01.R01', conditionExpression: 'auth_methods_count = 0', description: 'Shows the No Auth section when no active providers are available.', domain: 'frontend', family: 'ValidationRule', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'SHL01.SCN01.VRS01.R01', id: 'f586f4d5-fef1-3d8b-afb2-44c12636c52d', layer: 'instance', name: 'Show No Auth Section', priority: 10, sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active', stopProcessing: false};

CREATE (n161:SystemShellGraphNode:Instance:ValidationRule);
SET n161 += {actionType: 'show', backgroundImagePath: '', code: 'SHL01.SCN01.VRS01.R02', conditionExpression: 'tenant_access_blocked = true', description: 'Shows the blocked-state banner when tenant access conditions block login.', domain: 'frontend', family: 'ValidationRule', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'SHL01.SCN01.VRS01.R02', id: 'd77207b1-0bae-3d4e-9f33-f79b7b782e61', layer: 'instance', name: 'Show Tenant Access-State Banner', priority: 20, sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active', stopProcessing: false};

CREATE (n162:SystemShellGraphNode:Instance:ValidationRule);
SET n162 += {actionType: 'show', backgroundImagePath: '', code: 'SHL01.SCN01.VRS01.R03', conditionExpression: 'credentials_valid = false', description: 'Shows the invalid-credentials feedback banner inside the login screen.', domain: 'frontend', family: 'ValidationRule', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'SHL01.SCN01.VRS01.R03', id: 'dd0eef57-2bb2-393c-9c36-f6932f494891', layer: 'instance', name: 'Show Invalid Credentials Banner', priority: 30, sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active', stopProcessing: false};

CREATE (n163:SystemShellGraphNode:Instance:ValidationRule);
SET n163 += {actionType: 'show', backgroundImagePath: '', code: 'SHL01.SCN01.VRS01.R04', conditionExpression: 'selected_provider_type = \'credential\'', description: 'Shows username and password fields when the selected provider requires credentials.', domain: 'frontend', family: 'ValidationRule', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'SHL01.SCN01.VRS01.R04', id: '36858f44-d2a9-314c-b760-d042e77c5028', layer: 'instance', name: 'Reveal Credential Inputs For Credential Provider', priority: 40, sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active', stopProcessing: false};

CREATE (n164:SystemShellGraphNode:Instance:ValidationRule);
SET n164 += {actionType: 'hide', backgroundImagePath: '', code: 'SHL01.SCN01.VRS01.R05', conditionExpression: 'selected_provider_type = \'redirect\'', description: 'Hides credential fields and keeps only the submit action for redirect providers.', domain: 'frontend', family: 'ValidationRule', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'SHL01.SCN01.VRS01.R05', id: '95a7d0f9-5938-3898-a2c2-8792e056ef98', layer: 'instance', name: 'Collapse Credential Inputs For Redirect Provider', priority: 50, sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active', stopProcessing: false};

CREATE (n165:SystemShellGraphNode:Instance:Screen);
SET n165 += {backgroundImagePath: '', code: 'SHL01.SCN02', description: 'Step-up verification screen for MFA challenge entry.', domain: 'frontend', family: 'Screen', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'dd558933-1dc6-3034-a585-39d51cb44c26', hierarchyCode: 'SHL01.SCN02', id: 'e42b4fd6-e4b6-33e4-9043-52b976a03524', layer: 'instance', name: 'MFA Screen', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n166:SystemShellGraphNode:Instance:Section);
SET n166 += {backgroundImagePath: '', code: 'SHL01.SCN02.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Primary MFA modal container.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'ab470bef-bd2f-382d-9380-adb8cd9b9613', hierarchyCode: 'SHL01.SCN02.SEC02', id: 'cfbe7867-763a-342b-a36e-ac1137361d9c', layer: 'instance', name: 'Verification Modal Section', renderMode: 'static', repeatable: false, sectionType: 'modal', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n167:SystemShellGraphNode:Instance:Section);
SET n167 += {backgroundImagePath: '', code: 'SHL01.SCN02.SEC02.SEC01', controlSource: 'none', defaultState: 'visible', description: 'MFA header group.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '6966db9a-1a52-3547-8217-1108d9a3791a', hierarchyCode: 'SHL01.SCN02.SEC02.SEC01', id: 'cafd103d-b3e3-3b82-b9fd-e1cb63892dae', layer: 'instance', name: 'Verification Header Section', renderMode: 'static', repeatable: false, sectionType: 'header', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n168:SystemShellGraphNode:Element:Instance);
SET n168 += {backgroundImagePath: '', code: 'SHL01.SCN02.SEC02.SEC01.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'title', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'f32bf719-6e3a-3f08-95b7-fc9fea552173', hierarchyCode: 'SHL01.SCN02.SEC02.SEC01.ELT01', id: '75c26a30-e25e-3993-afd3-2af1996c5ca3', layer: 'instance', name: 'Verification Title', renderMode: 'static', semanticLevel: 'H2', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n169:SystemShellGraphNode:Element:Instance);
SET n169 += {backgroundImagePath: '', code: 'SHL01.SCN02.SEC02.SEC01.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'text', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'e51c3474-65f4-3fe9-89dd-3e6912d8acf0', hierarchyCode: 'SHL01.SCN02.SEC02.SEC01.ELT02', id: 'd62b69e8-e56d-3e5d-9af6-e76cbb5f5804', layer: 'instance', name: 'Verification Description', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n170:SystemShellGraphNode:Instance:Section);
SET n170 += {backgroundImagePath: '', code: 'SHL01.SCN02.SEC02.SEC02', controlSource: 'validation_rule_set', defaultState: 'hidden', description: 'MFA status-message region.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '5a71d3a7-8551-356b-8bec-0c04263cfa0a', hierarchyCode: 'SHL01.SCN02.SEC02.SEC02', id: '23d1b127-0408-34d5-afee-e7935b62b69e', layer: 'instance', name: 'Status Message Section', renderMode: 'conditional', repeatable: false, sectionType: 'message', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n171:SystemShellGraphNode:Element:Instance);
SET n171 += {backgroundImagePath: '', code: 'SHL01.SCN02.SEC02.SEC02.ELT01', controlSource: 'validation_rule_set', defaultState: 'hidden', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'banner', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'dbdec184-8bff-3c44-b7fc-4db8863ed3b5', hierarchyCode: 'SHL01.SCN02.SEC02.SEC02.ELT01', id: 'b769d400-86fa-35f5-9233-8c1c0ce9a620', layer: 'instance', name: 'Status Banner', renderMode: 'conditional', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n172:SystemShellGraphNode:Instance:Section);
SET n172 += {backgroundImagePath: '', code: 'SHL01.SCN02.SEC02.SEC03', controlSource: 'none', defaultState: 'visible', description: 'OTP input container backed by the registered InputOtp asset.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'e4e31aa0-fbb6-3177-839f-b9c9ba99c912', hierarchyCode: 'SHL01.SCN02.SEC02.SEC03', id: '66361bc2-cc9e-3c45-9826-a7ea9a54556c', layer: 'instance', name: 'Verification Code Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n173:SystemShellGraphNode:Element:Instance);
SET n173 += {backgroundImagePath: '', code: 'SHL01.SCN02.SEC02.SEC03.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'input', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '05684b84-4215-3b12-9a50-46dbe10c235b', hierarchyCode: 'SHL01.SCN02.SEC02.SEC03.ELT01', id: 'dce3ea79-eec6-396c-b1c4-598019e82fa6', layer: 'instance', name: 'Verification Code Input', primeComponent: 'INPUTOTP', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-space-*']};

CREATE (n174:SystemShellGraphNode:ComponentInstance:Instance);
SET n174 += {assetName: 'Otp Input', assetType: 'inputotp', code: 'SHL01.SCN02.SEC02.SEC03.ELT01.CP01.INPUTOTP', configurationJson: '{}', definitionCode: 'CD.INPUTOTP', description: 'Editable instance configuration for the Otp Input component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '30931ab3-f922-3ea7-8d53-b15280bcad96', hierarchyCode: 'SHL01.SCN02.SEC02.SEC03.ELT01.CP01.INPUTOTP', id: '5eca204b-824a-386d-94fb-4c74aaee3ceb', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-inputotp.mjs', layer: 'instance', name: 'Verification Code Input Otp Input Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n175:SystemShellGraphNode:Instance:Section);
SET n175 += {backgroundImagePath: '', code: 'SHL01.SCN02.SEC02.SEC04', controlSource: 'none', defaultState: 'visible', description: 'MFA action buttons.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '582f3a6f-dda8-30b7-b8ce-e1302f38a07c', hierarchyCode: 'SHL01.SCN02.SEC02.SEC04', id: '5c2a7c94-d986-367d-b464-2de6242b0840', layer: 'instance', name: 'Verification Action Bar', renderMode: 'static', repeatable: false, sectionType: 'action_bar', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n176:SystemShellGraphNode:Element:Instance);
SET n176 += {backgroundImagePath: '', code: 'SHL01.SCN02.SEC02.SEC04.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'button', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '3d45f9d0-46b6-31a4-a12d-cea491aa471c', hierarchyCode: 'SHL01.SCN02.SEC02.SEC04.ELT01', id: '59dced12-bb4a-32ff-a5d3-67ae5e92e633', layer: 'instance', name: 'Back Action', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-space-*']};

CREATE (n177:SystemShellGraphNode:Element:Instance);
SET n177 += {backgroundImagePath: '', code: 'SHL01.SCN02.SEC02.SEC04.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'button', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '5086afbb-fd27-372c-8bde-d8cdecff244a', hierarchyCode: 'SHL01.SCN02.SEC02.SEC04.ELT02', id: 'd84d713a-f16c-38cc-8098-7347c94a39ad', layer: 'instance', name: 'Verify Action', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-space-*']};

CREATE (n178:SystemShellGraphNode:Instance:ValidationRuleSet);
SET n178 += {backgroundImagePath: '', code: 'SHL01.SCN02.VRS01', description: 'Controls runtime UI state inside the MFA screen.', domain: 'frontend', family: 'ValidationRuleSet', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'SHL01.SCN02.VRS01', id: 'cb54c49b-46c6-3432-a224-9c89b2ccd909', layer: 'instance', name: 'MFA Validation Rule Set', ruleSetScope: 'screen', ruleSetType: 'screen_runtime', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active'};

CREATE (n179:SystemShellGraphNode:Instance:ValidationRule);
SET n179 += {actionType: 'show', backgroundImagePath: '', code: 'SHL01.SCN02.VRS01.R01', conditionExpression: 'mfa_code_valid = false', description: 'Shows invalid MFA feedback in the MFA screen.', domain: 'frontend', family: 'ValidationRule', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'SHL01.SCN02.VRS01.R01', id: 'b4686c28-b035-3823-9347-703f007433cb', layer: 'instance', name: 'Show Invalid MFA Banner', priority: 60, sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active', stopProcessing: false};

CREATE (n180:SystemShellGraphNode:Instance:Screen);
SET n180 += {backgroundImagePath: '', code: 'SHL01.SCN03', description: 'Dedicated tenant-not-found screen.', domain: 'frontend', family: 'Screen', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '504b400e-a549-3697-bc37-5789f186f0af', hierarchyCode: 'SHL01.SCN03', id: '4b5e3b44-0746-3fbc-beed-7538bb6cfe29', layer: 'instance', name: 'Tenant Not Found Screen', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n181:SystemShellGraphNode:Instance:Section);
SET n181 += {backgroundImagePath: '', code: 'SHL01.SCN03.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Tenant-not-found shell header container.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '5125540d-f717-36f3-ac8b-e00b1ad6d7ab', hierarchyCode: 'SHL01.SCN03.SEC01', id: '1ddd71a5-4ca1-3162-9ddb-499ba4824768', layer: 'instance', name: 'Screen Header Section', renderMode: 'static', repeatable: false, sectionType: 'header', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n182:SystemShellGraphNode:Element:Instance);
SET n182 += {backgroundImagePath: '', code: 'SHL01.SCN03.SEC01.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'title', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'bcdb72ea-6fda-36aa-9444-855ab4965015', hierarchyCode: 'SHL01.SCN03.SEC01.ELT01', id: '6f66e6ba-62c5-3e0d-a4a6-2cc58ac440e1', layer: 'instance', name: 'Screen Title', renderMode: 'static', semanticLevel: 'H1', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n183:SystemShellGraphNode:Element:Instance);
SET n183 += {backgroundImagePath: '', code: 'SHL01.SCN03.SEC01.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'title', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'd6560ec7-0161-30c4-bfe1-d0c9ea4c8b3c', hierarchyCode: 'SHL01.SCN03.SEC01.ELT02', id: '85960c10-125c-34ed-a22f-dd43d99add2c', layer: 'instance', name: 'Screen Subtitle', renderMode: 'static', semanticLevel: 'H2', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n184:SystemShellGraphNode:Instance:Section);
SET n184 += {backgroundImagePath: '', code: 'SHL01.SCN03.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Tenant-not-found shell logo container.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'b4e6de55-9370-3452-b0cc-ff9740daa981', hierarchyCode: 'SHL01.SCN03.SEC02', id: '0c65b525-f1da-3140-a66e-07f86f593b07', layer: 'instance', name: 'Logo Section', renderMode: 'static', repeatable: false, sectionType: 'logo', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n185:SystemShellGraphNode:Element:Instance);
SET n185 += {backgroundImagePath: '', code: 'SHL01.SCN03.SEC02.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'visual', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '0a2f01b2-ca8c-35ff-b253-07f46df2f060', hierarchyCode: 'SHL01.SCN03.SEC02.ELT01', id: '826d0957-31ac-3741-8de3-3f82a19c4d03', layer: 'instance', name: 'SVG Logo Renderer', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-primary-*', '--tp-space-*']};

CREATE (n186:SystemShellGraphNode:Instance:Section);
SET n186 += {backgroundImagePath: '', code: 'SHL01.SCN03.SEC03', controlSource: 'none', defaultState: 'visible', description: 'Tenant-not-found outcome container.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '495ed279-58af-3cf7-ae05-be181d4085ea', hierarchyCode: 'SHL01.SCN03.SEC03', id: '1c31ffe7-8b61-3046-9fcb-6890946d81c4', layer: 'instance', name: 'Tenant Not Found Section', renderMode: 'static', repeatable: false, sectionType: 'outcome', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n187:SystemShellGraphNode:Element:Instance);
SET n187 += {backgroundImagePath: '', code: 'SHL01.SCN03.SEC03.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'title', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '3f611c8c-fc7a-33a6-8e6b-241e272d11d2', hierarchyCode: 'SHL01.SCN03.SEC03.ELT01', id: '60e9cc6c-3c28-3323-9fca-0e5006ac2684', layer: 'instance', name: 'Not Found Title', renderMode: 'static', semanticLevel: 'H2', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n188:SystemShellGraphNode:Element:Instance);
SET n188 += {backgroundImagePath: '', code: 'SHL01.SCN03.SEC03.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'text', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'dd1a7c9d-56d0-32fb-afec-ea3b41adf66b', hierarchyCode: 'SHL01.SCN03.SEC03.ELT02', id: 'a5c0a120-0199-35ca-9091-7d948367befd', layer: 'instance', name: 'Not Found Description', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n189:SystemShellGraphNode:Element:Instance);
SET n189 += {backgroundImagePath: '', code: 'SHL01.SCN03.SEC03.ELT03', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'button', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'b82f919b-c9f7-3c86-9a0a-29d846d68dcd', hierarchyCode: 'SHL01.SCN03.SEC03.ELT03', id: '3c0441aa-f250-3781-8866-11f4b39627c4', layer: 'instance', name: 'Back To Login Action', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-space-*']};

CREATE (n190:SystemShellGraphNode:Instance:ValidationRuleSet);
SET n190 += {backgroundImagePath: '', code: 'SHL01.SCN03.VRS01', description: 'Controls runtime UI state inside the tenant-not-found screen.', domain: 'frontend', family: 'ValidationRuleSet', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'SHL01.SCN03.VRS01', id: '0d3000ed-6a66-346d-946d-0c86a0db6d49', layer: 'instance', name: 'Tenant Not Found Validation Rule Set', ruleSetScope: 'screen', ruleSetType: 'screen_runtime', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active'};

CREATE (n191:SystemShellGraphNode:Instance:ValidationRule);
SET n191 += {actionType: 'transition', actionValue: 'SHL01.SCN03', backgroundImagePath: '', code: 'SHL01.SCN03.VRS01.R01', conditionExpression: 'tenant_exists = false', description: 'Activates the tenant-not-found screen when tenant resolution fails.', domain: 'frontend', family: 'ValidationRule', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'SHL01.SCN03.VRS01.R01', id: 'b5097597-c5f2-3936-8efe-276c5e2e8de2', layer: 'instance', name: 'Show Tenant Not Found Screen', priority: 70, sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md', status: 'active', stopProcessing: true};

CREATE (n192:SystemShellGraphNode:Instance:Section);
SET n192 += {backgroundImagePath: '', code: 'SHL01.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Login-shell header container.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'eabb5db0-4945-3894-8f99-1564332a08ac', hierarchyCode: 'SHL01.SEC01', id: 'ccd3190f-6ef2-3c33-a72a-052411487571', layer: 'instance', name: 'Header Container', renderMode: 'static', repeatable: false, sectionType: 'header', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n193:SystemShellGraphNode:Instance:Section);
SET n193 += {backgroundImagePath: '', code: 'SHL01.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Login-shell main content container.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '527cfaf0-cb41-3f2f-8de9-89894ab446b1', hierarchyCode: 'SHL01.SEC02', id: '0607f326-afa2-3378-83c7-a635a648541c', layer: 'instance', name: 'Main Container', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n194:SystemShellGraphNode:Instance:Section);
SET n194 += {backgroundImagePath: '', code: 'SHL01.SEC03', controlSource: 'none', defaultState: 'visible', description: 'Login-shell footer container.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'ed4da59f-317c-3c7f-ac83-3ba9a6a5b121', hierarchyCode: 'SHL01.SEC03', id: 'fc1c3f0c-b222-3a3e-ba2d-e855fed9b61c', layer: 'instance', name: 'Footer Container', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n195:SystemShellGraphNode:Instance:Shell);
SET n195 += {backgroundColorStyle: 'var(--tp-bg)', backgroundImagePath: '', backgroundType: 'color', code: 'SHL02', description: 'Application shell that owns the shared header, main, and footer containers.', domain: 'frontend', family: 'Shell', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '4d15b84e-302e-385c-8221-9ba475c6cc6c', hierarchyCode: 'SHL02', id: '01261212-b693-334e-8f4a-c46bbcb7a7b5', layer: 'instance', name: 'application-shell', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n196:SystemShellGraphNode:Instance:Screen);
SET n196 += {backgroundImagePath: '', code: 'SHL02.SCN01', description: 'Authenticated administration workspace page hosted inside ShellLayoutComponent.', domain: 'frontend', family: 'Screen', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'e38332d6-7b97-3741-b53f-0b7c43401394', hierarchyCode: 'SHL02.SCN01', id: '4bd08994-2ecc-33f8-875b-df09c9c7b940', layer: 'instance', name: 'Administration Page', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n197:SystemShellGraphNode:Instance:Section);
SET n197 += {backgroundImagePath: '', code: 'SHL02.SCN01.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Authenticated shell header bar.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'd2f8e312-f347-3b82-be9a-401168abcd22', hierarchyCode: 'SHL02.SCN01.SEC01', id: '9640a424-850b-3287-a9d9-3f768b0c5c1a', layer: 'instance', name: 'Header Bar Section', renderMode: 'static', repeatable: false, sectionType: 'header', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n198:SystemShellGraphNode:Element:Instance);
SET n198 += {backgroundImagePath: '', code: 'SHL02.SCN01.SEC01.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'title', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'e492130c-b9d5-3b04-a461-863148f24e55', hierarchyCode: 'SHL02.SCN01.SEC01.ELT01', id: '5a52f153-81d2-3c72-8507-6f4ac33ead6a', layer: 'instance', name: 'Shell Title', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n199:SystemShellGraphNode:Element:Instance);
SET n199 += {backgroundImagePath: '', code: 'SHL02.SCN01.SEC01.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'button', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '4af831bb-be62-3b36-83e6-5167a023ee73', hierarchyCode: 'SHL02.SCN01.SEC01.ELT02', id: '8a45c51a-5d86-3d8e-b2be-6d9d457b938f', layer: 'instance', name: 'Header Actions', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n200:SystemShellGraphNode:Instance:Section);
SET n200 += {backgroundImagePath: '', code: 'SHL02.SCN01.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Authenticated navigation dock.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'a0f860f2-1984-3e74-806c-252283f88a70', hierarchyCode: 'SHL02.SCN01.SEC02', id: 'b6f3226a-10e5-346d-b4ad-560ea93db391', layer: 'instance', name: 'Navigation Dock Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n201:SystemShellGraphNode:Element:Instance);
SET n201 += {backgroundImagePath: '', code: 'SHL02.SCN01.SEC02.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '29fd2f0a-ec77-3073-af39-8df8bf91aec0', hierarchyCode: 'SHL02.SCN01.SEC02.ELT01', id: '461ea5ac-8716-30fa-abb2-910bc2fcee0a', layer: 'instance', name: 'Navigation Dock Label', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n202:SystemShellGraphNode:Element:Instance);
SET n202 += {backgroundImagePath: '', code: 'SHL02.SCN01.SEC02.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '8c60bda8-6e65-305e-92fe-fe99935714f5', hierarchyCode: 'SHL02.SCN01.SEC02.ELT02', id: '12a2696e-b8d4-3cb8-ba3f-aedd2376c10e', layer: 'instance', name: 'Active Module', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-primary-*', '--tp-space-*']};

CREATE (n203:SystemShellGraphNode:Instance:Section);
SET n203 += {backgroundImagePath: '', code: 'SHL02.SCN01.SEC03', controlSource: 'none', defaultState: 'visible', description: 'Authenticated workspace content region.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'e810adeb-f8a8-36e8-9e32-12ed79a1178a', hierarchyCode: 'SHL02.SCN01.SEC03', id: 'abe7c3f0-e084-3283-ade2-b06aef618085', layer: 'instance', name: 'Workspace Content Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n204:SystemShellGraphNode:Element:Instance);
SET n204 += {backgroundImagePath: '', code: 'SHL02.SCN01.SEC03.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'title', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '25b6a68a-fcb7-3760-ab8b-162f9e4f3b9d', hierarchyCode: 'SHL02.SCN01.SEC03.ELT01', id: 'c72a2263-3c8e-3084-8ca1-66962d361bc4', layer: 'instance', name: 'Workspace Title', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n205:SystemShellGraphNode:Element:Instance);
SET n205 += {backgroundImagePath: '', code: 'SHL02.SCN01.SEC03.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Element instance for the frontend screen preview.', domain: 'frontend', elementType: 'text', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '08080678-ad57-301f-8433-2b42c82caf72', hierarchyCode: 'SHL02.SCN01.SEC03.ELT02', id: '6f327a06-57fa-368f-b86d-1605ef5a45ea', layer: 'instance', name: 'Workspace Description', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n206:SystemShellGraphNode:Instance:Screen);
SET n206 += {backgroundImagePath: '', code: 'SHL02.SCN02', description: 'Main tenant-management screen for browsing, searching, filtering, and selecting tenants.', domain: 'frontend', family: 'Screen', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'a91b774f-3726-320b-b2b2-89a9c8c97cf3', hierarchyCode: 'SHL02.SCN02', id: '2b2c4900-3a65-3c5c-87f6-a7cd3220d5e4', layer: 'instance', name: 'View Tenant List', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.02 Settings Shell/G01.02.01 Tenant Registry/G01.02.01.01 View Tenant List/01-Persona-Journey-Channel-Touchpoint-Variant.md', status: 'active'};

CREATE (n207:SystemShellGraphNode:Instance:Section);
SET n207 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Tenant-list title region.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'bbf0a08c-dc57-33f4-8e03-78c895ccce08', hierarchyCode: 'SHL02.SCN02.SEC01', id: '85ef34a2-57df-3470-91b2-dd3e78c6a176', layer: 'instance', name: 'List Header Section', renderMode: 'static', repeatable: false, sectionType: 'header', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active'};

CREATE (n208:SystemShellGraphNode:Element:Instance);
SET n208 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC01.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Tenant-list title element.', domain: 'frontend', elementType: 'title', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'e1da48aa-425a-3119-af92-8168be693beb', hierarchyCode: 'SHL02.SCN02.SEC01.ELT01', id: '6042ed18-5883-39ff-a89b-974203102b5a', layer: 'instance', name: 'Screen Title', renderMode: 'static', semanticLevel: 'H1', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n209:SystemShellGraphNode:Instance:Section);
SET n209 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Tenant-list primary toolbar.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '2b6e1d11-1c3f-375b-8de7-86d2586fc0a3', hierarchyCode: 'SHL02.SCN02.SEC02', id: '05de60d0-fd6e-39ab-9094-ae4de8821c95', layer: 'instance', name: 'Toolbar Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active'};

CREATE (n210:SystemShellGraphNode:Instance:Section);
SET n210 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC02.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Search entry region for the tenant list.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'dc9d2f1c-73b2-3608-8dc6-f8e566f7742c', hierarchyCode: 'SHL02.SCN02.SEC02.SEC01', id: '84e4fa77-b9f0-3350-b2f0-10171bf17cfd', layer: 'instance', name: 'Search Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active'};

CREATE (n211:SystemShellGraphNode:Element:Instance);
SET n211 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC02.SEC01.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Search input for tenant list filtering.', domain: 'frontend', elementType: 'input', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '8bbb2d21-b44c-3b4f-b87d-dbae78e8abfc', hierarchyCode: 'SHL02.SCN02.SEC02.SEC01.ELT01', id: 'b5466fd9-921f-397d-ac19-835ec79b26a0', layer: 'instance', name: 'Search Input', primeComponent: 'InputText', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active', tokenFamilies: ['--p-inputtext-*', '--tp-space-*']};

CREATE (n212:SystemShellGraphNode:ComponentInstance:Instance);
SET n212 += {assetName: 'InputText', assetType: 'inputtext', code: 'SHL02.SCN02.SEC02.SEC01.ELT01.CP01.INPUTTEXT', configurationJson: '{}', definitionCode: 'CD.INPUTTEXT', description: 'Editable instance configuration for the InputText component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '6fe71850-48e5-30e8-a188-cf51888c1ca6', hierarchyCode: 'SHL02.SCN02.SEC02.SEC01.ELT01.CP01.INPUTTEXT', id: 'e2ab1a98-f187-33f7-9565-1f16fadb41b6', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-inputtext.mjs', layer: 'instance', name: 'Search Input InputText Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n213:SystemShellGraphNode:Instance:Section);
SET n213 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC02.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Toolbar filter-toggle action region.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '1c7842a5-8d47-35d0-9614-7f6c74a3894f', hierarchyCode: 'SHL02.SCN02.SEC02.SEC02', id: 'ac5659be-1805-35e1-9bc8-0f2889485796', layer: 'instance', name: 'Filter Toggle Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active'};

CREATE (n214:SystemShellGraphNode:Element:Instance);
SET n214 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC02.SEC02.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Toolbar action that reveals filter controls.', domain: 'frontend', elementType: 'toggle', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'a243ce8b-d340-330b-b6a4-884d98d122ad', hierarchyCode: 'SHL02.SCN02.SEC02.SEC02.ELT01', id: '9585f022-0727-3656-83e7-46040ccec0ec', layer: 'instance', name: 'Filter Toggle Action', primeComponent: 'Button', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active', tokenFamilies: ['--p-button-*', '--tp-space-*']};

CREATE (n215:SystemShellGraphNode:ComponentInstance:Instance);
SET n215 += {assetName: 'Button', assetType: 'button', code: 'SHL02.SCN02.SEC02.SEC02.ELT01.CP01.BUTTON', configurationJson: '{}', definitionCode: 'CD.BUTTON', description: 'Editable instance configuration for the Button component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '71a80bb3-e82f-31d7-a206-4b423a81264e', hierarchyCode: 'SHL02.SCN02.SEC02.SEC02.ELT01.CP01.BUTTON', id: '610c131e-c2b1-303d-9f29-c9738cb9f386', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-button.mjs', layer: 'instance', name: 'Filter Toggle Action Button Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n216:SystemShellGraphNode:Instance:Section);
SET n216 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC02.SEC03', controlSource: 'none', defaultState: 'visible', description: 'Toolbar view-mode control region.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'a1f40ddc-e808-3594-b524-93fe543ccc18', hierarchyCode: 'SHL02.SCN02.SEC02.SEC03', id: 'c1d6b1b4-2945-3bb4-91ed-ae023cae66a5', layer: 'instance', name: 'View Mode Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active'};

CREATE (n217:SystemShellGraphNode:Element:Instance);
SET n217 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC02.SEC03.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Toggle control switching between grid and table presentation.', domain: 'frontend', elementType: 'toggle', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '55b4df16-6276-3040-a48d-e54460dfea49', hierarchyCode: 'SHL02.SCN02.SEC02.SEC03.ELT01', id: '9b29109d-9791-39f9-b089-822d29ca5bbe', layer: 'instance', name: 'View Mode Toggle', primeComponent: 'ToggleButton', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active', tokenFamilies: ['--p-togglebutton-*', '--tp-space-*']};

CREATE (n218:SystemShellGraphNode:ComponentInstance:Instance);
SET n218 += {assetName: 'ToggleButton', assetType: 'togglebutton', code: 'SHL02.SCN02.SEC02.SEC03.ELT01.CP01.TOGGLEBUTTON', configurationJson: '{}', definitionCode: 'CD.TOGGLEBUTTON', description: 'Editable instance configuration for the ToggleButton component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'd2c22137-75be-3494-bfa8-8128cf4fe692', hierarchyCode: 'SHL02.SCN02.SEC02.SEC03.ELT01.CP01.TOGGLEBUTTON', id: 'e0446ea0-1cbd-3695-abbe-f6fd5de8c8b7', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-togglebutton.mjs', layer: 'instance', name: 'View Mode Toggle ToggleButton Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n219:SystemShellGraphNode:Instance:Section);
SET n219 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC02.SEC04', controlSource: 'none', defaultState: 'visible', description: 'Toolbar create-tenant action region.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'e54f2b88-e49c-36ce-a32e-22e0e1b562bb', hierarchyCode: 'SHL02.SCN02.SEC02.SEC04', id: 'c716244a-fc8c-37b9-b1db-9026c4ccac42', layer: 'instance', name: 'Create Tenant Action Section', renderMode: 'static', repeatable: false, sectionType: 'action_bar', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active'};

CREATE (n220:SystemShellGraphNode:Element:Instance);
SET n220 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC02.SEC04.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Primary action that starts tenant creation.', domain: 'frontend', elementType: 'button', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '47a62bf9-3a47-3a06-be58-3e14ea4dcc27', hierarchyCode: 'SHL02.SCN02.SEC02.SEC04.ELT01', id: '0de2df99-a18a-3c88-a444-7c40c6b76f2a', layer: 'instance', name: 'New Tenant Action', primeComponent: 'Button', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active', tokenFamilies: ['--p-button-*', '--tp-space-*']};

CREATE (n221:SystemShellGraphNode:ComponentInstance:Instance);
SET n221 += {assetName: 'Button', assetType: 'button', code: 'SHL02.SCN02.SEC02.SEC04.ELT01.CP01.BUTTON', configurationJson: '{}', definitionCode: 'CD.BUTTON', description: 'Editable instance configuration for the Button component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '31311d8c-4d57-3b12-a483-e8c9c130fe21', hierarchyCode: 'SHL02.SCN02.SEC02.SEC04.ELT01.CP01.BUTTON', id: '99bf9bbe-b050-38b2-acaf-8be2a803e406', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-button.mjs', layer: 'instance', name: 'New Tenant Action Button Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n222:SystemShellGraphNode:Instance:Section);
SET n222 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC03', controlSource: 'validation_rule_set', defaultState: 'hidden', description: 'Expanded tenant-list filter controls.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '705e7ae5-bf74-345a-8c5e-ffdecc9b043a', hierarchyCode: 'SHL02.SCN02.SEC03', id: '7e1e7a4b-4ef0-3ee3-8fd3-dc4b10763b75', layer: 'instance', name: 'Filter Row Section', renderMode: 'conditional', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active'};

CREATE (n223:SystemShellGraphNode:Element:Instance);
SET n223 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC03.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Tenant-type filter selection.', domain: 'frontend', elementType: 'input', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '8ed35121-e8cc-34a4-bc74-3a992d004be2', hierarchyCode: 'SHL02.SCN02.SEC03.ELT01', id: '331b3892-f1a2-3e7f-9e40-03c5683a9627', layer: 'instance', name: 'Type Filter Select', primeComponent: 'Select', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active', tokenFamilies: ['--p-select-*', '--tp-space-*']};

CREATE (n224:SystemShellGraphNode:ComponentInstance:Instance);
SET n224 += {assetName: 'Select', assetType: 'select', code: 'SHL02.SCN02.SEC03.ELT01.CP01.SELECT', configurationJson: '{}', definitionCode: 'CD.SELECT', description: 'Editable instance configuration for the Select component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'd12edd99-e692-3bef-8ee5-9effff456fc9', hierarchyCode: 'SHL02.SCN02.SEC03.ELT01.CP01.SELECT', id: '64ccd10d-06c9-3861-8728-561f3189c1d6', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-select.mjs', layer: 'instance', name: 'Type Filter Select Select Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n225:SystemShellGraphNode:Element:Instance);
SET n225 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC03.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Tenant-status filter selection.', domain: 'frontend', elementType: 'input', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'a9658074-8e30-3178-a25f-c9a1ed6a2422', hierarchyCode: 'SHL02.SCN02.SEC03.ELT02', id: 'ed9621c9-2192-3a44-9155-05d7d21959c5', layer: 'instance', name: 'Status Filter Select', primeComponent: 'Select', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active', tokenFamilies: ['--p-select-*', '--tp-space-*']};

CREATE (n226:SystemShellGraphNode:ComponentInstance:Instance);
SET n226 += {assetName: 'Select', assetType: 'select', code: 'SHL02.SCN02.SEC03.ELT02.CP01.SELECT', configurationJson: '{}', definitionCode: 'CD.SELECT', description: 'Editable instance configuration for the Select component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'a7de4d82-e3d9-375d-9994-62ccf80e1fbf', hierarchyCode: 'SHL02.SCN02.SEC03.ELT02.CP01.SELECT', id: '4ca915c7-8ddb-37ed-9b0e-206f92fefd9e', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-select.mjs', layer: 'instance', name: 'Status Filter Select Select Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n227:SystemShellGraphNode:Element:Instance);
SET n227 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC03.ELT03', controlSource: 'none', defaultState: 'visible', description: 'Action that clears active tenant-list filters.', domain: 'frontend', elementType: 'button', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '222eb838-63c0-3527-8b84-a48e554db669', hierarchyCode: 'SHL02.SCN02.SEC03.ELT03', id: '9d4487dc-6b6c-36ba-81af-eab0e787577d', layer: 'instance', name: 'Clear Filters Action', primeComponent: 'Button', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active', tokenFamilies: ['--p-button-*', '--tp-space-*']};

CREATE (n228:SystemShellGraphNode:ComponentInstance:Instance);
SET n228 += {assetName: 'Button', assetType: 'button', code: 'SHL02.SCN02.SEC03.ELT03.CP01.BUTTON', configurationJson: '{}', definitionCode: 'CD.BUTTON', description: 'Editable instance configuration for the Button component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '1f928ecd-5401-3a16-bf28-f5aea88d9b91', hierarchyCode: 'SHL02.SCN02.SEC03.ELT03.CP01.BUTTON', id: 'bc1a7cf5-55b9-3801-aa1b-486c0178c796', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-button.mjs', layer: 'instance', name: 'Clear Filters Action Button Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n229:SystemShellGraphNode:Instance:Section);
SET n229 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC04', controlSource: 'validation_rule_set', defaultState: 'hidden', description: 'Zero-results or no-tenants state.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '4310a682-ad53-33c3-9c8b-4c2a395c0436', hierarchyCode: 'SHL02.SCN02.SEC04', id: 'e053007b-06a6-39a4-90c3-8d14a96c5736', layer: 'instance', name: 'Empty State Section', renderMode: 'conditional', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active'};

CREATE (n230:SystemShellGraphNode:Element:Instance);
SET n230 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC04.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Title shown when the tenant list has no visible results.', domain: 'frontend', elementType: 'title', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '9085eebc-b9cd-3d3f-ba3f-fe29c892d331', hierarchyCode: 'SHL02.SCN02.SEC04.ELT01', id: '9ea29177-93d4-3ed3-87e1-508b31bb51b8', layer: 'instance', name: 'Empty State Title', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n231:SystemShellGraphNode:Element:Instance);
SET n231 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC04.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Message shown when the tenant list has no visible results.', domain: 'frontend', elementType: 'message', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '7adc28a0-b761-3475-944e-5ba15fd79758', hierarchyCode: 'SHL02.SCN02.SEC04.ELT02', id: '4adec46f-490f-3414-96df-e919e636aaf3', layer: 'instance', name: 'Empty State Message', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n232:SystemShellGraphNode:Element:Instance);
SET n232 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC04.ELT03', controlSource: 'none', defaultState: 'visible', description: 'Recovery action shown in the empty state.', domain: 'frontend', elementType: 'button', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '80f0e2da-29dd-3d95-a61f-a707e4f645ce', hierarchyCode: 'SHL02.SCN02.SEC04.ELT03', id: '09a97877-69a1-351f-8e8f-e0b4f5358a27', layer: 'instance', name: 'Empty State Action', primeComponent: 'Button', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active', tokenFamilies: ['--p-button-*', '--tp-space-*']};

CREATE (n233:SystemShellGraphNode:ComponentInstance:Instance);
SET n233 += {assetName: 'Button', assetType: 'button', code: 'SHL02.SCN02.SEC04.ELT03.CP01.BUTTON', configurationJson: '{}', definitionCode: 'CD.BUTTON', description: 'Editable instance configuration for the Button component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'a160e6f5-2720-3a2b-b179-9443b4de2297', hierarchyCode: 'SHL02.SCN02.SEC04.ELT03.CP01.BUTTON', id: 'f6096a75-f2b4-3434-a871-3828e5f05dbd', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-button.mjs', layer: 'instance', name: 'Empty State Action Button Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n234:SystemShellGraphNode:Instance:Section);
SET n234 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC05', controlSource: 'none', defaultState: 'visible', description: 'Main results area for tenant browsing.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '215f9cda-292f-3630-a7b7-82a14273c748', hierarchyCode: 'SHL02.SCN02.SEC05', id: 'e2babf9e-397e-3e8f-a01d-bb18c9b475ec', layer: 'instance', name: 'Results Surface Section', renderMode: 'static', repeatable: false, sectionType: 'surface', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active'};

CREATE (n235:SystemShellGraphNode:Instance:Section);
SET n235 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC05.SEC01', controlSource: 'validation_rule_set', defaultState: 'hidden', description: 'Grid presentation for tenant results.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'c34f73a3-1421-39e4-842b-eab4da7c2f28', hierarchyCode: 'SHL02.SCN02.SEC05.SEC01', id: '445ba0dd-9fb9-331b-aff9-d7ae418c0627', layer: 'instance', name: 'Grid View Section', renderMode: 'conditional', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active'};

CREATE (n236:SystemShellGraphNode:Element:Instance);
SET n236 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC05.SEC01.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Representative tenant card unit in the grid view.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'df33c8d9-1c5e-3bcc-98bc-55a59f158269', hierarchyCode: 'SHL02.SCN02.SEC05.SEC01.ELT01', id: 'c1368554-5959-32d0-b4ff-d884a97d0533', layer: 'instance', name: 'Tenant Card Result', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active', tokenFamilies: ['--tp-surface-*', '--tp-space-*']};

CREATE (n237:SystemShellGraphNode:Element:Instance);
SET n237 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC05.SEC01.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Representative tenant type badge within the grid card.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '148c0afb-bcbe-32af-83b2-36741aee1127', hierarchyCode: 'SHL02.SCN02.SEC05.SEC01.ELT02', id: 'be762091-eaf8-305b-b9ad-42b67c8a4cdd', layer: 'instance', name: 'Tenant Type Badge', primeComponent: 'Tag', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active', tokenFamilies: ['--p-tag-*', '--tp-space-*']};

CREATE (n238:SystemShellGraphNode:ComponentInstance:Instance);
SET n238 += {assetName: 'Tag', assetType: 'tag', code: 'SHL02.SCN02.SEC05.SEC01.ELT02.CP01.TAG', configurationJson: '{}', definitionCode: 'CD.TAG', description: 'Editable instance configuration for the Tag component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '0b8999df-e16a-3637-a5a3-29f167718878', hierarchyCode: 'SHL02.SCN02.SEC05.SEC01.ELT02.CP01.TAG', id: '42875ee2-9962-3b4b-a29c-d2fc10aae9df', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-tag.mjs', layer: 'instance', name: 'Tenant Type Badge Tag Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n239:SystemShellGraphNode:Instance:Section);
SET n239 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC05.SEC02', controlSource: 'validation_rule_set', defaultState: 'hidden', description: 'Table presentation for tenant results.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '1ada0e7c-621e-30e7-a02c-e7208179d56e', hierarchyCode: 'SHL02.SCN02.SEC05.SEC02', id: '14d63f1a-f49b-3eb2-a231-680415db2e1b', layer: 'instance', name: 'Table View Section', renderMode: 'conditional', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active'};

CREATE (n240:SystemShellGraphNode:Element:Instance);
SET n240 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC05.SEC02.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Structured tenant table view.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'db19d645-cec1-3ee0-8e00-9c1fcb83f07b', hierarchyCode: 'SHL02.SCN02.SEC05.SEC02.ELT01', id: '419fa697-7dd9-3cb4-8f1b-081e7e010cc1', layer: 'instance', name: 'Tenant Data Table', primeComponent: 'Table', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active', tokenFamilies: ['--p-datatable-*', '--tp-space-*']};

CREATE (n241:SystemShellGraphNode:ComponentInstance:Instance);
SET n241 += {assetName: 'Table', assetType: 'table', code: 'SHL02.SCN02.SEC05.SEC02.ELT01.CP01.TABLE', configurationJson: '{}', definitionCode: 'CD.TABLE', description: 'Editable instance configuration for the Table component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '6da44ad3-9a5e-3ae5-85ef-f3d26b11f211', hierarchyCode: 'SHL02.SCN02.SEC05.SEC02.ELT01.CP01.TABLE', id: '2cb404a5-3a58-350d-bad7-d795f9685d42', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-table.mjs', layer: 'instance', name: 'Tenant Data Table Table Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n242:SystemShellGraphNode:Element:Instance);
SET n242 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC05.SEC02.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Representative tenant type badge within the table view.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '6723eb99-2e23-3a0d-b7c9-0a2fde8db1bd', hierarchyCode: 'SHL02.SCN02.SEC05.SEC02.ELT02', id: '82bde4a1-5066-3057-996f-8bedc37a67e4', layer: 'instance', name: 'Tenant Type Badge', primeComponent: 'Tag', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active', tokenFamilies: ['--p-tag-*', '--tp-space-*']};

CREATE (n243:SystemShellGraphNode:ComponentInstance:Instance);
SET n243 += {assetName: 'Tag', assetType: 'tag', code: 'SHL02.SCN02.SEC05.SEC02.ELT02.CP01.TAG', configurationJson: '{}', definitionCode: 'CD.TAG', description: 'Editable instance configuration for the Tag component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'b5f97e43-3bcb-3ac0-937d-1f66cac87b32', hierarchyCode: 'SHL02.SCN02.SEC05.SEC02.ELT02.CP01.TAG', id: 'b2549743-c364-3bb8-81af-e6ee6ceeacf3', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-tag.mjs', layer: 'instance', name: 'Tenant Type Badge Tag Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n244:SystemShellGraphNode:Instance:Section);
SET n244 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC05.SEC03', controlSource: 'none', defaultState: 'visible', description: 'Paging control region for tenant results.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'e580c16f-49ac-372b-9bfe-e924e4c1cf52', hierarchyCode: 'SHL02.SCN02.SEC05.SEC03', id: '32998237-e0e6-3794-8076-d14d8c43154b', layer: 'instance', name: 'Pagination Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active'};

CREATE (n245:SystemShellGraphNode:Element:Instance);
SET n245 += {backgroundImagePath: '', code: 'SHL02.SCN02.SEC05.SEC03.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Paginator for tenant list navigation.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '3301b895-eb3c-344a-bd07-8527af660195', hierarchyCode: 'SHL02.SCN02.SEC05.SEC03.ELT01', id: '9df1fcbb-f05c-3628-a207-eae82c6e92af', layer: 'instance', name: 'Tenant Paginator', primeComponent: 'Paginator', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html', status: 'active', tokenFamilies: ['--p-paginator-*', '--tp-space-*']};

CREATE (n246:SystemShellGraphNode:ComponentInstance:Instance);
SET n246 += {assetName: 'Paginator', assetType: 'paginator', code: 'SHL02.SCN02.SEC05.SEC03.ELT01.CP01.PAGINATOR', configurationJson: '{}', definitionCode: 'CD.PAGINATOR', description: 'Editable instance configuration for the Paginator component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'b628aa14-0d1a-38be-8382-a28a23954e51', hierarchyCode: 'SHL02.SCN02.SEC05.SEC03.ELT01.CP01.PAGINATOR', id: 'd3e8ebfb-96d0-30dd-aeda-185e23ed3f0c', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-paginator.mjs', layer: 'instance', name: 'Tenant Paginator Paginator Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n247:SystemShellGraphNode:Instance:ValidationRuleSet);
SET n247 += {backgroundImagePath: '', code: 'SHL02.SCN02.VRS01', description: 'Controls runtime UI state inside the tenant list screen.', domain: 'frontend', family: 'ValidationRuleSet', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'SHL02.SCN02.VRS01', id: '6415a889-cf42-3e60-8fcd-d09b3b33f95b', layer: 'instance', name: 'Tenant List Validation Rule Set', ruleSetScope: 'screen', ruleSetType: 'screen_runtime', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.02 Settings Shell/G01.02.01 Tenant Registry/G01.02.01.01 View Tenant List/01-Persona-Journey-Channel-Touchpoint-Variant.md', status: 'active'};

CREATE (n248:SystemShellGraphNode:Instance:ValidationRule);
SET n248 += {actionType: 'show', backgroundImagePath: '', code: 'SHL02.SCN02.VRS01.R01', conditionExpression: 'filters_expanded = true', description: 'Shows the filter row when tenant-list filters are expanded.', domain: 'frontend', family: 'ValidationRule', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'SHL02.SCN02.VRS01.R01', id: '6ae57eb7-ff2f-3a41-84c8-f05f12be42d5', layer: 'instance', name: 'Show Filter Row', priority: 10, sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.02 Settings Shell/G01.02.01 Tenant Registry/G01.02.01.01 View Tenant List/01-Persona-Journey-Channel-Touchpoint-Variant.md', status: 'active', stopProcessing: false};

CREATE (n249:SystemShellGraphNode:Instance:ValidationRule);
SET n249 += {actionType: 'show', backgroundImagePath: '', code: 'SHL02.SCN02.VRS01.R02', conditionExpression: 'tenant_result_count = 0', description: 'Shows the empty state when no tenant results are available.', domain: 'frontend', family: 'ValidationRule', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'SHL02.SCN02.VRS01.R02', id: '811a695a-eb06-37c5-97e7-01b91b4a2a77', layer: 'instance', name: 'Show Empty State', priority: 20, sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.02 Settings Shell/G01.02.01 Tenant Registry/G01.02.01.01 View Tenant List/01-Persona-Journey-Channel-Touchpoint-Variant.md', status: 'active', stopProcessing: false};

CREATE (n250:SystemShellGraphNode:Instance:ValidationRule);
SET n250 += {actionType: 'show', backgroundImagePath: '', code: 'SHL02.SCN02.VRS01.R03', conditionExpression: 'tenant_view_mode = \'grid\'', description: 'Shows the grid presentation when tenant view mode is grid.', domain: 'frontend', family: 'ValidationRule', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'SHL02.SCN02.VRS01.R03', id: '6e322e6b-afe4-38b7-8f46-696b64519652', layer: 'instance', name: 'Show Grid View', priority: 30, sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.02 Settings Shell/G01.02.01 Tenant Registry/G01.02.01.01 View Tenant List/01-Persona-Journey-Channel-Touchpoint-Variant.md', status: 'active', stopProcessing: false};

CREATE (n251:SystemShellGraphNode:Instance:ValidationRule);
SET n251 += {actionType: 'show', backgroundImagePath: '', code: 'SHL02.SCN02.VRS01.R04', conditionExpression: 'tenant_view_mode = \'table\'', description: 'Shows the table presentation when tenant view mode is table.', domain: 'frontend', family: 'ValidationRule', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'SHL02.SCN02.VRS01.R04', id: '60bb2a18-6aed-3d77-bbdd-008dc8472ea0', layer: 'instance', name: 'Show Table View', priority: 40, sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.02 Settings Shell/G01.02.01 Tenant Registry/G01.02.01.01 View Tenant List/01-Persona-Journey-Channel-Touchpoint-Variant.md', status: 'active', stopProcessing: false};

CREATE (n252:SystemShellGraphNode:Instance:Screen);
SET n252 += {backgroundImagePath: '', code: 'SHL02.SCN03', description: 'Tenant detail screen with banner hero, actions, KPI chips, and tabbed workspaces.', domain: 'frontend', family: 'Screen', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '0ba9b311-b610-3091-95a6-246047ddd5c3', hierarchyCode: 'SHL02.SCN03', id: 'e4708dc1-79be-3fec-96a3-b91358ece90a', layer: 'instance', name: 'Tenant Fact Sheet', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.03 Tenant Fact Sheet/G01.03.01 View Tenant Fact Sheet/01-Persona-Journey-Touchpoint-Variant.md', status: 'active'};

CREATE (n253:SystemShellGraphNode:Instance:Section);
SET n253 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Tenant fact-sheet breadcrumb region.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'e18791ed-caf5-37a6-83db-acb63255253b', hierarchyCode: 'SHL02.SCN03.SEC01', id: '8c19bd62-6603-3462-8220-eb6fb965cc92', layer: 'instance', name: 'Breadcrumb Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n254:SystemShellGraphNode:Element:Instance);
SET n254 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC01.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Breadcrumb path for the tenant fact sheet.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '125b385b-163d-326b-9367-04cf3257bfff', hierarchyCode: 'SHL02.SCN03.SEC01.ELT01', id: 'b3da487d-9c0d-3937-b808-13fcb3a7e8f6', layer: 'instance', name: 'Tenant Breadcrumb Trail', primeComponent: 'Breadcrumb', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-breadcrumb-*', '--tp-space-*']};

CREATE (n255:SystemShellGraphNode:ComponentInstance:Instance);
SET n255 += {assetName: 'Breadcrumb', assetType: 'breadcrumb', code: 'SHL02.SCN03.SEC01.ELT01.CP01.BREADCRUMB', configurationJson: '{}', definitionCode: 'CD.BREADCRUMB', description: 'Editable instance configuration for the Breadcrumb component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'e634bd69-4859-3c05-aad5-963dbc25a465', hierarchyCode: 'SHL02.SCN03.SEC01.ELT01.CP01.BREADCRUMB', id: '7e21a4df-4314-354c-ac8b-9e85d00485c5', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-breadcrumb.mjs', layer: 'instance', name: 'Tenant Breadcrumb Trail Breadcrumb Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n256:SystemShellGraphNode:Instance:Section);
SET n256 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Tenant fact-sheet banner hero.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '6a3c2559-89d0-3fe9-8e79-0900570eee0a', hierarchyCode: 'SHL02.SCN03.SEC02', id: '59243d90-6c72-3d0c-82f3-c9be67a6b175', layer: 'instance', name: 'Banner Hero Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n257:SystemShellGraphNode:Instance:Section);
SET n257 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC02.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Tenant identity, status, and KPI content.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'f4878b3d-50cd-37a3-8fb1-b887e574ef62', hierarchyCode: 'SHL02.SCN03.SEC02.SEC01', id: '983603ce-e016-3af9-86fc-8be8ba8806ec', layer: 'instance', name: 'Tenant Identity Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n258:SystemShellGraphNode:Element:Instance);
SET n258 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC02.SEC01.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Tenant logo or initials avatar.', domain: 'frontend', elementType: 'visual', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '304d51ff-1332-322e-a4be-cdcb9de646b2', hierarchyCode: 'SHL02.SCN03.SEC02.SEC01.ELT01', id: '15db4e6a-c3ab-3a27-9de5-5f7e9f4f65de', layer: 'instance', name: 'Tenant Logo', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--tp-primary-*', '--tp-space-*']};

CREATE (n259:SystemShellGraphNode:Element:Instance);
SET n259 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC02.SEC01.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Primary tenant title in the banner hero.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '6cd18d0c-896a-3440-ac31-b7bac70dcba3', hierarchyCode: 'SHL02.SCN03.SEC02.SEC01.ELT02', id: '2bbf5606-137d-3d16-ae39-da9e11b43db6', layer: 'instance', name: 'Tenant Name', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n260:SystemShellGraphNode:Element:Instance);
SET n260 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC02.SEC01.ELT03', controlSource: 'none', defaultState: 'visible', description: 'Tenant classification badge.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '4bfefb54-2e32-3071-9682-fabc58e09826', hierarchyCode: 'SHL02.SCN03.SEC02.SEC01.ELT03', id: 'bc97588f-b0be-34f0-a54d-81da582482ce', layer: 'instance', name: 'Tenant Type Badge', primeComponent: 'Tag', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-tag-*', '--tp-space-*']};

CREATE (n261:SystemShellGraphNode:ComponentInstance:Instance);
SET n261 += {assetName: 'Tag', assetType: 'tag', code: 'SHL02.SCN03.SEC02.SEC01.ELT03.CP01.TAG', configurationJson: '{}', definitionCode: 'CD.TAG', description: 'Editable instance configuration for the Tag component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '7d2d385f-aaeb-34c6-b164-161f81a77842', hierarchyCode: 'SHL02.SCN03.SEC02.SEC01.ELT03.CP01.TAG', id: 'e7a960f8-8fa6-3db8-99ae-7f1950834766', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-tag.mjs', layer: 'instance', name: 'Tenant Type Badge Tag Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n262:SystemShellGraphNode:Element:Instance);
SET n262 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC02.SEC01.ELT04', controlSource: 'none', defaultState: 'visible', description: 'Lifecycle state badge for the selected tenant.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '25e057dc-f64b-3ca7-9fe0-681111c6c2a0', hierarchyCode: 'SHL02.SCN03.SEC02.SEC01.ELT04', id: '74017a07-8171-39bd-9aca-4d07c3670e9a', layer: 'instance', name: 'Tenant Status Badge', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--tp-success-*', '--tp-warning-*', '--tp-danger-*']};

CREATE (n263:SystemShellGraphNode:Element:Instance);
SET n263 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC02.SEC01.ELT05', controlSource: 'none', defaultState: 'visible', description: 'Health indicator badge for the selected tenant.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '2a363e46-43c4-37cf-85a1-a3e8c63cfee6', hierarchyCode: 'SHL02.SCN03.SEC02.SEC01.ELT05', id: '03951b4d-47dc-327b-b0d3-fab62430d09a', layer: 'instance', name: 'Tenant Health Badge', primeComponent: 'Tag', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-tag-*', '--tp-space-*']};

CREATE (n264:SystemShellGraphNode:ComponentInstance:Instance);
SET n264 += {assetName: 'Tag', assetType: 'tag', code: 'SHL02.SCN03.SEC02.SEC01.ELT05.CP01.TAG', configurationJson: '{}', definitionCode: 'CD.TAG', description: 'Editable instance configuration for the Tag component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '58432caa-4666-3016-b9a6-706a4d9477bc', hierarchyCode: 'SHL02.SCN03.SEC02.SEC01.ELT05.CP01.TAG', id: '6a39c2a4-1016-3d1d-83a3-4a524b3f9525', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-tag.mjs', layer: 'instance', name: 'Tenant Health Badge Tag Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n265:SystemShellGraphNode:Element:Instance);
SET n265 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC02.SEC01.ELT06', controlSource: 'none', defaultState: 'visible', description: 'Short-name slug shown under the banner title.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'b7de1d23-30c4-36be-859d-f636178b6a08', hierarchyCode: 'SHL02.SCN03.SEC02.SEC01.ELT06', id: '24774d55-b583-3018-adb4-68202bc9fa62', layer: 'instance', name: 'Tenant Short Name', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--tp-text-*', '--tp-space-*']};

CREATE (n266:SystemShellGraphNode:Element:Instance);
SET n266 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC02.SEC01.ELT07', controlSource: 'none', defaultState: 'visible', description: 'User-count KPI chip in the banner hero.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'dbbfac6a-0a56-39b9-a492-2073157a774f', hierarchyCode: 'SHL02.SCN03.SEC02.SEC01.ELT07', id: '2abb4a73-3676-3c49-b022-19ffe27fc57b', layer: 'instance', name: 'Users KPI Chip', primeComponent: 'Chip', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-chip-*', '--tp-space-*']};

CREATE (n267:SystemShellGraphNode:ComponentInstance:Instance);
SET n267 += {assetName: 'Chip', assetType: 'chip', code: 'SHL02.SCN03.SEC02.SEC01.ELT07.CP01.CHIP', configurationJson: '{}', definitionCode: 'CD.CHIP', description: 'Editable instance configuration for the Chip component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '279d2e3b-0a80-3ce8-a48d-7b0b0bc7e29a', hierarchyCode: 'SHL02.SCN03.SEC02.SEC01.ELT07.CP01.CHIP', id: '8e36bfa9-0589-3883-a5bb-fc40070526e6', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-chip.mjs', layer: 'instance', name: 'Users KPI Chip Chip Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n268:SystemShellGraphNode:Element:Instance);
SET n268 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC02.SEC01.ELT08', controlSource: 'none', defaultState: 'visible', description: 'Agent-count KPI chip in the banner hero.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '06d55577-e5fa-3fc7-8588-239bd5f93b6e', hierarchyCode: 'SHL02.SCN03.SEC02.SEC01.ELT08', id: '1ee8f9d0-9262-344e-9b37-1dc775eb1cbb', layer: 'instance', name: 'Agents KPI Chip', primeComponent: 'Chip', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-chip-*', '--tp-space-*']};

CREATE (n269:SystemShellGraphNode:ComponentInstance:Instance);
SET n269 += {assetName: 'Chip', assetType: 'chip', code: 'SHL02.SCN03.SEC02.SEC01.ELT08.CP01.CHIP', configurationJson: '{}', definitionCode: 'CD.CHIP', description: 'Editable instance configuration for the Chip component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'd570a1a5-0c0f-3b00-84f3-3dc771755838', hierarchyCode: 'SHL02.SCN03.SEC02.SEC01.ELT08.CP01.CHIP', id: '9573000a-19d4-3da6-9d1d-fdedbc540c56', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-chip.mjs', layer: 'instance', name: 'Agents KPI Chip Chip Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n270:SystemShellGraphNode:Element:Instance);
SET n270 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC02.SEC01.ELT09', controlSource: 'none', defaultState: 'visible', description: 'Object-type-count KPI chip in the banner hero.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '367d3ada-610a-3be2-bc12-3ba16700e83c', hierarchyCode: 'SHL02.SCN03.SEC02.SEC01.ELT09', id: '9725d743-70eb-3860-920b-efe3a4c78010', layer: 'instance', name: 'Object Types KPI Chip', primeComponent: 'Chip', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-chip-*', '--tp-space-*']};

CREATE (n271:SystemShellGraphNode:ComponentInstance:Instance);
SET n271 += {assetName: 'Chip', assetType: 'chip', code: 'SHL02.SCN03.SEC02.SEC01.ELT09.CP01.CHIP', configurationJson: '{}', definitionCode: 'CD.CHIP', description: 'Editable instance configuration for the Chip component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '4bd46265-b314-32a1-b51b-5cd192cedcf6', hierarchyCode: 'SHL02.SCN03.SEC02.SEC01.ELT09.CP01.CHIP', id: 'a53f70d5-2c96-30af-8836-004c5f8a09e2', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-chip.mjs', layer: 'instance', name: 'Object Types KPI Chip Chip Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n272:SystemShellGraphNode:Element:Instance);
SET n272 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC02.SEC01.ELT10', controlSource: 'none', defaultState: 'visible', description: 'License-utilization KPI chip in the banner hero.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '67f14adf-f2f3-31bc-9cf6-6e7860322f84', hierarchyCode: 'SHL02.SCN03.SEC02.SEC01.ELT10', id: '9870ad4d-a96d-3c5f-b10e-7c64eb176bbe', layer: 'instance', name: 'License KPI Chip', primeComponent: 'Chip', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-chip-*', '--tp-space-*']};

CREATE (n273:SystemShellGraphNode:ComponentInstance:Instance);
SET n273 += {assetName: 'Chip', assetType: 'chip', code: 'SHL02.SCN03.SEC02.SEC01.ELT10.CP01.CHIP', configurationJson: '{}', definitionCode: 'CD.CHIP', description: 'Editable instance configuration for the Chip component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '32a2919a-ee5b-3d12-ab96-364dc83316e4', hierarchyCode: 'SHL02.SCN03.SEC02.SEC01.ELT10.CP01.CHIP', id: 'f81fde4b-fae1-3107-9cdb-438de91502ea', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-chip.mjs', layer: 'instance', name: 'License KPI Chip Chip Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n274:SystemShellGraphNode:Instance:Section);
SET n274 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC02.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Tenant fact-sheet action region.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'b24d3c40-c2a4-3a1e-8676-2b09bb2ba647', hierarchyCode: 'SHL02.SCN03.SEC02.SEC02', id: '5d51a348-8b48-3fb8-8ab2-e99371524baa', layer: 'instance', name: 'Tenant Actions Section', renderMode: 'static', repeatable: false, sectionType: 'action_bar', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n275:SystemShellGraphNode:Element:Instance);
SET n275 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC02.SEC02.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Return from the tenant fact sheet to the previous screen.', domain: 'frontend', elementType: 'button', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '49e9e432-bba0-32a4-a97e-1673838d4e73', hierarchyCode: 'SHL02.SCN03.SEC02.SEC02.ELT01', id: 'bb538593-81c6-374c-955e-9580d9a35649', layer: 'instance', name: 'Back Action', primeComponent: 'Button', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-button-*', '--tp-space-*']};

CREATE (n276:SystemShellGraphNode:ComponentInstance:Instance);
SET n276 += {assetName: 'Button', assetType: 'button', code: 'SHL02.SCN03.SEC02.SEC02.ELT01.CP01.BUTTON', configurationJson: '{}', definitionCode: 'CD.BUTTON', description: 'Editable instance configuration for the Button component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '14e356f8-5a0b-3ce0-83ad-6d4ef85e979d', hierarchyCode: 'SHL02.SCN03.SEC02.SEC02.ELT01.CP01.BUTTON', id: '9ce7cd1f-ecd9-3a27-a54a-1841347e16c7', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-button.mjs', layer: 'instance', name: 'Back Action Button Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n277:SystemShellGraphNode:Element:Instance);
SET n277 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC02.SEC02.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Enter tenant factsheet edit mode.', domain: 'frontend', elementType: 'button', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '0edfad2f-e98c-3469-bd85-7005bf83f68f', hierarchyCode: 'SHL02.SCN03.SEC02.SEC02.ELT02', id: '7a8cdbf3-fd94-3de9-9242-2146b25f8b9d', layer: 'instance', name: 'Edit Action', primeComponent: 'Button', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-button-*', '--tp-space-*']};

CREATE (n278:SystemShellGraphNode:ComponentInstance:Instance);
SET n278 += {assetName: 'Button', assetType: 'button', code: 'SHL02.SCN03.SEC02.SEC02.ELT02.CP01.BUTTON', configurationJson: '{}', definitionCode: 'CD.BUTTON', description: 'Editable instance configuration for the Button component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'c60d4b33-9fcc-3c3f-a024-35b4cb087422', hierarchyCode: 'SHL02.SCN03.SEC02.SEC02.ELT02.CP01.BUTTON', id: 'fe4e14c6-f827-3647-aec3-8f659c5becb1', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-button.mjs', layer: 'instance', name: 'Edit Action Button Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n279:SystemShellGraphNode:Element:Instance);
SET n279 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC02.SEC02.ELT03', controlSource: 'none', defaultState: 'visible', description: 'Contextual lifecycle action for the selected tenant.', domain: 'frontend', elementType: 'button', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'b188c81b-336d-36d4-97f1-b04ee2f5172e', hierarchyCode: 'SHL02.SCN03.SEC02.SEC02.ELT03', id: 'd6dc4e75-9f13-3036-916b-33ffe8c6daab', layer: 'instance', name: 'Lifecycle Action', primeComponent: 'Button', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-button-*', '--tp-space-*']};

CREATE (n280:SystemShellGraphNode:ComponentInstance:Instance);
SET n280 += {assetName: 'Button', assetType: 'button', code: 'SHL02.SCN03.SEC02.SEC02.ELT03.CP01.BUTTON', configurationJson: '{}', definitionCode: 'CD.BUTTON', description: 'Editable instance configuration for the Button component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '57a8ba6d-b5ec-3e92-ae26-cecbffe242a8', hierarchyCode: 'SHL02.SCN03.SEC02.SEC02.ELT03.CP01.BUTTON', id: '679bf7d5-3645-3a78-a996-6b6fe03b7c2f', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-button.mjs', layer: 'instance', name: 'Lifecycle Action Button Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n281:SystemShellGraphNode:Instance:Section);
SET n281 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC03', controlSource: 'none', defaultState: 'visible', description: 'Primary factsheet tab bar.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '00125e87-cacc-3800-84e3-e2573acbc942', hierarchyCode: 'SHL02.SCN03.SEC03', id: 'b7c55c43-6798-3324-97b5-7372f66e17ec', layer: 'instance', name: 'Tab Bar Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n282:SystemShellGraphNode:Element:Instance);
SET n282 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC03.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Primary factsheet tab bar.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'a02d06d6-23a2-36eb-afbf-3a2e18a3101b', hierarchyCode: 'SHL02.SCN03.SEC03.ELT01', id: '05c269a5-ab75-3d4d-b23c-086cbc8cb553', layer: 'instance', name: 'Factsheet Tabs', primeComponent: 'Tabs', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-tabs-*', '--tp-space-*']};

CREATE (n283:SystemShellGraphNode:ComponentInstance:Instance);
SET n283 += {assetName: 'Tabs', assetType: 'tabs', code: 'SHL02.SCN03.SEC03.ELT01.CP01.TABS', configurationJson: '{}', definitionCode: 'CD.TABS', description: 'Editable instance configuration for the Tabs component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '9e548286-f4dd-3595-bfaa-04419ea01363', hierarchyCode: 'SHL02.SCN03.SEC03.ELT01.CP01.TABS', id: '8a7f26c9-1145-3a36-aa7a-4e26980ac35e', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-tabs.mjs', layer: 'instance', name: 'Factsheet Tabs Tabs Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n284:SystemShellGraphNode:Instance:Section);
SET n284 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC04', controlSource: 'none', defaultState: 'visible', description: 'Active users tab workspace.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '5f45cd67-7198-3aea-822e-0aa949edf857', hierarchyCode: 'SHL02.SCN03.SEC04', id: '8c628228-48e1-3fed-bd18-97dd90e83878', layer: 'instance', name: 'Users Tab Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n285:SystemShellGraphNode:Instance:Section);
SET n285 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC04.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Users-tab search and actions toolbar.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '38e49f7c-2f2f-3e48-ad69-da7d6b0bfd71', hierarchyCode: 'SHL02.SCN03.SEC04.SEC01', id: 'f68f9a0c-16bd-3433-a29f-7aef6d0c5e9c', layer: 'instance', name: 'Users Toolbar Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n286:SystemShellGraphNode:Element:Instance);
SET n286 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC04.SEC01.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Search field for tenant users.', domain: 'frontend', elementType: 'input', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '512e76b2-61b0-389a-82c0-f8f2265ec247', hierarchyCode: 'SHL02.SCN03.SEC04.SEC01.ELT01', id: '757ee4c2-f9ff-321a-830a-aa61bd96ed48', layer: 'instance', name: 'Users Search Input', primeComponent: 'InputText', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-inputtext-*', '--tp-space-*']};

CREATE (n287:SystemShellGraphNode:ComponentInstance:Instance);
SET n287 += {assetName: 'InputText', assetType: 'inputtext', code: 'SHL02.SCN03.SEC04.SEC01.ELT01.CP01.INPUTTEXT', configurationJson: '{}', definitionCode: 'CD.INPUTTEXT', description: 'Editable instance configuration for the InputText component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '0876beb7-5164-3683-9f23-85a93b666843', hierarchyCode: 'SHL02.SCN03.SEC04.SEC01.ELT01.CP01.INPUTTEXT', id: '45f75537-e000-343c-a075-06319638788f', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-inputtext.mjs', layer: 'instance', name: 'Users Search Input InputText Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n288:SystemShellGraphNode:Element:Instance);
SET n288 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC04.SEC01.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Filter toggle for users tab.', domain: 'frontend', elementType: 'toggle', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'cd546001-bd08-3048-9165-037de5e2aba9', hierarchyCode: 'SHL02.SCN03.SEC04.SEC01.ELT02', id: '34e169f1-842b-3c9b-ba3f-4cb0287ff562', layer: 'instance', name: 'Users Filter Toggle', primeComponent: 'Button', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-button-*', '--tp-space-*']};

CREATE (n289:SystemShellGraphNode:ComponentInstance:Instance);
SET n289 += {assetName: 'Button', assetType: 'button', code: 'SHL02.SCN03.SEC04.SEC01.ELT02.CP01.BUTTON', configurationJson: '{}', definitionCode: 'CD.BUTTON', description: 'Editable instance configuration for the Button component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '91f8bfde-c107-308f-a029-6dea1b418fa0', hierarchyCode: 'SHL02.SCN03.SEC04.SEC01.ELT02.CP01.BUTTON', id: '55493557-7a39-38b2-8e74-8689a8806683', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-button.mjs', layer: 'instance', name: 'Users Filter Toggle Button Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n290:SystemShellGraphNode:Element:Instance);
SET n290 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC04.SEC01.ELT03', controlSource: 'none', defaultState: 'visible', description: 'Primary action for inviting a tenant user.', domain: 'frontend', elementType: 'button', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '4417611c-9736-3c02-bd75-2b619661b43a', hierarchyCode: 'SHL02.SCN03.SEC04.SEC01.ELT03', id: 'bd799bd1-d352-3670-9321-18dfa011ad85', layer: 'instance', name: 'Invite User Action', primeComponent: 'Button', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-button-*', '--tp-space-*']};

CREATE (n291:SystemShellGraphNode:ComponentInstance:Instance);
SET n291 += {assetName: 'Button', assetType: 'button', code: 'SHL02.SCN03.SEC04.SEC01.ELT03.CP01.BUTTON', configurationJson: '{}', definitionCode: 'CD.BUTTON', description: 'Editable instance configuration for the Button component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'c1abae65-ecfa-3bb4-b2de-66d2de581f67', hierarchyCode: 'SHL02.SCN03.SEC04.SEC01.ELT03.CP01.BUTTON', id: '39a80174-01dd-3bd6-b984-3b6314cf4c71', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-button.mjs', layer: 'instance', name: 'Invite User Action Button Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n292:SystemShellGraphNode:Instance:Section);
SET n292 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC04.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Users table and paging region.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '70f62dfb-ece8-3c9b-bd77-3377bd8741b6', hierarchyCode: 'SHL02.SCN03.SEC04.SEC02', id: 'a16a7175-27d4-3996-a8db-949037f795ab', layer: 'instance', name: 'Users Results Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n293:SystemShellGraphNode:Element:Instance);
SET n293 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC04.SEC02.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Tabular users listing for the tenant.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '65f1d357-c36a-3d45-ab9f-7f20ce5eeda8', hierarchyCode: 'SHL02.SCN03.SEC04.SEC02.ELT01', id: '52f23d47-8b50-3c5d-bf0f-a3f2369cd7a6', layer: 'instance', name: 'Users Table', primeComponent: 'Table', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-datatable-*', '--tp-space-*']};

CREATE (n294:SystemShellGraphNode:ComponentInstance:Instance);
SET n294 += {assetName: 'Table', assetType: 'table', code: 'SHL02.SCN03.SEC04.SEC02.ELT01.CP01.TABLE', configurationJson: '{}', definitionCode: 'CD.TABLE', description: 'Editable instance configuration for the Table component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '575fdf14-a532-3fb1-82f5-aaf1b4498296', hierarchyCode: 'SHL02.SCN03.SEC04.SEC02.ELT01.CP01.TABLE', id: '15acdbbd-4fdc-3b23-870f-7ac29bd27a4e', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-table.mjs', layer: 'instance', name: 'Users Table Table Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n295:SystemShellGraphNode:Element:Instance);
SET n295 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC04.SEC02.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Paginator for the users table.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'b10919fd-04da-3bcf-a636-5e342cc4cc16', hierarchyCode: 'SHL02.SCN03.SEC04.SEC02.ELT02', id: 'db8661e5-130b-3f20-a916-ecdfd10f3bd3', layer: 'instance', name: 'Users Paginator', primeComponent: 'Paginator', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-paginator-*', '--tp-space-*']};

CREATE (n296:SystemShellGraphNode:ComponentInstance:Instance);
SET n296 += {assetName: 'Paginator', assetType: 'paginator', code: 'SHL02.SCN03.SEC04.SEC02.ELT02.CP01.PAGINATOR', configurationJson: '{}', definitionCode: 'CD.PAGINATOR', description: 'Editable instance configuration for the Paginator component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'f39627af-7168-3b0e-a1c7-42ab3bf6c61c', hierarchyCode: 'SHL02.SCN03.SEC04.SEC02.ELT02.CP01.PAGINATOR', id: '3980dc7d-38d5-325a-a4fd-25112d9ddf71', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-paginator.mjs', layer: 'instance', name: 'Users Paginator Paginator Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n297:SystemShellGraphNode:Element:Instance);
SET n297 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC04.SEC02.ELT03', controlSource: 'none', defaultState: 'visible', description: 'Representative user status badge within the users table.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'e52a12bf-cc83-300c-b7bf-262272c199b1', hierarchyCode: 'SHL02.SCN03.SEC04.SEC02.ELT03', id: 'ce0ae0b5-dab2-321d-8078-e4c4faa1f4e3', layer: 'instance', name: 'User Status Badge', primeComponent: 'Tag', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-tag-*', '--tp-space-*']};

CREATE (n298:SystemShellGraphNode:ComponentInstance:Instance);
SET n298 += {assetName: 'Tag', assetType: 'tag', code: 'SHL02.SCN03.SEC04.SEC02.ELT03.CP01.TAG', configurationJson: '{}', definitionCode: 'CD.TAG', description: 'Editable instance configuration for the Tag component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '9c2996d8-4c9c-314e-8ce3-c08f22510cc0', hierarchyCode: 'SHL02.SCN03.SEC04.SEC02.ELT03.CP01.TAG', id: '00f2fa38-58c4-3185-aedc-0f021f419a47', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-tag.mjs', layer: 'instance', name: 'User Status Badge Tag Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n299:SystemShellGraphNode:Instance:Section);
SET n299 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC05', controlSource: 'none', defaultState: 'visible', description: 'Branding tab workspace.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '0c494f18-4a13-3650-9094-019737db731b', hierarchyCode: 'SHL02.SCN03.SEC05', id: 'eec4c05e-6947-37b1-9c8c-a95dcd664d5c', layer: 'instance', name: 'Branding Tab Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n300:SystemShellGraphNode:Element:Instance);
SET n300 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC05.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Preview action for branding changes.', domain: 'frontend', elementType: 'button', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'f8b4cd6a-c6f0-30d9-855a-32c358b14da9', hierarchyCode: 'SHL02.SCN03.SEC05.ELT01', id: '89ee93d5-2d14-35bc-b5aa-08b1e213a01f', layer: 'instance', name: 'Branding Preview Action', primeComponent: 'Button', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-button-*', '--tp-space-*']};

CREATE (n301:SystemShellGraphNode:ComponentInstance:Instance);
SET n301 += {assetName: 'Button', assetType: 'button', code: 'SHL02.SCN03.SEC05.ELT01.CP01.BUTTON', configurationJson: '{}', definitionCode: 'CD.BUTTON', description: 'Editable instance configuration for the Button component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'fc935bfa-aca2-39ae-9eba-b2e20b61ea2e', hierarchyCode: 'SHL02.SCN03.SEC05.ELT01.CP01.BUTTON', id: 'fb75e895-7957-350a-b04a-dbec0cf96fc2', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-button.mjs', layer: 'instance', name: 'Branding Preview Action Button Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n302:SystemShellGraphNode:Element:Instance);
SET n302 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC05.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Publish action for branding changes.', domain: 'frontend', elementType: 'button', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'db4cbc94-a07f-3630-9403-304d7387153c', hierarchyCode: 'SHL02.SCN03.SEC05.ELT02', id: '8759e72c-b117-356d-b13d-8ada560ecea0', layer: 'instance', name: 'Branding Publish Action', primeComponent: 'Button', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-button-*', '--tp-space-*']};

CREATE (n303:SystemShellGraphNode:ComponentInstance:Instance);
SET n303 += {assetName: 'Button', assetType: 'button', code: 'SHL02.SCN03.SEC05.ELT02.CP01.BUTTON', configurationJson: '{}', definitionCode: 'CD.BUTTON', description: 'Editable instance configuration for the Button component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '169ae3e6-ffe4-3bbb-993a-d2c8f4172a1f', hierarchyCode: 'SHL02.SCN03.SEC05.ELT02.CP01.BUTTON', id: 'a75117be-5cc6-31a7-aa34-908f83c4f71f', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-button.mjs', layer: 'instance', name: 'Branding Publish Action Button Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n304:SystemShellGraphNode:Instance:Section);
SET n304 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC06', controlSource: 'none', defaultState: 'visible', description: 'Integrations tab workspace.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '15551d39-7ea0-3613-a784-baef1a54bb15', hierarchyCode: 'SHL02.SCN03.SEC06', id: '5e9cbf48-8894-31f4-9f7b-ad8991d131ce', layer: 'instance', name: 'Integrations Tab Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n305:SystemShellGraphNode:Instance:Section);
SET n305 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC06.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Integrations tab toolbar.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'd2ca5baf-3221-35c9-9d31-039ac5c61917', hierarchyCode: 'SHL02.SCN03.SEC06.SEC01', id: '57c92823-f51e-3e72-ab17-0ab25be29159', layer: 'instance', name: 'Integrations Toolbar Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n306:SystemShellGraphNode:Element:Instance);
SET n306 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC06.SEC01.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Search field for integrations.', domain: 'frontend', elementType: 'input', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '5249913a-889d-32ee-baa2-2bf401baff8b', hierarchyCode: 'SHL02.SCN03.SEC06.SEC01.ELT01', id: '35ddd352-3d20-3f6e-a19f-598ba17fd1bb', layer: 'instance', name: 'Integrations Search Input', primeComponent: 'InputText', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-inputtext-*', '--tp-space-*']};

CREATE (n307:SystemShellGraphNode:ComponentInstance:Instance);
SET n307 += {assetName: 'InputText', assetType: 'inputtext', code: 'SHL02.SCN03.SEC06.SEC01.ELT01.CP01.INPUTTEXT', configurationJson: '{}', definitionCode: 'CD.INPUTTEXT', description: 'Editable instance configuration for the InputText component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '934eb71d-45ea-32b2-8fdd-880e125094da', hierarchyCode: 'SHL02.SCN03.SEC06.SEC01.ELT01.CP01.INPUTTEXT', id: '1dbd66c8-658d-38b6-9370-c95fda63e665', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-inputtext.mjs', layer: 'instance', name: 'Integrations Search Input InputText Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n308:SystemShellGraphNode:Element:Instance);
SET n308 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC06.SEC01.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Filter toggle for integrations.', domain: 'frontend', elementType: 'toggle', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '9a0baea9-b10e-3bb3-8445-eff459c50eaa', hierarchyCode: 'SHL02.SCN03.SEC06.SEC01.ELT02', id: '34dad4d7-6bb9-3314-92db-00e8597b3fe1', layer: 'instance', name: 'Integrations Filter Toggle', primeComponent: 'Button', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-button-*', '--tp-space-*']};

CREATE (n309:SystemShellGraphNode:ComponentInstance:Instance);
SET n309 += {assetName: 'Button', assetType: 'button', code: 'SHL02.SCN03.SEC06.SEC01.ELT02.CP01.BUTTON', configurationJson: '{}', definitionCode: 'CD.BUTTON', description: 'Editable instance configuration for the Button component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'b71daac9-1b99-3592-9fb4-62f212c0268e', hierarchyCode: 'SHL02.SCN03.SEC06.SEC01.ELT02.CP01.BUTTON', id: 'a813dea0-6cb8-3a32-9fb9-4afe57019b30', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-button.mjs', layer: 'instance', name: 'Integrations Filter Toggle Button Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n310:SystemShellGraphNode:Element:Instance);
SET n310 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC06.SEC01.ELT03', controlSource: 'none', defaultState: 'visible', description: 'Grid/table toggle for integrations.', domain: 'frontend', elementType: 'toggle', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '421706bc-a292-3dbe-b49d-ec4b38d1c150', hierarchyCode: 'SHL02.SCN03.SEC06.SEC01.ELT03', id: 'bf51b828-02fb-3af0-ae7b-4008badbb832', layer: 'instance', name: 'Integrations View Toggle', primeComponent: 'ToggleButton', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-togglebutton-*', '--tp-space-*']};

CREATE (n311:SystemShellGraphNode:ComponentInstance:Instance);
SET n311 += {assetName: 'ToggleButton', assetType: 'togglebutton', code: 'SHL02.SCN03.SEC06.SEC01.ELT03.CP01.TOGGLEBUTTON', configurationJson: '{}', definitionCode: 'CD.TOGGLEBUTTON', description: 'Editable instance configuration for the ToggleButton component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'dc257906-dca7-307f-8fa6-def7cc7bae91', hierarchyCode: 'SHL02.SCN03.SEC06.SEC01.ELT03.CP01.TOGGLEBUTTON', id: 'c0a4b995-28e3-3c63-bbde-643cafb3d093', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-togglebutton.mjs', layer: 'instance', name: 'Integrations View Toggle ToggleButton Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n312:SystemShellGraphNode:Instance:Section);
SET n312 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC06.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Integrations cards and paging.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '23e1b291-80c8-3fdf-9c8b-2f78fce093e9', hierarchyCode: 'SHL02.SCN03.SEC06.SEC02', id: '4de6b554-7a13-392b-be5a-a1b69e54a76f', layer: 'instance', name: 'Integrations Results Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n313:SystemShellGraphNode:Element:Instance);
SET n313 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC06.SEC02.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Representative protocol badge in the integrations grid.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '5fcb513c-5b9e-3565-89c4-1a5a140c926a', hierarchyCode: 'SHL02.SCN03.SEC06.SEC02.ELT01', id: 'fa148c82-fcd3-33ca-a889-47411a89d3c3', layer: 'instance', name: 'Integration Protocol Badge', primeComponent: 'Tag', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-tag-*', '--tp-space-*']};

CREATE (n314:SystemShellGraphNode:ComponentInstance:Instance);
SET n314 += {assetName: 'Tag', assetType: 'tag', code: 'SHL02.SCN03.SEC06.SEC02.ELT01.CP01.TAG', configurationJson: '{}', definitionCode: 'CD.TAG', description: 'Editable instance configuration for the Tag component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'c4722a3b-bc55-37b4-b90f-53f11cef806d', hierarchyCode: 'SHL02.SCN03.SEC06.SEC02.ELT01.CP01.TAG', id: '293b4d5a-5dbe-39a1-902c-cedb51a9e50f', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-tag.mjs', layer: 'instance', name: 'Integration Protocol Badge Tag Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n315:SystemShellGraphNode:Element:Instance);
SET n315 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC06.SEC02.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Representative enabled/disabled badge in the integrations grid.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'fa597d91-71dd-3798-a3f6-2584494d1abe', hierarchyCode: 'SHL02.SCN03.SEC06.SEC02.ELT02', id: '00496877-a733-367a-b0a9-a1fde56d4dd4', layer: 'instance', name: 'Integration Status Badge', primeComponent: 'Tag', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-tag-*', '--tp-space-*']};

CREATE (n316:SystemShellGraphNode:ComponentInstance:Instance);
SET n316 += {assetName: 'Tag', assetType: 'tag', code: 'SHL02.SCN03.SEC06.SEC02.ELT02.CP01.TAG', configurationJson: '{}', definitionCode: 'CD.TAG', description: 'Editable instance configuration for the Tag component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '96269ef4-ecc3-38fa-8dc2-23a453f428c9', hierarchyCode: 'SHL02.SCN03.SEC06.SEC02.ELT02.CP01.TAG', id: 'c68e18f9-253f-3844-8bac-f6fcba1cadec', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-tag.mjs', layer: 'instance', name: 'Integration Status Badge Tag Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n317:SystemShellGraphNode:Element:Instance);
SET n317 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC06.SEC02.ELT03', controlSource: 'none', defaultState: 'visible', description: 'Paginator for integrations results.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '479fa24f-46ab-3d72-b921-c7aafd335bb5', hierarchyCode: 'SHL02.SCN03.SEC06.SEC02.ELT03', id: 'f4e01ed0-b90e-322c-b73e-92bbf146def4', layer: 'instance', name: 'Integrations Paginator', primeComponent: 'Paginator', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-paginator-*', '--tp-space-*']};

CREATE (n318:SystemShellGraphNode:ComponentInstance:Instance);
SET n318 += {assetName: 'Paginator', assetType: 'paginator', code: 'SHL02.SCN03.SEC06.SEC02.ELT03.CP01.PAGINATOR', configurationJson: '{}', definitionCode: 'CD.PAGINATOR', description: 'Editable instance configuration for the Paginator component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'aab2b9b0-a44e-30c7-a981-e4003c6684ce', hierarchyCode: 'SHL02.SCN03.SEC06.SEC02.ELT03.CP01.PAGINATOR', id: '8a0d44da-e091-30dc-b75c-5482d721aa56', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-paginator.mjs', layer: 'instance', name: 'Integrations Paginator Paginator Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n319:SystemShellGraphNode:Instance:Section);
SET n319 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC07', controlSource: 'none', defaultState: 'visible', description: 'Dictionary tab workspace.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '453b297e-2ad5-3bd3-8b15-6c57dd6ee2a1', hierarchyCode: 'SHL02.SCN03.SEC07', id: 'b7e872bb-b01a-3bd5-bb40-6d18aa466a7d', layer: 'instance', name: 'Dictionary Tab Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n320:SystemShellGraphNode:Instance:Section);
SET n320 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC07.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Dictionary tab toolbar.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '46c63561-31a4-3573-a042-5c664f2df745', hierarchyCode: 'SHL02.SCN03.SEC07.SEC01', id: 'ae8ec334-b77d-3b82-bea4-c37f2fefb356', layer: 'instance', name: 'Dictionary Toolbar Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n321:SystemShellGraphNode:Element:Instance);
SET n321 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC07.SEC01.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Search field for dictionary entries.', domain: 'frontend', elementType: 'input', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'eb35eb03-7745-3868-a9b5-aa46f2d10fb6', hierarchyCode: 'SHL02.SCN03.SEC07.SEC01.ELT01', id: '2f0bbb13-5b15-3996-8ccb-561e21809f59', layer: 'instance', name: 'Dictionary Search Input', primeComponent: 'InputText', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-inputtext-*', '--tp-space-*']};

CREATE (n322:SystemShellGraphNode:ComponentInstance:Instance);
SET n322 += {assetName: 'InputText', assetType: 'inputtext', code: 'SHL02.SCN03.SEC07.SEC01.ELT01.CP01.INPUTTEXT', configurationJson: '{}', definitionCode: 'CD.INPUTTEXT', description: 'Editable instance configuration for the InputText component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '796949ca-815b-3bc9-8d1b-5cec99f4973c', hierarchyCode: 'SHL02.SCN03.SEC07.SEC01.ELT01.CP01.INPUTTEXT', id: '41dd4d74-dcc7-334f-936f-dce193b95585', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-inputtext.mjs', layer: 'instance', name: 'Dictionary Search Input InputText Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n323:SystemShellGraphNode:Element:Instance);
SET n323 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC07.SEC01.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Filter toggle for dictionary results.', domain: 'frontend', elementType: 'toggle', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '3190599d-6d54-341b-b250-87f50d36706b', hierarchyCode: 'SHL02.SCN03.SEC07.SEC01.ELT02', id: '592cb812-fe87-301a-865e-7f62237d5ee5', layer: 'instance', name: 'Dictionary Filter Toggle', primeComponent: 'Button', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-button-*', '--tp-space-*']};

CREATE (n324:SystemShellGraphNode:ComponentInstance:Instance);
SET n324 += {assetName: 'Button', assetType: 'button', code: 'SHL02.SCN03.SEC07.SEC01.ELT02.CP01.BUTTON', configurationJson: '{}', definitionCode: 'CD.BUTTON', description: 'Editable instance configuration for the Button component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '68c9ba41-c64f-31eb-a1ca-c1085f74bbf4', hierarchyCode: 'SHL02.SCN03.SEC07.SEC01.ELT02.CP01.BUTTON', id: 'ab42451e-ed60-36b4-b64c-b4c33d7be585', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-button.mjs', layer: 'instance', name: 'Dictionary Filter Toggle Button Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n325:SystemShellGraphNode:Element:Instance);
SET n325 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC07.SEC01.ELT03', controlSource: 'none', defaultState: 'visible', description: 'Grid/table toggle for dictionary results.', domain: 'frontend', elementType: 'toggle', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'ad92fd70-6314-369a-ad31-4c6f845989bc', hierarchyCode: 'SHL02.SCN03.SEC07.SEC01.ELT03', id: '49db51ad-cc99-3d6a-aa5d-d5067650bf69', layer: 'instance', name: 'Dictionary View Toggle', primeComponent: 'ToggleButton', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-togglebutton-*', '--tp-space-*']};

CREATE (n326:SystemShellGraphNode:ComponentInstance:Instance);
SET n326 += {assetName: 'ToggleButton', assetType: 'togglebutton', code: 'SHL02.SCN03.SEC07.SEC01.ELT03.CP01.TOGGLEBUTTON', configurationJson: '{}', definitionCode: 'CD.TOGGLEBUTTON', description: 'Editable instance configuration for the ToggleButton component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '01b17e5a-ff6e-305e-a5ba-1ba3e6bb9d73', hierarchyCode: 'SHL02.SCN03.SEC07.SEC01.ELT03.CP01.TOGGLEBUTTON', id: 'a53610b5-ef56-3c23-91bd-e7675d6c306e', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-togglebutton.mjs', layer: 'instance', name: 'Dictionary View Toggle ToggleButton Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n327:SystemShellGraphNode:Instance:Section);
SET n327 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC07.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Dictionary results and paging.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '95a18868-4da8-3008-89f0-af43cb53776c', hierarchyCode: 'SHL02.SCN03.SEC07.SEC02', id: 'f05a0f64-4444-3b0e-895e-4436183ccc3f', layer: 'instance', name: 'Dictionary Results Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n328:SystemShellGraphNode:Element:Instance);
SET n328 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC07.SEC02.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Tabular dictionary view.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'dfc4336d-bd08-3f04-b853-7a1845c9d0d7', hierarchyCode: 'SHL02.SCN03.SEC07.SEC02.ELT01', id: '678d90bb-7e93-3ccc-b577-b65dee3e24a1', layer: 'instance', name: 'Dictionary Table', primeComponent: 'Table', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-datatable-*', '--tp-space-*']};

CREATE (n329:SystemShellGraphNode:ComponentInstance:Instance);
SET n329 += {assetName: 'Table', assetType: 'table', code: 'SHL02.SCN03.SEC07.SEC02.ELT01.CP01.TABLE', configurationJson: '{}', definitionCode: 'CD.TABLE', description: 'Editable instance configuration for the Table component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'e92ebf19-3dd3-3743-ac5b-ec84a0389218', hierarchyCode: 'SHL02.SCN03.SEC07.SEC02.ELT01.CP01.TABLE', id: '69ec767a-5922-360a-a5da-e2001d5497cd', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-table.mjs', layer: 'instance', name: 'Dictionary Table Table Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n330:SystemShellGraphNode:Element:Instance);
SET n330 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC07.SEC02.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Representative origin badge in dictionary results.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'e3ee5134-c05b-39ee-9468-b049674ed137', hierarchyCode: 'SHL02.SCN03.SEC07.SEC02.ELT02', id: 'a8b4c3ff-8d8b-31bb-b710-09e5329a60c0', layer: 'instance', name: 'Dictionary Origin Badge', primeComponent: 'Tag', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-tag-*', '--tp-space-*']};

CREATE (n331:SystemShellGraphNode:ComponentInstance:Instance);
SET n331 += {assetName: 'Tag', assetType: 'tag', code: 'SHL02.SCN03.SEC07.SEC02.ELT02.CP01.TAG', configurationJson: '{}', definitionCode: 'CD.TAG', description: 'Editable instance configuration for the Tag component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'e7e4a3d3-28db-342d-b534-aa06477668c8', hierarchyCode: 'SHL02.SCN03.SEC07.SEC02.ELT02.CP01.TAG', id: '4ccb72dc-6145-3561-96ae-16b3f88816f4', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-tag.mjs', layer: 'instance', name: 'Dictionary Origin Badge Tag Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n332:SystemShellGraphNode:Element:Instance);
SET n332 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC07.SEC02.ELT03', controlSource: 'none', defaultState: 'visible', description: 'Paginator for dictionary results.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '597b8501-2a28-3041-b362-dd31ff691f59', hierarchyCode: 'SHL02.SCN03.SEC07.SEC02.ELT03', id: '88ca190c-2d9c-3ca6-8498-319be329becb', layer: 'instance', name: 'Dictionary Paginator', primeComponent: 'Paginator', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-paginator-*', '--tp-space-*']};

CREATE (n333:SystemShellGraphNode:ComponentInstance:Instance);
SET n333 += {assetName: 'Paginator', assetType: 'paginator', code: 'SHL02.SCN03.SEC07.SEC02.ELT03.CP01.PAGINATOR', configurationJson: '{}', definitionCode: 'CD.PAGINATOR', description: 'Editable instance configuration for the Paginator component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '0dc15679-f53b-3016-b214-f3c8239e7710', hierarchyCode: 'SHL02.SCN03.SEC07.SEC02.ELT03.CP01.PAGINATOR', id: '9f8865bd-d093-3ae3-8f60-a7a5755eee5e', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-paginator.mjs', layer: 'instance', name: 'Dictionary Paginator Paginator Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n334:SystemShellGraphNode:Instance:Section);
SET n334 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC08', controlSource: 'none', defaultState: 'visible', description: 'Agents tab workspace.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '972b23a1-6b32-3deb-b62d-67d3d9a26468', hierarchyCode: 'SHL02.SCN03.SEC08', id: 'e1ce00b8-1867-381c-b74c-8a61670b7642', layer: 'instance', name: 'Agents Tab Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n335:SystemShellGraphNode:Element:Instance);
SET n335 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC08.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Representative agent status badge in the agents grid.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'a95d3a7c-c3cc-34dc-a503-129d1a7967b7', hierarchyCode: 'SHL02.SCN03.SEC08.ELT01', id: '60c9e9c4-d7a0-3a7e-9847-8fdd572d41ea', layer: 'instance', name: 'Agent Status Badge', primeComponent: 'Tag', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-tag-*', '--tp-space-*']};

CREATE (n336:SystemShellGraphNode:ComponentInstance:Instance);
SET n336 += {assetName: 'Tag', assetType: 'tag', code: 'SHL02.SCN03.SEC08.ELT01.CP01.TAG', configurationJson: '{}', definitionCode: 'CD.TAG', description: 'Editable instance configuration for the Tag component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '6287d109-8bf3-3fd8-9e63-79cb0f5c459e', hierarchyCode: 'SHL02.SCN03.SEC08.ELT01.CP01.TAG', id: '8ce9f162-c2dc-373c-a859-ab4723c0e668', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-tag.mjs', layer: 'instance', name: 'Agent Status Badge Tag Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n337:SystemShellGraphNode:Instance:Section);
SET n337 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC09', controlSource: 'none', defaultState: 'visible', description: 'Studio tab workspace.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '101776a7-dcdd-3206-b675-adc1ba63ae91', hierarchyCode: 'SHL02.SCN03.SEC09', id: '905ac2b7-b481-380a-89b1-b48f916f8cab', layer: 'instance', name: 'Studio Tab Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n338:SystemShellGraphNode:Element:Instance);
SET n338 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC09.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Search field for studio processes.', domain: 'frontend', elementType: 'input', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '7a7d5b51-c86f-36a4-a718-3537e737b130', hierarchyCode: 'SHL02.SCN03.SEC09.ELT01', id: '1bd6c18e-afa5-33e2-adbc-864db4ef53d0', layer: 'instance', name: 'Studio Search Input', primeComponent: 'InputText', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-inputtext-*', '--tp-space-*']};

CREATE (n339:SystemShellGraphNode:ComponentInstance:Instance);
SET n339 += {assetName: 'InputText', assetType: 'inputtext', code: 'SHL02.SCN03.SEC09.ELT01.CP01.INPUTTEXT', configurationJson: '{}', definitionCode: 'CD.INPUTTEXT', description: 'Editable instance configuration for the InputText component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'c4add895-f92d-3e64-9448-be23b3cb7fda', hierarchyCode: 'SHL02.SCN03.SEC09.ELT01.CP01.INPUTTEXT', id: '4ec3b413-9e2b-36eb-99b5-f2410733ef46', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-inputtext.mjs', layer: 'instance', name: 'Studio Search Input InputText Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n340:SystemShellGraphNode:Element:Instance);
SET n340 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC09.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Action to open Studio.', domain: 'frontend', elementType: 'button', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '315716c3-1206-3ff1-835c-4d22420c25fb', hierarchyCode: 'SHL02.SCN03.SEC09.ELT02', id: 'f3bc6239-06c9-33e0-ab78-64e710d6718a', layer: 'instance', name: 'Open Studio Action', primeComponent: 'Button', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-button-*', '--tp-space-*']};

CREATE (n341:SystemShellGraphNode:ComponentInstance:Instance);
SET n341 += {assetName: 'Button', assetType: 'button', code: 'SHL02.SCN03.SEC09.ELT02.CP01.BUTTON', configurationJson: '{}', definitionCode: 'CD.BUTTON', description: 'Editable instance configuration for the Button component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'c005e283-6c27-3be9-9f48-79384f5e1157', hierarchyCode: 'SHL02.SCN03.SEC09.ELT02.CP01.BUTTON', id: 'e9e6b030-cc41-31c8-812e-0a42e279b432', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-button.mjs', layer: 'instance', name: 'Open Studio Action Button Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n342:SystemShellGraphNode:Instance:Section);
SET n342 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC10', controlSource: 'none', defaultState: 'visible', description: 'Audit log tab workspace.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '5b0991df-8ae8-33da-bcc1-bb0870b3b543', hierarchyCode: 'SHL02.SCN03.SEC10', id: 'b5de1763-d199-3248-897f-4a4af50eff6e', layer: 'instance', name: 'Audit Tab Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n343:SystemShellGraphNode:Instance:Section);
SET n343 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC10.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Audit tab toolbar.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'c2d82145-7557-3158-8aae-464a17f769b1', hierarchyCode: 'SHL02.SCN03.SEC10.SEC01', id: '610874a5-63d5-38f9-8429-2f712645eb18', layer: 'instance', name: 'Audit Toolbar Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n344:SystemShellGraphNode:Element:Instance);
SET n344 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC10.SEC01.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Search field for audit log entries.', domain: 'frontend', elementType: 'input', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '4f835716-30d9-39bf-ba83-5c98fcc293c6', hierarchyCode: 'SHL02.SCN03.SEC10.SEC01.ELT01', id: '343f5538-c829-3089-9f5f-ba7aea9c74f0', layer: 'instance', name: 'Audit Search Input', primeComponent: 'InputText', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-inputtext-*', '--tp-space-*']};

CREATE (n345:SystemShellGraphNode:ComponentInstance:Instance);
SET n345 += {assetName: 'InputText', assetType: 'inputtext', code: 'SHL02.SCN03.SEC10.SEC01.ELT01.CP01.INPUTTEXT', configurationJson: '{}', definitionCode: 'CD.INPUTTEXT', description: 'Editable instance configuration for the InputText component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '9649f807-308b-3ee0-851d-30a31e5998fe', hierarchyCode: 'SHL02.SCN03.SEC10.SEC01.ELT01.CP01.INPUTTEXT', id: 'b4123e81-ad70-394f-bbf3-96fbb34cc065', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-inputtext.mjs', layer: 'instance', name: 'Audit Search Input InputText Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n346:SystemShellGraphNode:Element:Instance);
SET n346 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC10.SEC01.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Filter toggle for audit results.', domain: 'frontend', elementType: 'toggle', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '99f2c8fe-0945-3a63-9f18-3cbb1f9c842e', hierarchyCode: 'SHL02.SCN03.SEC10.SEC01.ELT02', id: '504e9247-c95f-3528-bd3d-7eade7063584', layer: 'instance', name: 'Audit Filter Toggle', primeComponent: 'Button', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-button-*', '--tp-space-*']};

CREATE (n347:SystemShellGraphNode:ComponentInstance:Instance);
SET n347 += {assetName: 'Button', assetType: 'button', code: 'SHL02.SCN03.SEC10.SEC01.ELT02.CP01.BUTTON', configurationJson: '{}', definitionCode: 'CD.BUTTON', description: 'Editable instance configuration for the Button component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'fa46101d-e750-3c03-95c9-5805d5b6b982', hierarchyCode: 'SHL02.SCN03.SEC10.SEC01.ELT02.CP01.BUTTON', id: '69f438d5-ae6a-315e-9902-6b10e0f0f7fa', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-button.mjs', layer: 'instance', name: 'Audit Filter Toggle Button Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n348:SystemShellGraphNode:Element:Instance);
SET n348 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC10.SEC01.ELT03', controlSource: 'none', defaultState: 'visible', description: 'Grid/table toggle for audit results.', domain: 'frontend', elementType: 'toggle', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '6c4a4304-f066-30d0-a824-1e8d38d9e8c4', hierarchyCode: 'SHL02.SCN03.SEC10.SEC01.ELT03', id: '220c45f4-57e3-39d4-82b9-956032c36b0e', layer: 'instance', name: 'Audit View Toggle', primeComponent: 'ToggleButton', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-togglebutton-*', '--tp-space-*']};

CREATE (n349:SystemShellGraphNode:ComponentInstance:Instance);
SET n349 += {assetName: 'ToggleButton', assetType: 'togglebutton', code: 'SHL02.SCN03.SEC10.SEC01.ELT03.CP01.TOGGLEBUTTON', configurationJson: '{}', definitionCode: 'CD.TOGGLEBUTTON', description: 'Editable instance configuration for the ToggleButton component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '331bf687-7a8c-3f0a-a496-9ed65c0a7d26', hierarchyCode: 'SHL02.SCN03.SEC10.SEC01.ELT03.CP01.TOGGLEBUTTON', id: 'f555277b-6b05-361d-a151-6561374c47de', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-togglebutton.mjs', layer: 'instance', name: 'Audit View Toggle ToggleButton Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n350:SystemShellGraphNode:Instance:Section);
SET n350 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC10.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Audit results and paging.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'fe5920c2-8f62-3795-a3b8-faeeee4531f2', hierarchyCode: 'SHL02.SCN03.SEC10.SEC02', id: 'ed561332-4486-3247-84f2-27e97abc659a', layer: 'instance', name: 'Audit Results Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n351:SystemShellGraphNode:Element:Instance);
SET n351 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC10.SEC02.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Tabular audit log view.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '389060a0-a1d2-3fe4-96a6-66a33615cb35', hierarchyCode: 'SHL02.SCN03.SEC10.SEC02.ELT01', id: 'bb374442-5ed5-396b-a136-5168db04ee17', layer: 'instance', name: 'Audit Table', primeComponent: 'Table', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-datatable-*', '--tp-space-*']};

CREATE (n352:SystemShellGraphNode:ComponentInstance:Instance);
SET n352 += {assetName: 'Table', assetType: 'table', code: 'SHL02.SCN03.SEC10.SEC02.ELT01.CP01.TABLE', configurationJson: '{}', definitionCode: 'CD.TABLE', description: 'Editable instance configuration for the Table component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '546ee155-a608-329d-84da-95e7dcd2068b', hierarchyCode: 'SHL02.SCN03.SEC10.SEC02.ELT01.CP01.TABLE', id: 'd43399de-a92b-359e-82de-86937f937e9b', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-table.mjs', layer: 'instance', name: 'Audit Table Table Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n353:SystemShellGraphNode:Element:Instance);
SET n353 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC10.SEC02.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Paginator for audit results.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'b14ed96f-69d6-3d8d-8d71-07f5e7db890c', hierarchyCode: 'SHL02.SCN03.SEC10.SEC02.ELT02', id: 'b23905f5-04b4-3a5c-a859-d5fed2e1d648', layer: 'instance', name: 'Audit Paginator', primeComponent: 'Paginator', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-paginator-*', '--tp-space-*']};

CREATE (n354:SystemShellGraphNode:ComponentInstance:Instance);
SET n354 += {assetName: 'Paginator', assetType: 'paginator', code: 'SHL02.SCN03.SEC10.SEC02.ELT02.CP01.PAGINATOR', configurationJson: '{}', definitionCode: 'CD.PAGINATOR', description: 'Editable instance configuration for the Paginator component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'a8bfc74f-18be-3c89-8dca-e512f274b851', hierarchyCode: 'SHL02.SCN03.SEC10.SEC02.ELT02.CP01.PAGINATOR', id: 'a14eab48-9c0f-36fe-afa1-e318eebd6ca3', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-paginator.mjs', layer: 'instance', name: 'Audit Paginator Paginator Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n355:SystemShellGraphNode:Instance:Section);
SET n355 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC11', controlSource: 'none', defaultState: 'visible', description: 'Health checks tab workspace.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '303069ae-f2f5-3747-a98f-b7b928488072', hierarchyCode: 'SHL02.SCN03.SEC11', id: 'f1ec7953-2f58-391a-a645-957bf30ba5df', layer: 'instance', name: 'Health Tab Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n356:SystemShellGraphNode:Instance:Section);
SET n356 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC11.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Health tab toolbar.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '432e2a1b-2f70-3a3e-a3e3-6423fab9cc5d', hierarchyCode: 'SHL02.SCN03.SEC11.SEC01', id: '5a59465f-032f-3436-bee4-c6a8f10fc384', layer: 'instance', name: 'Health Toolbar Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n357:SystemShellGraphNode:Element:Instance);
SET n357 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC11.SEC01.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Search field for health checks.', domain: 'frontend', elementType: 'input', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '797e330a-e784-3a36-be7b-2099f7948aae', hierarchyCode: 'SHL02.SCN03.SEC11.SEC01.ELT01', id: '165214b8-ccc4-3869-abf3-90ac729c7f21', layer: 'instance', name: 'Health Search Input', primeComponent: 'InputText', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-inputtext-*', '--tp-space-*']};

CREATE (n358:SystemShellGraphNode:ComponentInstance:Instance);
SET n358 += {assetName: 'InputText', assetType: 'inputtext', code: 'SHL02.SCN03.SEC11.SEC01.ELT01.CP01.INPUTTEXT', configurationJson: '{}', definitionCode: 'CD.INPUTTEXT', description: 'Editable instance configuration for the InputText component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '47dc9140-e2d3-3f66-b3d1-a35eec009a1d', hierarchyCode: 'SHL02.SCN03.SEC11.SEC01.ELT01.CP01.INPUTTEXT', id: '5136552e-c2d0-3336-a178-9166b104fad6', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-inputtext.mjs', layer: 'instance', name: 'Health Search Input InputText Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n359:SystemShellGraphNode:Element:Instance);
SET n359 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC11.SEC01.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Filter toggle for health results.', domain: 'frontend', elementType: 'toggle', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '1cf16cce-bf70-3fd0-9c87-f803424fa146', hierarchyCode: 'SHL02.SCN03.SEC11.SEC01.ELT02', id: 'e6683d66-b9b2-33da-a726-1b84b1bea8de', layer: 'instance', name: 'Health Filter Toggle', primeComponent: 'Button', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-button-*', '--tp-space-*']};

CREATE (n360:SystemShellGraphNode:ComponentInstance:Instance);
SET n360 += {assetName: 'Button', assetType: 'button', code: 'SHL02.SCN03.SEC11.SEC01.ELT02.CP01.BUTTON', configurationJson: '{}', definitionCode: 'CD.BUTTON', description: 'Editable instance configuration for the Button component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '7e63258e-b1d3-3950-90fb-26146f304b8d', hierarchyCode: 'SHL02.SCN03.SEC11.SEC01.ELT02.CP01.BUTTON', id: '0ec56258-cf4c-38e0-b5d5-ae812bbdd38c', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-button.mjs', layer: 'instance', name: 'Health Filter Toggle Button Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n361:SystemShellGraphNode:Element:Instance);
SET n361 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC11.SEC01.ELT03', controlSource: 'none', defaultState: 'visible', description: 'Grid/table toggle for health results.', domain: 'frontend', elementType: 'toggle', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '23496afe-805c-381d-9722-af44f00cec13', hierarchyCode: 'SHL02.SCN03.SEC11.SEC01.ELT03', id: '519d1d89-f1bf-3937-ac7b-a2778b47104f', layer: 'instance', name: 'Health View Toggle', primeComponent: 'ToggleButton', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-togglebutton-*', '--tp-space-*']};

CREATE (n362:SystemShellGraphNode:ComponentInstance:Instance);
SET n362 += {assetName: 'ToggleButton', assetType: 'togglebutton', code: 'SHL02.SCN03.SEC11.SEC01.ELT03.CP01.TOGGLEBUTTON', configurationJson: '{}', definitionCode: 'CD.TOGGLEBUTTON', description: 'Editable instance configuration for the ToggleButton component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '905f323f-99d1-332d-85d4-79d4cb33b477', hierarchyCode: 'SHL02.SCN03.SEC11.SEC01.ELT03.CP01.TOGGLEBUTTON', id: '99756c2e-569a-3b2a-b407-8ce282791825', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-togglebutton.mjs', layer: 'instance', name: 'Health View Toggle ToggleButton Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n363:SystemShellGraphNode:Instance:Section);
SET n363 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC11.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Health cards and paging.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '3221c90b-3960-3c2c-856a-6b6be84a04b7', hierarchyCode: 'SHL02.SCN03.SEC11.SEC02', id: 'd714a59a-3812-325d-a89a-1ac391a1187a', layer: 'instance', name: 'Health Results Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n364:SystemShellGraphNode:Element:Instance);
SET n364 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC11.SEC02.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Representative health status badge in the health grid.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '03cb9f14-3968-300c-a36f-3b81ed9e4520', hierarchyCode: 'SHL02.SCN03.SEC11.SEC02.ELT01', id: '3e80874e-eb66-3663-8e5e-22353c075133', layer: 'instance', name: 'Health Status Badge', primeComponent: 'Tag', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-tag-*', '--tp-space-*']};

CREATE (n365:SystemShellGraphNode:ComponentInstance:Instance);
SET n365 += {assetName: 'Tag', assetType: 'tag', code: 'SHL02.SCN03.SEC11.SEC02.ELT01.CP01.TAG', configurationJson: '{}', definitionCode: 'CD.TAG', description: 'Editable instance configuration for the Tag component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'e48cfe20-a3b3-3d3b-9182-33d4f326af32', hierarchyCode: 'SHL02.SCN03.SEC11.SEC02.ELT01.CP01.TAG', id: '40a78da3-3071-3f71-ad32-5c98c1664039', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-tag.mjs', layer: 'instance', name: 'Health Status Badge Tag Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n366:SystemShellGraphNode:Element:Instance);
SET n366 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC11.SEC02.ELT02', controlSource: 'none', defaultState: 'visible', description: 'Paginator for health results.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'f505cd28-3ef9-3ea1-9ecb-2e270bf8b603', hierarchyCode: 'SHL02.SCN03.SEC11.SEC02.ELT02', id: '3393e3ce-c286-32ef-a1a6-d39130d28082', layer: 'instance', name: 'Health Paginator', primeComponent: 'Paginator', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-paginator-*', '--tp-space-*']};

CREATE (n367:SystemShellGraphNode:ComponentInstance:Instance);
SET n367 += {assetName: 'Paginator', assetType: 'paginator', code: 'SHL02.SCN03.SEC11.SEC02.ELT02.CP01.PAGINATOR', configurationJson: '{}', definitionCode: 'CD.PAGINATOR', description: 'Editable instance configuration for the Paginator component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '44fba8c2-8968-39fa-90f7-c04574c02c45', hierarchyCode: 'SHL02.SCN03.SEC11.SEC02.ELT02.CP01.PAGINATOR', id: '4e0709d2-7b54-36c3-a1ae-d7e1dedf35b8', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-paginator.mjs', layer: 'instance', name: 'Health Paginator Paginator Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n368:SystemShellGraphNode:Instance:Section);
SET n368 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC12', controlSource: 'none', defaultState: 'visible', description: 'License tab workspace.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '92078f26-fb29-334e-b200-ddd2fb0e4889', hierarchyCode: 'SHL02.SCN03.SEC12', id: 'f6006c69-9fd5-3870-a659-7cca08fb0580', layer: 'instance', name: 'License Tab Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n369:SystemShellGraphNode:Instance:Section);
SET n369 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC12.SEC01', controlSource: 'none', defaultState: 'visible', description: 'License summary region.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '00013146-f506-31f4-8247-fcfc652a09a9', hierarchyCode: 'SHL02.SCN03.SEC12.SEC01', id: '277d3457-591c-3d8d-901c-dd0efa0640ba', layer: 'instance', name: 'License Summary Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n370:SystemShellGraphNode:Element:Instance);
SET n370 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC12.SEC01.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Status badge for the tenant license.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'd02a4473-37cf-3340-b392-cc5eeb6ec993', hierarchyCode: 'SHL02.SCN03.SEC12.SEC01.ELT01', id: '0ce95799-ae7d-3602-aa58-e30d5f3a2792', layer: 'instance', name: 'License Status Badge', primeComponent: 'Tag', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-tag-*', '--tp-space-*']};

CREATE (n371:SystemShellGraphNode:ComponentInstance:Instance);
SET n371 += {assetName: 'Tag', assetType: 'tag', code: 'SHL02.SCN03.SEC12.SEC01.ELT01.CP01.TAG', configurationJson: '{}', definitionCode: 'CD.TAG', description: 'Editable instance configuration for the Tag component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '25347337-2d87-3094-a2e9-c61d98743176', hierarchyCode: 'SHL02.SCN03.SEC12.SEC01.ELT01.CP01.TAG', id: 'c8d3d147-11dc-3a85-a8a8-5564d11932db', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-tag.mjs', layer: 'instance', name: 'License Status Badge Tag Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n372:SystemShellGraphNode:Instance:Section);
SET n372 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC12.SEC02', controlSource: 'none', defaultState: 'visible', description: 'License allocations table region.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'b4ef2238-66e1-3219-a5f3-6d3a495b5b0f', hierarchyCode: 'SHL02.SCN03.SEC12.SEC02', id: '43078be8-6b0c-38ed-a11f-eeb60d2fa5c7', layer: 'instance', name: 'License Results Section', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active'};

CREATE (n373:SystemShellGraphNode:Element:Instance);
SET n373 += {backgroundImagePath: '', code: 'SHL02.SCN03.SEC12.SEC02.ELT01', controlSource: 'none', defaultState: 'visible', description: 'Tabular license allocation view.', domain: 'frontend', elementType: 'display', family: 'Element', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'c05fe644-0b62-3348-b207-33b450060eb6', hierarchyCode: 'SHL02.SCN03.SEC12.SEC02.ELT01', id: '2c4197f8-6b93-3df7-b998-cac6d9d7291d', layer: 'instance', name: 'License Table', primeComponent: 'Table', renderMode: 'static', semanticLevel: 'none', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html', status: 'active', tokenFamilies: ['--p-datatable-*', '--tp-space-*']};

CREATE (n374:SystemShellGraphNode:ComponentInstance:Instance);
SET n374 += {assetName: 'Table', assetType: 'table', code: 'SHL02.SCN03.SEC12.SEC02.ELT01.CP01.TABLE', configurationJson: '{}', definitionCode: 'CD.TABLE', description: 'Editable instance configuration for the Table component asset.', domain: 'frontend', family: 'Component', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: 'a12ed0fb-fcb7-3294-9a84-f8d5dfc10c95', hierarchyCode: 'SHL02.SCN03.SEC12.SEC02.ELT01.CP01.TABLE', id: '77b2b8ab-7ec7-357e-9963-506203694b90', implementationSourcePath: '/frontend/node_modules/primeng/fesm2022/primeng-table.mjs', layer: 'instance', name: 'License Table Table Component', objectType: 'Component', sourceArtifactPath: 'system-shell-graph-component-registry.json', status: 'active'};

CREATE (n375:SystemShellGraphNode:Instance:Section);
SET n375 += {backgroundImagePath: '', code: 'SHL02.SEC01', controlSource: 'none', defaultState: 'visible', description: 'Application-shell header container.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '8a074203-6928-35a7-8425-7152d30242b9', hierarchyCode: 'SHL02.SEC01', id: 'e362c30b-65ad-36f8-b2a5-4e9f8983ffa0', layer: 'instance', name: 'Header Container', renderMode: 'static', repeatable: false, sectionType: 'header', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n376:SystemShellGraphNode:Instance:Section);
SET n376 += {backgroundImagePath: '', code: 'SHL02.SEC02', controlSource: 'none', defaultState: 'visible', description: 'Application-shell main content container.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '65132572-e663-38d1-a6b4-4fb6ba8f5eaa', hierarchyCode: 'SHL02.SEC02', id: 'da55c2d3-725d-3a1b-aac1-cdf4eff1b5d5', layer: 'instance', name: 'Main Container', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n377:SystemShellGraphNode:Instance:Section);
SET n377 += {backgroundImagePath: '', code: 'SHL02.SEC03', controlSource: 'none', defaultState: 'visible', description: 'Application-shell footer container.', domain: 'frontend', family: 'Section', graphScope: 'SYSTEM_FRONTEND_GRAPH', guid: '11def1ec-2a81-3e86-be7d-ece193c60bb1', hierarchyCode: 'SHL02.SEC03', id: '5a653f87-d514-3066-9167-d590fa65030c', layer: 'instance', name: 'Footer Container', renderMode: 'static', repeatable: false, sectionType: 'section', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active'};

CREATE (n378:SystemShellGraphNode:Instance:ViewportProfile);
SET n378 += {backgroundImagePath: '', code: 'VPR90', description: 'Generic web viewport used by the preview runtime for every selected screen.', domain: 'frontend', family: 'ViewportProfile', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'VPR90', id: '699277ac-1ecb-3812-8176-720ecc5e1f0e', layer: 'instance', name: 'Web Viewport', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', viewportCategory: 'desktop', viewportHeight: 1024, viewportWidth: 1440};

CREATE (n379:SystemShellGraphNode:Instance:ViewportProfile);
SET n379 += {backgroundImagePath: '', code: 'VPR91', description: 'Generic tablet device viewport used by the preview runtime.', domain: 'frontend', family: 'ViewportProfile', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'VPR91', id: '31e28bb9-4065-381d-9c2f-d4402e39da5d', layer: 'instance', name: 'Tablet Device Viewport', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', viewportCategory: 'tablet', viewportHeight: 1366, viewportWidth: 1024};

CREATE (n380:SystemShellGraphNode:Instance:ViewportProfile);
SET n380 += {backgroundImagePath: '', code: 'VPR92', description: 'Generic mobile device viewport used by the preview runtime.', domain: 'frontend', family: 'ViewportProfile', graphScope: 'SYSTEM_FRONTEND_GRAPH', hierarchyCode: 'VPR92', id: '9958fb3d-8571-3cad-9e38-a945483f29f8', layer: 'instance', name: 'Mobile Device Viewport', sourceArtifactPath: '/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html', status: 'active', viewportCategory: 'mobile', viewportHeight: 844, viewportWidth: 390};

MATCH (from:SystemShellGraphNode {code: 'APP01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SHELL {activeName: 'has-child-shell', passiveName: 'has-parent-application'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'APP01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SHELL {activeName: 'has-child-shell', passiveName: 'has-parent-application'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'BR02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BL01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:RAISES {activeName: 'raises-blocker', passiveName: 'raised-by-business-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'BR02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BL02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:RAISES {activeName: 'raises-blocker', passiveName: 'raised-by-business-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'BR02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BL03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:RAISES {activeName: 'raises-blocker', passiveName: 'raised-by-business-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'BR02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BL04', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:RAISES {activeName: 'raises-blocker', passiveName: 'raised-by-business-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'BR03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BL05', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:RAISES {activeName: 'raises-blocker', passiveName: 'raised-by-business-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'BR04', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BL06', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:RAISES {activeName: 'raises-blocker', passiveName: 'raised-by-business-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'BR04', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BL07', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:RAISES {activeName: 'raises-blocker', passiveName: 'raised-by-business-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'BR05', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BL08', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:RAISES {activeName: 'raises-blocker', passiveName: 'raised-by-business-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'J01.JS01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_STEP {activeName: 'has-child-journey-step', passiveName: 'has-parent-journey'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'J01.JS02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_STEP {activeName: 'has-child-journey-step', passiveName: 'has-parent-journey'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'J01.JS03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_STEP {activeName: 'has-child-journey-step', passiveName: 'has-parent-journey'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'J01.JS04', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_STEP {activeName: 'has-child-journey-step', passiveName: 'has-parent-journey'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'J01.JS05', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_STEP {activeName: 'has-child-journey-step', passiveName: 'has-parent-journey'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'J01.JS06', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_STEP {activeName: 'has-child-journey-step', passiveName: 'has-parent-journey'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'J01.JS07', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_STEP {activeName: 'has-child-journey-step', passiveName: 'has-parent-journey'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'J01.JS08', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_STEP {activeName: 'has-child-journey-step', passiveName: 'has-parent-journey'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:ACTIVATES_SCREEN {activeName: 'activates-screen', passiveName: 'activated-by-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BR01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:GOVERNED_BY {activeName: 'governed-by-business-rule', passiveName: 'governs-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:ACTIVATES_SCREEN {activeName: 'activates-screen', passiveName: 'activated-by-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BR01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:GOVERNED_BY {activeName: 'governed-by-business-rule', passiveName: 'governs-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BR02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:GOVERNED_BY {activeName: 'governed-by-business-rule', passiveName: 'governs-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BL01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_BLOCKER {activeName: 'has-child-blocker', passiveName: 'has-parent-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BL02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_BLOCKER {activeName: 'has-child-blocker', passiveName: 'has-parent-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BL03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_BLOCKER {activeName: 'has-child-blocker', passiveName: 'has-parent-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BL04', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_BLOCKER {activeName: 'has-child-blocker', passiveName: 'has-parent-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:ACTIVATES_SCREEN {activeName: 'activates-screen', passiveName: 'activated-by-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BR03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:GOVERNED_BY {activeName: 'governed-by-business-rule', passiveName: 'governs-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BL05', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_BLOCKER {activeName: 'has-child-blocker', passiveName: 'has-parent-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS04', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:ACTIVATES_SCREEN {activeName: 'activates-screen', passiveName: 'activated-by-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS04', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BR04', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:GOVERNED_BY {activeName: 'governed-by-business-rule', passiveName: 'governs-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS05', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:ACTIVATES_SCREEN {activeName: 'activates-screen', passiveName: 'activated-by-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS05', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BR04', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:GOVERNED_BY {activeName: 'governed-by-business-rule', passiveName: 'governs-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS05', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BL06', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_BLOCKER {activeName: 'has-child-blocker', passiveName: 'has-parent-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS05', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BL07', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_BLOCKER {activeName: 'has-child-blocker', passiveName: 'has-parent-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS06', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:ACTIVATES_SCREEN {activeName: 'activates-screen', passiveName: 'activated-by-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS06', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BR05', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:GOVERNED_BY {activeName: 'governed-by-business-rule', passiveName: 'governs-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS07', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:ACTIVATES_SCREEN {activeName: 'activates-screen', passiveName: 'activated-by-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS07', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BR05', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:GOVERNED_BY {activeName: 'governed-by-business-rule', passiveName: 'governs-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS07', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BL08', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_BLOCKER {activeName: 'has-child-blocker', passiveName: 'has-parent-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS08', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:ACTIVATES_SCREEN {activeName: 'activates-screen', passiveName: 'activated-by-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'J01.JS08', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'BR06', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:GOVERNED_BY {activeName: 'governed-by-business-rule', passiveName: 'governs-journey-step'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'PER.USER', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'J01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:CAN_EXECUTE {activeName: 'can-execute-journey', passiveName: 'executable-by-persona'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-shell'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-shell'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-shell'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.VRS01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:USES_RULE_SET {activeName: 'uses-rule-set', passiveName: 'used-by-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC01.ELT01.CP01.MESSAGE', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC01.ELT01.CP01.MESSAGE', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.MESSAGE', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC01.ELT01.CP01.MESSAGE', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC01.ELT02.CP01.MESSAGE', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC01.ELT02.CP01.MESSAGE', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.MESSAGE', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC01.ELT02.CP01.MESSAGE', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC01.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC01.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC01.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC01.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC01.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC01.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC01.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC01.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC01.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC01.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT04', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC03.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.VRS01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.VRS01.R01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_RULE {activeName: 'has-child-validation-rule', passiveName: 'has-parent-validation-rule-set'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.VRS01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.VRS01.R02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_RULE {activeName: 'has-child-validation-rule', passiveName: 'has-parent-validation-rule-set'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.VRS01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.VRS01.R03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_RULE {activeName: 'has-child-validation-rule', passiveName: 'has-parent-validation-rule-set'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.VRS01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.VRS01.R04', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_RULE {activeName: 'has-child-validation-rule', passiveName: 'has-parent-validation-rule-set'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.VRS01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.VRS01.R05', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_RULE {activeName: 'has-child-validation-rule', passiveName: 'has-parent-validation-rule-set'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.VRS01.R01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:TARGETS {activeName: 'targets-section', passiveName: 'targeted-by-validation-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.VRS01.R02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:TARGETS {activeName: 'targets-element', passiveName: 'targeted-by-validation-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.VRS01.R03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:TARGETS {activeName: 'targets-element', passiveName: 'targeted-by-validation-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.VRS01.R04', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:TARGETS {activeName: 'targets-section', passiveName: 'targeted-by-validation-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.VRS01.R04', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:TARGETS {activeName: 'targets-section', passiveName: 'targeted-by-validation-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN01.VRS01.R05', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:TARGETS {activeName: 'targets-section', passiveName: 'targeted-by-validation-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02.VRS01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:USES_RULE_SET {activeName: 'uses-rule-set', passiveName: 'used-by-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC04', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC03.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC03.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC03.ELT01.CP01.INPUTOTP', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC03.ELT01.CP01.INPUTOTP', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.INPUTOTP', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC03.ELT01.CP01.INPUTOTP', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC03.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC04', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC04.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC04', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC04.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN02.VRS01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02.VRS01.R01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_RULE {activeName: 'has-child-validation-rule', passiveName: 'has-parent-validation-rule-set'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN02.VRS01.R01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02.SEC02.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:TARGETS {activeName: 'targets-element', passiveName: 'targeted-by-validation-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN03.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN03.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN03.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN03.VRS01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:USES_RULE_SET {activeName: 'uses-rule-set', passiveName: 'used-by-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN03.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN03.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN03.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN03.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN03.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN03.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN03.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN03.SEC03.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN03.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN03.SEC03.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN03.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN03.SEC03.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN03.VRS01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN03.VRS01.R01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_RULE {activeName: 'has-child-validation-rule', passiveName: 'has-parent-validation-rule-set'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN03.VRS01.R01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:TARGETS {activeName: 'targets-screen', passiveName: 'targeted-by-validation-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SCN03.VRS01.R01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN03.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:TARGETS {activeName: 'targets-section', passiveName: 'targeted-by-validation-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SCREEN {activeName: 'has-child-screen', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SCREEN {activeName: 'has-child-screen', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL01.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL01.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SCREEN {activeName: 'has-child-screen', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-shell'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-shell'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-shell'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN01.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN01.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN01.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN01.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN01.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN01.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN01.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN01.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN01.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN01.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN01.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN01.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN01.SEC03.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN01.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN01.SEC03.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC04', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.VRS01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:USES_RULE_SET {activeName: 'uses-rule-set', passiveName: 'used-by-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC04', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC01.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC01.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.INPUTTEXT', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC01.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC02.ELT01.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC02.ELT01.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.BUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC02.ELT01.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC03.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC03.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC03.ELT01.CP01.TOGGLEBUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC03.ELT01.CP01.TOGGLEBUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TOGGLEBUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC03.ELT01.CP01.TOGGLEBUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC03.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC04', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC04.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC04.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC04.ELT01.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC04.ELT01.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.BUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC04.ELT01.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC02.SEC04.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03.ELT01.CP01.SELECT', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03.ELT01.CP01.SELECT', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.SELECT', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03.ELT01.CP01.SELECT', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03.ELT02.CP01.SELECT', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03.ELT02.CP01.SELECT', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.SELECT', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03.ELT02.CP01.SELECT', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03.ELT03.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03.ELT03.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.BUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03.ELT03.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC04', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC04.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC04', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC04.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC04', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC04.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC04.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC04.ELT03.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC04.ELT03.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.BUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC04.ELT03.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC04.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC01.ELT02.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC01.ELT02.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TAG', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC01.ELT02.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC02.ELT01.CP01.TABLE', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC02.ELT01.CP01.TABLE', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TABLE', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC02.ELT01.CP01.TABLE', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC02.ELT02.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC02.ELT02.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TAG', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC02.ELT02.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC03.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC03.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC03.ELT01.CP01.PAGINATOR', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC03.ELT01.CP01.PAGINATOR', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.PAGINATOR', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC03.ELT01.CP01.PAGINATOR', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC03.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.VRS01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.VRS01.R01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_RULE {activeName: 'has-child-validation-rule', passiveName: 'has-parent-validation-rule-set'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.VRS01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.VRS01.R02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_RULE {activeName: 'has-child-validation-rule', passiveName: 'has-parent-validation-rule-set'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.VRS01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.VRS01.R03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_RULE {activeName: 'has-child-validation-rule', passiveName: 'has-parent-validation-rule-set'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.VRS01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.VRS01.R04', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_RULE {activeName: 'has-child-validation-rule', passiveName: 'has-parent-validation-rule-set'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.VRS01.R01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:TARGETS {activeName: 'targets-section', passiveName: 'targeted-by-validation-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.VRS01.R02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC04', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:TARGETS {activeName: 'targets-section', passiveName: 'targeted-by-validation-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.VRS01.R03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:TARGETS {activeName: 'targets-section', passiveName: 'targeted-by-validation-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN02.VRS01.R04', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02.SEC05.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:TARGETS {activeName: 'targets-section', passiveName: 'targeted-by-validation-rule'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC05', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC08', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC09', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-screen'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC01.ELT01.CP01.BREADCRUMB', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC01.ELT01.CP01.BREADCRUMB', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.BREADCRUMB', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC01.ELT01.CP01.BREADCRUMB', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT04', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT05', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT06', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT07', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT08', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT09', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT10', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT03.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT03.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TAG', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT03.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT05', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT05.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT05.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TAG', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT05.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT05', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT07', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT07.CP01.CHIP', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT07.CP01.CHIP', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.CHIP', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT07.CP01.CHIP', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT07', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT08', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT08.CP01.CHIP', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT08.CP01.CHIP', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.CHIP', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT08.CP01.CHIP', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT08', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT09', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT09.CP01.CHIP', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT09.CP01.CHIP', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.CHIP', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT09.CP01.CHIP', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT09', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT10', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT10.CP01.CHIP', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT10.CP01.CHIP', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.CHIP', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT10.CP01.CHIP', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC01.ELT10', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02.ELT01.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02.ELT01.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.BUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02.ELT01.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.BUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02.ELT03.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02.ELT03.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.BUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02.ELT03.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC02.SEC02.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC03.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC03.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC03.ELT01.CP01.TABS', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC03.ELT01.CP01.TABS', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TABS', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC03.ELT01.CP01.TABS', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC03.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.INPUTTEXT', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.BUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01.ELT03.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01.ELT03.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.BUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01.ELT03.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02.ELT01.CP01.TABLE', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02.ELT01.CP01.TABLE', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TABLE', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02.ELT01.CP01.TABLE', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02.ELT02.CP01.PAGINATOR', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02.ELT02.CP01.PAGINATOR', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.PAGINATOR', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02.ELT02.CP01.PAGINATOR', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02.ELT03.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02.ELT03.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TAG', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02.ELT03.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC04.SEC02.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC05', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC05.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC05', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC05.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC05.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC05.ELT01.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC05.ELT01.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.BUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC05.ELT01.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC05.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC05.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC05.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC05.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.BUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC05.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC05.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.INPUTTEXT', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.BUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01.ELT03.CP01.TOGGLEBUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01.ELT03.CP01.TOGGLEBUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TOGGLEBUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01.ELT03.CP01.TOGGLEBUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02.ELT01.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02.ELT01.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TAG', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02.ELT01.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02.ELT02.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02.ELT02.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TAG', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02.ELT02.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02.ELT03.CP01.PAGINATOR', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02.ELT03.CP01.PAGINATOR', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.PAGINATOR', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02.ELT03.CP01.PAGINATOR', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC06.SEC02.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.INPUTTEXT', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.BUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01.ELT03.CP01.TOGGLEBUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01.ELT03.CP01.TOGGLEBUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TOGGLEBUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01.ELT03.CP01.TOGGLEBUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02.ELT01.CP01.TABLE', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02.ELT01.CP01.TABLE', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TABLE', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02.ELT01.CP01.TABLE', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02.ELT02.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02.ELT02.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TAG', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02.ELT02.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02.ELT03.CP01.PAGINATOR', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02.ELT03.CP01.PAGINATOR', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.PAGINATOR', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02.ELT03.CP01.PAGINATOR', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC07.SEC02.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC08', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC08.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC08.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC08.ELT01.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC08.ELT01.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TAG', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC08.ELT01.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC08.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC09', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC09.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC09', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC09.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC09.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC09.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC09.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.INPUTTEXT', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC09.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC09.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC09.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC09.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC09.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.BUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC09.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC09.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.INPUTTEXT', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.BUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01.ELT03.CP01.TOGGLEBUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01.ELT03.CP01.TOGGLEBUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TOGGLEBUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01.ELT03.CP01.TOGGLEBUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC02.ELT01.CP01.TABLE', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC02.ELT01.CP01.TABLE', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TABLE', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC02.ELT01.CP01.TABLE', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC02.ELT02.CP01.PAGINATOR', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC02.ELT02.CP01.PAGINATOR', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.PAGINATOR', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC02.ELT02.CP01.PAGINATOR', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC10.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.INPUTTEXT', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01.ELT01.CP01.INPUTTEXT', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.BUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01.ELT02.CP01.BUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01.ELT03.CP01.TOGGLEBUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01.ELT03.CP01.TOGGLEBUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TOGGLEBUTTON', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01.ELT03.CP01.TOGGLEBUTTON', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC01.ELT03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC02.ELT01.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC02.ELT01.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TAG', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC02.ELT01.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC02.ELT02.CP01.PAGINATOR', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC02.ELT02.CP01.PAGINATOR', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.PAGINATOR', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC02.ELT02.CP01.PAGINATOR', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC11.SEC02.ELT02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SECTION {activeName: 'has-child-section', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12.SEC01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12.SEC01.ELT01.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12.SEC01.ELT01.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TAG', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12.SEC01.ELT01.CP01.TAG', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12.SEC01.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_ELEMENT {activeName: 'has-child-element', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12.SEC02.ELT01.CP01.TABLE', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_COMPONENT {activeName: 'has-child-component', passiveName: 'has-parent-element'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12.SEC02.ELT01.CP01.TABLE', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'CD.TABLE', graphScope: 'SYSTEM_COMPONENT_REGISTRY'})
CREATE (from)-[:INSTANCE_OF {activeName: 'instance-of', passiveName: 'instance-of'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12.SEC02.ELT01.CP01.TABLE', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03.SEC12.SEC02.ELT01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:PLACED_WITHIN {activeName: 'placed-within', passiveName: 'placed-within'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN01', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SCREEN {activeName: 'has-child-screen', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN02', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SCREEN {activeName: 'has-child-screen', passiveName: 'has-parent-section'}]->(to);

MATCH (from:SystemShellGraphNode {code: 'SHL02.SEC02', graphScope: 'SYSTEM_FRONTEND_GRAPH'}), (to:SystemShellGraphNode {code: 'SHL02.SCN03', graphScope: 'SYSTEM_FRONTEND_GRAPH'})
CREATE (from)-[:HAS_SCREEN {activeName: 'has-child-screen', passiveName: 'has-parent-section'}]->(to);
