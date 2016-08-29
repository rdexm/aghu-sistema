package br.gov.mec.aghu.blococirurgico.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.vo.VisualizaCirurgiaCanceladaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class VisualizaCirurgiaCanceladaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(VisualizaCirurgiaCanceladaController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 6128660844162662072L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	private static final String PESQUISA_AGENDA_CIRURGIA = "blococirurgico-pesquisaAgendaCirurgia";
	
	/**
	 * Codigo da cirurgia, obtido via page parameter.
	 */
	private Integer agdSeq;
	
	/**
	 * Dados da ciruriga.
	 */
	private VisualizaCirurgiaCanceladaVO cirurgiaCancelada;
	
	/**
	 * Controle de abas.
	 */
	private String abaAtiva;
	
	private static final Integer maxWidth = 65;
	
	public void inicio() {

		if (this.agdSeq != null) {

			try {
				this.cirurgiaCancelada = this.blocoCirurgicoFacade.buscarCirurgiaCancelada(this.agdSeq);
			} catch (ApplicationBusinessException e) {
				LOG.error("Excecao Capturada: ", e);
				apresentarExcecaoNegocio(e);
			}
			
			this.cirurgiaCancelada.setListaAnestesias(this.blocoCirurgicoPortalPlanejamentoFacade.listarAgendaAnestesiaPorAgdSeq(this.agdSeq));
			this.cirurgiaCancelada.setListaProcedimentos(this.blocoCirurgicoPortalPlanejamentoFacade.listarAgendaProcedimentoPorAgdSeq(this.agdSeq));
			this.cirurgiaCancelada.setDiagnostico(this.blocoCirurgicoPortalPlanejamentoFacade.obterAgendaDiagnosticoEscalaCirurgicaPorAgenda(this.agdSeq));
		}
	
	}
	
	
	public String voltar() {
		this.agdSeq = null;
		this.cirurgiaCancelada = null;
		this.abaAtiva = null;
		return PESQUISA_AGENDA_CIRURGIA;
	}
	
	public String abreviar(String str){
		String abreviado = str;
		if(str != null && str.length() > maxWidth) {
			abreviado = StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}
	
	//Getters and Setters
	
	public String getAbaAtiva() {
		return abaAtiva;
	}
	
	public void setAbaAtiva(String aba) {
		abaAtiva = aba;
	}
	
	public Integer getAgdSeq() {
		return agdSeq;
	}

	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}

	public VisualizaCirurgiaCanceladaVO getCirurgiaCancelada() {
		return cirurgiaCancelada;
	}

	public void setCirurgiaCancelada(VisualizaCirurgiaCanceladaVO cirurgiaCancelada) {
		this.cirurgiaCancelada = cirurgiaCancelada;
	}
}
