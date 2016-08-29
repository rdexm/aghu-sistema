package br.gov.mec.aghu.controleinfeccao.vo;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class TopografiaProcedimentoVO implements BaseBean {

	private static final long serialVersionUID = 6830173642437766805L;

	private Short seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private Boolean indPermSobreposicao;
	private Date criadoEm;
	private Integer servidorMatricula;
	private Short servidorVinCodigo;
	private Date alteradoEm;
	private Integer serMatriculaMovimentado;
	private Short serVinCodigoMovimentado;
	private Short seqTopografiaInfeccao;
	private String descricaoTopografiaInfeccao;
	private Boolean situacaoBoolean;
	private Integer procedimentoCirurgico;

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

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Boolean getIndPermSobreposicao() {
		return indPermSobreposicao;
	}

	public void setIndPermSobreposicao(Boolean indPermSobreposicao) {
		this.indPermSobreposicao = indPermSobreposicao;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Integer getServidorMatricula() {
		return servidorMatricula;
	}

	public void setServidorMatricula(Integer servidorMatricula) {
		this.servidorMatricula = servidorMatricula;
	}

	public Short getServidorVinCodigo() {
		return servidorVinCodigo;
	}

	public void setServidorVinCodigo(Short servidorVinCodigo) {
		this.servidorVinCodigo = servidorVinCodigo;
	}

	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	public Integer getSerMatriculaMovimentado() {
		return serMatriculaMovimentado;
	}

	public void setSerMatriculaMovimentado(Integer serMatriculaMovimentado) {
		this.serMatriculaMovimentado = serMatriculaMovimentado;
	}

	public Short getSerVinCodigoMovimentado() {
		return serVinCodigoMovimentado;
	}

	public void setSerVinCodigoMovimentado(Short serVinCodigoMovimentado) {
		this.serVinCodigoMovimentado = serVinCodigoMovimentado;
	}

	public Short getSeqTopografiaInfeccao() {
		return seqTopografiaInfeccao;
	}

	public void setSeqTopografiaInfeccao(Short seqTopografiaInfeccao) {
		this.seqTopografiaInfeccao = seqTopografiaInfeccao;
	}

	public String getDescricaoTopografiaInfeccao() {
		return descricaoTopografiaInfeccao;
	}

	public void setDescricaoTopografiaInfeccao(
			String descricaoTopografiaInfeccao) {
		this.descricaoTopografiaInfeccao = descricaoTopografiaInfeccao;
	}

	public Boolean getSituacaoBoolean() {
		return situacaoBoolean;
	}

	public void setSituacaoBoolean(Boolean situacaoBoolean) {
		this.situacaoBoolean = situacaoBoolean;
	}
	
	public Integer getProcedimentoCirurgico() {
		return procedimentoCirurgico;
	}

	public void setProcedimentoCirurgico(Integer procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}

	public String getDescricaoCompleta() {
		if (StringUtils.isNotBlank(getDescricao()) &&
			StringUtils.isNotBlank(getDescricaoTopografiaInfeccao())){
			return getDescricao().concat(" - ").concat(getDescricaoTopografiaInfeccao());
		} else if (StringUtils.isNotBlank(getDescricao())) {
			return getDescricao();
		} else if (StringUtils.isNotBlank(getDescricaoTopografiaInfeccao())) {
			return getDescricaoTopografiaInfeccao();
		}
		return "";
	}
	
	public String getSeqCompleto() {
		if(getSeq() != null && getSeqTopografiaInfeccao() != null) {
			return getSeq() + " - " + getSeqTopografiaInfeccao(); 
		} else if (getSeq() != null) {
			return String.valueOf(getSeq());
		} else if (getSeqTopografiaInfeccao() != null){
			return String.valueOf(getSeqTopografiaInfeccao());
		}
		return " ";
	}

	public enum Fields {
		SEQ("seq"),
		DESCRICAO("descricao"),	
		IND_SITUACAO("indSituacao"),
		IND_PERM_SOBREPOSICAO("indPermSobreposicao"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_MATRICULA("servidorMatricula"),
		SERVIDOR_VIN_CODIGO("servidorVinCodigo"),
		ALTERADO_EM("alteradoEm"),
		SER_MATRICULA_MOVI("serMatriculaMovimentado"), 
		SER_VINCULO_CODIGO_MOVI("serVinCodigoMovimentado"),
		SEQ_TOPOGRAFIA_INFECCAO("seqTopografiaInfeccao"),
		DESCRICAO_TOPOGRA_FIAINFECCAO("descricaoTopografiaInfeccao"),
		PROCEDIMENTO_CIRURGICO("procedimentoCirurgico")
		;


		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
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
		TopografiaProcedimentoVO other = (TopografiaProcedimentoVO) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "TopografiaProcedimentoVO [seq=" + seq + ", descricao="
				+ descricao + ", indSituacao=" + indSituacao
				+ ", indPermSobreposicao=" + indPermSobreposicao
				+ ", criadoEm=" + criadoEm + ", servidorMatricula="
				+ servidorMatricula + ", servidorVinCodigo="
				+ servidorVinCodigo + ", alteradoEm=" + alteradoEm
				+ ", serMatriculaMovimentado=" + serMatriculaMovimentado
				+ ", serVinCodigoMovimentado=" + serVinCodigoMovimentado
				+ ", seqTopografiaInfeccao=" + seqTopografiaInfeccao
				+ ", descricaoTopografiaInfeccao="
				+ descricaoTopografiaInfeccao + ", situacaoBoolean="
				+ situacaoBoolean + "]";
	}

}
