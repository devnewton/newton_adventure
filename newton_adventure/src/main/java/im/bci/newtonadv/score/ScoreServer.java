/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author Borome
 */
public class ScoreServer {

    private String serverUrl = "http://bci.im/scoreserver";
    private String player = "anonymous";
    private String secret = "c20d29ce-36dd-11e1-94e7-0016cba93a68";
    
    public ScoreServer(Properties properties) {
        serverUrl = properties.getProperty("scoreserver.url", serverUrl);
        player = properties.getProperty("scoreserver.player", player);
        secret = properties.getProperty("scoreserver.secret", secret);
    }

    public void sendScore(String level, int score) {
        try {
            URL url = new URL(serverUrl);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            try {
                String parameters = encodeScore(level, score);
                writer.write(parameters);
                writer.flush();
            } finally {
                writer.close();
            }
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                Logger.getLogger(ScoreServer.class.getName()).log(Level.INFO,line);
            }
        } catch (Exception ex) {
            Logger.getLogger(ScoreServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String encodeScore(String level, int score) throws UnsupportedEncodingException {
        return encodeParameter("level", level)
                + encodeParameter("player", player)
                + encodeParameter("secret", secret)
                + encodeParameter("score", "" + score);
    }

    private String encodeParameter(String key, String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
    }
}
