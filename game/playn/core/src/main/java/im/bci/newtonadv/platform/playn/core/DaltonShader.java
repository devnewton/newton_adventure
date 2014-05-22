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
package im.bci.newtonadv.platform.playn.core;

import playn.core.gl.GLContext;
import playn.core.gl.IndexedTrisShader;

/**
 *
 * @author devnewton
 *
 * Inspired by
 * http://blog.noblemaster.com/2013/10/26/opengl-shader-to-correct-and-simulate-color-blindness-experimental/
 *
 */
public class DaltonShader extends IndexedTrisShader {

    public enum BlindnessFilter {

        PROTANOPIA,
        DEUTERANOPIA,
        TRITANOPIA
    }

    private BlindnessFilter blindnessFilter = BlindnessFilter.TRITANOPIA;

    public DaltonShader(GLContext ctx, BlindnessFilter blindnessFilter) {
        super(ctx);
        this.blindnessFilter = blindnessFilter;
    }

    @Override
    protected String textureFragmentShader() {
        String blindnessFilterData = "";
        String blindnessFilterCode = "";
        if (blindnessFilter != null) {
            switch (blindnessFilter) {
                case PROTANOPIA: {
                    blindnessFilterCode = "vec3 opponentColor = RGBtoOpponentMat * vec3(fragColor.r, fragColor.g, fragColor.b);\n"
                            + "opponentColor.x -= opponentColor.y * 1.5;                                           \n"
                            + "vec3 rgbColor = OpponentToRGBMat * opponentColor;                                   \n"
                            + "fragColor = vec4(rgbColor.r, rgbColor.g, rgbColor.b, fragColor.a);                  \n";
                    break;
                }
                case DEUTERANOPIA: {
                    blindnessFilterCode = "vec3 opponentColor = RGBtoOpponentMat * vec3(fragColor.r, fragColor.g, fragColor.b);\n"
                            + "opponentColor.x -= opponentColor.y * 1.5;                                           \n"
                            + "vec3 rgbColor = OpponentToRGBMat * opponentColor;                                   \n"
                            + "fragColor = vec4(rgbColor.r, rgbColor.g, rgbColor.b, fragColor.a);                  \n";
                    break;
                }
                case TRITANOPIA: {
                    blindnessFilterCode = "vec3 opponentColor = RGBtoOpponentMat * vec3(fragColor.r, fragColor.g, fragColor.b);\n"
                            + "opponentColor.x -= ((3.0 * opponentColor.z) - opponentColor.y) * 0.25;                \n"
                            + "vec3 rgbColor = OpponentToRGBMat * opponentColor;                                   \n"
                            + "fragColor = vec4(rgbColor.r, rgbColor.g, rgbColor.b, fragColor.a);                  \n";
                    break;
                }
                default:
                    throw new RuntimeException("Color filter not implemented for " + blindnessFilter);
            }
            blindnessFilterData = "const mat3 RGBtoOpponentMat = mat3(0.2814, -0.0971, -0.0930, 0.6938, 0.1458,-0.2529, 0.0638, -0.0250, 0.4665);\n"
                    + "const mat3 OpponentToRGBMat = mat3(1.1677, 0.9014, 0.7214, -6.4315, 2.5970, 0.1257, -0.5044, 0.0159, 2.0517);\n";
        }
        String program
                = "#ifdef GL_ES                                          \n"
                + "precision mediump float;                              \n"
                + "#endif                                                \n"
                + "varying vec2 v_TexCoord;                              \n"
                + "varying vec4 v_Color;                                 \n"
                + "uniform sampler2D u_Texture;                          \n"
                + blindnessFilterData
                + "void main()                                           \n"
                + "{                                                     \n"
                + "  vec4 fragColor = texture2D(u_Texture, v_TexCoord);  \n"
                + "  fragColor *= v_Color;                               \n"
                + blindnessFilterCode
                + "  gl_FragColor = fragColor;                           \n"
                + "}                                                     \n";

        return program;
    }

}
