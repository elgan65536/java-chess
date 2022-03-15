package chess.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import chess.engine.PlayerColor;
import chess.engine.board.Board;
import static chess.engine.board.BoardUtil.*;
import chess.engine.board.Move;
import chess.engine.board.Tile;
import chess.engine.board.Move.MoveFactory;
import chess.engine.board.Move.PawnPromotion;
import chess.engine.pieces.Piece;
import chess.engine.pieces.*;
import chess.engine.player.MoveTransition;
import chess.engine.player.ai.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Table {
	
	private Tile selectedTile = null;
	private Tile destinationTile = null;
	private Piece selectedPiece = null;
	private final Stage gameStage;
	private final Scene gameScene;
	private final BoardPanel boardPanel;
	private final MoveLogPanel moveLogPanel;
	private final TakenPiecesPanel takenPiecesPanel;
	private final PromotionPanel promotionPanel;
	private final BorderPane gamePane = new BorderPane();
	final MenuBar tableMenu = createMenuBar();
	private Board chessBoard = Board.createStartingPosition();
	private boolean isFlipped = false;
	
	private final MoveStrategy strat = new ModifiedABPrune();
	
	public final static String PIECE_ICON_PATH = "file:art/pieces/";
	public final static String DARK_TILE_COLOR = "#999999";
	public final static String LIGHT_TILE_COLOR = "#cccccc";
	public final static double TILE_SIZE = 96;
	public final static double BOARD_HEIGHT = TILE_SIZE * BOARD_RANKS;
	public final static double BOARD_WIDTH = TILE_SIZE * BOARD_FILES;

	public Table() {
		this.gameStage = new Stage();
		this.gameStage.setTitle("Chess");
		this.boardPanel = new BoardPanel();
		this.moveLogPanel = new MoveLogPanel();
		this.takenPiecesPanel = new TakenPiecesPanel();
		this.promotionPanel = new PromotionPanel();
		this.gamePane.setTop(tableMenu);
		this.gamePane.setCenter(boardPanel);
		this.gamePane.setLeft(takenPiecesPanel);
		this.gamePane.setRight(moveLogPanel);
		this.gamePane.setBottom(promotionPanel);
		this.boardPanel.setAlignment(Pos.CENTER);
		this.promotionPanel.setAlignment(Pos.CENTER);
		this.promotionPanel.setVisible(false);
		this.gameStage.setMaximized(true);
		this.gameStage.getIcons().add(new Image(PIECE_ICON_PATH + "WN.png"));
		gameScene = new Scene(this.gamePane);
		gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.F) {
					isFlipped = !isFlipped;
					boardPanel.flip();
				}
			}
		});
		gameStage.setScene(this.gameScene);
		gameStage.show();
		
	}
	
	private MenuBar createMenuBar() {
		final MenuBar menu = new MenuBar();
		menu.getMenus().add(CreateFileMenu());
		return menu;
	}

	private Menu CreateFileMenu() {
		final Menu fileMenu = new Menu("File");
		final MenuItem newGame = new MenuItem("New Game");
		newGame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				selectedTile = null;
				selectedPiece = null;
				destinationTile = null;
				chessBoard = Board.createStartingPosition();
				boardPanel.drawBoard(chessBoard);
				moveLogPanel.clear();
				takenPiecesPanel.clear();
				if (promotionPanel.isVisible()) {
					promotionPanel.setVisible(false);
				}
				try {
					Platform.exitNestedEventLoop("PromotionLoop", null);
				} catch (Exception e) {}
			}
		});
		
		final MenuItem flip = new MenuItem("Flip Board (f)");
		flip.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				isFlipped = !isFlipped;
				boardPanel.flip();
			}
		});
		
		final MenuItem analyze = new MenuItem("Analyze");
		analyze.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				long timeTaken = 0;
				Move move = null;
				for (int i = 1; i < 50 && timeTaken < 2500L; i++) {
					long startTime = System.currentTimeMillis();
					move = strat.execute(chessBoard, i);
					timeTaken = System.currentTimeMillis() - startTime;
				}
				System.out.println(move);
			}
		});
				
		final MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Platform.exit();
			}
		});
		
		fileMenu.getItems().add(newGame);		
		fileMenu.getItems().add(flip);
		fileMenu.getItems().add(analyze);
		fileMenu.getItems().add(exit);		

		return fileMenu;
	}
	
	private class BoardPanel extends GridPane {
		
		final List<TilePanel> boardTiles;
		
		BoardPanel() {
			this.boardTiles = new ArrayList<TilePanel>();
			for (int i = 0; i < BOARD_SQUARES; i++) {
				final TilePanel tilePanel = new TilePanel(this, i);
				this.boardTiles.add(tilePanel);
				super.add(tilePanel, fileOf(i), rankOf(i));
				super.setRotate(isFlipped ? 180 : 0);
			}
			super.setMinSize(BOARD_WIDTH, BOARD_HEIGHT);
			super.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		}

		public void drawBoard(final Board board) {
			
			super.getChildren().clear();
			for (final TilePanel tilePanel : this.boardTiles) {
				tilePanel.drawTile(board);
				super.add(tilePanel, fileOf(tilePanel.tileCoordinate), rankOf(tilePanel.tileCoordinate));
			}
		}
		
		public TilePanel getTile(int i) {
			return boardTiles.get(i);
		}
		
		private void flip() {
			super.setRotate(isFlipped ? 180 : 0);
			for (TilePanel tilePanel : boardTiles) {
				tilePanel.flip();
			}
		}
	}
	
	private class TakenPiecesPanel extends VBox {
		
		private final TilePane whitePanel;
		private final TilePane blackPanel;
		
		TakenPiecesPanel() {
			super();
			this.whitePanel = new TilePane(Orientation.VERTICAL);
			this.blackPanel = new TilePane(Orientation.VERTICAL);
			this.whitePanel.setAlignment(Pos.TOP_LEFT);
			this.whitePanel.setPrefTileHeight(TILE_SIZE / 2);
			this.whitePanel.setPrefTileWidth(TILE_SIZE / 2);
			this.whitePanel.setPrefRows(8);
			this.blackPanel.setAlignment(Pos.BOTTOM_LEFT);
			this.blackPanel.setPrefTileHeight(TILE_SIZE / 2);
			this.blackPanel.setPrefTileWidth(TILE_SIZE / 2);
			this.blackPanel.setPrefRows(8);
			super.setAlignment(Pos.CENTER_RIGHT);
			super.setMinHeight(BOARD_RANKS * TILE_SIZE);
			super.setMinWidth(TILE_SIZE);
			super.getChildren().add(whitePanel);
			super.getChildren().add(blackPanel);
		}
		
		public void redraw(final MoveLogPanel moveLogPanel) {
			
			final List<Piece> whitePieces = new ArrayList<>();
			final List<Piece> blackPieces = new ArrayList<>();
			
			this.whitePanel.getChildren().clear();
			this.blackPanel.getChildren().clear();
			
			for(final Move move : moveLogPanel.getMoves()) {
				if (move.isCapture()) {
					final Piece takenPiece = move.getCapturedPiece();
					if (takenPiece.getPieceColor() == PlayerColor.WHITE) {
						whitePieces.add(takenPiece);
					} else {
						blackPieces.add(takenPiece);
					}
				}
			}
			Collections.sort(whitePieces);
			Collections.sort(blackPieces);
			Collections.reverse(whitePieces);
			for (final Piece piece : whitePieces) {
				this.whitePanel.getChildren().add(new ImageView(new Image(PIECE_ICON_PATH +  piece.getPieceColor().toString() + piece.getPieceType().toString() + ".png", TILE_SIZE / 2, TILE_SIZE / 2, true, true)));
			}
			for (final Piece piece : blackPieces) {
				this.blackPanel.getChildren().add(new ImageView(new Image(PIECE_ICON_PATH +  piece.getPieceColor().toString() + piece.getPieceType().toString() + ".png", TILE_SIZE / 2, TILE_SIZE / 2, true, true)));
			}
		}
		public void clear() {
			this.blackPanel.getChildren().clear();
			this.whitePanel.getChildren().clear();
		}
		
	}
	
	private class MoveLogPanel extends TableView<MoveLogRow>{
		
		private final ObservableList<Move> moves;
		
		MoveLogPanel() {
			this.moves = FXCollections.observableArrayList();
			TableColumn<MoveLogRow, String> whiteCol = new TableColumn<>("White");
			TableColumn<MoveLogRow, String> blackCol = new TableColumn<>("Black");
			whiteCol.setCellValueFactory(cellData -> cellData.getValue().whiteProperty());
			blackCol.setCellValueFactory(cellData -> cellData.getValue().blackProperty());
			super.getColumns().add(whiteCol);
			super.getColumns().add(blackCol);
			super.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		}
		
		public List<Move> getMoves() {
			return this.moves;
		}
		
		public void addMove(final Move move) {
			this.moves.add(move);
		}
		
		public int size() {
			return moves.size();
		}
		
		public void clear() {
			this.moves.clear();
			super.getItems().clear();
		}
		
		public boolean removeMove(final Move move) {
			return this.moves.remove(move);
		}
		
		public void redraw(final Board board) {
			MoveLogRow currentRow = new MoveLogRow();
			int RowNbr = 0;
			super.getItems().clear();
			for (final Move move : moves) {
				String moveText = move.toString();
				if (move == moves.get(moves.size() - 1)) {
					moveText += calculateCheck(board);
				}
				if (move.getMovedPiece().getPieceColor() == PlayerColor.WHITE) {
					RowNbr++;
					currentRow = new MoveLogRow();
					currentRow.setWhiteMove(RowNbr + ". " + moveText);
					super.getItems().add(currentRow);
				} else {
					currentRow.setBlackMove(moveText);
				}
			}
		}
		
		private String calculateCheck(final Board board) {
			if (board.getCurrentPlayer().isInStalemate()) {
				return " 1/2";
			}
			if (board.getCurrentPlayer().isInCheckmate()) {
				return "#";
			}
			if (board.getCurrentPlayer().isInCheck()) {
				return "+";
			}
			return "";
		}
		
	}
	
	private class MoveLogRow {
		
		private String whiteMove = "";
		private String blackMove = "";
		
		MoveLogRow() {}
		
		public String getWhiteMove() {
			return whiteMove;
		}

		public void setWhiteMove(String whiteMove) {
			this.whiteMove = whiteMove;
		}

		public String getBlackMove() {
			return blackMove;
		}

		public void setBlackMove(String blackMove) {
			this.blackMove = blackMove;
		}
		
		public StringProperty whiteProperty() {
			return new SimpleStringProperty(whiteMove);
		}
		
		public StringProperty blackProperty() {
			return new SimpleStringProperty(blackMove);
		}
		
	}
	
	private class PromotionPanel extends VBox {
				
		PromotionPanel() {
			Label promotionText = new Label("Which piece would you like to promote to?");
			promotionText.setAlignment(Pos.CENTER);
			super.getChildren().add(promotionText);
			HBox hbox = new HBox();
			Button btnQueen = new Button("Queen");
			Button btnRook = new Button("Rook");
			Button btnBishop = new Button("Bishop");
			Button btnKnight = new Button("Knight");
			
			btnQueen.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent ae) {
					try {
						Platform.exitNestedEventLoop("PromotionLoop", "Q");
					} catch (Exception e) {}
				}
			});
			btnRook.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent ae) {
					try {
						Platform.exitNestedEventLoop("PromotionLoop", "R");
					} catch (Exception e) {}
				}
			});
			btnBishop.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent ae) {
					try {
						Platform.exitNestedEventLoop("PromotionLoop", "B");
					} catch (Exception e) {}
				}
			});
			btnKnight.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent ae) {
					try {
						Platform.exitNestedEventLoop("PromotionLoop", "N");
					} catch (Exception e) {}
				}
			});
			
			hbox.getChildren().add(btnQueen);
			hbox.getChildren().add(btnRook);
			hbox.getChildren().add(btnBishop);
			hbox.getChildren().add(btnKnight);
			hbox.setAlignment(Pos.CENTER);
			super.getChildren().add(hbox);
		}
		
	}
	
	private class TilePanel extends StackPane {
		
		private final int tileCoordinate;
		
		TilePanel(final BoardPanel boardPanel, final int coordinate) {
			this.tileCoordinate = coordinate;
			super.setMinSize(TILE_SIZE, TILE_SIZE);
			super.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			assignTileColor();
			assignPieceIcon(chessBoard);
			super.setRotate(isFlipped ? 180 : 0);
			
			this.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(final MouseEvent event) {
					if (selectedTile == null) {
						selectedTile = chessBoard.getTile(coordinate);
						selectedPiece = selectedTile.getPiece();
						if (selectedPiece == null) {
							selectedTile = null;
						}
					} else {
						if (event.getButton() == MouseButton.SECONDARY) {
							selectedTile = null;
							destinationTile = null;
							selectedPiece = null;
							for (final TilePanel tilePanel : boardPanel.boardTiles) {
								tilePanel.highLightLegals(chessBoard);
							}
							return;
						}
						destinationTile = chessBoard.getTile(coordinate);
						Move move = MoveFactory.createMove(chessBoard, selectedTile.getCoordinate(), destinationTile.getCoordinate());
						MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
						if (transition.getMoveStatus().isDone()) {	
							if (move.isPromotion()) {
								promotionPanel.setVisible(true);
								Object o;
								try {
									o = Platform.enterNestedEventLoop("PromotionLoop");
								} catch (Exception e) {
									Platform.exitNestedEventLoop("PromotionLoop", null);
									o = Platform.enterNestedEventLoop("PromotionLoop");
								}
								promotionPanel.setVisible(false);
								if (o instanceof String) {
									final String s = (String) o;
									switch (s) {
									case "Q":
										move = new PawnPromotion(((PawnPromotion) move).getPawnMove(), new Queen(move.getMovedPiece().getPieceColor(), move.getDestinationCoordinate(), false));
										break;
									case "R":
										move = new PawnPromotion(((PawnPromotion) move).getPawnMove(), new Rook(move.getMovedPiece().getPieceColor(), move.getDestinationCoordinate(), false));
										break;
									case "B":
										move = new PawnPromotion(((PawnPromotion) move).getPawnMove(), new Bishop(move.getMovedPiece().getPieceColor(), move.getDestinationCoordinate(), false));
										break;
									case "N":
										move = new PawnPromotion(((PawnPromotion) move).getPawnMove(), new Knight(move.getMovedPiece().getPieceColor(), move.getDestinationCoordinate(), false));
										break;
									default:
										return;
									}
									transition = chessBoard.getCurrentPlayer().makeMove(move);
								} else {
									return;
								}
							} else {
								if (promotionPanel.isVisible()) {
									promotionPanel.setVisible(false);
								}
								try {
									Platform.exitNestedEventLoop("PromotionLoop", null);
								} catch (Exception e) {}
							}
							chessBoard = transition.getTransitionBoard();
							moveLogPanel.addMove(move);
							moveLogPanel.redraw(chessBoard);
							takenPiecesPanel.redraw(moveLogPanel);
							selectedTile = null;
							destinationTile = null;
							selectedPiece = null;
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									boardPanel.drawBoard(chessBoard);
								}
							});
							
						} else {
							selectedTile = destinationTile;
							selectedPiece = selectedTile.getPiece();
							destinationTile = null;
						}
					}
					for (final TilePanel tilePanel : boardPanel.boardTiles) {
						tilePanel.highLightLegals(chessBoard);
					}
				}
			});
		}
		
		public void drawTile(final Board board) {
			assignPieceIcon(board);
			super.setRotate(isFlipped ? 180 : 0);
		}
		
		public void highLightLegals(final Board board) {
			for (int i = 5; i >= 0; i--) {
				if (getChildren().size() > i) {
					if (getChildren().get(i) instanceof ImageView) {
						if (((ImageView) (getChildren().get(i))).getImage().getUrl().equals("file:art/misc/selectBorder.png")) {
							getChildren().remove(i);
						}
					}
				}
			}
			if (selectedPiece != null && selectedPiece.getPieceColor() == board.getCurrentPlayer().getColor()) {
				for (final Move move : board.getCurrentPlayer().getLegalMovesNoCheck()) {
					if (move.getMovedPiece().equals(selectedPiece) && move.getDestinationCoordinate() == this.tileCoordinate) {
						getChildren().add(new ImageView(new Image("file:art/misc/selectBorder.png", TILE_SIZE, TILE_SIZE, true, false)));
					}
				}
			}
		}

		private void assignPieceIcon(final Board board) {
			if (!getChildren().isEmpty()) {
				getChildren().remove(0);
			}
			if (board.getTile(tileCoordinate).isOccupied()) {
				String fileName = PIECE_ICON_PATH + 
								  board.getTile(tileCoordinate).getPiece().getPieceColor().toString() + 
								  board.getTile(tileCoordinate).getPiece().getPieceType().toString() + 
								  ".png";
				final Image pieceImage = new Image(fileName, TILE_SIZE, TILE_SIZE, true, true);
				final ImageView imageView = new ImageView(pieceImage);
				getChildren().add(imageView);
			}
		}
		
		private void assignTileColor() {
			setStyle("-fx-background-color: " + 
					((fileOf(tileCoordinate) + 
					  rankOf(tileCoordinate) + 
					  BOARD_FILES + 
					  BOARD_RANKS) % 2 == 0 ? Table.LIGHT_TILE_COLOR : Table.DARK_TILE_COLOR));
		}
		
		private void flip() {
			super.setRotate(isFlipped ? 180 : 0);
		}

	}
	
}