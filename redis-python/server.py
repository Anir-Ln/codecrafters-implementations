from gevent import socket
from gevent.pool import Pool
from gevent.server import StreamServer

from collections import namedtuple
from io import BytesIO
import logging

logging.basicConfig(level=logging.DEBUG, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

class Disconnect(Exception): pass
class CommandError(Exception): pass

Error = namedtuple("Error", ("message",))

# SET age 10 --stored in socket file as--> +key\r\n:10\r\n
class ProtocolHandler(object):
  def __init__(self,):
    self.handlers = {
      b'+': self.handle_simple_string,
      b'-': self.handle_error,
      b':': self.handle_integer,
      b'$': self.handle_string,
      b'*': self.handle_array,
      b'%': self.handle_dict,
    }

  def handle_request(self, socket_file):
    # parse the req
    first_byte = socket_file.read(1)
    logger.info('handling request, first byte: %s', first_byte)
    if not first_byte:
      raise Disconnect
    try:
      return self.handlers[first_byte](socket_file)
    except KeyError:
      raise CommandError('Bad request')

  def handle_simple_string(self, socket_file):
    return socket_file.readline().strip(b'\r\n').decode('utf-8')

  def handle_error(self, socket_file):
    return Error(socket_file.readline().strip(b'\r\n'))

  def handle_integer(self, socket_file):
    return int(socket_file.readline().strip(b'\r\n'))

  # handle binary data
  def handle_string(self, socket_file):
    length = int(socket_file.readline().strip(b'\r\n'))
    if length == -1:
      return None
    length += 2 # include trailing \r\n
    return socket_file.read(length)[:-2] # not include trailing \r\n
  
  def handle_array(self, socket_file):
    length = int(socket_file.readline().strip(b'\r\n'))
    return [self.handle_request(socket_file) for _ in range(length)]

  def handle_dict(self, socket_file):
    length = int(socket_file.readline().strip(b'\r\n'))
    elements = [self.handle_request(socket_file) for _ in range(length*2)]
    return dict(zip(elements[::2], elements[1::2]))


  def write_response(self, socket_file, data):
    logger.info("Writing response %s", str(data))
    buf = BytesIO()
    self._write(buf, data)
    buf.seek(0) # Seek back to beginning
    socket_file.write(buf.getvalue())
    socket_file.flush() # ensure data is transmitted

  def _write(self, buf: BytesIO, data):
    if isinstance(data, bytes):
      buf.write(b'$%d\r\n%s\r\n' % (len(data), data))
    elif isinstance(data, str):
      data: bytes = data.encode('utf-8')
      buf.write(b'+%s\r\n' % data)
    elif isinstance(data, int):
      buf.write(b':%d\r\n' % data)
    elif isinstance(data, Error):
      buf.write(b'-%s\r\n' % data.message)
    elif isinstance(data, (list, tuple)):
      buf.write(b'*%d\r\n' % len(data))
      for item in data:
        self._write(buf, item)
    elif isinstance(data, dict):
      buf.write(b'%%%d\r\n' % len(data))
      for key, value in data.items():
        self._write(buf, key)
        self._write(buf, value)
    elif data is None:
      buf.write(b'$-1\r\n')
    else:
      raise CommandError('Unrecognized type: %s' % data)



class Server(object):
  def __init__(self, host='localhost', port=31337, max_clients=64):
    self._pool = Pool(max_clients)
    self._server = StreamServer((host, port), self.conncetion_handler, spawn=self._pool)
    self._protocol = ProtocolHandler()
    self._kv = dict()

    self._commands = {
      'GET': self.get,
      'SET': self.set,
      'DELETE': self.delete,
      'FLUSH': self.flush,
      'MGET': self.mget,
      'MSET': self.mset,
    }

  def get(self, key):
    return self._kv.get(key)
  def set(self, key, value):
    self._kv[key] = value
    return 1
  def delete(self, key):
    if key in self._kv: 
      del self._kv[key]
      return 1
    return 0
  def flush(self):
    length = len(self._kv)
    self._kv.clear()
    return length
  def mget(self, *keys):
    return [self._kv.get(key) for key in keys]
  def mset(self, *items):
    if len(items) % 2 != 0:
      raise CommandError("Invalid number of items: %d" % len(items))
    obj = dict(zip(items[::2], items[1::2]))
    for key, value in obj.items():
      self._kv[key] = value
    return len(obj)


  def conncetion_handler(self, conn, address):
    socket_file = conn.makefile('rwb')

    # process client requests until client disconnects
    while True:
      try:
        data = self._protocol.handle_request(socket_file)
        logger.info(data)
      except Disconnect:
        break

      try:
        resp = self.get_response(data)
      except CommandError as e:
        resp = Error(e.args[0])

      self._protocol.write_response(socket_file, resp)

      logger.info(self._kv)

  def get_response(self, data):
    # execute the command ex: data = ['SET', key, value]
    logger.info("getting response " + str(data))
    command = data[0].upper()
    if command not in self._commands:
      raise CommandError('Unrecognized command')

    return self._commands[command](*data[1:])

  def run(self):
    self._server.serve_forever()

  
class Client(object):
  def __init__(self, host='127.0.0.1', port=31337, disable_logging=False):
    self._protocol = ProtocolHandler()
    self._socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    self._socket.connect((host, port))
    self._fh = self._socket.makefile('rwb')
    logger.disabled = disable_logging

  def execute(self, *args):
    # example args = ['SET', key, value]
    self._protocol.write_response(self._fh, args)
    resp = self._protocol.handle_request(self._fh)
    if isinstance(resp, Error):
      raise CommandError(resp.message)
    return resp
  
  def close_connection(self):
    if self._fh:
      self._fh.close()
      self._fh = None
    if self._socket:
      self._socket.close()
      self._socket = None


if __name__ == "__main__":
  from gevent import monkey; monkey.patch_all()
  logger.disabled = True
  Server().run()