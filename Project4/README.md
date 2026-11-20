# CS 200 Project 4 - ChocAn Implementation (Java)

Command-line implementation of the Chocoholics Anonymous (ChocAn) data processing system for Team 16.

Implemented: login + role menus, member management, provider management, service recording with member
validation, main accounting procedure (member / provider / summary reports and EFT data), manager reports,
and the provider directory. It has two interfaces that share the same code and saved data: a console menu
(default) and a Swing GUI (`--gui`).

**Extra credit**
- **Persistence:** all data is saved to `data/chocan.dat` after every change and reloaded on the next start.
- **GUI:** `run-gui.bat` / `sh run-gui.sh` opens a window with the login screen and operator, provider and
  manager screens (tables, forms, report viewers).

## Requirements

A JDK (Java 11 or newer) with `javac` and `java` on the PATH. No libraries or build tools are needed.

## Compile and run

**Windows**

```
compile.bat
run.bat
```

**macOS / Linux**

```
sh compile.sh
sh run.sh
```

**By hand (any OS)** - from this folder:

```
javac -d out -sourcepath src src/chocan/Main.java
java -cp out chocan.Main
```

To open the **GUI** instead of the console menu, use `run-gui.bat` (Windows), `sh run-gui.sh` (macOS / Linux), or
`java -cp out chocan.Main --gui`.

`--reset` (e.g. `run.bat --reset`) deletes the saved data and starts again from the sample data.
Saved data is written to `data/` and report files to `reports/`, both relative to the folder you run from.
If you open the folder in VS Code or Eclipse, mark `src` as the source folder and run `chocan.Main`.

## Logging in (sample data created on first run)

| Role     | ID(s)                                  |
|----------|----------------------------------------|
| Operator | `300000001`                            |
| Manager  | `400000001`                            |
| Provider | `200000001`, `200000002`, `200000003`  |

Sample members are `100000001` - `100000005` (`100000003` is **suspended**). Sample service codes: `598470`
Dietitian Session, `883948` Aerobics Exercise, `293748` Counseling Session, `412356`, `730192`, `651204`,
`905517`, `318826` (see the Provider Directory). Type `exit` at the login prompt to quit.

Dates are entered as `MM-DD-YYYY`. Reports default to the last 7 days (press Enter to accept).

## Menus

| Menu     | Options |
|----------|---------|
| Operator | Add / edit / delete / list members; add / edit / delete / list providers; run main accounting procedure |
| Provider | Validate member; Bill ChocAn (record a service); request provider directory |
| Manager  | Summary report; member report; provider report; run main accounting procedure |

The GUI has the same functions: the operator gets Members / Providers / Accounting tabs, the provider gets
Validate member / Bill ChocAn / Provider directory tabs, and the manager gets Reports / Accounting tabs.

Editing a member also lets the operator suspend or reinstate them. Press Enter at any edit prompt to keep the
current value. New member/provider numbers are assigned automatically (a provider's number is their login ID).

## Suggested demo script (matches the rubric workflows)

1. Log in with a bad ID (`123456789`), then as operator `300000001`.
2. Operator: add a member, edit a member (reinstate `100000003`), delete a member, add / edit / delete a provider.
3. Log out; log in as provider `200000001`: validate `100000001` (Validated), `100000003` (suspended, if not
   yet reinstated) and `999999999` (Invalid number); Bill ChocAn for a valid member (date, code, confirm,
   comments); request the provider directory.
4. Repeat billing as providers `200000002` / `200000003` so the reports have data.
5. Log in as manager `400000001`: summary report, member report, provider report, run main accounting procedure
   (report files appear in `reports/`).
6. Exit completely, start the program again and show that members, providers and service records are still there
   (persistence extra credit).
7. Run `run-gui.bat` and show the same flows in the GUI (GUI extra credit): log in, add/edit/delete a member, bill
   a service as a provider, generate manager reports.

## Project layout

```
Project4/
  src/chocan/
    Main.java                     entry point: loads data, wires classes together
    model/    Person, Member, Provider, PersonDetails, StaffAccount, ServiceType,
              ServiceRecord, ReportPeriod, Role, MemberStatus
    data/     ChocAnDatabase (storage + persistence), SeedData (sample data)
    service/  AuthService, MemberManager, ProviderManager, BillingService, MemberCheck,
              ReportGenerator, AccountingProcedure, AccountingResult, ReportWriter,
              ProviderDirectory, UserSession, ChocAnException
    ui/       console interface: ChocAnApp (login), Menu (abstract), OperatorMenu, ProviderMenu,
              ManagerMenu, ConsoleIO, InputEndedException
    gui/      Swing interface: ChocAnGui (window), LoginPanel, OperatorPanel, ProviderPanel, ManagerPanel,
              AccountingPanel, PersonTablePanel, MemberTablePanel, ProviderTablePanel, PersonDialog, GuiUtil
    AppContext.java               builds the database + services for both interfaces
    util/     Validation, Formats
```

How the classes work together: `Main` builds a `ChocAnDatabase` and the service objects and hands them to
`ChocAnApp`. `ChocAnApp` (or `ChocAnGui`) asks `AuthService` to log the user in and opens the `OperatorMenu`,
`ProviderMenu` or `ManagerMenu` (or the matching GUI panel) for their role. The menus/panels only talk to the service classes (`MemberManager`,
`ProviderManager`, `BillingService`, `ReportGenerator`, `AccountingProcedure`, `ProviderDirectory`), which read
and change data through `ChocAnDatabase` and call `save()` after every change.

## Rubric map

| Rubric item | Where |
|-------------|-------|
| Login / menus | `ChocAnApp`, `AuthService`, `OperatorMenu`, `ProviderMenu`, `ManagerMenu` |
| Member / provider management | `MemberManager`, `ProviderManager`, `OperatorMenu` |
| Service records + member validation | `BillingService`, `MemberCheck`, `ProviderMenu` |
| Accounting procedure + reports | `AccountingProcedure`, `ReportGenerator`, `ReportWriter` |
| Manager reports | `ManagerMenu`, `ReportGenerator` |
| Provider directory | `ProviderDirectory`, `ProviderMenu` |
| Extra credit: GUI | `gui/` package, started with `--gui` |
| Extra credit: persistence | `ChocAnDatabase.save()` / `load()` |

## Team TODO before submitting

- **Authors:** every class has `@author [TEAM MEMBER NAME]` in its header comment. Replace it with the team
  member who actually wrote or owns that class (the assignment requires it), e.g. one person each for
  `model/` + `data/`, `service/` member/provider/billing, `service/` reports/accounting, and `ui/`.
- **Commits:** every team member must commit to the repository.
- **Video:** record the ~10 minute demo using the script above; explain the class interactions and say who did what.
- **ZIP:** zip this folder (`src`, scripts, `README.md`); do not include `out/`, `data/` or `reports/`.

## Design notes / assumptions

- Operators and managers have fixed IDs (sample staff accounts); providers log in with their provider number.
- A weekly period is "the 7 days ending today" and a service record belongs to the day it was entered.
  The original Friday-midnight schedule is replaced by running the accounting procedure from a menu.
- Service records copy the member/provider/service names and the fee when created, so later edits or deletes
  do not change past billing. If a provider is deleted after billing, the summary and EFT data still include them
  but no separate provider report is produced (a warning is shown).
- Suspended members cannot be billed.
