package tryjavacv;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

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
                    frame = new JFrame("Video test") {
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
		VideoCapture cap = new VideoCapture();
		cap.open(source);
		cap.set(Videoio.CAP_PROP_BUFFERSIZE, 1024);
		cap.set(Videoio.CAP_PROP_CONVERT_RGB, 1);


		Mat mat = new Mat();


		while (true) {
			try {

				cap.read(mat);

				int w = mat.cols();
				int h = mat.rows();

				if (w == 0 || h == 0) {
					continue;
				}

				byte[] data = new byte[w * h * 3];
				mat.get(0, 0, data);

				BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);

				final byte[] targetPixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
				System.arraycopy(data, 0, targetPixels, 0, data.length);

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
