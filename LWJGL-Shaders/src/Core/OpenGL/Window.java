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
 * This class handles an OpenGL context.  All OpenGL setup calls should be made
 * here.
 *
 * @author Ryan Fehcney
 */

package Core.OpenGL;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import java.awt.HeadlessException;
import java.awt.GraphicsEnvironment;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import Core.Geometry.Point;
import Core.OpenGL.Textures.TextureFactory;
import Core.OpenGL.Shaders.ShaderFactory;

public class Window 
{

    private double frameAngle = 0;
    private Point center = new Point();
    private ByteBuffer data = ByteBuffer.allocateDirect(16);
    private FloatBuffer GLData = null;
    private Point zeroCamera = new Point(0,0,0);
    
    public TextureFactory textureFactory = new TextureFactory();
    public ShaderFactory shaderFactory = new ShaderFactory();
    
    private float light1Ambient[] = {0.0f, 0.0f, 0.0f, 1.0f};  // Ambient Light Values
    private float light1Diffuse[] = {1.0f, 1.0f, 1.0f, 1.0f};      // Diffuse Light Values
    private float light1Position[] = {80.0f, 0.0f, 0.0f, 1.0f}; // Light Position

    public Window() throws HeadlessException 
    {        
        // Try to enforce not headless
        System.setProperty("java.awt.headless", "false");
        
        // Check for headless just in case it didn't work
        if(GraphicsEnvironment.isHeadless() == true)
        {
            System.out.println("\nFATAL ERROR\n\nCannot create window in headless environment!\n");
            throw new HeadlessException();
        }
        
        data.order(ByteOrder.nativeOrder()); 
        GLData = data.asFloatBuffer();
    }

    public boolean create(boolean fullscreen, int width, int height, int bpp) throws Exception 
    {
        // Try to enforce not headless
        System.setProperty("java.awt.headless", "false");
        
        // Check for headless just in case it didn't work
        if(GraphicsEnvironment.isHeadless() == true)
        {
            System.out.println("\nFATAL ERROR\n\nCannot create window in headless environment!\n");
            return false;
        }
        
        // Start with no mode
        DisplayMode mode = null;

        // If we're after fullscreen, ask what we are allowed to use 
        if (fullscreen) 
        {
            mode = findDisplayMode(width, height, bpp);
            Display.setFullscreen(true);
        }

        // If we don't have a fullscreen mode (as it may not exist or we asked for windowed)
        // then use a window
        if (mode == null)
        {
            mode = new DisplayMode(width, height);
            Display.setFullscreen(false);
        }
        try 
        {
            Display.setDisplayMode(mode);
        }
        catch (LWJGLException e) 
        {
            e.printStackTrace();
            return false;
        }

        Display.create();

        //sync frame (only works on windows and linux apparently)
        Display.setVSyncEnabled(false);
        Display.makeCurrent();

        initGL();
        
        return true;
    }

    private DisplayMode findDisplayMode(int width, int height, int bpp)
    {
        DisplayMode[] modes;

        try
        {
            modes = Display.getAvailableDisplayModes();
        }
        catch (LWJGLException e)
        {
            System.out.println("Can't get modes.\n");
            return null;
        }
        for (int i = 0; i < modes.length; i++) 
        {
            if (modes[i].getWidth() == width && modes[i].getHeight() == height && modes[i].getBitsPerPixel() >= bpp && modes[i].getFrequency() <= 85)
            {
                return modes[i];
            }
        }
        for (int i = 0; i < modes.length; i++)
        {
            if (modes[i].getWidth() == width && modes[i].getHeight() == height && modes[i].getFrequency() <= 85)
            {
                return modes[i];
            }
        }
        return null;
    }

    /**
     * Initialize OpenGL
     *
     */
    public void initGL()
    {
        GL11.glEnable(GL11.GL_LIGHTING); // Lighting on!
        GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_FALSE);
        GL11.glLightModeli(GL11.GL_LIGHT_MODEL_LOCAL_VIEWER, GL11.GL_TRUE);
        GL11.glEnable(GL11.GL_TEXTURE_2D); // Enable Texture Mapping
        //GL11.glTexEnv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        GL11.glShadeModel(GL11.GL_SMOOTH); // Enable Smooth Shading
        GL11.glDisable(GL11.GL_NORMALIZE); // Turn off auto nrmalize
        GL11.glClearColor(0.1f, 0.0f, 0.3f, 0.0f); // Blue sky Background
        GL11.glClearDepth(10.0); // Depth Buffer Setup
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
        GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do
        GL11.glCullFace(GL11.GL_BACK); // Backface cull
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_CULL_FACE);
        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // Set transparency maths
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST); // Really Nice Perspective Calculations
        //GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_FASTEST); // Really Fast Perspective Calculations
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY); // Enable Vertex Arrays
        GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY); // Enable Normal Arrays
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY); // Enable Texture Coord Arrays
        GL11.glPointSize((float)(15.0));

        GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
        GL11.glLoadIdentity(); // Reset The Projection Matrix

        // Calculate The Aspect Ratio Of The Window
        GLU.gluPerspective(
                45.0f,
                (float) Display.getDisplayMode().getWidth() / (float) Display.getDisplayMode().getHeight(),
                0.00001f,
                10.0f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW); // Select The Modelview Matrix

        GL11.glShadeModel(GL11.GL_SMOOTH);
    }

    /**
     * Blank display ready for render
     */
    public void resetGLBuffers(Point target, Point upDirection)
    {

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);          // Clear The Screen And The Depth Buffer

        // set up normal view frustrum
        //center.overwriteWith(origin);
        //center.add(target);
        center.overwriteWith(target);

        GL11.glLoadIdentity(); // Reset The Current Modelview Matrix  
        GLU.gluLookAt(
                (float) 0, (float) 0, (float) 0, // camera position
                (float) center.x, (float) center.y, (float) center.z, // target direction from camera
                (float) upDirection.x, (float) upDirection.y, (float) upDirection.z);     // up from camera

        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, (FloatBuffer) GLData.put(light1Ambient).flip()); // Setup The Ambient Light
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, (FloatBuffer) GLData.put(light1Diffuse).flip()); // Setup The Diffuse Light
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_SPECULAR, (FloatBuffer) GLData.put(light1Diffuse).flip());
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, (FloatBuffer) GLData.put(light1Position).flip()); // Position The Light

        GL11.glEnable(GL11.GL_LIGHT1);

        return;
    }

    /**
     * Swap GL buffers
     */
    public void swapBuffers() 
    {
        /* Dispaly it all */
        //long time = System.currentTimeMillis();
        Display.update();
        //System.out.println(System.currentTimeMillis() - time);
        return;
    }

    /**
     * Set model materials
     */
    public void setMaterials(float[] ambient, float[] diffuse, float[] specular, float[] emission, float shininess)
    {
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT,  (FloatBuffer) GLData.put(ambient).flip());
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE,  (FloatBuffer) GLData.put(diffuse).flip());
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, (FloatBuffer) GLData.put(specular).flip());
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_EMISSION, (FloatBuffer) GLData.put(emission).flip());
        GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, shininess);
    }
    
    public boolean visible()
    {
        return Display.isVisible();
    }
    
    public void destroy()
    {
        Display.destroy();
    }
    
    public boolean isCloseRequested()
    {
	return Display.isCloseRequested();
    }
}
