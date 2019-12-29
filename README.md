# javaMeters
Two pointer meters to display CAN messages in this project

The CAN input is from a tcp/ip socket. The usual usage is a gateway from the CAN bus to a usb serial is connected to a socket using 'socat'. 'socat' connects to 'hub-server' which acts as a server to many programs on the PC. 

The ascii format uses a leading byte on the line to carry a sequence number and a byte at the end a checksum. The CAN message is in hex.

RPM - Display Commanded and Reported speed to/from DMOC interter.
Torque - Display Commanded and Reported torque to/from DMOC inverter.

