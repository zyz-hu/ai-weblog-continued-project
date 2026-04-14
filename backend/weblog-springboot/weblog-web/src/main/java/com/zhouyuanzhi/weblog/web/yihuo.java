package com.zhouyuanzhi.weblog.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class yihuo {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

         int i = sc.nextInt();

         while((i--)>0) {
             int x = sc.nextInt();
             int a =0,b =0;
             boolean fa = false;
             List<Integer> path  = new ArrayList<>();
             while( x > 0) {
                 int ab = x % 2 ;
                 path.add(ab);
                 if(!fa && ab == 1) {
                     fa = true;
                     a = path.size()-1;
                 }

                 if(ab == 1) {
                     b =path.size()-1;
                 }
                 x = x>>1;
             }

             if(path.isEmpty()) {
                 System.out.println(true);
                 continue;
             }

             if (a!=b) {
                 int t = a+1;
                 for(;t<b;t++) {
                     if(t < (b-t+a) && Objects.equals(path.get(t), path.get(b - (t - a)))) {
                         continue;
                     }
                     break;
                 }
                 if((t > (b-t+a)) || (t==(b-t+a) && path.get(t)==0))
                 {
                     System.out.println(true);
                     continue;
                 }
             }
             System.out.println(false);
         }
    }
}
