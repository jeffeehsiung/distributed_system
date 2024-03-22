package hotel;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BookingServer {
    //     Create a server application that registers the remote object in the RMI registry for clients to locate.
    public static void main(String[] args) {
        try {
            BookingManager manager = new BookingManager();
            Registry registry = LocateRegistry.createRegistry(1099); // Default RMI port
            registry.bind("BookingManager", manager);
            System.out.println("Booking Manager is ready.");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
