package br.gov.mec.aghu.protocolos.vo;

import java.io.Serializable;

public class ProtocoloAssociadoVO implements Serializable {

	
	private static final long serialVersionUID = 6900965554595250114L;	
	
	private Integer seq;
	private Integer seqProtocoloAssociacao;
	private Integer agrupador;
	private String titulo;

	public Integer getAgrupador() {
		return agrupador;
	}

	public void setAgrupador(Integer agrupador) {
		this.agrupador = agrupador;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Integer getSeqProtocoloAssociacao() {
		return seqProtocoloAssociacao;
	}

	public void setSeqProtocoloAssociacao(Integer seqProtocoloAssociacao) {
		this.seqProtocoloAssociacao = seqProtocoloAssociacao;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public enum Fields {	
		SEQ("seq"),
		SEQ_PROTOCOLO_ASSOCIACAO("seqProtocoloAssociacao"),
		AGRUPADOR("agrupador"),
		TITULO("titulo");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
}
