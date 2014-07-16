@parsing
Feature: Parsing tests
  As a tool
  I can parse log data
  so that I can determine if any errant behavior is happening

Background: Preconditions
  Given I have the following log:
    | 09:27:00.661 DEBUG - Page Entry: [screen=Startup] |
    | 13:52:37.986 DEBUG - BillAcceptorDisconnectedEvent: [Description=Acceptor disconnected, username=Unknown, level=0] |
    | 13:53:00.846 DEBUG - BillAcceptorConnectedEvent: [Description=Acceptor connected, username=Unknown, level=0]       |
    | 13:56:19.565 DEBUG - SurveyResponse: [SurveyResponse=GoBack, sessionid=640e9f89-b487-4cda-af5a-f0125c2061f9, Screen=Survey, level=0, message=] |
    | 09:41:00.251 DEBUG - Page Entry: [screen=Startup] |
    | 13:56:19.572 DEBUG - Page Entry: [screen=Select Keys, sessionid=640e9f89-b487-4cda-af5a-f0125c2061f9] |

Scenario Outline: I should be able to detect that a screen was visited, as well as how often
  When I parse the log data
  Then I should see the screen "<Screen>" was visited <Times Visited> times
Examples: Sad path
  | Screen       | Times Visited |
  | Attract Loop | 0             |
Examples: Happy path
  | Screen      | Times Visited |
  | Startup     | 2             |
  | Select Keys | 1             |

Scenario: Any screens visited that were not Startup should have a session ID
  When I parse the log data
  Then any screen entries that are not "Startup" should contain a session id
