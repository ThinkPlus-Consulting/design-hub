import { AfterViewInit, ChangeDetectionStrategy, Component, ElementRef, OnDestroy, ViewChild, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToggleButtonModule } from 'primeng/togglebutton';
import { PreviewCanvasComponent } from './preview-canvas-renderer/preview-canvas.component';
import { DesignHubWorkspaceService } from '../../../../../services/design-hub-workspace.service';

@Component({
  selector: 'app-design-hub-inspector-preview-tab',
  standalone: true,
  imports: [FormsModule, ToggleButtonModule, PreviewCanvasComponent],
  templateUrl: './design-hub-inspector-preview-tab.component.html',
  styleUrl: './design-hub-inspector-preview-tab.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DesignHubInspectorPreviewTabComponent implements AfterViewInit, OnDestroy {
  private readonly workspace = inject(DesignHubWorkspaceService);

  @ViewChild('previewStage', { read: ElementRef }) private previewStageRef?: ElementRef<HTMLElement>;
  @ViewChild('previewCanvasRoot', { read: ElementRef }) private previewCanvasRootRef?: ElementRef<HTMLElement>;

  readonly previewViewportProfile = this.workspace.previewViewportProfile;
  readonly activePreviewInteractionPalette = this.workspace.activePreviewInteractionPalette;
  readonly displayedPreviewSelectedGuid = this.workspace.displayedPreviewSelectedGuid;
  readonly displayedPreviewShellObjectId = this.workspace.displayedPreviewShellObjectId;
  readonly displayedPreviewBackgroundConfig = this.workspace.displayedPreviewBackgroundConfig;
  readonly displayedPreviewScreenObjectId = this.workspace.displayedPreviewScreenObjectId;
  readonly previewCanvasWidth = this.workspace.previewCanvasWidth;
  readonly previewCanvasHeight = this.workspace.previewCanvasHeight;
  readonly previewScale = this.workspace.previewScale;

  readonly setPreviewViewportProfile = this.workspace.setPreviewViewportProfile.bind(this.workspace);

  readonly isMobileViewportSelected = (): boolean => this.previewViewportProfile() === 'mobile';
  readonly onViewportToggleChange = (mobileEnabled: boolean): void => {
    this.setPreviewViewportProfile(mobileEnabled ? 'mobile' : 'web');
  };

  ngAfterViewInit(): void {
    this.workspace.attachPreviewDom(
      this.previewStageRef?.nativeElement ?? null,
      this.previewCanvasRootRef?.nativeElement ?? null,
    );
  }

  ngOnDestroy(): void {
    this.workspace.detachPreviewDom();
  }
}
