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

import br.gov.mec.aghu.model.MptProtocoloCiclo;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ExtratoSessaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteAguardandoAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteEmAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteAcolhimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteConcluidoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.utils.DateUtil;


public class AbaEmAtendimentoPaginatorController extends ActionController {

	private static final String REGISTRAR_INTERCORRENCIA = "procedimentoterapeutico-registrarIntercorrencia";

	/**
	 * 
	 */
	private static final long serialVersionUID = 5626145373844848030L;

	private static final String REGISTRAR_HORARIO_INICIO_FIM_SESSAO = "procedimentoterapeutico-registrarHorarioInicioFimSessao";
	private static final String LISTA_PACIENTE_LIST = "procedimentoterapeutico-listaPacienteList";
	
	@Inject
	private ManterRegistrarHorarioInicioFimSessaoController manterRegistrarHorarioInicioFimSessaoController;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ListaTrabalhoSessaoTerapeuticaPaginatorController controleFiltro;
	
	private List<ListaPacienteEmAtendimentoVO> listaPacientes;

	private ListaPacienteEmAtendimentoVO parametroSelecionado;
	
	private int tempo;
	
	@Inject
	private RegistrarIntercorrenciaController registrarIntercorrencia;
	@Inject
	private ListaTrabalhoSessaoTerapeuticaPaginatorController listaTrabalhoSessaoTerapeuticaPaginatorController;
	@Inject
	private ListaConcluidoPaginatorController listaConcluidoPaginatorController;
	
	private List<String> listaSiglas = new ArrayList<String>();
	private List<ExtratoSessaoVO> listaExtratoSessao = new ArrayList<ExtratoSessaoVO>();
	private static final String TRACO = " - ";
	
	private boolean exibirColunaIS;
	private boolean exibirColunaCP;
	private boolean exibirColunaMD;
	private boolean exibirColunaLM;
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	public void iniciar() {
		listaPacientes = new ArrayList<ListaPacienteEmAtendimentoVO>();		
		pesquisar();		
		tempoManipulacao();
	}
	
	
	public void pesquisar() {		
		try {
			procedimentoTerapeuticoFacade.validarCampos(controleFiltro.getDataInicial(), controleFiltro.getTipoSessaoCombo());
			listaPacientes = null;
			listaSiglas = procedimentoTerapeuticoFacade.obterSiglaCaracteristicaPorTpsSeq(controleFiltro.getTipoSessaoCombo());
			listaPacientes = this.procedimentoTerapeuticoFacade.pesquisarListaPacientesEmAtendimento(controleFiltro.getDataInicial(), 
					controleFiltro.getHorario(), controleFiltro.getTipoSessaoCombo(), controleFiltro.getSalaCombo(), 
					controleFiltro.getLocalAtendimento(), controleFiltro.getAcomodacaoCombo(), controleFiltro.getMpaProtocoloAssistencial());
				
			parametroSelecionado = null;
			
			procedimentoTerapeuticoFacade.validarListagemAba4(listaPacientes);
			carregarExibicaoComponentes();
		}catch (BaseListException e) {
				apresentarExcecaoNegocio(e);			
		} catch (ApplicationBusinessException e) {
			 apresentarExcecaoNegocio(e);
		}
	}
	
