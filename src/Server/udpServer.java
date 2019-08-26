package Server;

import java.io.IOException;  
import java.net.*;
import Server.Dictionary;
public class udpServer {
	public void start() {
		DatagramSocket serverSocket = null;
		Dictionary dic = new Dictionary();
		try
		{
			serverSocket = new DatagramSocket(1252);
			byte[] receiveData = new byte[2080];
			while(true){
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                reactThread rt = new reactThread(dic,receivePacket,serverSocket);
                rt.run();
                receiveData = new byte[2080];
             }
		}
		catch(SocketException e)
		{
			System.out.println("Socket: " + e.getMessage());
			
		}
		catch(IOException e)
		{
			System.out.println("Socket: " + e.getMessage());
		}
		finally 
		{
			if(serverSocket != null) 
				serverSocket.close();
		}
    
	}
	public static void main(String args[]) {
		udpServer server = new udpServer();
		server.start();
	}
		
}

class reactThread extends Thread{
	Dictionary dic;
	String index;
	String str;
	DatagramPacket dp;
	String opeList[];
	DatagramSocket serverSocket;
	public reactThread(Dictionary dic, DatagramPacket dp, DatagramSocket serverSocket) {
		this.dic = dic;
		this.dp = dp;
		//Delete the useless space in the Clinet's input.
        opeList = new String(dp.getData()).trim().split(" ");
		this.serverSocket = serverSocket;
	}
	public String getExplanation() {
		String res ="";
		for(int i=2;i<opeList.length; i++) {
			res += opeList[i]+" ";
		}
		return res;
	}
	public void run() {
		if(this.opeList[0].equals("ADD")) {
			index = opeList[1];
			str = this.getExplanation();
			String response = dic.Add(index, str);
			byte[] sendData = response.getBytes();
			InetAddress IPAddress = dp.getAddress();
			int port = dp.getPort();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			try {
				serverSocket.send(sendPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(this.opeList[0].equals("SEA")) {
			index = opeList[1];
			String response = dic.Search(index);
			System.out.println("Search for: "+index);
			byte[] sendData = response.getBytes();
			InetAddress IPAddress = dp.getAddress();
			int port = dp.getPort();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			try {
				serverSocket.send(sendPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(this.opeList[0].equals("DEL")){
			index = opeList[1];
			String response = dic.Delete(index);
			byte[] sendData = response.getBytes();
			InetAddress IPAddress = dp.getAddress();
			int port = dp.getPort();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			try {
				serverSocket.send(sendPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			String response = "Please input correct Operation: ADD/SEA/DEL";
			byte[] sendData = response.getBytes();
			InetAddress IPAddress = dp.getAddress();
			int port = dp.getPort();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			try {
				serverSocket.send(sendPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}