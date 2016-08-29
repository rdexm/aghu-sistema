package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioTipoAcomodacao;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.MpaProtocoloAssistencial;
import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.model.MptProtocoloCiclo;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ExtratoSessaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteAguardandoAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteEmAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteAcolhimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteConcluidoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.RegistroIntercorrenciaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.utils.DateUtil;


public class AbaAgardandoAtendimentoPaginatorController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5626145373844848030L;

	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ListaTrabalhoSessaoTerapeuticaPaginatorController controleFiltro;
	
	private MpaProtocoloAssistencial mpaProtocoloAssistencial; 

	private MptLocalAtendimento localAtendimento; 
	
	private List<MptTipoSessao> listaTipoSessao = new ArrayList<MptTipoSessao>();

	private Short tipoSessaoCombo;
	
	private DominioTurno turnoCombo;

	private DominioTipoAcomodacao acomodacaoCombo;
	
	private List<MptSalas> listaSalas = new ArrayList<MptSalas>();
	
	private Short salaCombo;
	
	private Date dataInicial;
	
	private RapServidores servidorLogado;
	
	private List<ListaPacienteAguardandoAtendimentoVO> listaPacientes;

	private ListaPacienteAguardandoAtendimentoVO parametroSelecionado;

	private Long countPaciente;

	private MptTurnoTipoSessao horario;
	
	private boolean pause = false;
	
	private int tempo;
	
	private List<String> listaSiglas = new ArrayList<String>();
	
	private boolean exibirColunaLQ;	
	private boolean exibirColunaIP;
	private boolean exibirColunaIS;
	private boolean exibirColunaCP;
	private boolean exibirColunaLM;	
	
	@Inject
	private RegistrarIntercorrenciaController registrarIntercorrencia;
	private List<ExtratoSessaoVO> listaExtratoSessao = new ArrayList<ExtratoSessaoVO>();
	private static final String TRACO = " - ";
	
	@Inject
	private ListaTrabalhoSessaoTerapeuticaPaginatorController listaTrabalhoSessaoTerapeuticaPaginatorController;
	@Inject
	private ListaAcolhimentoPaginatorController listaAcolhimentoPaginatorController;
	private static final String REGISTRAR_INTERCORRENCIA = "procedimentoterapeutico-registrarIntercorrencia";
	private List<RegistroIntercorrenciaVO> listaIntercorrenciasSelecionadas = new ArrayList<RegistroIntercorrenciaVO>();
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	public void iniciar() {	
		listaPacientes = new ArrayList<ListaPacienteAguardandoAtendimentoVO>();		
		pesquisar();
		tempoManipulacao();
	}
	
	
	public void pesquisar() {		
		try {
			procedimentoTerapeuticoFacade.validarCampos(controleFiltro.getDataInicial(), controleFiltro.getTipoSessaoCombo());
			listaPacientes = null;
			countPaciente = null;
			listaSiglas = procedimentoTerapeuticoFacade.obterSiglaCaracteristicaPorTpsSeq(controleFiltro.getTipoSessaoCombo());
			listaPacientes = this.procedimentoTerapeuticoFacade.pesquisarListaPacientesAguardandoAtendimento(controleFiltro.getDataInicial(), 
					controleFiltro.getHorario(), controleFiltro.getTipoSessaoCombo(), controleFiltro.getSalaCombo(), 
					controleFiltro.getLocalAtendimento(), controleFiltro.getAcomodacaoCombo(), controleFiltro.getMpaProtocoloAssistencial());
			parametroSelecionado = null;
			
			procedimentoTerapeuticoFacade.validarListagemAba3(listaPacientes);
			carregarExibicaoComponentes();
		}catch (BaseListException e) {
				apresentarExcecaoNegocio(e);			
		} catch (ApplicationBusinessException e) {
			 apresentarExcecaoNegocio(e);
		}
	}
	
	public Boolean semaforo(ListaPacienteAguardandoAtendimentoVO item){
		if(registrarIntercorrencia.getListaIntercorrenciasSelecionadas() != null && !registrarIntercorrencia.getListaIntercorrenciasSelecionadas().isEmpty()){
			for(ListaPacienteVO vo : registrarIntercorrencia.getListaIntercorrenciasSelecionadas()){
				if (vo instanceof PacienteAcolhimentoVO) {
					PacienteAcolhimentoVO voConvertido = (PacienteAcolhimentoVO) vo;
					if (voConvertido.getCodigoPaciente().equals(item.getCodigoPaciente())) {
						item.setListaIntercorrenciasSelecionadas(voConvertido.getListaIntercorrenciasSelecionadas());
						return Boolean.TRUE;
					}
				}
				if (vo instanceof ListaPacienteEmAtendimentoVO) {
					ListaPacienteEmAtendimentoVO voConvertido = (ListaPacienteEmAtendimentoVO) vo;
					if (voConvertido.getCodigoPaciente().equals(item.getCodigoPaciente())) {
						item.setListaIntercorrenciasSelecionadas(voConvertido.getListaIntercorrenciasSelecionadas());
						return Boolean.TRUE;
					}
				}
				if (vo instanceof PacienteConcluidoVO) {
					PacienteConcluidoVO voConvertido = (PacienteConcluidoVO) vo;
					if (voConvertido.getCodigoPaciente().equals(item.getCodigoPaciente())) {
						item.setListaIntercorrenciasSelecionadas(voConvertido.getListaIntercorrenciasSelecionadas());
						return Boolean.TRUE;
					}
				}
				if (vo instanceof ListaPacienteAguardandoAtendimentoVO) {
					ListaPacienteAguardandoAtendimentoVO voConvertido = (ListaPacienteAguardandoAtendimentoVO) vo;
					if (voConvertido.getCodigoPaciente().equals(item.getCodigoPaciente())) {
						item.setListaIntercorrenciasSelecionadas(voConvertido.getListaIntercorrenciasSelecionadas());
						return Boolean.TRUE;
					}
				}
			}
		}
		return Boolean.FALSE;
	}
	
	/**
	 * 	Marca o paciente.
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	public void emAtendimento(ListaPacienteAguardandoAtendimentoVO paciente) {
		
		if (paciente != null){
			procedimentoTerapeuticoFacade.emAtendimento(paciente);		
			pesquisar();				
		}
	}
	
	/**
	 * Calcular a idade.
	 * @param dataNascimento
	 * @return String
	 * @throws ParseException
	 */
	public String formatarIdade(Date dataNascimento) throws ParseException {
		if(StringUtils.isNotBlank(dataNascimento.toString())){			
			return DateUtil.getIdade(dataNascimento).toString();
		}
		return null;
	}

	/**
	 * Obtém o hint da cor da coluna.
	 * @param colorColuna
	 * @return String
	 * @throws ParseException
	 */
	public String obterHintColuna(String colorColuna) throws ParseException {
		String messagem = "";
		if (colorColuna != null && colorColuna.equals("#FFFFD9")){
			messagem = "Primeira Sessão";
		}else if (colorColuna != null && colorColuna.equals("#00FFFF")){
			messagem = "Paciente portador de Germe Multirresistente.";
		}
		return messagem;
	}

	/**
	 * Obtém o hint da cor da coluna.
	 * @param colorLinha
	 * @return String
	 * @throws ParseException
	 */
	public String obterHintLinha(String colorLinha) throws ParseException {
		String messagem = "";
		if (colorLinha != null && colorLinha.equals("#00FFFF")){
			messagem = "Paciente portador de Germe Multirresistente.";
		}
		return messagem;
	}
	
	
    /**
	 * Trunca nome da Grid.
	 * @param item
	 * @param tamanhoMaximo
	 * @return String truncada.
	 */
	public String obterHint(String item, Integer tamanhoMaximo) {
		String retorno = item;
		if (retorno.length() > tamanhoMaximo) {
			retorno = StringUtils.abbreviate(retorno, tamanhoMaximo);
		}
		return retorno;
	}
	
	/**
	 * Concatena os protocolos ciclo.
	 * @param listaProtocolo
	 * @return String
	 * @throws ParseException
	 */
	public String hintProtocolo(List<MptProtocoloCiclo> listaProtocolo) throws ParseException {
		 
		String resultado = "";
		StringBuilder protocolosBuilder = new StringBuilder();
		 
		resultado = concatenarProtocolo(listaProtocolo, resultado,
				protocolosBuilder);	
		return resultado;
	}

	
	/**
	 * Retorna a lista de protocolos e concatena. 
	 * @param itens
	 * @param tamanhoMaximo
	 * @return String
	 */
	public String protocoloTruncado(List<MptProtocoloCiclo> itens, Integer tamanhoMaximo) {
		
		String resultado = "";
		StringBuilder protocolosBuilder = new StringBuilder();
		
		resultado = concatenarProtocolo(itens, resultado, protocolosBuilder);
		
		if (resultado.length() > tamanhoMaximo) {
			resultado = StringUtils.abbreviate(resultado, tamanhoMaximo);
		}
		
		return resultado;
	}
	
	private String concatenarProtocolo(List<MptProtocoloCiclo> listaProtocolo, String resultado, StringBuilder protocolosBuilder) {
		if (listaProtocolo == null || listaProtocolo.isEmpty()){
			return resultado;
		}else {
			boolean primeira = true;
			for (MptProtocoloCiclo mptProtocoloCiclo : listaProtocolo) {
				if (mptProtocoloCiclo.getDescricao() != null){
					resultado = protocolosBuilder.append(mptProtocoloCiclo.getDescricao()).toString();
					break;
				}else{
					if(primeira){
						resultado = protocolosBuilder.append(mptProtocoloCiclo.getMpaVersaoProtAssistencial().getMpaProtocoloAssistencial().getTitulo()).toString();
						primeira = false;
					}else{
						resultado = protocolosBuilder.append(" - " + mptProtocoloCiclo.getMpaVersaoProtAssistencial().getMpaProtocoloAssistencial().getTitulo()).toString();
					}
				}
			}	
		}
		return resultado;
	}
	
	/**
	 * Retorna os horários do turno;
	 * @throws BaseListException 
	 */
	public void consultarHorarioTurno() {
		
		try {
			
			horario = null;
			if (turnoCombo != null){
				procedimentoTerapeuticoFacade.validarCampoTipoSessao(tipoSessaoCombo);
				MptTurnoTipoSessao retorno = procedimentoTerapeuticoFacade.obterHorariosTurnos(tipoSessaoCombo, turnoCombo);
				
				horario = retorno;				
			}
			
		} catch (ApplicationBusinessException e) {
			turnoCombo = null;			
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Método que volta o paciente para acolhimento.
	 * @param parametroSelecionado
	 * @throws ApplicationBusinessException
	 */
	public void voltarAcolhimento(ListaPacienteAguardandoAtendimentoVO parametroSelecionado) {

		if (parametroSelecionado != null){
			listaAcolhimentoPaginatorController.setListaPacienteAguardandoAtendimentoVO(parametroSelecionado);
			procedimentoTerapeuticoFacade.voltarAcolhimento(parametroSelecionado);		
			pesquisar();				
		}		
	}
	
	/**
	 * Retorna o tempo em segundos para a atualização da Manupulação.
	 */
	public void tempoManipulacao() {
		BigDecimal valor = procedimentoTerapeuticoFacade.tempoManipulacao();		
		tempo = Integer.valueOf(valor.intValue());
	}
	
	public void carregarExibicaoComponentes() {
		exibirColunaLQ = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "MANI");
		exibirColunaIP = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "IMPP");
		exibirColunaIS = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "INTE");
		exibirColunaCP = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "SINA");
		exibirColunaLM = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "LIBM");
	}
	
	public String registrarIntercorrencia(ListaPacienteAguardandoAtendimentoVO item){
		registrarIntercorrencia.setCodigoPaciente(item.getPacCodigo());
		registrarIntercorrencia.setSeqSessao(item.getSeqSessao());
		registrarIntercorrencia.setNome(item.getPaciente());
		registrarIntercorrencia.setProntuario(item.getProntuario());
		registrarIntercorrencia.setSelectAba(3);
		registrarIntercorrencia.setListaPacienteAguardandoAtendimentoVO(item);
		listaTrabalhoSessaoTerapeuticaPaginatorController.setFromBack(true);
		return REGISTRAR_INTERCORRENCIA;
	}
	
	/**
	 * #41703
	 * Método que envia aviso a Farmácia.
	 * @param ListaPacienteAguardandoAtendimentoVO
	 */
	public void liberarQuimioterapia(ListaPacienteAguardandoAtendimentoVO pacienteAguardandoAtendimentoVO){
		try {
		    procedimentoTerapeuticoFacade.liberarQuimioterapia(pacienteAguardandoAtendimentoVO.getSeqSessao(), 
		    		pacienteAguardandoAtendimentoVO.getSeqAtendimento(), controleFiltro.getTipoSessaoCombo());
			pesquisar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public boolean verificarExisteSessao(ListaPacienteAguardandoAtendimentoVO pacienteAguardandoAtendimentoVO){
		return procedimentoTerapeuticoFacade.verificarExisteSessao(pacienteAguardandoAtendimentoVO.getSeqSessao());
	}
	
	public void visualizarExtratoSessaoModal(Integer pacCodigo) {		
		listaExtratoSessao = null;	
		listaExtratoSessao = this.procedimentoTerapeuticoFacade.pesquisarExtratoSessao(pacCodigo);
	}
	
	public String hintExtratoSessao(String tipoJustificativaDescrica, String descricaoJustificativa,Integer tamanhoMaximo) {
		String retorno = StringUtils.EMPTY;
		if(StringUtils.isNotBlank(tipoJustificativaDescrica) && StringUtils.isNotBlank(descricaoJustificativa)){			
			retorno = tipoJustificativaDescrica+TRACO+descricaoJustificativa;
			return retorno = verificarTamanhoString(tamanhoMaximo, retorno);
		} 
		if(StringUtils.isNotBlank(tipoJustificativaDescrica)){
			retorno = tipoJustificativaDescrica;
		} 
		if(StringUtils.isNotBlank(descricaoJustificativa)){
			retorno = descricaoJustificativa;
		}
		retorno = verificarTamanhoString(tamanhoMaximo, retorno);
		return retorno;
	}

	private String verificarTamanhoString(Integer tamanhoMaximo, String retorno) {
		if (retorno.length() > tamanhoMaximo) {
			retorno = StringUtils.abbreviate(retorno, tamanhoMaximo);
		}
		return retorno;
	}
	
	public boolean showHint(String tipoJustificativaDescrica, String descricaoJustificativa,Integer tamanhoMaximo) {
		String retorno = tipoJustificativaDescrica+TRACO+descricaoJustificativa;
		if (retorno.length() > tamanhoMaximo) {
			return true;
		}
		return false;
	}
	
	public boolean showHintServidor(String usuarioServidor, Integer tamanhoMaximo) {
		String retorno = usuarioServidor;
		if (retorno.length() > tamanhoMaximo) {
			return true;
		}
		return false;
	}

	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}

	public MpaProtocoloAssistencial getMpaProtocoloAssistencial() {
		return mpaProtocoloAssistencial;
	}

	public void setMpaProtocoloAssistencial(
			MpaProtocoloAssistencial mpaProtocoloAssistencial) {
		this.mpaProtocoloAssistencial = mpaProtocoloAssistencial;
	}

	public MptLocalAtendimento getLocalAtendimento() {
		return localAtendimento;
	}

	public void setLocalAtendimento(MptLocalAtendimento localAtendimento) {
		this.localAtendimento = localAtendimento;
	}

	public List<MptTipoSessao> getListaTipoSessao() {
		return listaTipoSessao;
	}

	public void setListaTipoSessao(List<MptTipoSessao> listaTipoSessao) {
		this.listaTipoSessao = listaTipoSessao;
	}

	public Short getTipoSessaoCombo() {
		return tipoSessaoCombo;
	}

	public void setTipoSessaoCombo(Short tipoSessaoCombo) {
		this.tipoSessaoCombo = tipoSessaoCombo;
	}

	public DominioTurno getTurnoCombo() {
		return turnoCombo;
	}

	public void setTurnoCombo(DominioTurno turnoCombo) {
		this.turnoCombo = turnoCombo;
	}

	public DominioTipoAcomodacao getAcomodacaoCombo() {
		return acomodacaoCombo;
	}

	public void setAcomodacaoCombo(DominioTipoAcomodacao acomodacaoCombo) {
		this.acomodacaoCombo = acomodacaoCombo;
	}

	public List<MptSalas> getListaSalas() {
		return listaSalas;
	}

	public void setListaSalas(List<MptSalas> listaSalas) {
		this.listaSalas = listaSalas;
	}

	public Short getSalaCombo() {
		return salaCombo;
	}

	public void setSalaCombo(Short salaCombo) {
		this.salaCombo = salaCombo;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

	public List<ListaPacienteAguardandoAtendimentoVO> getListaPacientes() {
		return listaPacientes;
	}

	public void setListaPacientes(
			List<ListaPacienteAguardandoAtendimentoVO> listaPacientes) {
		this.listaPacientes = listaPacientes;
	}

	public ListaPacienteAguardandoAtendimentoVO getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(
			ListaPacienteAguardandoAtendimentoVO parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	public Long getCountPaciente() {
		return countPaciente;
	}

	public void setCountPaciente(Long countPaciente) {
		this.countPaciente = countPaciente;
	}

	public MptTurnoTipoSessao getHorario() {
		return horario;
	}

	public void setHorario(MptTurnoTipoSessao horario) {
		this.horario = horario;
	}

	public ListaTrabalhoSessaoTerapeuticaPaginatorController getControleFiltro() {
		return controleFiltro;
	}

	public void setControleFiltro(
			ListaTrabalhoSessaoTerapeuticaPaginatorController controleFiltro) {
		this.controleFiltro = controleFiltro;
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public int getTempo() {
		return tempo;
	}

	public void setTempo(int tempo) {
		this.tempo = tempo;
	}

	public List<String> getListaSiglas() {
		return listaSiglas;
	}

	public void setListaSiglas(List<String> listaSiglas) {
		this.listaSiglas = listaSiglas;
	}

	public boolean isExibirColunaLQ() {
		return exibirColunaLQ;
	}

	public void setExibirColunaLQ(boolean exibirColunaLQ) {
		this.exibirColunaLQ = exibirColunaLQ;
	}

	public boolean isExibirColunaIP() {
		return exibirColunaIP;
	}

	public void setExibirColunaIP(boolean exibirColunaIP) {
		this.exibirColunaIP = exibirColunaIP;
	}

	public boolean isExibirColunaIS() {
		return exibirColunaIS;
	}

	public void setExibirColunaIS(boolean exibirColunaIS) {
		this.exibirColunaIS = exibirColunaIS;
	}

	public boolean isExibirColunaCP() {
		return exibirColunaCP;
	}

	public void setExibirColunaCP(boolean exibirColunaCP) {
		this.exibirColunaCP = exibirColunaCP;
	}

	public boolean isExibirColunaLM() {
		return exibirColunaLM;
	}

	public void setExibirColunaLM(boolean exibirColunaLM) {
		this.exibirColunaLM = exibirColunaLM;
	}
	
	public List<ExtratoSessaoVO> getListaExtratoSessao() {
		return listaExtratoSessao;
	}

	public void setListaExtratoSessao(List<ExtratoSessaoVO> listaExtratoSessao) {
		this.listaExtratoSessao = listaExtratoSessao;
	}	
	
	public List<RegistroIntercorrenciaVO> getListaIntercorrenciasSelecionadas() {
		return listaIntercorrenciasSelecionadas;
	}

	public void setListaIntercorrenciasSelecionadas(
			List<RegistroIntercorrenciaVO> listaIntercorrenciasSelecionadas) {
		this.listaIntercorrenciasSelecionadas = listaIntercorrenciasSelecionadas;
	}
}