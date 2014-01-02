package im.bci.newtonadv.anim;

import im.bci.newtonadv.platform.interfaces.ITexture;

import java.util.LinkedHashMap;

public class AnimationCollection {
    
    LinkedHashMap<String/*animation name*/, Animation> animations;
    private boolean ready;
    
    public AnimationCollection(ITexture texture) {
        animations = new LinkedHashMap<String, Animation>(1);
        Animation animation = new Animation("" + texture);
        animation.addFrame(texture, Integer.MAX_VALUE);
        addAnimation(animation);
        ready = true;
    }
    
    public AnimationCollection(ITexture texture, float u1, float v1, float u2, float v2) {
        animations = new LinkedHashMap<String, Animation>(1);
        Animation animation = new Animation("" + texture);
        animation.addFrame(texture, Integer.MAX_VALUE, u1, v1, u2, v2);
        addAnimation(animation);
        ready = true;
    }
    
    public AnimationCollection() {
        animations = new LinkedHashMap<String, Animation>();
        ready = false;
    }
    
    public final void addAnimation(Animation animation) {
        animations.put(animation.getName(), animation);
    }
    
    public IAnimation getFirst() {
        if (!animations.isEmpty()) {
            return animations.values().iterator().next();
        } else {
            return new NotReadyFirstAnimation();
        }
    }
    
    public IAnimation getAnimationByName(String name) {
        if (!animations.isEmpty()) {
            return animations.get(name);
        } else {
            return new NotReadyAnimationWithName(name);
        }
    }
    
    public boolean isReady() {
        return ready;
    }
    
    public void setReady(boolean ready) {
        this.ready = ready;
    }
    
    private abstract class NotReadyAnimation implements IAnimation {
        
        abstract Animation getWhenReady();
        
        @Override
        abstract public String getName();
        
        @Override
        public Play start() {
            return start(PlayMode.LOOP);
        }
        
        @Override
        public Play start(PlayMode mode) {
            Play play = new Play(this);
            play.start(mode);
            return play;
        }
        
        @Override
        public void stop(Play play) {
            play.stop();
        }
        
        @Override
        public AnimationFrame getFrame(int i) {
            Animation a = getWhenReady();
            if (null != a) {
                return a.getFrame(i);
            } else {
                return null;
            }
        }
        
        @Override
        public int getFrameCount() {
            Animation a = getWhenReady();
            if (null != a) {
                return a.getFrameCount();
            } else {
                return 0;
            }
        }
        
        @Override
        public long getTotalDuration() {
            Animation a = getWhenReady();
            if (null != a) {
                return a.getTotalDuration();
            } else {
                return 0;
            }
        }
    }
    
    private class NotReadyFirstAnimation extends NotReadyAnimation {
        
        @Override
        Animation getWhenReady() {
            if (ready) {
                return animations.values().iterator().next();
            } else {
                return null;
            }
        }
        
        @Override
        public String getName() {
            Animation a = getWhenReady();
            if (null != a) {
                return a.getName();
            } else {
                return "";
            }
        }
        
    }
    
    private class NotReadyAnimationWithName extends NotReadyAnimation {
        
        private final String name;
        
        public NotReadyAnimationWithName(String name) {
            this.name = name;
        }
        
        @Override
        Animation getWhenReady() {
            if (ready) {
                return animations.get(name);
            } else {
                return null;
            }
        }
        
        @Override
        public String getName() {
            return name;
        }
    }
    
}
