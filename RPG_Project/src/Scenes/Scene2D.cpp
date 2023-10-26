#include "Scenes/Scene2D.h"

#include "Camera2D.h"
#include "Game.h"
#include "ResourceManager.h"
#include "Shader.h"
#include "Texture.h"
#include "Window.h"

#include <glad/glad.h>
#include <GLFW/glfw3.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>

#include <iostream>

Scene2D::Scene2D(Game& game) : Scene(game), camera(game) {}

void Scene2D::init() {
    glGenVertexArrays(1, &this->VAO);
    glBindVertexArray(this->VAO);

    glGenBuffers(1, &this->VBO);
    glBindBuffer(GL_ARRAY_BUFFER, this->VBO);

    glGenBuffers(1, &this->EBO);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this->EBO);

    const unsigned int vertexSize = 2 + 3 + 2;
    this->vertices = new float[vertexSize * this->width * this->height * 4];
    this->indices = new unsigned int[6 * this->width * this->height];

    // Construct vertices
    unsigned int index = 0;
    for (int y = 0; y < this->height; y++) {
        for (int x = 0; x < this->width; x++) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {

                    // Position
                    this->vertices[index++] = x + j;
                    this->vertices[index++] = y + i;

                    // Color
                    this->vertices[index++] = (x + j) / (float) this->width;
                    this->vertices[index++] = (y + i) / (float) this->height;
                    this->vertices[index++] = 0;

                    // UVs
                    this->vertices[index++] = (float) j;
                    this->vertices[index++] = (float) i;

                }
            }
        }
    }
    glBufferData(GL_ARRAY_BUFFER, vertexSize * this->width * this->height * 4 * sizeof(float), this->vertices, GL_DYNAMIC_DRAW);

    // Construct indices
    index = 0;
    for (int y = 0; y < this->height; y++) {
        for (int x = 0; x < this->width; x++) {
            unsigned int tileBase = 4 * (y * this->width + x);
            this->indices[index++] = tileBase;
            this->indices[index++] = tileBase + 1;
            this->indices[index++] = tileBase + 2;

            this->indices[index++] = tileBase + 1;
            this->indices[index++] = tileBase + 2;
            this->indices[index++] = tileBase + 3;
        }
    }
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, 6 * this->width * this->height * sizeof(unsigned int), this->indices, GL_STATIC_DRAW);

    // Configure VAO
    glVertexAttribPointer(0, 2, GL_FLOAT, GL_FALSE, 7 * sizeof(float), (void*) 0);
    glEnableVertexAttribArray(0);

    glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 7 * sizeof(float), (void*) (sizeof(float) * 2));
    glEnableVertexAttribArray(1);

    glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, 7 * sizeof(float), (void*) (sizeof(float) * 5));
    glEnableVertexAttribArray(2);

    const ResourceManager& resourceManager = this->game.getResourceManager();
    const Shader& shaderProgram = resourceManager.getShader(ResourceManager::EShader::TEST_2D);

    shaderProgram.use();
}

void Scene2D::update(double deltaTime) {
    this->camera.update(deltaTime);
}

void Scene2D::render() {
    const ResourceManager& resourceManager = this->game.getResourceManager();
    const Shader& shaderProgram = resourceManager.getShader(ResourceManager::EShader::TEST_2D);

    glm::mat4 projection = this->camera.getWorldToScreenMatrix();
    shaderProgram.setUniformMat4("projection", projection);

    glBindVertexArray(this->VAO);
    glDrawElements(GL_TRIANGLES, 6 * this->width * this->height, GL_UNSIGNED_INT, 0);
}

void Scene2D::teardown() {
    // Free resources
    glDeleteVertexArrays(1, &this->VAO);
    glDeleteBuffers(1, &this->EBO);
    glDeleteBuffers(1, &this->VBO);
}
