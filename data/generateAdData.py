import random
import sys

COMPANIES = ['Zabka', 'Biedronka']
MAX_BUDGET = 10
ACTIONS = 1000

if(len(sys.argv) < 3):
	print('Insufficient number of arguments, setting default values...')
else:
	MAX_BUDGET = int(sys.argv[1])
	ACTIONS = int(sys.argv[2])

f = open("transactions.txt", "w")
for i in range(ACTIONS):
	fromCompany = random.randint(0, len(COMPANIES) - 1)
	budget = random.randint(0, MAX_BUDGET)
	f.write(COMPANIES[fromCompany] + ' ' + str(budget) + '\n')
f.close()

#open and read the file after the appending:
f = open("transactions.txt", "r")
print(f.read()) 
