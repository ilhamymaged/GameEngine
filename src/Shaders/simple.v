#version 460 core
layout(location = 0) in vec3 aPos;
layout(location = 1) in vec3 aNormals;
layout(location = 2) in vec2 aTexCoords;

out vec2 TexCoords;
out vec3 Normals;
out vec3 FragPos;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main() {
    vec4 worldPos = transformationMatrix * vec4(aPos, 1.0);
    FragPos = worldPos.xyz;
    mat3 normalMatrix = transpose(inverse(mat3(transformationMatrix)));
    Normals = normalMatrix * aNormals;
    gl_Position = projectionMatrix * viewMatrix * worldPos;
    TexCoords = aTexCoords;
}