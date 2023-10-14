#include <glad/glad.h>
#include <GLFW/glfw3.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <stb_image.h>

#include "../include/Renderer.h"
#include "../include/Camera.h"
#include "../include/Dialogue.h"

#include <iostream>
#include <string>
#include <windows.h>

// Settings
const unsigned int screenWidth = 800;
const unsigned int screenHeight = 600;
bool useWireframe = false;

// Global variables
Camera camera(glm::vec3(0.0f, 0.0f, 3.0f));
float deltaTime = 0.0f;	// Time between current frame and last frame
float lastFrame = 0.0f; // Time of last frame
float lastMouseX = screenWidth / 2, lastMouseY = screenHeight / 2;
bool firstMouse = true;

// Callbacks

void frameBufferSize_Callback(GLFWwindow* window, int width, int height) {
    glViewport(0, 0, width, height);
}

void processInput(GLFWwindow* window) {
    // Handle debug settings and escaping
    if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        glfwSetWindowShouldClose(window, true);
    if (glfwGetKey(window, GLFW_KEY_F) == GLFW_PRESS)
        useWireframe = true;
    if (glfwGetKey(window, GLFW_KEY_F) == GLFW_RELEASE)
        useWireframe = false;

    // Handle camera movement
    if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) camera.processKeyboardInput(CameraDirection::FORWARD, deltaTime);
    if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) camera.processKeyboardInput(CameraDirection::BACKWARD, deltaTime);
    if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) camera.processKeyboardInput(CameraDirection::LEFT, deltaTime);
    if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) camera.processKeyboardInput(CameraDirection::RIGHT, deltaTime);
    if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) camera.processKeyboardInput(CameraDirection::UP, deltaTime);
    if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS || glfwGetKey(window, GLFW_KEY_RIGHT_SHIFT) == GLFW_PRESS) camera.processKeyboardInput(CameraDirection::DOWN, deltaTime);

}

void handleMouseMovement(GLFWwindow* window, double xpos, double ypos) {

    if (firstMouse) {
        lastMouseX = xpos;
        lastMouseY = ypos;
        firstMouse = false;
    }

    float xOffset = xpos - lastMouseX;
    float yOffset = lastMouseY - ypos;

    lastMouseX = xpos;
    lastMouseY = ypos;

    camera.processMouseMovement(xOffset, yOffset);
}

void handleMouseScroll(GLFWwindow* window, double xOffset, double yOffset) {
    camera.processMouseScroll(yOffset);
}

// Helper Functions

unsigned int loadTexture(char const* path) {
    unsigned int textureID;
    glGenTextures(1, &textureID);

    int width, height, numComponents;
    unsigned char* data = stbi_load(path, &width, &height, &numComponents, 0);
    if (data) {
        GLenum format;
        if (numComponents == 1)
            format = GL_RED;
        else if (numComponents == 3)
            format = GL_RGB;
        else if (numComponents == 4)
            format = GL_RGBA;

        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, data);
        glGenerateMipmap(GL_TEXTURE_2D);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        stbi_image_free(data);
    } else {
        std::cout << "Texture failed to load at path: " << path << std::endl;
        stbi_image_free(data);
    }

    return textureID;
}

// Main OpenGL Function
int doOpenGLStuff() {
    /* Initialize GLFW */
    glfwInit();
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    /* Create window */
    GLFWwindow* window = glfwCreateWindow(800, 600, "RPG Project", NULL, NULL);
    if (window == NULL) {
        std::cout << "Failed to create GLFW window" << std::endl;
        glfwTerminate();
        return -1;
    }
    glfwMakeContextCurrent(window);

    // Capture cursor
    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    glfwSetCursorPosCallback(window, handleMouseMovement);
    glfwSetScrollCallback(window, handleMouseScroll);

    /* Initialize GLAD before any GL calls */
    if (!gladLoadGLLoader((GLADloadproc) glfwGetProcAddress)) {
        std::cout << "Failed to initialize GLAD" << std::endl;
        glfwTerminate();
        return -1;
    }

    // Set viewport and resize callback
    glViewport(0, 0, screenWidth, screenHeight);
    glEnable(GL_DEPTH_TEST);
    glfwSetFramebufferSizeCallback(window, frameBufferSize_Callback);

    // Create shader
    Shader shaderProgram("res/shaders/VertexShader.vert", "res/shaders/FragmentShader.frag");

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
    unsigned int textures[2];
    stbi_set_flip_vertically_on_load(true);
    glActiveTexture(GL_TEXTURE0);
    textures[0] = loadTexture("res/img/container.jpg");
    glActiveTexture(GL_TEXTURE1);
    textures[1] = loadTexture("res/img/awesomeface.png");

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
    while (!glfwWindowShouldClose(window)) {

        // Process input
        processInput(window);

        // Clear screen
        glClearColor(0.2, 0.3, 0.3, 1.0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Wireframe mode
        glPolygonMode(GL_FRONT_AND_BACK, useWireframe ? GL_LINE : GL_FILL);

        // Render
        float time = glfwGetTime();
        float radius = 10;
        glm::mat4 view = camera.getViewMatrix();

        glm::mat4 projection = camera.getPerspectiveProjectionMatrix((float) screenWidth / screenHeight, 0.1f, 100.0f);
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
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    // Free resources
    glDeleteVertexArrays(1, &VAO);
    glDeleteBuffers(1, &EBO);
    glDeleteBuffers(1, &VBO);

    glfwTerminate();
    return 0;
}

int main(int argc, char** argv) {
    doOpenGLStuff();

    /*
    DialogueManager::parse("res/dialogue/Dialogue_Test.dlog");
    DialogueController* dc = DialogueManager::get("introNpc");
    const std::unordered_map<std::string, bool> gameFlags = {
        { "worldSaved", true },
        { "dogMissing", false }
    };
    dc->chooseDialogue(gameFlags);
    */

    return 0;
}