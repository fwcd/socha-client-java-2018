package com.thedroide.sc18.test.clientbench.core;

import javax.swing.table.AbstractTableModel;

public class ScoreTable extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private String[] colNames = {"No data"};
	private String[][] data = new String[1][1];
	
	public void set(String[] colNames, String[][] data) {
		this.colNames = colNames;
		this.data = data;
		fireTableStructureChanged();
		fireTableDataChanged();
	}
	
	public void set(int x, int y, String v) {
		data[y][x] = v;
	}
	
	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public int getColumnCount() {
		return data[0].length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}

	@Override
	public String getColumnName(int column) {
		return colNames[column];
	}
}
