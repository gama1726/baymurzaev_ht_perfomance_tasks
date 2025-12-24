import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Используйте: java Main ellipse.txt points.txt");
            return;
        }

        // ellipse.txt: x0 y0 a b (в любой разметке по пробелам/переносам)
        double[] e = readAllNumbers(args[0]);
        double x0 = e[0];
        double y0 = e[1];
        double a = e[2];
        double b = e[3];

        // points.txt: x1 y1 x2 y2 ...
        double[] p = readAllNumbers(args[1]);

        double a2 = a * a;
        double b2 = b * b;
        double eps = 1e-9;

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < p.length; i += 2) {
            double x = p[i];
            double y = p[i + 1];

            double dx = x - x0;
            double dy = y - y0;

            double v = (dx * dx) / a2 + (dy * dy) / b2;

            if (Math.abs(v - 1.0) <= eps) out.append("0\n");
            else if (v < 1.0) out.append("1\n");
            else out.append("2\n");
        }

        System.out.print(out.toString());
    }

    private static double[] readAllNumbers(String path) throws Exception {
        ArrayList<Double> list = new ArrayList<>();
        Scanner sc = new Scanner(new File(path));
        while (sc.hasNext()) {
            if (sc.hasNextDouble()) list.add(sc.nextDouble());
            else sc.next(); // пропускаем мусор
        }
        sc.close();

        double[] arr = new double[list.size()];
        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
        return arr;
    }
}
