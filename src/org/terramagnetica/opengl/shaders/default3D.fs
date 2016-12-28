
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


in vec3 fragVert;
in vec2 fragTexCoord;
in vec3 fragNormal;
in vec4 fragColor;

out vec4 finalColor;


vec4 applyLights(vec4 initColor);

void main() {
	vec4 tempColor = vec4(fragTexCoord, 0, 0) + vec4(0.5, 0.5, 0.5, 1);
	if (useTextures) {
		tempColor *= texture(tex, fragTexCoord);
	}
	
	finalColor = applyLights(tempColor);
}
