#pragma once

#include <glm/glm.hpp>

class Shader {

public:
    unsigned int id;

    Shader(const char* vertexShaderPath, const char* fragmentPath);
    ~Shader();
    Shader(Shader& other) = delete;
    void operator=(const Shader&) = delete;

    void use() const;

    void setUniformBool(const char* name, bool value) const;
    void setUniformInt(const char* name, int value) const;
    void setUniformUInt(const char* name, unsigned int value) const;
    void setUniformFloat(const char* name, float value) const;
    void setUniformVec2(const char* name, const glm::vec2& value) const;
    void setUniformVec2(const char* name, float x, float y) const;
    void setUniformVec3(const char* name, const glm::vec3& value) const;
    void setUniformVec3(const char* name, float x, float y, float z) const;
    void setUniformVec4(const char* name, const glm::vec4& value) const;
    void setUniformVec4(const char* name, float x, float y, float z, float w) const;
    void setUniformMat2(const char* name, const glm::mat2& matrix) const;
    void setUniformMat3(const char* name, const glm::mat3& matrix) const;
    void setUniformMat4(const char* name, const glm::mat4& matrix) const;
};