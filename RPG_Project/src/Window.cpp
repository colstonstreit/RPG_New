#include "Window.h"

#include <glad/glad.h>
#include <GLFW/glfw3.h>

#include <functional>
#include <iostream>

Window* Window::_instance = nullptr;

Window::Window(unsigned int width, unsigned int height, const char* title)
    : width(width), height(height), title(title), lastMousePos(glm::vec2(0, 0)), window(nullptr), mouseScroll(0) {

    this->inputMap = {
        { Window::Input::LEFT, { RawInput(GLFW_KEY_LEFT), RawInput(GLFW_KEY_A) } },
        { Window::Input::RIGHT, { RawInput(GLFW_KEY_RIGHT), RawInput(GLFW_KEY_D) }},
        { Window::Input::FORWARD, { RawInput(GLFW_KEY_UP), RawInput(GLFW_KEY_W) }},
        { Window::Input::BACKWARD, { RawInput(GLFW_KEY_DOWN), RawInput(GLFW_KEY_S) }},
        { Window::Input::CLICK, { RawInput(GLFW_MOUSE_BUTTON_LEFT, false) } },
        { Window::Input::QUIT, { RawInput(GLFW_KEY_ESCAPE) } },
        { Window::Input::TOGGLE_DEBUG, { RawInput(GLFW_KEY_F), RawInput(GLFW_MOUSE_BUTTON_LEFT, false) } },
        { Window::Input::UP, { RawInput(GLFW_KEY_SPACE) } },
        { Window::Input::DOWN, { RawInput(GLFW_KEY_LEFT_SHIFT), RawInput(GLFW_KEY_RIGHT_SHIFT) } }
    };

    memset(this->currentKeyStates, 0, sizeof(bool) * static_cast<int>(Input::NUM_KEYS));
    memset(this->previousKeyStates, 0, sizeof(bool) * static_cast<int>(Input::NUM_KEYS));
}

bool Window::IsKeyPressed(Window::Input input) const {
    int keyEnumIndex = static_cast<int>(input);
    return this->currentKeyStates[keyEnumIndex];
}

bool Window::WasKeyClicked(Window::Input input) const {
    int keyEnumIndex = static_cast<int>(input);
    return this->previousKeyStates[keyEnumIndex] && !this->currentKeyStates[keyEnumIndex];
}

glm::vec2 Window::GetMousePos() const {
    double xpos, ypos;
    glfwGetCursorPos(this->window, &xpos, &ypos);
    return glm::vec2(xpos, ypos);
}

Window::~Window() {
    glfwTerminate();
}

Window& Window::sGet() {
    return *Window::_instance;
}

Window& Window::sGet(unsigned int width, unsigned int height, const char* title) {
    if (Window::_instance == nullptr) {
        Window::_instance = new Window(width, height, title);
    }
    return *Window::_instance;
}

void Window::InitGLFW() {
    /* Initialize GLFW */
    glfwInit();
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    /* Create window */
    this->window = glfwCreateWindow(this->width, this->height, title, NULL, NULL);
    if (this->window == NULL) {
        std::cout << "Failed to create GLFW window" << std::endl;
        glfwTerminate();
    }
    glfwMakeContextCurrent(this->window);

    /* Initialize GLAD before any GL calls */
    if (!gladLoadGLLoader((GLADloadproc) glfwGetProcAddress)) {
        std::cout << "Failed to initialize GLAD" << std::endl;
        glfwTerminate();
    }

    // Set viewport and enable depth
    glViewport(0, 0, width, height);
    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LEQUAL);

    // Capture cursor
    //glfwSetInputMode(this->window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

    // Set callbacks
    glfwSetScrollCallback(this->window, &Window::handleMouseScroll);
    glfwSetFramebufferSizeCallback(this->window, &Window::handleWindowResize);
}

unsigned int Window::GetWidth() const {
    return this->width;
}

unsigned int Window::GetHeight() const {
    return this->height;
}

void Window::Update() {

    // Update keyboard (and mouse buttons)
    memcpy(this->previousKeyStates, this->currentKeyStates, sizeof(bool) * static_cast<int>(Input::NUM_KEYS));
    memset(this->currentKeyStates, 0, sizeof(bool) * static_cast<int>(Input::NUM_KEYS));

    for (const auto& element : this->inputMap) {
        int keyEnumIndex = static_cast<int>(element.first);
        const std::vector<Window::RawInput>& rawInputs = element.second;
        for (auto it = rawInputs.begin(); it != rawInputs.end(); it++) {
            RawInput inputInfo = *it;
            if (inputInfo.typeIsKey) {
                this->currentKeyStates[keyEnumIndex] |= (glfwGetKey(this->window, inputInfo.inputCode) == GLFW_PRESS);
            } else {
                this->currentKeyStates[keyEnumIndex] |= (glfwGetMouseButton(this->window, inputInfo.inputCode) == GLFW_PRESS);
            }
        }
    }

    // Update mouse position and scroll
    this->lastMousePos = this->GetMousePos();
    this->mouseScroll = glm::vec2(0, 0);

    // Poll new events
    glfwPollEvents();
}

void Window::SwapBuffers() const {
    glfwSwapBuffers(this->window);
}

void Window::Close() const {
    glfwSetWindowShouldClose(this->window, true);
}

bool Window::ShouldClose() const {
    return glfwWindowShouldClose(this->window);
}

void Window::handleMouseScroll(GLFWwindow* window, double xOffset, double yOffset) {
    Window& windowInstance = Window::sGet();
    windowInstance.mouseScroll += glm::vec2(xOffset, yOffset);
}

void Window::handleWindowResize(GLFWwindow* window, int newWidth, int newHeight) {
    glViewport(0, 0, newWidth, newHeight);
    Window::_instance->width = newWidth;
    Window::_instance->height = newHeight;
}

glm::vec2 Window::GetLastMousePos() const {
    return this->lastMousePos;
}

glm::vec2 Window::GetMouseScroll() const {
    return this->mouseScroll;
}

Window::RawInput::RawInput(int inputCode, bool typeIsKey) : inputCode(inputCode), typeIsKey(typeIsKey) {}