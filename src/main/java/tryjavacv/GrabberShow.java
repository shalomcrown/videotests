package tryjavacv;

import java.time.Duration;
import java.time.Instant;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

public class GrabberShow implements Runnable {
	final static int INTERVAL = 40;/// you may use interval
	static CanvasFrame canvas = new CanvasFrame("JavaCV player");
	static FrameGrabber frameGrabber;
	static int lastWidth = -1;
	static int lastHeight = -1;
	static Instant lastGrabTime = null;


	public GrabberShow() {
		canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
	}


	public static FrameGrabber restartGrabber() throws Exception {
		System.out.println("Restart grabber");

		if (frameGrabber != null) {
			frameGrabber.close();
		}


		frameGrabber = FFmpegFrameGrabber.createDefault("udp://0.0.0.0:4443"); // FFmpegFrameGrabber(file.getAbsolutePath());

		frameGrabber.setVideoOption("overrun_nonfatal", "1");
		frameGrabber.setNumBuffers(1024);

		lastGrabTime = null;
		lastWidth = lastHeight = -1;

		frameGrabber.start();

		return frameGrabber;
	}


	//===================================================================

	public static void play() {

		try {
			restartGrabber();
			Frame frame = null;


			while (true) {
				try {
					frame = frameGrabber.grab();
					Instant grabTime = Instant.now();
					long interval = 0;

					if (lastGrabTime != null) {
						interval = Duration.between(lastGrabTime, grabTime).toMillis();
					}

					lastGrabTime = grabTime;

					System.out.println("Grabbed " + grabTime + " Interval " + interval);

					if (frame == null) {
						System.out.println("Null frame");
						continue;
					}

					if (frame.imageChannels != 1 && frame.imageChannels != 3 && frame.imageChannels != 4) {
						System.out.format("Incorrect number of image channels: %d\n", frame.imageChannels);

						restartGrabber();
						continue;
					}


					if (frame.imageDepth < 1 || frame.imageDepth > 16) {
						System.out.format("Incorrect depth: %d\n", frame.imageDepth);

						restartGrabber();
						continue;
					}

					if (lastWidth != frame.imageWidth || lastHeight != frame.imageHeight) {
						if (lastHeight != -1 || lastWidth != -1) {
							System.out.println("Incorrect dimensions");
							restartGrabber();
							continue;
						}

						lastWidth = frame.imageWidth;
						lastHeight = frame.imageHeight;
					}


					if (interval > 120) {
						System.out.println("Too long between frames");
						restartGrabber();
						continue;
					}

					canvas.showImage(frame);

				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		new UDPVideoSource(null);

		play();
		// convert(new File("/dev/video0"));
	}

	public static void main(String[] args) {
		GrabberShow gs = new GrabberShow();
		Thread th = new Thread(gs, "Display thread");
		th.start();
	}
}