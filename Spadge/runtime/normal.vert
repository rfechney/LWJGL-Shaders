varying vec3 lightVec;
varying vec3 halfVec;
//varying vec3 eyeVec;
varying vec2 texCoord;

attribute vec3 u;
attribute vec3 v;
attribute vec3 w;
uniform vec4 lightSource;

void main()
{
	texCoord = gl_MultiTexCoord0.st;

	vec3 vertexPosition = vec3(gl_ModelViewMatrix * gl_Vertex);
	vec3 lightDir = normalize(lightSource.xyz - vertexPosition);	
		
	// transform light and half angle vectors by tangent basis
	lightVec.x = dot(lightDir, u);
	lightVec.y = dot(lightDir, v);
	lightVec.z = dot(lightDir, w);
	lightVec = normalize(lightVec);
	
	vertexPosition = normalize(vertexPosition);
	
	// Normalize the halfVector to pass it to the fragment shader
	vec3 halfVector = vertexPosition + lightDir;
	halfVec.x = dot(halfVector, u);
	halfVec.y = dot(halfVector, v);
	halfVec.z = dot(halfVector, w);
	halfVector = normalize(halfVector);
  
	gl_Position = ftransform();
}