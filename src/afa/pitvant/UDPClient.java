package afa.pitvant;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.content.SharedPreferences;
import android.util.Log;

public class UDPClient extends Thread {

	private InetAddress serverAddr;
	private DatagramSocket socket;
	private DatagramPacket packet;
	private String coords;
	private String TARGETIP;
	private int TARGETPORT;
	SharedPreferences prefs;
	float longitude=0, latitude=0;
	
	/**
	 * Constructor for the UDPClient class. Initializes it's target's IP, port and InetAddress.
	 * @param targetIP a string containing the IP for future use.
	 * @param targetPORT an integer containing the port for future use.*/
	public UDPClient (String targetIP, int targetPORT){
		Log.d("Contructor", "Created");
		/* initialize IP and Port for 1st time use */
		this.TARGETIP = targetIP;
		this.TARGETPORT = targetPORT;
		Log.d("Contructor:PORT", String.valueOf(TARGETPORT));
		Log.d("Contructor:IP", TARGETIP);
		
		/* initializes InetAddress and socket */
		try {
			serverAddr = InetAddress.getByName(TARGETIP);
			socket = new DatagramSocket();			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Changes the IP and port for future use and updates de InetAddress for the thread.
	 * @param targetIP a string containing the IP for future use.
	 * @param targetPORT an integer containing the port for future use.*/
	public void setAddr (String targetIP, int targetPORT){
		this.TARGETIP = targetIP; //class IP and port are now set to values received
		this.TARGETPORT = targetPORT;
		
		try {
			serverAddr = InetAddress.getByName(TARGETIP); //updates InetAddress
			Log.d("setAddr", "New IP and port set.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/**
	 * Changes the current coordinates that the thread should use.
	 * @param longitude a float containing the current location's longitude.
	 * @param latitude a float containing the current location's lotitude.*/
	public void setCoords (float longitude, float latitude){
		this.longitude=longitude;
		this.latitude=latitude;
		run();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		/* Sends a UDP message to target address with the formart longitude:latitude */
		try {
			coords = String.valueOf(longitude)+":"+String.valueOf(latitude);
			Log.d("SendUDP Coords:", coords);
			byte[] data = coords.getBytes();
			
			/* preparation o DatagramPacket with formated String, server address and server port */
			packet = new DatagramPacket(data, data.length, serverAddr, TARGETPORT);
			
			Log.d("SendUDP DataLen", String.valueOf(data.length));
			Log.d("SendUDP DataIP", serverAddr.getHostAddress());
			Log.d("SendUDP DataPort", String.valueOf(TARGETPORT));
			Log.d("SendUDP", "Sending");
			
			socket.send(packet);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
