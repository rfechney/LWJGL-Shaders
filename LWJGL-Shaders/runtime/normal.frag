#extension GL_EXT_gpu_shader4 : enable

varying vec3 lightTS;
varying vec3 halfTS;
varying vec2 texCoord;

uniform sampler2D texMap;
uniform sampler2D normMap;

void main()
{

	// lookup normal from normal map, move from [0,1] to  [-1, 1] range, normalize
	vec3 normal = normalize(2.0 * texture2D(normMap, texCoord).rgb - vec3(1.0, 1.0, 1.0));

	// determine if lit by light
	float incident = max (dot (lightTS, normal), 0.0);
 
	// set ambient
	gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);

	// compute lighting
	if (incident > 0.0)
	{
		// compute diffuse lighting
		vec4 diffuseMaterial = texture2D (texMap, texCoord);
		vec4 diffuseLight  = vec4(1.0, 1.0, 1.0, 1.0);
		
		// compute specular lighting
		vec4 specularMaterial = vec4(1.0, 1.0, 1.0, 1.0);
		vec4 specularLight = vec4(1.0, 1.0, 1.0, 1.0);
		float shininess = pow (max (dot (halfTS, normal), 0.0), 10.0);
		
		// apply lighting
		gl_FragColor +=	diffuseMaterial * diffuseLight * incident;
		gl_FragColor +=	specularMaterial * specularLight * shininess;			
	
	}
}