package br.gov.mec.aghu.compras.pac.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ScoLicitacaoVO implements BaseBean{


	/**
	 * 
	 */
	private static final long serialVersionUID = 157494L;
	private Short vinculoGestor;
	private Integer matriculaGestor;
	private String nomeGestor;
	private Integer numeroPAC;
	private String modalidadeLicitacao;
	private String descricaoPAC;
	private DominioSituacaoLicitacao situacao;
	private Date dtDigitacao;
	private Date dtGeracaoArqRemessa;
	private String nomeArqRemessa;
	private String nomeArqRetorno;
	private Boolean selecionado;
	
	public ScoLicitacaoVO(){
		selecionado = Boolean.FALSE;
	}
	
	public enum Fields{
		VINCULO_GESTOR("vinculoGestor"),
		MATRICULA_GESTOR("matriculaGestor"),
		NOME_GESTOR("nomeGestor"),
		NUMERO_PAC("numeroPAC"),
		MODALIDADE_LICITACAO("modalidadeLicitacao"),
		DESCRICAO_PAC("descricaoPAC"),
		SITUACAO("situacao"),
		DT_DIGITACAO("dtDigitacao"),
		DT_GERACAO_ARQ_REM("dtGeracaoArqRemessa"),
		NOME_ARQ_REMESSA("nomeArqRemessa"),
		NOME_ARQ_RETORNO("nomeArqRetorno"),
		SELECIONADO("selecionado");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Short getVinculoGestor() {
		return vinculoGestor;
	}

	public void setVinculoGestor(Short vinculoGestor) {
		this.vinculoGestor = vinculoGestor;
	}

	public Integer getMatriculaGestor() {
		return matriculaGestor;
	}

	public void setMatriculaGestor(Integer matriculaGestor) {
		this.matriculaGestor = matriculaGestor;
	}

	public String getNomeGestor() {
		return nomeGestor;
	}

	public void setNomeGestor(String nomeGestor) {
		this.nomeGestor = nomeGestor;
	}

	public Integer getNumeroPAC() {
		return numeroPAC;
	}

	public void setNumeroPAC(Integer numeroPAC) {
		this.numeroPAC = numeroPAC;
	}

	public String getDescricaoPAC() {
		return descricaoPAC;
	}

	public void setDescricaoPAC(String descricaoPAC) {
		this.descricaoPAC = descricaoPAC;
	}

	public DominioSituacaoLicitacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoLicitacao situacao) {
		this.situacao = situacao;
	}

	public Date getDtDigitacao() {
		return dtDigitacao;
	}

	public void setDtDigitacao(Date dtDigitacao) {
		this.dtDigitacao = dtDigitacao;
	}

	public Date getDtGeracaoArqRemessa() {
		return dtGeracaoArqRemessa;
	}

	public void setDtGeracaoArqRemessa(Date dtGeracaoArqRemessa) {
		this.dtGeracaoArqRemessa = dtGeracaoArqRemessa;
	}

	public String getNomeArqRemessa() {
		return nomeArqRemessa;
	}

	public void setNomeArqRemessa(String nomeArqRemessa) {
		this.nomeArqRemessa = nomeArqRemessa;
	}

	public String getNomeArqRetorno() {
		return nomeArqRetorno;
	}

	public void setNomeArqRetorno(String nomeArqRetorno) {
		this.nomeArqRetorno = nomeArqRetorno;
	}

	public String getModalidadeLicitacao() {
		return modalidadeLicitacao;
	}

	public void setModalidadeLicitacao(String modalidadeLicitacao) {
		this.modalidadeLicitacao = modalidadeLicitacao;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result	+ ((numeroPAC == null) ? 0 : numeroPAC.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ScoLicitacaoVO other = (ScoLicitacaoVO) obj;
		if (numeroPAC == null) {
			if (other.numeroPAC != null){
				return false;
			}
		} else if (!numeroPAC.equals(other.numeroPAC)){
			return false;
		}
		return true;
	}

}
