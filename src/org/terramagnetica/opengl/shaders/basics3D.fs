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

vec4 applyBasics(vec4 initColor) {
	if (useColor) {
		initColor *= fragColor;
	}
	
	//Alpha test
	if (initColor.a <= 1.0 / 255.0) discard;
	
	if (useTextures) {
		initColor *= texture(tex0, fragTexCoord);
	}
	
	//Alpha test
	if (initColor.a <= 1.0 / 255.0) discard;
	
	return initColor;
}