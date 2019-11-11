import random

COMPANIES = ['Coca-Cola', 'AdForm', 'PwC', 'Deloitte', 'Google']
MAX_BUDGET = 10
ACTIONS = 1000

f = open("transactions.txt", "w")
for i in range(ACTIONS):
	fromCompany = random.randint(0, len(COMPANIES) - 1)
	budget = random.randint(0, MAX_BUDGET)
	f.write(COMPANIES[fromCompany] + ' ' + str(budget) + '\n')
f.close()

#open and read the file after the appending:
f = open("transactions.txt", "r")
print(f.read()) 
