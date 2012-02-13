package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.TileLayer;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.Resources;
import tiled.mapeditor.util.MinimapGenerator;
import tiled.util.MathUtils;
import tiled.util.TiledConfiguration;

public class MinimapAction extends AbstractAction {

	private static final long serialVersionUID = 5078262158940560726L;
	private final MapEditor editor;
	
	public MinimapAction(MapEditor editor) {
		super(Resources.getString("action.minimap.name"));
		this.editor = editor;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String startLocation =
                TiledConfiguration.node("recent").get("file0", null);
		JFileChooser chooser = new JFileChooser(startLocation);
		chooser.setFileFilter(new FileNameExtensionFilter("PNG file", "png"));
        int result = chooser.showSaveDialog(editor.getAppFrame());
        if (result == JFileChooser.APPROVE_OPTION)
        {
            String selectedFile = chooser.getSelectedFile().getAbsolutePath();
    		Map map = editor.getCurrentMap();
    		if(null != map) {
    			generateMinimap(map, selectedFile);
    		}            
        }
        
	}

	private void generateMinimap(Map map, String selectedFile) {
		int w = Math.max(256, MathUtils.getPowerOfTwoBiggerThan(map.getWidth()));
		int h = Math.max(256, MathUtils.getPowerOfTwoBiggerThan(map.getHeight()));
		MinimapGenerator minimap = new MinimapGenerator(w,h);
		for(MapLayer layer: map.getLayerVector()) {
			if(layer instanceof TileLayer) {
				TileLayer tileLayer = (TileLayer)layer;
				minimap.addLayer(tileLayer);
			}
		}
		minimap.save(selectedFile);
	}

}
