import java.awt.*;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Using a quadtree for collision detection
 * Sunint Bindra, PS2 Collision GUI
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for blobs
 * @author CBK, Fall 2016, using generic PointQuadtree
 */
public class CollisionGUI extends DrawingGUI {
	private static final int width=800, height=600;		// size of the universe

	private List<Blob> blobs;						// all the blobs
	private List<Blob> colliders;					// the blobs who collided at this step
	private char blobType = 'b';						// what type of blob to create
	private char collisionHandler = 'c';				// when there's a collision, 'c'olor them, or 'd'estroy them
	private int delay = 100;							// timer control

	public CollisionGUI() {
		super("super-collider", width, height);

		blobs = new ArrayList<Blob>();

		// Timer drives the animation.
		startTimer();
	}

	/**
	 * Adds an blob of the current blobType at the location
	 */
	private void add(int x, int y) {
		if (blobType=='b') {
			blobs.add(new Bouncer(x,y,width,height));
		}
		else if (blobType=='w') {
			blobs.add(new Wanderer(x,y));
		}
		else if (blobType=='p') {
			blobs.add(new Pulsar(x,y));
		}
		else if (blobType=='q') {
			blobs.add(new WanderingPulsar(x, y));
		}
		else {
			System.err.println("Unknown blob type "+blobType);
		}
	}

	/**
	 * DrawingGUI method, here creating a new blob
	 */
	public void handleMousePress(int x, int y) {
		add(x,y);
		repaint();
	}

	/**
	 * DrawingGUI method
	 */
	public void handleKeyPress(char k) {
		if (k == 'f') { // faster
			if (delay>1) delay /= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 's') { // slower
			delay *= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 'r') { // add some new blobs at random positions
			for (int i=0; i<10; i++) {
				add((int)(width*Math.random()), (int)(height*Math.random()));
				repaint();
			}			
		}
		else if (k == 'c' || k == 'd') { // control how collisions are handled
			collisionHandler = k;
			System.out.println("collision:"+k);
		}
		else { // set the type for new blobs
			blobType = k;			
		}
	}

	/**
	 * DrawingGUI method, here drawing all the blobs and then re-drawing the colliders in red
	 */
	public void draw(Graphics g) {
		// Ask all the blobs to draw themselves.
		if (blobs != null) {
			for (Blob colorBlob : blobs) {
				// Blobs are drawn as blue color
				g.setColor(Color.BLUE);
				colorBlob.draw(g);
			}
		}

		// Ask the colliders to draw themselves in red.
		if (colliders != null) {
			if (collisionHandler == 'c') {
				// Loops over all blobs in collider and draws them as red
				for (Blob collisionForRed : colliders) {
					g.setColor(Color.RED);
					collisionForRed.draw(g);
				}
			}
			if (collisionHandler == 'h') {
				colliders.removeAll(blobs);
			}
		}
	}
		// Ask all the blobs to draw themselves.
		// Ask the colliders to draw themselves in red.


	/**
	 * Sets colliders to include all blobs in contact with another blob
	 */
	private void findColliders() {
		// Create the tree
		// Instantiates new PointQuadtree called collisionBlob
		PointQuadtree<Blob> collisionBlob = new PointQuadtree<>(blobs.get(0), 0, 0, width, height);
		// loops over all blobs (except first one) and inserts them into collision list
		for (int i=1; i < blobs.size(); i++) {
			collisionBlob.insert(blobs.get(i));
		}

		// For each blob, see if anybody else collided with it
		colliders = new ArrayList<Blob>();
		for (Blob blob : blobs) {
			List<Blob> collidedBlob = collisionBlob.findInCircle(blob.getX(), blob.getY(), blob.getR() * 2);
			if (collidedBlob.size() > 1) {
				colliders.add(blob);
			}

		}
		// Create the tree
		// For each blob, see if anybody else collided with it
	}

	/**
	 * DrawingGUI method, here moving all the blobs and checking for collisions
	 */
	public void handleTimer() {
		// Ask all the blobs to move themselves.
		for (Blob blob : blobs) {
			blob.step();
		}
		// Check for collisions
		if (blobs.size() > 0) {
			findColliders();
			if (collisionHandler=='d') {
				blobs.removeAll(colliders);
				colliders = null;
			}
		}
		// Now update the drawing
		repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CollisionGUI();
			}
		});
	}
}
