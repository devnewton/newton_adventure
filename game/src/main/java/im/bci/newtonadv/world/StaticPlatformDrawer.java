package im.bci.newtonadv.world;

import im.bci.newtonadv.game.Drawable;
import im.bci.newtonadv.platform.interfaces.IGameView;

import java.util.ArrayList;
import java.util.List;

public class StaticPlatformDrawer {
	private List<StaticPlatformDrawable> drawables;
	private List<StaticPlatform> platforms = new ArrayList<StaticPlatform>();
	
	void add(StaticPlatform platform) {
		platforms.add(platform);
	}
	
	void postConstruct(IGameView view) {
		drawables = StaticPlatformDrawable.create(view, platforms);
		platforms.clear();
	}
	
	public void resetVisibles() {
		for(StaticPlatformDrawable drawable : drawables) {
			drawable.indices.rewind();
			drawable.indices.limit(drawable.indices.capacity());
		}
	}
	
	public void addVisible(StaticPlatform platform) {
		int v = platform.vertexBufferIndex * 4;
		for(int i=0; i<4; ++i) {
			platform.drawable.indices.put(v + i);
		}
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
