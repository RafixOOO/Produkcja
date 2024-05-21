package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class DatabaseQueryWindow {
    private static final String JDBC_URL = "jdbc:postgresql://10.100.100.48:5432/punktualnik_db";
    private static final String JDBC_USER = "postgres";
    private static final String JDBC_PASSWORD = "sa";

    private JFrame frame;
    private JLabel label;

    public DatabaseQueryWindow() {
        frame = new JFrame("Obecność");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        label = new JLabel("Loading...", SwingConstants.CENTER);
        frame.getContentPane().add(label, BorderLayout.CENTER);

        frame.setVisible(true);
        scheduleDataRefresh();
    }

    private void scheduleDataRefresh() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshData();
            }
        }, 0, 5000); // Odświeżaj co 5 sekund
    }

    private void refreshData() {
        SwingUtilities.invokeLater(() -> {
            String data = fetchDataFromDatabase();
            label.setText(data);
        });
    }

    private String fetchDataFromDatabase() {

        String resultText = "<html>";

        String pracownicy = "SELECT count(distinct o.idx_osoby) as nazwa FROM users o\n" +
                "                JOIN att_log l ON l.idx_osoby = o.idx_osoby\n" +
                "                JOIN dzialy d ON o.idx_dzialu = d.idx_dzialu\n" +
                "                WHERE l.in_out IN ('0', '2')\n" +
                "                 and l.aktywny = 'true'\n" +
                "                AND l.idx_device in ('37', '1', '38', '5', '2', '43', '42', '4', '6', '3','45','20')\n" +
                "                --and o.idx_osoby='3001'\n" +
                "                AND d.nazwa LIKE '%Produkcja%'\n" +
                "                AND CAST(l.data_czas AS DATE) = CURRENT_DATE";


        String queryKolowrot = "SELECT COUNT(distinct o.idx_osoby) AS kolowrot\n" +
                "FROM users o\n" +
                "JOIN att_log l ON l.idx_osoby = o.idx_osoby\n" +
                "JOIN dzialy d ON o.idx_dzialu = d.idx_dzialu\n" +
                "WHERE l.in_out IN ('0', '2')\n" +
                "AND l.aktywny = 'true'\n" +
                "AND l.idx_device = '20'\n" +
                "AND d.nazwa LIKE '%Produkcja%'\n" +
                "AND CAST(l.data_czas AS DATE) = CURRENT_DATE;";

        String queryNowaHala = "SELECT count(distinct o.idx_osoby) as nazwa FROM users o\n" +
                "                JOIN att_log l ON l.idx_osoby = o.idx_osoby\n" +
                "                JOIN dzialy d ON o.idx_dzialu = d.idx_dzialu\n" +
                "                WHERE l.in_out IN ('0', '2')\n" +
                "                 and l.aktywny = 'true'\n" +
                "                AND (l.idx_device = '45')\n" +
                "                --and o.idx_osoby='3001'\n" +
                "                AND d.nazwa LIKE '%Produkcja%'\n" +
                "                AND CAST(l.data_czas AS DATE) = CURRENT_DATE";

        String queryCzasPracy = "SELECT count(distinct o.idx_osoby) as workTime FROM users o\n" +
                "                JOIN att_log l ON l.idx_osoby = o.idx_osoby\n" +
                "                JOIN dzialy d ON o.idx_dzialu = d.idx_dzialu\n" +
                "                WHERE l.in_out IN ('0', '2')\n" +
                "                 and l.aktywny = 'true'\n" +
                "                AND (l.idx_device IN ('37', '1', '38', '5', '2', '43', '42', '4', '6', '3'))\n" +
                "                --and o.idx_osoby='3001'\n" +
                "                AND d.nazwa LIKE '%Produkcja%'\n" +
                "                AND CAST(l.data_czas AS DATE) = CURRENT_DATE";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement stmt = conn.createStatement()) {

            ResultSet prac = stmt.executeQuery(pracownicy);
            if (prac.next()) {
                resultText += "Pracownicy: " + prac.getInt("nazwa") + "<br>";
            }


            ResultSet rsKolowrot = stmt.executeQuery(queryKolowrot);
            if (rsKolowrot.next()) {
                resultText += "Kołowrót: " + rsKolowrot.getInt("kolowrot") + "<br>";
            }

            ResultSet rsNowaHala = stmt.executeQuery(queryNowaHala);
            if (rsNowaHala.next()) {
                resultText += "Nowa hala: " + rsNowaHala.getInt("nazwa") + "<br>";
            }

            ResultSet rsCzasPracy = stmt.executeQuery(queryCzasPracy);
            if (rsCzasPracy.next()) {
                resultText += "Czas pracy: " + rsCzasPracy.getInt("workTime") + "<br>";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            resultText = "<html>Błąd zapytania: " + e.getMessage() + "</html>";
        }

        resultText += "</html>";
        return resultText;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DatabaseQueryWindow::new);
    }
}
