package br.gov.mec.aghu.ambulatorio.action;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.EspecialidadeDisponivelVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroEspecialidadeDisponivelVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.prescricaomedica.vo.AghEspecialidadeVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class PesquisaEspecialidadeDisponivelPaginatorController extends ActionController implements ActionPaginator{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3643914104469810998L;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject @Paginator
	private DynamicDataModel<EspecialidadeDisponivelVO> dataModel;
	
	private EspecialidadeDisponivelVO especialidadeDisponivelVO;
	
	
	private FiltroEspecialidadeDisponivelVO filtro = new FiltroEspecialidadeDisponivelVO();
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar(){
		try {
			ambulatorioFacade.validarCamposPreenchidos(filtro);
			dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String limpar(){
		
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		filtro = new FiltroEspecialidadeDisponivelVO();
		dataModel.limparPesquisa();
		return null;
	}
	/**
	 * Percorre o formulário resetando os valores digitados nos campos (inputText, inputNumero, selectOneMenu, ...)
	 * 
	 * @param object {@link Object}
	 */
	private void limparValoresSubmetidos(Object object) {
		
		if (object == null || object instanceof UIComponent == false) {
			return;
		}
		
		Iterator<UIComponent> uiComponent = ((UIComponent) object).getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}
		
		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}
	
	/**
	 * Trunca descrição da Grid.
	 * @param item
	 * @param tamanhoMaximo
	 * @return String truncada.
	 */
	public String obterHint(String item, Integer tamanhoMaximo) {
		
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
	}	
	
	public List<AghEspecialidadeVO> obterEspecialidade(String parametro) throws ApplicationBusinessException {
		return returnSGWithCount(aghuFacade.pesquisarEspecialidadesPorSiglaNomeCodigo(parametro.trim()), obterEspecialidadeCount(parametro));
	}
	
	public Long obterEspecialidadeCount(String parametro) throws ApplicationBusinessException {
		return aghuFacade.pesquisarEspecialidadesPorSiglaNomeCodigoCount(parametro.trim());
	}
	
	public List<AacCondicaoAtendimento> obterListaCondicaoAtendimento(String parametro) throws ApplicationBusinessException{
		return returnSGWithCount(ambulatorioFacade.obterListaCondicaoAtendimento(parametro.trim()), obterListaCondicaoAtendimentoCount(parametro));
	}
	
	public Long obterListaCondicaoAtendimentoCount(String parametro) throws ApplicationBusinessException {
		return ambulatorioFacade.obterListaCondicaoAtendimentoCount(parametro.trim());
	}
	public List<AacPagador> obterListaPagadores(String parametro){
		return returnSGWithCount(ambulatorioFacade.obterListaPagadores(parametro.trim()), obterListaPagadoresCount(parametro));
	}
	
	public Long obterListaPagadoresCount(String parametro){
		return ambulatorioFacade.obterListaPagadoresCount(parametro.trim());
	}
	
	public List<AacTipoAgendamento> obterListaTiposAgendamento(String parametro){
		return returnSGWithCount(ambulatorioFacade.obterListaTiposAgendamento(parametro.trim()), obterListaTiposAgendamentoCount(parametro));
	}
	
	public Long obterListaTiposAgendamentoCount(String parametro){
		return ambulatorioFacade.obterListaTiposAgendamentoCount(parametro.trim());
	}
	
	@Override
	public Long recuperarCount() {
		return ambulatorioFacade.obterListaEspecialidadesDisponiveisCount(filtro);
	}
	
	@Override
	public List<EspecialidadeDisponivelVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc) {
		return ambulatorioFacade.obterListaEspecialidadesDisponiveis(filtro,firstResult,maxResult,orderProperty,asc);
	}
	
	public BigDecimal obterSomatorio(){
		return ambulatorioFacade.obterSomatorioQuantidade(filtro).get(0);
	}
	
	//get e set
	public DynamicDataModel<EspecialidadeDisponivelVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<EspecialidadeDisponivelVO> dataModel) {
		this.dataModel = dataModel;
	}

	public FiltroEspecialidadeDisponivelVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroEspecialidadeDisponivelVO filtro) {
		this.filtro = filtro;
	}

	public EspecialidadeDisponivelVO getEspecialidadeDisponivelVO() {
		return especialidadeDisponivelVO;
	}

	public void setEspecialidadeDisponivelVO(
			EspecialidadeDisponivelVO especialidadeDisponivelVO) {
		this.especialidadeDisponivelVO = especialidadeDisponivelVO;
	}
}