#version 330 core

uniform bool stencils;

uniform bool useLights;

uniform bool inGame;

uniform vec3 levelColor;
uniform vec3 playerPos;
uniform float visionSize; // Si négatif, il y a pas.

uniform struct {
	mat4 projection;
	mat4 camera;
	mat4 model;
	vec3 cameraPosition;
} view;

in vec3 fragVert;

out vec4 finalColor;


// FONCTIONS

float getAttenuation(int lightID, float length);

vec4 applyBasics(vec4 initColor);
vec4 applyLights(vec4 initColor);

void main() {
	
	if (stencils) {
		finalColor = vec4(1);
		return;
	}
	
	vec4 tempColor = vec4(1);
	
	if (inGame) {
		// Level color
		tempColor *= vec4(levelColor, 1);
		
		// Vision limitée
		vec3 position = vec3(view.model * vec4(fragVert, 1));
		if (visionSize > 0) {
			float length = distance(vec2(position), vec2(playerPos));
			float factor = clamp(1.2 - (length / visionSize), 0, 1);
			
			tempColor *= vec4(factor, factor, factor, 1);
			
			// Attenuation des non modèles
			if (!useLights) {
				float attenuation = getAttenuation(0, length);
				tempColor *= vec4(attenuation, attenuation, attenuation, 1);
			}
		}
	}
	
	finalColor = 
		applyLights(
		applyBasics(
		tempColor));
}
