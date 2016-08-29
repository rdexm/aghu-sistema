package br.gov.mec.aghu.exames.action;

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
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoMapa;
import br.gov.mec.aghu.dominio.DominoOrigemMapaAmostraItemExame;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AelConfigMapa;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

public class EmissaoMapaTrabalhoController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AelConfigMapa> dataModel;

	private static final Log LOG = LogFactory.getLog(EmissaoMapaTrabalhoController.class);

	private static final long serialVersionUID = -7013320980575836924L;
	
	private final String ERRO_GERAR_RELATORIO = "ERRO_GERAR_RELATORIO";

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	private Boolean allChecked = Boolean.FALSE;
	private List<Short> seqsMap;
	private List<Short> allSeqsMapBanco;
	private Short seqMapaSelecionado;

	// Campos de filtro para pesquisa
	private AghUnidadesFuncionais unidadeExecutora;
	private AelUnidExecUsuario usuarioUnidadeExecutora;

	private String mapa;
	private String cameFrom;

	private DominoOrigemMapaAmostraItemExame origem;

	private DominioTipoImpressaoMapa tipoImpressao;

	private Boolean fecharModal;
	private String tituloModal;
	private boolean exibeModal = false;

	private AelConfigMapa mapaSelecionado;
	private Date dataMapa;
	private Integer nroMapa;
	private boolean directPrint;
	private boolean pesquisaAutomatica = true;

	@Inject
	private RelMapaTrabHemoCD4Controller relMapaTrabHemoCD4Controller;

	@Inject
	private RelMapaTrabBioEquController relMapaTrabBioEquController;

	@Inject
	private RelMapaTrabalhoBioqController relMapaTrabalhoBioqController;

	@Inject
	private RelMapaTrabalhoEPFController relMapaTrabalhoEPFController;

	@Inject
	private RelMapaTrabalhoUroController relMapaTrabalhoUroController;

	@Inject
	private RelMapaTrabalhoHemoController relMapaTrabalhoHemoController;

	@Inject
	private RelMapaTrabalhoHemaController relMapaTrabalhoHemaController;

	//@Inject
	//private RelMapaTrabalhoSoroController relMapaTrabalhoSoroController;

	/**
	 * Chamado no inicio de cada conversacao
	 */
	public void iniciar() {
	 


		if (this.pesquisaAutomatica) {

			exibeModal = false;

			// Obtem o usuario da unidade executora
			try {
				this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
			} catch (ApplicationBusinessException e) {
				this.usuarioUnidadeExecutora = null;
			}

			// Resgata a unidade executora associada ao usuario
			if (this.usuarioUnidadeExecutora != null) {
				this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
			}

			this.pesquisar();
			this.pesquisaAutomatica = false;
		}
	
	}

	/**
	 * Pesquisa principal de solicitacao de exame e suas respectivas amostras
	 */
	public void pesquisar() {
		allChecked = Boolean.FALSE;
		seqsMap = new ArrayList<Short>();
		seqMapaSelecionado = null;
		mapaSelecionado = null;
		nroMapa = null;
		dataMapa = null;
		exibeModal = false;

		// Caso se entre na tela sem uma unidade executora,
		// não pode efetuar a pesquisa
		if (unidadeExecutora != null) {
			this.dataModel.reiniciarPaginator();
		}
	}

	/**
	 * Limpa os filtros da consulta
	 */
	public void limpar() {

		if (this.usuarioUnidadeExecutora != null) {
			// Reseta a unidade executora associada ao usuario
			this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
		}

		origem = null;
		mapa = null;
		this.pesquisaAutomatica = true;
		pesquisar();
	}

	@Override
	public Long recuperarCount() {
		if(unidadeExecutora == null){
			return null;
		}
		return examesFacade.pesquisarAelConfigMapaPorPrioridadeUnidadeFederativaCount(unidadeExecutora, mapa, origem);
	}

	@Override
	public List<AelConfigMapa> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<AelConfigMapa> result = examesFacade.pesquisarAelConfigMapaPorPrioridadeUnidadeFederativa(unidadeExecutora, mapa, origem, firstResult, maxResult, orderProperty, asc);
		allSeqsMapBanco = new ArrayList<Short>();
		for (AelConfigMapa aelConfigMapa : result) {
			allSeqsMapBanco.add(aelConfigMapa.getSeq());
		}

		return result;
	}

	public void addSeqMapa(AelConfigMapa seqMapa) {
		if (seqsMap == null) {
			seqsMap = new ArrayList<Short>();

		} else {
			if (seqsMap.contains(seqMapa.getSeq())) {
				seqsMap.remove(seqMapa.getSeq());
			} else {
				seqsMap.add(seqMapa.getSeq());
			}

			allChecked = (seqsMap.size() == allSeqsMapBanco.size());
		}
	}

	/**
	 * Metodo para pesquisa na suggestion box de unidade executora
	 */
	public List<AghUnidadesFuncionais> obterAghUnidadesFuncionaisExecutoras(final String objPesquisa) {
		return returnSGWithCount(this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricao(objPesquisa),
				this.obterAghUnidadesFuncionaisExecutorasCount(objPesquisa));
	}
	
	public Long obterAghUnidadesFuncionaisExecutorasCount(final String objPesquisa) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricaoCount(objPesquisa);
	}

	/**
	 * Persiste identificacao da unidade executora atraves do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora() {
		try {
			// Persiste identificacao da unidade executora do usuario
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.unidadeExecutora);
			pesquisar();
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String voltar() {

		String retorno = this.cameFrom;

		this.allChecked = Boolean.FALSE;
		this.seqsMap = null;
		this.allSeqsMapBanco = null;
		this.seqMapaSelecionado = null;
		this.unidadeExecutora = null;
		this.usuarioUnidadeExecutora = null;
		this.mapa = null;
		this.cameFrom = null;
		this.origem = null;
		this.tipoImpressao = null;
		this.fecharModal = null;
		this.tituloModal = null;
		this.exibeModal = false;
		this.mapaSelecionado = null;
		this.dataMapa = null;
		this.nroMapa = null;
		this.directPrint = false;
		this.pesquisaAutomatica = true;
		this.dataModel.limparPesquisa();

		return retorno;
	}

	public void validaExibicaoModal() {
		if (seqsMap.isEmpty()) {
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_MAPA_NAO_SELECIONADO");
			exibeModal = false;

		} else if (seqsMap.size() != 1) {
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_MULTIPLOS_MAPAS_SELECIONADOS");
			exibeModal = false;

		} else {
			try {
				mapaSelecionado = examesFacade.obterAelConfigMapaPorSeq(seqsMap.get(0));

				Class.forName(this.getClass().getPackage().getName() + "." + mapaSelecionado.getReport());

				tituloModal = mapaSelecionado.getNomeMapa();
				tipoImpressao = DominioTipoImpressaoMapa.R;
				nroMapa = null;
				dataMapa = new Date();
				exibeModal = true;
			} catch (ClassNotFoundException e) {
				this.apresentarMsgNegocio(Severity.ERROR, "MSG_CONTROLLER_NOT_FOUND", mapaSelecionado.getReport());
				exibeModal = false;
			}
		}
	}

	public void emitirRelatorio() {

		if (seqsMap.isEmpty()) {
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_MAPA_NAO_SELECIONADO");
		}

		for (Short seq : seqsMap) {
			final AelConfigMapa mapa = examesFacade.obterAelConfigMapaPorSeq(seq);
			boolean achouController = true;
			try {
				if (RelMapaTrabalhoBioqController.class.getSimpleName().equalsIgnoreCase(mapa.getReport())) {
					emiteRelatorioMapaTrabalhoBioquimica(mapa, DominioTipoImpressaoMapa.I, "AELR_MAPA_BIOQ");
				} else if (RelMapaTrabalhoSoroController.class.getSimpleName().equalsIgnoreCase(mapa.getReport())) {
					emiteRelatorioMapaTrabalhoBioquimica(mapa, DominioTipoImpressaoMapa.I, "AELR_MAPA_SORO");
				} else if (RelMapaTrabalhoEPFController.class.getSimpleName().equalsIgnoreCase(mapa.getReport())) {
					emiteRelatorioMapaTrabalhoEPF(mapa, DominioTipoImpressaoMapa.I);
				} else if (RelMapaTrabalhoUroController.class.getSimpleName().equalsIgnoreCase(mapa.getReport())) {
					emiteRelatorioMapaTrabalhoUrocultura(mapa, DominioTipoImpressaoMapa.I);
				} else if (RelMapaTrabBioEquController.class.getSimpleName().equalsIgnoreCase(mapa.getReport())) {
					emiteRelMapaTrabBioEquController(mapa, DominioTipoImpressaoMapa.I);
				} else if (RelMapaTrabHemoCD4Controller.class.getSimpleName().equalsIgnoreCase(mapa.getReport())) {
					emiteRelMapaTrabHemoCD4Controller(mapa, DominioTipoImpressaoMapa.I);
				} else if (RelMapaTrabalhoHemoController.class.getSimpleName().equalsIgnoreCase(mapa.getReport())) {
					emiteRelatorioMapaTrabalhoHemocultura(mapa, DominioTipoImpressaoMapa.I, "AELR_MAPA_HEMO");
				} else if (RelMapaTrabalhoHemaController.class.getSimpleName().equalsIgnoreCase(mapa.getReport())) {
					emiteRelatorioMapaTrabalhoHematologia(mapa, DominioTipoImpressaoMapa.I);
				} else {
					this.apresentarMsgNegocio(Severity.ERROR, "MSG_CONTROLLER_NOT_FOUND", mapa.getReport());
					achouController = false;
				}
				if (achouController) {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
				}
			} catch (SistemaImpressaoException e) {
				LOG.error(e.getMessage(),e);
				this.apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getMessage(),e);
				this.apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
			}
		}
		limpar();
	}

	public String reEmitirRelatorio() {
		return mapaSelecionado.getReport();
	}

	public void reEmitirRelatorioPrinter() {
		if (mapaSelecionado != null) {
			try {
				if (RelMapaTrabalhoBioqController.class.getSimpleName().equalsIgnoreCase(mapaSelecionado.getReport())) {
					emiteRelatorioMapaTrabalhoBioquimica(mapaSelecionado, DominioTipoImpressaoMapa.R, "AELR_MAPA_BIOQ");
				} else if (RelMapaTrabalhoSoroController.class.getSimpleName().equalsIgnoreCase(mapaSelecionado.getReport())) {
					emiteRelatorioMapaTrabalhoBioquimica(mapaSelecionado, DominioTipoImpressaoMapa.R, "AELR_MAPA_SORO");
				} else if (RelMapaTrabalhoEPFController.class.getSimpleName().equalsIgnoreCase(mapaSelecionado.getReport())) {
					emiteRelatorioMapaTrabalhoEPF(mapaSelecionado, DominioTipoImpressaoMapa.R);
				} else if (RelMapaTrabalhoUroController.class.getSimpleName().equalsIgnoreCase(mapaSelecionado.getReport())) {
					emiteRelatorioMapaTrabalhoUrocultura(mapaSelecionado, DominioTipoImpressaoMapa.R);
				} else if (RelMapaTrabBioEquController.class.getSimpleName().equalsIgnoreCase(mapaSelecionado.getReport())) {
					emiteRelMapaTrabBioEquController(mapaSelecionado, DominioTipoImpressaoMapa.R);
				} else if (RelMapaTrabHemoCD4Controller.class.getSimpleName().equalsIgnoreCase(mapaSelecionado.getReport())) {
					emiteRelMapaTrabHemoCD4Controller(mapaSelecionado, DominioTipoImpressaoMapa.R);
				} else if (RelMapaTrabalhoHemoController.class.getSimpleName().equalsIgnoreCase(mapaSelecionado.getReport())) {
					emiteRelatorioMapaTrabalhoHemocultura(mapaSelecionado, DominioTipoImpressaoMapa.R, "AELR_MAPA_HEMO");
				} else if (RelMapaTrabalhoHemaController.class.getSimpleName().equalsIgnoreCase(mapaSelecionado.getReport())) {
					emiteRelatorioMapaTrabalhoHematologia(mapaSelecionado, DominioTipoImpressaoMapa.I);
				}
			} catch (SistemaImpressaoException e) {
				LOG.error(e.getMessage(),e);
				this.apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getMessage(),e);
				this.apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
			} finally {
				limpar();
			}
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		}
	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	private void emiteRelMapaTrabHemoCD4Controller(final AelConfigMapa mapa, final DominioTipoImpressaoMapa tipoImpressao) throws SistemaImpressaoException, ApplicationBusinessException {
		final RelMapaTrabHemoCD4Controller controller = this.relMapaTrabHemoCD4Controller;
		controller.setMapa(mapa);
		controller.setNroMapa(nroMapa);
		controller.setDataMapa(dataMapa);
		controller.setTipoImpressao(tipoImpressao);
		controller.directPrint();
	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	private void emiteRelMapaTrabBioEquController(final AelConfigMapa mapa, final DominioTipoImpressaoMapa tipoImpressao) throws SistemaImpressaoException, ApplicationBusinessException {
		final RelMapaTrabBioEquController controller = this.relMapaTrabBioEquController;
		controller.setMapa(mapa);
		controller.setNroMapa(nroMapa);
		controller.setDataMapa(dataMapa);
		controller.setTipoImpressao(tipoImpressao);
		controller.directPrint();
	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	private void emiteRelatorioMapaTrabalhoBioquimica(final AelConfigMapa mapa, final DominioTipoImpressaoMapa tipoImpressao, final String nmRelatorio) throws SistemaImpressaoException, ApplicationBusinessException {
		final RelMapaTrabalhoBioqController controller = this.relMapaTrabalhoBioqController;
		controller.setMapa(mapa);
		controller.setNroMapa(nroMapa);
		controller.setDataMapa(dataMapa);
		controller.setTipoImpressao(tipoImpressao);
		controller.setNmRelatorio(nmRelatorio);
		controller.directPrint();
	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	private void emiteRelatorioMapaTrabalhoEPF(final AelConfigMapa mapa, final DominioTipoImpressaoMapa tipoImpressao) throws SistemaImpressaoException, ApplicationBusinessException {
		final RelMapaTrabalhoEPFController controller = this.relMapaTrabalhoEPFController;
		controller.setMapa(mapa);
		controller.setNroMapa(nroMapa);
		controller.setDataMapa(dataMapa);
		controller.setTipoImpressao(tipoImpressao);
		controller.directPrint();
	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	private void emiteRelatorioMapaTrabalhoUrocultura(final AelConfigMapa mapa, final DominioTipoImpressaoMapa tipoImpressao) throws SistemaImpressaoException, ApplicationBusinessException {
		final RelMapaTrabalhoUroController controller = this.relMapaTrabalhoUroController;
		controller.setMapa(mapa);
		controller.setNroMapa(nroMapa);
		controller.setDataMapa(dataMapa);
		controller.setTipoImpressao(tipoImpressao);
		controller.directPrint();
	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	private void emiteRelatorioMapaTrabalhoHemocultura(final AelConfigMapa mapa, final DominioTipoImpressaoMapa tipoImpressao, final String nmRelatorio) throws SistemaImpressaoException, ApplicationBusinessException {
		final RelMapaTrabalhoHemoController controller = this.relMapaTrabalhoHemoController;
		controller.setMapa(mapa);
		controller.setNroMapa(nroMapa);
		controller.setDataMapa(dataMapa);
		controller.setTipoImpressao(tipoImpressao);
		controller.setNmRelatorio(nmRelatorio);
		controller.directPrint();
	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	private void emiteRelatorioMapaTrabalhoHematologia(final AelConfigMapa mapa, final DominioTipoImpressaoMapa tipoImpressao) throws SistemaImpressaoException, ApplicationBusinessException {
		final RelMapaTrabalhoHemaController controller = this.relMapaTrabalhoHemaController;
		controller.setMapa(mapa);
		controller.setNroMapa(nroMapa);
		controller.setDataMapa(dataMapa);
		controller.setTipoImpressao(tipoImpressao);
		controller.directPrint();
	}

	/*
	 * Getters e Setters
	 */

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(final AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public AelUnidExecUsuario getUsuarioUnidadeExecutora() {
		return usuarioUnidadeExecutora;
	}

	public void setUsuarioUnidadeExecutora(AelUnidExecUsuario usuarioUnidadeExecutora) {
		this.usuarioUnidadeExecutora = usuarioUnidadeExecutora;
	}

	public String getMapa() {
		return mapa;
	}

	public void setMapa(String mapa) {
		this.mapa = mapa;
	}

	public DominoOrigemMapaAmostraItemExame getOrigem() {
		return origem;
	}

	public void setOrigem(DominoOrigemMapaAmostraItemExame origem) {
		this.origem = origem;
	}

	public Boolean getAllChecked() {
		return allChecked;
	}

	public void checkAll() {
		seqsMap = new ArrayList<Short>();

		if (allChecked) {
			seqsMap.addAll(allSeqsMapBanco);
		}
	}

	public void setAllChecked(Boolean allChecked) {
		this.allChecked = allChecked;
	}

	public List<Short> getSeqsMap() {
		return seqsMap;
	}

	public void setSeqsMap(List<Short> seqsMap) {
		this.seqsMap = seqsMap;
	}

	public Short getSeqMapaSelecionado() {
		return seqMapaSelecionado;
	}

	public void setSeqMapaSelecionado(Short seqMapaSelecionado) {
		this.seqMapaSelecionado = seqMapaSelecionado;
	}

	public List<Short> getAllSeqsMapBanco() {
		return allSeqsMapBanco;
	}

	public void setAllSeqsMapBanco(List<Short> allSeqsMapBanco) {
		this.allSeqsMapBanco = allSeqsMapBanco;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public DominioTipoImpressaoMapa getTipoImpressao() {
		return tipoImpressao;
	}

	public void setTipoImpressao(DominioTipoImpressaoMapa tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}

	public Boolean getFecharModal() {
		return fecharModal;
	}

	public void setFecharModal(Boolean fecharModal) {
		this.fecharModal = fecharModal;
	}

	public String getTituloModal() {
		return tituloModal;
	}

	public void setTituloModal(String tituloModal) {
		this.tituloModal = tituloModal;
	}

	public AelConfigMapa getMapaSelecionado() {
		return mapaSelecionado;
	}

	public void setMapaSelecionado(AelConfigMapa mapaSelecionado) {
		this.mapaSelecionado = mapaSelecionado;
	}

	public Date getDataMapa() {
		return dataMapa;
	}

	public void setDataMapa(Date dataMapa) {
		this.dataMapa = dataMapa;
	}

	public Integer getNroMapa() {
		return nroMapa;
	}

	public void setNroMapa(Integer nroMapa) {
		this.nroMapa = nroMapa;
	}

	public boolean isExibeModal() {
		return exibeModal;
	}

	public void setExibeModal(boolean exibeModal) {
		this.exibeModal = exibeModal;
	}

	public boolean isDirectPrint() {
		return directPrint;
	}

	public void setDirectPrint(boolean directPrint) {
		this.directPrint = directPrint;
	}

	public DynamicDataModel<AelConfigMapa> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelConfigMapa> dataModel) {
		this.dataModel = dataModel;
	}
}
