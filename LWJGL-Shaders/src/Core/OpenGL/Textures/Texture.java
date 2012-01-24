/*
Copyright (c) 2011, Ryan Fechney - ryan.fechney gmail
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met: 

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer. 
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/**
 * A texture to be bound within LWJGL. This object is responsible for 
 * keeping track of a given OpenGL texture and for calculating the
 * texturing mapping coordinates of the full image.
 * 
 * Since textures need to be powers of 2 the actual texture may be
 * considerably bigger that the source image and hence the texture
 * mapping coordinates need to be adjusted to match up drawing the
 * sprite against the texture.
 * @author Ryan Fechney
 * 
 * Inspired by previous work done by
 * @author Kevin Glass
 * @author Brian Matzon
 */
package Core.OpenGL.Textures;

import org.lwjgl.opengl.GL11;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import Core.OpenGL.Colour;
import org.lwjgl.opengl.GL13;

public class Texture
{
    /** The GL texture ID */
    private int textureID = 0;
    /** The height of the real image */
    protected int height = 0;
    /** The width of the real image */
    protected int width = 0;
    /** The height of the GL buffer necessary */
    protected int allocatedHeight = 0;
    /** The width of the GL buffer necessary */
    protected int allocatedWidth = 0;
    /** The width of the image */
    private boolean uploaded = false;
    /** The width of the image */
    private String name;
    /** The texture data */
    protected ByteBuffer pixelData = null;

    /**
     * Create a new texture
     *
     * @param newPixelData pixel data
     */
    Texture(BufferedImage newImage, String newName) 
    {
        name = newName;

        width = newImage.getWidth();
        height = newImage.getHeight();
        
        allocate();

        putImageBuffer(newImage);
    }

    /**
     * Create a new texture
     *
     * @param newPixelData pixel data
     */
    Texture(int newWidth, int newHeight, String newName)
    {
        name = newName;

        width = newWidth;
        height = newHeight;

        allocate();
    }

    /**
     * Allocate new space for at least width and height
     */
    private void allocate()
    {
        if(width <= 0 || height <= 0 )
        {
            allocatedWidth = 0;
            allocatedHeight = 0;
            pixelData = null;
        }
        
        // find the closest power of 2 for the width and height
        // of the produced texture
        allocatedWidth = get2Fold(width);
        allocatedHeight = get2Fold(height);
        
        if(pixelData == null || pixelData.capacity() != allocatedWidth * allocatedHeight * 4)
        {
            pixelData = ByteBuffer.allocateDirect(allocatedWidth * allocatedHeight * 4);
        }
    }

    /* Manipulation methods */
    /**
     * Get the height of the original image
     *
     * @return The height of the original image
     */
    public int getHeight()
    {
        return height;
    }

    /** 
     * Get the width of the original image
     *
     * @return The width of the original image
     */
    public int getWidth()
    {
        return width;
    }

    /** 
     * Get the name of the image
     *
     * @return The name of the image
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the pixel value at u,v (ints)
     *
     * @param u - x direction
     * @param v - y direction
     */
    public void setPixel(int u, int v, Colour c)
    {
        if (u < 0) 
        {
            setPixel(u + width, v, c);
            return;
        }
        if (u >= width)
        {
            setPixel(u - width, v, c);
            return;
        }
        if (v < 0)
        {
            setPixel(u, v + height, c);
            return;
        }
        if (v >= height)
        {
            setPixel(u, v - height, c);
            return;
        }
        
        int index = (u + v * allocatedWidth) * 4;
        // the + 0.5 is to make it round up or down to nearest, rather than
        // just down
        pixelData.put(index++, (byte) (clamp((int)(c.r * 255.0 + 0.5))));
        pixelData.put(index++, (byte) (clamp((int)(c.g * 255.0 + 0.5))));
        pixelData.put(index++, (byte) (clamp((int)(c.b * 255.0 + 0.5))));
        pixelData.put(index  , (byte) (clamp((int)(c.a * 255.0 + 0.5))));

        // The data might not match anymore, so force an upload.
        uploaded = false;
    }

