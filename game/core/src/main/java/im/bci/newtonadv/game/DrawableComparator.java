package im.bci.newtonadv.game;

import java.util.Comparator;

public class DrawableComparator implements Comparator<Drawable> {

	@Override
	public int compare(Drawable o1, Drawable o2) {
		int z1 = o1.getZOrder(), z2 = o2.getZOrder();
		if (z1 < z2)
			return -1;
		else if (z2 < z1)
			return 1;
		else
			return 0;
	}

}
