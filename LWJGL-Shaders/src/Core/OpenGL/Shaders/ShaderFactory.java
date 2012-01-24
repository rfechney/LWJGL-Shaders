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
 * A caching factory for shaders.
 * @author Ryan Fechney
 */
package Core.OpenGL.Shaders;

import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

public class ShaderFactory
{

    /** The table of Shaders that have been loaded in this loader */
    private HashMap<String, Shader> table = new HashMap<String, Shader>();

    // Cannot instanciate
    public ShaderFactory()
    {
    }

    ArrayList<String> shaderFileNames;
    public void pushShaderFileName(String shaderFileName)
    {
	
    }
    
    
    /**
     * Get a shader from the disk
     *
     * @param resourceName The location of the resource to load
     * @return The loaded Shader
     * @throws IOException Indicates a failure to access the resource
     */
    public Shader getShader(String shaderName, String[] files) throws IOException, Exception
    {
	Shader shader = table.get(shaderName);

	if (shader != null)
	{
	    return shader;
	}

	ArrayList<ShaderPart> shaderPrograms = new ArrayList<ShaderPart>();
	for (String shaderFileName : files)
	{
	    System.out.print(" - Loading \"" + shaderFileName + "\" ");
	    String sourceCode = "";
	    String line;
	    try
	    {
		BufferedReader reader = new BufferedReader(new FileReader(shaderFileName));
		while ((line = reader.readLine()) != null)
		{
		    sourceCode += line + "\n";
		}

		// Make sheder from type
		String type = shaderFileName.substring(shaderFileName.indexOf("."));
		
		if(type.compareTo(".vert") == 0)
		{
		    shaderPrograms.add(new VertexShader(sourceCode));
		}	
		else if(type.compareTo(".frag") == 0)
		{
		    shaderPrograms.add(new FragmentShader(sourceCode));
		}
		else
		{
		    System.out.println("Shader type is not recognised: " + type);
		}
	    }
	    catch (Exception e)
	    {
		System.out.println("Fail reading shading code: " + e.getMessage());
		return null;
	    }
	    System.out.println(" ok");
	}

	shader = new Shader(shaderPrograms, shaderName);

	table.put(shaderName, shader);

	return shader;
    }

    /**
     * Load a Shader
     *
     * @param resourceName The location of the resource to load
     * @return The loaded Shader
     * @throws IOException Indicates a failure to access the resource
     */
    public Shader getShader(String shaderName) throws IOException
    {
	return table.get(shaderName);
    }

    /**
     * Remove a Shader from the cache
     */
    public void remove(Shader ref)
    {
	if (table.containsValue(ref))
	{
	    table.remove(ref.getName());
	}
    }
}
