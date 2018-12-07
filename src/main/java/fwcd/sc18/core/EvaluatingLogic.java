package fwcd.sc18.core;

import java.util.stream.Stream;

import com.antelmann.game.GameMove;
import com.antelmann.game.GamePlay;
import fwcd.sc18.agbinds.AGGameState;
import fwcd.sc18.agbinds.AGMove;
import fwcd.sc18.evaluator.MoveEvaluator;
import fwcd.sc18.trainer.core.VirtualClient;

import sc.plugin2018.AbstractClient;
import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.shared.PlayerColor;

/**
 * A logic based upon evaluating moves by assigning
 * a numeric rating to a move/state-combination.
 */
public abstract class EvaluatingLogic extends TemplateLogic implements MoveEvaluator {
	private boolean parallelize = true;
	
	public EvaluatingLogic(VirtualClient client) {
		super(client);
	}
	
	public EvaluatingLogic(AbstractClient client) {
		super(client);
	}
	
	@Override
	protected Move selectMove(GameState gameBeforeMove, Player me) {
		Stream<Move> moves = gameBeforeMove.getPossibleMoves().stream();
		
		if (parallelize) {
			moves = moves.parallel();
		}
		
		return moves
				.max((a, b) -> Float.compare(
						evaluateMove(a, gameBeforeMove, me),
						evaluateMove(b, gameBeforeMove, me)
				))
				.orElseThrow(IllegalStateException::new);
	}
	
	protected abstract float evaluateMove(Move move, GameState gameBeforeMove, Player me);

	public void setParallelize(boolean parallelize) {
		this.parallelize = parallelize;
	}

	@Override
	public double evaluate(GamePlay game, GameMove move, int[] role, int level, long milliseconds) {
		return heuristic(game, move, role);
	}

	@Override
	public double heuristic(GamePlay game, GameMove move, int[] role) {
		GameState state = ((AGGameState) game).getState();
		return evaluateMove(((AGMove) move).get(), state, state.getCurrentPlayer());
	}

	@Override
	public float rate(Move move, PlayerColor myColor, GameState gameBeforeMove, GameState gameAfterMove, boolean wasPruned) {
		return evaluateMove(move, gameBeforeMove, gameBeforeMove.getPlayer(myColor));
	}
}
