#pragma once

#include <glm/glm.hpp>

class Shader {

public:
    Shader(const char* vertexShaderPath, const char* fragmentPath);
    ~Shader();
    Shader(Shader& other) = delete;
    void operator=(const Shader&) = delete;

    void Use() const;

    void SetUniformBool(const char* name, bool value) const;
    void SetUniformInt(const char* name, int value) const;
    void SetUniformIntArray(const char* name, int* value, unsigned int count) const;
    void SetUniformUInt(const char* name, unsigned int value) const;
    void SetUniformFloat(const char* name, float value) const;
    void SetUniformVec2(const char* name, const glm::vec2& value) const;
    void SetUniformVec2(const char* name, float x, float y) const;
    void SetUniformVec3(const char* name, const glm::vec3& value) const;
    void SetUniformVec3(const char* name, float x, float y, float z) const;
    void SetUniformVec4(const char* name, const glm::vec4& value) const;
    void SetUniformVec4(const char* name, float x, float y, float z, float w) const;
    void SetUniformMat2(const char* name, const glm::mat2& matrix) const;
    void SetUniformMat3(const char* name, const glm::mat3& matrix) const;
    void SetUniformMat4(const char* name, const glm::mat4& matrix) const;

private:
    unsigned int id;
};