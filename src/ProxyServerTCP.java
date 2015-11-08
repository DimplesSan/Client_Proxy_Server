import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyServerTCP implements Runnable{
	
	ServerSocket objTCPSock;
	private int PrxyTCPPort; 
	public ProxyServerTCP(int TCPPort){
		
		try {
			
			//Create a TCP server socket to listen for clients
			objTCPSock = new ServerSocket(TCPPort);
			PrxyTCPPort = TCPPort;
		} catch (IOException e) {
			System.out.println("ProxyServerTCP-Constructor()-Error:"+e.getMessage());
		}
	}
	
	@Override
	public void run() {
		
		System.out.println("Listening on TCP port for proxy started : "+this.PrxyTCPPort);
		
		while(true){
			Socket objTCPsock;
			try {
				
				//Accept connection from a client on TCP port
				objTCPsock = objTCPSock.accept();
				
				//Create a appropriate type service handler the client at TCP socket based on the flag 
				// for origin server channel
				Thread serviceThread = new Thread(new ServiceClient(objTCPsock , ProxyServerMain.isUseTCPChannelToOrServ()));
				serviceThread.start();
				
				
			} catch (IOException e) {

				e.printStackTrace();
			}

		}
		
	}

}

class ServiceClient implements Runnable{

	private Socket clientTCPSock;
	private boolean boolTCPChannelToOrServ;
	ServiceClient(Socket _clientTCPSock, boolean _isTCPChannel){
		clientTCPSock = _clientTCPSock;
		boolTCPChannelToOrServ = _isTCPChannel;
	}
	
	@Override
	public void run() {
		
		BufferedInputStream objBuffIS = null;
		BufferedOutputStream objBuffOS =null;
		
		try {
				objBuffIS = new BufferedInputStream(clientTCPSock.getInputStream());
				objBuffOS = new BufferedOutputStream(clientTCPSock.getOutputStream());
				
				//Read the msg from client into a msg buffer
				int msglen = (int)objBuffIS.read();
				byte [] temp = new byte[msglen];
				objBuffIS.read(temp);
				
				byte[] relayBuff = new byte[1+msglen];
				relayBuff[0]= (byte)msglen;
				for(int i=0; i<msglen; ++i)
					relayBuff[i+1] = temp[i];
				
				//Send to origin server based on the flag for channel to Origin Server
				//If true indicates TCP channel to server
				if(this.boolTCPChannelToOrServ){
					
					//Create a TCP client socket to contact origin server
					TCPClient objPrClient = new TCPClient(ProxyServerMain.getIpOriginServer(), ProxyServerMain.getTcpPortOrServer());
					
					//Send msg to origin server
					objPrClient.sendMessage(relayBuff);
					
					//Get response origin server
					String respFromOrServer = objPrClient.getResponse();
					byte [] bytesFromServ = respFromOrServer.getBytes();
					int lenOfbyteFromServ = bytesFromServ.length;
					
					//Encapsulate the response for the client
					byte [] newResToClient = new byte[1+lenOfbyteFromServ];
					newResToClient[0] = (byte)lenOfbyteFromServ;
					for(int i = 0; i<lenOfbyteFromServ; ++i){
						newResToClient[i+1] = bytesFromServ[i];
					}
					
					//respond to the client
					objBuffOS.write(newResToClient);
					objBuffOS.flush();
				}
				else{
					//Create a UDP Client socket to contact the origin server
					UDPClient objPrxUDPClient = new UDPClient(ProxyServerMain.getIpOriginServer(), ProxyServerMain.getUpdPortOrServer(), relayBuff);
					
					//Send msg to origin server 
					objPrxUDPClient.sendMsg();
					
					//Get the response from the origin server
					String respFromOrServer = objPrxUDPClient.getResponse();
					
					byte [] bytesFromServ = respFromOrServer.getBytes();
					int lenOfbyteFromServ = bytesFromServ.length;
					
					//Encapsulate the response for the client
					byte [] newResToClient = new byte[1+lenOfbyteFromServ];
					newResToClient[0] = (byte)lenOfbyteFromServ;
					for(int i = 0; i<lenOfbyteFromServ; ++i){
						newResToClient[i+1] = bytesFromServ[i];
					}
					
					
					//respond to the client
					objBuffOS.write(newResToClient);
					objBuffOS.flush();
					
	
				}

			
			
		} catch (IOException e) {
			
			byte [] arrRetErrMsg = "e # # # Cannot_connect_to_orign_server".getBytes();
			int bytLen = arrRetErrMsg.length;
			byte [] newRetByteArr = new byte[bytLen+1];
			newRetByteArr[0] = (byte)bytLen;
			for(int i=0; i< bytLen; ++i)
				newRetByteArr[i+1] = arrRetErrMsg[i];
			
			try {
				//Respond with error message to client
				objBuffOS.write(newRetByteArr);
				objBuffOS.flush();
			} catch (IOException e1) {
				
				System.out.println("ProxyServerTCP: Error while responding to client with the error msg from Origin Server - " +e1.getMessage() );
			}
			
//			try {
//				
//				//Close the output and input streams
//				objBuffOS.close();
//				objBuffIS.close();
//				
//				
//			} catch (IOException e2) {
//				
//				System.out.println("ProxyServerTCP: Error while Closing connections to client - " +e.getMessage() );
//			}
			
		}

		
		
		
	}
	

	
}
