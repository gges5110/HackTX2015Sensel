__author__ = 'Jenny'

import bluetooth

class ConnectBluetooth:


	def __init__(self):
		target_name = "Onetouch Idol 3"
		target_address = None

		nearby_devices = bluetooth.discover_devices()
		print nearby_devices

		for addr in nearby_devices:
			print bluetooth.lookup_name(addr)
			if target_name == bluetooth.lookup_name(addr):
				target_address = addr
				break

		print target_address

		uuid = "fa87c0d0-afac-11de-8a39-0800200c9a66"
		service_matches = bluetooth.find_service(uuid = uuid, address = target_address)

		if len(service_matches) == 0:
			print "Could not  find service"
			sys.exit(0)

		port = service_matches[0]["port"]
		name = service_matches[0]["name"]
		host = service_matches[0]["host"]

		self.socket = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
		self.socket.connect((host, port))

	def send(self, message):
		self.socket.send(str(message))
		if(message == "exit"):
			self.socket.close()

	def close(self):
		self.socket.close()

