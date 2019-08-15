package Socket;
import java.net.*;
import java.io.*;
/*
 * This UDP server program wait for the client's requests and then accepts the datagram
 * and send back the same message is given below.
 * 
 * DatagramPacket : |msg| |length| |host| |serverPort|
 */

public class UDPServer {
	public static void main(String args[]) {
		DatagramSocket aSocket = null;
		if(args.length<1) {
			System.out.println("Usage: java UDPServer<Port Number>");
			System.exit(1); 
		}
		try {
			int socket_no = Integer.valueOf(args[0]).intValue();
			aSocket = new DatagramSocket(socket_no);
			byte [] buffer = new byte[1000];
			while(true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				DatagramPacket reply = new DatagramPacket(request.getData(),request.getLength(),request.getAddress(),request.getPort());
				aSocket.send(reply);
			}
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
