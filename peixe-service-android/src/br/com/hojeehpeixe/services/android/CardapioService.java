package br.com.hojeehpeixe.services.android;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;

//INICIO imports usados para o serviço REST + JSON
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
// FIM imports usados para o serviço REST + JSON

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import br.com.hojeehpeixe.services.android.exceptions.CardapioException;
import br.com.hojeehpeixe.services.android.Cardapio;
import br.com.hojeehpeixe.services.android.CardapioDia;

/**
 * Classe SingleTon que usa o WebService para montar os Cardápios
 * @author victor
 *
 */
public class CardapioService {
	public boolean noConnection = false;
	
	// Variáveis do WebService
	public Cardapio central;
	public Cardapio quimica;
	public Cardapio fisica;
	public Cardapio prefeitura;

	/**
	 * Id sequencial da requisição WS. "-1" significa que não tem nada no cache. (se tiver algo,
	 * está codificado para colocar o valor "atual" do cache)
	 */
	public int atual = -1;
	public int code = -1;
	public String message = null;
	
	// Fim das variáveis do webservice

	private final static String URL = "http://peixe-aws.no-ip.org/cardapio";
	private Calendar data;
	private boolean _hasUpdate = false;
	
	private static CardapioService instance = null;
	
	/**
	 * Use este método para usar esta classe! Como é uma classe SingleTon, ela não tem contrutor público!
	 * @return
	 */
	public static CardapioService getInstance() {
		if (instance == null) instance = new CardapioService();
		return instance;
	}
	
	public boolean hasUpdate() {
		return _hasUpdate;
	}
	
	/**
	 * 
	 * @param input getResources().openRawResource(R.raw.cardapiosLocal) - Versão local do json
	 * @return
	 * @throws Exception
	 */
	public void updateCardapios(Activity context, FileInputStream input) throws CardapioException {
		_hasUpdate = true;
		noConnection = false;
		
		// Algumas pessoas tiveram problemas por causa do relógio mal ajustado no celular.
		// Entram Segunda 1h da manhã e acham q o cardápio está atualizado. Isto vai previnir
		//if (ehViradaDaSemana())
		//	throw new CardapioException("O cardápio desta semana ainda não foi atualizado pelo Coseas");
		try {
			updateCardapiosREST(context, input);
		} catch (IOException e) {
			//throw new CardapioException("Não foi possível encontrar uma conexão");
			noConnection = true;
		}
	}
		
	/**
	 * A classe é SingleTon. Utilize o método getInstance() para utilizá-la.
	 */
	private CardapioService() {		
		// pegando o calendário
		data = Calendar.getInstance();
		// A semana tem que começar na Segunda!
		data.setFirstDayOfWeek(Calendar.MONDAY);

		// inicializando cardápios
		central = new Cardapio("central", -1, -1);
		fisica = new Cardapio("fisica", -1, -1);
		quimica = new Cardapio("quimica", -1, -1);
		prefeitura = new Cardapio("prefeitura", -1, -1);
	}
	
	private void updateCardapiosREST(Activity context, FileInputStream input) throws IOException, CardapioException {
		// Popula com o que tem no cache
		if (input != null) {
			InputStreamReader isr = new InputStreamReader(input);
			BufferedReader br = new BufferedReader(isr);
			PopulaListaCardapioJSON(br);
		}
		
		// Verifica se tem conexão. 
		if (isNetworkAvailable(context)) {
			// Caso tenha conexão
			
			// pede para popular a lista, e verifica se já populou ou não
			// se tem coisa no cache, "atual" já é o valor do cache. Senão, atual é -1
			// e o WS vai mandar o cardápio de qqr jeito (se o COSEAS tiver atualizado)
			if (PopulaListaCardapioJSON(getJSONFromREST(atual))) {
				// populou... então falta salvar no cache!
				FileOutputStream output = context.openFileOutput("cardapios.json", Context.MODE_PRIVATE);
				saveOnCache(output);
				output.close();
			}
		} else {
			throw new IOException("isNetworkAvailable() returned false");
		}
	}
	
