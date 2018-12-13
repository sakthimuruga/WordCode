/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CrawlWord;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.io.File;

/**
 *
 * @author Administrator
 */
public class CrawlWord {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, SQLException {
        String path = "/home/WordCode/Crawled_Files/";//Path of the crawled Files
int value = 0;
                int id = 1;
        String files;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {

            if (listOfFiles[i].isFile()) {
                files = listOfFiles[i].getName();
                System.out.println(files);
                String inputFileName = "/home/WordCode/Crawled_Files/" + files;
                  System.out.println(inputFileName);
                InputStream is = null;
                InputStreamReader isr = null;
                BufferedReader br = null;
                String temp_string = "";

                
                try {

                    is = new FileInputStream(inputFileName);
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);

                    while ((value = br.read()) != -1) {
                        // System.out.println(value + "\n");
                        temp_string = "";
                        while (((value >= 97) && (value <= 122)) || ((value >= 65) && (value <= 90))) {
                            temp_string += (char) value;
                            value = br.read();

                        }
                        if (temp_string.length() > 2) {    //three or more words
                            System.out.println(temp_string + "\n");
                            Connection connection = getConnection();//Get DB Connection
                            PreparedStatement psmt1, psmt2, psmt3;
                            ResultSet rs;
                            psmt1 = connection.prepareStatement("select ID, Count from Table1 where Word='" + temp_string + "'");
                            rs = psmt1.executeQuery();
                            if (rs.next()) {
                                psmt3 = connection.prepareStatement("update Table1 set Count=? where ID=?");
                                psmt3.setInt(2, rs.getInt(1));
                                psmt3.setInt(1, rs.getInt(2) + 1);
                                psmt3.executeUpdate();
                            } else {
                                psmt2 = connection.prepareStatement("insert into Table1 values(?,?,?)");
                                psmt2.setInt(1, id);
                                psmt2.setString(2, temp_string);
                                psmt2.setInt(3, 1);
                                psmt2.execute();
                                id++;
                            }
                            rs.close();
                            connection.close();

                        }



                    }
                } catch (Exception e) {

                    e.printStackTrace();
                } finally {

                    if (is != null) {
                        is.close();
                    }
                    if (isr != null) {
                        isr.close();
                    }
                    if (br != null) {
                        br.close();
                    }
                }
            }
        }

    }
}
