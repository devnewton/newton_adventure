package im.bci.newtonadv.anim;

import im.bci.nanim.NanimParser.Nanim;
import im.bci.newtonadv.platform.interfaces.ITexture;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnimationCollection {
	
	LinkedHashMap<String/*animation name*/, Animation> animations;
	
	public AnimationCollection(AnimationCollection other) {
		animations = new LinkedHashMap<String, Animation>(other.animations.size());
		for(Animation otherAnimation : other.animations.values()) {
			addAnimation(new Animation(otherAnimation));
		}
	}

	public AnimationCollection(Nanim nanim, Map<String, ITexture> textures) {
		animations = new LinkedHashMap<String, Animation>(nanim.getAnimationsCount());
		for(im.bci.nanim.NanimParser.Animation nanimation : nanim.getAnimationsList()) {
			addAnimation(new Animation(nanimation,textures));
		}
	}
	
	public AnimationCollection(Nanim nanim, Map<String, ITexture> textures,
			String... animationNames) {
		HashSet<String> animationNamesSet = new HashSet<String>(Arrays.asList(animationNames));
		animations = new LinkedHashMap<String, Animation>(nanim.getAnimationsCount());
		for(im.bci.nanim.NanimParser.Animation nanimation : nanim.getAnimationsList()) {
			if(animationNamesSet.contains(nanimation.getName())) {
				addAnimation(new Animation(nanimation,textures));
			}
		}
	}
	
	public AnimationCollection(ITexture texture) {
		animations = new LinkedHashMap<String, Animation>(1);
		Animation animation = new Animation("" + texture.getId());
		animation.addFrame(texture, Integer.MAX_VALUE);
		addAnimation(animation);
	}

	public AnimationCollection() {
		animations = new LinkedHashMap<String, Animation>();
	}

	public void addAnimation(Animation animation) {
		animations.put(animation.getName(),animation);
	}
	
	public Animation getFirst() {
		return animations.values().iterator().next();
	}
	
	public Animation getAnimationByName(String name) {
		return animations.get(name);
	}
	
}
