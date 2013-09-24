
package com.esotericsoftware.tablelayout.twl;

import com.esotericsoftware.tablelayout.Cell;

import de.matthiasmann.twl.Widget;

public class Table extends Widget {
	public final TableLayout layout;

	public Table () {
		this(new TableLayout());
	}

	public Table (TableLayout layout) {
		this.layout = layout;
		layout.setTable(this);
		setTheme("");
	}

	public Cell<Widget, Table> addCell (Widget widget) {
		return layout.add(widget);
	}

	public Cell<Widget, Table> row () {
		return layout.row();
	}

	public Cell<Widget, Table> columnDefaults (int column) {
		return layout.columnDefaults(column);
	}

	public Cell<Widget, Table> defaults () {
		return layout.defaults();
	}

	public void layout () {
		layout.layout();
	}

	public int getMinWidth () {
		return (int)layout.getMinWidth();
	}

	public int getMinHeight () {
		return (int)layout.getMinHeight();
	}

	public int getPreferredWidth () {
		return (int)layout.getPrefWidth();
	}

	public int getPreferredHeight () {
		return (int)layout.getPrefHeight();
	}

	public void invalidateLayout () {
		super.invalidateLayout();
	}
}
