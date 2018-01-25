package com.thedroide.sc18.test.demo.pong;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * A demo environment for training.
 */
public class PongGame {
	private final JFrame view;
	
	private final Ball ball = new Ball(320, 240);
	private final Paddle leftPaddle;
	private final Paddle rightPaddle;
	private final PongPlayer leftPlayer;
	private final PongPlayer rightPlayer;
	
	/**
	 * Creates a pong game with two human players. (W/S and O/L)
	 */
	public PongGame() {
		this(null, null);
	}

	/**
	 * Creates a pong game with one human players. (O/L)
	 */
	public PongGame(PongPlayer left) {
		this(left, null);
	}
	
	/**
	 * Creates a pong game with two AI players.
	 */
	public PongGame(PongPlayer left, PongPlayer right) {
		leftPlayer = left;
		rightPlayer = right;
		
		view = new JFrame("Pong");
		view.setSize(640, 480);
		view.setLayout(new BorderLayout());
		JPanel canvas = new JPanel() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				render((Graphics2D) g);
			}
		};
		canvas.setBackground(Color.BLACK);
		view.add(canvas, BorderLayout.CENTER);
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.setVisible(true);
		
		leftPaddle = new Paddle(canvas, true, 100);
		rightPaddle = new Paddle(canvas, false, 100);
		
		bindKey(canvas, KeyStroke.getKeyStroke("W"), leftPaddle::moveUp);
		bindKey(canvas, KeyStroke.getKeyStroke("S"), leftPaddle::moveDown);
		bindKey(canvas, KeyStroke.getKeyStroke("O"), rightPaddle::moveUp);
		bindKey(canvas, KeyStroke.getKeyStroke("L"), rightPaddle::moveDown);
		
		new Timer().scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				ball.move(canvas.getWidth(), canvas.getHeight(), leftPaddle, rightPaddle);
				SwingUtilities.invokeLater(view::repaint);
				
				if (leftPlayer != null) {
					if (leftPlayer.shouldMoveUp(leftPaddle, rightPaddle, ball)) {
						leftPaddle.moveUp();
					} else {
						leftPaddle.moveDown();
					}
				}
				
				if (rightPlayer != null) {
					if (rightPlayer.shouldMoveUp(rightPaddle, leftPaddle, ball)) {
						rightPaddle.moveUp();
					} else {
						rightPaddle.moveDown();
					}
				}
			}
			
		}, 1000 / 60, 1000 / 60);
	}
	
	private void bindKey(JPanel pane, KeyStroke k, Runnable action) {
		pane.getInputMap().put(k, k);
		pane.getActionMap().put(k, new AbstractAction() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				action.run();
			}
		});
	}
	
	public static class Ball {
		private final int radius = 4;
		private final double velocity = 5;
		private double posX;
		private double posY;
		private double dirX;
		private double dirY;
		
		public Ball(int posX, int posY) {
			this.posX = posX;
			this.posY = posY;
			ThreadLocalRandom r = ThreadLocalRandom.current();
			dirX = Math.cos(Math.toRadians(r.nextDouble(360))) * velocity;
			dirY = Math.sin(Math.toRadians(r.nextDouble(360))) * velocity;
		}
		
		public double getPosX() { return posX; }
		
		public double getPosY() { return posY; }
		
		public double getDirX() { return dirX; }
		
		public double getDirY() { return dirY; }
		
		public void move(int canvasWidth, int canvasHeight, Paddle leftPaddle, Paddle rightPaddle) {
			posX += dirX;
			posY += dirY;
			
			if (posX <= 0) {
				dirX *= -1;
				leftPaddle.lose();
			} else if (posX >= canvasWidth) {
				dirX *= -1;
				rightPaddle.lose();
			} else if (posY <= 0 || posY >= canvasHeight) {
				dirY *= -1;
			} else if (rightPaddle.intersects(posX, posY)) {
				if (dirX > 0) {
					rightPaddle.incrementScore();
				}
				dirX *= -1;
			} else if (leftPaddle.intersects(posX, posY)) {
				if (dirX < 0) {
					leftPaddle.incrementScore();
				}
				dirX *= -1;
			}
		}
		
		public void render(Graphics2D g2d) {
			g2d.setColor(Color.WHITE);
			g2d.fillOval((int) (posX - radius), (int) (posY - radius), radius * 2, radius * 2);
		}
	}
	
	public static class Paddle {
		private final JPanel canvas;
		private final int sensivity = 10;
		private final boolean left;
		private final int width = 5;
		private final int height = 100;
		private int x;
		private int y;
		private int score = 0;
		
		public Paddle(JPanel canvas, boolean left, int y) {
			this.canvas = canvas;
			this.left = left;
			this.y = y;
		}
		
		public int getY() { return y; }
		
		public void incrementScore() {
			score++;
		}
		
		public void lose() {
			score = 0;
		}

		public boolean intersects(double x, double y) {
			return x > this.x && x < (this.x + width)
					&& y > this.y && y < (this.y + height);
		}
		
		public void moveUp() {
			if (y > height) {
				y -= sensivity;
			}
		}
		
		public void moveDown() {
			if (y < (canvas.getHeight() - height)) {
				y += sensivity;
			}
		}
		
		public void render(Graphics2D g2d) {
			x = left ? 40 : (canvas.getWidth() - 40);
			g2d.setColor(Color.WHITE);
			g2d.fillRect(x, y, width, height);
			g2d.setFont(g2d.getFont().deriveFont(14F));
			g2d.drawString(Integer.toString(score), x, y - 10);
		}
	}
	
	private void render(Graphics2D g2d) {
		leftPaddle.render(g2d);
		rightPaddle.render(g2d);
		ball.render(g2d);
	}
}
