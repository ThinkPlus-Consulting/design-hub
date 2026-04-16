import { MenuItem } from 'primeng/api';

export interface ApplicationShellHeaderSlotData {
  hasContent: boolean;
  headerLeftSourceObjectId: string | null;
  headerLeftGuid: string | null;
  headerLeftFocused: boolean;
  headerMenuToggleSourceObjectId: string | null;
  headerMenuToggleGuid: string | null;
  headerMenuToggleFocused: boolean;
  headerLogoLinkSourceObjectId: string | null;
  headerLogoLinkGuid: string | null;
  headerLogoLinkFocused: boolean;
  headerLeftDividerSourceObjectId: string | null;
  headerLeftDividerGuid: string | null;
  headerLeftDividerFocused: boolean;
  headerPageIndicatorSourceObjectId: string | null;
  headerPageIndicatorGuid: string | null;
  headerPageIndicatorFocused: boolean;
  headerRightSourceObjectId: string | null;
  headerRightGuid: string | null;
  headerRightFocused: boolean;
  headerNotificationTriggerSourceObjectId: string | null;
  headerNotificationTriggerGuid: string | null;
  headerNotificationTriggerFocused: boolean;
  headerNotificationBadgeSourceObjectId: string | null;
  headerNotificationBadgeGuid: string | null;
  headerNotificationBadgeFocused: boolean;
  headerHelpActionSourceObjectId: string | null;
  headerHelpActionGuid: string | null;
  headerHelpActionFocused: boolean;
  headerLanguageSwitcherSourceObjectId: string | null;
  headerLanguageSwitcherGuid: string | null;
  headerLanguageSwitcherFocused: boolean;
  headerRightDividerSourceObjectId: string | null;
  headerRightDividerGuid: string | null;
  headerRightDividerFocused: boolean;
  headerUserAvatarSourceObjectId: string | null;
  headerUserAvatarGuid: string | null;
  headerUserAvatarFocused: boolean;
  menuToggleAriaLabel: string;
  menuIconSrc: string;
  logoAriaLabel: string;
  logoSrc: string;
  logoAlt: string;
  pageTitle: string;
  pageIndicatorIconSrc: string;
  notificationAriaLabel: string;
  notificationIconSrc: string;
  notificationCount: string;
  helpAriaLabel: string;
  helpIconSrc: string;
  languageLabel: string;
  languageAriaLabel: string;
  languageFlagSrc: string;
  userMenuAriaLabel: string;
  userAvatarLabel: string;
}

export interface ApplicationShellBreadcrumbSlotData {
  trailSourceObjectId: string | null;
  trailGuid: string | null;
  trailFocused: boolean;
  showContent: boolean;
  items: MenuItem[];
  home?: MenuItem;
}
