package br.gov.mec.aghu.exames.pesquisa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioLwsOrigem;
import br.gov.mec.aghu.dominio.DominioLwsTipoComunicacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaResultadoCargaInterfaceVO;
import br.gov.mec.aghu.exames.vo.PesquisaResultadoCargaVO;
import br.gov.mec.aghu.model.LwsComunicacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;

public class PesquisaResultadoCargaInterfaceController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 3661785096909101631L;

	@EJB
	private IExamesFacade examesFacade;

	private DominioLwsTipoComunicacao[] tiposComunicao = new DominioLwsTipoComunicacao[]{ DominioLwsTipoComunicacao.PEDIDO_CARGA_EXAMES, 
																						  DominioLwsTipoComunicacao.CANCELAMENTO_PEDIDO_EXAME, 
																						  DominioLwsTipoComunicacao.RECEPCAO_RESULTADOS};
	
	private DominioLwsOrigem[] origem = new DominioLwsOrigem[] {DominioLwsOrigem.AGHU, DominioLwsOrigem.INTERFACEAMENTO};

	private PesquisaResultadoCargaInterfaceVO filtro = new PesquisaResultadoCargaInterfaceVO();

	@Inject @Paginator
	private DynamicDataModel<PesquisaResultadoCargaVO> dataModel;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		try {
			return this.examesFacade.pesquisarLwsComSolicitacaoExamesCount(filtro);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	@Override
	public List<PesquisaResultadoCargaVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		try {
			return this.examesFacade.pesquisarLwsComSolicitacaoExames(firstResult, maxResult, LwsComunicacao.Fields.DATA_HORA.toString(), false, filtro);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}	
	}

	public void limparPesquisa() {
		setFiltro(new PesquisaResultadoCargaInterfaceVO());
		dataModel.limparPesquisa();
	}

	public boolean disabledComErro() {
		if (DominioLwsTipoComunicacao.RECEPCAO_RESULTADOS.equals(filtro.getTipoComunicao())) {
			return true;
		}
		return false;
	}
	

	public PesquisaResultadoCargaInterfaceVO getFiltro() {
		return filtro;
	}

	public void setFiltro(PesquisaResultadoCargaInterfaceVO filtro) {
		this.filtro = filtro;
	}

	public DominioLwsTipoComunicacao[] getTiposComunicao() {
		return tiposComunicao;
	}

	public void setTiposComunicao(DominioLwsTipoComunicacao[] tiposComunicao) {
		this.tiposComunicao = tiposComunicao;
	}

	public DominioLwsOrigem[] getOrigem() {
		return origem;
	}

	public void setOrigem(DominioLwsOrigem[] origem) {
		this.origem = origem;
	}

	public DynamicDataModel<PesquisaResultadoCargaVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<PesquisaResultadoCargaVO> dataModel) {
		this.dataModel = dataModel;
	}
}