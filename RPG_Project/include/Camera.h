#pragma once

#include <glm/glm.hpp>

const double CAM_DEFAULT_YAW = -90.0f;
const double CAM_DEFAULT_PITCH = 0.0f;
const double CAM_DEFAULT_SPEED = 2.5f;
const double CAM_DEFAULT_SENSITIVITY = 0.1f;
const double CAM_DEFAULT_FOV = 45.0f;

const double CAM_MAX_PITCH = 89.999f;
const double CAM_MIN_FOV = 1.0f;
const double CAM_MAX_FOV = 90.0f;

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
    double movementSpeed;
    double mouseSensitivity;
    double fov;

private:
    glm::vec3 front;
    glm::vec3 up;
    glm::vec3 right;
    glm::vec3 worldUp;

    double yaw;
    double pitch;

public:
    Camera(glm::vec3 position = glm::vec3(0, 0, 0), glm::vec3 up = glm::vec3(0, 1, 0), double yaw = CAM_DEFAULT_YAW, double pitch = CAM_DEFAULT_PITCH);

    glm::mat4 getViewMatrix();
    glm::mat4 getPerspectiveProjectionMatrix(double aspectRatio, double zNear, double zFar);

    void processKeyboardInput(CameraDirection direction, double deltaTime);
    void processMouseMovement(double xOffset, double yOffset);
    void processMouseScroll(double yOffset);

private:
    void updateCameraVectors();

};

