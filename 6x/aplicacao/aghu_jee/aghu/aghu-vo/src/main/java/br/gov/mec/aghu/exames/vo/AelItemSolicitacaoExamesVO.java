package br.gov.mec.aghu.exames.vo;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoColeta;

public class AelItemSolicitacaoExamesVO {
	
	private Integer iseSoeSeq;
	private Short iseSeqp;
	private DominioTipoColeta tipoColeta;	
	private String descricaoUsual;
	private String descricao;
	private String exameSigla;
	private String sitCodigo;	
	private Short ufeUnfSeq;
	private Integer velSeqp;
	
	
	public String getDescricaoUsual() {
		return descricaoUsual;
	}

	public void setDescricaoUsual(String descricaoUsual) {
		this.descricaoUsual = descricaoUsual;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	public Short getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}	

	public DominioTipoColeta getTipoColeta() {
		return tipoColeta;
	}

	public void setTipoColeta(DominioTipoColeta tipoColeta) {
		this.tipoColeta = tipoColeta;
	}

	public String getExameSigla() {
		return exameSigla;
	}

	public void setExameSigla(String exameSigla) {
		this.exameSigla = exameSigla;
	}
	
	public String getSitCodigo() {
		return sitCodigo;
	}

	public void setSitCodigo(String sitCodigo) {
		this.sitCodigo = sitCodigo;
	}
	
	public Short getUfeUnfSeq() {
		return ufeUnfSeq;
	}

	public void setUfeUnfSeq(Short ufeUnfSeq) {
		this.ufeUnfSeq = ufeUnfSeq;
	}
	
	public String getNomeUsualMaterial() {
		StringBuffer returnValue = new StringBuffer();
		if (StringUtils.isNotBlank(this.getDescricaoUsual())) {
			returnValue.append(this.getDescricaoUsual());
		}
		if (StringUtils.isNotBlank(this.getDescricao())) {
			if (returnValue.length() > 0) {
				returnValue.append(" / ");
			}
			returnValue.append(this.getDescricao());
		}
		return returnValue.length() > 0 ? returnValue.toString() : null;
	}
	
	
	public String getSituacao() {
		if(StringUtils.isNotBlank(this.sitCodigo)) { 
			return DominioSituacaoItemSolicitacaoExame.valueOf(this.sitCodigo).getDescricao();
		} else {
			return null;
		}
	}

	
	public Integer getVelSeqp() {
		return velSeqp;
	}

	public void setVelSeqp(Integer velSeqp) {
		this.velSeqp = velSeqp;
	}
	
		
	public enum Fields {
		ISE_SOE_SEQ("iseSoeSeq"),
		ISE_SEQP("iseSeqp"),
		DESCRICAO_USUAL("descricaoUsual"),//
		DESC_MATERIAL_ANALISE("descricao"),//
		UFE_EMA_EXA_SIGLA("exameSigla"),//
		TIPO_COLETA("tipoColeta"),//
		SITUACAO_CODIGO("sitCodigo"),//
		UFE_UNF_SEQ("ufeUnfSeq"),//
		VEL_SEQP("velSeqp"),//
		;
		
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((iseSeqp == null) ? 0 : iseSeqp.hashCode());
		result = prime * result
				+ ((iseSoeSeq == null) ? 0 : iseSoeSeq.hashCode());
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
		if (!(obj instanceof AelItemSolicitacaoExamesVO)) {
			return false;
		}
		AelItemSolicitacaoExamesVO other = (AelItemSolicitacaoExamesVO) obj;
		if (iseSeqp == null) {
			if (other.iseSeqp != null) {
				return false;
			}
		} else if (!iseSeqp.equals(other.iseSeqp)) {
			return false;
		}
		if (iseSoeSeq == null) {
			if (other.iseSoeSeq != null) {
				return false;
			}
		} else if (!iseSoeSeq.equals(other.iseSoeSeq)) {
			return false;
		}
		return true;
	}

}
