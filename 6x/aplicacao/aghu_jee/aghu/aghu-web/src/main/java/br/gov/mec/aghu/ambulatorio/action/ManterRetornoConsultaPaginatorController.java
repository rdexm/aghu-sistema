package br.gov.mec.aghu.ambulatorio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.FiltroConsultaRetornoConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroParametrosPadraoConsultaVO;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da listagem de grupo da condição
 * de atendimento.
 * 
 * @author vinicius.silva
 */
public class ManterRetornoConsultaPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AacRetornos> dataModel;

	private static final Log LOG = LogFactory.getLog(ManterRetornoConsultaPaginatorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1393921256668856337L;
	
	private static final String RETORNO_CONSULTA_CRUD = "ambulatorio-manterRetornoConsultaCRUD";
	
	private AacRetornos selecionado;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	private FiltroParametrosPadraoConsultaVO filtroPadrao; 
	private FiltroConsultaRetornoConsultaVO filtroConsulta;
	private Short seq;
	private boolean exibirBotaoNovo;

	public void inicio() {
	 

	 

		filtroConsulta = new FiltroConsultaRetornoConsultaVO();
		filtroPadrao = new FiltroParametrosPadraoConsultaVO();
	
	}
	
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoNovo = true;
	}

	public void limparPesquisa() {
		this.filtroConsulta = new FiltroConsultaRetornoConsultaVO();
		this.filtroPadrao = new FiltroParametrosPadraoConsultaVO();
		this.dataModel.limparPesquisa();
		this.seq = null;
	}
	
	@Override
	public Long recuperarCount() {
		return ambulatorioFacade.pesquisarConsultaCountRetornoConsulta(filtroConsulta);
	}

	@Override
	public List<AacRetornos> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,boolean asc) {
		filtroPadrao.setFirstResult(firstResult);
		filtroPadrao.setMaxResult(maxResult);
		filtroPadrao.setOrdenacaoAscDesc(asc);
		filtroPadrao.setOrderProperty(orderProperty);
		return this.ambulatorioFacade.pesquisarConsultaPaginadaRetornoConsulta(filtroPadrao, filtroConsulta);
	}

	public void excluir() {
		try {
			this.ambulatorioFacade.removerRegistroRetornoConsulta(this.selecionado.getSeq());
			this.dataModel.reiniciarPaginator();
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_RETORNO_CONSULTA_EXCLUIDO");
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir(){
		return RETORNO_CONSULTA_CRUD;
	}
	
	public String editar(){
		return RETORNO_CONSULTA_CRUD;
	}
	
	
	/************************* getters and setters *************************************/
	public FiltroParametrosPadraoConsultaVO getFiltroPadrao() {
		return filtroPadrao;
	}

	public void setFiltroPadrao(FiltroParametrosPadraoConsultaVO filtroPadrao) {
		this.filtroPadrao = filtroPadrao;
	}

	public FiltroConsultaRetornoConsultaVO getFiltroConsulta() {
		return filtroConsulta;
	}

	public void setFiltroConsulta(FiltroConsultaRetornoConsultaVO filtroConsulta) {
		this.filtroConsulta = filtroConsulta;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}
	public DynamicDataModel<AacRetornos> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AacRetornos> dataModel) {
	 this.dataModel = dataModel;
	}

	public AacRetornos getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AacRetornos selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}
}
