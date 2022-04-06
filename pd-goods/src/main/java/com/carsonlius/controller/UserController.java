package com.carsonlius.controller;

import com.carsonlius.dto.BaseDto;
import com.carsonlius.entity.User;
import com.carsonlius.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author carsonlius
 * @Date 2022/3/6 17:43
 * @Version 1.0
 */
@RestController
@RequestMapping("/user")
@Api(value = "用户模块", tags = "用户模块")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 获取users列表
     * */
    @GetMapping
    @ApiOperation(value = "用户列表", response = List.class, httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "merchantId", value = "", dataType = "String", paramType = "query", required= true)
    })
    public List<User> lists(BaseDto baseDto){

        return userService.lists();
    }


    public static void main(String[] args) {
        UserController controller = new UserController();
        int[] nodes = new int[]{2,4,1,5,3,1,7,5,6};

        //
        System.out.println(controller.maxWater(nodes));


    }

    /**
     * 因为要该容器是一个高低不平的容器，所以我们直接找出容器的左右边界，很明显，
     * 为了不让水溢出来，容器的边界肯定取那个更低的。
     * 然后使用双指针，分别从两边往中间扫描，
     * 如果此时左边arr[left]的高度小于右边的高度时，
     * 左指针向右扫描+1，如果此时当前位置的高度小于容器的边界高度，
     * 那么意味着此位置可以盛水；反之，则右指针向左扫描-1。
     * @param
     */
    public long maxWater (int[] arr) {

        if(arr.length == 0 || arr.length <= 2){
            return 0;
        }

        int left = 0;
        int right = arr.length-1;
        int min = Math.min(arr[left],arr[right]);
        long result = 0;

        while(left < right){
            if(arr[left] < arr[right]){
                left++;
                //如果当前水位小于边界，则可以装水
                if(arr[left] < min){
                    result += min-arr[left];
                }else{
                    min = Math.min(arr[left],arr[right]);
                }
            }else{
                right--;
                if(arr[right] < min){
                    result += min-arr[right];
                }else{
                    min = Math.min(arr[right],arr[left]);
                }
            }
        }
        return result;
    }
}
