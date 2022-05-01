package com.voyager.commuinty.t2;

import java.util.Scanner;

/**
 * @author Voyager1
 * @create 2022-04-26 15:43
 */
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.next();
        int[] num = {1,2,3};
        System.out.println(num.length);
        System.out.println("*******");
        System.out.println(s.length());
        int n = maxLengthBetweenEqualCharacters(s);
        System.out.println(n);
    }
    public static int maxLengthBetweenEqualCharacters(String s) {
        int maxLen = s.length();
        while(maxLen > 0){
            for(int i = 0; i < s.length() - maxLen; i++){
                if(s.charAt(i) == s.charAt(maxLen + i))
                    return maxLen - 1;
            }
            maxLen--;
        }
        return -1;
    }
}
