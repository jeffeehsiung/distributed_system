package staff;

import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hotel.BookingDetail;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import hotel.BookingManagerInterface;
import hotel.IBookingSession;

public class BookingClient extends AbstractScriptedSimpleTest {

	// locate the remote BookingManager object in the RMI registry and invoke its methods
	private BookingManagerInterface bm = null;

	// public static void main(String[] args) throws Exception {
	// 	BookingClient client = new BookingClient();
	// 	client.run();
	// }

	 public static void main(String[] args) {
		Runnable task = () -> {
			try {
				BookingClient client = new BookingClient();
				client.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		Thread client1 = new Thread(task);
		Thread client2 = new Thread(task);

		// Start both threads, simulating concurrent clients
		client1.start();
		client2.start();

		// Wait for both threads to finish
		try {
			client1.join();
			client2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
            BookingClient client = new BookingClient();
            client.testConcurrentBookingDifferentRooms();
            client.testConcurrentBookingSameRoom();
            // Test using IBookingSession for transactional bookings
            client.testSessionBasedBooking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

private void testConcurrentBookingDifferentRooms() throws Exception {
	System.out.println("Testing concurrent booking for different rooms");
	ExecutorService executor = Executors.newFixedThreadPool(2);
	executor.submit(() -> {
		try {
			attemptBooking(101, LocalDate.now());
		} catch (Exception e) {
			e.printStackTrace();
		}
	});
	executor.submit(() -> {
		try {
			attemptBooking(102, LocalDate.now());
		} catch (Exception e) {
			e.printStackTrace();
		}
	});
	executor.shutdown();
}

    private void testConcurrentBookingSameRoom() throws Exception {
        System.out.println("Testing concurrent booking for the same room");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        // Both threads attempt to book the same room on the same date
        executor.submit(() -> {
			try {
				attemptBooking(101, LocalDate.now());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
        executor.submit(() -> {
			try {
				attemptBooking(101, LocalDate.now());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
        executor.shutdown();
    }

    private void attemptBooking(int roomNumber, LocalDate date) throws Exception{
        try {
            if (isRoomAvailable(roomNumber, date)) {
                System.out.println(Thread.currentThread().getName() + " - Room " + roomNumber + " is available, attempting to book.");
                addBooking(new BookingDetail("Guest " + Thread.currentThread().getName(), roomNumber, date));
                System.out.println(Thread.currentThread().getName() + " - Room " + roomNumber + " booked successfully.");
            } else {
                System.out.println(Thread.currentThread().getName() + " - Room " + roomNumber + " is not available.");
            }
        } catch (Exception e) {
            System.out.println(Thread.currentThread().getName() + " - Failed to book room " + roomNumber + ": " + e.getMessage());
        }
    }

    private void testSessionBasedBooking() throws Exception {
        System.out.println("Testing session-based booking");
        IBookingSession session = bm.createBookingSession();
        LocalDate date = LocalDate.now();
        session.addBookingDetail(new BookingDetail("Alice", 103, date));
        session.addBookingDetail(new BookingDetail("Bob", 104, date));
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
			Registry registry = LocateRegistry.getRegistry("localhost", 1099);
			bm = (BookingManagerInterface) registry.lookup("BookingManager");
			if (bm == null) {
				System.err.println("Failed to lookup BookingManager.");
				System.exit(1); // Exit or handle the error appropriately
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			System.exit(1); // Exit or handle the error appropriately
		}
	}

	@Override
	public boolean isRoomAvailable(Integer roomNumber, LocalDate date) throws Exception{
		return bm.isRoomAvailable(roomNumber, date);
	}

	@Override
	public void addBooking(BookingDetail bookingDetail) throws Exception {
		//Implement this method
		bm.addBooking(bookingDetail);
	}

	@Override
	public Set<Integer> getAvailableRooms(LocalDate date) throws Exception{
		//Implement this method
		return bm.getAvailableRooms(date);
	}

	@Override
	public Set<Integer> getAllRooms() throws Exception{
		return bm.getAllRooms();
	}

	

}
