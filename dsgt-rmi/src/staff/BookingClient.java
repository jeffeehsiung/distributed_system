package staff;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import hotel.BookingDetail;
import hotel.BookingManagerInterface;
import hotel.IBookingSession;

public class BookingClient {

    private static final String DEFAULT_HOST = "dsjjgi.eastus.cloudapp.azure.com";
    private static final int DEFAULT_PORT = 1099;

    private String host = DEFAULT_HOST;
    private int port = DEFAULT_PORT;
    private BookingManagerInterface bm = null;
    private final static LocalDate today = LocalDate.now();

    public BookingClient(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            System.out.println("[Client] Connecting to RMI registry...");
            Registry registry = LocateRegistry.getRegistry(host, port);
            System.out.println("[Client] Looking up the BookingManager remote object...");
            this.bm = (BookingManagerInterface) registry.lookup("BookingManager");
            System.out.println("[Client] Connected to the server successfully.");
        } catch (Exception e) {
            System.err.println("[Client] Failed to connect to the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

	public void startLoadTest() {
		ExecutorService executor = Executors.newFixedThreadPool(6);
		try {
			for (int i = 0; i < 3; i++) {
				executor.submit(() -> attemptBooking(101, LocalDate.now()));
				executor.submit(() -> attemptBooking(102, LocalDate.now()));
				executor.submit(() -> attemptBooking(102, today));
				executor.submit(() -> attemptBooking(101, today));
				executor.submit(() -> attemptBooking(201, LocalDate.now()));
				executor.submit(this::testSessionBasedBooking);
			}
		} finally {
			executor.shutdown();
			try {
				executor.awaitTermination(1, TimeUnit.MINUTES); // Wait for all tasks to complete
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

    private void attemptBooking(int roomNumber, LocalDate date) {
        try {
            if (bm.isRoomAvailable(roomNumber, date)) {
                bm.addBooking(new BookingDetail("[Client] Guest " + roomNumber, roomNumber, date));
                System.out.println("[Client] Booking successful for room " + roomNumber + " for " + date);
            } else {
                System.out.println("[Client] Room " + roomNumber + " is not available for " + date);
            }
        } catch (RemoteException e) {
            System.out.println("[Client] Failed to book room " + roomNumber + ": " + e.getMessage());
        }
    }
    
    private void testSessionBasedBooking() {
        try {
            System.out.println("[Client] Testing session-based booking");
            IBookingSession session = bm.createBookingSession();
            LocalDate date = LocalDate.now().plusDays(1);
            session.addBookingDetail(new BookingDetail("Alice", 102, date));
            session.addBookingDetail(new BookingDetail("Bob", 203, LocalDate.now()));
            session.addBookingDetail(new BookingDetail("Andy", 201, LocalDate.now()));
            session.bookAll();
            System.out.println("[Client] Session-based booking completed successfully.");
        } catch (Exception e) {
            System.out.println("[Client] Failed to complete session-based booking: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("[Client] Starting BookingClient...");
            String host = (args.length < 1) ? DEFAULT_HOST : args[0];
            int port = (args.length < 2) ? DEFAULT_PORT : Integer.parseInt(args[1]);
            System.out.println("[Client] Connecting to server at " + host + ":" + port);
            BookingClient client = new BookingClient(host, port);
            client.startLoadTest();
        } catch (Exception e) {
            System.err.println("[Client] Failed to start BookingClient: " + e.getMessage());
        }
    }

}
