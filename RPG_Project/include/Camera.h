#pragma once

#include <glm/glm.hpp>

const float CAM_DEFAULT_YAW = -90.0f;
const float CAM_DEFAULT_PITCH = 0.0f;
const float CAM_DEFAULT_SPEED = 2.5f;
const float CAM_DEFAULT_SENSITIVITY = 0.1f;
const float CAM_DEFAULT_FOV = 45.0f;

const float CAM_MAX_PITCH = 89.999f;
const float CAM_MIN_FOV = 1.0f;
const float CAM_MAX_FOV = 90.0f;

enum class CameraDirection {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    UP,
    DOWN
};

class Camera {
public:
    glm::vec3 position;
    float movementSpeed;
    float mouseSensitivity;
    float fov;

private:
    glm::vec3 front;
    glm::vec3 up;
    glm::vec3 right;
    glm::vec3 worldUp;

    float yaw;
    float pitch;

public:
    Camera(glm::vec3 position = glm::vec3(0, 0, 0), glm::vec3 up = glm::vec3(0, 1, 0), float yaw = CAM_DEFAULT_YAW, float pitch = CAM_DEFAULT_PITCH);

    glm::mat4 getViewMatrix();
    glm::mat4 getPerspectiveProjectionMatrix(float aspectRatio, float zNear, float zFar);

    void processKeyboardInput(CameraDirection direction, float deltaTime);
    void processMouseMovement(float xOffset, float yOffset);
    void processMouseScroll(float yOffset);

private:
    void updateCameraVectors();

};

