package br.gov.mec.aghu.prescricaomedica.action;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import br.gov.mec.aghu.comissoes.vo.SolicitacoesUsoMedicamentoVO;
import br.gov.mec.aghu.comissoes.vo.VMpmItemPrcrMdtosVO;
import br.gov.mec.aghu.dominio.DominioIndRespAvaliacao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoMedicamento;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.VAghUnidFuncional;
import br.gov.mec.aghu.model.VMedicoSolicitante;
import br.gov.mec.aghu.model.VMpmItemPrcrMdtos;
import br.gov.mec.aghu.model.VMpmpProfInterna;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class PesquisarSolicitacoesUsoMedicamentoPaginatorController extends ActionController implements ActionPaginator{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3155866786352055343L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject @Paginator
	private DynamicDataModel<VMpmItemPrcrMdtos> dataModel;
	
//	private VMpmItemPrcrMdtos itemSelecionado;
	
	private SolicitacoesUsoMedicamentoVO filtro;
	private VMpmItemPrcrMdtosVO itemSelecionado;
	
	
	
	public enum SolicitacaoUsoMedicamentoONExceptionCode implements BusinessExceptionCode {
		MPM_01554, MPM_01439;
	}

	@PostConstruct
	public void iniciar() {

		if (filtro == null) {
			filtro = new SolicitacoesUsoMedicamentoVO();
			filtro.setSituacao(DominioSituacaoSolicitacaoMedicamento.P);
			filtro.setAvaliador(DominioIndRespAvaliacao.C);
			this.dataModel.setPesquisaAtiva(false);
		}
		this.begin(conversation);
	}
	
	public void limpar() {
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		this.dataModel.setPesquisaAtiva(false);
		filtro = new SolicitacoesUsoMedicamentoVO();
		this.dataModel.limparPesquisa();
	}
	
	private void limparValoresSubmetidos(Object object) {
		if (object == null || object instanceof UIComponent == false) {
			return;
		}
		Iterator<UIComponent> uiComponent = ((UIComponent) object)
				.getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}
		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}
	
	public String truncaString(String generica){
		if(!generica.isEmpty() && (generica.length() > 20)){
			generica = generica.substring(0, 18) + " ...";
		}
		return generica;
	}
	
	public String truncaLocalPaciente(String generica){
		if(!generica.isEmpty() && (generica.length() > 8)){
			generica = generica.substring(0, 7) + " ...";
		}
		return generica;
	}
	
	/**
	 * Suggestion Box de Medicamento - SB1.
	 */
	public List<AfaMedicamento> listarMedicamento(String parametro) {
		return returnSGWithCount(this.prescricaoMedicaFacade.pesquisarMedicamentosSB1(parametro), 
								 this.prescricaoMedicaFacade.pesquisarMedicamentosSB1Count(parametro));
	}
	
	/**
	 * Suggestion Box de Tipo Uso - SB2.
	 */
	public List<AfaTipoUsoMdto> listarTipoUso(String parametro) {
		return returnSGWithCount(this.prescricaoMedicaFacade.pesquisarTipoUsoSB2(parametro), 
								 this.prescricaoMedicaFacade.pesquisarTipoUsoSB2Count(parametro));
	}
	
	/**
	 * Suggestion Box de Grupo Uso - SB3.
	 */
	public List<AfaGrupoUsoMedicamento> listarGrupoUso(String parametro) {
		return returnSGWithCount(this.prescricaoMedicaFacade.pesquisarGrupoUsoSB3(parametro), 
								 this.prescricaoMedicaFacade.pesquisarGrupoUsoSB3Count(parametro));
	}
	
	/**
	 * Suggestion Box de Unidade Funcional - SB4.
	 */
	public List<VAghUnidFuncional> listarVAghUnidFuncional(String parametro) {
		return returnSGWithCount(this.prescricaoMedicaFacade.pesquisarVUnidFuncionalSB4(parametro), 
								 this.prescricaoMedicaFacade.pesquisarVUnidFuncionalSB4Count(parametro));
	}
	
	/**
	 * Suggestion Box de Equipe - SB5.
	 */
	public List<VMpmpProfInterna> listarVMpmpProfInterna(String parametro) {
		return returnSGWithCount(this.prescricaoMedicaFacade.pesquisarVMpmpProfInternaSB5(parametro), 
								 this.prescricaoMedicaFacade.pesquisarVMpmpProfInternaSB5Count(parametro));
	}
	
	/**
	 * Suggestion Box de Medico Solicitante - SB6.
	 */
	public List<VMedicoSolicitante> listarVMedicoSolicitante(String parametro) {
		return returnSGWithCount(this.prescricaoMedicaFacade.pesquisarVMedicoSolicitanteSB6(parametro), 
								 this.prescricaoMedicaFacade.pesquisarVMedicoSolicitanteSB6Count(parametro));
	}
	
	@Override
	public Long recuperarCount() {
		return prescricaoMedicaFacade.pesquisarSolicitacoesUsoMedicamentoCount(filtro);
	}

	@Override
	public List<VMpmItemPrcrMdtosVO> recuperarListaPaginada(Integer arg0, Integer arg1,
			String arg2, boolean arg3) {
		return prescricaoMedicaFacade.pesquisarSolicitacoesUsoMedicamento(arg0, arg1, arg2, arg3, filtro);
	}
	
	public void pesquisar() throws ApplicationBusinessException{

		try {
			prescricaoMedicaFacade.validarPesquisaSolicitacoesUsoMedicamentos(filtro);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		dataModel.reiniciarPaginator();
	}		
	
	public String buscarResumoLocalPaciente(VMpmItemPrcrMdtosVO p)
			throws ApplicationBusinessException {
		AghAtendimentos aghAtendimentos = new AghAtendimentos();
		AinLeitos ainLeitos = null;
		AinQuartos ainQuartos = null;
		AghUnidadesFuncionais aghUnidadesFuncional = null;
		
		if(p.getLtoLtoId() != null){
			ainLeitos = prescricaoMedicaFacade.pesquisarAinLeitoPorId(p.getLtoLtoId());
		}
		
		if(p.getQrtNumero() != null){
			ainQuartos = prescricaoMedicaFacade.pesquisarAinQuartoPorId(p.getQrtNumero());
		}
		
		if(p.getUnfSeq() != null){
			aghUnidadesFuncional = prescricaoMedicaFacade.pesquisarAghUnidadesFuncionaisPorId(p.getUnfSeq());
		}
		
		aghAtendimentos.setLeito(ainLeitos);
		aghAtendimentos.setQuarto(ainQuartos);
		aghAtendimentos.setUnidadeFuncional(aghUnidadesFuncional);
		
		return prescricaoMedicaFacade.buscarResumoLocalPacienteII(aghAtendimentos);
	}

	public SolicitacoesUsoMedicamentoVO getFiltro() {
		return filtro;
	}

	public void setFiltro(SolicitacoesUsoMedicamentoVO filtro) {
		this.filtro = filtro;
	}

	public DynamicDataModel<VMpmItemPrcrMdtos> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<VMpmItemPrcrMdtos> dataModel) {
		this.dataModel = dataModel;
	}

	public VMpmItemPrcrMdtosVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(VMpmItemPrcrMdtosVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

//	public VMpmItemPrcrMdtos getItemSelecionado() {
//		return itemSelecionado;
//	}
//
//	public void setItemSelecionado(VMpmItemPrcrMdtos itemSelecionado) {
//		this.itemSelecionado = itemSelecionado;
//	}

}
