package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaFatTipoCaractItensPaginatorController extends ActionController implements ActionPaginator {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3597191059525506792L;
	
	//Filtros
	private Integer seq;
	private String caracteristica;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	@Inject @Paginator
	private DynamicDataModel<FatTipoCaractItens> dataModel;
	private FatTipoCaractItens itemSelecionado;
	
	private static final String PAGINA_CADASTRO = "cadastroFatTipoCaractItens";
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	public void limpar() {
		seq= null;
		caracteristica= null;
		dataModel.limparPesquisa();
		dataModel.setPesquisaAtiva(false);
	}
	
	public String editar() {
		return PAGINA_CADASTRO;
	}
	
	public String criar() {
		return PAGINA_CADASTRO;
	}
	
	public void excluir() {
		try{
			String descricao = itemSelecionado.getCaracteristica();		
			faturamentoFacade.excluirFatTipoCaractItensPorSeq(itemSelecionado.getSeq());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_FTCI",descricao);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	@Override
	public List<FatTipoCaractItens> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return faturamentoFacade.pesquisarTiposCaractItensPorSeqCaracteristica(firstResult, maxResult, orderProperty, true, seq, caracteristica);
	}

	@Override
	public Long recuperarCount() {
		return faturamentoFacade.pesquisarTiposCaractItensPorSeqCaracteristicaCount(seq, caracteristica);
	}
	
	public void pesquisar() {
		getDataModel().reiniciarPaginator();
	}
	
	public Integer getSeq() {
		return seq;
	}
	public String getCaracteristica() {
		return caracteristica;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public void setCaracteristica(String caracteristica) {
		this.caracteristica = caracteristica;
	}
	public DynamicDataModel<FatTipoCaractItens> getDataModel() {
		return dataModel;
	}
	public void setDataModel(DynamicDataModel<FatTipoCaractItens> dataModel) {
		this.dataModel = dataModel;
	}

	public FatTipoCaractItens getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(FatTipoCaractItens itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

}
