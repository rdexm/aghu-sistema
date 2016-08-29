package br.gov.mec.aghu.prescricaomedica.vo;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 */
public class AghEspecialidadeVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7754861779735037973L;

	private DominioSimNao preIndProfRealizaConsultoria;
	private Boolean preIndProfRealizaConsultoriaBoolean;
	private String espSigla;
	private String espNomeEspecialidade;
	private Short seq;
	private Short espSeq;
	private DominioSimNao indImpSoServico;
	private Integer cctCodigo;
	
	private String siglaNome = "";
	
	public AghEspecialidadeVO(Short seq, String sigla, String nome){
		this.seq = seq;
		this.espSigla = sigla;
		this.espNomeEspecialidade = nome;
		this.siglaNome = espSigla + "/" +espNomeEspecialidade;
	}
	public AghEspecialidadeVO(){
	}
	
	public DominioSimNao getPreIndProfRealizaConsultoria() {
		return preIndProfRealizaConsultoria;
	}

	public void setPreIndProfRealizaConsultoria(DominioSimNao preIndProfRealizaConsultoria) {
		this.preIndProfRealizaConsultoria = preIndProfRealizaConsultoria;
	}

	public String getEspSigla() {
		return espSigla;
	}

	public void setEspSigla(String espSigla) {
		this.espSigla = espSigla;
	}

	public String getEspNomeEspecialidade() {
		return espNomeEspecialidade;
	}

	public void setEspNomeEspecialidade(String espNomeEspecialidade) {
		this.espNomeEspecialidade = espNomeEspecialidade;
	}
	
	public Boolean getPreIndProfRealizaConsultoriaBoolean() {
		if (getPreIndProfRealizaConsultoria() != null) {
			return DominioSimNao.getBooleanInstance(getPreIndProfRealizaConsultoria());
		}
		return preIndProfRealizaConsultoriaBoolean;
	}

	public void setPreIndProfRealizaConsultoriaBoolean(Boolean preIndProfRealizaConsultoriaBoolean) {
		if (preIndProfRealizaConsultoriaBoolean != null) {
			setPreIndProfRealizaConsultoria(DominioSimNao.getInstance(preIndProfRealizaConsultoriaBoolean));
		}
		this.preIndProfRealizaConsultoriaBoolean = preIndProfRealizaConsultoriaBoolean;
	}
	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public Short getEspSeq() {
		return espSeq;
	}
	
	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
		
	public DominioSimNao getIndImpSoServico() {
		return indImpSoServico;
	}
	public void setIndImpSoServico(DominioSimNao indImpSoServico) {
		this.indImpSoServico = indImpSoServico;
	}
	public Integer getCctCodigo() {
		return cctCodigo;
	}
	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}
	
	public enum Fields {
		IND_PROF_REALIZA_CONSULTORIA("preIndProfRealizaConsultoria"),
		SIGLA("espSigla"),
		ESP_SEQ("espSeq"),
		NOME_ESPECIALIDADE("espNomeEspecialidade"),
		IND_IMP_SO_SERVICO("indImpSoServico"),
		CCT_CODIGO("cctCodigo"),
		SEQ("seq");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	public String getSiglaNome() {
		return siglaNome;
	}

	public void setSiglaNome(String siglaNome) {
		this.siglaNome = siglaNome;
	}
}