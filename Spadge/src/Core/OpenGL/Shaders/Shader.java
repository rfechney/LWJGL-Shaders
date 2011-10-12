/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Core.OpenGL.Shaders;

import org.lwjgl.opengl.ARBShaderObjects;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author ryanf
 */
public class Shader
{

    private boolean loaded = false;
    private int shaderID = 0;
    private ArrayList<ShaderProgram> shaderPrograms;
    private String name;

    public Shader(ArrayList<ShaderProgram> inputShaderPrograms, String inputName)
    {
	name = inputName;
	
	shaderID = ARBShaderObjects.glCreateProgramObjectARB();
	if (shaderID != 0)
	{
	    // Copy programs in
	    shaderPrograms = inputShaderPrograms;
	    
	    // Bind
	    for(Iterator<ShaderProgram> it = shaderPrograms.iterator(); it.hasNext();)
	    {
		ARBShaderObjects.glAttachObjectARB(shaderID, it.next().shaderID());
	    }
	    ARBShaderObjects.glLinkProgramARB(shaderID);
	    ARBShaderObjects.glValidateProgramARB(shaderID);
	    loaded = true;
	}
	else
	{
	    loaded = false;
	}
    }

    public void bind()
    {
	if (loaded)
	{
	    ARBShaderObjects.glUseProgramObjectARB(shaderID);
	}
    }
    
    public void unbind()
    {
	ARBShaderObjects.glUseProgramObjectARB(0);
    }
    
    public String getName()
    {
        return name;
    }
}
