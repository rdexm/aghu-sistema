package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MbcAgendaDiagnostico;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class CadastroPlanejamentoPacienteAgendaDiagnosticoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long								serialVersionUID	= -7833946503129886120L;

	@Inject
	private SecurityController securityController;
	
	@Inject
	private CadastroPlanejamentoPacienteAgendaController	principalController;

	@EJB
	private IAghuFacade										aghuFacade;

	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	private MbcAgendaDiagnostico							diagnostico;
	private Boolean											modificouDiag		= false;

	private List<AghCid>									combo;
	private AghCid											cidCombo;
	private AghCid cidSelecionado;
	private MbcAgendaDiagnostico diagnosticoRemocao;
	private List<SelectItem> cidsSelectItems;
	private Boolean readOnlySuggestionCid = Boolean.FALSE;
	private Boolean readOnlySelectCid = Boolean.FALSE;

	public void inicio() {
		if (this.getCombo() == null || this.getCombo().isEmpty()) {
			this.setCombo(this.aghuFacade.pesquisarCidAtivosUsuaisEquipe(principalController.getMatriculaEquipe(), principalController.getVinCodigoEquipe()));
		}
		if (principalController.getAgenda() != null && principalController.getAgenda().getSeq() != null) {
			List<MbcAgendaDiagnostico> listaAgendaDiag = this.blocoCirurgicoPortalPlanejamentoFacade.pesquisarAgendaDiagnosticoEscalaCirurgicaPorAgenda(principalController.getAgenda().getSeq());
			if (listaAgendaDiag != null && !listaAgendaDiag.isEmpty()) {
				this.diagnostico = listaAgendaDiag.get(0);	
			}
			
		}
		if (diagnostico == null) {
			this.diagnostico = new MbcAgendaDiagnostico();
			this.diagnostico.setMbcAgendas(principalController.getAgenda());
		} else {
			this.cidSelecionado = this.diagnostico.getAghCid();
			this.cidCombo = this.diagnostico.getAghCid();
		}
		getValidarRegrasPermissao();
		carregarCidsSelectItems();
	}
	
	private void getValidarRegrasPermissao() {
		Boolean permissaoExecutar = securityController.usuarioTemPermissao("cadastroPlanejamentoPacienteAgendaAbaDiagnosticosExecutar", "cadastroPlanejamentoPacienteAgendaAbaDiagnosticosExecutar");
		Boolean permissaoAlterar = securityController.usuarioTemPermissao("cadastroPlanejamentoPacienteAgendaAbaDiagnosticosAlterar", "cadastroPlanejamentoPacienteAgendaAbaDiagnosticosAlterar");
		readOnlySuggestionCid = !permissaoExecutar && !permissaoAlterar;
		readOnlySelectCid = !permissaoExecutar && !permissaoAlterar;
	}
	
	public List<SelectItem> carregarCidsSelectItems(){
		cidsSelectItems = new ArrayList<SelectItem>();
		for (AghCid aghCid : getCombo()) {
			SelectItem item = new SelectItem();
			String label = aghCid.getCodigoDescricaoCapitalize();
			item.setLabel(label.length()>165 ? label.substring(0,165):label );
			item.setValue(aghCid);
			cidsSelectItems.add(item);
		}
		return cidsSelectItems;
	}
	
	public List<AghCid> pesquisarCids(String objParam) throws ApplicationBusinessException {
		return this.returnSGWithCount(this.aghuFacade.pesquisarCidPorCodigoDescricao(100, objParam),pesquisarCidsCount(objParam));
	}

	public Long pesquisarCidsCount(String objParam) throws ApplicationBusinessException {
		return this.aghuFacade.pesquisarCidPorCodigoDescricaoCount((Object)objParam);
	}

	public void removerCid() {
		if(diagnostico.getId() != null) {
			diagnosticoRemocao = diagnostico;
		}
		this.diagnostico = new MbcAgendaDiagnostico();
		this.diagnostico.setMbcAgendas(principalController.getAgenda());
		this.cidCombo = null;
		this.cidSelecionado = null;
		this.modificouDiag = true;
	}
	
	public void mudarCidUsual() {
		this.diagnostico = new MbcAgendaDiagnostico();
		this.diagnostico.setMbcAgendas(principalController.getAgenda());
		this.diagnostico.setAghCid(this.cidCombo);
		this.cidSelecionado = this.cidCombo;
		this.modificouDiag = true;
	}

	public void adicionar() {
		this.diagnostico.setAghCid(this.cidSelecionado);
		this.cidCombo = this.cidSelecionado;
		this.modificouDiag = true;
	}
	
	public void limparParametros(){
		this.diagnostico = null;
		this.modificouDiag = false;
		this.combo = null;
		this.cidCombo  = null;
		this.cidSelecionado  = null;
		this.diagnosticoRemocao  = null;
		this.cidsSelectItems  = null;
		this.readOnlySuggestionCid = Boolean.FALSE;
		this.readOnlySelectCid = Boolean.FALSE;
	}

	public void setDiagnostico(MbcAgendaDiagnostico diagnostico) {
		this.diagnostico = diagnostico;
	}

	public MbcAgendaDiagnostico getDiagnostico() {
		return diagnostico;
	}

	public void setModificouDiag(Boolean modificouDiag) {
		this.modificouDiag = modificouDiag;
	}

	public Boolean getModificouDiag() {
		return modificouDiag;
	}

	public void setCidCombo(AghCid cidCombo) {
		this.cidCombo = cidCombo;
	}

	public AghCid getCidCombo() {
		return cidCombo;
	}

	public void setCombo(List<AghCid> combo) {
		this.combo = combo;
	}

	public List<AghCid> getCombo() {
		return combo;
	}

	public MbcAgendaDiagnostico getDiagnosticoRemocao() {
		return diagnosticoRemocao;
	}

	public void setDiagnosticoRemocao(MbcAgendaDiagnostico diagnosticoRemocao) {
		this.diagnosticoRemocao = diagnosticoRemocao;
	}

	public AghCid getCidSelecionado() {
		return cidSelecionado;
	}

	public void setCidSelecionado(AghCid cidSelecionado) {
		this.cidSelecionado = cidSelecionado;
	}

	public List<SelectItem> getCidsSelectItems() {
		return cidsSelectItems;
	}

	public void setCidsSelectItems(List<SelectItem> cidsSelectItems) {
		this.cidsSelectItems = cidsSelectItems;
	}

	public Boolean getReadOnlySuggestionCid() {
		return readOnlySuggestionCid;
	}

	public void setReadOnlySuggestionCid(Boolean readOnlySuggestionCid) {
		this.readOnlySuggestionCid = readOnlySuggestionCid;
	}

	public Boolean getReadOnlySelectCid() {
		return readOnlySelectCid;
	}

	public void setReadOnlySelectCid(Boolean readOnlySelectCid) {
		this.readOnlySelectCid = readOnlySelectCid;
	}

}
