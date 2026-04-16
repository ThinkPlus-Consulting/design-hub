import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { BreadcrumbModule } from 'primeng/breadcrumb';
import { ApplicationShellBreadcrumbSlotData } from '../application-shell-slot.types';

@Component({
  selector: 'app-application-shell-breadcrumb-slot',
  standalone: true,
  imports: [BreadcrumbModule],
  templateUrl: './application-shell-breadcrumb-slot.component.html',
  styleUrl: './application-shell-breadcrumb-slot.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApplicationShellBreadcrumbSlotComponent {
  readonly slot = input.required<ApplicationShellBreadcrumbSlotData>();
}
