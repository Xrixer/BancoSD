// package Client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;


public class ATM {

   static String ultimaCuenta;
   static int iUltimaCuenta;
   static Registry registry;
   static CrearRMI FuncionesRemotas;
   static Usuario user;
   static Usuario user2;
   
   public static void main(String[] args) {  // Programa principal del cajero

      
      // Inicio cliente, leer registro y cargar metodos remotos
      try {
         //Obteniendo registro de rmiregistry
         registry = LocateRegistry.getRegistry("127.0.0.1", 9100);
         //Buscando el objeto RMI remoto
         // System.out.println("Buscando objeto RMI CrearRMI y creando stub...");
         FuncionesRemotas = (CrearRMI) registry.lookup("MetodosServidor");
         // System.out.println("Objeto CrearUsuario remoto encontrado...");
  
      } catch (Exception e) {
         System.out.println("Servidor No Activo, Ejecute primero el Servidor Bank en la Carpeta Servidor.");
         System.out.println(e);
         System.exit(1);
      }

      // inicio del programa, presentacion de menu de bienvenida y menu
      cls();
      int opcion = -1;
      while (opcion !=0) {
         opcion = MenuPpal();
         switch (opcion){
            case 0:
               System.out.println("Gracias por preferir nuestro Banco.");
               System.out.println("Hasta Luego.");
               // System.exit(0);
               break;
            case 1:
               try {
                  CrearCuenta();
               } catch (RemoteException e) {
                  System.out.println("Servidor No Activo, Ejecute primero el Servidor Bank en la Carpeta Servidor.");
                  e.printStackTrace();
               } catch (NotBoundException e) {
                  System.out.println("Servidor No Activo, Ejecute primero el Servidor Bank en la Carpeta Servidor.");
                  e.printStackTrace();
               }
               break;
            case 2:
               RetirarDeCuenta();
               break;
            case 3:
               DepositarEnCuenta();
               break;
            case 4:
               TransfCuentas();
               break;
            case 5:
               ConsultarUltimos5();
               break;
            default:
               System.out.println("Opción no válida.");
               break;
         }
      }
   }


   static int MenuPpal(){  //Dibuja Menu Principal
      char tecla = 0; 
      int tec = -1;
      cls();
      while(tec <  0 || tec > 6){
         System.out.println("Bienvenidos a su Banco");
         System.out.println("[1] Crear Una Cuenta (Máximo 3 por Persona)");
         System.out.println("[2] Retiro de Efectivo");
         System.out.println("[3] Deposito en Efectivo");
         System.out.println("[4] Transferencia entre Cuentas (Propias y Terceros)");
         System.out.println("[5] Consultar Ultimos 5 Movimientos de una Cuenta");
         System.out.println("[0] Salir");
         System.out.print("Seleccione una opcion: ");
         tecla = getKey();
         tec = (int) tecla - '0';
         cls();
      }      
      return(tec);
   }

