import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-login-shell-header-slot',
  standalone: true,
  templateUrl: './login-shell-header-slot.component.html',
  styleUrl: './login-shell-header-slot.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginShellHeaderSlotComponent {}
