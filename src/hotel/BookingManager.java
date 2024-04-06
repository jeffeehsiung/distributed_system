package hotel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BookingManager implements BookingManagerInterface {

    private Room[] rooms;

    public BookingManager() throws RemoteException {
        super();
        this.rooms = initializeRooms();
    }

    @Override
    public Set<Integer> getAllRooms() throws RemoteException {
        return Arrays.stream(rooms)
                     .map(Room::getRoomNumber)
                     .collect(Collectors.toSet());
    }

    @Override
    public synchronized boolean isRoomAvailable(Integer roomNumber, LocalDate date) throws RemoteException {
        // Validate roomNumber and date
        if (roomNumber == null || roomNumber <= 0) {
            throw new IllegalArgumentException("Invalid room number: " + roomNumber);
        }
        if (date == null || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid date: " + date);
        }

        for (Room room : rooms) {
            if (room.getRoomNumber().equals(roomNumber)) {
                return room.getBookings().stream()
                            .noneMatch(booking -> booking.getDate().equals(date));
            }
        }
        return false; // Room not found
    }

	@Override
	public synchronized void addBooking(BookingDetail bookingDetail) throws RemoteException {
		// Validate bookingDetail
		if (bookingDetail == null || bookingDetail.getRoomNumber() == null || bookingDetail.getDate() == null) {
			throw new IllegalArgumentException("Invalid booking detail: " + bookingDetail);
		}

		for (Room room : rooms) {
			if (room.getRoomNumber().equals(bookingDetail.getRoomNumber())) {
				if (isRoomAvailable(bookingDetail.getRoomNumber(), bookingDetail.getDate())) {
					room.getBookings().add(bookingDetail);
					return;
				} else {
					// Throw RemoteException with a cleaner error message
					throw new RemoteException("Room " + bookingDetail.getRoomNumber() + " is not available on " + bookingDetail.getDate());
				}
			}
		}
		throw new RemoteException("Room number " + bookingDetail.getRoomNumber() + " not found");
	}


    @Override
    public synchronized Set<Integer> getAvailableRooms(LocalDate date) throws RemoteException {
        // Validate date
        if (date == null || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid date: " + date);
        }

        Set<Integer> availableRooms = new HashSet<>();
        for (Room room : rooms) {
            boolean isAvailable = room.getBookings().stream()
                                    .noneMatch(b -> b.getDate().isEqual(date));
            if (isAvailable) {
                availableRooms.add(room.getRoomNumber());
            }
        }
        return availableRooms;
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
