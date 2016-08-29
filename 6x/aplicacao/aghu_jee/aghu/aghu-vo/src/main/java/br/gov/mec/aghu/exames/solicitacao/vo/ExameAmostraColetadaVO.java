package br.gov.mec.aghu.exames.solicitacao.vo;

import java.util.Date;

public class ExameAmostraColetadaVO {
	
	private Date dtExame;
	private Short ordemNivel1;
	private Short ordemNivel2;
	private String descricaoUsual;
	private String matDescricao;
	private String siglaExame;
	private Integer manSeq;
	private Integer soeSeq;
	private Short seqp;
	private String descGrpMatAnalise;
	private Integer seqGrupoMaterialAnalise;
	private Integer ordProntOnline;
	
	public enum Fields {
		DT_EXAME("dtExame"),
		ORDEM_NIVEL1("ordemNivel1"),
		ORDEM_NIVEL2("ordemNivel2"),
		DESCRICAO_USUAL("descricaoUsual"),
		MAT_DESCRICAO("matDescricao"),
		SIGLA("siglaExame"),
		MAN_SEQ("manSeq"),
		SOLICITACAO_SEQ("soeSeq"),
		ITEM_SOLICITACAO_SEQ("seqp"),
		DESCRICAO_GRUPO_MAT("descGrpMatAnalise"),
		SEQ_GRUPO_MAT_ANALISE("seqGrupoMaterialAnalise"),
		ORD_PRONT_ONLINE("ordProntOnline");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Date getDtExame() {
		return dtExame;
	}
	public void setDtExame(Date dtExame) {
		this.dtExame = dtExame;
	}
	public Short getOrdemNivel1() {
		return ordemNivel1;
	}
	public void setOrdemNivel1(Short ordemNivel1) {
		this.ordemNivel1 = ordemNivel1;
	}
	public Short getOrdemNivel2() {
		return ordemNivel2;
	}
	public void setOrdemNivel2(Short ordemNivel2) {
		this.ordemNivel2 = ordemNivel2;
	}
	public String getDescricaoUsual() {
		return descricaoUsual;
	}
	public void setDescricaoUsual(String descricaoUsual) {
		this.descricaoUsual = descricaoUsual;
	}
	public String getMatDescricao() {
		return matDescricao;
	}
	public void setMatDescricao(String matDescricao) {
		this.matDescricao = matDescricao;
	}
	public String getSiglaExame() {
		return siglaExame;
	}
	public void setSiglaExame(String siglaExame) {
		this.siglaExame = siglaExame;
	}
	public Integer getManSeq() {
		return manSeq;
	}
	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}
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
	public String getDescGrpMatAnalise() {
		return descGrpMatAnalise;
	}
	public void setDescGrpMatAnalise(String descGrpMatAnalise) {
		this.descGrpMatAnalise = descGrpMatAnalise;
	}
	public Integer getSeqGrupoMaterialAnalise() {
		return seqGrupoMaterialAnalise;
	}
	public void setSeqGrupoMaterialAnalise(Integer seqGrupoMaterialAnalise) {
		this.seqGrupoMaterialAnalise = seqGrupoMaterialAnalise;
	}
	public void setOrdProntOnline(Integer ordProntOnline) {
		this.ordProntOnline = ordProntOnline;
	}
	public Integer getOrdProntOnline() {
		return ordProntOnline;
	}
	
	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dtExame == null) ? 0 : dtExame.hashCode());
		result = prime * result + ((manSeq == null) ? 0 : manSeq.hashCode());
		result = prime
				* result
				+ ((seqGrupoMaterialAnalise == null) ? 0
						: seqGrupoMaterialAnalise.hashCode());
		result = prime * result + ((seqp == null) ? 0 : seqp.hashCode());
		result = prime * result
				+ ((siglaExame == null) ? 0 : siglaExame.hashCode());
		result = prime * result + ((soeSeq == null) ? 0 : soeSeq.hashCode());
		return result;
	}
	
	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}	
		if (obj == null){
			return false;
		}	
		if (!(obj instanceof ExameAmostraColetadaVO)){
			return false;
		}	
		ExameAmostraColetadaVO other = (ExameAmostraColetadaVO) obj;
		if (dtExame == null) {
			if (other.dtExame != null){
				return false;
			}	
		} else if (!dtExame.equals(other.dtExame)){
			return false;
		}	
		if (manSeq == null) {
			if (other.manSeq != null){
				return false;
			}	
		} else if (!manSeq.equals(other.manSeq)){
			return false;
		}	
		if (seqGrupoMaterialAnalise == null) {
			if (other.seqGrupoMaterialAnalise != null){
				return false;
			}	
		} else if (!seqGrupoMaterialAnalise
				.equals(other.seqGrupoMaterialAnalise)){
			return false;
		}	
		if (seqp == null) {
			if (other.seqp != null){
				return false;
			}	
		} else if (!seqp.equals(other.seqp)){
			return false;
		}	
		if (siglaExame == null) {
			if (other.siglaExame != null){
				return false;
			}	
		} else if (!siglaExame.equals(other.siglaExame)){
			return false;
		}	
		if (soeSeq == null) {
			if (other.soeSeq != null){
				return false;
			}	
		} else if (!soeSeq.equals(other.soeSeq)){
			return false;
		}	
		return true;
	}
}
