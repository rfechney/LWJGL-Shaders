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
 * Shader compiler base type which handles common functions between vertex and
 * fragment shaders.
 * @author Ryan Fechney
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