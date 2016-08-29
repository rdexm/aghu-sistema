package br.gov.mec.aghu.prescricaoenfermagem.action;

import java.net.UnknownHostException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioModulo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.action.PesquisaExameController;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaoenfermagem.vo.PacienteEnfermagemVO;
import br.gov.mec.aghu.prescricaomedica.action.RelatorioListaPacientesController;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ListaPacientePrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusAltaObito;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusSumarioAlta;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

public class ListaPacientesEnfermagemController extends ActionController {

	private static final String CONTROLEPACIENTE_VISUALIZAR_REGISTROS_CONTROLE = "controlepaciente-visualizarRegistrosControle";

	private static final String ELABORACAO_PRESCRICAO_ENFERMAGEM = "prescricaoenfermagem-elaboracaoPrescricaoEnfermagem";

	private static final String EXAMES_SOLICITACAO_EXAME_CRUD = "exames-solicitacaoExameCRUD";

	private static final String PRESCRICAOENFERMAGEM_SELECIONAR_PRESCRICAO_ENFERMAGEM = "prescricaoenfermagem-selecionarPrescricaoEnfermagem";

	private static final String PRESCRICAOMEDICA_SELECIONAR_PRESCRICAO_CONSULTAR = "prescricaomedica-selecionarPrescricaoConsultar";

	private static final String PRESCRICAOMEDICA_CONFIGURAR_LISTA_PACIENTE = "prescricaomedica-configurarListaPacientes";
	
	private static final String VISUALIZAR_REGISTROS = "controlepaciente-visualizarRegistros";
	
	private static final String LISTA_PACIENTES_ENFERMAGEM = "prescricaoenfermagem-listaPacientesEnfermagem";
	
	private final String PAGE_PESQUISAR_EXAMES = "exames-pesquisaExames";

	private static final Log LOG = LogFactory.getLog(ListaPacientesEnfermagemController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 3003441061052024242L;

	/**
	 * Comparator null safe e locale-sensitive String.
	 */
	@SuppressWarnings("unchecked")
	private static final Comparator<Object> PT_BR_COMPARATOR = new Comparator<Object>() {
		@Override
		public int compare(Object o1, Object o2) {
			final Collator collator = Collator.getInstance(new Locale("pt", "BR"));
			collator.setStrength(Collator.PRIMARY);
			return ((Comparable<Object>) o1).compareTo(o2);
		}
	};
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject
	private RelatorioListaPacientesController relatorioListaPacientesController;
	
	@Inject
	private PesquisaExameController pesquisaExameController;
	
	@Inject
	private IAghuFacade aghuFacade;

	private List<PacienteEnfermagemVO> lista;
	private Integer vinculo;
	private Integer matricula;
	private Integer atdSeq;
	private Integer apaSeq;
	private StatusAltaObito labelAlta = StatusAltaObito.ALTA;
	private StatusAltaObito labelObito = StatusAltaObito.OBITO;
	private Boolean desabilitaBotaoPrescrever = true;
	private Boolean desabilitaBotaoControles = true;
	private Boolean desabilitaBotaoSolicitarExames = true;
	private Boolean desabilitaBotaoEvolucao = true;
	private Boolean desabilitaBotaoDiagEnfAtivo = true;
	private Boolean desabilitaBotaoDiagEnfPaciente = true;
	private Boolean escolhaConsultoriaEnfermagem = false;

	private StatusSumarioAlta statusSumario;
	private Comparator<PacienteEnfermagemVO> currentComparator;
	private String currentSortProperty;
	private Integer pacCodigo;
	private String prontuario;
	private Boolean moduloExamesAtivo = false;
	private Boolean moduloPrescMedicaAtivo = false;

	private Boolean integracaoAGHUAGHWEB = false;
	private String corGmr;
	private String corPrevAlta;

	private Boolean desabilitaBotaoSolicitarExamesAGHWEB = true;
	
	private PacienteEnfermagemVO pacienteEnfermagemVOSelecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
		this.pesquisar();
	}

