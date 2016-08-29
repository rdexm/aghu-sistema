package br.gov.mec.aghu.paciente.vo;


import java.util.Date;



public class RelatorioParadaInternacaoExamesVO {

	private String data;          //data_evento de Q_SUE2
	private Double valor;         //REE_VALOR de Q_SUE2
	private String nome;          //CAL_NOME de Q_SUE2
	private String hora;          //hora_evento de Q_SUE2
	private String identificador; //Alimentado pelo retorno de CF_IDENTIFICADORFormula
	private Date dataEvento;	  //data_hora_evento de Q_SUE2
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getHora() {
		return hora;
	}
	public void setHora(String hora) {
		this.hora = hora;
	}
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	
	public String getDataHora(){
		StringBuilder sb = new StringBuilder();
		sb.append(data).append(' ').append(hora);
		return sb.toString();
	}
	
	public void setDataEvento(Date dataEvento) {
		this.dataEvento = dataEvento;
	}
	
	public Date getDataEvento() {
		return dataEvento;
	}
}
