package br.gov.mec.aghu.controleinfeccao.vo;

import br.gov.mec.aghu.dominio.DominioFormaContabilizacao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;


public class LocaisOrigemInfeccaoVO implements BaseBean {
	
	private static final long serialVersionUID = -968274629038968243L;

	private String codigoOrigem;
	private Short unfSeq;
	private DominioFormaContabilizacao indFormaContabilizacao;
	private DominioSituacao indSituacao;
	private Boolean situacao;
	private String descricaoUnidadeFuncional; // Andar - Ala - Descrição
	private String descricaoLocal; // Descricao
	
	public LocaisOrigemInfeccaoVO() {
		super();
	}
	
	public enum Fields {

		CODIGO_ORIGEM("codigoOrigem"),
		UNF_SEQ("unfSeq"),
		FORMA_CONTABILIZACAO("indFormaContabilizacao"),
		SITUACAO("indSituacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	//Getters and Setters
	
	public String getCodigoOrigem() {
		return codigoOrigem;
	}

	public void setCodigoOrigem(String codigoOrigem) {
		this.codigoOrigem = codigoOrigem;
	}

	public String getDescricaoLocal() {
		return descricaoLocal;
	}

	public void setDescricaoLocal(String descricaoLocal) {
		this.descricaoLocal = descricaoLocal;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public DominioFormaContabilizacao getIndFormaContabilizacao() {
		return indFormaContabilizacao;
	}

	public void setIndFormaContabilizacao(
			DominioFormaContabilizacao indFormaContabilizacao) {
		this.indFormaContabilizacao = indFormaContabilizacao;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public String getDescricaoUnidadeFuncional() {
		return descricaoUnidadeFuncional;
	}

	public void setDescricaoUnidadeFuncional(String descricaoUnidadeFuncional) {
		this.descricaoUnidadeFuncional = descricaoUnidadeFuncional;
	}
}