package staff;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.Set;

import hotel.BookingDetail;
import hotel.BookingManagerInterface;
import hotel.IBookingSession;

public class BookingClient extends AbstractScriptedSimpleTest {

	private BookingManagerInterface bm = null;
	private final static LocalDate today = LocalDate.now();

	public static void main(String[] args) {
        try {
			String host = (args.length < 1) ? "dsjjgi.eastus.cloudapp.azure.com" : args[0];
			int port = (args.length < 2) ? 8082: Integer.parseInt(args[1]);
            // try connect to the server
			Registry registry = LocateRegistry.getRegistry(host, port);
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
	public BookingClient(String[] args) {
		try {
			int port = 8082; // 默认的 RMI 注册表端口号
			String host = (args.length<1)?null:args[0];
			//Look up the registered remote instance
			if (args.length >= 2) {
				port = Integer.parseInt(args[1]); // 如果命令行参数中提供了第二个参数，则使用它作为端口号
			}
			// 获取 RMI 注册表，并指定主机名和端口号
			Registry registry = LocateRegistry.getRegistry(host, port);
			bm = (BookingManagerInterface) registry.lookup("BookingManager");
		} catch (Exception exp) {
			System.err.println("Client Exception: "+ exp.toString());
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
	public Set<Integer> getAllRooms() throws RemoteException {
		return bm.getAllRooms();
	}
}
