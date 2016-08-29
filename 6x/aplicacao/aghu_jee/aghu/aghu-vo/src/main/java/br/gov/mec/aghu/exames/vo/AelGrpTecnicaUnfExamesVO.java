package br.gov.mec.aghu.exames.vo;

import br.gov.mec.aghu.model.AelGrpTecnicaUnfExamesId;

public class AelGrpTecnicaUnfExamesVO {

	private AelGrpTecnicaUnfExamesId id;
	private String descricaoExame;
	private String descricaoMaterialAnalise;
	private String descricaoUnidadeExecutora;
	private String descricaoGrupoExameTecnica;
	


	private Integer grtSeq;
	private String ufeEmaExaSigla;
	private Integer ufeEmaManSeq;
	private Short ufeUnfSeq;
	
	
	
	public Integer getGrtSeq() {
		return grtSeq;
	}

	public void setGrtSeq(Integer grtSeq) {
		this.grtSeq = grtSeq;
	}

	public String getUfeEmaExaSigla() {
		return ufeEmaExaSigla;
	}

	public void setUfeEmaExaSigla(String ufeEmaExaSigla) {
		this.ufeEmaExaSigla = ufeEmaExaSigla;
	}

	public Integer getUfeEmaManSeq() {
		return ufeEmaManSeq;
	}

	public void setUfeEmaManSeq(Integer ufeEmaManSeq) {
		this.ufeEmaManSeq = ufeEmaManSeq;
	}

	public Short getUfeUnfSeq() {
		return ufeUnfSeq;
	}

	public void setUfeUnfSeq(Short ufeUnfSeq) {
		this.ufeUnfSeq = ufeUnfSeq;
	}

	public AelGrpTecnicaUnfExamesId getId() {
		return id;
	}

	public void setId(AelGrpTecnicaUnfExamesId id) {
		this.id = id;
	}

	public String getDescricaoExame() {
		return descricaoExame;
	}

	public void setDescricaoExame(String descricaoExame) {
		this.descricaoExame = descricaoExame;
	}

	public String getDescricaoMaterialAnalise() {
		return descricaoMaterialAnalise;
	}

	public void setDescricaoMaterialAnalise(String descricaoMaterialAnalise) {
		this.descricaoMaterialAnalise = descricaoMaterialAnalise;
	}

	public String getDescricaoUnidadeExecutora() {
		return descricaoUnidadeExecutora;
	}

	public void setDescricaoUnidadeExecutora(String descricaoUnidadeExecutora) {
		this.descricaoUnidadeExecutora = descricaoUnidadeExecutora;
	}
	
	public String getDescricaoGrupoExameTecnica() {
		return descricaoGrupoExameTecnica;
	}

	public void setDescricaoGrupoExameTecnica(String descricaoGrupoExameTecnica) {
		this.descricaoGrupoExameTecnica = descricaoGrupoExameTecnica;
	}
	
	
	public String getGrupoSeqDesc() {
		StringBuilder sb = new StringBuilder();
			sb.append(this.grtSeq)
			.append(" - ")
			.append(this.descricaoGrupoExameTecnica);
		return sb.toString();
	}
	
	
	public String getExameSeqDesc() {
		StringBuilder sb = new StringBuilder();
			sb.append(this.descricaoExame)
			.append(" - ")
			.append(this.descricaoMaterialAnalise);
		return sb.toString();
	}
	
	
	public AelGrpTecnicaUnfExamesId obterAelGrpTecnicaUnfExamesId() {
		AelGrpTecnicaUnfExamesId id = new AelGrpTecnicaUnfExamesId();
		
		id.setGrtSeq(this.grtSeq);
		id.setUfeEmaExaSigla(this.ufeEmaExaSigla);
		id.setUfeEmaManSeq(this.ufeEmaManSeq);
		id.setUfeUnfSeq(this.ufeUnfSeq);
		
		return id;
	}
	
	
	public enum Fields {
		
		ID("id"),
		UFE_EMA_EXA_SIGLA("ufeEmaExaSigla"),
		UFE_EMA_MAN_SEQ("ufeEmaManSeq"),
		UFE_UNF_SEQ("ufeUnfSeq"),
		GRT_SEQ("grtSeq"),
		DESCRICAO_EXAME("descricaoExame"),
		DESCRICAO_MAT_ANALISE("descricaoMaterialAnalise"),
		DESCRICAO_UNIDADE_EXECUTORA("descricaoUnidadeExecutora"),
		DESCRICAO_GRUPO_EXAME_TECNICA("descricaoGrupoExameTecnica"),
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

}
