package lab11;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.*;

/**
 * The main() program in this class is designed to read requests from a Web
 * browser and display the requests on standard output. The program sets up a
 * listener on port 50505. It can be contacted by a Web browser running on the
 * same machine using a URL of the form
 * http://localhost:505050/path/to/resource.html This method does not return any
 * data to the web browser. It simply reads the request, writes it to standard
 * output, and then closes the connection. The program continues to run, and the
 * server continues to listen for new connections, until the program is
 * terminated (by clicking the red "stop" square in Eclipse or by Control-C on
 * the command line).
 */
public class ReadRequest {

	/**
	 * The server listens on this port. Note that the port number must be greater
	 * than 1024 and lest than 65535.
	 */
	private final static int LISTENING_PORT = 50505;

	/**
	 * Main program opens a server socket and listens for connection requests. It
	 * calls the handleConnection() method to respond to connection requests. The
	 * program runs in an infinite loop, unless an error occurs.
	 * 
	 * @param args ignored
	 */
	public static void main(String[] args) {
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(LISTENING_PORT);
		} catch (Exception e) {
			System.out.println("Failed to create listening socket.");
			return;
		}
		System.out.println("Listening on port " + LISTENING_PORT);
		try {
			while (true) {
				Socket connection = serverSocket.accept();
				System.out.println("\nConnection from " + connection.getRemoteSocketAddress());
				ConnectionThread thread = new ConnectionThread(connection);
				thread.start();
			}
		} catch (Exception e) {
			System.out.println("Server socket shut down unexpectedly!");
			System.out.println("Error: " + e);
			System.out.println("Exiting.");
		}
	}

	/**
	 * Handle commuincation with one client connection. This method reads lines of
	 * text from the client and prints them to standard output. It continues to read
	 * until the client closes the connection or until an error occurs or until a
	 * blank line is read. In a connection from a Web browser, the first blank line
	 * marks the end of the request. This method can run indefinitely, waiting for
	 * the client to send a blank line. NOTE: This method does not throw any
	 * exceptions. Exceptions are caught and handled in the method, so that they
	 * will not shut down the server.
	 * 
	 * @param connection the connected socket that will be used to communicate with
	 *                   the client.
	 */
	private static void handleConnection(Socket connection) {
		try {
			Scanner in = new Scanner(connection.getInputStream());
			String line;
			if (in.hasNextLine()) {
				line = in.nextLine();
				if (line.trim().length() == 0)
					return;
				String[] splited = line.split("\\s+");
				if (splited.length == 3) {
					if (!splited[0].equals("GET")) {
						sendErrorResponse(501,connection.getOutputStream());
						return;
					}
					if (!isPathValid(splited[1])) {
						sendErrorResponse(404,connection.getOutputStream());
						return;
					}
					File file = new File("C:\\Users\\datre\\OneDrive\\Escritorio\\Files" + splited[1]);
					if (!file.exists()) {
						System.out.print("No file in here");
						sendErrorResponse(404,connection.getOutputStream());
						return;
					}
					PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
					out.print("HTTP/1.1 200 OK" + "\r\n");
					out.print("Connection : close" + "\r\n");
					out.print("Content-Type : " + getMimeType(splited[1]) + "\r\n");
					out.print("Content-Length : " + file.length() + "\r\n");
					out.flush();
					sendFile(file,connection.getOutputStream());
				} else {
					System.out.println("Error in the Header");
					return;
				}
			}
		} catch (Exception e) {
			System.out.println("Error while communicating with client: " + e);
		} finally { // make SURE connection is closed before returning!
			try {
				connection.close();
			} catch (Exception e) {
			}
			System.out.println("Connection closed.");
		}
	}
	

    private static class ConnectionThread extends Thread {
       Socket connection;
       ConnectionThread(Socket connection) {
          this.connection = connection;
       }
       public void run() {
          handleConnection(connection);
       }
    }
	
	static void sendErrorResponse(int errorCode, OutputStream socketOut) {
		PrintWriter out = new PrintWriter(socketOut, true);
		switch(errorCode) {
			case 404:
				out.print("HTTP/1.1 404 Not Found\r\n");
				out.print("Connection : close\\r\\n");
				out.print("Content-Type : text/plain\\r\\n");
				out.print("<html><head><title>Error</title></head><body>"
						+ "<h2>Error: 404 Not Found</h2>"
						+ "<p>The resource that you requested does not exist on this server.</p>"
						+ "</body></html>");
				break;
			case 501:
				out.print("HTTP/1.1 501 Not Implemented\r\n"
						+ "Connection : close\r\n" 
						+ "Content-Type : text/html\r\n"
						+ "<html><head><title>Error</title></head><body>\r\n"
						+ "<h2>Error: 501 Not Implemented</h2>\r\n"
						+ "<p>the method in the request in anything other than \"GET\".</p>\r\n"
						+ "</body></html>");
				break;
			default:
				System.out.print("Error");
		}
	}

	public static boolean isPathValid(String path) {

		try {

			Paths.get(path);

		} catch (InvalidPathException ex) {
			return false;
		}

		return true;
	}

	private static void sendFile(File file, OutputStream socketOut) throws IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(file));
		OutputStream out = new BufferedOutputStream(socketOut);
		while (true) {
			int x = in.read(); // read one byte from file
			if (x < 0)
				break; // end of file reached
			out.write(x); // write the byte to the socket
		}
		out.flush();
	}

	private static String getMimeType(String fileName) {
		int pos = fileName.lastIndexOf('.');
		if (pos < 0) // no file extension in name
			return "x-application/x-unknown";
		String ext = fileName.substring(pos + 1).toLowerCase();
		if (ext.equals("txt"))
			return "text/plain";
		else if (ext.equals("html"))
			return "text/html";
		else if (ext.equals("htm"))
			return "text/html";
		else if (ext.equals("css"))
			return "text/css";
		else if (ext.equals("js"))
			return "text/javascript";
		else if (ext.equals("java"))
			return "text/x-java";
		else if (ext.equals("jpeg"))
			return "image/jpeg";
		else if (ext.equals("jpg"))
			return "image/jpeg";
		else if (ext.equals("png"))
			return "image/png";
		else if (ext.equals("gif"))
			return "image/gif";
		else if (ext.equals("ico"))
			return "image/x-icon";
		else if (ext.equals("class"))
			return "application/java-vm";
		else if (ext.equals("jar"))
			return "application/java-archive";
		else if (ext.equals("zip"))
			return "application/zip";
		else if (ext.equals("xml"))
			return "application/xml";
		else if (ext.equals("xhtml"))
			return "application/xhtml+xml";
		else
			return "x-application/x-unknown";
		// Note: x-application/x-unknown is something made up;
		// it will probably make the browser offer to save the file.
	}
}
