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
 * An example main function which will draw two polygons, using a different
 * shader set for each.
 * @author Ryan Fechney
 */
package Core;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Core.OpenGL.Window;
import Core.OpenGL.Textures.Texture;
import Core.OpenGL.Shaders.Shader;
import Core.Geometry.Point;

public class Main
{

    static boolean quit = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
	// Build context
	Window mainWindow = null;
	try
	{

	    System.out.println("LWJGL Shaders example - c 2011 Ryan Fechney");

	    /* Install a signal handler
	     */
	    Runtime.getRuntime().addShutdownHook(new Thread()
	    {

		@Override
		public void run()
		{
		    if (quit == false)
		    {
			System.out.println("Signal caught!");
			quit = true;
		    }
		}
	    });


	    /* Autodetect processor count
	     */
	    Runtime rt = Runtime.getRuntime();
	    int defaultThreads = rt.availableProcessors();
	    System.out.println(defaultThreads + " processors detected");
	    int megsOfMemory = (int) (rt.maxMemory() / 1024 / 1024);
	    System.out.println(megsOfMemory + "MB RAM useable");

	    // Build window and show it
	    mainWindow = new Window();
	    mainWindow.create(false, 800, 600, 32);

	    // Set up textures and shaders.	    
	    Texture texture = mainWindow.textureFactory.getTexture("sun.png");
	    Texture reliefMap = mainWindow.textureFactory.getGSReliefMapTexture("whorl.png");
	    Texture normalMap = mainWindow.textureFactory.getTexture("normal.jpg");
	    if (texture == null || reliefMap == null || normalMap == null)
	    {
		System.out.println("Failed to load textures.");
		mainWindow.destroy();
		return;
	    }
	    
	    Shader relief = mainWindow.shaderFactory.getShader("relief", new String[]
		    {
			"relief.frag", "relief.vert"
		    });
	    Shader normal = mainWindow.shaderFactory.getShader("normal", new String[]
		    {
			"normal.frag", "normal.vert"
		    });    
	    if (relief == null || normal == null)
	    {
		System.out.println("Failed to build shaders.");
		mainWindow.destroy();
		return;
	    }

	    // We can set up some uniforms as universal constants.
	    relief.bind();
	    relief.setUniformTexture("texMap", texture, 0);
	    relief.setUniformTexture("reliefMap", reliefMap, 1);
	    relief.unbind();
	    normal.bind();
	    normal.setUniformTexture("texMap", texture, 2);
	    normal.setUniformTexture("normMap", normalMap, 3);
	    normal.unbind();
	    
	    // Core loop variables here.
	    double i = 0;
	    Point target = new Point(0, 0, -1);
	    Point upDirection = new Point(0, 1, 0);
	    Point a = new Point();
	    Point b = new Point();
	    Point c = new Point();
	    Point d = new Point();
	    Point u = new Point();
	    Point v = new Point();
	    Point w = new Point();
	    Point lightSource = new Point(1.0, 0.0, 0.0);
	    Point rotationAxis = new Point(1.0, 0.0, 0.0);
	    
	    // Go!
	    while (quit == false && mainWindow.isCloseRequested() == false)
	    {
		// Begin frame.
		// Clear Z and frame buffers.
		mainWindow.resetGLBuffers(target, upDirection);

		
		
		// Render relief mapped quad. ----------------------------------
		// We could also set uniforms here if we needed to.
		relief.bind();
		
		// Vertextes
		a.overwriteWith(-1.0, -1.0, 0.0);
		b.overwriteWith(1.0, -1.0, 0.0);
		c.overwriteWith(-1.0, 1.0, 0.0);
		d.overwriteWith(1.0, 1.0, 0.0);
		lightSource.overwriteWith(3.0, 0.0, 0.0);
		
		a.rotate(rotationAxis, i);
		b.rotate(rotationAxis, i);
		c.rotate(rotationAxis, i);
		d.rotate(rotationAxis, i);

		// Basis
		u.overwriteWith(b);
		u.subtract(a);
		u.normalise();
		v.overwriteWith(c);
		v.subtract(a);
		v.normalise();
		w.cross(u, v);
		w.normalise();

		// Push polygon
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
		    // Do atributes.  We can set them at lower levels also, but
		    // I expect to modify these per vertex.
		    // Set up U,V,W data.
		    relief.setAttribute4fp("lightMS", lightSource, 1.0f);
		    relief.setAttribute3fp("uMS", u);
		    relief.setAttribute3fp("vMS", v);
		    relief.setAttribute3fp("wMS", w);
		    
		    // Draw two triangles to make a quad.
		    GL11.glTexCoord2d(0.0, 0.0);
		    GL11.glVertex4d(a.x - 1.0, a.y, a.z - 4.0, 1.0);
		    GL11.glTexCoord2d(1.0, 0.0);
		    GL11.glVertex4d(b.x - 1.0, b.y, b.z - 4.0, 1.0);
		    GL11.glTexCoord2d(0.0, 1.0);
		    GL11.glVertex4d(c.x - 1.0, c.y, c.z - 4.0, 1.0);

		    GL11.glTexCoord2d(1.0, 1.0);
		    GL11.glVertex4d(d.x - 1.0, d.y, d.z - 4.0, 1.0);
		    GL11.glTexCoord2d(0.0, 1.0);
		    GL11.glVertex4d(c.x - 1.0, c.y, c.z - 4.0, 1.0);
		    GL11.glTexCoord2d(1.0, 0.0);
		    GL11.glVertex4d(b.x - 1.0, b.y, b.z - 4.0, 1.0);
		}
		GL11.glEnd();
		relief.unbind();
		// Done. -------------------------------------------------------
		
		
		
		// Draw normal mapped quad. ------------------------------------
		// We could also set uniforms here if we needed to.
		normal.bind();
		
		// Vertextes
		a.overwriteWith(-1.0, -1.0, 0.0);
		b.overwriteWith(1.0, -1.0, 0.0);
		c.overwriteWith(-1.0, 1.0, 0.0);
		d.overwriteWith(1.0, 1.0, 0.0);
		lightSource.overwriteWith(3.0, 0.0, 0.0);
		
		a.rotate(rotationAxis, -i);
		b.rotate(rotationAxis, -i);
		c.rotate(rotationAxis, -i);
		d.rotate(rotationAxis, -i);

		// Basis
		u.overwriteWith(b);
		u.subtract(a);
		u.normalise();
		v.overwriteWith(c);
		v.subtract(a);
		v.normalise();
		w.cross(u, v);
		w.normalise();

		// Push polygon
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
		    // Do atributes.  We can set them at lower levels also, but
		    // I expect to modify these per vertex.
		    // Set up U,V,W data.
		    relief.setAttribute4fp("lightMS", lightSource, 1.0f);
		    relief.setAttribute3fp("uMS", u);
		    relief.setAttribute3fp("vMS", v);
		    relief.setAttribute3fp("wMS", w);
		    
		    // Draw two triangles to make a quad.
		    GL11.glTexCoord2d(0.0, 0.0);
		    GL11.glVertex4d(a.x + 1.0, a.y, a.z - 4.0, 1.0);
		    GL11.glTexCoord2d(1.0, 0.0);
		    GL11.glVertex4d(b.x + 1.0, b.y, b.z - 4.0, 1.0);
		    GL11.glTexCoord2d(0.0, 1.0);
		    GL11.glVertex4d(c.x + 1.0, c.y, c.z - 4.0, 1.0);

		    GL11.glTexCoord2d(1.0, 1.0);
		    GL11.glVertex4d(d.x + 1.0, d.y, d.z - 4.0, 1.0);
		    GL11.glTexCoord2d(0.0, 1.0);
		    GL11.glVertex4d(c.x + 1.0, c.y, c.z - 4.0, 1.0);
		    GL11.glTexCoord2d(1.0, 0.0);
		    GL11.glVertex4d(b.x + 1.0, b.y, b.z - 4.0, 1.0);
		}
		GL11.glEnd();
		normal.unbind();
		// Done. -------------------------------------------------------

		
		
		// Done. Next frame!
		i += 0.001;

		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
		{
		    System.out.println("Quitting...");
		    quit = true;
		}

		// Swap the buffers.
		mainWindow.swapBuffers();
	    }
	}
	catch (Exception ex)
	{
	    ex.printStackTrace();
	}

	// Clean up
	if (mainWindow != null)
	{
	    mainWindow.destroy();
	}

	System.out.println("Exit.");
    }
}
