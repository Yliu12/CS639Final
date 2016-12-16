package socketServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Socket Listener Thread 
 * @author	Ying Liu 
 * @email	yliu12@bsu.edu 
 * @date	12/3/2016
 */
public class ServerThread implements Runnable {

	//

	private Socket client = null;

	public ServerThread(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		try {
			// Socket output Stream
			PrintStream out = new PrintStream(client.getOutputStream());
			// socket input Stream
			BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
			boolean flag = true;

			String curLocation = null;

			while (flag) {
				
				// Listen data from socket
				String str = "";
				Iterator<String> stream = buf.lines().iterator();

				// Process Data Line by Line
				while (stream.hasNext()) {
					String thisLine = stream.next();
					if (null == thisLine || thisLine.equals("")||thisLine.indexOf("=")>-1) {
						continue;
					}
					str = str + thisLine + "\n";
					
					// process Signal For Y
					if (thisLine.indexOf("Y") > -1) {
						thisLine = thisLine.substring(1);
						if (!thisLine.isEmpty()) {
							curLocation = "Y";
						}
					}
					
					// Process Signal For G
					if (thisLine.indexOf("G") > -1) {
						thisLine = thisLine.substring(1);

						if (!thisLine.isEmpty()) {
							curLocation = curLocation == null ? ("G") : ("B");

						}
					}
				}

				
				//stop flag && response message
				if (str == null || "".equals(str)) {
					flag = false;
				} else {

					//print in messages
					System.out.println(str);
					//System.out.println("Current Location: " + curLocation);

					
					if (null != curLocation) {
						String respMessage = checkReturnMessage(curLocation);
						System.out.println("respMessage: " + respMessage);
						if (null != respMessage) {
							out.write(respMessage.getBytes());
							out.write(("Probability:"+Server.Next_Location_Possibility).getBytes());

						}
					}

				}
			} // end Info

			// Close Connection
			out.close();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if there is a prediction need to be sent back push curLocation to
	 * Location List if needed
	 * 
	 * @param curLocation
	 * @return prediction or null
	 */
	private String checkReturnMessage(String curLocation) {
		ArrayList<String> locationList = Server.LocationList;
		
		System.out.println("locationList: " + locationList.toString());
		System.out.println("curLocation: " + curLocation);

		if (locationList.isEmpty()) {
			if (curLocation.equals("B")) {
				locationList = new ArrayList<String>();
				return Server.Next_Location_From_History_Data;
			} else {
				locationList.add(curLocation);
				return null;
			}
		} else if (locationList.size() == 1) {
			if (curLocation == "B") {
				String result = locationList.get(0) == "G" ? "Y" : "G";
				locationList.clear();
				return result;
			} else {
				locationList.set(0, curLocation);
				return null;
			}

		} else {
			System.out.println(
					" WTF Just Happened???????????????WTF Just Happened???????????????WTF Just Happened???????????????WTF Just Happened???????????????WTF Just Happened???????????????WTF Just Happened???????????????WTF Just Happened???????????????");
			return null;
		}

	}

}
