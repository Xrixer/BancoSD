// package Client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Usuario extends Remote {

   public String getName() throws RemoteException;
   public String getUserName() throws RemoteException;
   public String getDocum() throws RemoteException;
   public String getPassword() throws RemoteException;
   public String getCuenta1() throws RemoteException;
   public double getSCuenta1() throws RemoteException;
   public String getCuenta2() throws RemoteException;
   public double getSCuenta2() throws RemoteException;
   public String getCuenta3() throws RemoteException;
   public double getSCuenta3() throws RemoteException;
}