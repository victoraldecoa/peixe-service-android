package br.com.hojeehpeixe.services.android;

import java.io.IOException;
import java.net.ConnectException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import br.com.hojeehpeixe.services.android.exceptions.UpDownException;

public class UpDownService {

	/**
	*
	* Registra a preferência de um usuário em relação a um determidado cardápio.
	*
	*
	* @param restaurante String contento o nome do restaurante.
	* @param horario String contendo o horário do cardapio (Almoco ou Janta).
	* @param dia String contendo a data que o cardápio foi servido.
	* @param gostou TRUE caso o usuário gostou do cardápio. FALSE caso contrário.
	*
	* @return Um inteiro de acordo com a seguinte convenção:
	*          0 - Se a operação foi efetuada com sucesso.
	*          1 - Se o sistema não conseguiu se conectar ao banco da dados.
	*          2 - Se ocorreu um erro ao executar os comandos SQL.
	*          6 - Se ocorreu um erro inesperado.
	*          7 - Se não foi encontrado o cardápio referenciado.
	 * @throws XmlPullParserException 
	 * @throws IOException 
	*/
	
	private final static String NAMESPACE = "http://libcoseas.pcs2420.poli.com";
	
	/**
	 * ORDEM: UP-Quimica, Central, Prefeitura; DOWN-Quimica, Central, Prefeitura
	 * @param horario
	 * @param dia
	 * @return
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 * @throws UpDownException 
	 */
	public static int[] get6UpDown (String horario, String dia) throws IOException, XmlPullParserException, UpDownException 
	{
		String METHOD_NAME = "get6UpDown";
		String SOAP_ACTION = "http://libcoseas.pcs2420.poli.com/get6UpDown";
		String URL = "http://www.hojeehpeixe.com.br/axis2/services/CardapioWS4/get6UpDown/";
		
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		PropertyInfo pi1 = new PropertyInfo();
		pi1.setName("horario");
		pi1.setValue(horario);
		pi1.setType(String.class);
		request.addProperty(pi1);
		
		PropertyInfo pi2 = new PropertyInfo();
		pi2.setName("dia");
		pi2.setValue(dia);
		pi2.setType(String.class);
		request.addProperty(pi2);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		androidHttpTransport.call(SOAP_ACTION, envelope);
		
		int[] resultado = new int[6];
		String resultadoString;
		resultadoString = envelope.bodyIn.toString();
		
		try {
			
			for(int i=0;i<6;i++)
			{
				resultadoString = resultadoString.substring(resultadoString.indexOf("=")+1);
				resultado[i] = Integer.parseInt(resultadoString.substring(0,resultadoString.indexOf(";")));
				if(resultado[i]<0)
					resultado[i]=0;
			}
			
		} catch (Exception e) {
			throw new UpDownException("N�o foi poss�vel achar os itens do vetor de notas.");
		}
		
		return resultado;
	}
	
	public static int[] getAllUpDown() throws IOException, XmlPullParserException, UpDownException
	{
		String METHOD_NAME = "getAllUpDown";
		String SOAP_ACTION = "http://libcoseas.pcs2420.poli.com/getAllUpDown";
		String URL = "http://107.20.161.57/axis2/services/CardapioWS4/getAllUpDown/";
		
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
		} catch (ConnectException e) {
			throw new IOException("KSOAP2: No connection");
		} catch (java.net.SocketTimeoutException e) {
			throw new IOException("KSOAP2: No connection");
		}
		
		int[] resultado = new int[102];
		String resultadoString;
		resultadoString = envelope.bodyIn.toString();
				
		try {
			
			for(int i=0;i<resultado.length;i++)
			{
				resultadoString = resultadoString.substring(resultadoString.indexOf("=")+1);
				resultado[i] = Integer.parseInt(resultadoString.substring(0,resultadoString.indexOf(";")));
				if(resultado[i]<0)
					resultado[i]=0;
			}
			
		} catch (Exception e) {
			throw new UpDownException("N�o foi poss�vel achar os itens do vetor de notas.");
		}
		
