package br.gov.mec.aghu.exames.pesquisa.action;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioRestricaoUsuario;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioSubTipoImpressaoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoLaudo;
import br.gov.mec.aghu.exames.action.CarregarArquivoLaudoResultadoExameController;
import br.gov.mec.aghu.exames.action.ConsultarResultadosNotaAdicionalController;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.business.PesquisaExamesExceptionCode;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesVO;
import br.gov.mec.aghu.exames.sismama.action.VerificaQuestoesSismamaController;
import br.gov.mec.aghu.exames.sismama.business.ISismamaFacade;
import br.gov.mec.aghu.exames.solicitacao.action.DetalharItemSolicitacaoExameController;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelExamesSolicitacao;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.action.ArvorePOLController;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;


@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
public class PesquisaExameController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(PesquisaExameController.class);
	private static final long serialVersionUID = 3661785096909101631L;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IExamesLaudosFacade examesLaudosFacade;

	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	@EJB
	private ISismamaFacade sismamaFacade;

	@Inject
	private PesquisaExamesPorPacienteController pesquisaExamesPorPacienteController;

	@Inject
	private PesquisaExamesPorSolicitanteController pesquisaExamesPorSolicitanteController;

	@Inject
	private CarregarArquivoLaudoResultadoExameController carregarArquivoLaudoResultadoExameController;

	@Inject
	private ConsultarResultadosNotaAdicionalController consultarResultadosNotaAdicionalController;

	@Inject
	private VerificaQuestoesSismamaController verificaQuestoesSismamaController;
	
	@Inject
	private DetalharItemSolicitacaoExameController detalharItemSolicitacaoExameController;
	
	@Inject
	private ArvorePOLController arvorePOLController;
	

	private Map<Integer, Vector<Short>> solicitacoes = new HashMap<Integer, Vector<Short>>();

	@EJB 
	private IPacienteFacade pacienteFacade;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	private Boolean alterarFiltroPesquisaExames = Boolean.TRUE;

	public enum TipoPesquisa { PACIENTE, SOLICITANTE; }

	private TipoPesquisa tipoPesquisa = null;

	private boolean filtroAberto = true;

	/*	fitro da tela de pesquisa	*/
	private PesquisaExamesFiltroVO filtro = new PesquisaExamesFiltroVO();
	private List<PesquisaExamesPacientesVO> listaPacientes = new ArrayList<PesquisaExamesPacientesVO>();
	private RapServidores servExame = new RapServidores();
	private Integer codigoSoeSelecionado;
	private Short iseSeqSelecionado;
	private String situacaoCodigoSelecionado;
	private String voltarPara;

	/** Prontuário da lista de pacientes da prescricao */
	private Integer prontuario;
	private String nomePaciente;
	private String nomeSocialPaciente;
	private AipPacientes paciente;

	/** Retorna para a lista de pacientes da prescricao */
	private boolean exibirBotaoVoltar = false;
	private boolean botaoImagens = false;
	private Integer pacCodigoFonetica;
	private Boolean isHist = Boolean.FALSE;
	private Boolean origemProntuarioOnline = Boolean.FALSE;//default é false
	private Boolean itensSelecionados = Boolean.FALSE;

    private DominioSimNao indMostraCanceladosInfo = DominioSimNao.N;
    private DominioRestricaoUsuario restricao = DominioRestricaoUsuario.TD;
    
    private Boolean conexaoBancoHist = Boolean.FALSE;

    /**Variaveis impressao de etiqueta*/
	private String mensagemConfirmacaoImpressaoEtiquetas;
	private List<AelAmostraItemExames> amostrasItemExames = new ArrayList<AelAmostraItemExames>();

    
	private String voltarDaTelaCancelar;
	
	private Boolean exibeModalConfirmacao = Boolean.FALSE;
	
	private static final String EXAMES_PESQUISAR_SOLIC_DIVERSOS = "exames-pesquisarSolicitacaoDiversos";
	private static final String PRESCRICAO_MEDICA_PESQUISA_LISTA_PACIENTES_INTERNADOS = "prescricaomedica-pesquisarListaPacientesInternados";
	private static final String PACIENTE_MEDICA_PESQUISA_PACIENTE_COMPONENTE = "paciente-pesquisaPacienteComponente";
	private static final String EXAMES_CANCELAR_EXAMES_RESPOSABILIDADE_SOLICITANTE = "exames-cancelarExamesResponsabilidadeSolicitante";
	private static final String EXAMES_PESQUISAR_RESPOSTA_QUESTIONARIO = "exames-pesquisaRespostaQuestionario";
	private static final String EXAMES_VISUALIZAR_QUESTIONARIO_SISMAMA_BIOPSIA = "exames-visualizarQuestionarioSismamaBiopsia";
	private static final String EXAMES_VERIFICA_QUESTOES_SISMAMA = "exames-verificaQuestoesSismama";
	private static final String EXAMES_DETALHES_ITEM_EXAMES = "exames-detalhesItemExames";
	private static final String EXAMES_DETALHAR_ITEM_SOLICITACAO_EXAME = "exames-detalharItemSolicitacaoExame";
	private static final String EXAMES_GERAR_PROTOCOLO = "exames-gerarProtocoloEntregaExames";
	private static final String VOLTAR_PARA_PESQUISA_EXAMES ="exames-pesquisaExames";
	private static final String SITUACAO_COLETADO_SOLICITANTE = "CS";
	private static final String SITUACAO_EM_COLETA = "EC";
	private static final String SITUACAO_A_COLETAR = "AC";
	
	public void inicio() {
		this.itensSelecionados = Boolean.FALSE;
		if (StringUtils.isNotBlank(this.voltarPara) && !this.voltarPara.equals(VOLTAR_PARA_PESQUISA_EXAMES)) {
			exibirBotaoVoltar = true;
		} else {
			setVoltarPara(VOLTAR_PARA_PESQUISA_EXAMES);
		}
		
		if (getProntuario() != null){
			paciente = pacienteFacade.obterPacientePorProntuario(prontuario);
			filtro.setProntuarioPac(paciente.getProntuario());
			filtro.setNomePacientePac(paciente.getNome());		
			pesquisar();
		}
		
		if(pacCodigoFonetica != null){
			AipPacientes pac = pacienteFacade.obterPacientePorCodigo(pacCodigoFonetica);
			paciente = pac;
			filtro.setProntuarioPac(pac.getProntuario());
			filtro.setNomePacientePac(pac.getNome());
			return;
		}
		if (arvorePOLController.getNodoSelecionado() != null){
			prontuario = arvorePOLController.getNodoSelecionado().getProntuario();
			paciente = pacienteFacade.obterPacientePorProntuario(prontuario);
			filtro.setProntuarioPac(paciente.getProntuario());
			filtro.setNomePacientePac(paciente.getNome());
			this.setAlterarFiltroPesquisaExames(false);
			pesquisar();
		}	
				
		if(alterarFiltroPesquisaExames == null){
			alterarFiltroPesquisaExames = Boolean.FALSE;
		}
		
		this.limparSelecao();
	}

	public void selecionarItemExame(Integer codigoSoeSelecionado, Short iseSeqSelecionado, String situacaoCodigo) {
		setCodigoSoeSelecionado(codigoSoeSelecionado);
		setIseSeqSelecionado(iseSeqSelecionado);
		setSituacaoCodigoSelecionado(situacaoCodigo);
	}

	public void selecionaItemExame(Integer codigoSoeSelecionado, Short iseSeqSelecionado, String situacaoCodigo) {
		this.itensSelecionados = Boolean.TRUE;
		this.selecionarItemExame(codigoSoeSelecionado, iseSeqSelecionado, situacaoCodigo);
		if(this.solicitacoes.containsKey(codigoSoeSelecionado)){
			if(this.solicitacoes.get(codigoSoeSelecionado).contains(iseSeqSelecionado)){

				this.solicitacoes.get(codigoSoeSelecionado).remove(iseSeqSelecionado);

				if(this.solicitacoes.get(codigoSoeSelecionado).size()==0){
					this.solicitacoes.remove(codigoSoeSelecionado);
				}
			}else{
				this.solicitacoes.get(codigoSoeSelecionado).add(iseSeqSelecionado);
			}
		}else{
			this.solicitacoes.put(codigoSoeSelecionado, new Vector<Short>());
			this.solicitacoes.get(codigoSoeSelecionado).add(iseSeqSelecionado);
		}
	}
	
	public void reimprimirAmostra() throws ApplicationBusinessException, UnknownHostException{
		reimprimirAmostra(null);
	}
	
	public void reimprimirAmostra(String nomeImpressora) throws ApplicationBusinessException, UnknownHostException {
		try {					
			AelSolicitacaoExames solicitacao = this.solicitacaoExameFacade.obterSolicitacaoExame(this.codigoSoeSelecionado);
						
			String nomeMicro = (nomeImpressora == null) ? getEnderecoRedeHostRemoto() : null;
			Boolean usuarioLogadoColetador = solicitacaoExameFacade.verificarUsuarioLogadoColetador(this.obterLoginUsuarioLogado());
			
			//Atualiza e imprime todas as amostras de um determinado item de solicitação e todos os itens de solitação de cada amostra.
			for (AelAmostraItemExames amostraItemExame : this.amostrasItemExames){
				List<AelItemSolicitacaoExames> listaItemSolicPorAmostra = this.solicitacaoExameFacade.buscarItensPorAmostra(amostraItemExame.getId().getIseSoeSeq(),amostraItemExame.getId().getAmoSeqp());
				for (AelItemSolicitacaoExames itemSolic : listaItemSolicPorAmostra){
					if (SITUACAO_A_COLETAR.equals(itemSolic.getSitCodigo())){
						this.solicitacaoExameFacade.atualizarItemSolicitacaEmColeta(itemSolic.getId().getSoeSeq(), itemSolic.getId().getSeqp(), nomeMicro);
						if (!DominioSituacaoAmostra.M.equals(amostraItemExame.getSituacao())){
							this.solicitacaoExameFacade.atualizarAmostraSolicitacaEmColeta(amostraItemExame.getId().getIseSoeSeq(), amostraItemExame.getId().getIseSeqp(), amostraItemExame.getId().getAmoSoeSeq(), amostraItemExame.getId().getAmoSeqp(), nomeMicro);
						}								
					}
					Boolean usuarioSolicitacaoColetador = solicitacaoExameFacade.verificarUsuarioSolicitanteColetador(itemSolic.getId().getSoeSeq(), itemSolic.getId().getSeqp());
					//Se foi um “coletador”  que passou a amostra para “Em Coleta” , então somente um “coletador” poderá reimprimir . 
					if (usuarioSolicitacaoColetador && !usuarioLogadoColetador){
						String descricaoUsual = this.solicitacaoExameFacade.obterDescricaoUsualExame(this.codigoSoeSelecionado, this.iseSeqSelecionado);
						throw new ApplicationBusinessException(PesquisaExamesExceptionCode.SOMENTE_COLETADOR_IMPRIMIR,descricaoUsual);
					}
				}

				nomeImpressora = this.solicitacaoExameFacade.reimprimirAmostra(solicitacao.getUnidadeFuncional(), this.codigoSoeSelecionado, amostraItemExame.getId().getAmoSeqp().shortValue(), nomeMicro, nomeImpressora) ;
			}
			pesquisar();
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ETIQUETAS_IMPRESSAS_SUCESSO_REIMPRIMIR_ETIQUETA",
					nomeImpressora);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
			javax.faces.application.FacesMessage.Severity severity = WebUtil.getSeverity(Severity.ERROR);
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);
			context.addMessage("Messages", new FacesMessage(severity, e.getMessage(), e.getMessage()));
		}
	}
	
	public void processaReimpressaoEtiquetas() {
		if (this.solicitacoes != null && !this.solicitacoes.isEmpty()) {
			if (!validaExamesSelecionados(solicitacoes)) {
				this.apresentarMsgNegocio(Severity.ERROR,"ERRO_MENU_EXAMES");
			} else {
				Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
				if (it.hasNext()) {
					Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
					Integer solicitacao = paramCLValores.getKey();
					Vector<Short> seqps = paramCLValores.getValue();
					AelItemSolicitacaoExames itemSolicitacaoExame =  this.solicitacaoExameFacade.obterItemSolicitacaoExamePorId(solicitacao, seqps.get(0));
					if (itemSolicitacaoExame!=null){ 
						this.amostrasItemExames =  this.solicitacaoExameFacade.buscarAelAmostraItemExamesAelAmostrasPorItemSolicitacaoExame(itemSolicitacaoExame);
						selecionarItemExame(solicitacao, seqps.get(0), itemSolicitacaoExame.getSitCodigo());
					}
				}
			}
		}
		//Pega todos os números de amostra dos exames para confirmação
		StringBuffer numeroAmostras = null;
		for (AelAmostraItemExames amostraItemExame : this.amostrasItemExames){
			if (numeroAmostras == null){
				numeroAmostras = new StringBuffer(amostraItemExame.getId().getAmoSoeSeq().toString()).append(StringUtils.leftPad(amostraItemExame.getId().getAmoSeqp().toString(), 3, '0'));
			}else{
				numeroAmostras.append(" e ").append(amostraItemExame.getId().getAmoSoeSeq().toString()).append(StringUtils.leftPad(amostraItemExame.getId().getAmoSeqp().toString(), 3, '0'));
			}
		}
		
		//Se situação do exame for  AC – A COLETAR,   então exibir confirmação de alteração de situação;
		if (SITUACAO_A_COLETAR.equals(this.situacaoCodigoSelecionado)){
			String descricaoUsual = this.solicitacaoExameFacade.obterDescricaoUsualExame(this.codigoSoeSelecionado, this.iseSeqSelecionado);
			this.setMensagemConfirmacaoImpressaoEtiquetas(new StringBuffer("A etiqueta da amostra ").append(numeroAmostras)
					.append(", ser\u00E1 impressa. Aten\u00e7\u00e3o! A situa\u00e7\u00e3o do exame ").append(descricaoUsual).append(" ser\u00E1 alterada de \u0027A Coletar\u0027 para \u0027Em Coleta\u0027. Confirmar a impress\u00E3o da etiqueta? ").toString());
		}else{
			this.setMensagemConfirmacaoImpressaoEtiquetas(new StringBuffer("A etiqueta da amostra ").append(numeroAmostras)
					.append(", ser\u00E1 impressa. Confirmar a impress\u00E3o da etiqueta?").toString());
		}
	}		
	
	public Boolean habilitarBotaoImprimirEtiqueta(){
		Boolean habilitar = Boolean.FALSE;
		if (validaExamesSelecionados(this.solicitacoes)){// Verifica se existe apenas um exame selecionado
			Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
			if (it.hasNext()) {
				Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
				Integer solicitacao = paramCLValores.getKey();
				Vector<Short> seqps = paramCLValores.getValue();
				AelItemSolicitacaoExames itemSolicitacaoExame =  this.solicitacaoExameFacade.obterItemSolicitacaoExamePorId(solicitacao, seqps.get(0));
				if(SITUACAO_A_COLETAR.equals(itemSolicitacaoExame.getSitCodigo())||
					   SITUACAO_EM_COLETA.equals(itemSolicitacaoExame.getSitCodigo())||
					   SITUACAO_COLETADO_SOLICITANTE.equals(itemSolicitacaoExame.getSitCodigo())){
					habilitar = Boolean.TRUE;
				}
			}
		}			
		return habilitar;
			
	}
	
	public void selecionarLiberados(){
		limparSelecao();
		int count = 0;
		for(PesquisaExamesPacientesResultsVO vo : pesquisaExamesPorPacienteController.getListaResultadoPesquisa()){
			if ("LI".equals(vo.getSituacaoCodigo())){
				selecionaItemExame(vo.getCodigoSoe(), vo.getIseSeq(), vo.getSituacaoCodigo());
				vo.setVerResultado(true);
				count++;
			}			
		}
		if (count > 1000) {
			limparSelecao();
			this.apresentarMsgNegocio(Severity.ERROR,"MSG_MAXIMO_MIL_EXAMES");
		}
	}

	public void limparSelecao(){
		this.itensSelecionados = Boolean.FALSE;
		selecionarItemExame(null, null, null);
		if(pesquisaExamesPorPacienteController.getListaResultadoPesquisa() != null){
			for(PesquisaExamesPacientesResultsVO vo : pesquisaExamesPorPacienteController.getListaResultadoPesquisa()){
				vo.setVerResultado(false);
			}
		}
		solicitacoes.clear();
	}
	
	public void selecionarPaciente() throws ApplicationBusinessException {
		limparSelecao();
		pesquisaExamesPorPacienteController.selecionarPaciente();
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String pesquisarSolicitacaoDiversos() {
		return EXAMES_PESQUISAR_SOLIC_DIVERSOS;
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void pesquisar() {
		this.aghuFacade.limparEntityManager();
		try {
            this.atualizarFiltro();

			selecionarItemExame(null, null, null);
			setSolicitacoes(new HashMap<Integer, Vector<Short>>());
			if (verificaExistenciaCriterioPesquisa()) { throw new ApplicationBusinessException(PesquisaExamesExceptionCode.AEL_00833); }
			if (((this.getFiltro().getServidorPac() != null && this.getFiltro().getServidorPac().getId()==null))) { throw new ApplicationBusinessException(PesquisaExamesExceptionCode.AEL_01180);			}
			if ((this.getFiltro().getConselhoSolic() == null && this.getFiltro().getNumeroConselhoSolic() != null) || (this.getFiltro().getConselhoSolic() != null && this.getFiltro().getNumeroConselhoSolic() == null)){
				throw new ApplicationBusinessException(PesquisaExamesExceptionCode.AEL_01064);
			}
			if (this.getFiltro().getNumeroSolicitacaoInfo() != null) {
				listaPacientes = this.pesquisaExamesFacade.buscarAipPacientesPorSolicExa(this.getFiltro().getNumeroSolicitacaoInfo());
				if (listaPacientes == null || listaPacientes.size()==0) {
					this.getFiltro().setConsultaPac(null);
					this.getFiltro().setProntuarioPac(null);
					this.getFiltro().setNomePacientePac(null);
					this.pesquisaExamesPorPacienteController.setListaResultadoPesquisa(null);
					throw new ApplicationBusinessException(PesquisaExamesExceptionCode.AEL_00845);
				} else {
					tipoPesquisa = TipoPesquisa.PACIENTE;
					this.getFiltro().setConsultaPac(listaPacientes.get(0).getConsulta());
					this.getFiltro().setProntuarioPac(listaPacientes.get(0).getProntuario());
					this.getFiltro().setNomePacientePac(listaPacientes.get(0).getNomePaciente());

					this.pesquisaExamesPorPacienteController.setFiltro(filtro);
					this.pesquisaExamesPorPacienteController.selecionarPaciente(listaPacientes.get(0).getCodigo(), this.getFiltro().getConsultaPac(), listaPacientes.get(0).getProntuario());
				}
			} else if (getFiltro().getNumeroAp() != null && getFiltro().getConfigExame() != null) { //nroAP
				listaPacientes = this.pesquisaExamesFacade.buscarAipPacientesPorNumeroAp(getFiltro().getNumeroAp(), getFiltro().getConfigExame());
				if (listaPacientes == null || listaPacientes.size()==0) {

					this.getFiltro().setConsultaPac(null);
					this.getFiltro().setProntuarioPac(null);
					this.getFiltro().setNomePacientePac(null);
					this.pesquisaExamesPorPacienteController.setListaResultadoPesquisa(null);
					throw new ApplicationBusinessException(PesquisaExamesExceptionCode.AEL_01505);
				} else {
					tipoPesquisa = TipoPesquisa.PACIENTE;
					this.getFiltro().setConsultaPac(listaPacientes.get(0).getConsulta());
					this.getFiltro().setProntuarioPac(listaPacientes.get(0).getProntuario());
					this.getFiltro().setNomePacientePac(listaPacientes.get(0).getNomePaciente());
					this.getFiltro().setNumeroSolicitacaoInfo(listaPacientes.get(0).getNumeroSolicitacaoInfo());
					this.pesquisaExamesPorPacienteController.setFiltro(filtro);
					this.pesquisaExamesPorPacienteController.selecionarPaciente(listaPacientes.get(0).getCodigo(), this.getFiltro().getConsultaPac(), listaPacientes.get(0).getProntuario());
				}
			} else if (this.getFiltro().getConsultaPac() != null){
				listaPacientes = this.pesquisaExamesFacade.buscarAipPacientesPorConsulta(this.getFiltro().getConsultaPac());
				if (listaPacientes == null || listaPacientes.size() == 0) {
					throw new ApplicationBusinessException(PesquisaExamesExceptionCode.AEL_00475);
				} else {
					tipoPesquisa = TipoPesquisa.PACIENTE;
					if (listaPacientes.get(0) != null && listaPacientes.get(0).getConsulta() != null) {
						this.getFiltro().setConsultaPac(listaPacientes.get(0).getConsulta());
						this.getFiltro().setProntuarioPac(listaPacientes.get(0).getProntuario());
						this.getFiltro().setNomePacientePac(listaPacientes.get(0).getNomePaciente());
						this.pesquisaExamesPorPacienteController.setFiltro(filtro);
						this.pesquisaExamesPorPacienteController.selecionarPaciente(listaPacientes.get(0).getCodigo(), listaPacientes.get(0).getConsulta(), listaPacientes.get(0).getProntuario());
					} else {
						this.getFiltro().setConsultaPac(null);
						this.getFiltro().setProntuarioPac(null);
						this.getFiltro().setNomePacientePac(null);
						this.pesquisaExamesPorPacienteController.setListaResultadoPesquisa(null);
					}
				}
			} else if (this.getFiltro().getServidorPac() != null) {
				listaPacientes = this.pesquisaExamesFacade.buscarAipPacientesPorServidor(this.getFiltro().getServidorPac().getId().getMatricula(), this.getFiltro().getServidorPac().getId().getVinCodigo());
				if (listaPacientes == null || listaPacientes.size() == 0) {
					throw new ApplicationBusinessException(PesquisaExamesExceptionCode.AEL_01272);
				} else {
					tipoPesquisa = TipoPesquisa.PACIENTE;
					this.getFiltro().setConsultaPac(null);
					this.pesquisaExamesPorPacienteController.setFiltro(filtro);
					this.pesquisaExamesPorPacienteController.selecionarPaciente(listaPacientes.get(0).getCodigo(), null, listaPacientes.get(0).getProntuario());
				}
			} else if (this.getFiltro().getProntuarioPac() != null || this.getFiltro().getLeitoPac() != null
					|| this.getFiltro().getAelUnffuncionalPac() != null || (this.getFiltro().getNomePacientePac()!=null && !this.getFiltro().getNomePacientePac().trim().equals(""))) {  
				listaPacientes = this.pesquisaExamesFacade.buscarAipPacientesPorParametros(this.getFiltro().getProntuarioPac(), this.getFiltro().getNomePacientePac(), this.getFiltro().getLeitoPac(), this.getFiltro().getAelUnffuncionalPac());
				if (listaPacientes == null || listaPacientes.size()==0) {
					throw new ApplicationBusinessException(PesquisaExamesExceptionCode.AEL_00475);
				} else {
					tipoPesquisa = TipoPesquisa.PACIENTE;
					this.getFiltro().setConsultaPac(null);
					this.pesquisaExamesPorPacienteController.setFiltro(filtro);
					this.pesquisaExamesPorPacienteController.selecionarPaciente(listaPacientes.get(0).getCodigo(), listaPacientes.get(0).getConsulta(), listaPacientes.get(0).getProntuario());
				}
			} else if (this.filtro.getConselhoSolic() != null 
					|| this.filtro.getNumeroConselhoSolic() != null 
					|| this.filtro.getServidorSolic() != null) { 
				Short vinCodigo = null;
				Integer matricula = null;
				String siglaConselho = null;
				String numeroConselho = null;
				Integer diasPermitidos = null;
				try {
					AghParametros param = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_SOL_EX_FIM_VINCULO_PERMITIDO);
					diasPermitidos = param.getVlrNumerico().intValue();
				} catch (ApplicationBusinessException e) {
					// Se o parametro nao foi criado / configurado. Entao usa o valor default do metodo de pesquisa.
					LOG.error(e.getMessage(),e);
				}
				if (this.filtro.getServidorSolic() != null) {
					vinCodigo = this.filtro.getServidorSolic().getId().getVinCodigo();
					matricula = this.filtro.getServidorSolic().getId().getMatricula();
				}
				if (this.filtro.getConselhoSolic() != null && this.filtro.getNumeroConselhoSolic() != null) {
					siglaConselho = this.filtro.getConselhoSolic().getSigla();
					numeroConselho = this.filtro.getNumeroConselhoSolic().toString();
				}
				servExame = this.registroColaboradorFacade.buscaVRapServSolExme(vinCodigo, matricula, diasPermitidos, numeroConselho, siglaConselho);
				this.pesquisaExamesPorPacienteController.setListaResultadoPesquisa(null);
				if (servExame==null) {
					this.pesquisaExamesPorSolicitanteController.setListaResultadoPesquisa(null);
				} else {
					tipoPesquisa = TipoPesquisa.SOLICITANTE;
					filtro.setServidorSolic(servExame);
					this.pesquisaExamesPorSolicitanteController.setFiltro(filtro);
					this.pesquisaExamesPorSolicitanteController.selecionarSolicitante(filtro);
				}
			}
			renderPesquisa();
		} catch (BaseException e) {
			this.getFiltro().setConsultaPac(null);
			tipoPesquisa = null;
			setFiltroAberto(true);
			this.pesquisaExamesPorPacienteController.setListaResultadoPesquisa(null);
			this.pesquisaExamesPorSolicitanteController.setListaResultadoPesquisa(null);
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void pesquisarEntregaExames() throws BaseException {
		this.aghuFacade.limparEntityManager();		
		try {
			this.atualizarFiltro();
			
			selecionarItemExame(null, null, null);
			setSolicitacoes(new HashMap<Integer, Vector<Short>>());

			if (verificaExistenciaCriterioPesquisa()) {
				throw new ApplicationBusinessException(PesquisaExamesExceptionCode.AEL_00833);
			}

			if (((this.getFiltro().getServidorPac() != null && this.getFiltro().getServidorPac().getId()==null))) {
				throw new ApplicationBusinessException(PesquisaExamesExceptionCode.AEL_01180);
			}

			if ((this.getFiltro().getConselhoSolic() == null && this.getFiltro().getNumeroConselhoSolic() != null) || (this.getFiltro().getConselhoSolic() != null && this.getFiltro().getNumeroConselhoSolic() == null)){
				throw new ApplicationBusinessException(PesquisaExamesExceptionCode.AEL_01064);
			}
			
			pesquisarNumSolicitacaoConselhoProntConsultaPac();

			renderPesquisa();
		} catch (ApplicationBusinessException e) {
			this.getFiltro().setConsultaPac(null);
			tipoPesquisa = null;
			setFiltroAberto(true);
			this.pesquisaExamesPorPacienteController.setListaResultadoPesquisa(null);
			this.pesquisaExamesPorSolicitanteController.setListaResultadoPesquisa(null);
			apresentarExcecaoNegocio(e);
		}
	}

	private void pesquisarNumSolicitacaoConselhoProntConsultaPac() throws BaseException {
		
		if (this.getFiltro().getNumeroSolicitacaoInfo() != null) {
			listaPacientes = this.pesquisaExamesFacade.buscarAipPacientesPorSolicExa(this.getFiltro().getNumeroSolicitacaoInfo());
			if (listaPacientes == null || listaPacientes.size()==0) {
				this.getFiltro().setConsultaPac(null);
				this.getFiltro().setProntuarioPac(null);
				this.getFiltro().setNomePacientePac(null);
				this.pesquisaExamesPorPacienteController.setListaResultadoPesquisa(null);
				throw new ApplicationBusinessException(PesquisaExamesExceptionCode.AEL_00845);
			} else {
				tipoPesquisa = TipoPesquisa.PACIENTE;
				this.getFiltro().setConsultaPac(listaPacientes.get(0).getConsulta());
				this.getFiltro().setProntuarioPac(listaPacientes.get(0).getProntuario());
				this.getFiltro().setNomePacientePac(listaPacientes.get(0).getNomePaciente());

				this.pesquisaExamesPorPacienteController.setFiltro(filtro);
				this.pesquisaExamesPorPacienteController.selecionarPacienteEntregaExames(listaPacientes.get(0).getCodigo(), this.getFiltro().getConsultaPac(), listaPacientes.get(0).getProntuario());
			}

		} else {
			pesquisarConselhoProntuarioConsultaPac();
		}
	}

	private void pesquisarConselhoProntuarioConsultaPac() throws BaseException {
		
		if (this.getFiltro().getConsultaPac() != null) {
			listaPacientes = this.pesquisaExamesFacade.buscarAipPacientesPorConsulta(this.getFiltro().getConsultaPac());

			if (listaPacientes == null || listaPacientes.size() == 0) {
				throw new ApplicationBusinessException(PesquisaExamesExceptionCode.AEL_00475);
			} else {
				tipoPesquisa = TipoPesquisa.PACIENTE;
				if (listaPacientes.get(0) != null && listaPacientes.get(0).getConsulta() != null) {
					this.getFiltro().setConsultaPac(listaPacientes.get(0).getConsulta());
					this.getFiltro().setProntuarioPac(listaPacientes.get(0).getProntuario());
					this.getFiltro().setNomePacientePac(listaPacientes.get(0).getNomePaciente());

					this.pesquisaExamesPorPacienteController.setFiltro(filtro);
					this.pesquisaExamesPorPacienteController.selecionarPacienteEntregaExames(listaPacientes.get(0).getCodigo(), listaPacientes.get(0).getConsulta(), listaPacientes.get(0).getProntuario());

				} else {
					this.getFiltro().setConsultaPac(null);
					this.getFiltro().setProntuarioPac(null);
					this.getFiltro().setNomePacientePac(null);

					this.pesquisaExamesPorPacienteController.setListaResultadoPesquisa(null);
				}
			}

		} else {
			pesquisarConselhoProntuarioPac();
		}
			
	}

	private void pesquisarConselhoProntuarioPac() throws BaseException {
		
		if (this.getFiltro().getProntuarioPac() != null || this.getFiltro().getLeitoPac() != null
			|| this.getFiltro().getAelUnffuncionalPac() != null || (this.getFiltro().getNomePacientePac()!=null && !this.getFiltro().getNomePacientePac().trim().equals(""))) {  

			listaPacientes = this.pesquisaExamesFacade.buscarAipPacientesPorParametros(this.getFiltro().getProntuarioPac(), this.getFiltro().getNomePacientePac(), this.getFiltro().getLeitoPac(), this.getFiltro().getAelUnffuncionalPac());
			if (listaPacientes == null || listaPacientes.size()==0) {
				throw new ApplicationBusinessException(PesquisaExamesExceptionCode.AEL_00475);
			} else {
				tipoPesquisa = TipoPesquisa.PACIENTE;
				this.getFiltro().setConsultaPac(null);
				this.pesquisaExamesPorPacienteController.setFiltro(filtro);
				this.pesquisaExamesPorPacienteController.selecionarPacienteEntregaExames(listaPacientes.get(0).getCodigo(), listaPacientes.get(0).getConsulta(), listaPacientes.get(0).getProntuario());
			}
		} else {
			pesquisarConselhoSolictacao();
		}
	}

	private void pesquisarConselhoSolictacao() throws BaseException {
		
		if (this.filtro.getConselhoSolic() != null || this.filtro.getNumeroConselhoSolic() != null 	|| this.filtro.getServidorSolic() != null) { 
				
			Short vinCodigo = null;
			Integer matricula = null;
			String siglaConselho = null;
			String numeroConselho = null;
			Integer diasPermitidos = null;

		    try {
				AghParametros param = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_SOL_EX_FIM_VINCULO_PERMITIDO);
				diasPermitidos = param.getVlrNumerico().intValue();

			} catch (ApplicationBusinessException e) {
				LOG.error(e.getMessage(),e);
			}

			if (this.filtro.getServidorSolic() != null) {
				vinCodigo = this.filtro.getServidorSolic().getId().getVinCodigo();
				matricula = this.filtro.getServidorSolic().getId().getMatricula();
			}

			if (this.filtro.getConselhoSolic() != null && this.filtro.getNumeroConselhoSolic() != null) {
				siglaConselho = this.filtro.getConselhoSolic().getSigla();
				numeroConselho = this.filtro.getNumeroConselhoSolic().toString();
			}
			servExame = this.registroColaboradorFacade.buscaVRapServSolExme(vinCodigo, matricula, diasPermitidos, numeroConselho, siglaConselho);
			this.pesquisaExamesPorPacienteController.setListaResultadoPesquisa(null);
			if (servExame==null) {
				this.pesquisaExamesPorSolicitanteController.setListaResultadoPesquisa(null);
			} else {
				tipoPesquisa = TipoPesquisa.SOLICITANTE;
				filtro.setServidorSolic(servExame);
				this.pesquisaExamesPorSolicitanteController.setFiltro(filtro);
				this.pesquisaExamesPorSolicitanteController.selecionarSolicitante(filtro);
			}
		}
	}

	public boolean isDisableButtonDetalheExame(){
		return codigoSoeSelecionado==null; 
	}

	public String downloadAnexo(Integer iseSoeSeq, Short iseSeqp)  {
		String resultado = this.carregarArquivoLaudoResultadoExameController.downloadArquivoLaudo(iseSoeSeq, iseSeqp);
		try {
			this.examesFacade.persistirVisualizacaoDownloadAnexo(iseSoeSeq, iseSeqp);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return resultado;
	}

	private boolean verificaExistenciaCriterioPesquisa(){
		if (this.getFiltro().getProntuarioPac() == null && this.getFiltro().getLeitoPac() == null&& this.getFiltro().getAelUnffuncionalPac() == null
				&& this.getFiltro().getServidorPac() == null&& this.getFiltro().getConsultaPac() == null&& this.getFiltro().getConselhoSolic() == null
				&& this.getFiltro().getNumeroConselhoSolic() == null&& this.getFiltro().getServidorSolic() == null&& this.getFiltro().getNumeroSolicitacaoInfo() == null
				&& (this.getFiltro().getNumeroAp() == null || this.getFiltro().getConfigExame() == null)&& (this.getFiltro().getNomePacientePac()==null || this.getFiltro().getNomePacientePac().trim().equals(""))	){
			return true;
		} else {
			return false;
		}
	}

	private void renderPesquisa() {
		setFiltroAberto(false);
		if (TipoPesquisa.PACIENTE.equals(this.tipoPesquisa)) {
			this.pesquisaExamesPorPacienteController.setListaPacientes(this.listaPacientes);
		} else if (TipoPesquisa.SOLICITANTE.equals(this.tipoPesquisa)){
			this.pesquisaExamesPorSolicitanteController.setServExame(servExame);
		}
	}

	/**
	 * Metodo que limpa os campos de filtro<br>
	 * na tele de pesquisa de exames.
	 */
	public void limpar() {
		setFiltro(new PesquisaExamesFiltroVO());
		tipoPesquisa = null;
		pacCodigoFonetica = null;
		paciente=null;
		nomePaciente=null;
		prontuario=null;
		setFiltroAberto(true);
		this.itensSelecionados = Boolean.FALSE;
        this.pesquisaExamesPorPacienteController.setListaPacientes(null);
        this.pesquisaExamesPorPacienteController.setCodigoConsulta(null);
        this.pesquisaExamesPorPacienteController.setCodigoPaciente(null);
        this.pesquisaExamesPorPacienteController.setFiltro(getFiltro());
        this.pesquisaExamesPorPacienteController.setListaResultadoPesquisa(null);
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(String parametro) {
		return this.returnSGWithCount(this.aghuFacade.obterUnidadesFuncionaisComLeitosEPodemSolicitarExames(parametro),this.obterUnidadesFuncionaisComLeitosEPodemSolicitarExamesCount(parametro));
	}
	
	private Long obterUnidadesFuncionaisComLeitosEPodemSolicitarExamesCount(String parametro){
		return this.aghuFacade.obterUnidadesFuncionaisComLeitosEPodemSolicitarExamesCount(parametro);
	}

	public List<RapServidores> obterServidorPac(String objPesquisa){
		try{
			return this.returnSGWithCount(registroColaboradorFacade.pesquisarServidorPorSituacaoAtivo(objPesquisa),obterServidorPacCount(objPesquisa));
		} catch (BaseException e) {
			LOG.error(e.getMessage(),e);
		} 
		return new ArrayList<RapServidores>();
	}

	public String voltar() {
		if(this.voltarPara != null){
			return this.voltarPara;
		}
		return PRESCRICAO_MEDICA_PESQUISA_LISTA_PACIENTES_INTERNADOS;
	}

	public String redirecionarPesquisaFonetica(){
		return PACIENTE_MEDICA_PESQUISA_PACIENTE_COMPONENTE;
	}

	public DominioOrigemAtendimento[] gerarItensSelectOrigemAtendimento() {
		DominioOrigemAtendimento[] lista = DominioOrigemAtendimento.values();
		
		List<DominioOrigemAtendimento> returnList = new LinkedList<DominioOrigemAtendimento>(); 
		for (DominioOrigemAtendimento dominioOrigemAtendimento : lista) {
			if (DominioOrigemAtendimento.T != dominioOrigemAtendimento) {
				returnList.add(dominioOrigemAtendimento);
			}
		}
		
		DominioOrigemAtendimento[] dominioTemp = new DominioOrigemAtendimento[]{};
		return  returnList.toArray(dominioTemp);
	}

	public boolean isPesquisaPaciente(){
		return TipoPesquisa.PACIENTE.equals(this.tipoPesquisa);}

	public boolean isPesquisaSolicitante(){
		return TipoPesquisa.SOLICITANTE.equals(this.tipoPesquisa);}

	public List<AelAgrpPesquisas> obterAgrpPesquisaPac(Object objPesquisa){
		return this.pesquisaExamesFacade.buscaAgrupamentosPesquisa(objPesquisa);}

	public List<AinLeitos> pesquisarLeito(String objPesquisa){
		return this.returnSGWithCount(this.pesquisaExamesFacade.obterLeitosAtivosPorUnf(objPesquisa, (this.getFiltro().getAelUnffuncionalPac()==null?null:this.getFiltro().getAelUnffuncionalPac().getSeq())),pesquisarLeitoCount(objPesquisa));
	}
	
	public void limparSBLeito(){
		this.filtro.setLeitoPac(null);
	}

	public List<AacConsultas> obtemListSituacaoExames() {
		return examesFacade.listarSituacaoExames();
	}

	public String verDetalhesExame(){
		return "detalhesExame";
	}

	public boolean isDisableButton() {
		if (this.codigoSoeSelecionado != null && this.iseSeqSelecionado != null) {
			return false;
		} else {
			return true;
		}
	}
	
	public String verResultadosNotHist(){
		isHist = Boolean.FALSE;
		return verResultados();
	}

	public String verResultados() {
		consultarResultadosNotaAdicionalController.setIsHist(getIsHist());
		if (this.solicitacoes != null && this.solicitacoes.size() > 0) {
			
			try{
				this.pesquisaExamesFacade.validaSituacaoExamesSelecionados(solicitacoes, this.conexaoBancoHist, Boolean.TRUE);
				consultarResultadosNotaAdicionalController.setVoltarPara(VOLTAR_PARA_PESQUISA_EXAMES);				
				
				if (this.pesquisaExamesFacade.permiteVisualizarLaudoMedico() || 
						this.pesquisaExamesFacade.permitevisualizarLaudoAtdExt() || 
						this.pesquisaExamesFacade.permitevisualizarLaudoSamis()) {
					
					consultarResultadosNotaAdicionalController.setSolicitacoes(solicitacoes);
					consultarResultadosNotaAdicionalController.setTipoLaudo(DominioTipoImpressaoLaudo.LAUDO_PAC_EXTERNO);
					consultarResultadosNotaAdicionalController.setDominioSubTipoImpressaoLaudo(DominioSubTipoImpressaoLaudo.LAUDO_GERAL);
					
					return "exames-consultarResultadoNotaAdicional";
				}
				else {
					this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_PERMISSAO_USUARIO");
				}
			}
			catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		return null;
	}

	public String redirecionarRespos() {
		if (this.solicitacoes != null && !this.solicitacoes.isEmpty()) {
			if (!this.pesquisaExamesFacade.validaQuantidadeExamesSelecionados(solicitacoes)) {
				this.apresentarMsgNegocio(Severity.ERROR,"ERRO_SELECIONE_APENAS_UMA_SOLICITACAO_EXAME");
				return "";
			}
			Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
			if (it.hasNext()) {
				Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
				/*Item de solicitacao*/
				Integer solicitacao = paramCLValores.getKey();
				/*colecao de seqp da solicitacao acima*/
				Vector<Short> seqps = paramCLValores.getValue();
				selecionarItemExame(solicitacao, seqps.get(0), null);
			}
		}
		return EXAMES_DETALHES_ITEM_EXAMES;
	}

	public String detalhesExames() {
		if (this.solicitacoes != null && !this.solicitacoes.isEmpty()) {
			if (!validaExamesSelecionados(solicitacoes)) {
				this.apresentarMsgNegocio(Severity.ERROR,"ERRO_MENU_EXAMES");
				return null;
			} else {
				Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
				if (it.hasNext()) {
					Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
					/*Item de solicitacao*/
					Integer solicitacao = paramCLValores.getKey();
					/*colecao de seqp da solicitacao acima*/
					Vector<Short> seqps = paramCLValores.getValue();
					selecionarItemExame(solicitacao, seqps.get(0), null);
					detalharItemSolicitacaoExameController.setSeqp(iseSeqSelecionado);
					detalharItemSolicitacaoExameController.setSoeSeq(codigoSoeSelecionado);
				}
			}
		}
		detalharItemSolicitacaoExameController.setOrigemSolicDetalhamentoAmostras(Boolean.TRUE);
		return EXAMES_DETALHAR_ITEM_SOLICITACAO_EXAME;
	}

	/**
	 * Menu Cancelar Exames
	 * @return
	 */
	public String cancelarExames() {
		if (this.solicitacoes != null && !this.solicitacoes.isEmpty()) {
			if (!validaExamesSelecionados(solicitacoes)) {
				this.apresentarMsgNegocio(Severity.ERROR,"ERRO_MENU_EXAMES");
				return null;
			} else {
				Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
				if (it.hasNext()) {
					Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
					Integer solicitacao = paramCLValores.getKey();
					Vector<Short> seqps = paramCLValores.getValue();
					selecionarItemExame(solicitacao, seqps.get(0), this.situacaoCodigoSelecionado);
					if (!validaResponsavelExameSelecionado()) {
						this.apresentarMsgNegocio(Severity.ERROR,"AEL_01279",solicitacao);
						return null;
					}
					if (!this.validarSituacaoExameSelecionado(this.situacaoCodigoSelecionado)) {
						this.apresentarMsgNegocio(Severity.ERROR,"AEL_01278");
						return null;
					}
				}
			}
		}
		return EXAMES_CANCELAR_EXAMES_RESPOSABILIDADE_SOLICITANTE;
	}

	public boolean validarSituacaoExameSelecionado(final String situacaoCodigo) {
		return this.pesquisaExamesFacade.validarSituacaoExameSelecionado(situacaoCodigo);
	}

	public String redirecionarRespostaQuestionario() {
		if (this.solicitacoes != null && !this.solicitacoes.isEmpty()) {
			try {
				if (!this.pesquisaExamesFacade.validaQuantidadeExamesSelecionados(solicitacoes)) {
					this.apresentarMsgNegocio(Severity.ERROR,"ERRO_SELECIONE_APENAS_UMA_SOLICITACAO_EXAME");
					return null;
				}
				Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
				if (it.hasNext()) {
					Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
					/*Item de solicitacao*/
					Integer solicitacao = paramCLValores.getKey();
					/*colecao de seqp da solicitacao acima*/
					Vector<Short> seqps = paramCLValores.getValue();
					selecionarItemExame(solicitacao, seqps.get(0), null);
				}
				this.pesquisaExamesFacade.validarExamesComResposta(this.codigoSoeSelecionado, this.iseSeqSelecionado);
				return EXAMES_PESQUISAR_RESPOSTA_QUESTIONARIO;
			}
			catch(ApplicationBusinessException e){
				this.apresentarMsgNegocio(Severity.ERROR,"ERRO_SELECIONE_UMA_SOLICITACAO_COM_RESPOSTA_QUESTIONARIO");
				return null;
			}
		}
		return null;
	}

	/** Verifica se foi selecionado apenas um exame */
	private Boolean validaExamesSelecionados(Map<Integer, Vector<Short>> solicitacoes) {
		if (solicitacoes.size() > 1) {
			return false;
		} else if (solicitacoes.size() == 1) {
			Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
			if (it.hasNext()) {
				Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
				Vector<Short> seqps = paramCLValores.getValue();
				Iterator<Short> iterator = seqps.iterator();
				iterator.next();
				if (iterator.hasNext()) {
					return false;
				}
			}
		}
		return true;
	}

	/** Valida de o usuario é o responsavel do exame para cancelamento	 */
	public boolean validaResponsavelExameSelecionado() {
		boolean retorno = false;
		if (this.codigoSoeSelecionado != null && this.iseSeqSelecionado != null) {
			try{
				AelSolicitacaoExames solicitacao = examesFacade.obterAelSolicitacaoExamesPeloId(codigoSoeSelecionado);
				if (solicitacao != null && solicitacao.getServidor().getId().getMatricula().equals(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId().getMatricula())) {
					if (examesFacade.possuiExameCancelamentoSolicitante(codigoSoeSelecionado, iseSeqSelecionado)) {
						retorno = true;
					}
				}
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
				return false;
			}			
		}
		return retorno;
	}

	public String getUrlImpax() {
		Integer codPaciente = null;
		String accessiom= null; //pac_oru_acc_numbe
		String url = null;
		try {
			if(solicitacoes!=null) {
				Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
				if (it.hasNext()) {
					accessiom = getPacOruAccNumber(it);
				}	
				if(listaPacientes != null && !listaPacientes.isEmpty() 
						&& listaPacientes.get(0).getCodigo() != null){
					codPaciente = listaPacientes.get(0).getCodigo();
				}else if(pacCodigoFonetica !=null){
					codPaciente = pacCodigoFonetica;
				}
				if(codPaciente!=null && accessiom!=null){
					AghParametros paramverImgImpax = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_VER_IMAGENS_IMPAX);
					if(paramverImgImpax.getVlrNumerico().equals(BigDecimal.valueOf(1))){
						AghParametros paramEndWebImagens = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_ENDERECO_WEB_IMAGENS);
						botaoImagens = true;
						url = paramEndWebImagens.getVlrTexto()+codPaciente+"%26accession%3D" + accessiom;
					}
				}
			}
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
		return url;
	}
	
	public String gerarProtocolo() throws ApplicationBusinessException {
		String opcao = null;
		if(!solicitacoes.isEmpty()) {
			opcao = EXAMES_GERAR_PROTOCOLO;
		} else {
			this.apresentarMsgNegocio(Severity.ERROR,"NENHUM_ITEM_SELECIONADO");
		}
		return opcao;
	}

	private String getPacOruAccNumber(Iterator it) {
		String accessiom;
		Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
		Integer solicitacao = paramCLValores.getKey();
		Vector<Short> seqps = paramCLValores.getValue();
		AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
		id.setSoeSeq(solicitacao);
		id.setSeqp(seqps.get(0));
		AelItemSolicitacaoExames itemSolicitacaoExames = this.examesFacade.obteritemSolicitacaoExamesPorChavePrimaria(id);
		accessiom = itemSolicitacaoExames.getPacOruAccNumber();
		return accessiom;
	}

	public List<RapServidores> obterServidorSolic(String objPesquisa){
		return this.returnSGWithCount(pesquisaExamesFacade.obterServidorSolic((String) objPesquisa),obterServidorSolicCount(objPesquisa));	
	}
	
    public int controlaAccordion(){
    	int retorno = 0;
    	if(!filtroAberto){
    		retorno = -1;
    	}
        return retorno;
    }

	public Long obterServidorPacCount(String param) throws BaseException {
		try {
			return this.registroColaboradorFacade.pesquisarServidorPorSituacaoAtivoCount(param);
		} catch (BaseException e) {
			LOG.error(e.getMessage(),e);
		} 
		return Long.valueOf(0);
	}

	public Integer pesquisarUnidadeFuncionalCount(String param) {
		return this.aghuFacade.
		pesquisarUnidadeFuncionalPorCodigoEDescricaoCount((String) param);
	}

	public Long pesquisarLeitoCount(String objPesquisa){
		return this.pesquisaExamesFacade.
		obterLeitosAtivosPorUnfCount(objPesquisa, 
				(this.getFiltro().getAelUnffuncionalPac()==null?
						null:this.getFiltro().getAelUnffuncionalPac().getSeq()));
	}

	public Integer obterServidorSolicCount(String objPesquisa){
		return pesquisaExamesFacade.obterServidorSolicCount((String) objPesquisa);	
	}

	// Metódo para Suggestion Box de Conselhos
	public List<RapConselhosProfissionais> pesquisarConselhos(String objPesquisa){
		return this.returnSGWithCount(cadastrosBasicosFacade.pesquisarConselhosAtivosPorDescricao(objPesquisa),pesquisarConselhosCount(objPesquisa));
	}

	public Long pesquisarConselhosCount(String objPesquisa){
		return cadastrosBasicosFacade.pesquisarConselhosAtivosPorDescricaoCount(objPesquisa);
	}

	// Metódo para Suggestion Box de Exames
	public List<VAelExamesSolicitacao> pesquisarExames(String objPesquisa){
		return this.returnSGWithCount(this.pesquisaExamesFacade.obterNomeExames(objPesquisa),pesquisarExamesCount(objPesquisa));
	}

	public Long pesquisarExamesCount(String objPesquisa){
		return this.pesquisaExamesFacade.obterNomeExamesCount(objPesquisa);
	}

	// Metódo para Suggestion Box Unidade executora
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(String param) {
		return this.returnSGWithCount(this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExamesOrdenadaDescricao(param),pesquisarUnidadeExecutoraCount(param));
	}

	public Integer pesquisarUnidadeExecutoraCount(String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoEDescricaoCount(param);
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
		if(this.filtro!=null){
			this.filtro.setProntuarioPac(this.prontuario);
		}
	}

	public boolean isExibirBotaoVoltar() {
		return exibirBotaoVoltar;
	}

	public void setExibirBotaoVoltar(boolean exibirBotaoVoltar) {
		this.exibirBotaoVoltar = exibirBotaoVoltar;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
		if(this.filtro!=null){
			this.filtro.setCodigoPac(this.pacCodigoFonetica);
		}
	}

	public boolean isFiltroAberto() {
		return filtroAberto;
	}

	public void setFiltroAberto(boolean filtroAberto) {
		this.filtroAberto = filtroAberto;
	}

	public Map<Integer, Vector<Short>> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(Map<Integer, Vector<Short>> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public boolean isBotaoImagens() {
		return botaoImagens;
	}

	public void setBotaoImagens(boolean botaoImagens) {
		this.botaoImagens = botaoImagens;
	}

	public PesquisaExamesFiltroVO getFiltro() {
		return filtro;
	}

	public void setFiltro(PesquisaExamesFiltroVO filtro) {
		this.filtro = filtro;
	}
	public RapServidores getServExame() {
		return servExame;
	}
	public void setServExame(RapServidores servExame) {
		this.servExame = servExame;
	}

	public Integer getCodigoSoeSelecionado() {
		return codigoSoeSelecionado;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public void setCodigoSoeSelecionado(Integer codigoSoeSelecionado) {
		this.codigoSoeSelecionado = codigoSoeSelecionado;
	}

	public Short getIseSeqSelecionado() {return iseSeqSelecionado;}

	public void setIseSeqSelecionado(Short iseSeqSelecionado) {this.iseSeqSelecionado = iseSeqSelecionado;}

	public String getNomePaciente() {return nomePaciente;}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
		if(this.filtro!=null){
			this.filtro.setNomePacientePac(this.nomePaciente);
		}
	}
	public AipPacientes getPaciente() {return paciente;}
	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
		if(paciente != null 
				&& StringUtils.isNotBlank(
						paciente.getNome())) {
			setNomePaciente(paciente.getNome());
		}
	}

    private void atualizarFiltro(){
        if (restricao != null){
            filtro.setRestricao(restricao);
        }
        if (indMostraCanceladosInfo != null){
            filtro.setIndMostraCanceladosInfo(indMostraCanceladosInfo);
        }
        if (this.paciente != null){
            if (paciente.getProntuario() != null){
                filtro.setProntuarioPac(paciente.getProntuario());
            }
            if (paciente.getCodigo() != null){
                filtro.setCodigoPac(paciente.getCodigo());
            }
            if (paciente.getNome() != null && StringUtils.isNotBlank(paciente.getNome())){
                filtro.setNomePacientePac(paciente.getNome());
            }
        } else {
            filtro.setProntuarioPac(null);
            filtro.setCodigoPac(null);
            filtro.setNomePacientePac(null);
        }
    }

	public String verificarQuestoesSismamaBiopsia() {
		Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
		if (it.hasNext()) {
			Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
			/*Item de solicitacao*/
			Integer solicitacao = paramCLValores.getKey();
			/*colecao de seqp da solicitacao acima*/
			Vector<Short> seqps = paramCLValores.getValue();
			selecionarItemExame(solicitacao, seqps.get(0), null);
		}
		return EXAMES_VISUALIZAR_QUESTIONARIO_SISMAMA_BIOPSIA;
	}

	public Boolean habilitarBotaoQuestoesSismama() {		
		return examesLaudosFacade.habilitarBotaoQuestaoSismama(solicitacoes, Boolean.FALSE);
	}
	public Boolean habilitarBotaoQuestoesSismamaBiopsia() {		
		return sismamaFacade.habilitarBotaoQuestaoSismamaBiopsia(solicitacoes, Boolean.FALSE);
	}
	public String verificarQuestoesSismama() {
		if (habilitarBotaoQuestoesSismama()) {
			//Carrega todas as resposta do questionário
			if (!solicitacoes.isEmpty() && solicitacoes.size() == 1) {
				Integer iseSoeSeq = solicitacoes.keySet().iterator().next();
				Short iseSeqp = null;
				if (solicitacoes.get(iseSoeSeq).size() == 1) {
					iseSeqp = solicitacoes.get(iseSoeSeq).iterator().next();
					verificaQuestoesSismamaController.preencherQuestionario(iseSoeSeq, iseSeqp, Boolean.FALSE);
				}
			}
			return EXAMES_VERIFICA_QUESTOES_SISMAMA;
		} else {
			return verificarQuestoesSismamaBiopsia();
		}
	}
	public Boolean getAlterarFiltroPesquisaExames() {return alterarFiltroPesquisaExames;}
	public void setAlterarFiltroPesquisaExames(Boolean alterarFiltroPesquisaExames) {this.alterarFiltroPesquisaExames = alterarFiltroPesquisaExames;}
	public TipoPesquisa getTipoPesquisa() {return tipoPesquisa;}
	public void setTipoPesquisa(TipoPesquisa tipoPesquisa) {this.tipoPesquisa = tipoPesquisa;}
	public List<PesquisaExamesPacientesVO> getListaPacientes() {return listaPacientes;}
	public void setListaPacientes(List<PesquisaExamesPacientesVO> listaPacientes) {	this.listaPacientes = listaPacientes;}
	public Boolean getIsHist() {return isHist;}
	public void setIsHist(Boolean isHist) {	this.isHist = isHist;}

	public Boolean getOrigemProntuarioOnline() {
		return origemProntuarioOnline;
	}

	public void setOrigemProntuarioOnline(Boolean origemProntuarioOnline) {
		this.origemProntuarioOnline = origemProntuarioOnline;
	}
	
	public List<AelConfigExLaudoUnico> pesquisarAelConfigExLaudoUnico(String value){
		return this.returnSGWithCount(examesPatologiaFacade.pesquisarAelConfigExLaudoUnico(AelConfigExLaudoUnico.Fields.NOME.toString(), (String) value),pesquisarAelConfigExLaudoUnicoCount(value));
	}
	
	public Long pesquisarAelConfigExLaudoUnicoCount(String value){
		return examesPatologiaFacade.pesquisarAelConfigExLaudoUnicoCount(AelConfigExLaudoUnico.Fields.NOME.toString(), (String) value);
	}
	
	public String getVoltarDaTelaCancelar() {
		return voltarDaTelaCancelar;
	}

	public void setVoltarDaTelaCancelar(String voltarDaTelaCancelar) {
		this.voltarDaTelaCancelar = voltarDaTelaCancelar;
	}

	public Boolean getExibeModalConfirmacao() {
		return exibeModalConfirmacao;
	}

	public void setExibeModalConfirmacao(Boolean exibeModalConfirmacao) {
		this.exibeModalConfirmacao = exibeModalConfirmacao;
	}

	public String getSituacaoCodigoSelecionado() {
		return situacaoCodigoSelecionado;
	}

	public void setSituacaoCodigoSelecionado(String situacaoCodigoSelecionado) {
		this.situacaoCodigoSelecionado = situacaoCodigoSelecionado;
	}

    public DominioSimNao getIndMostraCanceladosInfo() {
        return indMostraCanceladosInfo;
    }

    public void setIndMostraCanceladosInfo(DominioSimNao indMostraCanceladosInfo) {
        this.indMostraCanceladosInfo = indMostraCanceladosInfo;
    }

    public DominioRestricaoUsuario getRestricao() {
        return restricao;
    }

    public void setRestricao(DominioRestricaoUsuario restricao) {
        this.restricao = restricao;
    }

	public String getMensagemConfirmacaoImpressaoEtiquetas() {
		return mensagemConfirmacaoImpressaoEtiquetas;
	}

	public void setMensagemConfirmacaoImpressaoEtiquetas(String mensagemConfirmacaoImpressaoEtiquetas) {
		this.mensagemConfirmacaoImpressaoEtiquetas = mensagemConfirmacaoImpressaoEtiquetas;
	}
	
	public String getNomeSocialPaciente() {
		return nomeSocialPaciente;
	}

	public void setNomeSocialPaciente(String nomeSocialPaciente) {
		this.nomeSocialPaciente = nomeSocialPaciente;
	}
	
	public Boolean getItensSelecionados() {
		return itensSelecionados;
	}

	public void setItensSelecionados(Boolean itensSelecionados) {
		this.itensSelecionados = itensSelecionados;
	}

	public Boolean getConexaoBancoHist() {
		return conexaoBancoHist;
	}

	public void setConexaoBancoHist(Boolean conexaoBancoHist) {
		this.conexaoBancoHist = conexaoBancoHist;
	}

}