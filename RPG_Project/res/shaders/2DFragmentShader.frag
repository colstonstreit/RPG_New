#version 330 core

out vec4 FragColor;

in vec3 color;
in vec2 texCoords;

uniform float time;
uniform sampler2D texture1;
uniform sampler2D texture2;

void main()
{
    FragColor = vec4(color, 1.0); // 0.4 * vec4(color.rg, time, 1.0) + 0.3 * texture(texture1, texCoords) + 0.3 * texture(texture2, texCoords);
}