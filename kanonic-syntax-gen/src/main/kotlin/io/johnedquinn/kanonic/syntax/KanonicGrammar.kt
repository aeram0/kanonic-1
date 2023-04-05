package io.johnedquinn.kanonic.syntax

import io.johnedquinn.kanonic.dsl.GrammarBuilder.Companion.buildGrammar
import io.johnedquinn.kanonic.dsl.RuleBuilder.Companion.buildGeneratedRule
import io.johnedquinn.kanonic.dsl.RuleBuilder.Companion.buildRule

// TODO: Make internal once grammar is serialized
public object KanonicGrammar {

    // TOKEN NAMES
    private const val IDENT_UPPER_CASE = "IDENT_UPPER_CASE"
    private const val IDENT_CAMEL_CASE = "IDENT_CAMEL_CASE"
    private const val EPSILON = "EPSILON"
    private const val COLON = "COLON"
    private const val COLON_SEMI = "COLON_SEMI"
    private const val CURLY_BRACE_LEFT = "CURLY_BRACE_LEFT"
    private const val CURLY_BRACE_RIGHT = "CURLY_BRACE_RIGHT"
    private const val LINE_VERTICAL = "LINE_VERTICAL"
    private const val LITERAL_STRING = "LITERAL_STRING"
    private const val DASH = "DASH"
    private const val CARROT_RIGHT = "CARROT_RIGHT"
    private const val COMMENT_SINGLE = "COMMENT_SINGLE"
    private const val COMMENT_BLOCK = "COMMENT_BLOCK"

    // RULE NAMES
    private const val file = "file"
    private const val config = "config"
    private const val configDef = "configDef"
    private const val tokenDef = "tokenDef"
    private const val ruleDef = "ruleDef"
    private const val ruleItem = "ruleItem"
    private const val ruleVariant = "ruleVariant"

    // GENERATED RULE NAMES
    private const val configDefs = "_0"
    private const val expressions = "_1"
    private const val ruleItems = "_2"
    private const val ruleVariants = "_3"

    public val grammar = buildGrammar("Kanonic", "file") {
        packageName("io.johnedquinn.kanonic.syntax.generated")
        tokens {
            IDENT_UPPER_CASE - "[A-Z][A-Z_]*"
            IDENT_CAMEL_CASE - "[a-z][a-zA-Z]*"
            COLON - ":"
            COLON_SEMI - ";"
            CURLY_BRACE_LEFT - "\\{"
            CURLY_BRACE_RIGHT - "\\}"
            LINE_VERTICAL - "\\|"
            LITERAL_STRING - "\"((\\\")|[^\"])*\""
            DASH - "-"
            CARROT_RIGHT - ">"
            COMMENT_SINGLE - ("//[^\\r\\n]*?\\r?\\n?" channel "hidden")
            COMMENT_BLOCK - ("/\\*(?s).*?\\*/" channel "hidden")
        }

        // TOP RULE
        file eq buildRule(this, file) {
            "Root" eq config - expressions
        }

        // config
        //   : IDENT_CAMEL_CASE ":" "{" configDef* "}" ";"
        //   ;
        config eq buildRule(this, config) {
            "ConfigStruct" eq IDENT_CAMEL_CASE - COLON - CURLY_BRACE_LEFT - configDefs - CURLY_BRACE_RIGHT - COLON_SEMI
        }

        // configDef
        //  : IDENT_CAMEL_CASE COLON IDENT_CAMEL_CASE COLON_SEMI
        //  ;
        configDef eq buildRule(this, configDef) {
            "ConfigDefinition" eq IDENT_CAMEL_CASE - COLON - IDENT_CAMEL_CASE - COLON_SEMI
        }

        // token
        //  : IDENT_UPPER_CASE COLON LITERAL_STRING COLON_SEMI --> tokenDef
        //  ;
        tokenDef eq buildRule(this, tokenDef) {
            "Token" eq IDENT_UPPER_CASE - COLON - LITERAL_STRING - COLON_SEMI
        }

        // rule
        //   : IDENT_CAMEL_CASE COLON variant (LINE_VERTICAL variant)* COLON_SEMI --> Rule
        //   ;
        ruleDef eq buildRule(this, ruleDef) {
            "Rule" eq IDENT_CAMEL_CASE - COLON - ruleVariants - COLON_SEMI
        }

        // variant
        //   : item+ "-->" IDENT_CAMEL_CASE --> Variant
        //   ;
        ruleVariant eq buildRule(this, ruleVariant) {
            "Variant" eq ruleItems - DASH - DASH - CARROT_RIGHT - IDENT_CAMEL_CASE
        }

        // item
        //   : IDENT_CAMEL_CASE --> RuleReference
        //   | IDENT_UPPER_CASE --> TokenReference
        //   ;
        ruleItem eq buildRule(this, ruleItem) {
            "RuleReference" eq IDENT_CAMEL_CASE
            "TokenReference" eq IDENT_UPPER_CASE
            "LineReference" eq LINE_VERTICAL // TODO
        }

        //
        //
        // GENERATED
        //
        //

        configDefs eq buildGeneratedRule(this, configDefs) {
            "EmptyConfigDefinition" eq EPSILON
            "MultipleConfigDefinitions" eq configDefs - configDef
        }

        // EXPRESSIONS
        expressions eq buildGeneratedRule(this, expressions) {
            "EmptyExpressions" eq EPSILON
            "TokenAdded" eq expressions - tokenDef
            "RuleAdded" eq expressions - ruleDef
        }

        // VARIANTS
        ruleVariants eq buildGeneratedRule(this, ruleVariants) {
            "SingleVariant" eq ruleVariant
            "MultipleVariants" eq ruleVariants - ruleVariant
        }

        // ITEMS
        ruleItems eq buildGeneratedRule(this, ruleItems) {
            "SingleRule" eq ruleItem
            "MultipleRules" eq ruleItems - ruleItem
        }
    }
}
