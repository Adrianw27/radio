## Radio
A Java implementation of a state machine modeled radio with a graphical interface.
* **States**
* On: Tuned to the top frequency of FM band
* Off: Radio is off
* Scanning: Radio scans towards bottom frequency. It stops scanning when it locks onto a station or reaches the bottom.
* Locked: Radio is locked on to a frequency.
---
* **Controls**
* On/Off: Changes state to on or off
* Scan: Changes state to scanning
* Reset: Resets radio to top frequency
