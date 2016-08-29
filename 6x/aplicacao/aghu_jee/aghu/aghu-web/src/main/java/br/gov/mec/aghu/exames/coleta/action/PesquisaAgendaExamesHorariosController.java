package br.gov.mec.aghu.exames.coleta.action;

import java.net.UnknownHostException;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.agendamento.vo.ExamesAgendadosNoHorarioVO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.coleta.business.IColetaExamesFacade;
import br.gov.mec.aghu.exames.coleta.vo.AgendaExamesHorariosVO;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemHorarioAgendadoId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller para tela de pesquisa de agenda de exames e seus horários
 * 
 * @author fpalma
 *
 */
public class PesquisaAgendaExamesHorariosController extends ActionController {

	private static final String EXCECAO_CAPTURADA = "Exceção capturada:";
	private static final String INFORMACAO_COLETA_AMOSTRA = "informacaoColetaAmostra";

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(PesquisaAgendaExamesHorariosController.class);

	private static final long serialVersionUID = 5118710971998752265L;
	
	@EJB
	private IExamesFacade examesFacade;
	@EJB
	private IColetaExamesFacade coletaExamesFacade;
	@EJB
	private IAghuFacade aghuFacade;
	@EJB
	private IPacienteFacade pacienteFacade;
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	// Comparator
	private Comparator<AgendaExamesHorariosVO> currentComparator;
	private String currentSortProperty;
	// Parâmetros do filtro de pesquisa
	private Date dtAgenda;
	private AghUnidadesFuncionais unidadeExecutora;
	private AelSalasExecutorasExames salaExecutoraExames;
	private RapServidoresVO servidor;
	private DominioSituacaoHorario situacaoHorario;
	private AelUnidExecUsuario usuarioUnidadeExecutora;

	// Item selecionado
	private Short seqVO;
	private Integer atdSeq;
	private Integer pacCodigo;
	private Integer prontuario;
	private Integer soeSeq;
	private AgendaExamesHorariosVO itemSelecionado;
	private Long longTime;
	
	private DominioSituacaoHorario[] listaSituacaoHorario = {DominioSituacaoHorario.M, DominioSituacaoHorario.E};
	private List<AgendaExamesHorariosVO> listaAgendaExames;
	
	// Flag para indicar se já foi feita uma pesquisa
	private Boolean pesquisaEfetuada;
	private Boolean desabilitaBotaoReceberPaciente;
	private Boolean desabilitaBotaoVoltarRecebimento;
	private Boolean desabilitaBotaoAmostras;
	private Boolean desabilitaBotaoExamesAgendados;
	private Boolean desabilitaBotaoTransferir;
	private Boolean desabilitaBotaoVoltarColeta;
	
	private List<ExamesAgendadosNoHorarioVO> listaExamesAgendadosNoHorario;
	private List<AelItemSolicitacaoExames> listaItemSolicitacaoExames;
	
	
	/**
	 * Comparator null safe e locale-sensitive String.
	 */
	@SuppressWarnings("unchecked")
	private static final Comparator<Object> PT_BR_COMPARATOR = new Comparator<Object>() {
		@Override
		public int compare(Object o1, Object o2) {

			final Collator collator = Collator.getInstance(new Locale("pt",
					"BR"));
			collator.setStrength(Collator.PRIMARY);

			return ((Comparable<Object>) o1).compareTo(o2);
		}
	};
	
	public void carregarModal(){
		listaExamesAgendadosNoHorario = this.coletaExamesFacade.pesquisarExamesAgendadosNoHorario(
				this.itemSelecionado.getHedGaeUnfSeq(), this.itemSelecionado.getHedGaeSeqp(),
				this.itemSelecionado.getHedDthrAgenda());
	}

	public List<ExamesAgendadosNoHorarioVO> getListaExamesAgendadosNoHorario() {
		return listaExamesAgendadosNoHorario;
	}

