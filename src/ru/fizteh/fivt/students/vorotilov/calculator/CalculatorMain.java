package ru.fizteh.fivt.students.vorotilov.calculator;

import java.util.Stack;

public class CalculatorMain {

    static final int NUMBER_BASE = 17;

    static boolean isOperator(char c) {
        return (c == '+') || (c == '-') || (c == '*') || (c == '/');
    }

    static int operatorPriority(char op) {
        if (op == '+' || op == '-') {
            return 1;
        } else if (op == '*' || op == '/') {
            return 2;
        } else {
            return -1;
        }
    }

    public static void checkIntegerOverflow(long a) throws Exception {
        if (a > Integer.MAX_VALUE) {
            throw new Exception("Integer Overflow");
        } else if (a < Integer.MIN_VALUE) {
            throw new Exception("Integer Underflow");
        }
    }

    static void calculationStep(Stack<Integer> st, char op) throws Exception {
        if (st.size() < 2) {
            throw new Exception("Operator: " + op + " need two arguments");
        }
        Integer r = st.pop();
        Integer l = st.pop();
        switch (op) {
            case '+':
                long add = ((long) l) + r;
                checkIntegerOverflow(add);
                st.push((int) add);
                break;
            case '-':
                long min = ((long) l) - r;
                checkIntegerOverflow(min);
                st.push((int) min);
                break;
            case '*':
                long mul = ((long) l) * r;
                checkIntegerOverflow(mul);
                st.push((int) mul);
                break;
            case '/':
                long div = ((long) l) / r;
                checkIntegerOverflow(div);
                st.push((int) div);
                break;
            default:
                throw new Exception("Unknown operator: " + op);
        }
    }

    static String calculate(String inputString) throws Exception {
        Stack<Integer> st = new Stack<>();
        Stack<Character> op = new Stack<>();
        for (int i = 0; i < inputString.length(); ++i) {
            if (!Character.isWhitespace(inputString.charAt(i))) {
                if (inputString.charAt(i) == '(') {
                    op.push('(');
                } else if (inputString.charAt(i) == ')') {
                    while (!op.empty() && op.peek() != '(') {
                        calculationStep(st, op.pop());
                    }
                    op.pop();
                } else if (isOperator(inputString.charAt(i))) {
                    while (!op.empty()
                            && operatorPriority(op.peek()) >= operatorPriority(inputString.charAt(i))) {
                        calculationStep(st, op.pop());
                    }
                    op.push(inputString.charAt(i));
                } else {
                    StringBuilder operand = new StringBuilder();
                    while (i < inputString.length()
                            && (Character.isAlphabetic(inputString.charAt(i))
                            || Character.isDigit(inputString.charAt(i)))) {
                        operand.append(inputString.charAt(i));
                        ++i;
                    }
                    --i;
                    st.push(Integer.parseInt(operand.toString(), NUMBER_BASE));
                }
            }
        }
        while (!op.empty()) {
            calculationStep(st, op.pop());
        }
        if (st.size() != 1) {
            throw new Exception("Wrong expression");
        }
        return Integer.toString(st.peek(), NUMBER_BASE);
    }

    public static void main(String[] args) {
        try {
            StringBuilder theWholeArgument = new StringBuilder();
            for (String arg : args) {
                theWholeArgument.append(arg);
                theWholeArgument.append(' ');
            }
            System.out.println(calculate(theWholeArgument.toString()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}

