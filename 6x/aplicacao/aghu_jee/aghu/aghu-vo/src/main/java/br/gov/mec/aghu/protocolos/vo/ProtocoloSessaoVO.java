package br.gov.mec.aghu.protocolos.vo;

import java.io.Serializable;

public class ProtocoloSessaoVO implements Serializable {

	private static final long serialVersionUID = -7815275833514772016L;
	
	private String descricao;
	
	private Integer qtdCiclo;

	private Short seqTipoSessao;
	
	private Short diasTratamento;
	
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getQtdCiclo() {
		return qtdCiclo;
	}

	public void setQtdCiclo(Integer qtdCiclo) {
		this.qtdCiclo = qtdCiclo;
	}

	public enum Fields {	
		SEQ_TIPO_SESSAO("seqTipoSessao"),
		DESCRICAO("descricao"),
		QTD_CICLO("qtdCiclo"),
		DIAS_TRATAMENTO("diasTratamento");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Short getSeqTipoSessao() {
		return seqTipoSessao;
	}

	public void setSeqTipoSessao(Short seqTipoSessao) {
		this.seqTipoSessao = seqTipoSessao;
	}

	public Short getDiasTratamento() {
		return diasTratamento;
	}

	public void setDiasTratamento(Short diasTratamento) {
		this.diasTratamento = diasTratamento;
	}	
}
