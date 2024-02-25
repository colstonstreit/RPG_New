#pragma once

#include <glm/glm.hpp>

#include <string>
#include <unordered_map>
#include <vector>

struct GLFWwindow;

class Window {

public:
    // Keys
    enum class Input {
        FORWARD, BACKWARD, LEFT, RIGHT, CLICK, TOGGLE_DEBUG, QUIT, UP, DOWN,
        NUM_KEYS
    };

    // Singleton setup
    static Window& get();
    static Window& get(unsigned int width, unsigned int height, const char* title);
    Window(Window& other) = delete;
    void operator=(const Window&) = delete;

    void initGLFW();
    void update();
    void swapBuffers() const;
    void close() const;

    bool isKeyPressed(Window::Input input) const;
    bool wasKeyClicked(Window::Input input) const;
    glm::vec2 getMousePos() const;

    bool shouldClose() const;
    unsigned int getWidth() const;
    unsigned int getHeight() const;
    glm::vec2 getLastMousePos() const;
    glm::vec2 getMouseScroll() const;

private:
    struct RawInput {
        RawInput(int inputCode, bool typeIsKey = true);
        int inputCode;
        bool typeIsKey; // true if key, false if mouse
    };

    Window(unsigned int width, unsigned int height, const char* title);
    ~Window();
    static void handleMouseScroll(GLFWwindow* window, double xOffset, double yOffset);
    static void handleWindowResize(GLFWwindow* window, int newWidth, int newHeight);

private:
    static Window* _instance;

    GLFWwindow* window;

    // Key management
    std::unordered_map<Window::Input, std::vector<Window::RawInput>> inputMap;
    bool previousKeyStates[static_cast<int>(Input::NUM_KEYS)];
    bool currentKeyStates[static_cast<int>(Input::NUM_KEYS)];

    // Mouse management
    glm::vec2 lastMousePos;
    glm::vec2 mouseScroll;

    // Window properties
    unsigned int width;
    unsigned int height;
    const char* title;
};

