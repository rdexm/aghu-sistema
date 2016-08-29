package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;


public class AfaComposicaoNptPadraoVO {

	private Short formulaSeq;
	private Short seqVelAdministracao;
	private String descricao;
	private Short velocidadeAdministracao;
	private String descricaovelAdministracao;
	private Short seqComposicao;
	private Date criadoEm;
	private Date alteradoEm;
	private String criadoPor;
	private Integer matriculaUsuario;
	private Short vinCodigoUsuario;
	private String descricaoToolTip;
	
	private Short idComposicaoFnpSeq;
	private Short idComposicaoSeqP; 
	
	
	public enum Fields {
		SEQ_FORMULA_PADRAO("formulaSeq"),
		DESCRICAO_COMPOSICOES("descricao"),
		VELOCIDADE_ADMINISTRACAO("velocidadeAdministracao"),
		SEQ_VELOCIDADE_ADMINISTRACAO("seqVelAdministracao"),
		DESCRICAO_VELOCIDADE_ADMINISTRACAO("descricaovelAdministracao"),
		SEQ_COMPOSICOES("seqComposicao"),
		COMPOSICAO_CRIADO_EM("criadoEm"),
		COMPOSICAO_CRIADO_POR("criadoPor"),
		COMPOSICAO_ID_FNP_SEQ("idComposicaoFnpSeq"),
		COMPOSICAO_ID_SEQ_P("idComposicaoSeqP")
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Short getSeqVelAdministracao() {
		return seqVelAdministracao;
	}

	public void setSeqVelAdministracao(Short seqVelAdministracao) {
		this.seqVelAdministracao = seqVelAdministracao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Short getVelocidadeAdministracao() {
		return velocidadeAdministracao;
	}

	public void setVelocidadeAdministracao(Short velocidadeAdministracao) {
		this.velocidadeAdministracao = velocidadeAdministracao;
	}

	public String getDescricaovelAdministracao() {
		return descricaovelAdministracao;
	}

	public void setDescricaovelAdministracao(String descricaovelAdministracao) {
		this.descricaovelAdministracao = descricaovelAdministracao;
	}

	public Short getFormulaSeq() {
		return formulaSeq;
	}

	public void setFormulaSeq(Short formulaSeq) {
		this.formulaSeq = formulaSeq;
	}

	public Short getSeqComposicao() {
		return seqComposicao;
	}

	public void setSeqComposicao(Short seqComposicao) {
		this.seqComposicao = seqComposicao;
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

	public void setCriadoPor(String craidoPor) {
		this.criadoPor = craidoPor;
	}

	public String getDescricaoToolTip() {
		return descricaoToolTip;
	}

	public void setDescricaoToolTip(String descricaoToolTip) {
		this.descricaoToolTip = descricaoToolTip;
	}

	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	public Integer getMatriculaUsuario() {
		return matriculaUsuario;
	}

	public void setMatriculaUsuario(Integer matriculaUsuario) {
		this.matriculaUsuario = matriculaUsuario;
	}

	public Short getVinCodigoUsuario() {
		return vinCodigoUsuario;
	}

	public void setVinCodigoUsuario(Short vinCodigoUsuario) {
		this.vinCodigoUsuario = vinCodigoUsuario;
	}

	public Short getIdComposicaoFnpSeq() {
		return idComposicaoFnpSeq;
	}

	public void setIdComposicaoFnpSeq(Short idComposicaoFnpSeq) {
		this.idComposicaoFnpSeq = idComposicaoFnpSeq;
	}

	public Short getIdComposicaoSeqP() {
		return idComposicaoSeqP;
	}

	public void setIdComposicaoSeqP(Short idComposicaoSeqP) {
		this.idComposicaoSeqP = idComposicaoSeqP;
	}

}
