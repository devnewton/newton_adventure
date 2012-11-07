package tiled.mapeditor.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import tiled.core.Tile;
import tiled.core.TileLayer;

public class MinimapGenerator {

	final BufferedImage image;
	final int tileSize;

	public MinimapGenerator(int imageWidth, int imageHeight, int tileSize) {
		image = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_INT_ARGB);
		this.tileSize = tileSize;
		clearBackground();
	}

	private void clearBackground() {
		Graphics g = image.getGraphics();
		try {
			g.setColor(new Color(128, 128, 128, 128));
			g.fillRect(0, 0, image.getWidth(), image.getHeight());
		} finally {
			g.dispose();
		}
	}

	public void addLayer(TileLayer layer) {
		Graphics g = image.getGraphics();
		try {
			for (int x = 0; x < layer.getWidth(); ++x)
				for (int y = 0; y < layer.getHeight(); ++y) {
					Color color = getColor(layer.getTileAt(x, y));
					if (null != color) {
						g.setColor(color);
						g.fillRect(x * tileSize, y * tileSize, tileSize,
								tileSize);
					}
				}
		} finally {
			g.dispose();
		}
	}

	private Color getColor(Tile tile) {
		if (null != tile) {
			String c = tile.getProperties().getProperty(
					"newton_adventure.type", "unknown");
			if (c.equals("platform")) {
				return new Color(0, 0, 237);
			} else if (c.equals("up_right_half_platform")) {
				return new Color(0, 0, 237);
			} else if (c.equals("up_left_half_platform")) {
				return new Color(0, 0, 237);
			} else if (c.equals("down_left_half_platform")) {
				return new Color(0, 0, 237);
			} else if (c.equals("down_right_half_platform")) {
				return new Color(0, 0, 237);
			} else if (c.equals("door")) {
				return new Color(255, 255, 0);
			} else if (c.equals("door_to_bonus_world")) {
				return new Color(255, 0, 255);
			} else if (c.equals("pikes_up")) {
				return new Color(255, 0, 0);
			} else if (c.equals("pikes_down")) {
				return new Color(255, 0, 0);
			} else if (c.equals("pikes_left")) {
				return new Color(255, 0, 0);
			} else if (c.equals("pikes_right")) {
				return new Color(255, 0, 0);
			} else if (c.equals("cannon_up")) {
				return new Color(255, 0, 0);
			} else if (c.equals("cannon_down")) {
				return new Color(255, 0, 0);
			} else if (c.equals("cannon_right")) {
				return new Color(255, 0, 0);
			} else if (c.equals("cannon_left")) {
				return new Color(255, 0, 0);
			} else if (c.equals("mobile_pike_anchor")) {
				return new Color(255, 0, 0);
			} else if (c.equals("axe_anchor")) {
				return new Color(255, 0, 0);
			} else if (c.equals("bounce_platform")) {
				return new Color(0, 0, 237);
			} else if (c.equals("activator1")) {
				return new Color(80, 255, 40);
			} else if (c.equals("activator2")) {
				return new Color(80, 255, 40);
			} else if (c.equals("activator3")) {
				return new Color(80, 255, 40);
			} else if (c.equals("blocker1")) {
				return new Color(80, 255, 40);
			} else if (c.equals("blocker2")) {
				return new Color(80, 255, 40);
			} else if (c.equals("blocker3")) {
				return new Color(80, 255, 40);
			} else if (!c.equals("unknown")) {
				System.err.println("Unhandled tile type " + c);
			}
		}
		return null;
	}

	public void save(String selectedFile) {
		try {
			ImageIO.write(image, "png", new File(selectedFile));
		} catch (IOException e) {
			System.err.println(e);
		}
	}

}
