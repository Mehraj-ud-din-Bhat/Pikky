package mp4composer.filter;

/*==================================================
 Pikky

 © XScoder 2018
 All Rights reserved


 RE-SELLING THIS SOURCE CODE TO ANY ONLINE MARKETPLACE IS A SERIOUS COPYRIGHT INFRINGEMENT.
 YOU WILL BE LEGALLY PROSECUTED

==================================================*/

import android.opengl.GLES20;

public class GlBrightnessFilter extends GlFilter {

    private static final String VERTEX_SHADER =
                    "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uSTMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +

                    "varying highp vec2 vTextureCoord;\n" +


                    "const lowp int GAUSSIAN_SAMPLES = 9;" +

                    "uniform highp float texelWidthOffset;" +
                    "uniform highp float texelHeightOffset;" +
                    "uniform highp float blurSize;" +

                    "varying highp vec2 blurCoordinates[GAUSSIAN_SAMPLES];" +

                    "void main() {" +

                    "  gl_Position = uMVPMatrix * aPosition;\n" +
                    "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +

                    // Calculate the positions for the blur
                    "     int multiplier = 0;" +
                    "     highp vec2 blurStep;" +
                    "     highp vec2 singleStepOffset = vec2(texelHeightOffset, texelWidthOffset) * blurSize;" +

                    "     for (lowp int i = 0; i < GAUSSIAN_SAMPLES; i++) {" +
                    "          multiplier = (i - ((GAUSSIAN_SAMPLES - 1) / 2));" +
                    // Blur in x (horizontal)
                    "          blurStep = float(multiplier) * singleStepOffset;" +
                    "          blurCoordinates[i] = vTextureCoord.xy + blurStep;" +
                    "     }" +
                    "}";


    private static final String FRAGMENT_SHADER =
                    "#extension GL_OES_EGL_image_external : require\n"
                    + "precision mediump float;\n"
                    + "uniform samplerExternalOES sTexture;\n"
                    + "float brightness ;\n" + "varying vec2 vTextureCoord;\n"
                    + "void main() {\n" + "  brightness =1.4;\n"
                    + "  vec4 color = texture2D(sTexture, vTextureCoord);\n"
                    + "  gl_FragColor = brightness * color;\n" + "}\n";


    private float texelWidthOffset = 0.004f;
    private float texelHeightOffset = 0.004f;
    private float blurSize = 1.0f;

    public GlBrightnessFilter() {
        super(VERTEX_SHADER, FRAGMENT_SHADER);
    }


    public float getTexelWidthOffset() {
        return texelWidthOffset;
    }

    public void setTexelWidthOffset(final float texelWidthOffset) {
        this.texelWidthOffset = texelWidthOffset;
    }

    public float getTexelHeightOffset() {
        return texelHeightOffset;
    }

    public void setTexelHeightOffset(final float texelHeightOffset) {
        this.texelHeightOffset = texelHeightOffset;
    }

    public float getBlurSize() {
        return blurSize;
    }

    public void setBlurSize(final float blurSize) {
        this.blurSize = blurSize;
    }

    @Override
    public void onDraw() {
        GLES20.glUniform1f(getHandle("texelWidthOffset"), texelWidthOffset);
        GLES20.glUniform1f(getHandle("texelHeightOffset"), texelHeightOffset);
        GLES20.glUniform1f(getHandle("blurSize"), blurSize);
    }


}
