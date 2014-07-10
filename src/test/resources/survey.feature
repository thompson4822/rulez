Feature: Survey
  As a kiosk
  I should look at survey results
  to try and determine if errant behavior is occurring

#val ConfusingProcess = Value("ConfusingProcess")
#val ExpectDifferentPrice = Value("ExpectDifferentPrice")

Scenario: If lots of surveys indicate kiosk not working, create a ticket
  Given there have been more than two consecutive "kiosk not working" surveys
  Then I should generate a ticket

Scenario: If the Attract loop indicates that the Kiosk is cash but customers are choosing no cash, create ticket
  Given pending
  Then I should generate a ticket


