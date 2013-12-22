package ru.fizteh.fivt.students.ichalovaDiana.calculator;

import java.util.HashSet;
import java.math.BigInteger;


public class Calculator {

    static final int RADIX = 19;

    public static void main(String[] args) {
        //args = new String[]{"1/2 + "};
        String usage = "usage: Calculator <arithmetic expression>";
        try {
            if (args.length == 0) {
                throw new Exception(usage);
            }

            StringBuilder concatArgs = new StringBuilder();
            for (String item : args) {
                concatArgs.append(item).append(" ");
            }
            String expression = concatArgs.toString();

            StringParser parser = new StringParser(expression, RADIX);
            BigInteger result = calculateExpression(parser);
            LexemeType currentLexeme = parser.getCurrentLexemeType();
            if (currentLexeme != LexemeType.END) {
                throw new Exception("Unexpected lexeme: " + currentLexeme);
            }
            System.out.println(result.toString(RADIX));
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            System.exit(1);
        }
    }

    private static BigInteger calculateExpression(StringParser parser) throws Exception {
        BigInteger result = calculateItem(parser);
        LexemeType currentLexeme = parser.getCurrentLexemeType();
        while (currentLexeme == LexemeType.PLUS
                || currentLexeme == LexemeType.MINUS) {
            parser.getNextLexemeType();
            if (currentLexeme == LexemeType.PLUS) {
                result = result.add(calculateItem(parser));
            } else if (currentLexeme == LexemeType.MINUS) {
                result = result.subtract(calculateItem(parser));
            }
            currentLexeme = parser.getCurrentLexemeType();
        }
        return result;
    }

    private static BigInteger calculateItem(StringParser parser) throws Exception {
        BigInteger result = calculateMultiplier(parser);
        LexemeType currentLexeme = parser.getCurrentLexemeType();
        while (currentLexeme == LexemeType.MULTIPLICATION
                || currentLexeme == LexemeType.INTEGER_DIVISION) {
            parser.getNextLexemeType();
            if (currentLexeme == LexemeType.MULTIPLICATION) {
                result = result.multiply(calculateMultiplier(parser));
            } else if (currentLexeme == LexemeType.INTEGER_DIVISION) {
                result = result.divide(calculateMultiplier(parser));
            }
            currentLexeme = parser.getCurrentLexemeType();
        }
        return result;
    }

    private static BigInteger calculateMultiplier(StringParser parser) throws Exception {
        BigInteger result;
        LexemeType currentLexeme = parser.getCurrentLexemeType();

        if (currentLexeme == LexemeType.NUMBER) {
            result = parser.getCurrentNumber();
        } else if (currentLexeme == LexemeType.OPENING_BRACKET) {
            parser.getNextLexemeType();
            result = calculateExpression(parser);
            currentLexeme = parser.getCurrentLexemeType();
            if (currentLexeme != LexemeType.CLOSING_BRACKET) {
                throw new Exception("Unexpected lexeme: should be closing bracket");
            }
        } else {
            throw new Exception("Unexpected lexeme: " + currentLexeme);
        }
        parser.getNextLexemeType();

        return result;
    }
}

enum LexemeType {
    NUMBER,
    OPENING_BRACKET,
    CLOSING_BRACKET,
    PLUS,
    MINUS,
    MULTIPLICATION,
    INTEGER_DIVISION,
    END;
}

class StringParser {

    private String expression;
    private HashSet<Character> possibleDigitsSet = new HashSet<Character>();
    private int radix;
    private int currentPosition = 0;
    private LexemeType currentState;

    private BigInteger currentNumber;

    StringParser(String stringToParse, int base) throws Exception {
        expression = stringToParse;
        calculatePossibleDigitsSet(base);
        radix = base;
        getNextLexemeType();
    }


    LexemeType getNextLexemeType() throws Exception {
       while (currentPosition < expression.length()
               && Character.isWhitespace(expression.charAt(currentPosition))) {
           currentPosition += 1;
       }

       if (currentPosition >= expression.length()) {
           currentState = LexemeType.END;
           return currentState;
       }

       char currentChar = expression.charAt(currentPosition);

       if (currentChar == '(') {
           currentPosition += 1;
           currentState = LexemeType.OPENING_BRACKET;
       } else if (currentChar == ')') {
           currentPosition += 1;
           currentState = LexemeType.CLOSING_BRACKET;
       } else if (currentChar == '+') {
           currentPosition += 1;
           currentState = LexemeType.PLUS;
       } else if (currentChar == '-') {
           currentPosition += 1;
           currentState = LexemeType.MINUS;
       } else if (currentChar == '*') {
           currentPosition += 1;
           currentState = LexemeType.MULTIPLICATION;
       } else if (currentChar == '/') {
           currentPosition += 1;
           currentState = LexemeType.INTEGER_DIVISION;
       } else if (possibleDigitsSet.contains(currentChar)) {
           int startPosition = currentPosition;
           int endPosition;

           while (currentPosition + 1 < expression.length()
                   && possibleDigitsSet.contains(expression.charAt(currentPosition + 1))) {
               currentPosition += 1;
           }
           currentPosition += 1;
           endPosition = currentPosition;
           currentNumber = new BigInteger(expression.substring(startPosition, endPosition), radix);
           currentState = LexemeType.NUMBER;
       } else {
           throw new Exception("Unknown character " + currentChar);
       }
       return currentState;
    }

    BigInteger getCurrentNumber() {
        //assert (currentState == LexemeType.NUMBER);
        return currentNumber;
    }

    LexemeType getCurrentLexemeType() {
        return currentState;
    }

    private void calculatePossibleDigitsSet(int base) {
        for (int i = 0; i < base; ++i) {
            if (i < 10) {
                possibleDigitsSet.add((char) ('0' + i));
            } else {
                possibleDigitsSet.add((char) ('A' + i - 10));
                possibleDigitsSet.add((char) ('a' + i - 10));
            }
        }
    }
}
