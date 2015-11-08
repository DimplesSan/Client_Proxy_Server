import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable{
	
	
	private ServerSocket tcpServerSocket;
	private static boolean shutdownFlag;
	
	TCPServer(int iTCPPort){
		
		try {
			
			shutdownFlag = false;
			tcpServerSocket = new ServerSocket(iTCPPort);
//			System.out.println("Server Constructor + Port :"+iTCPPort);
		} catch (IOException e) {
			
			System.out.println("TCPSever-Constructor-Error:"+e.getMessage());
		}
	}
	
	public static void shutdownTCPServer(){
		shutdownFlag = true;
	}
	
	
	@Override
	public void run() {
		
		System.out.println("Server alive at TCP port");
		//Thread must be alive continuously
		while(true && !TCPServer.shutdownFlag){
			
			try {
				
				//listen for clients and accept a connection
				Socket clientSocket = tcpServerSocket.accept();
				
				//process message from TCP client
				processTCPMsg(clientSocket);
				clientSocket.close();
				
			} catch (IOException e){ 
				System.out.println("TCPSever-Run()-Error: "+e.getMessage());
			}
		}
		
		//Release the port
		if(TCPServer.shutdownFlag){
			try {
				tcpServerSocket.close();
				
			} catch (IOException e) {
				System.out.println("TCPSever-Run()-ShutdownBlock-Error: "+e.getMessage());
			}
		}
		
		
	}
	
	
	public void processTCPMsg(Socket clientSocket){
		
		try {
			
			BufferedInputStream clientInputStream = new BufferedInputStream(clientSocket.getInputStream());
			BufferedOutputStream clientOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
			
			//Read From Stream
			int msgLen = (int)clientInputStream.read();
			byte [] msg = new byte[msgLen];
			clientInputStream.read(msg);
			String sMsg = new String(msg);
			
			//Perform Appropriate action and create return message
			byte [] retByteMsg = ServerMain.performActionAndNotify(sMsg);
			
			//Send the return message to the client
			clientOutputStream.write(retByteMsg);
			clientOutputStream.flush();
			
			clientOutputStream.close();
			clientInputStream.close();
			
		} catch (IOException e) {
			System.out.println("TCPSever-processTCPMsg()-Error: "+e.getMessage());
		}
	}
	
	

	

	
}
