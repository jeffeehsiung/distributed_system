package hotel;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// This class is the server-side implementation of the BookingManagerInterface interface.
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;


public class BookingManager extends UnicastRemoteObject implements BookingManagerInterface {

	private Room[] rooms;
	// private final Object lock = new Object();

	public BookingManager() throws RemoteException{
		super();
		this.rooms = initializeRooms();
	}

	@Override
	public Set<Integer> getAllRooms() {
		// Set<Integer> allRooms = new HashSet<Integer>();
		// Iterable<Room> roomIterator = Arrays.asList(rooms);
		// for (Room room : roomIterator) {
		// 	allRooms.add(room.getRoomNumber());
		// }
		// return allRooms;
		return Arrays.stream(rooms).map(Room::getRoomNumber).collect(Collectors.toSet());
	}

	@Override
	public synchronized boolean isRoomAvailable(Integer roomNumber, LocalDate date) throws RemoteException{
		// synchronized (lock) {
			for (Room room : rooms) {
				if (room.getRoomNumber().equals(roomNumber)) {
					return room.getBookings().stream().noneMatch(booking -> booking.getDate().equals(date));
				}
			}
			return false; // room not found
		// }
	}

	@Override
	public synchronized void addBooking(BookingDetail bookingDetail) throws RemoteException {
		// synchronized (lock) {
			for (Room room : rooms) {
				if (room.getRoomNumber().equals(bookingDetail.getRoomNumber())) {
					if (isRoomAvailable(bookingDetail.getRoomNumber(), bookingDetail.getDate())) {
						room.getBookings().add(bookingDetail);
						return;
					} else {
						throw new RemoteException("Room is not available on this date");
					}
				}
			}
			throw new RemoteException("Room number not found");
		// }
	}

	@Override
	public synchronized Set<Integer> getAvailableRooms(LocalDate date) throws RemoteException {
        Set<Integer> availableRooms = new HashSet<>();
		// synchronized (lock) {
			for (Room room : rooms) {
				boolean isAvailable = room.getBookings().stream()
										.noneMatch(b -> b.getDate().isEqual(date));
				if (isAvailable) {
					availableRooms.add(room.getRoomNumber());
				}
			}
			return availableRooms;
		// }
	}

	private static Room[] initializeRooms() {
		Room[] rooms = new Room[5];
		rooms[0] = new Room(101);
		rooms[1] = new Room(102);
		rooms[2] = new Room(201);
		rooms[3] = new Room(202);
		rooms[4] = new Room(203);
		return rooms;
	}

	@Override
	public IBookingSession createBookingSession() throws RemoteException {
		return new BookingSession(this);
	}
}
