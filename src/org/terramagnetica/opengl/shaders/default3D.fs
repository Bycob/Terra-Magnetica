
// Main shader
#version 330 core

uniform bool stencils;

out vec4 finalColor;

vec4 applyBasics(vec4 initColor);
vec4 applyLights(vec4 initColor);

void main() {
	
	if (stencils) {
		finalColor = vec4(1);
		return;
	}
	
	finalColor = 
		applyLights(
		applyBasics(
		vec4(1, 1, 1, 1)));
}
