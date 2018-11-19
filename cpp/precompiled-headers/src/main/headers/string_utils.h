/*
 * A library of string utility functions.
 */

#pragma once

#include <string>

#include "linked_list.h"

#define UTILITIES_API

/*
 * Splits the given string into a list of tokens. Tokens are separated by one or more whitespace characters.
 */
UTILITIES_API linked_list split(const std::string & source);

/*
 * Joins the list of tokens into a string, separated by space characters.
 */
UTILITIES_API std::string join(const linked_list & source);
