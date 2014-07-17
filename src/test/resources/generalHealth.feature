Feature: General Health

Background: Preconditions
  Given the following screen records:
    | Screen            | Timeout |
    | Insert Key        | 3       |

  And I have the following log:
    | 09:27:00.661 DEBUG - Page Entry: [screen=Startup] |
    | 13:52:37.986 DEBUG - BillAcceptorDisconnectedEvent: [Description=Acceptor disconnected, username=Unknown, level=0] |
    | 13:53:00.846 DEBUG - BillAcceptorConnectedEvent: [Description=Acceptor connected, username=Unknown, level=0]       |
    | 13:56:19.565 DEBUG - SurveyResponse: [SurveyResponse=GoBack, sessionid=640e9f89-b487-4cda-af5a-f0125c2061f9, Screen=Survey, level=0, message=] |
    | 09:41:00.251 DEBUG - Page Entry: [screen=Startup] |
    | 13:56:19.572 DEBUG - Page Entry: [screen=Select Keys, sessionid=640e9f89-b487-4cda-af5a-f0125c2061f9] |


@clickTest
Scenario Outline: If the cancel button is being hit repeatedly for a particular screen, create a ticket
  Given the number of cancel button clicks is <Cancel Click Count>
  Then whether to generate a ticket is <Gen Ticket>
Examples:
  | Cancel Click Count | Gen Ticket |
  | 1                  | no         |
  | 3                  | yes        |
  | 5                  | yes        |

Scenario Outline: If the customer's last screen was not remove key, create a ticket
  Given the customer's last screen is "<Last Screen>"
  Then whether to generate a ticket is <Gen Ticket>
Examples:
  | Last Screen   | Gen Ticket |
  | Cash Payment  | yes        |
  | Remove Key    | no         |

Scenario Outline: Screen hasn't transitioned within its timeout period, create a ticket
  Given the current screen is "<Current Screen>"
  When time elapsed is <Time Elapsed> seconds
  Then a ticket <Expectation> be sent
Examples: Sad path
  | Current Screen | Time Elapsed | Expectation |
  | Insert Key     | 2            | should      |
Examples: Happy path
  | Current Screen | Time Elapsed | Expectation |
  | Insert Key     | 1            | should not  |

# This would account for size of cart and mk+ or regular machine
Scenario Outline: Screen hasn't transitioned from key copy in a given number of minutes, create a ticket
  Given we're on key copy progress
  And our kiosk is <Kiosk Type>
  And there are <Keys In Cart> keys
  Then the expected time on this screen is <Expected Time> minutes
Examples:
  | Kiosk Type | Keys In Cart | Expected Time |
  | mk+        | 1            | 2             |
  | mk+        | 2            | 4             |
  | standard   | 2            | 3             |

@current
Scenario Outline: Multiple hardware disconnects within a certain time should create a ticket
  Given we have USB attached hardware device <Device>
  And the disconnect count is <Disconnect Count>
  Then whether to generate a ticket is <Gen Ticket>
Examples:
  | Device           | Disconnect Count | Gen Ticket |
  | Bill Collector   | 1                | no         |
  | Bill Collector   | 3                | yes        |
  | Card Reader      | 3                | yes        |


