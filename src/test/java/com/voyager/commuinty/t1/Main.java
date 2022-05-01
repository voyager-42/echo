package com.voyager.commuinty.t1;

import java.util.Arrays;
import java.util.Scanner;

/**
 * @author Voyager1
 * @create 2022-04-24 15:09
 */
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] s = sc.nextLine().split(" ");
        int[] power = new int[s.length];
        for(int i = 0; i < s.length; i++){
            power[i] = Integer.parseInt(s[i]);
        }
        boolean res = damage(power);
        System.out.println(res);
    }
    public static boolean damage(int[] power) {
        int len = power.length;
        if(power == null || power.length == 0){
            return false;
        }
        Arrays.sort(power);
        int sum = 0;
        for(int num:power){
            sum += num;
        }
        if(sum == 25 || sum == 14 ||sum == 11 || sum == 10) {
            return true;
        }
        return false;
    }
}
