package br.gov.mec.aghu.suprimentos.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioFrequenciaEntrega;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ScoItemPacVO implements BaseBean {

	private static final long serialVersionUID = -1149031004341799402L;
	
	private Integer seq;
	private Short numeroItem;
	private BigDecimal valorUnitarioPrevisto;
	private DominioFrequenciaEntrega indFrequencia;
	private Integer frequenciaEntrega;
	private Boolean indEmAf;
	private Boolean indJulgada;
	private Short numeroLote;
	private Integer numeroSolicitacao;
	private DominioTipoSolicitacao tipoSolicitacao;
	private String nomeMaterial;
	private String unidadeMaterial;
	private Integer qtdeSolicitada;
	private Boolean indExclusao;
	private String complemento;
	private ScoItemLicitacao itemLicitacaoOriginal;
	private Boolean itemOutroPac;
	private String motivoExclusao;
	private Boolean indEscolhido;
	private String criterioEscolha;
	private ScoFornecedor forncedorVencedor;
	private Boolean possuiAnexo;
	private Integer codMatServ;
	
	public ScoItemPacVO() {
		
	}
	
	public Short getNumeroItem() {
		return numeroItem;
	}
	public void setNumeroItem(Short numeroItem) {
		this.numeroItem = numeroItem;
	}
	public BigDecimal getValorUnitarioPrevisto() {
		return valorUnitarioPrevisto;
	}
	public void setValorUnitarioPrevisto(BigDecimal valorUnitarioPrevisto) {
		this.valorUnitarioPrevisto = valorUnitarioPrevisto;
	}
	
	public DominioFrequenciaEntrega getIndFrequencia() {
		return indFrequencia;
	}
	
	public void setIndFrequencia(DominioFrequenciaEntrega indFrequencia) {
		this.indFrequencia = indFrequencia;
	}
	
	public  Integer getFrequenciaEntrega() {
		return frequenciaEntrega;
	}
	
	public void setFrequenciaEntrega(Integer frequenciaEntrega) {
		this.frequenciaEntrega = frequenciaEntrega;
	}
	
	public Boolean getIndEmAf() {
		return indEmAf;
	}
	public void setIndEmAf(Boolean indEmAf) {
		this.indEmAf = indEmAf;
	}
	public Boolean getIndJulgada() {
		return indJulgada;
	}
	public void setIndJulgada(Boolean indJulgada) {
		this.indJulgada = indJulgada;
	}
	public Short getNumeroLote() {
		return numeroLote;
	}
	public void setNumeroLote(Short numeroLote) {
		this.numeroLote = numeroLote;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemLicitacaoOriginal.getId().getNumero() == null) ? 0 : itemLicitacaoOriginal.getId().getNumero().hashCode());
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
		ScoItemPacVO other = (ScoItemPacVO) obj;
		if (itemLicitacaoOriginal.getId().getNumero() == null) {
			if (other.itemLicitacaoOriginal.getId().getNumero() != null){
				return false;
			}
		} else if (!itemLicitacaoOriginal.getId().getNumero().equals(other.itemLicitacaoOriginal.getId().getNumero())){
			return false;
		}
		return true;
	}
	public ScoItemLicitacao getItemLicitacaoOriginal() {
		return itemLicitacaoOriginal;
	}
	public void setItemLicitacaoOriginal(ScoItemLicitacao itemLicitacaoOriginal) {
		this.itemLicitacaoOriginal = itemLicitacaoOriginal;
	}
	public Integer getNumeroSolicitacao() {
		return numeroSolicitacao;
	}
	public void setNumeroSolicitacao(Integer numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}
	public DominioTipoSolicitacao getTipoSolicitacao() {
		return tipoSolicitacao;
	}
	public void setTipoSolicitacao(DominioTipoSolicitacao tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}
	public String getNomeMaterial() {
		return nomeMaterial;
	}
	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}
	public String getUnidadeMaterial() {
		return unidadeMaterial;
	}
	public void setUnidadeMaterial(String unidadeMaterial) {
		this.unidadeMaterial = unidadeMaterial;
	}
	public Integer getQtdeSolicitada() {
		return qtdeSolicitada;
	}
	public void setQtdeSolicitada(Integer qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}
	public Boolean getIndExclusao() {
		return indExclusao;
	}
	public void setIndExclusao(Boolean indExclusao) {
		this.indExclusao = indExclusao;
	}
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Boolean getItemOutroPac() {
		return itemOutroPac;
	}

	public void setItemOutroPac(Boolean itemOutroPac) {
		this.itemOutroPac = itemOutroPac;
	}

	public String getMotivoExclusao() {
		return motivoExclusao;
	}

	public void setMotivoExclusao(String motivoExclusao) {
		this.motivoExclusao = motivoExclusao;
	}

	public Boolean getIndEscolhido() {
		return indEscolhido;
	}

	public void setIndEscolhido(Boolean indEscolhido) {
		this.indEscolhido = indEscolhido;
	}

	public String getCriterioEscolha() {
		return criterioEscolha;
	}

	public void setCriterioEscolha(String criterioEscolha) {
		this.criterioEscolha = criterioEscolha;
	}

	public ScoFornecedor getForncedorVencedor() {
		return forncedorVencedor;
	}

	public void setForncedorVencedor(ScoFornecedor forncedorVencedor) {
		this.forncedorVencedor = forncedorVencedor;
	}

	public Boolean getPossuiAnexo() {
		return possuiAnexo;
	}

	public void setPossuiAnexo(Boolean possuiAnexo) {
		this.possuiAnexo = possuiAnexo;
	}

	public Integer getCodMatServ() {
		return codMatServ;
	}

	public void setCodMatServ(Integer codMatServ) {
		this.codMatServ = codMatServ;
	}
}
