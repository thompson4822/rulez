@billAcceptor
Feature: Bill Acceptor
  As a Kiosk
  I should be able to identify unusual bill acceptor behavior
  so that TSO can be informed that there may be a problem

  # This should be sufficient for testing both connect and disconnect
  Scenario: If the bill acceptor is reconnecting on a regular basis, it could indicate hardware issues
    When the bill acceptor has re-connected several times today
    Then a "Suspicious Bill Acceptor Connectivity" ticket should be generated

  # This should be sufficient for testing both detach and reattach
  Scenario: If the bill acceptor cassette is being detached frequently, it could indicate Kiosk tampering or hardware issues
    When the bill acceptor cassette has been detached more than once today
    Then a "Suspicious Bill Acceptor Cassette Handling" ticket should be generated

