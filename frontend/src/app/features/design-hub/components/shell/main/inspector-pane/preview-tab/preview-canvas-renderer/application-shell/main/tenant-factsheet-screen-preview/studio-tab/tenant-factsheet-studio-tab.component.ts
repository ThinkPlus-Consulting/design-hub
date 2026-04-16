import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { StudioSectionContentConfig } from '../../../../../../../../../../models/preview-content.types';

@Component({
  selector: 'app-tenant-factsheet-studio-tab',
  standalone: true,
  imports: [ButtonModule, InputTextModule],
  templateUrl: './tenant-factsheet-studio-tab.component.html',
  styleUrl: './tenant-factsheet-studio-tab.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TenantFactsheetStudioTabComponent {
  readonly section = input.required<StudioSectionContentConfig | null>();
}
