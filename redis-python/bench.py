from gevent import monkey
monkey.patch_all()

from server import Client
import contextlib
import time

N = 10_000
benchmarks = []

@contextlib.contextmanager
def timed(s):
  start = time.time()
  yield
  duration = round(time.time() - start, 3)
  benchmarks.append("%s: time = %ss, speed = %dset&get/s" % (s, duration, 10000/duration))


def run_benchmark(client):
  with timed("get/set"):
    for i in range(N):
      client.execute("SET", "k%d" % i, "v%d" % i)
    for i in range(N + int(N * 0.1)):
      client.execute("GET", "k%d" % i)

  with timed("serializing arrays"):
    arr = [1, 2, 3, 4, 5, 6, [7, 8, 9, [10, 11, 12], 13], 14, 15]
    for i in range(N):
      client.execute("SET", "k%d" % i, arr)
    for i in range(N):
      client.execute("GET", "k%d" % i)

  with timed("serializing dicts"):
    d = {"k1": "v1", "k2": "v2", "k3": {"v3": {"v4": "v5"}}}
    for i in range(N):
      client.execute("SET", "k%d" % i, d)
    for i in range(N):
      client.execute("GET", "k%d" % i)


if __name__ == "__main__":
  client = Client(disable_logging=True)
  try:
    run_benchmark(client)
    print(*benchmarks, sep='\n')
  finally:
    client.close_connection()
