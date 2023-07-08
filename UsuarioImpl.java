import java.rmi.RemoteException;

public class UsuarioImpl  implements Usuario {
    // Define attributes and implement all the methods defined in product interface.

    // Define attributes.
    public String name;
    public String userName;
    public String docum;
    public String password;
    public String cuenta1;
    public double sCuenta1;
    public String cuenta2;
    public double sCuenta2;
    public String cuenta3;
    public double sCuenta3;
    
    // Parametrized constructor para luego Crear el Usuario
    // este es el constructor
    public UsuarioImpl(String name, String userName, String  docum, String password, String cuenta1, double sCuenta1,
                         String cuenta2, double sCuenta2, String cuenta3, double sCuenta3) throws RemoteException {
        
        
        this.name  = name;
        this.userName = userName;
        this.docum = docum;
        this.password = password;
        this.cuenta1 = cuenta1;
        this.sCuenta1 = sCuenta1;
        this.cuenta2 = cuenta2;
        this.sCuenta2 = sCuenta2;
        this.cuenta3 = cuenta3;
        this.sCuenta3 = sCuenta3;
        
    }

    
    public String getName() throws RemoteException {
        return this.name;

    }
    public String getUserName() throws RemoteException {
        return this.userName;

    }
    public String getDocum() throws RemoteException {
        return this.docum;

    }
    public String getPassword() throws RemoteException {
        return this.password;

    }
    public String getCuenta1() throws RemoteException {
        return this.cuenta1;

    }
    public String getCuenta2() throws RemoteException {
        return this.cuenta2;

    }
    public String getCuenta3() throws RemoteException {
        return this.cuenta3;

    }
    public double getSCuenta1() throws RemoteException {
        return this.sCuenta1;

    }
    public double getSCuenta2() throws RemoteException {
        return this.sCuenta2;

    }
    public double getSCuenta3() throws RemoteException {
        return this.sCuenta3;

    }
    
}