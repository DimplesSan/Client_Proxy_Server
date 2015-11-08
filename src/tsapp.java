import java.util.Arrays;
import java.util.Date;

public class tsapp {

	public static int iNumOfArgs;
	
	public static void main(String [] args){

//		java.util.Date D = new java.util.Date();
//		long t = D.getTime();
//		System.out.println(new Long(5).toString().getBytes().length);
//		
//		Date d = new Date(t);
//		System.out.println(d.toString());

		iNumOfArgs = args.length;
	
		//Get the first argument to check if the Server/Proxy/Client is to be initiated.
		if(args[0].toLowerCase().charAt(1) == 's'){
			//Initiate Server
			initiateServer(args);
			
		}else if(args[0].toLowerCase().charAt(1) == 'p'){
			//Initiate Proxy
			initiateProxy(args);
		}
		else{
			//Initiate Client
			intitateClient(args);
		}
	}
	
	
	private static void initiateProxy(String[] args){	
		
		ProxyServerMain.setIpOriginServer(args[1]); //set the IP of the origin server
		
		//Set the Ip based on the args position in the constructor and initialize the proxy server on TCP and UDP ports
		ProxyServerMain objProxyMain = new ProxyServerMain(Integer.parseInt(args[iNumOfArgs-1]), Integer.parseInt(args[iNumOfArgs-2]),args );
		
//		//Extract the port numbers of the origin server
		ProxyServerMain.setOriginServerPorts(args);
		objProxyMain.startProxyServer();
	}
	
	
	private static void initiateServer(String [] args){
		
		//the structure of the command will be 
		//tsapp -s -T<time> [options] UDP_Port TCP_Port
		ServerMain oServer= new ServerMain(Integer.parseInt(args[iNumOfArgs-1]), Integer.parseInt(args[iNumOfArgs-2]), Long.parseLong(args[2]));
		oServer.setCredentialsFromCommLine(args);
		oServer.startServer();
		
	}
	
	private static void intitateClient(String [] args){
		
		//default behavior - UDP - hence default value is false
		boolean bTCPFlag = false; //set based on -u /-t options
		
		
		//the structure of the args is as 
		//tsapp –c server [options] port
		try{
			String sIpAddr = args[1];
			int iDestPort = Integer.parseInt(args[iNumOfArgs-1]);
			
			//Check for TCP or UDP Client
			bTCPFlag = checkForTCPFlag(args);
			
			ClientMain oClient = new ClientMain(args);


				if(bTCPFlag){
					
					//Create and initialize the TCPclient object
					oClient.startTCPClient(sIpAddr, iDestPort, args);
				}
				else{
					
					oClient.startUDPClient(sIpAddr, iDestPort, args);
				}

			
				
		}
		catch(Exception e){
			System.out.println("tsapp-inititateClient()-Error:"+e.getMessage());
		}

	}
	
	private static boolean checkForTCPFlag(String [] args){
		
		boolean bReturnVal = false;
		
			for(int i =0; i<= args.length-1; ++i ){
				if(args[i].equalsIgnoreCase("-t")){
					bReturnVal = true;
					break;
				}
					
			}
		return bReturnVal;
	}
	
	
}


