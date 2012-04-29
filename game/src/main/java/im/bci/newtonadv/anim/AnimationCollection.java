package im.bci.newtonadv.anim;

import im.bci.nanim.NanimParser.Nanim;
import im.bci.newtonadv.platform.interfaces.ITexture;

import java.util.ArrayList;
import java.util.Map;

public class AnimationCollection {
	
	ArrayList<Animation> animations;

	public AnimationCollection(Nanim nanim, Map<String, ITexture> textures) {
		animations = new ArrayList<Animation>(nanim.getAnimationsCount());
		for(im.bci.nanim.NanimParser.Animation nanimation : nanim.getAnimationsList()) {
			animations.add(new Animation(nanimation,textures));
		}
	}
	
	public AnimationCollection(ITexture texture) {
		animations = new ArrayList<Animation>(1);
		Animation animation = new Animation();
		animation.addFrame(texture, Integer.MAX_VALUE);
		animations.add(animation);
	}

	public AnimationCollection() {
		animations = new ArrayList<Animation>();
	}

	public void addAnimation(Animation animation) {
		animations.add(animation);
	}
	
	public Animation getFirst() {
		return animations.get(0);
	}
	
}
