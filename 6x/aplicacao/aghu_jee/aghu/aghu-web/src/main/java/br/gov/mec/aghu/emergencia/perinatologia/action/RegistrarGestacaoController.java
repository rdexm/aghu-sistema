package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import br.gov.mec.aghu.dominio.DominioGravidez;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.ServidorIdVO;
import br.gov.mec.aghu.paciente.vo.DadosSanguineos;
import br.gov.mec.aghu.perinatologia.vo.DadosGestacaoVO;
import br.gov.mec.aghu.perinatologia.vo.ExameResultados;
import br.gov.mec.aghu.perinatologia.vo.ResultadoExameSignificativoPerinatologiaVO;
import br.gov.mec.aghu.perinatologia.vo.UnidadeExamesSignificativoPerinatologiaVO;
import br.gov.mec.controller.AutenticacaoController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.service.ServiceException;

public class RegistrarGestacaoController extends ActionController {

	public static final String NAME_ABA_GESTACAO_ATUAL = "abaGestacaoAtual", NAME_ABA_INTERCORRENCIAS = "abaIntercorrencias", NAME_ABA_NASCIMENTO = "abaNascimento", NAME_ABA_TRABALHO_PARTO = "abaTrabalhoParto", NAME_ABA_CONSULTA_CO = "abaConsultaCO", NAME_ABA_EX_FISICO_RN = "abaExFisicoRN", NAME_ABA_RECEM_NASCIDO = "abaRecemNascido", NAME_ABA_PUERPERIO = "abaPuerperio";
	public static final Integer ABA_GESTACAO_ATUAL = 0, ABA_INTERCORRENCIAS = 1, ABA_CONSULTA_CO = 2, ABA_TRABALHO_PARTO = 3, ABA_NASCIMENTO = 4, ABA_RECEM_NASCIDO = 5, ABA_EX_FISICO_RN = 6, ABA_PUERPERIO = 7; 
	private static final String EXECUTAR = "executar";
	private static final String NOVA_DATA = "modalNovaDataWG";
	private static final long serialVersionUID = -1712613170642413556L;
	private static final String REDIRECIONA_PESQUISAR_GESTACOES = "/pages/perinatologia/pesquisaGestacoesList.xhtml";
	private static final String TELA_ORIGEM = "/pages/perinatologia/registrarGestacao.xhtml";
	private static final String TELA_ORIGEM_ABA_EM_ATENDIMENTO = "emergencia-pacientesEmergenciaAbaEmAtendimento"; 
	private static final String TELA_ORIGEM_ABA_ATENDIDOS = "emergencia-pacientesEmergenciaAbaAtendidos";
	@EJB 
	private IEmergenciaFacade emergenciaFacade;
	@Inject
	private RegistrarConsultaCOController registrarConsultaCOController;
	@Inject
	private RegistrarGestacaoAbaRecemNascidoController registrarGestacaoAbaRecemNascidoController;
	@Inject
	private PesquisaGestacaoController pesquisaGestacaoController;
	@Inject
	private AutenticacaoController autenticacaoController;
	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}

	private Integer pacCodigo;
	private Short seqp;
	private Integer numeroConsulta;
	private String nomePaciente;
	private String idadeFormatada;
	private Integer prontuario;
	private String justificativa;
	private Integer abaSelecionada = 0;
	private Boolean mostraModalGravar;
	private boolean habilitarBtConsultaExames;
	private boolean atualizar;
	private DadosGestacaoVO dadosGestacaoVO;
	private DadosGestacaoVO dadosGestacaoVOOriginal;
	private Integer abaDestino;
	private String voltarPara;
	@Inject
	private RegistrarGestacaoAbaTrabalhoPartoController registrarGestacaoAbaTrabalhoPartoController;
	@Inject
	private RegistrarGestacaoAbaNascimentoController registrarGestacaoAbaNascimentoController;
	@Inject RegistrarGestacaoAbaExtFisicoRNController registrarGestacaoAbaExtFisicoRNController;

	private boolean permManterGestacoes;
	private boolean permProntuarioOnline;
	private boolean permConsultarExames;
	private boolean permAcessoExamesPOLBasico;
	private boolean permInformarCargaExame;
	private UnidadeExamesSignificativoPerinatologiaVO exameSelecionado;
	private ResultadoExameSignificativoPerinatologiaVO resultadoSelecionado;
	private List<ExameResultados> examesResultados = new ArrayList<ExameResultados>();
	private List<ResultadoExameSignificativoPerinatologiaVO> resultadosExamesExcluidos = new ArrayList<ResultadoExameSignificativoPerinatologiaVO>();
	private List<Date> datasExames = new ArrayList<Date>();	
	private Date novaData;
	private boolean alterarData;
	private Date dataSelecionada;
	private boolean mostraModalExcluirData;
	private boolean alterouExames;
	private boolean permVisualizarAcolhimento;
	private boolean permRealizarAcolhimento;
	private Long trgSeq;
	private Boolean retornoTelaPrescricao; 
	private boolean habilitarSolicitarExames = false;
	private ServidorIdVO servidorIdVO;
	private Integer matricula;
	private Short vinCodigo;
	private Integer atdSeq;
	private boolean exibeModalAutenticacao;
	private Boolean temPermissaoManter;
	private boolean adicionouExame = false;
		
	@PostConstruct
	public void init() {
		begin(conversation, true);
		carregarPermissoes();
		prepararRedirecionamentos();
	}
	
	private void carregarPermissoes() {
		String usuarioLogado = super.obterLoginUsuarioLogado();
		this.permManterGestacoes = getPermissionService().usuarioTemPermissao(usuarioLogado, "manterGestacoes", EXECUTAR);
		this.permProntuarioOnline = getPermissionService().usuarioTemPermissao(usuarioLogado, "prontuarioOnline", "pesquisar");
		this.permConsultarExames = getPermissionService().usuarioTemPermissao(usuarioLogado, "consultarExames", "acessar");
		this.permAcessoExamesPOLBasico = getPermissionService().usuarioTemPermissao(usuarioLogado, "acessoExamesPOLBasico", "acessar");
		this.permInformarCargaExame = getPermissionService().usuarioTemPermissao(usuarioLogado, "informarCargaExame", EXECUTAR);
		if (this.permProntuarioOnline || this.permConsultarExames || this.permAcessoExamesPOLBasico) {
			this.habilitarBtConsultaExames = true;
		}
		this.setAtualizar(true);
		//#28365
		this.habilitarSolicitarExames = verificaSeModuloEstaAtivo("agendamentoExames");
		// #36386
		this.permVisualizarAcolhimento = getPermissionService().usuarioTemPermissao(usuarioLogado, "visualizarAcolhimento", "visualizar");
		// #36387
		this.permRealizarAcolhimento = getPermissionService().usuarioTemPermissao(usuarioLogado, "realizarAcolhimento", EXECUTAR);
	}
	
	private String verificaAbaSelecionada(Integer abaSelecionada) {
		switch (abaSelecionada) {
		case 0:
			return NAME_ABA_GESTACAO_ATUAL;
		case 1:
			return NAME_ABA_INTERCORRENCIAS;
		case 2:
			return NAME_ABA_CONSULTA_CO;
		case 3:
			return NAME_ABA_TRABALHO_PARTO;
		case 4:
			return NAME_ABA_NASCIMENTO;
		case 5:
			return NAME_ABA_RECEM_NASCIDO;
		case 6:
			return NAME_ABA_EX_FISICO_RN;
		case 7:
			return NAME_ABA_PUERPERIO;
		}
		return null;
	}
	
	private void prepararRedirecionamentos() {		
		
		setParameters();
		// #28133
		String abaDestinoParm = verificaAbaSelecionada(this.abaSelecionada);
		if (StringUtils.isNotBlank(abaDestinoParm) && abaDestinoParm.equalsIgnoreCase(NAME_ABA_CONSULTA_CO)) {
		
			this.abaDestino = ABA_CONSULTA_CO;
			
			String retornoTelaPrescricaoParm = getRequestParameter("retornoTelaPrescricao");
			if (StringUtils.isNotBlank(retornoTelaPrescricaoParm)) {
				this.setRetornoTelaPrescricao(Boolean.valueOf(retornoTelaPrescricaoParm));
			}
			preparaAbaDestino();
		} 
		// Redireciona Aba Trabalho Parto
		else if(StringUtils.isNotBlank(abaDestinoParm) && abaDestinoParm.equalsIgnoreCase(NAME_ABA_TRABALHO_PARTO)){
			this.abaDestino = ABA_TRABALHO_PARTO;
			String pacCodigoParm = getRequestParameter("pacCodigo");
			if (StringUtils.isNotBlank(pacCodigoParm)) {
				this.pacCodigo = Integer.valueOf(pacCodigoParm);
			}
			String seqpParm = getRequestParameter("seqp");
			if (StringUtils.isNotBlank(seqpParm)) {
				this.seqp = Short.valueOf(seqpParm);
			}
			String numeroConsultaParm = getRequestParameter("numeroConsulta");
			if (StringUtils.isNotBlank(numeroConsultaParm)) {
				this.numeroConsulta = Integer.valueOf(numeroConsultaParm);
			}
			redirecionarAbaTrabalhoParto();
		}
		// Redireciona Aba Nascimento
		else if(StringUtils.isNotBlank(abaDestinoParm) && abaDestinoParm.equalsIgnoreCase(NAME_ABA_NASCIMENTO)){
			String pacCodigoParm = getRequestParameter("pacCodigo");
			if (StringUtils.isNotBlank(pacCodigoParm)) {
				this.pacCodigo = Integer.valueOf(pacCodigoParm);
			}
			String seqpParm = getRequestParameter("seqp");
			if (StringUtils.isNotBlank(seqpParm)) {
				this.seqp = Short.valueOf(seqpParm);
			}
			String numeroConsultaParm = getRequestParameter("numeroConsulta");
			if (StringUtils.isNotBlank(numeroConsultaParm)) {
				this.numeroConsulta = Integer.valueOf(numeroConsultaParm);
			}
			this.redirecionarAbaNascimento();
		}
		
		else if(StringUtils.isNotBlank(abaDestinoParm) && abaDestinoParm.equalsIgnoreCase(NAME_ABA_EX_FISICO_RN)){
			redicionarAbaExtFisicoRN();
		} else if(StringUtils.isNotBlank(abaDestinoParm) && abaDestinoParm.equalsIgnoreCase(NAME_ABA_RECEM_NASCIDO)){
			abaDestino = ABA_RECEM_NASCIDO;
			redirecionarAbaRecemNascido();
			setAbaSelecionada(abaDestino);
		}
	}

	private void setParameters() {
		String pacCodigoGestacao = getRequestParameter("pacCodigoGestacao");
		String pacCodigoParm = getRequestParameter("pacCodigo");		
		if (StringUtils.isNotBlank(pacCodigoGestacao)) {
			this.pacCodigo = Integer.valueOf(pacCodigoGestacao);
		} else if (StringUtils.isNotBlank(pacCodigoParm)) {
			this.pacCodigo = Integer.valueOf(pacCodigoParm);
		}
		
		String seqpParm = getRequestParameter("seqp");
		if (StringUtils.isNotBlank(seqpParm)) {
			this.seqp = Short.valueOf(seqpParm);
		}
		
		String numeroConsultaParm = getRequestParameter("numeroConsulta");
		if (StringUtils.isNotBlank(numeroConsultaParm)) {
			this.numeroConsulta = Integer.valueOf(numeroConsultaParm);
		}
		
		String prontuarioParm = getRequestParameter("prontuario");
		if (StringUtils.isNotBlank(prontuarioParm)) {
			this.prontuario = Integer.valueOf(prontuarioParm);
		}
		
		String nomeParm = getRequestParameter("nome");
		if (StringUtils.isNotBlank(nomeParm)) {
			this.nomePaciente = nomeParm;
		}
		
		String idadeFormatadaParm = getRequestParameter("idade");
		if (StringUtils.isNotBlank(idadeFormatadaParm)) {
			this.idadeFormatada = idadeFormatadaParm;
		}
	}

	private boolean verificaSeModuloEstaAtivo(String nome) {
		boolean ativo = false;
		try{
			ativo = emergenciaFacade.verificarSeModuloEstaAtivo(nome);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return ativo;		
	}
	
	public void inicio() {
		temPermissaoManter = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterGestacoes", EXECUTAR);
		setMostraModalGravar(Boolean.FALSE);
		setJustificativa(null);
		this.registrarGestacaoAbaNascimentoController.setMostraModalSolicitarVdrl(Boolean.FALSE);
		// RN01
		if (this.seqp != null) {
			try {
				this.dadosGestacaoVO = this.emergenciaFacade.pesquisarMcoGestacaoPorId(this.pacCodigo, this.seqp, this.numeroConsulta);
			
				this.dadosGestacaoVO.setInformacoesPaciente(this.emergenciaFacade.preencherInformacoesPaciente(this.nomePaciente, this.idadeFormatada, this.prontuario, this.numeroConsulta));

				DadosSanguineos dadosSanguineos = this.emergenciaFacade
						.obterRegSanguineosPorCodigoPaciente(this.pacCodigo, this.seqp.byteValue());
				if (dadosSanguineos != null) {
					this.dadosGestacaoVO.setTipoSanguineoMae(dadosSanguineos.getGrupoSanguineo());
					this.dadosGestacaoVO.setFatorRHMae(dadosSanguineos.getFatorRh());
					this.dadosGestacaoVO.setCoombs(dadosSanguineos.getCoombs());
				}
			} catch (ApplicationBusinessException e) {
				this.dadosGestacaoVO.setTipoSanguineoMae(null);
				this.dadosGestacaoVO.setFatorRHMae(null);
				this.dadosGestacaoVO.setCoombs(null);
				apresentarExcecaoNegocio(e);
			}
			atualizarDadosGestacaoVOOriginal();
		} else {
			registrarConsultaCOController.init();
			this.dadosGestacaoVO = new DadosGestacaoVO();
			this.dadosGestacaoVO.setGravidez(DominioGravidez.GCO);
			this.dadosGestacaoVO.setInformacoesPaciente(this.emergenciaFacade
					.preencherInformacoesPaciente(this.nomePaciente, this.idadeFormatada, this.prontuario, this.numeroConsulta));
			atualizarDadosGestacaoVOOriginal();
		}
		if(abaDestino != null){
			preparaAbaDestino();
		}
		this.carregarExamesResultados();
	}

	@SuppressWarnings("unchecked")
	public void carregarExamesResultados() {
		try {
			Object[] examesResult = emergenciaFacade.carregarExames(this.pacCodigo, this.seqp);
			this.examesResultados = (List<ExameResultados>) examesResult[0];
			this.datasExames = (List<Date>) examesResult[1];
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public Integer obterTamanhoPrimeiraColuna() {
		int tamanho = 120;
		for (int i = 0; i < examesResultados.size(); i++) {
			if (StringUtils.isNotBlank(examesResultados.get(i).getExame().getDescricao())) {
				int auxTamanho = (int) (examesResultados.get(i).getExame().getDescricao().length() * 7.5);
				if(auxTamanho > tamanho){
					tamanho = auxTamanho;
				}
			}			
		}		
		return tamanho;
	}
	
	public void informarNovaDataExame(){
		this.alterarData = false;
		openDialog(NOVA_DATA);
	}
	
	public void alterarDataExame(Date data){
		this.dataSelecionada = data;
		this.novaData = data;
		this.alterarData = true;
		openDialog(NOVA_DATA);
	}
	
	public void confirmarNovaDataExame(){
		if(this.alterarData){
			if(!this.datasExames.contains(novaData) && !novaData.equals(dataSelecionada)){
				int indice = datasExames.indexOf(dataSelecionada);
				if(indice >= 0){
					datasExames.set(indice, novaData);
					for (int i = 0; i < examesResultados.size(); i++) {
						ExameResultados exame = examesResultados.get(i);
						exame.getResultados()[indice].setDataRealizacao(novaData);						
					}
					this.alterouExames = true;
				}
				this.cancelarNovaDataExame();
			} else if(!novaData.equals(dataSelecionada)){
				this.cancelarNovaDataExame();
				super.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_DATA_JA_EXISTENTE");
			}
		} else {
			if(!this.datasExames.contains(novaData)){
				datasExames.add(novaData);
				for (int i = 0; i < examesResultados.size(); i++) {
					ExameResultados exame = examesResultados.get(i);
					ResultadoExameSignificativoPerinatologiaVO[] results = Arrays.copyOf(exame.getResultados(), exame.getResultados().length+1);
					results[results.length - 1] = new ResultadoExameSignificativoPerinatologiaVO(this.pacCodigo, this.seqp, null, novaData,
							exame.getExame().getEmaExaSigla(), exame.getExame().getEmaManSeq(), exame.getExame().getEexSeq(), exame
									.getExame().getDescricao());
					exame.setResultados(results);
				}
				this.alterouExames = true;
				this.cancelarNovaDataExame();
			} else {
				this.cancelarNovaDataExame();
				super.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_DATA_JA_EXISTENTE");
			}
		}
		closeDialog(NOVA_DATA);
	}
	
	public void cancelarNovaDataExame(){
		this.novaData = null;
		this.alterarData = false;
		this.dataSelecionada = null;
		closeDialog("modalNovaDataWG");
	}
	
	public void excluirDataExame(Date data){
		this.dataSelecionada = data;
		openDialog("modalExcluirDataWG");
	}
	
	public void confirmarExcluirDataExame(){
		int indice = datasExames.indexOf(dataSelecionada);
		if(indice >= 0){
			datasExames.remove(indice);			
			for (int i = 0; i < examesResultados.size(); i++) {
				ExameResultados exame = examesResultados.get(i);
				ResultadoExameSignificativoPerinatologiaVO[] results = new ResultadoExameSignificativoPerinatologiaVO[exame.getResultados().length-1];
				int excluiu = 0;
				for (int j = 0; j < exame.getResultados().length; j++) {
					ResultadoExameSignificativoPerinatologiaVO resultadoExameSignificativoPerinatologiaVO = exame.getResultados()[j];
					if(j != indice){
						results[j-excluiu] = resultadoExameSignificativoPerinatologiaVO;
					} else {
						excluiu++;
						if(resultadoExameSignificativoPerinatologiaVO.getSeqp() != null){
							resultadosExamesExcluidos.add(resultadoExameSignificativoPerinatologiaVO);
						}
					}
				}				
				exame.setResultados(results);
			}	
			this.alterouExames = true;
		}
		this.limparExcluirDataExame();
	}
	
	public void limparExcluirDataExame(){
		closeDialog("modalExcluirDataWG");
		this.dataSelecionada = null;
	}
	
	public void atualizarDadosGestacaoVOOriginal() {
		this.dadosGestacaoVOOriginal = new DadosGestacaoVO();
		try {
			PropertyUtils.copyProperties(this.dadosGestacaoVOOriginal, this.dadosGestacaoVO);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Não foi possivel fazer a cópia dos objetos da classe DadosGestacaoVO.", e);
		}
	}
	
	public void validarParto() {
		try {
			this.emergenciaFacade.validarParto(this.dadosGestacaoVO, this.dadosGestacaoVOOriginal, this.pacCodigo, this.seqp);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void validarEcoSemanasDias(Boolean isSemanas) {
		try {
			this.emergenciaFacade.validarEcoSemanasDias(this.dadosGestacaoVO, isSemanas, this.atualizar, true);
			this.setAtualizar(false);
			calcularDtProvavelParto();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void calcularDtProvavelParto() {
		this.emergenciaFacade.calcularDtProvavelParto(this.dadosGestacaoVO);
	}
	
	public String confirmaVoltar() {
		if (this.alterouExames || emergenciaFacade.isMcoGestacoesAlterada(this.dadosGestacaoVO, this.dadosGestacaoVOOriginal)) {
			setMostraModalGravar(Boolean.TRUE);
			return null;
		}
		if(registrarGestacaoAbaTrabalhoPartoController.validarAlteracoes()){
			setMostraModalGravar(Boolean.TRUE);
			return null;
		}
		if(registrarGestacaoAbaNascimentoController.getHouveAlteracao()){
			openDialog("modalConfirmacaoGravarDadosNascimentoWG");
			return null;
		}
		return this.voltar();
	}
	
	public String voltar() {
		setAlterouExames(false);
		setMostraModalGravar(Boolean.FALSE);
		if(registrarGestacaoAbaTrabalhoPartoController.validarAlteracoes()){
			registrarGestacaoAbaTrabalhoPartoController.fecharModalPendencias();
		}
		
		if (TELA_ORIGEM_ABA_EM_ATENDIMENTO.equals(voltarPara) || TELA_ORIGEM_ABA_ATENDIDOS.equals(voltarPara)) {
			this.pesquisaGestacaoController.setNroProntuario(prontuario);
			this.pesquisaGestacaoController.setNroConsulta(numeroConsulta);
			this.pesquisaGestacaoController.setVoltarPara(voltarPara);
			this.pesquisaGestacaoController.setManterGestacao(true);
		}
		
		//this.pesquisaGestacaoController.carregarDadosPaciente();
		this.pesquisaGestacaoController.pesquisar();
		return REDIRECIONA_PESQUISAR_GESTACOES;
	}
	
	public void gravarJustificativa() {
		if (this.justificativa == null) {
			this.apresentarMsgNegocio("justificativa", Severity.ERROR, "CAMPO_OBRIGATORIO", "Justificativa");
			return;
		}
		this.dadosGestacaoVO.setJustificativa(this.justificativa);
		closeDialog("modalJustificativaWG");
		this.gravar();
	}
	
	public void cancelarGravarJustificativa() {
		this.dadosGestacaoVO.setJustificativa(null);
		setJustificativa(null);
	}

	public void preGravar() {
		if (this.dadosGestacaoVO.getGestacao() == null) {
			this.apresentarMsgNegocio("gestacao", Severity.ERROR, "CAMPO_OBRIGATORIO", "Gestação");
			return;
		}
		try {
			boolean exibirModalJustificativa = this.emergenciaFacade.preGravar(this.dadosGestacaoVO, this.dadosGestacaoVOOriginal, 
					this.pacCodigo, this.seqp);
			
			if (exibirModalJustificativa) {
				openDialog("modalJustificativaWG");
				return;
			}
			if(ABA_TRABALHO_PARTO.equals(abaSelecionada) && registrarGestacaoAbaTrabalhoPartoController.validarAlteracoes()){
				registrarGestacaoAbaTrabalhoPartoController.gravarTrabalhoPartoSemMsgSucesso();
				if(registrarGestacaoAbaTrabalhoPartoController.isMantemAba()){
					setMostraModalGravar(Boolean.FALSE);
					return;
				}else {
					this.gravar();
				}
			}
				
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
		this.gravar();
				
	}
	
	public void gravar() {
		try {
			
			this.emergenciaFacade.gravar(this.dadosGestacaoVO, this.dadosGestacaoVOOriginal, this.pacCodigo, this.seqp,
					this.examesResultados, this.resultadosExamesExcluidos);
			setAlterouExames(false);
			atualizarDadosGestacaoVOOriginal();
			if (getMostraModalGravar()) {
				this.voltar();
			}			
			this.adicionouExame = false;
			if(this.seqp==null){
				this.setSeqp(this.dadosGestacaoVOOriginal.getSeqp());
				this.inicio();
			}
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_GESTACAO");			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void prepararTelaAcolhimento() {
		try {
		   this.trgSeq = this.emergenciaFacade.obterTrgSeqParaAcolhimento(this.numeroConsulta);
		   
		   final String jsExecutaVisualizarAcolhimento = "parent.tab.addNewTab('redirect_#{id}', 'Acolhimento', "
					+ "'/aghu/pages/emergencia/realizarAcolhimentoPacienteCRUD.xhtml?tudoDesabilitado=true;"
					+ "trgSeq=" + this.trgSeq.toString() + ";"
					+ "pacCodigo=" + this.pacCodigo.toString() + "', null, 1, true)";
		   
		   RequestContext.getCurrentInstance().execute(jsExecutaVisualizarAcolhimento);
		   
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}		
	}

	public void prepararTela(Integer pacCodigo, Short seqp, Integer numeroConsulta, String nomePaciente, String idadeFormatada,
			Integer prontuario) {
		this.pacCodigo = pacCodigo;
		this.seqp = seqp;
		this.numeroConsulta = numeroConsulta;
		this.nomePaciente = nomePaciente;
		this.idadeFormatada = idadeFormatada;
		this.prontuario = prontuario;
	}
	
	public void verificarAlteracoesPendentes() {
		verificarAlteracoesPendentes(true);
	}

	public void verificarAlteracoesPendentes(final boolean verificarMesmaAba) {
		if(verificarMesmaAba && ABA_NASCIMENTO.equals(this.abaSelecionada)){
			this.redirecionarAbaNascimento();
			return;
		}
		if(this.getAbaDestino().equals(ABA_GESTACAO_ATUAL) && this.temPermissaoManter){
			if(emergenciaFacade.isMcoGestacoesAlterada(this.dadosGestacaoVO, this.dadosGestacaoVOOriginal ) || this.alterouExames || this.adicionouExame){
				this.preGravar();
			}
		}	
		this.setAbaDestino(this.abaSelecionada);
		if (this.registrarGestacaoAbaNascimentoController.getHouveAlteracao().equals(Boolean.TRUE) || registrarGestacaoAbaNascimentoController.getModoEdicao()) {
			this.setAbaSelecionada(ABA_NASCIMENTO);
			this.registrarGestacaoAbaNascimentoController.setMostraModalGravarDadosNascimento(Boolean.TRUE);	
		} else if (registrarGestacaoAbaExtFisicoRNController.validarAlteracaoItens()) {
			this.setAbaSelecionada(ABA_EX_FISICO_RN);
			registrarGestacaoAbaExtFisicoRNController.setMostraModalGravarExFisicoRN(Boolean.TRUE);
		} else if (registrarGestacaoAbaRecemNascidoController.isAlgumaAlteracao()) {
			this.setAbaSelecionada(ABA_RECEM_NASCIDO);
			registrarGestacaoAbaRecemNascidoController.setModalDadosPendentes(Boolean.TRUE);	
		} else if (registrarGestacaoAbaTrabalhoPartoController.validarAlteracoes()) {
			this.setAbaSelecionada(ABA_TRABALHO_PARTO);
			registrarGestacaoAbaTrabalhoPartoController.setMostrarModalDadosPendentesAbaTrabParto(Boolean.TRUE);	
		}else if(registrarConsultaCOController.verificarAlteracaoPendentes()){
			this.setAbaSelecionada(ABA_CONSULTA_CO);
			registrarConsultaCOController.gravaDadosPendentes();
		}
		else {
			this.preparaAbaDestino();
		}
	}
	
	public void preparaAbaDestino() {
		if (ABA_CONSULTA_CO.equals(this.abaDestino)) {
			this.redirecionarAbaConsultaCO();
			this.setAbaSelecionada(this.abaDestino);
			
		} else if (ABA_TRABALHO_PARTO.equals(this.abaDestino)) {
			this.redirecionarAbaTrabalhoParto();
			this.setAbaSelecionada(this.abaDestino);
		
		} else if (ABA_RECEM_NASCIDO.equals(this.abaDestino)){
			redirecionarAbaRecemNascido();
			setAbaSelecionada(abaDestino);	
		
		} else if (ABA_EX_FISICO_RN.equals(this.abaDestino)) {
			this.redicionarAbaExtFisicoRN();
			this.setAbaSelecionada(this.abaDestino);
			
		} else if (!ABA_NASCIMENTO.equals(this.abaDestino)) {
			this.setAbaSelecionada(this.abaDestino);
			
		} else {
			try {
				this.registrarGestacaoAbaNascimentoController.atualizaDadosNascimento();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}	
	public void descartarAlteracoes() {
		this.registrarGestacaoAbaNascimentoController.setMostraModalGravarDadosNascimento(Boolean.FALSE);
		this.registrarGestacaoAbaNascimentoController.setHouveAlteracao(Boolean.FALSE);

		if(this.registrarGestacaoAbaNascimentoController.getModoEdicao()){
			this.registrarGestacaoAbaNascimentoController.cancelarEdicaoNascimento();
		} else {
			try {
				this.registrarGestacaoAbaNascimentoController.atualizaDadosNascimento();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}

		this.setAbaSelecionada(this.getAbaDestino());
	}	
	public void redirecionarAbaNascimento() {
		this.setAbaDestino(ABA_NASCIMENTO);
		this.registrarGestacaoAbaNascimentoController.setMostraModalSolicitarVdrl(Boolean.FALSE);
		this.registrarGestacaoAbaNascimentoController.prepararTela(this.pacCodigo, this.seqp, this.numeroConsulta);
	}
	public void redirecionarAbaTrabalhoParto(){
		this.registrarGestacaoAbaTrabalhoPartoController.prepararAbaTrabalhoParto(getPacCodigo(), getSeqp(), getNumeroConsulta());
	}	
	public void redirecionarAbaConsultaCO(){
		registrarConsultaCOController.setPacCodigo(pacCodigo);
		registrarConsultaCOController.setSeqp(seqp);
		registrarConsultaCOController.setNumeroConsulta(numeroConsulta);
		registrarConsultaCOController.setProntuario(prontuario);
		registrarConsultaCOController.setNomePaciente(nomePaciente);
		registrarConsultaCOController.setIdadeFormatada(idadeFormatada);
		registrarConsultaCOController.setRetornoTelaPrescricao(retornoTelaPrescricao);
		registrarConsultaCOController.setTrgSeq(trgSeq);
		registrarConsultaCOController.inicio();
	}	
	public void redirecionarAbaRecemNascido(){
		registrarGestacaoAbaRecemNascidoController.setPacCodigo(pacCodigo);
		registrarGestacaoAbaRecemNascidoController.setSeqp(seqp);
		registrarGestacaoAbaRecemNascidoController.setNumeroConsulta(numeroConsulta);
		registrarGestacaoAbaRecemNascidoController.setProntuario(prontuario);
		registrarGestacaoAbaRecemNascidoController.setNomePaciente(nomePaciente);
		registrarGestacaoAbaRecemNascidoController.setIdadeFormatada(idadeFormatada);
		registrarGestacaoAbaRecemNascidoController.setVoltarPara(REDIRECIONA_PESQUISAR_GESTACOES);
		registrarGestacaoAbaRecemNascidoController.inicio();
	}	
	public void redicionarAbaExtFisicoRN(){
		registrarGestacaoAbaExtFisicoRNController.prepararTela(pacCodigo, seqp);
	}	
	public List<UnidadeExamesSignificativoPerinatologiaVO> pesquisarExames(String param) {
		try {
			return  this.returnSGWithCount(emergenciaFacade.pesquisarExames((String)param),pesquisarExamesCount(param));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return new ArrayList<UnidadeExamesSignificativoPerinatologiaVO>();
		}
	}
	public Long pesquisarExamesCount(String param) {
		try {
			return emergenciaFacade.pesquisarExamesCount((String)param);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return 0l;
		}
	}	
	public void adicionarExameSelecionado(){
		if(exameSelecionado != null){
			ExameResultados exameResultado = new ExameResultados(exameSelecionado);
			if(!this.examesResultados.contains(exameResultado)){			 
				ResultadoExameSignificativoPerinatologiaVO[] results = new ResultadoExameSignificativoPerinatologiaVO[datasExames.size()];
				for (int i = 0; i < results.length; i++) {
					results[i] = new ResultadoExameSignificativoPerinatologiaVO(this.pacCodigo, this.seqp, null, datasExames.get(i),
							exameResultado.getExame().getEmaExaSigla(), exameResultado.getExame().getEmaManSeq(), exameResultado.getExame()
									.getEexSeq(), exameResultado.getExame().getDescricao());
				}
				exameResultado.setResultados(results);
				this.adicionouExame = true;
				this.examesResultados.add(exameResultado);
			} else {
				super.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_EXAME_JA_EXISTENTE");
			}
			this.exameSelecionado = null;
		}
	}	
	public void solicitarExames(){
		setExibeModalAutenticacao(true);
		openDialog("modalAutenticacaoSolicitarExamesAbaGestacaoAtualWG");
	}	
	public void verificarPermissaoParaSolicitarExames() throws ServiceException{
		try {
			this.matricula = this.servidorIdVO.getMatricula();
			this.vinCodigo = this.servidorIdVO.getSerVinCodigo();
			emergenciaFacade.verificarPermissaoParaSolicitarExames(this.matricula, this.vinCodigo);
			if(this.numeroConsulta != null){
				this.atdSeq = emergenciaFacade.obterAtendimentoSeqPorNumeroDaConsulta(this.numeroConsulta);
			}				
			final String jsExecutaSolicitarExamesAbaGestacaoAtual = "parent.tab.addNewTab('tab_da_gestacao', 'Gestacao',"
				+ "'/aghu/pages/exames/solicitacao/solicitacaoExameCRUD.xhtml?"
				+ "atendimento=" + this.atdSeq + ";"
				+ "matricula=" + this.matricula + ";"
				+ "vinCodigo=" + this.vinCodigo + ";"
				+ "voltarEmergencia=true;"
				+ "voltarPara=" + this.getTelaOrigem() + ";"
				+ "abaOrigem=" + this.getAbaDestino() + ";"
				+ "pacCodigo=" + this.pacCodigo + ";"
				+ "seqp=" + this.seqp + ";"			
				+ "numeroConsulta=" + this.numeroConsulta + ";"
				+ "usuarioSolicitante=" + this.autenticacaoController.getUsername() + ";"
				+ "paramCid=#{javax.enterprise.context.conversation.id}" + ";"
				+ "', '', '', 'false')";
			
			RequestContext.getCurrentInstance().execute(jsExecutaSolicitarExamesAbaGestacaoAtual); 			
			
			setExibeModalAutenticacao(false);
		} catch (ApplicationBusinessException e) {
			FacesContext.getCurrentInstance().validationFailed();
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void atualizarCampoGestacao(){
		if (Byte.valueOf("1").equals(dadosGestacaoVO.getGestacao())) {
			dadosGestacaoVO.setParto(Byte.valueOf("0"));
			dadosGestacaoVO.setCesariana(Byte.valueOf("0"));
			dadosGestacaoVO.setAborto(Byte.valueOf("0"));
			dadosGestacaoVO.setEctopica(Byte.valueOf("0"));
		}
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

	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
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

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public Integer getAbaSelecionada() {
		return abaSelecionada;
	}

	public void setAbaSelecionada(Integer abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}

	public Boolean getMostraModalGravar() {
		return mostraModalGravar;
	}

	public void setMostraModalGravar(Boolean mostraModalGravar) {
		this.mostraModalGravar = mostraModalGravar;
	}

	public boolean isAtualizar() {
		return atualizar;
	}

	public void setAtualizar(boolean atualizar) {
		this.atualizar = atualizar;
	}

	public DadosGestacaoVO getDadosGestacaoVO() {
		return dadosGestacaoVO;
	}

	public void setDadosGestacaoVO(DadosGestacaoVO dadosGestacaoVO) {
		this.dadosGestacaoVO = dadosGestacaoVO;
	}

	public DadosGestacaoVO getDadosGestacaoVOOriginal() {
		return dadosGestacaoVOOriginal;
	}

	public void setDadosGestacaoVOOriginal(DadosGestacaoVO dadosGestacaoVOOriginal) {
		this.dadosGestacaoVOOriginal = dadosGestacaoVOOriginal;
	}
	
	public Integer getAbaDestino() {
		return abaDestino;
	}

	public void setAbaDestino(Integer abaDestino) {
		this.abaDestino = abaDestino;
	}

	public boolean isPermManterGestacoes() {
		return permManterGestacoes;
	}

	public void setPermManterGestacoes(boolean permManterGestacoes) {
		this.permManterGestacoes = permManterGestacoes;
	}

	public boolean isPermProntuarioOnline() {
		return permProntuarioOnline;
	}

	public void setPermProntuarioOnline(boolean permProntuarioOnline) {
		this.permProntuarioOnline = permProntuarioOnline;
	}

	public boolean isPermConsultarExames() {
		return permConsultarExames;
	}

	public void setPermConsultarExames(boolean permConsultarExames) {
		this.permConsultarExames = permConsultarExames;
	}

	public boolean isPermAcessoExamesPOLBasico() {
		return permAcessoExamesPOLBasico;
	}

	public void setPermAcessoExamesPOLBasico(boolean permAcessoExamesPOLBasico) {
		this.permAcessoExamesPOLBasico = permAcessoExamesPOLBasico;
	}

	public boolean isHabilitarBtConsultaExames() {
		return habilitarBtConsultaExames;
	}

	public void setHabilitarBtConsultaExames(boolean habilitarBtConsultaExames) {
		this.habilitarBtConsultaExames = habilitarBtConsultaExames;
	}	

	public UnidadeExamesSignificativoPerinatologiaVO getExameSelecionado() {
		return exameSelecionado;
	}

	public void setExameSelecionado(UnidadeExamesSignificativoPerinatologiaVO exameSelecionado) {
		this.exameSelecionado = exameSelecionado;
	}

	public boolean isPermInformarCargaExame() {
		return permInformarCargaExame;
	}

	public void setPermInformarCargaExame(boolean permInformarCargaExame) {
		this.permInformarCargaExame = permInformarCargaExame;
	}

	public boolean isPermVisualizarAcolhimento() {
		return permVisualizarAcolhimento;
	}

	public void setPermVisualizarAcolhimento(boolean permVisualizarAcolhimento) {
		this.permVisualizarAcolhimento = permVisualizarAcolhimento;
	}

	public Long getTrgSeq() {
		return trgSeq;
	}

	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}

	public boolean isPermRealizarAcolhimento() {
		return permRealizarAcolhimento;
	}

	public void setPermRealizarAcolhimento(boolean permRealizarAcolhimento) {
		this.permRealizarAcolhimento = permRealizarAcolhimento;
	}

	public List<ExameResultados> getExamesResultados() {
		return examesResultados;
	}

	public void setExamesResultados(List<ExameResultados> examesResultados) {
		this.examesResultados = examesResultados;
	}

	public List<Date> getDatasExames() {
		return datasExames;
	}

	public void setDatasExames(List<Date> datasExames) {
		this.datasExames = datasExames;
	}

	public void setHabilitarSolicitarExames(boolean habilitarSolicitarExames) {
		this.habilitarSolicitarExames = habilitarSolicitarExames;
	}

	public Date getNovaData() {
		return novaData;
	}
	public void setNovaData(Date novaData) {
		this.novaData = novaData;
	}

	public Boolean getRetornoTelaPrescricao() {
		return retornoTelaPrescricao;
	}

	public void setRetornoTelaPrescricao(Boolean retornoTelaPrescricao) {
		this.retornoTelaPrescricao = retornoTelaPrescricao;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public boolean isHabilitarSolicitarExames() { return habilitarSolicitarExames; }	
	public ServidorIdVO getServidorIdVO() { return servidorIdVO; }
	public void setServidorIdVO(ServidorIdVO servidorIdVO) { this.servidorIdVO = servidorIdVO; }
	public Integer getMatricula() { return matricula;}
	public Short getVinCodigo() { return vinCodigo;	}
	public Integer getAtdSeq() { return atdSeq; }
	public ResultadoExameSignificativoPerinatologiaVO getResultadoSelecionado() { return resultadoSelecionado; }
	public void setResultadoSelecionado(ResultadoExameSignificativoPerinatologiaVO resultadoSelecionado) { this.resultadoSelecionado = resultadoSelecionado; }
	public String getTelaOrigem() {return TELA_ORIGEM;}
	public void setAtdSeq(Integer atdSeq) {this.atdSeq = atdSeq;}
	public boolean isExibeModalAutenticacao() {return exibeModalAutenticacao;}
	public void setExibeModalAutenticacao(boolean exibeModalAutenticacao) {	this.exibeModalAutenticacao = exibeModalAutenticacao;}
	public Date getDataSelecionada() {return dataSelecionada;}
	public void setDataSelecionada(Date dataSelecionada) {this.dataSelecionada = dataSelecionada;}
	public boolean isMostraModalExcluirData() {return mostraModalExcluirData;}
	public void setMostraModalExcluirData(boolean mostraModalExcluirData) {this.mostraModalExcluirData = mostraModalExcluirData;}
	public boolean isAlterouExames() {return alterouExames;}
	public void setAlterouExames(boolean alterouExames) {this.alterouExames = alterouExames;}
	public Boolean getTemPermissaoManter() { return temPermissaoManter; }
	public void setTemPermissaoManter(Boolean temPermissaoManter) {	this.temPermissaoManter = temPermissaoManter; }
	public String getUsuarioSolicitante() {	return this.autenticacaoController.getUsername(); }
	public boolean isAdicionouExame() {	return adicionouExame;}
	public void setAdicionouExame(boolean adicionouExame) { this.adicionouExame = adicionouExame;}
}