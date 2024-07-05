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

void Scene3DTest::Init() {

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

    const ResourceManager& resourceManager = Game::GetResourceManager();
    const Shader& shaderProgram = resourceManager.GetShader(EShader::DEFAULT);
    const Texture& texture1 = resourceManager.GetTexture(ETexture::BOX);
    const Texture& texture2 = resourceManager.GetTexture(ETexture::TILE_SHEET);

    glActiveTexture(GL_TEXTURE0);
    texture1.Bind();
    glActiveTexture(GL_TEXTURE1);
    texture2.Bind();

    // Set shader and texture units
    shaderProgram.Use();
    shaderProgram.SetUniformInt("texture1", 0);
    shaderProgram.SetUniformInt("texture2", 1);
}

void Scene3DTest::Update(double deltaTime) {

    // Process input
    const Window& window = Game::GetWindow();

    if (window.IsKeyPressed(Window::Input::FORWARD)) this->camera.ProcessKeyboardInput(CameraDirection::FORWARD, deltaTime);
    if (window.IsKeyPressed(Window::Input::BACKWARD)) this->camera.ProcessKeyboardInput(CameraDirection::BACKWARD, deltaTime);
    if (window.IsKeyPressed(Window::Input::LEFT)) this->camera.ProcessKeyboardInput(CameraDirection::LEFT, deltaTime);
    if (window.IsKeyPressed(Window::Input::RIGHT)) this->camera.ProcessKeyboardInput(CameraDirection::RIGHT, deltaTime);
    if (window.IsKeyPressed(Window::Input::UP)) this->camera.ProcessKeyboardInput(CameraDirection::UP, deltaTime);
    if (window.IsKeyPressed(Window::Input::DOWN)) this->camera.ProcessKeyboardInput(CameraDirection::DOWN, deltaTime);

    glm::vec2 mousePos = window.GetMousePos();
    glm::vec2 prevMousePos = window.GetLastMousePos();
    glm::vec2 mouseMoved = glm::vec2(mousePos.x - prevMousePos.x, prevMousePos.y - mousePos.y);
    this->camera.ProcessMouseMovement(mouseMoved.x, mouseMoved.y);

    glm::vec2 mouseScroll = window.GetMouseScroll();
    this->camera.ProcessMouseScroll(mouseScroll.y);
}

void Scene3DTest::Render() {

    const Shader& shaderProgram = Game::GetResourceManager().GetShader(EShader::DEFAULT);
    const Window& window = Game::GetWindow();

    double time = glfwGetTime();
    glm::mat4 view = this->camera.GetViewMatrix();

    glm::mat4 projection = this->camera.GetPerspectiveProjectionMatrix((float) window.GetWidth() / window.GetHeight(), 0.1f, 100.0f);
    //projection = glm::ortho(-10.0f, 10.0f, -10.0f, 10.0f, 0.1f, 100.0f);

    shaderProgram.SetUniformFloat("time", sin(3.1415f * (float) time) / 2.0f + 0.5f);
    shaderProgram.SetUniformMat4("view", view);
    shaderProgram.SetUniformMat4("projection", projection);

    glBindVertexArray(this->VAO);

    for (unsigned int i = 0; i < 10; i++) {
        glm::mat4 model = glm::mat4(1.0f);
        model = glm::translate(model, cubePositions[i]);
        if (i % 2 == 0) {
            model = glm::rotate(model, (float) (time + i), glm::vec3(1.0f, 0.3f, 0.5f));
        }
        shaderProgram.SetUniformMat4("model", model);
        glDrawElements(GL_TRIANGLES, sizeof(indices) / sizeof(unsigned int), GL_UNSIGNED_INT, 0);
    }
}

void Scene3DTest::Teardown() {
    // Free resources
    glDeleteVertexArrays(1, &this->VAO);
    glDeleteBuffers(1, &this->EBO);
    glDeleteBuffers(1, &this->VBO);
}
