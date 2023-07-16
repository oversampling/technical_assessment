package online.chanjinyee.technical_assessment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AlienClock {

    @GetMapping("/alien-time")
    public String getAlienTime() {
        return "Alien time";
    }

    @GetMapping("/earth-time")
    public String getEarthTime() {
        return "Earth time";
    }
}
