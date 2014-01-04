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
package im.bci.newtonadv.platform.playn.core;

import playn.core.Assets;
import playn.core.Image;
import playn.core.Sound;
import playn.core.util.Callback;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class RealWatchedAssets implements Assets {

    private final Assets delegate;

    private int totalRequestsCount = 0;
    private int successCount = 0;
    private int errorsCount = 0;

    private final Callback<Object> callback = new Callback<Object>() {
        @Override
        public void onSuccess(Object resource) {
            ++successCount;
        }

        @Override
        public void onFailure(Throwable e) {
            ++errorsCount;
        }
    };

    public RealWatchedAssets(Assets delegate) {
        this.delegate = delegate;
    }

    @Override
    public Image getImageSync(String path) {
        ++totalRequestsCount;
        Image image = delegate.getImageSync(path);
        image.addCallback(callback);
        return image;
    }

    @Override
    public Image getImage(String path) {
        ++totalRequestsCount;
        Image image = delegate.getImage(path);
        image.addCallback(callback);
        return image;
    }

    @Override
    public Image getRemoteImage(String url) {
        ++totalRequestsCount;
        Image image = delegate.getRemoteImage(url);
        image.addCallback(callback);
        return image;
    }

    @Override
    public Image getRemoteImage(String url, float width, float height) {
        ++totalRequestsCount;
        Image image = delegate.getRemoteImage(url, width, height);
        image.addCallback(callback);
        return image;
    }

    @Override
    public Sound getSound(String path) {
        ++totalRequestsCount;
        Sound sound = delegate.getSound(path);
        sound.addCallback(callback);
        return sound;
    }

    @Override
    public Sound getMusic(String path) {
        ++totalRequestsCount;
        Sound sound = delegate.getSound(path);
        sound.addCallback(callback);
        return sound;
    }

    @Override
    public String getTextSync(String path) throws Exception {
        return delegate.getTextSync(path);
    }

    @Override
    public void getText(String path, final Callback<String> textCallback) {
        ++totalRequestsCount;

        delegate.getText(path, new Callback<String>() {

            @Override
            public void onSuccess(String result) {
                textCallback.onSuccess(result);
                ++successCount;
            }

            @Override
            public void onFailure(Throwable cause) {
                textCallback.onFailure(cause);
                ++errorsCount;
            }
        });
    }

    @Override
    public byte[] getBytesSync(String path) throws Exception {
        return delegate.getBytesSync(path);
    }

    @Override
    public void getBytes(String path, final Callback<byte[]> bytesCallback) {
        ++totalRequestsCount;
        delegate.getBytes(path, new Callback<byte[]>() {

            @Override
            public void onSuccess(byte[] result) {
                bytesCallback.onSuccess(result);
                ++successCount;
            }

            @Override
            public void onFailure(Throwable cause) {
                bytesCallback.onFailure(cause);
                ++errorsCount;
            }
        });
    }

    boolean isDone() {
        return totalRequestsCount == successCount + errorsCount;
    }

}
