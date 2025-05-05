package vn.minhdat.jobhunter_be;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello {

    @GetMapping("/")
    public String getHello(){
        return "Hello World";
    }
}
