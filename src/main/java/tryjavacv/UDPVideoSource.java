package tryjavacv;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;

import com.sun.jna.Platform;

public class UDPVideoSource {

	public static final long TIME_BETWEEN_PROCESSES = 150;


	public UDPVideoSource() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (true) {
						CommandLine cmdLine = null;
						ExecuteWatchdog watchdog;
						Executor executor;
						try {
							System.out.println("Start standard scale FFMPEG");

							if (Platform.isLinux()) {
								cmdLine = CommandLine.parse("ffmpeg -i /dev/video0 -f mpegts udp://0.0.0.0:4443");
							} else if (Platform.isWindows()) {
								cmdLine = CommandLine.parse("ffmpeg -f dshow -i video=\"Integrated Camera\" -f mpegts udp://0.0.0.0:4443");

							} else {
								throw new Exception("Unknown platform");
							}

							watchdog = new ExecuteWatchdog(10 * 1000);
							executor = new DefaultExecutor();
							executor.setExitValue(1);
							executor.setWatchdog(watchdog);
							executor.execute(cmdLine);
						} catch (Exception e) {
							e.printStackTrace();
						}

						Thread.sleep(TIME_BETWEEN_PROCESSES);

						try {
							System.out.println("Start altered scale FFMPEG");
							if (Platform.isLinux()) {
								cmdLine = CommandLine.parse("ffmpeg -i /dev/video0 -vf scale=240:200 -f mpegts udp://0.0.0.0:4443");
							} else if (Platform.isWindows()) {
								cmdLine = CommandLine.parse("ffmpeg -f dshow -i video=\"Integrated Camera\" -vf scale=240:200 -f mpegts udp://0.0.0.0:4443");
							} else {
								throw new Exception("Unknown platform");
							}

							watchdog = new ExecuteWatchdog(10 * 1000);
							executor = new DefaultExecutor();
							executor.setExitValue(1);
							executor.setWatchdog(watchdog);
							executor.execute(cmdLine);
						} catch (Exception e) {
							e.printStackTrace();
						}

						Thread.sleep(TIME_BETWEEN_PROCESSES);
					}

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}, "FFMPEG runner").start();
	}

}
