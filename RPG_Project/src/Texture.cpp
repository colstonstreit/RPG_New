#include "../include/Texture.h"

#include <glad/glad.h>
#include <GLFW/glfw3.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <stb_image.h>

#include <iostream>

Texture::Texture(const char* path) {

    // Allocate texture and get ID
    unsigned int textureID = 0;
    glGenTextures(1, &textureID);
    this->id = textureID;

    stbi_set_flip_vertically_on_load(true);

    int width, height, numComponents;
    unsigned char* data = stbi_load(path, &width, &height, &numComponents, 0);
    this->width = width;
    this->height = height;

    if (data) {
        GLenum format = 0;
        if (numComponents == 1)
            format = GL_RED;
        else if (numComponents == 3)
            format = GL_RGB;
        else if (numComponents == 4)
            format = GL_RGBA;

        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, data);
        glGenerateMipmap(GL_TEXTURE_2D);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        stbi_image_free(data);
    } else {
        std::cout << "Texture failed to load at path: " << path << std::endl;
        stbi_image_free(data);
    }
}

void Texture::bind() {
    glBindTexture(GL_TEXTURE_2D, this->id);
}

unsigned int Texture::getID() const {
    return this->id;
}

unsigned int Texture::getWidth() const {
    return this->width;
}

unsigned int Texture::getHeight() const {
    return this->height;
}
