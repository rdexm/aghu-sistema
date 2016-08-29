package br.gov.mec.aghu.controleinfeccao.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;


public class OrigemInfeccoesVO implements BaseBean {
	
	private static final long serialVersionUID = -962601348264968243L;

	private String codigoOrigem;
	private String descricao;
	private String textoNotificacao;
	private DominioSituacao situacao;
	private Short unfSeq;
	private String descricaoUnidadeFuncional; // Andar - Ala - Descrição
	
	public OrigemInfeccoesVO() {
		super();
	}
	
	public enum Fields {

		CODIGO_ORIGEM("codigoOrigem"),
		DESCRICAO("descricao"),
		TEXTO_NOTIFICACAO("textoNotificacao"),
		SITUACAO("situacao"),
		UNF_SEQ("unfSeq");

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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTextoNotificacao() {
		return textoNotificacao;
	}

	public void setTextoNotificacao(String textoNotificacao) {
		this.textoNotificacao = textoNotificacao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public String getDescricaoUnidadeFuncional() {
		return descricaoUnidadeFuncional;
	}

	public void setDescricaoUnidadeFuncional(String descricaoUnidadeFuncional) {
		this.descricaoUnidadeFuncional = descricaoUnidadeFuncional;
	}
}