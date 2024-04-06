package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import hotel.BookingManager;
import hotel.BookingManagerInterface;

public class BookingServer {
    //     Create a server application that registers the remote object in the RMI registry for clients to locate.
    public static void main(String[] args) {
        try {
            BookingManager manager = new BookingManager();

            // define the port number
            int rmiPort = 1099;
            // export the booking manager to the RMI registry and specify the port
            BookingManagerInterface stub = (BookingManagerInterface) UnicastRemoteObject.exportObject(manager, rmiPort);
            
            Registry registry = LocateRegistry.createRegistry(rmiPort); // Default RMI port
            registry.bind("BookingManager", stub);
            System.out.println("Booking Manager is ready.");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}