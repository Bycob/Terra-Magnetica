// Lighting shader
#version 330 core

//types de lumières
#define MAX_LIGHT 10
#define POINT 0;
#define SUN 1;
#define SPOT 2;

//uniforms

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
	//TODO
} material;

vec4 applyLight(int lightID, vec4 initColor) {
	if (light[lightID].activated == 0) {
		return initColor;
	}
	
	return initColor;
}