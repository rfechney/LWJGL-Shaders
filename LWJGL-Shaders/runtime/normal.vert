varying vec3 lightTS;
varying vec3 halfTS;
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

	// Find eye direction in modelspace
	vec3 eyeMS = -normalize((gl_ModelViewMatrix * gl_Vertex).xyz);
	vec3 halfMS = normalize(eyeMS + lightMS);
		
	// transform light, half and eye vectors by tangent basis
	lightTS.x = dot(lightMS, uMS);
	lightTS.y = dot(lightMS, vMS);
	lightTS.z = dot(lightMS, wMS);
	lightTS = normalize(lightTS);

	halfTS.x = dot(halfMS, uMS);
	halfTS.y = dot(halfMS, vMS);
	halfTS.z = dot(halfMS, wMS);
	halfTS = normalize(halfTS);

	// Done.  Do transform.
	gl_Position = ftransform();
}