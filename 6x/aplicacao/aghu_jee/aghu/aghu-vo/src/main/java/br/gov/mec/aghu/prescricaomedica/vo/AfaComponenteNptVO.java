package br.gov.mec.aghu.prescricaomedica.vo;

public class AfaComponenteNptVO {
	
	private Integer medMatCodigoComponenteNpts;
	
	private String descricaoComponenteNpts;
	
	private Double qtdItemNpt;
	
	private String secUnidadeVmpmDosagem;
	
	
	
	
	public enum Fields {
		MED_MAT_CODIGO_COMPONENTE_NPTS("medMatCodigoComponenteNpts"),
		DESCRICAO_COMPONENTE_NPT("descricaoComponenteNpts"),
		QTD_ITEM_NPT_PADRAO("qtdItemNpt"),
		SEQ_UNIDADE_VMPM_DOSAGEM("secUnidadeVmpmDosagem");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}
		
		@Override
		public String toString() {
			return this.fields;
		}

	}
	
	
	

	public String getDescricaoComponenteNpts() {
		return descricaoComponenteNpts;
	}

	public void setDescricaoComponenteNpts(String descricaoComponenteNpts) {
		this.descricaoComponenteNpts = descricaoComponenteNpts;
	}

	public Double getQtdItemNpt() {
		return qtdItemNpt;
	}

	public void setQtdItemNpt(Double qtdItemNpt) {
		this.qtdItemNpt = qtdItemNpt;
	}

	public String getSecUnidadeVmpmDosagem() {
		return secUnidadeVmpmDosagem;
	}

	public void setSecUnidadeVmpmDosagem(String secUnidadeVmpmDosagem) {
		this.secUnidadeVmpmDosagem = secUnidadeVmpmDosagem;
	}

	public Integer getMedMatCodigoComponenteNpts() {
		return medMatCodigoComponenteNpts;
	}

	public void setMedMatCodigoComponenteNpts(Integer medMatCodigoComponenteNpts) {
		this.medMatCodigoComponenteNpts = medMatCodigoComponenteNpts;
	}
	

	
	
	
	
	
	
}
