package im.bci.newtonadv.world;

import im.bci.newtonadv.game.Drawable;

import java.util.ArrayList;
import java.util.List;

public class StaticPlatformDrawer {
	private List<StaticPlatformDrawable> drawables;
	private List<StaticPlatform> platforms = new ArrayList<StaticPlatform>();
	
	void add(StaticPlatform platform) {
		platforms.add(platform);
	}
	
	void postConstruct() {
		drawables = StaticPlatformDrawable.create(platforms);
		platforms.clear();
	}
	
	public void resetVisibles() {
		for(StaticPlatformDrawable drawable : drawables) {
			drawable.indices.rewind();
			drawable.indices.limit(drawable.indices.capacity());
		}
	}
	
	public void addVisible(StaticPlatform platform) {
		platform.drawable.indices.put(platform.vertexBufferIndex);
	}
	
	public void getVisibleDrawables(List<Drawable> visibleDrawables) {
		for(StaticPlatformDrawable drawable : drawables) {
			drawable.indices.flip();
			if(drawable.indices.limit()>0) {
				visibleDrawables.add(drawable);
			}
		}
	}

}
