package hotel;

import java.rmi.Remote;
import java.rmi.RemoteException;

// The remote interface for the booking session
public interface IBookingSession extends Remote {
    void addBookingDetail(BookingDetail bookingDetail) throws RemoteException;
    void bookAll() throws RemoteException;
}