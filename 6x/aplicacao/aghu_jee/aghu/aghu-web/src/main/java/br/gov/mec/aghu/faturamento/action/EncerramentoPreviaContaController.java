package br.gov.mec.aghu.faturamento.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.context.http.HttpConversationContext;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoSSM;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.ContaHospitalarVO;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class EncerramentoPreviaContaController extends ActionController implements ActionPaginator {

	private static final String EXCECAO_CAPTURADA = "Exceção capturada:";

	/**
	 * 
	 */
	private static final long serialVersionUID = -5203443572230339578L;
	
	private static final Log LOG = LogFactory.getLog(EncerramentoPreviaContaController.class);
	
	@Inject @Paginator
	private DynamicDataModel<ContaHospitalarVO> dataModel;

	@Inject
	private ConsultarFatLogErrorPaginatorController consultarFatLogErrorPaginatorController;
	
	private Integer pacProntuario;
	private Long cthNroAih;
	private Integer pacCodigo;
	private Integer cthSeq;

	private AipPacientes paciente;

	private Integer cthSeqSelected;
	private String situacaoContaSelected;
	private Boolean reapresentada;
	private DominioSituacaoConta novaSituacao; 
	
	private String urlLogInconsistencias;
	
	private static final String RELATORIO_LOG_INCONSISTENCIAS="/aghu/pages/faturamento/internacao/relatorios/relatorioLogInconsistenciasInternacaoPdf.xhtml";

	/**
	 * Enumeracao de <em>MAGIC STRINGS</em>
	 * @author fgka
	 *
	 */
	public static enum RetornoAcaoStrEnum {

		CONSULTAR_INCONSISTENCIAS("faturamento-consultarInconsistencias"),
		IMPRIMIR_ESPELHO("faturamento-imprimirEspelho"),
		RELATORIO_INCONSISTENCIAS("faturamento-relatorioInconsistencias"),
		ERRO("erro"),
		PENDENCIAS_ADMINISTRATIVAS("faturamento-pendenciasAdm"),
		ESPELHO("faturamento-espelho"),
		DESDOBRAMENTO("faturamento-desdobramentoContaHospitalar"),
		VISUALIZAR_CONTA_HOSPITALAR("faturamento-consultarContaHospitalar"),
		INFORMAR_SOLICITADO("faturamento-informarSolicitadoContaHospitalar"),
		REIMPRESSAO_LAUDOS_PROCEDIMENTOS("faturamento-reimpressaoLaudosProcedimentos"),
		PROTOCOLOS("faturamento-pesquisarProtocolosAihs"),
		VISUALIZAR_NOTA_CONSUMO("blococirurgico-pesquisaCirurgiaRealizadaNotaConsumo"),
        VISUALIZAR_EXTRATO_PACIENTE("internacao-pesquisarExtratoPaciente"),
        PESQUISAR_CADASTRO_ITENS("faturamento-visualizarItensDoProcedimento");

		private final String str;

		RetornoAcaoStrEnum(final String str) {

			this.str = str;
		}

		@Override
		public String toString() {

			return this.str;
		}
	}

	private List<ContaHospitalarVO> contasHopitalares;

	private ContaHospitalarVO contaSelecionada;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	private Integer pacCodigoFonetica;

	private Boolean reabrirConta = false;

	private ICascaFacade cascaFacade = ServiceLocator.getBean(
			ICascaFacade.class, "aghu-casca");

	@Inject
    private HttpConversationContext conversationContext;	

	@Override
	public Long recuperarCount() {
		return this.faturamentoFacade
		.pesquisarAbertaFechadaEncerradaCount(this.pacProntuario,
				this.cthNroAih, this.pacCodigo, this.cthSeq);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContaHospitalarVO> recuperarListaPaginada(final Integer firstResult,
			final Integer maxResult, final String orderProperty, final boolean asc) {
		
		reabrirConta = false;

		final List<VFatContaHospitalarPac> result = this.faturamentoFacade
		.pesquisarAbertaFechadaEncerrada(firstResult, maxResult,
				VFatContaHospitalarPac.Fields.CTH_DT_INT_ADMINISTRATIVA
				.toString(), true, this.pacProntuario,
				this.cthNroAih, this.pacCodigo, this.cthSeq);

		contasHopitalares = new ArrayList<ContaHospitalarVO>();
		for (final VFatContaHospitalarPac vFatContaHospitalarPac : result) {

			final FatContasHospitalares contaHospitalar = vFatContaHospitalarPac.getContaHospitalar();
			final Integer pCthSeq = vFatContaHospitalarPac.getCthSeq();

			Short pCspCnvCodigo = null;
			Byte pCspSeq = null;
			if(vFatContaHospitalarPac.getConvenioSaudePlano() != null){
				pCspCnvCodigo = vFatContaHospitalarPac.getConvenioSaudePlano().getId().getCnvCodigo();
				pCspSeq = vFatContaHospitalarPac.getConvenioSaudePlano().getId().getSeq();
			}

			Integer pCthPhiSeq = null;
			if(contaHospitalar.getProcedimentoHospitalarInterno() != null){
				pCthPhiSeq = contaHospitalar.getProcedimentoHospitalarInterno().getSeq();
			}

			Integer pCthPhiSeqRealizado = null;
			if(contaHospitalar.getProcedimentoHospitalarInternoRealizado() != null){
				pCthPhiSeqRealizado = contaHospitalar.getProcedimentoHospitalarInternoRealizado().getSeq();
			}

			final String ssmSolicitado = this.faturamentoFacade.buscaSSM(pCthSeq, pCspCnvCodigo, pCspSeq, DominioSituacaoSSM.S);
			final String ssmRealizado =  this.faturamentoFacade.buscaSSM(pCthSeq, pCspCnvCodigo, pCspSeq, DominioSituacaoSSM.R);

			final String financiamentoSolicitado = this.faturamentoFacade.buscaSsmComplexidade(pCthSeq, pCspCnvCodigo, pCspSeq, pCthPhiSeq, pCthPhiSeqRealizado, DominioSituacaoSSM.S);
			final String financiamentoRealizado =  this.faturamentoFacade.buscaSsmComplexidade(pCthSeq, pCspCnvCodigo, pCspSeq, pCthPhiSeq, pCthPhiSeqRealizado, DominioSituacaoSSM.R);

			final String leito = this.faturamentoFacade.buscaLeito(contaHospitalar);
			final String situacaoSMS = this.faturamentoFacade.situacaoSMS(contaHospitalar);

			contasHopitalares.add(new ContaHospitalarVO(vFatContaHospitalarPac, ssmSolicitado, ssmRealizado, financiamentoSolicitado, financiamentoRealizado, leito, situacaoSMS));
		}

        //Merge manual da revision 175752 da versão 5_fr
		if(deveSelecionarUmaConta()){
			cthSeqSelected = contasHopitalares.get(0).getSeq();
			situacaoContaSelected = contasHopitalares.get(0).getSituacaoConta().toString();
		}
		
		//55069
		if(contasHopitalares != null && !contasHopitalares.isEmpty()){
			if(faturamentoFacade.isPacienteTransplantado(paciente)){
				// ATENCÃO. Paciente é transplantado!
				apresentarMsgNegocio(Severity.INFO,"MBC_00537", paciente.getNome());
			}
		}
		return contasHopitalares;
	}
    //Merge manual da revision 175752 da versão 5_fr
    private boolean deveSelecionarUmaConta() {
        return cthSeqSelected == null && contasHopitalares != null && !contasHopitalares.isEmpty();
    }
	
	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {

		if (this.pacProntuario == null && this.cthNroAih == null
				&& this.pacCodigo == null && this.cthSeq == null) {
			this.apresentarMsgNegocio(Severity.ERROR,"FILTRO_OBRIGATORIO_PESQUISA_CONTA");
			return;
		}

		if(this.paciente == null || this.paciente.getNome() == null){
			this.paciente = this.faturamentoFacade
			.pesquisarAbertaFechadaEncerradaPaciente(this.pacProntuario,
					this.cthNroAih, this.pacCodigo, this.cthSeq);
			
			if(paciente != null){
				pacProntuario = paciente.getProntuario();
				pacCodigo = paciente.getCodigo();
			}
		}
		
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		this.pacProntuario = null;
		this.cthNroAih = null;
		this.pacCodigo = null;
		this.cthSeq = null;
		this.paciente = null;
		this.pacCodigoFonetica = null;	
		this.cthSeqSelected = null;
		this.dataModel.limparPesquisa();
	}
	
	@PostConstruct
	public void init() {
		try {
			ajustarTempoSessao();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		begin(conversation);
	}

	private void ajustarTempoSessao() throws ApplicationBusinessException {
		Usuario usuario = cascaFacade.recuperarUsuario(obterLoginUsuarioLogado());

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

		session.setMaxInactiveInterval(usuario.getTempoSessaoMinutos() * 180);
		
		int sessionTime = session.getMaxInactiveInterval()*1000;  //pega tempo de sessão e passa para milisegundos
        if (conversationContext != null) {
            if (conversationContext.getDefaultTimeout() < sessionTime){
                conversationContext.setDefaultTimeout(sessionTime);
            }
        }

	}

	public void inicio(){
		
		boolean achouPaciente = false;
		
		if(this.cthSeqSelected!=null && this.pacCodigoFonetica == null){
			return;
		}
		
		if(this.pacCodigoFonetica != null){
			this.paciente = pacienteFacade.obterPacientePorCodigo(this.pacCodigoFonetica);
			this.pacProntuario = paciente.getProntuario();
			this.pacCodigo =paciente.getCodigo();
			achouPaciente = true;
			
		} else if(pacCodigo != null){
			this.obterPacientePorCodigo();
			achouPaciente = true;
			
		} else if(pacProntuario != null){
			this.obterPacientePorProntuario();
			achouPaciente = true;
		}
		
		if(achouPaciente){
			this.cthNroAih = null;
			this.cthSeq = null;
			this.cthSeqSelected = null;
			this.dataModel.limparPesquisa();
			this.pacCodigoFonetica = null;
		}
	
	}
	
	public String redirecionarPesquisaFonetica(){
		return "paciente-pesquisaPacienteComponente";
	}
	
	public void obterPacientePorCodigo(){
		final Integer filtro = pacCodigo;
		limparPesquisa();
	
		if(filtro != null){
			paciente = pacienteFacade.obterNomePacientePorCodigo(filtro);
			if(paciente != null){
				pacProntuario = paciente.getProntuario();
				pacCodigo =paciente.getCodigo();

			} else {
				this.apresentarMsgNegocio("FAT_00731");
			}
		}
	}

	public void obterPacientePorProntuario(){
		final Integer filtro = pacProntuario;
		limparPesquisa();
		
		if(filtro != null){
			paciente = pacienteFacade.obterPacientePorProntuario(filtro);
			
			if(paciente != null){
				pacProntuario = paciente.getProntuario();
				pacCodigo =paciente.getCodigo();
			} else {
				this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_PACIENTE_PRONTUARIO_NAO_ENCONTRADO");
			}
		}
	}
	
	public boolean exibirBotoes() {
		if (this.pacProntuario != null || this.cthNroAih != null
				|| this.pacCodigo != null || this.cthSeq != null) {
			final Long count = recuperarCount();
			return count != null && count >0;
			
		} else {
			return false;
		}
	}
	
	
	/**
	 * Apenas redireciona para a tela de Pesquisar Inconsistências de Espelho
	 * 
	 * @return
	 */
	public String consultarInconsistencias(){
		if(cthSeqSelected == null){
			apresentarExcecaoNegocio(new ApplicationBusinessException(EncerramentoPreviaContaControllerExceptionCode.CONTA_NAO_SELECIONADA));
			return null;
		} else {
			return RetornoAcaoStrEnum.CONSULTAR_INCONSISTENCIAS.toString();
		}
	}
	

	/**
	 * Valida se a conta pode ser desdobrada e envia para a tela de desdobramento
	 * 
	 */
	public String desdobrarConta(){
		if(cthSeqSelected == null || 
				(!DominioSituacaoConta.A.toString().equalsIgnoreCase(situacaoContaSelected) && !DominioSituacaoConta.F.toString().equalsIgnoreCase(situacaoContaSelected))){
			apresentarExcecaoNegocio(new ApplicationBusinessException(EncerramentoPreviaContaControllerExceptionCode.ERRO_DESDOBRAMENTO_CONTA_HOSPITALAR));
			return null;
		} else {
			return RetornoAcaoStrEnum.DESDOBRAMENTO.toString();
		}
	}
	
	
	public void relatorioInconsistencias(){
		if(cthSeqSelected == null){
			apresentarExcecaoNegocio(new ApplicationBusinessException(EncerramentoPreviaContaControllerExceptionCode.CONTA_NAO_SELECIONADA));
		} else {

			final FatContasHospitalares cth = faturamentoFacade.obterContaHospitalar(cthSeqSelected);
			
			reapresentada = (cth.getContaHospitalarReapresentada() != null);
			
			
			
			
			StringBuilder stringBuilderLogInconsistencias = new StringBuilder(250)
			.append(RELATORIO_LOG_INCONSISTENCIAS)
			.append("?cthSeq=")
			.append(this.getCthSeqSelected())
			.append(";pacProntuario=")
			.append(this.getPaciente().getProntuario())
			.append(";reapresentada=")
			.append(this.getReapresentada())
			.append(";isDirectPrint=")
			.append(false);
			
		
			urlLogInconsistencias = stringBuilderLogInconsistencias.toString();
			
			RequestContext.getCurrentInstance().execute("jsExecutaBotaoLogInconsistencias()");
		}
	}
	
	/**
	 * Apenas redireciona para a tela de Pendencias Administrativas
	 * 
	 * @return
	 */
	public String pendenciasAdm(){
		if(cthSeqSelected == null){
			apresentarExcecaoNegocio(new ApplicationBusinessException(EncerramentoPreviaContaControllerExceptionCode.CONTA_NAO_SELECIONADA));
			return null;
		} else {
			return RetornoAcaoStrEnum.PENDENCIAS_ADMINISTRATIVAS.toString();
		}
	}

	/**
	 * Apenas redireciona para a tela de Visualizar Conta Hospitalar
	 * 
	 * @return
	 */
	public String visualizarContaHospitalar(){
		if(cthSeqSelected == null){
			apresentarExcecaoNegocio(new ApplicationBusinessException(EncerramentoPreviaContaControllerExceptionCode.CONTA_NAO_SELECIONADA));
			return null;
		} else {
			return RetornoAcaoStrEnum.VISUALIZAR_CONTA_HOSPITALAR.toString();
		}
	}

	public String visualizarNotaConsumo(){

		if(cthSeqSelected == null){

			apresentarExcecaoNegocio(new ApplicationBusinessException(EncerramentoPreviaContaControllerExceptionCode.CONTA_NAO_SELECIONADA));
			return null;

		} else {

			return RetornoAcaoStrEnum.VISUALIZAR_NOTA_CONSUMO.toString();
		}
	}

    public String visualizarExtratoPaciente(){
        if(cthSeqSelected == null){
            apresentarExcecaoNegocio(new ApplicationBusinessException(EncerramentoPreviaContaControllerExceptionCode.CONTA_NAO_SELECIONADA));
            return null;
        } else {
            return RetornoAcaoStrEnum.VISUALIZAR_EXTRATO_PACIENTE.toString();
        }
    }
	
	/**
	 * Transformar uma conta fechada em conta sem cobertura
	 * 
	 * @return
	 */
	public void converterContaEmSemCobertura(){
		if(cthSeqSelected == null){
			apresentarExcecaoNegocio(new ApplicationBusinessException(EncerramentoPreviaContaControllerExceptionCode.CONTA_NAO_SELECIONADA));

		} else {
			try {
				
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoIPv4HostRemoto().getHostName();
				} catch (UnknownHostException e) {
					LOG.error(EXCECAO_CAPTURADA, e);
				}
				
				final Date dt = new Date();
				faturamentoFacade.converterContaEmSemCobertura( cthSeqSelected, DominioSituacaoConta.valueOf(situacaoContaSelected), 
																nomeMicrocomputador, dt);
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUC_CONVERTER_CONTA_SEM_COBERTURA");
				pesquisar();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}


	/**
	 * Autoriza faturamento da conta com 1 dia
	 * 
	 * @return
	 */
	public void faturarContaUmDia(){
		if(cthSeqSelected == null){
			apresentarExcecaoNegocio(new ApplicationBusinessException(EncerramentoPreviaContaControllerExceptionCode.CONTA_NAO_SELECIONADA));

		} else {
			try {
				
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoIPv4HostRemoto().getHostName();
				} catch (UnknownHostException e) {
					LOG.error(EXCECAO_CAPTURADA, e);
				}

				final Date dataFimVinculoServidor = new Date();
				faturamentoFacade.faturarContaUmDia( cthSeqSelected, DominioSituacaoConta.valueOf(situacaoContaSelected), 
													 nomeMicrocomputador, dataFimVinculoServidor);
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUC_FATURAMENTO_INTERNACAO");
				pesquisar();

			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	/**
	 * Valida a possibilidade de se reabrir a conta
	 * 
	 */
	public void validarReaberturaContaHospitalar(){
		if(cthSeqSelected == null){
			apresentarExcecaoNegocio(new ApplicationBusinessException(EncerramentoPreviaContaControllerExceptionCode.CONTA_NAO_SELECIONADA));
		} else {
			try {
				situacaoContaSelected = novaSituacao.toString();
				conversationContext.setConcurrentAccessTimeout(conversationContext.getConcurrentAccessTimeout()*10);
				reabrirConta = faturamentoFacade.validarReaberturaContaHospitalar(cthSeqSelected, DominioSituacaoConta.valueOf(situacaoContaSelected));
				
				if(!reabrirConta){
					reabrirContaHospitalar();
				}

			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}


	public void reabrirContaHospitalar(){
		if(cthSeqSelected == null){
			apresentarExcecaoNegocio(new ApplicationBusinessException(EncerramentoPreviaContaControllerExceptionCode.CONTA_NAO_SELECIONADA));
			
		} else {
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoIPv4HostRemoto().getHostName();
			} catch (UnknownHostException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
			}
			
			try {
			
				reabrirConta = false;

				final Date dataFimVinculoServidor = new Date();
				if(faturamentoFacade.reabrirContaHospitalar(cthSeqSelected, nomeMicrocomputador, dataFimVinculoServidor)){
					this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REABERTURA", cthSeqSelected.toString());
					this.dataModel.reiniciarPaginator();
				}
				
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	public void cancelarReaberturaContaHospitalar(){
		reabrirConta = false;
		this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NAO_CONFIRMA_REABERTURA_C_H");
	}

	public enum EncerramentoPreviaContaControllerExceptionCode implements BusinessExceptionCode {
		CONTA_NAO_SELECIONADA, ERRO_DESDOBRAMENTO_CONTA_HOSPITALAR;
	}

	public String espelho() {
		if (cthSeqSelected == null) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(EncerramentoPreviaContaControllerExceptionCode.CONTA_NAO_SELECIONADA));
			return null;
		} else {
			return RetornoAcaoStrEnum.ESPELHO.toString();
		}
	}
	
	public String previaContaHospitalar() {
		return encerramentoPreviaContaHospitalar(true);
	}
	
	public String encerramentoContaHospitalar() {
		return encerramentoPreviaContaHospitalar(false);
	}
	
	private String encerramentoPreviaContaHospitalar(final boolean isPrevia) {
		try {
			if (cthSeqSelected == null) {
				apresentarExcecaoNegocio(new ApplicationBusinessException(EncerramentoPreviaContaControllerExceptionCode.CONTA_NAO_SELECIONADA));
				return null;
			} else {
				
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoIPv4HostRemoto().getHostName();
				} catch (UnknownHostException e) {
					LOG.error(EXCECAO_CAPTURADA, e);
				}
				
				// Seta sessão para um dia
				//faturamentoFacade.setTimeout(60 * 60 * 24);
				final Date dataFimVinculoServidor = new Date();
				faturamentoFacade.removerPorCthModulo(cthSeqSelected, DominioModuloCompetencia.INT);
				
				Boolean retorno = this.faturamentoFacade.rnCthcAtuGeraEsp(cthSeqSelected, isPrevia, nomeMicrocomputador, dataFimVinculoServidor, false);
				
				if (Boolean.TRUE.equals(retorno)) {

					this.apresentarMsgNegocio(Severity.INFO, isPrevia ? "PREVIA_CONCLUIDA" : "ENCERRAMENTO_CONTAS_HOSPITALARES_SUCESSO");
					
					if (!isPrevia) {
						this.dataModel.reiniciarPaginator();
					}
					
					Long countEspelhosAih = this.faturamentoFacade.listarFatEspelhoAihCount(cthSeqSelected);
					
					if (countEspelhosAih != null && countEspelhosAih > 0) {
						Long countErros = this.faturamentoFacade.pesquisarFatLogErrorCount(cthSeqSelected, null, null, null, null,
								null, DominioModuloCompetencia.INT);
						
						if (countErros != null && countErros > 0) {
							carregarConsultaInconsistencias();
							return RetornoAcaoStrEnum.CONSULTAR_INCONSISTENCIAS.toString();
						}
					}
				} else {
					this.apresentarMsgNegocio(Severity.INFO, isPrevia ? "CONTA_NAO_PODE_SER_ENCERRADA_PREVIA_NAO_CONCLUIDA" : "ENCERRAMENTO_CONTAS_HOSPITALARES_ERRO_SIMPLE");
					carregarConsultaInconsistencias();
					return RetornoAcaoStrEnum.CONSULTAR_INCONSISTENCIAS.toString();
				}
			}
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		}
		
		// Volta o tempo de sessão para 15 minutos.
//		try {
//			faturamentoFacade.setTimeout(60 * 15);
//		} catch (ApplicationBusinessException e) {
//			apresentarExcecaoNegocio(e);
//		}
		
		return null;
	}

    public void pesquisaPaciente(ValueChangeEvent event){
        try {
            paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
            if (paciente != null) {
                pacProntuario = paciente.getProntuario();
                pacCodigo =paciente.getCodigo();
            } else {
                this.apresentarMsgNegocio("FAT_00731");
            }
        }catch(BaseException e){
            apresentarExcecaoNegocio(e);
        }
    }
    
    /**
	 * @throws ApplicationBusinessException 
     * @ORADB Procedure AGH.FAT_DESFAZ_REINTERNACAO
	 */
	public void desfazerReintegracao(){
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoIPv4HostRemoto().getHostName();
			faturamentoFacade.desfazerReintegracao(paciente.getProntuario(), nomeMicrocomputador);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	private void carregarConsultaInconsistencias() {
		consultarFatLogErrorPaginatorController.setCthSeqSelected(cthSeqSelected);
		if(paciente != null) {
			consultarFatLogErrorPaginatorController.setPacCodigo(paciente.getCodigo());
			consultarFatLogErrorPaginatorController.setPacNome(paciente.getNome());
			consultarFatLogErrorPaginatorController.setPacProntuario(paciente.getProntuario());
		}
	}
	
	public void selecionarContaHospitalar() {
		if(this.contaSelecionada != null) {
			this.cthSeqSelected = this.contaSelecionada.getSeq();
			this.situacaoContaSelected = this.contaSelecionada.getSituacaoConta().toString();
		}
	}
	
	public String imprimirEspelho(){
		return RetornoAcaoStrEnum.IMPRIMIR_ESPELHO.toString();
	}
	
	public String salvarRelatorio(){
		return RetornoAcaoStrEnum.IMPRIMIR_ESPELHO.toString();
	}
	
	public String informarSolicitado() {
		return RetornoAcaoStrEnum.INFORMAR_SOLICITADO.toString();
	}
	
	public String reimpressaoLaudosProcedimentos() {
		return RetornoAcaoStrEnum.REIMPRESSAO_LAUDOS_PROCEDIMENTOS.toString();
	}
	
	public String protocolos() {
		return RetornoAcaoStrEnum.PROTOCOLOS.toString();
	}

	public String voltar() {
		return "voltar";
	}

	public Boolean desabilitarNotaConsumo(){

		if(this.paciente.getCodigo() == null) {
			return true;			
		}

		return false;
	}

    public Boolean desabilitarExtratoPaciente(){
        if(this.paciente.getProntuario() == null){
            return true;
        }
        return false;
    }

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public Long getCthNroAih() {
		return cthNroAih;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setPacProntuario(final Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public void setCthNroAih(final Long cthNroAih) {
		this.cthNroAih = cthNroAih;
	}

	public void setPacCodigo(final Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public void setCthSeq(final Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Integer getCthSeqSelected() {
		return cthSeqSelected;
	}

	public void setCthSeqSelected(final Integer cthSeqSelected) {
		this.cthSeqSelected = cthSeqSelected;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(final AipPacientes paciente) {
		this.paciente = paciente;
	}

	public String getSituacaoContaSelected() {
		return situacaoContaSelected;
	}

	public void setSituacaoContaSelected(final String situacaoContaSelected) {
		this.situacaoContaSelected = situacaoContaSelected;
	}

	public Boolean getReabrirConta() {
		return reabrirConta;
	}

	public void setReabrirConta(Boolean reabrirConta) {
		this.reabrirConta = reabrirConta;
	}

	public Boolean getReapresentada() {
		return reapresentada;
	}

	public void setReapresentada(Boolean reapresentada) {
		this.reapresentada = reapresentada;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public DynamicDataModel<ContaHospitalarVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ContaHospitalarVO> dataModel) {
		this.dataModel = dataModel;
	}

	public ContaHospitalarVO getContaSelecionada() {
		return contaSelecionada;
	}

	public void setContaSelecionada(ContaHospitalarVO contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}

	public DominioSituacaoConta getNovaSituacao() {
		return novaSituacao;
	}

	public void setNovaSituacao(DominioSituacaoConta novaSituacao) {
		this.novaSituacao = novaSituacao;
	}
	
	public String getUrlLogInconsistencias() {
		return urlLogInconsistencias;
	}

	public void setUrlLogInconsistencias(String urlLogInconsistencias) {
		this.urlLogInconsistencias = urlLogInconsistencias;
	}
}
