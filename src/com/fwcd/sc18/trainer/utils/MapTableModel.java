package com.fwcd.sc18.trainer.utils;

import javax.swing.table.AbstractTableModel;

public class MapTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 4544434268594930329L;
	private final BiList<String, String[]> data = new ArrayBiList<>();
	private int width = 2;
	
	public void clear() {
		data.clear();
		fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return width;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		
		case 0:
			return data.getA(rowIndex);
		default:
			return data.getB(rowIndex)[columnIndex - 1];
		
		}
	}

	public void put(String key, String... values) {
		if (!data.containsA(key)) {
			data.add(key, values);
		} else {
			data.remap(key, values);
		}
		
		if (values.length > width) {
			width = values.length;
			fireTableStructureChanged();
		}
		fireTableDataChanged();
	}
}
