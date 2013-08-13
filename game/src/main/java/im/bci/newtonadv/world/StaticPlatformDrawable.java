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
import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.newtonadv.platform.interfaces.ITexture;
import im.bci.newtonadv.util.ShapeUtils;

public class StaticPlatformDrawable implements Drawable {
	private int zOrder;
	private IGameView view;
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
    
    static List<StaticPlatformDrawable> create(IGameView view, List<StaticPlatform> platforms) {
    	List<StaticPlatformDrawable> result = new ArrayList<StaticPlatformDrawable>();
    	java.util.Collections.sort(platforms, comparator);    
    	for(List<StaticPlatform> partitionedPlatforms : partition(platforms)) {
    		if(!partitionedPlatforms.isEmpty()) {
    			StaticPlatformDrawable drawable = new StaticPlatformDrawable();
    			StaticPlatform p = partitionedPlatforms.get(0);
    			drawable.view = view;
				drawable.texture = p.getTexture();
    			drawable.zOrder = p.getZOrder();
    			drawable.addStaticPlatforms(partitionedPlatforms);
    			result.add(drawable);
    		}
    	}
    	return result;
    }
    
    private static List<List<StaticPlatform>> partition(List<StaticPlatform> platforms) {
    	List<List<StaticPlatform>> result = new ArrayList<List<StaticPlatform>>();    	
    	StaticPlatform previousPlatform = null;
    	List<StaticPlatform> currentPlatforms = null;
    	for(StaticPlatform platform : platforms) {
    		if(null == currentPlatforms || 0 != comparator.compare(previousPlatform, platform)) {
    			currentPlatforms = new ArrayList<StaticPlatform>();
    			result.add(currentPlatforms);
    		}
    		currentPlatforms.add(platform);
    		previousPlatform = platform;
    	}
    	return result;
    }
    
    
    private void addStaticPlatforms(List<StaticPlatform> platforms) {
        final int nb = platforms.size();
		vertices = ByteBuffer.allocateDirect(nb * 2 * 4 * Float.SIZE / 8)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        texCoords = ByteBuffer.allocateDirect(nb * 2 * 4 * Float.SIZE / 8)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        indices = ByteBuffer.allocateDirect(nb * 4 * Integer.SIZE / 8)
                .order(ByteOrder.nativeOrder()).asIntBuffer();
    	for(int i=0; i<nb; ++i) {
    		StaticPlatform platform = platforms.get(i);
    		platform.vertexBufferIndex = i;
    		addStaticPlatform(platform);
    	}
    	vertices.flip();
    	texCoords.flip();
    }
    
    private void addStaticPlatform(StaticPlatform platform) {
    	platform.drawable = this;
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
		view.drawStaticPlatforms(this);
	}

	@Override
	public int getZOrder() {
		return zOrder;
	}

}
