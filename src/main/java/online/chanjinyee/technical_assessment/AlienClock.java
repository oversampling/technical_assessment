package online.chanjinyee.technical_assessment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AlienClock {

    @GetMapping("/alien-time")
    @ResponseBody
    public String getAlienTime() {
        int year = AlienClockController.earthYear;
        int month = AlienClockController.earthMonth;
        int day = AlienClockController.earthDay;
        int hour = AlienClockController.earthHour;
        int minute = AlienClockController.earthMinute;
        int second = AlienClockController.earthSecond;

        AlienClockController.convertEarthTimeToAlienTime(year, month, day, hour,
                minute, second);
        return AlienClockController.getAlienDate();
    }

    @PostMapping(value = "/set-earth-time")
    @ResponseBody
    public void setEarthTime(@RequestBody Earth_Time_Body earthTimeBody, HttpServletResponse response) {
        int year = earthTimeBody.getEarthYear();
        int month = earthTimeBody.getEarthMonth();
        int day = earthTimeBody.getEarthDay();
        int hour = earthTimeBody.getEarthHour();
        int minute = earthTimeBody.getEarthMinute();
        int second = earthTimeBody.getEarthSecond();
        if (!AlienClockController.setEarthDateAndTime(year, month, day, hour, minute, second)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
    }

    @GetMapping("/earth-time")
    @ResponseBody
    public String getEarthTime() {
        return AlienClockController.getEarthDate();
    }

    @GetMapping(value = "/")
    public String getEarthTime(Model model) {
        model.addAttribute("alienTime", getAlienTime());
        model.addAttribute("earthTime", AlienClockController.getEarthDate());
        return "index";
    }
}
