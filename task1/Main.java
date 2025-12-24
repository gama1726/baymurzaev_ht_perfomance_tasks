public class Main {

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Используйте: java Main n1 m1 n2 m2");
            return;
        }

        int n1 = Integer.parseInt(args[0]);
        int m1 = Integer.parseInt(args[1]);
        int n2 = Integer.parseInt(args[2]);
        int m2 = Integer.parseInt(args[3]);

        String p1 = buildPath(n1, m1);
        String p2 = buildPath(n2, m2);

        System.out.print(p1 + p2);
    }

    private static String buildPath(int n, int m) {
        // шаг = m-1, потому что следующий интервал начинается с конца предыдущего
        int step = (m - 1) % n;

        int cur = 1; // стартуем с 1
        StringBuilder sb = new StringBuilder();

        do {
            sb.append(cur);
            cur = cur + step;
            // закольцовываем в диапазон 1..n
            cur = ((cur - 1) % n) + 1;
        } while (cur != 1);

        return sb.toString();
    }
}
