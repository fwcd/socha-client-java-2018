package com.fwcd.sc18.trainer.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import sc.plugin2018.Board;
import sc.plugin2018.GameState;
import sc.plugin2018.Player;
import sc.shared.PlayerColor;

public class GameView {
	private final JPanel view;
	private final JPanel canvas;
	private final ScoreTable scoreTable;
	
	private final ColoredGrid grid = new ColoredGrid(13, 13);
	private final int[][] track = {
			{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{-1, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, -1},
			{-1, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, 19, -1},
			{-1, 32, -1, -1, -1, -1, 55, 54, 53, 52, -1, 18, -1},
			{-1, 33, -1, -1, -1, -1, 56, -1, -1, 51, -1, 17, -1},
			{-1, 34, -1, 64, -1, -1, 57, -1, -1, 50, -1, 16, -1},
			{-1, 35, -1, 63, -1, -1, 58, -1, -1, 49, -1, 15, -1},
			{-1, 36, -1, 62, 61, 60, 59, -1, -1, 48, -1, 14, -1},
			{-1, 37, -1, -1, -1, -1, -1, -1, -1, 47, -1, 13, -1},
			{-1, 38, 39, 40, 41, 42, 43, 44, 45, 46, -1, 12, -1},
			{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 11, -1},
			{-1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, -1},
			{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}
	};
	
	{
		view = new JPanel();
		view.setLayout(new BorderLayout());
		
		canvas = new JPanel() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				render((Graphics2D) g, getSize());
			}
		};
		view.add(canvas, BorderLayout.CENTER);
		
		scoreTable = new ScoreTable();
		JTable table = new JTable(scoreTable);
		table.setPreferredScrollableViewportSize(new Dimension(250, 100));
		view.add(new JScrollPane(table), BorderLayout.SOUTH);
	}
	
	public GameView() {
		this(new GameState());
	}
	
	public GameView(GameState game) {
		update(game);
	}

	public void update(GameState game) {
		updateState(game);
		updateBoard(game.getBoard());
	}
	
	private void updateState(GameState game) {
		grid.clear();
		
		Player red = game.getPlayer(PlayerColor.RED);
		Player blue = game.getPlayer(PlayerColor.BLUE);
		
		updateScores(red, blue);
		
		int redField = red.getFieldIndex();
		int blueField = blue.getFieldIndex();
		
		for (int y=0; y<track.length; y++) {
			for (int x=0; x<track[0].length; x++) {
				int i = track[y][x];
				
				if (redField == i) {
					grid.setForeground(x, y, Color.RED);
				} else if (blueField == i) {
					grid.setForeground(x, y, Color.BLUE);
				}
			}
		}
		repaintSoon();
	}

	private void repaintSoon() {
		SwingUtilities.invokeLater(view::repaint);
	}

	private void updateScores(Player red, Player blue) {
		scoreTable.set(new String[] {
				"RED: " + red.getDisplayName(), "BLUE: " + blue.getDisplayName()
		}, new String[][] {
				{"carrots: " + Integer.toString(red.getCarrots()), "carrots: " + Integer.toString(blue.getCarrots())},
				{"salads: " + Integer.toString(red.getSalads()), "salads: " + Integer.toString(blue.getSalads())},
				{"cards: " + Integer.toString(red.getCards().size()), "cards: " + Integer.toString(blue.getCards().size())},
		});
	}

	private void updateBoard(Board board) {
		for (int y=0; y<track.length; y++) {
			for (int x=0; x<track[0].length; x++) {
				int i = track[y][x];
				
				if (i >= 0) {
					Color bg = null;
					
					switch (board.getTypeAt(i)) {
					
					case CARROT:
						bg = Color.ORANGE;
						break;
					case GOAL:
						bg = Color.CYAN;
						break;
					case HARE:
						bg = Color.ORANGE.darker().darker();
						break;
					case HEDGEHOG:
						bg = Color.BLACK;
						grid.setChar(x, y, 'H');
						break;
					case POSITION_1:
						bg = Color.YELLOW;
						grid.setChar(x, y, '1');
						break;
					case POSITION_2:
						bg = Color.YELLOW;
						grid.setChar(x, y, '2');
						break;
					case SALAD:
						bg = Color.GREEN;
						break;
					case START:
						bg = Color.CYAN;
						break;
					default:
						break;
					
					}
					
					if (bg != null) {
						grid.setBackground(x, y, bg);
					}
				}
			}
		}
		repaintSoon();
	}
	
	private void render(Graphics2D g2d, Dimension canvasSize) {
		int sideLength = boardSideLength();
		
		g2d.setColor(Color.GREEN.darker().darker());
		g2d.fillRect(0, 0, sideLength, sideLength);
		grid.render(g2d, sideLength);
	}

	private int boardSideLength() {
		return (int) Math.min(view.getHeight(), view.getWidth());
	}

	public Component getView() {
		return view;
	}
}
