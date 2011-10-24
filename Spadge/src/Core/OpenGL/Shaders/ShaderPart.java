/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Core.OpenGL.Shaders;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author ryanf
 */
public abstract class ShaderPart
{
    private int shaderID = 0;
    
    protected ShaderPart(String sourceCode, int shaderType) throws Exception
    {
	// ShaderID will be non zero if succefully created
	shaderID = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
	if (shaderID == 0)
	{
	    throw new Exception("Could not create shader handle - code: " + sourceCode);
	}

	/*
	 * associate the code String with the created shader
	 * and compile
	 */

	ARBShaderObjects.glShaderSourceARB(shaderID, sourceCode);
	ARBShaderObjects.glCompileShaderARB(shaderID);
	int logLength = ARBShaderObjects.glGetObjectParameteriARB(shaderID, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB);	
	String log = "";
	if(logLength != 0)
	{
	    log = ARBShaderObjects.glGetInfoLogARB(shaderID, logLength);
	}
	if(GL20.glGetShader(shaderID, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE)
	{
	    throw new Exception("Program did not compile. " + log);
	}
	if(logLength != 0)
	{
	    System.out.print("\nCompile log:\n " + log + "\n");
	}
    }
    
    int shaderID()
    {
	return shaderID;
    }		
}