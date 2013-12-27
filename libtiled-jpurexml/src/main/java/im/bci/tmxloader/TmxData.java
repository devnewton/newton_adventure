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

import java.util.Scanner;

/**
 *
 * @author devnewton
 */
public class TmxData {

    private String encoding;
    private String data;

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void decodeTo(int width, int height, int[][] data) {
        switch (encoding) {
            case "csv":
                decodeCsvTo(width, height, data);
                break;
            default:
                throw new RuntimeException(
                        "Unsupported tiled layer data encoding: " + encoding);
        }
    }

    private void decodeCsvTo(int width, int height, int[][] gidArray) {
        try (Scanner scanner = new Scanner(this.data.trim())) {
            scanner.useDelimiter("[\\s]*,[\\s]*");
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    String str = scanner.next();
                    gidArray[x][y] = Integer.parseInt(str);
                }
            }
        }
    }
}
