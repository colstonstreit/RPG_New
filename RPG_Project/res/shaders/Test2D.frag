#version 330 core

out vec4 FragColor;

in vec3 color;
in vec2 texCoords;

uniform float time;
uniform sampler2D texture1;

void main()
{
    if (texCoords.x == -1.0 && texCoords.y == -1.0f) {
        FragColor = vec4(color, 1.0);
    } else {
        vec4 texColor = texture(texture1, texCoords);
        if (texColor.a < 0.1)
            discard;
        if (texCoords.x < 0 || texCoords.x > 1 || texCoords.y < 0 || texCoords.y > 1)
            discard;
        FragColor = texColor;
    }
}