# TasteBuds Catering System

A console-based Java application that simulates an end-to-end restaurant order management workflow — from customer order placement through kitchen preparation to delivery and customer feedback. This project was developed as an assignment for the Object Oriented Concepts-II course (3rd Semester).

## Table of contents

- About
- Key features
- High-level flow (what the program does)
- Main menus and what each does
- How to use (step-by-step examples)
- Project layout & persistence
- How to run and test
- Notes

## About

TasteBuds is a small, educational, console-driven catering system implemented in Java and organized with clear model, service, and persistence layers. It demonstrates object-oriented design, simple input-driven UI, XML persistence via JAXB, and unit tests for core model/service behavior.

## Key features

- Console-based interactive menus for customers, kitchen staff, and delivery management.
- Support for Registered and Guest customers (models under `model/`).
- Order lifecycle: create order, kitchen preparation by chefs, hand-off to delivery drivers.
- Driver verification (driving license model) and vehicle assignment for deliveries.
- Feedback subsystem where customers can leave feedback after delivery.
- XML persistence for all core data (customers, drivers, chefs, orders, vehicles, feedback) using `JAXBHandler` under `persistence/`.
- Unit tests that cover models and service behaviors (see `test/` directory).

## What the program actually does (high-level flow)

1. Start the application (entry point: `Main.java`).
2. Present a top-level menu that separates user interactions into logical areas (Customer, Kitchen, Delivery, Feedback, Exit).
3. Customers can register or use the system as guests, then create orders by selecting categories/items and providing delivery details.
4. Orders are recorded and persisted to XML files in the `data/` folder.
5. Kitchen staff (chefs/head chef) view pending orders, update preparation status, and mark orders ready for delivery.
6. Delivery manager assigns an available driver and vehicle, verifies the driver's license, and marks the order as out-for-delivery.
7. Once the delivery is completed, customers can leave feedback, which is persisted as well.

## Main menus and mapping (what menu takes you where)

Below is an organized mapping so any user can understand where to find functionality.

- Main Menu
	- Customer Menu
		- Register or Login as Registered Customer
		- Continue as Guest (GuestCustomer)
		- Place New Order (choose category, item, quantity, address)
		- View Order History / Track Order
	- Kitchen / Chef Menu
		- Login as Chef or HeadChef
		- View Pending Orders
		- Mark Order as Prepared
		- HeadChef: overview and priority ordering
	- Delivery / Driver Menu
		- View Orders Ready for Delivery
		- Assign Driver and Vehicle
		- Verify Driver's DrivingLicense
		- Mark Order as Delivered
	- Feedback Menu
		- Leave feedback for a delivered order
		- View existing feedback entries
	- Feedback Menu
		- Leave feedback for a delivered order
		- View existing feedback entries
	- Admin / System Menu (Admin only)
		- Manage data files (view / reload from XML)
		- Save all data to XML (persist current in-memory state)
		- Export / Import XML (backup or load external data)
		- Add / Edit / Remove records (customers, drivers, chefs, vehicles, orders, feedback)
		- Run data validation / consistency checks
		- Reset data to defaults or load sample data
		- Backup current data files (copy `data/*.xml` to a timestamped file)
	- Exit / System Menu
		- Save and exit (invokes the same save-all persistence behavior)
		- (Admin options: see Admin / System Menu above)

Note: The project organizes these roles around model classes in `model/` (for example, `Customer`, `Order`, `Chef`, `Driver`, `Vehicle`, `Feedback`) and the coordinating logic resides in the `service/` package (`TasteBudsSystem`). Persistence is handled centrally by `persistence/JAXBHandler` which reads/writes the XML files under `data/`.

## How to use — quick walkthroughs

Example: Place an order as a guest

1. Start the app (see run steps below).
2. From the Main Menu choose: Customer -> Continue as Guest.
3. Choose a category and items, enter quantities and delivery address.
4. Confirm order. The system creates an `Order` object and persists it.
5. The kitchen menu will show the new order as pending.

Example: Kitchen processing + delivery

1. Kitchen: Chef views pending orders and marks the order as "Prepared" when done.
2. Delivery Manager: views prepared orders, chooses a driver and vehicle, verifies the driver's license status, and assigns the delivery.
3. Driver completes delivery and the order is marked as delivered.
4. The customer can then leave feedback via the Feedback menu.

## Project layout (important files & folders)

- `src/main/java/com/tastebuds/model/` — domain classes: `Customer`, `Order`, `Chef`, `Driver`, `Vehicle`, `Feedback`, and list-wrapper classes like `OrderList` used for JAXB.
- `src/main/java/com/tastebuds/service/TasteBudsSystem.java` — central coordination and menu-driven flows.
- `src/main/java/com/tastebuds/persistence/JAXBHandler.java` — simple XML read/write utilities for persisting application data (`data/*.xml`).
- `data/` — XML files used at runtime: `chefs.xml`, `customers.xml`, `drivers.xml`, `orders.xml`, `vehicles.xml`, `feedback.xml`.
- `test/` — unit tests for models/service implementations.

## How to run

This project uses Maven. From the project root run:

```bash
# compile and run the main class
mvn compile
mvn exec:java -Dexec.mainClass="com.tastebuds.Main"
```

To run unit tests:

```bash
mvn test
```

If you prefer an IDE, import the project as a Maven project and run `com.tastebuds.Main`.

## Data and persistence notes

- All runtime data is stored as XML files in `data/`. The project includes sample XML files that the program reads and updates.
- `JAXBHandler` serializes/deserializes the list-wrapper classes (e.g., `OrderList`) to/from XML. If you edit or replace XML files manually, keep the same XML structure to avoid parsing errors.

Persistence and save behavior (Admin + Exit)

- Saving: The application persists in-memory data (customers, drivers, chefs, vehicles, orders, feedback) to the XML files located in the `data/` directory. Use the Admin / System Menu -> "Save all data to XML" to explicitly write the current state to disk. The Exit / System Menu -> "Save and exit" triggers the same save behavior before shutting down.
- Export / Import: The Admin menu includes options to export the current XML files (create a copy for backup) and to import an XML file to replace or merge with the current data. Use import with care: the XML must match the expected JAXB structure.
- Backups: The Admin menu can create timestamped backups of the `data/*.xml` files (for example, copying them into a `data/backups/` folder with a timestamp). Backups let you restore a previous application state should manual edits or imports go wrong.
- Reloading: After performing a manual edit on XML files (outside the app), use Admin -> Reload from XML to re-read and replace the in-memory data with the file contents. This avoids restarting the application.
- Validation: Admin -> Run data validation helps detect malformed XML, missing required fields, duplicate IDs, or referential inconsistencies before saving or importing.

Note: The README documents recommended admin capabilities; if you extend or modify persistence behavior in code, make sure `JAXBHandler` and the data wrapper classes match any new XML layout.

## Developer notes & testing

- Follow the existing package structure when adding features: keep domain objects in `model/`, business logic in `service/`, and persistence in `persistence/`.
- Unit tests live under `test/` and use the same package structure — run them with `mvn test`.
- The project currently expects console input; consider adding a small script or integration test if you need automated interaction.

## A few typical edge cases to be aware of

- Empty or malformed XML files — the JAXB handler expects a valid structure.
- Duplicate IDs for customers/drivers — the model code assumes unique identifiers.
- Concurrency isn't handled (this is a local, single-user console app).

## Note

This project was completed as an assignment for the 3rd Semester university course "Object Oriented Concepts-II." The code and tests reflect learning-focused implementations rather than production-ready robustness.

---

