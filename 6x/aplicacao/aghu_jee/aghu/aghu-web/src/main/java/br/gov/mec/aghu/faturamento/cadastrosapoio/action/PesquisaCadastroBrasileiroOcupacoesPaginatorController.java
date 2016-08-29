package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.FatCbos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class PesquisaCadastroBrasileiroOcupacoesPaginatorController extends
		ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8526951872924389996L;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	@Inject @Paginator
	private DynamicDataModel<FatCbos> dataModel;

	private FatCbos filtroFatCbos = new FatCbos();
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public List<FatCbos> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return this.faturamentoApoioFacade
				.pesquisarCadastroBrasileiroOcupacoes(firstResult, maxResult,
						orderProperty, asc, filtroFatCbos);
	}

	@Override
	public Long recuperarCount() {
		return this.faturamentoApoioFacade
				.pesquisarCadastroBrasileiroOcupacoesCount(this.filtroFatCbos);
	}

	/**
	 * Realiza pesquisa que carrega a grid.
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Limpa os campos e o grid da tela de pesquisa.
	 */
	public void limpar() {
		this.filtroFatCbos = new FatCbos();
		this.dataModel.limparPesquisa();
	}

	/**
	 * Deve exibir todos os campos do registro que não aparecem na listagem
	 * 
	 * @param item
	 * @return
	 */
	public String obterHint(FatCbos item) {


		String descricao = "";
		if(StringUtils.isNotBlank(item.getDescricao())){
			descricao = StringUtils.abbreviate(item.getDescricao(), 50);
		}
		return descricao;
	}

	/*
	 * Getters and setters
	 */

	public FatCbos getFiltroFatCbos() {
		return filtroFatCbos;
	}

	public void setFiltroFatCbos(FatCbos filtroFatCbos) {
		this.filtroFatCbos = filtroFatCbos;
	}

	public DynamicDataModel<FatCbos> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatCbos> dataModel) {
		this.dataModel = dataModel;
	}

}
