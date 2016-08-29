package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioAfpPublicado;
import br.gov.mec.aghu.core.commons.BaseBean;

public class AFPFornecedoresVO implements BaseBean{

	private static final long serialVersionUID = 1094758748075863219L;

	private Integer numeroAF;
	private Integer numeroAFP;
	private Short complemento;
	private String origem;
	private DominioAfpPublicado publicacao;
	private Date dtPublicacao;
	private Date dtAcesso;
	private String acesso;
	private String fornecedor;

	public enum Fields {

		NUMERO_AF("numeroAF"),
		NUMERO_AFP("numeroAFP"),
		COMPLEMENTO("complemento"),
		ORIGEM("origem"),
		PUBLICACAO("publicacao"),
		DATA_PUBLICACAO("dtPublicacao"),
		DATA_ACESSO("dtAcesso"),
		ACESSO("acesso"),
		FORNECEDOR("fornecedor");


		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getNumeroAF() {
		return numeroAF;
	}

	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}

	public void setNumeroAFP(Integer numeroAFP) {
		this.numeroAFP = numeroAFP;
	}

	public Integer getNumeroAFP() {
		return numeroAFP;
	}

	public Short getComplemento() {
		return complemento;
	}

	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public DominioAfpPublicado getPublicacao() {
		return publicacao;
	}

	public void setPublicacao(DominioAfpPublicado publicacao) {
		this.publicacao = publicacao;
	}

	public Date getDtPublicacao() {
		return dtPublicacao;
	}

	public void setDtPublicacao(Date dtPublicacao) {
		this.dtPublicacao = dtPublicacao;
	}

	public Date getDtAcesso() {
		return dtAcesso;
	}

	public void setDtAcesso(Date dtAcesso) {
		this.dtAcesso = dtAcesso;
	}

	public String getAcesso() {
		return acesso;
	}

	public void setAcesso(String acesso) {
		this.acesso = acesso;
	}

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

}

