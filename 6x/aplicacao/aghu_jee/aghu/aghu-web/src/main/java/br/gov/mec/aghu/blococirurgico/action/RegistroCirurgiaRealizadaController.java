package br.gov.mec.aghu.blococirurgico.action;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.blococirurgico.vo.AlertaModalVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaCodigoProcedimentoSusVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaAnestesiaVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProcedimentoVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProfissionalVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.faturamento.action.ManterContaHospitalarController;
import br.gov.mec.aghu.faturamento.action.ManterProcedimentoAmbulatorialController;
import br.gov.mec.aghu.faturamento.action.ManterProcedimentoAmbulatorialPaginatorController;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgiasId;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDestinoPaciente;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgsId;
import br.gov.mec.aghu.model.MbcMotivoCancelamento;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgiasId;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfCirurgiasId;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VMbcProcEsp;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.paciente.action.PesquisaPacienteController;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.action.CadastrarPacienteController;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.vo.AgendaProcedimentoPesquisaProfissionalVO;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;


@SuppressWarnings({"PMD.AghuTooManyMethods","PMD.ExcessiveClassLength","PMD.NPathComplexity"})
public class RegistroCirurgiaRealizadaController extends ActionController {

	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";

	private static final String ID_SB_ESPECIALIDADE_PROCEDIMENTO = "sbEspecialidadeProcedimento";
	
	private static final String REDIRECT_PESQUISA_PACIENTE = "paciente-pesquisaPaciente";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(RegistroCirurgiaRealizadaController.class);

	private static final long serialVersionUID = -6734245080357723369L;
	
	public enum RegistroCirurgiaRealizadaControllerExceptionCode implements BusinessExceptionCode {
		MSG_ERRO_ADD_EDITAR_PROCED;	}

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;


	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	
	@EJB
	private IPermissionService permissionService;

	// PARÂMETROS DE CONVERSAÇÃO
	private Integer crgSeq;
	private String voltarPara;
	private boolean emEdicao; // Modo de edição

	private boolean sliderCirurgiaAberto = true, sliderProcedimentoAberto = true, sliderProfissionaisAberto = true, sliderAnestesiasAberto = true; // CONTROLE DOS SLIDERS

	// CAMPOS DA CIRURGIA
	private CirurgiaTelaVO vo; // A Cirurgia está neste VO
	private Boolean digitacaoConfirmada;
	private Boolean digitacaoConfirmadaAposGravar; //Utilizada para controle dos botoes faturamento e internação pois quando confirma o botao da digitação ainda nao pode habilitar os faturamentos

	private FatConvenioSaudePlano plano;
	private Short convenioId; // Convênio
	private Byte planoId; // Plano

	private List<CirurgiaTelaProcedimentoVO> listaProcedimentos; // PROCEDIMENTOS
	private AghEspecialidades especialidadeProcedimentoAdd; // Especialidade do item ADICIONADO na lista de procedimentos
	private VMbcProcEsp procedimentoAdd; // Procedimento do item ADICIONADO na lista de procedimentos
	private AghCid cidAdd; // CID selecionado na Sggestion ao editar um procedimento com phiSeq
	private Byte quantidadeProcedimentoAdd;
	private boolean situacaoProcedimentoAdd = true, indPrincipalProcedimentoAdd, procedimentoAddEmEdicao, procedimentoEmEdicaoPossuiPhi; // Valor default é TRUE
	private CirurgiaTelaProcedimentoVO procedimentoRemove; // Item REMOVIDO da lista de procedimentos
	private CirurgiaTelaProcedimentoVO procedimentoSelecionado; // Item SELECIONADO da lista de procedimentos para abrir a modal de SIGTAP
	private String codigoSUSProcedimento; // Valor do cadastro SUS do procedimento em edição
	private BigDecimal valorCadastralProcedimento; // Valor cadastral do procedimento em edição
	private Short valorPermProcedimento; // Valor Perm do procedimento em edição
	private Integer phiSeqProcedimento; // phiSeq do procedimento em edição
	private List<CirurgiaCodigoProcedimentoSusVO> listaCodigoProcedimentos; // PROCEDIMENTOS SUS EXIBIDOS NA MODAL
	private List<CirurgiaTelaProfissionalVO> listaProfissionais; // PROFISSIONAIS
	private AgendaProcedimentoPesquisaProfissionalVO profissionalAdd; // Profissional do item ADICIONADO na lista de profissionais
	private CirurgiaTelaProfissionalVO profissionalRemove; // Item REMOVIDO da lista de profissionais
	private List<CirurgiaTelaAnestesiaVO> listaAnestesias; // ANESTESIAS
	private MbcTipoAnestesias tipoAnestesiaAdd; // Anestesia do item ADICIONADO na lista de anestesias
	private CirurgiaTelaAnestesiaVO anestesiaRemove;
	private Integer seqContaHospitalar;
	
	private Long seqCodigoProcedimento;
	private Short cpgCphCspCnvCodigo;
	private Short cpgGrcSeq;
	private Byte cpgCphCspSeq;
	private String voltarParaTela;
	
	private boolean alterarProntuario;
	private Integer prontuario, pacCodigoFonetica;
	private String nomePaciente;
	private Integer codigoPaciente;
	private MbcProcEspPorCirurgiasId id;
	private AipPacientes paciente;
	
	private Boolean origemOk;
	private boolean alterouAnestesia;
	private boolean ocorreuTrocaPaciente;
	private CirurgiaCodigoProcedimentoSusVO codigoProcedimentoVO;
	private boolean codigoProcedimentoVOEdicao;

	//PArametros para chamadas das descrições em outra aba
	private static final String RELATORIO_CIRURGIA = "/aghu/pages/blococirurgico/relatorios/relatorioListarCirurgiasCIR.xhtml?";
	private static final String RELATORIO_PDT = "/aghu/pages/blococirurgico/relatorios/relatorioListarCirurgiasPDT.xhtml?";
	private static final String RELATORIO_PDT_VOLTAR_PARA = ";voltarPara=registroCirurgiaRealizada";
	private String urlRelatorioCirurgia;
	
	@Inject
	private CadastrarPacienteController cadastrarPacienteController; 
	
	@Inject
	private ManterProcedimentoAmbulatorialPaginatorController manterProcedimentoAmbulatorialPaginatorController;
	
	@Inject
	private ManterProcedimentoAmbulatorialController manterProcedimentoAmbulatorialController;
	
	@Inject 
	private PesquisaPacienteController pesquisaPacienteController;

	@Inject 
	private ManterContaHospitalarController manterContaHospitalarController;

	// CONTROLA MODAIS COM A CONFIRMAÇÃO DE AÇÕES OU PASSOS DO BOTÃO GRAVAR
	private AlertaModalVO alertaVO;
	private boolean exibirModalAlertaGravar; // Flag que controla a exibição da modal de alerta
	private boolean confirmarDigitacaoNota;
	private boolean houveColisao; //Controla se houve colisão ao confirmar notas de consumo
	private boolean procedimentoFaturado; //Controla se o procedimento já foi faturado
	
	// Atributo para guardar o erro de permissao para o botao Descricao cirurgica
	// Erro gerado ao tentar montar URL do botao no inicio da pagina
	private ApplicationBusinessException erroUsuarioNaoPermitidoDescricaoPDT;
	
	private final String PAGE_CADASTRAR_PACIENTE = "paciente-cadastroPaciente";
	private final String PAGE_REGISTRO_CIRURGIA_REALIZADA = "blococirurgico-registroCirurgiaRealizada";
	private final String PAGE_MATERIAIS_CONSUMIDOS = "materialPorCirurgiaCRUD";
	private final String PAGE_FATURAMENTO_PROC_AMBULATORIAL_LIST = "faturamento-manterProcedimentoAmbulatorialList";
	private final String PAGE_FATURAMENTO_PROC_AMBULATORIAL = "faturamento-manterProcedimentoAmbulatorial";
	private final String PAGE_SOLICITACOES_ESPECIAIS = "cadastroSolicitacoesEspeciais";
	private final String PAGE_EQUIPAMENTOS = "blococirurgico-equipamentos";
	private final String PAGE_FATURAMENTO_MANTER_CONTA_HOSPITALAR = "faturamento-manterContaHospitalar";
	private final String PAGE_FATURAMENTO_MANTER_CONTA_HOSPITAL_LIST="faturamento-manterContaHospitalarList";
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";


