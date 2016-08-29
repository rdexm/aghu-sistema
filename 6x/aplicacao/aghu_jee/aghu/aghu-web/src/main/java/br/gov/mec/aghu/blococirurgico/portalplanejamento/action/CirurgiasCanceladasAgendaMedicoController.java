package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.CirurgiasCanceladasAgendaMedicoVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class CirurgiasCanceladasAgendaMedicoController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<CirurgiasCanceladasAgendaMedicoVO> dataModelCancelados;

	private static final long serialVersionUID = -7102047377830610820L;

	private static final String TROCAR_LOCAL_ESP_EQUIPE_ESPERA = "trocarLocalEspEquipeListaEspera";
	//private static final String PESQUISA_AGENDA_CIRURGIA = "pesquisaAgendaCirurgia";
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	/*@Inject
	private TrocarLocalEspEquipeListaEsperaController trocarLocalEspEquipeListaEsperaController;*/
	
	private Integer pucSerMatricula;
	private Short pucSerVinCodigo;
	private Short pucUnfSeq;
	private DominioFuncaoProfissional pucIndFuncaoProf;
	private Short espSeq;
	private Short unfSeq;	
	private Integer pacCodigo;

	private List<CirurgiasCanceladasAgendaMedicoVO> cirurgiasCanceladasAgendaMedicoVO;	
	
	private CirurgiasCanceladasAgendaMedicoVO itemSelecionado;

	public void recebeParametros(Integer pucSerMatricula, Short pucSerVinCodigo, Short pucUnfSeq, DominioFuncaoProfissional pucIndFuncaoProf, Short espSeq, Short unfSeq, Integer pacCodigo) {
		cirurgiasCanceladasAgendaMedicoVO = null;
		this.dataModelCancelados.limparPesquisa();
		this.pucSerMatricula = pucSerMatricula;
		this.pucSerVinCodigo = pucSerVinCodigo;
		this.pucUnfSeq = pucUnfSeq;
		this.pucIndFuncaoProf = pucIndFuncaoProf;
		this.espSeq = espSeq;
		this.unfSeq = unfSeq;
		this.pacCodigo = pacCodigo;
	}	
	
	@Override
	public Long recuperarCount() {		
		return blocoCirurgicoPortalPlanejamentoFacade.pesquisarCirgsCanceladasByMedicoEquipeCount
		(pucSerMatricula, pucSerVinCodigo, pucUnfSeq, pucIndFuncaoProf, espSeq, unfSeq, pacCodigo);
	}
	
	@Override
	public List<CirurgiasCanceladasAgendaMedicoVO> recuperarListaPaginada
		(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		try {
			cirurgiasCanceladasAgendaMedicoVO = blocoCirurgicoPortalPlanejamentoFacade.pesquisarCirgsCanceladasByMedicoEquipe
				(firstResult, maxResult, orderProperty, asc, pucSerMatricula, pucSerVinCodigo, pucUnfSeq, pucIndFuncaoProf, espSeq, unfSeq, pacCodigo);			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}				
		return cirurgiasCanceladasAgendaMedicoVO;
	}

	public String redirectTrocarLocalEspEquipeListaEspera(Object seqObj) {
		//trocarLocalEspEquipeListaEsperaController.setAgdSeq(seq);
		//trocarLocalEspEquipeListaEsperaController.setCameFrom(PESQUISA_AGENDA_CIRURGIA);
		return TROCAR_LOCAL_ESP_EQUIPE_ESPERA;
	}
	
	// Gets e Sets	
	
	public IBlocoCirurgicoPortalPlanejamentoFacade getBlocoCirurgicoPortalPlanejamentoFacade() {
		return blocoCirurgicoPortalPlanejamentoFacade;
	}

	public List<CirurgiasCanceladasAgendaMedicoVO> getCirurgiasCanceladasAgendaMedicoVO() {
		return cirurgiasCanceladasAgendaMedicoVO;
	}

	public void setCirurgiasCanceladasAgendaMedicoVO(
			List<CirurgiasCanceladasAgendaMedicoVO> cirurgiasCanceladasAgendaMedicoVO) {
		this.cirurgiasCanceladasAgendaMedicoVO = cirurgiasCanceladasAgendaMedicoVO;
	}

	public void setBlocoCirurgicoPortalPlanejamentoFacade(
			IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade) {
		this.blocoCirurgicoPortalPlanejamentoFacade = blocoCirurgicoPortalPlanejamentoFacade;
	}	

	public Short getPucUnfSeq() {
		return pucUnfSeq;
	}

	public void setPucUnfSeq(Short pucUnfSeq) {
		this.pucUnfSeq = pucUnfSeq;
	}	

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Integer getPucSerMatricula() {
		return pucSerMatricula;
	}

	public void setPucSerMatricula(Integer pucSerMatricula) {
		this.pucSerMatricula = pucSerMatricula;
	}

	public Short getPucSerVinCodigo() {
		return pucSerVinCodigo;
	}

	public void setPucSerVinCodigo(Short pucSerVinCodigo) {
		this.pucSerVinCodigo = pucSerVinCodigo;
	}

	public DominioFuncaoProfissional getPucIndFuncaoProf() {
		return pucIndFuncaoProf;
	}

	public void setPucIndFuncaoProf(DominioFuncaoProfissional pucIndFuncaoProf) {
		this.pucIndFuncaoProf = pucIndFuncaoProf;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public DynamicDataModel<CirurgiasCanceladasAgendaMedicoVO> getDataModelCancelados() {
	 return dataModelCancelados;
	}

	public void setDataModelCancelados(DynamicDataModel<CirurgiasCanceladasAgendaMedicoVO> dataModelCancelados) {
	 this.dataModelCancelados = dataModelCancelados;
	}

	public CirurgiasCanceladasAgendaMedicoVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(CirurgiasCanceladasAgendaMedicoVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
}