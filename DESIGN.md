# RWX Kool UI Design System

This document defines the visual design tokens and constraints for the cross-platform Kool UI2 layer used across desktop
and Android. Legacy RML/RCSS assets are visual references only; runtime UI code must express design through Kotlin
tokens in `core/src/main/kotlin/io/github/rwx/ui`.

## Typography

Kool UI2 uses its default font rendering. These are shared size tokens and do not vary by color scheme.

| Token           | Value | Usage                                      |
|-----------------|-------|--------------------------------------------|
| `headingLarge`  | 44f   | Main menu title, top-level heading         |
| `headingMedium` | 34f   | Screen title, dialog title                 |
| `headingSmall`  | 28f   | Section heading, including settings groups |
| `bodyLarge`     | 26f   | Main menu button text                      |
| `bodyMedium`    | 22f   | Default body text, sub-menu buttons        |
| `bodySmall`     | 18f   | Settings labels, secondary information     |
| `caption`       | 14f   | Version number, update log, chat           |
| `hudText`       | 16f   | HUD overlay text                           |

### Styling

| Token           | Value  | Usage                    |
|-----------------|--------|--------------------------|
| `headingWeight` | Bold   | All headings             |
| `buttonWeight`  | Bold   | Menu buttons             |
| `bodyWeight`    | Normal | Body text, toggle labels |
| `headingAlign`  | Center | Screen titles            |
| `buttonAlign`   | Center | Menu buttons             |

---

## Spacing

Spacing is shared across all color schemes so switching color schemes changes tone, not layout.

### Layout Scale

| Token       | Value | Usage                              |
|-------------|-------|------------------------------------|
| `spaceNone` | 0f    | No spacing                         |
| `spaceXxs`  | 2f    | Minimal gap                        |
| `spaceXs`   | 4f    | Tight gap, button-to-button margin |
| `spaceSm`   | 8f    | Small gap, compact panel padding   |
| `spaceMd`   | 12f   | Medium gap, section separator      |
| `spaceLg`   | 16f   | Large gap, title-to-content        |
| `spaceXl`   | 24f   | Panel internal padding             |
| `space2xl`  | 32f   | Panel outer padding                |

### Component Dimensions

| Token                    | Value | Usage                                                   |
|--------------------------|-------|---------------------------------------------------------|
| `menuButtonWidth`        | 280f  | Standard menu button width                              |
| `menuButtonHeight`       | 44f   | Standard menu button height                             |
| `settingsPanelWidth`     | 520f  | Polished Settings card content width                    |
| `settingsContentWidth`   | 480f  | Settings tabs, rows, and viewport width inside the card |
| `settingsTabButtonWidth` | 112f  | Wider Settings category tab button width                |
| `settingsRowWidth`       | 360f  | Settings toggle and selector row width                  |
| `settingsRowHeight`      | 48f   | Settings row-card height                                |
| `settingsLabelWidth`     | 275f  | Settings label text width                               |
| `settingsFooterGap`      | 16f   | Gap between Settings viewport and Back footer           |
| `levelSelectButtonWidth` | 360f  | Level select button width                               |
| `dialogMessageWidth`     | 400f  | Dialog message text width                               |
| `dialogButtonWidth`      | 160f  | Dialog action button width                              |

---

## Panel Patterns

Panel colors always come from the active color scheme palette.

### Menu Panel

- Background: active color scheme `panelOverlayLight`
- Padding: `spaceXl`
- Alignment: Center/Center
- Width/Height: FitContent
- Title: active color scheme `textPrimary`, `headingLarge`, centered, `spaceLg` bottom margin

### Settings Panel

- Background: active color scheme `panelOverlayLight`
- Padding: `spaceXl`
- Alignment: Center/Center
- Width: `settingsPanelWidth`
- Title and hint: centered, using active color scheme text colors
- Category tabs: horizontal row, selected tab uses `primaryContainer`, inactive tabs use `surfaceSunken`
- Content viewport: `settingsContentWidth` by `settingsViewportHeight`
- Toggle rows: row-card helper with active color scheme `surfaceSunken` background and `textPrimary` label
- Footer: Back button below the viewport, separated by `settingsFooterGap`

### HUD Panel

- Background: active color scheme `panelHud`
- Padding: `spaceSm`
- Margin: `spaceMd`
- Alignment: Start/Top
- Text: active color scheme `textPrimary`, `hudText`

