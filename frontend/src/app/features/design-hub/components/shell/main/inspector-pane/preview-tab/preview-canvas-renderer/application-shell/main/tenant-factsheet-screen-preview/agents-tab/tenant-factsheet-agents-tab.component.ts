import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { TagModule } from 'primeng/tag';
import { AgentCard, AgentsSectionContentConfig } from '../../../../../../../../../../models/preview-content.types';

@Component({
  selector: 'app-tenant-factsheet-agents-tab',
  standalone: true,
  imports: [TagModule],
  templateUrl: './tenant-factsheet-agents-tab.component.html',
  styleUrl: './tenant-factsheet-agents-tab.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TenantFactsheetAgentsTabComponent {
  readonly section = input.required<AgentsSectionContentConfig | null>();
  readonly items = input.required<readonly AgentCard[]>();
}
