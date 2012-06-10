package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import tiled.mapeditor.MapEditor;
import tiled.mapeditor.Resources;
import tiled.mapeditor.dialogs.ConvertImageToTileLayerDialog;
import tiled.util.TiledConfiguration;

public class ConvertImageToTileLayerAction extends AbstractAction {

	private static final long serialVersionUID = -6117686275748936514L;
	private final MapEditor editor;

	public ConvertImageToTileLayerAction(MapEditor editor) {
		super(Resources.getString("action.convert.image.to.tile.layer.name"));
		this.editor = editor;
	}

	public void actionPerformed(ActionEvent e) {
		String startLocation = TiledConfiguration.node("recent").get("file0",
				null);
		JFileChooser chooser = new JFileChooser(startLocation);
		chooser.setFileFilter(new FileNameExtensionFilter("PNG file", "png"));
		int result = chooser.showOpenDialog(editor.getAppFrame());
		if (result == JFileChooser.APPROVE_OPTION) {
			String selectedFile = chooser.getSelectedFile().getAbsolutePath();
			ConvertImageToTileLayerDialog dialog = null;
			try {
				dialog = new ConvertImageToTileLayerDialog(editor, selectedFile);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(editor.getAppFrame(),
						ex.getLocalizedMessage(), "Error loading image",
						JOptionPane.WARNING_MESSAGE);
			}
			if (null != dialog) {
				dialog.setVisible(true);
			}
		}

	}
}
