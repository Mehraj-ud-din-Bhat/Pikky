package mp4composer.filter;

import mp4composer.utils.GlUtils;

/*==================================================
 Pikky

 © XScoder 2018
 All Rights reserved


 RE-SELLING THIS SOURCE CODE TO ANY ONLINE MARKETPLACE IS A SERIOUS COPYRIGHT INFRINGEMENT.
 YOU WILL BE LEGALLY PROSECUTED

==================================================*/


public class GlGrayScaleFilter extends GlFilter {
    private static final String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "const highp vec3 weight = vec3(0.2125, 0.7154, 0.0721);\n" +
                    "void main() {\n" +
                    "float luminance = dot(texture2D(sTexture, vTextureCoord).rgb, weight);\n" +
                    "gl_FragColor = vec4(vec3(luminance), 1.0);\n" +
                    "}";

    public GlGrayScaleFilter() {
        super(GlUtils.DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
    }
}
