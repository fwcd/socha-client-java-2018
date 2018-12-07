package fwcd.sc18.core;

import sc.plugin2018.AbstractClient;
import sc.plugin2018.IGameHandler;

public interface CopyableLogic extends IGameHandler {
	CopyableLogic copy(AbstractClient client);
}
