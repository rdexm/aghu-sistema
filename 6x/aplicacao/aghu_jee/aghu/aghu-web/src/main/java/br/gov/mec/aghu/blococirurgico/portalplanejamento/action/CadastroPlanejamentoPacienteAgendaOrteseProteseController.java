package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcAgendaOrtProtese;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


public class CadastroPlanejamentoPacienteAgendaOrteseProteseController extends ActionController{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 4254219625997598246L;
	
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	
	@Inject
	private CadastroPlanejamentoPacienteAgendaController principalController;
	
	private ScoMaterial scoMaterial;
	
	private MbcAgendaOrtProtese agendaOrtProtese ;
	
	private List<MbcAgendaOrtProtese> agendaOrtProteseList ;
	
	private MbcAgendaOrtProtese itemSelecionado;
	
	private Boolean modificouAgendaOrtProtese;
	
	private AghParametros parametro;
	
	private List<MbcAgendaOrtProtese> listaRemocao = new ArrayList<MbcAgendaOrtProtese>();
	
	public void inicio() {
		pesquisar();
	}
	
	public List<ScoMaterial> pesquisarMateriaisOrteseseProteses(String objParam) {
		BigDecimal paramVlNumerico = null;

		
		try {
			parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.GRPO_MAT_ORT_PROT);
			if (parametro != null) {
				paramVlNumerico = parametro.getVlrNumerico();
			}
			
			ScoGrupoMaterial grupoMaterial = this.comprasFacade.obterGrupoMaterialPorId(paramVlNumerico.intValue());
			
			//obter materiais
			return this.returnSGWithCount(this.comprasFacade.listarScoMateriaisGrupoAtiva(objParam, grupoMaterial, true, true),
					this.comprasFacade.contarScoMateriaisGrupoAtiva(objParam, grupoMaterial, true));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return new ArrayList<ScoMaterial>();
	}
	
	public Long pesquisarMateriaisOrteseseProtesesCount(String objParam) {
		BigDecimal paramVlNumerico = null;
		if (parametro != null) {
			paramVlNumerico = parametro.getVlrNumerico();
		}
		return comprasFacade.obterMateriaisOrteseseProtesesAgendaCount(paramVlNumerico, (String) objParam);
	}
	
	
	public void pesquisar() {
		if(getAgdSeq()!=null){
			agendaOrtProteseList = blocoCirurgicoPortalPlanejamentoFacade.buscarOrteseprotesePorAgenda(getAgdSeq());
			for (MbcAgendaOrtProtese agendaOrtprotese : agendaOrtProteseList) {
				ScoMaterial scoMaterialOriginal = comprasFacade.obterScoMaterialOriginal(agendaOrtprotese.getScoMaterial().getCodigo());
				agendaOrtprotese.setScoMaterial(scoMaterialOriginal);
			}
		}
		listaRemocao = new ArrayList<MbcAgendaOrtProtese>();
		modificouAgendaOrtProtese=false;
	}
	
	public void removerAgendaOrtProtese(MbcAgendaOrtProtese agendaOrtProtese) {
		modificouAgendaOrtProtese=true;
		for(int i = 0; i < agendaOrtProteseList.size(); i++){
			if(agendaOrtProtese.getScoMaterial().getCodigo().equals(agendaOrtProteseList.get(i).getScoMaterial().getCodigo())) {
				if(agendaOrtProtese.getId() != null) {
					listaRemocao.add(agendaOrtProtese);
				}
				getAgendaOrtProteseList().remove(i);
				break;
			}
		}
	}
	
	public void limparCampos() {
		scoMaterial = null;
		agendaOrtProtese = null;
		agendaOrtProteseList = null;
		itemSelecionado = null;
		modificouAgendaOrtProtese = false;
	}
	
	public void adicionar() {
		modificouAgendaOrtProtese=true;
		if(scoMaterial!=null){
			try{
				MbcAgendaOrtProtese novoAgendaOrtProtese = new MbcAgendaOrtProtese();
				novoAgendaOrtProtese.setQtde(Short.valueOf("1"));
				ScoMaterial scoMaterialOriginal = comprasFacade.obterScoMaterialOriginal(scoMaterial.getCodigo());
				novoAgendaOrtProtese.setScoMaterial(scoMaterialOriginal);
				
				if(getAgdSeq()!=null){
					novoAgendaOrtProtese.setMbcAgendas(blocoCirurgicoPortalPlanejamentoFacade.obterAgendaPorAgdSeq(getAgdSeq()));
				}
				if(getAgendaOrtProteseList() != null && getAgendaOrtProteseList().size()>0 ){
					blocoCirurgicoFacade.validarAgendaOrteseproteseAdicionadoExistente(agendaOrtProteseList, novoAgendaOrtProtese);
				}else{
					this.setAgendaOrtProteseList(new ArrayList<MbcAgendaOrtProtese>());
				}
				blocoCirurgicoFacade.validarQtdeOrtProtese(novoAgendaOrtProtese);
				getAgendaOrtProteseList().add(novoAgendaOrtProtese);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}finally{
				scoMaterial = null;
			}
		}
	}
	
	public Integer getAgdSeq() {
		return principalController.getAgenda().getSeq();
	}
	
	public MbcAgendaOrtProtese getAgendaOrtProtese() {
		return agendaOrtProtese;
	}

	public void setAgendaOrtProtese(MbcAgendaOrtProtese agendaOrtProtese) {
		this.agendaOrtProtese = agendaOrtProtese;
	}

	public List<MbcAgendaOrtProtese> getAgendaOrtProteseList() {
		return agendaOrtProteseList;
	}

	public void setAgendaOrtProteseList(
			List<MbcAgendaOrtProtese> agendaOrtProteseList) {
		this.agendaOrtProteseList = agendaOrtProteseList;
	}

	public Boolean getModificouAgendaOrtProtese() {
		return modificouAgendaOrtProtese;
	}

	public void setModificouAgendaOrtProtese(Boolean modificouAgendaOrtProtese) {
		this.modificouAgendaOrtProtese = modificouAgendaOrtProtese;
	}

	public ScoMaterial getScoMaterial() {
		return scoMaterial;
	}

	public void setScoMaterial(ScoMaterial scoMaterial) {
		this.scoMaterial = scoMaterial;
	}

	public AghParametros getParametro() {
		return parametro;
	}

	public void setParametro(AghParametros parametro) {
		this.parametro = parametro;
	}

	public List<MbcAgendaOrtProtese> getListaRemocao() {
		return listaRemocao;
	}

	public void setListaRemocao(List<MbcAgendaOrtProtese> listaRemocao) {
		this.listaRemocao = listaRemocao;
	}

	public MbcAgendaOrtProtese getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(MbcAgendaOrtProtese itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
	
}
