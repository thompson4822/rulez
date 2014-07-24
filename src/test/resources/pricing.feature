@pricing
Feature: Pricing rules
  As a kiosk
  I should be able to analyze pricing trends
  to determine if there is some problem

#  Scenario Outline: If a machine is deviating in purchases significantly from the week prior, generate a ticket
#    Given machine purchase deviations of <Deviation>
#    Then I should generate a ticket: <Ticket>
#  Examples:
#    | Deviation | Ticket |
#    |  50       | yes    |
#    |  10       | no     |
#
#  Scenario Outline: If there are X Payment failures in a row, generate a ticket
#    Given <Payment Failures> transactions time out in Cash screen
#    Then I should generate a ticket: <Ticket>
#  Examples:
#    | Payment Failures | Ticket |
#    |  3               | yes    |
#    |  1               | no     |
