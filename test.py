class Item:
    def __init__(self, id, benefit, weight):
        # Constructor for the Item class
        self.id = id
        self.benefit = benefit
        self.weight = weight

class Node:
    def __init__(self, level, profit, weight, taken):
        # Constructor for the Node class
        self.level = level
        self.profit = profit
        self.weight = weight
        self.taken = taken

# List of items with their id, benefit, and weight
items = []
items.append(Item(1, 20, 15))
items.append(Item(2, 40, 32))

# Capacity of the knapsack
capacity = 420

# Number of items
n = len(items)

# Breadth-first search (BFS) function to find the maximum profit
def bfs():
    # Initialize the root node
    root = Node(0, 0, 0, [])

    # Initialize the queue with the root node
    queue = [root]

    # Initialize the maximum profit
    maxProfit = 0

    # Continue BFS until the queue is empty
    while queue:
        # Get the current node from the front of the queue
        curr = queue.pop(0)

        # If all items are considered
        if curr.level == n:
            # Check if the current configuration is valid and update maxProfit if needed
            if curr.weight <= capacity and curr.profit > maxProfit:
                maxProfit = curr.profit
            continue

        # Consider including and excluding the current item
        taken = curr.taken[:]
        notTaken = curr.taken[:]

        taken.append(True)
        notTaken.append(False)

        include = Node(curr.level+1, curr.profit+items[curr.level].benefit, curr.weight+items[curr.level].weight, taken)
        exclude = Node(curr.level+1, curr.profit, curr.weight, notTaken)

        # Add the included and excluded nodes to the queue
        queue.append(include)
        queue.append(exclude)

    return maxProfit

# Depth-first search (DFS) function to find the maximum profit
def dfs(node, maxProfit):
    # If all items are considered
    if node.level == n:
        # Check if the current configuration is valid and update maxProfit if needed
        if node.weight <= capacity and node.profit > maxProfit[0]:
            maxProfit[0] = node.profit
        return

    # Consider including the current item
    taken = node.taken[:]
    taken.append(True)

    include = Node(node.level+1, node.profit + items[node.level].benefit, node.weight + items[node.level].weight, taken)

    # Recursively call DFS for the included node
    dfs(include, maxProfit)

    # Consider excluding the current item
    notTaken = node.taken[:]
    notTaken.append(False)

    exclude = Node(node.level+1, node.profit, node.weight, notTaken)

    # Recursively call DFS for the excluded node
    dfs(exclude, maxProfit)

# Initialize the root node for DFS
root = Node(0, 0, 0, [])
# Initialize maxProfit as a list to pass by reference
maxProfit = [0]
# Call DFS to find the maximum profit
dfs(root, maxProfit)
# Print the maximum profit found by DFS
print("DFS Max Profit: ", maxProfit[0])

# Call BFS to find the maximum profit
print("BFS Max Profit: ", bfs())