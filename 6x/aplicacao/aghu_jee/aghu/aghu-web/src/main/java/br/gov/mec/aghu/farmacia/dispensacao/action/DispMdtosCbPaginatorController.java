package br.gov.mec.aghu.farmacia.dispensacao.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dispensacao.business.IFarmaciaDispensacaoFacade;
import br.gov.mec.aghu.farmacia.vo.TicketMdtoDispensadoVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaPrescricaoMedicamento;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.cups.ImpComputadorImpressora;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import net.sf.jasperreports.engine.JRException;

public class DispMdtosCbPaginatorController extends ActionController{

	private static final String DD_MM_YY_HH_MM = "dd/MM/yy HH:mm";

	public enum DispMdtosCbPaginatorControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_IMPRESSORA_NAO_ENCONTRADA
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3051394561082074501L;

	@EJB
	private IFarmaciaFacade farmaciaFacade;

	@EJB
	private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IAdministracaoFacade administracaoFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IParametroFacade parametroFacade;

	private Integer atdSeqPrescricao;
	private Integer seqPrescricao;
	private Long pmmSeq;
	private MpmPrescricaoMedica prescricaoMedica;
	private AfaPrescricaoMedicamento prescricaoMedicamento;
	private String etiqueta;
	private List<AfaDispensacaoMdtos> listaDispMdtosPrescrDispensar;
	private List<TicketMdtoDispensadoVO> listaMdtoDispensado;

	private String unidadeFuncionalComputador;
	private AghMicrocomputador computador;
	private AghUnidadesFuncionais unidadeFuncionalMicro = null;

	// Variaveis de controle de tela
	private boolean btVoltarOuFechar;
	private Boolean possuiPendencia;
	private String urlBtVoltar;
	private Boolean encerraConversacaoBtVoltar;
	private String mensagemConfirmacaoOperacao;
	private String metodoOperacaoEvento;
	private String hintCheckBox;

	private String nomeComputadorRede;
	private AghMicrocomputador micro;
	private AghMicrocomputador microDispensador;

	private Long seqAfaDispSelecionadaCheckBox;

	private static final String PAGE_ESTORNA_MEDICAMENTO = "estornarMedicamentoDispensadoList";
	
	private Boolean matricial;
	private Boolean dispencacaoComMdto;
	private ImpComputadorImpressora computadorImpressora;
	private Integer qtdVias;
	private Date dataUltimaImpressao;
	private Boolean exibeModal;
	private Boolean erroValidacaoModalTicket = Boolean.FALSE;

	private Boolean ativo;

	public void iniciarPagina() throws ApplicationBusinessException {
		this.ativo = Boolean.FALSE;
		try {
			nomeComputadorRede = getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(DispMdtosCbPaginatorControllerExceptionCode.MENSAGEM_IMPRESSORA_NAO_ENCONTRADA));
		}

		this.prescricaoMedica = this.prescricaoMedicaFacade
				.obterPrescricaoComFatConvenioSaude(atdSeqPrescricao, seqPrescricao);
		if (pmmSeq != null) {
			this.prescricaoMedicamento = farmaciaFacade
					.obterAfaPrescricaoMedicamento(pmmSeq);
		}

		try {
			Object[] microENome = farmaciaDispensacaoFacade
					.obterNomeUnidFuncComputadorDispMdtoCb(
							unidadeFuncionalMicro, nomeComputadorRede);
			this.unidadeFuncionalMicro = (AghUnidadesFuncionais) microENome[0];
			this.unidadeFuncionalComputador = microENome[1] != null ? (String) microENome[1]
					: null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		this.listaDispMdtosPrescrDispensar = farmaciaDispensacaoFacade
				.pesquisarListaDispMdtoDispensarPelaPrescricao(
						atdSeqPrescricao, seqPrescricao, unidadeFuncionalMicro);
		if (!(listaDispMdtosPrescrDispensar.isEmpty())) {
			this.ativo = Boolean.TRUE;
		}
		this.possuiPendencia = false;

		try {
			recuperaAghMicroComputador();
		} catch (ApplicationBusinessException e) {
			// apresentarExcecaoNegocio(e);
			this.possuiPendencia = false;
			// Não é necessário fazer nada neste catch, exceção será lançada
			// depois
		}
		instanciaParametrosModalTicket();
		exibeModal = true;
	}

