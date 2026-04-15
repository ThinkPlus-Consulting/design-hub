import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-design-hub-footer',
  standalone: true,
  templateUrl: './design-hub-footer.component.html',
  styleUrl: './design-hub-footer.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DesignHubFooterComponent {
  readonly title = input('Design Hub');
  readonly subtitle = input('');
}
