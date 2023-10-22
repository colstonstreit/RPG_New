#include "Util.h"

std::vector<std::string> splitString(const std::string& line, std::string separators) {
    std::vector<std::string> list;
    int prevStart = 0;
    for (int i = 0; i < line.length(); i++) {
        for (int j = 0; j < separators.length(); j++) {
            if (line[i] == separators[j]) {
                if (i == prevStart) {
                    prevStart++;
                } else {
                    list.push_back(line.substr(prevStart, i - prevStart));
                    prevStart = i + 1;
                }
                break;
            }
        }
    }

    if (prevStart < line.length()) {
        std::string sub = line.substr(prevStart, line.length() - prevStart);
        if (!sub.empty()) {
            list.push_back(sub);
        }
    }
    return list;
}

void removeCommentedEntries(std::vector<std::string>& tokens) {

    bool needToErase = false;
    int startEraseIndex = -1;
    for (int j = 0; j < tokens.size(); j++) {
        const std::string& string = tokens[j];
        for (int i = 0; i < string.length() - 1; i++) {
            if (string[i] == '/' && string[i + 1] == '/') {
                tokens[j] = string.substr(0, i);
                needToErase = true;
                startEraseIndex = j + 1;
                if (i == 0) startEraseIndex--;
                break;
            }
        }
        if (needToErase) break;
    }
    if (startEraseIndex < tokens.size()) {
        tokens.erase(tokens.begin() + startEraseIndex, tokens.end());
    }
}

int findLastIndexOf(char c, const std::string& line, int startIndex, int endIndex) {
    if (endIndex >= line.length())
        endIndex = line.length() - 1;
    for (int i = endIndex; i >= startIndex; i--) {
        if (line[i] == c)
            return i;
    }
    return -1;
}