	public Boolean semaforo(ListaPacienteEmAtendimentoVO item){
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
	 * Concluir atendimento.
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	public String concluirAtendimento(ListaPacienteEmAtendimentoVO paciente) {
		
		if (paciente != null){	
			listaConcluidoPaginatorController.setListaPacienteEmAtendimentoVO(paciente);
			manterRegistrarHorarioInicioFimSessaoController.setSeqSessao(paciente.getSeqSessao());
			manterRegistrarHorarioInicioFimSessaoController.setPaciente(paciente.getPaciente());
			manterRegistrarHorarioInicioFimSessaoController.setProntuario(paciente.getProntuario());
			manterRegistrarHorarioInicioFimSessaoController.setPacCodigo(paciente.getCodigoPaciente());
			manterRegistrarHorarioInicioFimSessaoController.setCameFrom(LISTA_PACIENTE_LIST);
			manterRegistrarHorarioInicioFimSessaoController.setHabilitardesabilitarCampos(false);
			manterRegistrarHorarioInicioFimSessaoController.setAbaEmAtendimento(Boolean.TRUE);
			listaTrabalhoSessaoTerapeuticaPaginatorController.setFromBack(true);
		}
		return REGISTRAR_HORARIO_INICIO_FIM_SESSAO;
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
	
	public String registrarIntercorrencia(ListaPacienteEmAtendimentoVO item){
		registrarIntercorrencia.setCodigoPaciente(item.getCodigoPaciente());
		registrarIntercorrencia.setSeqSessao(item.getSeqSessao());
		registrarIntercorrencia.setNome(item.getPaciente());
		registrarIntercorrencia.setProntuario(item.getProntuario());
		registrarIntercorrencia.setSelectAba(4);
		registrarIntercorrencia.setListaPacienteEmAtendimentoVO(item);
		listaTrabalhoSessaoTerapeuticaPaginatorController.setFromBack(true);
		return REGISTRAR_INTERCORRENCIA;
	}
	
	/**
	 * Método que volta o paciente para acolhimento.
	 * @param parametroSelecionado
	 * @throws ApplicationBusinessException
	 */
	public void voltarAguardandoAte(ListaPacienteEmAtendimentoVO parametroSelecionado) {
		
		if (parametroSelecionado != null){
			procedimentoTerapeuticoFacade.voltarAguardandoAte(parametroSelecionado);		
			pesquisar();				
		}		
	}

	/**
	 * Atualiza medicamentos administrados em domicilio em MPT_SESSAO.
	 * @param parametroSelecionado
	 */
	public void medicamentoDomiciliar(ListaPacienteEmAtendimentoVO medDomiciliar) {		
		
		if (medDomiciliar != null){
			procedimentoTerapeuticoFacade.medicamentoDomiciliar(medDomiciliar);
			pesquisar();				
		}
	}
	
	public boolean verificarExisteSessao(ListaPacienteEmAtendimentoVO pacienteEmAtendimentoVO){
		return procedimentoTerapeuticoFacade.verificarExisteSessao(pacienteEmAtendimentoVO.getSeqSessao());
	}
	
	/**
	 * Retorna o tempo em segundos para atualização da coluna manipulação.
	 */
	public void tempoManipulacao() {		
		BigDecimal valor = procedimentoTerapeuticoFacade.tempoManipulacao();		
		tempo = Integer.valueOf(valor.intValue());
	}
	
	public void carregarExibicaoComponentes() {
		exibirColunaMD = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "DOMI");
		exibirColunaIS = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "INTE");
		exibirColunaCP = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "SINA");
		exibirColunaLM = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "LIBM");
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

	public ListaTrabalhoSessaoTerapeuticaPaginatorController getControleFiltro() {
		return controleFiltro;
	}

	public void setControleFiltro(
			ListaTrabalhoSessaoTerapeuticaPaginatorController controleFiltro) {
		this.controleFiltro = controleFiltro;
	}

	public ListaPacienteEmAtendimentoVO getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(
			ListaPacienteEmAtendimentoVO parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	public IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return procedimentoTerapeuticoFacade;
	}

	public void setProcedimentoTerapeuticoFacade(
			IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade) {
		this.procedimentoTerapeuticoFacade = procedimentoTerapeuticoFacade;
	}

	public List<ListaPacienteEmAtendimentoVO> getListaPacientes() {
		return listaPacientes;
	}

	public void setListaPacientes(List<ListaPacienteEmAtendimentoVO> listaPacientes) {
		this.listaPacientes = listaPacientes;
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

	public boolean isExibirColunaMD() {
		return exibirColunaMD;
	}

	public void setExibirColunaMD(boolean exibirColunaMD) {
		this.exibirColunaMD = exibirColunaMD;
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
}