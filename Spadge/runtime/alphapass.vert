varying vec2 passedTextureCoord0;
varying vec4 passedIncidentVec;
varying float alpha;

// Do nothing execpt apply matrix to the vertex.
// Pull alpha out of vector and pass it through.
// Pass the multitexture co-ord through as passedTextureCoord

void main()
{
    passedTextureCoord0 = vec2(gl_MultiTexCoord0);
    alpha = gl_Vertex[3];
    vec4 pos = gl_Vertex;
    pos[3] = 1.0;
    gl_Position = gl_ModelViewProjectionMatrix * pos;
    passedIncidentVec = gl_Position;
}