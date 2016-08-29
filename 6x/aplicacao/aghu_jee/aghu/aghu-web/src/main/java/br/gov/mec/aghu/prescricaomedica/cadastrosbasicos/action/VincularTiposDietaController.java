package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.AnuTipoItemDietaUnfs;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


public class VincularTiposDietaController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<AnuTipoItemDietaUnfs> dataModel;

	private static final Log LOG = LogFactory.getLog(VincularTiposDietaController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 2268130714768981509L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IModeloBasicoFacade modeloBasicoFacade;

	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private RapServidores servidor;
    
	private AnuTipoItemDieta tipoDieta;

	private AnuTipoItemDietaUnfs anuTipoItemDietaUnfs;

	private List<AnuTipoItemDietaUnfs> anuTipoItemDietaUnfsList;

	private MpmUnidadeMedidaMedica unidadeMedida;
	private MpmTipoFrequenciaAprazamento tipoAprazamento;

	/* Unidades funcionais */
	private List<AghUnidadesFuncionais> listaUnidadesFuncionais = new ArrayList<AghUnidadesFuncionais>();
	private AghUnidadesFuncionais unidadeFuncional, unidadeFuncionalSelecionada;

	private boolean exigeComplemento = false;
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	private enum VincularTiposDietaControllerExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_UNIDADE_FUNCIONAL_TIPO_DIETA_OBRIGATORIA, MENSAGEM_SUCESSO_REMOCAO_VINCULO_TIPO_ITEM_DIETA, MENSAGEM_SUCESSO_CRIACAO_TIPO_ITEM_DIETA, MENSAGEM_ERRO_REMOCAO_VINCULO_TTIPO_ITEM_DIETA, MENSAGEM_SUCESSO_CRIACAO_VINCULO_TIPO_ITEM_DIETA, MENSAGEM_SUCESSO_CRIACAO_VINCULO_TIPO_ITEM_DIETA_UNIDADES;
	}

	public void inicio() throws ApplicationBusinessException {
		servidor=registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			pesquisar();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AnuTipoItemDietaUnfs> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		List<AnuTipoItemDietaUnfs> result = this.cadastrosBasicosPrescricaoMedicaFacade
				.pesquisarTipoDieta(firstResult, maxResult, orderProperty, asc,
						unidadeFuncional);
		if (result == null) {
			result = new ArrayList<AnuTipoItemDietaUnfs>();
		}

		return result;
	}

	@Override
	public Long recuperarCount() {
		Integer count = this.cadastrosBasicosPrescricaoMedicaFacade
				.pesquisarTipoDietaCount(unidadeFuncional);
		return count.longValue();
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void cadastrarTodos() {
			try {
				this.cadastrosBasicosPrescricaoMedicaFacade.inserirTodosTiposDietasUnfs(servidor);
				
				apresentarMsgNegocio(
								Severity.INFO,
								VincularTiposDietaControllerExceptionCode.MENSAGEM_SUCESSO_CRIACAO_VINCULO_TIPO_ITEM_DIETA_UNIDADES
										.toString());

			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(), e);
			}
			this.dataModel.reiniciarPaginator();
	}

	public void vincularUnidades() {
		if (unidadeFuncional != null) {
			try {
				this.cadastrosBasicosPrescricaoMedicaFacade.incluirTiposDietasUnfs(unidadeFuncional, servidor);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(), e);
				return;
			}			
			apresentarMsgNegocio(
							Severity.INFO,
							VincularTiposDietaControllerExceptionCode.MENSAGEM_SUCESSO_CRIACAO_VINCULO_TIPO_ITEM_DIETA
									.toString(),
							unidadeFuncional.getDescricao());
		} else {
			apresentarMsgNegocio(
							Severity.ERROR,
							VincularTiposDietaControllerExceptionCode.MENSAGEM_UNIDADE_FUNCIONAL_TIPO_DIETA_OBRIGATORIA
									.toString());
		}
		this.dataModel.reiniciarPaginator();
	}

	public void excluir() {
		this.cadastrosBasicosPrescricaoMedicaFacade
				.excluir(anuTipoItemDietaUnfs);

		apresentarMsgNegocio(
						Severity.INFO,
						VincularTiposDietaControllerExceptionCode.MENSAGEM_SUCESSO_REMOCAO_VINCULO_TIPO_ITEM_DIETA
								.toString(),
						anuTipoItemDietaUnfs.getTipoItemDieta().getDescricao());
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Método da suggestion box para pesquisa de unidades funcionais a incluir
	 * na lista Exclui da listagem os itens que já estão na tela Ignora a
	 * pesquisa caso o parametro seja o próprio valor selecionado anteriormente
	 * (contorna falha de pesquisa múltipla na suggestion box)
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais(
			String parametro) {
		return returnSGWithCount(aghuFacade.listarUnidadesFuncionaisPorSeqDescricaoAndarAla(
				parametro, null, Boolean.TRUE), aghuFacade.listarUnidadesFuncionaisPorSeqDescricaoAndarAlaCount(
						parametro, null, Boolean.TRUE));
	}

	public void selecionouUnidadeFuncional() {
		setUnidadeFuncionalSelecionada(getUnidadeFuncional());
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public boolean isExigeComplemento() {
		return exigeComplemento;
	}

	public void setExigeComplemento(boolean exigeComplemento) {
		this.exigeComplemento = exigeComplemento;
	}

	public AnuTipoItemDieta getTipoDieta() {
		return tipoDieta;
	}

	public void setTipoDieta(AnuTipoItemDieta tipoDieta) {
		this.tipoDieta = tipoDieta;
	}

	public MpmUnidadeMedidaMedica getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(MpmUnidadeMedidaMedica unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public MpmTipoFrequenciaAprazamento getTipoAprazamento() {
		return tipoAprazamento;
	}

	public void setTipoAprazamento(MpmTipoFrequenciaAprazamento tipoAprazamento) {
		this.tipoAprazamento = tipoAprazamento;
	}

	public List<AghUnidadesFuncionais> getListaUnidadesFuncionais() {
		return listaUnidadesFuncionais;
	}

	public void setListaUnidadesFuncionais(
			List<AghUnidadesFuncionais> listaUnidadesFuncionais) {
		this.listaUnidadesFuncionais = listaUnidadesFuncionais;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalSelecionada() {
		return unidadeFuncionalSelecionada;
	}

	public void setUnidadeFuncionalSelecionada(
			AghUnidadesFuncionais unidadeFuncionalSelecionada) {
		this.unidadeFuncionalSelecionada = unidadeFuncionalSelecionada;
	}

	public IModeloBasicoFacade getModeloBasicoFacade() {
		return modeloBasicoFacade;
	}

	public void setModeloBasicoFacade(IModeloBasicoFacade modeloBasico) {
		this.modeloBasicoFacade = modeloBasico;
	}

	public List<AnuTipoItemDietaUnfs> getAnuTipoItemDietaUnfsList() {
		return anuTipoItemDietaUnfsList;
	}

	public void setAnuTipoItemDietaUnfsList(
			List<AnuTipoItemDietaUnfs> anuTipoItemDietaUnfsList) {
		this.anuTipoItemDietaUnfsList = anuTipoItemDietaUnfsList;
	}

	public AnuTipoItemDietaUnfs getAnuTipoItemDietaUnfs() {
		return anuTipoItemDietaUnfs;
	}

	public void setAnuTipoItemDietaUnfs(
			AnuTipoItemDietaUnfs anuTipoItemDietaUnfs) {
		this.anuTipoItemDietaUnfs = anuTipoItemDietaUnfs;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public DynamicDataModel<AnuTipoItemDietaUnfs> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AnuTipoItemDietaUnfs> dataModel) {
	 this.dataModel = dataModel;
	}
}
