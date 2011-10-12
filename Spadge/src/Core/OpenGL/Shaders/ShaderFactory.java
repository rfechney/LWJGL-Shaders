/**
 * A utility class to load Shaders for JOGL.
 * @author Ryan Fechney
 */
package Core.OpenGL.Shaders;

import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
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
    public Shader getShader(String shaderName, String[] files) throws IOException
    {
	Shader shader = table.get(shaderName);

	if (shader != null)
	{
	    return shader;
	}

	ArrayList<ShaderProgram> shaderPrograms = new ArrayList<ShaderProgram>();
	for (String shaderFileName : files)
	{
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

	}

	shader = new Shader(shaderPrograms, shaderName);

	table.put(shaderName, shader);
	System.out.println(" ok");

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
