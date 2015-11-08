
public class ProxyServerMain{

	private ProxyServerTCP oProxyTCP;
	private ProxyServerUDP oProxyUDP;
	
	private static String ipOriginServer;
	private static int tcpPortOrServer = 0;
	private static int updPortOrServer = 0;
	private static boolean useTCPChannelToOrServ = false;
	
	public ProxyServerMain(int iTCPPort, int iUDPPort, String [] args ) {
		
		//Initialize with the ip of origin server
		ipOriginServer = args[1];
		System.out.println("IP Address of origin server: "+ ipOriginServer);
		
		//Get the channel to communicate with the origin server
		ProxyServerMain.getPortOfOrginServer(args);
		
		//Start the proxy on TCP port
		oProxyTCP = new ProxyServerTCP(iTCPPort);
		
		//Start the proxy on the UDP port
		oProxyUDP = new ProxyServerUDP(iUDPPort);
		
		
	}

	public static boolean isUseTCPChannelToOrServ() {
		return useTCPChannelToOrServ;
	}

	public static void setUseTCPChannelToOrServ(boolean useTCPChannelToOrServ) {
		ProxyServerMain.useTCPChannelToOrServ = useTCPChannelToOrServ;
	}

	public static void setOriginServerPorts(String[] args){
		
		updPortOrServer = 0;
		for(int i =0; i<= args.length-1; ++i ){
			if(args[i].equals("--proxy-udp")){
				updPortOrServer =  Integer.parseInt(args[i+1]);
				System.out.println("UDP port of the origin server: "+ updPortOrServer);
				break;
			}	
		}
		
		tcpPortOrServer = 0;
		for(int i =0; i<= args.length-1; ++i ){
			if(args[i].equals("--proxy-tcp")){
				tcpPortOrServer = Integer.parseInt(args[i+1]);
				System.out.println("TCP port of the origin server: " + tcpPortOrServer);
				break;
			}
		}
	}
	
	public static void getPortOfOrginServer(String [] args){
		
		for(int i =0; i<= args.length-1; ++i ){
			if(args[i].equals("-u")){
				System.out.println("Proxy will contact Origin server on UDP channel");
			}	
		}
		
		for(int i =0; i<= args.length-1; ++i ){
			if(args[i].equals("-t")){
				System.out.println("Proxy will contact Origin server on TCP channel");
				useTCPChannelToOrServ = true;
			}
		}		
	}
	
	public static String getIpOriginServer() {
		return ipOriginServer;
	}

	public static void setIpOriginServer(String ipOriginServer) {
		ProxyServerMain.ipOriginServer = ipOriginServer;
	}

	public static int getTcpPortOrServer() {
		return tcpPortOrServer;
	}

	public static void setTcpPortOrServer(int tcpPortOrServer) {
		ProxyServerMain.tcpPortOrServer = tcpPortOrServer;
	}

	public static int getUpdPortOrServer() {
		return updPortOrServer;
	}

	public static void setUpdPortOrServer(int updPortOrServer) {
		ProxyServerMain.updPortOrServer = updPortOrServer;
	}

	
	public void startProxyServer(){
		
		Thread proxyTCPThread = new Thread(oProxyTCP);
		Thread proxyUDPThread = new Thread(oProxyUDP);
		
		proxyTCPThread.start();
		proxyUDPThread.start();
	}
}
