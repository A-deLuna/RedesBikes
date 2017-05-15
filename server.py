from time import sleep
import SocketServer
import json
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
import threading
from nmea import parse_nmea

bikes = {
         1: (32.656, -115.4086),
         2: (32.655, -115.4100),
         3: (32.6555, -115.4090),
        }
class MyTCPHandler(SocketServer.StreamRequestHandler):

    def handle(self):
        try:
            while True:
                # self.rfile is a file-like object created by the handler;
                # we can now use e.g. readline() instead of raw recv() calls
                self.data = self.rfile.readline()
                print "what I actually received {}".format(self.data)
                if not self.data:
                    break
                self.data = self.data.strip()
                print "{} wrote: ".format(self.client_address[0])
                print self.data
                # Likewise, self.wfile is a file-like object used to write back
                # to the client
                for i in xrange(60):
                    lr = 1 if i / 10 % 2 else -1;
                    msg = json.dumps(bikes) + "\n"
                    print "Wrote to {}: {}".format(self.client_address[0], msg)
                    self.wfile.write(msg)
                    #simulation code, remove when doing the real thing
                    #for k, (lat, lng) in bikes.iteritems():
                    #    if k % 2 == 0:
                    #        bikes[k] = (lat + lr * .00005, lng)
                    #    else:
                    #        bikes[k] = (lat , lng + lr *  .00005)
                    sleep(1)
                self.wfile.write("EOT\n")
        except Exception as e:
            print "Error: {}".format(e)


class myHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-Type', 'text/plain')
        self.end_headers()
        self.wfile.write("Hello")
    def do_POST(self):
        content_length= int(self.headers.getheader('content-length', 0))
        nmea_string = self.rfile.read(content_length)
        print "NMEA STRING: ", nmea_string
        lat_lng = parse_nmea(nmea_string)
        bikes[4] = lat_lng
        self.send_response(200)
        self.send_header('Content-Type', 'text/plain')
        self.end_headers()
        self.wfile.write(str(lat_lng))



def ok():
    httpserver = HTTPServer(("127.0.0.1",8000), myHandler)
    httpserver.serve_forever()


if __name__ == "__main__":
    HOST, PORT = "192.168.1.66", 26968

    # Create the server, binding to localhost on port
    server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)
    # Activate the server; this will keep running until you
    # interrupt the program with Ctrl-C
    threading.Thread(target=ok).start()
    server.serve_forever()
