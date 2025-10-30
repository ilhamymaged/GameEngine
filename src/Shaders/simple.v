#version 460 core
layout(location = 0) in vec3 aPos;
layout(location = 1) in vec2 aTexCoords;

out vec2 TexCoords;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main() {
    TexCoords = aTexCoords;
    gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(aPos, 1.0);

}