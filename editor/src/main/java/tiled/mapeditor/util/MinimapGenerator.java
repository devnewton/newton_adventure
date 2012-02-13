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

	BufferedImage image;

	public MinimapGenerator(int w, int h) {
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	}

	public void addLayer(TileLayer layer) {
		Graphics g = image.getGraphics();
		int rx = image.getWidth() / layer.getWidth();
		int ry = image.getHeight() / layer.getHeight();
		try {
			for (int x = 0; x < layer.getWidth(); ++x)
				for (int y = 0; y < layer.getHeight(); ++y) {
					g.setColor(getColor(layer.getTileAt(x, y)));
					g.fillRect(x * rx, y * ry, rx, ry);
				}
		} finally {
			g.dispose();
		}
	}

	private Color getColor(Tile tile) {
		if(null != tile)
		{
	        String c = tile.getProperties().getProperty("newton_adventure.type", "unknown");
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
	        	return new Color(255,255,0);
	        } else if (c.equals("door_to_bonus_world")) {
	        	return new Color(255,0,255);
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
	        }
		}
		return new Color(128, 128, 128, 128);
	}

	public void save(String selectedFile) {
		try {
			ImageIO.write(image, "png", new File(selectedFile));
		} catch (IOException e) {
			System.err.println(e);
		}
	}

}
