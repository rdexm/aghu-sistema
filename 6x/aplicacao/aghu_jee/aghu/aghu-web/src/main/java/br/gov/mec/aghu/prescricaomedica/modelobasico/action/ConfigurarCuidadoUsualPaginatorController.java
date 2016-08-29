package br.gov.mec.aghu.prescricaomedica.modelobasico.action;

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
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmCuidadoUsualUnf;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

/**
 * 
 * @author Carlos Leilson
 * 
 * 
 */

public class ConfigurarCuidadoUsualPaginatorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<MpmCuidadoUsualUnf> dataModel;

	private static final Log LOG = LogFactory
			.getLog(ConfigurarCuidadoUsualPaginatorController.class);

	/**
         * 
         */
	private static final long serialVersionUID = -145733189582395257L;

	@EJB
	private IModeloBasicoFacade modeloBasicoFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private RapServidores servidor;
	private MpmCuidadoUsualUnf mpmCuidadoUsualUnf;
	private List<MpmCuidadoUsualUnf> listaCuidadoUsualUnf;
	private List<AghUnidadesFuncionais> listaUnidadesFuncionais;
	private AghUnidadesFuncionais unidadeFuncional,
			unidadeFuncionalSelecionada;
	private Boolean marcar;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() throws ApplicationBusinessException {
		servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(
				obterLoginUsuarioLogado(), new Date());
		this.dataModel.setPesquisaAtiva(true);
		if(this.dataModel.getFirst() == 0) {
			this.dataModel.reiniciarPaginator();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<MpmCuidadoUsualUnf> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		List<MpmCuidadoUsualUnf> result = this.modeloBasicoFacade
				.pesquisarCuidadoUsualUnf(firstResult, maxResult,
				MpmCuidadoUsualUnf.Fields.CDU_SEQ.toString(),
				true, unidadeFuncional);
		if (result == null) {
			result = new ArrayList<MpmCuidadoUsualUnf>();
		}
		return result;
	}

	@Override
	public Long recuperarCount() {
		Long count = modeloBasicoFacade.pesquisarCuidadoUsualUnfCount(
				unidadeFuncional).longValue();
		return count;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais(String parametro) {
		return returnSGWithCount(aghuFacade.listarUnidadesFuncionaisPorSeqDescricaoAndarAla(parametro, null, Boolean.TRUE),
				aghuFacade.listarUnidadesFuncionaisPorSeqDescricaoAndarAlaCount(parametro, null, Boolean.TRUE));
	}

	public void pesquisar() {
		this.dataModel.setPesquisaAtiva(true);
		this.dataModel.reiniciarPaginator();
	}

	public void cadastrarTodos() {
		try {
			modeloBasicoFacade.incluirTodosCuidadosUnf(getServidor());
			apresentarMsgNegocio("MENSAGEM_CUIDADOS_UNIDADES_VINCULADOS_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
		this.dataModel.reiniciarPaginator();
	}

	public void vincularUnidades() {
		try {
			modeloBasicoFacade.incluirCuidadosUnf(unidadeFuncional,
					getServidor());
			apresentarMsgNegocio("MENSAGEM_CUIDADOS_UNIDADE_VINCULADOS_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
		this.dataModel.reiniciarPaginator();
	}

	public void excluir() {
		try {
			this.modeloBasicoFacade.excluir(mpmCuidadoUsualUnf, getServidor());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
		this.dataModel.reiniciarPaginator();
	}

	public void selecionouUnidadeFuncional() {
		setUnidadeFuncionalSelecionada(getUnidadeFuncional());
	}

	public List<MpmCuidadoUsualUnf> getListaCuidadoUsualUnf() {
		return listaCuidadoUsualUnf;
	}

	public void setListaCuidadoUsualUnf(
			List<MpmCuidadoUsualUnf> listaCuidadoUsualUnf) {
		this.listaCuidadoUsualUnf = listaCuidadoUsualUnf;
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

	public void setModeloBasicoFacade(IModeloBasicoFacade modeloBasicoFacade) {
		this.modeloBasicoFacade = modeloBasicoFacade;
	}

	public Boolean getMarcar() {
		return marcar;
	}

	public void setMarcar(Boolean marcar) {
		this.marcar = marcar;
	}

	public MpmCuidadoUsualUnf getMpmCuidadoUsualUnf() {
		return mpmCuidadoUsualUnf;
	}

	public void setMpmCuidadoUsualUnf(MpmCuidadoUsualUnf mpmCuidadoUsualUnf) {
		this.mpmCuidadoUsualUnf = mpmCuidadoUsualUnf;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public DynamicDataModel<MpmCuidadoUsualUnf> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmCuidadoUsualUnf> dataModel) {
		this.dataModel = dataModel;
	}

}
