import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPServer implements Runnable {
	
	private DatagramSocket udpSocket;
	private static boolean shutdownFlag;
	
	UDPServer(int iUDPPort){
		
		try {
			udpSocket = new DatagramSocket(iUDPPort);
	
		} catch (SocketException e) {
			System.out.println("UDPSever-Constructor-Error:"+e.getMessage());
		}
	}
	
	
	public static void shutdownUDPServer(){
		shutdownFlag = true;
	}
	
	@Override
	public void run() {

		System.out.println("Server alive on UDP port");
		
		//Define the buffer array
		byte[] arrByteBuffer;
		
		
		//Keep the server alive
		while(true && !UDPServer.shutdownFlag){
			
			arrByteBuffer = new byte[1024];
			//Define the datagram packet using the buffer defined above
			DatagramPacket objRecPacket = new DatagramPacket(arrByteBuffer, arrByteBuffer.length);
			try {
				
				//Receive the incoming message in the datagram packet
				udpSocket.receive(objRecPacket);
				
				//Process request and send the packet
				udpSocket.send(processUDPMsg(udpSocket, objRecPacket));
				
			} catch (IOException e) {
				System.out.println("UDPSever-run()-Error:"+e.getMessage());
			}
			
		}
		
		if(UDPServer.shutdownFlag){
				udpSocket.close();
				udpSocket.disconnect();
		}
			
		
	}
	
	
	private DatagramPacket processUDPMsg(DatagramSocket objUDPSocket, DatagramPacket objRecPacket){
		
		byte[] arrRecMsg = objRecPacket.getData();
		int iClientPort = objRecPacket.getPort();
		InetAddress objClientIPAddr = objRecPacket.getAddress();
		byte [] tempByteArr = new byte[arrRecMsg.length];
		
		for(int i=1; i<arrRecMsg.length; ++i)
			tempByteArr[i-1] = arrRecMsg[i];

		byte[] arrRetMsg = ServerMain.performActionAndNotify(new String(tempByteArr));
		return new DatagramPacket(arrRetMsg, arrRetMsg.length, objClientIPAddr, iClientPort);
		
	}
}
