#include "Game.h"

#include "Dialogue.h"
#include "Scene.h"
#include "Window.h"

#include <glad/glad.h>
#include <GLFW/glfw3.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>

#include <iostream>
#include <windows.h>


static bool useWireframe = false;

Game::Game(unsigned int width, unsigned int height, const char* title) :
    window(Window::get(width, height, title)) {

}

Game::~Game() {
    if (this->currentScene) {
        this->currentScene->teardown();
        delete this->currentScene;
    }
}

void Game::init() {
    this->window.initGLFW();
    this->resourceManager.loadResources();
}

void Game::run() {

    const double fps = 60.0;
    double lastTime = glfwGetTime();
    double secondsPerFrame = 1.0 / fps;
    double delta = 0;

    while (!this->window.shouldClose()) {

        double now = glfwGetTime();
        double deltaTime = now - lastTime;
        delta += deltaTime / secondsPerFrame;
        lastTime = now;

        this->update(deltaTime);

        if (delta >= 1) {
            delta--;
            this->render();
        }

    }
}

void Game::update(double deltaTime) {

    if (this->window.isKeyPressed(Window::Input::QUIT))
        this->window.close();

    if (this->window.wasKeyClicked(Window::Input::TOGGLE_DEBUG)) {
        useWireframe = !useWireframe;
        glPolygonMode(GL_FRONT_AND_BACK, useWireframe ? GL_LINE : GL_FILL);
    }

    this->currentScene->update(deltaTime);
    this->window.update();
}

void Game::render() {
    // Clear screen
    glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // Actual rendering code
    this->currentScene->render();

    // Swap buffer
    this->window.swapBuffers();
}

void Game::stop() {
    this->window.close();
}

void Game::changeScene(Scene* newScene) {
    if (newScene) {
        if (this->currentScene) {
            this->currentScene->teardown();
            delete this->currentScene;
        }
        newScene->init();
        this->currentScene = newScene;
    }
}

int Game::getWidth() const {
    return this->window.getWidth();
}

int Game::getHeight() const {
    return this->window.getHeight();
}

const Window& Game::getWindow() const {
    return this->window;
}

const ResourceManager& Game::getResourceManager() const {
    return this->resourceManager;
}

const Scene& Game::getCurrentScene() const {
    return *(this->currentScene);
}
