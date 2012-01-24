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
 * A shader itself. This is how the user will normally access a shader and it's
 * methods.
 * @author Ryan Fechney
 */
package Core.OpenGL.Shaders;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import java.util.Iterator;

import Core.Geometry.Point;
import Core.OpenGL.Textures.Texture;

/**
 *
 * @author ryanf
 */
public class Shader
{

    private boolean loaded = false;
    private int shaderID = 0;
    private ArrayList<ShaderPart> shaderPrograms;
    private String name;
    private ArrayList<String> uniforms;
    private ArrayList<String> attributes;

    public Shader(ArrayList<ShaderPart> inputShaderPrograms, String inputName) throws Exception
    {
	name = inputName;

	shaderID = ARBShaderObjects.glCreateProgramObjectARB();
	if (shaderID != 0)
	{
	    // Copy programs in
	    shaderPrograms = inputShaderPrograms;

	    // Bind
	    for (Iterator<ShaderPart> it = shaderPrograms.iterator(); it.hasNext();)
	    {
		ARBShaderObjects.glAttachObjectARB(shaderID, it.next().shaderID());
	    }
	    
	    // Link
	    ARBShaderObjects.glLinkProgramARB(shaderID);
	    int logLength = ARBShaderObjects.glGetObjectParameteriARB(shaderID, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB);	
	    String log = "";
	    if(logLength != 0)
	    {
		log = ARBShaderObjects.glGetInfoLogARB(shaderID, logLength);
	    }
	    if(GL20.glGetProgram(shaderID, GL20.GL_LINK_STATUS) != GL11.GL_TRUE)
	    {
		throw new Exception("Program did not link:\n" + log);
	    }
	    if(logLength != 0)
	    {
		System.out.print("\nLink log:\n " + log + "\n");
	    }
	    
	    ARBShaderObjects.glValidateProgramARB(shaderID);
	    logLength = ARBShaderObjects.glGetObjectParameteriARB(shaderID, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB);	
	    log = "";
	    if(logLength != 0)
	    {
		log = ARBShaderObjects.glGetInfoLogARB(shaderID, logLength);
	    }
	    if(GL20.glGetProgram(shaderID, GL20.GL_VALIDATE_STATUS) != GL11.GL_TRUE)
	    {
		throw new Exception("Program did not validate:\n" + log);
	    }
	    if(logLength != 0)
	    {
		System.out.print("\nValidate log:\n " + log + "\n");
	    }	
	    
	    // Find uniforms
	    uniforms = new ArrayList<String>();
	    int i = 0;
	    String previous = "";
	    int count = GL20.glGetProgram(shaderID, GL20.GL_ACTIVE_UNIFORMS);
	    while(i < count)
	    {
		String uniform = GL20.glGetActiveUniform(shaderID, i, 100);
		if(uniform.isEmpty() == false)
		{
		    if(previous.isEmpty() == false && uniform.matches(previous))
		    {
			break;
		    }
		    previous = uniform;
		    uniforms.add(uniform);
		}
		i++;
	    }
	    
	    // Find attributes
	    attributes = new ArrayList<String>();
	    i = 0;
	    previous = "";
	    count = GL20.glGetProgram(shaderID, GL20.GL_ACTIVE_ATTRIBUTES);
	    while(i < count)
	    {
		String attribute = GL20.glGetActiveAttrib(shaderID, i, 100);
		if(attribute.isEmpty() == false)
		{
		    if(previous.isEmpty() == false && attribute.matches(previous))
		    {
			break;
		    }
		    previous = attribute;
		    attributes.add(attribute);
		}
		i++;
	    }
	    
	    // Done
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

    public int getUniformAddress(String name) throws Exception
    {
	if(uniforms == null)
	{
	    throw new Exception("Shader not loaded.");
	}
	for(int i = 0; i < uniforms.size(); i ++)
	{
	    if(uniforms.get(i).compareTo(name) == 0)
	    {
		return i;
	    }
	}
	
	String e = "Uniform (" + name + ") not found!\n";
	e += "Uniforms: " + uniforms.size() + "\n";
	for(int i = 0; i < uniforms.size(); i ++)
	{
	    e += i + " (" + uniforms.get(i) + ")\n";
	}
	e += "Attributes: " + attributes.size() + "\n";
	for(int i = 0; i < attributes.size(); i ++)
	{
	    e += i + " (" + attributes.get(i) + ")\n";
	}
	throw new Exception(e);
    }
	
    public void setUniform3fp(String name, Point p) throws Exception
    {
	setUniform3f(name, (float)p.x, (float)p.y, (float)p.z);
    }
    
    public void setUniform4fp(String name, Point p, float a) throws Exception
    {
	setUniform4f(name, (float)p.x, (float)p.y, (float)p.z, a);
    }     
 
    public void setUniform1f(String name, float a) throws Exception
    {
	GL20.glUniform1f(getUniformAddress(name), a);
    }    
    
    public void setUniform2f(String name, float a, float b) throws Exception
    {
	GL20.glUniform2f(getUniformAddress(name), a, b);
    }
	
    public void setUniform3f(String name, float a, float b, float c) throws Exception
    {
	GL20.glUniform3f(getUniformAddress(name), a, b, c);
    }
    
    public void setUniform4f(String name, float a, float b, float c, float d) throws Exception
    {
	GL20.glUniform4f(getUniformAddress(name), a, b, c, d);
    }
    
    public void setUniformTexture(String name, Texture tex, int index) throws Exception
    {
	tex.bind(index);
	GL20.glUniform1i(getUniformAddress(name), index);
    }
    
    public int getAttributeAddress(String name) throws Exception
    {
	if(attributes == null)
	{
	    throw new Exception("Shader not loaded.");
	}
	for(int i = 0; i < attributes.size(); i ++)
	{
	    if(attributes.get(i).compareTo(name) == 0)
	    {
		return i;
	    }
	}
	
	String e = "Attribute (" + name + ") not found!\n";
	e += "Uniforms: " + uniforms.size() + "\n";
	for(int i = 0; i < uniforms.size(); i ++)
	{
	    e += i + " (" + uniforms.get(i) + ")\n";
	}
	e += "Attributes: " + attributes.size() + "\n";
	for(int i = 0; i < attributes.size(); i ++)
	{
	    e += i + " (" + attributes.get(i) + ")\n";
	}
	throw new Exception(e);	
    }
    
    public void setAttribute3fp(String name, Point p) throws Exception
    {
	setAttribute3f(name, (float)p.x, (float)p.y, (float)p.z);
    }
    
    public void setAttribute4fp(String name, Point p, float a) throws Exception
    {
	setAttribute4f(name, (float)p.x, (float)p.y, (float)p.z, a);
    }     
 
    public void setAttribute1f(String name, float a) throws Exception
    {
	GL20.glVertexAttrib1f(getAttributeAddress(name), a);
    }    
    
    public void setAttribute2f(String name, float a, float b) throws Exception
    {
	GL20.glVertexAttrib2f(getAttributeAddress(name), a, b);
    }
	
    public void setAttribute3f(String name, float a, float b, float c) throws Exception
    {
	GL20.glVertexAttrib3f(getAttributeAddress(name), a, b, c);
    }

    public void setAttribute4f(String name, float a, float b, float c, float d) throws Exception
    {
	GL20.glVertexAttrib4f(getAttributeAddress(name), a, b, c, d);
    }
}
