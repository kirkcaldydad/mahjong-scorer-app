package house.mcintosh.mahjong.ui;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import house.mcintosh.mahjong.model.Wind;

public class CreateGameActivity extends AppCompatActivity
{

	private String[] m_names = new String[] {"", "", "", ""};
	private Wind[] m_winds = new Wind[] {Wind.EAST, Wind.SOUTH, Wind.WEST, Wind.NORTH};
	private Map<Wind, CharSequence> m_windNames = new HashMap<>();
	private TextView[] m_windTextViews = new TextView[4];

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game);

		// Create listeners for the names being typed in.

		((EditText)findViewById(R.id.playerName0)).addTextChangedListener(new NameWatcher(0));
		((EditText)findViewById(R.id.playerName1)).addTextChangedListener(new NameWatcher(1));
		((EditText)findViewById(R.id.playerName2)).addTextChangedListener(new NameWatcher(2));
		((EditText)findViewById(R.id.playerName3)).addTextChangedListener(new NameWatcher(3));

		m_windNames.put(Wind.EAST, getText(R.string.east));
		m_windNames.put(Wind.SOUTH, getText(R.string.south));
		m_windNames.put(Wind.WEST, getText(R.string.west));
		m_windNames.put(Wind.NORTH, getText(R.string.north));

		m_windTextViews[0] = (TextView)findViewById(R.id.playerWindText0);
		m_windTextViews[1] = (TextView)findViewById(R.id.playerWindText1);
		m_windTextViews[2] = (TextView)findViewById(R.id.playerWindText2);
		m_windTextViews[3] = (TextView)findViewById(R.id.playerWindText3);

		displayWinds();
	}

	public void onRotateWindClick(View view)
	{
		Wind last = m_winds[3];
		m_winds[3] = m_winds[2];
		m_winds[2] = m_winds[1];
		m_winds[1] = m_winds[0];
		m_winds[0] = last;

		displayWinds();
	}

	private void displayWinds()
	{
		// Update the fields to display the new wind values.

		for (int i = 0 ; i < 4 ; i++)
		{
			TextView windView = m_windTextViews[i];
			Wind wind = m_winds[i];

			windView.setText(m_windNames.get(wind));

			if (wind == Wind.EAST)
				windView.setTypeface(windView.getTypeface(), Typeface.BOLD);
			else
				windView.setTypeface(Typeface.create(windView.getTypeface(), Typeface.NORMAL));
		}
	}

	public void onStartGameClick(View view)
	{

	}

	private class NameWatcher implements TextWatcher
	{
		final private int m_nameIndex;

		public NameWatcher(int nameIndex)
		{
			m_nameIndex = nameIndex;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			m_names[m_nameIndex] = s.toString();
		}

		@Override
		public void afterTextChanged(Editable nameField)
		{

		}
	}
}
