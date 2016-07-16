package de.ygriega.heinzelnisse;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import info.heinzelnisse.he.BitObfuscatorInputStream;

public class HeinzelnisseSQLiteConverter {

  public final static String CHARSET_IN = "ISO8859-1";
  public final static String CHARSET_OUT = "UTF-8";
  public static final String FILE_IN = "/directory/to/heinzelliste.txt";
  public static final String FILE_OUT = "/directory/to/heinzelliste_decode.txt";
  public static final String DATABASE = "/directory/to/heinzelliste.db";

  public static void main(String[] args) {

    System.out.println("=====================================");
    System.out.println("= Heinzelnisse SQLite Converter 0.1 =");
    System.out.println("=====================================");
    System.out.println();

    try {
      System.out.println("Reading raw file from: " + FILE_IN);
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
          new BitObfuscatorInputStream(new BufferedInputStream(new FileInputStream(FILE_IN))), CHARSET_IN));
      System.out.println("Preparing decoded textfile on: " + FILE_OUT);
      BufferedWriter bufferedWriter = new BufferedWriter(
          new OutputStreamWriter(new FileOutputStream(FILE_OUT), CHARSET_OUT));
      String line;

      Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE);
      Statement statement = connection.createStatement();
      statement.setQueryTimeout(30);
      System.out.println("Creating database at: " + DATABASE);
      statement.executeUpdate("drop table if exists heinzelnisse");
      statement.executeUpdate(
          "create virtual table heinzelnisse using fts4(id integer primary key, no_word text, no_gender text, no_optional text, no_other text, de_word text, de_gender text, de_optional text, de_other text, category text, grade text)");

      PreparedStatement preparedStatement = connection
          .prepareStatement("insert into heinzelnisse values( ?,?,?,?,?,?,?,?,?,?,? )");

      int wordIndex = 0;
      while ((line = bufferedReader.readLine()) != null) {
        if (line.startsWith("#")) {
          // First line
          continue;
        }
        wordIndex++;

        preparedStatement.clearParameters();
        String[] oneEntryAsArray = line.split("\t");
        preparedStatement.setInt(1, wordIndex);
        System.out.println("Storing word " + wordIndex + ": " + oneEntryAsArray[0]);
        for (int i = 2; i < 12; i++) {
          if (oneEntryAsArray.length > i - 1) {
            preparedStatement.setString(i, oneEntryAsArray[i - 2]);
          } else {
            break;
          }
        }
        preparedStatement.executeUpdate();

        bufferedWriter.write(line);
        bufferedWriter.newLine();
      }
      bufferedReader.close();
      bufferedWriter.close();
      connection.close();

    } catch (IOException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

}
