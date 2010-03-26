first = True
startTime = None

transactions = [0 for x in range(0, 100)]
responseTimes = [[] for x in range(0, 100)]

currentTime = 0
currentCounter = 0

file = open("C:/temp/performance.csv")

for line in file.readlines():
    # Split the CSV line
    elements = line.rsplit(',')
    
    # Check if thats the first line in the file 
    if first:
        first = False
        startTime = long(elements[0])
        continue
    
    # Get the index of this entry
    time = long(elements[0]) + long(elements[1]) - startTime 
    timeIndex = time / 5000
    
    if timeIndex < len(transactions):
        # Update counters    
        transactions[timeIndex] += 1
        
        # Update response time
        times = responseTimes[timeIndex]
        times.append(float(elements[1]))
    
file.close()

file = open("C:/temp/performanceResponse.csv", "a")
for tx in transactions:
    file.write("%i," % tx)
file.write("\n")
file.close()

file = open("C:/temp/performanceDelay.csv", "a")
for times in responseTimes:
    if len(times) != 0:
        sum = 0
        for x in times:
            sum += x
        sum = sum / len(times)
        file.write("%f," % sum)
    else:
        file.write("%f," % 1000)
file.write('\n')
file.close()