		return resultado;
	}
	
	/**
	 * ORDEM: Quimica, Central, Prefeitura
	 * @param horario
	 * @param dia
	 * @param gostou
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws UpDownException
	 */
	public static int[] getAllUpDown(String horario, String dia, String gostou) throws IOException, XmlPullParserException, UpDownException
	{
		String METHOD_NAME = "getAllUpDown";
		String SOAP_ACTION = "http://libcoseas.pcs2420.poli.com/getAllUpDown";
		String URL = "http://107.20.161.57/axis2/services/CardapioWS4/getAllUpDown/";
		
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		PropertyInfo pi2 = new PropertyInfo();
		pi2.setName("horario");
		pi2.setValue(horario);
		pi2.setType(String.class);
		request.addProperty(pi2);
		
		PropertyInfo pi3 = new PropertyInfo();
		pi3.setName("dia");
		pi3.setValue(dia);
		pi3.setType(String.class);
		request.addProperty(pi3);
		
		PropertyInfo pi4 = new PropertyInfo();
		pi4.setName("gostou");
		pi4.setValue(gostou);
		pi4.setType(String.class);
		request.addProperty(pi4);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		androidHttpTransport.call(SOAP_ACTION, envelope);
		
		int[] resultado = new int[3];
		String resultadoString;
		resultadoString = envelope.bodyIn.toString();
		
		
		try {
			
			resultadoString = resultadoString.substring(resultadoString.indexOf("=")+1);
			resultado[0] = Integer.parseInt(resultadoString.substring(0,resultadoString.indexOf(";")));
			if(resultado[0]<0)
				resultado[0]=0;
			
			resultadoString = resultadoString.substring(resultadoString.indexOf("=")+1);
			resultado[1] = Integer.parseInt(resultadoString.substring(0,resultadoString.indexOf(";")));
			if(resultado[1]<0)
				resultado[1]=0;
			
			resultadoString = resultadoString.substring(resultadoString.indexOf("=")+1);
			resultado[2] = Integer.parseInt(resultadoString.substring(0,resultadoString.indexOf(";")));
			if(resultado[2]<0)
				resultado[2]=0;
			
		} catch (Exception e) {
			throw new UpDownException("N�o foi poss�vel achar os itens do vetor de notas.");
		}
		
		return resultado;
	}
	
	/**
	 * Respostas menores que 0 significam erro
	 * @param restaurante
	 * @param horario
	 * @param dia
	 * @param gostou
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static int getUpDown(String restaurante, String horario, String dia, String gostou) throws IOException, XmlPullParserException
	{
		String METHOD_NAME = "getUpDown";
		String SOAP_ACTION = "http://libcoseas.pcs2420.poli.com/getUpDown";
		String URL = "http://107.20.161.57/axis2/services/CardapioWS4/getUpDown/";
		
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		PropertyInfo pi1 = new PropertyInfo();
		pi1.setName("restaurante");
		pi1.setValue(restaurante);
		pi1.setType(String.class);
		request.addProperty(pi1);
		
		PropertyInfo pi2 = new PropertyInfo();
		pi2.setName("horario");
		pi2.setValue(horario);
		pi2.setType(String.class);
		request.addProperty(pi2);
		
		PropertyInfo pi3 = new PropertyInfo();
		pi3.setName("dia");
		pi3.setValue(dia);
		pi3.setType(String.class);
		request.addProperty(pi3);
		
		PropertyInfo pi4 = new PropertyInfo();
		pi4.setName("gostou");
		pi4.setValue(gostou);
		pi4.setType(String.class);
		request.addProperty(pi4);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		SoapPrimitive response;
		int resultado = -10;
		
		androidHttpTransport.call(SOAP_ACTION, envelope);
		response = (SoapPrimitive)envelope.getResponse();
		resultado =  Integer.parseInt(response.toString());
				
		if(resultado<0)
			return 0;
		
		return resultado;
	}
	
	/**
	 * Envia um up ou down para o banco de dados
	 * @param restaurante
	 * @param horario
	 * @param dia
	 * @param gostou
	 * @return 0  se tiver enviado ok, x<0 se tiver ocorrido algum erro
	 * @throws IOException
	 * @throws UpDownException 
	 */
	public static int upDown(String restaurante, String horario, String dia, String gostou) throws IOException, UpDownException
	{		
		String METHOD_NAME = "votarPreferencia";
		String SOAP_ACTION = "http://libcoseas.pcs2420.poli.com/votarPreferencia";
		String URL = "http://107.20.161.57/axis2/services/CardapioWS4/votarPreferencia/";
		
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		PropertyInfo pi1 = new PropertyInfo();
		pi1.setName("restaurante");
		pi1.setValue(restaurante);
		pi1.setType(String.class);
		request.addProperty(pi1);
		
		PropertyInfo pi2 = new PropertyInfo();
		pi2.setName("horario");
		pi2.setValue(horario);
		pi2.setType(String.class);
		request.addProperty(pi2);
		
		PropertyInfo pi3 = new PropertyInfo();
		pi3.setName("dia");
		pi3.setValue(dia);
		pi3.setType(String.class);
		request.addProperty(pi3);
		
		PropertyInfo pi4 = new PropertyInfo();
		pi4.setName("gostou");
		pi4.setValue(gostou);
		pi4.setType(String.class);
		request.addProperty(pi4);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		
		SoapPrimitive response;
		int resultado = 0;
		
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			response = (SoapPrimitive)envelope.getResponse();
			resultado =  Integer.parseInt(response.toString());
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			throw new IOException();
		}
		
		if(resultado!=0)
			throw new UpDownException();
		
		return resultado;
		
	}
	
}
