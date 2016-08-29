package br.gov.mec.aghu.ambulatorio.vo;

import java.util.Date;


public class RelatorioSolicitacaoProcedimentoVO {
	private String justificativa;
	private Integer serMatriculaValida;
	private Short serVinCodigoValida;
	private String descricao;
	private Short seqTipo;
	private Integer prontuario;
	private String nome;
	private Date dtNascimento;
	private Integer grdSeq;
	private Short cspCnvCodigo;
	private Date dtConsulta;
	private String sigla;
	private String descricaoConvenio;
	private String descricaoTipo;
	private String nomeMedico;
	private String especialidade;
	private Integer conNumero;
	private String convenio;
	private String assinatura;
	private String prontuarioFormatado;
	
	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}


	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}


	public String getAssinatura() {
		return assinatura;
	}


	public void setAssinatura(String assinatura) {
		this.assinatura = assinatura;
	}


	public String getConvenio() {
		return convenio;
	}


	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}


	public String getNomeMedico() {
		return nomeMedico;
	}


	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}


	public String getEspecialidade() {
		return especialidade;
	}


	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}


	public Integer getConNumero() {
		return conNumero;
	}


	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
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


	public Date getDtNascimento() {
		return dtNascimento;
	}


	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}


	public Integer getGrdSeq() {
		return grdSeq;
	}


	public void setGrdSeq(Integer grdSeq) {
		this.grdSeq = grdSeq;
	}


	public Short getCspCnvCodigo() {
		return cspCnvCodigo;
	}


	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}


	public Date getDtConsulta() {
		return dtConsulta;
	}


	public void setDtConsulta(Date dtConsulta) {
		this.dtConsulta = dtConsulta;
	}


	public String getSigla() {
		return sigla;
	}


	public void setSigla(String sigla) {
		this.sigla = sigla;
	}


	public String getDescricaoConvenio() {
		return descricaoConvenio;
	}


	public void setDescricaoConvenio(String descricaoConvenio) {
		this.descricaoConvenio = descricaoConvenio;
	}


	public String getDescricaoTipo() {
		return descricaoTipo;
	}


	public void setDescricaoTipo(String descricaoTipo) {
		this.descricaoTipo = descricaoTipo;
	}


	public Short getSeqTipo() {
		return seqTipo;
	}


	public void setSeqTipo(Short seqTipo) {
		this.seqTipo = seqTipo;
	}


	private String seq;
	
	
	public String getJustificativa() {
		return justificativa;
	}


	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}


	public Integer getSerMatriculaValida() {
		return serMatriculaValida;
	}


	public void setSerMatriculaValida(Integer serMatriculaValida) {
		this.serMatriculaValida = serMatriculaValida;
	}


	public Short getSerVinCodigoValida() {
		return serVinCodigoValida;
	}


	public void setSerVinCodigoValida(Short serVinCodigoValida) {
		this.serVinCodigoValida = serVinCodigoValida;
	}


	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getSeq() {
		return seq;
	}


	public void setSeq(String seq) {
		this.seq = seq;
	}


	public enum Fields {
		JUSTIFICATIVA("justificativa"), 
		SER_MATRICULA_VALIDA("serMatriculaValida"), 
		SER_VIN_CODIGO_VALIDA("serVinCodigoValida"), 
		DESCRICAO("descricao"), 
		DESCRICAO_TIPO("descricaoTipo"), 
		SEQ_TIPO("seqTipo"),
		PRONTUARIO("prontuario"),
		NOME("nome"),
		DT_NASCIMENTO("dtNascimento"),
		GRD_SEQ("grdSeq"),
		CSP_CNV_CODIGO("cspCnvCodigo"),
		DATA_CONSULTA("dtConsulta"),
		SIGLA("sigla"),
		DESCRICAOCNV("descricaoConvenio"),
		CON_NUMERO("conNumero"),
		ESPECIALIDADE("especialidade"),
		NOME_MEDICO("nomeMedico")
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
}