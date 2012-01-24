#extension GL_EXT_gpu_shader4 : enable

varying vec3 lightTS;
varying vec3 halfTS;
varying vec3 eyeTS;
varying vec2 texCoord;

uniform sampler2D texMap;
uniform sampler2D reliefMap;

void main()
{	

	vec2 newCoord = texCoord;

	// Determine relief if front face
	if(eyeTS.z < 0.0)
	{
	    // Move back for raytrace
	    vec2 nearCoord = texCoord - eyeTS.xy / 4.0;
	    vec2 farCoord = texCoord;
	    newCoord = nearCoord;

	    float z = 1.0;
	    float step = 1.0 / sqrt(eyeTS.x * eyeTS.x + eyeTS.y * eyeTS.y) / 256.0;
	    while(z > texture2D(reliefMap, newCoord).a)
	    {
		z-=step;
		newCoord = mix(farCoord, nearCoord, z);
	    }
	}

	// lookup normal from normal map, move from [0,1] to  [-1, 1] range, normalize
	vec3 reliefData = texture2D(reliefMap, newCoord).rgb;
	vec3 normal = normalize(2.0 * reliefData.rgb - vec3(1.0, 1.0, 1.0));

	// determine if lit by light
	float incident = max (dot (lightTS, normal), 0.0);
 
	// set ambient
	gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);

	// compute lighting
	if (incident > 0.0)
	{
		// compute diffuse lighting
		vec4 diffuseMaterial = texture2D (texMap, newCoord);
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