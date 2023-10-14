#pragma once

#include <string>
#include <vector>
#include <unordered_map>

class DialogueState;

class DialogueController {

public:
    DialogueController();
    DialogueController(std::string name);
    void addFlagRef(std::string& name);
    void addEntityRef(std::string& name);

    DialogueState* chooseDialogue(const std::unordered_map<std::string, bool>& gameFlags);

    // Choose dialog? returns DialogueState

    friend class DialogueManager;
    friend class DialogueState;

private:
    std::string name;
    std::vector<std::string> flagNameRefs;
    std::vector<std::string> entityNameRefs;

    struct StateContainer {
        std::vector<DialogueState> vStates;
        int currentState;
    };
    std::unordered_map<std::string, StateContainer> states;
    
}; // class DialogueController

class DialogueMessage {
public:
    DialogueMessage(std::string text);
private:
    std::string text;
};

class DialogueState {
private:
    DialogueController* controller;
    std::vector<DialogueState*> successors;
    std::vector<DialogueMessage> messages;
}; // class DialogueState

class DialogueManager {

private:
    static std::unordered_map<std::string, DialogueController> dialogueMap;
    static DialogueController* getOrCreate(const std::string& name);

public:
    static DialogueController* get(const std::string& name);
    static bool parse(std::string fileName);

}; // class DialogueManager