	public void instanciaParametrosModalTicket() {
		// Realizado pré carregamento para o caso do parâmetro não estar
		// cadastrado
		getUnidadeFormatado();
		if (prescricaoMedica != null) {
			if (prescricaoMedica.getAtendimento().getQuarto() != null) {
				prescricaoMedica.getAtendimento().getQuarto().getDescricao();
			}
			prescricaoMedica.getAtendimento().getPaciente().getProntuario();
			prescricaoMedica.getAtendimento().getPaciente()
					.getUltimoPesoFormatado();
			if (prescricaoMedica.getAtendimento().getInternacao() != null
					&& prescricaoMedica.getAtendimento().getInternacao()
							.getConvenioSaudePlano() != null) {
				prescricaoMedica.getAtendimento().getInternacao()
						.getConvenioSaudePlano().getConvenioSaude();
			}
			if (prescricaoMedica.getAtendimento().getServidor() != null) {
				prescricaoMedica.getAtendimento().getServidor()
						.getPessoaFisica().getNomeUsualOuNome();
			}
		}
		try {
			AghParametros parametro = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_AGHU_TICKET_DISPENSACAO_COM_MDTO);
			dispencacaoComMdto = "S".equals(parametro.getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			dispencacaoComMdto = null;
		}
	}

	private void recuperaAghMicroComputador()
			throws ApplicationBusinessException {
		micro = administracaoFacade
				.obterAghMicroComputadorPorNomeOuIPException(nomeComputadorRede);
		microDispensador = administracaoFacade
				.obterAghMicroComputadorPorNomeOuIP(nomeComputadorRede,
						DominioCaracteristicaMicrocomputador.DISPENSADOR);
	}

	// ##ACOES

