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
 * A factory to cache texture loads.
 *
 * @author Ryan Fechney
 */
package Core.OpenGL.Textures;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class TextureFactory
{
    /** The table of textures that have been loaded in this loader */
    private HashMap<String, Texture> table = new HashMap<String, Texture>();

    // Cannot instanciate
    public TextureFactory()
    {
    }
    
    /**
     * Load a texture
     *
     * @param resourceName The location of the resource to load
     * @return The loaded texture
     * @throws IOException Indicates a failure to access the resource
     */
    public Texture getTexture(String textureName) throws IOException
    {
        Texture texture = table.get(textureName);

        if (texture != null)
        {
            return texture;
        }

        System.out.print(" - Loading \"" + textureName + "\" ");
        // Load the image data
        BufferedImage bufferedImage = ImageIO.read(new FileInputStream(textureName));
        if (bufferedImage == null)
        {
            System.out.println(" fail");
            return null;
        }

        // Make the new texture object
        texture = new Texture(bufferedImage, textureName);

        table.put(textureName, texture);
        System.out.println(" ok");

        BufferedImage save = texture.getImageBuffer();
        FileOutputStream file = new FileOutputStream("out.png");
        ImageIO.write(save, "png", file);
        
        return texture;
    }
    
        /**
     * Load a texture
     *
     * @param resourceName The location of the resource to load
     * @return The loaded texture
     * @throws IOException Indicates a failure to access the resource
     */
    public Texture getGSReliefMapTexture(String textureName) throws IOException
    {
        Texture texture = table.get(textureName);

        if (texture != null)
        {
            return texture;
        }

        System.out.print(" - Loading \"" + textureName + "\" ");
        // Load the image data
        BufferedImage bufferedImage = ImageIO.read(new FileInputStream(textureName));
        if (bufferedImage == null)
        {
            System.out.println(" fail");
            return null;
        }

        // Make the new texture object
        texture = new GSReliefMapTexture(bufferedImage, textureName);

        table.put(textureName, texture);
        System.out.println(" ok");

        BufferedImage save = texture.getImageBuffer();
        FileOutputStream file = new FileOutputStream("out.png");
        ImageIO.write(save, "png", file);
        
        return texture;
    }
    
    /**
     * Make a new texture from scratch, does not load it.
     *
     * @param resourceName The location of the resource to load
     * @return The new texture
     */
    public Texture newTexture(int newWidth, int newHeight, String textureName)
    {
        Texture texture = table.get(textureName);

        if (texture != null)
        {
            return null;
        }

        // Make the new texture object
        texture = new Texture(newWidth, newHeight, textureName);

        table.put(textureName, texture);

        return texture;
    }

    /**
     * Remove a texture from the cache
     */
    public void remove(Texture ref)
    {
        if(table.containsValue(ref))
        {
            table.remove(ref.getName());
        }
    }
}
