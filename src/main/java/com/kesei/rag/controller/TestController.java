package com.kesei.rag.controller;

import com.kesei.rag.entity.dto.GenericResponse;
import com.kesei.rag.entity.vo.GenerationVO;
import com.kesei.rag.support.utils.ResponseUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kesei
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @RequestMapping("/receive")
    public GenericResponse<String> test(){
        System.out.println("received");
        return ResponseUtils.success("received");
    }
    
    @RequestMapping("/receive2")
    public GenericResponse<GenerationVO> test2(){
        System.out.println("received");
        return ResponseUtils.success(new GenerationVO());
    }
}
