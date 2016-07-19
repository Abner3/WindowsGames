/**
 * 
 * @author Jipesh
 */
package bomberman.content;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import bomberman.content.Bomb.ExplosionFlame;
import bomberman.gui.GameGraphics;

import game.engine2D.Engine2DGame;
import game.engine2D.Engine2DPolygonBoundingBox.Engine2DRectangleBoundingBox;
import game.engine2D.Engine2DRectangleBoundingBoxEntity;
import game.engine2D.Engine2DScreen;
import game.engine2D.Engine2DEntity;

public class Game extends Engine2DGame {
	protected static final String PLAYERS_KEY = "0x12fd";
	protected static final String POWERUP_KEY = "0x15ec";
	protected static final String WALLS_KEY = "0x84fg";
	protected static final String OBSTACLES_KEY = "0x47af";
	protected static final String BOMBS_KEY = "0x53ba";
	private final int[][] BATTLE_FIELD = new int[17][11];
	private BufferedImage sprite_sheet;
	private boolean gameover = false;
	private Character player1, player2;
	private Engine2DScreen gui;

	public Game() {
		super("BomberMan", 766, 620, false);
		try {
			sprite_sheet = ImageIO.read(getClass().getResource("/bomberman/resources/sprite_sheet.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		setupList();
		init();
	}

	
	/**
	 * initiate method which set up the battlefield with the brick
	 * wall(Obstacle) and walls, Battle field array represent the map in terms
	 * of each block. whereby the battlefield is 17 * 11 (width * height)
	 */
	public void init() {
		int index = 0;
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 17; j++) {
				if (j % 2 == 1 && i % 2 == 1 && (i != 0 || i != 16)) {
					BATTLE_FIELD[j][i] = 1;
					Wall wall = new Wall(((j + 1) * 40), ((i + 1) * 40), this);
					addEntityToList(WALLS_KEY, wall);

					/*
					 * j+1 and i+1 because it should not be placed at the border
					 */

				} else if (!((i <= 1 || i >= 9) && (j <= 1 || j >= 15))) {
					Random rnd = new Random();
					int x = rnd.nextInt(3);
					if (x == 1 || x == 2) { // fill up as many spaces as
											// possible
						BATTLE_FIELD[j][i] = 2;
						Obstacle obs = new Obstacle(index++, ((j + 1) * 40), ((i + 1) * 40), this);
						addEntityToList(OBSTACLES_KEY, obs);
					}
				}
			}
		}
		addPlayers();
		String[] keys = {PLAYERS_KEY,POWERUP_KEY,WALLS_KEY,OBSTACLES_KEY,BOMBS_KEY};
		gui = new GameGraphics(this,keys);
		addScreen(gui);
		setScreen(0);
		gameover = false;
		start(60);
	}
	
	private void setupList(){
		GameThreadList<Engine2DEntity> players = new GameThreadList<>();
		GameThreadList<Engine2DEntity> powerups = new GameThreadList<>();
		GameThreadList<Engine2DEntity> bombs = new GameThreadList<>();
		GameThreadList<Engine2DEntity> walls = new GameThreadList<>();
		GameThreadList<Engine2DEntity> obstacles = new GameThreadList<>();
		addEntityList(PLAYERS_KEY, players);
		addEntityList(POWERUP_KEY, powerups);
		addEntityList(BOMBS_KEY, bombs);
		addEntityList(WALLS_KEY, walls);
		addEntityList(OBSTACLES_KEY, obstacles);
	}

	private void addPlayers() {
		player1 = new Player(1, 42, 42, this); // for starting stage only
		addThread(new Thread(player1));
		addEntityToList(PLAYERS_KEY, player1);
		player2 = new Player(2, (17 * 40) + 2, 42, this); /* for starting stage
															 only */
		addThread(new Thread(player2));
		addEntityToList(PLAYERS_KEY, player2);

	}

	@Override
	public void gameLoop() {
		if (getEntityList(PLAYERS_KEY).size() == 1) {
			gameOver(); // if there is only one player then game will be over
		}
		checkGameover();
	}

	/**
	 * frees up the exact value in the array by setting it to zero which means
	 * empty space
	 * 
	 * @param x
	 *            the x position
	 * @param y
	 *            the y position
	 * 
	 */
	public void makeAvailable(int x, int y) {
		BATTLE_FIELD[x - 1][y - 1] = 0;
	}

	/**
	 * returns the exact sprite image using the x position and the multiply by
	 * block size which is 40 to locate the image x and y coordinate on the
	 * sprite sheet
	 * 
	 * @param x
	 *            the x position
	 * @param y
	 *            the y position
	 * @return the exact sprite from the sprite sheet
	 */
	public Image getSprite(int x, int y, int width, int height) {
		return sprite_sheet.getSubimage((x * 40), (y * 40), width, height);
	}

	protected Bomb bombCollision(Engine2DRectangleBoundingBoxEntity e) {
		for (Engine2DEntity entity : getEntityList(BOMBS_KEY)) {
			Bomb bomb = (Bomb)entity;
			if (e.getBoundingBox().checkCollision(bomb.getBoundingBox())) {
				return bomb;
			}
		}
		return null;

	}

	/**
	 * the method checks if the player is on top of the bomb and around the
	 * center
	 */
	protected void updateWalkable() {
		if (!getEntityList(BOMBS_KEY).isEmpty()) {
			for (Engine2DEntity entity : getEntityList(BOMBS_KEY)) {
				Bomb bomb = (Bomb)entity;
				for (Engine2DEntity entity2 : getEntityList(PLAYERS_KEY)) {
					Character player = (Character)entity2;
					if (bomb.getBoundingBox().checkCollision(player.getBoundingBox())) {
						Engine2DRectangleBoundingBox box = new Engine2DRectangleBoundingBox(bomb.getX() + 4, bomb.getY() + 4, bomb.getWidth() - 4,
								bomb.getHeight() - 4);
						if (player.getBoundingBox().checkCollision(box)) {
							player.addWalkable(bomb);
						}
					}
				}
			}
		}
	}

	/**
	 * returns the exact sprite image using the x position and the multiply by
	 * block size which is 40 to locate the image x and y coordinate on the
	 * sprite sheet
	 * 
	 * @param x
	 *            the x position
	 * @param y
	 *            the y position
	 * @return the exact sprite from the sprite sheet
	 */
	Image getSprite(int x, int y) {
		return sprite_sheet.getSubimage((x * 40), (y * 40), 40, 40);
	}

	/**
	 * checks to make sure the position is valid if so adds the bomb to the list
	 * and updates map
	 * 
	 * @param bomb
	 *            the bomb to add to the list
	 */
	void addBomb(Bomb bomb) {
		if (checkAvailability((bomb.getX() / 40), (bomb.getY() / 40))) {
			addEntityToList(BOMBS_KEY, bomb);
			BATTLE_FIELD[(bomb.getX() / 40) - 1][(bomb.getY() / 40) - 1] = 3;
		}
	}

	public void addSpecials(PowerUp power) {
		addEntityToList(POWERUP_KEY, power);
		BATTLE_FIELD[(power.getX() / 40) - 1][(power.getY() / 40) - 1] = 4;
	}

	/**
	 * checks if the explosions have touched any of the player
	 */
	synchronized void checkGameover() {
		for (Engine2DEntity entity : getEntityList(BOMBS_KEY)) {
			Bomb bomb = (Bomb)entity;
			if (bomb.getDetonated()) {
				for (ExplosionFlame exp : bomb.getExplostions()) {
					bomb.playerHit(exp.getBoundingBox().getX(), exp.getBoundingBox().getY());
				}
			}
		}
	}

	/**
	 * A method to check if the x and y position are available on the map
	 * 
	 * @param x
	 *            the x value on first array
	 * @param y
	 *            the y value on second array
	 * @return if the area is empty space
	 */
	boolean checkAvailability(int x, int y) {
		if (x - 1 < 0 || y - 1 < 0 || x - 1 > 16 || y - 1 > 10) {
			return false;
		} else if (BATTLE_FIELD[x - 1][y - 1] == 0) {
			return true;
		}
		return false;
	}

	/**
	 * checks to see it's a valid value then return value which exist within the
	 * map at that x and y position
	 * 
	 * @param x
	 *            the x position on the map
	 * @param y
	 *            the y position on the map
	 * @return the value with that x and y on the map
	 */
	int checkMap(int x, int y) {
		if (x - 1 < 0 || y - 1 < 0 || x - 1 > 16 || y - 1 > 10) {
			return -1;
		}
		return BATTLE_FIELD[x - 1][y - 1];
	}

	/**
	 * The method checks if the entity is overlapping/colliding with any of the
	 * walls or obstacles
	 * 
	 * @param box
	 *            the entity bounding box
	 * @return the entity it is colliding with
	 */
	Engine2DRectangleBoundingBoxEntity checkCollision(Engine2DRectangleBoundingBox box) {
		for (Engine2DEntity entity : getEntityList(WALLS_KEY)) {
			Wall wall = (Wall)entity;
			if (wall.getBoundingBox().checkCollision(box)) {
				return wall;
			}
		}
		for (Engine2DEntity entity : getEntityList(OBSTACLES_KEY)) {
			Obstacle obs = (Obstacle)entity;
			if (obs.getBoundingBox().checkCollision(box)) {
				return obs;
			}
		}
		return null;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void gameOver() {
		gameover = true;
	}

}
