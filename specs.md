# Baby Log

## Data to collect
Build an app for Android OS(mobile) to save these data:
- Date
- Time
- Food in milliliters(eg: 90, 110, 140)
- a check for food vitamins(once a day tipically in the morning between 7 AM and 9 AM)
- a check if the baby had a poop
- a check if the baby had a pee
- baby's weight in kilograms (eg: 5.1, 6.22)

## Design
Use Android material UI -> https://m3.material.io/develop/android/jetpack-compose


## Logic and pages
The main page should display:
- A main header with a 
    - a button of the left that will display the current Day. When tapped a carusel containings previous days should be displayed. When a day is selected the page content should update the display the datat from that day
    - button on the right, that when tapped a line chart should be displayed. The chart will display the baby's weight(in kilograms) and the total food(in milliliters) for each day
- A header containing quick informations for current day like: 
    - total food eaten(eg: Total food: 1000 ml)
    - had vitamin(yes or no - you can alos add a check for this)
    - total poops and pees
- a floating button that when tapped 2 buttons shoudl appear:
    - a button to add a new entry. When the button is clicked, a floating window should appear with these fileds:
        - time (should have default value to current time)
        - food amount (in milliliters)
        - a check for poop
        - a check for pee
        - a check for vitamins (this should be displayed once a day, if in that day the vitamins were taken the this should be hidden)
    - a button to enter babys weight in kilograms
- a timeline data display for each entry that displays the hour, food amount and a check for poop, pee and vitamins. The check should be display only if true. If the user taps and hold on a timeline item a floating window should appear to update the entry

