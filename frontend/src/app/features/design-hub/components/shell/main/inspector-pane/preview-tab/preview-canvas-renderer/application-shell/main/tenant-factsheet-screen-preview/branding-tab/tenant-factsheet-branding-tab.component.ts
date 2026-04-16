import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { BrandingSectionContentConfig } from '../../../../../../../../../../models/preview-content.types';

@Component({
  selector: 'app-tenant-factsheet-branding-tab',
  standalone: true,
  imports: [ButtonModule],
  templateUrl: './tenant-factsheet-branding-tab.component.html',
  styleUrl: './tenant-factsheet-branding-tab.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TenantFactsheetBrandingTabComponent {
  readonly section = input.required<BrandingSectionContentConfig | null>();
}