	// Chamado antes de renderizar a tela.
	public void inicio() {
		this.alterouAnestesia = false;
		
		if(vo == null){
			this.instanciarRegistroCirurgia(this.crgSeq);
		}
		
		if (this.pacCodigoFonetica != null) { // Retorno da pesquisa fonética
			// verificar se trocou de paciente 	
			if (vo.getCirurgia().getPaciente().getCodigo().intValue() != pacCodigoFonetica.intValue()){
				ocorreuTrocaPaciente = true;
			}

			this.setPaciente(this.pacienteFacade.obterPaciente(this.pacCodigoFonetica));
			this.prontuario = this.paciente.getProntuario();
			this.vo.getCirurgia().setPaciente(this.getPaciente());
			this.alterarProntuario = true;
			
			this.codigoPaciente = this.getPaciente().getCodigo();
			this.nomePaciente = this.getPaciente().getNome();
		}
		else if (this.vo.getCirurgia().getPaciente()!=null){
			this.setPaciente(vo.getCirurgia().getPaciente());
			this.pacCodigoFonetica = vo.getCirurgia().getPaciente().getCodigo();
			this.codigoPaciente = vo.getCirurgia().getPaciente().getCodigo();
			this.nomePaciente = vo.getCirurgia().getPaciente().getNome();
			
			if (vo.getCirurgia().getPaciente().getProntuario()!=null){
				this.prontuario = vo.getCirurgia().getPaciente().getProntuario();
			}	
		}
		if (this.digitacaoConfirmada == null) {
			this.digitacaoConfirmada = this.vo.getCirurgia().getDigitaNotaSala();
			this.digitacaoConfirmadaAposGravar = this.vo.getCirurgia().getDigitaNotaSala();
		}
		if (!this.vo.getCirurgia().getDigitaNotaSala() && this.vo.getCirurgia().getData().before(new Date())){
			this.confirmarDigitacaoNota = false;
		}
		else {
			this.confirmarDigitacaoNota = true;
		}
		// Validações do FORMS ao carregar a tela
		String warningProcedimentoFaturado = this.blocoCirurgicoFacade.validarProcedimentoFaturado(vo);
		if (warningProcedimentoFaturado != null){
			procedimentoFaturado = true;
			this.apresentarMsgNegocio(Severity.WARN, warningProcedimentoFaturado);
		} else {
			procedimentoFaturado = false;
		}
		if (this.blocoCirurgicoFacade.validarFaturamentoPacienteTransplantado(vo)!=null){
			this.apresentarMsgNegocio(Severity.WARN, this.blocoCirurgicoFacade.validarFaturamentoPacienteTransplantado(vo), vo.getCirurgia().getPaciente().getNome());
		}
		List<String> msgs = this.blocoCirurgicoFacade.validarProntuario(vo , pacCodigoFonetica);
		for (String msg : msgs) {
			// 7.10. EVT_POST_BLOCK (Executada ao entrar na tela):
			if (msg!=null){
				this.apresentarMsgNegocio(Severity.WARN,msg);
			}
		}
		try {
			this.blocoCirurgicoFacade.popularCodigoSsm(this.listaProcedimentos, vo.getCirurgia()); // PROCEDURE MBCP_CODIGO_SSM (Ao entrar na interface de Cirurgia Realizada -
			// Procedimentos)
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
		
		this.setCrgSeq(crgSeq);
		this.visualizarDescricaoCirurgicaOuPDT(crgSeq);
	}

	public String pesquisaPaciente() {
		this.pesquisaPacienteController.limparCampos();
		this.pesquisaPacienteController.setCameFrom(PAGE_REGISTRO_CIRURGIA_REALIZADA);
		return REDIRECT_PESQUISA_PACIENTE;
	}
	
	// Instancia uma novo AgendaProcedimentosVO
	public void instanciarRegistroCirurgia(Integer crgSeqRegistroCirurgiaRealizada) {
		this.vo = new CirurgiaTelaVO();
		this.tipoAnestesiaAdd = null;

		if (crgSeqRegistroCirurgiaRealizada != null) {
			MbcCirurgias cirurgia = this.blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(crgSeqRegistroCirurgiaRealizada, 
					new Enum[] {MbcCirurgias.Fields.CONVENIO_SAUDE_PLANO}, new Enum[] {MbcCirurgias.Fields.ATENDIMENTO}); 
			
			this.vo.setCirurgia(cirurgia);
			this.plano =  faturamentoFacade.obterConvenioSaudePlanoPorChavePrimaria(cirurgia.getConvenioSaudePlano().getId());
			this.convenioId = this.plano.getId().getCnvCodigo();
			this.planoId = this.plano.getId().getSeq();
			
			if(cirurgia.getSalaCirurgica()!= null){
				MbcSalaCirurgica salaCirurgica = this.blocoCirurgicoFacade.obterSalaCirurgicaPorId(cirurgia.getSalaCirurgica().getId());
				this.vo.getCirurgia().setSalaCirurgica(salaCirurgica);
			}
			
			if (cirurgia.getMotivoCancelamento() != null) {
			   MbcMotivoCancelamento motivoCanc = this.blocoCirurgicoFacade.obterMotivoCancelamentoPorChavePrimaria(cirurgia.getMotivoCancelamento().getSeq());
			   this.vo.getCirurgia().setMotivoCancelamento(motivoCanc);
			}
			
			if (this.vo.getCirurgia().getAtendimento()!= null){
				this.vo.getCirurgia().setAtendimento(aghuFacade.obterAghAtendimentoPorChavePrimaria(this.vo.getCirurgia().getAtendimento().getSeq()));
				
			}
			
			if (this.vo.getCirurgia().getDestinoPaciente()!= null){
				this.vo.getCirurgia().setDestinoPaciente(blocoCirurgicoFacade.obterDestinoPaciente(this.vo.getCirurgia().getDestinoPaciente().getSeq()));
				
			}
			
			if (this.vo.getCirurgia().getUnidadeFuncional()!= null){
				this.vo.getCirurgia().setUnidadeFuncional(aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(this.vo.getCirurgia().getUnidadeFuncional().getSeq()));
			} 
			
			if (this.vo.getCirurgia().getEspecialidade()!= null){
				AghEspecialidades esp = aghuFacade.obterAghEspecialidadesPorChavePrimaria(this.vo.getCirurgia().getEspecialidade().getSeq());
				this.vo.getCirurgia().setEspecialidade(esp);
				this.especialidadeProcedimentoAdd = esp;				
			}
			
			if (cirurgia.getDigitaNotaSala()){
				this.listaProcedimentos = this.blocoCirurgicoFacade.pesquisarProcedimentosAgendaProcedimentos(this.vo.getCirurgia().getSeq(), DominioIndRespProc.NOTA);
			}
			else {
				if (cirurgia.getTemDescricao()!=null && cirurgia.getTemDescricao()){
					this.listaProcedimentos = this.blocoCirurgicoFacade.pesquisarProcedimentosAgendaProcedimentos(this.vo.getCirurgia().getSeq(), DominioIndRespProc.DESC);
				} 
				
				if(this.listaProcedimentos == null || this.listaProcedimentos.isEmpty()){ 
					this.listaProcedimentos = this.blocoCirurgicoFacade.pesquisarProcedimentosAgendaProcedimentos(this.vo.getCirurgia().getSeq(), DominioIndRespProc.AGND);
				}	
			}
			if (this.vo.getCirurgia().getPaciente()!=null){
				this.vo.getCirurgia().setPaciente(pacienteFacade.obterAipPacientesPorChavePrimaria(vo.getCirurgia().getPaciente().getCodigo()));
				
				this.setPaciente(vo.getCirurgia().getPaciente());
				if (vo.getCirurgia().getPaciente().getProntuario()!=null){
					this.prontuario = vo.getCirurgia().getPaciente().getProntuario();
				}	
				else {
					this.alterarProntuario = true;
				}	
			}
			try {
				this.blocoCirurgicoFacade.popularCodigoSsm(this.listaProcedimentos, vo.getCirurgia()); // PROCEDURE MBCP_CODIGO_SSM (Ao entrar na interface de Cirurgia Realizada - Procedimentos)
			} catch (BaseException e) {
				super.apresentarExcecaoNegocio(e);
			}
			if (this.listaProfissionais == null) {
				this.listaProfissionais = this.blocoCirurgicoFacade.pesquisarProfissionaisAgendaProcedimentos(this.vo.getCirurgia().getSeq());
			}
			if(this.listaAnestesias == null){
				this.listaAnestesias = this.blocoCirurgicoFacade.pesquisarAnestesiasAgendaProcedimentos(this.vo.getCirurgia().getSeq());
			}
		}
	}
	
	public void processarBuscaPacientePorProntuario() {
		if (this.prontuario != null) {
			tratarResultadoBuscaPaciente(pacienteFacade
					.obterPacientePorProntuario(this.prontuario));

		}

	}
			
	public void processarBuscaPacientePorCodigo() {
		if (this.codigoPaciente != null) {
			tratarResultadoBuscaPaciente(pacienteFacade
					.buscaPaciente(codigoPaciente));
		}
	}
	
	
	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}
		
	private void tratarResultadoBuscaPaciente(AipPacientes paciente) {

		if (paciente != null) {

			this.prontuario = (paciente.getProntuario());
			this.codigoPaciente = paciente.getCodigo();
			this.nomePaciente = paciente.getNome();
			this.paciente = paciente;

		} else {

			this.prontuario = null;
			this.codigoPaciente = null;
			this.nomePaciente = null;
			this.paciente = null;
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_NENHUM_PACIENTE_ENCONTRADO");

		}

	}

	public Long getSmmInternacao() {
		if (this.vo.getCirurgia().getAtendimento() != null) {
			AinInternacao internacao = this.internacaoFacade.obterAinInternacaoPorAtdSeq(this.vo.getCirurgia().getAtendimento().getSeq());
			if (internacao != null && internacao.getItemProcedimentoHospitalar() != null) { // Consulta realizada para evitar Lazy exception
				return this.internacaoFacade.obterItemProcedimentoHospitalar(internacao.getIphPhoSeq(),
						internacao.getIphSeq()).getCodTabela();
				
			}
		}
		return null;
	}

	public String getLocal() {
		return this.blocoCirurgicoFacade.obterQuarto(this.vo.getCirurgia().getPaciente().getCodigo(), false);
		
	}

	public void verificarColisaoDigitacaoNS() throws ApplicationBusinessException {
		vo.setListaProcedimentosVO(this.listaProcedimentos);
		
		if(vo.getCirurgia().getDataEntradaSala() == null) {
			vo.getCirurgia().setDataEntradaSala(vo.getCirurgia().getDataInicioCirurgia());
		}
		if(vo.getCirurgia().getDataSaidaSala() == null){
			vo.getCirurgia().setDataSaidaSala(vo.getCirurgia().getDataFimCirurgia());
		}
		
		try{
			String warning = this.blocoCirurgicoFacade.verificarColisaoDigitacaoNS(this.vo); // EVT_WHEN_VALIDATE_ITEM
			if(warning != null) { // Caso tenha retornado um warning
				apresentarMsgNegocio(Severity.WARN, warning);
			}
			houveColisao = false;
		} catch(ApplicationBusinessException e){
			houveColisao = true;
			apresentarExcecaoNegocio(e);
		} 
		
		this.blocoCirurgicoFacade.executaRotinaParaSetarResponsavelAoConfirmarNotaConsumo(listaProfissionais, vo.getCirurgia().getUnidadeFuncional().getSeq());
		
		this.blocoCirurgicoFacade.executarRotinaVincularCidUnicoAoProcedimento(listaProcedimentos, obterDominioTipoPlano());

		this.digitacaoConfirmada = true;
	}
	
	public void setarValores() {
		this.vo.getCirurgia().setPaciente(this.paciente);
		this.exibirModalAlertaGravar = false;
		// Seta a necessidade de anestesia conforme a lista de anestesias. Vide: GLOBAL.DEVE_TER_ANESTESISTA
		this.vo.setDeveTerAnestesista(false); // Reseta variável
			for (CirurgiaTelaAnestesiaVO anestesia : this.listaAnestesias) {
				if (Boolean.TRUE.equals(anestesia.getMbcTipoAnestesias().getIndNessAnest())) {
					this.vo.setDeveTerAnestesista(true); // Um item garante a necessidade!
				}
			}
		this.vo.getCirurgia().setConvenioSaudePlano(this.plano); // Seta convênio do plano de saúde
		this.vo.getCirurgia().setConvenioSaude(this.plano.getConvenioSaude());
		this.vo.setListaProcedimentosVO(this.listaProcedimentos); // Seta LISTA DE PROCEDIMENTOS
		this.vo.setListaProfissionaisVO(this.listaProfissionais); // Seta LISTA DE PROFISSIONAIS
		this.vo.setListaAnestesiasVO(this.listaAnestesias); // Seta LISTA DE ANESTESIAS
		this.vo.getCirurgia().setSciSeqp(vo.getCirurgia().getSalaCirurgica().getId().getSeqp()); //Seta a informação da sala nas variaveis primiticas que guardam o valor da sala na entidade cirurgia
		this.vo.getCirurgia().setSciUnfSeq(vo.getCirurgia().getSalaCirurgica().getId().getUnfSeq());
		if(this.digitacaoConfirmada){
			this.vo.setIndDigtNotaSala(digitacaoConfirmada);
			this.vo.getCirurgia().setDigitaNotaSala(true);
		}
	}
	
