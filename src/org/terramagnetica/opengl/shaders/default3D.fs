#version 330

uniform mat4 model;
uniform vec3 cameraPos;

#define POINT_LIGHT 0
#define SUN_LIGHT 1

uniform struct {
    int type;
    vec3 position;
} light;

uniform struct {
    vec3 diffuseColor;
    vec3 specularColor;
    vec3 ambientColor;

    float specularIntensity;
    float specularHardness;
} material; //Valeurs par d�faut

in vec3 fragVert;
in vec3 fragNorm;

out vec4 finalColor;

void main() {
    //Calcul des positions r�elles des �l�ments de la scene
    vec3 normal = normalize(transpose(inverse(mat3(model))) * fragNorm);
    vec3 position = vec3(model * vec4(fragVert, 1));
    vec3 lightPos = light.position; //vec3(model * vec4(lightPos, 1));

    //Calcul du vecteur fragment->camera
    vec3 fragToCam = normalize(cameraPos - position);


    //Calcul des caract�ristiques de la lumi�re
    //d�termination du vecteur lumi�re
    vec3 lightVect;
    float lightVectLength;

    if (light.type == SUN_LIGHT) {
        lightVect = - lightPos;
    }
    else {
        lightVect = position - lightPos;
    }

    //att�nuation en mode POINT_LIGHT
    float attenuation = 1;
    if (light.type == POINT_LIGHT) {
        attenuation *= 1 / (1 + lightVectLength * lightVectLength * 0.1);
    }

    lightVectLength = length(lightVect);
    lightVect = normalize(lightVect);
	
    //Calcul de l'effet de la lumi�re sur le mat�riau
    //intensit� diffuse
    float diffuseCoef = max(- dot(normal, lightVect), 0);
    vec3 diffuseIntensity = vec3(diffuseCoef);
    diffuseIntensity *= material.diffuseColor;

    //intensit� ambiente
    vec3 ambientIntensity = vec3(0.2);
    ambientIntensity *= material.ambientColor;

    //intensit� speculaire
    vec3 specularIntensity = vec3(0.0);
    if (diffuseCoef != 0) {
        specularIntensity = vec3(pow(max(dot(fragToCam, reflect(lightVect, normal)), 0), material.specularHardness));
    }
    specularIntensity *= material.specularColor * material.specularIntensity;


    //Calcul final
    vec3 gamma = vec3(1);
    finalColor = vec4(pow(ambientIntensity + attenuation * (diffuseIntensity + specularIntensity), gamma), 1);
}


// NOUVEAU SHADER

// Main shader
#version 330 core

uniform struct {
	mat4 projection;
	mat4 camera;
	mat4 model;
	vec3 cameraPosition;
} view;


vec4 applyLight(int lightID, vec4 initColor);

in vec3 fragVert;
in vec2 fragTexCoord;
in vec3 fragNormal;

out vec4 finalColor;

void main() {
	
}
