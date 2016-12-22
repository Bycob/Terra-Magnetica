// NOUVEAU SHADER

// Main shader
#version 330 core

uniform bool useTextures;
uniform bool useLights;

uniform sampler2D tex;

uniform struct {
	mat4 projection;
	mat4 camera;
	mat4 model;
	vec3 cameraPosition;
} view;


vec4 applyLights(vec4 initColor);

in vec3 fragVert;
in vec2 fragTexCoord;
in vec3 fragNormal;
in vec3 fragColor;

out vec4 finalColor;

void main() {
	vec4 tempColor = vec4(fragColor, 1);
	if (useTextures) {
		tempColor *= texture(tex, fragTexCoord);
	}
	
	finalColor = applyLights(tempColor);
}
