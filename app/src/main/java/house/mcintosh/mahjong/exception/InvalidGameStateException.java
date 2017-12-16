package house.mcintosh.mahjong.exception;

public class InvalidGameStateException extends MahjongException
{
	private static final long serialVersionUID = 1L;

	public InvalidGameStateException(String message)
	{
		super(message);
	}
}
