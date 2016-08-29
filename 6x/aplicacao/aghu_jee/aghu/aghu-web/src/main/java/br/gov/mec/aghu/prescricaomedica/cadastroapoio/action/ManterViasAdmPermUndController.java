package br.gov.mec.aghu.prescricaomedica.cadastroapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.model.AfaViaAdmUnf;
import br.gov.mec.aghu.model.AfaViaAdmUnfId;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterViasAdmPermUndController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -6069185589052192076L;
	
	private static final String PAGE_MANTER_VIAS_ADM_PERM_UND = "manterViasAdmPermUnd";
	private static final String PAGE_MANTER_MULTIPLAS_VIAS_ADM_PERM_UND = "manterMultiplasViasAdministracaoPermitidasUnidade";

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	@Inject @Paginator
	private DynamicDataModel<AfaViaAdmUnf> dataModel;
	
	private AfaViaAdmUnf unf;
	private AghUnidadesFuncionaisVO unidFuncionais;
	private AfaViaAdministracao viaAdministracao;
	private List<AfaViaAdmUnf> lista; 
	
	private Boolean edita = false;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
		this.unf = new AfaViaAdmUnf();
		this.unf.setId(new AfaViaAdmUnfId());
		this.unf.setIndSituacao(DominioSituacao.A);
	}
	
	//suggestions
	public List<AghUnidadesFuncionaisVO> listarUnidFuncional(String param) {
		return  this.returnSGWithCount(farmaciaApoioFacade.listarUnidadeExecutora(param),listarUnidFuncionalCount(param));
	}
	
	public Long listarUnidFuncionalCount(String param) {
		return farmaciaApoioFacade.listarUnidadeExecutoraCount(param);
	}
	public List<AfaViaAdministracao>listarViasDeAdministracao(String param) {
		return  farmaciaFacade.listarViasAdministracao(param);
	}
	
	public void gravar() {
		try {
			if (this.edita) {
				farmaciaApoioFacade.gravarViaAdministracao(this.unf, this.edita);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATUALIZAR_VIA_ADMIN_PERMITIDA");
			} else {
				unf.setId(new AfaViaAdmUnfId());
				this.unf.getId().setUnfSeq(unidFuncionais.getSeq());
				this.unf.getId().setVadSigla(viaAdministracao.getSigla());
				this.unf.setViaAdministracao(this.viaAdministracao);
				farmaciaApoioFacade.gravarViaAdministracao(this.unf, edita);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_VIA_ADMIN_PERMITIDA");
			}
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}	
		
		this.getDataModel().reiniciarPaginator();
		cancelarEdicao();
	}
	
	
	public void pesquisar(){
		this.getDataModel().reiniciarPaginator();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AfaViaAdmUnf> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<AfaViaAdmUnf> result = this.farmaciaFacade.listarViaAdmUnf(firstResult, maxResult, orderProperty, asc, unidFuncionais);
		
		if (result == null){
			result = new ArrayList<AfaViaAdmUnf>();
		}
		
		return result;
	}

	@Override
	public Long recuperarCount() {
		return this.farmaciaFacade.listarViaAdmUnfCount(this.unidFuncionais);
	}	
	
	public void editar(AfaViaAdmUnf unf) {
		String sigla = unf.getId().getVadSigla();
		Short unfSeq = unf.getId().getUnfSeq();
		this.viaAdministracao = farmaciaFacade.obterViaAdministracao(sigla);
		this.unf = farmaciaFacade.obterViaAdmUnfId(sigla, unfSeq);
		this.unidFuncionais = farmaciaApoioFacade.obterUnidadeFuncionalVO(unfSeq);
		edita = true;
	}
	
	public String limpar() {
		this.unf =null;
		this.unidFuncionais = null;
		this.viaAdministracao = null;
		this.lista = null;
		this.edita = false;
		this.getDataModel().limparPesquisa();
		return PAGE_MANTER_VIAS_ADM_PERM_UND;
	}
	
	
	public void cancelarEdicao() {
		this.viaAdministracao = null;
		this.unf.setIndSituacao(DominioSituacao.A);
		this.edita = false;
		this.getDataModel().reiniciarPaginator();
	}
	
	public String manterMultiplasVias() {
		return PAGE_MANTER_MULTIPLAS_VIAS_ADM_PERM_UND;
	}
	
	/*
	 * Getters and Setters
	 */
	public DynamicDataModel<AfaViaAdmUnf> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AfaViaAdmUnf> dataModel) {
		this.dataModel = dataModel;
	}
	
	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}
	public AfaViaAdmUnf getUnf() {
		return unf;
	}
	public void setUnf(AfaViaAdmUnf unf) {
		this.unf = unf;
	}
	public AghUnidadesFuncionaisVO getUnidFuncionais() {
		return unidFuncionais;
	}
	public void setUnidFuncionais(AghUnidadesFuncionaisVO unidFuncionais) {
		this.unidFuncionais = unidFuncionais;
	}
	public AfaViaAdministracao getViaAdministracao() {
		return viaAdministracao;
	}
	public void setViaAdministracao(AfaViaAdministracao viaAdministracao) {
		this.viaAdministracao = viaAdministracao;
	}
	public List<AfaViaAdmUnf> getLista() {
		return lista;
	}
	public void setLista(List<AfaViaAdmUnf> lista) {
		this.lista = lista;
	}
	public Boolean getEdita() {
		return edita;
	}
	public void setEdita(Boolean edita) {
		this.edita = edita;
	}
	
}