   private static void CrearCuenta() throws RemoteException, NotBoundException { // [1] Crear Una Cuenta (Máximo 3 por Persona)
      System.out.println("[1] Crear Una Cuenta (Máximo 3 por Persona)");
      System.out.print("Introduzca su numero de cedula (sin puntos):");
      String docum = getString();
      int dif=8-docum.length();
      for(int i=0; i<dif; i++)
         docum="0"+docum; //para hacerla de 8 caracteres
      
            
      try{
         //Obteniendo registro de rmiregistry
         // Registry registry = LocateRegistry.getRegistry("127.0.0.1", 9100);
         // para obtener la cuenta de mayor numero, es el primer registro
         user = (Usuario) registry.lookup("00000000"); // aqui da error si no existe y va al Catch
                                                            // si no lee ultima cuenta creada
         ultimaCuenta = user.getCuenta1();
         iUltimaCuenta = Integer.parseInt(ultimaCuenta);
         user = (Usuario) registry.lookup(docum); // aqui da error si no existe y va al Catch
         if(user.getCuenta1().compareTo((String)"00000000")==0
            && user.getCuenta2().compareTo((String)"00000000")==0 
            && user.getCuenta3().compareTo((String)"00000000")==0){
            System.out.println("Usted ya tiene 3 cuentas. Llego al limite");
            System.out.print("Presione una tecla para continuar.");
            getKey();
            return;
         }
         if(!CheckCuenta()) // validacion de Nombre de Usuario y Password
            return;
      
         // el usuario existe y por lo menos debe tener una cuenta
         // averiguamos cuantas tiene, numCuentas
         int numCuentas = 1;
         String [] cuenta = {"","","",""};
         Double [] sCuenta = {0.0, 0.0, 0.0, 0.0};
         String name = user.getName();
         String userName = user.getUserName();
         String password = user.getPassword();
         cuenta[1]  = user.getCuenta1();
         sCuenta[1] = user.getSCuenta1();
         cuenta[2]  = user.getCuenta2();
         sCuenta[2] = user.getSCuenta2();
         cuenta[3]  = user.getCuenta3();
         sCuenta[3] = user.getSCuenta3();

         System.out.println("Cuenta [1]: "+cuenta[1]);
         if(cuenta[2].compareTo((String)"00000000") != 0){
            System.out.println("Cuenta [2]: "+cuenta[2]);
            numCuentas++;
         }
         if(user.getCuenta3().compareTo((String)"00000000") != 0){
            System.out.println("Cuenta [3]: "+cuenta[3]);
            numCuentas++;
         }
         
         // si llega hasta aqui, se crea la cuenta nueva al usuario existente
         numCuentas++; // nueva cuenta
         System.out.print("Monto de apertura de la cuenta["+Integer.toString(numCuentas)+"]: ");
         String montoApertura = getString(); // ojo chequear que sea numero positivo y > 0
         sCuenta[numCuentas]=Double.parseDouble(montoApertura);
         iUltimaCuenta++;
         String sNum = Integer.toString(iUltimaCuenta);
         ultimaCuenta = "";
         for(int i = 0; i<8-sNum.length(); i++)
            ultimaCuenta += "0";  
         ultimaCuenta += sNum;  
         cuenta[numCuentas] = ultimaCuenta;

         // actualizar con metodo la base de dato del servidor y el encabezado debe tener el ultimo numero de cuenta usado
         // solo se actualiza cuenta2 o 3
        
         // registry = LocateRegistry.getRegistry("127.0.0.1", 9100);
         

         // CrearRMI FuncionesRemotas = (CrearRMI) registry.lookup("CrearUsuario");
         FuncionesRemotas.ActualizarUltimoNumCuenta(ultimaCuenta);
         // se registra el monto completo porque es una apertura y el saldo anterior es 0, sino seria solo el deposito o retiro
         FuncionesRemotas.RespaldarMovimiento(docum, cuenta[numCuentas], sCuenta[numCuentas]); 
         FuncionesRemotas.CrearOActualizarUsuario(name, userName, docum, password, cuenta[1], sCuenta[1], cuenta[2], sCuenta[2], cuenta[3], sCuenta[3]);
         System.out.println("Cuenta ["+Integer.toString(numCuentas)+"] creada bajo el numero "+ ultimaCuenta+" por un monto de " + Double.parseDouble(montoApertura)); 
      }
      catch(IOException | NotBoundException e) {
         System.out.println("Cedula no registrada. Se procede a abrir cuenta.");
         // se crea la cuenta nueva al usuario existente
         System.out.print("Nombre Completo: ");
         String name = getString();
         System.out.print("Nombre de Usuario: ");
         String nameUser = getString();
         System.out.print("Password: ");
         String password = getString();
         System.out.print("Monto de apertura: ");
         String montoApertura = getString(); // ojo chequear que sea numero positivo y > 0
         iUltimaCuenta++;
         String sNum = Integer.toString(iUltimaCuenta);
         ultimaCuenta = "";
         for(int i = 0; i<8-sNum.length(); i++)
            ultimaCuenta += "0";  
         ultimaCuenta += sNum;  

         // actualizar con metodo la base de dato del servidor y el encabezado debe tener el ultimo numero de cuenta usado
         // solo se actualiza cuenta1 en el primer registro
         // Registry registry = LocateRegistry.getRegistry("127.0.0.1", 9100);
         // CrearRMI FuncionesRemotas = (CrearRMI) registry.lookup("CrearOActualizarUsuario");
         // boolean  result = 
         // actualiza el encbezado
         FuncionesRemotas.ActualizarUltimoNumCuenta(ultimaCuenta);
          // se registra el monto completo porque es una apertura y el saldo anterior es 0, sino seria solo el deposito o retiro
         FuncionesRemotas.RespaldarMovimiento(docum, ultimaCuenta, Double.parseDouble(montoApertura)); 
         // Actualizar Usuario
         FuncionesRemotas.CrearOActualizarUsuario(name, nameUser, docum, password, ultimaCuenta, Double.parseDouble(montoApertura),"00000000", 0.0,"00000000",0.0);
         System.out.println("Cuenta [1] creada bajo el numero "+ ultimaCuenta+" por un monto de " + montoApertura);
         
         
         /* ojo solo para debug
         String[] nombresObjetos = registry.list();
         // Imprimir los nombres de los objetos registrados
         for (String nombre : nombresObjetos) {
            System.out.println(nombre);
         }
         */
      }
      
      System.out.print("Presione una tecla para continuar.");
      getKey();
   }

