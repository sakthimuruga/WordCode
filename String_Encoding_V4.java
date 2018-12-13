package test1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.zip.GZIPOutputStream;

public class String_Encoding_V4 {

    static int charSiz = 0, intSiz = 0;
    static ArrayList<Integer> prefix = new ArrayList<Integer>();
    static int id = 1;
    static Node root;
    static int mem;

    static ArrayList<String> Level_1 = new ArrayList<String>();
    static int symbolHit1 = 0, symbolHit2 = 0, symbolHit3 = 0, symbolHit4 = 0, SymbolMiss = 0;
    static int Level1 = 0, Level2 = 0, Level3 = 0, Level4 = 0;

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {

        root = new Node();
        charSiz++;

        OutputStreamWriter log = new OutputStreamWriter(new FileOutputStream("/Users/Sakthi/Research/WordCode/test/Log/log.txt"));
        loadMemory();
        intializePrefix();
        File dir = new File(".");
        String files;
        String path = "/Users/Sakthi/Research/WordCode/test/html/";
        String outputpath = "/Users/Sakthi/Research/WordCode/test/Codes/";
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        int totalFiles = 0, comSucess = 0, wdSucess = 0, wtSucess = 0, gwtSucess = 0, gtSucess = 0, gwdSucess = 0;
        System.out.println("File Name  \t File Size \t WcodeFile Size  \t gzipsize \t gzipWcodesize \t WordCodeTime  \t WordDeCodeTime \t gzipsizeTime  \t gzipWcodesizeTime    \t symbolHit1  \t symbolHit2 \t symbolHit3 \t symbolHit4 \t SymbolMiss  ");
        for (int i = 0; (i < listOfFiles.length); i++) {
            if (listOfFiles[i].toString().endsWith(".html")) {
                symbolHit1 = 0;
                symbolHit2 = 0;
                symbolHit3 = 0;
                symbolHit4 = 0;
                SymbolMiss = 0;
                if (listOfFiles[i].isFile()) {
                    files = listOfFiles[i].getName();
                    String input = path + files;
                    long fileSize = listOfFiles[i].length();
                    //  String source = input;
                    String source_filepath = input;//Wikipedia.html
                    totalFiles++;

                    // String dest = outputpath + files + ".cmp";
                    String Wcode = outputpath + files + ".wc";
                    String WDecode = outputpath + files + ".dwc";
                    String gzip = outputpath + files + ".gzip";
                    String gzipWcode = Wcode + ".gzip";
                    // long size = getGzipFileSize(source_filepath);
                    //  System.out.println("Filesize in bytes: " + size);
                    System.out.print(files + " \t " + fileSize + " \t ");
                    // Compress(source_filepath, dest);
                    long startTime = System.currentTimeMillis();
                    WordCode(source_filepath, Wcode, log);
                    long WordCodeTime = System.currentTimeMillis();
                    symbolHit1 = 0;
                    symbolHit2 = 0;
                    symbolHit3 = 0;
                    symbolHit4 = 0;
                    SymbolMiss = 0;
                    WordDeCode(Wcode, WDecode);
                    long WordDeCodeTime = System.currentTimeMillis();
                    

                    long gzipsize = getGzipFileSize(source_filepath, gzip);
                    long gzipsizeTime = System.currentTimeMillis();
                    long gzipWcodesize = getGzipFileSize(Wcode, gzipWcode);
                    long gzipWcodesizeTime = System.currentTimeMillis();

                    File WcodeFile = new File(Wcode);
                    System.out.print(WcodeFile.length() + " \t ");
                    System.out.print(gzipsize + " \t ");
                    System.out.print(gzipWcodesize + " \t ");

             

                    System.out.print(symbolHit1 + " \t ");
                    System.out.print(symbolHit2 + " \t ");
                    System.out.print(symbolHit3 + " \t ");
                    System.out.print(symbolHit4 + " \t ");
                    System.out.print(SymbolMiss + " \t ");

                    if (fileSize >= WcodeFile.length()) {
                        System.out.print("\t WordCode");
                        wdSucess++;
                    } else {
                        System.out.print("\t ");
                    }
                    
                    if (gzipsize >= gzipWcodesize) {
                        System.out.print("\t GzipWordCode");
                        gwdSucess++;
                    } else {
                        System.out.print("\t ");
                    }
                    
                    

                    System.out.println();
                }
            }
        }
        System.out.println("totalFiles = " + totalFiles + ", WordCode = " + wdSucess +  ", GzipWordCode = " + gwdSucess );
        log.close();
        //        long stopTime = System.currentTimeMillis();
//        long elapsedTime = stopTime - startTime;
//        System.out.println("Time took to load String code to memory : " + elapsedTime + "millisecond");
//        Runtime runtime = Runtime.getRuntime();
//        runtime.gc();
//        long memory = runtime.totalMemory() - runtime.freeMemory();
//        System.out.println("Used memory to load String code in bytes: " + memory);
//        long MEGABYTE = 1024L * 1024L;
//        System.out.println("Used memory  to load String code megabytes: " + (memory) / MEGABYTE);
    }
    private static int getWodCod(int[] pre) {
        int retVal = 0;
        int prefix1 = prefix.indexOf(pre[0]);
        if (prefix1 == 0) {
            retVal = pre[1];
        } else {
            int val1=pre[1],val2=pre[2];
            if(val2<128){
                retVal=55000+(val1*128)+(16384*(prefix1-1))+val2;
            }else{
                retVal=267992+(val1*1920)+(245760*(prefix1-1))+(val2-128);
            }
        }
        return retVal +1;
    }

    private static int[] getPrefix(int i) {
        int[] retVal = new int[3];
        if (i < 55000) {
            retVal[0] = prefix.get(0);
            retVal[1] = i;
            retVal[2] = 0;

        } else if (i < 267992) {//55000+(13*128*128)
            i -= 55000;
            int t1 = i % 16384;
            int t2 = (i - t1) / 16384;
            //System.out.print(i+"\t"+t1+"\t"+t2+"\n");
            retVal[0] = prefix.get(t2 + 1);//128*128
            retVal[1] = t1 / 128;
            retVal[2] = t1 % 128;
        } else if (i <= 3290679) {//55000+267992+(128*1920*13)
            i -= 267992;
            int t1 = i % 245760;
            int t2 = (i - t1) / 245760;
            //System.out.print(i+"\t"+t1+"\t"+t2+"\n");
            retVal[0] = prefix.get(t2 + 1);//128*128
            retVal[1] = t1 / 1920;
            retVal[2] = 128 + t1 % 1920;
        }

        return retVal;
    }

