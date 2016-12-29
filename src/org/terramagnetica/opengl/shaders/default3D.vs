//Main shader
#version 330

//uniforms
uniform struct {
	mat4 projection;
	mat4 camera;
	mat4 model;
	vec3 cameraPosition;
} view;

attribute vec2 v_texCoord;
attribute vec3 v_normal;
attribute vec3 v_pos;
attribute vec4 v_color;

out vec3 fragVert;
out vec2 fragTexCoord;
out vec3 fragNormal;
out vec4 fragColor;

void main() {
    //Passage au fragment shader
    fragTexCoord = v_texCoord;
    fragVert = v_pos;
    fragNormal = v_normal;
    fragColor = v_color;

    //Application des modifications de position.
    gl_Position = view.projection * view.camera * view.model * vec4(v_pos, 1);
}