   private static void RetirarDeCuenta() {   // [2] Retiro de Efectivo
      System.out.println("[2] Retiro de Efectivo");
      System.out.println("Introduzca su numero de cedula (sin puntos):");
      String docum = getString();
      int dif=8-docum.length();
      for(int i=0; i<dif; i++)
         docum="0"+docum; //para hacerla de 8 caracteres
           
      try{
         user = (Usuario) registry.lookup(docum); // aqui da error si no existe y va al Catch
         if(!CheckCuenta()) // validacion de Nombre de Usuario y Password
            return;
      
         // se busca el numero de cuentas que posee              
         String [] cuenta = {"","","",""};
         Double [] sCuenta = {0.0, 0.0, 0.0, 0.0};
         int numCuentas = 1; //la primera siempre deberia existir
         cuenta[1]  = user.getCuenta1();
         sCuenta[1]  = user.getSCuenta1();
         cuenta[2]  = user.getCuenta2();
         sCuenta[2]  = user.getSCuenta2();         
         cuenta[3]  = user.getCuenta3();
         sCuenta[3]  = user.getSCuenta3();
         if(user.getCuenta2().compareTo((String)"00000000")!=0)
            numCuentas++;
         if(user.getCuenta3().compareTo((String)"00000000")!=0)
            numCuentas++;
         cls();
         System.out.println("Usted posee "+Integer.toString(numCuentas)+" Cuenta(s) en nuestro Banco.");
         for(int i = 0; i<numCuentas; i++)
            System.out.println("Cuenta ["+Integer.toString(i+1)+"] "+ cuenta[i+1]);
         int tecla=1;
         if(numCuentas>1){         
            System.out.print("Seleccione la cuenta de la que quiere Retirar: ");
            tecla= getKey()-'0';
         }
         if(tecla <1 || tecla > numCuentas){
            System.out.println("Selección Inválida.");
            return;
         }
         
         System.out.print("Ingrese el Monto en Efectivo a Retirar de la Cuenta ["+tecla+"]: ");
         String montoRetirar = getString(); // ojo chequear que sea numero positivo y > 0
         double dMontoRetirar = Double.parseDouble(montoRetirar);
         double dSaldo = sCuenta[tecla];
         if(dMontoRetirar > dSaldo)
         {
            System.out.println("Saldo insuficiente en la cuenta ["+tecla+"]");

         }
         else
         {
            // Actualizar registro
            if(tecla==1)
               sCuenta[1]-=dMontoRetirar;
            else if (tecla==2)
               sCuenta[2]-=dMontoRetirar;
            else
               sCuenta[3]-=dMontoRetirar;
            
            FuncionesRemotas.CrearOActualizarUsuario(user.getName(), user.getUserName(), docum, user.getPassword(), cuenta[1], sCuenta[1], cuenta[2], sCuenta[2], cuenta[3], sCuenta[3]);

            // Actualizar Archivo cuenta
            FuncionesRemotas.RespaldarMovimiento(docum, cuenta[tecla], -dMontoRetirar); 
            System.out.println("Transaccion Exitosa!. Retire su Efectivo.");   
         }
      }
      catch(IOException | NotBoundException e) {
         System.out.println("Cedula no registrada. No se puede Retirar.");         
      }   
      System.out.print("Presione una tecla para continuar.");
      getKey();     
   }

