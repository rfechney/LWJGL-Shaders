#extension GL_EXT_gpu_shader4 : enable

varying vec3 lightVec;
varying vec3 halfVec;
varying vec3 eyeVec;
varying vec2 texCoord;

uniform sampler2D texMap;
uniform sampler2D normMap;

void main()
{

	// lookup normal from normal map, move from [0,1] to  [-1, 1] range, normalize
	vec3 normal = 2.0 * texture2D (normMap, texCoord).rgb - 1.0;
	normal = normalize (normal);
	
	// compute diffuse lighting
	float lamberFactor= max (dot (lightVec, normal), 0.0);
	vec4 diffuseMaterial;
	vec4 diffuseLight;
	
	// compute specular lighting
	vec4 specularMaterial;
	vec4 specularLight;
	float shininess;
 
	// compute ambient
	vec4 ambientLight = vec4(0.0, 0.0, 0.0, 1.0);

	gl_FragColor = vec4(0.0);
	if (lamberFactor > 0.0)
	{
		diffuseMaterial = texture2D (texMap, texCoord);
		diffuseMaterial = vec4(0.5, 0.5, 0.5, 1.0);
		diffuseLight  = vec4(1.0, 1.0, 1.0, 1.0);
		
		// In doom3, specular value comes from a texture 
		specularMaterial =  vec4(1.0);
		specularLight = vec4(1.0, 1.0, 1.0, 1.0);
		shininess = pow (max (dot (halfVec, normal), 0.0), 2.0);
		 
		gl_FragColor =	diffuseMaterial * diffuseLight * lamberFactor;
		gl_FragColor +=	specularMaterial * specularLight * shininess;			
	
	}
	
	gl_FragColor +=	ambientLight;
}