package com.github.mydogtom.ktlint.rules

import com.github.mydogtom.ktlint.custom.rules.ClassAndFunctionHeaderFormatRule
import com.github.shyiko.ktlint.core.RuleSet
import com.github.shyiko.ktlint.core.RuleSetProvider

class CustomRuleSetProvider : RuleSetProvider {

    override fun get(): RuleSet = RuleSet("custom",
        ClassAndFunctionHeaderFormatRule()
    )
}
