package cn.hangman.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Random;

/**
 * 通用方法类
 *
 */
public class Common {
	/**
	 * 判断一个字符串是否是数字组成的
	 * @param s
	 * @return true for s is one number ,else not a number
	 */
	public static boolean isnumber(String s) {

		try {
			if (s == null)
				return false;
			int result = Integer.parseInt(s);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
    /** 
     * 获得一个新的单词
     */  
    public static String getWord() {   
        File file = new File("source/words.txt");//文件路径  
        FileReader fileReader = null;
        String txt = "";
        LineNumberReader reader = null;
		try {
			fileReader = new FileReader(file);
			reader = new LineNumberReader(fileReader); 
			Random ra =new Random();
			int number = ra.nextInt(51500)+1;//随机行数
			int lines = 0;  
			while (txt != null) {  
				lines++;  
				txt = reader.readLine();
				if (lines == number) {  
					System.out.println("From user\n" + reader.getLineNumber() + "Answer is" + txt + "\n");   
					break; 
				}  
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				reader.close();
				fileReader.close(); 
			} catch (IOException e) {
				e.printStackTrace();
			}  
		}
		return txt;
    }  
    /**
     * 比较对错
     * @param word
     * @param guess
     * @param result
     * @return
     */
    public static char[] compare(String word,String guess,char[] result){
		char[] wordChar = word.toCharArray();
		char[] guessChar = guess.toCharArray();
		for(int i=0;i<guessChar.length;i++){
			for(int j=0;j<wordChar.length;j++){
				if(guessChar[i]==wordChar[j]){
					result[j]=guessChar[i];
				}
			}
		}
		return result;
	}
}
