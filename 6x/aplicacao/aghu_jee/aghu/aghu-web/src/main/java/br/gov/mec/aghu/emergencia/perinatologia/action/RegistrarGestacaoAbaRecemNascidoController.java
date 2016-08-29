package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.dominio.DominioEventoLogImpressao;
import br.gov.mec.aghu.dominio.DominioEventoNotaAdicional;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.ServidorIdVO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.farmacia.vo.ViaAdministracaoVO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.perinatologia.vo.MedicamentoRecemNascidoVO;
import br.gov.mec.aghu.perinatologia.vo.RecemNascidoFilterVO;
import br.gov.mec.aghu.perinatologia.vo.RecemNascidoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.service.ServiceException;

public class RegistrarGestacaoAbaRecemNascidoController extends ActionController {

	private static final long serialVersionUID = -1712613170642413556L;
	
	private static final String REDIRECIONA_ESCORE_APGAR = "preencherEscoreApgar";
	private static final String NAME_ABA_ORIGEM = "abaRecemNascido";
	private static final Integer ABA_ORIGEM = 5;
	
	private static final String PAC_CODIGO_EQ = "pacCodigo=";

	@EJB
	private IEmergenciaFacade emergenciaFacade;	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	@Inject
	RegistrarGestacaoController registrarGestacaoController;
	@Inject
	private NotaAdicionalController notaAdicionalController;
	
	private RecemNascidoVO recemNascidoVO;
	private RecemNascidoVO recemNascidoVOselecionado;
	private List<RecemNascidoVO> listRecemNascidoVOs;	
	private Boolean modoEdicao;
	private Boolean modoSelecao;
	private Boolean modoInclusao;	
	/**
	 * Controle de edição de itens
	 */	
	private Boolean alteracaoItem = Boolean.FALSE;
	private Boolean inclusaoItem = Boolean.FALSE;
	private Boolean excluiuItem = Boolean.FALSE;	
	private boolean modalDadosPendentes;
	
	// Dados recebidos da #36508
	private Integer pacCodigo;
	// Gestacao Seqp - gsoSeqp
	private Short seqp;
	private String nomePaciente;
	private String idadeFormatada;
	private Integer prontuario;
	private Integer numeroConsulta;
	// Dados recebidos da #36508

	
	private String voltarPara;
	private Integer atendimentoSeq;
	private Integer internacaoSeq;
	 //#28358
	private ServidorIdVO servidorIdVO;
	private Integer atdSeqRecemNascido;
	private Short serVinCodigo;
	private Integer serMatricula;
	 //#41973
	private Boolean alteracaoMedicamentos = Boolean.FALSE;
	private MedicamentoRecemNascidoVO medicamentoSB;
	private MedicamentoRecemNascidoVO medicamentoSelecionado;
	private ViaAdministracaoVO administracaoVO;
	private List<MedicamentoRecemNascidoVO> medicamentos = new ArrayList<MedicamentoRecemNascidoVO>();
	private Boolean modoEdicaoMedicamento = Boolean.FALSE;
	private Boolean camposDesabilitados = Boolean.TRUE;
	private Integer indexMedicamento;
	private Boolean modalAltMedicamento = Boolean.FALSE;
	
	private boolean modalAutenticacaoSolicitarExames;
	
