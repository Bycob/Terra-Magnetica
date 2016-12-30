
// Main shader
#version 330 core

uniform sampler2D tex0;

uniform bool useTextures;
uniform bool useLights;
uniform bool useColor;

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
	vec4 tempColor = vec4(1, 1, 1, 1);
	if (useColor) {
		tempColor *= fragColor;
	}
	
	//Alpha test
	if (tempColor.a <= 1.0 / 255.0) discard;
	
	if (useTextures) {
		tempColor *= texture(tex0, fragTexCoord);
	}
	
	//Alpha test
	if (tempColor.a <= 1.0 / 255.0) discard;
	
	finalColor = applyLights(tempColor);
}
