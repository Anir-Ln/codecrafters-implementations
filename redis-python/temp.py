# var = dict(zip([1, 2, 3], [4, 5, 6]))
# print(var)
# print(type(var))
# print(isinstance(var,list))
# s = b'value\r\n'
s = "value".encode('utf-8')
# d = s.decode('utf-8')
print(s.strip(b'\r\n').decode('utf-8'))
