# Focus Monster
Focus monster is a time-tracking tool, designed to help users shorten their procrastination periods
and motivate them to focus on their work.

Users can set goals which they wish to achieve, be it reduce time spent doing unproductive tasks,
or vice versa, spend more time working.

All this can be achieved either by manual inputs from the user, or the automated tab-tracker extension for Google Chrome.

Download extension <a href="https://github.com/novotnej/tab_tracking/blob/master/tab_tracking.crx?raw=true">here</a> and install it by dragging the .crx file onto [chrome://extensions](chrome://extensions) site.

Run the FocusMonster.jar file and keep it running, if you wish to use the tab tracker

#For developers
##Installation

1. Clone repository
2. Install maven on your OS (if not previously installed)
3. Create IntelliJ maven project and set to auto import dependencies

##How to run sonarqube

run in terminal from within repository:

`mvn sonar:sonar   -Dsonar.organization=novotnej-github   -Dsonar.host.url=https://sonarcloud.io   -Dsonar.login=921033e4141040370ca85e7971e7d4b8e58ad810`

