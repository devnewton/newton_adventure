package tiled.mapeditor.dialogs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

import tiled.core.Map;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.core.TileSet;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.widget.IntegerSpinner;

public class ConvertImageToTileLayerDialog extends PropertiesDialog {

	private static final long serialVersionUID = 6590784120296960941L;
	private Map map;
	private BufferedImage image;
	private IntegerSpinner tileWidthSpinner;
	private IntegerSpinner tileHeightSpinner;

	public ConvertImageToTileLayerDialog(MapEditor editor, String imageFilename)
			throws IOException {
		super(editor.getAppFrame(), new Properties());
		this.map = editor.getCurrentMap();
		this.image = ImageIO.read(new File(imageFilename));

		JPanel tileSizePanel = new JPanel();

		tileSizePanel.add(new JLabel("Tile width"));
		tileWidthSpinner = new IntegerSpinner(map.getTileWidth(), 1, 8192);
		tileSizePanel.add(tileWidthSpinner);

		tileSizePanel.add(new JLabel("Tile height"));
		tileHeightSpinner = new IntegerSpinner(map.getTileHeight(), 1, 8192);
		tileSizePanel.add(tileHeightSpinner);

		mainPanel.add(tileSizePanel);
		pack();
	}

	@Override
	protected void buildPropertiesAndDispose() {
		super.buildPropertiesAndDispose();
		int tileWidth = tileWidthSpinner.intValue();
		int tileHeight = tileHeightSpinner.intValue();
		int xTileIncrement = Math.max(1, tileWidth / map.getTileWidth());
		int yTileIncrement = Math.max(1, tileHeight / map.getTileHeight());

		TileLayer layer = map.addLayer();
		TileSet tileset = new TileSet();
		tileset.setName(layer.getName());
		map.addTileset(tileset);
		for (int x = 0, ximg = 0; x < map.getWidth(); x += xTileIncrement, ximg += tileWidth) {
			for (int y = xTileIncrement / 2, yimg = 0; y < map.getHeight(); y += yTileIncrement, yimg += tileHeight) {
				BufferedImage tileImage = getSubimage(ximg, yimg, tileWidth, tileHeight);
				if (hasContent(tileImage)) {
					Tile tile = new Tile();
					tile.setImage(tileImage);
					tile.setProperties(new Properties(properties));
					tileset.addNewTile(tile);
					layer.setTileAt(x, y, tile);
				}
			}
		}
	}

	private BufferedImage getSubimage(int x, int y, int w, int h) {
		if ((x + w) > image.getWidth() || (y + h) > image.getHeight()) {
			return null;
		} else {
			return image.getSubimage(x, y, w, h);
		}
	}

	private boolean hasContent(BufferedImage tileImage) {
		if (null != tileImage) {
			int w = image.getWidth();
			int h = image.getHeight();
			for (int y = 0; y < h; ++y) {
				for (int x = 0; x < w; ++x) {
					int rgba = image.getRGB(x, y);
					byte a = (byte) ((rgba >> 24) & 0xff);
					if (a != 0) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
