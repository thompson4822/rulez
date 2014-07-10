Feature: Pricing rules
  As a kiosk
  I should be able to analyze pricing trends
  to determine if there is some problem

Scenario Outline: If there are no purchases on a machine in a certain time, generate a ticket
  Given pending
  Then I should generate a ticket
Examples:
  | Grade | Time |

# Take into account how holidays and weekends might have an impact

Scenario: If a machine is deviating in purchases significantly from the day prior, generate a ticket
  Given pending
  Then I should generate a ticket

Scenario: If a machine is deviating in purchases significantly from the week prior, generate a ticket
  Given pending
  Then I should generate a ticket

# Hardware (card reader) bad
# CC
Scenario: If a number of transactions are canceled in the CC screen, generate a ticket
  Given pending
  Then I should generate a ticket

Scenario: If a number of transactions time out in the CC screen, generate a ticket
  Given pending
  Then I should generate a ticket

# Cash
Scenario: If a number of transactions are canceled in the Cash screen and bills aren't stacked, generate a ticket
  Given pending
  Then I should generate a ticket

Scenario: If a number of transactions time out in the Cash screen, generate a ticket
  Given pending
  Then I should generate a ticket

#Scenario: If Attract Loop price indicates cash and customer never gets the payment option, generate ticket
#  Pending

Scenario: If there are X Payment failures in a row, generate a ticket