### Dialog Panel

- Background: active color scheme `panelOverlay`
- Padding: `spaceXl`
- Alignment: Center/Center
- Width/Height: FitContent

---

## Button Patterns

### Menu Button

- Width: `menuButtonWidth`
- Height: `menuButtonHeight`
- Margin: `spaceXs`
- Default background: active color scheme `surfaceSunken`
- Hover background: active color scheme `surfaceRaised`
- Text: active color scheme `textPrimary`
- Hover text: active color scheme `primary`
- Font size: `bodyLarge` for main menu, `bodyMedium` for lower-emphasis usages

### Back Button

- Width: matches parent context, usually `menuButtonWidth`, `settingsRowWidth`, or `levelSelectButtonWidth`
- Margin: `spaceXs`
- Text: `Back`

### Box Button

- Width: `dialogButtonWidth`
- Margin: `spaceXs`
- Text: active color scheme `textPrimary`

---

## Settings Controls

### Toggle Row

- Width: `settingsRowWidth`
- Label: active color scheme `textPrimary`, `bodySmall`, width `settingsLabelWidth`
- Switch: standard Kool UI2 `Switch`
- Row margin: `spaceXs`

### Settings Row Card

- Width: `settingsContentWidth`
- Height: `settingsRowHeight`
- Margin: `spaceXs`
- Background: active color scheme `surfaceSunken`
- Label: active color scheme `textPrimary`, `bodySmall`, width `settingsLabelWidth`
- Control area: right-aligned standard Kool UI2 control
- Hover row background: active color scheme `surfaceRaised` when supported by the component primitive

### Color Scheme Selector Row

- Section title: `Color Scheme`
- Hint text: `Color scheme changes apply immediately.`
- Row width: `settingsRowWidth`
- Each row displays a primary-color swatch, color scheme display name, and selected indicator.
- Default row background: active color scheme `surfaceSunken`.
- Hover row background: active color scheme `surfaceRaised`.
- Selected row background: active color scheme `primaryContainer`.
- Row text and swatch colors always come from the selected color scheme palette.

### Section Gap

- Between setting rows: `spaceXs`
- Between sections and Back button: `spaceLg`

---

## HUD Overlay

- Position: top-left corner
- Panel margin: `spaceMd`
- Panel padding: `spaceSm`
- Background: active color scheme `panelHud`
- Text: active color scheme `textPrimary`, `hudText`, normal weight
- Line spacing: default

---

## Hover/Focus States

Kool UI2 handles baseline hover and focus behavior through `Colors.darkColors()`. RWX supplies those colors from the
active color scheme:

- **Button default**: active color scheme `surfaceSunken`, `textPrimary`
- **Button hover**: active color scheme `surfaceRaised`, `primary`
- **Switch active**: active color scheme `primary`
- **Selected color scheme row**: active color scheme `primaryContainer`

Do not introduce raw hover colors outside `UiTheme.kt`.

---

## Cross-Platform Constraints

### Allowed in core/ui/

- `de.fabmax.kool.modules.ui2.*` UI2 components
- `de.fabmax.kool.scene.Scene`
- `de.fabmax.kool.util.Color`
- `de.fabmax.kool.modules.ui2.Dp`

### Forbidden in core/ui/

- `de.fabmax.kool.KoolConfigJvm`
- `de.fabmax.kool.platform.*`
- `org.lwjgl.*`
- `android.*`
- `GameSession`, `AppCommand`, gameplay types
- `computePickRay`, `selectUnitAtWorld`, movement, waypoint types

### Legacy UI References

Legacy `.rml` and `.rcss` files under `assets/gui/` are visual references only. They must never be loaded, parsed, or
referenced from runtime code. All design tokens are expressed as Kotlin constants in `UiTheme.kt`.

---

## Design System File Map

| File                               | Purpose                                                                                    |
|------------------------------------|--------------------------------------------------------------------------------------------|
| `DESIGN.md`                        | Design system overview and constraints                                                     |
| `core/.../ui/UiTheme.kt`           | Shared color conversion, spacing, layout, and typography tokens plus color scheme registry |
| `core/.../ui/SettingsViewModel.kt` | Runtime selected color scheme state and settings selector items                            |
| `core/.../ui/UiComponents.kt`      | Shared composable functions for color-schemed UI elements                                  |
| `core/.../ui/*SceneHost.kt`        | Scene hosts consuming selected color scheme state                                          |
