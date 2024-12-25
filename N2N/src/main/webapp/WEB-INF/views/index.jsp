<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <h1>Earth Time</h1>
    <p id="earthTime">${earthTime}</p>
    <h1>Alient Time</h1>
    <p id="alienTime">${alienTime}</p>

    <h1>Set Earth Time</h1>
    <form method="post" id="setEarthTimeform">
        <label for="earthYear">Year</label>
        <input type="number" name="earthYear" id="earthYear" min="0" required value="1970">
        <br/>
        <label for="earthMonth">Month</label>
        <input type="number" name="earthMonth" id="earthMonth" min="1" max="12" required value="1">
        <br/>
        <label for="earthDay">Day</label>
        <input type="number" name="earthDay" id="earthDay" required value="1">
        <br/>
        <label for="earthHour">Hour</label>
        <input type="number" name="earthHour" id="earthHour"  min="0" max="23" required value="0">
        <br/>
        <label for="earthMinute">Minute</label>
        <input type="number" name="earthMinute" id="earthMinute" min="0" max="59" required value="0">
        <br/>
        <label for="earthSecond">Second</label>
        <input type="number" name="earthSecond" id="earthSecond" min="0" max="59" required value="0">

        <button type="submit" value="earthTimeSubmit" name="earthTimeSubmit">Submit</button>
    </form>
</body>
<script>
    let earthMonth = document.getElementById("earthMonth");
    let earthDay = document.getElementById("earthDay");
    let earthYear = document.getElementById("earthYear");
    let earthHour = document.getElementById("earthHour");
    let earthMinute = document.getElementById("earthMinute");
    let earthSecond = document.getElementById("earthSecond");
    let earthTime = document.getElementById("earthTime");
    let alienTime = document.getElementById("alienTime");
    earthMonth.addEventListener("change", () => {
        if (earthMonth.value == 1 || earthMonth.value == 3 || earthMonth.value == 5 || earthMonth.value == 7 || earthMonth.value == 8 || earthMonth.value == 10 || earthMonth.value == 12) {
            earthDay.max = 31;
        } else if (earthMonth.value == 4 || earthMonth.value == 6 || earthMonth.value == 9 || earthMonth.value == 11) {
            earthDay.max = 30;
        } else if (earthMonth.value == 2) {
            if (earthYear.value % 4 == 0 && (earthYear.value  % 100 != 0 || earthYear.value  % 400 == 0)) {
                return day <= 29;
            } else {
                return day <= 28;
            }
        }
    })
    let setEarthTimeform = document.getElementById("setEarthTimeform");
    setEarthTimeform.addEventListener("submit", async (e) => {
        e.preventDefault()
        const body = {
            earthDay: parseInt(earthDay.value),
            earthHour: parseInt(earthHour.value),
            earthMinute: parseInt(earthMinute.value),
            earthMonth: parseInt(earthMonth.value),
            earthSecond: parseInt(earthSecond.value),
            earthYear: parseInt(earthYear.value)
        }
        const response = await fetch("/set-earth-time", {
            method: "POST",
            headers:  {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        })
        const earthResponse = await fetch("earth-time", {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        })
        await earthResponse.text().then((data) => {
            earthTime.innerHTML = data
        })
        const alienResponse = await fetch("alien-time", {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        })
        await alienResponse.text().then((data) => {
            alienTime.innerHTML = data
        })
    })
    let daysInMonth = [
            0, // Month 0 (not used)
            44, // Month 1
            42, // Month 2
            48, // Month 3
            40, // Month 4
            48, // Month 5
            44, // Month 6
            40, // Month 7
            44, // Month 8
            42, // Month 9
            40, // Month 10
            40, // Month 11
            42, // Month 12
            44, // Month 13
            48, // Month 14
            42, // Month 15
            40, // Month 16
            44, // Month 17
            38 // Month 18
    ];
    setInterval(() => {
        let alientTimeSplited = alienTime.innerHTML.split(" ");
        let alienSecond = parseInt(alientTimeSplited[10]);
        let alienMinute = parseInt(alientTimeSplited[8]);
        let alienHour = parseInt(alientTimeSplited[6]);
        let alienDay = parseInt(alientTimeSplited[4]);
        let alienMonth = parseInt(alientTimeSplited[2]);
        let alienYear = parseInt(alientTimeSplited[0]);
        let alientTimeDetail = {
            alienSecond: alienSecond,
            alienMinute: alienMinute,
            alienHour: alienHour,
            alienDay: alienDay,
            alienMonth: alienMonth,
            alienYear: alienYear
        }
        let display = incrementAlienTime(alientTimeDetail.alienSecond, alientTimeDetail.alienMinute, alientTimeDetail.alienHour, alientTimeDetail.alienDay, alientTimeDetail.alienMonth, alientTimeDetail.alienYear, daysInMonth)
        alienTime.innerHTML = display
    }, 500)

    setInterval(() => {
        let earthTimeSplited = earthTime.innerHTML.split(" ");
        let earthSecond = parseInt(earthTimeSplited[10]);
        let earthMinute = parseInt(earthTimeSplited[8]);
        let earthHour = parseInt(earthTimeSplited[6]);
        let earthDay = parseInt(earthTimeSplited[4]);
        let earthMonth = parseInt(earthTimeSplited[2]);
        let earthYear = parseInt(earthTimeSplited[0]);
        let earthTimeDetail = {
            earthSecond: earthSecond,
            earthMinute: earthMinute,
            earthHour: earthHour,
            earthDay: earthDay,
            earthMonth: earthMonth,
            earthYear: earthYear
        }
        let display = incrementEarthTime(earthTimeDetail.earthSecond, earthTimeDetail.earthMinute, earthTimeDetail.earthHour, earthTimeDetail.earthDay, earthTimeDetail.earthMonth, earthTimeDetail.earthYear, daysInMonth)
        earthTime.innerHTML = display
    }, 1000)
    
    function incrementEarthTime(earthSecond, earthMinute, earthHour, earthDay, earthMonth, earthYear, daysInMonth) {
        earthSecond++;

        if (earthSecond >= 60) {
            earthSecond = 0;
            earthMinute++;

            if (earthMinute >= 60) {
                earthMinute = 0;
                earthHour++;

                if (earthHour >= 24) {
                    earthHour = 0;
                    earthDay++;

                    if (earthDay > daysInMonth[earthMonth]) {
                        earthDay = 1;
                        earthMonth++;

                        if (earthMonth > 12) {
                            earthMonth = 1;
                            earthYear++;
                        }
                    }
                }
            }
        }
        return earthYear + " Y, " + earthMonth + " M, " + earthDay + " D, " + earthHour + " H, " + earthMinute + " M, " + earthSecond + " S"
    }

    function incrementAlienTime(alienSecond, alienMinute, alienHour, alienDay, alienMonth, alienYear, daysInMonth) {
        alienSecond++;

        if (alienSecond >= 90) {
            alienSecond = 0;
            alienMinute++;

            if (alienMinute >= 90) {
                alienMinute = 0;
                alienHour++;

                if (alienHour >= 36) {
                    alienHour = 0;
                    alienDay++;

                    if (alienDay > daysInMonth[alienMonth]) {
                        alienDay = 1;
                        alienMonth++;

                        if (alienMonth > 18) {
                            alienMonth = 1;
                            alienYear++;
                        }
                    }
                }
            }
        }
        return alienYear + " Y, " + alienMonth + " M, " + alienDay + " D, " + alienHour + " H, " + alienMinute + " M, " + alienSecond + " S"
    }
</script>
</html>