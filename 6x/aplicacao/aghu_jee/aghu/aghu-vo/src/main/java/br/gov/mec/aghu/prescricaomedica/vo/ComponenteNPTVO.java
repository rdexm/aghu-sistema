package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioIdentificacaoComponenteNPT;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 */
public class ComponenteNPTVO implements BaseBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7607874543706741501L;
	
	private Integer seqJn;
	private String nomeMedicamento;
	private String medicamento;
	private String grupo;
	private Short seqGrupo;
	private String descricao;
	private String descricaoTrunc;
	private Short ordem;
	private DominioIdentificacaoComponenteNPT identificacao;
	private String identificacaoTrunc;
	private String observacao;
	private DominioSituacao situacao;
	private Boolean situacaoBoolean;
	private Boolean adulto;
	private Boolean pediatria;
	private Date criadoEm;
	private String criadoPor;
	private Date dataAtualizacao;
	private Integer medMatCodigo;


	public String getNomeMedicamento() {
		return nomeMedicamento;
	}
	public void setNomeMedicamento(String nomeMedicamento) {
		this.nomeMedicamento = nomeMedicamento;
	}
	public String getMedicamento() {
		if (getMedMatCodigo() != null && getNomeMedicamento() != null) {
			medicamento = getMedMatCodigo() + "-" + getNomeMedicamento();
		}
		return medicamento;
	}
	public String getGrupo() {
		return grupo;
	}
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Short getOrdem() {
		return ordem;
	}
	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}
	public DominioIdentificacaoComponenteNPT getIdentificacao() {
		return identificacao;
	}
	public void setIdentificacao(DominioIdentificacaoComponenteNPT identificacao) {
		this.identificacao = identificacao;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public DominioSituacao getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	public Boolean getSituacaoBoolean() {
		if (getSituacao() != null) {
			return getSituacao().isAtivo();
		}
		return situacaoBoolean;
	}
	public void setSituacaoBoolean(Boolean situacaoBoolean) {
		if (situacaoBoolean != null) {
			setSituacao(DominioSituacao.getInstance(situacaoBoolean));
		}
		this.situacaoBoolean = situacaoBoolean;
	}
	public Boolean getAdulto() {
		return adulto;
	}
	public void setAdulto(Boolean adulto) {
		this.adulto = adulto;
	}
	public Boolean getPediatria() {
		return pediatria;
	}
	public void setPediatria(Boolean pediatria) {
		this.pediatria = pediatria;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	public String getCriadoPor() {
		return criadoPor;
	}
	public void setCriadoPor(String criadoPor) {
		this.criadoPor = criadoPor;
	}
	public void setMedicamento(String medicamento) {
		this.medicamento = medicamento;
	}
	public Integer getSeqJn() {
		return seqJn;
	}
	public void setSeqJn(Integer seqJn) {
		this.seqJn = seqJn;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}
	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}
	public Integer getMedMatCodigo() {
		return medMatCodigo;
	}
	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}

	public Short getSeqGrupo() {
		return seqGrupo;
	}
	public void setSeqGrupo(Short seqGrupo) {
		this.seqGrupo = seqGrupo;
	}

	public String getDescricaoTrunc() {
		return descricaoTrunc;
	}
	public void setDescricaoTrunc(String descricaoTrunc) {
		this.descricaoTrunc = descricaoTrunc;
	}
	public String getIdentificacaoTrunc() {
		return identificacaoTrunc;
	}
	public void setIdentificacaoTrunc(String identificacaoTrunc) {
		this.identificacaoTrunc = identificacaoTrunc;
	}


	public enum Fields {
		CODIGO_MEDICAMENTO("codigoMedicamento"),
		NOME_MEDICAMENTO("nomeMedicamento"),
		GRUPO("grupo"),
		SEQ_GRUPO("seqGrupo"),
		DESCRICAO("descricao"),
		ORDEM("ordem"),
		IDENTIFICACAO("identificacao"),
		OBSERVACAO("observacao"),
		SITUACAO("situacao"),
		ADULTO("adulto"),
		PEDIATRIA("pediatria"),
		CRIADO_EM("criadoEm"),
		CRIADO_POR("criadoPor"),
		DT_ATUALIZACAO("dataAtualizacao"),
		MED_MAT_CODIGO("medMatCodigo"),
		SEQ_JN("seqJn");

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