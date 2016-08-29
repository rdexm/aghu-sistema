package br.gov.mec.aghu.compras.pac.vo;

import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.core.commons.BaseBean;

public class LotesLicitacaoVO implements BaseBean {

	private static final long serialVersionUID = -2982988432503728419L;
	
	
	private String lote;
	private String descricao;
	private String ocorrencia;
	private List<ItensLicitacaoCadastradaVO> listaItemLicitacao;
	private String descricaoOcorrencia;
	
	public LotesLicitacaoVO() {
		super();
		this.listaItemLicitacao = new ArrayList<ItensLicitacaoCadastradaVO>();
	}
	public String getLote() {
		return lote;
	}
	public void setLote(String lote) {
		this.lote = lote;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getOcorrencia() {
		return ocorrencia;
	}
	public void setOcorrencia(String ocorrencia) {
		this.ocorrencia = ocorrencia;
	}
	public List<ItensLicitacaoCadastradaVO> getListaItemLicitacao() {
		return listaItemLicitacao;
	}
	public void setListaItemLicitacao(
			List<ItensLicitacaoCadastradaVO> listaItemLicitacao) {
		this.listaItemLicitacao = listaItemLicitacao;
	}
	public String getDescricaoOcorrencia() {
		return descricaoOcorrencia;
	}
	public void setDescricaoOcorrencia(String descricaoOcorrencia) {
		this.descricaoOcorrencia = descricaoOcorrencia;
	}
	
	
	
}
