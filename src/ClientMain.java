import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class ClientMain {
	
	private TCPClient oTCPClient;
	private UDPClient oUDPClient;
	private static byte[] msg;
	private static boolean bUTCFormatFlag; //set based on -z option
	private static int iNumofQueries;

	public ClientMain(String []args){
		
		//Set the display format
		setUTCFormatFlag(args);
		
		//Set num of queries from Server
		iNumofQueries = setNumofQueries(args);
		
		//Build message from command line arguments
		//-type of Msg - ('g'-get/'s'-set/'r'-response/'e'-error)
		//-time in UTC format
		//username 
		//password 
		//Rest of the message from server (success or error message)
		msg = buildMsg(args);
		
	}
	
	private void setUTCFormatFlag(String[] args){
		
		for(int i =0; i<= args.length-1; ++i ){
			if(args[i].equalsIgnoreCase("-z")){
				ClientMain.bUTCFormatFlag = true;
				break;
			}
		}
		
	}
	
	private int setNumofQueries(String[] args){
		
		int iReturnVal = 1;
		
		for(int i =0; i<= args.length-1; ++i ){
			if(args[i].equalsIgnoreCase("-n")){
				if(args[i+1].contains("-"))
					break;
				else{
					iReturnVal = Integer.parseInt(args[i+1]);
					System.out.println("Num of times to query: " + args[i+1] );
					break;
				}
			}
		}
		
		return iReturnVal;
	}	
	
	
	private byte [] buildMsg(String[] args){
		
		String sMsg = null;
		
		//Time to be get from server - Default Message
		sMsg = "g # # # #";
		byte[] msg = sMsg.getBytes();		
		
		for(int i =0; i<= args.length-1; ++i ){
			
			//Time has to be set to server
			if(args[i].equals("-T")){
				
				//Extract username and password
				String userName = extractUserName(args);
				String passWord = extractPassword(args);
				
				if(userName != null && passWord!= null){
					
					System.out.println("Time to be set to server in UTC format: "+ args[i+1]);
					sMsg = "s "+ args[i+1]+" "+ userName +" "+ passWord + " #"; //"s <long time> <username> <password> <msg>"
					msg = sMsg.getBytes();	//get the num of bytes
					
					int msgLen = msg.length;
					byte []newMsg = new byte[msgLen + 1];
					newMsg[0] = (byte)msgLen;
					
					//Prepend the num of bytes of the message
				    for(int j=0;j<msgLen; ++j){
				    	newMsg[j+1] = msg[j];
				    }
				    
				    msg = newMsg;
					return msg;
				}
				else{
					System.out.println("User name or Password is blank. Defaulting to get message.");
					return msg;	
				}
			}
			
		}
		return msg;
	}
	
	private String extractUserName(String []args){
		
		for(int i =0; i<= args.length-1; ++i ){
			
			//Time has to be set to server
			if(args[i].equalsIgnoreCase("--user") || args[i].equalsIgnoreCase("--usr")){
				if(args[i+1].contains("-"))
					break;
				else{
					System.out.println("Username entered: " + args[i+1]);
					return args[i+1];
				}
					
			}
		}	
		return null;
	}
	
	private String extractPassword(String []args){
		
		for(int i =0; i<= args.length-1; ++i ){
			
			//Time has to be set to server
			if(args[i].equalsIgnoreCase("--pass")){
				if(args[i+1].contains("-"))
					break;
				else{
					System.out.println("Password entered: " + args[i+1]);
					return args[i+1];
				}
					
			}
		}	
		return null;		
	}
	
	
	public void displayResponse(String serverResp, long lRTT){
		
		
		String [] arrRespFields = serverResp.split(" ");
		
		if(!arrRespFields[0].trim().equalsIgnoreCase("e")){	//If message type is not that of an error
			
			if(bUTCFormatFlag){
				System.out.println("Server time is: "+arrRespFields[1].trim() + "    RTT: " + lRTT + "millisecs");	//Server time in UTC format
			}else{
				Date dt = new Date(Long.parseLong(arrRespFields[1].trim()) );
				System.out.println("Server time is: " + dt.toString() + "    RTT: " + lRTT + "millisecs");	//Server time in human readable format
			}
		}
		else{
			System.out.println("ERROR!");
		}
		
		System.out.println(arrRespFields[arrRespFields.length-1]);	//Print out the message from server
	}
	
	
	public void  startTCPClient(String sIpAddr, int iDestPort, String[] args){
		
		//Query for time as specified in the command line args
		for(int i=0; i<iNumofQueries; ++i){
			
			try {
				//Connection Established else error is thrown
				oTCPClient = new TCPClient(sIpAddr, iDestPort);
				System.out.println("TCP Client started and initialized.");
				
				//Note the time before sending the message
				long timeBeforeSend = System.currentTimeMillis();
				
				//Send Msg
				oTCPClient.sendMessage(ClientMain.msg);
				System.out.println("TCP Client msg sent to destination with ip address: "+sIpAddr + " & port: " + iDestPort);
				
				//Get Response
				String sResponse = oTCPClient.getResponse();
				
				//Note the time after getting the response
				long timeAfterReceive = System.currentTimeMillis();
				long lRTT = timeAfterReceive - timeBeforeSend;
				
				displayResponse(sResponse, lRTT);
				
				oTCPClient.severConnection();
							
			} catch (IOException e) {
				System.out.println("clientMain-startTCPClient()-Error:"+e.getMessage());
			}
		}

	}
	
	
	
	public void startUDPClient(String sIpAddr, int iDestPort, String[] args){
		
		//Query for time as specified in the command line args
		for(int i=0; i<iNumofQueries; ++i){
			try {
				UDPClient oUDPClient = new UDPClient(sIpAddr, iDestPort, msg);
				System.out.println("UDP Client started and initialized.");

				//Note the time before sending the message
				long timeBeforeSend = System.currentTimeMillis();
				
				oUDPClient.sendMsg();
				
				//Get Response
				String sResponse = oUDPClient.getResponse();
				
				//Note the time after getting the response
				long timeAfterReceive = System.currentTimeMillis();
				long lRTT = timeAfterReceive - timeBeforeSend;
				
				displayResponse(sResponse, lRTT);
				
								
			} catch (IOException e) {
				System.out.println("clientMain-startTCPClient()-Error:"+e.getMessage());
			}
		}
	}

}
