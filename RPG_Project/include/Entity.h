#pragma once

#include <glm/glm.hpp>

#include "Constants.h"

struct Transform {
    glm::vec2 position;
    glm::vec2 scale;

    bool operator==(const Transform& other);
    bool operator!=(const Transform& other);
};

class Entity {

public:
    Entity(const char* name, glm::vec2 position = { 0.0f, 0.0f }, glm::vec2 scale = { 1.0f, 1.0f });
    virtual void Update(double deltaTime);

    const Transform& GetTransform();
    virtual void SetTransform(Transform newTransform);

protected:
    Transform transform;
    const char* name;

};

struct Sprite;

class VisibleEntity : public Entity {
public:
    VisibleEntity(const char* name, glm::vec2 position = { 0.0f, 0.0f }, glm::vec2 scale = { 1.0f, 1.0f }, Sprite* sprite = nullptr, glm::vec3 color = { 1.0f, 1.0f, 1.0f });
    virtual void Update(double deltaTime);
    virtual void SetTransform(Transform newTransform);

    ETexture GetTexture() const;

    Sprite* GetSprite() const;
    void SetSprite(Sprite* sprite);

    glm::vec3 GetColor() const;
    void SetColor(glm::vec3 color);

    bool IsDirty() const;
    void SetIsDirty(bool value);

protected:
    Sprite* sprite;
    glm::vec3 color;
    bool isDirty = true;
};