	public void setListaExamesAgendadosNoHorario(
			List<ExamesAgendadosNoHorarioVO> listaExamesAgendadosNoHorario) {
		this.listaExamesAgendadosNoHorario = listaExamesAgendadosNoHorario;
	}
	
	@SuppressWarnings("unchecked")
	public void ordenar(String propriedade) {
		LOG.debug("++ begin ordenar pela propriedade " + propriedade);

		Comparator<AgendaExamesHorariosVO> comparator = null;

		// se mesma propriedade, reverte a ordem
		if (this.currentComparator != null
				&& this.currentSortProperty.equals(propriedade)) {
			comparator = new ReverseComparator(this.currentComparator);
		} else {
			// cria novo comparator para a propriedade escolhida
			comparator = new BeanComparator(propriedade, new NullComparator(
					PT_BR_COMPARATOR, false));
		}

		Collections.sort(this.listaAgendaExames, comparator);

		// guarda ordenação corrente
		this.currentComparator = comparator;
		this.currentSortProperty = propriedade;

		LOG.debug("++ end ordenar");
	}
	
	/**
	 * Metodo que limpa os campos de filtro<br>
	 * na tele de pesquisa de exames.
	 */
	public void limparPesquisa() {
		this.dtAgenda = null;
		this.unidadeExecutora = null;
		this.salaExecutoraExames = null;
		this.servidor = null;
		this.situacaoHorario = null;
		this.pesquisaEfetuada = false;
		this.listaAgendaExames = null;
		desabilitarBotoes();
	}
	
	private void desabilitarBotoes() {
		this.atdSeq = null;
		this.pacCodigo = null;
		this.prontuario = null;
		this.soeSeq = null;
		this.itemSelecionado = null;
		this.seqVO = null;
		this.listaItemSolicitacaoExames = null;
		setDesabilitaBotaoReceberPaciente(Boolean.TRUE);
		setDesabilitaBotaoVoltarRecebimento(Boolean.TRUE);
		setDesabilitaBotaoAmostras(Boolean.TRUE);
		setDesabilitaBotaoExamesAgendados(Boolean.TRUE);
		setDesabilitaBotaoTransferir(Boolean.TRUE);
		setDesabilitaBotaoVoltarColeta(Boolean.TRUE);
	}
	
	public void limparListaExamesAgendados() {
		this.listaExamesAgendadosNoHorario = null;
	}
	
	public void selecionaItem() {
		for(AgendaExamesHorariosVO item : listaAgendaExames) {
			if(item.getSeqVO().equals(this.seqVO)) {
				this.atdSeq = item.getAtdSeq();
				this.pacCodigo = item.getPacCodigo();
				this.prontuario = item.getProntuario();
				this.soeSeq = item.getSoeSeq();
				this.itemSelecionado = item;
				break;
			}
		}
		habilitarBotoes();
	}
	
	private void habilitarBotoes() {
		//Botão receber paciente
		listaItemSolicitacaoExames = this.coletaExamesFacade.pesquisarItemSolicitacaoExamePorSolicitacaoExame(this.soeSeq);
		for(AelItemSolicitacaoExames item : listaItemSolicitacaoExames) {
			this.coletaExamesFacade.refresh(item);
			if(item.getSituacaoItemSolicitacao().getCodigo().equals("AG")) {
				setDesabilitaBotaoReceberPaciente(Boolean.FALSE);
				break;
			}
			setDesabilitaBotaoReceberPaciente(Boolean.TRUE);
		}
		if(getSoeSeq() == null) {
			setDesabilitaBotaoVoltarRecebimento(Boolean.TRUE);
			setDesabilitaBotaoExamesAgendados(Boolean.TRUE);
			setDesabilitaBotaoAmostras(Boolean.TRUE);
			setDesabilitaBotaoVoltarColeta(Boolean.TRUE);
			setDesabilitaBotaoTransferir(Boolean.TRUE);
		} else {
			setDesabilitaBotaoTransferir(Boolean.FALSE);
			setDesabilitaBotaoAmostras(Boolean.FALSE);
			setDesabilitaBotaoExamesAgendados(Boolean.FALSE);
			setDesabilitaBotaoVoltarColeta(Boolean.FALSE);
			for(AelItemSolicitacaoExames item : listaItemSolicitacaoExames) {
				if(item.getSituacaoItemSolicitacao().getCodigo().equals("AE")) {
					setDesabilitaBotaoVoltarRecebimento(Boolean.FALSE);
					break;
				}
				setDesabilitaBotaoVoltarRecebimento(Boolean.TRUE);
			}
		}
	}
	
