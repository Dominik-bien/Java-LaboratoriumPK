public class ONP {
    private TabStack stack = new TabStack();

    /**
     * Metoda sprawdza, czy równanie kończy się znakiem '='
     */
    boolean czyPoprawneRownanie(String rownanie) {
        return rownanie.endsWith("=");
    }

    /**
     * Metoda oblicza wartość wyrażenia ONP
     */
    public String obliczOnp(String rownanie) {
        if (!czyPoprawneRownanie(rownanie)) {
            return "Błąd: Równanie musi kończyć się znakiem '='.";
        }

        stack = new TabStack();
        String wynik = "";
        Double a, b;

        try {
            for (int i = 0; i < rownanie.length(); i++) {
                char znak = rownanie.charAt(i);

                if (Character.isDigit(znak)) {
                    wynik += znak;
                    if (i + 1 >= rownanie.length() || !Character.isDigit(rownanie.charAt(i + 1))) {
                        stack.push(wynik);
                        wynik = "";
                    }
                } else if (znak == '=') {
                    return stack.pop();
                } else if (znak != ' ') {
                    if (znak == 'x' || znak == '!') {
                        a = Double.parseDouble(stack.pop());
                        if (znak == 'x') {
                            stack.push(Math.sqrt(a) + "");
                        } else {
                            stack.push(factorial(a.intValue()) + "");
                        }
                    } else {
                        b = Double.parseDouble(stack.pop());
                        a = Double.parseDouble(stack.pop());

                        switch (znak) {
                            case '+':
                                stack.push((a + b) + "");
                                break;
                            case '-':
                                stack.push((a - b) + "");
                                break;
                            case '*':
                                stack.push((a * b) + "");
                                break;
                            case '/':
                                if (b == 0){
                                    throw new ArithmeticException("Błąd: Dzielenie przez zero.");
                                }
                                stack.push((a / b) + "");
                                break;
                            case '%':
                                if (b == 0) {
                                    throw new ArithmeticException("Błąd: Modulo przez zero.");
                                }
                                stack.push((a % b) + "");
                                break;
                            case '^': stack.push(Math.pow(a, b) + ""); break;
                            default:
                                throw new IllegalArgumentException("Błąd: Nieobsługiwany operator '" + znak + "'.");
                        }
                    }
                }
            }
        } catch (IllegalStateException | NumberFormatException e) {
            return "Błąd obliczeń: " + e.getMessage();
        }
        return "Błąd: Niepoprawne równanie.";
    }

    /**
     * Metoda zamienia równanie na postać ONP
     */
    public String przeksztalcNaOnp(String rownanie) {
        if (!czyPoprawneRownanie(rownanie)) {
            return "Błąd: Równanie musi kończyć się znakiem '='.";
        }

        String wynik = "";
        stack = new TabStack();

        try {
            for (int i = 0; i < rownanie.length(); i++) {
                char znak = rownanie.charAt(i);

                if (Character.isDigit(znak)) {
                    wynik += znak;
                    if (i + 1 >= rownanie.length() || !Character.isDigit(rownanie.charAt(i + 1))) {
                        wynik += " ";
                    }
                } else {
                    switch (znak) {
                        case '+': case '-': {
                            while (stack.getSize() > 0 && !stack.showValue(stack.getSize() - 1).equals("(")) {
                                wynik += stack.pop() + " ";
                            }
                            stack.push("" + znak);
                            break;
                        }
                        case '*': case '/': case '%': {
                            while (stack.getSize() > 0 && !stack.showValue(stack.getSize() - 1).equals("(") &&
                                    !stack.showValue(stack.getSize() - 1).equals("+") &&
                                    !stack.showValue(stack.getSize() - 1).equals("-")) {
                                wynik += stack.pop() + " ";
                            }
                            stack.push("" + znak);
                            break;
                        }
                        case '^': {
                            while (stack.getSize() > 0 && stack.showValue(stack.getSize() - 1).equals("^")) {
                                wynik += stack.pop() + " ";
                            }
                            stack.push("" + znak);
                            break;
                        }
                        case 'x': case '!': {
                            stack.push("" + znak);
                            break;
                        }
                        case '(': {
                            stack.push("" + znak);
                            break;
                        }
                        case ')': {
                            while (stack.getSize() > 0 && !stack.showValue(stack.getSize() - 1).equals("(")) {
                                wynik += stack.pop() + " ";
                            }
                            stack.pop();
                            break;
                        }
                        case '=': {
                            while (stack.getSize() > 0) {
                                wynik += stack.pop() + " ";
                            }
                            wynik += "=";
                            break;
                        }
                        default:
                            throw new IllegalArgumentException("Błąd: Nieobsługiwany znak '" + znak + "'.");
                    }
                }
            }
        } catch (IllegalStateException e) {
            return "Błąd przekształcania: " + e.getMessage();
        }

        return wynik;
    }

    private int factorial(int n) {
        if (n < 0){
            throw new IllegalArgumentException("Błąd: Silnia z liczby ujemnej jest niemożliwa.");
        }
        int result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static void main(String[] args) {
        String tmp = "(22+3)/(2-34)*3+8)=";
        ONP onp = new ONP();
        String rownanieOnp = onp.przeksztalcNaOnp(tmp);
        System.out.println("ONP: " + rownanieOnp);
        System.out.println("Wynik: " + onp.obliczOnp(rownanieOnp));
    }
}

