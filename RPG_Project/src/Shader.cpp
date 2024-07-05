#include "Shader.h"

#include <glad/glad.h>

#include <fstream>
#include <iostream>
#include <sstream>
#include <string>

unsigned int Shader::s_boundShaderID = 0;

Shader::Shader(const char* vertexShaderPath, const char* fragmentPath) {

    // Get the source code from filepaths
    std::string vertexCode;
    std::string fragmentCode;
    std::ifstream vShaderFile;
    std::ifstream fShaderFile;

    // Ensure ifstream objects can throw exceptions
    vShaderFile.exceptions(std::ifstream::failbit | std::ifstream::badbit);
    fShaderFile.exceptions(std::ifstream::failbit | std::ifstream::badbit);

    try {
        // Open files
        vShaderFile.open(vertexShaderPath);
        fShaderFile.open(fragmentPath);
        std::stringstream vShaderStream, fShaderStream;

        // Read file's buffer contents into streams
        vShaderStream << vShaderFile.rdbuf();
        fShaderStream << fShaderFile.rdbuf();

        // Close file handlers
        vShaderFile.close();
        fShaderFile.close();

        // Convert stream into string
        vertexCode = vShaderStream.str();
        fragmentCode = fShaderStream.str();
    } catch (std::ifstream::failure e) {
        std::cout << "ERROR::SHADER::FILE_NOT_SUCCESFULLY_READ" << std::endl;
    }

    const char* vertexShaderCode = vertexCode.c_str();
    const char* fragmentShaderCode = fragmentCode.c_str();

    // Vertex Shader
    unsigned int vertexShader = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertexShader, 1, &vertexShaderCode, NULL);
    glCompileShader(vertexShader);
    int success;
    char infoLog[512];
    glGetShaderiv(vertexShader, GL_COMPILE_STATUS, &success);
    if (!success) {
        glGetShaderInfoLog(vertexShader, 512, NULL, infoLog);
        std::cout << "Error: vertex shader did not compile.\n" << infoLog << std::endl;
    }

    // Fragment Shader
    unsigned int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragmentShader, 1, &fragmentShaderCode, NULL);
    glCompileShader(fragmentShader);
    glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, &success);
    if (!success) {
        glGetShaderInfoLog(fragmentShader, 512, NULL, infoLog);
        std::cout << "Error: fragment shader did not compile.\n" << infoLog << std::endl;
    }

    // Generate shader program
    this->id = glCreateProgram();
    glAttachShader(this->id, vertexShader);
    glAttachShader(this->id, fragmentShader);
    glLinkProgram(this->id);
    glGetProgramiv(this->id, GL_LINK_STATUS, &success);
    if (!success) {
        glGetProgramInfoLog(this->id, 512, NULL, infoLog);
        std::cout << "Error: shaders failed to link.\n" << infoLog << std::endl;
    }

    // Free unnecessary memory
    glDeleteShader(vertexShader);
    glDeleteShader(fragmentShader);
}

Shader::~Shader() {
    glDeleteProgram(this->id);
}

void Shader::Use() const {
    if (this->id != Shader::s_boundShaderID) {
        glUseProgram(this->id);
        Shader::s_boundShaderID = this->id;
    }
}

void Shader::SetUniformBool(const char* name, bool value) const {
    int uniformLocation = glGetUniformLocation(this->id, name);
    glUniform1i(uniformLocation, (int) value);
}

void Shader::SetUniformInt(const char* name, int value) const {
    int uniformLocation = glGetUniformLocation(this->id, name);
    glUniform1i(uniformLocation, value);
}

void Shader::SetUniformIntArray(const char* name, int* value, unsigned int count) const {
    int uniformLocation = glGetUniformLocation(this->id, name);
    glUniform1iv(uniformLocation, count, value);
}

void Shader::SetUniformUInt(const char* name, unsigned int value) const {
    int uniformLocation = glGetUniformLocation(this->id, name);
    glUniform1ui(uniformLocation, value);
}

void Shader::SetUniformFloat(const char* name, float value) const {
    int uniformLocation = glGetUniformLocation(this->id, name);
    glUniform1f(uniformLocation, value);
}

void Shader::SetUniformVec2(const char* name, const glm::vec2& value) const {
    int uniformLocation = glGetUniformLocation(this->id, name);
    glUniform2fv(uniformLocation, 1, &value[0]);
}

void Shader::SetUniformVec2(const char* name, float x, float y) const {
    int uniformLocation = glGetUniformLocation(this->id, name);
    glUniform2f(uniformLocation, x, y);
}

void Shader::SetUniformVec3(const char* name, const glm::vec3& value) const {
    int uniformLocation = glGetUniformLocation(this->id, name);
    glUniform3fv(uniformLocation, 1, &value[0]);
}

void Shader::SetUniformVec3(const char* name, float x, float y, float z) const {
    int uniformLocation = glGetUniformLocation(this->id, name);
    glUniform3f(uniformLocation, x, y, z);
}

void Shader::SetUniformVec4(const char* name, const glm::vec4& value) const {
    int uniformLocation = glGetUniformLocation(this->id, name);
    glUniform4fv(uniformLocation, 1, &value[0]);
}

void Shader::SetUniformVec4(const char* name, float x, float y, float z, float w) const {
    int uniformLocation = glGetUniformLocation(this->id, name);
    glUniform4f(uniformLocation, x, y, z, w);
}

void Shader::SetUniformMat2(const char* name, const glm::mat2& mat) const {
    int uniformLocation = glGetUniformLocation(this->id, name);
    glUniformMatrix2fv(uniformLocation, 1, GL_FALSE, &mat[0][0]);
}

void Shader::SetUniformMat3(const char* name, const glm::mat3& mat) const {
    int uniformLocation = glGetUniformLocation(this->id, name);
    glUniformMatrix3fv(uniformLocation, 1, GL_FALSE, &mat[0][0]);
}

void Shader::SetUniformMat4(const char* name, const glm::mat4& mat) const {
    int uniformLocation = glGetUniformLocation(this->id, name);
    glUniformMatrix4fv(uniformLocation, 1, GL_FALSE, &mat[0][0]);
}


