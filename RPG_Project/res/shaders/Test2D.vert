#version 330 core

layout (location=0) in vec2 aPos;
layout (location=1) in vec3 aColor;
layout (location=2) in vec2 aTexCoords;

out vec3 color;
out vec2 texCoords;

uniform mat4 projection;

void main()
{
    //gl_Position = vec4((aPos.x + 0.5f) / 4 * 2 - 1, -((aPos.y + 0.5f) / 4.0 * 2 - 1), 0.5, 1.0);
    gl_Position = projection *  vec4(aPos, 0.5, 1.0);
    color = aColor;
    texCoords = aTexCoords;
}