import java.util.LinkedList;
import java.util.Queue;

import game.board.compact.*;

public class DeadSquareDetector {
    public static boolean[][] detect(BoardCompact boardCompact) {
        int[][] board = boardCompact.tiles.clone();
        int width = boardCompact.width();
        int height = boardCompact.height();

        // create board
        boolean[][] result = new boolean[boardCompact.width()][boardCompact.height()];

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                result[x][y] = isSquareDead(x, y, board);
            }
        }

        return result;

    }


    private static boolean isSquareDead(int x, int y, int[][] board) {
        // start bread-first search from (x, y)

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        // keep track of visited tiles
        boolean[][] visited = new boolean[board.length][board[0].length];
        visited[x][y] = true;

        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{x, y});

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int cx = current[0];
            int cy = current[1];

            if (CTile.forSomeBox(board[cx][cy])) {
                return false;
            }

            for (int[] direction : directions) {
                int nx = cx + direction[0];
                int ny = cy + direction[1];

                int mx = cx - direction[0];
                int my = cy - direction[1];


                // check if we are still in the board
                if (nx < 0 || nx >= board.length || ny < 0 || ny >= board[0].length ||
                        mx < 0 || mx >= board.length || my < 0 || my >= board[0].length) {
                    continue;
                }

                // check if we have already visited this tile
                if (visited[nx][ny]) {
                    continue;
                }

                // check that we are not on a wall
                if (CTile.isWall(board[nx][ny]) || CTile.isWall(board[mx][my])) {
                    continue;
                }

                // add to queue
                queue.add(new int[]{nx, ny});
                visited[nx][ny] = true;
            }

        }

        return true;
    }
        
}
