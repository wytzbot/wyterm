# WyTerm

Phone-first Android developer terminal starter.

## Monetization

- **Terminal + command execution: FREE forever**
- **No ads**
- **No AI**
- **No account/signup required**
- **WyTerm Pro: $5 one-time purchase through Google Play Billing**
- Premium features are unlocked by the user's Google Play purchase and restored automatically when the app starts.

### Important payment design
Google Play Billing does not provide the app with the payer's email address as a reliable entitlement credential. Therefore WyTerm does **not** pretend to validate a typed email against the payment. Instead, it uses Google Play's purchase record and restore flow, so the user can enjoy Pro without creating a WyTerm account.

If email-based recovery is ever added, it should be an optional recovery/support identifier, not the proof of payment.

## Play Console setup

Create a one-time in-app product (managed product) with:

- Product ID: `wyterm_pro`
- Price: USD $5.00 (Google Play can apply local pricing/taxes according to its configuration)

The app reads the product price from Google Play rather than hardcoding a currency amount in the purchase UI.

## Current V1.1

- Native Kotlin Android project
- Free local shell command execution
- Mobile shortcut bar
- Pro purchase button
- Google Play Billing 8.0.0 integration
- Purchase restore on launch/resume
- Purchase acknowledgement
- No backend required for the basic purchase/restore flow

## Premium feature placeholders

The purchase gate is ready for these features:

- Advanced code editor
- Project workspace
- Git GUI
- Local server tools
- SSH profiles
- Extra developer tools

The current starter still needs the actual Linux userspace/runtime before it becomes a full Termux-class environment.

## Phone-first development

Open/import the project in a Gradle-capable Android IDE on your phone. Google Play Billing testing requires a Play-configured app and license/test track setup; Google documents that license testers can test billing without normal charges.
