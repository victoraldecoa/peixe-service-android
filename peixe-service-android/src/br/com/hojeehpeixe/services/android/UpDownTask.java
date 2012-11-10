package br.com.hojeehpeixe.services.android;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import br.com.hojeehpeixe.services.android.exceptions.UpDownException;

import android.os.AsyncTask;

public class UpDownTask extends AsyncTask<Void, Void, int[]> {

	@Override
	protected int[] doInBackground(Void... params) {
		try {
			return UpDownService.getAllUpDown();
		} catch(IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (UpDownException e) {
			e.printStackTrace();
		}
		return null;
	}

}
