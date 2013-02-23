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
package im.bci.newtonadv.platform.lwjgl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author devnewton
 */
public class RuntimeUtils {

    public static String getApplicationDir() throws IOException {
        try {
            return new File(RuntimeUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        } catch (URISyntaxException uriEx) {
            Logger.getLogger(RuntimeUtils.class.getName()).log(Level.WARNING,
                    "Cannot find application directory, try current", uriEx);
            return new File(".").getCanonicalPath();
        }
    }

    public static String getApplicationParentDir() throws IOException {
        return (new File(getApplicationDir())).getParent();
    }

    public static Properties loadPropertiesFromFile(File f) {
        try {
            FileInputStream fs = new FileInputStream(f);
            try {
                Properties config = new Properties();
                config.load(fs);
                return config;
            } finally {
                fs.close();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Properties loadPropertiesFromFile(String f) {
        return loadPropertiesFromFile(new File(f));
    }

    static List<String> getPropertyAsList(Properties properties, String name) {
        String value = properties.getProperty(name, "").trim();
        if (!value.isEmpty()) {
            return Arrays.asList(value.trim().split(","));
        } else {
            return Collections.emptyList();
        }
    }
}