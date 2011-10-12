#extension GL_EXT_gpu_shader4 : enable

uniform sampler2D texture1;

varying vec2 passedTextureCoord0;

void main()
{
    int x = int(passedTextureCoord0[0] * 10.0);
    int y = int(passedTextureCoord0[1] * 10.0);
    float c = float((x + y) & 1);
    vec3 textureColour = vec3(texture2D(texture1, passedTextureCoord));
    gl_FragColor = vec4(textureColour[0] + c, textureColour[1] + c * passedTextureCoord[0], textureColour[2] + c * passedTextureCoord[1], 1.0);
}