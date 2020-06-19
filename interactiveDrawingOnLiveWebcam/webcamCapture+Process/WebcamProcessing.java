import javax.swing.SwingUtilities;

/**
 * Fun with the webcam, built on JavaCV
 * Just applies one of our image processing methods to the webcam image
 * 
 */
public class WebcamProcessing extends Webcam {
	private ImageProcessor proc;	// handles image processing
	
	public WebcamProcessing() {
		proc = new ImageProcessor(null);
	}
	
	/**
	 * Use proc to change colors (try something more fun yourself)
	 */
	@Override
	public void processImage() {
		proc.setImage(image);
		proc.scaleColor(0.5, 0.5, 1.5);
		image = proc.getImage();
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new WebcamProcessing();
			}
		});
	}
}
