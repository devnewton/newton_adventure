/*
 *  Copyright (c) 2009-2010 devnewton <devnewton@bci.im>
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 * 
 *  *  Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 *  *  Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 *  *  Neither the name of 'devnewton <devnewton@bci.im>' nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package im.bci.newtonadv.game;

import im.bci.newtonadv.Game;

/**
 *
 * @author devnewton
 */
public class FadeSequence implements Sequence {
    private final Sequence nextSequence;
    private final float r, g, b;
    private float a;
    private final Game game;
    private final long duration;
    private long endTime;
	private FadeSequenceTransition transition;
    
    public enum FadeSequenceTransition {
    	NORMAL,
    	RESUME,
    	RESUMABLE
    }

    public FadeSequence(Game game, Sequence nextSequence, float r, float g, float b, long duration, FadeSequenceTransition transition) {
        this.game = game;
        this.nextSequence = nextSequence;
        this.r = r;
        this.g = g;
        this.b = b;
        this.duration = duration;
        this.transition = transition;
    }

    @Override
	public void start() {
        endTime = game.getFrameTimeInfos().currentTime + duration;
    }

    @Override
	public void draw() {
        game.getView().drawFadeSequence(r,g,b,a);
    }

    @Override
	public void stop() {
    }

    @Override
	public void update() throws Sequence.NormalTransitionException, ResumeTransitionException, ResumableTransitionException {
        long remaining = endTime - game.getFrameTimeInfos().currentTime;
        if(remaining <= 0) {
        	switch(transition) {
        	case NORMAL:
        		throw new NormalTransitionException(nextSequence);
        	case RESUME:
        		throw new ResumeTransitionException(nextSequence);
        	case RESUMABLE:
        		throw new ResumableTransitionException(nextSequence);
        	}
        }
        a = 1.0f - (float)remaining / (float)duration;
    }

    @Override
	public void processInputs() {
    }

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

}
