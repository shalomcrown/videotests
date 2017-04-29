package tryjavacv;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

public class GrabberShow implements Runnable {
	final static int INTERVAL = 40;/// you may use interval
	static CanvasFrame canvas = new CanvasFrame("JavaCV player");

	public GrabberShow() {
		canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
	}


	public static void ffmpegVideoSource() {
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

							watchdog = new ExecuteWatchdog(20 * 1000);
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

							watchdog = new ExecuteWatchdog(20 * 1000);
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

	public static void play() {

		try {
			FrameGrabber frameGrabber = FFmpegFrameGrabber.createDefault("udp://0.0.0.0:4443"); // FFmpegFrameGrabber(file.getAbsolutePath());

			frameGrabber.setVideoOption("overrun_nonfatal", "1");
			frameGrabber.setNumBuffers(1024);
			Frame frame = null;

			int lastWidth = 0;
			int lastHeight = 0;


			frameGrabber.start();

			while (true) {
				try {
					frame = frameGrabber.grab();

					if (frame == null || frame.imageChannels == 0 || frame.imageDepth == 0) {
						System.out.println("!!! Failed cvQueryFrame");
						break;
					}

					if (lastWidth != frame.imageWidth || lastHeight != frame.imageHeight) {
						canvas.setCanvasSize(frame.imageWidth, frame.imageHeight);

						lastWidth = frame.imageWidth;
						lastHeight = frame.imageHeight;
					}

					canvas.showImage(frame);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		ffmpegVideoSource();

		play();
		// convert(new File("/dev/video0"));
	}

	public static void main(String[] args) {
		GrabberShow gs = new GrabberShow();
		Thread th = new Thread(gs);
		th.start();
	}
}