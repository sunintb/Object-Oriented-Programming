import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Webcam-based drawing 
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * Sunint Bindra
 * CS10
 * CamPaint color implements methods in RegionFinder into GUI and performs actions based on that such as recoloring,
 * creating a blank background, and painting tracks left by an assigned paintbrush
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 */
public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece


	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting,
	 * depending on display variable ('w', 'r', or 'p')
	 * uses different drawImage type calls to set what sort of "background" is neccessary for webcam, recolored image, or painting
	 */
	@Override
	public void draw(Graphics g) {
		if (displayMode == 'w') {
			g.drawImage(image, 0, 0, null);

		}
		else if (displayMode == 'r') {
			g.drawImage(finder.getRecoloredImage(), 0, 0, null);

		}
		else if (displayMode == 'p') {
			g.drawImage(painting, 0, 0, null);
		}

	}
	/**
	 * Webcam method, here finding regions and updating the painting.
	 * takes color at mouse click, finds largest region of that color in the image, recolors that region with random colors
	 * and sets it as paintbrush, leaves trail of paintColor (blue) for all points paintbrush travels over
	 */
	@Override
	public void processImage() {
		ArrayList<Point> paintbrush;
		// only class that has ability to work with image, returns answer to problem (largest region
		if (targetColor != null) {
			//calls methods from region finder to implement flood-fill algorithm for target color in image
			finder.setImage(image);
			finder.findRegions(targetColor);
			paintbrush = finder.largestRegion();
			// recolors the largest region
			if (finder.largestRegion() != null) {
				finder.recolorImage();
				// leaves a trail of paintColor (blue) for all points paintbrush travels over
				for (Point paintingArea : paintbrush) {
					painting.setRGB((int) paintingArea.getX(), (int) paintingArea.getY(), paintColor.getRGB());
				}
			}
		}
	}

	/**
	 * Overrides the DrawingGUI method to set the track color.
	 * Gets color at mouse click and sets targetColor to that color
	 */
	@Override
	public void handleMousePress(int x, int y) {
		if (image != null) {
			//sets targetColor to color at mouse click
			targetColor = new Color(image.getRGB(x, y));
		}
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "pictures/painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}
