#pragma once

class Texture {

public:
    Texture(const char* path);
    Texture(Texture& other) = delete;
    void operator=(const Texture&) = delete;

    void bind() const;

    unsigned int getID() const;
    unsigned int getWidth() const;
    unsigned int getHeight() const;

private:
    unsigned int id;
    unsigned int width;
    unsigned int height;

};

