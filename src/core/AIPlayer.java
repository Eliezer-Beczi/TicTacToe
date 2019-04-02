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
            int aux = (int) Math.pow(10d, i - 1);
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
        int[] result;

        if (size == 3) {
            result = minimax(mySymbol, Integer.MIN_VALUE, Integer.MAX_VALUE);
        } else {
            result = minimax(depth, mySymbol, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }

        return new int[]{result[1], result[2]};
    }

    // full depth minimax WITH alpha–beta pruning
    private int[] minimax(String symbol, int alpha, int beta) {
        ArrayList<int[]> moves = generateMoves();

        int score;
        int bestRow = -1;
        int bestCol = -1;

        if (hasWon(mySymbol)) {
            return new int[]{1, bestRow, bestCol};
        } else if (hasWon(opponentSymbol)) {
            return new int[]{-1, bestRow, bestCol};
        } else if (moves.isEmpty()) {
            return new int[]{0, bestRow, bestCol};
        } else {
            for (int[] move : moves) {
                board[move[0]][move[1]] = symbol;

                if (symbol.equals(mySymbol)) {
                    score = minimax(opponentSymbol, alpha, beta)[0];

                    if (score > alpha) {
                        alpha = score;
                        bestRow = move[0];
                        bestCol = move[1];
                    }
                } else {
                    score = minimax(mySymbol, alpha, beta)[0];

                    if (score < beta) {
                        beta = score;
                        bestRow = move[0];
                        bestCol = move[1];
                    }
                }

                board[move[0]][move[1]] = "";

                if (alpha >= beta) {
                    break;
                }
            }

            if (symbol.equals(mySymbol)) {
                return new int[]{alpha, bestRow, bestCol};
            } else {
                return new int[]{beta, bestRow, bestCol};
            }
        }
    }

    // depth limited minimax WITH alpha–beta pruning
    private int[] minimax(int depth, String symbol, int alpha, int beta) {
        ArrayList<int[]> moves = generateMoves();

        int score;
        int bestRow = -1;
        int bestCol = -1;

        if (hasWon(mySymbol)) {
            return new int[]{heuristicArray[requiredSymbols][0], bestRow, bestCol};
        } else if (hasWon(opponentSymbol)) {
            return new int[]{heuristicArray[0][requiredSymbols], bestRow, bestCol};
        } else if (moves.isEmpty()) {
            return new int[]{0, bestRow, bestCol};
        } else if (depth == 0) {
            return new int[]{evaluate(), bestRow, bestCol};
        } else {
            for (int[] move : moves) {
                board[move[0]][move[1]] = symbol;

                if (symbol.equals(mySymbol)) {
                    score = minimax(depth - 1, opponentSymbol, alpha, beta)[0];

                    if (score > alpha) {
                        alpha = score;
                        bestRow = move[0];
                        bestCol = move[1];
                    }
                } else {
                    score = minimax(depth - 1, mySymbol, alpha, beta)[0];

                    if (score < beta) {
                        beta = score;
                        bestRow = move[0];
                        bestCol = move[1];
                    }
                }

                board[move[0]][move[1]] = "";

                if (alpha >= beta) {
                    break;
                }
            }

            if (symbol.equals(mySymbol)) {
                return new int[]{alpha, bestRow, bestCol};
            } else {
                return new int[]{beta, bestRow, bestCol};
            }
        }
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
        int score = 0;

        for (int i = 0; i < size - requiredSymbols + 1; ++i) {
            int myCounter = 0;
            int opponentCounter = 0;

            for (int j = 0; j < requiredSymbols; ++j) {
                if (board[row][i + j].equals(mySymbol)) {
                    ++myCounter;
                } else if (board[row][i + j].equals(opponentSymbol)) {
                    ++opponentCounter;
                }
            }

            score += heuristicArray[myCounter][opponentCounter];
        }

        return score;
    }

    private int evaluateColumn(int col) {
        int score = 0;

        for (int i = 0; i < size - requiredSymbols + 1; ++i) {
            int myCounter = 0;
            int opponentCounter = 0;

            for (int j = 0; j < requiredSymbols; ++j) {
                if (board[i + j][col].equals(mySymbol)) {
                    ++myCounter;
                } else if (board[i + j][col].equals(opponentSymbol)) {
                    ++opponentCounter;
                }
            }

            score += heuristicArray[myCounter][opponentCounter];
        }

        return score;
    }

    private int evaluateDiagonal(int direction) {
        int score = 0;

        if (direction == 1) {
            for (int diagonal = 0; diagonal < size - requiredSymbols + 1; ++diagonal) {
                for (int row = diagonal; row < size - requiredSymbols + 1; ++row) {
                    int myBelowCounter = 0;
                    int opponentBelowCounter = 0;

                    int myAboveCounter = 0;
                    int opponentAboveCounter = 0;

                    for (int col = 0; col < requiredSymbols; ++col) {
                        String belowSymbol = board[row + col][row + col - diagonal];
                        String aboveSymbol = board[row + col - diagonal][row + col];

                        if (belowSymbol.equals(mySymbol)) {
                            ++myBelowCounter;
                        } else if (belowSymbol.equals(opponentSymbol)) {
                            ++opponentBelowCounter;
                        }

                        if (aboveSymbol.equals(mySymbol)) {
                            ++myAboveCounter;
                        } else if (aboveSymbol.equals(opponentSymbol)) {
                            ++opponentAboveCounter;
                        }
                    }

                    score += heuristicArray[myBelowCounter][opponentBelowCounter] +
                            heuristicArray[myAboveCounter][opponentAboveCounter];
                }
            }
        } else {
            for (int diagonal = 0; diagonal < size - requiredSymbols + 1; ++diagonal) {
                for (int row = diagonal; row < size - requiredSymbols + 1; ++row) {
                    int myBelowCounter = 0;
                    int opponentBelowCounter = 0;

                    int myAboveCounter = 0;
                    int opponentAboveCounter = 0;

                    for (int col = 0; col < requiredSymbols; ++col) {
                        String belowSymbol = board[row + col][size - (row + 1) - col + diagonal];
                        String aboveSymbol = board[row + col - diagonal][size - (row + 1) - col];

                        if (belowSymbol.equals(mySymbol)) {
                            ++myBelowCounter;
                        } else if (belowSymbol.equals(opponentSymbol)) {
                            ++opponentBelowCounter;
                        }

                        if (aboveSymbol.equals(mySymbol)) {
                            ++myAboveCounter;
                        } else if (aboveSymbol.equals(opponentSymbol)) {
                            ++opponentAboveCounter;
                        }
                    }

                    score += heuristicArray[myBelowCounter][opponentBelowCounter] +
                            heuristicArray[myAboveCounter][opponentAboveCounter];
                }
            }
        }

        return score;
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

    private boolean checkDiagonal(int direction, String symbol) {
        if (direction == 1) {
            for (int diagonal = 0; diagonal < size - requiredSymbols + 1; ++diagonal) {
                int belowCounter = 0;
                int aboveCounter = 0;

                for (int row = diagonal; row < size; ++row) {
                    String belowSymbol = board[row][row - diagonal];
                    String aboveSymbol = board[row - diagonal][row];

                    if (belowSymbol.equals(symbol)) {
                        ++belowCounter;

                        if (belowCounter == requiredSymbols) {
                            return true;
                        }
                    } else {
                        belowCounter = 0;
                    }

                    if (aboveSymbol.equals(symbol)) {
                        ++aboveCounter;

                        if (aboveCounter == requiredSymbols) {
                            return true;
                        }
                    } else {
                        aboveCounter = 0;
                    }
                }
            }
        } else {
            for (int diagonal = 0; diagonal < size - requiredSymbols + 1; ++diagonal) {
                int belowCounter = 0;
                int aboveCounter = 0;

                for (int row = diagonal; row < size; ++row) {
                    String belowSymbol = board[row][size - (row + 1) + diagonal];
                    String aboveSymbol = board[row - diagonal][size - (row + 1)];

                    if (belowSymbol.equals(symbol)) {
                        ++belowCounter;

                        if (belowCounter == requiredSymbols) {
                            return true;
                        }
                    } else {
                        belowCounter = 0;
                    }

                    if (aboveSymbol.equals(symbol)) {
                        ++aboveCounter;

                        if (aboveCounter == requiredSymbols) {
                            return true;
                        }
                    } else {
                        aboveCounter = 0;
                    }
                }
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
