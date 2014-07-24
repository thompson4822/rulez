@generalHealth
Feature: General Health
  As a Kiosk
  I should be able to make some general determinations about the UI
  to ensure that it is functioning correctly

  Background: Preconditions
    Given I have the following log:
      | 09:27:00.661 DEBUG - Page Entry: [screen=Startup] |
      | 13:52:37.986 DEBUG - BillAcceptorDisconnectedEvent: [Description=Acceptor disconnected, username=Unknown, level=0] |
      | 13:53:00.846 DEBUG - BillAcceptorConnectedEvent: [Description=Acceptor connected, username=Unknown, level=0]       |
      | 13:56:19.565 DEBUG - SurveyResponse: [SurveyResponse=GoBack, sessionid=640e9f89-b487-4cda-af5a-f0125c2061f9, Screen=Survey, level=0, message=] |
      | 09:41:00.251 DEBUG - Page Entry: [screen=Startup] |
      | 13:56:19.572 DEBUG - Page Entry: [screen=Select Keys, sessionid=640e9f89-b487-4cda-af5a-f0125c2061f9] |
      | 12:46:46.798 DEBUG - KeyEject: [Stack=9, sessionid=507d4452-589f-4fa4-894a-e8860f9aca63, Quantity=30, SKU=KSCJMB00001BRAS, level=0, message=] |

  @cancelButton
  Scenario: If the cancel button is being hit repeatedly for a particular screen, create a ticket
    When there are 2 cancel button clicks on screen "Welcome"
    Then a "Excessive cancels on screen Welcome" ticket should be generated

  @cancelButton
  Scenario: If the cancel button was hit once for a particular screen, don't create a ticket
    When there is 1 cancel button clicks on screen "Welcome"
    Then a "Excessive cancels on screen Welcome" ticket should not be generated

  @lastCustomerScreen
  Scenario: If the customer's last screen was not remove key, create a ticket
    When the customer's last screen was "Welcome"
    Then a "Customer Session Did Not Exit In The Expected Manner" ticket should be generated

  @lastCustomerScreen
  Scenario: If the customer's last screen was remove key, don't create a ticket
    When the customer's last screen was "Remove Key"
    Then a "Customer Session Did Not Exit In The Expected Manner" ticket should not be generated

  @screenTransition
  Scenario: Screen hasn't transitioned within its timeout period, create a ticket
    When the screen "Welcome" doesn't transition within its timeout period
    Then a "Customer Screen 'Welcome' Did Not Transition Within Its Timeout Period" ticket should be generated

  @screenTransition
  Scenario: Screen transitioned within its timeout period, don't create a ticket
    When the screen "Welcome" does transition within its timeout period
    Then a "Customer Screen 'Welcome' Did Not Transition Within Its Timeout Period" ticket should not be generated
