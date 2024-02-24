package hotel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
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
        // boolean list for rooms booking status
        List<Boolean> bookingStatus = new ArrayList<>();
            for (BookingDetail detail : shoppingCart) {
                if (!bookingManager.isRoomAvailable(detail.getRoomNumber(), detail.getDate())) {
                    bookingStatus.add(false);
                }else{
                    bookingManager.addBooking(detail);
                    bookingStatus.add(true);
                }
            }
            // throws the exception of the rooms and print the number of the rooms that are not available
            if (bookingStatus.contains(false)) {
                throw new RemoteException("Transaction successful: Room " + shoppingCart.get(bookingStatus.indexOf(true)).getRoomNumber() + " is booked sucessfully on " + shoppingCart.get(bookingStatus.indexOf(false)).getDate() + "\n" + "Transaction failed: Room " + shoppingCart.get(bookingStatus.indexOf(false)).getRoomNumber() + " is not available on " + shoppingCart.get(bookingStatus.indexOf(false)).getDate());
            }

    }

}