	private void verificarOcorreuTrocaPaciente() {
		if (!this.ocorreuTrocaPaciente){
			ocorreuTrocaPaciente =  vo.getCirurgia().getPaciente().getCodigo().intValue() != paciente.getCodigo().intValue();
		 }
	}
	
	
	public String validarListaProcedimentos() throws ApplicationBusinessException{	
		boolean procedimentoAtivo = false;
		boolean temPrincipal = false;		
		boolean temCidObrigatorio = false;	
		DominioTipoPlano plano = getObterDominioTipoPlano();
		Integer nroCidsRetorno  = 0;
		
		
		if (this.listaProcedimentos.isEmpty()) { // Valida a obrigatoriedade de preencher as listas de procedimentos e profissionas
			apresentarMsgNegocio(ID_SB_ESPECIALIDADE_PROCEDIMENTO, Severity.ERROR, "LISTA_OBRIGATORIA", "procedimentos", "procedimento");
			return null;
		} else {
				for (CirurgiaTelaProcedimentoVO procedimento : listaProcedimentos) {
					if (procedimento.getSituacao()== DominioSituacao.A) {
						procedimentoAtivo = true;
						if (procedimento.getIndPrincipal()) {
							temPrincipal = true;							
						}
					}
					
					if (nroCidsRetorno == 0 && !temCidObrigatorio && this.digitacaoConfirmada) {
						if (procedimento.getCgnbtEprPciseq()!=null){
							 Integer phiSeq = 0;
							 List<CirurgiaCodigoProcedimentoSusVO> listSus =  this.blocoCirurgicoFacade.listarCodigoProcedimentosSUS(procedimento.getSeqPhi(), procedimento.getMbcEspecialidadeProcCirgs().getId().getEspSeq(),	this.vo.getCirurgia().getOrigemPacienteCirurgia());
							 if (listSus!=null && listSus.size()==1){
								phiSeq = listSus.get(0).getPhiSeq();
							}
							else if (listSus!=null && listSus.size()>1){
								for (CirurgiaCodigoProcedimentoSusVO codProcSus : listSus){
									if (codProcSus.getCodTabela().toString().equalsIgnoreCase(procedimento.getCgnbtEprPciseq())){
										phiSeq = codProcSus.getPhiSeq();
										break;
									}
								}
							}
							
							List<AghCid> listaCids = new ArrayList<AghCid>();					
							listaCids = this.blocoCirurgicoFacade.pesquisarCidsPorPhiSeq(phiSeq, plano, null);						
							nroCidsRetorno = listaCids.size();
							temCidObrigatorio = procedimento.getCid() != null;	
								
						}
					 }
				}
				if (!procedimentoAtivo) {
					apresentarMsgNegocio(ID_SB_ESPECIALIDADE_PROCEDIMENTO, Severity.ERROR, "LISTA_PROCEDIMENTOS_ATIVA");
					return null;
				}
				if (!temPrincipal) {
					apresentarMsgNegocio(ID_SB_ESPECIALIDADE_PROCEDIMENTO, Severity.ERROR, "LISTA_PROCEDIMENTOS_PRINCIPAL_ATIVO");
					return null;
				}
				if (nroCidsRetorno > 0 && !temCidObrigatorio) {
					apresentarMsgNegocio(Severity.ERROR, "LISTA_PROCEDIMENTOS_VALIDA_CID");
					return null;
				}
		}
		return "";
	}
	
	private void verificarTrocaAtendimento() {
		final Integer atdSeq = this.vo.getCirurgia().getAtendimento() != null ? this.vo.getCirurgia().getAtendimento().getSeq() : null;
		AghAtendimentos atendimentVigente = this.blocoCirurgicoFacade.obterAtendimentoVigentePacienteInternado(atdSeq, 
				this.vo.getCirurgia().getPaciente().getCodigo(), this.vo.getCirurgia().getDataInicioCirurgia());
			vo.getCirurgia().setAtendimento(atendimentVigente!=null && atendimentVigente.getSeq()!=null?atendimentVigente:this.vo.getCirurgia().getAtendimento());
	}
	
	
	// Gravar: Chama a parte 1 da RN1
	/*@Restrict("#{s:hasPermission('registroCirurgiaRealizada','executar')}")*/
	public String gravar() {
		try {
			this.verificarOcorreuTrocaPaciente();
			this.setarValores();
			
			// As validacoes de colisao deve ocorrer apos o metodo setarValores
			// pois eh ele que seta varias informacoes no VO utilizado por este metodo.
			// Valida antes de gravar se não há uma colisão de nota de consumo
			String warning = this.blocoCirurgicoFacade.verificarColisaoDigitacaoNS(this.vo);
			if(warning != null) { // Caso tenha retornado um warning
				apresentarMsgNegocio(Severity.WARN, warning);
			}
			
			this.validaOrigemComPlano();
			if (!this.getOrigemOk()) {
				return null;
			}
			
			if (this.validarListaProcedimentos() == null) {
				return null;
			}
			
			if (this.listaProfissionais.isEmpty()) {
				apresentarMsgNegocio("sbProfissional", Severity.ERROR, "LISTA_OBRIGATORIA", "profissionais", "profissional");
				return null;
			}
			
			for (CirurgiaTelaProfissionalVO servidorProfissional :listaProfissionais) {
				if (servidorProfissional!=null &&  servidorProfissional.getIndResponsavel()
						&& DominioNaturezaFichaAnestesia.URG.equals(this.vo.getCirurgia().getNaturezaAgenda())) {
					List<AghProfEspecialidades> listaProfEspecialidades = this.aghuFacade.pesquisarProfEspecialidadesCirurgiao(this.vo.getCirurgia().getEspecialidade().getSeq(),
							servidorProfissional.getServidorConselho().getId().getVinCodigo(), servidorProfissional.getServidorConselho().getId().getMatricula());
					if (listaProfEspecialidades.isEmpty()) {
						this.apresentarMsgNegocio(Severity.WARN, "MSG_ESP_PROFISSIONAL_DIFERENTE_CIRURGIA");
						return null;
					}
				}
			}
			
			if (ocorreuTrocaPaciente) {
				//Vincula o quando o paciente é alterado esta internado e tem atendimento vigente.
				this.verificarTrocaAtendimento();
			}
			
			// PARTE 1 DE REGISTRAR CIRURGIA REALIZADA NOTA CONSUMO que poderá retornar ALERTAS DE CONFIRMAÇÃO
			this.alertaVO = this.blocoCirurgicoFacade.registrarCirurgiaRealizadaNotaConsumo(this.emEdicao, this.digitacaoConfirmada, this.vo, super.getEnderecoRedeHostRemoto());
			if (StringUtils.isNotEmpty(alertaVO.getAlerta())) {
				this.exibirModalAlertaGravar = true; // Retorna para tela e exibe a modal de alerta sugerida pela parte 1 da gravação
				return null;
			}
			
			this.gravarParte2(); // Executa a CONTINUAÇÃO DO GRAVAR
			
			
			/*#41806 –  REGISTRA TRANSPLANTES - ÓRGÃOS */
			
			if(permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "registroTransplanteOrgaosAut", "registrarTransplantes")){
				
				List<MbcProcedimentoCirurgicos> procedimentos = new ArrayList<MbcProcedimentoCirurgicos>();
				
				for (CirurgiaTelaProcedimentoVO item : listaProcedimentos) {
					procedimentos.add(item.getProcedimentoCirurgico());
				}
				
				transplanteFacade.registarTransplantes(procedimentos, codigoPaciente);
			}
			
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		return null;
	}

	// Ação do botão SIM das mensagens de alerta para confirmação
	public void getPressionarBotaoSimModal() {
		try {
			if (Boolean.TRUE.equals(this.alertaVO.isTempoUtlzO2()) || Boolean.TRUE.equals(this.alertaVO.isTempoUtlzProAzot())) {
				this.blocoCirurgicoFacade.validarModalTempoUtilizacaoO2Ozot(this.vo, this.alertaVO, true); // SIM
			}
			this.gravarParte2(); // Executa a CONTINUAÇÃO DO GRAVAR
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		} finally {
			this.exibirModalAlertaGravar = false;
		}
	}

	// Ação do botão NÃO das mensagens de alerta para confirmação
	public void getPressionarBotaoNaoModal() {
		try {
			if (Boolean.TRUE.equals(this.alertaVO.isTempoUtlzO2()) || Boolean.TRUE.equals(this.alertaVO.isTempoUtlzProAzot())) {
				this.blocoCirurgicoFacade.validarModalTempoUtilizacaoO2Ozot(this.vo, this.alertaVO, false); // NÃO
			}
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		} finally {
			this.exibirModalAlertaGravar = false;
		}
	}

	//@Restrict("#{s:hasPermission('registroCirurgiaRealizada','executar')}")
	public String gravarParte2() throws BaseException {		
		this.vo.getListaProcedimentosRemovidos().clear(); // Limpa lista de ITENS REMOVIDOS
		this.vo.getListaProfissionaisRemovidos().clear();
		this.vo.getListaAnestesiasRemovidas().clear();
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATUALIZAR_CIRURGIA_REALIZADA");
		this.instanciarRegistroCirurgia(this.vo.getCirurgia().getSeq()); // Entra no modo edição
		this.digitacaoConfirmada = this.vo.getCirurgia().getDigitaNotaSala(); // Confirma a digitação
		this.digitacaoConfirmadaAposGravar = this.vo.getCirurgia().getDigitaNotaSala(); // Confirma a digitação
		this.alterouAnestesia = false;
		cancelarProcedimento();
		return null;
	}

	public String getCancelar() {
		this.pacCodigoFonetica = null;
		this.digitacaoConfirmada = null;
		this.listaProfissionais = null;
		this.listaAnestesias = null;
		this.listaProcedimentos = null;
		this.listaCodigoProcedimentos = null;
		this.vo = null;
		this.prontuario = null;
		this.codigoPaciente = null;
		this.nomePaciente = null;
		
		cancelarProcedimento();
		return this.voltarPara;
	}

	public String getEquipamentos() {
		return PAGE_EQUIPAMENTOS;
	}

	public String getSolicitacoesEspeciais() {
		return PAGE_SOLICITACOES_ESPECIAIS;
	}

	public String getCadastroProntuario() {
		AipPacientes paciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(this.vo.getCirurgia().getPaciente().getCodigo());
		cadastrarPacienteController.setCameFrom(PAGE_REGISTRO_CIRURGIA_REALIZADA);
		cadastrarPacienteController.prepararEdicaoPaciente(paciente, null);
		
		return PAGE_CADASTRAR_PACIENTE;
	}

	public String getMateriaisConsumidos() {
		return PAGE_MATERIAIS_CONSUMIDOS;
	}

	public String getFaturamentoInternacao() {
		List<FatContasHospitalares>  contasHospitalar =  faturamentoFacade.pesquisaContaHospitalarParaNotaConsumoDaCirurgia(vo.getCirurgia().getAtendimento().getSeq());
		if (contasHospitalar != null && contasHospitalar.size()>0){
			if (contasHospitalar.size()>1){
				 return PAGE_FATURAMENTO_MANTER_CONTA_HOSPITAL_LIST;
			}
			else{
				seqContaHospitalar = contasHospitalar.get(0).getSeq();
				setVoltarParaTela(PAGE_REGISTRO_CIRURGIA_REALIZADA);
				manterContaHospitalarController.setSeq(seqContaHospitalar);
				manterContaHospitalarController.setVoltarParaTela(PAGE_REGISTRO_CIRURGIA_REALIZADA);
				return PAGE_FATURAMENTO_MANTER_CONTA_HOSPITALAR;

		}
	}		
		return null;
}

