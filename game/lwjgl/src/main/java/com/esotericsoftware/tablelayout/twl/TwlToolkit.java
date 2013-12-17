
package com.esotericsoftware.tablelayout.twl;

import com.esotericsoftware.tablelayout.BaseTableLayout;
import com.esotericsoftware.tablelayout.BaseTableLayout.Debug;
import com.esotericsoftware.tablelayout.Cell;
import com.esotericsoftware.tablelayout.Toolkit;

import de.matthiasmann.twl.Widget;

public class TwlToolkit extends Toolkit<Widget, Table> {
	static public final TwlToolkit instance = new TwlToolkit();

	public Cell<Widget, Table> obtainCell (BaseTableLayout<Widget, Table> layout) {
		Cell<Widget, Table> cell = new Cell<Widget, Table>();
		cell.setLayout(layout);
		return cell;
	}

	public void freeCell (Cell<Widget, Table> cell) {
	}

	public void addChild (Widget parent, Widget child) {
		parent.add(child);
	}

	public void removeChild (Widget parent, Widget child) {
		parent.removeChild(child);
	}

	public float getMinWidth (Widget widget) {
		return widget.getMinWidth();
	}

	public float getMinHeight (Widget widget) {
		return widget.getMinHeight();
	}

	public float getPrefWidth (Widget widget) {
		return widget.getPreferredWidth();
	}

	public float getPrefHeight (Widget widget) {
		System.out.println(widget.getClass() + " " + widget.getPreferredHeight());
		return widget.getPreferredHeight();
	}

	public float getMaxWidth (Widget widget) {
		return widget.getMaxWidth();
	}

	public float getMaxHeight (Widget widget) {
		return widget.getMaxHeight();
	}

	public float getWidth (Widget widget) {
		return widget.getWidth();
	}

	public float getHeight (Widget widget) {
		return widget.getHeight();
	}

	public void clearDebugRectangles (BaseTableLayout<Widget, Table> layout) {
	}

	public void addDebugRectangle (BaseTableLayout<Widget, Table> layout, Debug type, float x, float y, float w, float h) {
	}
}
