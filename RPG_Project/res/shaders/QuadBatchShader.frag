#version 330 core

out vec4 FragColor;

in vec3 color;
in vec2 texCoords;
flat in int textureID;

uniform sampler2D uTextures[16];

void main()
{
    if (textureID == -1) {
        FragColor = vec4(color, 1.0);
    } else {
        vec4 texColor = texture(uTextures[textureID], texCoords);
        if (texColor.a < 0.1)
            discard;
        if (texCoords.x < 0 || texCoords.x > 1 || texCoords.y < 0 || texCoords.y > 1)
            discard;
        FragColor = texColor;
    }
}