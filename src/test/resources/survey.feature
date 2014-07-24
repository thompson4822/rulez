@survey
Feature: Survey
  As a kiosk
  I should look at survey results
  to try and determine if errant behavior is occurring

  Scenario: If lots of surveys indicate kiosk not working, create a ticket
    When there are multiple surveys in a row that indicate the kiosk is not working
    Then a "Surveys Indicating Kiosk Not Working" ticket should be generated

  Scenario: If only a single survey indicates kiosk not working, do not create a ticket
    When there is one surveys in a row that indicate the kiosk is not working
    Then a "Surveys Indicating Kiosk Not Working" ticket should not be generated