	public void pesquisar() {
		LOG.debug("++ begin pesquisar ");
		RapServidores servidor = getServidor();

		try {
			lista = this.prescricaoEnfermagemFacade.listarPacientes(servidor);
			if (this.lista.isEmpty()) {
				this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO");
			}

			// mantem a ordem corrente
			if (this.currentComparator != null) {
				Collections.sort(this.lista, this.currentComparator);
			}

			moduloExamesAtivo = cascaFacade.verificarSeModuloEstaAtivo(DominioModulo.EXAMES_LAUDOS.getDescricao());

			moduloPrescMedicaAtivo = cascaFacade.verificarSeModuloEstaAtivo(DominioModulo.PRESCRICAO_MEDICA.getDescricao());

			// atualizar os atributos de controle para habilitar/desabilitar
			// os botões de ação
			Boolean atualizaBotoes = true;
			if (this.atdSeq != null) {
				for (PacienteEnfermagemVO vo : lista) {
					if (vo.getAtdSeq().equals(this.atdSeq)) {
						this.setDesabilitaBotaoPrescrever(vo.getDesabilitaBotaoPrescrever());
						this.setDesabilitaBotaoControles(vo.getDesabilitaBotaoControles());
						this.setDesabilitaBotaoEvolucao(vo.getDesabilitaBotaoEvolucao());
						this.setEscolhaConsultoriaEnfermagem(vo.getIndConsultoriaEnf());
						// #40795 verifica se o paciente selecionado continua na lista após uma configuração da lista
						atualizaBotoes = false;
						break;
					}
				}
			}
			
			if(this.atdSeq != null && atualizaBotoes) {
				this.atdSeq = null;
				this.prontuario = null;
				this.pacCodigo = null;
				this.desabilitaBotaoPrescrever = true;
				this.desabilitaBotaoEvolucao = true;
				this.desabilitaBotaoControles = true;
				this.desabilitaBotaoSolicitarExamesAGHWEB = true;
				this.desabilitaBotaoDiagEnfAtivo = true;
				this.desabilitaBotaoDiagEnfPaciente = true;
			}

			AghParametros paramGmr = parametroFacade.obterAghParametro(AghuParametrosEnum.P_AGHU_COR_NOTIF_GMR);
			this.setCorGmr(paramGmr.getVlrTexto());
			this.setCorPrevAlta("#D9FFD9");

			this.setIntegracaoAGHUAGHWEB(verificaIntegracaoAGHUAGHWEB());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error("Erro", e);
		}

		LOG.debug("++ end pesquisar");
	}

	@SuppressWarnings("unchecked")
	public void ordenar(String propriedade) {
		LOG.debug("++ begin ordenar pela propriedade #0" + propriedade);

		Comparator<PacienteEnfermagemVO> comparator = null;

		// se mesma propriedade, reverte a ordem
		if (this.currentComparator != null && this.currentSortProperty.equals(propriedade)) {
			comparator = new ReverseComparator(this.currentComparator);
		} else {
			// cria novo comparator para a propriedade escolhida
			comparator = new BeanComparator(propriedade, new NullComparator(PT_BR_COMPARATOR, false));
		}

		Collections.sort(this.lista, comparator);

		// guarda ordenação corrente
		this.currentComparator = comparator;
		this.currentSortProperty = propriedade;

		LOG.debug("++ end ordenar");
	}

