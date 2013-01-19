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
package im.bci.newtonadv.util;

/**
 *
 * @author devnewton
 */
public class MultidimensionnalIterator {

    private int dimensions[];
    private int[] indexes;

    public MultidimensionnalIterator(int[] dimensions) {
        this.dimensions = dimensions;
        this.indexes = new int[dimensions.length];

    }

    public boolean hasNext() {
        for (int i = 0; i < indexes.length; i++) {
            if (indexes[i] != dimensions[i] - 1) {
                return true;
            }
        }
        return false;
    }

    public int[] next() {
        for (int i = dimensions.length - 1; i >= 0; i--) {
            if (indexes[i] == dimensions[i] - 1) {
                indexes[i] = 0;
            } else {
                ++indexes[i];
                break;
            }
        }
        return indexes;
    }

/*    public static void main(String[] args) {
        MultidimensionnalIterator it = new MultidimensionnalIterator(new int[]{3, 5, 7});
        while (it.hasNext()) {
            for (int i : it.next()) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }*/
}
