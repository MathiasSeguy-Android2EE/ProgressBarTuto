/**
* <ul>
* Android Tutorial, An <strong>Android2EE</strong>'s project.</br>
* Produced by <strong>Dr. Mathias SEGUY</strong> with the smart contribution of <strong>Julien PAPUT</strong>.</br>
* Delivered by <strong>http://android2ee.com/</strong></br>
* Belongs to <strong>Mathias Seguy</strong></br>
* ****************************************************************************************************************</br>
* This code is free for any usage but can't be distribute.</br>
* The distribution is reserved to the site <strong>http://android2ee.com</strong>.</br>
* The intelectual property belongs to <strong>Mathias Seguy</strong>.</br>
* <em>http://mathias-seguy.developpez.com/</em></br>
* </br>
* For any information (Advice, Expertise, J2EE or Android Training, Rates, Business):</br>
* <em>mathias.seguy.it@gmail.com</em></br>
* *****************************************************************************************************************</br>
* Ce code est libre de toute utilisation mais n'est pas distribuable.</br>
* Sa distribution est reservée au site <strong>http://android2ee.com</strong>.</br>
* Sa propriété intellectuelle appartient à <strong>Mathias Séguy</strong>.</br>
* <em>http://mathias-seguy.developpez.com/</em></br>
* </br>
* Pour tous renseignements (Conseil, Expertise, Formations J2EE ou Android, Prestations, Forfaits):</br>
* <em>mathias.seguy.it@gmail.com</em></br>
* *****************************************************************************************************************</br>
* Merci à vous d'avoir confiance en Android2EE les Ebooks de programmation Android.
* N'hésitez pas à nous suivre sur twitter: http://fr.twitter.com/#!/android2ee
* N'hésitez pas à suivre le blog Android2ee sur Developpez.com : http://blog.developpez.com/android2ee-mathias-seguy/
* *****************************************************************************************************************</br>
* com.android2ee.android.tuto</br>
* 25 mars 2011</br>
*/
package com.android2ee.android.tuto.gui.progressbar;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author (Julien PAPUT sous la direction du Dr. Mathias Séguy)
 * @goals This class aims to:
 *        <ul>
 *        <li>Complete a progress bar</li>
 *        </ul>
 */
public class ProgressBarTuto extends Activity {

	/******************************************************************************************/
	/** Attributes **************************************************************************/
	/******************************************************************************************/

	//the textView of the progressbar
	TextView txvProgressTitle;
	// define the progressbar
	ProgressBar bar;
	// Boolean thread safe
	AtomicBoolean isRunning = new AtomicBoolean(true);
	// Boolean thread pause
	AtomicBoolean isPausing = new AtomicBoolean(false);
	// hint
	int i = 0;

	/**
	 * This constant permit to the handler to call the incrementBar() method
	 */
	final static String INCREMENT_PROGRESS = "incrementProgressBar";
	/**
	 * This constant permit to the handler to call the showMessage() method
	 */
	final static String SHOW_MESSAGE = "showToast";
	/**
	 * This constant permit to the handler to call the showMessage() method
	 */
	final static String MESSAGE_TO_SHOW = "messageToShow";

	// define the velocity of the progressbar
	Integer speedness = 100;
	// define the thread
	Thread background;

	/******************************************************************************************/
	/** Define the Handler ********************************************************************/
	/******************************************************************************************/
	Handler handler = null;

