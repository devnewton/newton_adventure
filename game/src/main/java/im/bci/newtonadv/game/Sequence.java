/*
 * Copyright (c) 2009-2010 devnewton <devnewton@bci.im>
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
package im.bci.newtonadv.game;

/**
 *
 * @author devnewton
 */
public interface Sequence {

    static public abstract class AbstractTransitionException extends Throwable {

        private static final long serialVersionUID = -5802470623539315494L;
        protected Sequence nextSequence;

        public AbstractTransitionException(Sequence nextSequence) {
            this.nextSequence = nextSequence;
        }

        public Sequence getNextSequence() {
            return nextSequence;
        }

        public abstract void throwMe() throws NormalTransitionException, ResumableTransitionException, ResumeTransitionException;

        public abstract void startPreload();
    }

    static public class NormalTransitionException extends AbstractTransitionException {

        private static final long serialVersionUID = 8455803096542664269L;

        public NormalTransitionException(Sequence nextSequence) {
            super(nextSequence);
        }

        @Override
        public void throwMe() throws NormalTransitionException {
            throw this;
        }

        @Override
        public void startPreload() {
            if (nextSequence instanceof PreloadableSequence) {
                ((PreloadableSequence) nextSequence).prestart();
            }
        }
    }

    static public class ResumableTransitionException extends AbstractTransitionException {

        private static final long serialVersionUID = -1975859829767781443L;

        public ResumableTransitionException(Sequence nextSequence) {
            super(nextSequence);
        }

        @Override
        public void throwMe() throws ResumableTransitionException {
            throw this;
        }

        @Override
        public void startPreload() {
        }
    }

    static public class ResumeTransitionException extends AbstractTransitionException {

        private static final long serialVersionUID = -1654173561106215285L;

        public ResumeTransitionException(Sequence nextSequence) {
            super(nextSequence);
        }

        @Override
        public void throwMe() throws ResumeTransitionException {
            throw this;
        }

        @Override
        public void startPreload() {
        }
    }

    void start();

    void draw();

    void stop();

    void update() throws NormalTransitionException, ResumeTransitionException, ResumableTransitionException;

    void processInputs() throws NormalTransitionException, ResumeTransitionException, ResumableTransitionException;

    void resume();
}