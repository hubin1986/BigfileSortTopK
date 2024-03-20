package org.woven.sorter;

import org.woven.tools.DataPair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: hubin
 * @date: 2024/1/20 5:31
 * @description: implement by quick sort
 */
public class TopKQuickSorter {

    public List<DataPair> findTopK(List<DataPair> nums, int k) {
        if(nums == null || nums.isEmpty()){
            return nums;
        }
        return helper(nums, 0, nums.size()-1, nums.size()-k);
    }

    public List<Long> findTopKIds(List<DataPair> nums, int k) {
        List<DataPair> list = findTopK(nums,k);
        List<Long> result = new ArrayList<>();
        for(DataPair pair : list){
            result.add(pair.getId());
        }
        return result;
    }

    public List<Long> findTopKValues(List<DataPair> nums, int k) {
        List<DataPair> list = findTopK(nums,k);
        List<Long> result = new ArrayList<>();
        for(DataPair pair : list){
            result.add(pair.getValue());
        }
        return result;
    }

    public List<DataPair> helper(List<DataPair> nums, int left, int right,int k){
        int l = left;
        int r = right;
        DataPair target = nums.get(l);
        while(l<r){
            while(l<r && target.getValue()<= nums.get(r).getValue()){
                r--;
            }
            if(l<r){
                //nums[l] = nums[r];
                nums.set(l,nums.get(r));
                l++;
            }

            while(l<r && target.getValue()>nums.get(l).getValue()){
                l++;
            }
            if(l<r){
                //nums[r] = nums[l];
                nums.set(r,nums.get(l));
                r--;
            }
        }
        //nums[l] = target;
        nums.set(l,target);
        if (l < k)
            return helper(nums, l+1, right, k);
        if (l > k)
            return helper(nums, left, l-1, k);
        return nums.subList(l,nums.size());
    }

}
