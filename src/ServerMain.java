import java.util.Calendar;

public class ServerMain {
	
	private TCPServer oTCPServer;
	private UDPServer oUDPServer;
	private static long iServTime;
	private static String userName;
	private static String password;
	
	public ServerMain(int iTCPPort, int iUDPPort, long _iServTime){
		
		oTCPServer = new TCPServer(iTCPPort);
		System.out.println("TCP port of server initialized at " + iTCPPort);
		
		oUDPServer = new UDPServer(iUDPPort);
		System.out.println("UDP port of server initialized at " + iUDPPort);
		
		iServTime = _iServTime;
		System.out.println("Server time initialized to : "+iServTime);
		ServerMain.userName="NA";
		ServerMain.password="NA";
				
	}
	
	
	public static long getServerTime(){
		return iServTime; 
	}
	

	public static boolean setServerTime(long newTime, String user, String passWd){
		
		boolean retVal = false;
		
		if(user.equals(ServerMain.userName) && passWd.equals(ServerMain.password) ){
			retVal = true;
			iServTime = newTime;
		}
		return retVal;
		
	}
	
	
	public void startServer(){
		
		Thread oTCPServerThread = new Thread(oTCPServer);
		Thread oUDPServerThread = new Thread(oUDPServer);
		
		oTCPServerThread.start();
		oUDPServerThread.start();
		
	}
	
	
	public void shutdownServers(){
		TCPServer.shutdownTCPServer();
		UDPServer.shutdownUDPServer();
	}
	
	
	public static byte[] performActionAndNotify(String sMsg){
		
		//Default action get the servertime and create return message
		String retMsg = "r "+ServerMain.getServerTime()+" # # Success";
		long tempServerTime = ServerMain.getServerTime();
		
		String []msgFields = sMsg.split(" ");
		//Perform set only if type of message is 'set'
		if(msgFields[0].equalsIgnoreCase("s")){
			
			if(ServerMain.setServerTime(Long.parseLong(msgFields[1]), msgFields[2], msgFields[3]) )
				retMsg = "r "+ServerMain.getServerTime()+" # # Success OldTime:"+tempServerTime;
			else
				retMsg = "e "+ServerMain.getServerTime()+" # # Invalid_Credentials";
		}
		
		byte [] retByteArr = retMsg.getBytes();
		int retLen = retByteArr.length;
		byte [] newByteArr = new byte[1+retLen];
		newByteArr[0] = (byte)retLen;
		for(int i=0; i< retLen; ++i){
			newByteArr[i+1] = retByteArr[i];
		}
			
		return newByteArr;	
	}
	
	
	public void setCredentialsFromCommLine(String[] args){
		
		//Check for username
		for(int i =0; i<= args.length-1; ++i ){
			if(args[i].equalsIgnoreCase("--user")){
				//Check if the arg is not blank
				if(args[i+1].contains("-"))
					break;
				else{
					ServerMain.userName = args[i+1];
					break;
				}
			}
		}
		
		//Check for password
		for(int i =0; i<= args.length-1; ++i ){
			if(args[i].equalsIgnoreCase("--pass")){
				//Check if the arg is not blank
				if(args[i+1].contains("-"))
					break;
				else{
					ServerMain.password = args[i+1];
					break;
				}
			}
		}
	}
	
}