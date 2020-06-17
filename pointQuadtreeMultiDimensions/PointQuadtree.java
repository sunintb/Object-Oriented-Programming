import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants
 * Sunint Bindra, PointQuadtree PS2
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, explicit rectangle
 * @author CBK, Fall 2016, generic with Point2D interface
 * 
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		if (p2.getX() <= point.getX()) {
			if (p2.getY() <= point.getY()) {
				if (hasChild(2)) {
					c2.insert(p2);
				} else {
					c2 = new PointQuadtree<E>(p2, getX1(), getY1(), (int) point.getX(), (int) point.getY());
				}
			} else if (p2.getY() >= point.getY()) {
				if (hasChild(3)) {
					c3.insert(p2);
				} else {
					c3 = new PointQuadtree<E>(p2, getX1(), (int) point.getY(), (int) point.getX(), getY2());
				}
			}
		} else if (p2.getX() >= point.getX()) {
			if (p2.getY() <= point.getY()) {
				if (hasChild(1)) {
					c1.insert(p2);
				} else {
					c1 = new PointQuadtree<E>(p2, (int) point.getX(), getY1(), getX2(), (int) point.getY());
				}
			} else if (p2.getY() >= point.getY()) {
				if (hasChild(4)) {
					c4.insert(p2);
				} else {
					c4 = new PointQuadtree<E>(p2, (int) point.getX(), (int) point.getY(), getX2(), getY2());
				}
			}
		}
	}
	
	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		int s = 1;
		if (this.hasChild(1)) {
			s = s + c1.size();
		} else if (this.hasChild(2)) {
			s = s + c2.size();
		} else if (this.hasChild(3)) {
			s = s + c3.size();
		} else if (this.hasChild(4)) {
			s = s + c4.size();
		}
		return s;
	}
	
	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public ArrayList<E> allPoints() {
		ArrayList<E> points = new ArrayList<E>();
		this.addToAllPoint(points);
		return points;
	}	

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		// creates list of points in circle using helper method
		ArrayList<E> circleAroundPoint = new ArrayList<E>();
		this.circleAroundPoint(cx,  cy,  cr, circleAroundPoint);
		return circleAroundPoint;
	}

	public void circleAroundPoint(double cx, double cy, double cr, ArrayList<E> listOfPoints) {
		// if circle intersects rectangle and point is in circle adds point to the list
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, this.getX1(), this.getY1(), this.getX2(), this.getY2())) {
			if (Geometry.pointInCircle(this.getPoint().getX(), this.getPoint().getY(), cx, cy, cr)) {
				listOfPoints.add(this.getPoint());
			}
			// for all quadrants that have child get child for circles in point radius
			for (int x = 1; x < 5; x++) {
				if (this.hasChild(x)) {
					this.getChild(x).circleAroundPoint(cx, cy, cr, listOfPoints);
				}
			}
		}
	}

	public void addToAllPoint (ArrayList < E > allPoints) {
		// adds points based on quadrant location, adds to list if all are null
		if (c1 == null && c2 == null & c3 == null & c4 == null) {
			allPoints.add(point);
		}
		else {
			if (hasChild(1)) c1.addToAllPoint(allPoints);
			if (hasChild(2)) c2.addToAllPoint(allPoints);
			if (hasChild(3)) c3.addToAllPoint(allPoints);
			if (hasChild(4)) c4.addToAllPoint(allPoints);
		}
	}
}
