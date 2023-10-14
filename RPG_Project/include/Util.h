#pragma once

#include <string>
#include <vector>

/// <summary>
/// Returns a vector of strings split from the given line by the given separator
/// </summary>
/// <param name="line"></param>
/// <param name="separator"></param>
/// <returns></returns>
std::vector<std::string> splitString(const std::string& line, std::string separators);

/// <summary>
/// Removes tokens that occur after a comment (//) from the specified vector
/// </summary>
/// <param name="tokens"></param>
void removeCommentedEntries(std::vector<std::string>& tokens);

/// <summary>
/// Returns the index of the last occurrence of the specified character in the specified string in the specified range.
/// </summary>
/// <param name="c"></param>
/// <param name="line"></param>
/// <param name="startIndex"></param>
/// <param name="endIndex"></param>
/// <returns></returns>
int findLastIndexOf(char c, const std::string& line, int startIndex = 0, int endIndex = std::string::npos);




