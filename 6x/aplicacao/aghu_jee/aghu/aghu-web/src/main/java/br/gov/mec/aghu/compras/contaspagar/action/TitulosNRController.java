package br.gov.mec.aghu.compras.contaspagar.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloNrVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class TitulosNRController extends ActionController implements ActionPaginator {


	private static final long serialVersionUID = -8303137598526636172L;

	/**
	 * Atributo responsavel por armazenar todo o log de ações
	 */
	private static final Log LOG = LogFactory.getLog(TitulosNRController.class);
		
	/**
	 * Constante usada para redirecionar a página quando necessário
	 */
	private static final String PAGE_PESQUISAR_TITULOS_NR = "pesquisarTitulosNR"; 	
		
	/**
	 * Injeção da Facade para chamada da RN, ON e DAO
	 */
	@EJB
	private IComprasFacade comprasFacade;
		
	/**
	 * Atributo utilizado para paginação
	 */
	@Inject @Paginator
	private DynamicDataModel<TituloNrVO> dataModel;
	
	/**
	 * Atributo usado para filtro
	 */
	private TituloNrVO tituloNrVO;
	
	/**
	 * Atributo usado para SIM/NÃO do campo estornado
	 */
	private DominioSimNao dominioSimNao;
	
	/**
	 * Atributo usado para pular linha dentro do tooltip
	 */
	private static final String BREAK_LINE = "<br/>";
	
	/**
	 * Atributo usado para popular o selectOneMenu
	 */
	private DominioSimNao pagamentoIndestorno;

	
	/**
	 * Método executado cada vez que instanciar a página
	 */
	@PostConstruct
	protected void inicializar() {
		
		this.begin(conversation);
		
		//inicializando os objetos
		this.tituloNrVO = new TituloNrVO();
	}
		
	/**
	 * Mensagens do Sistema
	 */		
	private enum AgenciaMessages implements BusinessExceptionCode {
		ERRO_PESQUISA;
	}
		
	/**
	 * Metodo usado no botão pesquisar
	 */
	public void pesquisar() {
		
		//setando o valor do DominioSimNao do selectOneMenu dentro do VO para que possa fazer a consulta
		if (this.pagamentoIndestorno != null) {
			this.tituloNrVO.setPagamentoIndEstorno(this.pagamentoIndestorno.isSim()); 
		}
		
		this.dataModel.reiniciarPaginator();
	}
		
	/**
	 * Método responsável por pesquisar a lista de tituloNrVO
	 * @return lista de tituloRN
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TituloNrVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		List<TituloNrVO> titulosNrVO = new ArrayList<TituloNrVO>();
		
		try {
			
			titulosNrVO = this.comprasFacade.pesquisarListaTitulosNR(firstResult, maxResult, orderProperty, asc, this.tituloNrVO);
					
		} catch (BaseException e) {
			
			LOG.error("Exceção capturada ao recuperar lista paginada: ", e);
			apresentarMsgNegocio(Severity.INFO, AgenciaMessages.ERRO_PESQUISA.toString());
		}
		
		return titulosNrVO;
	}

	/**
	 * Método responsável por recuperar a quantidade de titulos
	 * @return quantidade de agencias
	 */
	@Override
	public Long recuperarCount() {
		
		Long countTituloNrVO = null;
		
		try {
			
			countTituloNrVO = this.comprasFacade.pesquisarCountTitulosNR(this.tituloNrVO);
			
		} catch (BaseException e) {
			
			LOG.error("Exceção capturada ao recuperar count da lista: ", e);
			apresentarMsgNegocio(Severity.INFO, AgenciaMessages.ERRO_PESQUISA.toString());
		}
		
		return countTituloNrVO;
	}
		
	/**
	 * Metodo usado para cancelar a consulta
	 * @return Página de pesquisart Titulos RN.
	 */
	public String cancelar() {
		
		return PAGE_PESQUISAR_TITULOS_NR;
	}
			
	/**
	 * 
	 * Método responsável por limpar os filtros da tela
	 * @return Página de pesquisart Titulos RN.
	 */
	public String limpar() {
		
		this.tituloNrVO = new TituloNrVO();
		this.pagamentoIndestorno = null;
		
		this.dataModel.limparPesquisa();
		
		return PAGE_PESQUISAR_TITULOS_NR;
	}
	
	/**
	 * Método usado para preencher o Tooltip
	 * @param item
	 * @return
	 */
	public String getHintTitulo(TituloNrVO item) {
		
		  StringBuilder buffer = new StringBuilder(64);
		  buffer.append("Geração NR: ").append(transformarNuloVazio(DateUtil.obterDataFormatada(item.getDataGeracao(), "dd/MM/yyyy"))).append(BREAK_LINE)
		  .append("Licitação: ").append(transformarNuloVazio(item.getLctNumero())).append(' ')
		  .append(transformarNuloVazio(item.getAutorizFornNumCompl())).append(BREAK_LINE)
		  .append("NF: ").append(transformarNuloVazio(item.getNotaRecebimentoNumero()))
		  .append(transformarNuloVazio(item.getDocumentoFiscalSerie())).append(BREAK_LINE);
		 
		  return buffer.toString();
	}
	
	/**
	  * Transforma nulo em vazio
	  * 
	  * @param o
	  * @return
	  */
	 private String transformarNuloVazio(Object o) {
	  return o == null ? StringUtils.EMPTY : o.toString().trim();
	 }
	
	/**
	 * 
	 * Declaração dos Getters and Setters
	 * 
	 */
	
	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}
	
	public DynamicDataModel<TituloNrVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<TituloNrVO> dataModel) {
		this.dataModel = dataModel;
	}

	public DominioSimNao getDominioSimNao() {
		return dominioSimNao;
	}

	public void setDominioSimNao(DominioSimNao dominioSimNao) {
		this.dominioSimNao = dominioSimNao;
	}

	public TituloNrVO getTituloNrVO() {
		return tituloNrVO;
	}

	public void setTituloNrVO(TituloNrVO tituloNrVO) {
		this.tituloNrVO = tituloNrVO;
	}

	public DominioSimNao getPagamentoIndestorno() {
		return pagamentoIndestorno;
	}

	public void setPagamentoIndestorno(DominioSimNao pagamentoIndestorno) {
		this.pagamentoIndestorno = pagamentoIndestorno;
	}
}