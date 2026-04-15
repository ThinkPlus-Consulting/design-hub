import { ChangeDetectionStrategy, Component, computed, input, output } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { BreadcrumbModule } from 'primeng/breadcrumb';

export interface DesignHubBreadcrumbItem {
  objectId: string | null;
  label: string;
}

@Component({
  selector: 'app-design-hub-breadcrumb',
  standalone: true,
  imports: [BreadcrumbModule],
  templateUrl: './design-hub-breadcrumb.component.html',
  styleUrl: './design-hub-breadcrumb.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DesignHubBreadcrumbComponent {
  readonly crumbs = input<readonly DesignHubBreadcrumbItem[]>([]);
  readonly navigate = output<string | null>();
  readonly items = computed<MenuItem[]>(() =>
    this.crumbs().map((crumb, index, crumbs) => {
      const last = index === crumbs.length - 1;
      return {
        label: crumb.label,
        disabled: last,
        command: last ? undefined : () => this.onNavigate(crumb.objectId),
      };
    }),
  );

  onNavigate(objectId: string | null): void {
    this.navigate.emit(objectId);
  }
}
