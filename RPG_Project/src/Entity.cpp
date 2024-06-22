#include "Entity.h"

#include <stdexcept>

#include "Spritesheet.h"

Entity::Entity(const char* name, glm::vec2 position, glm::vec2 scale) : name(name), transform({ position, scale }) {}

void Entity::Update(double deltaTime) {}

const Transform& Entity::GetTransform() {
    return this->transform;
}

void Entity::SetTransform(Transform newTransform) {
    this->transform = newTransform;
}

VisibleEntity::VisibleEntity(const char* name, glm::vec2 position, glm::vec2 scale, Sprite* sprite, glm::vec3 color) : Entity(name, position, scale), sprite(sprite), color(color) {}

void VisibleEntity::Update(double deltaTime) {}

void VisibleEntity::SetTransform(Transform newTransform) {
    if (this->transform != newTransform) {
        this->transform = newTransform;
        this->isDirty = true;
    }
}

Sprite* VisibleEntity::GetSprite() const {
    return this->sprite;
}

void VisibleEntity::SetSprite(Sprite* sprite) {
    if (sprite == nullptr)
        throw std::invalid_argument("Cannot replace VisibleEntity.sprite with nullptr!");
    if (this->GetTexture() != sprite->texture)
        throw std::invalid_argument("Can only modify VisibleEntity.sprite if new sprite comes from the same texture.");

    this->sprite = sprite;
    this->isDirty = true;
}

ETexture VisibleEntity::GetTexture() const {
    Sprite* sprite = this->GetSprite();
    if (sprite == nullptr)
        return ETexture::NUM_TEXTURES;
    return sprite->texture;
}

glm::vec3 VisibleEntity::GetColor() const {
    return this->color;
}

void VisibleEntity::SetColor(glm::vec3 color) {
    if (this->color != color) {
        this->color = color;
        this->isDirty = true;
    }
}

bool VisibleEntity::IsDirty() const {
    return this->isDirty;
}

void VisibleEntity::SetIsDirty(bool value) {
    this->isDirty = value;
}

bool Transform::operator==(const Transform& other) {
    return position.x == other.position.x && position.y == other.position.y && scale.x == other.scale.x && scale.y == other.scale.y;
}

bool Transform::operator!=(const Transform& other) {
    return !(*this == other);
}
