import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClient {
	
	private DatagramSocket oUDPClient;
	private	DatagramPacket oUDPSendPacket,oUDPReceivePacket;
	public byte[] receiveBuffer;// Standard receive buffer
	
//	private BufferedOutputStream oBuffOpStream;
//	private BufferedInputStream oBuffIpStream;
	
	public UDPClient(String ipAddr, int port, byte [] msg){
		
		try {
			
			
			oUDPClient = new DatagramSocket();
			oUDPSendPacket = new DatagramPacket(msg, msg.length, InetAddress.getByName(ipAddr), port);
			receiveBuffer = new byte[1024*5];
			oUDPReceivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			
			
		} catch (UnknownHostException | SocketException e) {
			
			System.out.println("UDPClient-Constructor-Error:"+e.getMessage());
		}
	}
	
	public void sendMsg() throws IOException{
			
			oUDPClient.send(oUDPSendPacket);
			System.out.println("Message sent to "+oUDPSendPacket.getAddress() + " "+ oUDPSendPacket.getPort());
	}
	
	
	public String getResponse() throws IOException{
		
		oUDPClient.receive(oUDPReceivePacket);
		return new String (oUDPReceivePacket.getData());
		
	}
	
	
}
