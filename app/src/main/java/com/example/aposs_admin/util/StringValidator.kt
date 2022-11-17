package com.example.aposs_admin.util

import java.util.regex.Pattern

object StringValidator {
    fun isStringContainNumberOrSpecialCharacter(name: String): Boolean {
        val hasNumber: Boolean = Pattern.compile(
            "[0-9]"
        ).matcher(name).find()
        val hasSpecialCharacter: Boolean = Pattern.compile(
            "[!@#$%&.,\"':;?*()_+=|<>{}\\[\\]~-]"
        ).matcher(name).find()
        return hasNumber || hasSpecialCharacter
    }

    fun isStringContainSpecialCharacter(name: String): Boolean{
        return Pattern.compile(
            "[!@#$%&.,\"':;?*()_+=|<>{}\\[\\]~-]"
        ).matcher(name).find()
    }


    fun isNumberOnly(str: String): Boolean {
        if(str.toLongOrNull() == null){
            return false
        }
        return true
    }

}