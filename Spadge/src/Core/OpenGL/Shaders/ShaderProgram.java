/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Core.OpenGL.Shaders;

import org.lwjgl.opengl.ARBShaderObjects;

/**
 *
 * @author ryanf
 */
public abstract class ShaderProgram
{
    private int shaderID = 0;
    
    protected ShaderProgram(String sourceCode, int shaderType) throws Exception
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
	
	/*
	 * Check the shader was created
	 */	
	int shaderLength = ARBShaderObjects.glGetObjectParameteriARB(shaderID, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB);	
	if(shaderLength != 0)
	{
	    /*
	     * Bad shader.
	     * Read the shader log out
	     */
	    System.out.println("Shader log: " + ARBShaderObjects.glGetInfoLogARB(shaderID, shaderLength));
	    throw new Exception("Could not create shader object - code: " + sourceCode);
	}	
    }
    
    int shaderID()
    {
	return shaderID;
    }		
}