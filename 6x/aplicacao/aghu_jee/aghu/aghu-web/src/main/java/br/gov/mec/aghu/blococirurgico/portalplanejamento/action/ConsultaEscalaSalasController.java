package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaSalasDiasVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaSalasVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;


public class ConsultaEscalaSalasController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = -4997391332261628111L;
	private static final String RELATORIO_ESCALA_SALAS = "relatorioEscalaDeSalasPdf";
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	@EJB
	private IAghuFacade  aghuFacade;

	@Inject
	private RelatorioEscalaDeSalasController relatorioEscalaDeSalasController;
	
	private AghUnidadesFuncionais aghUnidadesFuncionais;
	private List<AghUnidadesFuncionais> listUnidadesFuncionais;
	private List<EscalaSalasVO> escala;
	private String urlVoltar;
	private Short unfSeq;
	private List<EscalaSalasDiasVO> itemSelecionado;

	public void iniciar() {
		if(getListUnidadesFuncionais() == null || getListUnidadesFuncionais().isEmpty() || unfSeq != null){
			setListUnidadesFuncionais(aghuFacade.pesquisarUnidadesFuncionaisAtivasPorCaracteristica(ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS));
			if(getListUnidadesFuncionais() == null || getListUnidadesFuncionais().isEmpty()){
				aghUnidadesFuncionais = null;
			} else {
				if(unfSeq != null){
					for(AghUnidadesFuncionais unidade : getListUnidadesFuncionais()){
						if(unidade.getSeq().equals(unfSeq)){
							aghUnidadesFuncionais = unidade;
							break;
						}
					}
				} else {
					aghUnidadesFuncionais = getListUnidadesFuncionais().get(0);
				}
				this.mudarUnidadeFuncional();				
			}
		}
	
	}
	
	
	public String redirecionarRelatorio() {
		relatorioEscalaDeSalasController.setSeqUnidade(this.aghUnidadesFuncionais.getSeq());
		return RELATORIO_ESCALA_SALAS;
	}
	
	public void mudarUnidadeFuncional() {
		if(aghUnidadesFuncionais != null) {
			this.setEscala(blocoCirurgicoPortalPlanejamentoFacade.pesquisarEscalaSalasPorUnidadeCirurgica(aghUnidadesFuncionais.getSeq()));
		}
	}
	
	public String voltar() {
		return getUrlVoltar();
	}

	public void setAghUnidadesFuncionais(AghUnidadesFuncionais aghUnidadesFuncionais) {
		this.aghUnidadesFuncionais = aghUnidadesFuncionais;
	}

	public AghUnidadesFuncionais getAghUnidadesFuncionais() {
		return aghUnidadesFuncionais;
	}

	public void setEscala(List<EscalaSalasVO> escala) {
		this.escala = escala;
	}

	public List<EscalaSalasVO> getEscala() {
		return escala;
	}

	public void setUrlVoltar(String urlVoltar) {
		this.urlVoltar = urlVoltar;
	}

	public String getUrlVoltar() {
		return urlVoltar;
	}

	public void setListUnidadesFuncionais(List<AghUnidadesFuncionais> listUnidadesFuncionais) {
		this.listUnidadesFuncionais = listUnidadesFuncionais;
	}

	public List<AghUnidadesFuncionais> getListUnidadesFuncionais() {
		return listUnidadesFuncionais;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public List<EscalaSalasDiasVO> getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(List<EscalaSalasDiasVO> itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
}
