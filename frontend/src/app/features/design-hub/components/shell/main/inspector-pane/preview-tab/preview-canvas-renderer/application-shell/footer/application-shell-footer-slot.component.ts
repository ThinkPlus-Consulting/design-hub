import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-application-shell-footer-slot',
  standalone: true,
  templateUrl: './application-shell-footer-slot.component.html',
  styleUrl: './application-shell-footer-slot.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApplicationShellFooterSlotComponent {}
