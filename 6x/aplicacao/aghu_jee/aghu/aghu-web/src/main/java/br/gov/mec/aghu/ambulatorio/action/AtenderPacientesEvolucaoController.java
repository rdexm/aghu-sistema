package br.gov.mec.aghu.ambulatorio.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.DocumentosPacienteVO;
import br.gov.mec.aghu.ambulatorio.vo.EspecialidadeRelacionadaVO;
import br.gov.mec.aghu.ambulatorio.vo.EvolucaoVO;
import br.gov.mec.aghu.ambulatorio.vo.ExamesLiberadosVO;
import br.gov.mec.aghu.ambulatorio.vo.GeraEvolucaoVO;
import br.gov.mec.aghu.ambulatorio.vo.PreGeraItemQuestVO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.emergencia.action.ListaPacientesEmergenciaPaginatorController;
import br.gov.mec.aghu.exames.pesquisa.action.PesquisaFluxogramaController;
import br.gov.mec.aghu.exames.solicitacao.action.SolicitacaoExameController;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamItemEvolucoes;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.MamTipoItemEvolucao;
import br.gov.mec.aghu.model.MamValorValidoQuestao;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.paciente.prontuarioonline.action.ArvorePOLController;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Controller da tela de Pesquisar Consulta/Agenda
 * 
 * @author georgenes.zapalaglio
 * 
 */
public class AtenderPacientesEvolucaoController extends ActionController {
	private static final long serialVersionUID = -3744812806595136737L;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
    @EJB
    private ICascaFacade cascaFacade;
    
    private String acao;
	
	private List<ExamesLiberadosVO> listaExamesLiberados;
	
	private List<ExamesLiberadosVO> examesSelecionados;
	
	private ExamesLiberadosVO exameSelecionado;

	private Integer selectedTab;

	private Boolean readonlyEvolucao;

	private List<MamNotaAdicionalEvolucoes> notasAdicionaisEvolucaoList;

	private String textoEvolucao;

	private String descrNotaAdicionalEvolucao;

	private MamNotaAdicionalEvolucoes notaAdicionalEvolucoes;

	private Integer sliderAtual;

	private Boolean habilitaEvolucao = false;
	
	private MpmPrescricaoMedica prescricaoMedica;
	
	private AghEspecialidades especialidade;
	
	private AipPacientes paciente;
	
	private String cameFrom;
	
	private AacConsultas consultaSelecionada;
	
	private String titleAccordion;
	
	private Integer cagSeq;
	
	private List<EvolucaoVO> listaBotoes;
	
	private Boolean permiteColar;
	
	private Boolean alterado;
	
	private String idadeFormatada;
	
	private Long evoSeq;
	
	private String motivoPendencia;
	
	private boolean executouIniciar;
	
	private boolean exibirIconePol;
	
	private List<PreGeraItemQuestVO> listaPreGeraItemQuestVO;
	private boolean p1RetornaRegistro;
	
	private EspecialidadeRelacionadaVO especialidadeRelacionadaVO;
	
	@Inject
	private PesquisarPacientesAgendadosController pesquisarPacientesAgendadosController;
	
	@Inject
	private AtenderPacientesAgendadosController atenderPacientesAgendadosController;

	@Inject
	private ManterResumoCasoListController manterResumoCasoListController;
	
	@Inject
	private ListaPacientesEmergenciaPaginatorController listaPacientesEmergenciaController;
	
	private static final String LISTA_PACIENTES_EMERGENCIA = "emergencia-listaPacientesEmergencia";
	
	@Inject
	private ArvorePOLController arvorePOLController;
	@Inject
	private SolicitacaoExameController solicitacaoExameController;
	@Inject 
	private PesquisaFluxogramaController pesquisaFluxogramaController;
	
	private SolicitacaoExameFilter filtro = new SolicitacaoExameFilter();
		
	private static final String PAGE_PESQUISAR_PACIENTES_AGENDADOS = "ambulatorio-pesquisarPacientesAgendados";
	private static final String PAGE_RESUMO_CASOS = "ambulatorio-resumoCasoList";
	private static final String PAGE_SOLICITAR_EXAMES = "exames-solicitacaoExameCRUD";
	private static final String PAGE_FLUXOGRAMA = "exames-pesquisaFluxograma";
	private static final String PAGE_CHAMADORA = "ambulatorio-atenderPacientesEvolucao";
	

