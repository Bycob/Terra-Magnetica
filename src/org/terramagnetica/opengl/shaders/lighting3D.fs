// Lighting shader
#version 330 core

#define true 1
#define false 0

//types de lumières
#define MAX_LIGHT 10
#define POINT 0
#define SUN 1
#define SPOT 2

//uniforms
uniform bool useTextures;
uniform bool useLights;

uniform struct {
	mat4 projection;
	mat4 camera;
	mat4 model;
	vec3 cameraPosition;
} view;

uniform struct {
	int activated;

	int type;
	vec3 position;
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
	vec3 attenuation;
} light[MAX_LIGHT];

uniform struct {
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
	float specularIntensity;
	float shininess;
} material;

in vec3 fragVert;
in vec2 fragTexCoord;
in vec3 fragNormal;
in vec4 fragColor;

vec4 applyLight(int lightID) {
	if (light[lightID].activated == 0) {
		return vec4(0);
	}
	
	//Calcul des positions réelles des éléments de la scene
    vec3 normal = normalize(transpose(inverse(mat3(view.model))) * fragNormal);
    vec3 position = vec3(view.model * vec4(fragVert, 1));
    vec3 lightPos = light[lightID].position; //vec3(view.model * vec4(lightPos, 1));

    //Calcul du vecteur fragment->camera
    vec3 fragToCam = normalize(view.cameraPosition - position);


    //Calcul des caractéristiques de la lumière
    //détermination du vecteur lumière
    vec3 fragToLight;
    float fragToLightLength;

    if (light[lightID].type == SUN) {
        fragToLight = - lightPos;
    }
    else {
        fragToLight = position - lightPos;
    }

    fragToLightLength = length(fragToLight);
    fragToLight /= fragToLightLength;


    //atténuation en mode POINT_LIGHT
    float attenuation = 1;
    if (light[lightID].type == POINT) {
        attenuation *= 1 / (light[lightID].attenuation.x
        	+ fragToLightLength * light[lightID].attenuation.y
        	+ fragToLightLength * fragToLightLength * light[lightID].attenuation.z);
    }
	
	
    //Calcul de l'effet de la lumière sur le matériau
    
    //intensité diffuse
    vec3 diffuseIntensity = vec3(max(- dot(normal, fragToLight), 0));
    diffuseIntensity *= material.diffuse * light[lightID].diffuse;

    //intensité ambiente
    vec3 ambientIntensity = vec3(1);
    ambientIntensity *= material.ambient * light[lightID].ambient;

    //intensité speculaire
    vec3 specularIntensity = vec3(pow(max(dot(fragToCam, reflect(fragToLight, normal)), 0), material.shininess));
    specularIntensity *= diffuseIntensity * material.specular * light[lightID].specular * material.specularIntensity;


    //Calcul final
    vec3 gamma = vec3(1);
    return vec4(pow(ambientIntensity + attenuation * (diffuseIntensity + specularIntensity), gamma), 1);
}

vec4 applyLights(vec4 initColor) {
    if (!useLights) {
    	return initColor;
    }
    
    //Application de chaque lumière
	vec4 color = vec4(0);
	for (int i = 0 ; i < MAX_LIGHT ; i++) {
		color += applyLight(i);
	}
	return clamp(color * initColor, vec4(0), vec4(1));
}