	public String getFaturamentoAmbulatorial() {
		List<FatProcedAmbRealizado>  listaProcedimentos = faturamentoFacade.buscarProcedimentosCirurgicosParaSeremFaturados(vo.getCirurgia().getPaciente(), 
				vo.getCirurgia(), vo.getCirurgia().getUnidadeFuncional().getSeq(), convenioId, planoId);
		if(listaProcedimentos != null && listaProcedimentos.size() > 1){			
			manterProcedimentoAmbulatorialPaginatorController.setPacCodigo(vo.getCirurgia().getPaciente().getCodigo());			
			manterProcedimentoAmbulatorialPaginatorController.setConvenioId(this.convenioId);
			manterProcedimentoAmbulatorialPaginatorController.setPlanoId(this.planoId);
			manterProcedimentoAmbulatorialPaginatorController.setDataCompetencia(vo.getCirurgia().getData());
			manterProcedimentoAmbulatorialPaginatorController.setVoltarParaTela(PAGE_REGISTRO_CIRURGIA_REALIZADA);
			return PAGE_FATURAMENTO_PROC_AMBULATORIAL_LIST;
		}
		if(listaProcedimentos != null && listaProcedimentos.size() == 1){
			getCreateParametrosFind();
			setSeqCodigoProcedimento(listaProcedimentos.get(0).getSeq());
			
			manterProcedimentoAmbulatorialController.setCpgCphCspCnvCodigo(this.cpgCphCspCnvCodigo);
			manterProcedimentoAmbulatorialController.setCpgGrcSeq(this.cpgGrcSeq);
			manterProcedimentoAmbulatorialController.setCpgCphCspSeq(this.cpgCphCspSeq);
			manterProcedimentoAmbulatorialController.setPacCodigo(vo.getCirurgia().getPaciente().getCodigo());
			manterProcedimentoAmbulatorialController.setSeq(this.seqCodigoProcedimento);
			manterProcedimentoAmbulatorialController.setVoltarParaTela(PAGE_REGISTRO_CIRURGIA_REALIZADA);
			
			return PAGE_FATURAMENTO_PROC_AMBULATORIAL;

		}
		List<FatProcedAmbRealizado>  listaProcedimentosCancelados = this.faturamentoFacade.pesquisarProcedAmbPorCirurgiaCancelada(this.vo.getCirurgia().getSeq());
		if(listaProcedimentosCancelados != null && listaProcedimentosCancelados.size() > 1){			
			manterProcedimentoAmbulatorialPaginatorController.setPacCodigo(vo.getCirurgia().getPaciente().getCodigo());			
			manterProcedimentoAmbulatorialPaginatorController.setConvenioId(this.convenioId);
			manterProcedimentoAmbulatorialPaginatorController.setPlanoId(this.planoId);
			manterProcedimentoAmbulatorialPaginatorController.setDataCompetencia(vo.getCirurgia().getData());
			manterProcedimentoAmbulatorialPaginatorController.setVoltarParaTela(PAGE_REGISTRO_CIRURGIA_REALIZADA);
			return PAGE_FATURAMENTO_PROC_AMBULATORIAL_LIST;
		}
		if(listaProcedimentosCancelados != null && listaProcedimentosCancelados.size() == 1){
			getCreateParametrosFind();
			setSeqCodigoProcedimento(listaProcedimentosCancelados.get(0).getSeq());
			
			manterProcedimentoAmbulatorialController.setCpgCphCspCnvCodigo(this.cpgCphCspCnvCodigo);
			manterProcedimentoAmbulatorialController.setCpgGrcSeq(this.cpgGrcSeq);
			manterProcedimentoAmbulatorialController.setCpgCphCspSeq(this.cpgCphCspSeq);
			manterProcedimentoAmbulatorialController.setPacCodigo(vo.getCirurgia().getPaciente().getCodigo());
			manterProcedimentoAmbulatorialController.setSeq(this.seqCodigoProcedimento);
			manterProcedimentoAmbulatorialController.setVoltarParaTela(PAGE_REGISTRO_CIRURGIA_REALIZADA);
			
			return PAGE_FATURAMENTO_PROC_AMBULATORIAL;

		}
		return null;
	}


	private void getCreateParametrosFind() {
		try {
			final AghParametros planoSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);
			final AghParametros convenioSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
			final AghParametros grupoSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
			setCpgCphCspCnvCodigo(convenioSUS.getVlrNumerico().shortValue());
			setCpgCphCspSeq(planoSUS.getVlrNumerico().byteValue());
			setCpgGrcSeq(grupoSUS.getVlrNumerico().shortValue());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
	}

