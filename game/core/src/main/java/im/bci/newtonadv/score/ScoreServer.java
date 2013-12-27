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
package im.bci.newtonadv.score;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author devnewton
 */
public class ScoreServer {
    private static final Logger logger = Logger.getLogger(ScoreServer.class.getName());

    private String serverUrl = "http://devnewton.bci.im/scoreserver";
    private String player = "anonymous";
    private String secret = "c20d29ce-36dd-11e1-94e7-0016cba93a68";
    private boolean scoreShareEnabled;

    public ScoreServer(Properties config) {
        serverUrl = config.getProperty("scoreserver.url", serverUrl);
        player = config.getProperty("scoreserver.player", player);
        secret = config.getProperty("scoreserver.secret", secret);
        scoreShareEnabled = "true".equals(config.getProperty("scoreserver.share", "" + scoreShareEnabled));
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isScoreShareEnabled() {
        return scoreShareEnabled;
    }

    public void setScoreShareEnabled(boolean scoreShareEnabled) {
        this.scoreShareEnabled = scoreShareEnabled;
    }

    public void sendScore(String level, int score) {
        if (scoreShareEnabled) {
            try {
                logger.log(Level.INFO, "Send score to {0}", serverUrl);
                String hurle = serverUrl + "/score/";
                URL url = new URL(hurle);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                try (OutputStreamWriter writer = new OutputStreamWriter(
                        conn.getOutputStream())) {
                    String parameters = encodeScore(level, score);
                    writer.write(parameters);
                    writer.flush();
                }
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    logger.log(Level.INFO,
                            line);
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE,
                        null, ex);
            }
        }
    }

    private String encodeScore(String level, int score)
            throws UnsupportedEncodingException {
        return encodeParameter("level", level) + '&'
                + encodeParameter("player", player) + '&'
                + encodeParameter("secret", secret) + '&'
                + encodeParameter("score", "" + score);
    }

    private String encodeParameter(String key, String value)
            throws UnsupportedEncodingException {
        return URLEncoder.encode(key, "UTF-8") + "="
                + URLEncoder.encode(value, "UTF-8");
    }
}
