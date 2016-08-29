package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.model.MptProtocoloCiclo;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ExtratoSessaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteAguardandoAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteEmAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteAcolhimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteConcluidoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ListaConcluidoPaginatorController extends ActionController{ 

	/**
	 * 
	 */
	private static final long serialVersionUID = -4316543993137498335L;

	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@Inject
	private SecurityController securityController;
	
	private List<PacienteConcluidoVO> listaPacientes;

	private PacienteConcluidoVO pacienteSelecionado;
	
	private Long countPaciente;
	
	private ListaTrabalhoSessaoTerapeuticaPaginatorController controllerFiltro;
	
	private List<String> listaSiglas = new ArrayList<String>();
	private List<ExtratoSessaoVO> listaExtratoSessao = new ArrayList<ExtratoSessaoVO>();
	private static final String TRACO = " - ";
	
	private boolean exibirColunaIS;
	private boolean exibirColunaCP;
	private boolean exibirColunaMD;
	private static final String REGISTRAR_INTERCORRENCIA = "procedimentoterapeutico-registrarIntercorrencia";
	
	@Inject
	private RegistrarIntercorrenciaController registrarIntercorrencia;
	@Inject
	private ListaTrabalhoSessaoTerapeuticaPaginatorController listaTrabalhoSessaoTerapeuticaPaginatorController;
	
	private ListaPacienteEmAtendimentoVO listaPacienteEmAtendimentoVO;
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	public void iniciar() {	
		listaPacientes = new ArrayList<PacienteConcluidoVO>();
		pesquisar();
	}
		
	public void pesquisar() {		
		try {
			listaPacientes = null;
			countPaciente = 0L;
			
			listaSiglas = procedimentoTerapeuticoFacade.obterSiglaCaracteristicaPorTpsSeq(controllerFiltro.getTipoSessaoCombo());
			listaPacientes = this.procedimentoTerapeuticoFacade.obterPacientesAtendimentoConcluido(controllerFiltro.getDataInicial(), controllerFiltro.getHorario(), controllerFiltro.getTipoSessaoCombo(), 
					controllerFiltro.getSalaCombo(), controllerFiltro.getLocalAtendimento(), controllerFiltro.getAcomodacaoCombo(), controllerFiltro.getMpaProtocoloAssistencial());
			pacienteSelecionado = null;
			
			if (!listaPacientes.isEmpty()){
				countPaciente = (long)listaPacientes.size();
			}
			
			procedimentoTerapeuticoFacade.validarListagemConcluido(listaPacientes);
			carregarExibicaoComponentes();
		} catch (ApplicationBusinessException e) {
			 apresentarExcecaoNegocio(e);
		}
	}
	
	public Boolean semaforo(PacienteConcluidoVO item){
		
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
	
	public void limpar() {
		listaPacientes = null;
		countPaciente = null;
	}
	
	public void voltarStatusParaAtendimento() throws ApplicationBusinessException{
		if (pacienteSelecionado != null){
			procedimentoTerapeuticoFacade.voltarParaAtendimento(pacienteSelecionado);
			pesquisar();
		}
	}

	public void voltarStatusParaAgendado() throws ApplicationBusinessException{
		if (pacienteSelecionado != null){
			procedimentoTerapeuticoFacade.voltarStatusAgendado(pacienteSelecionado.getCodigo());
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

	public String obterHintColuna(String colorColuna) throws ParseException {
		if (colorColuna != null) {
			if (colorColuna.equals("#FFFFD9")) {
				return "Primeira Sessão";
			} else if (colorColuna.equals("#93C47D")) {
				return "Reserva";
			} else if (colorColuna.equals("#00FFFF")) {
				return "Paciente portador de Germe Multirresistente.";
			}
		}

		return "";
	}

	public String obterHintLinha(String colorLinha) throws ParseException {
		if (colorLinha != null && colorLinha.equals("#00FFFF")){
			return "Paciente portador de Germe Multirresistente.";
		}
		
		return "";
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
	 * Trunca nome da Grid.
	 * @param item
	 * @param tamanhoMaximo
	 * @return String truncada.
	 */
	public String obterHint(String item, Integer tamanhoMaximo) {
		String retorno = item;
		retorno = verificarTamanhoString(tamanhoMaximo, retorno);
		return retorno;
	}

	
	public String protocoloTruncado(List<MptProtocoloCiclo> itens, Integer tamanhoMaximo) {
		
		String resultado = "";
		StringBuilder protocolosBuilder = new StringBuilder();
		
		resultado = concatenarProtocolo(itens, resultado, protocolosBuilder);
		
		resultado = verificarTamanhoString(tamanhoMaximo, resultado);
		
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
	
	public void marcarMedicamentoDomiciliar(PacienteConcluidoVO PacienteConcluidoVO){
		try {
			procedimentoTerapeuticoFacade.marcarMedicamentoDomiciliar(PacienteConcluidoVO);
			pesquisar();
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}		
	}
	
	public void carregarExibicaoComponentes() {
		exibirColunaMD = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "DOMI");
		exibirColunaIS = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "INTE");
		exibirColunaCP = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "SINA");
	}
	
	public Boolean verificarSituacaoNaoComparecimento(){
		Boolean podeRetornar = false;
		if (securityController.usuarioTemPermissao("registrarAusencia", "executar")){
			podeRetornar = pacienteSelecionado != null && pacienteSelecionado.getSituacaoSessao().equals(DominioSituacaoSessao.SNC);
		}
		
		return podeRetornar;
	}
	
	public String registrarIntercorrencia(PacienteConcluidoVO item){
		registrarIntercorrencia.setCodigoPaciente(item.getCodigoPaciente());
		registrarIntercorrencia.setSeqSessao(item.getCodigo());
		registrarIntercorrencia.setNome(item.getPaciente());
		registrarIntercorrencia.setProntuario(item.getProntuario());
		registrarIntercorrencia.setSelectAba(5);
		registrarIntercorrencia.setPacienteConcluidoVO(item);
		listaTrabalhoSessaoTerapeuticaPaginatorController.setFromBack(true);
		return REGISTRAR_INTERCORRENCIA;
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
	
	public List<PacienteConcluidoVO> getListaPacientes() {
		return listaPacientes;
	}

	public void setListaPacientes(List<PacienteConcluidoVO> listaPacientes) {
		this.listaPacientes = listaPacientes;
	}

	public PacienteConcluidoVO getPacienteSelecionado() {
		return pacienteSelecionado;
	}

	public void setPacienteSelecionado(PacienteConcluidoVO pacienteSelecionado) {
		this.pacienteSelecionado = pacienteSelecionado;
	}

	public Long getCountPaciente() {
		return countPaciente;
	}

	public void setCountPaciente(Long countPaciente) {
		this.countPaciente = countPaciente;
	}

	public ListaTrabalhoSessaoTerapeuticaPaginatorController getControllerFiltro() {
		return controllerFiltro;
	}

	public void setControllerFiltro(ListaTrabalhoSessaoTerapeuticaPaginatorController controllerFiltro) {
		this.controllerFiltro = controllerFiltro;
	}
	
	public Boolean gethabilitaVoltarAtendimento(){
		return pacienteSelecionado != null && pacienteSelecionado.getSituacaoSessao().equals(DominioSituacaoSessao.SAC);
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

	public ListaPacienteEmAtendimentoVO getListaPacienteEmAtendimentoVO() {
		return listaPacienteEmAtendimentoVO;
	}

	public void setListaPacienteEmAtendimentoVO(
			ListaPacienteEmAtendimentoVO listaPacienteEmAtendimentoVO) {
		this.listaPacienteEmAtendimentoVO = listaPacienteEmAtendimentoVO;
	}

	public List<ExtratoSessaoVO> getListaExtratoSessao() {
		return listaExtratoSessao;
	}

	public void setListaExtratoSessao(List<ExtratoSessaoVO> listaExtratoSessao) {
		this.listaExtratoSessao = listaExtratoSessao;
	}
}