package house.mcintosh.mahjong.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.Map;

import house.mcintosh.mahjong.model.Group;
import house.mcintosh.mahjong.model.Player;
import house.mcintosh.mahjong.model.Tile;
import house.mcintosh.mahjong.model.Wind;

public class EnterHandActivity extends AppCompatActivity
{
	private final static String LOG_TAG = EnterHandActivity.class.getName();

	public final static String PLAYER_KEY = EnterHandActivity.class.getName() + "PLAYER";

	// A statically initialised map of the tiles that are associated with each grid item.
	private final static Map<Integer, Tile> viewToTile = new HashMap<>();
	
	static
	{
		viewToTile.put(R.id.btnCircles1, new Tile(Tile.Suit.CIRCLES, Tile.Number.ONE));
		viewToTile.put(R.id.btnCircles2, new Tile(Tile.Suit.CIRCLES, Tile.Number.TWO));
		viewToTile.put(R.id.btnCircles3, new Tile(Tile.Suit.CIRCLES, Tile.Number.THREE));
		viewToTile.put(R.id.btnCircles4, new Tile(Tile.Suit.CIRCLES, Tile.Number.FOUR));
		viewToTile.put(R.id.btnCircles5, new Tile(Tile.Suit.CIRCLES, Tile.Number.FIVE));
		viewToTile.put(R.id.btnCircles6, new Tile(Tile.Suit.CIRCLES, Tile.Number.SIX));
		viewToTile.put(R.id.btnCircles7, new Tile(Tile.Suit.CIRCLES, Tile.Number.SEVEN));
		viewToTile.put(R.id.btnCircles8, new Tile(Tile.Suit.CIRCLES, Tile.Number.EIGHT));
		viewToTile.put(R.id.btnCircles9, new Tile(Tile.Suit.CIRCLES, Tile.Number.NINE));
		viewToTile.put(R.id.btnCharacters1, new Tile(Tile.Suit.CHARACTERS, Tile.Number.ONE));
		viewToTile.put(R.id.btnCharacters2, new Tile(Tile.Suit.CHARACTERS, Tile.Number.TWO));
		viewToTile.put(R.id.btnCharacters3, new Tile(Tile.Suit.CHARACTERS, Tile.Number.THREE));
		viewToTile.put(R.id.btnCharacters4, new Tile(Tile.Suit.CHARACTERS, Tile.Number.FOUR));
		viewToTile.put(R.id.btnCharacters5, new Tile(Tile.Suit.CHARACTERS, Tile.Number.FIVE));
		viewToTile.put(R.id.btnCharacters6, new Tile(Tile.Suit.CHARACTERS, Tile.Number.SIX));
		viewToTile.put(R.id.btnCharacters7, new Tile(Tile.Suit.CHARACTERS, Tile.Number.SEVEN));
		viewToTile.put(R.id.btnCharacters8, new Tile(Tile.Suit.CHARACTERS, Tile.Number.EIGHT));
		viewToTile.put(R.id.btnCharacters9, new Tile(Tile.Suit.CHARACTERS, Tile.Number.NINE));
		viewToTile.put(R.id.btnBamboo1, new Tile(Tile.Suit.BAMBOO, Tile.Number.ONE));
		viewToTile.put(R.id.btnBamboo2, new Tile(Tile.Suit.BAMBOO, Tile.Number.TWO));
		viewToTile.put(R.id.btnBamboo3, new Tile(Tile.Suit.BAMBOO, Tile.Number.THREE));
		viewToTile.put(R.id.btnBamboo4, new Tile(Tile.Suit.BAMBOO, Tile.Number.FOUR));
		viewToTile.put(R.id.btnBamboo5, new Tile(Tile.Suit.BAMBOO, Tile.Number.FIVE));
		viewToTile.put(R.id.btnBamboo6, new Tile(Tile.Suit.BAMBOO, Tile.Number.SIX));
		viewToTile.put(R.id.btnBamboo7, new Tile(Tile.Suit.BAMBOO, Tile.Number.SEVEN));
		viewToTile.put(R.id.btnBamboo8, new Tile(Tile.Suit.BAMBOO, Tile.Number.EIGHT));
		viewToTile.put(R.id.btnBamboo9, new Tile(Tile.Suit.BAMBOO, Tile.Number.NINE));
		viewToTile.put(R.id.btnEast, new Tile(Wind.EAST));
		viewToTile.put(R.id.btnSouth, new Tile(Wind.SOUTH));
		viewToTile.put(R.id.btnWest, new Tile(Wind.WEST));
		viewToTile.put(R.id.btnNorth, new Tile(Wind.NORTH));
		viewToTile.put(R.id.btnRedDragon, new Tile(Tile.Dragon.RED));
		viewToTile.put(R.id.btnWhiteDragon, new Tile(Tile.Dragon.WHITE));
		viewToTile.put(R.id.btnGreenDragon, new Tile(Tile.Dragon.GREEN));
	}

	private ToggleButton m_selectedGroupTypeButton = null;
	private Group.Type m_selectedGroupType = null;
	private ImageView m_selectedTileButton = null;
	private Tile m_selectedTile = null;
	private Drawable m_tileNormalBackground;
	private Drawable m_tileSelectedBackground;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_hand);

		Intent intent = getIntent();
		Player player = (Player) intent.getSerializableExtra(PLAYER_KEY);

		Log.d(LOG_TAG, "Starting EnterHandActivity for player: " + player.getName());

		m_tileNormalBackground = getDrawable(R.drawable.tile_border);
		m_tileSelectedBackground = getDrawable(R.drawable.tile_border_selected);
	}

	public void onGridTileClick(View view)
	{
		Log.d(LOG_TAG, "Got click on grid view item: " + viewToTile.get(view.getId()));

		m_selectedTile = viewToTile.get(view.getId());

		if (m_selectedTileButton != null)
		{
			m_selectedTileButton.setBackground(m_tileNormalBackground);
		}

		m_selectedTileButton = (ImageView)view;
		m_selectedTileButton.setBackground(m_tileSelectedBackground);
	}

	private void selectGroupTypeButton(View view)
	{
		if (m_selectedGroupTypeButton != null)
			m_selectedGroupTypeButton.setChecked(false);

		if (view != null)
		{
			m_selectedGroupTypeButton = (ToggleButton) view;
			m_selectedGroupTypeButton.setChecked(true);
		}
	}

	public void onChowButtonClick(View view)
	{
		selectGroupTypeButton(view);
		m_selectedGroupType = Group.Type.CHOW;
	}

	public void onPungButtonClick(View view)
	{
		selectGroupTypeButton(view);
		m_selectedGroupType = Group.Type.PUNG;
	}

	public void onKongButtonClick(View view)
	{
		selectGroupTypeButton(view);
		m_selectedGroupType = Group.Type.KONG;
	}

	public void onPairButtonClick(View view)
	{
		selectGroupTypeButton(view);
		m_selectedGroupType = Group.Type.PAIR;
	}
}
