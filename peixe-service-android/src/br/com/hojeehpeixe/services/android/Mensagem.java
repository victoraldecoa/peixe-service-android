package br.com.hojeehpeixe.services.android;

import java.io.IOException;

import javax.mail.MessagingException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import br.com.hojeehpeixe.services.android.exceptions.MensagemException;

public class Mensagem {
	
	private String email;
	private String conteudo;
	private String tipo;
	private String destino;
	private String restaurante;
	
	public String getAssunto() {
		return this.getDestino() + " / " + this.tipo + " / " + this.getRestaurante();
	}
	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}
	public String getConteudo() {
		return conteudo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getTipo() {
		return tipo;
	}
	public void setDestino(String destino) {
		this.destino = destino;
	}
	public String getDestino() {
		return destino;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmail() {
		return email;
	}
	public void setRestaurante(String restaurante) {
		this.restaurante = restaurante;
	}
	public String getRestaurante() {
		return restaurante;
	}
	
	public boolean isMensagemCompleta()
	{
		if(getEmail()==null || getEmail().equalsIgnoreCase(""))
			return false;
		
		if(getConteudo()==null || getConteudo().equalsIgnoreCase(""))
			return false;

		return true;
	}

	public void enviar() throws IOException, XmlPullParserException, MessagingException, MensagemException
	{
		
		if(isMensagemCompleta()==false)
			throw new MensagemException("Não foi possível enviar a mensagem, pois algum dos campos obrigatórios não foi preenchido.");
		
		String NAMESPACE = "http://ws.apache.org/axis2";
		String METHOD_NAME = "enviaEmail";
		String SOAP_ACTION = "http://ws.apache.org/axis2";
		String URL = "http://143.107.102.24:9090/axis2/services/Email2/enviaEmail/";
		
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		PropertyInfo pi1 = new PropertyInfo();
		pi1.setName("from");
		pi1.setValue(this.email);
		pi1.setType(String.class);
		request.addProperty(pi1);
		
		PropertyInfo pi2 = new PropertyInfo();
		pi2.setName("subject");
		pi2.setValue(this.getAssunto());
		pi2.setType(String.class);
		request.addProperty(pi2);
		
		PropertyInfo pi3 = new PropertyInfo();
		pi3.setName("text");
		pi3.setValue(this.getConteudo());
		pi3.setType(String.class);
		request.addProperty(pi3);
		
		PropertyInfo pi4 = new PropertyInfo();
		pi4.setName("aplicativo");
		pi4.setValue("peixe");
		pi4.setType(String.class);
		request.addProperty(pi4);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		androidHttpTransport.call(SOAP_ACTION, envelope);
		SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
		String resultado =  response.toString();
		
		if(resultado.equalsIgnoreCase("Email enviado!")==false)
		{
			throw new MessagingException();
		}
				
	}
	
}