	public void dispensarMedicamentoEtiquetaComCb() throws BaseException {
		//this.getStatusMessages().clear();
		//this.getStatusMessages().clearGlobalMessages();
		try {
			final String nroEtiquetaFormatada = farmaciaDispensacaoFacade
					.validarCodigoBarrasEtiqueta(etiqueta);
			farmaciaDispensacaoFacade.dispensarMedicamentoByEtiquetaComCb(
					nroEtiquetaFormatada, this.listaDispMdtosPrescrDispensar,
					microDispensador, nomeComputadorRede);

			//farmaciaFacade.flush();
			farmaciaDispensacaoFacade
					.processaCorSinaleiroPosAtualizacao(listaDispMdtosPrescrDispensar);
			ordernarListaAposDispensacao();
			apresentarMsgNegocio(Severity.INFO, "MESSAGE_DISP_MDTOS_ETQ_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			etiqueta = "";
		}
	}

	private void ordernarListaAposDispensacao() {
		CoreUtil.ordenarLista(listaDispMdtosPrescrDispensar,
				"medicamento.descricaoEditada", false);
		CoreUtil.ordenarLista(listaDispMdtosPrescrDispensar,
				"corSinaleiro.codigo", true);
	}

	public void imprimirTicket() throws ApplicationBusinessException, JRException,
			SystemException, IOException {
		this.possuiPendencia = Boolean.FALSE;

		imprimirRelatorioTicket(null);
	}

	private void imprimirRelatorioTicket(List<TicketMdtoDispensadoVO> listaMdto) 
		throws ApplicationBusinessException, JRException, SystemException, IOException{

		Integer prontuario = getProntuario();
		String local = getLocalizacao();
		String nomePaciente = getNomePaciente();
		String prescricaoInicio = getPrescricaoInicio();
		String prescricaoFim = getPrescricaoFim();
		Boolean prescricaoEletronica = isPrescricaoEletronica();

		if (qtdVias == null) {
			qtdVias = 1;
		}
		
		try {
			RapServidores servidorLogado;
			servidorLogado = registroColaboradorFacade
					.obterServidorAtivoPorUsuarioSemCache(obterLoginUsuarioLogado());
			String nomeServidorLogado = servidorLogado.getPessoaFisica()
					.getNome();

			String tickets = this.farmaciaDispensacaoFacade
					.gerarTicketDispensacaoMdto(prontuario, local,
							nomePaciente, prescricaoInicio, prescricaoFim,
							dispencacaoComMdto, prescricaoEletronica,
							listaMdto, nomeServidorLogado);
			for (int i = 0; i < qtdVias; i++) {
				sistemaImpressao.imprimir(tickets,
						super.getEnderecoIPv4HostRemoto());
			}

			if (dispencacaoComMdto) {
				farmaciaDispensacaoFacade.atualizarRegistroImpressao(
						listaMdtoDispensado, servidorLogado);
			}
			apresentarMsgNegocio(Severity.INFO,
					"MESSAGE_DISP_MDTOS_ETQ_IMPRESSO_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		}
		
	}

	private String getNomePaciente() {
		String nomePaciente = "";
		if (this.prescricaoMedica != null
				&& this.prescricaoMedica.getAtendimento() != null
				&& this.prescricaoMedica.getAtendimento().getPaciente() != null) {
			nomePaciente = this.prescricaoMedica.getAtendimento().getPaciente()
					.getNome();
		} else {
			if (this.prescricaoMedicamento != null
					&& this.prescricaoMedicamento.getAtendimento() != null
					&& this.prescricaoMedicamento.getAtendimento()
							.getPaciente() != null) {
				nomePaciente = this.prescricaoMedicamento.getAtendimento()
						.getPaciente().getNome();
			}
		}
		return nomePaciente;
	}

	private String getPrescricaoInicio() {
		String prescricaoInicio = "";
		if (prescricaoMedica != null) {
			prescricaoInicio = DateUtil.dataToString(
					this.prescricaoMedica.getDthrInicio(),  DD_MM_YY_HH_MM);
		} else {
			if (prescricaoMedicamento != null) {
				prescricaoInicio = DateUtil.dataToString(
						this.prescricaoMedicamento.getDthrInicio(),
						DD_MM_YY_HH_MM);
			}
		}
		return prescricaoInicio;
	}

	private String getPrescricaoFim() {
		String prescricaoInicio = "";
		if (prescricaoMedica != null) {
			prescricaoInicio = DateUtil.dataToString(
					this.prescricaoMedica.getDthrFim(), DD_MM_YY_HH_MM);
		} else {
			if (prescricaoMedicamento != null) {
				prescricaoInicio = DateUtil.dataToString(
						this.prescricaoMedicamento.getDthrFim(),
						DD_MM_YY_HH_MM);
			}
		}
		return prescricaoInicio;
	}

	private Boolean isPrescricaoEletronica() {
		Boolean prescricaoEletronica;
		if (prescricaoMedica != null) {
			prescricaoEletronica = true;
		} else {
			prescricaoEletronica = false;
		}
		return prescricaoEletronica;
	}

	public String getUnidadeFormatado() {
		String unidade = "";
		if (prescricaoMedica != null) {
			unidade = prescricaoMedica.getAtendimento().getUnidadeFuncional()
					.getSeq()
					+ " - "
					+ prescricaoMedica.getAtendimento().getUnidadeFuncional()
							.getAndarAlaDescricao();
		}
		return unidade;
	}

	private String getLocalizacao() {
		String localizacao = "";
		if (this.prescricaoMedica != null
				&& this.prescricaoMedica.getAtendimento() != null
				&& this.prescricaoMedica.getAtendimento().getLeito() != null) {
			localizacao = this.prescricaoMedica.getAtendimento().getLeito()
					.getLeitoID();
		} else {

			if (this.prescricaoMedicamento != null
					&& this.prescricaoMedicamento.getAtendimento() != null
					&& this.prescricaoMedicamento.getAtendimento().getLeito() != null) {
				localizacao = this.prescricaoMedicamento.getAtendimento()
						.getLeito().getLeitoID();
			}
		}
		return localizacao;
	}

	private Integer getProntuario() {
		Integer prontuario = null;
		if (this.prescricaoMedica != null
				&& this.prescricaoMedica.getAtendimento() != null) {
			prontuario = this.prescricaoMedica.getAtendimento().getProntuario();
		} else {
			if (this.prescricaoMedicamento != null
					&& this.prescricaoMedicamento.getAtendimento() != null) {
				prontuario = this.prescricaoMedicamento.getAtendimento()
						.getProntuario();
			}
		}
		return prontuario;
	}

	public void assinaMedicamento(AfaDispensacaoMdtos adm) throws BaseException {
		//this.getStatusMessages().clear();
		//this.getStatusMessages().clearGlobalMessages();
		try {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				//logError("Exceção caputada:", e);
				apresentarExcecaoNegocio(new ApplicationBusinessException(DispMdtosCbPaginatorControllerExceptionCode.MENSAGEM_IMPRESSORA_NAO_ENCONTRADA));
			}
			this.seqAfaDispSelecionadaCheckBox = adm.getSeq();
			farmaciaDispensacaoFacade.assinaDispensarMdtoQtdeTotalSemEtiqueta(adm, nomeMicrocomputador);
			//farmaciaFacade.flush();
			ordernarListaAposDispensacao();
		} catch (ApplicationBusinessException e) {
			this.listaDispMdtosPrescrDispensar = farmaciaDispensacaoFacade
					.pesquisarListaDispMdtoDispensarPelaPrescricao(
							atdSeqPrescricao, seqPrescricao,
							unidadeFuncionalMicro);
			if(e.getCode().toString().equals("CANCELAR_DISPENSACAO_SEM_ETIQUETA_SOMENTE_POR_ESTORNO")){
				openDialog("modalConfirmacaoGoEstornarMedicamentoWG");
			} else {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	public AghMicrocomputador getMicro() {
		return micro;
	}

	public void setMicro(AghMicrocomputador micro) {
		this.micro = micro;
	}

	public AghMicrocomputador getMicroDispensador() {
		return microDispensador;
	}

	public void setMicroDispensador(AghMicrocomputador microDispensador) {
		this.microDispensador = microDispensador;
	}

	public String voltar() {
		return RetornoAcaoStrEnum.VOLTAR.toString();
	}

	public String fechar() {
		return RetornoAcaoStrEnum.FECHAR.toString();
	}

	public String verificaDispensacaoAntesDeEvento(String evento)
			throws ApplicationBusinessException, JRException, SystemException, IOException {
		String retorno = evento;
		this.possuiPendencia = this.farmaciaDispensacaoFacade
				.verificaDispensacaoAntesDeEvento(this.atdSeqPrescricao,
						this.seqPrescricao, unidadeFuncionalMicro);
		if (possuiPendencia) {
			if (DispMdtosCBEventEnum.VERIF_DISP_FECHAR.toString()
					.equals(evento)) {
				this.mensagemConfirmacaoOperacao = this.getBundle().getString("MESSAGE_DISP_MDTOS_CONF_FECHAR"); 
			} else if (DispMdtosCBEventEnum.VERIF_DISP_VOLTAR.toString()
					.equals(evento)) {
				this.mensagemConfirmacaoOperacao = this.getBundle().getString("MESSAGE_DISP_MDTOS_CONF_VOLTAR");
			} else if (DispMdtosCBEventEnum.VERIF_DISP_IMPR_TICKET.toString()
					.equals(evento)) {
				this.mensagemConfirmacaoOperacao = this.getBundle().getString("MESSAGE_DISP_MDTOS_CONF_IMPR_TICKET");
			}
			this.metodoOperacaoEvento = evento;
			return null;
		}
		if (DispMdtosCBEventEnum.VERIF_DISP_IMPR_TICKET.toString().equals(
				evento)) {
			try {
				imprimirTicket();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}

		return retorno;
	}
	
	public String voltarOperacaoEvento(){
		return metodoOperacaoEvento;
	}

	public void carregarListaImpressaoTicket() {
		if (dispencacaoComMdto == null) {
			instanciaParametrosModalTicket();
		}
		if (dispencacaoComMdto != null) {
			qtdVias = 1;

			dataUltimaImpressao = farmaciaDispensacaoFacade
					.pesquisarMaxDataHrTicket(atdSeqPrescricao, seqPrescricao,
							unidadeFuncionalMicro, pmmSeq);
			listaMdtoDispensado = farmaciaDispensacaoFacade
					.pesquisarDispensacaoMdto(atdSeqPrescricao, seqPrescricao,
							unidadeFuncionalMicro, pmmSeq);
		}
		if (listaMdtoDispensado == null || listaMdtoDispensado.isEmpty()) {
			apresentarExcecaoNegocio(new ApplicationBusinessException("MESSAGE_IMPRIMIR_TICKET_NENHUM_ITEM_DISPONIVEL", Severity.ERROR));
			exibeModal = false;
		}else{
			exibeModal = true;
		}
	}

	public void imprimirTicketMdtoDispensados() throws ApplicationBusinessException,
			JRException, SystemException, IOException {
		erroValidacaoModalTicket = Boolean.FALSE;
		if (qtdVias == null) {
			apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Quantidade de Vias");
			//getStatusMessages().addToControlFromResourceBundle("dtdVias", Severity.ERROR, "CAMPO_OBRIGATORIO", "Quantidade de Vias");
			erroValidacaoModalTicket = Boolean.TRUE;
		}else if (qtdVias < 1) {
			apresentarExcecaoNegocio(new ApplicationBusinessException("MESSAGE_IMPRIMIR_TICKET_CAMPO_QTD_VIAS_ZERO", Severity.ERROR));
			erroValidacaoModalTicket = Boolean.TRUE;
		} else {
			processarListaimpressaoTicket(false);
			if (listaMdtoDispensado.size() < 1) {
				apresentarExcecaoNegocio(new ApplicationBusinessException("MESSAGE_IMPRIMIR_TICKET_NENHUM_ITEM_SELECIONADO", Severity.ERROR));
				erroValidacaoModalTicket = Boolean.TRUE;
				carregarListaImpressaoTicket();
			} else {
				imprimirRelatorioTicket(listaMdtoDispensado);
			}
		}
	}

	public void reImprimirTicketMdtoDispensados() throws ApplicationBusinessException,
			JRException, SystemException, IOException {
		processarListaimpressaoTicket(true);
		imprimirRelatorioTicket(listaMdtoDispensado);
	}

	private void processarListaimpressaoTicket(Boolean reimpressao) {
		for (int i = 0; i < listaMdtoDispensado.size(); i++) {
			TicketMdtoDispensadoVO item = listaMdtoDispensado.get(i);
			if (reimpressao) {
				if (item.getCheckboxSelecionado()) {
					listaMdtoDispensado.set(i, null);
				}
			} else {
				if (!item.getCheckboxSelecionado()) {
					listaMdtoDispensado.set(i, null);
				}
			}
		}
		while (listaMdtoDispensado.contains(null)) {
			listaMdtoDispensado.remove(null);
		}
	}

	public static enum RetornoAcaoStrEnum {

		CANCELAR("cancelar"), FECHAR("fechar"), VOLTAR("voltar"), ESTORNAR_MEDICAMENTO(
				"estornarMedicamento");

		private final String str;

		RetornoAcaoStrEnum(String str) {

			this.str = str;
		}

		@Override
		public String toString() {

			return this.str;
		}
	}

	/**
	 * Definem retorno para mensagens de alerta ao usuario para os eventos de
	 * tela dos botoes voltar, fechar e imprimir ticket
	 * 
	 * @author Sedimar
	 * 
	 */
	public enum DispMdtosCBEventEnum implements BusinessExceptionCode {
		VERIF_DISP_VOLTAR		("farmacia-voltarPesquisarPacientesParaDispensacao"), 
		VERIF_DISP_FECHAR		("fechar"), 
		VERIF_DISP_IMPR_TICKET	("imprimirTicket");

		private final String str;

		DispMdtosCBEventEnum(String str) {

			this.str = str;
		}

		@Override
		public String toString() {

			return this.str;
		}
	}

	public String redirecionaEstornoMedicamentosDispensados(){
		return PAGE_ESTORNA_MEDICAMENTO;
	}
	
	// GETTERS E SETTERS

	public Integer getAtdSeqPrescricao() {
		return atdSeqPrescricao;
	}

	public void setAtdSeqPrescricao(Integer atdSeqPrescricao) {
		this.atdSeqPrescricao = atdSeqPrescricao;
	}

	public Integer getSeqPrescricao() {
		return seqPrescricao;
	}

	public void setSeqPrescricao(Integer seqPrescricao) {
		this.seqPrescricao = seqPrescricao;
	}

	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}

	public boolean isBtVoltarOuFechar() {
		return btVoltarOuFechar;
	}

	public void setBtVoltarOuFechar(boolean btVoltarOuFechar) {
		this.btVoltarOuFechar = btVoltarOuFechar;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public String getUrlBtVoltar() {
		return urlBtVoltar;
	}

	public void setUrlBtVoltar(String urlBtVoltar) {
		this.urlBtVoltar = urlBtVoltar;
	}

	public Boolean getEncerraConversacaoBtVoltar() {
		return encerraConversacaoBtVoltar;
	}

	public void setEncerraConversacaoBtVoltar(Boolean encerraConversacaoBtVoltar) {
		this.encerraConversacaoBtVoltar = encerraConversacaoBtVoltar;
	}

	public List<AfaDispensacaoMdtos> getListaDispMdtosPrescrDispensar() {
		return listaDispMdtosPrescrDispensar;
	}

	public void setListaDispMdtosPrescrDispensar(
			List<AfaDispensacaoMdtos> listaDispMdtosPrescrDispensar) {
		this.listaDispMdtosPrescrDispensar = listaDispMdtosPrescrDispensar;
	}

	public String getMensagemConfirmacaoOperacao() {
		return mensagemConfirmacaoOperacao;
	}

	public void setMensagemConfirmacaoOperacao(
			String mensagemConfirmacaoOperacao) {
		this.mensagemConfirmacaoOperacao = mensagemConfirmacaoOperacao;
	}

	public Boolean getPossuiPendencia() {
		return possuiPendencia;
	}

	public void setPossuiPendencia(Boolean possuiPendencia) {
		this.possuiPendencia = possuiPendencia;
	}

	public String getMetodoOperacaoEvento() {
		return metodoOperacaoEvento;
	}

	public void setMetodoOperacaoEvento(String metodoOperacaoEvento) {
		this.metodoOperacaoEvento = metodoOperacaoEvento;
	}

	public String getHintCheckBox() {
		return hintCheckBox;
	}

	public void setHintCheckBox(String hintCheckBox) {
		this.hintCheckBox = hintCheckBox;
	}

	public String getUnidadeFuncionalComputador() {
		return unidadeFuncionalComputador;
	}

	public void setUnidadeFuncionalComputador(String unidadeFuncionalComputador) {
		this.unidadeFuncionalComputador = unidadeFuncionalComputador;
	}

	public AghMicrocomputador getComputador() {
		return computador;
	}

	public void setComputador(AghMicrocomputador computador) {
		this.computador = computador;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalMicro() {
		return unidadeFuncionalMicro;
	}

	public void setUnidadeFuncionalMicro(
			AghUnidadesFuncionais unidadeFuncionalMicro) {
		this.unidadeFuncionalMicro = unidadeFuncionalMicro;
	}

	public Long getSeqAfaDispSelecionadaCheckBox() {
		return seqAfaDispSelecionadaCheckBox;
	}

	public void setSeqAfaDispSelecionadaCheckBox(
			Long seqAfaDispSelecionadaCheckBox) {
		this.seqAfaDispSelecionadaCheckBox = seqAfaDispSelecionadaCheckBox;
	}

	public Boolean getMatricial() {
		return matricial;
	}

	public ImpComputadorImpressora getComputadorImpressora() {
		return computadorImpressora;
	}

	public void setComputadorImpressora(
			ImpComputadorImpressora computadorImpressora) {
		this.computadorImpressora = computadorImpressora;
	}

	public void setDispencacaoComMdto(Boolean dispencacaoComMdto) {
		this.dispencacaoComMdto = dispencacaoComMdto;
	}

	public Boolean getDispencacaoComMdto() {
		return dispencacaoComMdto;
	}

	public void setQtdVias(Integer qtdVias) {
		this.qtdVias = qtdVias;
	}

	public Integer getQtdVias() {
		return qtdVias;
	}

	public void setDataUltimaImpressao(Date dataUltimaImpressao) {
		this.dataUltimaImpressao = dataUltimaImpressao;
	}

	public Date getDataUltimaImpressao() {
		return dataUltimaImpressao;
	}

	public Long getPmmSeq() {
		return pmmSeq;
	}

	public void setPmmSeq(Long pmmSeq) {
		this.pmmSeq = pmmSeq;
	}

	public void setMatricial(Boolean matricial) {
		this.matricial = matricial;
	}

	public void setListaMdtoDispensado(
			List<TicketMdtoDispensadoVO> listaMdtoDispensado) {
		this.listaMdtoDispensado = listaMdtoDispensado;
	}

	public List<TicketMdtoDispensadoVO> getListaMdtoDispensado() {
		return listaMdtoDispensado;
	}

	public void setPrescricaoMedicamento(
			AfaPrescricaoMedicamento prescricaoMedicamento) {
		this.prescricaoMedicamento = prescricaoMedicamento;
	}

	public AfaPrescricaoMedicamento getPrescricaoMedicamento() {
		return prescricaoMedicamento;
	}

	public void setExibeModal(Boolean exibeModal) {
		this.exibeModal = exibeModal;
	}

	public Boolean getExibeModal() {
		return exibeModal;
	}

	public Boolean getErroValidacaoModalTicket() {
		return erroValidacaoModalTicket;
	}

	public void setErroValidacaoModalTicket(Boolean erroValidacaoModalTicket) {
		this.erroValidacaoModalTicket = erroValidacaoModalTicket;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
}
