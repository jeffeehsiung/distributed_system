package hotel;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class BookingManager extends UnicastRemoteObject implements BookingManagerInterface {

	private Room[] rooms;
    private final ConcurrentHashMap<Integer, Lock> roomLocks = new ConcurrentHashMap<>();

    // Constructor must throw RemoteException due to the super class
	public BookingManager() throws RemoteException {
        super();
        this.rooms = initializeRooms();
        for (Room room : rooms) {
            roomLocks.put(room.getRoomNumber(), new ReentrantLock());
        }
    }

	@Override
	public Set<Integer> getAllRooms() throws RemoteException{
		// Set<Integer> allRooms = new HashSet<Integer>();
		// Iterable<Room> roomIterator = Arrays.asList(rooms);
		// for (Room room : roomIterator) {
		// 	allRooms.add(room.getRoomNumber());
		// }
		// return allRooms;
        return Arrays.stream(rooms).map(Room::getRoomNumber).collect(Collectors.toSet());
	}

    @Override
    public boolean isRoomAvailable(Integer roomNumber, LocalDate date) throws RemoteException{
        Lock lock = roomLocks.get(roomNumber);
        lock.lock();
        try {
            for (Room room : rooms) {
                if (room.getRoomNumber().equals(roomNumber)) {
                    return room.getBookings().stream().noneMatch(b -> b.getDate().isEqual(date));
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void addBooking(BookingDetail bookingDetail) throws RemoteException {
        Lock lock = roomLocks.get(bookingDetail.getRoomNumber());
        if (lock == null) {
            throw new RemoteException("Room number not found");
        }
        lock.lock();
        try {
            if (!isRoomAvailable(bookingDetail.getRoomNumber(), bookingDetail.getDate())) {
                throw new RemoteException("Room is not available on this date");
            }
            for (Room room : rooms) {
                if (room.getRoomNumber().equals(bookingDetail.getRoomNumber())) {
                    room.getBookings().add(bookingDetail);
                    return;
                }
            }
        } finally {
            lock.unlock();
        }
    }

	@Override
	public Set<Integer> getAvailableRooms(LocalDate date) throws RemoteException{
		Set<Integer> availableRooms = new HashSet<Integer>();
		for (Room room : rooms) {
			if (room.getBookings().stream().noneMatch(b -> b.getDate().isEqual(date))) {
				availableRooms.add(room.getRoomNumber());
			}
		}
		return availableRooms;
	}

	private static Room[] initializeRooms() {
		Room[] rooms = new Room[4];
		rooms[0] = new Room(101);
		rooms[1] = new Room(102);
		rooms[2] = new Room(201);
		rooms[3] = new Room(203);
		return rooms;
	}

	@Override
	public IBookingSession createBookingSession() throws RemoteException {
        // This method creates a new session and returns it to the client
        return new BookingSession(this);
    }

}
