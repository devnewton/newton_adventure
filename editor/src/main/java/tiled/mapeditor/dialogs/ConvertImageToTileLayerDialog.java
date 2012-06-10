package tiled.mapeditor.dialogs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;

import tiled.core.Map;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.core.TileSet;
import tiled.mapeditor.MapEditor;

public class ConvertImageToTileLayerDialog extends PropertiesDialog {

	private static final long serialVersionUID = 6590784120296960941L;
	private Map map;
	private BufferedImage image;

	public ConvertImageToTileLayerDialog(MapEditor editor, String imageFilename) throws IOException {
		super(editor.getAppFrame(), new Properties());
		this.map = editor.getCurrentMap();
		this.image = ImageIO.read(new File(imageFilename));
	}
	
	@Override
	protected void buildPropertiesAndDispose() {
		super.buildPropertiesAndDispose();
		TileLayer layer = map.addLayer();
		TileSet tileset = new TileSet();
		tileset.setName(layer.getName());
		map.addTileset(tileset);
		for(int x = 0; x<map.getWidth(); ++x) {
			for(int y = 0; y<map.getHeight(); ++y) {
				BufferedImage tileImage = image.getSubimage(x * map.getTileWidth(), y * map.getTileHeight(), map.getTileWidth(), map.getTileHeight());
				if(hasContent(tileImage)) {
				Tile tile = new Tile();
				tile.setImage(tileImage);
				tile.setProperties(new Properties(properties));
				tileset.addNewTile(tile);
				layer.setTileAt(x,  y, tile);
				}
			}
		}
	}

	private boolean hasContent(BufferedImage tileImage) {
			int w = image.getWidth();
			int h = image.getHeight();
			for (int y = 0; y < h; ++y) {
				for (int x = 0; x < w; ++x) {
					int rgba = image.getRGB(x, y);
					byte a = (byte) ((rgba >> 24) & 0xff);
					if(a != 0) {
						return true;
					}
				}
			}
			return false;
		}
}
