package br.gov.mec.aghu.prescricaomedica.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class AnuTipoItemDietaVO implements BaseBean{

	private static final long serialVersionUID = 6626705020507864152L;
	
	private Integer seq;
	private String descricao;
	private Boolean indDietaPadronizada;
	private Boolean indItemUnico;
	private Short frequencia;
	private Short seqAprazamento;
	private String criadoEm;
	private DominioSituacao indSituacao;
	private String responsavel;
	private String un;
	private String aprazamento;

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getIndDietaPadronizada() {
		return indDietaPadronizada;
	}

	public void setIndDietaPadronizada(Boolean indDietaPadronizada) {
		this.indDietaPadronizada = indDietaPadronizada;
	}

	public Boolean getIndItemUnico() {
		return indItemUnico;
	}

	public void setIndItemUnico(Boolean indItemUnico) {
		this.indItemUnico = indItemUnico;
	}

	public Short getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	public String getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(String criadoEm) {
		this.criadoEm = criadoEm;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public String getUn() {
		return un;
	}

	public void setUn(String un) {
		this.un = un;
	}

	public String getAprazamento() {
		return aprazamento;
	}

	public void setAprazamento(String aprazamento) {
		this.aprazamento = aprazamento;
	}

	public Short getSeqAprazamento() {
		return seqAprazamento;
	}

	public void setSeqAprazamento(Short seqAprazamento) {
		this.seqAprazamento = seqAprazamento;
	}

}
