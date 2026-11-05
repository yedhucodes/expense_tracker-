# Expense Tracker — UI Redesign Build Plan

> **Project direction:** Obsidian Indigo — Liquid Finance  
> **Document type:** Implementation brief / build specification  
> **Baseline source:** `build-status.md`, generated 2026-11-05  
> **Primary visual reference:** [Dribbble — Tab Bar Animation](https://dribbble.com/shots/18177434-Tab-Bar-Animation)  
> **Animation library:** [LottieFiles](https://lottiefiles.com/)

---

## 1. Project objective

Redesign the existing expense-tracker Android application into a polished, modern fintech experience. The redesign should feel premium, fluid, minimal, and distinctive without compromising the existing financial features, data integrity, navigation behaviour, accessibility, or performance.

The visual system should be based on:

- Deep obsidian surfaces.
- Electric indigo and restrained violet accents.
- Selective liquid-glass surfaces and highlights.
- Clear financial information hierarchy.
- Consistent category iconography.
- Smooth spring-based interactions.
- Selective Lottie illustrations for expressive states.

**Primary design principle:** make the interface feel new through a coherent design system and interaction language, not through decorative effects on every element.

## 2. Important platform and architecture clarification

The supplied build-status document describes a **native Android application built with Kotlin and Jetpack Compose**. It does not describe a Flutter application.

Use the existing native stack:

- Kotlin 2.0.
- Jetpack Compose and Material 3.
- AndroidX Navigation 3.
- Room 2.7 with KSP.
- DataStore Preferences.
- Existing Haze and custom shader/liquid-glass implementation.
- JVM 21 / JDK 21.

Do not introduce Flutter or a second UI runtime as part of this redesign. If a separate Flutter target is later confirmed, treat it as a separate implementation project rather than mixing it into this Android codebase.

### Preserve existing architecture

Do not rewrite the database, repositories, business logic, calculations, navigation stack, or feature architecture solely to achieve the visual redesign. Prefer incremental refactoring of reusable UI components.

The current project already documents:

- `Navigation.kt` and `NavigationKeys.kt` for navigation.
- `GlassNavigationBar.kt` for the current custom navigation bar.
- Home, Activity, Insights, Budgets, Add Transaction, Category Manager, and Settings screens.
- Reusable glass components including `GlassCard`, `GlassDialog`, segmented controls, and text fields.

Before editing, inspect the actual repository and verify current class names, package paths, dependency versions, and implementation details. The build-status document is a baseline, not a substitute for inspecting source code.

## 3. Existing baseline — do not regress

The supplied build-status report records the following at its timestamp:

| Area | Recorded baseline |
|---|---|
| Git branch | `main` |
| Latest commit | `76e3596` |
| Working tree | Clean |
| Gradle build | Successful |
| Unit tests | 15 passed, 0 failed |
| Compile / target SDK | SDK 36 |
| Minimum SDK | SDK 24 |
| Kotlin / JVM | Kotlin 2.0 / JVM 21 |
| Debug APK | 20.57 MB |
| Monetary arithmetic | Minor units using `Long` |
| Database | Room 2.7 |

These values are the recorded baseline from the supplied document. Re-run the build and tests before making changes to establish the live state.

### Regression requirements

- Preserve minor-unit financial arithmetic; do not introduce floating-point money calculations.
- Preserve transaction creation, editing, deletion, and undo behaviour.
- Preserve budget calculations and threshold states.
- Preserve category management and fallback resolution.
- Preserve currency formatting and locale handling.
- Preserve JSON/CSV backup and restore behaviour.
- Preserve Android system back handling and top-level navigation behaviour.
- Do not change Room schemas or migrations unless a separately approved feature requires it.

---

## 4. Visual design system

### 4.1 Design name

**Obsidian Indigo — Liquid Finance**

### 4.2 Colour tokens

Use the following as the initial design-token proposal. First inspect the current theme implementation and consolidate duplicate or conflicting values into one source of truth.

| Token | Hex | Usage |
|---|---|---|
| `Canvas` | `#090A0F` | Main application background |
| `Surface` | `#131620` | Standard card and navigation underlay |
| `SurfaceRaised` | `#1B1F2D` | Raised cards, dialogs, selected surfaces |
| `Primary` | `#6366F1` | Primary action and active state |
| `VioletHighlight` | `#8B5CF6` | Restrained accent gradient/highlight |
| `Income` | `#10B981` | Positive cash flow and income |
| `Expense` | `#F43F5E` | Expense values and negative cash flow |
| `Warning` | `#F59E0B` | Budget caution and warning |
| `TextPrimary` | `#F8FAFC` | Main text and important figures |
| `TextSecondary` | `#94A3B8` | Supporting text |
| `TextTertiary` | `#64748B` | Low-emphasis labels |

### 4.3 Surface and glass rules

- Use glass treatment selectively for the bottom navigation, floating controls, modal surfaces, and a small number of important hero surfaces.
- Use opaque or near-opaque surfaces for dense transaction lists and information-heavy areas to maintain legibility.
- Use subtle white/translucent edge highlights rather than bright borders.
- Avoid excessive blur, reflections, glow, gradients, and layered transparency.
- Never place essential text over an uncontrolled or high-contrast background.
- Keep income, expense, and warning colours semantically consistent throughout the application.

### 4.4 Typography and spacing

- Use a consistent modern sans-serif type scale.
- Establish a clear hierarchy between screen title, key monetary figure, card title, body, and caption.
- Use tabular numerals for financial amounts where supported by the chosen typeface and Compose text configuration.
- Use an 8dp spacing rhythm, with smaller 4dp increments only where needed for compact icon/text alignment.
- Standardise corner radii, card padding, icon sizes, and touch-target dimensions in design tokens.
- Keep primary controls comfortably touchable and support Android font scaling.

### 4.5 Iconography

- Use one coherent icon family and consistent stroke/filled treatment.
- Avoid mixing emoji, unrelated icon packs, and inconsistent illustration styles in the same functional list.
- Category icons should use consistent container shapes and restrained semantic tints.
- Active and inactive navigation icons must have clearly distinguishable states without depending on colour alone.

---

## 5. Navigation redesign — highest-priority component

### Visual reference

[Dribbble — Tab Bar Animation](https://dribbble.com/shots/18177434-Tab-Bar-Animation)

Use the reference for its animated tab-selection language and sense of movement. Do not copy unrelated branding or colour choices. Adapt the interaction to the app's existing Obsidian Indigo visual system.

### 5.1 Proposed structure

Retain the application's current top-level destinations unless repository inspection shows a deliberate product decision to change them:

1. Home
2. Activity
3. Central Add Transaction action (`+`)
4. Insights
5. Budgets

The central action is an action, not a navigation destination. Keep it logically separate from the selected-tab state.

### 5.2 Visual behaviour

- Floating, rounded navigation capsule with a dark translucent underlay.
- Subtle border/highlight and restrained background blur.
- A selected-state background/indicator that moves fluidly between destinations.
- Selected icon receives a small scale/emphasis treatment.
- Selected label appears with a short fade/slide transition.
- Inactive labels remain visually quiet but readable.
- The central `+` action is a distinct circular or softly rounded floating control, visually aligned with the navigation capsule.
- Use a restrained indigo/violet treatment for the central action.
- Respect edge-to-edge insets, gesture navigation, and system bars.

### 5.3 Interaction behaviour

On selecting a tab:

1. Update the navigation state immediately and reliably.
2. Animate the active indicator to the selected destination.
3. Slightly emphasise the selected icon.
4. Reveal or emphasise the selected label.
5. Apply subtle haptic feedback where appropriate and available.
6. Keep the active state synchronised with the actual navigation back stack.

On pressing the central Add action:

- Provide a quick spring-based press response.
- Open the existing Add Transaction flow.
- Do not accidentally change the selected top-level tab.
- If a modal presentation is used, an optional plus-to-close morph may be used only when it communicates a real state change.

### 5.4 Motion targets

These are initial targets, not hard requirements. Tune them against real-device performance and perceived responsiveness.

| Interaction | Initial target |
|---|---:|
| Active indicator movement | 250–300 ms |
| Icon emphasis | 180–230 ms |
| Label appearance | 150–200 ms |
| FAB press response | 140–180 ms |
| Screen transition | 220–280 ms |

Use Compose animation APIs and spring/tween specifications appropriately. Avoid stacking several animations that create lag or make the active destination unclear.

### 5.5 Preserve current navigation capabilities

The supplied baseline documents the current navigation component as supporting direct accessible tab targets, horizontal drag tracking, spring physics, threshold haptics, droplet elongation, and a reactive floating action button.

Inspect and preserve working behaviour where possible. The redesign must not remove:

- Direct click/tap navigation.
- Correct selected-tab state.
- Existing horizontal drag behaviour if it remains reliable and discoverable.
- Accessibility semantics and tab roles.
- Device back handling.
- Home-root/back-stack behaviour.

Do not add drag gestures merely for decoration if they conflict with normal horizontal scrolling or accessibility.

---

## 6. Screen-by-screen redesign requirements

### 6.1 Home dashboard

**Goal:** Let users understand their current financial position and recent activity at a glance.

Redesign:

- Greeting/date or month header with a clear period selector.
- Net cash-flow hero card with a strong amount hierarchy.
- Separate income and expense summary metrics.
- Monthly budget overview and remaining amount.
- Daily spending trend or pulse visualisation.
- Top spending categories.
- Recent transactions with consistent category iconography.

Requirements:

- Preserve current date-period selection and financial aggregation.
- Ensure all hero figures use the existing money-formatting utilities.
- Avoid excessive cards above the fold; prioritise the most useful information.
- Provide useful loading, empty, and error states.

### 6.2 Activity screen

**Goal:** Make finding and understanding a transaction quick.

Redesign:

- Search field integrated with the screen header.
- Clear horizontal filter chips for All, Expenses, Income, Today, This Week, This Month, and This Year.
- Keep category and payment-method filters discoverable without overcrowding the initial viewport.
- Group transactions by Today, Yesterday, or formatted date.
- Use consistent category icon containers and readable amount alignment.
- Improve transaction detail/edit entry and swipe affordances.
- Provide snackbar undo feedback where existing deletion behaviour supports it.

Requirements:

- Preserve current query matching across notes, merchants, and categories.
- Preserve filter calculations and the filter-summary totals.
- Do not make swipe the only way to edit or delete a transaction.

### 6.3 Insights screen

**Goal:** Turn financial data into clear, readable patterns.

Redesign:

- Week / Month / Year / All segmented selector.
- Total spent and daily average metrics.
- Spending trend chart with a clear axis/period context.
- Category distribution chart, preferably a restrained donut/ring visual.
- Category breakdown with amounts, percentages, and transaction counts.
- Monthly net-flow comparison.

Requirements:

- Charts must reflect the existing calculation layer; do not duplicate financial logic inside composables.
- Provide a useful non-chart state when there is insufficient data.
- Use animation only when it clarifies data changes; avoid continuously moving chart elements.
- Preserve text alternatives or equivalent accessible summaries for chart information.

### 6.4 Budgets screen

**Goal:** Make planned spending and budget health obvious.

Redesign:

- Monthly budget summary card.
- Clearly labelled spent, remaining, and total budget values.
- Per-category budget cards with progress indicators.
- Distinct normal, caution, warning, and over-budget states.
- A clear create/edit budget action.
- Refined budget creation/edit dialog.

Requirements:

- Preserve current threshold logic and calculations.
- Keep warning states understandable without relying on colour alone.
- Do not use celebration animation for ordinary budget updates; reserve it for a meaningful completed goal if appropriate.

### 6.5 Add Transaction screen

**Goal:** Make the most frequent task fast, clear, and low-friction.

Redesign:

- Large, prominent amount field.
- Clear Expense / Income segmented toggle.
- Curated category grid with consistent icon containers.
- Date and time picker entry.
- Optional merchant/note field.
- Payment method selector or field.
- Strong, clearly labelled save action.
- Clear validation and save feedback.

Requirements:

- Preserve minor-unit input and currency-prefix behaviour.
- Preserve date/time handling and existing validation.
- Keep the screen keyboard-aware and usable on smaller devices.
- Ensure the save action cannot be triggered twice during an in-flight operation.

### 6.6 Category Manager

Redesign:

- Consistent category rows/cards.
- Clear expense/income category distinction.
- Improved icon picker and curated colour palette.
- Refined create/edit category dialog.

Requirements:

- Preserve category registry and fallback behaviour.
- Preserve the existing curated colour options unless there is a reason to migrate them.

### 6.7 Settings

Redesign:

- Group settings into clear sections.
- Improve currency selection presentation.
- Make category management easy to reach.
- Present JSON/CSV backup and restore actions clearly.
- Keep attribution and About information accessible without dominating the screen.

Requirements:

- Preserve currency options and persistence.
- Preserve backup and restore functionality.
- Do not change stored preferences or onboarding state without an explicit migration plan.

---

## 7. LottieFiles and motion strategy

### 7.1 Use Lottie for expressive moments

Use Lottie/dotLottie selectively for:

| Location | Suggested animation purpose |
|---|---|
| First-launch onboarding | Friendly finance introduction |
| Empty Activity | Empty wallet / no transactions illustration |
| Empty Insights | Analytics illustration when there is no data |
| Transaction saved | Small success/check animation |
| Receipt scanning (if/when implemented) | Document scanning feedback |
| Budget milestone | Small celebration for a meaningful completed goal |
| Loading (only where needed) | Minimal, unobtrusive loading state |

### 7.2 Prefer native Compose motion for interface mechanics

Use Compose animations for:

- Navigation indicator movement.
- Tab icon/label transitions.
- FAB press/scale response.
- Button state changes.
- Card expansion/collapse.
- Dialog presentation.
- Progress indicators.
- Chart drawing and data transitions.
- Small icon state transitions.

Do not use a Lottie animation for a simple opacity, position, scale, or progress animation that Compose can handle efficiently.

### 7.3 Asset selection and licensing

For every selected LottieFiles asset:

- Verify the asset's licence and permitted usage before shipping.
- Prefer minimal, clean illustrations that match the dark/indigo visual system.
- Avoid animations with baked-in background colours that clash with the app.
- Prefer transparent backgrounds where suitable.
- Keep animation loops subtle and stop animations when they are not visible or needed.
- Store production assets locally in the app; avoid making normal app functionality depend on a remote animation download.
- Record the source page, creator, licence, format, and intended usage in an asset manifest.

### 7.4 Suggested asset organisation

Adapt this structure to the project's actual resource conventions:

```text
app/src/main/res/
  raw/
    animation_onboarding.*
    animation_empty_activity.*
    animation_empty_insights.*
    animation_transaction_success.*
    animation_budget_milestone.*
```

Keep filenames descriptive and centralise asset references in one registry/helper rather than scattering raw resource IDs throughout feature screens.

### 7.5 Android player

Evaluate the official LottieFiles Android/dotLottie player and its current Compose integration before adding a dependency:

- https://github.com/LottieFiles/dotlottie-android
- https://docs.lottiefiles.com/

Check compatibility with the project's actual Gradle, Kotlin, Compose, and min-SDK configuration. Pin a stable compatible version; do not blindly copy an old version number from an example.

---

## 8. Proposed component architecture

The names below are suggested boundaries. Reuse and adapt existing components rather than creating duplicate versions.

```text
core/
  design/
    theme/
      AppTheme.kt
      Color.kt
      Type.kt
    tokens/
      ColorTokens.kt
      SpacingTokens.kt
      ShapeTokens.kt
      MotionTokens.kt
    components/
      GlassCard.kt
      GlassNavigationBar.kt
      GlassButton.kt
      GlassTextField.kt
      GlassDialog.kt
      GlassSegmentedControl.kt
      CategoryIcon.kt
      FinancialAmount.kt
      ProgressIndicator.kt
    animation/
      LottieAssets.kt
      AppAnimation.kt
```

Suggested component responsibilities:

- `ColorTokens`: single source of truth for semantic colours.
- `SpacingTokens`: standard spacing values.
- `ShapeTokens`: consistent radii and shape definitions.
- `MotionTokens`: reusable durations and animation specs.
- `GlassNavigationBar`: visual and interaction state for top-level navigation.
- `CategoryIcon`: consistent category icon and tint container.
- `FinancialAmount`: consistent amount formatting and typography presentation; formatting logic must remain in the existing domain/utility layer.
- `LottieAssets`: maps semantic animation names to packaged resources.

Do not create one enormous theme file or a single composable that contains every screen's styling. Keep components focused and testable.

---

## 9. Implementation phases and deliverables

### Phase 0 — Repository inspection and baseline verification

Tasks:

- Inspect the current repository and working tree.
- Read the existing theme, navigation, component, and screen implementations.
- Identify existing reusable components and avoid duplication.
- Run the current Gradle build and all unit tests.
- Capture baseline screenshots on at least one compact and one standard Android viewport if available.

Deliverables:

- Confirmed implementation map.
- Baseline build/test result.
- List of files/components planned for modification.

### Phase 1 — Design foundation

Tasks:

- Consolidate palette tokens.
- Define typography, spacing, shapes, and elevation/surface tokens.
- Establish consistent category icon treatment.
- Refine base glass components.
- Verify light/dark assumptions; do not introduce a light theme unless the product already supports or explicitly requires it.

Deliverables:

- Reusable design tokens.
- Updated shared components.
- No feature regression.

### Phase 2 — Navigation redesign

Tasks:

- Refine the current floating navigation capsule.
- Implement the active indicator transition.
- Refine selected icon and label transitions.
- Refine central Add Transaction button.
- Validate tab state against Navigation 3 back stack.
- Verify drag, tap, haptics, system insets, and back handling.

Deliverables:

- Production-ready navigation component.
- Navigation interaction tests or clear manual test record.
- Screenshots/video of each tab-selection state.

### Phase 3 — Core screen redesign

Recommended order:

1. Home.
2. Add Transaction.
3. Activity.
4. Insights.
5. Budgets.
6. Category Manager.
7. Settings.

For each screen:

- Keep existing state holders and domain logic.
- Update composables and shared component usage.
- Handle loading, empty, error, and populated states.
- Check compact layouts and font scaling.
- Verify all important actions.

Deliverables:

- Updated screens sharing one visual system.
- No duplicate business logic.
- Screenshots for each screen and key state.

### Phase 4 — Lottie integration

Tasks:

- Select and licence-check a small set of appropriate assets.
- Add the compatible Android player only if needed.
- Package assets locally.
- Create a central animation registry/helper.
- Add animations to approved empty, onboarding, and success states.
- Respect lifecycle and visibility so animations do not run unnecessarily.

Deliverables:

- Asset manifest with source and licence details.
- Reusable animation component.
- Integrated empty/success states.

### Phase 5 — Micro-interactions and polish

Tasks:

- Refine button press feedback.
- Refine dialog and card transitions.
- Add restrained chart transitions.
- Add haptics to meaningful actions only.
- Verify accessibility announcements and reduced-motion considerations where practical.

Deliverables:

- Consistent motion language.
- No distracting or continuous decorative motion.

### Phase 6 — QA, performance, and release readiness

Tasks:

- Run the full unit-test suite.
- Run Gradle build and relevant lint/static checks.
- Verify all financial calculations and transaction workflows.
- Verify database persistence across app restart.
- Verify backup and restore.
- Verify back navigation and tab restoration.
- Test compact and standard screen sizes.
- Inspect animation smoothness on a physical device where possible.
- Check APK size and resource growth.
- Remove unused assets and obsolete components.

Deliverables:

- Final build/test report.
- Regression checklist.
- Before/after screenshots.
- Updated build-status document.

---

## 10. Acceptance criteria

The redesign is complete only when all of the following are true.

### Visual consistency

- [ ] All primary screens use the same Obsidian Indigo design tokens.
- [ ] Cards, dialogs, fields, buttons, and segmented controls have consistent dimensions and radii.
- [ ] Glass is used selectively and does not reduce readability.
- [ ] Income, expense, and warning states are semantically consistent.
- [ ] Typography and financial amounts have a clear hierarchy.

### Navigation

- [ ] Every top-level destination can be selected by touch.
- [ ] Selected indicator always matches the actual destination.
- [ ] Central `+` opens the existing Add Transaction flow.
- [ ] The central action does not corrupt the selected-tab state.
- [ ] System back behaviour remains correct.
- [ ] Navigation remains usable with Android gesture navigation and accessibility services.
- [ ] No gesture conflict or dropped input is introduced.

### Functional integrity

- [ ] Existing transaction workflows still work.
- [ ] Existing financial calculations remain unchanged and correct.
- [ ] Monetary values continue to use integer minor units (`Long`) where currently designed.
- [ ] Budget thresholds and summaries remain correct.
- [ ] Search and filters return the same correct results.
- [ ] Category management still works.
- [ ] Currency preferences persist correctly.
- [ ] JSON/CSV backup and restore still work.
- [ ] Room data remains readable and no unintended migration is introduced.

### Motion and Lottie

- [ ] Navigation and core interactions use native Compose motion where practical.
- [ ] Lottie is limited to selected expressive states.
- [ ] Lottie assets are locally packaged for normal use.
- [ ] Asset licences and sources are recorded.
- [ ] Off-screen animations do not run unnecessarily.
- [ ] Motion does not block user input or make the app feel slow.

### Quality

- [ ] Gradle build succeeds.
- [ ] All existing tests pass.
- [ ] New logic/components have appropriate tests where applicable.
- [ ] No crashes on screen entry, navigation, or transaction save.
- [ ] App works on the configured minimum SDK and current target configuration.
- [ ] APK size is measured against the 20.57 MB recorded debug baseline.
- [ ] Final status and any known limitations are documented honestly.

---

## 11. Guardrails for the coding agent

When implementing this plan, follow these rules:

1. Inspect the actual source files before editing. Do not assume paths or class contents based only on this document.
2. Work in small, verifiable phases. Build and test after each meaningful phase.
3. Preserve existing architecture and business logic unless a necessary change is explicitly justified.
4. Do not change financial arithmetic, currency conversion, date aggregation, or budget thresholds as part of a visual task.
5. Do not add a dependency without checking compatibility, maintenance status, and APK impact.
6. Do not invent new product features during the redesign.
7. Do not replace the current navigation system with a different navigation framework.
8. Do not add Flutter to this Kotlin project.
9. Do not use Lottie for every interaction; prefer native Compose animation for UI mechanics.
10. Do not leave placeholder buttons, fake data, or non-functional interactions.
11. Maintain accessibility semantics, content descriptions, touch targets, and readable contrast.
12. Keep code modular and consistent with the existing package structure.
13. Never report tests as passing unless they were actually run.
14. If an implementation conflicts with the existing architecture, explain the conflict and propose the smallest safe adjustment.

---

## 12. Suggested implementation prompt

Use the following prompt when handing this document to a coding agent:

> Read `EXPENSE_TRACKER_UI_REDESIGN_BUILD_PLAN.md` and the existing `build-status.md`. Inspect the actual Kotlin/Jetpack Compose repository before making changes. Implement the redesign incrementally, starting with Phase 0 and then the design foundation and animated navigation. The supplied project is native Android Kotlin + Jetpack Compose, not Flutter. Preserve Navigation 3, Room, DataStore, existing financial calculations, minor-unit money representation, transaction workflows, and back handling. Reuse and refine existing glass components instead of duplicating them. Use the Dribbble tab-bar reference for interaction inspiration and adapt it to the Obsidian Indigo theme. Use LottieFiles selectively for expressive empty/success/onboarding states; use native Compose animations for navigation and micro-interactions. Build and run tests after each phase. Do not silently skip tests, invent functionality, or claim completion without verification. At the end of each phase, report changed files, behaviours verified, test/build results, and known issues. Do not proceed to destructive architectural changes without documenting why they are necessary.

---

## 13. Reference links

- Dribbble tab-bar animation reference: https://dribbble.com/shots/18177434-Tab-Bar-Animation
- LottieFiles asset library: https://lottiefiles.com/
- LottieFiles developer documentation: https://docs.lottiefiles.com/
- LottieFiles Android/dotLottie repository: https://github.com/LottieFiles/dotlottie-android

---

## 14. Final product direction

The finished application should feel like a premium, modern finance product: a calm obsidian canvas, purposeful indigo accents, highly readable financial information, an expressive animated floating navigation bar, and selective animation that makes important moments feel responsive.

**Implementation priority:** design foundation → navigation → core screens → Lottie states → micro-interactions → regression and performance validation.

**Do not sacrifice correctness for appearance.** The existing finance engine is the foundation; this project is a controlled visual and interaction redesign.