   private static void DepositarEnCuenta() { // [3] Deposito en Efectivo
      System.out.println("[3] Deposito en Efectivo");
      System.out.println("Introduzca su numero de cedula (sin puntos):");
      String docum = getString();
      int dif=8-docum.length();
      for(int i=0; i<dif; i++)
         docum="0"+docum; //para hacerla de 8 caracteres
           
      try{
         user = (Usuario) registry.lookup(docum); // aqui da error si no existe y va al Catch
         if(!CheckCuenta()) // validacion de Nombre de Usuario y Password
            return;
      
         // se busca el numero de cuentas que posee              
         String [] cuenta = {"","","",""};
         Double [] sCuenta = {0.0, 0.0, 0.0, 0.0};
         int numCuentas = 1; //la primera siempre deberia existir
         cuenta[1]  = user.getCuenta1();
         sCuenta[1]  = user.getSCuenta1();
         cuenta[2]  = user.getCuenta2();
         sCuenta[2]  = user.getSCuenta2();         
         cuenta[3]  = user.getCuenta3();
         sCuenta[3]  = user.getSCuenta3();
         if(user.getCuenta2().compareTo((String)"00000000")!=0)
            numCuentas++;
         if(user.getCuenta3().compareTo((String)"00000000")!=0)
            numCuentas++;
         cls();
         System.out.println("Usted posee "+Integer.toString(numCuentas)+" Cuenta(s) en nuestro Banco.");
         for(int i = 0; i<numCuentas; i++)
            System.out.println("Cuenta ["+Integer.toString(i+1)+"] "+ cuenta[i+1]);
         int tecla=1;
         if(numCuentas>1){         
            System.out.print("Seleccione la cuenta en la que quiere Depositar: ");
            tecla= getKey()-'0';
         }
         if(tecla < 1 || tecla > numCuentas){
            System.out.println("Selección Inválida.");
            return;
         }
         
         System.out.print("Ingrese el Monto en Efectivo a Depositar en la Cuenta ["+tecla+"]: ");
         String montoDepositar = getString(); // ojo chequear que sea numero positivo y > 0
         double dMontoDepositar = Double.parseDouble(montoDepositar);
         // double dSaldo = sCuenta[tecla];
         if(dMontoDepositar < 0)
         {
            System.out.print("Monto Inválido");

         }
         else
         {
            // Actualizar registro
            if(tecla==1)
               sCuenta[1]+=dMontoDepositar;
            else if (tecla==2)
               sCuenta[2]-=dMontoDepositar;
            else
               sCuenta[3]-=dMontoDepositar;
            
            FuncionesRemotas.CrearOActualizarUsuario(user.getName(), user.getUserName(), docum, user.getPassword(), cuenta[1], sCuenta[1], cuenta[2], sCuenta[2], cuenta[3], sCuenta[3]);

            // Actualizar Archivo cuenta
            FuncionesRemotas.RespaldarMovimiento(docum, cuenta[tecla], dMontoDepositar); 
            System.out.println("Transaccion Exitosa!. Efectivo verificado.");   
         }
      }
      catch(IOException | NotBoundException e) {
         System.out.println("Cedula no registrada. No se puede Depositar.");         
      }   
      System.out.print("Presione una tecla para continuar.");
      getKey();     
   }

