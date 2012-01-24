#extension GL_EXT_gpu_shader4 : enable

// Displacement texture applies a texture as a height map to a texture in pixel shader.

uniform sampler2D texture1;

varying vec2 passedTextureCoord0;
varying vec4 passedIncidentVec;
varying float alpha;

void main()
{
    // Using the displacement map, step to the texel that matches
    vec2 displacementTextureCoord = passedTextureCoord0;
    vec3 textureColour = vec3(texture2D(texture1, displacementTextureCoord));
    float uStep = passedIncidentVec[0] / -passedIncidentVec[3];
    float vStep = passedIncidentVec[1] / -passedIncidentVec[3];

    float height = (textureColour[0] + textureColour[1] + textureColour[2] - 1.5) * alpha;
    if(height > 0.0)
    {
	float check = 0.0;
	// Step back along ray until height < 0 as we're under the surface
	while(height > 0.0 && displacementTextureCoord[0] > 0.0 && displacementTextureCoord[0] < 1.0 && displacementTextureCoord[1] > 0.0 && displacementTextureCoord[1] < 1.0)
	{
	    displacementTextureCoord[0] = displacementTextureCoord[0] + 0.01;
	    displacementTextureCoord[1] = displacementTextureCoord[1] + 0.01;
	    check = check + 0.1;
	    textureColour = vec3(texture2D(texture1, displacementTextureCoord));
	    height = (textureColour[0] + textureColour[1] + textureColour[2] - 1.5) * alpha - check;
	}
    }
    else
    {
	float check = 0.0;
	// Step forward along ray until height > 0 as we're over the surface
	while(height < 0.0 && displacementTextureCoord[0] > 0.0 && displacementTextureCoord[0] < 1.0 && displacementTextureCoord[1] > 0.0 && displacementTextureCoord[1] < 1.0)
	{
	    displacementTextureCoord[0] = displacementTextureCoord[0] - 0.01;
	    displacementTextureCoord[1] = displacementTextureCoord[1] - 0.01;
	    check = check - 0.1;
	    textureColour = vec3(texture2D(texture1, displacementTextureCoord));
	    height = (textureColour[0] + textureColour[1] + textureColour[2] - 1.5) * alpha - check;
	}
    }

    // Using the displacement map, step to the texel that matches
    

    // Find the colour for the texture co-ordinate
    int x = int(displacementTextureCoord[0] * 10.0);
    int y = int(displacementTextureCoord[1] * 10.0);
    float c = float((x + y) & 1);
    textureColour = vec3(texture2D(texture1, displacementTextureCoord));
    gl_FragColor = vec4(textureColour[0] + c, textureColour[1] + c * displacementTextureCoord[0], textureColour[2] + c * displacementTextureCoord[1], 1.0);
}