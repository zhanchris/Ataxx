/* Skeleton code copyright (C) 2008, 2022 Paul N. Hilfinger and the
 * Regents of the University of California.  Do not distribute this or any
 * derivative work without permission. */

package ataxx;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ataxx.PieceColor.*;
import static java.lang.Math.min;
import static java.lang.Math.max;

/** A Player that computes its own moves.
 *  @author Chris Zhan
 */
class AI extends Player {

    /** Maximum minimax search depth before going to static evaluation. */
    private static final int MAX_DEPTH = 4;
    /** A position magnitude indicating a win (for red if positive, blue
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI for GAME that will play MYCOLOR. SEED is used to initialize
     *  a random-number generator for use in move computations.  Identical
     *  seeds produce identical behaviour. */
    AI(Game game, PieceColor myColor, long seed) {
        super(game, myColor);
        _random = new Random(seed);
    }

    @Override
    boolean isAuto() {
        return true;
    }

    @Override
    String getMove() {
        if (!getBoard().canMove(myColor())) {
            game().reportMove(Move.pass(), myColor());
            return "-";
        }
        Main.startTiming();
        Move move = findMove();
        Main.endTiming();
        game().reportMove(move, myColor());
        return move.toString();
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(getBoard());
        _lastFoundMove = null;
        if (myColor() == RED) {
            minMax(b, MAX_DEPTH, true, 1, -INFTY, INFTY);
        } else {
            minMax(b, MAX_DEPTH, true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to the findMove method
     *  above. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */
    private int minMax(Board board, int depth, boolean saveMove,
                       int sense, int alpha, int beta) {
        /* We use WINNING_VALUE + depth as the winning value so as to favor
         * wins that happen sooner rather than later (depth is larger the
         * fewer moves have been made. */
        if (depth == 0 || board.getWinner() != null) {
            return staticScore(board, WINNING_VALUE + depth);
        }

        Move best;
        best = null;
        int bestScore;
        bestScore = sense * -INFTY;

        List<Move> possibleMoves = legalMoves(board);
        for (Move m : possibleMoves) {
            board.makeMove(m);
            if (sense == 1) {
                int response = minMax(board, depth - 1, false,
                        -sense, alpha, beta);
                board.undo();
                if (response > bestScore) {
                    best = m;
                    bestScore = response;
                    alpha = max(alpha, bestScore);
                    if (alpha >= beta) {
                        return bestScore;
                    }
                }
            } else {
                int response = minMax(board, depth - 1, false,
                        sense, alpha, beta);
                board.undo();
                if (response < bestScore) {
                    best = m;
                    bestScore = response;
                    beta = min(beta, bestScore);
                    if (alpha >= beta) {
                        return bestScore;
                    }
                }

            }
        }

        if (possibleMoves.isEmpty()) {
            best = Move.PASS;
        }

        if (saveMove) {
            _lastFoundMove = best;
        }
        return bestScore;
    }

    /** Return a heuristic value for BOARD.  This value is +- WINNINGVALUE in
     *  won positions, and 0 for ties. */
    private int staticScore(Board board, int winningValue) {
        PieceColor winner = board.getWinner();
        if (winner != null) {
            return switch (winner) {
            case RED -> winningValue;
            case BLUE -> -winningValue;
            default -> 0;
            };
        }
        return board.numPieces(RED) - board.numPieces(BLUE);
    }

    /** Returns a list of all legal moves for the player whose turn
     * it is on BOARD. */
    private List<Move> legalMoves(Board board) {
        ArrayList<char[]> myPieces = new ArrayList<>();
        for (char c = 'a'; c <= 'g'; c += 1) {
            for (char r = '7'; r >= '1'; r -= 1) {
                if (board.get(c, r) == board.whoseMove()) {
                    myPieces.add(new char[] {c, r});
                }
            }
        }
        ArrayList<Move> legal = new ArrayList<>();
        for (int i = 0; i < myPieces.size(); i += 1) {
            char myCol = myPieces.get(i)[0];
            char myRow = myPieces.get(i)[1];
            for (char col1 = (char) (myPieces.get(i)[0] - 2);
                 col1 <  (char) (myPieces.get(i)[0] + 3); col1 += 1) {
                for (char row1 = (char) (myPieces.get(i)[1] + 2);
                    row1 > (char) (myPieces.get(i)[1] - 3); row1 -= 1) {
                    if (col1 < 'a') {
                        continue;
                    } else if (row1 > '7') {
                        continue;
                    }
                    if (board.legalMove(myCol, myRow, col1, row1)) {
                        legal.add(Move.move(myCol, myRow, col1, row1));
                    }
                }
            }
        }
        return legal;
    }



    /** Pseudo-random number generator for move computation. */
    private Random _random = new Random();
}
