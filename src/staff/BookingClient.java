package staff;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.Set;

import hotel.BookingDetail;
import hotel.BookingManager;
import hotel.BookingManagerInterface;
import hotel.IBookingSession;

public class BookingClient extends AbstractScriptedSimpleTest {

	private BookingManager bm = null;
	private final static LocalDate today = LocalDate.now();

	public static void main(String[] args) {
        try {
			
            // try connect to the server
			Registry registry = LocateRegistry.getRegistry("localhost", 1099);
			BookingManagerInterface bm = (BookingManagerInterface) registry.lookup("BookingManager");

            // Simulate multiple clients by creating multiple threads
            Thread client1 = new Thread(() -> attemptBooking(bm, 101, LocalDate.now()));
            Thread client2 = new Thread(() -> attemptBooking(bm, 102, today));

            // Thread trying to book the same room as client1
            Thread client3 = new Thread(() -> attemptBooking(bm, 102, today));
			Thread client4 = new Thread(() -> attemptBooking(bm, 101, today));

			Thread client5 = new Thread(() -> attemptBooking(bm, 201, today));

			// Test session-based booking
			Thread client6 = new Thread(() -> {
				try {
					testSessionBasedBooking(bm);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});


            // Start the threads
            client1.start();
            client2.start();
            client3.start();
			client4.start();
			client5.start();
			client6.start();

            client1.join();
            client2.join();
            client3.join();
			client4.join();
			client5.join();
			client6.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void attemptBooking(BookingManagerInterface bm, int roomNumber, LocalDate date) {
        try {
            if (bm.isRoomAvailable(roomNumber, date)) {
                bm.addBooking(new BookingDetail("Guest " + roomNumber, roomNumber, date));
                System.out.println("Booking successful for room " + roomNumber + " for " + date);
            } else {
                System.out.println("Room " + roomNumber + " is not available.");
            }
        } catch ( RemoteException e) {
            System.out.println("Failed to book room " + roomNumber + ": " + e.getMessage());
        }
    }
    
	private static void testSessionBasedBooking(BookingManagerInterface bm) throws Exception {
	    System.out.println("Testing session-based booking");
        IBookingSession session = bm.createBookingSession();
        LocalDate date = LocalDate.now();
		date = date.plusDays(1);
        session.addBookingDetail(new BookingDetail("Alice", 102, date));
        session.addBookingDetail(new BookingDetail("Bob", 203, today));
		session.addBookingDetail(new BookingDetail("Andy", 201, today));
        try {
            session.bookAll();
            System.out.println("Session-based booking completed successfully.");
        } catch (RemoteException e) {
            System.out.println("Failed to complete session-based booking: " + e.getMessage());
        }
    }
	/***************
	 * CONSTRUCTOR *
	 ***************/
	public BookingClient() {
		try {
            // Assuming BookingManager is already acting as a local instance for simplicity
            // In a real RMI setup, you would look up the remote instance here
			bm = new BookingManager();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	@Override
	public boolean isRoomAvailable(Integer roomNumber, LocalDate date) throws RemoteException{
		//Implement this method
		return bm.isRoomAvailable(roomNumber, date);
	}

	@Override
	public void addBooking(BookingDetail bookingDetail) throws RemoteException {
		//Implement this method
		bm.addBooking(bookingDetail);
	}

	@Override
	public Set<Integer> getAvailableRooms(LocalDate date) throws RemoteException{
		//Implement this method
		return bm.getAvailableRooms(date);
	}

	@Override
	public Set<Integer> getAllRooms() {
		return bm.getAllRooms();
	}
}
