package house.mcintosh.mahjong.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.Map;

import house.mcintosh.mahjong.model.Group;
import house.mcintosh.mahjong.model.Player;
import house.mcintosh.mahjong.model.Tile;
import house.mcintosh.mahjong.model.Wind;
import house.mcintosh.mahjong.scoring.ScoredGroup;
import house.mcintosh.mahjong.scoring.ScoredHand;
import house.mcintosh.mahjong.util.DisplayUtil;

public final class EnterHandActivity extends AppCompatActivity
{
	private final static String LOG_TAG = EnterHandActivity.class.getName();

	public final static String PLAYER_KEY = EnterHandActivity.class.getName() + "PLAYER";
	public final static String OWN_WIND_KEY = EnterHandActivity.class.getName() + "OWN_WIND";
	public final static String PREVAILING_WIND_KEY = EnterHandActivity.class.getName() + "PREVAILING_WIND";
	public final static String HAND_KEY = EnterHandActivity.class.getName() + "HAND";

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

	private Player m_player;
	private Wind m_ownWind;
	private Wind m_prevailingWind;

	private ToggleButton m_selectedGroupTypeButton = null;
	private Group.Type m_selectedGroupType = null;
	private ImageView m_selectedTileButton = null;
	private Tile m_selectedTile = null;
	private ToggleButton m_visibilityButton = null;
	private Group.Visibility m_selectedVisibility = null;
	private Drawable m_tileNormalBackground;
	private Drawable m_tileSelectedBackground;
	private LinearLayout m_handEntryDisplayTiles;
	private ToggleButton m_btnChow;
	private ToggleButton m_btnPung;
	private TextView m_txtEnteredGroupScore;
	private TextView m_txtWholeHandScores;
	private TextView m_txtTotal;
	private TextView m_txtTotalCalculation;

	private TileDrawables m_tileDrawables;

