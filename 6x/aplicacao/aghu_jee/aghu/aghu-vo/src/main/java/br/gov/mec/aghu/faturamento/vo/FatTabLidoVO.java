package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioTipoPlano;

public class FatTabLidoVO implements Serializable {
	private static final long serialVersionUID = 3315005592667415733L;

	private Integer codTabela;
	private String stPrincipal;
	private String cidsValidos;
	private DominioTipoPlano modalidade;

	public FatTabLidoVO() {
	}

	public FatTabLidoVO(final Integer codTabela, final String stPrincipal, final String cidsValidos, final DominioTipoPlano modalidade) {
		this.codTabela = codTabela;
		this.stPrincipal = stPrincipal;
		this.cidsValidos = cidsValidos;
		this.modalidade = modalidade;
	}

	public Integer getCodTabela() {
		return codTabela;
	}

	public void setCodTabela(Integer codTabela) {
		this.codTabela = codTabela;
	}

	public String getStPrincipal() {
		return stPrincipal;
	}

	public void setStPrincipal(String stPrincipal) {
		this.stPrincipal = stPrincipal;
	}

	public String getCidsValidos() {
		return cidsValidos;
	}

	public void setCidsValidos(String cidsValidos) {
		this.cidsValidos = cidsValidos;
	}

	public DominioTipoPlano getModalidade() {
		return modalidade;
	}

	public void setModalidade(DominioTipoPlano modalidade) {
		this.modalidade = modalidade;
	}

	@Override
	public int hashCode() {
		final int prime = 32452843;
		return prime * 1 + ((codTabela == null) ? 0 : codTabela.hashCode());
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
		FatTabLidoVO other = (FatTabLidoVO) obj;

		if (codTabela == null) {
			if (other.codTabela != null) {
				return false;
			}
		} else if (!codTabela.equals(other.codTabela)) {
			return false;
		}
		return true;
	}

}
