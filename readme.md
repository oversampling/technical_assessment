# N2N technical assessement (Alien Clock)

![](assets/Technical_Assessment.jpg)

## Architecture
To make thing simple, this assessment is done by using JSP and spring framework. When direct to "/" path, the server will server render jsp page with predefined alien and earth datetime. Another that, the server will have endpoint to set earth datetime (POST reqeust), get earth datetime (GET reqeust) and get alien datetime (GET reqeust).

## How I convert earth time to alien time
According to the question, 1 second alien time is 0.5 second earth time. Based on this requirement, I will convert earth date time to second representative and divide the result by 2 to get alien time in second form. Then, convert the alien time in second form to full datetime format according to the requirement number 2. To achieve this, I just loop through every second and add the second according to the logic (For example, when the second already been added up to 89 seconds, the next add up will increase 1 minute, same thing happen to hour, day, etc).

## How to display Alien clock showing date and the time updated every 1 Alien second
There will be two method came to mind to implement this. Either every 1 alien second perform fetch API to get latest Alien datetime or perform Alien datetime calculation in front-end part. I choose to second method where perform calculation in front-end part, first method may cause your server overflow if the amount of users increase.

## How I implement set earth datetime with validation (WIP)
Nothing fancy, validation mostly do in front-end part where I set minimun and maximun value that allowed in HTML input element. For server side validation, I just check is the earth date is valid and return status code of 202 if fine, 400 if invalid datetime provided. After finished, the client will fetch latest alien and earth time and update accordingly. 

To functionality still in progress where you only can set time greater than before due to the way we convert eart time to alien time.


## Showcase
![](assets/Showcase.jpg)
