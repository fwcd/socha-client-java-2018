package fwcd.sc18.alphabeta;

import fwcd.sc18.core.CopyableLogic;
import fwcd.sc18.core.EvaluatingLogic;
import fwcd.sc18.evaluator.HeuristicEvaluator;
import fwcd.sc18.evaluator.HeuristicPruner;
import fwcd.sc18.evaluator.MoveEvaluator;
import fwcd.sc18.evaluator.MovePruner;
import fwcd.sc18.trainer.core.VirtualClient;
import fwcd.sc18.utils.GameAlgorithms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.plugin2018.AbstractClient;
import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.Player;

public class AlphaBetaLogic extends EvaluatingLogic {
	private static final Logger LOG = LoggerFactory.getLogger("ownlog");
	private int depth = 4;
	private MoveEvaluator evaluator = new HeuristicEvaluator();
	private MovePruner pruner = new HeuristicPruner();

	private boolean benchmark = false;
	private int gameStateEvaluations = 0;
	
	public AlphaBetaLogic(VirtualClient client) {
		super(client);
	}
	
	public AlphaBetaLogic(AbstractClient client) {
		super(client);
	}

	@Override
	public CopyableLogic copy(AbstractClient client) {
		return new AlphaBetaLogic(client);
	}

	@Override
	protected float evaluateMove(Move move, GameState gameBeforeMove, Player me) {
		long startTime = System.currentTimeMillis();
		float rating = GameAlgorithms.alphaBeta(false, move, gameBeforeMove, depth, me.getPlayerColor(), pruner, evaluator);
		long delta = System.currentTimeMillis() - startTime;

		if (benchmark && delta > 0) {
			LOG.info("Alpha-Beta Search evaluated {} game states per second", (gameStateEvaluations * 1000) / delta);
			gameStateEvaluations = 0;
		}

		return rating;
	}
}
