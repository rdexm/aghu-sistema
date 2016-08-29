package br.gov.mec.aghu.prescricaomedica.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.certificacaodigital.action.ListarPendenciasAssinaturaPaginatorController;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioModulo;
import br.gov.mec.aghu.dominio.DominioSituacaoRegistro;
import br.gov.mec.aghu.exames.pesquisa.action.PesquisaExameController;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.DadosDialiseVO;
import br.gov.mec.aghu.prescricaomedica.vo.ListaPacientePrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusAltaObito;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusExamesNaoVistos;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusSumarioAlta;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import net.sf.jasperreports.engine.JRException;

@SuppressWarnings("PMD.ExcessiveClassLength")

public class ListaPacientesInternadosController extends ActionController {

	private static final long serialVersionUID = 4480929153661907603L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
    @EJB
    private IAghuFacade aghuFacade;

    @EJB
    private IAmbulatorioFacade ambulatorioFacade;

    @EJB
    private ICascaFacade cascaFacade;

	private String banco;
    private String UrlBaseWebForms;

	@Inject
	private ManterSumarioAltaController manterSumarioAltaController;
		
	@Inject
	private ListarPendenciasAssinaturaPaginatorController listarPendenciasAssinaturaPaginatorController;
	
	@Inject
	private PesquisaExameController pesquisaExameController;
	
	private List<PacienteListaProfissionalVO> lista;
	private Integer vinculo;
	private Integer matricula;
	private Integer atdSeq;
	private Integer apaSeq;
	private StatusAltaObito labelAlta = StatusAltaObito.ALTA;
	private StatusAltaObito labelObito = StatusAltaObito.OBITO;

	private Boolean disableButtonAltaObito = Boolean.FALSE;
    private Boolean disableButtonObito=Boolean.FALSE;
	private Boolean disableButtonPrescrever = Boolean.FALSE;
    private Boolean disableButtonAntecipaSumario = Boolean.FALSE;
    private Boolean isToInvokeAghWebOnBtnAtenderConsultoria = Boolean.FALSE;
    private Boolean isToDisableBtnDiagnosticos = Boolean.FALSE;
    private Boolean habilitaBotaoEvolucao = Boolean.FALSE;
    private boolean isPrescreverAghweb=Boolean.FALSE;
	private Boolean disableButtonSolicitarExame = Boolean.FALSE;
	private Boolean enableButtonAnamneseEvolucao = Boolean.FALSE;
	private StatusSumarioAlta statusSumario;
	private Comparator<PacienteListaProfissionalVO> currentComparator;
	private Integer pacCodigo;
	private String prontuario;
	private String tipo;

	private final String PAGE_PRESCRICAO = "prescricaomedica-verificaPrescricaoMedica";
	private final String PAGE_SOLICITAR_EXAME = "exames-solicitacaoExameCRUD";
	private final String PAGE_SUMARIO_ALTA = "prescricaomedica-manterSumarioAlta";
	private final String PAGE_ANTECIPAR_SUMARIO_ALTA = "prescricaomedica-anteciparSumario";
	private final String PAGE_SUMARIO_OBITO = "prescricaomedica-manterSumarioObito";
	private final String PAGE_DIAGNOSTICOS = "prescricaomedica-manterDiagnosticosPaciente";
	private final String PAGE_REDIRECIONAR_JUSTIFICATIVA_LAUDOS = "prescricaomedica-manterJustificativaLaudos";
	private final String PAGE_ERRO = "prescricaomedica-pesquisarListaPacientesInternados";
	private final String PAGE_VOLTAR_PESQUISAR = "prescricaomedica-pesquisarListaPacientesInternados";
	private final String PAGE_CONFIGURAR_LISTA = "prescricaomedica-configurarListaPacientes";
	private final String PAGE_VISUALIZAR_REGISTROS_CONTROLE = "controlepaciente-visualizarRegistrosControle";
	private final String PAGE_PESQUISAR_EXAMES = "exames-pesquisaExames";
	private final String PAGE_REDIRECIONAR_LISTA_PENDENCIAS_ASSINATURA = "certificacaodigital-listarPendenciasAssinatura";
	private final String PAGE_REDIRECIONAR_PESQUISAR_CONSULTORIAS_INTERNACAO = "prescricaomedica-pesquisarConsultoriasInternacao";
	private final String PAGE_MANTER_RECEITAS = "prescricaomedica-manterReceitas";
	private final String PAGE_LISTAR_ANAMNESE_EVOLUCAO = "prescricaomedica-listarAnamneseEvolucoes";
    private final String LABEL = "LABEL_";
	
	private PacienteListaProfissionalVO pacienteListaProfissionalVO;
	
	@Inject
	private RelatorioListaPacientesController relatorioListaPacientesController;
		
	private List<ListaPacientePrescricaoVO> listaPacientes;
	
	private String nome; 
	private String responsavel;
	private String idade;
	private String leito;

	private boolean pesquisar = true;

	private DadosDialiseVO caminhoDialise;

	private AghAtendimentos atendimento;
	
	private boolean fichaApachePendente = false;
    
