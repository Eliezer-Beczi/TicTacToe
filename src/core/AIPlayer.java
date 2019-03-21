package core;

import java.util.ArrayList;

public class AIPlayer {
    private int size;
    private String[][] board;

    private String mySymbol;
    private String opponentSymbol;

    private int requiredSymbols;
    private int[][] heuristicArray;

    public AIPlayer(int size, int requiredSymbols, String mySymbol, String opponentSymbol) {
        this.size = size;
        board = new String[size][size];
        this.requiredSymbols = requiredSymbols;
        heuristicArray = new int[requiredSymbols + 1][requiredSymbols + 1];

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                board[i][j] = "";
            }
        }

        for (int i = 1; i < requiredSymbols + 1; ++i) {
            int aux = (int) Math.pow(10, i - 1);
            heuristicArray[i][0] = aux;
            heuristicArray[0][i] = -aux;
        }

        this.mySymbol = mySymbol;
        this.opponentSymbol = opponentSymbol;
    }

    public void updateBoard(int row, int col, String symbol) {
        board[row][col] = symbol;
    }

    public int[] next(int depth) {
        int[] result = minimax(depth, mySymbol);
        return new int[]{result[1], result[2]};
    }

    private int[] minimax(int depth, String symbol) {
        ArrayList<int[]> moves = generateMoves();

        int bestScore = (symbol.equals(mySymbol)) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int currentScore;
        int bestRow = -1;
        int bestCol = -1;

        if (moves.isEmpty() || depth == 0) {
            bestScore = evaluate();
        } else {
            for (int[] move : moves) {
                board[move[0]][move[1]] = symbol;

                if (symbol.equals(mySymbol)) {
                    currentScore = minimax(depth - 1, opponentSymbol)[0];

                    if (currentScore > bestScore) {
                        bestScore = currentScore;
                        bestRow = move[0];
                        bestCol = move[1];
                    }
                } else {
                    currentScore = minimax(depth - 1, mySymbol)[0];

                    if (currentScore < bestScore) {
                        bestScore = currentScore;
                        bestRow = move[0];
                        bestCol = move[1];
                    }
                }

                board[move[0]][move[1]] = "";
            }
        }

        return new int[]{bestScore, bestRow, bestCol};
    }

    public ArrayList<int[]> generateMoves() {
        ArrayList<int[]> moves = new ArrayList<>();

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (board[i][j].isEmpty()) {
                    moves.add(new int[]{i, j});
                }
            }
        }

        return moves;
    }

    private int evaluate() {
        int score = 0;

        for (int i = 0; i < size; ++i) {
            score += evaluateRow(i) + evaluateColumn(i);
        }

        score += evaluateDiagonal(1) + evaluateDiagonal(-1);
        return score;
    }

    private int evaluateRow(int row) {
        int myCounter = 0;
        int opponentCounter = 0;

        for (int j = 0; j < size; ++j) {
            if (board[row][j].equals(mySymbol)) {
                ++myCounter;
            } else if (board[row][j].equals(opponentSymbol)) {
                ++opponentCounter;
            }
        }

        return heuristicArray[myCounter][opponentCounter];
    }

    private int evaluateColumn(int col) {
        int myCounter = 0;
        int opponentCounter = 0;

        for (int i = 0; i < size; ++i) {
            if (board[i][col].equals(mySymbol)) {
                ++myCounter;
            } else if (board[i][col].equals(opponentSymbol)) {
                ++opponentCounter;
            }
        }

        return heuristicArray[myCounter][opponentCounter];
    }

    private int evaluateDiagonal(int diagonal) {
        int myCounter = 0;
        int opponentCounter = 0;

        if (diagonal == 1) {
            for (int i = 0; i < size; ++i) {
                if (board[i][i].equals(mySymbol)) {
                    ++myCounter;
                } else if (board[i][i].equals(opponentSymbol)) {
                    ++opponentCounter;
                }
            }
        } else {
            int row = 0;
            int col = size - 1;

            while (row < size) {
                if (board[row][col].equals(mySymbol)) {
                    ++myCounter;
                } else if (board[row][col].equals(opponentSymbol)) {
                    ++opponentCounter;
                }

                ++row;
                --col;
            }
        }

        return heuristicArray[myCounter][opponentCounter];
    }

    private boolean checkRow(int row, String symbol) {
        int counter = 0;

        for (int j = 0; j < size; ++j) {
            if (board[row][j].equals(symbol)) {
                ++counter;

                if (counter == requiredSymbols) {
                    return true;
                }
            } else {
                counter = 0;
            }
        }

        return false;
    }

    private boolean checkColumn(int col, String symbol) {
        int counter = 0;

        for (int i = 0; i < size; ++i) {
            if (board[i][col].equals(symbol)) {
                ++counter;

                if (counter == requiredSymbols) {
                    return true;
                }
            } else {
                counter = 0;
            }
        }

        return false;
    }

    private boolean checkDiagonal(int diagonal, String symbol) {
        int counter = 0;

        if (diagonal == 1) {
            for (int i = 0; i < size; ++i) {
                if (board[i][i].equals(symbol)) {
                    ++counter;

                    if (counter == requiredSymbols) {
                        return true;
                    }
                } else {
                    counter = 0;
                }
            }
        } else {
            int row = 0;
            int col = size - 1;

            while (row < size) {
                if (board[row][col].equals(symbol)) {
                    ++counter;

                    if (counter == requiredSymbols) {
                        return true;
                    }
                } else {
                    counter = 0;
                }

                ++row;
                --col;
            }
        }

        return false;
    }

    public boolean hasWon(String symbol) {
        for (int i = 0; i < size; ++i) {
            if (checkRow(i, symbol) || checkColumn(i, symbol)) {
                return true;
            }
        }

        return checkDiagonal(1, symbol) || checkDiagonal(-1, symbol);
    }
}
