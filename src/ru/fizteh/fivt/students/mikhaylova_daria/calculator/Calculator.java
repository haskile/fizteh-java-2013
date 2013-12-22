package ru.fizteh.fivt.students.mikhaylova_daria.calculator;

import java.util.Stack;
import java.util.Vector;

public class Calculator {

    public static void main(String[] arg) {
        StringBuilder builderArgument = new StringBuilder();
        if (arg.length == 0) {
            System.out.println("Калькулятор, позволяющий вычислять числовые выражения в 17-ричной системе счисления\n"
                    + "\nВозможные операции:\n"
                    + "1) сложение\n"
                    + "2) вычитание\n"
                    + "3) умножение\n"
                    + "4) целочисленное деление\n"
                    + "\n"
                    + "Диапазон возможных значений: целые числа из промежутка от"
                    + Integer.toString(Integer.MIN_VALUE, 17) + " до " + Integer.toString(Integer.MAX_VALUE, 17) + "\n\n"
                    + "Пример корректного выражения:\n"
                    + "1 + A + c - (4 * 5 / 2)\n"
            );
            System.exit(1);
        } else {
            for (int i = 0; i < arg.length; ++i) {
                builderArgument.append(arg[i]);
                builderArgument.append(' ');
            }
            try {
                String answer = calculatorReversePolishNotation(converterToReversePolishNotation(builderArgument));
                System.out.println(answer);
            } catch (Exception exp) {
                System.err.println(exp.getLocalizedMessage());
                System.exit(1);
            }
        }
        return;
    }


    private static Vector<String> converterToReversePolishNotation(StringBuilder arg) throws Exception {
        Stack<String> stack = new Stack<String>(); 
        Vector<String> vectorReversePolishNotation = new Vector<String>();
        StringBuilder number = new StringBuilder();
        int i = 0;
        int operatorOrNum = 0;
        int bracketBalance = 0;
        int signOfNumber = 1;
        boolean lastCharWasNumber = false;
        while (i < arg.length()) {
            if (signOfNumber == -1) {
                number.append('-');
            }
            while ((i < arg.length())
                    && (('0' <= arg.charAt(i) && arg.charAt(i) <= '9')
                    || ('a' <= arg.charAt(i) && arg.charAt(i) <= 'g')
                    || ('A' <= arg.charAt(i) && arg.charAt(i) <= 'G'))) {
                number.append(arg.charAt(i));
                ++i;
            }
            if (!number.toString().isEmpty()) {
                if (lastCharWasNumber) {
                    throw new Exception("Пропущен оператор!");
                }
                vectorReversePolishNotation.add(number.toString());
                number = new StringBuilder();
                signOfNumber = 1;
                ++operatorOrNum;
                lastCharWasNumber = true;
            }
            if (i < arg.length()) {
                if (arg.charAt(i) == '(') {
                    if (operatorOrNum > 0) {
                        throw new Exception("Пропущен оператор!");
                    }
                    if (operatorOrNum < 0) {
                        throw new Exception("Пропущено число!");
                    }
                    stack.push("(");
                    ++bracketBalance;
                    lastCharWasNumber = false;
                } else {
                    if (arg.charAt(i) == ')') {
                        --bracketBalance;
                        if (bracketBalance < 0) {
                            throw new Exception("Нарушен баланс скобок!");
                        }
                        if (operatorOrNum < 1) {
                            throw new Exception("Некорректный ввод. Возможно, пропущено число!");
                        }
                        if (operatorOrNum > 1) {
                            throw new Exception("Некорректный ввод. Возможно, пропущен оператор!");
                        }
                        while (!stack.peek().equals("(")) {
                            vectorReversePolishNotation.add(stack.pop());
                        }
                        stack.pop();
                        lastCharWasNumber = true;
                    } else {
                        switch (arg.charAt(i)) {
                            case '+':
                                --operatorOrNum;
                                if (operatorOrNum != 0) {
                                    throw new Exception("Некорректный ввод. Возможно, пропущено число!");
                                }
                                while ((!stack.empty()) && !stack.peek().equals("(")
                                        && (stack.peek().equals("-") || stack.peek().equals("+")
                                        || stack.peek().equals("/") || stack.peek().equals("*"))) {
                                    vectorReversePolishNotation.add(stack.pop());
                                }
                                stack.push("+");
                                lastCharWasNumber = false;
                                break;
                            case '-':
                                boolean flag = !stack.empty();
                                if (flag) {
                                    flag = (stack.peek() == "(");
                                }
                                if (vectorReversePolishNotation.isEmpty() || (flag && !lastCharWasNumber)) {
                                    signOfNumber = -1;
                                } else {
                                    --operatorOrNum;
                                    if (operatorOrNum != 0) {
                                        throw new Exception("Некорректный ввод. Возможно, пропущено число!");
                                    }
                                    while ((!stack.empty()) && (!stack.peek().equals("(")
                                            && (stack.peek().equals("+") || stack.peek().equals("-")
                                            || stack.peek().equals("/") || stack.peek().equals("*")))) {
                                        vectorReversePolishNotation.add(stack.pop());
                                    }
                                    stack.push("-");
                                }
                                lastCharWasNumber = false;
                                break;
                            case '*':
                                --operatorOrNum;
                                if (operatorOrNum != 0) {
                                    throw new Exception("Некорректный ввод. Возможно, пропущено число!");
                                }
                                while ((!stack.empty())
                                        && (stack.peek().equals("/") || stack.peek().equals("*"))
                                        && !stack.peek().equals("(")) {
                                    vectorReversePolishNotation.add(stack.pop());
                                }
                                stack.push("*");
                                lastCharWasNumber = false;
                                break;
                            case '/':
                                --operatorOrNum;
                                if (operatorOrNum != 0) {
                                    throw new Exception("Некорректный ввод. Возможно, пропущено число!");
                                }
                                while ((!stack.empty())
                                        && (stack.peek().equals("*") || stack.peek().equals("/"))
                                        && !stack.peek().equals("(")) {
                                    vectorReversePolishNotation.add(stack.pop());
                                }
                                stack.push("/");
                                lastCharWasNumber = false;
                                break;
                            case ' ':
                                break;
                            default:
                                throw new Exception("Неизвестный символ:" + arg.charAt(i));
                        }
                    }
                }
            }
            ++i;
        }
        if (bracketBalance != 0) {
            throw new Exception("Нарушен баланс скобок!");
        }
        if (!number.toString().isEmpty()) {
            vectorReversePolishNotation.add(number.toString());
            if (stack.empty()) {
                if (vectorReversePolishNotation.size() != 1) {
                    throw new Exception("Некорректный ввод. Возможно, пропущен оператор!");
                }
            }
        }
        while (!stack.empty()) {
            vectorReversePolishNotation.add(stack.pop());
        }
        return vectorReversePolishNotation;
    }

