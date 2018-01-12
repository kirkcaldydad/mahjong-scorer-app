package house.mcintosh.mahjong.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	private ToggleButton m_btnKong;
	private ToggleButton m_btnPair;
	private TextView m_txtEnteredGroupScore;
	private TextView m_txtWholeHandScores;
	private TextView m_txtTotal;
	private TextView m_txtTotalCalculation;

	private TileDrawables m_tileDrawables;

	private ScoredHand m_hand;
	private ListView m_groupsList;
	private ScoredGroupsAdapter m_groupsAdapter;

	private static WholeHandQuestion[] ALL_MAHJONG_HAND_QUESTIONS = new WholeHandQuestion[]
			{
					new WholeHandQuestion(ScoredHand.HandCompletedBy.MAHJONG_WALL_TILE, R.string.questionMahjongByWallTile),
					new WholeHandQuestion(ScoredHand.HandCompletedBy.MAHJONG_LOOSE_TILE, R.string.questionMahjongByLooseTile),
					new WholeHandQuestion(ScoredHand.HandCompletedBy.MAHJONG_ONLY_POSSIBLE_TILE, R.string.questionMahjongByOnlyPossibleTile),
					new WholeHandQuestion(ScoredHand.HandCompletedBy.MAHJONG_ROBBING_KONG, R.string.questionMahjongByRobbingKong),
					new WholeHandQuestion(ScoredHand.HandCompletedBy.MAHJONG_LAST_WALL_TILE, R.string.questionMahjongByLastWallTile),
					new WholeHandQuestion(ScoredHand.HandCompletedBy.MAHJONG_LAST_DISCARD, R.string.questionMahjongByLastDiscard),
					new WholeHandQuestion(ScoredHand.HandCompletedBy.MAHJONG_ORIGINAL_CALL, R.string.questionMahjongByOriginalCall),
			};

	/** A subset of ALL_MAHJONG_HAND_QUESTIONS that can affect the score based on the scoring scheme. */
	private List<WholeHandQuestion> m_filteredMahjongHandQuestions;

	/** An additional question that is sometimes added to the Mahjong hand questions. */
	private WholeHandQuestion CONCEALED_PAIR_QUESTION = new WholeHandQuestion(ScoredHand.HandCompletedBy.MAHJONG_PAIR_CONCEALED, R.string.questionMahjongPairConcealed);

	private boolean m_mahjongQuestionsAsked = false;

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
		m_btnKong = findViewById(R.id.btnKong);
		m_btnPair = findViewById(R.id.btnPair);
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

		// Build a list of the questions to prompt for if the hand is made mahjong.

		m_filteredMahjongHandQuestions = new ArrayList<>();

		for (WholeHandQuestion question : ALL_MAHJONG_HAND_QUESTIONS)
		{
			if (m_hand.getScoringScheme().hasScore(question.completedBy.scoreElement))
				m_filteredMahjongHandQuestions.add(question);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		// Menu item click is handled in onOptionsItemSelected().
		getMenuInflater().inflate(R.menu.menu_hand_entry, menu);

		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu)
	{
		if (menu != null)
		{
			MenuItem item = menu.findItem(R.id.action_show_hand_completion_dialog);

			item.setEnabled(m_hand.isMahjong());
		}

		return super.onMenuOpened(featureId, menu);
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

		Group.Type maxGroupSize = calculateMaxGroup();

		ScoredGroup group;

		if (maxGroupSize == Group.Type.PAIR)

			group = selectGroupTypeButton(m_btnPair, Group.Type.PAIR);
		else
			group = selectGroupTypeButton(m_btnPung, Group.Type.PUNG);

		boolean canAddGroup = enableButtons(tile, maxGroupSize);

		if (canAddGroup)
		{
			// Add the newly created group to the list view.

			m_hand.add(group);
			m_groupsAdapter.notifyDataSetChanged();
			m_groupsList.smoothScrollToPosition(m_hand.getLatestAdditionPosition());

			showHandQuestionsIfRequired();

			// Hand has been changed, so update the total on display.
			displayTotal();
		}
		else
		{
			displayEntryGroup(null);

			Toast toast = Toast.makeText(this, getText(R.string.cannotAddGroupErrorMessage), Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	private Group.Type calculateMaxGroup()
	{
		int maxTiles = m_hand.getAvailableTileCapacity();

		if (maxTiles > 2)
			return Group.Type.CHOW;
		if (maxTiles == 2)
			return Group.Type.PAIR;
		return Group.Type.EMPTY;
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

		showHandQuestionsIfRequired();

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

	/**
	 * Enable or disable the various buttons, depending on the currently selected Tile.
	 *
	 * @return true if a group can be added to the hand.
 	 */
	public boolean enableButtons(Tile tile, Group.Type maxGroupSize)
	{
		boolean pairEnabled = true;
		boolean chowEnabled = true;
		boolean pungEnabled = true;
		boolean kongEnabled = true;

		switch (tile.getType())
		{
			case DRAGON:
			case WIND:
				chowEnabled = false;
				break;
		}

		if (maxGroupSize.getHandSize() < 3)
		{
			chowEnabled = false;
			pungEnabled = false;
			kongEnabled = false;
		}

		if (maxGroupSize.getHandSize() < 2)
			pairEnabled = false;

		m_btnPair.setEnabled(pairEnabled);
		m_btnChow.setEnabled(chowEnabled);
		m_btnPung.setEnabled(pungEnabled);
		m_btnKong.setEnabled(kongEnabled);

		boolean canAddGroup = pairEnabled || chowEnabled || pungEnabled || kongEnabled;

		return canAddGroup;
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

			case R.id.action_show_hand_completion_dialog:
				showHandCompletionDialog();
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

	private void showHandQuestionsIfRequired()
	{
		if (!m_mahjongQuestionsAsked && m_hand.isMahjong())
		{
			m_mahjongQuestionsAsked = true; // Only ask once by this route.
			showHandCompletionDialog();
		}
	}

	private void showHandCompletionDialog()
	{
		// Build an array of the question text that is to go on the dialog, and another array of
		// the corresponding states for the checkboxes.

		int questionCount = m_filteredMahjongHandQuestions.size();

		if (m_hand.requiresPairConcealedInfo())
			questionCount++;

		String[] questions = new String[questionCount];
		boolean[] states = new boolean[questionCount];
		final WholeHandQuestion[] whQuestions = new WholeHandQuestion[questionCount];

		int questionIndex = 0;

		if (m_hand.requiresPairConcealedInfo())
		{
			questions[questionIndex] = this.getString(CONCEALED_PAIR_QUESTION.questionResource);
			states[questionIndex] = m_hand.isPairConcealed();
			whQuestions[questionIndex] = CONCEALED_PAIR_QUESTION;
			questionIndex++;
		}

		for (int loopIndex = 0 ;  questionIndex < questions.length ; questionIndex++, loopIndex++)
		{
			WholeHandQuestion question = m_filteredMahjongHandQuestions.get(loopIndex);

			questions[questionIndex] = this.getString(question.questionResource);
			states[questionIndex] = m_hand.isMahjongCompletedBy(question.completedBy);
			whQuestions[questionIndex] = question;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder
				.setTitle(R.string.mahjongHandMultipliersDialogTitle)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							// User clicked OK button.  Update the displayed hand to show any
							// change in the score.

							displayTotal();
						}
					})
				.setMultiChoiceItems(questions, states,
					new DialogInterface.OnMultiChoiceClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which,
											boolean isChecked)
						{
							// Copy the checkbox state to the hand, for use in score calculations.

							WholeHandQuestion question = whQuestions[which];
							boolean tilesChanged = m_hand.setMahjongCompletedBy(question.completedBy, isChecked);

							if (tilesChanged)
								m_groupsAdapter.notifyDataSetChanged();
						}
					});

		AlertDialog dialog = builder.create();

		dialog.show();
	}

	private static class WholeHandQuestion
	{
		final ScoredHand.HandCompletedBy completedBy;
		final int questionResource;

		public WholeHandQuestion(ScoredHand.HandCompletedBy completedBy, int questionResource)
		{
			this.completedBy = completedBy;
			this.questionResource = questionResource;
		}
	}
}
