from bluetooth import *

def bluetooth_scan():
    print "performing bluetooth search... (might take a while)"
    nearby_devices = discover_devices(lookup_names = True)
    print "found %d devices (name - address)" % len(nearby_devices)
    for name, addr in nearby_devices:
         print " %s - %s" % (addr, name)


bluetooth_scan()

