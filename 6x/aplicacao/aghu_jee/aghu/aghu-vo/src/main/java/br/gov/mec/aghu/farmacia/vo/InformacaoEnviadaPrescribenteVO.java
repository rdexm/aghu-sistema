package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.utils.DateUtil;

public class InformacaoEnviadaPrescribenteVO implements Serializable{		
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1386623763164876372L;		
	private Integer prontuario;	
	private Integer pacCodigo;
	private String  pacNome;
	private Date    criadoEm;
	private AghUnidadesFuncionais unidadeFuncional;
	private Short 	seqUnidadesFuncionais;
	private String  descUnidadesFuncionais;
	private String  seqDescUnidadeFuncional;
	private String  responsavel;
	private Boolean indInfVerificada;	
	private Integer servidorMatricula;
	private Short   servidorVinCodigo;	
	private String criadoEmFormatado;
	private Integer seq;
	
	public String getCriadoEmFormatado() {
		criadoEmFormatado = DateUtil.obterDataFormatada(criadoEm, "ddMMyyy");
		return criadoEmFormatado;
	}
	public void setCriadoEmFormatado(String criadoEmFormatado) {
		this.criadoEmFormatado = criadoEmFormatado;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public String getPacNome() {
		return pacNome;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public Short getSeqUnidadesFuncionais() {
		return seqUnidadesFuncionais;
	}
	public String getDescUnidadesFuncionais() {
		return descUnidadesFuncionais;
	}
	public Boolean getIndInfVerificada() {
		return indInfVerificada;
	}
	public Integer getServidorMatricula() {
		return servidorMatricula;
	}
	public Short getServidorVinCodigo() {
		return servidorVinCodigo;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	public void setSeqUnidadesFuncionais(Short seqUnidadesFuncionais) {		
		this.seqUnidadesFuncionais = seqUnidadesFuncionais;
	}
	public void setDescUnidadesFuncionais(String descUnidadesFuncionais) {
		this.descUnidadesFuncionais = descUnidadesFuncionais;
	}
	public void setIndInfVerificada(Boolean indInfVerificada) {
		this.indInfVerificada = indInfVerificada;
	}
	public void setServidorMatricula(Integer servidorMatricula) {
		this.servidorMatricula = servidorMatricula;
	}
	public void setServidorVinCodigo(Short servidorVinCodigo) {
		this.servidorVinCodigo = servidorVinCodigo;
	}
	

	public String getSeqDescUnidadeFuncional() {
		return seqDescUnidadeFuncional;
	}
	public void setSeqDescUnidadeFuncional(String seqDescUnidadeFuncional) {
		this.seqDescUnidadeFuncional = seqDescUnidadeFuncional;
	}


	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}
	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}


	public String getResponsavel() {
		return responsavel;
	}
	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}


	public enum Fields {
		SEQ("seq"),
		PRONTUARIO("prontuario"),
		PACIENTE_CODIGO("pacCodigo"),
		PACIENTE_NOME("pacNome"),
		CRIADO_EM("criadoEm"),
		SEQ_UNIDADES_FUNCIONAIS("seqUnidadesFuncionais"),
		DESCRICAO_UNIDADES_FUNCIONAIS("descUnidadesFuncionais"),
		SEQ_DESCRICAO_UNIDADE_FUNCIONAL("seqDescUnidadeFuncional"),
		INFORMACAO_VERIFICADA("indInfVerificada"),
		SERVIDOR_MATRICULA("servidorMatricula"),
		SERVIDOR_VIN_CODIGO("servidorMatricula"),
		RESPONSAVEL("responsavel");		

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
}
