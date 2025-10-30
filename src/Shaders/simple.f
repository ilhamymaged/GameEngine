#version 460 core
out vec4 FragColor;

in vec2 TexCoords;
uniform sampler2D tex;

in vec3 Normals;
in vec3 FragPos;

uniform vec3 lightPos;
uniform vec3 lightColor;

void main() {

    vec3 normals = normalize(Normals);
    vec3 toLight = normalize(FragPos - lightPos);

    //Diffuse Lighting
    float diff  = max(dot(toLight, normals), 0.0);
    vec3 diffuse = diff * lightColor;

    vec4 result = vec4(diffuse, 1.0) * texture(tex, TexCoords);
    FragColor = result;
}