	/******************************************************************************************/
	/** Constructors **************************************************************************/
	/******************************************************************************************/

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		/** Thread Background **/
		background = new Thread(new Runnable() {
			/**
			 * The size max of the progress bar
			 */
			private static final int MAX_PROGRESS_BAR = 100;
			/**
			 * the interval time of sleeping
			 */
			private static final int SLEEP_TIME = 50;

			// define the run method
			@Override
			public void run() {
				// Create a BundleMessage bundle with SHOW_MESSAGE and boolean True
				Bundle messageBundleShow = new Bundle();
				messageBundleShow.putBoolean(SHOW_MESSAGE, true);
				// Create a BundleMessage bundle with INCREMENT_PROGRESS and boolean True
				Bundle messageBundleIncrement = new Bundle();
				messageBundleIncrement.putBoolean(INCREMENT_PROGRESS, true);

				Message message;

				while (isRunning.get()) {
					try {
						// increment the progressBar
						// {
						while (i < MAX_PROGRESS_BAR) {
							// if the thread is in pause state, wait 50 milliseconde
							while (isPausing.get()) {
								Thread.sleep(SLEEP_TIME);
							}
							// Just sleep 0.1 second
							Thread.sleep(speedness);
							// Ask the handler and obtain a message (memory optimisation)
							message = handler.obtainMessage();

							// Send to the Handler the messageBundleIncrement Bundle Message
							message.setData(messageBundleIncrement);
							handler.sendMessage(message);
							// increment counter
							i++;
						}
						// ?why
						i = 0;
						// Re-init the progressbar to 0
						bar.setProgress(0);

					} catch (Exception ex) {
						// just end the background thread
                        Log.e("ProgressBarTuto"," a problem in the run loop",ex);
					}
				}
			}
		});
		// Start the Thread
		background.start();
		/** Handler **/
		// Define the handler that receives the Thread's messages and communicate with the GUI
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Bundle messageBundle = msg.getData();
				// If the bundle message received by the handler, have INCREMENT_PROGRESS to true
				if (messageBundle.getBoolean(INCREMENT_PROGRESS)) {
					// call the incrementBar method
					incrementBar();
				}

				// If the bundle message received by the handler, have SHOW_MESSAGE to true
				if (messageBundle.getBoolean(SHOW_MESSAGE)) {
					// call the showToast method
					showToast(messageBundle.getString(MESSAGE_TO_SHOW));
				}
			}
		};
		// Instanciate the progress bar
		bar = (ProgressBar) findViewById(R.id.progress);

		txvProgressTitle=(TextView)findViewById(R.id.txvProgress);
		/** Instantiate listeners **/
		// set all the listeners
		setEventListeners();
	}

	/** * This method set on all the listeners */
	public void setEventListeners() {
		/** SeekBar listener **/
		// Retrieve the Seekbar
		SeekBar seek = (SeekBar) findViewById(R.id.seek);
		// add a listener on it
		seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// unused
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// unused
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				speedness = 100 - seekBar.getProgress();
				Toast.makeText(ProgressBarTuto.this, "new speed :" + speedness, Toast.LENGTH_LONG)
						.show();
			}
		});


	}

	/******************************************************************************************/
	/** Private methods **************************************************************************/
	/******************************************************************************************/

	/** This method show a toast */
	private void showToast(final String message) {
		//comments
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
			}
		});
	}


	boolean incrementFirstProgress=true;
	/** This method increment the bar */
	private void incrementBar() {
		// increment the bar by 1
		if(incrementFirstProgress) {
			bar.incrementProgressBy(1);
		}else {
			bar.incrementSecondaryProgressBy(1);
		}
		if(bar.getSecondaryProgress()>bar.getMax()-2) {
			bar.setSecondaryProgress(0);
			incrementFirstProgress=true;
			txvProgressTitle.setText(getString(R.string.progressbar)+" (increment main Progress)");
		}
		if(bar.getProgress()>bar.getMax()-2) {
			bar.setProgress(0);
			incrementFirstProgress=false;
			txvProgressTitle.setText(getString(R.string.progressbar)+" (increment secondary Progress)");
			
		}
	}

	/******************************************************************************************/
	/** Managing Activity's lifecycle **************************************************************************/
	/******************************************************************************************/

	@Override
	protected void onPause() {
		super.onPause();
		isPausing.set(false);
	}

	// method call at the activity stop
	@Override
	public void onStop() {
		super.onStop();
		// update the boolean
		isRunning.set(false);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Save instance some state
		outState.putInt("speedness", speedness);
//		outState.putBoolean("isRunning", isRunning.get());
//		outState.putBoolean("isPausing", isPausing.get());
		outState.putInt("int", i);

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// restore instance some state
		speedness = savedInstanceState.getInt("speedness");
		i = savedInstanceState.getInt("int");
//		isRunning.set(savedInstanceState.getBoolean("isRunning"));
//		isPausing.set(savedInstanceState.getBoolean("isPausing"));

	}
}