	/**
	 * 
	 * @param atual variável "atual" lida no JSON salvo no aparelho. Caso nada esteja salvo no aparelho, deve-se mandar "-1".
	 * @return
	 */
	private BufferedReader getJSONFromREST(int atual) throws IOException {		
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet;
		
		if (atual == -1)
			httpGet = new HttpGet(URL);
		else
			httpGet = new HttpGet(URL + "/" + atual);
		
		// trocando o User Agent, pois se for diferente de Peixe Android vX o link apenas redireciona
		// para o www.hojeehpeixe.com.br
		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Peixe Android v2.1");
		
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				
				return reader;
			} else {
				Log.e("CardapioService", "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Salva as informações obtidas em formato JSON no output
	 * Pré-requisito: o WebService deve ter sido acessado com sucesso
	 * @param output FileOutputStream output = context.openFileOutput("cardapios.json", Context.MODE_PRIVATE);
	 */
	private void saveOnCache(FileOutputStream output) {
		OutputStreamWriter osw;
		osw = new OutputStreamWriter(output);
		JsonWriter jsonWriter = new JsonWriter(osw);
		
		try {
			jsonWriter.beginObject();
			jsonWriter.name("atual").value(atual);
			jsonWriter.name("message").value(message);
			jsonWriter.name("code").value(code);
			
			jsonWriter.name("central");
			writeCardapio(jsonWriter, central);
			jsonWriter.name("fisica");
			writeCardapio(jsonWriter, fisica);
			jsonWriter.name("quimica");
			writeCardapio(jsonWriter, quimica);
			jsonWriter.name("prefeitura");
			writeCardapio(jsonWriter, prefeitura);

			jsonWriter.endObject();
			jsonWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeCardapio(JsonWriter jsonWriter, Cardapio cardapio) throws IOException {
		jsonWriter.beginObject();
		
		jsonWriter.name("idCardapio").value(cardapio.id);
		jsonWriter.name("local").value(cardapio.local);
		jsonWriter.name("semana").value(cardapio.semana);
		jsonWriter.name("ano").value(cardapio.ano);
		jsonWriter.name("alm");
		writeCardapioDiaVetor(jsonWriter, cardapio.almoco);
		jsonWriter.name("jan");
		writeCardapioDiaVetor(jsonWriter, cardapio.janta);
		
		jsonWriter.endObject();
	}

	private void writeCardapioDiaVetor(JsonWriter jsonWriter, CardapioDia[] vetor) throws IOException {
		jsonWriter.beginObject();
		
		jsonWriter.name("seg");
		writeCardapioDia(jsonWriter, vetor[0]);
		jsonWriter.name("ter");
		writeCardapioDia(jsonWriter, vetor[1]);
		jsonWriter.name("qua");
		writeCardapioDia(jsonWriter, vetor[2]);
		jsonWriter.name("qui");
		writeCardapioDia(jsonWriter, vetor[3]);
		jsonWriter.name("sex");
		writeCardapioDia(jsonWriter, vetor[4]);
		jsonWriter.name("sab");
		writeCardapioDia(jsonWriter, vetor[5]);
		jsonWriter.name("dom");
		writeCardapioDia(jsonWriter, vetor[6]);
		
		jsonWriter.endObject();
	}

	private void writeCardapioDia(JsonWriter jsonWriter, CardapioDia cardapioDia) throws IOException {
		jsonWriter.beginObject();
		
		if (cardapioDia.id != 0) jsonWriter.name("idCardapioDia").value(cardapioDia.id);
		if (cardapioDia.base != null) jsonWriter.name("base").value(cardapioDia.base);
		if (cardapioDia.mistura != null) jsonWriter.name("mistura").value(cardapioDia.mistura);
		if (cardapioDia.acompanhamento != null) jsonWriter.name("acomp").value(cardapioDia.acompanhamento);
		if (cardapioDia.salada != null) jsonWriter.name("salada").value(cardapioDia.salada);
		if (cardapioDia.opcional != null) jsonWriter.name("opcional").value(cardapioDia.opcional);
		if (cardapioDia.sobremesa != null) jsonWriter.name("sobremesa").value(cardapioDia.sobremesa);
		if (cardapioDia.suco != null) jsonWriter.name("suco").value(cardapioDia.suco);
		if (cardapioDia.calorias != null) jsonWriter.name("calorias").value(cardapioDia.calorias);
		if (cardapioDia.message != null) jsonWriter.name("message").value(cardapioDia.message);
		
		jsonWriter.endObject();
	}

	/**
	 * Popula os objetos central, fisica, quimica e prefeitura com o cardápio recebido
	 * usando JSON.
	 * Caso ele verifique que o que está populado já é o mais atual ele mantém o que estava e retorna false.
	 * @param input
	 * @return true caso tenha modificado os cardápios, false caso deva-se manter o que tem localmente
	 * @throws IOException caso tenha tido algum problema estranho com o servidor
	 * @throws CardapioException caso não tenha cardápio pra mostrar
	 */
	private boolean PopulaListaCardapioJSON(BufferedReader jsonBuffer) throws IOException, CardapioException {
		if (jsonBuffer == null) return false;
		
		JsonReader jsonReader = new JsonReader(jsonBuffer);
		
		try {
			jsonReader.beginObject();
			while (jsonReader.hasNext()) {
				String name = jsonReader.nextName();
				if (name.equals("atual")) {
					atual = jsonReader.nextInt();
				} else if (name.equals("central")) {
					central = getCardapioWithJSON(jsonReader, atual);
				} else if (name.equals("fisica")) {
					fisica = getCardapioWithJSON(jsonReader, atual);
				} else if (name.equals("quimica")) {
					quimica = getCardapioWithJSON(jsonReader, atual);
				} else if (name.equals("prefeitura")) {
					prefeitura = getCardapioWithJSON(jsonReader, atual);
				} else if (name.equals("message")) {
					message = jsonReader.nextString();
				} else if (name.equals("code")) {
					code = jsonReader.nextInt();
				} else {
					Log.d("JSON", "Objeto não reconhecido: " + name);
					jsonReader.skipValue();
				}
			}
			jsonReader.endObject();
		} catch(EOFException e) {
			atual = -1;
		}
		
		switch(code) {
		case 100:
			// o do cache já é o mais atual. Nada foi populado pois é pra por o do cache
			return false;
		case 101:
			// o do cache é o mais atual, mas é da semana passada. Peixe morto!
			throw new CardapioException(message);
		case 200:
			// o do cache é antigo e deve ser sincronizado. A lista foi populada com sucesso
			return true;
		case 201:
			// O do WS é mais atual, mas a COSEAS ainda não disponibilizou o desta semana. Peixe morto!
			throw new CardapioException(message);
		case 900:
			// TODO - mostrar mensagem com Toast. Porém, a lista foi populada.
			return true; // TODO - ou false? ou este código deve ser retirado?
		default:
			return false;
		}
	}
	
	/**
	 * Pré-requisito: você acabou de receber com jsonReader.nextName() o nome de um objeto do tipo Cardapio.
	 * @param jsonReader
	 * @param atual id sequencial mandado pelo servidor, recebido no escopo anterior  
	 * @return o Cardapio que estava encapsulado como JSONObject
	 * @throws IOException
	 */
	private Cardapio getCardapioWithJSON(JsonReader jsonReader, int atual) throws IOException {
		Cardapio cardapio = new Cardapio();
		cardapio.atual = atual;
		
		jsonReader.beginObject();
		while(jsonReader.hasNext()) {
			String name = jsonReader.nextName();
			if (name.equals("idCardapio")) {
				cardapio.id = jsonReader.nextLong();
			} else if (name.equals("local")) {
				cardapio.local = jsonReader.nextString();
			} else if (name.equals("semana")) {
				cardapio.semana = jsonReader.nextInt();
			} else if (name.equals("ano")) {
				cardapio.ano = jsonReader.nextInt();
			} else if (name.equals("alm")) {
				cardapio.almoco = getVetorCardapioDiaWithJSON(jsonReader);
			} else if (name.equals("jan")) {
				cardapio.janta = getVetorCardapioDiaWithJSON(jsonReader);
			} else {
				Log.d("JSON", "Objeto não reconhecido: " + name);
				jsonReader.skipValue();
			}
		}
		jsonReader.endObject();
		
		return cardapio;
	}
	
	private CardapioDia[] getVetorCardapioDiaWithJSON(JsonReader jsonReader) throws IOException {
		CardapioDia cardapios[] = new CardapioDia[7];
		
		jsonReader.beginObject();
		while(jsonReader.hasNext()) {
			String name = jsonReader.nextName();
			if (name.equals("seg")) {
				cardapios[0] = getCardapioDiaWithJSON(jsonReader);
			} else if (name.equals("ter")) {
				cardapios[1] = getCardapioDiaWithJSON(jsonReader);
			} else if (name.equals("qua")) {
				cardapios[2] = getCardapioDiaWithJSON(jsonReader);
			} else if (name.equals("qui")) {
				cardapios[3] = getCardapioDiaWithJSON(jsonReader);
			} else if (name.equals("sex")) {
				cardapios[4] = getCardapioDiaWithJSON(jsonReader);
			} else if (name.equals("sab")) {
				cardapios[5] = getCardapioDiaWithJSON(jsonReader);
			} else if (name.equals("dom")) {
				cardapios[6] = getCardapioDiaWithJSON(jsonReader);
			} else {
				Log.d("JSON", "Objeto não reconhecido: " + name);
				jsonReader.skipValue();
			}
		}
		jsonReader.endObject();
		
		return cardapios;
	}
	
	private CardapioDia getCardapioDiaWithJSON(JsonReader jsonReader) throws IOException {
		CardapioDia cardapioDia = new CardapioDia();

		jsonReader.beginObject();
		while(jsonReader.hasNext()) {
			String name = jsonReader.nextName();
			if (jsonReader.peek() != JsonToken.NULL) {
				if (name.equals("idCardapioDia")) {
					cardapioDia.id = jsonReader.nextLong();
				} else if (name.equals("base")) {
					cardapioDia.base = jsonReader.nextString();
				} else if (name.equals("mistura")) {
					cardapioDia.mistura = jsonReader.nextString();
				} else if (name.equals("acomp")) {
					cardapioDia.acompanhamento = jsonReader.nextString();
				} else if (name.equals("salada")) {
					cardapioDia.salada = jsonReader.nextString();
				} else if (name.equals("opcional")) {
					cardapioDia.opcional = jsonReader.nextString();
				} else if (name.equals("sobremesa")) {
					cardapioDia.sobremesa = jsonReader.nextString();
				} else if (name.equals("suco")) {
					cardapioDia.suco = jsonReader.nextString();
				} else if (name.equals("calorias")) {
					cardapioDia.calorias = jsonReader.nextString();
				} else if (name.equals("message")) {
					cardapioDia.message = jsonReader.nextString();
				} else {
					Log.d("JSON", "Objeto não reconhecido: " + name);
					jsonReader.skipValue();
				}
			}
			else {
				Log.d("JSON", "Objeto com valor nulo: " + name);
				jsonReader.skipValue();
			}
		}
		jsonReader.endObject();
		
		return cardapioDia;
	}
	
	/**
	 * Verifica se o número da semana contida nos cardápios é igual ao do celular
	 * Só deve ser usada caso não haja conexão, já que isso é feito com maior confiabilidade
	 * no servidor (já que a data do celular pode estar errada)
	 * @return
	 */
	public boolean isCacheAtual() {
		Calendar c = Calendar.getInstance();
		int weekOfYear = c.get(Calendar.WEEK_OF_YEAR);
		int year = c.get(Calendar.YEAR);
		
		if (year <= central.ano &&
				year <= fisica.ano &&
				year <= quimica.ano &&
				year <= prefeitura.ano) {
			if (weekOfYear <= central.semana &&
					weekOfYear <= fisica.semana &&
					weekOfYear <= quimica.semana &&
					weekOfYear <= prefeitura.semana) {
				return true;
			}
			else {
				return false;
			}
		}
		else
			return false;
	}
	
	private boolean isNetworkAvailable(Activity context) {
	    ConnectivityManager cm = (ConnectivityManager) 
	    	context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	    // if no network is available networkInfo will be null
	    // otherwise check if we are connected
	    if (networkInfo != null && networkInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
}