    private static void intializePrefix() {
        prefix.add(0);
        prefix.add(1);
        prefix.add(7);
        prefix.add(14);
        prefix.add(15);
        prefix.add(16);
        prefix.add(17);
        prefix.add(18);
        prefix.add(19);
        prefix.add(20);
        prefix.add(21);
        prefix.add(22);
        prefix.add(23);
        prefix.add(25);
    }

  

    private static void WordDeCode(String dest2, String dest3) throws FileNotFoundException, IOException {

        File fin = new File(dest2);
        FileInputStream fis = new FileInputStream(fin);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis));
        OutputStreamWriter os1 = new OutputStreamWriter(new FileOutputStream(dest3));
        int value = in.read();

        while (value != -1) {

            if (!prefix.contains(value)) {
                //   System.out.println(" value " + value);
                os1.write(value);
            } else {
                int pre[]= new int[3];
                pre[0]=value;
                if(value==prefix.get(0)){
                pre[1]= in.read();
                
                
                }else{
                pre[1] = in.read();
                pre[2]= in.read();
                }
                int WordCod= getWodCod(pre);
                
                Custom_write(os1, Level_1.get(WordCod-2 ));//code-1
                // System.out.println("First = "+first+"second = "+second+"Word = "+Level_1[code-1]+" code = "+(code-1));
            }
            value = in.read();
        }
        os1.close();
        in.close();
        fis.close();

    }

  

    private static void WordCode(String dest, String dest2, OutputStreamWriter log) throws FileNotFoundException, IOException, ClassNotFoundException, SQLException {
        File fin = new File(dest);
        FileInputStream fis = new FileInputStream(fin);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis));

        OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(dest2));
        //os.write(maxEnc);
        String temp_string;
        int value = in.read();
        while ((value) != -1) {
            temp_string = "";
            boolean flag = false;
            while (((value >= 97) && (value <= 122)) || ((value >= 65) && (value <= 90)) || ((value >= 48) && (value <= 57))) {
                temp_string += (char) value;
                value = in.read();

            }
            if ((temp_string.length() > 1) && (temp_string.length() < 30)) {
                if (getID(temp_string, os) == true) {
                    flag = true;
                } else {
                    SymbolMiss++;
                    log.append(temp_string);
                    log.append('\n');
                }
            }
            if (flag == false) {
                for (int str_write = 0; str_write < temp_string.length(); str_write++) {
                    int currentchar = temp_string.charAt(str_write);
                    os.write(currentchar);
                    // System.out.println(currentchar);
                }
            }
            while (((value >= 0) && (value <= 64)) || ((value >= 91) && (value <= 96)) || (value >= 123)) {
                os.write(value);
                //  System.out.println(value);
                value = in.read();
            }
        }
        os.close();
    }

    public static boolean getID(String word, OutputStreamWriter os) throws IOException {
        boolean found = false;
        int index = getIdTree(word);

        if (index != 0) {
            int pre[] = getPrefix(index);//1 to n
        
            
            if (pre[0] ==0) {
                os.write(pre[0]);
                os.write(pre[1]);
            }else{
             os.write(pre[0]);
                os.write(pre[1]);
                os.write(pre[2]);
            }
            found = true;
            
            if (index < 44) {
                symbolHit1++;
            } else if (index < 659) {
                symbolHit2++;
            }else if (index < 4328) {
                symbolHit3++;
            } else {
                symbolHit4++;
            }
            // System.out.println(Level_1[i] + " - " + (i ));
        }

        return found;
    }

    private static Node traverseAndAdd(Node tmpNode, char tmpChar) {
        Node retNode = null;
        if (tmpChar == 'a') {
            if (tmpNode.Letter_a == null) {
                retNode = tmpNode.Letter_a = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_a;
            }

        } else if (tmpChar == 'b') {
            if (tmpNode.Letter_b == null) {
                retNode = tmpNode.Letter_b = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_b;
            }

        } else if (tmpChar == 'c') {
            if (tmpNode.Letter_c == null) {
                retNode = tmpNode.Letter_c = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_c;
            }

        } else if (tmpChar == 'd') {
            if (tmpNode.Letter_d == null) {
                retNode = tmpNode.Letter_d = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_d;
            }

        } else if (tmpChar == 'e') {
            if (tmpNode.Letter_e == null) {
                retNode = tmpNode.Letter_e = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_e;
            }

        } else if (tmpChar == 'f') {
            if (tmpNode.Letter_f == null) {
                retNode = tmpNode.Letter_f = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_f;
            }

        } else if (tmpChar == 'g') {
            if (tmpNode.Letter_g == null) {
                retNode = tmpNode.Letter_g = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_g;
            }

        } else if (tmpChar == 'h') {
            if (tmpNode.Letter_h == null) {
                retNode = tmpNode.Letter_h = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_h;
            }

        } else if (tmpChar == 'i') {
            if (tmpNode.Letter_i == null) {
                retNode = tmpNode.Letter_i = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_i;
            }

        } else if (tmpChar == 'j') {
            if (tmpNode.Letter_j == null) {
                retNode = tmpNode.Letter_j = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_j;
            }

        } else if (tmpChar == 'k') {
            if (tmpNode.Letter_k == null) {
                retNode = tmpNode.Letter_k = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_k;
            }

        } else if (tmpChar == 'l') {
            if (tmpNode.Letter_l == null) {
                retNode = tmpNode.Letter_l = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_l;
            }

        } else if (tmpChar == 'm') {
            if (tmpNode.Letter_m == null) {
                retNode = tmpNode.Letter_m = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_m;
            }

        } else if (tmpChar == 'n') {
            if (tmpNode.Letter_n == null) {
                retNode = tmpNode.Letter_n = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_n;
            }

        } else if (tmpChar == 'o') {
            if (tmpNode.Letter_o == null) {
                retNode = tmpNode.Letter_o = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_o;
            }

        } else if (tmpChar == 'p') {
            if (tmpNode.Letter_p == null) {
                retNode = tmpNode.Letter_p = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_p;
            }

        } else if (tmpChar == 'q') {
            if (tmpNode.Letter_q == null) {
                retNode = tmpNode.Letter_q = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_q;
            }

        } else if (tmpChar == 'r') {
            if (tmpNode.Letter_r == null) {
                retNode = tmpNode.Letter_r = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_r;
            }

        } else if (tmpChar == 's') {
            if (tmpNode.Letter_s == null) {
                retNode = tmpNode.Letter_s = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_s;
            }

        } else if (tmpChar == 't') {
            if (tmpNode.Letter_t == null) {
                retNode = tmpNode.Letter_t = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_t;
            }

        } else if (tmpChar == 'u') {
            if (tmpNode.Letter_u == null) {
                retNode = tmpNode.Letter_u = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_u;
            }

        } else if (tmpChar == 'v') {
            if (tmpNode.Letter_v == null) {
                retNode = tmpNode.Letter_v = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_v;
            }

        } else if (tmpChar == 'w') {
            if (tmpNode.Letter_w == null) {
                retNode = tmpNode.Letter_w = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_w;
            }

        } else if (tmpChar == 'x') {
            if (tmpNode.Letter_x == null) {
                retNode = tmpNode.Letter_x = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_x;
            }

        } else if (tmpChar == 'y') {
            if (tmpNode.Letter_y == null) {
                retNode = tmpNode.Letter_y = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_y;
            }

        } else if (tmpChar == 'z') {
            if (tmpNode.Letter_z == null) {
                retNode = tmpNode.Letter_z = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_z;
            }

        } else if (tmpChar == 'A') {
            if (tmpNode.Letter_A == null) {
                retNode = tmpNode.Letter_A = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_A;
            }

        } else if (tmpChar == 'B') {
            if (tmpNode.Letter_B == null) {
                retNode = tmpNode.Letter_B = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_B;
            }

        } else if (tmpChar == 'C') {
            if (tmpNode.Letter_C == null) {
                retNode = tmpNode.Letter_C = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_C;
            }

        } else if (tmpChar == 'D') {
            if (tmpNode.Letter_D == null) {
                retNode = tmpNode.Letter_D = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_D;
            }

        } else if (tmpChar == 'E') {
            if (tmpNode.Letter_E == null) {
                retNode = tmpNode.Letter_E = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_E;
            }

        } else if (tmpChar == 'F') {
            if (tmpNode.Letter_F == null) {
                retNode = tmpNode.Letter_F = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_F;
            }

        } else if (tmpChar == 'G') {
            if (tmpNode.Letter_G == null) {
                retNode = tmpNode.Letter_G = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_G;
            }

        } else if (tmpChar == 'H') {
            if (tmpNode.Letter_H == null) {
                retNode = tmpNode.Letter_H = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_H;
            }

        } else if (tmpChar == 'I') {
            if (tmpNode.Letter_I == null) {
                retNode = tmpNode.Letter_I = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_I;
            }

        } else if (tmpChar == 'J') {
            if (tmpNode.Letter_J == null) {
                retNode = tmpNode.Letter_J = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_J;
            }

        } else if (tmpChar == 'K') {
            if (tmpNode.Letter_K == null) {
                retNode = tmpNode.Letter_K = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_K;
            }

        } else if (tmpChar == 'L') {
            if (tmpNode.Letter_L == null) {
                retNode = tmpNode.Letter_L = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_L;
            }

        } else if (tmpChar == 'M') {
            if (tmpNode.Letter_M == null) {
                retNode = tmpNode.Letter_M = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_M;
            }

        } else if (tmpChar == 'N') {
            if (tmpNode.Letter_N == null) {
                retNode = tmpNode.Letter_N = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_N;
            }

        } else if (tmpChar == 'O') {
            if (tmpNode.Letter_O == null) {
                retNode = tmpNode.Letter_O = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_O;
            }

        } else if (tmpChar == 'P') {
            if (tmpNode.Letter_P == null) {
                retNode = tmpNode.Letter_P = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_P;
            }

        } else if (tmpChar == 'Q') {
            if (tmpNode.Letter_Q == null) {
                retNode = tmpNode.Letter_Q = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_Q;
            }

        } else if (tmpChar == 'R') {
            if (tmpNode.Letter_R == null) {
                retNode = tmpNode.Letter_R = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_R;
            }

        } else if (tmpChar == 'S') {
            if (tmpNode.Letter_S == null) {
                retNode = tmpNode.Letter_S = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_S;
            }

        } else if (tmpChar == 'T') {
            if (tmpNode.Letter_T == null) {
                retNode = tmpNode.Letter_T = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_T;
            }

        } else if (tmpChar == 'U') {
            if (tmpNode.Letter_U == null) {
                retNode = tmpNode.Letter_U = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_U;
            }

        } else if (tmpChar == 'V') {
            if (tmpNode.Letter_V == null) {
                retNode = tmpNode.Letter_V = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_V;
            }

        } else if (tmpChar == 'W') {
            if (tmpNode.Letter_W == null) {
                retNode = tmpNode.Letter_W = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_W;
            }

        } else if (tmpChar == 'X') {
            if (tmpNode.Letter_X == null) {
                retNode = tmpNode.Letter_X = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_X;
            }

        } else if (tmpChar == 'Y') {
            if (tmpNode.Letter_Y == null) {
                retNode = tmpNode.Letter_Y = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_Y;
            }

        } else if (tmpChar == 'Z') {
            if (tmpNode.Letter_Z == null) {
                retNode = tmpNode.Letter_Z = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_Z;
            }

        } else if (tmpChar == '0') {
            if (tmpNode.Letter_0 == null) {
                retNode = tmpNode.Letter_0 = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_0;
            }

        } else if (tmpChar == '1') {
            if (tmpNode.Letter_1 == null) {
                retNode = tmpNode.Letter_1 = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_1;
            }

        } else if (tmpChar == '2') {
            if (tmpNode.Letter_2 == null) {
                retNode = tmpNode.Letter_2 = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_2;
            }

        } else if (tmpChar == '3') {
            if (tmpNode.Letter_3 == null) {
                retNode = tmpNode.Letter_3 = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_3;
            }

        } else if (tmpChar == '4') {
            if (tmpNode.Letter_4 == null) {
                retNode = tmpNode.Letter_4 = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_4;
            }

        } else if (tmpChar == '5') {
            if (tmpNode.Letter_5 == null) {
                retNode = tmpNode.Letter_5 = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_5;
            }

        } else if (tmpChar == '6') {
            if (tmpNode.Letter_6 == null) {
                retNode = tmpNode.Letter_6 = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_6;
            }

        } else if (tmpChar == '7') {
            if (tmpNode.Letter_7 == null) {
                retNode = tmpNode.Letter_7 = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_7;
            }

        } else if (tmpChar == '8') {
            if (tmpNode.Letter_8 == null) {
                retNode = tmpNode.Letter_8 = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_8;
            }

        } else if (tmpChar == '9') {
            if (tmpNode.Letter_9 == null) {
                retNode = tmpNode.Letter_9 = new Node();
                charSiz++;
            } else {
                retNode = tmpNode.Letter_9;
            }

        }
        return retNode;
    }

    private static void traverseAndAdd(Node tmpNode, char tmpChar, int i) {

        if (tmpChar == 'a') {
            if (tmpNode.Letter_a == null) {
                tmpNode.Letter_a = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_a.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'b') {
            if (tmpNode.Letter_b == null) {
                tmpNode.Letter_b = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_b.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'c') {
            if (tmpNode.Letter_c == null) {
                tmpNode.Letter_c = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_c.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'd') {
            if (tmpNode.Letter_d == null) {
                tmpNode.Letter_d = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_d.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'e') {
            if (tmpNode.Letter_e == null) {
                tmpNode.Letter_e = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_e.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'f') {
            if (tmpNode.Letter_f == null) {
                tmpNode.Letter_f = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_f.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'g') {
            if (tmpNode.Letter_g == null) {
                tmpNode.Letter_g = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_g.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'h') {
            if (tmpNode.Letter_h == null) {
                tmpNode.Letter_h = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_h.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'i') {
            if (tmpNode.Letter_i == null) {
                tmpNode.Letter_i = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_i.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'j') {
            if (tmpNode.Letter_j == null) {
                tmpNode.Letter_j = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_j.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'k') {
            if (tmpNode.Letter_k == null) {
                tmpNode.Letter_k = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_k.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'l') {
            if (tmpNode.Letter_l == null) {
                tmpNode.Letter_l = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_l.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'm') {
            if (tmpNode.Letter_m == null) {
                tmpNode.Letter_m = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_m.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'n') {
            if (tmpNode.Letter_n == null) {
                tmpNode.Letter_n = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_n.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'o') {
            if (tmpNode.Letter_o == null) {
                tmpNode.Letter_o = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_o.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'p') {
            if (tmpNode.Letter_p == null) {
                tmpNode.Letter_p = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_p.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'q') {
            if (tmpNode.Letter_q == null) {
                tmpNode.Letter_q = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_q.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'r') {
            if (tmpNode.Letter_r == null) {
                tmpNode.Letter_r = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_r.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 's') {
            if (tmpNode.Letter_s == null) {
                tmpNode.Letter_s = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_s.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 't') {
            if (tmpNode.Letter_t == null) {
                tmpNode.Letter_t = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_t.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'u') {
            if (tmpNode.Letter_u == null) {
                tmpNode.Letter_u = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_u.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'v') {
            if (tmpNode.Letter_v == null) {
                tmpNode.Letter_v = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_v.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'w') {
            if (tmpNode.Letter_w == null) {
                tmpNode.Letter_w = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_w.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'x') {
            if (tmpNode.Letter_x == null) {
                tmpNode.Letter_x = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_x.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'y') {
            if (tmpNode.Letter_y == null) {
                tmpNode.Letter_y = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_y.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'z') {
            if (tmpNode.Letter_z == null) {
                tmpNode.Letter_z = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_z.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'A') {
            if (tmpNode.Letter_A == null) {
                tmpNode.Letter_A = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_A.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'B') {
            if (tmpNode.Letter_B == null) {
                tmpNode.Letter_B = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_B.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'C') {
            if (tmpNode.Letter_C == null) {
                tmpNode.Letter_C = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_C.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'D') {
            if (tmpNode.Letter_D == null) {
                tmpNode.Letter_D = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_D.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'E') {
            if (tmpNode.Letter_E == null) {
                tmpNode.Letter_E = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_E.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'F') {
            if (tmpNode.Letter_F == null) {
                tmpNode.Letter_F = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_F.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'G') {
            if (tmpNode.Letter_G == null) {
                tmpNode.Letter_G = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_G.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'H') {
            if (tmpNode.Letter_H == null) {
                tmpNode.Letter_H = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_H.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'I') {
            if (tmpNode.Letter_I == null) {
                tmpNode.Letter_I = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_I.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'J') {
            if (tmpNode.Letter_J == null) {
                tmpNode.Letter_J = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_J.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'K') {
            if (tmpNode.Letter_K == null) {
                tmpNode.Letter_K = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_K.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'L') {
            if (tmpNode.Letter_L == null) {
                tmpNode.Letter_L = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_L.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'M') {
            if (tmpNode.Letter_M == null) {
                tmpNode.Letter_M = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_M.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'N') {
            if (tmpNode.Letter_N == null) {
                tmpNode.Letter_N = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_N.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'O') {
            if (tmpNode.Letter_O == null) {
                tmpNode.Letter_O = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_O.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'P') {
            if (tmpNode.Letter_P == null) {
                tmpNode.Letter_P = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_P.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'Q') {
            if (tmpNode.Letter_Q == null) {
                tmpNode.Letter_Q = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_Q.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'R') {
            if (tmpNode.Letter_R == null) {
                tmpNode.Letter_R = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_R.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'S') {
            if (tmpNode.Letter_S == null) {
                tmpNode.Letter_S = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_S.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'T') {
            if (tmpNode.Letter_T == null) {
                tmpNode.Letter_T = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_T.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'U') {
            if (tmpNode.Letter_U == null) {
                tmpNode.Letter_U = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_U.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'V') {
            if (tmpNode.Letter_V == null) {
                tmpNode.Letter_V = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_V.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'W') {
            if (tmpNode.Letter_W == null) {
                tmpNode.Letter_W = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_W.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'X') {
            if (tmpNode.Letter_X == null) {
                tmpNode.Letter_X = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_X.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'Y') {
            if (tmpNode.Letter_Y == null) {
                tmpNode.Letter_Y = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_Y.setData(i);
                intSiz++;
            }

        } else if (tmpChar == 'Z') {
            if (tmpNode.Letter_Z == null) {
                tmpNode.Letter_Z = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_Z.setData(i);
                intSiz++;
            }

        } else if (tmpChar == '0') {
            if (tmpNode.Letter_0 == null) {
                tmpNode.Letter_0 = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_0.setData(i);
                intSiz++;
            }

        } else if (tmpChar == '1') {
            if (tmpNode.Letter_1 == null) {
                tmpNode.Letter_1 = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_1.setData(i);
                intSiz++;
            }

        } else if (tmpChar == '2') {
            if (tmpNode.Letter_2 == null) {
                tmpNode.Letter_2 = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_2.setData(i);
                intSiz++;
            }

        } else if (tmpChar == '3') {
            if (tmpNode.Letter_3 == null) {
                tmpNode.Letter_3 = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_3.setData(i);
                intSiz++;
            }

        } else if (tmpChar == '4') {
            if (tmpNode.Letter_4 == null) {
                tmpNode.Letter_4 = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_4.setData(i);
                intSiz++;
            }

        } else if (tmpChar == '5') {
            if (tmpNode.Letter_5 == null) {
                tmpNode.Letter_5 = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_5.setData(i);
                intSiz++;
            }

        } else if (tmpChar == '6') {
            if (tmpNode.Letter_6 == null) {
                tmpNode.Letter_6 = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_6.setData(i);
                intSiz++;
            }

        } else if (tmpChar == '7') {
            if (tmpNode.Letter_7 == null) {
                tmpNode.Letter_7 = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_7.setData(i);
                intSiz++;
            }

        } else if (tmpChar == '8') {
            if (tmpNode.Letter_8 == null) {
                tmpNode.Letter_8 = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_8.setData(i);
                intSiz++;
            }

        } else if (tmpChar == '9') {
            if (tmpNode.Letter_9 == null) {
                tmpNode.Letter_9 = new Node(i);
                charSiz++;
            } else {
                tmpNode.Letter_9.setData(i);
                intSiz++;
            }

        }

    }

    private static void add(String Word) {
        Level_1.add(Word);
        Node tmpNode = root;
        for (int i = 0; i < Word.length() - 1; i++) {
            //System.out.println(Word.charAt(i));
            tmpNode = traverseAndAdd(tmpNode, Word.charAt(i));
        }
        // System.out.println(Word.charAt(Word.length() - 1));
        traverseAndAdd(tmpNode, Word.charAt(Word.length() - 1), id++);
    }

    private static int getIdTree(String Word) {
        Node tmpNode = root;
        int retID = 0;
        for (int i = 0; ((i < Word.length() - 1) && (tmpNode != null)); i++) {
            // System.out.println(Word.charAt(i));
            tmpNode = traverseAndFind(tmpNode, Word.charAt(i));
        }
        if (tmpNode != null) {
            retID = traverseAndFindID(tmpNode, Word.charAt(Word.length() - 1));
        }
        return retID;
    }

    private static void loadMemory() throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {
        File fin = new File("/Users/Sakthi/Research/WordCode/DB/2letterW.txt");
        int[] i = new int[55];
        for (int a = 0; a < 55; a++) {
            i[a] = 0;
        }
        FileInputStream fis = new FileInputStream(fin);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis));
        String line;
        while ((line = in.readLine()) != null) {
            i[line.length()]++;
            add(line);

        }
        fin = new File("/Users/Sakthi/Research/WordCode/DB/3letterW.txt");

        fis = new FileInputStream(fin);
        in = new BufferedReader(new InputStreamReader(fis));
        while ((line = in.readLine()) != null) {
            i[line.length()]++;
            add(line);
        }
        fin = new File("/Users/Sakthi/Research/WordCode/DB/4letterW.txt");

        fis = new FileInputStream(fin);
        in = new BufferedReader(new InputStreamReader(fis));
        while ((line = in.readLine()) != null) {
            i[line.length()]++;
            add(line);
        }
        fin = new File("/Users/Sakthi/Research/WordCode/DB/5letterW.txt");

        fis = new FileInputStream(fin);
        in = new BufferedReader(new InputStreamReader(fis));
        while ((line = in.readLine()) != null) {
            i[line.length()]++;
            add(line);
//            if (line.length() == 14) {
//                System.out.println(line);
//            }
        }
        int k = 0;
        for (int a = 0; a < 55; a++) {
            System.out.println(a + " - " + i[a]);
            k += i[a];
        }
        System.out.println("Total - " + k);
    }

    private static Node traverseAndFind(Node tmpNode, char tmpChar) {

        Node retNode = null;
        if (tmpChar == 'a') {
            if (tmpNode.Letter_a != null) {
                return (tmpNode.Letter_a);
            }

        } else if (tmpChar == 'b') {
            if (tmpNode.Letter_b != null) {
                return (tmpNode.Letter_b);
            }

        } else if (tmpChar == 'c') {
            if (tmpNode.Letter_c != null) {
                return (tmpNode.Letter_c);
            }

        } else if (tmpChar == 'd') {
            if (tmpNode.Letter_d != null) {
                return (tmpNode.Letter_d);
            }

        } else if (tmpChar == 'e') {
            if (tmpNode.Letter_e != null) {
                return (tmpNode.Letter_e);
            }

        } else if (tmpChar == 'f') {
            if (tmpNode.Letter_f != null) {
                return (tmpNode.Letter_f);
            }

        } else if (tmpChar == 'g') {
            if (tmpNode.Letter_g != null) {
                return (tmpNode.Letter_g);
            }

        } else if (tmpChar == 'h') {
            if (tmpNode.Letter_h != null) {
                return (tmpNode.Letter_h);
            }

        } else if (tmpChar == 'i') {
            if (tmpNode.Letter_i != null) {
                return (tmpNode.Letter_i);
            }

        } else if (tmpChar == 'j') {
            if (tmpNode.Letter_j != null) {
                return (tmpNode.Letter_j);
            }

        } else if (tmpChar == 'k') {
            if (tmpNode.Letter_k != null) {
                return (tmpNode.Letter_k);
            }

        } else if (tmpChar == 'l') {
            if (tmpNode.Letter_l != null) {
                return (tmpNode.Letter_l);
            }

        } else if (tmpChar == 'm') {
            if (tmpNode.Letter_m != null) {
                return (tmpNode.Letter_m);
            }

        } else if (tmpChar == 'n') {
            if (tmpNode.Letter_n != null) {
                return (tmpNode.Letter_n);
            }

        } else if (tmpChar == 'o') {
            if (tmpNode.Letter_o != null) {
                return (tmpNode.Letter_o);
            }

        } else if (tmpChar == 'p') {
            if (tmpNode.Letter_p != null) {
                return (tmpNode.Letter_p);
            }

        } else if (tmpChar == 'q') {
            if (tmpNode.Letter_q != null) {
                return (tmpNode.Letter_q);
            }

        } else if (tmpChar == 'r') {
            if (tmpNode.Letter_r != null) {
                return (tmpNode.Letter_r);
            }

        } else if (tmpChar == 's') {
            if (tmpNode.Letter_s != null) {
                return (tmpNode.Letter_s);
            }

        } else if (tmpChar == 't') {
            if (tmpNode.Letter_t != null) {
                return (tmpNode.Letter_t);
            }

        } else if (tmpChar == 'u') {
            if (tmpNode.Letter_u != null) {
                return (tmpNode.Letter_u);
            }

        } else if (tmpChar == 'v') {
            if (tmpNode.Letter_v != null) {
                return (tmpNode.Letter_v);
            }

        } else if (tmpChar == 'w') {
            if (tmpNode.Letter_w != null) {
                return (tmpNode.Letter_w);
            }

        } else if (tmpChar == 'x') {
            if (tmpNode.Letter_x != null) {
                return (tmpNode.Letter_x);
            }

        } else if (tmpChar == 'y') {
            if (tmpNode.Letter_y != null) {
                return (tmpNode.Letter_y);
            }

        } else if (tmpChar == 'z') {
            if (tmpNode.Letter_z != null) {
                return (tmpNode.Letter_z);
            }

        } else if (tmpChar == 'A') {
            if (tmpNode.Letter_A != null) {
                return (tmpNode.Letter_A);
            }

        } else if (tmpChar == 'B') {
            if (tmpNode.Letter_B != null) {
                return (tmpNode.Letter_B);
            }

        } else if (tmpChar == 'C') {
            if (tmpNode.Letter_C != null) {
                return (tmpNode.Letter_C);
            }

        } else if (tmpChar == 'D') {
            if (tmpNode.Letter_D != null) {
                return (tmpNode.Letter_D);
            }

        } else if (tmpChar == 'E') {
            if (tmpNode.Letter_E != null) {
                return (tmpNode.Letter_E);
            }

        } else if (tmpChar == 'F') {
            if (tmpNode.Letter_F != null) {
                return (tmpNode.Letter_F);
            }

        } else if (tmpChar == 'G') {
            if (tmpNode.Letter_G != null) {
                return (tmpNode.Letter_G);
            }

        } else if (tmpChar == 'H') {
            if (tmpNode.Letter_H != null) {
                return (tmpNode.Letter_H);
            }

        } else if (tmpChar == 'I') {
            if (tmpNode.Letter_I != null) {
                return (tmpNode.Letter_I);
            }

        } else if (tmpChar == 'J') {
            if (tmpNode.Letter_J != null) {
                return (tmpNode.Letter_J);
            }

        } else if (tmpChar == 'K') {
            if (tmpNode.Letter_K != null) {
                return (tmpNode.Letter_K);
            }

        } else if (tmpChar == 'L') {
            if (tmpNode.Letter_L != null) {
                return (tmpNode.Letter_L);
            }

        } else if (tmpChar == 'M') {
            if (tmpNode.Letter_M != null) {
                return (tmpNode.Letter_M);
            }

        } else if (tmpChar == 'N') {
            if (tmpNode.Letter_N != null) {
                return (tmpNode.Letter_N);
            }

        } else if (tmpChar == 'O') {
            if (tmpNode.Letter_O != null) {
                return (tmpNode.Letter_O);
            }

        } else if (tmpChar == 'P') {
            if (tmpNode.Letter_P != null) {
                return (tmpNode.Letter_P);
            }

        } else if (tmpChar == 'Q') {
            if (tmpNode.Letter_Q != null) {
                return (tmpNode.Letter_Q);
            }

        } else if (tmpChar == 'R') {
            if (tmpNode.Letter_R != null) {
                return (tmpNode.Letter_R);
            }

        } else if (tmpChar == 'S') {
            if (tmpNode.Letter_S != null) {
                return (tmpNode.Letter_S);
            }

        } else if (tmpChar == 'T') {
            if (tmpNode.Letter_T != null) {
                return (tmpNode.Letter_T);
            }

        } else if (tmpChar == 'U') {
            if (tmpNode.Letter_U != null) {
                return (tmpNode.Letter_U);
            }

        } else if (tmpChar == 'V') {
            if (tmpNode.Letter_V != null) {
                return (tmpNode.Letter_V);
            }

        } else if (tmpChar == 'W') {
            if (tmpNode.Letter_W != null) {
                return (tmpNode.Letter_W);
            }

        } else if (tmpChar == 'X') {
            if (tmpNode.Letter_X != null) {
                return (tmpNode.Letter_X);
            }

        } else if (tmpChar == 'Y') {
            if (tmpNode.Letter_Y != null) {
                return (tmpNode.Letter_Y);
            }

        } else if (tmpChar == 'Z') {
            if (tmpNode.Letter_Z != null) {
                return (tmpNode.Letter_Z);
            }

        } else if (tmpChar == '0') {
            if (tmpNode.Letter_0 != null) {
                return (tmpNode.Letter_0);
            }

        } else if (tmpChar == '1') {
            if (tmpNode.Letter_1 != null) {
                return (tmpNode.Letter_1);
            }

        } else if (tmpChar == '2') {
            if (tmpNode.Letter_2 != null) {
                return (tmpNode.Letter_2);
            }

        } else if (tmpChar == '3') {
            if (tmpNode.Letter_3 != null) {
                return (tmpNode.Letter_3);
            }

        } else if (tmpChar == '4') {
            if (tmpNode.Letter_4 != null) {
                return (tmpNode.Letter_4);
            }

        } else if (tmpChar == '5') {
            if (tmpNode.Letter_5 != null) {
                return (tmpNode.Letter_5);
            }

        } else if (tmpChar == '6') {
            if (tmpNode.Letter_6 != null) {
                return (tmpNode.Letter_6);
            }

        } else if (tmpChar == '7') {
            if (tmpNode.Letter_7 != null) {
                return (tmpNode.Letter_7);
            }

        } else if (tmpChar == '8') {
            if (tmpNode.Letter_8 != null) {
                return (tmpNode.Letter_8);
            }

        } else if (tmpChar == '9') {
            if (tmpNode.Letter_9 != null) {
                return (tmpNode.Letter_9);
            }

        }
        return retNode;
    }

    private static int traverseAndFindID(Node tmpNode, char tmpChar) {
        //int retVal=0;
        if (tmpChar == 'a') {
            if (tmpNode.Letter_a != null) {
                return (tmpNode.Letter_a.getData());
            }

        } else if (tmpChar == 'b') {
            if (tmpNode.Letter_b != null) {
                return (tmpNode.Letter_b.getData());
            }

        } else if (tmpChar == 'c') {
            if (tmpNode.Letter_c != null) {
                return (tmpNode.Letter_c.getData());
            }

        } else if (tmpChar == 'd') {
            if (tmpNode.Letter_d != null) {
                return (tmpNode.Letter_d.getData());
            }

        } else if (tmpChar == 'e') {
            if (tmpNode.Letter_e != null) {
                return (tmpNode.Letter_e.getData());
            }

        } else if (tmpChar == 'f') {
            if (tmpNode.Letter_f != null) {
                return (tmpNode.Letter_f.getData());
            }

        } else if (tmpChar == 'g') {
            if (tmpNode.Letter_g != null) {
                return (tmpNode.Letter_g.getData());
            }

        } else if (tmpChar == 'h') {
            if (tmpNode.Letter_h != null) {
                return (tmpNode.Letter_h.getData());
            }

        } else if (tmpChar == 'i') {
            if (tmpNode.Letter_i != null) {
                return (tmpNode.Letter_i.getData());
            }

        } else if (tmpChar == 'j') {
            if (tmpNode.Letter_j != null) {
                return (tmpNode.Letter_j.getData());
            }

        } else if (tmpChar == 'k') {
            if (tmpNode.Letter_k != null) {
                return (tmpNode.Letter_k.getData());
            }

        } else if (tmpChar == 'l') {
            if (tmpNode.Letter_l != null) {
                return (tmpNode.Letter_l.getData());
            }

        } else if (tmpChar == 'm') {
            if (tmpNode.Letter_m != null) {
                return (tmpNode.Letter_m.getData());
            }

        } else if (tmpChar == 'n') {
            if (tmpNode.Letter_n != null) {
                return (tmpNode.Letter_n.getData());
            }

        } else if (tmpChar == 'o') {
            if (tmpNode.Letter_o != null) {
                return (tmpNode.Letter_o.getData());
            }

        } else if (tmpChar == 'p') {
            if (tmpNode.Letter_p != null) {
                return (tmpNode.Letter_p.getData());
            }

        } else if (tmpChar == 'q') {
            if (tmpNode.Letter_q != null) {
                return (tmpNode.Letter_q.getData());
            }

        } else if (tmpChar == 'r') {
            if (tmpNode.Letter_r != null) {
                return (tmpNode.Letter_r.getData());
            }

        } else if (tmpChar == 's') {
            if (tmpNode.Letter_s != null) {
                return (tmpNode.Letter_s.getData());
            }

        } else if (tmpChar == 't') {
            if (tmpNode.Letter_t != null) {
                return (tmpNode.Letter_t.getData());
            }

        } else if (tmpChar == 'u') {
            if (tmpNode.Letter_u != null) {
                return (tmpNode.Letter_u.getData());
            }

        } else if (tmpChar == 'v') {
            if (tmpNode.Letter_v != null) {
                return (tmpNode.Letter_v.getData());
            }

        } else if (tmpChar == 'w') {
            if (tmpNode.Letter_w != null) {
                return (tmpNode.Letter_w.getData());
            }

        } else if (tmpChar == 'x') {
            if (tmpNode.Letter_x != null) {
                return (tmpNode.Letter_x.getData());
            }

        } else if (tmpChar == 'y') {
            if (tmpNode.Letter_y != null) {
                return (tmpNode.Letter_y.getData());
            }

        } else if (tmpChar == 'z') {
            if (tmpNode.Letter_z != null) {
                return (tmpNode.Letter_z.getData());
            }

        } else if (tmpChar == 'A') {
            if (tmpNode.Letter_A != null) {
                return (tmpNode.Letter_A.getData());
            }

        } else if (tmpChar == 'B') {
            if (tmpNode.Letter_B != null) {
                return (tmpNode.Letter_B.getData());
            }

        } else if (tmpChar == 'C') {
            if (tmpNode.Letter_C != null) {
                return (tmpNode.Letter_C.getData());
            }

        } else if (tmpChar == 'D') {
            if (tmpNode.Letter_D != null) {
                return (tmpNode.Letter_D.getData());
            }

        } else if (tmpChar == 'E') {
            if (tmpNode.Letter_E != null) {
                return (tmpNode.Letter_E.getData());
            }

        } else if (tmpChar == 'F') {
            if (tmpNode.Letter_F != null) {
                return (tmpNode.Letter_F.getData());
            }

        } else if (tmpChar == 'G') {
            if (tmpNode.Letter_G != null) {
                return (tmpNode.Letter_G.getData());
            }

        } else if (tmpChar == 'H') {
            if (tmpNode.Letter_H != null) {
                return (tmpNode.Letter_H.getData());
            }

        } else if (tmpChar == 'I') {
            if (tmpNode.Letter_I != null) {
                return (tmpNode.Letter_I.getData());
            }

        } else if (tmpChar == 'J') {
            if (tmpNode.Letter_J != null) {
                return (tmpNode.Letter_J.getData());
            }

        } else if (tmpChar == 'K') {
            if (tmpNode.Letter_K != null) {
                return (tmpNode.Letter_K.getData());
            }

        } else if (tmpChar == 'L') {
            if (tmpNode.Letter_L != null) {
                return (tmpNode.Letter_L.getData());
            }

        } else if (tmpChar == 'M') {
            if (tmpNode.Letter_M != null) {
                return (tmpNode.Letter_M.getData());
            }

        } else if (tmpChar == 'N') {
            if (tmpNode.Letter_N != null) {
                return (tmpNode.Letter_N.getData());
            }

        } else if (tmpChar == 'O') {
            if (tmpNode.Letter_O != null) {
                return (tmpNode.Letter_O.getData());
            }

        } else if (tmpChar == 'P') {
            if (tmpNode.Letter_P != null) {
                return (tmpNode.Letter_P.getData());
            }

        } else if (tmpChar == 'Q') {
            if (tmpNode.Letter_Q != null) {
                return (tmpNode.Letter_Q.getData());
            }

        } else if (tmpChar == 'R') {
            if (tmpNode.Letter_R != null) {
                return (tmpNode.Letter_R.getData());
            }

        } else if (tmpChar == 'S') {
            if (tmpNode.Letter_S != null) {
                return (tmpNode.Letter_S.getData());
            }

        } else if (tmpChar == 'T') {
            if (tmpNode.Letter_T != null) {
                return (tmpNode.Letter_T.getData());
            }

        } else if (tmpChar == 'U') {
            if (tmpNode.Letter_U != null) {
                return (tmpNode.Letter_U.getData());
            }

        } else if (tmpChar == 'V') {
            if (tmpNode.Letter_V != null) {
                return (tmpNode.Letter_V.getData());
            }

        } else if (tmpChar == 'W') {
            if (tmpNode.Letter_W != null) {
                return (tmpNode.Letter_W.getData());
            }

        } else if (tmpChar == 'X') {
            if (tmpNode.Letter_X != null) {
                return (tmpNode.Letter_X.getData());
            }

        } else if (tmpChar == 'Y') {
            if (tmpNode.Letter_Y != null) {
                return (tmpNode.Letter_Y.getData());
            }

        } else if (tmpChar == 'Z') {
            if (tmpNode.Letter_Z != null) {
                return (tmpNode.Letter_Z.getData());
            }

        } else if (tmpChar == '0') {
            if (tmpNode.Letter_0 != null) {
                return (tmpNode.Letter_0.getData());
            }

        } else if (tmpChar == '1') {
            if (tmpNode.Letter_1 != null) {
                return (tmpNode.Letter_1.getData());
            }

        } else if (tmpChar == '2') {
            if (tmpNode.Letter_2 != null) {
                return (tmpNode.Letter_2.getData());
            }

        } else if (tmpChar == '3') {
            if (tmpNode.Letter_3 != null) {
                return (tmpNode.Letter_3.getData());
            }

        } else if (tmpChar == '4') {
            if (tmpNode.Letter_4 != null) {
                return (tmpNode.Letter_4.getData());
            }

        } else if (tmpChar == '5') {
            if (tmpNode.Letter_5 != null) {
                return (tmpNode.Letter_5.getData());
            }

        } else if (tmpChar == '6') {
            if (tmpNode.Letter_6 != null) {
                return (tmpNode.Letter_6.getData());
            }

        } else if (tmpChar == '7') {
            if (tmpNode.Letter_7 != null) {
                return (tmpNode.Letter_7.getData());
            }

        } else if (tmpChar == '8') {
            if (tmpNode.Letter_8 != null) {
                return (tmpNode.Letter_8.getData());
            }

        } else if (tmpChar == '9') {
            if (tmpNode.Letter_9 != null) {
                return (tmpNode.Letter_9.getData());
            }

        }
        return 0;
    }

    private static void Custom_write(OutputStreamWriter os, String temp_string2) throws IOException {
        for (int i = 0; i < temp_string2.length(); i++) {
            os.write(temp_string2.charAt(i));
        }
    }

    private static boolean checkValidity(String source_filepath, String dest2) throws FileNotFoundException, IOException {
        File fin = new File(dest2);
        boolean valid = true;
        FileInputStream fis = new FileInputStream(fin);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis));
        File fin1 = new File(source_filepath);
        FileInputStream fis1 = new FileInputStream(fin1);
        BufferedReader in1 = new BufferedReader(new InputStreamReader(fis1));
        int value, value1;
        while ((value = in.read()) != -1) {
            value1 = in1.read();
            if (value != value1) {
                //  System.out.println(value1 + " wrongly printed as " + value + " " + (char) value1 + " " + (char) value);
                valid = false;
                break;
            }
        }
        return valid;
    }

    public static long getGzipFileSize(String filename, String destinaton_zip_filepath) throws FileNotFoundException, IOException {

        byte[] buffer = new byte[1024];

        FileOutputStream fileOutputStream = new FileOutputStream(destinaton_zip_filepath);

        GZIPOutputStream gzipOuputStream = new GZIPOutputStream(fileOutputStream);

        FileInputStream fileInput = new FileInputStream(filename);

        int bytes_read;

        while ((bytes_read = fileInput.read(buffer)) > 0) {
            gzipOuputStream.write(buffer, 0, bytes_read);
        }

        fileInput.close();

        gzipOuputStream.finish();
        gzipOuputStream.close();

        //  System.out.println("The file was compressed successfully!");
        File file = new File(destinaton_zip_filepath);
        if (!file.exists() || !file.isFile()) {
            System.out.println("File doesn\'t exist");
            return -1;
        }

        return file.length();
    }
}
