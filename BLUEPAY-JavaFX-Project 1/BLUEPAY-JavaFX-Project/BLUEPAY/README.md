# BLUEPAY — JavaFX UPI Payment Simulator

A native Java 17+ desktop educational simulator. It does **not** connect to banks, UPI rails, or real payment services.

## Requirements
- JDK 17 or later
- Maven 3.8+
- Internet connection on first build to download Maven dependencies

## Run
```bash
mvn javafx:run
```
Run this from the folder containing `pom.xml`. In VS Code, open the `BLUEPAY-JavaFX-Project` folder, then use **Tasks: Run Task** and select **Run BLUEPAY (JavaFX)**. Do not launch `BluePayApp` directly; JavaFX requires its runtime modules, which the Maven JavaFX plugin configures.

## Demo access
Register a new local demo user. The initial demo UPI PIN is `1234` (you can change it in Profile & Settings). Passwords are stored as PBKDF2 hashes. Do not use real passwords or real UPI PINs.

## Included
- JavaFX dark navy / electric-blue interface
- Direct account registration and login, PBKDF2 password hashing
- SQLite persistence for users, accounts, transactions, contacts, notifications and payment settings
- Simulated transfers with atomic SQLite balance update + transaction insert
- Transaction search/status filtering and PDF receipt export
- Demo bank accounts, QR image upload/decode (ZXing), recharge and bill simulations
- Session checks and logout confirmation

## Data
SQLite database is created at `~/.bluepay/bluepay.db`. This is local demo data. Back up or delete that folder to reset the app.

## Safety
All balances and payments are fictional. QR decoding accepts BLUEPAY demo QR payloads such as:
`upi://pay?pa=alex@bluepay&pn=Alex&am=250`
No actual UPI PIN, bank account or financial credential should be entered.
