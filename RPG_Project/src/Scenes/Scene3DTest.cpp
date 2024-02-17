#include "Scenes/Scene3DTest.h"

#include "Game.h"
#include "ResourceManager.h"
#include "Shader.h"
#include "Texture.h"
#include "Window.h"

#include <glad/glad.h>
#include <GLFW/glfw3.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>

static float vertices[] = {
    // xyz                     // rgb                     // st
    -0.5f, -0.5f, -0.5f,        1.0f, 0.0f, 0.0f,          0.0f, 0.0f,
    -0.5f, -0.5f, 0.5f,         0.0f, 1.0f, 0.0f,          1.0f, 0.0f,
    -0.5f, 0.5f, -0.5f,			1.0f, 0.0f, 0.0f,          0.0f, 1.0f,
    -0.5f, 0.5f, 0.5f,			0.0f, 1.0f, 0.0f,          1.0f, 1.0f,
    0.5f, -0.5f, -0.5f,         1.0f, 0.0f, 0.0f,          1.0f, 0.0f,
    0.5f, -0.5f, 0.5f,          0.0f, 1.0f, 0.0f,          1.0f, 0.0f,
    0.5f, 0.5f, -0.5f,			1.0f, 0.0f, 0.0f,          1.0f, 1.0f,
    0.5f, 0.5f, 0.5f,			0.0f, 1.0f, 0.0f,          1.0f, 0.0f,
};

static unsigned int indices[] = {
    // Front
    0, 2, 6,
    0, 6, 4,
    // East
    4, 6, 7,
    4, 7, 5,
    // West
    1, 3, 2,
    1, 2, 0,
    // Back
    5, 7, 3,
    5, 3, 1,
    // Top
    2, 3, 7,
    2, 7, 6,
    // Bottom
    1, 0, 4,
    1, 4, 5
};

static glm::vec3 cubePositions[] = {
    glm::vec3(0.0f,  0.0f,  0.0f),
    glm::vec3(2.0f,  5.0f, -15.0f),
    glm::vec3(-1.5f, -2.2f, -2.5f),
    glm::vec3(-3.8f, -2.0f, -12.3f),
    glm::vec3(2.4f, -0.4f, -3.5f),
    glm::vec3(-1.7f,  3.0f, -7.5f),
    glm::vec3(1.3f, -2.0f, -2.5f),
    glm::vec3(1.5f,  2.0f, -2.5f),
    glm::vec3(1.5f,  0.2f, -1.5f),
    glm::vec3(-1.3f,  1.0f, -1.5f)
};

Scene3DTest::Scene3DTest(Game& game) : Scene(game) {}

void Scene3DTest::init() {

    // VAO
    glGenVertexArrays(1, &this->VAO);
    glBindVertexArray(this->VAO);

    // VBO
    glGenBuffers(1, &this->VBO);
    glBindBuffer(GL_ARRAY_BUFFER, this->VBO);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);

    // EBO
    glGenBuffers(1, &this->EBO);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);

    // Set VAO attributes
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(float), (void*) 0);
    glEnableVertexAttribArray(0);

    glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(float), (void*) (3 * sizeof(float)));
    glEnableVertexAttribArray(1);

    glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, 8 * sizeof(float), (void*) (6 * sizeof(float)));
    glEnableVertexAttribArray(2);

    const ResourceManager& resourceManager = this->game.getResourceManager();
    const Shader& shaderProgram = resourceManager.getShader(ResourceManager::EShader::DEFAULT);
    const Texture& texture1 = resourceManager.getTexture(ResourceManager::ETexture::BOX);
    const Texture& texture2 = resourceManager.getTexture(ResourceManager::ETexture::TILE_SHEET);

    glActiveTexture(GL_TEXTURE0);
    texture1.bind();
    glActiveTexture(GL_TEXTURE1);
    texture2.bind();

    // Set shader and texture units
    shaderProgram.use();
    shaderProgram.setUniformInt("texture1", 0);
    shaderProgram.setUniformInt("texture2", 1);

}

void Scene3DTest::update(double deltaTime) {

    // Process input
    const Window& window = this->game.getWindow();

    if (window.isKeyPressed(Window::Input::FORWARD)) this->camera.processKeyboardInput(CameraDirection::FORWARD, deltaTime);
    if (window.isKeyPressed(Window::Input::BACKWARD)) this->camera.processKeyboardInput(CameraDirection::BACKWARD, deltaTime);
    if (window.isKeyPressed(Window::Input::LEFT)) this->camera.processKeyboardInput(CameraDirection::LEFT, deltaTime);
    if (window.isKeyPressed(Window::Input::RIGHT)) this->camera.processKeyboardInput(CameraDirection::RIGHT, deltaTime);
    if (window.isKeyPressed(Window::Input::UP)) this->camera.processKeyboardInput(CameraDirection::UP, deltaTime);
    if (window.isKeyPressed(Window::Input::DOWN)) this->camera.processKeyboardInput(CameraDirection::DOWN, deltaTime);

    glm::vec2 mousePos = window.getMousePos();
    glm::vec2 prevMousePos = window.getLastMousePos();
    glm::vec2 mouseMoved = glm::vec2(mousePos.x - prevMousePos.x, prevMousePos.y - mousePos.y);
    this->camera.processMouseMovement(mouseMoved.x, mouseMoved.y);

    glm::vec2 mouseScroll = window.getMouseScroll();
    this->camera.processMouseScroll(mouseScroll.y);
}

void Scene3DTest::render() {

    const ResourceManager& resourceManager = this->game.getResourceManager();
    const Shader& shaderProgram = resourceManager.getShader(ResourceManager::EShader::DEFAULT);
    const Window& window = this->game.getWindow();

    double time = glfwGetTime();
    glm::mat4 view = this->camera.getViewMatrix();

    glm::mat4 projection = this->camera.getPerspectiveProjectionMatrix((float) window.getWidth() / window.getHeight(), 0.1f, 100.0f);
    //projection = glm::ortho(-10.0f, 10.0f, -10.0f, 10.0f, 0.1f, 100.0f);

    shaderProgram.setUniformFloat("time", sin(3.1415f * (float) time) / 2.0f + 0.5f);
    shaderProgram.setUniformMat4("view", view);
    shaderProgram.setUniformMat4("projection", projection);

    glBindVertexArray(this->VAO);

    for (unsigned int i = 0; i < 10; i++) {
        glm::mat4 model = glm::mat4(1.0f);
        model = glm::translate(model, cubePositions[i]);
        if (i % 2 == 0) {
            model = glm::rotate(model, (float) (time + i), glm::vec3(1.0f, 0.3f, 0.5f));
        }
        shaderProgram.setUniformMat4("model", model);
        glDrawElements(GL_TRIANGLES, sizeof(indices) / sizeof(float), GL_UNSIGNED_INT, 0);
    }
}

void Scene3DTest::teardown() {
    // Free resources
    glDeleteVertexArrays(1, &this->VAO);
    glDeleteBuffers(1, &this->EBO);
    glDeleteBuffers(1, &this->VBO);
}
