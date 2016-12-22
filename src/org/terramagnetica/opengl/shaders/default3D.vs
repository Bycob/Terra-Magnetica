//Main shader
#version 330 core

//uniforms
uniform struct {
	mat4 projection;
	mat4 camera;
	mat4 model;
	vec3 cameraPosition;
} view;

in vec3 vert;
in vec2 texCoord;
in vec3 normal;
in vec3 color;

out vec3 fragVert;
out vec2 fragTexCoord;
out vec3 fragNormal;
out vec3 fragColor;

void main() {
    //Passage au fragment shader
    fragTexCoord = texCoord;
    fragVert = vert;
    fragNormal = normal;
    fragColor = color;

    //Application des modifications de position.
    gl_Position = view.projection * view.camera * view.model * vec4(vert, 1);
}

