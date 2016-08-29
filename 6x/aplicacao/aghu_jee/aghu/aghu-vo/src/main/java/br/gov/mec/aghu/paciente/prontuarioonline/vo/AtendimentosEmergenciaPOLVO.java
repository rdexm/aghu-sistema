package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.core.commons.BaseBean;

public class AtendimentosEmergenciaPOLVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2754807825581827277L;
	private Integer atdSeq;
	private Long trgSeq;
	private Boolean indSumAlta;
	private Date inicioTriagem;
	private Date fimTriagem;
	private Date alta;	 
	private Short espSeqPai;
	private String espNomeEspecialidade;
	private Integer numero;	
	private DominioPacAtendimento indPacAtendimento;
	private Date dthrInicio;
	private Date dataOrd;
	 
	private Integer id;
	
	public enum Fields {
	  ESP_NOME_ESPECIALIDADE("espNomeEspecialidade"),
	  ESP_SEQ_PAI("espSeqPai"),
	  CON_NUMERO("numero"),
	  DTHR_INICIO("dthrInicio")
	  ;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	public Long getTrgSeq() {
		return trgSeq;
	}


	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}


	public Boolean getIndSumAlta() {
		return indSumAlta;
	}


	public void setIndSumAlta(Boolean indSumAlta) {
		this.indSumAlta = indSumAlta;
	}


	public Date getInicioTriagem() {
		return inicioTriagem;
	}


	public void setInicioTriagem(Date inicioTriagem) {
		this.inicioTriagem = inicioTriagem;
	}


	public Date getAlta() {
		return alta;
	}


	public void setAlta(Date alta) {
		this.alta = alta;
	}

	
	public Short getEspSeqPai() {
		return espSeqPai;
	}


	public void setEspSeqPai(Short espSeqPai) {
		this.espSeqPai = espSeqPai;
	}


	public String getEspNomeEspecialidade() {
		return espNomeEspecialidade;
	}


	public void setEspNomeEspecialidade(String espNomeEspecialidade) {
		this.espNomeEspecialidade = espNomeEspecialidade;
	}


	public Integer getNumero() {
		return numero;
	}


	public void setNumero(Integer numero) {
		this.numero = numero;
	}


	public Integer getAtdSeq() {
		return atdSeq;
	}


	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public DominioPacAtendimento getIndPacAtendimento() {
		return indPacAtendimento;
	}


	public void setIndPacAtendimento(DominioPacAtendimento indPacAtendimento) {
		this.indPacAtendimento = indPacAtendimento;
	}


	public Date getFimTriagem() {
		return fimTriagem;
	}


	public void setFimTriagem(Date fimTriagem) {
		this.fimTriagem = fimTriagem;
	}


	public Date getDthrInicio() {
		return dthrInicio;
	}


	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}


	public Date getDataOrd() {
		return dataOrd;
	}


	public void setDataOrd(Date dataOrd) {
		this.dataOrd = dataOrd;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atdSeq == null) ? 0 : atdSeq.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((trgSeq == null) ? 0 : trgSeq.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}	
		if (obj == null){
			return false;
		}	
		if (!(obj instanceof AtendimentosEmergenciaPOLVO)){
			return false;
		}	
		AtendimentosEmergenciaPOLVO other = (AtendimentosEmergenciaPOLVO) obj;
		if (atdSeq == null) {
			if (other.atdSeq != null){
				return false;
			}	
		} else if (!atdSeq.equals(other.atdSeq)){
			return false;
		}	
		if (id == null) {
			if (other.id != null){
				return false;
			}	
		} else if (!id.equals(other.id)){
			return false;
		}	
		if (trgSeq == null) {
			if (other.trgSeq != null){
				return false;
			}	
		} else if (!trgSeq.equals(other.trgSeq)){
			return false;
		}	
		return true;
	}
			
			

}
	