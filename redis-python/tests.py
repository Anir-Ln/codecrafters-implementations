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
  yield

  service.terminate()
  service.wait()

# If i set autouse=True in the fixture, it will be automatically
# applied to each test function in the module. I don't need to 
# explicitly include the fixture in the test functions.
@pytest.fixture(scope='function', autouse=True)
def setup_teardown():
  # setup code
  client = Client()
  resource = {'client': client}
  client.execute('FLUSH')
  yield resource
  client.close_connection()


def test_set_get_string(setup_teardown):
  client = setup_teardown['client']
  client.execute('SET', 'key', 'value')
  assert client.execute('GET', 'key') == 'value'

def test_set_get_integer(setup_teardown):
  client = setup_teardown['client']
  client.execute('SET', 'key', 10)
  assert client.execute('GET', 'key') == 10

def test_set_get_array(setup_teardown):
  client = setup_teardown['client']
  value = ['value1', 'value2', 10]
  client.execute('SET', 'key', value)
  assert client.execute('GET', 'key') == value

def test_set_get_dict(setup_teardown):
  client = setup_teardown['client']
  value = {'key1': 'value1', 'key2': 'value2'}
  client.execute('SET', 'key', value)
  assert client.execute('GET', 'key') == value

def test_delete(setup_teardown):
  client = setup_teardown['client']
  client.execute('SET', 'key', 'value')
  client.execute('DELETE', 'key')
  assert client.execute('GET', 'key') == None

def test_set_mget(setup_teardown):
  client = setup_teardown['client']
  client.execute('SET', 'key1', 'value1')
  client.execute('SET', 'key2', 10)
  assert client.execute('MGET', 'key1', 'key2') == ['value1', 10]

def test_mset_mget(setup_teardown):
  client = setup_teardown['client']
  client.execute('MSET', 'key1', 'value1', 'key2', 'value2', 'key3', 'value3')
  assert client.execute('MGET', 'key1', 'key2', 'key3') == ['value1', 'value2', 'value3']

def test_flush(setup_teardown):
  client = setup_teardown['client']
  client.execute('SET', 'key1', 'value1')
  client.execute('SET', 'key2', 10)
  client.execute('FLUSH')
  assert client.execute('MGET', 'key1', 'key2') == [None, None]
