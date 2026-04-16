import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { LicenseSectionContentConfig, TenantLicenseSummary } from '../../../../../../../../../../models/preview-content.types';

@Component({
  selector: 'app-tenant-factsheet-license-tab',
  standalone: true,
  imports: [TableModule, TagModule],
  templateUrl: './tenant-factsheet-license-tab.component.html',
  styleUrl: './tenant-factsheet-license-tab.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TenantFactsheetLicenseTabComponent {
  readonly section = input.required<LicenseSectionContentConfig | null>();
  readonly license = input.required<TenantLicenseSummary>();
  readonly allocatedSeats = input.required<number>();
  readonly assignedSeats = input.required<number>();
  readonly availableSeats = input.required<number>();
}
