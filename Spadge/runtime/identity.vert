varying vec2 passedTextureCoord0;

// Do nothing execpt apply matrix to the vertex.
// Pass the multitexture co-ord through as passedTextureCoord

void main()
{
    passedTextureCoord0 = vec2(gl_MultiTexCoord0);
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}