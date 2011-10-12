/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Core.OpenGL.Shaders;

import org.lwjgl.opengl.ARBFragmentShader;

/**
 *
 * @author ryanf
 */
public class FragmentShader extends ShaderProgram
{
    FragmentShader(String sourceCode) throws Exception
    {
	super(sourceCode, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
    }
}
