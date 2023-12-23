import subprocess
import pytest
import time
from server import Client


# The scope="module" in the fixture means the service 
# will start and stop once for the entire test module
@pytest.fixture(scope='module', autouse=True)
def start():
  service = subprocess.Popen(['python', 'server.py'])
  time.sleep(1)
  client = Client()
  resource = {'client': client}
  yield resource

  service.terminate()
  service.wait()

# If i set autouse=True in the fixture, it will be automatically
# applied to each test function in the module. I don't need to 
# explicitly include the fixture in the test functions.
@pytest.fixture(scope='function', autouse=True)
def setup_teardown(start):
  # setup code
  client: Client = start['client']
  client.execute('FLUSH')
  yield


def test_set_get_strings(start):
  client = start['client']
  client.execute('SET', 'key', 'value')
  assert client.execute('GET', 'key') == 'value'

def test_set_get_integers(start):
  client = start['client']
  client.execute('SET', 'key', 10)
  assert client.execute('GET', 'key') == 10

def test_set_mget(start):
  client = start['client']
  client.execute('SET', 'key1', 'value1')
  client.execute('SET', 'key2', 10)
  assert client.execute('MGET', 'key1', 'key2') == ['value1', 10]

def test_mset_mget(start):
  client = start['client']
  client.execute('MSET', 'key1', 'value1', 'key2', 'value2', 'key3', 'value3')
  assert client.execute('MGET', 'key1', 'key2', 'key3') == ['value1', 'value2', 'value3']

def test_flush(start):
  client = start['client']
  client.execute('SET', 'key1', 'value1')
  client.execute('SET', 'key2', 10)
  client.execute('FLUSH')
  assert client.execute('MGET', 'key1', 'key2') == [None, None]
