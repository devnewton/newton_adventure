package org.tuxfamily.newtonadv.world;

import net.phys2d.math.ROVector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;
import org.lwjgl.opengl.GL11;
import org.tuxfamily.newtonadv.Texture;
import org.tuxfamily.newtonadv.game.Drawable;

strictfp class FireBall extends Body  implements Drawable {
    public static final float size = Cannon.size / 2.0f;
    World world;
    private Texture texture;
    FireBall(World world) {
        super(new Circle(size/2.0f), 40.0f);
        this.world = world;
    }

    void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public strictfp void collided(Body other) {
        if (other instanceof Hero) {
            Hero hero = (Hero) other;
            if (hero.isInvincible()) {
                return;
            }
            hero.hurtByFireBall();
        }
        world.remove(this);
        world.addTopLevelEntities(new Explosion(world, this.getPosition()));
    }
    
    public void draw() {
        GL11.glPushMatrix();
        ROVector2f pos = getPosition();
        GL11.glTranslatef(pos.getX(), pos.getY(), 0.0f);
        GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0, 1.0f);
        final float x1 = -size / 2.0f;
        final float x2 = size / 2.0f;
        final float y1 = -size / 2.0f;
        final float y2 = size / 2.0f;

        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);
        GL11.glEnable (GL11.GL_BLEND);
        GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        texture.bind();

        final float u1 = 0.0f,  u2 = 1.0f;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, 0.0f);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, 0.0f);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, 1.0f);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, 1.0f);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
    
}