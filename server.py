from time import sleep
import SocketServer
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
                msg = "Hola\n"
                for i in xrange(10):
                    print "Wrote to {}: {}".format(self.client_address[0], msg)
                    self.wfile.write(msg)
                    sleep(1)
                self.wfile.write("EOT\n")
        except Exception as e:
            print "Error: {}".format(e)


if __name__ == "__main__":
    HOST, PORT = "192.168.1.66", 26968

    # Create the server, binding to localhost on port 9999
    server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)

    # Activate the server; this will keep running until you
    # interrupt the program with Ctrl-C
    server.serve_forever()
