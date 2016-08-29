package br.gov.mec.aghu.compras.contaspagar.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.commons.BaseBean;

public class TituloProgramadoVO  implements BaseBean {

	private static final long serialVersionUID = -547269985021065702L;
	
	private ScoFornecedor fornecedor;
	private String fornecedorRazaoSocial;
	private Integer notaRecebimentoNumero;
	private Long fonteRecursoFinancCodigo;                                           
	private String fonteRecursoFinanDescricao;
	private Integer liquidacaoSiafiVinculacaoPagamento;
	private Integer tituloSeq;
	private Date tituloDataVencimento;
	private DominioSituacaoTitulo tituloIndSituacao;
	private String descricaoSituacao;
	private Double tituloValor;
	private boolean selecionado;

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public String getFornecedorRazaoSocial() {
		return fornecedorRazaoSocial;
	}

	public void setFornecedorRazaoSocial(String fornecedorRazaoSocial) {
		this.fornecedorRazaoSocial = fornecedorRazaoSocial;
	}

	public Integer getNotaRecebimentoNumero() {
		return notaRecebimentoNumero;
	}

	public void setNotaRecebimentoNumero(Integer notaRecebimentoNumero) {
		this.notaRecebimentoNumero = notaRecebimentoNumero;
	}

	public Long getFonteRecursoFinancCodigo() {
		return fonteRecursoFinancCodigo;
	}

	public void setFonteRecursoFinancCodigo(Long fonteRecursoFinancCodigo) {
		this.fonteRecursoFinancCodigo = fonteRecursoFinancCodigo;
	}

	public String getFonteRecursoFinanDescricao() {
		return fonteRecursoFinanDescricao;
	}

	public void setFonteRecursoFinanDescricao(String fonteRecursoFinanDescricao) {
		this.fonteRecursoFinanDescricao = fonteRecursoFinanDescricao;
	}

	public Integer getLiquidacaoSiafiVinculacaoPagamento() {
		return liquidacaoSiafiVinculacaoPagamento;
	}

	public void setLiquidacaoSiafiVinculacaoPagamento(
			Integer liquidacaoSiafiVinculacaoPagamento) {
		this.liquidacaoSiafiVinculacaoPagamento = liquidacaoSiafiVinculacaoPagamento;
	}

	public Integer getTituloSeq() {
		return tituloSeq;
	}

	public void setTituloSeq(Integer tituloSeq) {
		this.tituloSeq = tituloSeq;
	}

	public Date getTituloDataVencimento() {
		return tituloDataVencimento;
	}

	public void setTituloDataVencimento(Date tituloDataVencimento) {
		this.tituloDataVencimento = tituloDataVencimento;
	}

	public DominioSituacaoTitulo getTituloIndSituacao() {
		return tituloIndSituacao;
	}

	public void setTituloIndSituacao(DominioSituacaoTitulo tituloIndSituacao) {
		this.tituloIndSituacao = tituloIndSituacao;
	}

	public String getDescricaoSituacao() {
		return descricaoSituacao;
	}

	public void setDescricaoSituacao(String descricaoSituacao) {
		this.descricaoSituacao = descricaoSituacao;
	}

	public Double getTituloValor() {
		return tituloValor;
	}

	public void setTituloValor(Double tituloValor) {
		this.tituloValor = tituloValor;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public void setSelecionado(boolean selecionado) {
		this.selecionado  = selecionado;		
	}
	
	public boolean isSelecionado() {
        return selecionado;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((tituloSeq == null) ? 0 : tituloSeq.hashCode());
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
		if (!(obj instanceof TituloProgramadoVO)) {
			return false;
		}
		TituloProgramadoVO other = (TituloProgramadoVO) obj;
		if (tituloSeq == null) {
			if (other.tituloSeq != null) {
				return false;
			}
		} else if (!tituloSeq.equals(other.tituloSeq)) {
			return false;
		}
		return true;
	}
}
