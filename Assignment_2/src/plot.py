import subprocess
import numpy as np
import matplotlib.pyplot as plt
import sys
import os

def measure_exec_time(rows, cols, prog):
    if prog == "java":
        result = subprocess.run(["java", "ThreadedStrassen"], input=str(rows), capture_output=True, text=True)
    else:
        result = subprocess.run(["./Strassen"], input=f"{str(rows)} {str(cols)}", capture_output=True, text=True)

    output = result.stdout.splitlines()

    if prog == "java":
        time_str = output[-1].split()[-1]
    else:
        time_str = output[-1].split()[-2]
    print(time_str)
    return float(time_str)

matrix_list = [64, 128, 256, 512, 1024, 2048, 3096]

prog = 0
if len(sys.argv) > 1:
    prog = sys.argv[1]
exec_times = []
for size in matrix_list:
    exec_time = measure_exec_time(size, size, prog)
    exec_times.append(exec_time)

# Plot the results
plt.plot(matrix_list, exec_times, marker='o')
plt.title(f'Execution Time vs. Matrix Size {prog.title()} program')
plt.xlabel('Matrix Size')
plt.ylabel('Execution Time (Miliseconds)')
plt.grid(True)
plt.show()