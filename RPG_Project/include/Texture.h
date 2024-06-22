#pragma once

class Texture {

public:
    Texture(const char* path);
    Texture(Texture& other) = delete;
    void operator=(const Texture&) = delete;

    void Bind() const;

    unsigned int GetID() const;
    unsigned int GetWidth() const;
    unsigned int GetHeight() const;

private:
    unsigned int id;
    unsigned int width;
    unsigned int height;
};

