package socketServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Ying Liu
 * @mail yliu12@bsu.edu
 * @date 12/03/2016
 */
public class TimerReadFile extends TimerTask {
	private String PATH = "/Users/yliu12/Desktop/ArduinoLogger/";

	public void run() {
		// getPossibleNextLocation();
		getPossibleNextLocation();
		System.out.println("nextLocation: " + Server.Next_Location_From_History_Data);
		System.out.println("possibility: " + Server.Next_Location_Possibility);

	}

	private void getPossibleNextLocation() {
		long yCount = 0;
		long gCount = 0;
		long nCount = 0;

		List<String> fileList = getFileList(PATH);
		System.out.println(fileList.toString());
		BufferedReader reader = null;

		for (int i = 0; i < fileList.size() - 1; i++) {
			File file = new File(fileList.get(i));

			try {
				System.out.println("Flie ：" + file.getName());

				String tempString = null;
				reader = new BufferedReader(new FileReader(file));

				boolean bFlag = false;

				// readline loop, stop on null
				while ((tempString = reader.readLine()) != null) {

					String signal = tempString.substring(tempString.length() - 1, tempString.length());
					// System.out.println(signal);

					if (!bFlag) {
						if (signal.equals("B")) {
							bFlag = true;
							continue;
						}
					} else {
						if (signal.equals("B")) {
							continue;
						} else {
							switch (signal) {
							case "Y":
								yCount++;
								break;
							case "G":
								gCount++;
								break;
							case "N":
								nCount++;
								break;
							}
							bFlag = false;
						}
					}
				}
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("yCount: " + yCount);
		System.out.println("gCount: " + gCount);
		System.out.println("nCount: " + nCount);

		double total = (double) yCount + gCount + nCount;

		String nextLc = total == 0 ? null
				: (yCount >= gCount ? yCount >= nCount ? "Y" : "N" : gCount >= nCount ? "G" : "N");

		double max = Long.parseLong(
				(yCount >= gCount ? yCount >= nCount ? yCount : nCount : gCount >= nCount ? gCount : nCount) + "");

		double possiblity = (double) (total == 0 ? 0 : max / total);


		Server.Next_Location_From_History_Data = nextLc;
		Server.Next_Location_Possibility = Double.parseDouble(possiblity + "");

	}

	public List<String> getFileList(String path) {
		List<String> list = new ArrayList<String>();
		try {
			File file = new File(path);
			String[] filelist = file.list();
			for (String fileName : filelist) {
				if (fileName.indexOf("LOG_BY_SECOND") > -1)
					list.add(path + fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

//	public static void main(String[] args) {
//		Integer cacheTime = 1000 * 60 * 60;// an hour
//		Timer timer = new Timer();
//
//		timer.schedule(new TimerReadFile() {
//		}, 1000, cacheTime);
//	}
}