import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Sunint Bindra
 * CS10
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 *
 */
public class RegionFinder {
	private static final int maxColorDiff = 20;                // how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50;                // how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;            // a region is a list of points
	// so the identified regions are in a list of lists of points


	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 * Flood fill algorithm works by cycling through all points, checks if point has been visited, if point has not been visited
	 * and if color matches adds it to array list to points, if # of points is larger than minRegion creates an element in a
	 * nested Arraylist with that element storing all the points of a single region. In this manner finds all the regions and
	 * adds them to an array
	 */
	public void findRegions(Color targetColor) {
		regions = new ArrayList<ArrayList<Point>>();
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		//ArrayList targetArea is for points that need to be visited
		ArrayList<Point> targetArea = new ArrayList<Point>();
		//next 2 for loops cycle through all x and y points in image
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				// creates new color instance variable for the color at point currently selected
				Color click = new Color(image.getRGB(i, j));
				// if statement checks if two points have similar enough color and has not yet been visited (if black)
				if (colorMatch(click, targetColor) && visited.getRGB(i, j) == 0) {
					//newPoints array list stores all the points in a single region
					ArrayList<Point> newPoints = new ArrayList<Point>();
					Point firstPoint = new Point(i,j);
					//if point has met conditions adds it to list of points that need to be visited
					targetArea.add(firstPoint);
					// add points to newPoints array and removes it from targetArea array since it has been visited
					while (targetArea.size() != 0) {
						Point p = targetArea.remove(targetArea.size() - 1);
						newPoints.add(p);
						//changes point to white so we know it has been visited and it will be filtered out by earlier loop
						visited.setRGB(p.x, p.y, 1);
						//next 2 for loops finds neighboring pixels making sure they are not of the bounds
						for (int x = Math.max(p.x - 1, 0); x <= Math.min(image.getWidth() - 1, p.x + 1); x++) {
							for (int y = Math.max(p.y - 1, 0); y <= Math.min(image.getHeight() - 1, p.y + 1); y++) {
								// creates new color instance variable for color of neighbor point
								Color neighborColor = new Color(image.getRGB(x, y));
								// if color of neighbor and original pixel match adds to list of points to be visited
								if (colorMatch(neighborColor, targetColor) && visited.getRGB(x,y) == 0) {
									Point d = new Point(x, y);
									targetArea.add(d);
								}
							}
						}
					}
					//if the number of points is equal or greater than that for a minimum region stores that as an element
					// in regions list (this element represents a region)
					if (newPoints.size() >= minRegion) {
						//System.out.println(newPoints.size());
						regions.add(newPoints);
					}
				}
			}
		}
	}


	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 * Color match is based on taking the absolute value difference between r, g, and b values of two different points
	 * and returns false if there difference is too large (larger than maxColorDiff), otherwise method returns true
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		if (Math.abs((c1.getGreen() - c2.getGreen())) > maxColorDiff) {
			return false;
		} else if (Math.abs(c1.getRed() - c2.getRed()) > maxColorDiff) {
			return false;
		} else if (Math.abs(c1.getBlue() - c2.getBlue()) > maxColorDiff) {
			return false;
		} else {
			return true;
		}
	}


	/**
	 * Returns the largest region detected (if any region has been detected)
	 * Runs through all elements (regions) in the regions array for a non-empty list cycling through all elements
	 * finding largest region (based on # of elements in inner array representing largest # of points)
	 * and returning that 'biggestRegion' for the method
	 */
	public ArrayList<Point> largestRegion() {
		ArrayList<Point> biggestRegion = null;
		if(regions.size() != 0) {
			biggestRegion = regions.get(0);
			//only runs for loop if regions list actually has elements to cycle through
			if(regions.size()>1) {
				//cycles through all elements (region) in regions array setting biggestRegion to the largest region
				for (int k = 0; k < regions.size(); k++) {
					ArrayList<Point> innerRegions = regions.get(k);
					if (innerRegions.size() > biggestRegion.size()) {
						biggestRegion = innerRegions;
					}
				}
			}
		}
		return biggestRegion;
	}


	/**
	 * Sets recoloredImage to be a copy of image,
	 * but with each region a uniform random color,
	 * so we can see where they are
	 * For each element (being an individual region) in the regions array generates a random set of r,g,b values and
	 * sets that region to the randomly generated color
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		for (ArrayList<Point> region : regions) {
			// creates random r,g,b values for random color
			int r = (int) (Math.random() * 255);
			int g = (int) (Math.random() * 255);
			int b = (int) (Math.random() * 255);
			// creates new color instance variable
			Color newColor = new Color(r, g, b);
			for (Point p : region) {
				//recolors image with randomly generated color
				recoloredImage.setRGB(p.x, p.y, newColor.getRGB());
			}
		}
	}
}


