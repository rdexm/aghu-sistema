package br.gov.mec.aghu.exames.vo;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AelAmostraItemExamesId;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

public class AelAmostraItemExamesVO extends BaseEntityId<AelAmostraItemExamesId> implements java.io.Serializable {

	private static final long serialVersionUID = -7175978469707225137L;

	private AelAmostraItemExamesId id; // Este VO é utilizado em um DynamicDataModel
	private Integer iseSoeSeq;
	private Short iseSeqp;
	private Integer amoSoeSeq;
	private Integer amoSeqp;
	private Boolean selecionado;
	private Short mapa;
	private Integer numeroMapa;
	private String exame;
	private String descricaoUsual;
	private String descricao;
	private String equipamento;
	private Boolean enviado;

	public Integer getAmoSoeSeq() {
		return amoSoeSeq;
	}

	public void setAmoSoeSeq(Integer amoSoeSeq) {
		this.amoSoeSeq = amoSoeSeq;
	}

	public Integer getAmoSeqp() {
		return amoSeqp;
	}

	public void setAmoSeqp(Integer amoSeqp) {
		this.amoSeqp = amoSeqp;
	}

	public Short getMapa() {
		return mapa;
	}

	public Integer getNumeroMapa() {
		return numeroMapa;
	}

	public String getExame() {
		return exame;
	}

	public void setMapa(Short mapa) {
		this.mapa = mapa;
	}

	public void setNumeroMapa(Integer numeroMapa) {
		this.numeroMapa = numeroMapa;
	}

	public void setExame(String exame) {
		this.exame = exame;
	}

	public String getEquipamento() {
		return equipamento;
	}

	public void setEquipamento(String equipamento) {
		this.equipamento = equipamento;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public Boolean getEnviado() {
		return enviado;
	}

	public void setEnviado(Boolean enviado) {
		this.enviado = enviado;
	}

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

	public DominioSimNao getIndEnviado() {
		return DominioSimNao.getInstance(this.enviado);
	}

	@Override
	// Este VO é utilizado em um DynamicDataModel
	public AelAmostraItemExamesId getId() {
		id = new AelAmostraItemExamesId();
		id.setAmoSoeSeq(this.amoSoeSeq);
		id.setAmoSeqp(this.amoSeqp);
		id.setIseSoeSeq(this.iseSoeSeq);
		id.setIseSeqp(this.iseSeqp);
		return id;
	}

	@Override
	public void setId(AelAmostraItemExamesId id) {
		this.id = id; // Este VO é utilizado em um DynamicDataModel
	}

	public enum Fields {
		ISE_SOE_SEQ("iseSoeSeq"), ISE_SEQP("iseSeqp"), AMO_SOE_SEQ("amoSoeSeq"), //
		AMO_SEQP("amoSeqp"), //
		NRO_MAPA("numeroMapa"), //
		IND_ENVIADO("enviado"), //
		CONFIG_MAPA_SEQ("mapa"), //
		EQUIPAMENTO_DESCRICAO("equipamento"), //
		DESCRICAO_USUAL("descricaoUsual"), //
		DESC_MATERIAL_ANALISE("descricao"), //
		UFE_EMA_EXA_SIGLA("exame"), //
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
		result = prime * result + ((amoSeqp == null) ? 0 : amoSeqp.hashCode());
		result = prime * result + ((amoSoeSeq == null) ? 0 : amoSoeSeq.hashCode());
		result = prime * result + ((iseSeqp == null) ? 0 : iseSeqp.hashCode());
		result = prime * result + ((iseSoeSeq == null) ? 0 : iseSoeSeq.hashCode());
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
		if (!(obj instanceof AelAmostraItemExamesVO)) {
			return false;
		}
		AelAmostraItemExamesVO other = (AelAmostraItemExamesVO) obj;
		if (amoSeqp == null) {
			if (other.amoSeqp != null) {
				return false;
			}
		} else if (!amoSeqp.equals(other.amoSeqp)) {
			return false;
		}
		if (amoSoeSeq == null) {
			if (other.amoSoeSeq != null) {
				return false;
			}
		} else if (!amoSoeSeq.equals(other.amoSoeSeq)) {
			return false;
		}
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