	private boolean redirecionarAghWebSumarioAlta = false;
	private boolean redirecionarAghWebSumarioObito = false;
	private boolean redirecionarAghWebFichaApache = false;
	private boolean redirecionarAghWebAnteciparSumario = false;
	private String aghuUsoSumario;
	private boolean aghuBotoesExameHemoterapia = true;
	private Boolean isHist = Boolean.FALSE;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	public void inicio(){
	 
		try {
			limpaVariaveisRedirecionaAghWeb();
			buscaParametrosAghWeb();
			buscaParametroUsoSumario();

			if (pesquisar) {
				this.pesquisar();
			}else{
				pesquisar = true;
			}

            controlaBotoes();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void buscarParametroDesabilitarBotoesExameHemoterapia() throws ApplicationBusinessException {
		AghParametros aghParametroDesabilitarBotoes = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DESABILITAR_BOTOES_EXAME_HEMOTERAPIA);
		if(aghParametroDesabilitarBotoes != null && aghParametroDesabilitarBotoes.getVlrTexto() != null){
			if(aghParametroDesabilitarBotoes.getVlrTexto().equalsIgnoreCase("S")){
				setAghuBotoesExameHemoterapia(true);
			}else{
				setAghuBotoesExameHemoterapia(false);
			}
		}
	}

	private void buscaParametroUsoSumario() throws ApplicationBusinessException{
		AghParametros aghParametrosUsoSumario = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_USO_SUMARIO);
		if (aghParametrosUsoSumario != null) {
			aghuUsoSumario = aghParametrosUsoSumario.getVlrTexto();
		}
	}
	public void atualizarLista() {
		try {
			this.pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		atdSeq = null;
		nome = null;
		responsavel = null;
		idade = null;
		leito = null;
		pacCodigo = null;
		prontuario = null;

		reiniciaBotoes();
	}
	
    //metodo chamado quando volta da tela 'configurar lista'
    public void reiniciaBotoes() {
		limpaVariaveisRedirecionaAghWeb();
        disableButtonAltaObito = Boolean.FALSE;
        disableButtonObito=Boolean.FALSE;
        disableButtonPrescrever = Boolean.FALSE;
        disableButtonAntecipaSumario = Boolean.FALSE;
        isToInvokeAghWebOnBtnAtenderConsultoria = Boolean.FALSE;
        isToDisableBtnDiagnosticos = Boolean.FALSE;
        habilitaBotaoEvolucao = Boolean.FALSE;
        isPrescreverAghweb=Boolean.FALSE;
        disableButtonSolicitarExame = Boolean.FALSE;       
        this.pacienteListaProfissionalVO = null;
        setPacCodigo(null);
    }

    public boolean isPrescreverAghweb(){
        return isPrescreverAghweb;
    }

    /*
     *a regra desse metodo esta no doc anexado a tarefa #47346
     */
    private void controlaBotaoPrescricaoMedica() {
        try {

            if (aghuFacade.isHCPA()){
	
                //ii) Se for o HCPA e a unidade do paciente estiver no parâmetro criado chama AGHUse senão chama AGHWEB
                if (unidadeDoAtendimentoEstaNoParametro() == false){
                    isPrescreverAghweb=true;
                    buscaParametrosAghWeb();
                }else{
                    isPrescreverAghweb=false;
                }
            }
        } catch (BaseException e) {
            apresentarExcecaoNegocio(e);
        }
	}
	
    private boolean unidadeDoAtendimentoEstaNoParametro() throws ApplicationBusinessException {

        String unidadesDoParametro = "";
        String unidadeDoPaciente = "";

        AghParametros unidades = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_UNID_IMPL_MPM);
        if (unidades != null) {

	    // verificar se o parametro esta cadastrado corretamente, dever ter
	    // as unidades
	    // cadastradas no campo "vltTexto" e separadas por virgula, conforme
	    // especificado.
            unidadesDoParametro = unidades.getVlrTexto();

	    // Caso este parâmetro esteja vazio irá significar que todas as
	    // unidades estão implantadas
            if (unidadesDoParametro == null || unidadesDoParametro.equals("")) {
                return true;
            }
	
            if (unidadesDoParametro != null) {
                if (pacCodigo != null) {
			        AghAtendimentos atendimento = aghuFacade.obterAtendimentoComUnidadeFuncional(getAtdSeq());
                    if (atendimento.getUnidadeFuncional() != null  ) {
                        unidadeDoPaciente = atendimento.getUnidadeFuncional().getSeq().toString();
                    }
                }
            }
        }

        return unidadesDoParametro.contains(unidadeDoPaciente);
    }

    private void buscaParametrosAghWeb() throws ApplicationBusinessException {
		if (StringUtils.isBlank(banco)) {
	        AghParametros aghParametrosBanco = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_BANCO_AGHU_AGHWEB);
	        if (aghParametrosBanco != null) {
	            banco = aghParametrosBanco.getVlrTexto();
	        }
	    }
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

