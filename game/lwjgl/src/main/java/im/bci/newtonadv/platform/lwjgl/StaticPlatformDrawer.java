package im.bci.newtonadv.platform.lwjgl;

import im.bci.newtonadv.game.Drawable;
import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.newtonadv.world.IStaticPlatformDrawer;
import im.bci.newtonadv.world.StaticPlatform;

import java.util.ArrayList;
import java.util.List;

public class StaticPlatformDrawer implements IStaticPlatformDrawer {

    private List<StaticPlatformDrawable> drawables;
    private final List<StaticPlatform> platforms = new ArrayList<StaticPlatform>();

    @Override
    public void add(StaticPlatform platform) {
        platforms.add(platform);
    }

    @Override
    public void postConstruct(IGameView view) {
        drawables = StaticPlatformDrawable.create(view, platforms);
        platforms.clear();
    }

    @Override
    public void resetVisibles() {
        for (StaticPlatformDrawable drawable : drawables) {
            drawable.indices.rewind();
            drawable.indices.limit(drawable.indices.capacity());
        }
    }

    @Override
    public void addVisible(StaticPlatform platform) {
        int v = platform.vertexBufferIndex * 4;
        for (int i = 0; i < 4; ++i) {
            ((StaticPlatformDrawable) platform.drawable).indices.put(v + i);
        }
    }

    @Override
    public void getVisibleDrawables(List<Drawable> visibleDrawables) {
        for (StaticPlatformDrawable drawable : drawables) {
            drawable.indices.flip();
            if (drawable.indices.limit() > 0) {
                visibleDrawables.add(drawable);
            }
        }
    }

}
