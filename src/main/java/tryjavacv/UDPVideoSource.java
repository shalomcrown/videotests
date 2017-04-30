package tryjavacv;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;

public class UDPVideoSource {

	public UDPVideoSource() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (true) {
						CommandLine cmdLine;
						ExecuteWatchdog watchdog;
						Executor executor;
						try {
							System.out.println("Start standard scale FFMPEG");
							cmdLine = CommandLine.parse("ffmpeg -i /dev/video0 -f mpegts udp://0.0.0.0:4443");

							watchdog = new ExecuteWatchdog(10 * 1000);
							executor = new DefaultExecutor();
							executor.setExitValue(1);
							executor.setWatchdog(watchdog);
							executor.execute(cmdLine);
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							System.out.println("Start altered scale FFMPEG");
							cmdLine = CommandLine.parse("ffmpeg -i /dev/video0 -vf scale=240:200 -f mpegts udp://0.0.0.0:4443");

							watchdog = new ExecuteWatchdog(10 * 1000);
							executor = new DefaultExecutor();
							executor.setExitValue(1);
							executor.setWatchdog(watchdog);
							executor.execute(cmdLine);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}, "FFMPEG runner").start();
	}

}
