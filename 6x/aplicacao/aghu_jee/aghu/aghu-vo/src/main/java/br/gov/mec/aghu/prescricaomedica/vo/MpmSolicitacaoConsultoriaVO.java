package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class MpmSolicitacaoConsultoriaVO implements BaseBean {

	private static final long serialVersionUID = -6722254122023244213L;

	private Integer atdSeq;
	private Integer seq;
	private String motivo;
	private Date dtHrSolicitada;
	private String nomePessoaServidorCriacao;
	private String nomeReduzidoEspecialidade;
	private DominioSituacao indSituacao;

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public Integer getSeq() {
		return seq;
	}

	public String getMotivo() {
		return motivo;
	}

	public Date getDtHrSolicitada() {
		return dtHrSolicitada;
	}

	public String getNomePessoaServidorCriacao() {
		return nomePessoaServidorCriacao;
	}

	public String getNomeReduzidoEspecialidade() {
		return nomeReduzidoEspecialidade;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public void setDtHrSolicitada(Date dtHrSolicitada) {
		this.dtHrSolicitada = dtHrSolicitada;
	}

	public void setNomePessoaServidorCriacao(String nomePessoaServidorCriacao) {
		this.nomePessoaServidorCriacao = nomePessoaServidorCriacao;
	}

	public void setNomeReduzidoEspecialidade(String nomeReduzidoEspecialidade) {
		this.nomeReduzidoEspecialidade = nomeReduzidoEspecialidade;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

}
