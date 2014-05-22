/*
 * Copyright (c) 2013 devnewton <devnewton@bci.im>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'devnewton <devnewton@bci.im>' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package im.bci.newtonadv.ui;

import im.bci.jnuit.NuitPreferences;
import im.bci.jnuit.NuitToolkit;
import im.bci.jnuit.widgets.Button;
import im.bci.jnuit.widgets.Label;
import im.bci.jnuit.widgets.Select;
import im.bci.jnuit.widgets.Table;
import im.bci.jnuit.widgets.Toggle;
import im.bci.newtonadv.platform.interfaces.IGameView;
import java.util.ArrayList;

public class TweaksGUI extends Table {
    
    private Select<String> shaders;

    public TweaksGUI(IGameView view, NuitToolkit toolkit, final NuitPreferences config) {
        super(toolkit);
        this.defaults().expand();
        cell(new Label(toolkit, "tweaks.show.fps"));
        final Toggle mustDrawFps = new Toggle(toolkit);
        mustDrawFps.setEnabled(config.getBoolean("tweaks.show.fps", false));
        cell(mustDrawFps);
        row();
        cell(new Label(toolkit, "tweaks.rotate.view.with.gravity"));
        final Toggle rotateViewWithGravity = new Toggle(toolkit);
        rotateViewWithGravity.setEnabled(config.getBoolean("tweaks.rotate.view.with.gravity", true));
        cell(rotateViewWithGravity);
        row();
        
        if(!view.listShaders().isEmpty()) {
            cell(new Label(toolkit, "tweaks.shader"));
            shaders = new Select<String>(toolkit, new ArrayList<String>(view.listShaders()));
            shaders.setSelected(config.getString("tweaks.shader", "NORMAL"));
            cell(shaders);
            row();
        }
        cell(new Button(toolkit, "tweaks.back") {

            @Override
            public void onOK() {
                config.putBoolean("tweaks.show.fps", mustDrawFps.isEnabled());
                config.putBoolean("tweaks.rotate.view.with.gravity", rotateViewWithGravity.isEnabled());
                if(null != shaders) {
                    config.putString("tweaks.shader", shaders.getSelected());
                }
                TweaksGUI.this.close();
            }
        });
    }
}
