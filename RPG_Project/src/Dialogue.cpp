#include "../include/Dialogue.h"
#include "../include/Util.h"
#include <iostream>
#include <fstream>
#include <vector>

DialogueController* DialogueManager::get(const std::string& name) {
    auto match = dialogueMap.find(name);
    if (match == dialogueMap.end())
        return nullptr;
    return &match->second;
}

DialogueController* DialogueManager::getOrCreate(const std::string& name) {
    auto match = dialogueMap.find(name);
    if (match == dialogueMap.end())
        dialogueMap[name] = DialogueController(name);
    return &dialogueMap[name];
}

std::unordered_map<std::string, DialogueController> DialogueManager::dialogueMap;

bool DialogueManager::parse(std::string fileName) {

    std::ifstream file(fileName);
    if (!file) {
        std::cerr << "Could not open file " << fileName << std::endl;
        return false;
    }

    std::string line;
    while (file) {
        std::getline(file, line);
        std::vector<std::string> words = splitString(line, " \t\n\r");
        removeCommentedEntries(words);

        if (words.empty()) continue;

        // TODO: first pass to get all names?

        switch (words[0][0]) {
            case '@':
            {
     // Flag references
                if (words[0].length() == 1) {
                    std::cerr << "No name provided after @ symbol in dialogue file " << fileName << "; Could not parse" << std::endl;
                    return false;
                }
                std::string name = words[0].substr(1);

                DialogueController* controller = DialogueManager::getOrCreate(name);
                if (!controller->entityNameRefs.empty()) {
                    std::cerr << "Redefinition of flagRefs for " << name << " in " << fileName << "; Could not parse" << std::endl;
                    return false;
                }
                for (int i = 1; i < words.size(); i++) {
                    controller->addFlagRef(words[i]);
                }
                break;
            }
            case '&':
            {
     // Entity references
                if (words[0].length() == 1) {
                    std::cerr << "No name provided after & symbol in dialogue file " << fileName << "; Could not parse" << std::endl;
                    return false;
                }
                std::string name = words[0].substr(1);

                DialogueController* controller = DialogueManager::getOrCreate(name);
                if (!controller->entityNameRefs.empty()) {
                    std::cerr << "Redefinition of entityRefs for " << name << " in " << fileName << "; Could not parse" << std::endl;
                    return false;
                }
                for (int i = 1; i < words.size(); i++) {
                    controller->addEntityRef(words[i]);
                }
                break;
            }
            case '#':
            {
     // Dialogue definition
                int underscoreIndex = words[0].find_last_of('_');
                if (underscoreIndex == std::string::npos || underscoreIndex == words[0].length() - 1) {
                    std::cerr << "No _# after state name in dialogue file " << fileName << "; Could not parse" << std::endl;
                    return false;
                }
                int seqNumber = std::stoi(line.substr(underscoreIndex + 1));
                std::string name = words[0].substr(1, words[0].find_first_of('_') - 1);
                DialogueController* controller = DialogueManager::getOrCreate(name);

                // TODO: should I make separate controller for each sequence of states?
                // Keep parsing dialogue state

                break;
            }
            default:
                std::cout << line << std::endl;
                break;
        }

    }

    int i = 0;
    for (auto it = dialogueMap.begin(); it != dialogueMap.end(); it++) {
        i++;
    }

    return true;
}

DialogueController::DialogueController() {}

DialogueController::DialogueController(std::string name)
    : name(name) {}

DialogueState* DialogueController::chooseDialogue(const std::unordered_map<std::string, bool>& gameFlags) {
    for (auto& flag : flagNameRefs) {
        auto result = gameFlags.find(flag);
        if (result == gameFlags.end()) continue;
        if (result->second) {
            // Grab state
            StateContainer container = states[name + "_" + flag];
            return &container.vStates[container.currentState];
        }
    }
    // Otherwise Grab Default
    StateContainer main = states[name];
    return &main.vStates[main.currentState];
}

DialogueMessage::DialogueMessage(std::string text)
    : text(text) {}

void DialogueController::addFlagRef(std::string& name) {
    flagNameRefs.emplace_back(name);
}

void DialogueController::addEntityRef(std::string& name) {
    entityNameRefs.emplace_back(name);
}
