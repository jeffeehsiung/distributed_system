package hotel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class BookingSession extends UnicastRemoteObject implements IBookingSession {

    private List<BookingDetail> shoppingCart = new ArrayList<>();
    private BookingManager bookingManager;

    public BookingSession(BookingManager manager) throws RemoteException {
        super();
        this.bookingManager = manager;
    }

    @Override
    public void addBookingDetail(BookingDetail bookingDetail) throws RemoteException {
        shoppingCart.add(bookingDetail);

    }

    @Override
    public void bookAll() throws RemoteException {
        List<Integer> unavailableRooms = new ArrayList<>();

        // Check availability for each booking detail
        for (BookingDetail detail : shoppingCart) {
            if (!bookingManager.isRoomAvailable(detail.getRoomNumber(), detail.getDate())) {
                unavailableRooms.add(detail.getRoomNumber());
            } else {
                try {
                    bookingManager.addBooking(detail);
                } catch (RemoteException e) {
                    // Handle RemoteException for individual bookings
                    System.err.println("Failed to book room " + detail.getRoomNumber() + ": " + e.getMessage());
                }
            }
        }

        // If any rooms are unavailable, throw RemoteException with a clear error message
        if (!unavailableRooms.isEmpty()) {
            int firstUnavailableRoom = unavailableRooms.get(0);
            LocalDate unavailableDate = shoppingCart.stream()
                                                    .filter(detail -> detail.getRoomNumber() == firstUnavailableRoom)
                                                    .findFirst()
                                                    .map(BookingDetail::getDate)
                                                    .orElse(null);
            if (unavailableDate != null) {
                throw new RemoteException("Transaction failed: Room " + firstUnavailableRoom + " is not available on " + unavailableDate);
            }
        }
    }


}
