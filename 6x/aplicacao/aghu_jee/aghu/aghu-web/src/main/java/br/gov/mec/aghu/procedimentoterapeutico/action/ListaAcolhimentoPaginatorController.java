package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;

import br.gov.mec.aghu.internacao.action.ImpressaoPulseiraController;
import br.gov.mec.aghu.model.MptExtratoSessao;
import br.gov.mec.aghu.model.MptJustificativa;
import br.gov.mec.aghu.model.MptProtocoloCiclo;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ExtratoSessaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteAgendadoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteAguardandoAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteEmAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteAcolhimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteConcluidoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ListaAcolhimentoPaginatorController extends ActionController{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4316541993837998335L;

	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	private List<PacienteAcolhimentoVO> listaPacientes;

	private PacienteAcolhimentoVO pacienteSelecionado;
	
	private PacienteAcolhimentoVO pacienteSelecionadoSuspensao;

	private Long countPaciente;
	
	private ListaTrabalhoSessaoTerapeuticaPaginatorController controllerFiltro;
	
	private List<SelectItem> listaJustificativas;
	
	private List<String> listaSiglas = new ArrayList<String>();
	
	private List<ExtratoSessaoVO> listaExtratoSessao = new ArrayList<ExtratoSessaoVO>();
	
	private boolean exibirColunaLQ;
	private boolean exibirColunaMD;
	private boolean exibirColunaSS;
	private boolean exibirColunaIP;
	private boolean exibirColunaIS;
	private boolean exibirColunaCP;
	private boolean habilitarChegou;
	
	@Inject
	private RegistrarIntercorrenciaController registrarIntercorrencia;
	private static final String REGISTRAR_INTERCORRENCIA = "procedimentoterapeutico-registrarIntercorrencia";
	@Inject
	private ListaTrabalhoSessaoTerapeuticaPaginatorController listaTrabalhoSessaoTerapeuticaPaginatorController;
	
	@Inject
	private ImpressaoPulseiraController impressaoPulseiraController;
	private ListaPacienteAguardandoAtendimentoVO listaPacienteAguardandoAtendimentoVO;
	private ListaPacienteAgendadoVO listaPacienteAgendadoVO;
	
	private static final String TRACO = " - ";
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	public void iniciar() {	
		listaPacientes = new ArrayList<PacienteAcolhimentoVO>();
		pesquisar();
	}
		
	public void pesquisar() {		
		try {
			listaPacientes = null;
			countPaciente = 0L;
			listaSiglas = procedimentoTerapeuticoFacade.obterSiglaCaracteristicaPorTpsSeq(controllerFiltro.getTipoSessaoCombo());
			listaPacientes=  this.procedimentoTerapeuticoFacade.obterPacientesParaAcolhimento(controllerFiltro.getDataInicial(), controllerFiltro.getHorario(), controllerFiltro.getTipoSessaoCombo(), 
					controllerFiltro.getSalaCombo(), controllerFiltro.getLocalAtendimento(), controllerFiltro.getAcomodacaoCombo(), controllerFiltro.getMpaProtocoloAssistencial());
			pacienteSelecionado = null;
			
			if (!listaPacientes.isEmpty()){
				countPaciente = (long)listaPacientes.size();
			}
			
			procedimentoTerapeuticoFacade.validarListagemAcolhimento(listaPacientes);
			carregarExibicaoComponentes();
		} catch (ApplicationBusinessException e) {
			 apresentarExcecaoNegocio(e);
		}
	}
	
	public Boolean semaforo(PacienteAcolhimentoVO item){
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
	
	public void carregarExibicaoComponentes() {
		exibirColunaLQ = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "MANI");
		exibirColunaMD = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "DOMI");
		exibirColunaSS = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "SUSP");
		exibirColunaIP = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "IMPP");
		exibirColunaIS = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "INTE");
		exibirColunaCP = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "SINA");
		habilitarChegou = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "LIBM");
	}
	
	public void limpar() {
		listaPacientes = null;
		countPaciente = null;
		listaSiglas = null;
		exibirColunaLQ = false;
		exibirColunaMD = false;
		exibirColunaSS = false;
		exibirColunaIP = false;
		exibirColunaIS = false;
		exibirColunaCP = false;
		habilitarChegou = false;
	}
	
	public void imprimirPulseira(PacienteAcolhimentoVO pacienteImprimir) throws ApplicationBusinessException{
		impressaoPulseiraController.setAipPacCodigo(pacienteImprimir.getCodigoPaciente());
		impressaoPulseiraController.setAtdSeq(pacienteImprimir.getCodigoAtendimento());
		impressaoPulseiraController.imprimePulseira();
		
		if (impressaoPulseiraController.getOperacaoRealizada()){
			procedimentoTerapeuticoFacade.registrarImpressaoPulseira(pacienteImprimir);
		}
	}
	
	public void voltarStatusParaAgendado() throws ApplicationBusinessException{
		if (pacienteSelecionado != null){
			procedimentoTerapeuticoFacade.voltarParaAgendado(pacienteSelecionado);
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
		if (retorno.length() > tamanhoMaximo) {
			retorno = StringUtils.abbreviate(retorno, tamanhoMaximo);
		}
		return retorno;
	}

	
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
	
	public void concluirAcolhimento(PacienteAcolhimentoVO pacienteAcolhimentoVO){
		try {
			procedimentoTerapeuticoFacade.concluirAcolhimento(pacienteAcolhimentoVO);		
			pesquisar();
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}		
	}
	
	public void marcarMedicamentoDomiciliar(PacienteAcolhimentoVO pacienteAcolhimentoVO){
		try {
			procedimentoTerapeuticoFacade.marcarMedicamentoDomiciliar(pacienteAcolhimentoVO);		
			pesquisar();
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}		
	}
	
	/**
	 * #41703
	 * Método que envia aviso a Farmácia.
	 * @param pacienteEmAtendimentoVO
	 */
	public void liberarQuimioterapia(PacienteAcolhimentoVO pacienteAcolhimentoVO){
		try {
		    procedimentoTerapeuticoFacade.liberarQuimioterapia(pacienteAcolhimentoVO.getCodigo(), 
		    		pacienteAcolhimentoVO.getCodigoAtendimento(), controllerFiltro.getTipoSessaoCombo());
		    pesquisar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public boolean verificarExisteSessao(PacienteAcolhimentoVO pacienteAcolhimentoVO){
		return procedimentoTerapeuticoFacade.verificarExisteSessao(pacienteAcolhimentoVO.getCodigo());
	}
	
	public void suspenderSessao(PacienteAcolhimentoVO pacienteAcolhimentoVO){
		pacienteSelecionadoSuspensao = pacienteAcolhimentoVO;
		pacienteSelecionadoSuspensao.setMptExtratoSessao(new MptExtratoSessao());
		pacienteSelecionadoSuspensao.getMptExtratoSessao().setMotivo(new MptJustificativa());
		
		popularJustificativa();
		
		RequestContext.getCurrentInstance().execute("PF('suspenderSessaoModalWG').show()");
	}
	
	private void popularJustificativa() {
		List<MptJustificativa> lista = procedimentoTerapeuticoFacade.obterJustificativaParaSuspensao(pacienteSelecionadoSuspensao.getTipoSessaoSeq());
		listaJustificativas = new ArrayList<SelectItem>();
		if (!lista.isEmpty()){
			for (MptJustificativa mptJustificativa : lista) {
				listaJustificativas.add(new SelectItem(mptJustificativa.getSeq(), mptJustificativa.getDescricao()));
			}
		}
	}
	
	public void confirmarSuspensao() throws ApplicationBusinessException{
		String textoJustificativa = StringUtils.trim(pacienteSelecionadoSuspensao.getMptExtratoSessao().getJustificativa());

		if (!StringUtils.isEmpty(textoJustificativa)) {
			try {
				pacienteSelecionadoSuspensao.getMptExtratoSessao().setJustificativa(textoJustificativa);
				procedimentoTerapeuticoFacade.confirmarSuspensaoSessao(pacienteSelecionadoSuspensao);
			} catch (BaseException e) {
				this.apresentarExcecaoNegocio(e);
			}
			fecharJanelaSuspensao();
			pesquisar();
		} else {
			apresentarMsgNegocio(Severity.ERROR, "MSG_JUSTIFICATIVA_OBRIGATORIA");
		}
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
	
	public void visualizarExtratoSessaoModal(Integer pacCodigo) {		
		listaExtratoSessao = null;	
		listaExtratoSessao = this.procedimentoTerapeuticoFacade.pesquisarExtratoSessao(pacCodigo);
	}
	
    public void closeJanelaConfirmacao(CloseEvent event) {
    	fecharJanelaSuspensao();
    }
	
	public void fecharJanelaSuspensao(){
		pacienteSelecionadoSuspensao = null;
		RequestContext.getCurrentInstance().execute("PF('suspenderSessaoModalWG').hide()");
	}
	
	public String registrarIntercorrencia(PacienteAcolhimentoVO item){
		registrarIntercorrencia.setCodigoPaciente(item.getCodigoPaciente());
		registrarIntercorrencia.setSeqSessao(item.getCodigo());
		registrarIntercorrencia.setNome(item.getPaciente());
		registrarIntercorrencia.setProntuario(item.getProntuario());
		registrarIntercorrencia.setSelectAba(2);
		registrarIntercorrencia.setPacienteAcolhimentoVO(item);
		listaTrabalhoSessaoTerapeuticaPaginatorController.setFromBack(true);
		return REGISTRAR_INTERCORRENCIA;
	}
	
	public List<PacienteAcolhimentoVO> getListaPacientes() {
		return listaPacientes;
	}

	public void setListaPacientes(List<PacienteAcolhimentoVO> listaPacientes) {
		this.listaPacientes = listaPacientes;
	}

	public PacienteAcolhimentoVO getPacienteSelecionado() {
		return pacienteSelecionado;
	}

	public void setPacienteSelecionado(PacienteAcolhimentoVO pacienteSelecionado) {
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

	public PacienteAcolhimentoVO getPacienteSelecionadoSuspensao() {
		return pacienteSelecionadoSuspensao;
	}

	public void setPacienteSelecionadoSuspensao(PacienteAcolhimentoVO pacienteSelecionadoSuspensao) {
		this.pacienteSelecionadoSuspensao = pacienteSelecionadoSuspensao;
	}

	public List<SelectItem> getListaJustificativas() {
		return listaJustificativas;
	}

	public void setListaJustificativas(List<SelectItem> listaJustificativas) {
		this.listaJustificativas = listaJustificativas;
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

	public boolean isExibirColunaMD() {
		return exibirColunaMD;
	}

	public void setExibirColunaMD(boolean exibirColunaMD) {
		this.exibirColunaMD = exibirColunaMD;
	}

	public boolean isExibirColunaSS() {
		return exibirColunaSS;
	}

	public void setExibirColunaSS(boolean exibirColunaSS) {
		this.exibirColunaSS = exibirColunaSS;
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

	public boolean isHabilitarChegou() {
		return habilitarChegou;
	}

	public void setHabilitarChegou(boolean habilitarChegou) {
		this.habilitarChegou = habilitarChegou;
	}
	
	public ListaPacienteAguardandoAtendimentoVO getListaPacienteAguardandoAtendimentoVO() {
		return listaPacienteAguardandoAtendimentoVO;
	}

	public void setListaPacienteAguardandoAtendimentoVO(
			ListaPacienteAguardandoAtendimentoVO listaPacienteAguardandoAtendimentoVO) {
		this.listaPacienteAguardandoAtendimentoVO = listaPacienteAguardandoAtendimentoVO;
	}

	public ListaPacienteAgendadoVO getListaPacienteAgendadoVO() {
		return listaPacienteAgendadoVO;
	}

	public void setListaPacienteAgendadoVO(
			ListaPacienteAgendadoVO listaPacienteAgendadoVO) {
		this.listaPacienteAgendadoVO = listaPacienteAgendadoVO;
	}

	public List<ExtratoSessaoVO> getListaExtratoSessao() {
		return listaExtratoSessao;
	}

	public void setListaExtratoSessao(List<ExtratoSessaoVO> listaExtratoSessao) {
		this.listaExtratoSessao = listaExtratoSessao;
	}
}