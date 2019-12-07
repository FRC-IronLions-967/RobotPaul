import java.util.Scanner;
public class Test {
    public static void main(String args[]) {
        System.out.println("Input an integer:");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();
        System.out.println(input);
        scanner.close();
    }
}