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
</script>
</html>