	public List<AghEspecialidades> pesquisarEspecialidadesProcedimentos(String objPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarPorNomeOuSiglaEspSeqNulo(objPesquisa != null ? objPesquisa : null),pesquisarEspecialidadesProcedimentosCount(objPesquisa));
	}

	public Long pesquisarEspecialidadesProcedimentosCount(String objPesquisa) {
		return this.aghuFacade.pesquisarPorNomeOuSiglaEspSeqNuloCount(objPesquisa != null ? objPesquisa : null);
	}

	public void posRemoverEspecialidadesProcedimento() {
		this.especialidadeProcedimentoAdd = null;
		this.procedimentoAdd = null;
		this.quantidadeProcedimentoAdd = null;
		this.situacaoProcedimentoAdd = true;
		this.procedimentoEmEdicaoPossuiPhi = Boolean.FALSE;
	}
	
	public void posRemoverProcedimento(){
		this.procedimentoEmEdicaoPossuiPhi = Boolean.FALSE;
	}

	public List<VMbcProcEsp> pesquisarProcedimentoCirurgicos(String objPesquisa) {
		Short espSeq = this.especialidadeProcedimentoAdd != null ? this.especialidadeProcedimentoAdd.getSeq() : null;
		Integer pjqSeq = this.vo != null && this.vo.getCirurgia().getProjetoPesquisa() != null ? this.vo.getCirurgia().getProjetoPesquisa().getSeq() : null;
		return this.returnSGWithCount(this.blocoCirurgicoFacade.pesquisarProcedimentosEspecialidadeProjeto(objPesquisa != null ? objPesquisa : null, espSeq, pjqSeq),pesquisarProcedimentoCirurgicosCount(objPesquisa));
	}

	public Long pesquisarProcedimentoCirurgicosCount(String objPesquisa) {
		Short espSeq = this.especialidadeProcedimentoAdd != null ? this.especialidadeProcedimentoAdd.getSeq() : null;
		Integer pjqSeq = this.vo != null && this.vo.getCirurgia().getProjetoPesquisa() != null ? this.vo.getCirurgia().getProjetoPesquisa().getSeq() : null;
		return this.blocoCirurgicoFacade.pesquisarProcedimentosEspecialidadeProjetoCount(objPesquisa != null ? objPesquisa : null, espSeq, pjqSeq);
	}
	
	private List<String> obterConselhosMedicos() {
		try {
			List<String> retorno = new ArrayList<String>();
			String strListaSiglaConselho = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LISTA_CONSELHOS_PROF_MBC);
			List<String> listaSiglaConselho = Arrays.asList(strListaSiglaConselho.split(","));
			String siglaConselhoRegionalEnf = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_SIGLA_CONSELHO_REGIONAL_ENF);
            retorno.addAll(listaSiglaConselho);
            retorno.add(siglaConselhoRegionalEnf);
            return retorno;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	public List<AgendaProcedimentoPesquisaProfissionalVO> getPesquisarProfissionais(String objPesquisa) {
		Short unfSeq = this.vo != null && this.vo.getCirurgia().getUnidadeFuncional() != null ? this.vo.getCirurgia().getUnidadeFuncional().getSeq() : null;
		
		List<AgendaProcedimentoPesquisaProfissionalVO> list = this.blocoCirurgicoFacade.pesquisarProfissionaisAgendaENotaProcedimento(objPesquisa, unfSeq, this.obterConselhosMedicos(), false);
		
		return this.returnSGWithCount(list, getPesquisarProfissionaisCount(objPesquisa));
	}
	
	public Long getPesquisarProfissionaisCount(String objPesquisa) {
		Short unfSeq = this.vo != null && this.vo.getCirurgia().getUnidadeFuncional() != null ? this.vo.getCirurgia().getUnidadeFuncional().getSeq() : null;
		return this.blocoCirurgicoFacade.pesquisarProfissionaisAgendaENotaProcedimentoCount(objPesquisa, unfSeq, this.obterConselhosMedicos(), false);
	}
	
	public String retornaPesquisarProfissionaisCount(){
		Long valor = getPesquisarProfissionaisCount(null);
		return Long.toString(valor);
	}
	
	public List<MbcTipoAnestesias> pesquisarAnestesias(String objPesquisa) {
		return blocoCirurgicoCadastroApoioFacade.pequisarTiposAnestesiaSB(objPesquisa, null);
	}

	public Long pesquisarAnestesiasCount(String objPesquisa) {
		return blocoCirurgicoCadastroApoioFacade.pequisarTiposAnestesiaSBCount(objPesquisa, null);
	}

	public List<AghCid> getPesquisarAghCid(String objPesquisa) throws ApplicationBusinessException{
		DominioTipoPlano plano = getObterDominioTipoPlano();
		if (this.codigoProcedimentoVO!=null){
			return this.blocoCirurgicoFacade.pesquisarCidsPorPhiSeq(this.codigoProcedimentoVO.getPhiSeq(), plano,
					objPesquisa != null ? objPesquisa : null);
		}
		return null;
	}
	
	public boolean verificarExisteCidProcedimento() throws ApplicationBusinessException{
		Integer nroCidsRetorno  = getPesquisarAghCidCount("");
		return (nroCidsRetorno != 0);	
	}
	
	public boolean verificarExisteCodigoSus() throws ApplicationBusinessException{
		Integer nroCodSusRetorno  = listarProcedimentoSusCount("");
		return (nroCodSusRetorno != 0);	
	}	


	public Integer getPesquisarAghCidCount(String objPesquisa) throws ApplicationBusinessException{
		DominioTipoPlano plano = getObterDominioTipoPlano();
		List<AghCid> listaCids = new ArrayList<AghCid>();
		if (this.codigoProcedimentoVO!=null){
			listaCids = this.blocoCirurgicoFacade.pesquisarCidsPorPhiSeq(this.codigoProcedimentoVO.getPhiSeq(), plano,
				objPesquisa != null ? objPesquisa : null);
		}
		return listaCids.size();
	}
	
	private DominioTipoPlano getObterDominioTipoPlano() {
		DominioTipoPlano plano;
		if (this.plano != null && this.plano.getIndTipoPlano() == null) {
			FatConvenioSaudePlano fcsp = this.faturamentoFacade.obterConvenioSaudePlanoPeloAtendimentoDaConsulta(this.vo
							.getCirurgia().getAtendimento().getSeq());
			plano = fcsp.getIndTipoPlano();
		} else {
			plano = this.plano.getIndTipoPlano();
		}
		return plano;
	}
	
	public void habilitarPesquisaCid() {
		this.procedimentoEmEdicaoPossuiPhi = Boolean.TRUE;
	}

	public void posRemoverProcedimentoEspecialidade() {
		this.procedimentoEmEdicaoPossuiPhi = Boolean.FALSE;
		this.codigoProcedimentoVO = null;
	}
	
	public boolean validacoesCamposObrigatorios() throws ApplicationBusinessException{
		if (this.especialidadeProcedimentoAdd == null) {
			apresentarMsgNegocio(ID_SB_ESPECIALIDADE_PROCEDIMENTO, Severity.ERROR, CAMPO_OBRIGATORIO, "Especialidade");
			return true;
		} else if (this.procedimentoAdd == null) {
			apresentarMsgNegocio("sbProcedimento", Severity.ERROR, CAMPO_OBRIGATORIO, "Procedimento");
			return true;
		}else if (this.codigoProcedimentoVO == null) {
			apresentarMsgNegocio("sbProcedimentoSUS", Severity.ERROR, CAMPO_OBRIGATORIO, "Código SUS");
			return true;	
		} else if(this.cidAdd == null && verificarExisteCidProcedimento()) {
			apresentarMsgNegocio("sbCid", Severity.ERROR, CAMPO_OBRIGATORIO, "CID");
			return true;
		} else if (this.quantidadeProcedimentoAdd == null) {
			apresentarMsgNegocio("quantidade", Severity.ERROR, CAMPO_OBRIGATORIO, "Quantidade");
			return true;
		} 
		return false;
	}
	
	public void posRemoverCadastroSUS(){
		this.codigoProcedimentoVOEdicao = Boolean.FALSE;
	}

	public void adicionarProcedimento() throws ApplicationBusinessException {
		
		if (this.validacoesCamposObrigatorios()){
			return;
		}
		
		boolean existePrincipal = false;
		for (CirurgiaTelaProcedimentoVO procedimento : this.listaProcedimentos) {
			if (procedimento.getIndPrincipal()) {
				existePrincipal = true;
				break;
			}
		}
		CirurgiaTelaProcedimentoVO procedimentoVO = new CirurgiaTelaProcedimentoVO(); // 1. Instância que será gravada
		// Resgata o procedimento de VMbcProcEsp
		MbcProcedimentoCirurgicos procedimentoCirurgico = this.blocoCirurgicoFacade.obterMbcProcedimentoCirurgicosPorId(this.procedimentoAdd.getId().getPciSeq());
		MbcProcEspPorCirurgiasId id = new MbcProcEspPorCirurgiasId(); // 2. ID da instância que será gravada
		if (this.vo.getCirurgia().getSeq() != null) {
			id.setCrgSeq(vo.getCirurgia().getSeq()); // Cirurgia aqui
		}
		id.setEprEspSeq(this.especialidadeProcedimentoAdd.getSeq());
		id.setEprPciSeq(procedimentoCirurgico.getSeq());
		id.setIndRespProc(getIndRespProc(vo.getCirurgia().getDigitaNotaSala()));
		
		procedimentoVO.setId(id); // Seta ID

		// 3. Associar atributos
		if (!existePrincipal) { // Seta principal quando não existir o mesmo
			procedimentoVO.setIndPrincipal(true);
		} else {
			procedimentoVO.setIndPrincipal(false);
		}
		procedimentoVO.setProcedimentoCirurgico(procedimentoCirurgico);
		MbcEspecialidadeProcCirgs espProc = this.blocoCirurgicoCadastroApoioFacade.obterEspecialidadeProcedimentoCirurgico(new MbcEspecialidadeProcCirgsId(procedimentoCirurgico
				.getSeq(), this.especialidadeProcedimentoAdd.getSeq()));
		procedimentoVO.setMbcEspecialidadeProcCirgs(espProc);
		procedimentoVO.setSituacao(DominioSituacao.A);
		
		if (CoreUtil.maior(this.quantidadeProcedimentoAdd, (byte) 0)) {
			procedimentoVO.setQtd(this.quantidadeProcedimentoAdd); // Quantidade padrão é 1
		} else {
			procedimentoVO.setQtd((byte) 1); // Quantidade padrão é 1
		}
		procedimentoVO.setSituacao(DominioSituacao.getInstance(this.situacaoProcedimentoAdd));
		
		if (this.codigoProcedimentoVO!=null){
			procedimentoVO.setPhiSeq(this.codigoProcedimentoVO.getPhiSeq());
			procedimentoVO.setCgnbtEprPciseq(this.codigoProcedimentoVO.getCodTabela().toString());
			procedimentoVO.setCgnbtEprPciSeq2(this.codigoProcedimentoVO.getValorTotal());
			procedimentoVO.setCgnbtEprPciseq3(this.codigoProcedimentoVO.getValorPerm().shortValue());
		}

		if (this.listaProcedimentos.contains(procedimentoVO) && !this.procedimentoAddEmEdicao) { // Caso o item já exista na lista...
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ITEM_JA_CONSTA_LISTA", "Procedimento");
		} else if (this.procedimentoAddEmEdicao) {
			procedimentoVO.setId(this.id);
			procedimentoVO.setIndPrincipal(this.indPrincipalProcedimentoAdd);
			procedimentoVO.setCid(this.cidAdd);
			
			try {
				this.listaProcedimentos.set(this.listaProcedimentos.indexOf(procedimentoVO), procedimentoVO);  // 4. Editar na lista
			} catch (IndexOutOfBoundsException indexOutOfBoundsException) {
				apresentarExcecaoNegocio(new BaseException(
						RegistroCirurgiaRealizadaControllerExceptionCode.MSG_ERRO_ADD_EDITAR_PROCED, 
						indexOutOfBoundsException, indexOutOfBoundsException.getLocalizedMessage()));				
				return;
			} catch (Exception e) {
				apresentarExcecaoNegocio(new BaseException(RegistroCirurgiaRealizadaControllerExceptionCode.MSG_ERRO_ADD_EDITAR_PROCED, e, e.getLocalizedMessage()));
				return;
			}
			this.especialidadeProcedimentoAdd = null; // Em caso de edição limpa a especialidade
		} else {
			procedimentoVO.setExcluir(true);
			procedimentoVO.setCid(this.getCidAdd());
			this.listaProcedimentos.add(procedimentoVO); // 4. Acrescentar na lista
			tipoAnestesiaAdd = null; // Ajustes e operações na lista
			this.alterouAnestesia = true;

		}
		this.procedimentoAdd = null; // Ajustes e operações na lista
		this.quantidadeProcedimentoAdd = null;
		this.situacaoProcedimentoAdd = true;
		this.indPrincipalProcedimentoAdd = false;
		this.procedimentoAddEmEdicao = false;
		this.procedimentoEmEdicaoPossuiPhi = false;
		this.cidAdd = null;
		this.codigoSUSProcedimento = null;
		this.valorCadastralProcedimento = null;
		this.valorPermProcedimento = null;
		this.phiSeqProcedimento = null;
		this.codigoProcedimentoVO = null;
	}
	
	private DominioIndRespProc getIndRespProc(Boolean digitaNotaSala) {
		if (Boolean.TRUE.equals(digitaNotaSala)){
			return DominioIndRespProc.NOTA;
		} else {
			return DominioIndRespProc.AGND;
		}
	}

	public void editarProcedimento(CirurgiaTelaProcedimentoVO procedimentoVO) throws ApplicationBusinessException {
		if (procedimentoVO != null){
			this.especialidadeProcedimentoAdd = procedimentoVO.getMbcEspecialidadeProcCirgs().getEspecialidade();
			this.procedimentoAdd = this.blocoCirurgicoFacade.obterProcedimentosAgendadosPorId(procedimentoVO.getMbcEspecialidadeProcCirgs().getEspecialidade().getSeq(), procedimentoVO
					.getProcedimentoCirurgico().getSeq(), null);
			this.quantidadeProcedimentoAdd = procedimentoVO.getQtd();
			this.situacaoProcedimentoAdd = DominioSituacao.A.equals(procedimentoVO.getSituacao()) ? true : false;
			this.indPrincipalProcedimentoAdd = procedimentoVO.getIndPrincipal();
			this.procedimentoAddEmEdicao = true;
			if (procedimentoVO.getCgnbtEprPciseq()!=null){
				this.popularCadastroSUS(procedimentoVO.getCgnbtEprPciseq());
			}	
			this.procedimentoEmEdicaoPossuiPhi = Boolean.TRUE;
			this.cidAdd = procedimentoVO.getCid();
			this.codigoSUSProcedimento = procedimentoVO.getCgnbtEprPciseq();
			this.valorCadastralProcedimento = procedimentoVO.getCgnbtEprPciSeq2();
			this.valorPermProcedimento = procedimentoVO.getCgnbtEprPciseq3();
			this.phiSeqProcedimento = procedimentoVO.getPhiSeq();
			this.id = procedimentoVO.getId();
		}
	}
	
	public void popularCadastroSUS(String  codTabela) throws ApplicationBusinessException {
		List<CirurgiaCodigoProcedimentoSusVO> listSus =  this.blocoCirurgicoFacade.listarCodigoProcedimentosSUS(this.procedimentoAdd.getId().getPciSeq(),
				this.procedimentoAdd.getId().getEspSeq(),
				this.vo.getCirurgia().getOrigemPacienteCirurgia());
		
		if (listSus!=null && listSus.size()==1){
			this.codigoProcedimentoVO = listSus.get(0);
		}
		else if (listSus!=null && listSus.size()>1){
			for (CirurgiaCodigoProcedimentoSusVO codProcSus : listSus){
				if (codProcSus.getCodTabela().toString().equalsIgnoreCase(codTabela)){
					this.codigoProcedimentoVO = codProcSus;
					return;
				}
			}
		}
	}
	
	public void cancelarProcedimento() {
		this.especialidadeProcedimentoAdd = null;
		this.procedimentoAdd = null;
		this.quantidadeProcedimentoAdd = null;
		this.situacaoProcedimentoAdd = true;
		this.indPrincipalProcedimentoAdd = false;
		this.procedimentoAddEmEdicao = false;
		this.cidAdd = null;
		this.procedimentoEmEdicaoPossuiPhi = false;
		this.codigoProcedimentoVO = null;
	}
	
	
	public List<CirurgiaCodigoProcedimentoSusVO> listarProcedimentoSus(String objPesquisa) throws ApplicationBusinessException {

		return this.returnSGWithCount(
					this.blocoCirurgicoFacade.listarCodigoProcedimentosSUS(
							this.procedimentoAdd.getId().getPciSeq(),
							this.procedimentoAdd.getId().getEspSeq(),
							this.vo.getCirurgia().getOrigemPacienteCirurgia()
					),
					listarProcedimentoSusCount(objPesquisa)
		);
	}


	
	public void popularCadastroSUS() throws ApplicationBusinessException {
		List<CirurgiaCodigoProcedimentoSusVO> listSus = null;
		
		if (this.procedimentoAdd != null && this.procedimentoAdd.getId() != null) {
			listSus =  this.blocoCirurgicoFacade.listarCodigoProcedimentosSUS(
					this.procedimentoAdd.getId().getPciSeq(),
					this.procedimentoAdd.getId().getEspSeq(),
					this.vo.getCirurgia().getOrigemPacienteCirurgia()
			);
		}
		
		if (listSus!=null && listSus.size()==1) {
			this.codigoProcedimentoVO = listSus.get(0);
		}
	}
	

	public Integer listarProcedimentoSusCount(String objPesquisa) throws ApplicationBusinessException{
		int returnValue = 0;
		if (this.procedimentoAdd != null && this.procedimentoAdd.getId() != null
				&& this.vo != null && this.vo.getCirurgia() != null) {
			returnValue = this.blocoCirurgicoFacade.listarCodigoProcedimentosSUS(this.procedimentoAdd.getId().getPciSeq(),
								this.procedimentoAdd.getId().getEspSeq(),
								this.vo.getCirurgia().getOrigemPacienteCirurgia()
						).size();
		}
		return returnValue;
	}


	public void adicionarProfissional() {
		if (this.profissionalAdd == null) {
			apresentarMsgNegocio("sbProfissional", Severity.ERROR, CAMPO_OBRIGATORIO, "Profissional");
			return;
		}
		boolean existeResponsavel = false;
		for (CirurgiaTelaProfissionalVO profissionalVO : this.listaProfissionais) {
			if (profissionalVO.getIndResponsavel()) {
				existeResponsavel = true;
				break;
			}
		}
		CirurgiaTelaProfissionalVO profissionalVO = new CirurgiaTelaProfissionalVO(); // 1. Instância que será gravada
		// Instancia o servidor do profissional
		RapServidores servidorProfissional = this.registroColaboradorFacade.obterRapServidoresPorChavePrimaria(new RapServidoresId(this.profissionalAdd.getMatricula(),
				this.profissionalAdd.getVinCodigo()));
		Short unfSeq = this.vo != null && this.vo.getCirurgia().getUnidadeFuncional() != null ? this.vo.getCirurgia().getUnidadeFuncional().getSeq() : null;
		// Resgata o profissional que atua na unidade
		MbcProfAtuaUnidCirgs profAtuaUnidCirgs = this.blocoCirurgicoCadastroApoioFacade.pesquisarProfUnidCirgAtivoPorServidorUnfSeqFuncao(servidorProfissional,
				unfSeq, this.profissionalAdd.getFuncao());
		MbcProfCirurgiasId id = new MbcProfCirurgiasId(); // 2. ID da instância que será gravada
		id.setCrgSeq(this.vo.getCirurgia().getSeq());
		id.setPucIndFuncaoProf(profAtuaUnidCirgs.getId().getIndFuncaoProf());
		id.setPucSerMatricula(this.profissionalAdd.getMatricula());
		id.setPucSerVinCodigo(this.profissionalAdd.getVinCodigo());
		id.setPucUnfSeq(profAtuaUnidCirgs.getId().getUnfSeq());
		profissionalVO.setId(id);
		if (!existeResponsavel) {
			profissionalVO.setIndResponsavel(true);
		} else {
			profissionalVO.setIndResponsavel(false);
		}
		profissionalVO.setCirurgia(null);
		profissionalVO.setServidorPuc(profAtuaUnidCirgs.getRapServidores());
		profissionalVO.setMbcProfAtuaUnidCirgs(profAtuaUnidCirgs);
		profissionalVO.setFuncaoProfissional(profAtuaUnidCirgs.getIndFuncaoProf());
		VRapServidorConselho servidorConselho = this.registroColaboradorFacade.obterVRapServidorConselhoPeloId(this.profissionalAdd.getMatricula(),
				this.profissionalAdd.getVinCodigo(), null);
		profissionalVO.setServidorConselho(servidorConselho);
		if (this.listaProfissionais.contains(profissionalVO)) { // Caso o item já exista na lista...
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ITEM_JA_CONSTA_LISTA", "Profissional");
		} else {
			this.listaProfissionais.add(profissionalVO); // 4. Acrescentar na lista
			profissionalAdd = null; // Ajustes e operações na lista
		}
	
	}

	public void adicionarAnestesia() {
		if (this.tipoAnestesiaAdd == null) {
			apresentarMsgNegocio("sbAnestesia", Severity.ERROR, CAMPO_OBRIGATORIO, "Anestesia");
			return;
		}
		CirurgiaTelaAnestesiaVO anestesiaVO = new CirurgiaTelaAnestesiaVO(); // 1. Instancia que será gravada
		MbcAnestesiaCirurgiasId id = new MbcAnestesiaCirurgiasId(); // 2. ID da instância que será gravada
		id.setCrgSeq(this.vo.getCirurgia().getSeq());
		id.setTanSeq(this.tipoAnestesiaAdd.getSeq());
		anestesiaVO.setId(id); // Seta ID
		// 3. Associar atributos
		anestesiaVO.setMbcTipoAnestesias(this.tipoAnestesiaAdd);
		if (this.listaAnestesias.contains(anestesiaVO)) { // Caso o item já exista na lista...
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ITEM_JA_CONSTA_LISTA", "Anestesia");
		} else {
			listaAnestesias.add(anestesiaVO); // 4. Acrescentar na lista
			tipoAnestesiaAdd = null; // Ajustes e operações na lista
			this.alterouAnestesia = true;
		}
	}

	/**
	 * Métodos para setar o principal ou responsável nas listas ATENÇÃO: Como os itens pertencem a mesma classe, somente a comparação de HASH CODE será necessária para definir o
	 * selecionado
	 */
	public void setProcedimentoPrincipal(final int procedimentoPrincipalHashCode) {
		for (CirurgiaTelaProcedimentoVO procedimentoVO : this.listaProcedimentos) {
			if (procedimentoVO.hashCode() == procedimentoPrincipalHashCode) {
				procedimentoVO.setIndPrincipal(true);
			} else {
				procedimentoVO.setIndPrincipal(false);
			}
		}
	}

	public void setProfissionalResponsavel(final int profissionalResponsavelPrincipalHashCode) {
		for (CirurgiaTelaProfissionalVO profissionalVO : this.listaProfissionais) {
			if (profissionalVO.hashCode() == profissionalResponsavelPrincipalHashCode) {
				profissionalVO.setIndResponsavel(true);
			} else {
				profissionalVO.setIndResponsavel(false);
			}
		}
	}

	public void setProfissionalRealizaCirurgia(final int profissionalResponsavelPrincipalHashCode) {
		for (CirurgiaTelaProfissionalVO profissionalVO : this.listaProfissionais) {
			if (profissionalVO.hashCode() == profissionalResponsavelPrincipalHashCode && profissionalVO.getIndRealizou() == false) {
					profissionalVO.setIndRealizou(false);
			} else if (profissionalVO.hashCode() == profissionalResponsavelPrincipalHashCode && profissionalVO.getIndRealizou() == true){
					profissionalVO.setIndRealizou(true);
			}
		}
	}

	public void removerProcedimento() {
		if (this.listaProcedimentos.contains(this.procedimentoRemove)) {
			this.listaProcedimentos.remove(this.procedimentoRemove);
			if (Boolean.TRUE.equals(this.procedimentoRemove.getIndPrincipal())) {
				if (!this.listaProcedimentos.isEmpty()) {
					this.listaProcedimentos.get(0).setIndPrincipal(Boolean.TRUE);
				}
			}
		}
		
		this.procedimentoRemove = null;
	}

	public void removerProfissionais() {
		if (this.listaProfissionais.contains(this.profissionalRemove)) {
			this.listaProfissionais.remove(this.profissionalRemove);
			this.vo.getListaProfissionaisRemovidos().add(this.profissionalRemove.getId());
			if (Boolean.TRUE.equals(this.profissionalRemove.getIndResponsavel())) {
				if (!this.listaProfissionais.isEmpty()) {
					this.listaProfissionais.get(0).setIndResponsavel(Boolean.TRUE);
				}
			}
			if (Boolean.TRUE.equals(this.profissionalRemove.getIndRealizou())) {
				if (!this.listaProfissionais.isEmpty()) {
					this.listaProfissionais.get(0).setIndRealizou(Boolean.TRUE);
				}
			}
		}
		this.profissionalRemove = null;
	}

	public void removerAnestesia() {
		if (this.listaAnestesias.contains(this.anestesiaRemove)) {
			this.listaAnestesias.remove(this.anestesiaRemove);
			this.vo.getListaAnestesiasRemovidas().add(this.anestesiaRemove.getId());
			this.alterouAnestesia = true;
		}
		this.anestesiaRemove = null;
	}

	public List<AghEspecialidades> getPesquisarEspecialidades(String objPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarPorNomeOuSiglaEspSeqNulo(objPesquisa != null ? objPesquisa : null),getPesquisarEspecialidadesCount(objPesquisa));
	}

	public Long getPesquisarEspecialidadesCount(String objPesquisa) {
		return this.aghuFacade.pesquisarPorNomeOuSiglaEspSeqNuloCount(objPesquisa != null ? objPesquisa : null);
	}

	public List<AghAtendimentos> getPesquisarAtendimentos(String objPesquisa) {
		final Integer atdSeq = this.vo.getCirurgia().getAtendimento() != null ? this.vo.getCirurgia().getAtendimento().getSeq() : null;
		final Integer pacCodigo = this.vo.getCirurgia().getPaciente().getCodigo();
		Date dthrInicioCirg = this.vo.getCirurgia().getDataInicioCirurgia();
		return this.returnSGWithCount(this.aghuFacade.pesquisarAtendimentoRegistroCirurgiaRealizada(atdSeq, pacCodigo, dthrInicioCirg),getPesquisarAtendimentosCount(objPesquisa));
	}

	public Integer getPesquisarAtendimentosCount(String objPesquisa) {
		final Integer atdSeq = this.vo.getCirurgia().getAtendimento() != null ? this.vo.getCirurgia().getAtendimento().getSeq() : null;
		final Integer pacCodigo = this.vo.getCirurgia().getPaciente().getCodigo();
		Date dthrInicioCirg = this.vo.getCirurgia().getDataInicioCirurgia();
		return this.aghuFacade.pesquisarAtendimentoRegistroCirurgiaRealizadaCount(atdSeq, pacCodigo, dthrInicioCirg);
	}

	public List<MbcDestinoPaciente> getPesquisarDestinoPaciente(String objPesquisa) {
		return this.returnSGWithCount(this.blocoCirurgicoFacade.pesquisarDestinoPacienteAtivoPorSeqOuDescricao(objPesquisa, true, null, MbcDestinoPaciente.Fields.SEQ),getPesquisarDestinoPacienteCount(objPesquisa));
	}

	public Long getPesquisarDestinoPacienteCount(String objPesquisa) {
		return this.blocoCirurgicoFacade.pesquisarDestinoPacienteAtivoPorSeqOuDescricaoCount(objPesquisa, null);
	}

	public List<MbcSalaCirurgica> getPesquisarSalasCirurgicas(String objPesquisa) {
		Short unfSeq = this.vo != null && this.vo.getCirurgia().getUnidadeFuncional() != null ? this.vo.getCirurgia().getUnidadeFuncional().getSeq() : null;
		Short seqpSala = objPesquisa != null && StringUtils.isNotEmpty(objPesquisa) ? Short.valueOf(objPesquisa) : null;
		return this.returnSGWithCount(this.blocoCirurgicoCadastroApoioFacade.buscarSalaCirurgica(seqpSala, unfSeq, null, null, null, null, Boolean.TRUE, MbcSalaCirurgica.Fields.ID_SEQP),getPesquisarSalasCirurgicasCount(objPesquisa));
	}

	public Long getPesquisarSalasCirurgicasCount(String objPesquisa) {
		Short unfSeq = this.vo != null && this.vo.getCirurgia().getUnidadeFuncional() != null ? this.vo.getCirurgia().getUnidadeFuncional().getSeq() : null;
		String nome = objPesquisa != null && StringUtils.isNotEmpty(objPesquisa) ? objPesquisa : null;
		return this.blocoCirurgicoCadastroApoioFacade.buscarSalaCirurgicaCount(null, unfSeq, nome, null, null, null);
	}

	public List<FatConvenioSaudePlano> getPesquisarConvenioSaudePlanos(final String objPesquisa) {
		return this.returnSGWithCount(this.faturamentoApoioFacade.pesquisarConvenioSaudePlanos((String) objPesquisa),getPesquisarCountConvenioSaudePlanos(objPesquisa));
	}

	public Long getPesquisarCountConvenioSaudePlanos(final String objPesquisa) {
		final String strPesquisa = (String) objPesquisa;
		return this.faturamentoApoioFacade.pesquisarCountConvenioSaudePlanos(strPesquisa);
	}

	public void selecionarPlanoConvenio() {
		if (this.planoId != null && this.convenioId != null) {
			final FatConvenioSaudePlano plano = this.faturamentoApoioFacade.obterPlanoPorId(this.planoId, this.convenioId);
			if (plano == null) {
				this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_CONVENIO_PLANO_NAO_ENCONTRADO", this.convenioId, this.planoId);
			}
			this.atribuirPlano(plano);
		}
	}

	public void atribuirPlano(final FatConvenioSaudePlano plano) {
		if (plano != null) {
			this.plano = plano;
			this.convenioId = plano.getConvenioSaude().getCodigo();
			this.planoId = plano.getId().getSeq();
			this.vo.getCirurgia().setConvenioSaudePlano(plano);
		} else {
			this.plano = null;
			this.convenioId = null;
			this.planoId = null;
			this.vo.getCirurgia().setConvenioSaudePlano(null);
		}
		this.validaOrigemComPlano();
	}

	public void atribuirPlano() {
		if (this.plano != null) {
			this.convenioId = this.plano.getConvenioSaude().getCodigo();
			this.planoId = this.plano.getId().getSeq();
		} else {
			this.plano = null;
			this.convenioId = null;
			this.planoId = null;
		}
	}

	public boolean isPossuiProntuarioCadastrado() {
		AipPacientes paciente = this.vo.getCirurgia().getPaciente();
		return paciente != null && paciente.getProntuario() != null;
	}

	public boolean isDigitaNotaSalaOrigemForaAmbulatorio() {
		final boolean validacaoOrigem = this.getDigitacaoConfirmadaAposGravar() && 
				!DominioOrigemPacienteCirurgia.A.equals(this.vo.getCirurgia().getOrigemPacienteCirurgia()) 
				&& plano.getConvenioSaude().getGrupoConvenio() == DominioGrupoConvenio.S
				&& this.vo.getCirurgia().getDigitaNotaSala();
		final boolean validacaoFaturamento = validarAtendimentoFaturamento();
		if (validacaoOrigem || validacaoFaturamento) {
			return true;
		}
		return false;
	}

	public boolean isDigitaNotaSalaOrigemAmbulatorio() {
		final boolean validacao1 = this.getDigitacaoConfirmadaAposGravar() && 
				DominioOrigemPacienteCirurgia.A.equals(this.vo.getCirurgia().getOrigemPacienteCirurgia()) 
				&& plano.getConvenioSaude().getGrupoConvenio() == DominioGrupoConvenio.S
				&& this.vo.getCirurgia().getDigitaNotaSala();
		final boolean validacao2 = validarAtendimentoFaturamento();
		if (validacao1 || validacao2) {
			return true;
		}
		return false;
	}

	public boolean isHabilitarDescricaoCirurgica() {
		List<MbcDescricaoCirurgica> listaDescricaoCirurgica = this.blocoCirurgicoFacade.listarDescricaoCirurgicaPorSeqCirurgia(this.vo.getCirurgia().getSeq());
		List<PdtDescricao> listaPdtDescricao = this.blocoCirurgicoProcDiagTerapFacade.listarDescricaoPorSeqCirurgia(this.vo.getCirurgia().getSeq());
		return (!listaDescricaoCirurgica.isEmpty() || !listaPdtDescricao.isEmpty());
	}
	
	public boolean validarAtendimentoFaturamento() {
		MbcCirurgias cirurgia = this.blocoCirurgicoFacade.obterCirurgiaAtendimentoCancelada(this.vo.getCirurgia().getSeq());
		FatProcedAmbRealizado fatProcedAmbRealizado = this.faturamentoFacade.obterProcedAmbPorCirurgiaCancelada(this.vo.getCirurgia().getSeq());
		return (cirurgia != null && fatProcedAmbRealizado != null) ? true : false;
	}
	
	/*
	 * Prepara a URL para o botao Descricao Cirurgica
	 * Caso algum erro de negocio ocorra durante a criacao da URL, 
	 * este fica guardado para ser apresentado quando o botao for clicado. 
	 */
	public void visualizarDescricaoCirurgicaOuPDT(Integer seq) {
		if (seq != null) {
			try {
				String crgSeqP = "crgSeq=" + seq;
				String tipoRelatorio = this.blocoCirurgicoFacade.mbcImpressao(seq);
				if(tipoRelatorio.equals("CIR")) {
					urlRelatorioCirurgia = RELATORIO_CIRURGIA + crgSeqP + RELATORIO_PDT_VOLTAR_PARA;
				} else  if(tipoRelatorio.equals("PDT")) {
					urlRelatorioCirurgia = RELATORIO_PDT + crgSeqP + RELATORIO_PDT_VOLTAR_PARA;
				}
			} catch (ApplicationBusinessException e) {
				erroUsuarioNaoPermitidoDescricaoPDT = e;
				//apresentarExcecaoNegocio(e);
			}
		}	
	}
	
	public void acionarDescricaoCirurgica() {
		if (this.erroUsuarioNaoPermitidoDescricaoPDT != null) {
			apresentarExcecaoNegocio(erroUsuarioNaoPermitidoDescricaoPDT);
		} else {
			RequestContext.getCurrentInstance().execute("jsExecutaBotaoDescricaoCirurgica()");
		}
	}

	public void setListaProcedimentoSus(CirurgiaTelaProcedimentoVO procedimentoVO) {
		if(procedimentoVO != null){ 
			try {
				this.setProcedimentoSelecionado(procedimentoVO); // Armazena o procedimento selecionado.
				this.listaCodigoProcedimentos = this.blocoCirurgicoFacade.listarCodigoProcedimentosSUS(this.procedimentoAdd.getId().getPciSeq(),
						this.procedimentoAdd.getId().getEspSeq(), this.vo.getCirurgia().getOrigemPacienteCirurgia());
			} catch (ApplicationBusinessException e) {
				super.apresentarExcecaoNegocio(e);
			}
		}
	}

	public void selecionarPhi(Integer phiSeq) {
		try {
			this.getProcedimentoSelecionado().setPhiSeq(phiSeq);
			// Chama a PROCEDURE MBCP_POPULA_PHI
			this.blocoCirurgicoFacade.popularProcedimentoHospitalarInterno(this.getProcedimentoSelecionado(), this.vo.getCirurgia());
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public void validaOrigemComPlano(){
		if(this.plano != null && DominioTipoPlano.A.equals(this.plano.getIndTipoPlano()) && 
				DominioOrigemPacienteCirurgia.I.equals(this.vo.getCirurgia().getOrigemPacienteCirurgia())){
			this.apresentarMsgNegocio(Severity.ERROR, "MBC_00506");
			this.setOrigemOk(Boolean.FALSE);
			return;
		} else if (DominioTipoPlano.I.equals(this.plano.getIndTipoPlano()) && 
				this.vo.getCirurgia() != null && DominioOrigemPacienteCirurgia.A.equals(this.vo.getCirurgia().getOrigemPacienteCirurgia())){
			this.apresentarMsgNegocio(Severity.ERROR, "MBC_00507");
			this.setOrigemOk(Boolean.FALSE);
			return;
		}
		this.setOrigemOk(Boolean.TRUE);
	}
	
	private DominioTipoPlano obterDominioTipoPlano() {
		DominioTipoPlano plano;
		if (this.plano != null && this.plano.getIndTipoPlano() == null) {
			FatConvenioSaudePlano fcsp = this.faturamentoFacade
					.obterConvenioSaudePlanoPeloAtendimentoDaConsulta(this.vo
							.getCirurgia().getAtendimento().getSeq());
			plano = fcsp.getIndTipoPlano();
		} else {
			plano = this.plano.getIndTipoPlano();
		}
		return plano;
	}	
	
	public String redirecionarPesquisaFonetica() {	
		this.pesquisaPacienteController.setCameFrom(PAGE_REGISTRO_CIRURGIA_REALIZADA);
		return PESQUISA_FONETICA;
		 
	}
	
	public boolean habilitarBotaoConfirmar(){
		MbcCirurgias cirurgia = this.blocoCirurgicoFacade.obterCirurgiaAtendimentoCancelada(this.vo.getCirurgia().getSeq());
		return  cirurgia != null ? true : false;
	}
	
	protected Boolean getOrigemOk() {return origemOk;}
	protected void setOrigemOk(Boolean origemOk) {	this.origemOk = origemOk;}
	public boolean isSliderCirurgiaAberto() {	return sliderCirurgiaAberto;	}
	public void setSliderCirurgiaAberto(boolean sliderCirurgiaAberto) {	this.sliderCirurgiaAberto = sliderCirurgiaAberto;	}
	public boolean isSliderProcedimentoAberto() {	return sliderProcedimentoAberto;	}
	public void setSliderProcedimentoAberto(boolean sliderProcedimentoAberto) {	this.sliderProcedimentoAberto = sliderProcedimentoAberto;	}
	public CirurgiaTelaVO getVo() {	return vo;	}
	public boolean isSliderProfissionaisAberto() {	return sliderProfissionaisAberto;	}
	public Boolean getDigitacaoConfirmada() {	return digitacaoConfirmada;	}
	public void setSliderProfissionaisAberto(boolean sliderProfissionaisAberto) {	this.sliderProfissionaisAberto = sliderProfissionaisAberto;	}
	public boolean isSliderAnestesiasAberto() {		return sliderAnestesiasAberto;	}
	public void setSliderAnestesiasAberto(boolean sliderAnestesiasAberto) {		this.sliderAnestesiasAberto = sliderAnestesiasAberto;	}
	public boolean isEmEdicao() {		return emEdicao;	}
	public void setEmEdicao(boolean emEdicao) {		this.emEdicao = emEdicao;	}
	public Integer getCrgSeq() {		return crgSeq;	}
	public void setCrgSeq(Integer crgSeq) {		this.crgSeq = crgSeq;	}
	public String getVoltarPara() {		return voltarPara;	}
	public void setVoltarPara(String voltarPara) {		this.voltarPara = voltarPara;	}
	public FatConvenioSaudePlano getPlano() {		return plano;	}
	public void setPlano(FatConvenioSaudePlano plano) {		this.plano = plano;	}
	public Short getConvenioId() {		return convenioId;	}
	public void setConvenioId(Short convenioId) {		this.convenioId = convenioId;	}
	public Byte getPlanoId() {		return planoId;	}
	public void setPlanoId(Byte planoId) {		this.planoId = planoId;	}
	public IFaturamentoApoioFacade getFaturamentoApoioFacade() {		return faturamentoApoioFacade;	}
	public void setFaturamentoApoioFacade(IFaturamentoApoioFacade faturamentoApoioFacade) {		this.faturamentoApoioFacade = faturamentoApoioFacade;	}
	public List<CirurgiaTelaProcedimentoVO> getListaProcedimentos() {		return listaProcedimentos;	}
	public void setListaProcedimentos(List<CirurgiaTelaProcedimentoVO> listaProcedimentos) {		this.listaProcedimentos = listaProcedimentos;	}
	public AghEspecialidades getEspecialidadeProcedimentoAdd() {		return especialidadeProcedimentoAdd;	}
	public void setEspecialidadeProcedimentoAdd(AghEspecialidades especialidadeProcedimentoAdd) {		this.especialidadeProcedimentoAdd = especialidadeProcedimentoAdd;	}
	public VMbcProcEsp getProcedimentoAdd() {		return procedimentoAdd;	}
	public void setProcedimentoAdd(VMbcProcEsp procedimentoAdd) {		this.procedimentoAdd = procedimentoAdd;	}
	public AghCid getCidAdd() {		return cidAdd;	}
	public void setCidAdd(AghCid cidAdd) {		this.cidAdd = cidAdd;	}
	public CirurgiaTelaProcedimentoVO getProcedimentoRemove() {		return procedimentoRemove;	}
	public void setProcedimentoRemove(CirurgiaTelaProcedimentoVO procedimentoRemove) {		this.procedimentoRemove = procedimentoRemove;	}
	public CirurgiaTelaProcedimentoVO getProcedimentoSelecionado() {		return procedimentoSelecionado;	}
	public void setProcedimentoSelecionado(CirurgiaTelaProcedimentoVO procedimentoSelecionado) {		this.procedimentoSelecionado = procedimentoSelecionado;}
	public String getCodigoSUSProcedimento() {		return codigoSUSProcedimento;	}
	public void setCodigoSUSProcedimento(String codigoSUSProcedimento) {		this.codigoSUSProcedimento = codigoSUSProcedimento;	}
	public BigDecimal getValorCadastralProcedimento() {		return valorCadastralProcedimento;	}
	public void setValorCadastralProcedimento(BigDecimal valorCadastralProcedimento) {		this.valorCadastralProcedimento = valorCadastralProcedimento;	}
	public Short getValorPermProcedimento() {		return valorPermProcedimento;	}
	public void setValorPermProcedimento(Short valorPermProcedimento) {		this.valorPermProcedimento = valorPermProcedimento;	}
	public Integer getPhiSeqProcedimento() {		return phiSeqProcedimento;	}
	public void setPhiSeqProcedimento(Integer phiSeqProcedimento) {		this.phiSeqProcedimento = phiSeqProcedimento;	}
	public List<CirurgiaTelaAnestesiaVO> getListaAnestesias() {		return listaAnestesias;	}
	public void setListaAnestesias(List<CirurgiaTelaAnestesiaVO> listaAnestesias) {		this.listaAnestesias = listaAnestesias;	}
	public MbcTipoAnestesias getTipoAnestesiaAdd() {		return tipoAnestesiaAdd;	}
	public void setTipoAnestesiaAdd(MbcTipoAnestesias tipoAnestesiaAdd) {		this.tipoAnestesiaAdd = tipoAnestesiaAdd;	}
	public boolean isSituacaoProcedimentoAdd() {		return situacaoProcedimentoAdd;	}
	public void setSituacaoProcedimentoAdd(boolean situacaoProcedimentoAdd) {		this.situacaoProcedimentoAdd = situacaoProcedimentoAdd;	}
	public Byte getQuantidadeProcedimentoAdd() {		return quantidadeProcedimentoAdd;	}
	public void setQuantidadeProcedimentoAdd(Byte quantidadeProcedimentoAdd) {		this.quantidadeProcedimentoAdd = quantidadeProcedimentoAdd;	}
	public boolean isProcedimentoAddEmEdicao() {		return procedimentoAddEmEdicao;	}
	public void setProcedimentoAddEmEdicao(boolean procedimentoAddEmEdicao) {		this.procedimentoAddEmEdicao = procedimentoAddEmEdicao;	}
	public boolean isProcedimentoEmEdicaoPossuiPhi() {		return procedimentoEmEdicaoPossuiPhi;	}
	public void setProcedimentoEmEdicaoPossuiPhi(boolean procedimentoEmEdicaoPossuiPhi) {		this.procedimentoEmEdicaoPossuiPhi = procedimentoEmEdicaoPossuiPhi;	}
	public boolean isIndPrincipalProcedimentoAdd() {		return indPrincipalProcedimentoAdd;	}
	public void setIndPrincipalProcedimentoAdd(boolean indPrincipalProcedimentoAdd) {		this.indPrincipalProcedimentoAdd = indPrincipalProcedimentoAdd;	}
	public List<CirurgiaTelaProfissionalVO> getListaProfissionais() {		return listaProfissionais;	}
	public void setListaProfissionais(List<CirurgiaTelaProfissionalVO> listaProfissionais) {		this.listaProfissionais = listaProfissionais;	}
	public AgendaProcedimentoPesquisaProfissionalVO getProfissionalAdd() {		return profissionalAdd;	}
	public void setProfissionalAdd(AgendaProcedimentoPesquisaProfissionalVO profissionalAdd) {		this.profissionalAdd = profissionalAdd;	}
	public CirurgiaTelaProfissionalVO getProfissionalRemove() {		return profissionalRemove;	}
	public void setProfissionalRemove(CirurgiaTelaProfissionalVO profissionalRemove) {		this.profissionalRemove = profissionalRemove;	}
	public List<CirurgiaCodigoProcedimentoSusVO> getListaCodigoProcedimentos() {		return listaCodigoProcedimentos;	}
	public void setListaCodigoProcedimentos(List<CirurgiaCodigoProcedimentoSusVO> listaCodigoProcedimentos) {		this.listaCodigoProcedimentos = listaCodigoProcedimentos;	}
	public AlertaModalVO getAlertaVO() {		return alertaVO;	}
	public void setAlertaVO(AlertaModalVO alertaVO) {		this.alertaVO = alertaVO;	}
	public boolean isExibirModalAlertaGravar() {		return exibirModalAlertaGravar;	}
	public void setExibirModalAlertaGravar(boolean exibirModalAlertaGravar) {	this.exibirModalAlertaGravar = exibirModalAlertaGravar;	}
	public Integer getSeqContaHospitalar() {return seqContaHospitalar;}
	public void setSeqContaHospitalar(Integer seqContaHospitalar) {	this.seqContaHospitalar = seqContaHospitalar;}
	public Long getSeqCodigoProcedimento() {return seqCodigoProcedimento;}
	public void setSeqCodigoProcedimento(Long seqCodigoProcedimento) {this.seqCodigoProcedimento = seqCodigoProcedimento;}
	public Short getCpgCphCspCnvCodigo() {	return cpgCphCspCnvCodigo;}
	public void setCpgCphCspCnvCodigo(Short cpgCphCspCnvCodigo) {this.cpgCphCspCnvCodigo = cpgCphCspCnvCodigo;}
	public Short getCpgGrcSeq() {return cpgGrcSeq;}
	public void setCpgGrcSeq(Short cpgGrcSeq) {	this.cpgGrcSeq = cpgGrcSeq;	}
	public Byte getCpgCphCspSeq() {return cpgCphCspSeq;}
	public void setCpgCphCspSeq(Byte cpgCphCspSeq) {		this.cpgCphCspSeq = cpgCphCspSeq;	}
	public String getVoltarParaTela() {		return voltarParaTela;	}
	public void setVoltarParaTela(String voltarParaTela) { 		this.voltarParaTela = voltarParaTela; 	}
	public Boolean getDigitacaoConfirmadaAposGravar() {	return digitacaoConfirmadaAposGravar;	}
	public void setDigitacaoConfirmadaAposGravar(	Boolean digitacaoConfirmadaAposGravar) {	this.digitacaoConfirmadaAposGravar = digitacaoConfirmadaAposGravar;	}
	public Integer getProntuario() {return prontuario;	}
	public void setProntuario(Integer prontuario) {	this.prontuario = prontuario;	}
	public boolean isAlterarProntuario() {		return alterarProntuario;	}
	public void setAlterarProntuario(boolean alterarProntuario) {		this.alterarProntuario = alterarProntuario;	}
	public Integer getPacCodigoFonetica() {		return pacCodigoFonetica;	}
	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {		this.pacCodigoFonetica = pacCodigoFonetica;	}
	public String getNomePaciente() {		return nomePaciente;	}
	public void setNomePaciente(String nomePaciente) {		this.nomePaciente = nomePaciente;	}
	public MbcProcEspPorCirurgiasId getId() {		return id;	}
	public void setId(MbcProcEspPorCirurgiasId id) {		this.id = id;	}
	public AipPacientes getPaciente() {		return paciente;	}
	public void setPaciente(AipPacientes paciente) {		this.paciente = paciente;	}
	public void setVo(CirurgiaTelaVO vo) {		this.vo = vo;	}
	public boolean isConfirmarDigitacaoNota() {	return confirmarDigitacaoNota;	}
	public void setConfirmarDigitacaoNota(boolean confirmarDigitacaoNota) {	this.confirmarDigitacaoNota = confirmarDigitacaoNota;}

	public String getUrlRelatorioCirurgia() {
		return urlRelatorioCirurgia;
	}

	public void setUrlRelatorioCirurgia(String urlRelatorioCirurgia) {
		this.urlRelatorioCirurgia = urlRelatorioCirurgia;
	}

	public boolean isAlterouAnestesia() {
		return alterouAnestesia;
	}

	public void setAlterouAnestesia(boolean alterouAnestesia) {
		this.alterouAnestesia = alterouAnestesia;
	}

	public CirurgiaTelaAnestesiaVO getAnestesiaRemove() {
		return anestesiaRemove;
	}

	public void setAnestesiaRemove(CirurgiaTelaAnestesiaVO anestesiaRemove) {
		this.anestesiaRemove = anestesiaRemove;
	}
	
	public CirurgiaCodigoProcedimentoSusVO getCodigoProcedimentoVO() {
		return codigoProcedimentoVO;
	}

	public void setCodigoProcedimentoVO(CirurgiaCodigoProcedimentoSusVO codigoProcedimentoVO) {
		this.codigoProcedimentoVO = codigoProcedimentoVO;
	}

	public boolean isCodigoProcedimentoVOEdicao() {
		return codigoProcedimentoVOEdicao;
	}

	public void setCodigoProcedimentoVOEdicao(boolean codigoProcedimentoVOEdicao) {
		this.codigoProcedimentoVOEdicao = codigoProcedimentoVOEdicao;

	}
	
	public boolean isHouveColisao() {
		return houveColisao;
	}
	
	public void setHouveColisao(boolean houveColisao) {
		this.houveColisao = houveColisao;
	}
	
	public boolean isProcedimentoFaturado() {
		return procedimentoFaturado;
	}
	
	public void setProcedimentoFaturado(boolean procedimentoFaturado) {
		this.procedimentoFaturado = procedimentoFaturado;
	}

}