import java.io.*;
import java.util.*;

class Room {
    int roomNumber;
    String category; // Standard, Deluxe, Suite
    boolean isBooked;

    Room(int roomNumber, String category) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.isBooked = false;
    }

    public String toString() {
        return "Room " + roomNumber + " [" + category + "] - " + (isBooked ? "Booked" : "Available");
    }
}

class Reservation {
    String customerName;
    int roomNumber;
    String category;
    String paymentStatus;

    Reservation(String customerName, int roomNumber, String category, String paymentStatus) {
        this.customerName = customerName;
        this.roomNumber = roomNumber;
        this.category = category;
        this.paymentStatus = paymentStatus;
    }

    public String toString() {
        return "Customer: " + customerName + " | Room: " + roomNumber + " | Category: " + category + " | Payment: " + paymentStatus;
    }
}

public class HotelReservationSystem {
    static List<Room> rooms = new ArrayList<>();
    static List<Reservation> reservations = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        initializeRooms();
        loadReservationsFromFile();

        int choice;
        do {
            System.out.println("\n===== Hotel Reservation System =====");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Book a Room");
            System.out.println("3. Cancel a Booking");
            System.out.println("4. View All Reservations");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1: viewAvailableRooms(); break;
                case 2: bookRoom(); break;
                case 3: cancelBooking(); break;
                case 4: viewReservations(); break;
                case 5: saveReservationsToFile(); break;
                default: System.out.println("Invalid choice.");
            }
        } while (choice != 5);
    }

    static void initializeRooms() {
        for (int i = 1; i <= 5; i++) rooms.add(new Room(i, "Standard"));
        for (int i = 6; i <= 10; i++) rooms.add(new Room(i, "Deluxe"));
        for (int i = 11; i <= 15; i++) rooms.add(new Room(i, "Suite"));
    }

    static void viewAvailableRooms() {
        System.out.println("\n--- Available Rooms ---");
        for (Room room : rooms) {
            if (!room.isBooked) System.out.println(room);
        }
    }

    static void bookRoom() {
        System.out.print("\nEnter your name: ");
        String name = sc.nextLine();
        System.out.print("Choose category (Standard/Deluxe/Suite): ");
        String category = sc.nextLine();

        Room availableRoom = null;
        for (Room room : rooms) {
            if (room.category.equalsIgnoreCase(category) && !room.isBooked) {
                availableRoom = room;
                break;
            }
        }

        if (availableRoom == null) {
            System.out.println("No available rooms in selected category.");
            return;
        }

        availableRoom.isBooked = true;
        System.out.println("Room " + availableRoom.roomNumber + " booked successfully!");
        System.out.print("Simulate payment (type 'paid' to proceed): ");
        String payment = sc.nextLine();

        Reservation reservation = new Reservation(name, availableRoom.roomNumber, category, payment.equalsIgnoreCase("paid") ? "Paid" : "Pending");
        reservations.add(reservation);
    }

    static void cancelBooking() {
        System.out.print("Enter your name to cancel booking: ");
        String name = sc.nextLine();
        Reservation found = null;

        for (Reservation res : reservations) {
            if (res.customerName.equalsIgnoreCase(name)) {
                found = res;
                break;
            }
        }

        if (found != null) {
            reservations.remove(found);
            for (Room room : rooms) {
                if (room.roomNumber == found.roomNumber) {
                    room.isBooked = false;
                    break;
                }
            }
            System.out.println("Booking canceled successfully.");
        } else {
            System.out.println("No booking found with that name.");
        }
    }

    static void viewReservations() {
        System.out.println("\n--- All Reservations ---");
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
        } else {
            for (Reservation res : reservations) {
                System.out.println(res);
            }
        }
    }

    static void saveReservationsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("reservations.dat"))) {
            oos.writeObject(reservations);
            System.out.println("Reservations saved to file. Exiting...");
        } catch (IOException e) {
            System.out.println("Error saving reservations.");
        }
    }

    @SuppressWarnings("unchecked")
    static void loadReservationsFromFile() {
        File file = new File("reservations.dat");
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            reservations = (List<Reservation>) ois.readObject();
            for (Reservation res : reservations) {
                for (Room room : rooms) {
                    if (room.roomNumber == res.roomNumber) {
                        room.isBooked = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading reservations.");
        }
    }
}
