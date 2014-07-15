Feature: Pricing rules
  As a kiosk
  I should be able to analyze pricing trends
  to determine if there is some problem

Scenario Outline: If there are no purchases on a machine in a certain time, generate a ticket
  Given a <Grade> machine of A
  Then I should generate a ticket if no purchases in <Time>
Examples:
  | Grade | Time |
  |  A    | 4    |
  |  B    | 4    |

# Take into account how holidays and weekends might have an impact
Scenario Outline: If a machine is deviating in purchases significantly from the day prior, generate a ticket
  Given machine purchase deviations of <Deviation>
  Then I should generate a ticket: <Ticket>
Examples:
  | Deviation | Ticket |
  |  50       | yes    |
  |  10       | no     |

Scenario Outline: If a machine is deviating in purchases significantly from the week prior, generate a ticket
  Given machine purchase deviations of <Deviation>
  Then I should generate a ticket: <Ticket>
Examples:
  | Deviation | Ticket |
  |  50       | yes    |
  |  10       | no     |

# Hardware (card reader) bad
# CC
Scenario Outline: If a number of transactions are canceled in the CC screen, generate a ticket
  Given <Transactions> transactions are canceled in CC screen
  Then I should generate a ticket: <Ticket>
Examples:
  | Transactions | Ticket |
  |  3           | yes    |
  |  1           | no     |

Scenario Outline: If a number of transactions time out in the CC screen, generate a ticket
  Given <Transactions> transactions time out in CC screen
  Then I should generate a ticket: <Ticket>
Examples:
  | Transactions | Ticket |
  |  3           | yes    |
  |  1           | no     |

# Cash
Scenario Outline: If a number of transactions are canceled in the Cash screen and bills aren't stacked, generate a ticket
  Given <Transactions> transactions are canceled in Cash screen
  Then I should generate a ticket: <Ticket>
Examples:
  | Transactions | Ticket |
  |  3           | yes    |
  |  1           | no     |

Scenario Outline: If a number of transactions time out in the Cash screen, generate a ticket
  Given <Transactions> transactions time out in Cash screen
  Then I should generate a ticket: <Ticket>
Examples:
  | Transactions | Ticket |
  |  3           | yes    |
  |  1           | no     |

#Scenario: If Attract Loop price indicates cash and customer never gets the payment option, generate ticket
#  Pending

Scenario Outline: If there are X Payment failures in a row, generate a ticket
  Given <Payment Failures> transactions time out in Cash screen
  Then I should generate a ticket: <Ticket>
Examples:
  | Payment Failures | Ticket |
  |  3               | yes    |
  |  1               | no     |
