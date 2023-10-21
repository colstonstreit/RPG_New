#include <glad/glad.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>

#include <iostream>

#include "../include/Game.h"
#include "../include/Shader.h"
#include "../include/Camera.h"
#include "../include/Dialogue.h"
#include "../include/Texture.h"

static bool useWireframe = false;

// Global variables
static Camera camera(glm::vec3(0.0f, 0.0f, 3.0f));
static float deltaTime = 0.0f;	// Time between current frame and last frame
static float lastFrame = 0.0f; // Time of last frame

Game::Game(unsigned int width, unsigned int height, const char* title)
    : window(Window::get(width, height, title)) {

}

Game::~Game() {

}

void Game::init() {
    this->window.initGLFW();
    this->resourceManager.loadResources();
}

void Game::run() {

    // Get shader
    const Shader& shaderProgram = this->resourceManager.getShader(ResourceManager::EShader::DEFAULT);

    // Create vertices
    float vertices[] = {
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

    unsigned int indices[] = {
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

    glm::vec3 cubePositions[] = {
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

    // Textures
    const Texture& texture1 = this->resourceManager.getTexture(ResourceManager::ETexture::BOX);
    const Texture& texture2 = this->resourceManager.getTexture(ResourceManager::ETexture::FACE);

    glActiveTexture(GL_TEXTURE0);
    texture1.bind();
    glActiveTexture(GL_TEXTURE1);
    texture2.bind();


    // VAO
    unsigned int VAO;
    glGenVertexArrays(1, &VAO);
    glBindVertexArray(VAO);

    // VBO
    unsigned int VBO;
    glGenBuffers(1, &VBO);
    glBindBuffer(GL_ARRAY_BUFFER, VBO);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);

    // EBO
    unsigned int EBO;
    glGenBuffers(1, &EBO);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);

    // Set VAO attributes
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(float), (void*) 0);
    glEnableVertexAttribArray(0);

    glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(float), (void*) (3 * sizeof(float)));
    glEnableVertexAttribArray(1);

    glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, 8 * sizeof(float), (void*) (6 * sizeof(float)));
    glEnableVertexAttribArray(2);

    // Set shader and texture units
    shaderProgram.use();
    shaderProgram.setUniformInt("texture1", 0);
    shaderProgram.setUniformInt("texture2", 1);

    // Render Loop
    while (!this->window.shouldClose()) {

        // Process input
        if (this->window.isKeyPressed(Window::Input::QUIT))
            this->window.close();

        if (this->window.wasKeyClicked(Window::Input::TOGGLE_DEBUG))
            useWireframe = !useWireframe;

        if (this->window.isKeyPressed(Window::Input::FORWARD)) camera.processKeyboardInput(CameraDirection::FORWARD, deltaTime);
        if (this->window.isKeyPressed(Window::Input::BACKWARD)) camera.processKeyboardInput(CameraDirection::BACKWARD, deltaTime);
        if (this->window.isKeyPressed(Window::Input::LEFT)) camera.processKeyboardInput(CameraDirection::LEFT, deltaTime);
        if (this->window.isKeyPressed(Window::Input::RIGHT)) camera.processKeyboardInput(CameraDirection::RIGHT, deltaTime);
        if (this->window.isKeyPressed(Window::Input::UP)) camera.processKeyboardInput(CameraDirection::UP, deltaTime);
        if (this->window.isKeyPressed(Window::Input::DOWN)) camera.processKeyboardInput(CameraDirection::DOWN, deltaTime);

        glm::vec2 mousePos = this->window.getMousePos();
        glm::vec2 prevMousePos = this->window.getLastMousePos();
        glm::vec2 mouseMoved = glm::vec2(mousePos.x - prevMousePos.x, prevMousePos.y - mousePos.y);
        camera.processMouseMovement(mouseMoved.x, mouseMoved.y);

        glm::vec2 mouseScroll = this->window.getMouseScroll();
        camera.processMouseScroll(mouseScroll.y);


        // Clear screen
        glClearColor(0.2, 0.3, 0.3, 1.0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Wireframe mode
        glPolygonMode(GL_FRONT_AND_BACK, useWireframe ? GL_LINE : GL_FILL);

        // Render
        float time = glfwGetTime();
        float radius = 10;
        glm::mat4 view = camera.getViewMatrix();

        glm::mat4 projection = camera.getPerspectiveProjectionMatrix((float) this->window.getWidth() / this->window.getHeight(), 0.1f, 100.0f);
        //projection = glm::ortho(-10.0f, 10.0f, -10.0f, 10.0f, 0.1f, 100.0f);

        // Update time
        float currentFrame = glfwGetTime();
        deltaTime = currentFrame - lastFrame;
        lastFrame = currentFrame;


        shaderProgram.setUniformFloat("time", sin(3.1415 * time) / 2 + 0.5f);
        shaderProgram.setUniformMat4("view", view);
        shaderProgram.setUniformMat4("projection", projection);

        glBindVertexArray(VAO);

        for (unsigned int i = 0; i < 10; i++) {
            glm::mat4 model = glm::mat4(1.0f);
            //model = glm::rotate(model, glm::radians(-55.0f), glm::vec3(1.0f, 0.0f, 0.0f));
            model = glm::translate(model, cubePositions[i]);
            if (i % 2 == 0) {
                model = glm::rotate(model, time + i, glm::vec3(1.0f, 0.3f, 0.5f));
            }
            shaderProgram.setUniformMat4("model", model);
            glDrawElements(GL_TRIANGLES, sizeof(indices) / sizeof(float), GL_UNSIGNED_INT, 0);
        }

        // Poll Events and Swap Buffers
        this->window.swapBuffers();
        this->window.update();
    }

    // Free resources
    glDeleteVertexArrays(1, &VAO);
    glDeleteBuffers(1, &EBO);
    glDeleteBuffers(1, &VBO);

}

void Game::update() {
    this->window.update();
}

void Game::render() {

}

void Game::stop() {
    this->window.close();
}
