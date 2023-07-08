// implementacion de interfaz remota

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CrearRMIImplem implements CrearRMI {
   // implementa el metodos en el servior para ser atendidos para el cliente
   
   static String sEncabezado="";  

   public boolean CrearOActualizarUsuario(String name, String userName, String  docum, String password, String cuenta1,
                   double sCuenta1, String cuenta2, double sCuenta2, String cuenta3, double sCuenta3) throws RemoteException {
      // Implementación del método remoto en el servidor
      Usuario user = new UsuarioImpl(name, userName, docum, password, cuenta1, sCuenta1, cuenta2, sCuenta2, cuenta3, sCuenta3);
      Usuario stub = (Usuario) UnicastRemoteObject.exportObject(user, 0);
      Registry registry = LocateRegistry.getRegistry("127.0.0.1", 9100);
      registry.rebind(docum, stub);

      // grabar archivo actualizado
      if(!RespaldarArchivo())
         System.out.println("Se continuara trabajando con datos en memoria.");
      
      return true;
   }
      
   public boolean ActualizarUltimoNumCuenta(String cuenta) throws NumberFormatException, RemoteException {
      
      sEncabezado = cuenta;
      Usuario user = new UsuarioImpl("", "", "00000000", "", cuenta, Double.parseDouble(cuenta), "", 0.0, "", 0.0);
      Usuario stub = (Usuario) UnicastRemoteObject.exportObject(user, 0);
      Registry registry = LocateRegistry.getRegistry("127.0.0.1", 9100);
      registry.rebind("00000000", stub);
      /*
      File archivo = new File("Data/Usuarios.txt");
      try {
         FileWriter fw = new FileWriter(archivo);
         BufferedWriter bw = new BufferedWriter(fw);
            bw.write(cuenta);
            bw.close();
         } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      */
      return true;
   }
   
   public boolean RespaldarMovimiento(String docum, String cuenta, double monto) throws RemoteException {
      // respalda movimiento en el archivo "cuenta".txt
        
      try {
         File archivo = new File("Data/"+cuenta+".txt");
         FileWriter fw = new FileWriter(archivo, true);
         BufferedWriter bw = new BufferedWriter(fw);
         String linea= (monto>0)? "D|":"R|"; // Deposito o Retiro
         
         LocalDateTime fechaHora = LocalDateTime.now();
         DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
         String fechaHoraFormateada = fechaHora.format(formato);
         
         linea  += Double.toString(monto)+"|"+fechaHoraFormateada;
         bw.write(linea);
         bw.newLine(); // Agrega un salto de línea al final de la línea
         bw.flush();
         bw.close();

         return true;
      } catch (IOException e) {
         System.out.println("Error escribiendo archivo de Movimiento de Cuenta");
         System.exit(1);
         // e.printStackTrace();
      }
      return true;
   }
   
   public List<String> BuscarUltimos5Movs(String cuenta) throws RemoteException {
      // respalda movimiento en el archivo "cuenta".txt
        
      
      List<String> movimientos = new ArrayList<>();
      List<String> ultimos5Movs = new ArrayList<>();
      String[] moviComplet;
      try {
			FileReader fr = new FileReader("Data/"+cuenta+".txt");
			BufferedReader br = new BufferedReader(fr);
			String linea;
			while ((linea = br.readLine()) != null) {
				// lineas++;
            String lineaFormat="";
            moviComplet = linea.split("\\|");
            lineaFormat = moviComplet[2] + ((moviComplet[0].compareTo("D")==0)? " Deposito por : ":" Retiro   por : ");
            lineaFormat +=  moviComplet[1];
            movimientos.add(lineaFormat);
			}
         br.close();
         int numMov= movimientos.size();
         int ultMov = (numMov>5)? (numMov-5):0;
         for(int i = numMov; i > ultMov; i--){
            ultimos5Movs.add(movimientos.get(i-1));
         }
		} catch (IOException e) {
			System.out.println("Error al abrir el archivo de Movimiento de Cuentas");
         System.exit(1);
		}
      return ultimos5Movs;
   }
   
   public boolean RespaldarArchivo(){
      // grabar archivo actualizado
      Registry registry;
      Usuario user;
      try {
         registry = LocateRegistry.getRegistry("127.0.0.1", 9100);
         user = new UsuarioImpl("", "", "", "", "", 0.0, "", 0.0, "", 0.0);
      } catch (RemoteException e) {
         System.out.println("No se pudo respaldar el archivo.");
         return false;
         // e.printStackTrace();
      }
     
      
		try {
         File archivo = new File("Data/Usuarios.txt");
         FileWriter fw = new FileWriter(archivo);
         BufferedWriter bw = new BufferedWriter(fw);
         bw.write(sEncabezado);
         bw.newLine(); // Agrega un salto de línea al final de la línea
         bw.flush();
         String[] nombresObjetos = registry.list();
         // Imprimir los nombres de los objetos registrados
         for (String nombre : nombresObjetos) {
            if(nombre.compareTo("00000000")!=0 && nombre.length()==8) {
               try{
                     user = (Usuario) registry.lookup(nombre);
                     String linea = user.getName()+"|"+user.getUserName()+"|"+user.getDocum()+"|"+ user.getPassword()+"|"+user.getCuenta1()+"|"+Double.toString(user.getSCuenta1())+"|"+user.getCuenta2()+"|"+Double.toString(user.getSCuenta2())+"|"+user.getCuenta3()+"|"+Double.toString(user.getSCuenta3());
                     bw.write(linea);
                     bw.newLine(); // Agrega un salto de línea al final de la línea
                     bw.flush();
               } catch (NotBoundException e) {
                  System.out.println("Error accesando servidor");
                  System.exit(1);
                  e.printStackTrace();
               }
               //   System.out.println(nombre);
            }
         }
         bw.close();
         return true;
      } catch (IOException e) {
         System.out.println("Error escribiendo archivo: Usuarios.txt");
         System.exit(1);
         // e.printStackTrace();
      }

      return false;
   }

}  