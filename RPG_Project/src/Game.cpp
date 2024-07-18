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

Game* Game::s_instance = nullptr;

static bool useWireframe = false;

Game::Game(unsigned int width, unsigned int height, const char* title) : window(width, height, title) {}

void Game::Initialize(unsigned int width, unsigned int height, const char* title) {
    if (Game::s_instance == nullptr) {
        Game::s_instance = new Game(width, height, title);
        Game::s_instance->GetWindow().InitGLFW();
        //Game::s_instance->resourceManager.LoadResources();
        Tile::sInitializeTiles();
    }
}

Game& Game::GetInstance() {
    return *Game::s_instance;
}

Game::~Game() {
    if (this->currentScene) {
        delete this->currentScene;
    }
}

void Game::Run() {

    const double fps = 60.0;
    double lastTime = glfwGetTime();
    double secondsPerFrame = 1.0 / fps;
    double delta = 0;

    while (!Game::GetWindow().ShouldClose()) {

        double now = glfwGetTime();
        double deltaTime = now - lastTime;
        delta += deltaTime / secondsPerFrame;
        lastTime = now;

        Game::update(deltaTime);
        Game::render();

        if (delta >= 1) {
            delta--;
            Game::render();
        }

    }
}

void Game::update(double deltaTime) {

    Window& window = Game::GetWindow();

    window.Update();

    if (window.IsKeyPressed(Window::Input::QUIT))
        window.Close();

    if (window.WasKeyClicked(Window::Input::TOGGLE_DEBUG)) {
        useWireframe = !useWireframe;
        glPolygonMode(GL_FRONT_AND_BACK, useWireframe ? GL_LINE : GL_FILL);
    }

    Game::GetCurrentScene().Update(deltaTime);

}

void Game::render() {
    // Clear screen
    glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // Actual rendering code
    Game::GetCurrentScene().Render();

    // Swap buffer
    Game::GetWindow().SwapBuffers();
}

void Game::Stop() {
    Game::GetWindow().Close();
}

void Game::ChangeScene(Scene* newScene) {
    if (newScene) {
        Game& game = Game::GetInstance();
        if (game.currentScene) {
            delete game.currentScene;
        }
        game.currentScene = newScene;
    }
}

int Game::GetWidth() {
    return Game::GetWindow().GetWidth();
}

int Game::GetHeight() {
    return Game::GetWindow().GetHeight();
}

Window& Game::GetWindow() {
    return Game::GetInstance().window;
}

Scene& Game::GetCurrentScene() {
    return *Game::GetInstance().currentScene;
}

ResourceManager& Game::GetResourceManager() {
    return Game::GetInstance().resourceManager;
}