    private static String calculatorReversePolishNotation(Vector<String> argument) throws Exception {
        int i = 0;
        Stack<Integer> stack = new Stack<Integer>();
        while (i < argument.size()) {
            if (argument.elementAt(i).equals("+")
                    || argument.elementAt(i).equals("-")
                    || argument.elementAt(i).equals("*")
                    || argument.elementAt(i).equals("/")) {
                Integer operand2;
                Integer operand1;
                try {
                    operand2 = stack.pop();
                    operand1 = stack.pop();
                } catch (Throwable e) {
                    throw new Exception ("Непредвиденная ошибка");
                }
                if (argument.elementAt(i).equals("+")) {
                    Long result = operand1.longValue() + operand2.longValue();
                    if ((result > Integer.MAX_VALUE) || (result < Integer.MIN_VALUE)) {
                        throw new Exception("Произошло переполнение");
                    } else {
                        stack.push(operand1 + operand2);
                    }
                }
                if (argument.elementAt(i).equals("-")) {
                    Long result = operand1.longValue() - operand2.longValue();
                    if ((result > Integer.MAX_VALUE) || (result < Integer.MIN_VALUE)) {
                        throw new Exception("Произошло переполнение");
                    } else {
                        stack.push(operand1 - operand2);
                    }
                }
                if (argument.elementAt(i).equals("*")) {
                    Long result = operand1.longValue() * operand2.longValue();
                    if ((result > Integer.MAX_VALUE) || (result < Integer.MIN_VALUE)) {
                        throw new Exception("Произошло переполнение");
                    } else {
                        stack.push(operand1 * operand2);
                    }
                }
                if (argument.elementAt(i).equals("/")) {
                    Long result = operand1.longValue() / operand2.longValue();
                    if ((result > Integer.MAX_VALUE) || (result < Integer.MIN_VALUE)) {
                        throw new Exception("Произошло переполнение");
                    } else {
                        stack.push(operand1 / operand2);
                    }
                }
            } else {
                Integer number;
                try {
                    number = Integer.parseInt(argument.elementAt(i), 17);
                } catch (Exception e) {
                    throw new Exception("Непредвиденная ошибка");
                }
                stack.push(number);
            }
            ++i;
        }
        String result = Integer.toString(stack.peek(), 17);
        return result;
    }

}

