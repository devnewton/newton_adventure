package im.bci.newtonadv.anim;

import im.bci.nanim.NanimParser.Nanim;
import im.bci.newtonadv.platform.interfaces.ITexture;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnimationCollection {
	
	LinkedHashMap<String/*animation name*/, Animation> animations;

	public AnimationCollection(Nanim nanim, Map<String, ITexture> textures) {
		animations = new LinkedHashMap<>(nanim.getAnimationsCount());
		for(im.bci.nanim.NanimParser.Animation nanimation : nanim.getAnimationsList()) {
			addAnimation(new Animation(nanimation,textures));
		}
	}
	
	public AnimationCollection(Nanim nanim, Map<String, ITexture> textures,
			String... animationNames) {
		HashSet<String> animationNamesSet = new HashSet<>(Arrays.asList(animationNames));
		animations = new LinkedHashMap<>(nanim.getAnimationsCount());
		for(im.bci.nanim.NanimParser.Animation nanimation : nanim.getAnimationsList()) {
			if(animationNamesSet.contains(nanimation.getName())) {
				addAnimation(new Animation(nanimation,textures));
			}
		}
	}
	
	public AnimationCollection(ITexture texture) {
		animations = new LinkedHashMap<>(1);
		Animation animation = new Animation("" + texture.getId());
		animation.addFrame(texture, Integer.MAX_VALUE);
		addAnimation(animation);
	}
        
            public AnimationCollection(ITexture texture, float u1, float v1, float u2, float v2) {
		animations = new LinkedHashMap<>(1);
		Animation animation = new Animation("" + texture.getId());
            AnimationFrame frame = animation.addFrame(texture, Integer.MAX_VALUE);
            frame.u1 = u1;
            frame.v1 = v1;
            frame.u2 = u2;
            frame.v2 = v2;
		addAnimation(animation);
	}

	public AnimationCollection() {
		animations = new LinkedHashMap<>();
	}

	public final void addAnimation(Animation animation) {
		animations.put(animation.getName(),animation);
	}
	
	public Animation getFirst() {
		return animations.values().iterator().next();
	}
	
	public Animation getAnimationByName(String name) {
		return animations.get(name);
	}
	
}
