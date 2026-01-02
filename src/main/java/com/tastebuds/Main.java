package com.tastebuds;

import com.tastebuds.model.*;
import com.tastebuds.service.TasteBudsSystem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static TasteBudsSystem system = new TasteBudsSystem();

    public static void main(String[] args) {
        system.loadData();

        while (true) {
            displayRoleSelection();
            int role = getIntInput();

            switch (role) {
                case 1 -> customerMenu();
                case 2 -> headChefMenu();
                case 3 -> deliveryManagerMenu();
                case 4 -> driverMenu();
                case 5 -> adminMenu();
                case 6 -> {
                    system.saveData();
                    System.out.println("\n✔ Data saved successfully!");
                    System.out.println("Thank you for using TasteBuds Catering System!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid role choice! Please try again.");
            }
        }
    }

    private static void displayRoleSelection() {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║     TasteBuds Catering System (TBCS)       ║");
        System.out.println("║           Role Selection Menu              ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println("Current Serving: Order #" + String.format("%03d", system.getCurrentServingOrder()));
        System.out.println();
        System.out.println("1. Customer");
        System.out.println("2. Head Chef");
        System.out.println("3. Delivery Manager");
        System.out.println("4. Driver");
        System.out.println("5. Admin");
        System.out.println("6. Save & Exit");
        System.out.print("\n➤ Select your role: ");
    }

    private static void customerMenu() {
        while (true) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. Place an Order");
            System.out.println("2. Provide Feedback / Mark Delivered");
            System.out.println("3. View Current Queue");
            System.out.println("4. Back to Role Selection");
            System.out.print("➤ Choice: ");
            int c = getIntInput();
            switch (c) {
                case 1 -> placeOrder();
                case 2 -> customerFeedback();
                case 3 -> viewCurrentQueue();
                case 4 -> {
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void headChefMenu() {
        while (true) {
            System.out.println("\n--- Head Chef Menu ---");
            System.out.println("1. Kitchen Preparation (Process an Order)");
            System.out.println("2. View Current Queue");
            System.out.println("3. View All Chefs");
            System.out.println("4. Back to Role Selection");
            System.out.print("➤ Choice: ");
            int c = getIntInput();
            switch (c) {
                case 1 -> kitchenPreparation();
                case 2 -> viewCurrentQueue();
                case 3 -> viewAllChefs();
                case 4 -> { return; }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void deliveryManagerMenu() {
        while (true) {
            System.out.println("\n--- Delivery Manager Menu ---");
            System.out.println("1. Assign Delivery");
            System.out.println("2. View All Drivers");
            System.out.println("3. View Current Queue");
            System.out.println("4. Back to Role Selection");
            System.out.print("➤ Choice: ");
            int c = getIntInput();
            switch (c) {
                case 1 -> deliveryAssignment();
                case 2 -> viewAllDrivers();
                case 3 -> viewCurrentQueue();
                case 4 -> { return; }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void driverMenu() {
        while (true) {
            System.out.println("\n--- Driver Menu ---");
            System.out.println("1. Driver Checkout (Start Delivery)");
            System.out.println("2. Mark Order Delivered");
            System.out.println("3. Back to Role Selection");
            System.out.print("➤ Choice: ");
            int c = getIntInput();
            switch (c) {
                case 1 -> driverCheckout();
                case 2 -> driverMarkDelivered();
                case 3 -> { return; }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void driverMarkDelivered() {
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════╗");
        System.out.println("║       DRIVER-MARK DELIVERED       ║");
        System.out.println("╚═══════════════════════════════════╝");
        System.out.print("➤ Enter Order Number: ");
        String orderNo = scanner.nextLine();

        System.out.print("➤ Enter Driving License Number: ");
        String licenseNo = scanner.nextLine();

        boolean success = system.driverCompleteDelivery(orderNo, licenseNo);

        if (success) {
            System.out.println("\n✓ Delivery confirmed and recorded!");
            System.out.println("  Order " + orderNo + " marked as DELIVERED.");
            // Persist change
            system.saveData();
        } else {
            System.out.println("\nFailed to confirm delivery. Check order number and license.");
        }
    }



    private static void placeOrder() {
        System.out.println("\n" +
                           "╔═══════════════════════════════════╗");
        System.out.println("║          PLACE AN ORDER           ║");
        System.out.println("╚═══════════════════════════════════╝");
        System.out.println("\nCustomer Type:");
        System.out.println("  1. Registered Customer");
        System.out.println("  2. Guest Customer");
        System.out.print("➤ Choice: ");
        int type = getIntInput();

        Customer customer = null;
        if (type == 1) {
            System.out.print("➤ Enter Customer ID: ");
            String id = scanner.nextLine();
            customer = system.findCustomer(id);
            if (customer == null) {
                System.out.println("Customer not found!");
                return;
            }
            if (customer instanceof RegisteredCustomer rc) {
                System.out.println("✓ Found: " + rc.getName());
                System.out.println("  Orders this month: " + rc.getOrdersThisMonth());
                System.out.println("  Current discount: " + rc.getDiscount() + "%");
            }
        } else {
            System.out.print("➤ Enter Name: ");
            String name = scanner.nextLine();
            customer = new GuestCustomer("GUEST_" + System.currentTimeMillis(), name);
        }

        // Show hardcoded menu and let customer choose items and quantities
        Map<String, Double> menu = system.getMenu();
        List<String> itemNames = new ArrayList<>(menu.keySet());

        System.out.println("\n--- MENU ---");
        for (int i = 0; i < itemNames.size(); i++) {
            String item = itemNames.get(i);
            System.out.printf("%2d. %-25s %6.2f BDT\n", i + 1, item, menu.get(item));
        }

        Map<String, Integer> selection = new LinkedHashMap<>();
        while (true) {
            System.out.print("Enter item number to add (0 to finish): ");
            int idx = getIntInput();
            if (idx == 0) break;
            if (idx < 1 || idx > itemNames.size()) {
                System.out.println("Invalid item number");
                continue;
            }
            System.out.print("Quantity: ");
            int qty = getIntInput();
            String chosen = itemNames.get(idx - 1);
            selection.put(chosen, selection.getOrDefault(chosen, 0) + qty);
            System.out.println("Added: " + qty + " x " + chosen);
        }

        if (selection.isEmpty()) {
            System.out.println("No items selected. Cancelling order.");
            return;
        }

        StringBuilder itemsBuilder = new StringBuilder();
        double bill = 0.0;
        for (var e : selection.entrySet()) {
            itemsBuilder.append(e.getValue()).append("x ").append(e.getKey()).append(", ");
            bill += menu.get(e.getKey()) * e.getValue();
        }
        String items = itemsBuilder.toString();

        Order order = system.placeOrder(customer, items, bill);

        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════╗");
        System.out.println("║    ORDER PLACED SUCCESSFULLY!     ║");
        System.out.println("╚═══════════════════════════════════╝");
        System.out.println("Order Number    : " + order.getOrderNo());
        System.out.println("Queue Position  : " + order.getQueuePosition());
        System.out.println("Original Bill   : " + order.getBillAmount() + " BDT");
        if (customer instanceof RegisteredCustomer rc) {
            System.out.println("Discount Applied: " + rc.getDiscount() + "%");
            System.out.println("You saved       : " +
                    (order.getBillAmount() - order.getFinalBill()) + " BDT");
        }
        System.out.println("Final Bill      : " + order.getFinalBill() + " BDT");
        System.out.println("═══════════════════════════════════════");
    }

    private static void kitchenPreparation() {
        System.out.println("\n" +
                           "╔═══════════════════════════════════╗");
        System.out.println("║       HEAD CHEF DASHBOARD         ║");
        System.out.println("╚═══════════════════════════════════╝");
        System.out.print("➤ Enter Order Number: ");
        String orderNo = scanner.nextLine();

        Order order = system.findOrder(orderNo);
        if (order == null || !order.getStatus().equals("PLACED")) {
            System.out.println("Order not found or already processed!");
            return;
        }

        System.out.println("\nOrder Details:");
        System.out.println("  Customer ID : " + order.getCustomerId());
        System.out.println("  Items       : " + order.getItems());
        System.out.println("  Bill Amount : " + order.getFinalBill() + " BDT");

        OrderCategory suggested = order.getFinalBill() > 1000 ? OrderCategory.PRIORITY : OrderCategory.NORMAL;
        System.out.println("\nSuggested Category based on details: " + suggested);
        System.out.println("  1. Accept suggested");
        System.out.println("  2. Toggle to other category");
        System.out.print("➤ Choice: ");
        int choice = getIntInput();
        OrderCategory category = (choice == 2) ? (suggested == OrderCategory.PRIORITY ? OrderCategory.NORMAL : OrderCategory.PRIORITY) : suggested;

        System.out.print("➤ Assign Chefs (comma separated names): ");
        String chefs = scanner.nextLine();

        System.out.print("➤ Estimated Time (minutes): ");
        int time = getIntInput();

        system.prepareOrder(orderNo, category, chefs, time);

        System.out.println("\n✓ Order assigned for preparation!");
        System.out.println("  Category: " + category);
        System.out.println("  Chefs: " + chefs);
        System.out.println("  Estimated Time: " + time + " minutes");
        System.out.println("  Status: PREPARING");
    }

    private static void deliveryAssignment() {
        System.out.println("\n" +
                           "╔═══════════════════════════════════╗");
        System.out.println("║         DELIVERY MANAGER          ║");
        System.out.println("╚═══════════════════════════════════╝");
        System.out.print("➤ Enter Order Number: ");
        String orderNo = scanner.nextLine();

        Order order = system.findOrder(orderNo);
        if (order == null || !order.getStatus().equals("READY")) {
            System.out.println("Order not found or not ready for delivery!");
            System.out.println("   Current status: " + (order != null ? order.getStatus() : "NOT FOUND"));
            return;
        }

        System.out.println("Delivery Assignment Options:");
        System.out.println(" 1. Auto-assign driver & vehicle (based on availability)");
        System.out.println(" 2. Manual assign");
        System.out.print("➤ Choice: ");
        int opt = getIntInput();

        boolean assigned = false;
        if (opt == 1) {
            assigned = system.autoAssignDelivery(orderNo);
            if (!assigned) {
                System.out.println("Unable to auto-assign (no available driver or vehicle)");
                return;
            }
            Driver assignedDriver = system.findDriver(order.getDriverId());
            System.out.println("\n✓ Delivery Assigned Successfully (Auto)!");
            System.out.println("  Order    : " + orderNo);
            System.out.println("  Driver   : " + (assignedDriver != null ? assignedDriver.getName() : order.getDriverId()));
            System.out.println("  Vehicle  : " + order.getVehicle());
        } else {
            System.out.print("➤ Enter Driver ID: ");
            String driverId = scanner.nextLine();

            Driver driver = system.findDriver(driverId);
            if (driver == null) {
                System.out.println("Driver not found!");
                return;
            }

            if (!driver.isAvailable()) {
                System.out.println("Driver is currently unavailable!");
                return;
            }

            System.out.print("➤ Enter Vehicle ID: ");
            String vehicle = scanner.nextLine();

            system.assignDelivery(orderNo, driverId, vehicle);

            System.out.println("\n✓ Delivery Assigned Successfully (Manual)!");
            System.out.println("  Order    : " + orderNo);
            System.out.println("  Driver   : " + driver.getName());
            System.out.println("  Vehicle  : " + vehicle);
        }

        System.out.println("  Category : " + order.getCategory());
        if (order.getCategory() == OrderCategory.PRIORITY) {
            System.out.println("  ⚡ Priority - Assigned within 10 minutes");
        }
        System.out.println("  Status   : OUT_FOR_DELIVERY");
    }

    private static void driverCheckout() {
        System.out.println("╔═══════════════════════════════════╗");
        System.out.println("║       DRIVER MOBILE APP           ║");
        System.out.println("╚═══════════════════════════════════╝");
        System.out.print("➤ Enter Order Number: ");
        String orderNo = scanner.nextLine();

        System.out.print("➤ Enter Driving License Number: ");
        String licenseNo = scanner.nextLine();

        boolean success = system.driverCheckout(orderNo, licenseNo);

        if (success) {
            System.out.println("\n✓ License Verified");
            System.out.println("✓ Delivery Checked Out Successfully!");
            System.out.println("\nYou can now proceed to deliver the order.");
        } else {
            System.out.println("\nVerification Failed!");
            System.out.println("   Please check order number and license details.");
        }
    }

    private static void customerFeedback() {
        System.out.println("\n" +
                           "╔═══════════════════════════════════╗");
        System.out.println("║       CUSTOMER FEEDBACK           ║");
        System.out.println("╚═══════════════════════════════════╝");
        System.out.print("➤ Enter Order Number: ");
        String orderNo = scanner.nextLine();

        Order order = system.findOrder(orderNo);
        if (order == null) {
            System.out.println("Order not found!");
            return;
        }

        System.out.println("\nOrder Details:");
        System.out.println("  Items: " + order.getItems());
        System.out.println("  Status: " + order.getStatus());

        System.out.print("\n➤ Mark Order Delivered? (yes/no): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("yes")) {
            System.out.print("➤ Rate Your Order (1-5): ");
            int rating = getIntInput();

            System.out.print("➤ Any Comments? ");
            String comment = scanner.nextLine();

            system.submitFeedback(orderNo, rating, comment);

            System.out.println("\n✓ Thank you for your feedback!");
            System.out.println("  Rating: " + rating + "/5");
            System.out.println("  Comment: " + comment);
        }
    }

    private static void viewCurrentQueue() {
        System.out.println("\n" +
                           "╔═══════════════════════════════════╗");
        System.out.println("║       CURRENT ORDER QUEUE         ║");
        System.out.println("╚═══════════════════════════════════╝");
        system.displayQueue();
    }

    private static void registerCustomer() {
        System.out.println("\n" +
                           "╔═══════════════════════════════════╗");
        System.out.println("║         REGISTER CUSTOMER         ║");
        System.out.println("╚═══════════════════════════════════╝");
        System.out.print("➤ Customer ID: ");
        String id = scanner.nextLine();

        if (system.findCustomer(id) != null) {
            System.out.println("Customer ID already exists!");
            return;
        }

        System.out.print("➤ Name: ");
        String name = scanner.nextLine();

        RegisteredCustomer customer = new RegisteredCustomer(id, name);
        system.addCustomer(customer);
        System.out.println("\n✓ Customer registered successfully!");
        System.out.println("  ID: " + id);
        System.out.println("  Name: " + name);
    }

    private static void registerDriver() {
        System.out.println("\n" +
                           "╔═══════════════════════════════════╗");
        System.out.println("║          REGISTER DRIVER          ║");
        System.out.println("╚═══════════════════════════════════╝");
        System.out.print("➤ Driver ID: ");
        String id = scanner.nextLine();

        if (system.findDriver(id) != null) {
            System.out.println("Driver ID already exists!");
            return;
        }

        System.out.print("➤ Name: ");
        String name = scanner.nextLine();
        System.out.print("➤ License Number: ");
        String licenseNo = scanner.nextLine();

        Driver driver = new Driver(id, name, licenseNo);
        system.addDriver(driver);
        System.out.println("\n✓ Driver registered successfully!");
        System.out.println("  ID: " + id);
        System.out.println("  Name: " + name);
        System.out.println("  License: " + licenseNo);
    }

    private static void registerVehicle() {
        System.out.println("\n" +
                           "╔═══════════════════════════════════╗");
        System.out.println("║        REGISTER VEHICLE           ║");
        System.out.println("╚═══════════════════════════════════╝");
        System.out.print("➤ Vehicle ID: ");
        String id = scanner.nextLine();

        System.out.print("➤ Vehicle Type (e.g. Bike/Car/Van): ");
        String type = scanner.nextLine();

        Vehicle v = new Vehicle(id, type);
        system.addVehicle(v);
        System.out.println("\n✓ Vehicle registered successfully!");
        System.out.println("  ID: " + id);
        System.out.println("  Type: " + type);
        system.saveData();
    }

    private static void adminMenu() {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Register New Customer");
            System.out.println("2. Register New Driver");
            System.out.println("3. Register New Chef");
            System.out.println("4. Register New Vehicle");
            System.out.println("5. View All Customers");
            System.out.println("6. View All Drivers");
            System.out.println("7. View All Vehicles");
            System.out.println("8. View All Chefs");
            System.out.println("9. View Current Queue");
            System.out.println("10. View All Feedbacks");
            System.out.println("11. Save Data");
            System.out.println("12. Back to Role Selection");
            System.out.print("➤ Choice: ");
            int c = getIntInput();
            switch (c) {
                case 1 -> registerCustomer();
                case 2 -> registerDriver();
                case 3 -> registerChef();
                case 4 -> registerVehicle();
                case 5 -> viewAllCustomers();
                case 6 -> viewAllDrivers();
                case 7 -> viewAllVehicles();
                case 8 -> viewAllChefs();
                case 9 -> viewCurrentQueue();
                case 10 -> viewAllFeedbacks();
                case 11 -> {
                    system.saveData();
                    System.out.println("\n✓ Data saved");
                }
                case 12 -> { return; }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void registerChef() {
        System.out.println("\n" +
                           "╔═══════════════════════════════════╗");
        System.out.println("║          REGISTER CHEF            ║");
        System.out.println("╚═══════════════════════════════════╝");
        System.out.print("➤ Chef Name: ");
        String name = scanner.nextLine();

        if (system.findChef(name) != null) {
            System.out.println("Chef with that name already exists!");
            return;
        }

        Chef chef = new Chef(name);
        system.addChef(chef);
        // Persist chefs to XML immediately
        system.saveData();

        System.out.println("\n✓ Chef registered successfully!");
        System.out.println("  Name: " + name);
    }

    private static void viewAllChefs() {
        System.out.println("\n" +
                           "╔═══════════════════════════════════╗");
        System.out.println("║           ALL CHEFS               ║");
        System.out.println("╚═══════════════════════════════════╝");
        system.displayAllChefs();
    }

    private static void viewAllVehicles() {
        System.out.println("\n" +
                           "╔═══════════════════════════════════╗");
        System.out.println("║         ALL VEHICLES              ║");
        System.out.println("╚═══════════════════════════════════╝");
        List<Vehicle> vehicles = system.getVehicles();
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles registered");
            return;
        }
        System.out.println("\n┌────────────┬──────────┬──────────┐");
        System.out.println("│ Vehicle ID │   Type   │ Available│");
        System.out.println("├────────────┼──────────┼──────────┤");
        for (Vehicle v : vehicles) {
            System.out.printf("│ %-10s │ %-10s │ %-8s │%n", v.getVehicleId(), v.getType(), v.isAvailable() ? "Yes" : "No");
        }
        System.out.println("└────────────┴──────────┴──────────┘");
    }

    private static void viewAllFeedbacks() {
        System.out.println("\n" +
                           "╔═══════════════════════════════════╗");
        System.out.println("║         ALL CUSTOMER FEEDBACKS    ║");
        System.out.println("╚═══════════════════════════════════╝");

        List<Feedback> feedbacks = system.getFeedbacks();
        if (feedbacks.isEmpty()) {
            System.out.println("No feedbacks found");
            return;
        }

        System.out.println();
        System.out.println("┌─────────┬────────┬────────────┬─────────────────┬──────────┐");
        System.out.println("│ Order   │ Rating │ CustomerID │ Customer Name   │ Status   │");
        System.out.println("├─────────┼────────┼────────────┼─────────────────┼──────────┤");
        for (Feedback f : feedbacks) {
            Order o = system.findOrder(f.getOrderNo());
            String custId = o != null ? o.getCustomerId() : "N/A";
            Customer c = custId.equals("N/A") ? null : system.findCustomer(custId);
            String custName = (c != null) ? c.getName() : "N/A";
            String status = (o != null) ? o.getStatus() : "N/A";

            System.out.printf("│ %-7s │ %-6d │ %-10s │ %-15s │ %-8s │%n",
                    f.getOrderNo(), f.getRating(), custId, custName, status);

            String comment = f.getComment() == null || f.getComment().isBlank() ? "(no comment)" : f.getComment();
            System.out.println("├─────────┴────────┴────────────┴─────────────────┴──────────┤");
            System.out.printf("│ Comment: %s%n", comment);
            System.out.println("└──────────────────────────────────────────────────────────┘");
        }
    }

    private static void viewAllCustomers() {
        System.out.println("\n" +
                           "╔═══════════════════════════════════╗");
        System.out.println("║     ALL REGISTERED CUSTOMERS      ║");
        System.out.println("╚═══════════════════════════════════╝");
        system.displayAllCustomers();
    }

    private static void viewAllDrivers() {
        System.out.println("\n" +
                           "╔═══════════════════════════════════╗");
        System.out.println("║     ALL REGISTERED DRIVERS        ║");
        System.out.println("╚═══════════════════════════════════╝");
        system.displayAllDrivers();
    }

    private static int getIntInput() {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.print(" Invalid input. Enter a number: ");
            }
        }
    }

}