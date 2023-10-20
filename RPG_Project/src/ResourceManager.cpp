#include "../include/ResourceManager.h"
#include "../include/Shader.h"
#include "../include/Texture.h"

void ResourceManager::loadResources() {
    this->loadShaders();
    this->loadTextures();
}

Shader& ResourceManager::getShader(EShader eshader) {
    return this->shaderMap.at(eshader);
}

Texture& ResourceManager::getTexture(ETexture etexture) {
    return this->textureMap.at(etexture);
}

void ResourceManager::loadShaders() {
    this->shaderMap.emplace(EShader::DEFAULT, Shader("res/shaders/VertexShader.vert", "res/shaders/FragmentShader.frag"));
}

void ResourceManager::loadTextures() {
    this->textureMap.emplace(ETexture::FACE, Texture("res/img/awesomeface.png"));
    this->textureMap.emplace(ETexture::BOX, Texture("res/img/container.jpg"));
}
