/*
 * Copyright (c) 2014 devnewton <devnewton@bci.im>
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

import im.bci.jnuit.background.ColoredBackground;
import im.bci.jnuit.focus.ColoredRectangleFocusCursor;
import im.bci.jnuit.text.TextColor;
import im.bci.jnuit.visitors.WidgetVisitor;
import im.bci.jnuit.widgets.AudioConfigurator;
import im.bci.jnuit.widgets.Button;
import im.bci.jnuit.widgets.Container;
import im.bci.jnuit.widgets.ControlsConfigurator;
import im.bci.jnuit.widgets.Label;
import im.bci.jnuit.widgets.NullWidget;
import im.bci.jnuit.widgets.Select;
import im.bci.jnuit.widgets.Stack;
import im.bci.jnuit.widgets.Table;
import im.bci.jnuit.widgets.Toggle;
import im.bci.jnuit.widgets.VideoConfigurator;
import im.bci.jnuit.widgets.Widget;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class Themer implements WidgetVisitor {

    private static final ColoredBackground BUTTON_FOCUSED_BACKGROUND = new ColoredBackground(0.6f, 0f, 0f, 1f);
    private static final ColoredRectangleFocusCursor BUTTON_FOCUS_CURSOR = new ColoredRectangleFocusCursor(1, 0, 0, 1);
    private static final ColoredRectangleFocusCursor SELECT_SUCKED_FOCUS_CURSOR = new ColoredRectangleFocusCursor(0.8f, 0, 0, 1);
    private static final ColoredBackground BUTTON_BACKGROUND = new ColoredBackground(0.3f, 0f, 0f, 1f);
    private static final TextColor YELLOW = new TextColor(1f, 0.88f, 0.12f, 1f);

    public void theme(Widget w) {
        if (null != w) {
            w.accept(this);
            for (Widget child : w.getChildren()) {
                theme(child);
            }
        }
    }

    @Override
    public void visit(Button widget) {
        widget.setBackground(BUTTON_BACKGROUND);
        widget.setFocusedBackground(BUTTON_FOCUSED_BACKGROUND);
        widget.setSuckedFocusedBackground(BUTTON_FOCUSED_BACKGROUND);
        widget.setFocusCursor(BUTTON_FOCUS_CURSOR);
        widget.setFocusedTextColor(YELLOW);
    }

    @Override
    public void visit(Container widget) {
    }

    @Override
    public void visit(Table widget) {
    }

    @Override
    public void visit(ControlsConfigurator widget) {
    }

    @Override
    public void visit(AudioConfigurator widget) {
    }

    @Override
    public void visit(VideoConfigurator widget) {
    }

    @Override
    public void visit(Label widget) {
    }

    @Override
    public void visit(NullWidget widget) {
    }

    @Override
    public void visit(Select widget) {
        widget.setBackground(BUTTON_BACKGROUND);
        widget.setFocusedBackground(BUTTON_FOCUSED_BACKGROUND);
        widget.setSuckedFocusedBackground(BUTTON_FOCUSED_BACKGROUND);
        widget.setFocusCursor(BUTTON_FOCUS_CURSOR);
        widget.setSuckedFocusCursor(SELECT_SUCKED_FOCUS_CURSOR);
        widget.setFocusedTextColor(YELLOW);
    }

    @Override
    public void visit(Stack widget) {
    }

    @Override
    public void visit(Toggle widget) {
        widget.setBackground(BUTTON_BACKGROUND);
        widget.setFocusedBackground(BUTTON_FOCUSED_BACKGROUND);
        widget.setSuckedFocusedBackground(BUTTON_FOCUSED_BACKGROUND);
        widget.setFocusCursor(BUTTON_FOCUS_CURSOR);
        widget.setFocusedTextColor(YELLOW);
    }

    @Override
    public void visit(ControlsConfigurator.ControlConfigurator widget) {
    }

}
