Feature: Survey
  As a kiosk
  I should look at survey results
  to try and determine if errant behavior is occurring

#val ConfusingProcess = Value("ConfusingProcess")
#val ExpectDifferentPrice = Value("ExpectDifferentPrice")

Scenario Outline: If lots of surveys indicate kiosk not working, create a ticket
  Given <Not working surveys> consecutive "kiosk not working" surveys
  Then I should generate a ticket: <Ticket>
Examples:
  | Not working surveys | Ticket |
  |  2                  | yes    |
  |  1                  | no     |

Scenario Outline: If the Attract loop indicates that the Kiosk is cash but customers are not choosing cash, create ticket
  Given <Hours No Cash> consecutive "kiosk not working" surveys
  Then I should generate a ticket: <Ticket>
Examples:
  | Hours No Cash | Ticket |
  |  6            | yes    |
  |  3            | no     |


