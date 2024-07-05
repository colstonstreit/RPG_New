#pragma once

#include "ResourceManager.h"
#include "Window.h"

class Scene;

class Game {

public:

    // Singleton setup
    static void Initialize(unsigned int width, unsigned int height, const char* title);
    static Game& GetInstance();
    Game(Game& other) = delete;
    void operator=(const Game&) = delete;

    ~Game();

    static void Run();
    static void Stop();

    static void ChangeScene(Scene* newScene);

    static int GetWidth();
    static int GetHeight();

    static Window& GetWindow();
    static Scene& GetCurrentScene();
    static ResourceManager& GetResourceManager();

private:
    Game(unsigned int width, unsigned int height, const char* title);
    static void update(double deltaTime);
    static void render();

private:
    static Game* s_instance;

    Window window;
    ResourceManager resourceManager;
    Scene* currentScene = nullptr;
};

