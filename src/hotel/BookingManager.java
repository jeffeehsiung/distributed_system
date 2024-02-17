package hotel;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BookingManager {

	private Room[] rooms;

	public BookingManager() {
		this.rooms = initializeRooms();
	}

	public Set<Integer> getAllRooms() {
		// Set<Integer> allRooms = new HashSet<Integer>();
		// Iterable<Room> roomIterator = Arrays.asList(rooms);
		// for (Room room : roomIterator) {
		// 	allRooms.add(room.getRoomNumber());
		// }
		// return allRooms;
		return Arrays.stream(rooms).map(Room::getRoomNumber).collect(Collectors.toSet());
	}

	public boolean isRoomAvailable(Integer roomNumber, LocalDate date) {
		for (Room room : rooms) {
			if (room.getRoomNumber().equals(roomNumber)) {
				return room.getBookings().stream().noneMatch(booking -> booking.getDate().equals(date));
			}
		}
		return false; // room not found
	}

	public void addBooking(BookingDetail bookingDetail) {
		for (Room room : rooms) {
			if (room.getRoomNumber().equals(bookingDetail.getRoomNumber())) {
                if (isRoomAvailable(bookingDetail.getRoomNumber(), bookingDetail.getDate())) {
                    room.getBookings().add(bookingDetail);
                    return;
                } else {
                    throw new Exception("Room is not available on this date");
                }
			}
		}
		throw new Exception("Room number not found");
	}

	public Set<Integer> getAvailableRooms(LocalDate date) {
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
		Room[] rooms = new Room[4];
		rooms[0] = new Room(101);
		rooms[1] = new Room(102);
		rooms[2] = new Room(201);
		rooms[3] = new Room(203);
		return rooms;
	}
}
