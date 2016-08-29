package br.gov.mec.aghu.registrocolaborador.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class PessoaTipoInformacoesVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5163436907569314860L;
	
	
	private String pesNome;
	private Integer pesCodigo;
	private Short tiiSeq;
	private Long seq;
	
	private String valor;
	private Date criadoEm;
	private Date alteradoEm;
	private Date dtInicio;
	private Date dtFim;
	
	private String tipoInformacaoDescricao;
	private String cboDescricao;
	
	private Integer matriculaCriadoPor;
	private Short vinCodigoCriadoPor;
	private String nomeCriadoPor;

	private Integer matriculaAlteradoPor;
	private Short vinCodigoAlteradoPor;
	private String nomeAlteradoPor;

	public enum Fields {
		VALOR("valor"), 
		CRIADO_EM("criadoEm"), 
		ALTERADO_EM("alteradoEm"), 
		PES_CODIGO("pesCodigo"), 
		PES_NOME("pesNome"), 
		TII_SEQ("tiiSeq"),
		SEQ("seq"),
		DT_FIM("dtFim"),
		DT_INICIO("dtInicio"),
		TIPO_INFORMACAO_DESCRICAO("tipoInformacaoDescricao"),
		CBO_DESCRICAO("cboDescricao"),
		MATRICULA_CRIADO_POR("matriculaCriadoPor"),
		VIN_CODIGO_CRIADO_POR("vinCodigoCriadoPor"),
		NOME_CRIADO_POR("nomeCriadoPor"),
		MATRICULA_ALTERADO_POR("matriculaAlteradoPor"),
		VIN_CODIGO_ALTERADO_POR("vinCodigoAlteradoPor"),
		NOME_ALTERADO_POR("nomeAlteradoPor");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}


	public Integer getPesCodigo() {
		return pesCodigo;
	}


	public void setPesCodigo(Integer pesCodigo) {
		this.pesCodigo = pesCodigo;
	}


	public Short getTiiSeq() {
		return tiiSeq;
	}


	public void setTiiSeq(Short tiiSeq) {
		this.tiiSeq = tiiSeq;
	}


	public Long getSeq() {
		return seq;
	}


	public void setSeq(Long seq) {
		this.seq = seq;
	}


	public String getValor() {
		return valor;
	}


	public void setValor(String valor) {
		this.valor = valor;
	}


	public Date getCriadoEm() {
		return criadoEm;
	}


	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}


	public Date getAlteradoEm() {
		return alteradoEm;
	}


	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}


	public Date getDtInicio() {
		return dtInicio;
	}


	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}


	public Date getDtFim() {
		return dtFim;
	}


	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}


	public String getTipoInformacaoDescricao() {
		return tipoInformacaoDescricao;
	}


	public void setTipoInformacaoDescricao(String tipoInformacaoDescricao) {
		this.tipoInformacaoDescricao = tipoInformacaoDescricao;
	}


	public String getCboDescricao() {
		return cboDescricao;
	}


	public void setCboDescricao(String cboDescricao) {
		this.cboDescricao = cboDescricao;
	}


	public Integer getMatriculaCriadoPor() {
		return matriculaCriadoPor;
	}


	public void setMatriculaCriadoPor(Integer matriculaCriadoPor) {
		this.matriculaCriadoPor = matriculaCriadoPor;
	}


	public Short getVinCodigoCriadoPor() {
		return vinCodigoCriadoPor;
	}


	public void setVinCodigoCriadoPor(Short vinCodigoCriadoPor) {
		this.vinCodigoCriadoPor = vinCodigoCriadoPor;
	}


	public String getNomeCriadoPor() {
		return nomeCriadoPor;
	}


	public void setNomeCriadoPor(String nomeCriadoPor) {
		this.nomeCriadoPor = nomeCriadoPor;
	}


	public Integer getMatriculaAlteradoPor() {
		return matriculaAlteradoPor;
	}


	public void setMatriculaAlteradoPor(Integer matriculaAlteradoPor) {
		this.matriculaAlteradoPor = matriculaAlteradoPor;
	}


	public Short getVinCodigoAlteradoPor() {
		return vinCodigoAlteradoPor;
	}


	public void setVinCodigoAlteradoPor(Short vinCodigoAlteradoPor) {
		this.vinCodigoAlteradoPor = vinCodigoAlteradoPor;
	}


	public String getNomeAlteradoPor() {
		return nomeAlteradoPor;
	}


	public void setNomeAlteradoPor(String nomeAlteradoPor) {
		this.nomeAlteradoPor = nomeAlteradoPor;
	}


	public String getPesNome() {
		return pesNome;
	}


	public void setPesNome(String pesNome) {
		this.pesNome = pesNome;
	}


}
