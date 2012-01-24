/**
 * A utility class to load textures for JOGL. This source is based
 * on a texture that can be found in the Java Gaming (www.javagaming.org)
 * Wiki. It has been simplified slightly for explicit 2D graphics use.
 *
 * OpenGL uses a particular image format. Since the images that are
 * loaded from disk may not match this format this loader introduces
 * a intermediate image which the source image is copied into. In turn,
 * this image is used as source for the OpenGL texture.
 *
 * @author Ryan Fechney
 * 
 * based on work by
 * @author Kevin Glass
 * @author Brian Matzon
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
