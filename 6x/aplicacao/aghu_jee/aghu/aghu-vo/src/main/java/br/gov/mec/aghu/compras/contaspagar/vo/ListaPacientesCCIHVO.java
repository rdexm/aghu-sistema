package br.gov.mec.aghu.compras.contaspagar.vo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.commons.BaseBean;

public class ListaPacientesCCIHVO implements BaseBean {

	private static final long serialVersionUID = -2615913598749152064L;

	/*
	 * Campos principais da consulta C1
	 */
	private Integer prontuario; // PAC.Prontuario
	private String nome; // PAC.Nome
	private Integer ultimaInternacaoSeq; // UltimaInternacao.Seq
	private Short ultimaInternacaoUnfSeq; // UltimaInternacao.UNF_Seq
	private String ultimaInternacaoUnfDescricao;
	private String ultimaInternacaoLeitoID; // UltimaInternacao.LTO_LTO_ID
	private Boolean indPacienteInternado; // IND_PACIENTE_INTERNADO
	private Boolean indNotifNaoConferidas; // IND_NOTIF_NAO_CONFERIDAS
	private Boolean indPacienteGmr; // IND_PACIENTE_GMR
	
	/*
	 * Campos adicionais
	 */
	private String nomeEquipe;
	private Integer codigo;

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getUltimaInternacaoSeq() {
		return ultimaInternacaoSeq;
	}

	public void setUltimaInternacaoSeq(Integer ultimaInternacaoSeq) {
		this.ultimaInternacaoSeq = ultimaInternacaoSeq;
	}

	public Short getUltimaInternacaoUnfSeq() {
		return ultimaInternacaoUnfSeq;
	}

	public void setUltimaInternacaoUnfSeq(Short ultimaInternacaoUnfSeq) {
		this.ultimaInternacaoUnfSeq = ultimaInternacaoUnfSeq;
	}

	public String getUltimaInternacaoUnfDescricao() {
		return ultimaInternacaoUnfDescricao;
	}

	public void setUltimaInternacaoUnfDescricao(String ultimaInternacaoUnfDescricao) {
		this.ultimaInternacaoUnfDescricao = ultimaInternacaoUnfDescricao;
	}

	public String getUltimaInternacaoLeitoID() {
		return ultimaInternacaoLeitoID;
	}

	public void setUltimaInternacaoLeitoID(String ultimaInternacaoLeitoID) {
		this.ultimaInternacaoLeitoID = ultimaInternacaoLeitoID;
	}

	public String getNomeEquipe() {
		return nomeEquipe;
	}

	public void setNomeEquipe(String nomeEquipe) {
		this.nomeEquipe = nomeEquipe;
	}

	public Boolean getIndPacienteInternado() {
		return indPacienteInternado;
	}

	public void setIndPacienteInternado(Boolean indPacienteInternado) {
		this.indPacienteInternado = indPacienteInternado;
	}

	public Boolean getIndNotifNaoConferidas() {
		return indNotifNaoConferidas;
	}

	public void setIndNotifNaoConferidas(Boolean indNotifNaoConferidas) {
		this.indNotifNaoConferidas = indNotifNaoConferidas;
	}

	public Boolean getIndPacienteGmr() {
		return indPacienteGmr;
	}

	public void setIndPacienteGmr(Boolean indPacienteGmr) {
		this.indPacienteGmr = indPacienteGmr;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getCodigo());
		return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ListaPacientesCCIHVO other = (ListaPacientesCCIHVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getCodigo(), other.getCodigo());
		return umEqualsBuilder.isEquals();
	}

	public enum Fields {
		PAC_CODIGO("codigo"), 
		PRONTUARIO("prontuario"), 
		NOME("nome"), 
		ULTIMA_INTERNACAO_SEQ("ultimaInternacaoSeq"), 
		ULTIMA_INTERNACAO_UNF_SEQ("ultimaInternacaoUnfSeq"), 
		ULTIMA_INTERNACAO_UNF_DESCRICAO("ultimaInternacaoUnfDescricao"), 
		ULTIMA_INTERNACAO_LEITO_ID("ultimaInternacaoLeitoID"), 
		IND_PACIENTE_INTERNADO("indPacienteInternado"), 
		IND_NOTIF_NAO_CONFERIDAS("indNotifNaoConferidas"), 
		IND_PACIENTE_GMR("indPacienteGmr"),
		NOME_EQUIPE("nomeEquipe");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}
