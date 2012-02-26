package im.bci.newtonadv.game;

public abstract class AbstractEntity implements Entity {

	private int zorder = 0;

	@Override
	public int getZOrder() {
		return zorder;
	}

	public void setZOrder(int z) {
		zorder = z;
	}

}