	private ScoredHand m_hand;
	private ListView m_groupsList;
	private ScoredGroupsAdapter m_groupsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_hand);

		getSupportActionBar().setHomeButtonEnabled(true);

		Intent intent = getIntent();
		m_player = (Player) intent.getSerializableExtra(PLAYER_KEY);
		m_ownWind = (Wind) intent.getSerializableExtra(OWN_WIND_KEY);
		m_prevailingWind = (Wind) intent.getSerializableExtra(PREVAILING_WIND_KEY);
		m_hand = ((ScoredHandWrapper) intent.getSerializableExtra(HAND_KEY)).getHand();

		Log.d(LOG_TAG, "Starting EnterHandActivity for player: " + m_player.getName());

		m_tileNormalBackground = getDrawable(R.drawable.tile_border);
		m_tileSelectedBackground = getDrawable(R.drawable.tile_border_selected);
		m_handEntryDisplayTiles = findViewById(R.id.layoutEnteredGroup);
		m_btnChow = findViewById(R.id.btnChow);
		m_btnPung = findViewById(R.id.btnPung);
		m_txtEnteredGroupScore = findViewById(R.id.txtEnteredGroupScore);
		m_txtWholeHandScores = findViewById(R.id.txtWholeHandScores);
		m_txtTotal = findViewById(R.id.txtTotal);
		m_txtTotalCalculation = findViewById(R.id.txtTotalCalculation);

		m_tileDrawables = new TileDrawables(this);

		m_visibilityButton = findViewById(R.id.btnVisibility);

		// Create an adapter to display the hand.

		m_groupsAdapter = new ScoredGroupsAdapter(
				this,
				m_hand,
				m_tileDrawables,
				new ScoredGroupsAdapter.DeleteActioner()
				{
					public void requestDelete(int position)
					{
						deleteGroup(position);
					}
				});

		m_groupsList = findViewById(R.id.groupsList);
		m_groupsList.setAdapter(m_groupsAdapter);
	}

	public void onGridTileClick(View view)
	{
		Tile tile = viewToTile.get(view.getId());

		Log.d(LOG_TAG, "Got click on grid view item: " + tile);

		m_selectedTile = viewToTile.get(view.getId());

		if (m_selectedTileButton != null)
		{
			m_selectedTileButton.setBackground(m_tileNormalBackground);
		}

		m_selectedTileButton = (ImageView) view;
		m_selectedTileButton.setBackground(m_tileSelectedBackground);

		// Create a group - a pung by default, and display it in the entry area.

		setVisibility(Group.Visibility.EXPOSED);
		ScoredGroup group = selectGroupTypeButton(m_btnPung, Group.Type.PUNG);

		enableButtons(tile);

		// Add the newly created group to the list view.

		m_hand.add(group);
		m_groupsAdapter.notifyDataSetChanged();
		m_groupsList.smoothScrollToPosition(m_hand.getLatestAdditionPosition());

		// Hand has been changed, so update the total on display.
		displayTotal();
	}

	/**
	 * Refresh the display of the entry group area.
	 *
 	 * @param scoredGroup	The group of tiles to display.  If null, no tiles are displayed.
	 */
	private void displayEntryGroup(ScoredGroup scoredGroup)
	{
		// Display the tile images.

		DisplayUtil.displayTileGroup(scoredGroup, m_handEntryDisplayTiles, m_tileDrawables);

		// Display the score alongside the tiles.

		String score;

		if (scoredGroup != null)
		{
			score = DisplayUtil.getBasicScore(scoredGroup);
			String multipliers = DisplayUtil.getScoreMultipliers(scoredGroup);

			if (!multipliers.isEmpty())
				score += ' ' + multipliers;
		}
		else
		{
			score = "";
		}

		m_txtEnteredGroupScore.setText(score);
	}

	private ScoredGroup selectGroupTypeButton(View view, Group.Type groupType)
	{
		// Toggle off any previous selection, and toggle on the button just clicked.
		if (m_selectedGroupTypeButton != null)
			m_selectedGroupTypeButton.setChecked(false);

		if (view != null)
		{
			m_selectedGroupTypeButton = (ToggleButton) view;
			m_selectedGroupTypeButton.setChecked(true);
		}

		m_selectedGroupType = groupType;

		ScoredGroup group = updateScoredGroup();

		return group;
	}

	private ScoredGroup updateScoredGroup()
	{
		// If there is a selected tile, update the selected group to match the tile.

		ScoredGroup scoredGroup = null;

		if (m_selectedTile != null)
		{
			Group group = new Group(m_selectedGroupType, m_selectedTile, m_selectedVisibility);
			scoredGroup = new ScoredGroup(group, m_hand.getScoringScheme(), m_ownWind, m_prevailingWind);
		}

		// scoredGroup may still be null, meaning display no tiles.
		displayEntryGroup(scoredGroup);

		return scoredGroup;
	}

	private void replaceGroup(ScoredGroup revisedGroup)
	{
		if (revisedGroup == null)
			return;

		m_hand.replaceLatestAddition(revisedGroup);
		m_groupsAdapter.notifyDataSetChanged();
		m_groupsList.smoothScrollToPosition(m_hand.getLatestAdditionPosition());

		// Hand has been changed, so update the total to show it.
		displayTotal();
	}

	public void onChowButtonClick(View view)
	{
		ScoredGroup revisedGroup = selectGroupTypeButton(view, Group.Type.CHOW);
		replaceGroup(revisedGroup);
	}

	public void onPungButtonClick(View view)
	{
		ScoredGroup revisedGroup = selectGroupTypeButton(view, Group.Type.PUNG);
		replaceGroup(revisedGroup);
	}

	public void onKongButtonClick(View view)
	{
		ScoredGroup revisedGroup = selectGroupTypeButton(view, Group.Type.KONG);
		replaceGroup(revisedGroup);
	}

	public void onPairButtonClick(View view)
	{
		ScoredGroup revisedGroup = selectGroupTypeButton(view, Group.Type.PAIR);
		replaceGroup(revisedGroup);
	}

	public void onVisibilityButtonClick(View view)
	{
		m_selectedVisibility = m_visibilityButton.isChecked() ? Group.Visibility.CONCEALED : Group.Visibility.EXPOSED;
		setConcealedButtonStyle();
		ScoredGroup revisedGroup = updateScoredGroup();
		replaceGroup(revisedGroup);
	}

	public void deleteGroup(int position)
	{
		m_hand.remove(position);
		m_groupsAdapter.notifyDataSetChanged();
		displayTotal();
	}

	public void displayTotal()
	{
		m_txtWholeHandScores.setText(DisplayUtil.getWholeHandScores(m_hand));
		m_txtTotal.setText(DisplayUtil.getTotalScore(m_hand));
		m_txtTotalCalculation.setText(DisplayUtil.getTotalCalculation(m_hand));
	}

	private void setVisibility(Group.Visibility visibility)
	{
		m_selectedVisibility = visibility;
		m_visibilityButton.setChecked(visibility == Group.Visibility.CONCEALED);
		setConcealedButtonStyle();
	}

	private void setConcealedButtonStyle()
	{
		if (m_selectedVisibility == Group.Visibility.CONCEALED)
			m_visibilityButton.setTextSize(10);
		else
			m_visibilityButton.setTextSize(12);
	}

	// Enable or disable the various buttons, depending on the currently selected Tile.
	public void enableButtons(Tile tile)
	{
		switch (tile.getType())
		{
			case DRAGON:
			case WIND:
				m_btnChow.setEnabled(false);
				break;

			default:
				m_btnChow.setEnabled(true);
				break;
		}
	}

	/**
	 * Invoked when an item on the bar at the top is selected, including the back arrow button at the top.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				returnEnteredHand();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Handler for the back button to make sure that the hand entered so far is returned to
	 * the parent activity.
	 */
	@Override
	public void onBackPressed()
	{
		returnEnteredHand();
	}

	/**
	 * Finishes this activity, returning the entered hand to the calling activity.
	 */
	private void returnEnteredHand()
	{
		Intent returnHandIntent = NavUtils.getParentActivityIntent(this);

		returnHandIntent.putExtra(GamePlayActivity.EXTRA_KEY_ENTERED_HAND, new ScoredHandWrapper(m_hand));
		returnHandIntent.putExtra(GamePlayActivity.EXTRA_KEY_PLAYER, m_player);

		NavUtils.navigateUpTo(this, returnHandIntent);
	}
}
