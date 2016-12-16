package socketServer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;

/**
 * Socket Listener Thread
 * 
 * @author Ying Liu
 * @email yliu12@bsu.edu
 * @date 12/3/2016
 */
public class Server {

	public static ArrayList<String> LocationList = new ArrayList<String>();
	public static String Next_Location_From_History_Data = null;
	public static double Next_Location_Possibility;

	public static void main(String[] args) throws Exception {

		// Start timed loop for data process
		Integer cacheTime = 1000 * 60 * 60;// an hour
		Timer timer = new Timer();

		timer.schedule(new TimerReadFile() {
		}, 1000, cacheTime);

		// Start socket server at 9999
		ServerSocket server = new ServerSocket(9999);
		Socket client = null;
		boolean f = true;
		System.out.println("Socket Listener Started successfully");

		while (f) {
			// wait for connection
			client = server.accept();
			System.out.println("++++++++++++++++++++++++++++++++StartNewMessage++++++++++++++++++++++++++++++++");
			// Start a new thread for each connection
			new Thread(new ServerThread(client)).start();
		}
		server.close();

	}
}