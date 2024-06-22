#include "Game.h"

#include "Scene.h"
#include "Window.h"
#include "Tile.h"

#include <glad/glad.h>
#include <GLFW/glfw3.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>

#include <iostream>
#include <windows.h>


static bool useWireframe = false;

Game::Game(unsigned int width, unsigned int height, const char* title) :
    window(Window::sGet(width, height, title)) {

}

Game::~Game() {
    if (this->currentScene) {
        this->currentScene->Teardown();
        delete this->currentScene;
    }
}

void Game::Init() {
    this->window.InitGLFW();
    ResourceManager::LoadResources();
    Tile::sInitializeTiles();
}

void Game::Run() {

    const double fps = 60.0;
    double lastTime = glfwGetTime();
    double secondsPerFrame = 1.0 / fps;
    double delta = 0;

    while (!this->window.ShouldClose()) {

        double now = glfwGetTime();
        double deltaTime = now - lastTime;
        delta += deltaTime / secondsPerFrame;
        lastTime = now;

        this->update(deltaTime);
        this->render();

        if (delta >= 1) {
            delta--;
            this->render();
        }

    }
}

void Game::update(double deltaTime) {

    this->window.Update();

    if (this->window.IsKeyPressed(Window::Input::QUIT))
        this->window.Close();

    if (this->window.WasKeyClicked(Window::Input::TOGGLE_DEBUG)) {
        useWireframe = !useWireframe;
        glPolygonMode(GL_FRONT_AND_BACK, useWireframe ? GL_LINE : GL_FILL);
    }

    this->currentScene->Update(deltaTime);

}

void Game::render() {
    // Clear screen
    glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // Actual rendering code
    this->currentScene->Render();

    // Swap buffer
    this->window.SwapBuffers();
}

void Game::Stop() {
    this->window.Close();
}

void Game::ChangeScene(Scene* newScene) {
    if (newScene) {
        if (this->currentScene) {
            this->currentScene->Teardown();
            delete this->currentScene;
        }
        newScene->Init();
        this->currentScene = newScene;
    }
}

int Game::GetWidth() const {
    return this->window.GetWidth();
}

int Game::GetHeight() const {
    return this->window.GetHeight();
}

const Window& Game::GetWindow() const {
    return this->window;
}

const Scene& Game::GetCurrentScene() const {
    return *(this->currentScene);
}
