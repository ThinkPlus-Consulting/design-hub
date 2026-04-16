import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { AvatarModule } from 'primeng/avatar';
import { BadgeModule } from 'primeng/badge';
import { ButtonModule } from 'primeng/button';
import { DividerModule } from 'primeng/divider';
import { ImageModule } from 'primeng/image';
import { ApplicationShellHeaderSlotData } from '../application-shell-slot.types';

@Component({
  selector: 'app-application-shell-header-slot',
  standalone: true,
  imports: [
    AvatarModule,
    BadgeModule,
    ButtonModule,
    DividerModule,
    ImageModule,
  ],
  templateUrl: './application-shell-header-slot.component.html',
  styleUrl: './application-shell-header-slot.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApplicationShellHeaderSlotComponent {
  readonly slot = input.required<ApplicationShellHeaderSlotData>();
}
