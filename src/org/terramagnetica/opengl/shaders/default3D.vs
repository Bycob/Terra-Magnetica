#version 330

uniform mat4 projection;
uniform mat4 camera;
uniform mat4 model;

in vec3 vert;
in vec2 texCoord;
in vec3 normal;

out vec2 fragTexCoord;
out vec3 fragVert;
out vec3 fragNormal;

void main() {
    //Passage au fragment shader
    fragTexCoord = texCoord;
    fragVert = vert;
    fragNorm = normal;

    //Application des modifications de position.
    gl_Position = projection * camera * model * vec4(vert, 1);
}