   private static void TransfCuentas() {   // [4] Transferencia entre Cuentas 
   System.out.println("[4] Transferencia entre Cuentas (Propias y Terceros)");
   System.out.println("Introduzca su numero de cedula (sin puntos):");
   String docum = getString();
   int tecla=0;
   int numCuentaDestino=0;
   String [] cuentasDestino = {"","","",""};
   Double [] sCuentasDestino = {0.0, 0.0, 0.0, 0.0};
   double dMontoRetirar=0;
   int dif=8-docum.length();
   for(int i=0; i<dif; i++)
      docum="0"+docum; //para hacerla de 8 caracteres
        
   try{
      user = (Usuario) registry.lookup(docum); // aqui da error si no existe y va al Catch
      if(!CheckCuenta()) // validacion de Nombre de Usuario y Password
         return;
   
      // se busca el numero de cuentas que posee              
      String [] cuenta = {"","","",""};
      Double [] sCuenta = {0.0, 0.0, 0.0, 0.0};
      int numCuentas = 1; //la primera siempre deberia existir
      cuenta[1]  = user.getCuenta1();
      sCuenta[1]  = user.getSCuenta1();
      cuenta[2]  = user.getCuenta2();
      sCuenta[2]  = user.getSCuenta2();         
      cuenta[3]  = user.getCuenta3();
      sCuenta[3]  = user.getSCuenta3();
      if(user.getCuenta2().compareTo((String)"00000000")!=0)
         numCuentas++;
      if(user.getCuenta3().compareTo((String)"00000000")!=0)
         numCuentas++;
      cls();
      System.out.println("Usted posee "+Integer.toString(numCuentas)+" Cuenta(s) en nuestro Banco.");
      for(int i = 0; i<numCuentas; i++)
         System.out.println("Cuenta ["+Integer.toString(i+1)+"] "+ cuenta[i+1]);
      tecla=1;
      if(numCuentas>1){         
         System.out.print("Seleccione la cuenta de la que quiere Transferir: ");
         tecla= getKey()-'0';
      }
      if(tecla <1 || tecla > numCuentas){
         System.out.println("Selección Inválida.");
         return;
      }
      System.out.print("Ingrese la Cédula del dueño de la Cuenta a realizar la Transferencia (8 Digitos Numéricos): ");
      String cedulaDestino = getString(); 
      dif=8-cedulaDestino.length();
      String documDestino = cedulaDestino;
      for(int i=0; i<dif; i++)
         documDestino="0"+documDestino; //para hacerla de 8 caracteres
      try{
         user2 = (Usuario) registry.lookup(documDestino); // aqui da error si no existe y va al Catch
         System.out.print("Ingrese el Numero de Cuenta a Transferir (8 Digitos Numéricos): ");
         String cuentaDestino = getString(); 
         
         // Buscar si la cuenta existe
         numCuentaDestino = 0; //la primera siempre deberia existir
         cuentasDestino[1]  = user2.getCuenta1();
         sCuentasDestino[1]  = user2.getSCuenta1();
         cuentasDestino[2]  = user2.getCuenta2();
         sCuentasDestino[2]  = user2.getSCuenta2();
         cuentasDestino[3]  = user2.getCuenta3();
         sCuentasDestino[3]  = user2.getSCuenta3();
         for(int i=1; i<=3;i++){
            if(cuentaDestino.compareTo(cuentasDestino[i])==0){
               numCuentaDestino = i;
               break;
            }
         }
         if(numCuentaDestino==0){
            System.out.println("La Cuenta "+cuentaDestino+ " no pertenece a "+cedulaDestino);
            System.out.println("Presione una tecla para continuar.");
            getKey();     
            return;
         }
         System.out.print("Ingrese el Monto a Transferir ");
         String montoRetirar = getString(); // ojo chequear que sea numero positivo y > 0
         dMontoRetirar = Double.parseDouble(montoRetirar);
         double dSaldo = sCuenta[tecla];
         if(dMontoRetirar > dSaldo)
         {
            System.out.println("Saldo insuficiente en la cuenta["+tecla+"]");
            System.out.println("Presione una tecla para continuar.");
            getKey();     
            return;
         }
         else
         {
            // Actualizar registro del depositante
            if(tecla==1)
               sCuenta[1]-=dMontoRetirar;
            else if (tecla==2)
               sCuenta[2]-=dMontoRetirar;
            else
               sCuenta[3]-=dMontoRetirar;
            
            // Actualizar el Registro en el Servidor
            FuncionesRemotas.CrearOActualizarUsuario(user.getName(), user.getUserName(), docum, user.getPassword(), cuenta[1], sCuenta[1], cuenta[2], sCuenta[2], cuenta[3], sCuenta[3]);
   
            // Actualizar Archivo cuenta
            FuncionesRemotas.RespaldarMovimiento(docum, cuenta[numCuentaDestino], -dMontoRetirar); 
            
            // Actualizar registro del que recibe la transferencia
            sCuentasDestino[numCuentaDestino] = sCuentasDestino[numCuentaDestino] + dMontoRetirar;
            FuncionesRemotas.CrearOActualizarUsuario(user2.getName(), user2.getUserName(), documDestino, user2.getPassword(), cuentasDestino[1], sCuentasDestino[1],
                cuentasDestino[2], sCuentasDestino[2], cuentasDestino[3], sCuentasDestino[3]);
   
            // Actualizar Archivo cuenta
            FuncionesRemotas.RespaldarMovimiento(documDestino, cuentasDestino[tecla], dMontoRetirar); 
            
            System.out.println("Transaccion Exitosa!. Transferencia Realizada.");   
         }
      
      }
      catch(IOException | NotBoundException e) {
         System.out.println("Cedula no registrada. No se puede Transferir sin una Cuenta.");         
         return;
      }
   }
   catch(IOException | NotBoundException e) {
      System.out.println("Cedula no registrada. No se puede Transferir sin una Cuenta.");         
   }   
   System.out.print("Presione una tecla para continuar.");
   getKey();     
}
  
