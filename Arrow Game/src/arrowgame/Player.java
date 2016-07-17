/**
 * 
 * @author Jipesh
 */
package arrowgame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import game.engine2D.Engine2DMovableRectangleEntity;

public class Player extends Engine2DMovableRectangleEntity implements KeyListener{

	private int score;
	private boolean leftPressed;
	private boolean rightPressed;

	/**
	 * Set's the score to zero as well as set the starting position
	 * 
	 * @param x
	 *            The starting X value
	 * @param y
	 *            The starting Y value
	 */
	public Player(Game g, int x, int y) {
		super(x, y, 35, 62);
		score = 0;
	}

	/**
	 * checks if the left or right key is pressed and apply's action to the
	 * player accordingly
	 * 
	 * @see Player#moveLeft() moveLeft()
	 * @see Player#moveRight() moveRight()
	 */
	public void start() {
		if (leftPressed == true) {
			moveLeft();
		}
		if (rightPressed == true) {
			moveRight();
		}
	}

	public void reset() {
		score = 0; // set's the score back to 0
	}

	public void nextStage() {
		score += 2; // increases the score by 2
	}

	/**
	 * moves Player 5 pixels to the left but only if X is greater then 4
	 */
	private void moveLeft() {
		if (getX() > 4) {
			getBoundingBox().moveX(-5, 1);
		}
	}

	/**
	 * moves Player 5 pixels to the right but only if X is less then 356
	 */
	private void moveRight() {
		if (getX() < 356) {
			getBoundingBox().moveX(5, 1);
		}
	}

	/**
	 * @return The Player's score
	 */
	public int getScore() {
		return score;
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			leftPressed = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			rightPressed = true;
		}

	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			leftPressed = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			rightPressed = false;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public String toString() {
		return "[" + getX() + "," + (getX() + this.getBoundingBox().getWidth()) + "," + getY() + "," + (getY() + this.getBoundingBox().getHeight()) + "]";
	}

	@Override
	public void update() {
		if(getGame().getRunning()){
			start();
		}
	}

}
