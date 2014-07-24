@inventory
Feature: Inventory
  As a Kiosk
  I should be able to identify inventory trends
  so I can notify TSO when appropriate

#  Scenario Outline: Brass almost out should create ticket
#    Given a kiosk has brass keys
#    And the number of keys remaining is <Brass Count>
#    Then whether to generate a brass low ticket is <Gen Ticket>
#  Examples:
#    | Brass Count | Gen Ticket |
#    | 100         | no         |
#    | 9           | yes        |

  Scenario: If the machine is frequently unable to identify a keyway, it could mean issues with the robot configuration
    When too many keys could not be identified today
    Then a "Suspicious number of unidentified keys encountered, could there be a hardware configuration issue?" ticket should be generated

# We could potentially do some inventory forecasting in the
# future so that we can notify TSO before the machine needs keys

# Could there be non-fatal tickets? If so, maybe there is room
# to indicate to TSO the popularity of keyways and designs?

