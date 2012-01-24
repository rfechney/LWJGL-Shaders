/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Core.OpenGL.Shaders;

import org.lwjgl.opengl.ARBVertexShader;

/**
 *
 * @author ryanf
 */
public class VertexShader extends ShaderProgram
{
    VertexShader(String sourceCode) throws Exception
    {
	super(sourceCode, ARBVertexShader.GL_VERTEX_SHADER_ARB);
    }
}