	public void pesquisar() throws BaseException {
		//debug("++ begin pesquisar ");
		
		RapServidores servidor = getServidor();

		this.lista = this.prescricaoMedicaFacade.listaPaciente(servidor);
		if (this.lista.isEmpty()) {
	    apresentarMsgNegocio(Severity.WARN, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO");
		}

		// mantem a ordem corrente
		if (this.currentComparator != null) {
			Collections.sort(this.lista, this.currentComparator);
		}

		// atualizar os atributos de controle para habilitar/desabilitar
		// os botões de ação
		if (this.atdSeq != null) {
			for (PacienteListaProfissionalVO vo : lista) {
				if (vo.getAtdSeq().equals(this.atdSeq)) {
		    this.setDisableButtonAltaObito(vo.isDisableButtonAltaObito());
		    this.setDisableButtonPrescrever(vo.isDisableButtonPrescrever());
					break;
				}
			}
		}

		//debug("++ end pesquisar");
	}

	 public void exibirMsgAcessoQuimioAGHWEB() {
    	apresentarMsgNegocio(Severity.INFO, "MSG_QUIMIO_AGHWEB");
    }
	public Boolean verificaSumarioAltaObitoConcluido(){
		limpaVariaveisRedirecionaAghWeb();
		Boolean retorno = false;
		if(atdSeq != null){
			if(prescricaoMedicaFacade.validarSumarioConcluidoAltaEObitoPorAtdSeq(atdSeq)){
				apresentarMsgNegocio("MSG_SUMARIO_ALTA_OBITO_CONCLUIDO");
				retorno = true;
			}
		}
		return retorno;
	}
	
	private void limpaVariaveisRedirecionaAghWeb(){
		redirecionarAghWebSumarioAlta = false;
		redirecionarAghWebSumarioObito = false;
		redirecionarAghWebFichaApache = false;
		redirecionarAghWebAnteciparSumario = false;
	}
	
	public void preparaRedirecionarAghWebSumarioAlta(){
		redirecionarAghWebSumarioAlta = !verificaSumarioAltaObitoConcluido();
	}

	public void preparaRedirecionarAghWebSumarioObito(){
		redirecionarAghWebSumarioObito = !verificaSumarioAltaObitoConcluido();
	}

	public void preparaRedirecionarAghWebFichaApache(){
		redirecionarAghWebFichaApache = !verificaSumarioAltaObitoConcluido();
	}

	public void preparaRedirecionarAghWebAnteciparSumario(){
		try {
			if (atdSeq != null) {
				this.apaSeq = this.prescricaoMedicaFacade.recuperarAtendimentoPaciente(atdSeq);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		redirecionarAghWebAnteciparSumario = !verificaSumarioAltaObitoConcluido();
	}

	private RapServidores getServidor() {
		return servidorLogadoFacade.obterServidorLogado();
	}
	
	public String visualizarResultadoExames(PacienteListaProfissionalVO paciente){
		if (paciente.getStatusExamesNaoVistos() != null && paciente.getStatusExamesNaoVistos().equals(StatusExamesNaoVistos.RESULTADOS_NAO_VISUALIZADOS)){
			return realizarChamadaExamesNaoVistos(paciente);
		} else {
			Integer prontuario = Integer.valueOf(paciente.getProntuario());
			return realizarChamadaPesquisaExames(prontuario);
		}
	}

	public String realizarChamadaExamesNaoVistos(PacienteListaProfissionalVO paciente){
		
		List<AelItemSolicitacaoExames> listaItemSolicExame = this.prescricaoMedicaFacade.pesquisarExamesNaoVisualizados(paciente.getAtdSeq());
		Integer qtdMaxItens = 20;
		try {
			AghParametros param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_QTD_MAX_RESULTADOS_EXAMES_NAO_VISUALIZADOS);
			if(param != null) {
				qtdMaxItens = param.getVlrNumerico().intValue();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		if(listaItemSolicExame.size() > qtdMaxItens) {
			apresentarMsgNegocio(Severity.WARN, "INFO_RESULTADOS_EXAMES_LIBERADOS_NAO_VISUALIZADOS", qtdMaxItens);
			return realizarChamadaPesquisaExames(Integer.valueOf(paciente.getProntuario()));
		}
		
		Map<Integer, Vector<Short>> solicitacoes = new HashMap<Integer, Vector<Short>>();
		Integer soeSeq;
		Short seqP;
		for (AelItemSolicitacaoExames itemSolicExame : listaItemSolicExame){	
			soeSeq = itemSolicExame.getId().getSoeSeq();
			seqP   = itemSolicExame.getId().getSeqp();
			
			if (!solicitacoes.containsKey(soeSeq)){
				solicitacoes.put(soeSeq, new Vector<Short>());
				solicitacoes.get(soeSeq).add(seqP);
			} else {
				solicitacoes.get(soeSeq).add(seqP);
			}	
		}
		
		this.pesquisaExameController.setSolicitacoes(solicitacoes);
		this.pesquisaExameController.setVoltarPara(PAGE_VOLTAR_PESQUISAR);
		return this.pesquisaExameController.verResultados();
	}
	
	public String realizarChamadaPesquisaExames(Integer prontuario) {
		String retorno = null;
		if (prontuario != null) {
			this.pesquisaExameController.setVoltarPara(PAGE_VOLTAR_PESQUISAR);
			this.pesquisaExameController.setProntuario(prontuario);
			retorno = PAGE_PESQUISAR_EXAMES;
		}

		return retorno;
	}	
	
	public String realizarChamaPacientePesquisa(){
		//TODO
		return null;
	}
	
	public String realizarChamaEvolucao(){
		//TODO
		return null;
	}
	
    public String redirecionarListarAnamneseEvolucoes() {
    	return PAGE_LISTAR_ANAMNESE_EVOLUCAO;
    }
    
	/**
	 * Realizar as consistências antes de chamar a tela de Sumário Alta
	 * 
	 * @return
	 */
	public String realizarChamadaSumarioAlta() {
		String retorno = PAGE_ERRO;
		try{
		if (atdSeq != null) {
				this.prescricaoMedicaFacade.realizarConsistenciasSumarioAlta(atdSeq);
			retorno = PAGE_SUMARIO_ALTA;
		}
		} catch(ApplicationBusinessException e){
			this.apresentarExcecaoNegocio(e);
		}
		return retorno;
	}

	public String redirecionarJustificativaLaudos() {
		return PAGE_REDIRECIONAR_JUSTIFICATIVA_LAUDOS;
	}
	
	public String visualizarRegistrosControle() {
		return PAGE_VISUALIZAR_REGISTROS_CONTROLE;
	}
	
	/**
	 * Realizar as consistências antes de chamar a tela Antecipar Sumário
	 */
	public String realizarChamadaAnteciparSumario() {

		String retorno = PAGE_ERRO;

		try {

			if (atdSeq != null) {

				// this.prescricaoMedicaFacade.realizarConsistenciasAnteciparSumario();
		this.apaSeq = this.prescricaoMedicaFacade.recuperarAtendimentoPaciente(atdSeq);
				retorno = PAGE_ANTECIPAR_SUMARIO_ALTA;

			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return retorno;
	}

	/**
	 * Realizar as consistências antes de chamar a tela de Diagnósticos
	 */
	public String realizarChamadaDiagnosticos() {

		try {
			if (atdSeq != null) {
				this.prescricaoMedicaFacade.realizarConsistenciasDiagnosticos();
				return PAGE_DIAGNOSTICOS;
			} 
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Realizar as consistências antes de chamar a tela de Atestados
	 */
	public String realizarChamadaAtestado() {
		//try {
			if (atdSeq != null) {
				//this.prescricaoMedicaFacade.realizarConsistenciasDiagnosticos();
			return PAGE_SUMARIO_ALTA;
			} 
		/*} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}*/
		return null;
	}		

	/**
	 * Realizar as consistências ao clicar no ícone 'Sumario Alta Não
	 * Realizado'. Não realiza a chamada da tela quando o ícone for 'Sumário
	 * Alta Não Entregue ao SAMIS'.
	 */
	public String realizarChamadaSumarioPeloIcone() {
		if (this.statusSumario == StatusSumarioAlta.SUMARIO_ALTA_NAO_REALIZADO) {
			try {
				if (this.prescricaoMedicaFacade.isMotivoAltaObito(atdSeq)) {
					this.prescricaoMedicaFacade.realizarConsistenciasSumarioObito(atdSeq);
					this.apaSeq = this.prescricaoMedicaFacade.recuperarAtendimentoPaciente(atdSeq);
					
		    // <param name="asu_apa_atd_seq"
		    // value="#{listaPacientesInternadosController.atdSeq}" />
		    // <param name="altan_apa_seq"
		    // value="#{listaPacientesInternadosController.apaSeq}" />
//					<param name="altan_lista_origem" value="OBITO" />
		    // <param name="voltar_para"
		    // value="pesquisarListaPacientesInternados"/>
					
					return PAGE_SUMARIO_OBITO;
				} else {
					this.prescricaoMedicaFacade.realizarConsistenciasSumarioAlta(atdSeq);
					this.apaSeq = this.prescricaoMedicaFacade.recuperarAtendimentoPaciente(atdSeq);

					manterSumarioAltaController.setAltanAtdSeq(atdSeq);
					manterSumarioAltaController.setAltanApaSeq(apaSeq);
					manterSumarioAltaController.setAltanListaOrigem("ALTA");
					manterSumarioAltaController.setVoltarPara("pesquisarListaPacientesInternados");
					return PAGE_SUMARIO_ALTA;
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		return null;
	}

	/**
	 * Realizar as consistências antes de chamar a tela de Sumário Óbito
	 */
	public String realizarChamadaSumarioObito() {

		String retorno = PAGE_ERRO;

		try {
			if (atdSeq != null) {
				// this.prescricaoMedicaFacade.realizarConsistenciasSumarioObito(atdSeq);
		this.apaSeq = this.prescricaoMedicaFacade.recuperarAtendimentoPaciente(atdSeq);
				retorno = PAGE_SUMARIO_OBITO;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return retorno;
	}

	/**
	 * Apenas redireciona para a tela de prescrição
	 */
	public String realizarChamadaPrescricao() {
		if (atdSeq == null){
			return null;
		}
		return  PAGE_PRESCRICAO;
	}

	

	public String realizarChamadaSolicitarExame() {
		return realizarChamadaSolicitarExame(this.atdSeq, false);
	}

	public String realizarChamadaSolicitarExame(Integer atendimentoSeq, boolean origemSumarioAlta) {
		String retorno = null;

		try {
			if (atendimentoSeq != null) {
				atendimento = this.solicitacaoExameFacade.verificarPermissoesParaSolicitarExame(atendimentoSeq, origemSumarioAlta);
				retorno = PAGE_SOLICITAR_EXAME;
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return retorno;
	}

	public boolean habilitarContgExamesAGHWeb() {

		try {
			String habilitar = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_CONTINGENCIA_SOLIC_EXAMES_AGHWEB);

			return "S".equals(habilitar);
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return false;
	}
	

	//@Secure("#{s:hasPermission('assinaturaDigital','alterarContextoProfissional')}")
    public String redirecionarListarPendenciasAssinatura() { // TODO MIGRAÇÃO
							     // PENDENCIA
							     // ESCHWEIGERT

		//#46626
		selecionarPaciente();

		String retorno = null;

		RapServidores profissional;
		try {
			profissional = registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			profissional = null;
		}

		boolean habilitado = this.certificacaoDigitalFacade.verificaProfissionalHabilitado();

		if (habilitado) {
		AipPacientes paciente = this.pacienteFacade.buscaPaciente(pacCodigo);
		
		Long count = this.certificacaoDigitalFacade.listarPendentesResponsavelPacienteCount(profissional,paciente);
		
		if(count != null && count.intValue() > 0 ){
			this.setTipo("1");
		}else {
			this.setTipo("3");
		}
		
			//#46626
			listarPendenciasAssinaturaPaginatorController.setTipo(this.getTipo());
			listarPendenciasAssinaturaPaginatorController.setPacCodigo(pacCodigo);
			listarPendenciasAssinaturaPaginatorController.setVoltarPara(PAGE_VOLTAR_PESQUISAR);

			retorno = PAGE_REDIRECIONAR_LISTA_PENDENCIAS_ASSINATURA;
		}
		
		return retorno;
	}
	
	
	public String configurarLista() {
		return PAGE_CONFIGURAR_LISTA;
	}

	public String getUrlBaseWebForms() {
		if (StringUtils.isBlank(UrlBaseWebForms)) {
			try {
			AghParametros aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_URL_BASE_AGH_ORACLE_WEBFORMS);
	
				if (aghParametros != null) {
			    UrlBaseWebForms = aghParametros.getVlrTexto();
				}
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		return UrlBaseWebForms;
    }
	
	public Boolean habilitaBotaoAtestado() {
		if (this.atdSeq != null) {
			return true;
		}else{
			return false;
		}
	}	
	
	public Boolean habilitaBotaoEvolucao() {
		if (this.atdSeq != null) {
			try {
				AghParametros aghParametros = this.parametroFacade
						.buscarAghParametro(AghuParametrosEnum.P_AGHU_INTEGRACAO_AGH_ORACLE_WEBFORMS);
	
				if (aghParametros != null) {
					String str = aghParametros.getVlrTexto();
					
					if (str != null && str.equalsIgnoreCase("S")) {
						return true;
					}
				}
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		
		return false;
	}

    public boolean isToInvokeBtnAlta() {

			try {
            if (aghuFacade.isHCPA()) {
                if (unidadeDoAtendimentoEstaNoParametro() == false) {
                        disableButtonAltaObito = true;
                    }else{
                        disableButtonAltaObito = false;
                    }
                }
        }catch (ApplicationBusinessException e) {
            apresentarExcecaoNegocio(e);
        }

        return disableButtonAltaObito;
    }
	
    public Boolean isToInvokeBtnObito() {
	    	/*49085
			/*if (aghuFacade.isHCPA()) {
            disableButtonObito = true;
			}*/
        return disableButtonObito;
    }
					
    public boolean isToInvokeBtnAnteciparSumario () {
         try{
            if (aghuFacade.isHCPA()) {
                if (unidadeDoAtendimentoEstaNoParametro() == false) {
                    disableButtonAntecipaSumario=true;
                }else{
                    disableButtonAntecipaSumario=false;
					}
				}
        }catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
        return disableButtonAntecipaSumario;
    }

    public boolean isToInvokeAghWebOnBtnAtenderConsultoria() {
        if (aghuFacade.isHCPA()) {
            isToInvokeAghWebOnBtnAtenderConsultoria = true;
        }else{
            isToInvokeAghWebOnBtnAtenderConsultoria = false;
        }
        return isToInvokeAghWebOnBtnAtenderConsultoria;
    }

    public boolean isToDisableBtnDiagnostico() {
        if (aghuFacade.isHCPA()) {
            this.isToDisableBtnDiagnosticos = false;
        }else{
            this.isToDisableBtnDiagnosticos = true;
        }
        return isToDisableBtnDiagnosticos;
    }

    public boolean isToInvokeAghWebOnBtnControleInfeccao() {
        boolean disableButton = false;
        if  (aghuFacade.isHCPA()) {
            disableButton=false;
        }else{
            disableButton = true;
        }
        return disableButton;
		}
		
    public boolean isToInvokeAghWebOnBtnReceita() {
        boolean disableButton = false;
        if  (aghuFacade.isHCPA()) {
            disableButton=false;
        }else{
            disableButton = true;
        }
        return disableButton;
	}
	
    public boolean isToInvokeAghWebOnBtnQuimioterapiaDialise() {
        boolean disableButton = false;
        if  (aghuFacade.isHCPA()) {
            disableButton=false;
        }else{
            disableButton = true;
        }
        return disableButton;
    }
    
    /**
     * #46192
     */
    @SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.AvoidPrintStackTrace"})
    public void obterCaminhoDialise() {
    	try {
    		this.caminhoDialise = this.prescricaoMedicaFacade.obterCaminhoDialise(this.atdSeq, getEnderecoRedeHostRemoto());
    		
    		RequestContext.getCurrentInstance().execute("open_modal_dialise('" + this.UrlBaseWebForms + "', '" + this.obterTokenUsuarioLogado() + "', '" + this.caminhoDialise.getAtdSeq() + 
    		"', '" + this.caminhoDialise.getSeqTratamento() + "', '" + this.banco + "', 'mptf_portal_dialise.fmx'); return false;");
    		
    	} catch (BaseException e) {
    		apresentarExcecaoNegocio(e);
    	} catch (UnknownHostException e) {
    		e.printStackTrace();
    	}
    }
    
    
	public void selecionarPaciente(){
		if (pacienteListaProfissionalVO != null) {
			atdSeq = pacienteListaProfissionalVO.getAtdSeq();
			nome = pacienteListaProfissionalVO.getNome();
			responsavel = pacienteListaProfissionalVO.getNomeResponsavel();
			idade = String.valueOf(pacienteListaProfissionalVO.getIdade());
			leito = pacienteListaProfissionalVO.getLocal();
			pacCodigo = pacienteListaProfissionalVO.getPacCodigo();
			prontuario = pacienteListaProfissionalVO.getProntuario();
			labelAlta = pacienteListaProfissionalVO.getLabelAlta();
			labelObito = pacienteListaProfissionalVO.getLabelObito();
			disableButtonAltaObito = pacienteListaProfissionalVO.isDisableButtonAltaObito();
			disableButtonPrescrever = pacienteListaProfissionalVO.isDisableButtonPrescrever();
			enableButtonAnamneseEvolucao = pacienteListaProfissionalVO.isEnableButtonAnamneseEvolucao();
			//49085
		    try {
		    	buscarParametroDesabilitarBotoesExameHemoterapia();
		    	this.fichaApachePendente = prescricaoMedicaFacade.verificaPendenciaFichaApache(atdSeq);
				this.apaSeq = this.prescricaoMedicaFacade.recuperarAtendimentoPaciente(this.atdSeq);
			} catch (ApplicationBusinessException e1) {
				apresentarExcecaoNegocio(e1);
			}
		    
			// Desabilitar o botao de Solicitar exames //
			try {
				if (atdSeq != null) {
					atendimento = this.solicitacaoExameFacade.verificarPermissoesParaSolicitarExame(this.atdSeq);
				}
				disableButtonSolicitarExame = Boolean.FALSE;
			} catch (BaseException e) {
				//apresentarExcecaoNegocio(e);
				disableButtonSolicitarExame = Boolean.TRUE;
			}
            // ajustando botoes conforme doc em anexo na terefa #47346
            controlaBotoes();
            
//            if (!isToInvokeAghWebOnBtnQuimioterapiaDialise()) {
//            	this.obterCaminhoDialise();
//            }
		}
	}
	
    private void controlaBotoes() {
        controlaBotaoPrescricaoMedica();
        isToInvokeAghWebOnBtnEvolucao();
        if (!disableButtonAltaObito){
        	isToInvokeBtnAlta();
        }
        isToInvokeBtnObito ();
        isToInvokeBtnAnteciparSumario ();
        isToInvokeAghWebOnBtnAtenderConsultoria();
        isToDisableBtnDiagnostico();
    }
    
    public boolean isToInvokeAghWebOnBtnEvolucao(){
        try {
            if (aghuFacade.isHCPA()){
                habilitaBotaoEvolucao = true;
                buscaParametrosAghWeb();
            }
        } catch (BaseException e) {
            apresentarExcecaoNegocio(e);
        }
        return habilitaBotaoEvolucao;
    }

    public String obterBundleSumario(PacienteListaProfissionalVO itemVo){
		StringBuffer bundle = new StringBuffer();
		bundle.append(LABEL).append(itemVo.getStatusSumarioAlta().name());
		return bundle.toString();
	}
	
	public String obterBundlePendencia(PacienteListaProfissionalVO itemVo){
		StringBuffer bundle = new StringBuffer();
		bundle.append(LABEL).append(itemVo.getStatusPendenciaDocumento().name());
		return bundle.toString();
	}
	
	public String obterBundlePrescricao(PacienteListaProfissionalVO itemVo){
		StringBuffer bundle = new StringBuffer();
	bundle.append(LABEL).append(itemVo.getStatusPrescricao().name());
		return bundle.toString();
	}
	

	
	public void imprimirLista() throws SistemaImpressaoException, ApplicationBusinessException, UnknownHostException, JRException {
		Integer matricula = null;
		Short vinCodigo = null;

		RapServidores servidor = getServidor();
    	if (servidor != null && servidor.getId() != null && servidor.getId().getMatricula() != null
    		&& servidor.getId().getVinCodigo() != null) {
			matricula = servidor.getId().getMatricula();
			vinCodigo = servidor.getId().getVinCodigo();
		}
		this.listaPacientes = new ArrayList<ListaPacientePrescricaoVO>();
		this.listaPacientes = prescricaoMedicaFacade.obterListaDePacientes(matricula, vinCodigo);
		for (ListaPacientePrescricaoVO list : this.listaPacientes) {
			obterDadosPaciente(list);
		}
		relatorioListaPacientesController.setListaPacientes(this.listaPacientes);
		relatorioListaPacientesController.imprimir();
	}

	private void obterDadosPaciente(ListaPacientePrescricaoVO list) throws ApplicationBusinessException {
		if (list.getPacCodigo() != null) {
			Integer idadePaciente = prescricaoMedicaFacade.obterIdadePaciente(list.getPacCodigo());
			if (idadePaciente != null) {
				list.setIdadePaciente(idadePaciente);
			}
		}
		if (list.getMatriculaServidor() != null && list.getVinCodigoServidor() != null) {
			String nomeUsual = prescricaoMedicaFacade.obterNomeUsualPacitente(list.getMatriculaServidor(), list.getVinCodigoServidor());
			if (nomeUsual != null) {
				list.setNomeUsual(nomeUsual.length() > 11 ? nomeUsual.substring(0, 11) : nomeUsual.substring(0,nomeUsual.length()));
			}
		}

		if (list.getAtdSeq() != null) {
			String localPaciente = prescricaoMedicaFacade.obterLocalPaciente(list.getAtdSeq());
			if (localPaciente != null) {
				list.setLocal(localPaciente);
			}
		}

		if (list.getDthrInicio() != null) {
			list.setDthrInicio1(DateUtil.calcularDiasEntreDatas(DateUtil.truncaData(list.getDthrInicio()), DateUtil.truncaData(new Date())) -1);		
			}

		if (list.getDthrInicio() != null) {
			list.setDataFormatada(DateUtil.obterDataFormatada(list.getDthrInicio(), "dd/MM/yy"));
		}
	}

    public Long geraRegistroDeAtendimentoVersao2() {
    	Long rgt = null;
    	try {
    	    // so chama a rotina algum registro foi selecionado na lista
    	    if (atdSeq != null) {

    		// mamk_int_generica.mamp_int_gera_reg_2 (v_atd_seq, 'N', null,
    		// 'S', 'EE',v_rgt_seq);
    		rgt = ambulatorioFacade.geraRegistroDeAtendimentoVersao2(atdSeq, "N", null, "S", DominioSituacaoRegistro.EE);
    	    }

    	} catch (ApplicationBusinessException e) {
    	    this.apresentarExcecaoNegocio(e);
    	}
    	return rgt;
    }
	public String realizarChamadaTelaReceita(){
	return PAGE_MANTER_RECEITAS; // TODO chamada será para a pagina criada
				     // na estória #41635, a ser desenvolvida no
				     // SPRINT 101.
	}
	
	public String realizarChamadaTelaAtenderConsultoria(){
		return PAGE_REDIRECIONAR_PESQUISAR_CONSULTORIAS_INTERNACAO;
	}
	
	public Boolean getDisableButtonAltaObito() {
		return disableButtonAltaObito;
	}

	public void setDisableButtonAltaObito(Boolean disableButtonAltaObito) {
		this.disableButtonAltaObito = disableButtonAltaObito;
	}

	public Boolean getDisableButtonPrescrever() {
		return disableButtonPrescrever;
	}

	public void setDisableButtonPrescrever(Boolean disableButtonPrescrever) {
		this.disableButtonPrescrever = disableButtonPrescrever;
	}

	public StatusAltaObito getLabelAlta() {
		return labelAlta;
	}

	public void setLabelAlta(StatusAltaObito labelAlta) {
		this.labelAlta = labelAlta;
	}

	public StatusAltaObito getLabelObito() {
		return labelObito;
	}

	public void setLabelObito(StatusAltaObito labelObito) {
		this.labelObito = labelObito;
	}

	public List<PacienteListaProfissionalVO> getLista() {
		return lista;
	}

	public void setLista(List<PacienteListaProfissionalVO> lista) {
		this.lista = lista;
	}

	public Integer getVinculo() {
		return vinculo;
	}

	public void setVinculo(Integer vinculo) {
		this.vinculo = vinculo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public StatusSumarioAlta getStatusSumario() {
		return statusSumario;
	}

	public void setStatusSumario(StatusSumarioAlta statusSumario) {
		this.statusSumario = statusSumario;
	}

	public Integer getApaSeq() {
		return apaSeq;
	}

	public void setApaSeq(Integer apaSeq) {
		this.apaSeq = apaSeq;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	/**
	 * @param disableButtonSolicitarExame
	 *            the disableButtonSolicitarExame to set
	 */
    public void setDisableButtonSolicitarExame(Boolean disableButtonSolicitarExame) {
		this.disableButtonSolicitarExame = disableButtonSolicitarExame;
	}

	/**
	 * @return the disableButtonSolicitarExame
	 */
	public Boolean getDisableButtonSolicitarExame() {
		return disableButtonSolicitarExame;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Boolean verificarModuloExameAtivo() {
		return cascaFacade.verificarSeModuloEstaAtivo(DominioModulo.EXAMES_LAUDOS.getDescricao());
	}

	public PacienteListaProfissionalVO getPacienteListaProfissionalVO() {
		return pacienteListaProfissionalVO;
	}

    public void setPacienteListaProfissionalVO(PacienteListaProfissionalVO pacienteListaProfissionalVO) {
		if (pacienteListaProfissionalVO != null) {
			this.pacienteListaProfissionalVO = pacienteListaProfissionalVO;
		}
	}

	public List<ListaPacientePrescricaoVO> getListaPacientes() {
		return listaPacientes;
	}

	public void setListaPacientes(List<ListaPacientePrescricaoVO> listaPacientes) {
		this.listaPacientes = listaPacientes;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
}
	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public boolean isPesquisar() {
		return pesquisar;
	}

	public void setPesquisar(boolean pesquisar) {
		this.pesquisar = pesquisar;
	}

	public String getBanco(){
		AghParametros aghParametrosBanco;
		try {
			aghParametrosBanco = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_BANCO_AGHU_AGHWEB);
			if (aghParametrosBanco != null) {
				return aghParametrosBanco.getVlrTexto();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

    public boolean isFichaApachePendente() {
		return fichaApachePendente;
	}

	public void setFichaApachePendente(boolean fichaApachePendente) {
		this.fichaApachePendente = fichaApachePendente;
	}

    public String montarMensagemIconePrescricao(PacienteListaProfissionalVO pacienteListaProfissionalVO){
    	if(pacienteListaProfissionalVO!=null && pacienteListaProfissionalVO.getStatusPrescricao()!=null){
    		return this.getBundle().getString(this.obterBundlePrescricao(pacienteListaProfissionalVO));	
    	} else { return "";   	}
    }
    
    public String montarMensagemIconeSumario(PacienteListaProfissionalVO pacienteListaProfissionalVO){
    	if(pacienteListaProfissionalVO!=null && pacienteListaProfissionalVO.getStatusSumarioAlta()!=null){
    		return this.getBundle().getString(this.obterBundleSumario(pacienteListaProfissionalVO));	
    	} else {	return "";  	}
    }
    
    public String montarMensagemSumario(PacienteListaProfissionalVO pacienteListaProfissionalVO){
    	if(pacienteListaProfissionalVO!=null){
    		return this.getBundle().getString(LABEL.concat(pacienteListaProfissionalVO.getStatusExamesNaoVistos().name()));	
    	} else {return ""; 	}
    }
    
    public String montarMensagemIconePendenciaDocumento(PacienteListaProfissionalVO pacienteListaProfissionalVO){
    	if(pacienteListaProfissionalVO!=null){
    		return this.getBundle().getString(LABEL.concat(pacienteListaProfissionalVO.getStatusPendenciaDocumento().name()));	
    	} else {	return "";	}
    }
    
    public String montarMensagemPacientePesquisa(PacienteListaProfissionalVO pacienteListaProfissionalVO){
    	if(pacienteListaProfissionalVO!=null){
    		return this.getBundle().getString(LABEL.concat(pacienteListaProfissionalVO.getStatusPacientePesquisa().name()));	
    	} else {	return ""; 	}
    }
    
    public String montarMensagemEvolucao(PacienteListaProfissionalVO pacienteListaProfissionalVO){
    	if(pacienteListaProfissionalVO!=null){
    		return this.getBundle().getString(LABEL.concat(pacienteListaProfissionalVO.getStatusEvolucao().name()));	
    	} else {	return "";    	}
    }

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public boolean isRedirecionarAghWebSumarioAlta() {
		return redirecionarAghWebSumarioAlta;
}
	public DadosDialiseVO getCaminhoDialise() {
		return caminhoDialise;
	}

	public void setCaminhoDialise(DadosDialiseVO caminhoDialise) {
		this.caminhoDialise = caminhoDialise;
	}

	public void setRedirecionarAghWebSumarioAlta(
			boolean redirecionarAghWebSumarioAlta) {
		this.redirecionarAghWebSumarioAlta = redirecionarAghWebSumarioAlta;
	}

	public boolean isRedirecionarAghWebSumarioObito() {
		return redirecionarAghWebSumarioObito;
	}

	public void setRedirecionarAghWebSumarioObito(
			boolean redirecionarAghWebSumarioObito) {
		this.redirecionarAghWebSumarioObito = redirecionarAghWebSumarioObito;
	}

	public boolean isRedirecionarAghWebFichaApache() {
		return redirecionarAghWebFichaApache;
	}

	public void setRedirecionarAghWebFichaApache(
			boolean redirecionarAghWebFichaApache) {
		this.redirecionarAghWebFichaApache = redirecionarAghWebFichaApache;
	}

	public boolean isRedirecionarAghWebAnteciparSumario() {
		return redirecionarAghWebAnteciparSumario;
	}

	public void setRedirecionarAghWebAnteciparSumario(
			boolean redirecionarAghWebAnteciparSumario) {
		this.redirecionarAghWebAnteciparSumario = redirecionarAghWebAnteciparSumario;
	}
	
	public String getAghuUsoSumario() {
		return aghuUsoSumario;
	}

	public void setAghuUsoSumario(String aghuUsoSumario) {
		this.aghuUsoSumario = aghuUsoSumario;
	}

	public Boolean getIsHist() {
		return isHist;
	}

	public void setIsHist(Boolean isHist) {
		this.isHist = isHist;
	}
	
	public boolean isEnableButtonAnamneseEvolucao() {
		return enableButtonAnamneseEvolucao;
	}	
	
	public void setEnableButtonAnamneseEvolucao(boolean enableButtonAnamneseEvolucao) {
		this.enableButtonAnamneseEvolucao = enableButtonAnamneseEvolucao;
	}

	public Boolean getIsToDisableBtnDiagnosticos() {
		return isToDisableBtnDiagnosticos;
	}

	public void setIsToDisableBtnDiagnosticos(Boolean isToDisableBtnDiagnosticos) {
		this.isToDisableBtnDiagnosticos = isToDisableBtnDiagnosticos;
	}

	public boolean isAghuBotoesExameHemoterapia() {
		return aghuBotoesExameHemoterapia;
	}

	public void setAghuBotoesExameHemoterapia(boolean aghuBotoesExameHemoterapia) {
		this.aghuBotoesExameHemoterapia = aghuBotoesExameHemoterapia;
	}	
	
	
}
