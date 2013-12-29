package im.bci.newtonadv.anim;

import im.bci.newtonadv.platform.interfaces.ITexture;

import java.util.LinkedHashMap;

public class AnimationCollection {

    LinkedHashMap<String/*animation name*/, Animation> animations;
    private boolean ready;

    public AnimationCollection(ITexture texture) {
        animations = new LinkedHashMap<>(1);
        Animation animation = new Animation("" + texture.getId());
        animation.addFrame(texture, Integer.MAX_VALUE);
        addAnimation(animation);
        ready = true;
    }

    public AnimationCollection(ITexture texture, float u1, float v1, float u2, float v2) {
        animations = new LinkedHashMap<>(1);
        Animation animation = new Animation("" + texture.getId());
        animation.addFrame(texture, Integer.MAX_VALUE, u1, v1, u2, v2);
        addAnimation(animation);
        ready = true;
    }

    public AnimationCollection() {
        animations = new LinkedHashMap<>();
        ready = false;
    }

    public final void addAnimation(Animation animation) {
        animations.put(animation.getName(), animation);
    }

    public Animation getFirst() {
        return animations.values().iterator().next();
    }

    public Animation getAnimationByName(String name) {
        return animations.get(name);
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

}
