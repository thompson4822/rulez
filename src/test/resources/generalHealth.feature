Feature: General Health

Background: Preconditions
  Given the following screen records:
    | Screen            | Timeout |
    | Insert Key        | 3       |

Scenario: If the cancel button is being hit repeatedly for a particular screen, create a ticket
  Given pending

Scenario: If the customer's last screen was not remove key, create a ticket
  Given pending


@current
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

Scenario: Multiple hardware disconnects within a certain time should create a ticket
  Given pending
  Then I should generate a ticket

Scenario: Brass almost out should create ticket
  Given pending
  Then I should generate a ticket
