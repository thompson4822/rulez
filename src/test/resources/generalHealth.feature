Feature: General Health

Background: Preconditions
  Given the following screen records:
    | Screen            | Timeout |
    | Insert Key        | 3       |

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

Scenario Outline: Multiple hardware disconnects within a certain time should create a ticket
  Given we have USB attached hardware device <Device>
  And the disconnect count is <Disconnect Count>
  Then whether to generate a ticket is <Gen Ticket>
Examples:
  | Device           | Disconnect Count | Gen Ticket |
  | Bill Collector   | 1                | no         |
  | Bill Collector   | 3                | yes        |
  | Card Reader      | 3                | yes        |

Scenario Outline: Brass almost out should create ticket
  Given a kiosk has brass keys
  And the number of keys remaining is <Brass Count>
  Then whether to generate a brass low ticket is <Gen Ticket>
Examples:
  | Brass Count | Gen Ticket |
  | 100         | no         |
  | 9           | yes        |