	private final String IMPORTAR_RESULTADO_EXAMES = "Importar Resultado Exames";
		
	private GeraEvolucaoVO evolucao;
	
	private EvolucaoVO botaoSelecionado;
	private final String CONSULTAR_EXAMES = "Consultar Exames";
	private final String SOLICITAR_EXAMES = "Solicitar Exames";
	private final String FLUXOGRAMA = "Fluxograma";
	private final String ALTA_AMBULATORIAL = "Alta Ambulatorial";
	
	private Integer atendimentoSeq;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	@SuppressWarnings({"PMD.NPathComplexity"})
	public void iniciar() {
		if (!this.executouIniciar) {
			motivoPendencia = "POS";
			alterado = false;
			evoSeq = null;
			
			pacientePossuiProntuarioParaExibicaoPOL();
			
			try {
				permiteColar = ambulatorioFacade.verificaColar();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			
			List<MamEvolucoes> C1 = ambulatorioFacade.pesquisarMamEvolucoesPaciente(consultaSelecionada.getNumero());
			if(C1 != null){
				if(C1.size() > 0){
					evoSeq = C1.get(0).getSeq();
				}
			}
			
			List<CseCategoriaProfissional> catProf = cascaFacade.pesquisarCategoriaProfissional(servidorLogadoFacade.obterServidorLogado());
			if(catProf != null){
				if(catProf.size() > 0){
					cagSeq = catProf.get(0).getSeq();
				}
			}
			titleAccordion = "Evolução"; //C2 #49956
			List<MamTipoItemEvolucao> listaB = ambulatorioFacade.pesquisarTipoItemEvolucaoBotoes(cagSeq);
			if(listaB != null){
				if(listaB.size() > 0){
					titleAccordion = listaB.get(0).getDescricao();
					listaBotoes = new ArrayList<EvolucaoVO>();
					for (MamTipoItemEvolucao item : listaB) {
						EvolucaoVO evo = new EvolucaoVO();
						evo.setTipoItemEvolucao(item);
						evo.setRender(Boolean.FALSE);
						if(evoSeq != null){
							List<MamItemEvolucoes> C3 = ambulatorioFacade.pesquisarItemEvolucaoPorEvolucaoTipoItem(evoSeq, item.getSeq());
							if(C3 != null){
								if(C3.size() > 0){
									evo.setTexto(C3.get(0).getDescricao());
								}
							}
						}
						listaBotoes.add(evo);
					}
					listaBotoes.get(0).setRender(Boolean.TRUE);
					this.especialidadeRelacionadaVO = this.ambulatorioFacade.obterDadosEspecialidadesRelacionadoAConsulta(consultaSelecionada.getNumero());
					
					if(evoSeq == null){
						try {
							evolucao = this.ambulatorioFacade.geraEvolucao(consultaSelecionada.getNumero(), null);
							evoSeq = evolucao.getEvoSeq();
							for (EvolucaoVO registro : listaBotoes) {
								registro.setListaPreGeraItemQuestVO(new ArrayList<PreGeraItemQuestVO>(obterListaPreGeraItemQuestVO(registro.getTipoItemEvolucao().getSeq())));
							}
						} catch (ApplicationBusinessException e) {
							apresentarMsgNegocio(Severity.ERROR, e.getMessage());
						}
						this.listaPreGeraItemQuestVO = listaBotoes.get(0).getListaPreGeraItemQuestVO();
					}else{
						for (EvolucaoVO registro : listaBotoes) {
							registro.setListaPreGeraItemQuestVO(new ArrayList<PreGeraItemQuestVO>(obterListaPreGeraItemQuestVO(registro.getTipoItemEvolucao().getSeq())));
						}
						this.listaPreGeraItemQuestVO = listaBotoes.get(0).getListaPreGeraItemQuestVO();
					}
				}
			}
			this.executouIniciar = true;
		}
	}

	public void negarRespostas() {

		for (PreGeraItemQuestVO item : listaPreGeraItemQuestVO) {
			if (!item.isCheckValor() && StringUtils.isBlank(item.getResposta()) && StringUtils.isNotBlank(item.getPergunta())
					&& StringUtils.isNotBlank(item.getValor()) && !ambulatorioFacade.verificarCustomizacaoRespondida(Character.valueOf('E'), item.getQusQutSeq(),
							item.getQusSeqP(), item.getpEvoSeq())) {
				item.setCheckValor(Boolean.TRUE);
				item.setVvqQusQutSeq(item.getQusQutSeq());
				item.setVvqQusSeqP(item.getQusSeqP());
				item.setVvqSeqP(item.getSeqP());
			}
		}
	}

	private void pacientePossuiProntuarioParaExibicaoPOL() {
		if (consultaSelecionada != null && consultaSelecionada.getPaciente() != null
				&& consultaSelecionada.getPaciente() != null) {
			this.exibirIconePol = true;
		}
	}
	
	public List<PreGeraItemQuestVO>obterListaPreGeraItemQuestVO(Integer tieSeq){
		
		this.listaPreGeraItemQuestVO = this.ambulatorioFacade.pPreGeraItemQuest(evoSeq, especialidadeRelacionadaVO.getSeq(), tieSeq, paciente.getCodigo(), this.ambulatorioFacade.mamCGetTipoPac(consultaSelecionada.getNumero()));
		p1RetornaRegistro = true;

		if(this.listaPreGeraItemQuestVO == null || this.listaPreGeraItemQuestVO.isEmpty()){
			p1RetornaRegistro = false;
			this.listaPreGeraItemQuestVO = this.ambulatorioFacade.obterListaPreGeraItemQuestVO(evoSeq, tieSeq, this.ambulatorioFacade.mamCGetTipoPac(consultaSelecionada.getNumero()));
		}else{
			List<PreGeraItemQuestVO> listaC3 = this.ambulatorioFacade.obterListaPreGeraItemQuestVO(evoSeq, tieSeq, this.ambulatorioFacade.mamCGetTipoPac(consultaSelecionada.getNumero()));
			
			if(listaC3 != null && !listaC3.isEmpty()){
				if(this.listaPreGeraItemQuestVO.size() < listaC3.size()){
					this.listaPreGeraItemQuestVO = listaC3;
				}			
			}
		}
		
		obterSeqValorSituacaoMamValValidoQuestao(this.listaPreGeraItemQuestVO);
		
		return this.listaPreGeraItemQuestVO;
	}
	
	public void alterou(){
		alterado = true;
	}
	
	public void alteraTab(EvolucaoVO botao){
		for (EvolucaoVO evolucaoVO : listaBotoes) {
			evolucaoVO.setRender(Boolean.FALSE);
		}
		botao.setRender(Boolean.TRUE);
		this.botaoSelecionado = botao;
		titleAccordion = botao.getTipoItemEvolucao().getDescricao();
		this.listaPreGeraItemQuestVO = botao.getListaPreGeraItemQuestVO();
		RequestContext.getCurrentInstance().execute("binds();");
	}
	
	public String voltar(){
		//if(alterado){
			apresentarMsgNegocio(Severity.INFO, "MSG2_VOLTAR_EVOLUCAO");
			return null;
		//}
		//return redirect();
	}
	
	public String redirect(){
		titleAccordion = null;
		listaBotoes = null;
		cagSeq = null;
		consultaSelecionada = null;
		evoSeq = null;		
		executouIniciar = false;
		if ("listarPacientesEmergenciaAbaAguardando".equals(cameFrom)) {
			this.listaPacientesEmergenciaController.setAbaSelecionada(2);
			this.listaPacientesEmergenciaController.pesquisarPacientesAguardandoAtendimento();
			return LISTA_PACIENTES_EMERGENCIA;
		}
		if ("listarPacientesEmergenciaAbaEmAtendimento".equals(cameFrom)) {
			this.listaPacientesEmergenciaController.setAbaSelecionada(3);
			this.listaPacientesEmergenciaController.pesquisarPacientesEmAtendimento();
			return LISTA_PACIENTES_EMERGENCIA;
		}
		else {
			return cameFrom;
		}
	}
	
	public void gravar(){
		try {
			ambulatorioFacade.gravarEvolucao(consultaSelecionada.getNumero(),evoSeq,listaBotoes);
			alterado = false;
//			this.executouIniciar = false;
//			iniciar();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EVOLUCAO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String gravarOk(){
		try {
			ambulatorioFacade.gravarOkEvolucao(consultaSelecionada.getNumero(), evoSeq, listaBotoes);
			this.atenderPacientesAgendadosController.setEvoSeq(evoSeq);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EVOLUCAO");
			return redirect();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String excluir(){
		try {
			ambulatorioFacade.excluirEvolucao(consultaSelecionada.getNumero(), evoSeq, botaoSelecionado);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return redirect();
	}	
	
	public String gravarMotivoPendencia(){
		try {
			String nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			ambulatorioFacade.gravarMotivoPendente(consultaSelecionada.getNumero(), motivoPendencia, nomeMicrocomputador, evoSeq);
			this.closeDialog("modalPendenteWG");
			List<DocumentosPacienteVO> listaDocumentosPaciente = this.ambulatorioFacade
					.obterListaDocumentosPaciente(this.consultaSelecionada.getNumero(), null, true);
			
			if (!listaDocumentosPaciente.isEmpty()) {
				pesquisarPacientesAgendadosController.setListaDocumentosPaciente(listaDocumentosPaciente);
				pesquisarPacientesAgendadosController.setFlagModalImprimir(Boolean.TRUE);
				this.openDialog("modalFinalizarAtendimentoWG");
				return null;
			} else {
				titleAccordion = null;
				listaBotoes = null;
				cagSeq = null;
				consultaSelecionada = null;
				evoSeq = null;		
				executouIniciar = false;
				if (cameFrom.equals("listarPacientesEmergenciaAbaAguardando")) {
					this.listaPacientesEmergenciaController.setAbaSelecionada(2);
					this.listaPacientesEmergenciaController.pesquisarPacientesAguardandoAtendimento();
					return LISTA_PACIENTES_EMERGENCIA;
				}
				else {
					this.pesquisarPacientesAgendadosController.pesquisar();
					return PAGE_PESQUISAR_PACIENTES_AGENDADOS;
				}
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			apresentarMsgNegocio(Severity.ERROR,e.getMessage());
		}
	    return null;
	}
	
	/**
	 * Ação do combo.
	 */
	public void executarAcao() {
		if (this.acao != null && !this.acao.isEmpty()) {
			if (this.acao.equals(IMPORTAR_RESULTADO_EXAMES)) {
				montarTelaExamesLiberados();
			}else{
				pagItemSelecionado();
			}
		}
	}
	
	/**
	 * Monta popup de exames liberados.
	 */
	private void montarTelaExamesLiberados() {
		Integer codigoPaciente = null;
		Integer numeroConsulta = consultaSelecionada.getNumero();
		
		try {
			if (this.evoSeq == null) {
				GeraEvolucaoVO geraEvolucaoVO = ambulatorioFacade.geraEvolucao(numeroConsulta, evoSeq);
				this.evoSeq = geraEvolucaoVO.getEvoSeq();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
		
		if (this.consultaSelecionada != null && this.consultaSelecionada.getPaciente() != null) {
			codigoPaciente = this.consultaSelecionada.getPaciente().getCodigo();
		}
		
		if (this.acao != null && !this.acao.isEmpty()) {
			if (this.acao.equals(IMPORTAR_RESULTADO_EXAMES) && codigoPaciente != null) {
				try {
					this.listaExamesLiberados = ambulatorioFacade.montarTelaExamesLiberados(codigoPaciente, numeroConsulta);
					RequestContext context = RequestContext.getCurrentInstance();
					context.execute("PF('modalExamesLiberadosWG').show();");
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
		}
	}
	
	/**
	 * Copia exames selecionados
	 */
	public void copiarEscolhidos() {
		if (listaExamesLiberados != null && !listaExamesLiberados.isEmpty()) {
			examesSelecionados = new ArrayList<ExamesLiberadosVO>();
			for (ExamesLiberadosVO vo: listaExamesLiberados) {
				if (vo.isCheckbox()) {
					examesSelecionados.add(vo);
				}
			}
			if (!examesSelecionados.isEmpty()) {
				ambulatorioFacade.copiarEscolhidos(evoSeq, examesSelecionados);
				apresentarMsgNegocio("MSG_EXAMES_COPIADOS_SUCESSO");
			}
		}		
	}
	
	public String mostrarDataExameFormatada(Date data) {
		if (data == null) {
			return "";
		} else {
			return DateUtil.obterDataFormatada(data, "dd/MM/yyyy HH:mm:ss");
		}
	}
	
	public String truncarTexto(String texto, Integer tamanho) {		
		if (texto == null || texto.isEmpty()) {
			return "";
		} else {
			return StringUtils.abbreviate(texto, tamanho);
		}
	}
	
	//Botão que irá redicionar para a tela da estória #11893
	public String btResumoCasos(){
		this.manterResumoCasoListController.setNumProntuario(this.consultaSelecionada.getNumero());
		return PAGE_RESUMO_CASOS;
	}

	public void obterSeqValorSituacaoMamValValidoQuestao(List<PreGeraItemQuestVO> listaIteracao){
		if(listaIteracao != null && !listaIteracao.isEmpty()){
			for (PreGeraItemQuestVO registro : listaIteracao) {
				MamValorValidoQuestao mamValorValidoQuestao = this.ambulatorioFacade.obterSeqValorSituacaoMamValValidoQuestao(registro.getQusQutSeq(), registro.getQusSeqP());
				
				if(mamValorValidoQuestao != null){
					registro.setRenderCheck(Boolean.TRUE);
					if (!p1RetornaRegistro) {
						if(registro.getVvqQusQutSeq() != null && registro.getVvqQusSeqP() != null && registro.getVvqSeqP() != null){
						registro.setCheckValor(Boolean.TRUE);
						} else{
						registro.setCheckValor(Boolean.FALSE);
						}
					}
					registro.setValor(mamValorValidoQuestao.getValor());
				}
			}
		}
	}
	
	// Botão que irá redicionar para a tela da estória #43025
		public void btImprimir() {
			AacConsultas aacConsulta = this.ambulatorioFacade.obterAacConsultasJoinGradeEEspecialidade(this.consultaSelecionada.getNumero());
			AipPacientes paciente = new AipPacientes();
			paciente.setNome(this.consultaSelecionada.getPaciente().getNome());
			paciente.setIdade(this.consultaSelecionada.getPaciente().getIdade());
			paciente.setProntuario(this.consultaSelecionada.getPaciente().getProntuario());
			aacConsulta.setPaciente(paciente);

			if (aacConsulta != null) {
				try {
					if (this.ambulatorioFacade.existeDocumentosImprimirPaciente(aacConsulta, true)) {
						this.atenderPacientesAgendadosController.setConsultaSelecionada(aacConsulta);
						this.atenderPacientesAgendadosController.setListaDocumentosPaciente(obterListaDocumentosPacientePesquisa());
						RequestContext.getCurrentInstance().execute("PF('modalFinalizarAtendimentoWG').show()");
					}
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
		}
		
		public List<DocumentosPacienteVO> obterListaDocumentosPacientePesquisa() throws ApplicationBusinessException {
			List<DocumentosPacienteVO> listaDocumentosPaciente = this.ambulatorioFacade.obterListaDocumentosPaciente(this.consultaSelecionada.getNumero(),
					 this.consultaSelecionada.getGrdSeq(), true);

			 if(listaDocumentosPaciente != null && !listaDocumentosPaciente.isEmpty()){
				 for (DocumentosPacienteVO element : listaDocumentosPaciente) {
					 element.setSelecionado(false);
				 }
			}
			 return listaDocumentosPaciente;
		}
	
	public String obterConsultarExame(){
		return CONSULTAR_EXAMES;
	}
	public String obterSolicitarExame(){
		return SOLICITAR_EXAMES;
	}
	public String obterFluxograma(){
		return FLUXOGRAMA;
	}
	public String obterAltaAmbulatorial(){
		return ALTA_AMBULATORIAL;
	}

	public void pagItemSelecionado(){
		//String retorno = null;
		Integer prontuario = null;
		if(consultaSelecionada != null && consultaSelecionada.getPaciente() != null && consultaSelecionada.getPaciente().getProntuario() != null){
			prontuario = consultaSelecionada.getPaciente().getProntuario();
		}
		
		if(atenderPacientesAgendadosController.getAtendimento() != null &&
				atenderPacientesAgendadosController.getAtendimento().getSeq() != null){
			this.atendimentoSeq = atenderPacientesAgendadosController.getAtendimento().getSeq();
		}
		
		if (this.acao != null && !this.acao.isEmpty() && prontuario != null) {
			if (this.acao.equals(CONSULTAR_EXAMES)) {
				arvorePOLController.setProntuario(prontuario);
				arvorePOLController.setChamadaForaPOL(false);
				arvorePOLController.setChamadaExames(true);
				arvorePOLController.addPOLPaciente();
			}else if(this.acao.equals(SOLICITAR_EXAMES) && this.atendimentoSeq != null){
				solicitacaoExameController.setIsSolicitarExame(true);
				solicitacaoExameController.setOrigemAmbulatorio(true);
				solicitacaoExameController.setAtendimentoSeq(this.atendimentoSeq);
				solicitacaoExameController.setPaginaChamadora(PAGE_CHAMADORA);
								
				FacesContext facesContext = FacesContext.getCurrentInstance();
				String outcome = PAGE_SOLICITAR_EXAMES; // Do your thing?
                facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, null, outcome);
			}else if(this.acao.equals(FLUXOGRAMA)){
				pesquisaFluxogramaController.setProntuario(prontuario);
				pesquisaFluxogramaController.setIsFluxograma(true);
				pesquisaFluxogramaController.setExibirBotaoVoltar(true);
			
				FacesContext facesContext = FacesContext.getCurrentInstance();
				String outcome = PAGE_FLUXOGRAMA; // Do your thing?
                facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, null, outcome);
			}else if(this.acao.equals(ALTA_AMBULATORIAL)){
				RequestContext.getCurrentInstance().execute("jsExecutaAltaAmbulatorial()");
			
			}
		}
	}
		
	public String getProntuarioFormatado() {
		return  CoreUtil.formataProntuarioRelatorio(paciente.getProntuario());
	}

	public Integer getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(Integer selectedTab) {
		this.selectedTab = selectedTab;
	}

	public List<MamNotaAdicionalEvolucoes> getNotasAdicionaisEvolucaoList() {
		return notasAdicionaisEvolucaoList;
	}

	public void setNotasAdicionaisEvolucaoList(List<MamNotaAdicionalEvolucoes> notasAdicionaisEvolucaoList) {
		this.notasAdicionaisEvolucaoList = notasAdicionaisEvolucaoList;
	}

	public String getTextoEvolucao() {
		return textoEvolucao;
	}

	public void setTextoEvolucao(String textoEvolucao) {
		this.textoEvolucao = textoEvolucao;
	}

	public void setReadonlyEvolucao(Boolean readonlyEvolucao) {
		this.readonlyEvolucao = readonlyEvolucao;
	}

	public Boolean getReadonlyEvolucao() {
		return readonlyEvolucao;
	}

	public MamNotaAdicionalEvolucoes getNotaAdicionalEvolucoes() {
		return notaAdicionalEvolucoes;
	}

	public void setNotaAdicionalEvolucoes(MamNotaAdicionalEvolucoes notaAdicionalEvolucoes) {
		this.notaAdicionalEvolucoes = notaAdicionalEvolucoes;
	}

	public String getDescrNotaAdicionalEvolucao() {
		return descrNotaAdicionalEvolucao;
	}

	public void setDescrNotaAdicionalEvolucao(String descrNotaAdicionalEvolucao) {
		this.descrNotaAdicionalEvolucao = descrNotaAdicionalEvolucao;
	}

	public Boolean getHabilitaEvolucao() {
		return habilitaEvolucao;
	}

	public void setHabilitaEvolucao(Boolean habilitaEvolucao) {
		this.habilitaEvolucao = habilitaEvolucao;
	}

	public void setSliderAtual(Integer sliderAtual) {
		this.sliderAtual = sliderAtual;
	}

	public Integer getSliderAtual() {
		return sliderAtual;
	}

	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public AacConsultas getConsultaSelecionada() {
		return consultaSelecionada;
	}

	public void setConsultaSelecionada(AacConsultas consultaSelecionada) {
		this.consultaSelecionada = consultaSelecionada;
	}

	public String getTitleAccordion() {
		return titleAccordion;
	}

	public void setTitleAccordion(String titleAccordion) {
		this.titleAccordion = titleAccordion;
	}

	public Integer getCagSeq() {
		return cagSeq;
	}

	public void setCagSeq(Integer cagSeq) {
		this.cagSeq = cagSeq;
	}

	public List<EvolucaoVO> getListaBotoes() {
		return listaBotoes;
	}

	public void setListaBotoes(List<EvolucaoVO> listaBotoes) {
		this.listaBotoes = listaBotoes;
	}

	public Boolean getPermiteColar() {
		return permiteColar;
	}

	public void setPermiteColar(Boolean permiteColar) {
		this.permiteColar = permiteColar;
	}

	public Boolean getAlterado() {
		return alterado;
	}

	public void setAlterado(Boolean alterado) {
		this.alterado = alterado;
	}

	public String getIdadeFormatada() {
		return idadeFormatada;
	}

	public void setIdadeFormatada(String idadeFormatada) {
		this.idadeFormatada = idadeFormatada;
	}

	public Long getEvoSeq() {
		return evoSeq;
	}

	public void setEvoSeq(Long evoSeq) {
		this.evoSeq = evoSeq;
	}

	public String getMotivoPendencia() {
		return motivoPendencia;
	}

	public void setMotivoPendencia(String motivoPendencia) {
		this.motivoPendencia = motivoPendencia;
	}

	public boolean isExecutouIniciar() {
		return executouIniciar;
	}

	public void setExecutouIniciar(boolean executouIniciar) {
		this.executouIniciar = executouIniciar;
	}
	
	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

	public String obterImportarResultadosExames() {
		return IMPORTAR_RESULTADO_EXAMES;
	}

	public List<ExamesLiberadosVO> getListaExamesLiberados() {
		return listaExamesLiberados;
	}

	public List<ExamesLiberadosVO> getExamesSelecionados() {
		return examesSelecionados;
	}

	public void setExamesSelecionados(List<ExamesLiberadosVO> examesSelecionados) {
		this.examesSelecionados = examesSelecionados;
	}

	public void setListaExamesLiberados(List<ExamesLiberadosVO> listaExamesLiberados) {
		this.listaExamesLiberados = listaExamesLiberados;
	}
	public List<PreGeraItemQuestVO> getListaPreGeraItemQuestVO() {
		return listaPreGeraItemQuestVO;
	}

	public void setListaPreGeraItemQuestVO(List<PreGeraItemQuestVO> listaPreGeraItemQuestVO) {
		this.listaPreGeraItemQuestVO = listaPreGeraItemQuestVO;
	}

	public EspecialidadeRelacionadaVO getEspecialidadeRelacionadaVO() {
		return especialidadeRelacionadaVO;
	}

	public void setEspecialidadeRelacionadaVO(
			EspecialidadeRelacionadaVO especialidadeRelacionadaVO) {
		this.especialidadeRelacionadaVO = especialidadeRelacionadaVO;
	}

	public GeraEvolucaoVO getEvolucao() {
		return evolucao;
	}

	public void setEvolucao(GeraEvolucaoVO evolucao) {
		this.evolucao = evolucao;
	}

	public EvolucaoVO getBotaoSelecionado() {
		return botaoSelecionado;
	}

	public void setBotaoSelecionado(EvolucaoVO botaoSelecionado) {
		this.botaoSelecionado = botaoSelecionado;
	}

	public boolean isExibirIconePol() {
		return exibirIconePol;
	}
	public void setExibirIconePol(boolean exibirIconePol) {
		this.exibirIconePol = exibirIconePol;
	}

	public ExamesLiberadosVO getExameSelecionado() {
		return exameSelecionado;
	}

	public void setExameSelecionado(ExamesLiberadosVO exameSelecionado) {
		this.exameSelecionado = exameSelecionado;
	}
	
	public SolicitacaoExameFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(SolicitacaoExameFilter filtro) {
		this.filtro = filtro;
	}

	public Integer getAtendimentoSeq() {
		return atendimentoSeq;
	}

	public void setAtendimentoSeq(Integer atendimentoSeq) {
		this.atendimentoSeq = atendimentoSeq;
	}

}