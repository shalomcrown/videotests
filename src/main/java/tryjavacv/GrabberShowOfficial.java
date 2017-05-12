package tryjavacv;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class GrabberShowOfficial implements Runnable {

	static {
		nu.pattern.OpenCV.loadShared();
		System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
	}


	public static UDPVideoSource sourceProvider;
	String source;
	JFrame frame;
	BufferedImage latestFrame;


	public GrabberShowOfficial(String source) {
		this.source = source;

		EventQueue.invokeLater(new Runnable() {
            @Override
			public void run() {
                try {
                    JFrame frame = new JFrame("Video test") {
						private static final long serialVersionUID = 358723437047700939L;

						@Override
						public void paint(java.awt.Graphics g) {
                            if (latestFrame != null) {
                            	g = getContentPane().getGraphics();
                            	g.drawImage(latestFrame, 0, 0, this);
                            } else {
                            	super.paint(g);
                            }
                    	};
                    };

                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setBounds(100, 100, 650, 490);
                    JPanel panel = new JPanel();
                    frame.setContentPane(panel);
                    panel.setBorder(new EmptyBorder(5, 5, 5, 5));
                    panel.setLayout(null);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        new Thread(this).start();
	}

	@Override
	public void run() {
		VideoCapture cap = new VideoCapture("udp://@0.0.0.0:4443");

		Mat mat = new Mat();


		while (true) {
			try {
				Thread.sleep(300);

				if (! cap.read(mat)) {
					System.out.println("No frame received");
					continue;
				}

				int w = mat.cols();
				int h = mat.rows();

				System.out.printf("Received frame %dx%d\n", w,h);

				if (w == 0 || h == 0) {
					continue;
				}

				byte[] data = new byte[w * h * 3];
				mat.get(0, 0, data);

				BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
				img.getRaster().setDataElements(0, 0, w, h, data);

				latestFrame = img;
				if (frame != null && frame.isVisible()) {
					frame.repaint();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public static void main(String[] args) {
		sourceProvider = new UDPVideoSource(null);
		new GrabberShowOfficial(sourceProvider.getOutputUrl());
	}

}
