package br.gov.mec.aghu.controleinfeccao.vo;


import java.util.Date;
import br.gov.mec.aghu.core.commons.BaseBean;

public class TipoGrupoRiscoVO implements BaseBean {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3726897791403745412L;
	

	private Integer matricula;
	private Short vinCodigo;
	private Short porSeq;
	private Short tgpSeq;
	private String indSituacao;
	private Date criadoEm;
	private Short seq;
	private String descricao;
	
	
	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public Short getPorSeq() {
		return porSeq;
	}

	public void setPorSeq(Short porSeq) {
		this.porSeq = porSeq;
	}

	public Short getTgpSeq() {
		return tgpSeq;
	}

	public void setTgpSeq(Short tgpSeq) {
		this.tgpSeq = tgpSeq;
	}

	public String getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}




	public enum Fields {
		MATRICULA("matricula"), 
		CODIGO_VINCULO("vinCodigo"),
		POR_SEQ("porSeq"),
		TGP_SEQ("tgpSeq"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		SEQ("seq"),
		DESCRICAO("descricao");


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