   private static void ConsultarUltimos5() { // [5] Consultar Ultimos 5 Movimientos de una Cuenta
      System.out.println("[5] Consultar Ultimos 5 Movimientos de una Cuenta");
      System.out.print("Introduzca su numero de cedula (sin puntos):");
      String docum = getString();
      int dif=8-docum.length();
      for(int i=0; i<dif; i++)
         docum="0"+docum; //para hacerla de 8 caracteres

      try{
         
         user = (Usuario) registry.lookup(docum); // aqui da error si no existe y va al Catch
         if(!CheckCuenta()) // validacion de Nombre de Usuario y Password
            return;
      
         // se busca el numero de cuentas que posee              
         String [] cuenta = {"","","",""};
         Double [] sCuenta = {0.0, 0.0, 0.0, 0.0};
         int numCuentas = 1; //la primera siempre deberia existir
         cuenta[1]  = user.getCuenta1();
         sCuenta[1]  = user.getSCuenta1();
         
         if(user.getCuenta2().compareTo((String)"00000000")!=0){
            numCuentas++;
            cuenta[2]  = user.getCuenta2();
            sCuenta[2]  = user.getSCuenta2();
         }
         if(user.getCuenta3().compareTo((String)"00000000")!=0)
         {
            numCuentas++;
            cuenta[3]  = user.getCuenta3();
            sCuenta[3]  = user.getSCuenta3();
         }
         cls();
         System.out.println("Usted posee "+Integer.toString(numCuentas)+" Cuentas en nuestro Banco.");
         for(int i = 0; i<numCuentas; i++)
            System.out.println("Cuenta ["+Integer.toString(i+1)+"] "+ cuenta[i+1]);

         int tecla =1; 
         if(numCuentas>1){
            System.out.print("Seleccione la cuenta que quiere Consultar: ");
            
            tecla= getKey()-'0';
            if(tecla <1 || tecla > numCuentas){
               System.out.println("Selección Inválida.");
               return;
            }
         }
         List<String> movs5  = FuncionesRemotas.BuscarUltimos5Movs(cuenta[tecla]);
         System.out.println("Ultimos Movimientos");

         int movs = movs5.size();
         for(int i = 0; i<movs; i++)
            System.out.println(movs5.get(i));
            System.out.println("Saldo Actual en la cuenta "+cuenta[tecla]+" es de " + sCuenta[tecla]);
         }
      catch(IOException | NotBoundException e) {
         System.out.println("Cedula no registrada. No se puede Consultar.");         
   }   
   System.out.print("Presione una tecla para continuar.");
   getKey();
   }

   private static boolean CheckCuenta() { // Checkeo de Login (User Name) y Password
      System.out.print("Ingrese su Nombre de Usuario: ");
      String nameUser = getString();
      System.out.print("Ingrese su Password: ");
      String password = getString();
      try {
         if(user.getUserName().compareTo(nameUser) != 0 || user.getPassword().compareTo(password) != 0 ){
            System.out.println("Su Nombre de Usuario o Password no coincide ");
            System.out.println("Presione una tecla para continuar.");
            getKey();
            return false;
         }
      } catch (RemoteException e) {
         System.out.println("Exception in client side" + e);
         System.exit(1);
         // e.printStackTrace();
      }
      return true;
   
   }
   
   public static char getKey() { // Lee un caracter del teclado
         char c = '\0';
         BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
         try {
            c = (char) br.read();
         } catch (IOException e) {
            System.out.println("Error al leer la tecla: " + e.getMessage());
         }
         return c;
   }

   public static String getString() {   // Lee una cadena
      
      BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
      String texto = "";
      try {
         texto = br1.readLine();
      } catch (IOException e) {
          System.out.println("Error al leer el texto: " + e.getMessage());
      
      }
      return texto;
   }
   
   static void cls(){      //Borra Pantalla Terminal
      System.out.print("\033[H\033[2J");
      System.out.flush();
   }

}