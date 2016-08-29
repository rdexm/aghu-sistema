package br.gov.mec.aghu.ambulatorio.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 */
public class MamConsultorAmbulatorioVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2916498452117455368L;
	
	private Integer mcaSeq;
	private DominioSituacao mcaIndSituacao;
	private Boolean mcaIndSituacaoBoolean; 
	private String espSigla;
	private String espNomeEespecialidade;
	private Integer eqpSeq;
	private String eqpNome;

	public Integer getMcaSeq() {
		return mcaSeq;
	}
	public void setMcaSeq(Integer mcaSeq) {
		this.mcaSeq = mcaSeq;
	}
	public DominioSituacao getMcaIndSituacao() {
		return mcaIndSituacao;
	}
	public void setMcaIndSituacao(DominioSituacao mcaIndSituacao) {
		this.mcaIndSituacao = mcaIndSituacao;
	}
	
	public Boolean getMcaIndSituacaoBoolean() {
		if (getMcaIndSituacao() != null) {
			return getMcaIndSituacao() == DominioSituacao.A ? Boolean.TRUE : Boolean.FALSE;
		}
		return mcaIndSituacaoBoolean;
	}

	public void setMcaIndSituacaoBoolean(Boolean mcaIndSituacaoBoolean) {
		if (mcaIndSituacaoBoolean != null) {
			if (mcaIndSituacaoBoolean) {
				setMcaIndSituacao(DominioSituacao.A);
			} else {
				setMcaIndSituacao(DominioSituacao.I);
			}
		}
		this.mcaIndSituacaoBoolean = mcaIndSituacaoBoolean;
	}
	
	
	public String getEspSigla() {
		return espSigla;
	}
	public void setEspSigla(String espSigla) {
		this.espSigla = espSigla;
	}
	public String getEspNomeEespecialidade() {
		return espNomeEespecialidade;
	}
	public void setEspNomeEespecialidade(String espNomeEespecialidade) {
		this.espNomeEespecialidade = espNomeEespecialidade;
	}
	public Integer getEqpSeq() {
		return eqpSeq;
	}
	public void setEqpSeq(Integer eqpSeq) {
		this.eqpSeq = eqpSeq;
	}
	public String getEqpNome() {
		return eqpNome;
	}
	public void setEqpNome(String eqpNome) {
		this.eqpNome = eqpNome;
	}
	public enum Fields {
		SEQ("mcaSeq"),
		SITUACAO("mcaIndSituacao"),
		SIGLA("espSigla"),
		NOME_ESPECIALIDADE("espNomeEespecialidade"),
		EQP_SEQ("eqpSeq"),
		EQP_NOME("eqpNome");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
}