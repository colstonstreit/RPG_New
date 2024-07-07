#pragma once

#include <glm/glm.hpp>

const double CAM_DEFAULT_YAW = -90.0;
const double CAM_DEFAULT_PITCH = 0.0;
const double CAM_DEFAULT_SPEED = 2.5;
const double CAM_DEFAULT_SENSITIVITY = 0.1;
const double CAM_DEFAULT_FOV = 45.0;

const double CAM_MAX_PITCH = 89.999;
const double CAM_MIN_FOV = 1.0;
const double CAM_MAX_FOV = 90.0;

enum class CameraDirection {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    UP,
    DOWN
};

class Camera3D {

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
    Camera3D(glm::vec3 position = glm::vec3(0, 0, 0), glm::vec3 up = glm::vec3(0, 1, 0), double yaw = CAM_DEFAULT_YAW, double pitch = CAM_DEFAULT_PITCH);

    glm::mat4 GetViewMatrix() const;
    glm::mat4 GetPerspectiveProjectionMatrix(double aspectRatio, double zNear, double zFar);

    void ProcessKeyboardInput(CameraDirection direction, double deltaTime);
    void ProcessMouseMovement(double xOffset, double yOffset);
    void ProcessMouseScroll(double yOffset);

private:
    void updateCameraVectors();

};

