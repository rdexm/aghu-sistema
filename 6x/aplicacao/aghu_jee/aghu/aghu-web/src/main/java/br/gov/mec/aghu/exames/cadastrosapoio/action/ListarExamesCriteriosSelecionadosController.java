package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesBeanFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.vo.ExamesCriteriosSelecionadosVO;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ListarExamesCriteriosSelecionadosController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<ExamesCriteriosSelecionadosVO> dataModel;

	private static final Log LOG = LogFactory.getLog(ListarExamesCriteriosSelecionadosController.class);

	private static final String PAGE_PACIENTE_PESQUISA_PACIENTE_COMPONENTE = "paciente-pesquisaPacienteComponente";

	private static final long serialVersionUID = -4492878138386467536L;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IExamesBeanFacade examesBeanFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	private String voltarPara;

	// Campos filtro
	private AghUnidadesFuncionais aelUnfExecutaExames;
	private AelSolicitacaoExames aelSolicitacaoExames;
	private AelSitItemSolicitacoes aelSitItemSolicitacoes;
	private FatConvenioSaude fatConvenioSaude;
	private String codigoPaciente;
	private Integer prontuario;
	private String nomePaciente;
	private AelUnidExecUsuario usuarioUnidadeExecutora;
	private List<ExamesCriteriosSelecionadosVO> listaVO;

	private Integer pacCodigoFonetica;

	private Integer numeroSolicitacao;

	/**
	 * Chamado no inicio de "cada conversacao"
	 */
	public void iniciar() {
	 

		// Obtem o USUARIO da unidade executora
		try {
			this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
		} catch (ApplicationBusinessException e) {
			this.usuarioUnidadeExecutora = null;
		}
		if (this.usuarioUnidadeExecutora != null && this.usuarioUnidadeExecutora.getUnfSeq() != null) {
			// Resgata a unidade executora associada ao usuario
			this.aelUnfExecutaExames = this.usuarioUnidadeExecutora.getUnfSeq();
		}

		if (numeroSolicitacao != null && numeroSolicitacao != 0) {
			List<AelSolicitacaoExames> solicitacao = examesFacade.buscarAelSolicitacaoExames(numeroSolicitacao.toString());
			aelSolicitacaoExames = solicitacao.get(0);
			pesquisar();
		}

		if (pacCodigoFonetica != null) {
			AipPacientes pac = pacienteFacade.obterPacientePorCodigo(pacCodigoFonetica);
			prontuario = pac.getProntuario();
			nomePaciente = pac.getNome();
			codigoPaciente = pac.getCodigo().toString();
		}

		// TODO revisar
		// if(getFirstResult() == 0 && this.ativo){
		// pesquisar();
		// }else if(getFirstResult() != 0 && this.ativo){
		// Integer first = getFirstResult();
		// pesquisar();
		// setFirstResult(first);
		// }
	
	}

	/**
	 * Persiste identificacao da unidade executora atraves do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora() {
		try {
			// Persiste identificacao da unidade executora do usuario
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.aelUnfExecutaExames);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		if (numeroSolicitacao == null && aelSitItemSolicitacoes == null && fatConvenioSaude == null && (codigoPaciente == null || "".equals(codigoPaciente)) && prontuario == null
				&& (nomePaciente == null || "".equals(nomePaciente))) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_LISTAR_EMXAES_CRITERIOS_ALGUM_FILTRO_PREENCHIDO");
		} else {
			persistirIdentificacaoUnidadeExecutora();
            this.obterAelItemSolicitacaoExame();			
			this.dataModel.reiniciarPaginator();
		}
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		// Limpa filtro
		// aelUnfExecutaExames = new AghUnidadesFuncionais();
		aelSolicitacaoExames = null;
		aelSitItemSolicitacoes = null;
		fatConvenioSaude = null;
		codigoPaciente = null;
		prontuario = null;
		nomePaciente = null;
		pacCodigoFonetica = null;
		numeroSolicitacao = null;
		// prontuario = null;

		// Apaga resultados da exibição
		this.dataModel.limparPesquisa();
	}

	public void receber(ExamesCriteriosSelecionadosVO vo) {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {

			AelItemSolicitacaoExames itemSolicitacao = this.examesFacade.buscaItemSolicitacaoExamePorId(vo.getAelItemSolicitacaoExames().getId().getSoeSeq(), vo.getAelItemSolicitacaoExames().getId()
					.getSeqp());

			examesBeanFacade.receberItemSolicitacaoExame(itemSolicitacao, nomeMicrocomputador);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_RECEBER_IIEM_SOLICITACAO");

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	public void voltar(ExamesCriteriosSelecionadosVO vo) {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {

			AelItemSolicitacaoExames itemSolicitacao = this.examesFacade.buscaItemSolicitacaoExamePorId(vo.getAelItemSolicitacaoExames().getId().getSoeSeq(), vo.getAelItemSolicitacaoExames().getId()
					.getSeqp());

			examesBeanFacade.voltarItemSolicitacaoExame(itemSolicitacao, nomeMicrocomputador);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_VOLTAR_IIEM_SOLICITACAO");

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	public Boolean exibeReceber(ExamesCriteriosSelecionadosVO vo) {
		AelItemSolicitacaoExames item = vo.getAelItemSolicitacaoExames();
		if (item.getSituacaoItemSolicitacao().getCodigo().equals("AX") || item.getSituacaoItemSolicitacao().getCodigo().equals("AG") || item.getSituacaoItemSolicitacao().getCodigo().equals("CA")) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public Boolean exibeVoltar(ExamesCriteriosSelecionadosVO vo) {
		AelItemSolicitacaoExames item = vo.getAelItemSolicitacaoExames();
		if (!item.getSituacaoItemSolicitacao().getCodigo().equals("AX") && !item.getSituacaoItemSolicitacao().getCodigo().equals("AG") && !item.getSituacaoItemSolicitacao().getCodigo().equals("CA")) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public String cancelar() {
		String retorno = this.voltarPara;

		this.voltarPara = null;
		this.aelUnfExecutaExames = null;
		this.aelSolicitacaoExames = null;
		this.aelSitItemSolicitacoes = null;
		this.fatConvenioSaude = null;
		this.codigoPaciente = null;
		this.prontuario = null;
		this.nomePaciente = null;
		this.usuarioUnidadeExecutora = null;
		this.listaVO = null;
		this.pacCodigoFonetica = null;
		this.numeroSolicitacao = null;

		return retorno;
	}

	@Override
	public List<ExamesCriteriosSelecionadosVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

//		if (aelSolicitacaoExames == null) {
//			this.apresentarMsgNegocio(Severity.ERROR, "AEL_00424");
//			listaVO = new ArrayList<ExamesCriteriosSelecionadosVO>();
//			return listaVO;
//		}
		
		listaVO = null;

		listaVO = solicitacaoExameFacade.pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionados(firstResult, maxResult, AelItemSolicitacaoExames.Fields.DESC_MATERIAL_ANALISE.toString(), true,
				aelUnfExecutaExames, aelSolicitacaoExames, aelSitItemSolicitacoes, fatConvenioSaude, codigoPaciente, prontuario, nomePaciente);

		return listaVO;
	}

	@Override
	public Long recuperarCount() {
		return solicitacaoExameFacade.pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionadosCount(aelUnfExecutaExames, aelSolicitacaoExames, aelSitItemSolicitacoes, fatConvenioSaude,
				codigoPaciente, prontuario, nomePaciente);
	}

	public void obterAelItemSolicitacaoExame() {
		if (numeroSolicitacao != null) {
			aelSolicitacaoExames = examesFacade.obterAelSolicitacaoExamesPeloId(numeroSolicitacao);
		} else { 
			aelSolicitacaoExames = null;
		}
	}

	public String redirecionarPesquisaFonetica() {
		return PAGE_PACIENTE_PESQUISA_PACIENTE_COMPONENTE;
	}

	public boolean verificarPermissaoImpressaoPulseira(ExamesCriteriosSelecionadosVO vo) {

		boolean temAtendimento = vo.getAelItemSolicitacaoExames().getSolicitacaoExame().getAtendimento() != null;
		boolean temProntuario = false;

		String prontuario = vo.getProntuario();
		if (StringUtils.isNumeric(prontuario)) {
			temProntuario = Long.valueOf(prontuario) > 0;
		} else {
			temProntuario = StringUtils.isNotBlank(prontuario);
		}

		// _item.aelItemSolicitacaoExames.solicitacaoExame.atendimento != null and _item.prontuario != 0
		return temAtendimento && temProntuario;
	}

	/**
	 * Metodo para pesquisa na suggestion box de unidade executora
	 */
	public List<AghUnidadesFuncionais> obterAghUnidadesFuncionaisExecutoras(String objPesquisa) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricao(objPesquisa);
	}

	/**
	 * Metodo para limpeza da suggestion box de unidade executora
	 */
	public void limparAghUnidadesFuncionaisExecutoras() {
		this.aelUnfExecutaExames = null;
	}

	public List<AelSitItemSolicitacoes> buscarListaAelSitItemSolicitacoesPorParametro(String valor) {
		return examesFacade.buscarListaAelSitItemSolicitacoesPorParametro((String) valor);
	}

	public List<FatConvenioSaude> pesquisarConveniosSaudePorCodigoOuDescricaoAtivos(String valor) {
		return faturamentoApoioFacade.pesquisarConveniosSaudePorCodigoOuDescricaoAtivos((String) valor);
	}

	public AghUnidadesFuncionais getAelUnfExecutaExames() {
		return aelUnfExecutaExames;
	}

	public void setAelUnfExecutaExames(AghUnidadesFuncionais aelUnfExecutaExames) {
		this.aelUnfExecutaExames = aelUnfExecutaExames;
	}

	public AelSolicitacaoExames getAelSolicitacaoExames() {
		return aelSolicitacaoExames;
	}

	public void setAelSolicitacaoExames(AelSolicitacaoExames aelSolicitacaoExames) {
		this.aelSolicitacaoExames = aelSolicitacaoExames;
	}

	public AelSitItemSolicitacoes getAelSitItemSolicitacoes() {
		return aelSitItemSolicitacoes;
	}

	public void setAelSitItemSolicitacoes(AelSitItemSolicitacoes aelSitItemSolicitacoes) {
		this.aelSitItemSolicitacoes = aelSitItemSolicitacoes;
	}

	public FatConvenioSaude getFatConvenioSaude() {
		return fatConvenioSaude;
	}

	public void setFatConvenioSaude(FatConvenioSaude fatConvenioSaude) {
		this.fatConvenioSaude = fatConvenioSaude;
	}

	public String getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(String codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public AelUnidExecUsuario getUsuarioUnidadeExecutora() {
		return usuarioUnidadeExecutora;
	}

	public void setUsuarioUnidadeExecutora(AelUnidExecUsuario usuarioUnidadeExecutora) {
		this.usuarioUnidadeExecutora = usuarioUnidadeExecutora;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public Integer getNumeroSolicitacao() {
		return numeroSolicitacao;
	}

	public void setNumeroSolicitacao(Integer numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}

	public DynamicDataModel<ExamesCriteriosSelecionadosVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ExamesCriteriosSelecionadosVO> dataModel) {
		this.dataModel = dataModel;
	}
}