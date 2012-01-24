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
 * An extension of the texture type that takes a greyscale input and calculates
 * a normal map from it.
 * @author Ryan Fechney
 */
package Core.OpenGL.Textures;

import java.awt.image.BufferedImage;

/**
 *
 * @author ryanf
 */
public class GSReliefMapTexture extends Texture 
{
    GSReliefMapTexture(BufferedImage newImage, String newName)
    {
	super(newImage, newName);
    }
    
    private double getHeight(BufferedImage input, int x, int y)
    {
	int packed = input.getRGB(x, y);

	double r = (double)((packed >> 16)&0xFF);
	double g = (double)((packed >>  8)&0xFF);
	double b = (double)((packed      )&0xFF);
	
	return (r + g + b) / (256.0 * 3.0);
    }
    
    /**
     * Convert the buffered image to a texture
     *
     * @param bufferedImage The image to convert to a texture
     * @param texture The texture to store the data into
     * @return A buffer containing the data
     */
    @Override
    protected void putImageBuffer(BufferedImage input) 
    {
        // Load data in.      
        for (int y = 0; y < height; y++) 
        {
            for (int x = 0; x < width; x++) 
            {
		double h = getHeight(input, x, y);
		double du = 0;
		double dv = 0;
		
		if(x > 0)
		{	
		    du += h - getHeight(input, x-1, y);
		}
		if(x < width - 1)
		{
		    du += getHeight(input, x+1, y) - h;
		}
		if(y > 0)
		{
		    dv += h - getHeight(input, x , y-1);
		}
		if(y < height - 1)
		{
		    dv += getHeight(input, x, y+1) - h;
		}
		
		double u = -du;
		double v = -dv;
		double w = 0.25;
                
		double n = Math.sqrt(u*u + v*v + w*w);

                byte a = (byte)(h * 255.0);
                byte r = (byte)((u / n + 1.0) / 2.0 * 255.0);
                byte g = (byte)((v / n + 1.0) / 2.0 * 255.0);
                byte b = (byte)((w / n + 1.0) / 2.0 * 255.0);
                
                int index = (x + y * allocatedWidth) * 4;
                pixelData.put(index++, r);
                pixelData.put(index++, g);
                pixelData.put(index++, b);
                pixelData.put(index  , a);
            }
        }
    }
}
