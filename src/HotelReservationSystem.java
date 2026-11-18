import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {
    private static final String url ="jdbc:mysql://localhost:3306/hotel_db";
    private static final String username="root";
    private static final String  password ="Badal@94";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Connection con = DriverManager.getConnection(url, username, password);
            while (true) {
                System.out.println();
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a room ");
                System.out.println("2. View Reservations ");
                System.out.println("3. Get Room Number ");
                System.out.println("4. Update Reservations ");
                System.out.println("5. Delete Reservations ");
                System.out.println("0. Exit ");
                System.out.println("Enter the choice:");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(con, sc);
                        break;
                    case 2:
                        viewReservations(con);
                        break;
                    case 3:
                        getRoomNumber(con, sc);
                        break;
                    case 4:
                        updateReservations(con, sc);
                        break;
                    case 5:
                        deleteReservation(con, sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid Choice . Try again!!");


                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

        private static void reserveRoom(Connection con , Scanner sc)
        {
            System.out.println("Enter the guest name: ");
            String name = sc.next();
            sc.nextLine();
            System.out.println("Enter  room number: ");
            int roomNumber = sc.nextInt();
            System.out.println("Enter the contact number: ");
            String contactNo= sc.next();
            String sql = "INSERT INTO reservations (guest_name, room_number, contact_no) " +
                    "VALUES ('" + name + "', " + roomNumber + ", '" + contactNo + "')";
             try(Statement stmt= con.createStatement()){
                   int afftectedRows=stmt.executeUpdate(sql);

                   if(afftectedRows>0)
                   {
                       System.out.println("Reservation Successful !!");
                   }
                   else {
                       System.out.println("Reservation Failed !!");
                   }
             }
             catch (SQLException e)
             {
                 e.printStackTrace();
             }

        }

        private static void viewReservations(Connection con) throws  SQLException
        { String sql ="Select reservation_id,guest_name,room_number,contact_no,reservation_date from reservations;";
          try(Statement stmt= con.createStatement();
              ResultSet rs= stmt.executeQuery(sql))
          {
              System.out.println("Current Reservations: ");
              System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
              System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
              System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

              while(rs.next())
              {
                  int reservationId = rs.getInt("reservation_Id");
                  String guestName = rs.getString("guest_Name");
                  int roomNumber = rs.getInt("room_number");
                  String contactNo= rs.getString("contact_no");
                  String reservationDate = rs.getTimestamp("reservation_date").toString();

                  System.out.printf("| %-16d | %-16s | %-16d| %-16s | %-16s|\n", reservationId,guestName,roomNumber,contactNo,reservationDate);

              }
              System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
          }

        }
        private static void getRoomNumber(Connection con,Scanner sc) {
            try {
                System.out.print("Enter reservation ID: ");
                int reservationId = sc.nextInt();
                System.out.print("Enter guest name: ");
                String guestName = sc.next();
                String sql = "SELECT room_number FROM reservations " +
                        "WHERE reservation_id = " + reservationId +
                        " AND guest_name = '" + guestName + "'";
                try (Statement statement = con.createStatement();
                     ResultSet resultSet = statement.executeQuery(sql)) {

                    if (resultSet.next()) {
                        int roomNumber = resultSet.getInt("room_number");
                        System.out.println("Room number for Reservation ID " + reservationId +
                                " and Guest " + guestName + " is: " + roomNumber);
                    } else {
                        System.out.println("Reservation not found for the given ID and guest name.");
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private  static  void updateReservations(Connection con,Scanner sc)
        {
            try {
                System.out.print("Enter reservation ID to update: ");
                int reservationId = sc.nextInt();
                sc.nextLine(); // Consume the newline character

                if (!reservationExists(con, reservationId)) {
                    System.out.println("Reservation not found for the given ID.");
                    return;
                }

                System.out.print("Enter new guest name: ");
                String newGuestName = sc.nextLine();
                System.out.print("Enter new room number: ");
                int newRoomNumber = sc.nextInt();
                System.out.print("Enter new contact number: ");
                String newContactNumber = sc.next();

                String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                        "room_number = " + newRoomNumber + ", " +
                        "contact_no = '" + newContactNumber + "' " +
                        "WHERE reservation_id = " + reservationId;

                try (Statement statement = con.createStatement()) {
                    int affectedRows = statement.executeUpdate(sql);

                    if (affectedRows > 0) {
                        System.out.println("Reservation updated successfully!");
                    } else {
                        System.out.println("Reservation update failed.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    private static void deleteReservation(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = scanner.nextInt();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }


    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }


        }


