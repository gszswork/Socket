package Socket;
import java.net.*;
import java.io.*;
/*
 * This client program creates a datagram and then sends it to the UDPServer and then 
 * accpets a response.
 */
public class UDPClient {
	public static void main(String args[]) {
		DatagramSocket aSocket = null;
		if(args.length < 3) {
			//why we need three args?
			System.out.println("Usage: java UDPClient <message> <Host name> <Port number>");
			System.exit(1);
		}
		try {
			aSocket = new DatagramSocket();
			byte []m = args[0].getBytes();
			InetAddress aHost = InetAddress.getByName(args[1]);
			int serverPort = Integer.valueOf(args[2]).intValue();
			DatagramPacket request = new DatagramPacket(m,args[0].length(), aHost, serverPort);
			aSocket.send(request);
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer,buffer.length);
			aSocket.receive(reply);
			System.out.println("Reply: "+ new String(reply.getData()));
		}catch(SocketException e) {
			System.out.print("Socket:"+ e.getMessage());
		}
		catch(IOException e) {
			System.out.println("IO:"+ e.getMessage());
		}finally {
			if(aSocket != null) {
				aSocket.close();
			}
		}
	}
}
