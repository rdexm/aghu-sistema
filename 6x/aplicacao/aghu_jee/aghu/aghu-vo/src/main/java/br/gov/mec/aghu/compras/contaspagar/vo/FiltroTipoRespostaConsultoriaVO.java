package br.gov.mec.aghu.compras.contaspagar.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;

public class FiltroTipoRespostaConsultoriaVO implements Serializable {

	private static final long serialVersionUID = -7835623111653606311L;

	private Short codigo;
	private String descricao;
	private Short ordemVisual;
	private Boolean primeiraVez;
	private Boolean respostaObrigatoria;
	private Boolean acompanhamento;
	private Boolean respObrigAcompanhamento;
	private DominioSituacao situacao;
	
	
	public Short getCodigo() {
		return codigo;
	}
	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Short getOrdemVisual() {
		return ordemVisual;
	}
	public void setOrdemVisual(Short ordemVisual) {
		this.ordemVisual = ordemVisual;
	}
	public Boolean getPrimeiraVez() {
		return primeiraVez;
	}
	public void setPrimeiraVez(Boolean primeiraVez) {
		this.primeiraVez = primeiraVez;
	}
	public Boolean getRespostaObrigatoria() {
		return respostaObrigatoria;
	}
	public void setRespostaObrigatoria(Boolean respostaObrigatoria) {
		this.respostaObrigatoria = respostaObrigatoria;
	}
	public Boolean getAcompanhamento() {
		return acompanhamento;
	}
	public void setAcompanhamento(Boolean acompanhamento) {
		this.acompanhamento = acompanhamento;
	}
	public Boolean getRespObrigAcompanhamento() {
		return respObrigAcompanhamento;
	}
	public void setRespObrigAcompanhamento(Boolean respObrigAcompanhamento) {
		this.respObrigAcompanhamento = respObrigAcompanhamento;
	}
	public DominioSituacao getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.codigo);
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
		FiltroTipoRespostaConsultoriaVO other = (FiltroTipoRespostaConsultoriaVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getCodigo(), other.getCodigo());
		return umEqualsBuilder.isEquals();
	}
}
