package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;

public class FatTabIPHVO implements Serializable {

	private static final long serialVersionUID = -402823688023298764L;

	private Short phoSeq;
	private Integer iphSeq;
	private Long codTabela;
	private Short qtdMaximaExecucao;
	private Date dtInicioCompetencia;
	
	public FatTabIPHVO() {}

	public FatTabIPHVO(Long codTabela) {
		super();
		this.codTabela = codTabela;
	}

	public enum Fields {
		IPH_SEQ("iphSeq"), 
		PHO_SEQ("phoSeq"), 
		COD_TABELA("codTabela"),
		QTD_MAXIMA_EXECUCAO("qtdMaximaExecucao"),
		DT_INICIO_COMPETENCIA("dtInicioCompetencia");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Short getPhoSeq() {
		return phoSeq;
	}

	public void setPhoSeq(Short phoSeq) {
		this.phoSeq = phoSeq;
	}

	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	public Long getCodTabela() {
		return codTabela;
	}

	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}

	public Short getQtdMaximaExecucao() {
		return qtdMaximaExecucao;
	}

	public void setQtdMaximaExecucao(Short qtdMaximaExecucao) {
		this.qtdMaximaExecucao = qtdMaximaExecucao;
	}

	public Date getDtInicioCompetencia() {
		return dtInicioCompetencia;
	}

	public void setDtInicioCompetencia(Date dtInicioCompetencia) {
		this.dtInicioCompetencia = dtInicioCompetencia;
	}
	
	
	// ####################################################################################################################
	// NÃO ALTERAR, POIS É USADO PARA BUSCAR ELEMENTO EM LISTA DE ACORDO COM COD_TABELA
	// ####################################################################################################################	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((codTabela == null) ? 0 : codTabela.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FatTabIPHVO)) {
			return false;
		}
		FatTabIPHVO other = (FatTabIPHVO) obj;
		if (codTabela == null) {
			if (other.codTabela != null) {
				return false;
			}
		} else if (!codTabela.equals(other.codTabela)) {
			return false;
		}
		return true;
	}
	// ####################################################################################################################
	
}