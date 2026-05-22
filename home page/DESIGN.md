---
name: Campus SkillHub
colors:
  surface: '#f8f9ff'
  surface-dim: '#cbdbf5'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e5eeff'
  surface-container-high: '#dce9ff'
  surface-container-highest: '#d3e4fe'
  on-surface: '#0b1c30'
  on-surface-variant: '#424754'
  inverse-surface: '#213145'
  inverse-on-surface: '#eaf1ff'
  outline: '#727785'
  outline-variant: '#c2c6d6'
  surface-tint: '#005ac2'
  primary: '#0058be'
  on-primary: '#ffffff'
  primary-container: '#2170e4'
  on-primary-container: '#fefcff'
  inverse-primary: '#adc6ff'
  secondary: '#006c49'
  on-secondary: '#ffffff'
  secondary-container: '#6cf8bb'
  on-secondary-container: '#00714d'
  tertiary: '#825100'
  on-tertiary: '#ffffff'
  tertiary-container: '#a36700'
  on-tertiary-container: '#fffbff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#d8e2ff'
  primary-fixed-dim: '#adc6ff'
  on-primary-fixed: '#001a42'
  on-primary-fixed-variant: '#004395'
  secondary-fixed: '#6ffbbe'
  secondary-fixed-dim: '#4edea3'
  on-secondary-fixed: '#002113'
  on-secondary-fixed-variant: '#005236'
  tertiary-fixed: '#ffddb8'
  tertiary-fixed-dim: '#ffb95f'
  on-tertiary-fixed: '#2a1700'
  on-tertiary-fixed-variant: '#653e00'
  background: '#f8f9ff'
  on-background: '#0b1c30'
  surface-variant: '#d3e4fe'
typography:
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-sm:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-lg:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
  label-sm:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 16px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  margin-mobile: 16px
  margin-tablet: 24px
  gutter: 16px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 24px
---

## Brand & Style
The design system is engineered for a vibrant academic ecosystem where skill exchange and collaborative growth are central. It balances the rigor of an educational platform with the social energy of a campus hub.

The visual direction follows **Corporate / Modern** principles with a student-centric twist:
- **Professionalism:** High-quality typography and a structured grid instill trust for paid or formal skill exchanges.
- **Accessibility:** High contrast ratios and clear affordances ensure usability during a busy commute between classes.
- **Clarity:** A "utility-first" aesthetic that favors functional whitespace over decorative elements, ensuring students find what they need quickly.

## Colors
This design system utilizes a palette that reflects both academic stability and personal development.

- **Campus Blue (#3B82F6):** The primary driver for actions, signifying reliability and the official nature of university life. Use for primary buttons, active states, and branding.
- **Growth Green (#10B981):** Represents the "Skill" aspect of the platform. Used for success states, completed tasks, and secondary highlights related to personal progress.
- **Tertiary Amber (#F59E0B):** Reserved for "Urgent" or "High Demand" skill requests to create visual hierarchy without alarm.
- **Surface & Background:** A light gray background (#F9FAFB) provides a soft canvas for pure white (#FFFFFF) cards to "pop" via elevation, reducing eye strain during long browsing sessions.

## Typography
The system uses **Inter** to achieve a clean, systematic look that scales perfectly across various Android screen densities.

- **Headlines:** Use Bold (700) or SemiBold (600) weights with slight negative letter-spacing for a modern, compact feel.
- **Body Text:** Standardized at 16px for primary readability, dropping to 14px for secondary metadata or descriptions.
- **Labels:** Used for status badges and button text, employing medium weights to ensure legibility even at small sizes.

## Layout & Spacing
The layout follows a **Fluid Grid** model optimized for Android's diverse aspect ratios.

- **Mobile (Default):** 16px side margins with a vertical rhythm based on an 8px base unit.
- **Structure:** Content is organized into cards that span the full width (minus margins) or reside in a 2-column masonry grid for discovery feeds.
- **Touch Targets:** All interactive elements maintain a minimum 48x48dp area to ensure accessibility for students on the move.

## Elevation & Depth
Depth is created through **Tonal Layers** and **Ambient Shadows** to signify interactable surfaces:

- **Level 0 (Background):** #F9FAFB. Used for the app canvas.
- **Level 1 (Cards):** #FFFFFF with a soft, diffused shadow (Y: 4px, Blur: 12px, Color: rgba(0,0,0, 0.05)).
- **Level 2 (Buttons/Active):** Slightly higher elevation (Y: 6px, Blur: 16px) to suggest "press-ability."
- **Overlays:** Modals and bottom sheets use a 20% black scrim to pull focus from the background content.

## Shapes
The shape language is friendly and contemporary, moving away from sharp edges to create a welcoming environment.

- **Primary Radius:** 0.5rem (8px) for standard inputs and small buttons.
- **Feature Cards:** Use a generous 1rem (16px) to 1.5rem (24px) radius to create a soft, modern container.
- **Badges/Chips:** Fully pill-shaped to distinguish them from actionable buttons.

## Components
- **Search Bars:** Pure white background with a subtle 1px border (#E2E8F0) and a soft inner shadow. Hint text should be #94A3B8.
- **Feature Cards:** White surface, 16px corner radius, containing a Skill Icon (top left), Title (Headline-sm), and Provider Info (Label-md).
- **Buttons:** 
  - *Primary:* Filled Campus Blue, white text, 8px radius.
  - *Secondary:* Ghost style with Growth Green border and text.
- **Status Badges:** Subtle tinted backgrounds (e.g., 10% opacity Growth Green) with full-opacity colored text for "Verified," "Completed," or "In Progress" states.
- **Skill Chips:** Pill-shaped, light gray background (#F1F5F9) used for tagging skills like "Python," "Design," or "Mandarin."