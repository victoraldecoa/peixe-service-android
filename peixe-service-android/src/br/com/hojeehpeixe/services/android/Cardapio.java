package br.com.hojeehpeixe.services.android;

public class Cardapio {	
	public long id;
	public String local;
	public int semana;
	public int atual;
	public int ano;
	
	/**
	 * almoco[0] -> almoco da Segunda
	 * almoco[6] -> almoco do Domingo
	 */
	public CardapioDia[] almoco;
	public CardapioDia[] janta;
	
	public Cardapio() {
		almoco = new CardapioDia[7];
		janta = new CardapioDia[7];
	}
	
	public Cardapio(String local, int semana, int atual) {
		almoco = new CardapioDia[7];
		janta = new CardapioDia[7];
		
		this.local = local;
		this.semana = semana;
		this.atual = atual;
	}
}
