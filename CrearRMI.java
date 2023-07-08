import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface CrearRMI extends Remote {
      // define metodos para atender el cliente
      boolean CrearOActualizarUsuario(String name, String userName, String  docum, String password, String cuenta1, double sCuenta1, 
            String cuenta2, double sCuenta2, String cuenta3, double sCuenta3) throws java.rmi.RemoteException;

      boolean ActualizarUltimoNumCuenta(String  cuenta) throws java.rmi.RemoteException;
      
      boolean RespaldarMovimiento(String docum, String cuenta, double monto) throws RemoteException;
      
      List<String> BuscarUltimos5Movs(String cuenta) throws RemoteException;

	 
}



