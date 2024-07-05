#include "Renderer.h"

#include <unordered_map>

#include <glad/glad.h>
#include <GLFW/glfw3.h>

#include "Entity.h"
#include "Game.h"
#include "ResourceManager.h"
#include "Shader.h"
#include "Spritesheet.h"
#include "Texture.h"

QuadBatch::QuadBatch(unsigned int maxQuads) : maxQuads(maxQuads) {

    // Generate VAO
    glGenVertexArrays(1, &this->VAO);
    glBindVertexArray(this->VAO);

    // Create initial vertex data
    this->vertices = new float[maxQuads * 4 * sizeof(Vertex) / sizeof(float)];
    memset(this->vertices, 0, maxQuads * 4 * sizeof(Vertex));
    glGenBuffers(1, &this->VBO);
    glBindBuffer(GL_ARRAY_BUFFER, this->VBO);
    glBufferData(GL_ARRAY_BUFFER, maxQuads * 4 * sizeof(Vertex), this->vertices, GL_DYNAMIC_DRAW);

    // Construct indices
    this->indices = new unsigned int[maxQuads * 6];
    unsigned int indicesIndex = 0;
    for (unsigned int i = 0; i < maxQuads; i++) {
        unsigned int tileBase = 4 * i;
        this->indices[indicesIndex++] = tileBase;
        this->indices[indicesIndex++] = tileBase + 1;
        this->indices[indicesIndex++] = tileBase + 2;

        this->indices[indicesIndex++] = tileBase + 2;
        this->indices[indicesIndex++] = tileBase + 1;
        this->indices[indicesIndex++] = tileBase + 3;
    }
    glGenBuffers(1, &this->EBO);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this->EBO);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, maxQuads * 6 * sizeof(unsigned int), indices, GL_STATIC_DRAW);
    delete[] indices;

    // Build out attributes
    glVertexAttribPointer(0, 2, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void*) 0);
    glEnableVertexAttribArray(0);

    glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void*) (2 * sizeof(float)));
    glEnableVertexAttribArray(1);

    glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void*) (5 * sizeof(float)));
    glEnableVertexAttribArray(2);

    glVertexAttribPointer(3, 1, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void*) (7 * sizeof(float)));
    glEnableVertexAttribArray(3);
}

QuadBatch::QuadBatch(QuadBatch&& other) noexcept : maxQuads(other.maxQuads), maxTextures(other.maxTextures) {
    // Move resources over to this object
    this->VAO = other.VAO;
    this->VBO = other.VBO;
    this->EBO = other.EBO;
    this->vertices = other.vertices;
    this->quads = std::move(other.quads);
    this->usedTextures = std::move(other.usedTextures);

    // Modify other object so it no longer deletes these
    other.vertices = nullptr;
    other.VAO = 0;
    other.VBO = 0;
    other.EBO = 0;
}

QuadBatch::~QuadBatch() {
    // Free resources
    if (this->vertices)
        delete[] this->vertices;
    if (this->VAO != 0)
        glDeleteVertexArrays(1, &this->VAO);
    if (this->EBO != 0)
        glDeleteBuffers(1, &this->EBO);
    if (this->VBO != 0)
        glDeleteBuffers(1, &this->VBO);
}

bool QuadBatch::AddQuad(VisibleEntity* quad) {

    bool quadCanFit = (
        this->quads.size() < this->maxQuads
        && (
            this->usedTextures.size() < this->maxTextures
            || std::find(this->usedTextures.begin(), this->usedTextures.end(), quad->GetSprite()->texture) != this->usedTextures.end()
            || quad->GetTexture() == ETexture::NUM_TEXTURES
            )
        );

    if (!quadCanFit)
        return false;

    this->quads.push_back(quad);

    ETexture texture = quad->GetTexture();
    if (texture != ETexture::NUM_TEXTURES && std::find(this->usedTextures.begin(), this->usedTextures.end(), quad->GetSprite()->texture) == this->usedTextures.end()) {
        this->usedTextures.push_back(texture);
    }

    return true;
}

