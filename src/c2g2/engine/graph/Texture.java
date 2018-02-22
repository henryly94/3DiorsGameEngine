package c2g2.engine.graph;


import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;


public class Texture {

    public ByteBuffer textBuffer;

    private int width;

    private int height;

    private int id;

    public Texture(int id){
        this.id = id;

    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public Texture setTexture(String filename) throws IOException{
        PNGDecoder pngDecoder = new PNGDecoder(new FileInputStream(filename));
        width = pngDecoder.getWidth();
        height = pngDecoder.getHeight();
        textBuffer = ByteBuffer.allocateDirect(4 * width * height);
        pngDecoder.decode(textBuffer, width * 4, PNGDecoder.Format.RGBA);
        textBuffer.flip();


        id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);

        // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        // Set OpenGL Texture Parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Upload the texture data
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, textBuffer);

        // Generate Mip Map
        glGenerateMipmap(GL_TEXTURE_2D);
        return this;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    public int getId() {
        return id;
    }

    public void cleanup() {
        glDeleteTextures(id);
    }
}