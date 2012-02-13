/*
 *  Tiled Map Editor, (c) 2004-2006
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <bjorn@lindeijer.nl>
 */

package tiled.mapeditor.undo;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import tiled.mapeditor.MapEditor;
import tiled.util.TiledConfiguration;

/**
 * @version $Id$
 */
public class UndoHandler extends UndoManager
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 6225822937347591856L;

	UndoableEdit savedAt;

    private final Action undoAction = new UndoAction();
    private final Action redoAction = new RedoAction();
    private final MapEditor editor;

    public UndoHandler(MapEditor editor) {
        this.editor = editor;
        setLimit(TiledConfiguration.root().getInt("undoDepth", 30));
        updateActions();
    }

    /**
     * Overridden to update the undo/redo actions.
     * @see UndoManager#discardAllEdits()
     */
    @Override
	public synchronized void discardAllEdits()
    {
        super.discardAllEdits();
        updateActions();
    }

    /**
     * Overridden to update the undo/redo actions.
     * @see UndoManager#undo()
     * @throws CannotUndoException
     */
    @Override
	public synchronized void undo() throws CannotUndoException {
        super.undo();
        updateActions();
        editor.updateTitle();

        // todo: Updating of the mapview should ultimately happen
        // todo: automatically based on the changes made to the map.
        editor.getMapView().repaint();
    }

    /**
     * Overridden to update the undo/redo actions.
     * @see UndoManager#redo()
     * @throws CannotRedoException
     */
    @Override
	public synchronized void redo() throws CannotRedoException {
        super.redo();
        updateActions();
        editor.updateTitle();
        editor.getMapView().repaint();
    }

    @Override
	public void undoableEditHappened(UndoableEditEvent e) {
        super.undoableEditHappened(e);
        updateActions();
        editor.updateTitle();
    }

    public boolean isAllSaved() {
        return editToBeUndone() == savedAt;
    }

    public void commitSave() {
        savedAt = editToBeUndone();
    }

    public String[] getEdits() {
        String[] list = new String[edits.size()];
        Iterator<UndoableEdit> itr = edits.iterator();
        int i = 0;

        while (itr.hasNext()) {
            UndoableEdit e = itr.next();
            list[i++] = e.getPresentationName();
        }

        return list;
    }

    /**
     * Returns the redo action.
     * @return the redo action.
     */
    public Action getRedoAction() {
        return redoAction;
    }

    /**
     * Returns the undo action.
     * @return the undo action
     */
    public Action getUndoAction() {
        return undoAction;
    }

    private void updateActions() {
        undoAction.setEnabled(canUndo());
        redoAction.setEnabled(canRedo());
        undoAction.putValue(Action.NAME, getUndoPresentationName());
        redoAction.putValue(Action.NAME, getRedoPresentationName());
    }

    private class UndoAction extends AbstractAction {
        /**
		 * 
		 */
		private static final long serialVersionUID = -4072912036798139575L;
		public UndoAction() {
            super(getUndoPresentationName());
            putValue(SHORT_DESCRIPTION, "Undo one action");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Z"));
        }
        public void actionPerformed(ActionEvent evt) {
            undo();
        }
    }

    private class RedoAction extends AbstractAction {
        /**
		 * 
		 */
		private static final long serialVersionUID = -8771028902507353877L;
		public RedoAction() {
            super(getRedoPresentationName());
            putValue(SHORT_DESCRIPTION, "Redo one action");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Y"));
        }
        public void actionPerformed(ActionEvent evt) {
            redo();
        }
    }
}
