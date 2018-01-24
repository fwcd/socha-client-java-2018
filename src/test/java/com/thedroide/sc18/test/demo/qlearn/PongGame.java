package com.thedroide.sc18.test.demo.qlearn;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * A demo environment for training.
 */
public class PongGame {
	private final JFrame view;
	
	private final Ball ball = new Ball(320, 240);
	private final Paddle leftPaddle = new Paddle(true, 100);
	private final Paddle rightPaddle = new Paddle(false, 100);
	
	public PongGame() {
		view = new JFrame("Pong");
		view.setSize(640, 480);
		view.setLayout(new BorderLayout());
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				render((Graphics2D) g, getWidth());
			}
		};
		panel.setBackground(Color.BLACK);
		view.add(panel, BorderLayout.CENTER);
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.setVisible(true);
		
		new Timer().scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				ball.move(panel.getWidth(), panel.getHeight(), leftPaddle, rightPaddle);
				SwingUtilities.invokeLater(view::repaint);
			}
			
		}, 1000 / 60, 1000 / 60);
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
		private final int sensivity = 10;
		private final boolean left;
		private final int width = 5;
		private final int height = 100;
		private int x;
		private int y;
		private int score = 0;
		
		public Paddle(boolean left, int y) {
			this.left = left;
			this.y = y;
		}
		
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
			y -= sensivity;
		}
		
		public void moveDown() {
			y += sensivity ;
		}
		
		public void render(Graphics2D g2d, int canvasWidth) {
			x = left ? 40 : (canvasWidth - 40);
			g2d.setColor(Color.WHITE);
			g2d.fillRect(x, y, width, height);
			g2d.setFont(g2d.getFont().deriveFont(14F));
			g2d.drawString(Integer.toString(score), x, y - 10);
		}
	}
	
	private void render(Graphics2D g2d, int canvasWidth) {
		leftPaddle.render(g2d, canvasWidth);
		rightPaddle.render(g2d, canvasWidth);
		ball.render(g2d);
	}
}
