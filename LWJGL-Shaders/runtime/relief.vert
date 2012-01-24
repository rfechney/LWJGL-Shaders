varying vec3 lightTS;
varying vec3 halfTS;
varying vec3 eyeTS;
varying vec2 texCoord;

attribute vec3 uMS;
attribute vec3 vMS;
attribute vec3 wMS;
attribute vec4 lightMS;

void main()
{
	texCoord = gl_MultiTexCoord0.st;

	// Find light direction in modelspace
	vec3 lightMS = normalize(lightMS.xyz - gl_Vertex.xyz);

	// Find eye direction to the model in modelspace
	vec3 eyeMS = normalize((gl_ModelViewMatrix * gl_Vertex).xyz);

	// Use the eye and light direction to find the specular normal
	vec3 halfMS = normalize(lightMS - eyeMS);
		
	// transform light, half and eye vectors by tangent basis
	lightTS.x = dot(lightMS, uMS);
	lightTS.y = dot(lightMS, vMS);
	lightTS.z = dot(lightMS, wMS);
	lightTS = normalize(lightTS);

	halfTS.x = dot(halfMS, uMS);
	halfTS.y = dot(halfMS, vMS);
	halfTS.z = dot(halfMS, wMS);
	halfTS = normalize(halfTS);

	eyeTS.x = dot(eyeMS, uMS);
	eyeTS.y = dot(eyeMS, vMS);
	eyeTS.z = dot(eyeMS, wMS);
	//eyeTS = normalize(eyeTS);

	// Done.  Do transform.
	gl_Position = ftransform();
}