/*
 The MIT License (MIT)

 Copyright (c) 2013 devnewton <devnewton@bci.im>

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */
package im.bci.tmxloader;

import java.util.EnumSet;
/**
 *
 * @author devnewton
 */
public class TmxTileInstance {

    private final TmxTile tile;
    private final EnumSet<TmxTileInstanceEffect> effect;

    public TmxTileInstance(TmxTile tile, EnumSet<TmxTileInstanceEffect> effect) {
        this.tile = tile;
        this.effect = effect;
    }

    public TmxTile getTile() {
        return tile;
    }

    public EnumSet<TmxTileInstanceEffect> getEffect() {
        return effect;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.tile != null ? this.tile.hashCode() : 0);
        hash = 89 * hash + (this.effect != null ? this.effect.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TmxTileInstance other = (TmxTileInstance) obj;
        if (!TmxUtils.equals(this.tile, other.tile)) {
            return false;
        }
        if (!TmxUtils.equals(this.effect, other.effect)) {
            return false;
        }
        return true;
    }

    public String getProperty(String name, String defaultValue) {
        return tile.getProperty(name, defaultValue);
    }

    public int getWidth() {
        return tile.getWidth();
    }

    public int getHeight() {
        return tile.getHeight();
    }
    
    public TmxFrame getFrame() {
        return tile.getFrame();
    }
}
