#version 460 core
out vec4 FragColor;

in vec2 TexCoords;
uniform sampler2D tex;

in vec3 Normals;
in vec3 FragPos;

uniform vec3 cameraPos;
uniform vec3 lightPos;
uniform vec3 lightColor;

void main() {

    vec3 normals = normalize(Normals);
    vec3 toLight = normalize(lightPos - FragPos);

    //Ambient Lighting
    vec3 ambient = 0.15 * lightColor;

    //Diffuse Lighting
    float diff  = max(dot(toLight, normals), 0.0);
    vec3 diffuse = diff * lightColor;

    //Specular Lighting
    vec3 toCamera = normalize(cameraPos - FragPos);
    vec3 reflectedDir = reflect(-toLight, normals);
    float spec = pow(max(dot(reflectedDir, toCamera), 0.0), 64.0f);
    vec3 specular = spec * lightColor;

    vec4 result = vec4(ambient + diffuse + specular, 1.0) * texture(tex, TexCoords);
    //vec4 result = texture(tex, TexCoords);
    FragColor = result;
}