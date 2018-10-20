# File Automaticly gets called by the startup processes on the raspberry pi

import threading
from networktables import NetworkTables

cond = threading.Condition()
notified = [False]

def connectionListener(connected, info):
    print(info, '; Connected=%s' % connected)
    with cond:
        notified[0] = True
        cond.notify()

def networkTablesConnect():
    NetworkTables.initialize(server='10.xx.xx.2')
    NetworkTables.addConnectionListener(connectionListener, immediateNotify=True)
    with cond:
        print("Waiting for robot to connect")
        if not notified[0]:
            cond.wait()

networkTablesConnect()

# Insert your processing code here
print("Connected!")