    /**
     * Get the pixel value at u,v (ints) as raw
     *
     * @param u - x direction
     * @param v - y direction
     */
    public void getPixel(Colour result, int u, int v)
    {
        if (u < 0) 
        {
            getPixel(result, u + width, v);
            return;
        }
        if (u >= width)
        {
            getPixel(result, u - width, v);
            return;
        }
        if (v < 0)
        {
            getPixel(result, u, v + height);
            return;
        }
        if (v >= height)
        {
            getPixel(result, u, v - height);
            return;
        }
        
        int index = (u + v * allocatedWidth) * 4;
        result.r = ((double) pixelData.get(index++)) / 255.0;
        result.g = ((double) pixelData.get(index++)) / 255.0;
        result.b = ((double) pixelData.get(index++)) / 255.0;
        result.a = ((double) pixelData.get(index  )) / 255.0;

        // Make them unsigned (why Java why?)
        if (result.r < 0)
        {
            result.r += 1;
        }
        if (result.g < 0) 
        {
            result.g += 1;
        }
        if (result.b < 0)
        {
            result.b += 1;
        }
        if (result.a < 0)
        {
            result.a += 1;
        }
    }
    
    /**
     * Bind the specified GL context to a texture
     */
    public void bind(int index)
    {
	GL13.glActiveTexture(GL13.GL_TEXTURE0 + index);
        if (uploaded == false)
        {
            uploadToOpenGL();
        }
	else
	{
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}
    }
    
    /**
     * Unbind the specified GL context from any texture
     */
    public void unbind(int index)
    {
	GL13.glActiveTexture(GL13.GL_TEXTURE0 + index);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    /**
     * Upload the texture to OpenGL
     */
    public void uploadToOpenGL()
    {
        System.out.print("Uploading texture " + name + " to OpenGL");

        // Figure out what kind of data we are working with
        int srcPixelFormat = GL11.GL_RGBA;

        // build a texture ID that OpenGL can use 
        IntBuffer tmp = createIntBuffer(1);
        GL11.glGenTextures(tmp);
        textureID = tmp.get(0);

        // Attach to this texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

        // Set the filtering options for the next uploaded texture (this one to be precise)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        // produce a texture from the byte buffer.
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA,
                allocatedWidth,
                allocatedHeight,
                0,
                srcPixelFormat,
                GL11.GL_UNSIGNED_BYTE,
                pixelData);

        // Done!
        uploaded = true;

        System.out.println(" - ok");
    }
    
    /**
     * Convert the buffered image to a texture
     *
     * @param bufferedImage The image to convert to a texture
     * @param texture The texture to store the data into
     * @return A buffer containing the data
     */
    protected void putImageBuffer(BufferedImage input) 
    {
        // Load data in.      
        for (int y = 0; y < height; y++) 
        {
            for (int x = 0; x < width; x++) 
            {
                int packed = input.getRGB(x, y);
                byte a = (byte)((packed >> 24)&0xFF);
                byte r = (byte)((packed >> 16)&0xFF);
                byte g = (byte)((packed >>  8)&0xFF);
                byte b = (byte)((packed      )&0xFF);       
                
                int index = (x + y * allocatedWidth) * 4;
                pixelData.put(index++, r);
                pixelData.put(index++, g);
                pixelData.put(index++, b);
                pixelData.put(index  , a);
            }
        }
    }
    
    /**
     * Get an image buffer that is like this texture (big pilo 'o hack)
     * 
     * @return The image buffer
     */
    public BufferedImage getImageBuffer()
    {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) 
        {
            for (int x = 0; x < width; x++) 
            {
                int index = (x + y * allocatedWidth) * 4;
                int r = pixelData.get(index++)&0xFF;
                int g = pixelData.get(index++)&0xFF;
                int b = pixelData.get(index++)&0xFF;
                int a = pixelData.get(index  )&0xFF;
                int packed = (a << 24) | (r << 16) | (g << 8) | b;
                result.setRGB(x, y, packed);
            }
        }

        return result;
    }

    /**
     * Creates an integer buffer to hold specified ints
     * - strictly a utility method
     *
     * @param size how many int to contain
     * @return created IntBuffer
     */
    private static IntBuffer createIntBuffer(int size)
    {
        ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
        temp.order(ByteOrder.nativeOrder());

        return temp.asIntBuffer();
    }

    /**
     * Get the closest greater power of 2 to the fold number
     * 
     * @param fold The target number
     * @return The power of 2
     */
    public static int get2Fold(int fold)
    {
        int ret = 2;
        while (ret < fold)
        {
            ret *= 2;
        }
        return ret;
    }

    /**
     * clamp between 0 and 255 (byte)
     * 
     * @return clamped value
     */
    private int clamp(int input)
    {
        if (input < 0)
        {
            return 0;
        }
        if (input > 255)
        {
            return 255;
        }
        return input;
    }
    
    public String toXML(String stepIndent, String accumulatedIndent)
    {
        //String newIndent = stepIndent + accumulatedIndent;
        return accumulatedIndent + name + "\n";
    }
}
