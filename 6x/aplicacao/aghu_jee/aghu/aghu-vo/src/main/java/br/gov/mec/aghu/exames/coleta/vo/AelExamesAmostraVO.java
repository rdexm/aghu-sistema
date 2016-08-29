package br.gov.mec.aghu.exames.coleta.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioTipoColeta;

/**
 * 
 * @author gzapalaglio
 *
 */
public class AelExamesAmostraVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4632356688295331516L;
	
	private Integer soeSeq;
	private Short seqp;
	private String descricaoUsual;
	private DominioTipoColeta tipoColeta;
	private Short unfSeq;
	private DominioSituacaoAmostra situacao;
	private Date dthrAgenda;
	private Boolean selecionado;

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public String getDescricaoUsual() {
		return descricaoUsual;
	}

	public void setDescricaoUsual(String descricaoUsual) {
		this.descricaoUsual = descricaoUsual;
	}

	public DominioTipoColeta getTipoColeta() {
		return tipoColeta;
	}

	public void setTipoColeta(DominioTipoColeta tipoColeta) {
		this.tipoColeta = tipoColeta;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public DominioSituacaoAmostra getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoAmostra situacao) {
		this.situacao = situacao;
	}
	

	public Date getDthrAgenda() {
		return dthrAgenda;
	}

	public void setDthrAgenda(Date dthrAgenda) {
		this.dthrAgenda = dthrAgenda;
	}
	
	

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public AelExamesAmostraVO() {
		
	}
	
	public enum Fields {
		ISE_SOE_SEQ("soeSeq"),
		ISE_SEQP("seqp"),
		DESCRICAO_USUAL("descricaoUsual"),
		TIPO_COLETA("tipoColeta"),
		UNF_SEQ("unfSeq"),
		SITUACAO("situacao"),
		DTHR_AGENDA("dthrAgenda");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((descricaoUsual == null) ? 0 : descricaoUsual.hashCode());
		result = prime * result + ((seqp == null) ? 0 : seqp.hashCode());
		result = prime * result
				+ ((situacao == null) ? 0 : situacao.hashCode());
		result = prime * result + ((soeSeq == null) ? 0 : soeSeq.hashCode());
		result = prime * result
				+ ((tipoColeta == null) ? 0 : tipoColeta.hashCode());
		result = prime * result + ((unfSeq == null) ? 0 : unfSeq.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AelExamesAmostraVO other = (AelExamesAmostraVO) obj;
		if (descricaoUsual == null) {
			if (other.descricaoUsual != null) {
				return false;
			}
		} else if (!descricaoUsual.equals(other.descricaoUsual)) {
			return false;
		}
		
		if (seqp == null) {
			if (other.seqp != null) {
				return false;
			}
		} else if (!seqp.equals(other.seqp)) {
			return false;
		}
		if (situacao != other.situacao) {
			return false;
		}
		if (soeSeq == null) {
			if (other.soeSeq != null) {
				return false;
			}
		} else if (!soeSeq.equals(other.soeSeq)) {
			return false;
		}
		if (tipoColeta != other.tipoColeta) {
			return false;
		}
		if (unfSeq == null) {
			if (other.unfSeq != null) {
				return false;
			}
		} else if (!unfSeq.equals(other.unfSeq)) {
			return false;
		}
		return true;
	}
	
	
}
	