void QuadBatch::Render(const glm::mat4& projectionMatrix) {

    const ResourceManager& resourceManager = Game::GetResourceManager();

    if (this->isDirty()) {
        this->regenerateVertices();
    }

    const Shader& shaderProgram = resourceManager.GetShader(EShader::QUAD_BATCH);
    shaderProgram.Use();

    shaderProgram.SetUniformMat4("projection", projectionMatrix);

    static int textureSlots[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
    for (unsigned int i = 0; i < this->usedTextures.size(); i++) {
        glActiveTexture(GL_TEXTURE0 + i);
        resourceManager.GetTexture(this->usedTextures[i]).Bind();
    }
    shaderProgram.SetUniformIntArray("uTextures", textureSlots, sizeof(textureSlots) / sizeof(int));

    glBindVertexArray(this->VAO);
    glDrawElements(GL_TRIANGLES, 6 * this->quads.size(), GL_UNSIGNED_INT, 0);
}

void QuadBatch::regenerateVertices() {
    std::unordered_map<ETexture, int> textureToIndexMap;
    unsigned int index = 0;

    for (auto quad : this->quads) {
        // Grab Quad data
        glm::vec3 color = quad->GetColor();
        Sprite* sprite = quad->GetSprite();
        const Transform& transform = quad->GetTransform();
        ETexture texture = sprite->texture;

        // Find texture ID
        int textureID = 0;
        if (texture == ETexture::NUM_TEXTURES) {
            textureID = -1;
        } else if (textureToIndexMap.count(texture) != 0) {
            textureID = textureToIndexMap[texture];
        } else {
            for (int i = 0; i < this->usedTextures.size(); i++) {
                if (this->usedTextures[i] == texture) {
                    textureToIndexMap[texture] = i;
                    textureID = i;
                    break;
                }
            }
        }

        // Top Left vertex
        this->vertices[index++] = transform.position.x;
        this->vertices[index++] = transform.position.y;
        this->vertices[index++] = color.r;
        this->vertices[index++] = color.g;
        this->vertices[index++] = color.b;
        this->vertices[index++] = sprite->topLeftUV.s;
        this->vertices[index++] = sprite->topLeftUV.t;
        this->vertices[index++] = textureID;

        // Top Right vertex
        this->vertices[index++] = transform.position.x + transform.scale.x;
        this->vertices[index++] = transform.position.y;
        this->vertices[index++] = color.r;
        this->vertices[index++] = color.g;
        this->vertices[index++] = color.b;
        this->vertices[index++] = sprite->topRightUV.s;
        this->vertices[index++] = sprite->topRightUV.t;
        this->vertices[index++] = textureID;

        // Bottom Left vertex
        this->vertices[index++] = transform.position.x;
        this->vertices[index++] = transform.position.y + transform.scale.y;
        this->vertices[index++] = color.r;
        this->vertices[index++] = color.g;
        this->vertices[index++] = color.b;
        this->vertices[index++] = sprite->bottomLeftUV.s;
        this->vertices[index++] = sprite->bottomLeftUV.t;
        this->vertices[index++] = textureID;

        // Bottom Right vertex
        this->vertices[index++] = transform.position.x + transform.scale.x;
        this->vertices[index++] = transform.position.y + transform.scale.y;
        this->vertices[index++] = color.r;
        this->vertices[index++] = color.g;
        this->vertices[index++] = color.b;
        this->vertices[index++] = sprite->bottomRightUV.s;
        this->vertices[index++] = sprite->bottomRightUV.t;
        this->vertices[index++] = textureID;

        // Remove dirty flag
        quad->SetIsDirty(false);
    }

    glBindBuffer(GL_ARRAY_BUFFER, this->VBO);
    glBufferSubData(GL_ARRAY_BUFFER, 0, this->quads.size() * 4 * sizeof(Vertex), this->vertices);
}

bool QuadBatch::isDirty() const {
    for (auto& quad : this->quads) {
        if (quad->IsDirty()) {
            return true;
        }
    }
    return false;
}

void Renderer::AddQuad(VisibleEntity* quad) {
    // Try to add it into existing batch
    for (auto& quadBatch : this->quadBatches) {
        if (quadBatch.AddQuad(quad)) {
            return;
        }
    }

    // Otherwise create a new batch.
    this->quadBatches.emplace_back(Renderer::BATCH_SIZE);
    QuadBatch& lastBatch = this->quadBatches.back();
    lastBatch.AddQuad(quad);
}

void Renderer::Render(const glm::mat4& projectionMatrix) {
    for (auto& quadBatch : this->quadBatches) {
        quadBatch.Render(projectionMatrix);
    }
}
