package br.gov.mec.aghu.perinatologia.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioApresentacao;
import br.gov.mec.aghu.dominio.DominioModoNascimento;
import br.gov.mec.aghu.dominio.DominioRNClassificacaoNascimento;
import br.gov.mec.aghu.dominio.DominioTipoNascimento;

public class DadosNascimentoVO implements Serializable {
	
	private static final long serialVersionUID = -4101227518458831214L;

	private Date dtHrNascimento;
	private DominioTipoNascimento tipoNascimento;
	private DominioModoNascimento modoNascimento;
	private String descricaoAnestesia;
	private DominioApresentacao apresentacao;
	private DominioRNClassificacaoNascimento classificacao;
	private Short pesoPlacenta;
	private Short cordao;
	private Short pesoAborto;
	private Short tanSeq;
	private Integer gsoPacCodigo;
	private Short gsoSeqp;
	private Integer seqp;
	private String periodoExpulsivo;
	private String periodoDilatacao;
	private Boolean indEpisotomia;
	
	public Date getDtHrNascimento() {
		return dtHrNascimento;
	}
	public void setDtHrNascimento(Date dtHrNascimento) {
		this.dtHrNascimento = dtHrNascimento;
	}
	public DominioTipoNascimento getTipoNascimento() {
		return tipoNascimento;
	}
	public void setTipoNascimento(DominioTipoNascimento tipoNascimento) {
		this.tipoNascimento = tipoNascimento;
	}
	public DominioModoNascimento getModoNascimento() {
		return modoNascimento;
	}
	public void setModoNascimento(DominioModoNascimento modoNascimento) {
		this.modoNascimento = modoNascimento;
	}
	public String getDescricaoAnestesia() {
		return descricaoAnestesia;
	}
	public void setDescricaoAnestesia(String descricaoAnestesia) {
		this.descricaoAnestesia = descricaoAnestesia;
	}
	public DominioApresentacao getApresentacao() {
		return apresentacao;
	}
	public void setApresentacao(DominioApresentacao apresentacao) {
		this.apresentacao = apresentacao;
	}
	public DominioRNClassificacaoNascimento getClassificacao() {
		return classificacao;
	}
	public void setClassificacao(DominioRNClassificacaoNascimento classificacao) {
		this.classificacao = classificacao;
	}
	public Short getPesoPlacenta() {
		return pesoPlacenta;
	}
	public void setPesoPlacenta(Short pesoPlacenta) {
		this.pesoPlacenta = pesoPlacenta;
	}
	public Short getCordao() {
		return cordao;
	}
	public void setCordao(Short cordao) {
		this.cordao = cordao;
	}
	public Short getPesoAborto() {
		return pesoAborto;
	}
	public void setPesoAborto(Short pesoAborto) {
		this.pesoAborto = pesoAborto;
	}
	public Short getTanSeq() {
		return tanSeq;
	}
	public void setTanSeq(Short tanSeq) {
		this.tanSeq = tanSeq;
	}
	public Integer getSeqp() {
		return seqp;
	}
	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}
	public Integer getGsoPacCodigo() {
		return gsoPacCodigo;
	}
	public void setGsoPacCodigo(Integer gsoPacCodigo) {
		this.gsoPacCodigo = gsoPacCodigo;
	}
	public Short getGsoSeqp() {
		return gsoSeqp;
	}
	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}
	public String getPeriodoExpulsivo() {
		return periodoExpulsivo;
	}
	public void setPeriodoExpulsivo(String periodoExpulsivo) {
		this.periodoExpulsivo = periodoExpulsivo;
	}
	public String getPeriodoDilatacao() {
		return periodoDilatacao;
	}
	public void setPeriodoDilatacao(String periodoDilatacao) {
		this.periodoDilatacao = periodoDilatacao;
	}
	public Boolean getIndEpisotomia() {
		return indEpisotomia;
	}
	public void setIndEpisotomia(Boolean indEpisotomia) {
		this.indEpisotomia = indEpisotomia;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gsoPacCodigo == null) ? 0 : gsoPacCodigo.hashCode());
		result = prime * result + ((gsoSeqp == null) ? 0 : gsoSeqp.hashCode());
		result = prime * result + ((seqp == null) ? 0 : seqp.hashCode());
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
		DadosNascimentoVO other = (DadosNascimentoVO) obj;
		if (gsoPacCodigo == null) {
			if (other.gsoPacCodigo != null) {
				return false;
			}
		} else if (!gsoPacCodigo.equals(other.gsoPacCodigo)) {
			return false;
		}
		if (gsoSeqp == null) {
			if (other.gsoSeqp != null) {
				return false;
			}
		} else if (!gsoSeqp.equals(other.gsoSeqp)) {
			return false;
		}
		if (seqp == null) {
			if (other.seqp != null) {
				return false;
			}
		} else if (!seqp.equals(other.seqp)) {
			return false;
		}
		return true;
	}
}
