package br.gov.mec.aghu.compras.solicitacaoservico.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import br.gov.mec.aghu.compras.action.ConsultaSCSSPaginatorController;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.ItensSCSSVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;

import com.itextpdf.text.DocumentException;

public class ConsultaSSPaginatorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<ItensSCSSVO> dataModel;

	private static final long serialVersionUID = 1289998913199756967L;

	@EJB
	protected IComprasFacade comprasFacade;

	@Inject
	private ConsultaSCSSPaginatorController consultaSCSSPaginatorController;

	private Map<String, Boolean> listaOrdem;
	protected List<ItensSCSSVO> listaSS;
	
	private List<ItensSCSSVO> listaChecked;
	private List<ItensSCSSVO> allChecked;

	
	private static final String PAGE_IMPRIMIR_SOLICITACAO_SERVICO = "compras-imprimirSolicitacaoDeServicoPdfCadastro";
	
	@Inject
	private ImprimirSolicitacaoDeServicoController imprimirSolicitacaoDeServicoController;
	
	private static final Log LOG = LogFactory.getLog(ConsultaSSPaginatorController.class); 

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public List<ItensSCSSVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		if (this.listaSS == null) {
			this.listaSS = new ArrayList<ItensSCSSVO>();			
		} else {
			this.listaSS.clear();
		}
		this.limparControleGrid();

		this.listaSS = this.comprasFacade.listarSSItensSCSSVO(firstResult, maxResults, orderProperty, asc,
				consultaSCSSPaginatorController.getFiltro());

		return listaSS;
	}

	@Override
	public Long recuperarCount() {
		Long ret = null;
		if (consultaSCSSPaginatorController.getErroFiltros()) {
			ret = 0L;
		} else {
			ret = this.comprasFacade.listarSSItensSCSSVOCount(consultaSCSSPaginatorController.getFiltro());
		}
		return ret;
	}
	
	public void marcarTodos() {
		Integer paginaAtual = this.dataModel.getDataTableComponent().getPage() + 1;
		Integer paginaFinal = this.dataModel.getDataTableComponent().getPageCount();
		Integer totalRegistros = this.dataModel.getDataTableComponent().getRowCount();
		Integer registroInicial = this.dataModel.getDataTableComponent().getFirst();
		
		if ((paginaAtual < paginaFinal  &&  this.listaChecked.size() == this.dataModel.getPageSize()) ||
				paginaAtual == paginaFinal && this.listaChecked.size() == (totalRegistros - registroInicial)) {
			for(ItensSCSSVO item : listaSS) {
				if (!this.allChecked.contains(item)) {
					this.allChecked.add(item);
				}
				}
		} else if (this.listaChecked.size() == 0) {
			for(ItensSCSSVO item : listaSS) {
				if (this.allChecked.contains(item)) {
					this.allChecked.remove(item);
				}
			}
		}
	}

	public void selecionarLinha(SelectEvent event) {
		ItensSCSSVO item = (ItensSCSSVO)event.getObject();
		if (this.allChecked.contains(item)) {
			this.allChecked.remove(item);
		} else {
			this.allChecked.add(item);
		}
	}

	public void desmarcarLinha(UnselectEvent event) {
		ItensSCSSVO item = (ItensSCSSVO)event.getObject();
		if (this.allChecked.contains(item)) {
			this.allChecked.remove(item);
		} else {
			this.allChecked.add(item);
		}
	}
	
	public void limparControleGrid() {
		if (this.listaChecked == null) {
			this.setListaChecked(new ArrayList<ItensSCSSVO>());
		}
		this.listaChecked.clear();
		if (this.allChecked == null) {
			this.allChecked = new ArrayList<ItensSCSSVO>();
		}
		this.allChecked.clear();
	}
	
    public String imprimirItens() throws DocumentException {

		
		List<Integer> listaNumero = obterListaNumeroSs(this.allChecked);
		
		this.imprimirSolicitacaoDeServicoController.setNumSolicServicos(listaNumero);

		try {
			this.imprimirSolicitacaoDeServicoController.print(null);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (JRException e) {
			LOG.error(e.getMessage(), e);
		} catch (SystemException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		
		this.dataModel.reiniciarPaginator();

		return PAGE_IMPRIMIR_SOLICITACAO_SERVICO;
	}

    public List<Integer> obterListaNumeroSs(List<ItensSCSSVO> listaSs) {
		List<Integer> listaResultado = new ArrayList<Integer>();
		
		if (listaSs != null) {
			for(ItensSCSSVO ss : listaSs) {				
				listaResultado.add(ss.getNumero());
			}
		}
		
		return listaResultado;
	}

	public List<ItensSCSSVO> getListaSS() {
		return listaSS;
	}

	public void setListaSS(List<ItensSCSSVO> listaSS) {
		this.listaSS = listaSS;
	}

	public Map<String, Boolean> getListaOrdem() {
		return listaOrdem;
	}

	public void setListaOrdem(Map<String, Boolean> listaOrdem) {
		this.listaOrdem = listaOrdem;
	}

	public DynamicDataModel<ItensSCSSVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ItensSCSSVO> dataModel) {
		this.dataModel = dataModel;
	}

	public List<ItensSCSSVO> getListaChecked() {
		return listaChecked;
	}

	public void setListaChecked(List<ItensSCSSVO> listaChecked) {
		this.listaChecked = listaChecked;
	}

	public List<ItensSCSSVO> getAllChecked() {
		return allChecked;
	}

	public void setAllChecked(List<ItensSCSSVO> allChecked) {
		this.allChecked = allChecked;
	}
	
}
