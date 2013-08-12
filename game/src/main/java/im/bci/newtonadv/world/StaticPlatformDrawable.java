package im.bci.newtonadv.world;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.shapes.Shape;
import im.bci.newtonadv.game.Drawable;
import im.bci.newtonadv.platform.interfaces.ITexture;
import im.bci.newtonadv.util.ShapeUtils;

public class StaticPlatformDrawable implements Drawable {
	private int zOrder;
	public ITexture texture;
    public FloatBuffer vertices;
    public FloatBuffer texCoords;
    public IntBuffer indices;
    private static final Comparator<StaticPlatform> comparator = new Comparator<StaticPlatform>() {

		@Override
		public int compare(StaticPlatform o1, StaticPlatform o2) {
			int result = Integer.compare(o1.getZOrder(), o2.getZOrder());
			if(result == 0) {
				result = Integer.compare(o1.getTexture().getId(), o2.getTexture().getId());
			}
			return result;
		}
		
	};
    
    static List<StaticPlatformDrawable> create(List<StaticPlatform> platforms) {
    	List<StaticPlatformDrawable> result = new ArrayList<StaticPlatformDrawable>();
    	java.util.Collections.sort(platforms, comparator);
    	StaticPlatform previousPlatform = null;
    	StaticPlatformDrawable currentDrawable = null;
    	List<StaticPlatform> currentPlatforms = new ArrayList<StaticPlatform>();
		currentDrawable = new StaticPlatformDrawable();
    	for(StaticPlatform platform : platforms) {
    		if(null != previousPlatform && 0 != comparator.compare(previousPlatform, platform)) {
    			currentDrawable.texture = previousPlatform.getTexture();
    			currentDrawable.zOrder = previousPlatform.getZOrder();
    			currentDrawable.addStaticPlatforms(currentPlatforms);
    			result.add(currentDrawable);
    			currentDrawable = new StaticPlatformDrawable();
    			currentPlatforms.clear();
    		}
    		previousPlatform = platform;
    		platform.drawable = currentDrawable;
    		currentPlatforms.add(platform);
    	}
    	return result;
    }
    
    
    private void addStaticPlatforms(List<StaticPlatform> platforms) {
        final int nb = platforms.size();
		vertices = ByteBuffer.allocateDirect(nb * 2 * 4 * Float.SIZE / 8)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        texCoords = ByteBuffer.allocateDirect(nb * 2 * 4 * Float.SIZE / 8)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        indices = ByteBuffer.allocateDirect(nb * Integer.SIZE / 8)
                .order(ByteOrder.nativeOrder()).asIntBuffer();
    	for(int i=0; i<nb; ++i) {
    		StaticPlatform platform = platforms.get(i);
    		platform.vertexBufferIndex = i;
    		addStaticPlatform(platform);
    	}
    }
    
    private void addStaticPlatform(StaticPlatform platform) {
        Shape shape = platform.getShape();
        Vector2f[] points = ShapeUtils.getVertices(shape,  platform.getPosition(),
        		 platform.getRotation());
        int nbPoints = Math.min(points.length, vertices.capacity());
        for (int i = 0; i < nbPoints; ++i) {
            Vector2f point = points[i];
            vertices.put(point.x);
            vertices.put(point.y);
        }
        
        texCoords.put(0);
        texCoords.put(1);
        texCoords.put(1);
        texCoords.put(1);
        texCoords.put(1);
        texCoords.put(0);
        texCoords.put(0);
        texCoords.put(0);
    }

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getZOrder() {
		return zOrder;
	}

}
