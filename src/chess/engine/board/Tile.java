package chess.engine.board;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import chess.engine.pieces.Piece;

public abstract class Tile {
	
	final private int tileCoordinate;
	final private boolean isOccupied;
	final private static Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createEmptyTiles();
	
	private Tile(final int c, final boolean o) {
		this.tileCoordinate = c;
		this.isOccupied = o;
	}
	
	public static Tile createTile(final int coordinate, final Piece piece) {
		return piece == null ? EMPTY_TILES_CACHE.get(coordinate) : new OccupiedTile(coordinate, piece);
	}
	
	private static Map<Integer, EmptyTile> createEmptyTiles() {
		final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();
		for (int i = 0; i < BoardUtil.BOARD_SQUARES; i++) {
			emptyTileMap.put(i, new EmptyTile(i));
		}
		return Collections.unmodifiableMap(emptyTileMap);
	}

	public int getCoordinate() {
		return tileCoordinate;
	}
	
	public boolean isOccupied() {
		return isOccupied;
	}
	
	public abstract Piece getPiece();
	
	public static final class EmptyTile extends Tile {

		private EmptyTile(int c) {
			super(c, false);
		}

		@Override
		public Piece getPiece() {
			return null;
		}
		
		@Override
		public String toString() {
			return "[ ]";
		}

	}
	
	public static final class OccupiedTile extends Tile {
		
		private final Piece piece;
		
		private OccupiedTile(final int c, final Piece p) {
			super(c, true);
			this.piece = p;
		}

		@Override
		public Piece getPiece() {
			return piece;
		}
		
		@Override
		public String toString() {
			return "[" + this.piece.toString() + "]";
		}

	}
}


