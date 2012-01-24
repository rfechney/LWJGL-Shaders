/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Core.OpenGL.Window;
import Core.OpenGL.Textures.Texture;
import Core.OpenGL.Shaders.Shader;
import Core.Geometry.Point;

/**
 *
 * @author ryanf
 */
public class Main
{
    
    static boolean quit = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // Build context
        try
        {

            System.out.println("Enfos engine demo - c 2011 Ryan Fechney");
            
            /* Install a signal handler
             */
            Runtime.getRuntime().addShutdownHook(new Thread() 
                {
                    @Override
                    public void run() 
                    {
                        if(quit == false)
                        {
                            System.out.println ("Signal caught!");
                            quit = true;
                        }
                    }
                }
             );

            
            /* Autodetect processor count
             */
            Runtime rt = Runtime.getRuntime();
            int defaultLODThreads = rt.availableProcessors();
            System.out.println(defaultLODThreads + " processors detected");
            defaultLODThreads--;
            int megsOfMemory = (int)(rt.maxMemory()/1024/1024);
            System.out.println(megsOfMemory + "MB RAM useable");

            // Build window and show it
            Window mainWindow = new Window();
            mainWindow.create(false, 800, 600, 32);
            
            // Core loop here
            double i = 0;
            Point target = new Point(0,1,-1);
            Point upDirection = new Point(0,1,0);
            
            GL11.glEnable(GL11.GL_TEXTURE_2D); // Enable Texture Mapping
            Texture texture = mainWindow.textureFactory.getTexture("whorl.png");
	    Shader shader = mainWindow.shaderFactory.getShader("colour", new String[] {"displacement.frag", "alphapass.vert"});
	    //Shader shader = mainWindow.shaderFactory.getShader("colour", new String[] {"colour.frag", "identity.vert"});
	    
	    if(texture == null || shader == null)
	    {
		System.out.println("Failed to load shader/texture.");
		mainWindow.destroy();
		return;
	    }
	    
            texture.bind();
	    shader.bind();
	    
            
            while(quit == false && mainWindow.isCloseRequested() == false)
            {
                // Begin frame
                mainWindow.resetGLBuffers(target, upDirection);
                
                // Draw a triangle
                GL11.glBegin(GL11.GL_TRIANGLES);

                // Clock
                GL11.glColor3d(1.0, 1.0, 1.0);
                GL11.glVertex4d(Math.cos(i), Math.sin(i) + 2.0, -2.0, 0.01);
                GL11.glTexCoord2d(0.0, 0.0);
                GL11.glVertex4d(Math.cos(i+2.0), Math.sin(i+2.0) + 2.0, -2.0, 0.01);
                GL11.glTexCoord2d(1.0, 0.0);
                GL11.glVertex4d(Math.cos(i-2.0), Math.sin(i-2.0) + 2.0, -2.0, 0.01);
                GL11.glTexCoord2d(0.0, 1.0);
                GL11.glEnd();
                
                i+=0.001;
                
                if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
                {
                    System.out.println("Quitting...");
                    quit = true;
                }

                // Reset buffers
                mainWindow.swapBuffers();
            }
            
            mainWindow.destroy();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
        System.out.println("Exit.");
    }
}
