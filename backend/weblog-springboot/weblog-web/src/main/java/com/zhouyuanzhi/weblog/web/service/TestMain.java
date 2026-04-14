package com.zhouyuanzhi.weblog.web.service;

import java.util.Arrays;

public class TestMain {
    public static void main(String[] args) {
        int[] nums = new int[]{2,4,1,5,8,4};
        int k= 4;
        Arrays.sort(nums);
        int l=0,r=nums.length-1;

        while(l<=r){
            int mid = (l+r)/2;
            if(nums[mid]>k){
                r=mid;
            }else if (nums[mid]<=k){
                l=mid+1;
            }
        }
    }
}
