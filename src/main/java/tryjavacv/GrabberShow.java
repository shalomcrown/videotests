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



	public static void play(String file) {

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

					if (frame == null) {
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
		play("/home/shalomc/outputs/DS9/78_2017-04-27_16-34-27S.ts");
		// convert(new File("/dev/video0"));
	}

	public static void main(String[] args) {
		GrabberShow gs = new GrabberShow();
		Thread th = new Thread(gs);
		th.start();
	}
}