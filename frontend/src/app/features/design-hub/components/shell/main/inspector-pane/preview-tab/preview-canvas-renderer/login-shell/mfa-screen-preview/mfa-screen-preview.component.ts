import { ChangeDetectionStrategy, Component, computed, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputOtpModule } from 'primeng/inputotp';
import { ShellBackgroundConfig } from '../../../../../../../../models/graph.types';
import { MfaScreenContentConfig } from '../../../../../../../../models/preview-content.types';
import { parseNodeConfiguration } from '../../../../../../../../utils/node-configuration';
import { PreviewIdentityBound } from '../../../../../../../../utils/preview-identity-bound';
import { childByName, componentByName } from '../../../../../../../../utils/preview-structure';

@Component({
  selector: 'app-mfa-screen-preview',
  standalone: true,
  imports: [FormsModule, InputOtpModule, ButtonModule],
  templateUrl: './mfa-screen-preview.component.html',
  styleUrl: './mfa-screen-preview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MfaScreenPreviewComponent extends PreviewIdentityBound {
  readonly selectedGuid = input<string | null>(null);
  readonly backgroundConfig = input<ShellBackgroundConfig | null>(null);
  readonly screenObjectId = input<string | null>(null);
  private readonly screenNode = computed(() => this.state.nodeById(this.screenObjectId()));
  private readonly content = computed(() =>
    parseNodeConfiguration<MfaScreenContentConfig>(this.screenNode()),
  );
  private readonly otpValueOverride = signal<string | null>(null);
  private readonly slots = computed(() => {
    const screen = this.screenNode();
    const modal = childByName(this.state, screen?.id, 'Verification Modal Section');
    const header = childByName(this.state, modal?.id, 'Verification Header Section');
    const status = childByName(this.state, modal?.id, 'Status Message Section');
    const otp = childByName(this.state, modal?.id, 'Verification Code Section');
    const actions = childByName(this.state, modal?.id, 'Verification Action Bar');

    return {
      screen,
      modal,
      header,
      title: componentByName(this.state, header?.id, 'Verification Title'),
      description: componentByName(this.state, header?.id, 'Verification Description'),
      status,
      banner: componentByName(this.state, status?.id, 'Status Banner'),
      otp,
      otpInput: componentByName(this.state, otp?.id, 'Verification Code Input'),
      actions,
      backAction: componentByName(this.state, actions?.id, 'Back Action'),
      verifyAction: componentByName(this.state, actions?.id, 'Verify Action'),
    };
  });

  backgroundColorStyle(): string {
    return this.backgroundConfig()?.backgroundColorStyle ?? 'color-mix(in srgb, var(--tp-primary-dark) 42%, transparent)';
  }

  backgroundPatternOpacity(): string {
    const opacity = this.backgroundConfig()?.backgroundPatternOpacity;
    return typeof opacity === 'number' ? String(opacity) : '0.13';
  }

  showPattern(): boolean {
    const patternKey = this.backgroundConfig()?.backgroundPatternKey;
    return patternKey ? true : this.backgroundConfig() === null;
  }

  otpValue = computed(() => this.otpValueOverride() ?? this.content()?.otpValue ?? '');

  onOtpValueChange(value: string): void {
    this.otpValueOverride.set(value);
  }

  title(): string {
    return this.content()?.title ?? '';
  }

  description(): string {
    return this.content()?.description ?? '';
  }

  banner(): string {
    return this.content()?.banner ?? '';
  }

  otpAriaLabel(): string {
    return this.content()?.otpAriaLabel ?? '';
  }

  backLabel(): string {
    return this.content()?.backLabel ?? '';
  }

  verifyLabel(): string {
    return this.content()?.verifyLabel ?? '';
  }

  protected slotNode(slot: string | null | undefined) {
    const normalizedSlot = slot?.trim();
    if (!normalizedSlot) {
      return null;
    }

    return this.slots()[normalizedSlot as keyof ReturnType<typeof this.slots>] ?? null;
  }

  protected selectedPreviewGuid(): string | null {
    return this.selectedGuid();
  }
}
