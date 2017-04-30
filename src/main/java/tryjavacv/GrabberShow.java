package tryjavacv;

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


	public static void play() {

		try {
			FrameGrabber frameGrabber = FFmpegFrameGrabber.createDefault("udp://0.0.0.0:4443"); // FFmpegFrameGrabber(file.getAbsolutePath());

			frameGrabber.setVideoOption("overrun_nonfatal", "1");
			frameGrabber.setNumBuffers(1024);
			Frame frame = null;

			int lastWidth = -1;
			int lastHeight = -1;

			frameGrabber.start();

			while (true) {
				try {
					frame = frameGrabber.grab();

					if (frame == null) {
						System.out.println("Null frame");
						continue;
					}

					if (frame.imageChannels != 1 && frame.imageChannels != 3 && frame.imageChannels != 4 && frame.imageChannels != 8) {
						System.out.format("Incorrect number of image channels: %d\n", frame.imageChannels);
						continue;
					}


					if (frame.imageDepth < 1 || frame.imageDepth > 16) {
						System.out.format("Incorrect depth: %d\n", frame.imageDepth);
						continue;
					}

					if (lastWidth != frame.imageWidth || lastHeight != frame.imageHeight) {
						canvas.setCanvasSize(frame.imageWidth, frame.imageHeight);

						lastWidth = frame.imageWidth;
						lastHeight = frame.imageHeight;
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
		new UDPVideoSource();

		play();
		// convert(new File("/dev/video0"));
	}

	public static void main(String[] args) {
		GrabberShow gs = new GrabberShow();
		Thread th = new Thread(gs, "Display thread");
		th.start();
	}
}