	public void inicio() {
		try {
			carregarAbaRecemNascido();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void carregarAbaRecemNascido() throws BaseException {
		this.setAtendimentoSeq(
				emergenciaFacade.obterAtendimentoSeqPorNumeroDaConsulta(this.numeroConsulta)
		);
		obterInformacoesRecemNascidoVO();
		definirModoAberturarTela();
		carregarMedicamentos();
	}
	
	private void obterInformacoesRecemNascidoVO() throws BaseException {
		RecemNascidoFilterVO filter = new RecemNascidoFilterVO();
		filter.setNomeGestante(nomePaciente);
		filter.setPacCodigo(pacCodigo);
		filter.setSeqp(seqp);
		listRecemNascidoVOs = emergenciaFacade.listarRecemNascidoVOs(filter);
	}

	private void definirModoAberturarTela() {
		if(listRecemNascidoVOs.isEmpty()){
			abrirTelaModoInclusao();
		} else {
			abrirTelaModoSelecao();
		}
	}

	private void abrirTelaModoInclusao() {
		recemNascidoVO = obterNovoRecemNascido();	
		modoInclusao = Boolean.TRUE;
		modoEdicao = Boolean.FALSE;
		modoSelecao = Boolean.FALSE;
	}
	
	private void abrirTelaModoSelecao() {
		selecionarPrimeiroRecemNascidoDaLista();
		modoSelecao = Boolean.TRUE; 
		modoInclusao = Boolean.FALSE;
		modoEdicao = Boolean.FALSE;
	}
	
	private void abrirTelaModoEdicao() {
		recemNascidoVO = recemNascidoVOselecionado;
		modoEdicao = Boolean.TRUE;
		modoSelecao = Boolean.FALSE; 
		modoInclusao = Boolean.FALSE;
	}

	private void selecionarPrimeiroRecemNascidoDaLista() {
		recemNascidoVO = listRecemNascidoVOs.get(0);
	}

	private RecemNascidoVO obterNovoRecemNascido() {
		RecemNascidoVO vo = new RecemNascidoVO();
		vo.setPacCodigo(pacCodigo);
		vo.setSeqp(seqp);
		vo.setNomePaciente(nomePaciente);
		return vo;
	}

	public void adicionarRecemNascido(){
		try {
			emergenciaFacade.adicionarRecemNascido(recemNascidoVO, listRecemNascidoVOs.size());
			listRecemNascidoVOs.add(recemNascidoVO);
			recemNascidoVOselecionado = recemNascidoVO;
			abrirTelaModoEdicao();
			inclusaoItem = Boolean.TRUE;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private RecemNascidoVO obterRecemNascidoVO(RecemNascidoVO recemNascidoVO){
		int index = listRecemNascidoVOs.indexOf(recemNascidoVO);
		return listRecemNascidoVOs.get(index);
		
	}
	public void editarRecemNascido(){
		try {
			RecemNascidoVO recemNascidoOriginal =  obterRecemNascidoVO(recemNascidoVOselecionado);
			emergenciaFacade.validarRecemNascido(recemNascidoVOselecionado);
			alteracaoItem = emergenciaFacade.validarAlteracaoRecebemNascido(recemNascidoVOselecionado, recemNascidoOriginal);
			abrirTelaModoSelecao();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void excluirRecemNascido() {
		try {
			emergenciaFacade.excluirRecemNascido(recemNascidoVO, listRecemNascidoVOs);
			excluiuItem = Boolean.TRUE;
			apresentarMsgNegocio(Severity.INFO, "MSG_REG_REC_NASC_EXCLUSAO_SUCESSO", recemNascidoVOselecionado);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public boolean isAlgumaAlteracao() {
		return alteracaoItem || inclusaoItem || excluiuItem || alteracaoMedicamentos;
	}
	
	public String descartarAlteracoes() {
		limpar();
		if (registrarGestacaoController.getAbaDestino().equals("abaRecemNascido")) {
			registrarGestacaoController.setAbaSelecionada(ABA_ORIGEM);
			return getVoltarPara();
		} else {
			registrarGestacaoController.preparaAbaDestino();
			return null;
		}
	}

	private void limpar() {
		recemNascidoVO = null;
		recemNascidoVOselecionado = null;
		listRecemNascidoVOs = null;
		modoEdicao = null; 
		modoSelecao = null;
		modoInclusao = null;
		pacCodigo = null;
		seqp = null;
		nomePaciente = null;
		idadeFormatada = null;
		prontuario = null;
		numeroConsulta = null;
		alteracaoItem = Boolean.FALSE;
		inclusaoItem = Boolean.FALSE;
		excluiuItem  = Boolean.FALSE;
		modalDadosPendentes = false;
		alteracaoMedicamentos = false;
		cancelarMedicamento();
	}
			
	private void apresentarMsgNegocio(Severity severity, String msgKey, RecemNascidoVO vo ){
		apresentarMsgNegocio(severity, msgKey, vo.getNome());
	}
	
	public boolean temRegistroConselhoValido() {
		boolean returnValue = false;
		try {
			RapServidores servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(super.obterLoginUsuarioLogado());
			returnValue = this.usuarioTemPermissaoParaImprimirRelatoriosDefinitivos(servidor.getId().getMatricula(), servidor.getId().getVinCodigo());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return returnValue;
	}
	
	private boolean usuarioTemPermissaoParaImprimirRelatoriosDefinitivos(Integer matricula, Short vinculo) throws ApplicationBusinessException {
		String strParam = (String) parametroFacade.obterAghParametroPorNome("P_AGHU_CONSELHOS_PERINATOLOGIA_NASCIMENTO", "vlrTexto");
		StringTokenizer token = new StringTokenizer(strParam, ",");
		List<String> lista = new LinkedList<>();
		while (token.hasMoreTokens()) {
			lista.add(token.nextToken());
		}
		String[] arraySiglas = lista.toArray(new String[lista.size()]);
		return registroColaboradorFacade.usuarioTemPermissaoParaImprimirRelatoriosDefinitivos(matricula, vinculo, arraySiglas);
	}
	
	public void cancelarRecemNascido(){
		definirModoAberturarTela();
	}
	
	public void prepararEdicaoRecemNascido(){
		abrirTelaModoEdicao();
		carregarMedicamentos();
	}
	
	public void prepararInclusaoRecemNascido(){
		abrirTelaModoInclusao();
		carregarMedicamentos();
	}
	
	public String voltar(){
		if(isAlgumaAlteracao()){
			modalDadosPendentes = Boolean.TRUE;
			return "";
		} else if (registrarGestacaoController.getAbaDestino().equals("abaRecemNascido")) {
			return getVoltarPara();
		} else{
			registrarGestacaoController.preparaAbaDestino();
			return null;
		}
	}

	public String escoreApgar() {
		return REDIRECIONA_ESCORE_APGAR;
	}
	
	public void relatorioAtendRecemNasc() {
		String atendimentoSeq = this.atendimentoSeq != null ? this.atendimentoSeq.toString() : "";
		String internacaoSeq = this.internacaoSeq != null ? this.internacaoSeq.toString() : "";
		
		String jsExecute = "parent.tab.addNewTab('redirect_recemnascido_#{id}', "
				+ "'Imp Atend RN Prev', "
				+ "'/aghu/pages/paciente/prontuarioonline/relatorioAtendimentoRecemNascidoPdf.xhtml?"
				+ PAC_CODIGO_EQ + this.pacCodigo.toString() + ";"
				+ "conNumero=" + this.numeroConsulta.toString() + ";"
				+ "gsoSeqp=" + this.seqp.toString() + ";"
				+ "atdSeq=" + atendimentoSeq + ";"
				+ "rnaSeqp=" + this.recemNascidoVO.getSeqpPK().toString() + ";"
				+ "seqInternacao=" + internacaoSeq + ";"
				+ "tipoInternacao=INTERNACAO;"
				+ "origem=/aghu/pages/perinatologia/registrarGestacaoAbaRecemNascido.xhtml;"
				+ "voltarEmergencia=true;"
				+ "previa=true', null, 1, true)";
		
		RequestContext.getCurrentInstance().execute(jsExecute);
	}
	
	public void laudoAih() {
		String jsExecute = "parent.tab.addNewTab('redirect_laih_#{id}', "
				+ "'Laudo AIH', "
				+ "'/aghu/pages/blococirurgico/laudoAIH.xhtml?"
				+ PAC_CODIGO_EQ + this.recemNascidoVO.getPacCodigoRecemNascido().toString() + ";"
				+ "iniciaPesquisando=true;"
				+ "voltarParaEmergencia=true', null, 1, true)";
		
		RequestContext.getCurrentInstance().execute(jsExecute);
	}
	
	/**
	 * Quando usuario tem registro no conselho, conforme verificado pelo metodo temRegistroConselhoValido
	 * 
	 */
	public void imprimirAtendimentoRecemNascidoComRegistro() {
		try {
			this.verificarGravidezGCO();
			
			String atendimentoSeq = this.atendimentoSeq != null ? this.atendimentoSeq.toString() : "";
			String internacaoSeq = this.internacaoSeq != null ? this.internacaoSeq.toString() : "";
			
			String jsExecutaImprimeAtdRecemNascidoComRegistro = "parent.tab.addNewTab('redirect_recemnascido_#{id}', "
					+ "'Imp Atend RN Def', "
					+ "'/aghu/pages/paciente/prontuarioonline/relatorioAtendimentoRecemNascidoPdf.xhtml?"
					+ PAC_CODIGO_EQ + this.pacCodigo.toString() + ";"
					+ "conNumero=" + this.numeroConsulta.toString() + ";"
					+ "gsoSeqp=" + this.seqp.toString() + ";"
					+ "atdSeq=" + atendimentoSeq + ";"
					+ "rnaSeqp=" + this.recemNascidoVO.getSeqpPK().toString() + ";"
					+ "seqInternacao=" + internacaoSeq + ";"
					+ "tipoInternacao=INTERNACAO;"
					+ "origem=/aghu/pages/perinatologia/registrarGestacaoAbaRecemNascido.xhtml;"
					+ "voltarEmergencia=true;"
					+ "previa=false', null, 1, true)";
			
			RequestContext.getCurrentInstance().execute(jsExecutaImprimeAtdRecemNascidoComRegistro);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Quando usuario NAO tem registro no conselho, conforme verificado pelo metodo temRegistroConselhoValido
	 * este metodo serah chamado apos a autenticacao feita pelo modal de autenticacao desenvolvida pela arquitetura.
	 * @throws ApplicationBusinessException 
	 * 
	 */
	public void imprimirAtendimentoRecemNascidoSemRegistro() {
		if (this.servidorIdVO != null && this.servidorIdVO.getMatricula() != null) {
			boolean temPermissao;
			try {
				temPermissao = this.usuarioTemPermissaoParaImprimirRelatoriosDefinitivos(servidorIdVO.getMatricula(), servidorIdVO.getSerVinCodigo());
				if (!temPermissao) {
					apresentarMsgNegocio("Usuário " + this.servidorIdVO.getMatricula() + " - " + this.servidorIdVO.getSerVinCodigo() + "não tem permissão para imprimir relatorio definitivo.");
					return;
				}
				this.verificarGravidezGCO();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		} else {
			apresentarMsgNegocio("Autenticação falhou!!!");
		}
	}
	
	/**
	 * Verifica se a gravidez esta confirmada.
	 * @throws ApplicationBusinessException 
	 * 
	 */
	private void verificarGravidezGCO() throws ApplicationBusinessException {
		boolean confirmada = this.emergenciaFacade.temGravidezConfirmada(this.pacCodigo, this.seqp);
		if (!confirmada) {
			apresentarMsgNegocio("MCO-00746");
		} else {
			RapServidores servidor;
			servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(super.obterLoginUsuarioLogado());
			Integer matricula = servidor.getId().getMatricula();
			Short vinCodigo = servidor.getId().getVinCodigo();
			
			if (this.servidorIdVO != null && this.servidorIdVO.getMatricula() != null) {
				matricula = this.servidorIdVO.getMatricula();
				vinCodigo = this.servidorIdVO.getSerVinCodigo();
			}
			emergenciaFacade.inserirLogImpressoes(this.pacCodigo, seqp, this.recemNascidoVO.getSeqpPK().intValue()
				, this.numeroConsulta, DominioEventoLogImpressao.MCOR_CONSULTA_OBS.toString()
				, matricula, vinCodigo, new Date());
		}
	}
	
	 //#28358 ON02 e ON04
	public boolean habilitarBotaoSolicitarExames() {
		if (recemNascidoVO == null) {
			return false;
		} else {
			return emergenciaFacade.
				verificarExisteRegistroRecemNascido(recemNascidoVO)
				&& emergenciaFacade.moduloAgendamentoExameAtivo();
		}
	}
	
	 //#28358  RN04
	public void solicitarExame() {
		try {
			emergenciaFacade.validarConsultaFinalizada(this.getPacCodigo(),
					this.getSeqp(), this.getNumeroConsulta(),
					DominioEventoLogImpressao.MCOR_CONSULTA_OBS);
			buscarSeqAtdRecemNascido();
			if(atdSeqRecemNascido == null) {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ATENDIMENTO_RECEM_NASCIDO");
				return;
			}     
			solicitacaoExameFacade.verificarPermissoesParaSolicitarExame(atdSeqRecemNascido);
			this.modalAutenticacaoSolicitarExames = true;
			openDialog("modalAutenticacaoSolicitarExamesAbaRecemNascidoWG");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	 //#28358 Serviço #43626
	private void buscarSeqAtdRecemNascido() {
		if(atdSeqRecemNascido == null) {
		Integer atdSeq = emergenciaFacade
				.obterUltimoAtdSeqRecemNascidoPorPacCodigo
				(recemNascidoVO.getPacCodigoRecemNascido());
		setAtdSeqRecemNascido(atdSeq);
		}
	}
	
	// #28358
	public void verificarPermissaoSolicitarExamesRN() {
		if (this.servidorIdVO != null && this.servidorIdVO.getMatricula() != null) {
			try {
				emergenciaFacade.verificarPermissaoParaSolicitarExames(servidorIdVO.getMatricula(), servidorIdVO.getSerVinCodigo());
			} catch (ApplicationBusinessException e) {
				apresentarMsgNegocio(Severity.ERROR, "MSG_SEM_PERMISSAO_RN", this.servidorIdVO.getMatricula(), this.servidorIdVO.getSerVinCodigo());
				return;
			} catch (ServiceException e) {
				apresentarMsgNegocio(e.getMessage());
			}
			setSerVinCodigo(servidorIdVO.getSerVinCodigo());
			setSerMatricula(servidorIdVO.getMatricula());
			
			String jsExecute = "parent.tab.addNewTab('tab_da_recem_nascido', 'Recém Nascido',"
					+ "'/aghu/pages/exames/solicitacao/solicitacaoExameCRUD.xhtml?"
					+ "pacCodigoGestacao=" + this.pacCodigo.toString() + ";"
					+ "atendimento=" + this.atdSeqRecemNascido.toString() + ";"
					+ "matricula=" + this.serMatricula.toString() + ";"
					+ "vinCodigo=" + this.serVinCodigo.toString() + ";"
					+ "voltarEmergencia=true;"
					+ "voltarPara=/pages/perinatologia/registrarGestacao.xhtml;"
					+ "abaOrigem=" + this.getAbaOrigem() + ";"
					+ PAC_CODIGO_EQ + this.recemNascidoVO.getPacCodigoRecemNascido().toString() + ";"
					+ "seqp=" + this.seqp.toString() + ";"
					+ "numeroConsulta=" + this.numeroConsulta.toString() + ";"
					+ "paramCid=#{javax.enterprise.context.conversation.id}" + ";"
					+ "', '', '', 'false')";
			
			RequestContext.getCurrentInstance().execute(jsExecute);
			
			this.modalAutenticacaoSolicitarExames = false;
			
		} else {
			apresentarMsgNegocio(Severity.ERROR, "MSG_FALHA_AUTENTICAR_RN");
		}
	}

	//#41973
	public void habilitarCamposComplementares() {
		camposDesabilitados = false;
	}
	
	//#41973
	public void desalitarCamposComplementares() {
		camposDesabilitados = true;
		administracaoVO = null;
		medicamentoSB = null;
	}
	
	//#41973
	public void adicionarMedicamento() {
		try {
			if(!isCamposObrigatorioNull()){
				adicionarMedicamentoNaLista();
				setAlteracaoMedicamentos(Boolean.TRUE);
				cancelarMedicamento();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	//#41973
	private void adicionarMedicamentoNaLista()
			throws ApplicationBusinessException {
		MedicamentoRecemNascidoVO item = setDadosMedicamento(medicamentoSB, false);
		emergenciaFacade.validarMedicamento(item, medicamentos);
		medicamentos.add(item);
		getRecemNascidoVO().getSeqMedicamentosInsercao().add(item.getSeqPni());
	}
	
	//#41973
	public void alterarMedicamento() {
		if(!isCamposObrigatorioNull()) { 
			atualizarMedicamentoNaLista();
			setAlteracaoMedicamentos(Boolean.TRUE);	
			cancelarMedicamento();
		}
	}
	
	//#41973
	private void atualizarMedicamentoNaLista() {
		MedicamentoRecemNascidoVO medicamentoAtualizado = setDadosMedicamento(medicamentoSelecionado, true);
		medicamentos.set(indexMedicamento, medicamentoAtualizado);
	}
	
	//#41973
	public void editarMedicamento() {
		modoEdicaoMedicamento  = true;
		medicamentoSelecionado = medicamentos.get(indexMedicamento);
		medicamentoSB = medicamentoSelecionado;
		administracaoVO = obterViaAdministracaoVO(medicamentoSelecionado.getVadSigla());
		camposDesabilitados = false;
	}
	
	//#41973
	private ViaAdministracaoVO obterViaAdministracaoVO(String sigla) {
		List<ViaAdministracaoVO> lista = pesquisarViasAdministracao(sigla);
		if(lista == null || lista.isEmpty()) {
			return null;
		}
		return lista.get(0);
	}
	
	//#41973
	private MedicamentoRecemNascidoVO setDadosMedicamento(MedicamentoRecemNascidoVO item, boolean edicao) {
		if(edicao) {
			item = medicamentoSB;
		}
 		if(item != null) {
			item.setVadSigla(getAdministracaoVO().getSigla());
			item.setRnaGsoPacCodigo(getRecemNascidoVO().getGsoPacCodigoPK());
			item.setRnaGsoSeqp(getRecemNascidoVO().getGsoSeqpPK());
			item.setRnaSeqp(getRecemNascidoVO().getSeqpPK());
		}
		return item;
	}
	
	//#28249
	public boolean habilitarBotaoNotasAdicionais(){
		try {
			return emergenciaFacade.habilitarBotaoNotasAdicionais(
					getRecemNascidoVO().getGsoPacCodigoPK(),
					getRecemNascidoVO().getGsoSeqpPK(),
					getRecemNascidoVO().getSeqpPK(),
					this.numeroConsulta);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return false;
		}
	}
	
	//#41973
	public void excluirMedicamento() {
		medicamentoSelecionado = medicamentos.get(indexMedicamento);
		if (emergenciaFacade.isMedicamentoRecemNascidoCadastrado(
				getRecemNascidoVO().getGsoPacCodigoPK(),
				getRecemNascidoVO().getGsoSeqpPK(),
				getRecemNascidoVO().getSeqpPK(),
				medicamentoSelecionado.getSeqPni())) {
			getRecemNascidoVO().getSeqMedicamentosExclusao().add(medicamentoSelecionado.getSeqPni());
		}
		medicamentos.remove(medicamentoSelecionado);
		setAlteracaoMedicamentos(Boolean.TRUE);
	}
	
	//#41973
	public void cancelarMedicamento() {
		setAdministracaoVO(null);
		setMedicamentoSB(null);
		medicamentoSelecionado = null;
		modoEdicaoMedicamento = false;
		camposDesabilitados = true;
		indexMedicamento = null;
		modalAltMedicamento = Boolean.FALSE;
	}
	
	//#41973
	public void carregarMedicamentos() {
		cancelarMedicamento();
		medicamentos = emergenciaFacade.buscarMedicamentosPorRecemNascido(
				getRecemNascidoVO().getGsoPacCodigoPK(), 
				getRecemNascidoVO().getGsoSeqpPK(), 
				getRecemNascidoVO().getSeqpPK());
		if(!medicamentos.isEmpty()) {
			medicamentoSelecionado = medicamentos.get(0);
		}
	}
	
	//#41973
	private boolean isCamposObrigatorioNull() {
		boolean retorno = false;
		if (medicamentoSB == null) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_MED_OBRIGATORIO");
			retorno = true;
			if(camposDesabilitados) {
				return retorno;
			}
		}
		if(administracaoVO == null) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_VIA_ADM_OBRIGATORIO");
			retorno = true;
		}
		return retorno;
	}
	
	public void preparaModalNotaAdicional() {
		notaAdicionalController.setNotaAdicional(null);
		notaAdicionalController.setConNumero(this.numeroConsulta);
		notaAdicionalController.setPacCodigo(this.pacCodigo);
		notaAdicionalController.setGsoSeqp(this.seqp);
		notaAdicionalController.setEvento(DominioEventoNotaAdicional.MCOR_RN_SL_PARTO);
	}
	
	//#41973
	public void gravarAlteracaoMedicamentoRecemNascido() {
		carregarMedicamentos();
		alteracaoMedicamentos = Boolean.FALSE;
		//TODO chamar gravar #41564
	}
	
	//#41973
	public void cancelarAlteracaoMedicamentoRecemNascido () {
		modalAltMedicamento = Boolean.FALSE;
		alteracaoMedicamentos = Boolean.FALSE;
	}
	
	//#41973
	public void verificarAlteracaoMedicamentos() {
		if(this.alteracaoMedicamentos) {
			modalAltMedicamento = Boolean.TRUE;
		}
	}
	
	//#41973
	public List<MedicamentoRecemNascidoVO> pesquisarMedicamentos(String param) {
		return this.returnSGWithCount(emergenciaFacade.buscarMedicamentosPorDescricao(param),pesquisarMedicamentosCount(param));
	}
	
	//#41973
	public Long pesquisarMedicamentosCount(String param) {
		return emergenciaFacade.buscarMedicamentosCountPorDescricao(param);
	}
	
	//#41973
	public List<ViaAdministracaoVO> pesquisarViasAdministracao(String param) {
		return this.returnSGWithCount(emergenciaFacade.pesquisarViaAdminstracaoSiglaouDescricao(param),pesquisarViasAdministracaoCount(param));
	}
	
	//#41973
	public Long pesquisarViasAdministracaoCount(String param) {
		return emergenciaFacade.pesquisarViaAdminstracaoSiglaouDescricaoCount(param);
	}

	// getsetter
	public RecemNascidoVO getRecemNascidoVO() {
		return recemNascidoVO;
	}

	public void setRecemNascidoVO(RecemNascidoVO recemNascidoVO) {
		this.recemNascidoVO = recemNascidoVO;
	}

	public RecemNascidoVO getRecemNascidoVOselecionado() {
		return recemNascidoVOselecionado;
	}

	public void setRecemNascidoVOselecionado(
			RecemNascidoVO recemNascidoVOselecionado) {
		this.recemNascidoVOselecionado = recemNascidoVOselecionado;
	}

	public List<RecemNascidoVO> getListRecemNascidoVOs() {
		return listRecemNascidoVOs;
	}

	public void setListRecemNascidoVOs(List<RecemNascidoVO> listRecemNascidoVOs) {
		this.listRecemNascidoVOs = listRecemNascidoVOs;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}
	
	public Boolean getModoSelecao() {
		return modoSelecao;
	}

	public void setModoSelecao(Boolean modoSelecao) {
		this.modoSelecao = modoSelecao;
	}

	public Boolean getModoInclusao() {
		return modoInclusao;
	}

	public void setModoInclusao(Boolean modoInclusao) {
		this.modoInclusao = modoInclusao;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getIdadeFormatada() {
		return idadeFormatada;
	}

	public void setIdadeFormatada(String idadeFormatada) {
		this.idadeFormatada = idadeFormatada;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getAtendimentoSeq() {
		return atendimentoSeq;
	}

	public void setAtendimentoSeq(Integer atendimentoSeq) {
		this.atendimentoSeq = atendimentoSeq;
	}

	public Integer getInternacaoSeq() {
		return internacaoSeq;
	}

	public void setInternacaoSeq(Integer internacaoSeq) {
		this.internacaoSeq = internacaoSeq;
	}

	public ServidorIdVO getServidorIdVO() {
		return servidorIdVO;
	}

	public void setServidorIdVO(ServidorIdVO servidorIdVO) {
		this.servidorIdVO = servidorIdVO;
	}

	public Boolean getAlteracaoItem() {
		return alteracaoItem;
	}

	public void setAlteracaoItem(Boolean alteracaoItem) {
		this.alteracaoItem = alteracaoItem;
	}

	public Boolean getInclusaoItem() {
		return inclusaoItem;
	}

	public void setInclusaoItem(Boolean inclusaoItem) {
		this.inclusaoItem = inclusaoItem;
	}

	public Boolean getExcluiuItem() {
		return excluiuItem;
	}

	public void setExcluiuItem(Boolean excluiuItem) {
		this.excluiuItem = excluiuItem;
	}

	public boolean isModalDadosPendentes() {
		return modalDadosPendentes;
	}

	public void setModalDadosPendentes(boolean modalDadosPendentes) {
		this.modalDadosPendentes = modalDadosPendentes;
	}
	
	public Integer getAtdSeqRecemNascido() {
		return atdSeqRecemNascido;
	}

	public void setAtdSeqRecemNascido(Integer atdSeqRecemNascido) {
		this.atdSeqRecemNascido = atdSeqRecemNascido;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	
	public String getAbaOrigem() {
		return NAME_ABA_ORIGEM;
	}

	public Boolean getAlteracaoMedicamentos() {
		return alteracaoMedicamentos;
	}

	public void setAlteracaoMedicamentos(Boolean alteracaoMedicamentos) {
		this.alteracaoMedicamentos = alteracaoMedicamentos;
	}

	public MedicamentoRecemNascidoVO getMedicamentoSB() {
		return medicamentoSB;
	}

	public void setMedicamentoSB(MedicamentoRecemNascidoVO medicamentoSB) {
		this.medicamentoSB = medicamentoSB;
	}

	public MedicamentoRecemNascidoVO getMedicamentoSelecionado() {
		return medicamentoSelecionado;
	}

	public void setMedicamentoSelecionado(MedicamentoRecemNascidoVO medicamentoSelecionado) {
		this.medicamentoSelecionado = medicamentoSelecionado;
	}

	public ViaAdministracaoVO getAdministracaoVO() {
		return administracaoVO;
	}

	public void setAdministracaoVO(ViaAdministracaoVO administracaoVO) {
		this.administracaoVO = administracaoVO;
	}

	public List<MedicamentoRecemNascidoVO> getMedicamentos() {
		return medicamentos;
	}

	public void setMedicamentos(List<MedicamentoRecemNascidoVO> medicamentos) {
		this.medicamentos = medicamentos;
	}

	public Boolean getModoEdicaoMedicamento() {
		return modoEdicaoMedicamento;
	}

	public void setModoEdicaoMedicamento(Boolean modoEdicaoMedicamento) {
		this.modoEdicaoMedicamento = modoEdicaoMedicamento;
	}

	public Boolean getCamposDesabilitados() {
		return camposDesabilitados;
	}

	public void setCamposDesabilitados(Boolean camposDesabilitados) {
		this.camposDesabilitados = camposDesabilitados;
	}

	public Integer getIndexMedicamento() {
		return indexMedicamento;
	}

	public void setIndexMedicamento(Integer indexMedicamento) {
		this.indexMedicamento = indexMedicamento;
	}

	public Boolean getModalAltMedicamento() {
		return modalAltMedicamento;
	}

	public void setModalAltMedicamento(Boolean modalAltMedicamento) {
		this.modalAltMedicamento = modalAltMedicamento;
	}

	public boolean isModalAutenticacaoSolicitarExames() {
		return modalAutenticacaoSolicitarExames;
	}

	public void setModalAutenticacaoSolicitarExames(
			boolean modalAutenticacaoSolicitarExames) {
		this.modalAutenticacaoSolicitarExames = modalAutenticacaoSolicitarExames;
	}

}