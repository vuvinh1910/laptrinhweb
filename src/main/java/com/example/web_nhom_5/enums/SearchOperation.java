package com.example.web_nhom_5.enums;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public enum SearchOperation {
    EQUALITY,NEGATION,GREATER_THAN,LESS_THAN,LIKE,STARTS_WITH,ENDS_WITH,CONTAINS;
    public static final String[] SIMPLE_OPERATION_SET={":","!",">","<","~"};
    @Contract(pure = true)
    public static @Nullable SearchOperation getSimpleOperation(char input)
    {
        return switch (input) {
            case ':' -> EQUALITY;
            case '!' -> NEGATION;
            case '>' -> GREATER_THAN;
            case '<' -> LESS_THAN;
            case '~' -> LIKE;
            default -> null;
        };
    }
}
