import java.io.*;
import java.net.*;

public class TCPClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// establish connection
		Socket MySocket  = null;
		try {
		MySocket = new Socket("localhost", 59000);
		} catch (UnknownHostException e) {
			System.out.println("Sorry, I don't know localhost");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Sorry, I/O error has occurred while trying to establish a connection.");
			System.exit(1);
		}
		
		// get output and input streams
		OutputStream os = null;
		try {
			os = MySocket.getOutputStream();
		} catch (IOException e) {
			System.out.println("Sorry, I/O error has occurred while trying to obtain output stream.");
			System.exit(1);
		}
		
		InputStream is = null;
		try {
			is = MySocket.getInputStream();
		} catch (IOException e) {
			System.out.println("Sorry, I/O error has occurred while trying to obtain input stream.");
			System.exit(1);
		}

		// use advanced streams for easier manipulation
		PrintWriter out = new PrintWriter(os, true);
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		
		// send data
		out.println("Time");
		
		// receive response
		String T = null;
		try {
			T = in.readLine();
			System.out.println("according to the server, the time is: " +T);
		} catch (IOException e) {
			System.out.println("Sorry, I/O error has occurred while reading from input stream.");
		}
		
		out.close();
		try {
			in.close();
		} catch (IOException e) {
			System.out.println("Problem closing input stream");
			System.exit(1);
		}
		
		try {
			MySocket.close();
		} catch (IOException e) {
			System.out.println("Problem closing socket");
			System.exit(1);
		}
	}

}
