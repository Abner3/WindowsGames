/**
 * 
 * @author Jipesh
 */
package bomberman.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import bomberman.content.Bomb;
import bomberman.content.Bomb.ExplosionFlame;
import bomberman.content.Character;
import game.engine2D.Engine2DEntity;
import game.engine2D.Engine2DPolygonBoundingBox.Engine2DRectangleBoundingBox;
import game.engine2D.Engine2DScreen;
import bomberman.content.Game;
import bomberman.content.Obstacle;
import bomberman.content.Player;
import bomberman.content.PowerUp;

public class GameGraphics extends Engine2DScreen {
	private final String PLAYERS_KEY, POWERUP_KEY, WALLS_KEY, OBSTACLES_KEY, BOMBS_KEY;
	private Image background;
	private JButton restart;

	public GameGraphics(Game game, String... keys) {
		super(game);
		this.PLAYERS_KEY = keys[0];
		this.POWERUP_KEY = keys[1];
		this.WALLS_KEY = keys[2];
		this.OBSTACLES_KEY = keys[3];
		this.BOMBS_KEY = keys[4];
		try {
			background = ImageIO.read(getClass().getResourceAsStream("/bomberman/resources/background.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		restart = new JButton("Restart");
		restart.setBounds(250, 580, 60, 40);
		restart.setFocusable(false);
		restart.setVisible(false);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Game game = (Game) getGame();
		g.drawImage(background, 0, 0, null);
		if (game.getGameOver()) {
			restart.setVisible(true);
			g.setColor(Color.WHITE);
			for(Engine2DEntity entity : game.getEntityList(PLAYERS_KEY)){
				Player player = (Player)entity;
				switch(player.getCharacter()){
				case 1:
					g.drawString("Player 1 Wins", 700 / 2, 558 / 2);
					break;
				case 2:
					g.drawString("Player 2 Wins", 700 / 2, 558 / 2);
					break;
				}
			}
		} else {
			for(Engine2DEntity entity : game.getEntityList(OBSTACLES_KEY)){
				Obstacle obs = (Obstacle)entity;
				g.drawImage(obs.getImage(), obs.getX(), obs.getY(), null);
			}
			
			int position = 0;
			
			for (Engine2DEntity entity : game.getEntityList(PLAYERS_KEY)) {
				Character player = (Character)entity;
				g.setColor(Color.DARK_GRAY);
				g.fillRect((position * 40), 13 * 40, 200, 120);
				int id = player.getCharacter();
				g.setColor(Color.WHITE);

				/*
				 * draws the player statistics
				 */
				g.drawString("Player " + id + " Speed           : " + player.getSpeed(), (position * 40), 13 * 40 + 20);
				g.drawString("Player " + id + " Bombs           : " + player.getBombs(), (position * 40), 13 * 40 + 40);
				g.drawString("Player " + id + " explostion size : " + player.getExplosion_size(), (position * 40),
						13 * 40 + 60);
				position += 6;
				
				if (!(game.getEntityList(BOMBS_KEY).isEmpty())) {
					
					for(Engine2DEntity entity2 : game.getEntityList(BOMBS_KEY)){
					Bomb bomb = (Bomb)entity2;
						
						if (bomb.hasDetonated()) {
							g.drawImage(player.getImage(), player.getX(), player.getY(), null);
							g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), null);
							
							for (ExplosionFlame exp : bomb.getExplostions()) {
								Engine2DRectangleBoundingBox box = exp.getBoundingBox();
								g.drawImage(exp.getImage(), box.getX(), box.getY(), null);

								/*
								 * player will be bellow the explosion
								 */
							}
							
							if (bomb.delete()) {
								game.removeEntityFromList(BOMBS_KEY, entity2);;
								game.makeAvailable(bomb.getX() / 40, bomb.getY() / 40);
								bomb.updatePlayer();
							}
						} else {
							g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), null);
							g.drawImage(player.getImage(), player.getX(), player.getY(), null);

							/*
							 * player will be ontop of the box
							 */
						}
					}
				} else {
					g.drawImage(player.getImage(), player.getX(), player.getY(), null);

					// there may be no bombs plated
				}
			}
			for (Engine2DEntity entity : game.getEntityList(POWERUP_KEY)) {
				PowerUp power = (PowerUp)entity;
				g.drawImage(power.getImage(), power.getX(), power.getY(), null);
			}

		}
}}