	public String getBancoAghWeb(){
		
		String banco = null;

		try {
			AghParametros aghParametros = this.parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_BANCO_AGHU_AGHWEB);

			if (aghParametros != null) {
				banco = aghParametros.getVlrTexto();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return banco;
	}

	public RapServidores getServidor() {
		RapServidores servidor = null;
		if (vinculo == null || matricula == null) {
			servidor = servidorLogadoFacade.obterServidorLogado();
		} else {
			servidor = new RapServidores(new RapServidoresId(matricula, vinculo.shortValue()));
		}
		return servidor;
	}
	
	/**
	 * Faz validação de horário inicial de prescrição de enfermagem.
	 * Redireciona para a tela de prescrição
	 *@return
	 */
	public String realizarValidacaoChamadaPrescricao(){
		
		Date dataValidaHorario = this.getAtendimentoPorseq().getUnidadeFuncional().getHrioValidadePen();
				
				if(dataValidaHorario == null) {
					apresentarMsgNegocio(Severity.WARN, "ERRO_HORARIO_PRESC_ENFERMAGEM");
					return null;
				}
		
		return ELABORACAO_PRESCRICAO_ENFERMAGEM;
	}
	
	private AghAtendimentos getAtendimentoPorseq(){
		return aghuFacade.buscarAtendimentoPorSeq(atdSeq);
	}

	/**
	 * Apenas redireciona para a tela de prescrição
	 * 
	 * @return
	 */
	public String realizarChamadaPrescricao() {
		String retorno = null;

		if (atdSeq != null) {
			retorno = ELABORACAO_PRESCRICAO_ENFERMAGEM;
		}

		return retorno;
	}

	public String configurarLista() {
		return PRESCRICAOMEDICA_CONFIGURAR_LISTA_PACIENTE;
	}

	public String visualizarRegistrosControle() {
		return CONTROLEPACIENTE_VISUALIZAR_REGISTROS_CONTROLE;
	}

	public String consultarPrescricaoMedica() {
		return PRESCRICAOMEDICA_SELECIONAR_PRESCRICAO_CONSULTAR;
	}

	public String consultarPrescricaoEnfermagem() {
		return PRESCRICAOENFERMAGEM_SELECIONAR_PRESCRICAO_ENFERMAGEM;
	}

	public String solicitarExames() {
		return EXAMES_SOLICITACAO_EXAME_CRUD;
	}

	public StatusAltaObito getLabelAlta() {
		return labelAlta;
	}

	
	public void selecionarPacienteEnfermagem(){
		if (getPacienteEnfermagemVOSelecionado() != null){
		     this.atdSeq = getPacienteEnfermagemVOSelecionado().getAtdSeq();
		     this.pacCodigo =  getPacienteEnfermagemVOSelecionado().getPacCodigo();
	         this.prontuario = getPacienteEnfermagemVOSelecionado().getProntuario();	         
	         this.habilitarBotoes();
			
		}
	}
	
	public void habilitarBotoes() {				
		if (moduloExamesAtivo) {
			this.setDesabilitaBotaoSolicitarExames(false);
			desabilitaBotaoSolicitarExamesAGHWEB = true;
		} else {
			if (integracaoAGHUAGHWEB == true) {
				desabilitaBotaoSolicitarExamesAGHWEB = false;
			}
		}
		if(integracaoAGHUAGHWEB == true){
			desabilitaBotaoDiagEnfAtivo = false;
			desabilitaBotaoDiagEnfPaciente = false;
		}
		if (this.atdSeq != null) {
			for (PacienteEnfermagemVO vo : lista) {
				if (vo.getAtdSeq().equals(this.atdSeq)) {
					this.setDesabilitaBotaoPrescrever(vo.getDesabilitaBotaoPrescrever());
					this.setDesabilitaBotaoControles(vo.getDesabilitaBotaoControles());
					this.setDesabilitaBotaoEvolucao(vo.getDesabilitaBotaoEvolucao());
					this.setEscolhaConsultoriaEnfermagem(vo.getIndConsultoriaEnf());
					break;
				}
			}
		}
	}

	public String getUrlBaseWebForms() {
		String url = null;

		try {
			AghParametros aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_URL_BASE_AGH_ORACLE_WEBFORMS);

			if (aghParametros != null) {
				url = aghParametros.getVlrTexto();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return url;
	}
	
	public String vaiParaRegistroControle() {
		return getVisualizarRegistros();
	}

	private boolean verificaIntegracaoAGHUAGHWEB() {

		try {
			AghParametros aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_INTEGRACAO_AGH_ORACLE_WEBFORMS);

			if (aghParametros != null) {
				String str = aghParametros.getVlrTexto();

				if (str != null && str.equalsIgnoreCase("S")) {
					return true;
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return false;
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

	public void atualizarLista() {
		this.pesquisar();
		this.atdSeq = null;
		this.prontuario = null;
		this.pacCodigo = null;
		this.desabilitaBotaoPrescrever = true;
		this.desabilitaBotaoEvolucao = true;
		this.desabilitaBotaoControles = true;
		this.desabilitaBotaoSolicitarExamesAGHWEB = true;
		this.desabilitaBotaoDiagEnfAtivo = true;
		this.desabilitaBotaoDiagEnfPaciente = true;
		this.desabilitaBotaoSolicitarExames = true;
	}
	
	public void imprimirLista() throws SistemaImpressaoException, ApplicationBusinessException, UnknownHostException, JRException {
		List<ListaPacientePrescricaoVO> listaPacientes = new ArrayList<ListaPacientePrescricaoVO>(); 
		listaPacientes = prescricaoEnfermagemFacade.retornarListaImpressaoPrescricaoEnfermagem(lista);
		relatorioListaPacientesController.setListaPacientes(listaPacientes);
		relatorioListaPacientesController.imprimir();
	}
	
	public String visualizarResultadoExames(PacienteEnfermagemVO paciente){
		
		Boolean resultadosNaoVisualizados = examesFacade.pesquisarExamesResultadoNaoVisualizado(paciente.getAtdSeq());
		
		if (resultadosNaoVisualizados){
			return realizarChamadaExamesNaoVistos(paciente.getAtdSeq());
		} else {
			Integer prontuario = Integer.valueOf(paciente.getProntuario());
			return realizarChamadaPesquisaExames(prontuario);
		}
	}
	
	public String realizarChamadaExamesNaoVistos(Integer atdSeq){
		
		List<AelItemSolicitacaoExames> listaItemSolicExame = this.prescricaoMedicaFacade.pesquisarExamesNaoVisualizados(atdSeq);
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
		this.pesquisaExameController.setVoltarPara(LISTA_PACIENTES_ENFERMAGEM);
		return this.pesquisaExameController.verResultados();
	}
	
	public String realizarChamadaPesquisaExames(Integer prontuario) {
		String retorno = null;
		if (prontuario != null) {
			this.pesquisaExameController.setVoltarPara(LISTA_PACIENTES_ENFERMAGEM);
			this.pesquisaExameController.setProntuario(prontuario);
			retorno = PAGE_PESQUISAR_EXAMES;
		}

		return retorno;
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

	public List<PacienteEnfermagemVO> getLista() {
		return lista;
	}

	public void setLista(List<PacienteEnfermagemVO> lista) {
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

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade() {
		return prescricaoEnfermagemFacade;
	}

	public void setPrescricaoEnfermagemFacade(IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade) {
		this.prescricaoEnfermagemFacade = prescricaoEnfermagemFacade;
	}

	public Boolean getDesabilitaBotaoPrescrever() {
		return desabilitaBotaoPrescrever;
	}

	public void setDesabilitaBotaoPrescrever(Boolean desabilitaBotaoPrescrever) {
		this.desabilitaBotaoPrescrever = desabilitaBotaoPrescrever;
	}

	public Boolean getDesabilitaBotaoControles() {
		return desabilitaBotaoControles;
	}

	public void setDesabilitaBotaoControles(Boolean desabilitaBotaoControles) {
		this.desabilitaBotaoControles = desabilitaBotaoControles;
	}

	public Boolean getDesabilitaBotaoSolicitarExames() {
		return desabilitaBotaoSolicitarExames;
	}

	public void setDesabilitaBotaoSolicitarExames(Boolean desabilitaBotaoSolicitarExames) {
		this.desabilitaBotaoSolicitarExames = desabilitaBotaoSolicitarExames;
	}

	public String getCorGmr() {
		return corGmr;
	}

	public void setCorGmr(String corGmr) {
		this.corGmr = corGmr;
	}

	public String getCorPrevAlta() {
		return corPrevAlta;
	}

	public void setCorPrevAlta(String corPrevAlta) {
		this.corPrevAlta = corPrevAlta;
	}

	public Boolean getDesabilitaBotaoEvolucao() {
		return desabilitaBotaoEvolucao;
	}

	public void setDesabilitaBotaoEvolucao(Boolean desabilitaBotaoEvolucao) {
		this.desabilitaBotaoEvolucao = desabilitaBotaoEvolucao;
	}

	public Boolean getIntegracaoAGHUAGHWEB() {
		return integracaoAGHUAGHWEB;
	}

	public void setIntegracaoAGHUAGHWEB(Boolean integracaoAGHUAGHWEB) {
		this.integracaoAGHUAGHWEB = integracaoAGHUAGHWEB;
	}

	public Boolean getModuloExamesAtivo() {
		return moduloExamesAtivo;
	}

	public void setModuloExamesAtivo(Boolean moduloExamesAtivo) {
		this.moduloExamesAtivo = moduloExamesAtivo;
	}

	public Boolean getDesabilitaBotaoSolicitarExamesAGHWEB() {
		return desabilitaBotaoSolicitarExamesAGHWEB;
	}

	public void setDesabilitaBotaoSolicitarExamesAGHWEB(Boolean desabilitaBotaoSolicitarExamesAGHWEB) {
		this.desabilitaBotaoSolicitarExamesAGHWEB = desabilitaBotaoSolicitarExamesAGHWEB;
	}

	public Boolean getDesabilitaBotaoDiagEnfAtivo() {
		return desabilitaBotaoDiagEnfAtivo;
	}

	public void setDesabilitaBotaoDiagEnfAtivo(Boolean desabilitaBotaoDiagEnfAtivo) {
		this.desabilitaBotaoDiagEnfAtivo = desabilitaBotaoDiagEnfAtivo;
	}

	public Boolean getDesabilitaBotaoDiagEnfPaciente() {
		return desabilitaBotaoDiagEnfPaciente;
	}

	public void setDesabilitaBotaoDiagEnfPaciente(Boolean desabilitaBotaoDiagEnfPaciente) {
		this.desabilitaBotaoDiagEnfPaciente = desabilitaBotaoDiagEnfPaciente;
	}

	public Boolean getModuloPrescMedicaAtivo() {
		return moduloPrescMedicaAtivo;
	}

	public void setModuloPrescMedicaAtivo(Boolean moduloPrescMedicaAtivo) {
		this.moduloPrescMedicaAtivo = moduloPrescMedicaAtivo;
	}

	public Boolean getEscolhaConsultoriaEnfermagem() {
		return escolhaConsultoriaEnfermagem;
	}

	public void setEscolhaConsultoriaEnfermagem(Boolean escolhaConsultoriaEnfermagem) {
		this.escolhaConsultoriaEnfermagem = escolhaConsultoriaEnfermagem;
	}

	public String getVisualizarRegistros() {
		return VISUALIZAR_REGISTROS;
	}

	public String getListaPacientesEnfermagem() {
		return LISTA_PACIENTES_ENFERMAGEM;
	}

	public PacienteEnfermagemVO getPacienteEnfermagemVOSelecionado() {
		return pacienteEnfermagemVOSelecionado;
	}

	public void setPacienteEnfermagemVOSelecionado(
			PacienteEnfermagemVO pacienteEnfermagemVOSelecionado) {
		this.pacienteEnfermagemVOSelecionado = pacienteEnfermagemVOSelecionado;
	}

}