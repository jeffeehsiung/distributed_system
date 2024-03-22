// package hotel;

// import java.rmi.RemoteException;
// import java.util.ArrayList;
// import java.util.List;

// import java.rmi.server.UnicastRemoteObject;

// // The implementation of the booking session
// public class BookingSession extends UnicastRemoteObject implements IBookingSession {

//     private List<BookingDetail> shoppingCart = new ArrayList<>();
//     private BookingManager bookingManager;

//     public BookingSession(BookingManager manager) throws RemoteException {
//         super();
//         this.bookingManager = manager;
//     }

//     @Override
//     public void addBookingDetail(BookingDetail bookingDetail) {
//         shoppingCart.add(bookingDetail);
//     }

//     @Override
//     public void bookAll() throws RemoteException {
//         try{
//             for (BookingDetail detail : shoppingCart) {
//                 bookingManager.addBooking(detail); // addBooking now handles synchronization and throws BookingException
//             }
//         } catch (RemoteException e) {
//             // If there is an issue with booking, we need to cancel the transaction
//             // Since we haven't modified any state yet, we just rethrow the exception
//             throw e;
//         }
    
//     }

// }

package hotel;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import java.rmi.server.UnicastRemoteObject;

// The implementation of the booking session
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
        // This operation must be atomic
        synchronized (bookingManager) {
            try {
                // First, check all rooms are available before booking any
                for (BookingDetail detail : shoppingCart) {
                    if (!bookingManager.isRoomAvailable(detail.getRoomNumber(), detail.getDate())) {
                        throw new RemoteException("Transaction failed: Room is not available.");
                    }
                }
                // If all rooms are available, proceed to book them
                for (BookingDetail detail : shoppingCart) {
                    bookingManager.addBooking(detail);
                }
            } catch (RemoteException e) {
                // If there is an issue with booking, we need to cancel the transaction
                // Since we haven't modified any state yet, we just rethrow the exception
                throw e;
            }
        }
    }

}