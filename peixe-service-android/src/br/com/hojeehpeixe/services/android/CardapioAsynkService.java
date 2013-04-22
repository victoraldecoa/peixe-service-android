package br.com.hojeehpeixe.services.android;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import br.com.hojeehpeixe.services.android.exceptions.CardapioException;

import android.app.Activity;
import android.os.AsyncTask;

public class CardapioAsynkService extends AsyncTask<Void, Double, CardapioCompleto> {
	public interface OnCardapioServiceResponse {
		/**
		 * Chamado quando o serviço termina de buscar o cardápio
		 * @param result O cardápio
		 */
		public void onResult(CardapioCompleto resultado);
		
		public void onError(String erro);
	}
	
	private OnCardapioServiceResponse listener;
	private static CardapioService service = null;
	private static CardapioCompleto cardapio = null;
	private Activity activity;
	private String erro_cardapio;
	private boolean is_alive = false;
	
	public boolean isAlive() {
		return is_alive;
	}
	
	public CardapioAsynkService(Activity activity, OnCardapioServiceResponse listener) {
		this.activity = activity;
		this.listener = listener;
	}
	
	/**
	 * Use para saber se o método execute() foi chamado alguma vez
	 * @return se o cardápio já foi atualizado alguma vez
	 */
	public static boolean foiAtualizado() {
		if (service != null) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Próxima vez que execute() for chamado o cardápio será atualizado novamente
	 */
	public static void forcaAtualizar() {
		service = null;
	}
	
	@Override
	protected CardapioCompleto doInBackground(Void... params) {
		is_alive = true;
		if (service != null) {
			return cardapio;
		}
		
		FileInputStream input;

		try {
			input = activity.openFileInput("cardapios.json");
		} catch (FileNotFoundException e) {
			input = null;
		}
		
		service = CardapioService.getInstance();

		// fala pra atualizar os cardápios do Service, e ele se vira
		// pra saber se vai pegar do cache ou não
		try {
			cardapio = service.updateCardapios(activity, input);
		} catch (CardapioException e1) {
			erro_cardapio = e1.getMessage();
			return null;
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
				return null;
			}
		}
		
		return cardapio;
	}

	@Override
	protected void onPostExecute(CardapioCompleto result) {
		is_alive = false;
		if (result == null)
			listener.onError(erro_cardapio);
		listener.onResult(result);
	}
}
