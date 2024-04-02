def knapsack_bfs(weights, values, capacity):
    n = len(weights)
    max_value = 0
    
    queue = [(0, 0, 0)]  # (value, weight, index)
    
    while queue:
        curr_value, curr_weight, index = queue.pop(0)
        
        if curr_weight <= capacity:
            max_value = max(max_value, curr_value)
        
        if index < n:
            # Explore both possibilities: adding and excluding the current item
            queue.append((curr_value + values[index], curr_weight + weights[index], index + 1))  # Add current item
            queue.append((curr_value, curr_weight, index + 1))  # Exclude current item
    
    return max_value

def knapsack_dfs(weights, values, capacity):
    def dfs(index, curr_weight, curr_value):
        nonlocal max_value
        if curr_weight > capacity:
            return
        if index == len(weights):
            max_value = max(max_value, curr_value)
            return
        
        # Include the current item
        dfs(index + 1, curr_weight + weights[index], curr_value + values[index])
        
        # Exclude the current item
        dfs(index + 1, curr_weight, curr_value)
    
    max_value = 0
    dfs(0, 0, 0)
    return max_value

def read_knapsack_data(file_path):
    items = []
    weights = []
    values = []
    
    with open(file_path, 'r') as file:
        lines = file.readlines()
        for line in lines[1:]:  # Skip the header line
            if line.strip() == 'EOF':
                break  # Break the loop if 'EOF' is encountered
            data = line.strip().split()
            items.append(int(data[0]))
            values.append(int(data[1]))
            weights.append(int(data[2]))
    
    return items, weights, values
# Example usage:
file_path = './items.txt'
items, weights, values = read_knapsack_data(file_path)
capacity = 420  # Example capacity
print("BFS Max Value:", knapsack_bfs(weights, values, capacity))
print("DFS Max Value:", knapsack_dfs(weights, values, capacity))