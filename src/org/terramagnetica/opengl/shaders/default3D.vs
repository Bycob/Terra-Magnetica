//Main shader
#version 330 core

//uniforms
uniform struct {
	mat4 projection;
	mat4 camera;
	mat4 model;
} view;

in vec3 vert;
in vec2 texCoord;
in vec3 normal;

out vec3 fragVert;
out vec2 fragTexCoord;
out vec3 fragNormal;

void main() {
    //Passage au fragment shader
    fragTexCoord = texCoord;
    fragVert = vert;
    fragNormal = normal;

    //Application des modifications de position.
    gl_Position = view.projection * view.camera * view.model * vec4(vert, 1);
}

