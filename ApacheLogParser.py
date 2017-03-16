from sys import argv
import re
from datetime import datetime, timedelta

if __name__ == '__main__':
	file = 0

	# parameters initialization
	interval = -1
	start = datetime.min
	end = datetime.max
	correct = re.compile('2[0-9][0-9]')

	# parsing the parameters
	i = 1
	while i < len(argv):
		# time interval
		if argv[i] == '--interval':
			interval = int(argv[i + 1])
			i += 1
		# start datetime
		elif argv[i] == '--start':
			start = datetime.strptime(argv[i + 1], '%Y-%m-%dT%H:%M')
			i += 1
		# end datetime
		elif argv[i] == '--end':
			end = datetime.strptime(argv[i + 1], '%Y-%m-%dT%H:%M')
			i += 1
		# valid success codes
		elif argv[i] == '--success':
			success = argv[i + 1].split(',')
			i += 1
			# construction of a regular expression for code matching
			correct = ""
			for s in success:
				x = ""
				for j in xrange(3):
					if s[j] == 'x':
						x += '[0-9]'
					else:
						x += s[j]
				correct += '|' + x
			correct = correct[1:]
			correct = re.compile(correct)
		else:
			# argument not preceded by a flag should be the input file
			file = open(argv[i], 'r')
		i += 1

	# if no parameter has been set, set interval to default value
	if interval == -1 and start == datetime.min and end == datetime.max:
		interval = 1

	# read file
	content = file.read().split('\n')[:-1]

	# regular expressions for datetime, endpoint and http request code
	date_match = re.compile('\[\S*')
	end_match = re.compile('/\S*\.html')
	code_match = re.compile('" \d+')

	# dictionary for the log entries
	# {datetime : {endpoint : (hits, misses)}}
	entries = {}

	# line by line evaluation
	for line in content:
		# found date and time of request
		date = str(date_match.findall(line)[0])
		date = date[1:]
		date = datetime.strptime(date, '%d/%b/%Y:%H:%M:%S')
		date = date - timedelta(seconds = date.second)

		# check date and time
		if start <= date and date <= end:
			end_point = str(end_match.findall(line)[0])
			code = str(code_match.findall(line)[0])
			code = code[2:]

			# search matching entry in the time interval
			found = False
			i = 0
			while i in xrange(interval) and not found:
				time = date - timedelta(minutes = i)
				if time in entries:
					if end_point in entries[time]:
						# update matching entry if found
						if correct.match(code):
							entries[time][end_point] = (entries[time][end_point][0] + 1, entries[time][end_point][1] + 1)
						else:
							entries[time][end_point] = (entries[time][end_point][0], entries[time][end_point][1] + 1)
						found = True
				i += 1

			if not found:
				# if there is no matching entry create a new one
				if date in entries:
					if correct.match(code):
						entries[date].update({end_point:(1, 1)})
					else:
						entries[date].update({end_point:(0, 1)})
				else:
					if correct.match(code):
						entries.update({date:{end_point:(1, 1)}})
					else:
						entries.update({date:{end_point:(0, 1)}})

		elif date > end:
			break

	# print sorted entries
	res = ""
	for e in sorted(entries):
		for p in sorted(entries[e]):
			res += e.strftime('%Y-%m-%dT%H:%M') + ' ' + str(interval) + ' '
			res += p + ' ' + ('%.2f'%(entries[e][p][0] * 100 / float(entries[e][p][1]))) + '\n'

	print res[:-1]

	file.close()