import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-login-shell-breadcrumb-slot',
  standalone: true,
  templateUrl: './login-shell-breadcrumb-slot.component.html',
  styleUrl: './login-shell-breadcrumb-slot.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginShellBreadcrumbSlotComponent {}
