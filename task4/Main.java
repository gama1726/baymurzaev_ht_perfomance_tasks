import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Используйте: java Main nums.txt");
            return;
        }

        ArrayList<Long> a = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty()) a.add(Long.parseLong(line));
        }
        br.close();

        if (a.isEmpty()) {
            System.out.print(0);
            return;
        }

        Collections.sort(a);

        long med = a.get(a.size() / 2); // для чётного тоже ок (любая медиана)
        long moves = 0;
        for (long x : a) moves += Math.abs(x - med);

        if (moves <= 20) {
            System.out.print(moves);
        } else {
            System.out.print("20 ходов недостаточно для приведения всех элементов массива к одному числу");
        }
    }
}