	public void inicio() {
		desabilitarBotoes();
		try {
			//Carrega automaticamente a suggestion de unidade executora ao iniciar a tela
			AelUnidExecUsuario usuarioUnidadeExecutora = this.examesFacade
				.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado())
						.getId());
			if(usuarioUnidadeExecutora != null && usuarioUnidadeExecutora.getUnfSeq() != null) {
				unidadeExecutora = usuarioUnidadeExecutora.getUnfSeq();
			}
			this.pesquisaEfetuada = false;
			if(dtAgenda != null && unidadeExecutora != null) {
				pesquisar();
			}

		} catch ( BaseException e) {
			apresentarExcecaoNegocio(e);		
		}
	}	
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(String parametro) {
		return this.aghuFacade.listarUnidadeFuncionalComSala(parametro);
	}
	
	public List<AelSalasExecutorasExames> pesquisarSala(String parametro) {
		return this.examesFacade.pesquisarSalasExecutorasExamesPorNumeroEUnidade(parametro, unidadeExecutora);
	}
	
	public List<RapServidoresVO> pesquisarServidor(String parametro) {
		Short seqP = null;
		if(salaExecutoraExames != null && salaExecutoraExames.getId() != null) {
			seqP = salaExecutoraExames.getId().getSeqp();
		} else {
			return null;
		}
		
		if(unidadeExecutora != null) {
			return registroColaboradorFacade.pesquisarRapServidoresPorUnidFuncESala(parametro, unidadeExecutora.getSeq(), seqP);
		}
		return null;
	}
	
	public void pesquisar(){
		desabilitarBotoes();
		RapServidoresId respId = new RapServidoresId();
		Short seqP = null;
		Short seqUnf = null;
		
		if(servidor != null) {
			respId.setMatricula(servidor.getMatricula());
			respId.setVinCodigo(servidor.getVinculo());
		}
		if(salaExecutoraExames != null && salaExecutoraExames.getId() != null) {
			seqP = salaExecutoraExames.getId().getSeqp();
		}
		if(unidadeExecutora != null) {
			seqUnf = unidadeExecutora.getSeq();
			persistirIdentificacaoUnidadeExecutora();
		}
		
		this.listaAgendaExames = this.coletaExamesFacade.pesquisarAgendaExamesHorarios(dtAgenda, situacaoHorario, seqUnf, seqP, respId);
		for(AgendaExamesHorariosVO item : this.getListaAgendaExames()) {
			if(item.getNomePaciente() == null) {
				item.setNomePaciente(pacienteFacade.obterPacientePorCodigo(item.getPacCodigo()).getNome());
			}
		}
		this.pesquisaEfetuada = true;
	}
	
	/**
	 * Persiste identificacao da unidade executora atraves do usuario logado
	 */
	private void persistirIdentificacaoUnidadeExecutora(){
		try {
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			
			this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(servidorLogado.getId());
			// Persiste identificacao da unidade executora do usuario
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.unidadeExecutora);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void receberPaciente() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
		}
		try {
			coletaExamesFacade.receberItemSolicitacaoExameAgendado(this.itemSelecionado.getHedGaeUnfSeq(), 
					this.itemSelecionado.getHedGaeSeqp(), this.itemSelecionado.getHedDthrAgenda(), 
					this.itemSelecionado.getHedSituacaoHorario(), nomeMicrocomputador);
			apresentarMsgNegocio(Severity.INFO, "AEL_00911");
			pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String tranferirAgendamento(){
		try {
			coletaExamesFacade.validarTransferenciaAgendamento(this.itemSelecionado.getHedSituacaoHorario());
			longTime = this.itemSelecionado.getHedDthrAgenda().getTime();
			return "pesquisaExameTransferenciaAgendamento";
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
			return null;
		}
	}
	
	public String coletarAmostras() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {			
			coletaExamesFacade.executarAtualizacaoColetaAmostraHorarioAgendado(this.itemSelecionado.getHedGaeUnfSeq(), 
					this.itemSelecionado.getHedGaeSeqp(), this.itemSelecionado.getHedDthrAgenda(), nomeMicrocomputador);
			//longTime = this.itemSelecionado.getHedDthrAgenda().getTime();
			return INFORMACAO_COLETA_AMOSTRA;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
			return null;
		}
	}
	
	public void voltarRecebimento(){
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			coletaExamesFacade.voltarItemSolicitacaoExameAgendado(this.itemSelecionado.getHedGaeUnfSeq(), 
					this.itemSelecionado.getHedGaeSeqp(), this.itemSelecionado.getHedDthrAgenda(), 
					this.itemSelecionado.getHedSituacaoHorario(), nomeMicrocomputador);
			apresentarMsgNegocio(Severity.INFO, "AEL_00912");
			pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Redireciona para a tela de amostras
	 * 
	 * @return
	 */
	public String realizarChamadaAmostras() {
		String retorno = null;
		if (soeSeq != null) {
			try {
				AelItemHorarioAgendadoId id = new AelItemHorarioAgendadoId();
				id.setHedGaeUnfSeq(this.itemSelecionado.getHedGaeUnfSeq());
				id.setHedGaeSeqp(this.itemSelecionado.getHedGaeSeqp());
				id.setHedDthrAgenda(this.itemSelecionado.getHedDthrAgenda());
				
				AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
				itemHorarioAgendado.setId(id);
				coletaExamesFacade.validarAmostrasExamesAgendados(itemHorarioAgendado);
				longTime = this.itemSelecionado.getHedDthrAgenda().getTime();
				retorno = "exames-pesquisaAmostrasExames";
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(),e);
			}
		}

		return retorno;
	}
	
	public void voltarColeta() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			coletaExamesFacade.voltarColeta(this.itemSelecionado.getHedGaeUnfSeq(), 
					this.itemSelecionado.getHedGaeSeqp(), this.itemSelecionado.getHedDthrAgenda(), 
					this.itemSelecionado.getHedSituacaoHorario(), nomeMicrocomputador);
			apresentarMsgNegocio(Severity.INFO, "AEL_00875");
			pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void inicializarSalaResponsavel(){
		this.salaExecutoraExames = null;
		this.servidor = null;
	}
	
	public void inicializarResponsavel(){
		this.servidor = null;
	}

	public Date getDtAgenda() {
		return dtAgenda;
	}

	public void setDtAgenda(Date dtAgenda) {
		this.dtAgenda = dtAgenda;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public AelSalasExecutorasExames getSalaExecutoraExames() {
		return salaExecutoraExames;
	}

	public void setSalaExecutoraExames(AelSalasExecutorasExames salaExecutoraExames) {
		this.salaExecutoraExames = salaExecutoraExames;
	}

	public RapServidoresVO getServidor() {
		return servidor;
	}

	public void setServidor(RapServidoresVO servidor) {
		this.servidor = servidor;
	}

	public DominioSituacaoHorario getSituacaoHorario() {
		return situacaoHorario;
	}

	public void setSituacaoHorario(DominioSituacaoHorario situacaoHorario) {
		this.situacaoHorario = situacaoHorario;
	}

	public List<AgendaExamesHorariosVO> getListaAgendaExames() {
		return listaAgendaExames;
	}

	public void setListaAgendaExames(List<AgendaExamesHorariosVO> listaAgendaExames) {
		this.listaAgendaExames = listaAgendaExames;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public DominioSituacaoHorario[] getListaSituacaoHorario() {
		return listaSituacaoHorario;
	}

	public void setListaSituacaoHorario(
			DominioSituacaoHorario[] listaSituacaoHorario) {
		this.listaSituacaoHorario = listaSituacaoHorario;
	}

	public Boolean getPesquisaEfetuada() {
		return pesquisaEfetuada;
	}

	public void setPesquisaEfetuada(Boolean pesquisaEfetuada) {
		this.pesquisaEfetuada = pesquisaEfetuada;
	}

	public Boolean getDesabilitaBotaoReceberPaciente() {
		return desabilitaBotaoReceberPaciente;
	}

	public void setDesabilitaBotaoReceberPaciente(
			Boolean desabilitaBotaoReceberPaciente) {
		this.desabilitaBotaoReceberPaciente = desabilitaBotaoReceberPaciente;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Boolean getDesabilitaBotaoVoltarRecebimento() {
		return desabilitaBotaoVoltarRecebimento;
	}

	public void setDesabilitaBotaoVoltarRecebimento(
			Boolean desabilitaBotaoVoltarRecebimento) {
		this.desabilitaBotaoVoltarRecebimento = desabilitaBotaoVoltarRecebimento;
	}
	
	public Boolean getDesabilitaBotaoAmostras() {
		return desabilitaBotaoAmostras;
	}

	public void setDesabilitaBotaoAmostras(Boolean desabilitaBotaoAmostras) {
		this.desabilitaBotaoAmostras = desabilitaBotaoAmostras;
	}

	public Short getSeqVO() {
		return seqVO;
	}

	public void setSeqVO(Short seqVO) {
		this.seqVO = seqVO;
	}

	public Boolean getDesabilitaBotaoExamesAgendados() {
		return desabilitaBotaoExamesAgendados;
	}

	public void setDesabilitaBotaoExamesAgendados(
			Boolean desabilitaBotaoExamesAgendados) {
		this.desabilitaBotaoExamesAgendados = desabilitaBotaoExamesAgendados;
	}

	public AelUnidExecUsuario getUsuarioUnidadeExecutora() {
		return usuarioUnidadeExecutora;
	}

	public void setUsuarioUnidadeExecutora(
			AelUnidExecUsuario usuarioUnidadeExecutora) {
		this.usuarioUnidadeExecutora = usuarioUnidadeExecutora;
	}

	public AgendaExamesHorariosVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(AgendaExamesHorariosVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public List<AelItemSolicitacaoExames> getListaItemSolicitacaoExames() {
		return listaItemSolicitacaoExames;
	}

	public void setListaItemSolicitacaoExames(
			List<AelItemSolicitacaoExames> listaItemSolicitacaoExames) {
		this.listaItemSolicitacaoExames = listaItemSolicitacaoExames;
	}
	
	public void setLongTime(Long longTime) {
		this.longTime = longTime;
	}

	public Long getLongTime() {
		return longTime;
	}
	
	public Boolean getDesabilitaBotaoTransferir() {
		return desabilitaBotaoTransferir;
	}

	public void setDesabilitaBotaoTransferir(Boolean desabilitaBotaoTransferir) {
		this.desabilitaBotaoTransferir = desabilitaBotaoTransferir;
	}

	public Boolean getDesabilitaBotaoVoltarColeta() {
		return desabilitaBotaoVoltarColeta;
	}

	public void setDesabilitaBotaoVoltarColeta(Boolean desabilitaBotaoVoltarColeta) {
		this.desabilitaBotaoVoltarColeta = desabilitaBotaoVoltarColeta;
	}

}
