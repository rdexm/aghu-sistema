package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;
import java.util.Date;

public class ResultadoExamePim2VO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2180081639176301226L;
	
	private Date dthrLiberada;
	private Long valor;
	private Short quantidadeCasasDecimais;
	private String nome;
	
	public Date getDthrLiberada() {
		return dthrLiberada;
	}
	
	public void setDthrLiberada(Date dthrLiberada) {
		this.dthrLiberada = dthrLiberada;
	}
	
	public Long getValor() {
		return valor;
	}
	
	public void setValor(Long valor) {
		this.valor = valor;
	}
	
	public Short getQuantidadeCasasDecimais() {
		return quantidadeCasasDecimais;
	}
	
	public void setQuantidadeCasasDecimais(Short quantidadeCasasDecimais) {
		this.quantidadeCasasDecimais = quantidadeCasasDecimais;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public enum Fields {
		DTHR_LIBERADA("dthrLiberada"),
		VALOR("valor"),
		QTD_CASAS_DECIMAIS("quantidadeCasasDecimais"),
		NOME("nome");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}
