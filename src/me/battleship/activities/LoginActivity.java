package me.battleship.activities;

import me.battleship.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ViewAnimator;

/**
 * The activity for logging in
 * 
 * @author Manuel Vögele
 */
public class LoginActivity extends Activity implements OnClickListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		findViewById(R.id.login_button).setOnClickListener(this);
		findViewById(R.id.abort_button).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v)
	{
		ViewAnimator animator = (ViewAnimator) findViewById(R.id.login_buttons_animator);
		animator.showNext();
		// TODO Login
	}
}
