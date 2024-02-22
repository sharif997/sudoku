def is_valid_move(board, row, col, num):
    if num in board[row]:
        return False

    if num in [board[i][col] for i in range(9)]:
        return False

    start_row, start_col = 3 * (row // 3), 3 * (col // 3)
    for i in range(start_row, start_row + 3):
        for j in range(start_col, start_col + 3):
            if board[i][j] == num:
                return False

    return True


def solve_sudoku(board):
    empty_cell = find_empty_cell(board)
    if not empty_cell:
        return True

    row, col = empty_cell
    for num in range(1, 10):
        if is_valid_move(board, row, col, num):
            board[row][col] = num
            if solve_sudoku(board):
                return True
            board[row][col] = 0

    return False


def find_empty_cell(board):
    for i in range(9):
        for j in range(9):
            if board[i][j] == 0:
                return (i, j)
    return None


def print_board(board):
    for row in board:
        print(" ".join(map(str, row)))

def solve_sudoku_file(filename):
    with open(filename, "r") as file:
        sudoku_lines = file.readlines()

    sudoku_count = 0
    sudoku_index = 0
    sudoku_grids = []
    sudoku_iter = iter(sudoku_lines)
    for line in sudoku_iter:
        if "SUDOKU" in line:
            if sudoku_count > 0:
                sudoku_index += 1
                print("Sudoku", sudoku_index)
                if solve_sudoku(sudoku_grids[-1]):
                    print_board(sudoku_grids[-1])
                else:
                    print("No solution exists for this Sudoku puzzle.")
                print()
            sudoku_count += 1
            sudoku_grids.append([[int(num) for num in next(sudoku_iter).strip()] for _ in range(9)])
    if sudoku_count > 0:
        sudoku_index += 1
        print("Sudoku", sudoku_index)
        if solve_sudoku(sudoku_grids[-1]):
            print_board(sudoku_grids[-1])
        else:
            print("No solution exists for this Sudoku puzzle.")

if __name__ == "__main__":
    solve_sudoku_file("assignment 2 sudoku.txt")