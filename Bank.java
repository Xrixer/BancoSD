// package Server;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Bank {
	
	
	static String ultimaCuenta = "";
	static int iUltimaCuenta = 0;
	
	
	public static void main(String [] args) throws IOException {
	
		// apertura de archivo cuentas.txt, para revision de integridad y existencia

		try {
			FileReader fr = new FileReader("Data/Usuarios.txt");
			BufferedReader br = new BufferedReader(fr);
			String linea = br.readLine();
			if(linea == null || linea.length() != 8){ //archivo corrupto
				br.close();
				File archivo = new File("Data/Usuarios.txt");
				archivo.delete();
				CrearArchivoUsuarios();
			}
			// hace backup de archivo correcto, de existir, por si ocurre algun error, tener respaldo
			// HACER
			
		} catch (IOException e) {
			// System.out.println("Error al abrir el archivo: " + e.getMessage());
			CrearArchivoUsuarios();
		}
		// archivo de usuarios ya checkeado o creado correctamente
		// lista donde se cargaran los datos del archivo
		List<String> ListaCuentas = new ArrayList<String>();
		FileReader fr = new FileReader("Data/Usuarios.txt");
		BufferedReader br = new BufferedReader(fr);
		// primera linea tiene el ultimo numero de cuenta usado
		String linea = br.readLine();
		ultimaCuenta = linea;
		iUltimaCuenta = Integer.parseInt(linea);
		try {
			System.out.println("El Servidor esta Iniciando....");
			System.setProperty("java.rmi.server.hostname","127.0.0.1"); 
			// esta linea inicia el server
			LocateRegistry.createRegistry(9100);

			// Get the RMI registry.
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", 9100);

			

			// Creamos objetos de todos los usuarios desde el archivo C
			
			UsuarioImpl us;
			String[] userData;
			// publicacion en el RMI de la cuenta mas grande usada en texto y en entero
			us = new UsuarioImpl("", "", "00000000", "", ultimaCuenta,iUltimaCuenta, "",0, "",0);
			Usuario stub = (Usuario) UnicastRemoteObject.exportObject(us, 0);
			// se registra bajo el docum "00000000", que es un numero de cedula que nadie puede tener
			registry.rebind("00000000", stub);
			// se registran los demas usuarios del archivo
			while ((linea = br.readLine()) != null) {
				// lineas++;
				ListaCuentas.add(linea);
				System.out.println(linea);
				userData = linea.split("\\|");
				us = new UsuarioImpl(userData[0], userData[1], userData[2], userData[3], userData[4], Double.parseDouble(userData[5]), 
											userData[6], Double.parseDouble(userData[7]), userData[8], Double.parseDouble(userData[9]));
				stub = (Usuario) UnicastRemoteObject.exportObject(us, 0);
				registry.rebind(userData[2], stub);
			}
			System.out.println("Exportación de Usuarios del Banco completa...");

			// crea objeto para incluir en el registro los metodos
			// Instanciamos la clase implementada
			CrearRMIImplem objRemoto = new CrearRMIImplem();
			
			// Exportamos el objeto de la clase implementada
            // Con port=0 se usará el puerto por defecto de RMI, el 1099
			CrearRMI stubTransacUsuario = (CrearRMI) UnicastRemoteObject.exportObject(objRemoto, 0);
			
			// Vinculamos el objeto remoto (stub) en el registro (rmiregistry)
			// Enlazamos el stub y nombramos el objeto remoto RMI como "CrearUsuario"
			registry.rebind("MetodosServidor", stubTransacUsuario);

			System.out.println("Exportación de Metodos completa...");
			System.out.println("El Servidor esta escuchando.......");

		} catch (Exception e) {
			System.out.println("Error del Servidor" + e);
			System.exit(1);
		}
		//br.close();
		//System.exit(0);
	}

	private static void CrearArchivoUsuarios() throws IOException {
		File archivo = new File("Data/Usuarios.txt");
		FileWriter fw = new FileWriter(archivo);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("00000000");
		bw.close();
	}
	
}