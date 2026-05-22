import java.util.*;
import java.io.*;

public class HotelReservationSystem {
    private static ArrayList<Room> rooms = new ArrayList<>();
    private static ArrayList<Booking> bookings = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeRooms();
        loadBookings();  // Load previous bookings

        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("     HOTEL RESERVATION SYSTEM");
            System.out.println("=".repeat(50));
            System.out.println("1. View Available Rooms");
            System.out.println("2. Book a Room");
            System.out.println("3. View All Bookings");
            System.out.println("4. Cancel Booking");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewAvailableRooms();
                case 2 -> bookRoom();
                case 3 -> viewBookings();
                case 4 -> cancelBooking();
                case 5 -> {
                    saveBookings();
                    System.out.println("Thank you for using Hotel Reservation System!");
                    return;
                }
                default -> System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void initializeRooms() {
        rooms.add(new Room(101, "Standard", 2500));
        rooms.add(new Room(102, "Standard", 2500));
        rooms.add(new Room(201, "Deluxe", 4500));
        rooms.add(new Room(202, "Deluxe", 4500));
        rooms.add(new Room(301, "Suite", 7500));
        rooms.add(new Room(302, "Suite", 7500));
    }

    private static void viewAvailableRooms() {
        System.out.println("\n--- Available Rooms ---");
        boolean found = false;
        for (Room room : rooms) {
            if (!room.isBooked()) {
                System.out.printf("Room %d | Type: %s | Price: ₹%.2f%n", 
                    room.roomNumber, room.type, room.price);
                found = true;
            }
        }
        if (!found) System.out.println("No rooms available at the moment.");
    }

    private static void bookRoom() {
        viewAvailableRooms();
        System.out.print("\nEnter Room Number to book: ");
        int roomNum = scanner.nextInt();
        scanner.nextLine();

        Room selectedRoom = null;
        for (Room r : rooms) {
            if (r.roomNumber == roomNum && !r.isBooked()) {
                selectedRoom = r;
                break;
            }
        }

        if (selectedRoom == null) {
            System.out.println("Invalid or already booked room!");
            return;
        }

        System.out.print("Enter Customer Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Number of Days: ");
        int days = scanner.nextInt();

        Booking booking = new Booking(name, selectedRoom, days);
        bookings.add(booking);
        selectedRoom.book();

        System.out.println("\n✅ Booking Successful!");
        booking.displayBooking();
    }

    private static void viewBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings yet.");
            return;
        }
        System.out.println("\n--- All Bookings ---");
        for (Booking b : bookings) {
            b.displayBooking();
        }
    }

    private static void cancelBooking() {
        viewBookings();
        System.out.print("\nEnter Booking ID to cancel: ");
        int id = scanner.nextInt();

        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).bookingId == id) {
                bookings.get(i).room.cancel();
                bookings.remove(i);
                System.out.println("Booking cancelled successfully!");
                return;
            }
        }
        System.out.println("Booking ID not found!");
    }

    // File Handling
    private static void saveBookings() {
        try (PrintWriter writer = new PrintWriter("bookings.txt")) {
            for (Booking b : bookings) {
                writer.println(b.bookingId + "|" + b.customerName + "|" + 
                             b.room.roomNumber + "|" + b.days);
            }
        } catch (IOException e) {
            System.out.println("Error saving bookings.");
        }
    }

    private static void loadBookings() {
        try (Scanner fileScanner = new Scanner(new File("bookings.txt"))) {
            while (fileScanner.hasNextLine()) {
                String[] data = fileScanner.nextLine().split("\\|");
                int roomNum = Integer.parseInt(data[2]);
                for (Room r : rooms) {
                    if (r.roomNumber == roomNum) {
                        r.book();
                        bookings.add(new Booking(Integer.parseInt(data[0]), data[1], r, Integer.parseInt(data[3])));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            // File doesn't exist yet - first time run
        }
    }
}

// Room Class
class Room {
    int roomNumber;
    String type;
    double price;
    boolean booked = false;

    public Room(int roomNumber, String type, double price) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
    }

    public void book() { this.booked = true; }
    public void cancel() { this.booked = false; }
    public boolean isBooked() { return booked; }
}

// Booking Class
class Booking {
    static int nextId = 1;
    int bookingId;
    String customerName;
    Room room;
    int days;
    double totalAmount;

    public Booking(String customerName, Room room, int days) {
        this.bookingId = nextId++;
        this.customerName = customerName;
        this.room = room;
        this.days = days;
        this.totalAmount = room.price * days;
    }

    public Booking(int id, String name, Room room, int days) {
        this.bookingId = id;
        this.customerName = name;
        this.room = room;
        this.days = days;
        this.totalAmount = room.price * days;
        if (id >= nextId) nextId = id + 1;
    }

    public void displayBooking() {
        System.out.printf("ID: %d | Name: %s | Room: %d (%s) | Days: %d | Total: ₹%.2f%n",
            bookingId, customerName, room.roomNumber, room.type, days, totalAmount);
    }
}