import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPClient {
	
	private	Socket oTCPSocket;
	private BufferedOutputStream oBuffOpStream;
	private BufferedInputStream oBuffIpStream;
	
	TCPClient(String sIpAddr, int iTCPPort){
		
		try {
			
			oTCPSocket = new Socket(sIpAddr, iTCPPort);
			oBuffOpStream = new BufferedOutputStream(oTCPSocket.getOutputStream(),1024);
			oBuffIpStream = new BufferedInputStream(oTCPSocket.getInputStream(), 1024);
			
		} catch (IOException e) {
			System.out.println("TCPClient-Constructor-Error:"+e.getMessage());
		}
	}
	
	public Socket getSocketConnection(){
		return oTCPSocket;	
	}
	
	public void sendMessage(byte [] msg) throws IOException{

		oBuffOpStream.write(msg);
		oBuffOpStream.flush();
	}
	
	public String getResponse() throws IOException{
		
		//Get the length
		int msgLen = (int) oBuffIpStream.read();
		System.out.println("Msg Len received "+ msgLen);
		byte [] byteResp = new byte[msgLen];
		oBuffIpStream.read(byteResp);
		return new String(byteResp);
		
	}
	
	public void severConnection(){
		
		try {
			oBuffOpStream.close();
			oBuffIpStream.close();
			oTCPSocket.close();
		} catch (IOException e) {
			System.out.println("TCPClient-severConnection()-Error:"+e.getMessage());
		}

		
	}
	
}
