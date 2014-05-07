import java.io.*;
import java.net.*;

public class TCPServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
				// Create a server socket
				ServerSocket server = null;
				try {
					server = new ServerSocket(59000);
					System.out.println("Listening on port 59000");
				} catch (IOException e) {
					System.out.println("Sorry, can't listen on port 59000");
					System.exit(1);
				}
				while (true) {
					// wait for incoming connection requests
					Socket client = null;
					try {
						client = server.accept();
					} catch (IOException e) {
						System.out.println("Can't establish connection with this client.");
						continue;
					}
					
					// get output and input streams and advanced streams
					OutputStream os = null;
					try {
						os = client.getOutputStream();
					} catch (IOException e) {
						System.out.println("Can't get output stream.");
						continue;
					}
					
					InputStream is=null;
					try {
						is = client.getInputStream();
					} catch (IOException e) {
						System.out.println("Can't get input stream.");
						continue;
					}
					
					PrintWriter out = new PrintWriter(os, true);
					BufferedReader in = new BufferedReader(new InputStreamReader(is));
					
					// read token
					String token = null;
					try {
						token = in.readLine();
						if (token.matches("Time"))
							// send the time
							out.println(System.currentTimeMillis( ));
						else
							System.out.println("Unknown Request: "+token);
					} catch (IOException e) {
						System.out.println("Can't read client's data.");
						continue;
					}
					
					out.close();
					
					try {
						in.close();
					} catch (IOException e1) {
						System.out.println("Can't close input stream.");
						continue;
					}
					
					try {
						client.close();
					} catch (IOException e) {
						System.out.println("Can't close connection to client.");
						continue;
					}
				}
	}
}
