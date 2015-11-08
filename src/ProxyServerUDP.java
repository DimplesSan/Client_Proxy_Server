import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class ProxyServerUDP implements Runnable {

	private DatagramSocket udpSocket;
	private int prxyUDPPort;
	
	public ProxyServerUDP(int UDPPort){
		
		try {
			
			udpSocket = new DatagramSocket(UDPPort);
			prxyUDPPort = UDPPort;
	
		} catch (SocketException e) {
			System.out.println("ProxySeverUDP-Constructor-Error:"+e.getMessage());
		}
	}
	
	
	@Override
	public void run() {

		System.out.println("Listening on UDP port for proxy started : "+ this.prxyUDPPort);
		byte[] arrByteBuffer;
		
		//Run the UDP server thread on the proxy
		while(true){
			
			arrByteBuffer = new byte[1024];
			
			//Define the datagram packet using the buffer defined above
			DatagramPacket objRecPacket = new DatagramPacket(arrByteBuffer, arrByteBuffer.length);
			try {
				
				//Receive the incoming message in the datagram packet
				udpSocket.receive(objRecPacket);
				
				//Create a new thread to service the client that sent this packet.
				Thread objServiceClientAtUDP = new Thread(new ServiceClientAtUDP(objRecPacket, ProxyServerMain.isUseTCPChannelToOrServ()));
				objServiceClientAtUDP.start();
				
			} catch (IOException e) {
				System.out.println("Proxy UDP Sever-run()-Error:"+e.getMessage());
			}
		}
		
	}

}

class ServiceClientAtUDP implements Runnable{
	
	private DatagramPacket receivedPacketFrmClient;
	private boolean boolTCPChannelToOrServ;
	
	public ServiceClientAtUDP(DatagramPacket _receivedPacketFrmClient, boolean _boolTCPChannelToOrServ){
		receivedPacketFrmClient = _receivedPacketFrmClient;
		boolTCPChannelToOrServ = _boolTCPChannelToOrServ;
	}

	@Override
	public void run() {
		
		//Extract the Ip and port of client
		InetAddress IPOfClient = receivedPacketFrmClient.getAddress();
		int portOfClient = receivedPacketFrmClient.getPort();
		
		//length of array prepended already 
		byte [] dataFromClient = receivedPacketFrmClient.getData();
		
		//Check the flag for channel to be used to contact origin server
		//Use TCP channel to send the message
		if(this.boolTCPChannelToOrServ){
			

			//Create a TCP client socket to contact origin server
			TCPClient objPrClient = new TCPClient(ProxyServerMain.getIpOriginServer(), ProxyServerMain.getTcpPortOrServer());
			
			try{
				//Since length is already prepended we can send it directly			
				//Send msg to origin server
				objPrClient.sendMessage(dataFromClient);
				
				//Get response origin server
				String respFromOrServer = objPrClient.getResponse();
				byte [] bytesFromServ = respFromOrServer.getBytes();
				int lenOfbyteFromServ = bytesFromServ.length;
				
				//Encapsulate the response for the client
				byte [] newResToClient = new byte[1+lenOfbyteFromServ];
				newResToClient[0] = (byte)lenOfbyteFromServ;
				for(int i = 0; i<lenOfbyteFromServ; ++i)
					newResToClient[i+1] = bytesFromServ[i];
				
				
				//Send the response from the Origin server to Client
				DatagramPacket objSendPacket = new DatagramPacket(newResToClient, newResToClient.length, IPOfClient, portOfClient);
				DatagramSocket tempUDPSock = new DatagramSocket();
				tempUDPSock.send(objSendPacket);
				
				//Close the UDP socket responsible for sending the packet to the client 
				tempUDPSock.close();
				tempUDPSock.disconnect();
					
				
			}
			catch(IOException e){
				
				DatagramSocket tempUDPSock = null; 
				
				//Respond with an error to the client
				byte [] arrRetErrMsg = "e # # # Cannot_connect_to_orign_server".getBytes();
				int bytLen = arrRetErrMsg.length;
				byte [] newRetByteArr = new byte[bytLen+1];
				newRetByteArr[0] = (byte)bytLen;
				for(int i=0; i< bytLen; ++i)
					newRetByteArr[i+1] = arrRetErrMsg[i];
				
				//Create the Error response packet
				DatagramPacket objSendPacket = new DatagramPacket(newRetByteArr, newRetByteArr.length, IPOfClient, portOfClient);
				
				try{
					tempUDPSock = new DatagramSocket();
					tempUDPSock.send(objSendPacket);
					
					//Close the UDP socket responsible for sending the packet to the client 
					tempUDPSock.close();
					tempUDPSock.disconnect();
				}
				catch(Exception e1){
					System.out.println("ProxyServerUDP: Error while responding to client with the error msg from Origin Server - " +e1.getMessage() );
				}
			}
		}
		//Use UDP channel
		else{

			//Create a UDP Client socket to contact the origin server
			UDPClient objPrxUDPClient = new UDPClient(ProxyServerMain.getIpOriginServer(), ProxyServerMain.getUpdPortOrServer(), dataFromClient);
			DatagramSocket tempUDPSock = null;
			
			try{
					//Send msg to origin server 
					objPrxUDPClient.sendMsg();
					
					//Get the response from the origin server
					String respFromOrServer = objPrxUDPClient.getResponse();
					byte [] bytesFromServ = respFromOrServer.getBytes();
					int lenOfbyteFromServ = bytesFromServ.length;
					
					//Encapsulate the response for the client
					byte [] newResToClient = new byte[1+lenOfbyteFromServ];
					newResToClient[0] = (byte)lenOfbyteFromServ;
					for(int i = 0; i<lenOfbyteFromServ; ++i)
						newResToClient[i+1] = bytesFromServ[i];
						
					//Send the response from the Origin server to Client
					DatagramPacket objSendPacket = new DatagramPacket(newResToClient, newResToClient.length, IPOfClient, portOfClient);
					tempUDPSock = new DatagramSocket();
					tempUDPSock.send(objSendPacket);
					
					//Close the UDP socket responsible for sending the packet to the client 
					tempUDPSock.close();
					tempUDPSock.disconnect();
			}
			catch(Exception e2){
					System.out.println("");
			}
					
		}
	}
	
}











