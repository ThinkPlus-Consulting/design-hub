import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-login-shell-footer-slot',
  standalone: true,
  templateUrl: './login-shell-footer-slot.component.html',
  styleUrl: './login-shell-footer-slot.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginShellFooterSlotComponent {}
