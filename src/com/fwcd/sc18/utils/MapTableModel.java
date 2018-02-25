package com.fwcd.sc18.utils;

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
		if (columnIndex == 0) {
			return data.getA(rowIndex);
		} else {
			String[] values = data.getB(rowIndex);
			int i = columnIndex - 1;
			
			if (i < values.length) {
				return values[i];
			} else {
				return "";
			}
		}
	}

	public void put(String key, String... values) {
		if (!data.containsA(key)) {
			data.add(key, values);
		} else {
			data.remap(key, values);
		}
		
		if ((values.length + 1) > width) {
			width = values.length + 1;
			fireTableStructureChanged();
		}
		fireTableDataChanged();
	}
}
