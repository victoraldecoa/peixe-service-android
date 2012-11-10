package br.com.hojeehpeixe.services.android;

import java.io.FileInputStream;
import java.io.IOException;

import br.com.hojeehpeixe.services.android.exceptions.CardapioException;

import android.app.Activity;
import android.os.AsyncTask;

public class CardapioAsynkService extends AsyncTask<FileInputStream, Double, Boolean> {
	public interface OnCardapioServiceResponse {
		/**
		 * Called when something is answered from the service
		 * @param result whether we got a menu or not
		 */
		public void onResult(boolean result);
		
		public void onError(String error);
	}
	
	private OnCardapioServiceResponse listener;
	private CardapioService service;
	private Activity activity;
	private String erro_cardapio;
	
	public CardapioAsynkService(Activity activity, OnCardapioServiceResponse listener) {
		this.activity = activity;
		this.listener = listener;
	}
	
	@Override
	protected Boolean doInBackground(FileInputStream... params) {
		FileInputStream input = params[0];
		
		service = CardapioService.getInstance();

		// fala pra atualizar os cardápios do Service, e ele se vira
		// pra saber se vai pegar do cache ou não
		try {
			service.updateCardapios(activity, input);
		} catch (CardapioException e1) {
			erro_cardapio = e1.getMessage();
			return false;
		}

		if (input != null)
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		if (service.noConnection) {
			// Verifica se a semana atual do cardápio é a mesma do celular
			if (!service.isCacheAtual()) {
				erro_cardapio = "Não foi possível encontrar uma conexão e você ainda não"
						       + "sincronizou o cardápio desta semana. Conecte-se e tente novamente.";
				return false;
			}
		}
		
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result == false)
			listener.onError(erro_cardapio);
		listener.onResult(result);
	}
}
