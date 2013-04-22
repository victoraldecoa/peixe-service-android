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
		 * Chamado quando o servi�o termina de buscar o card�pio
		 * @param result O card�pio
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
	 * Use para saber se o m�todo execute() foi chamado alguma vez
	 * @return se o card�pio j� foi atualizado alguma vez
	 */
	public static boolean foiAtualizado() {
		if (service != null) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Pr�xima vez que execute() for chamado o card�pio ser� atualizado novamente
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

		// fala pra atualizar os card�pios do Service, e ele se vira
		// pra saber se vai pegar do cache ou n�o
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
			// Verifica se a semana atual do card�pio � a mesma do celular
			if (!service.isCacheAtual()) {
				erro_cardapio = "N�o foi poss�vel encontrar uma conex�o e voc� ainda n�o"
						       + "sincronizou o card�pio desta semana. Conecte-se e tente novamente.";
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
