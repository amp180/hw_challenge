#!/usr/bin/env python3
import random

filename = "./random_numbers.txt"
# Generate a 50-line tab separated file with line numbers and random numbers
with open(filename, 'wb') as f:
	for i in range(1, 51):
		line = "%d\t%d\n" % (i, random.randrange(100, 501, 1))
		line_bytes = bytes(line, "utf-8")
		f.write(line_bytes)
