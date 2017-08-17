package tryjavacv;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;

import com.sun.jna.Platform;

public class UDPVideoSource {

	public static final long TIME_BETWEEN_PROCESSES = 150;

	String outputUrl = "udp://127.0.0.1:4443";
	String readURL = "udp://127.0.0.1:4443";

	public String getOutputUrl() {
		return readURL;
	}

	public void setOutputUrl(String outputUrl) {
		this.outputUrl = outputUrl;
	}

	public UDPVideoSource(String sink) {
		if (sink == null) {
			sink = outputUrl;
		} else {
			outputUrl = sink;
		}

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
								cmdLine = CommandLine.parse("ffmpeg -i /dev/video0 -f mpegts " + outputUrl);
							} else if (Platform.isWindows()) {
								cmdLine = CommandLine.parse("ffmpeg -f dshow -i video=\"Integrated Webcam\" -f mpegts " + outputUrl);

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
								cmdLine = CommandLine.parse("ffmpeg -i /dev/video0 -vf scale=240:200 -f mpegts " + outputUrl);
							} else if (Platform.isWindows()) {
								cmdLine = CommandLine.parse("ffmpeg -f dshow -i video=\"Integrated Webcam\" -vf scale=240:200 -f mpegts " + outputUrl);
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

	public static void main(String[] args) {
		new UDPVideoSource(null);

		try {
			Thread.sleep(10000000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
