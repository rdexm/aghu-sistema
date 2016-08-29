package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoLogImpressoes;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.HistObstetricaVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.InternacaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class HistoriaObstetricaPorGestacaoPOLController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<HistObstetricaVO> dataModel;

	private static final long serialVersionUID = -8764119058026431909L;

	private static final String RELATORIO_ATOS_ANESTESICOS_PDF = "pol-relatorioAtosAnestesicosPdf";

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IEmergenciaFacade emergenciaFacade;
	
	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;
	
	@Inject
	private RelatorioAtendEmergObstetricaPOLController relatorioAtendEmergObstetricaPOLController;
	
	@Inject
	private RelatorioAtendimentoRNController relatorioAtendimentoRNController;	

	@Inject
	private RelatorioAdmissaoObstetricaController relatorioAdmissaoObstetricaController;	

	@Inject
	private SecurityController securityController;	
	
	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;

	

	private Short seqp;	
	private String origem;
	private AipPacientes paciente;
	private List<McoGestacoes> gestacoes;	
	private McoGestacoes gestacao;
	private List<HistObstetricaVO> histObstetricaVO;
	private HistObstetricaVO registroSelecionado;	
	private Boolean botaoConsObs;
	private Boolean botaoAdmObs;
	private Boolean botaoAssParto;
	private Boolean botaoAtoAnestesico;
	private Boolean botaoRnSiParto;

	//private Boolean botaoExameRnPosParto;
	
	private Boolean exibirModalRecemNascido;	
	private MbcFichaAnestesias fichaAnestesia;	
	private AghVersaoDocumento aghVersaoDocumento;
	
	//15839
	private List<McoRecemNascidos> listaRecensNascidos = new ArrayList<McoRecemNascidos>();
	private McoRecemNascidos recemNascidoSelecionado;
	
	private String indImpPrevia = "D";
	
	// 15836
	private Integer serMatricula;
	private Short serVinCodigo;
	private Date dthrMovimento;
	private Short gsoSeqp;
	
	private Boolean acessoAdminPol ;
	
	private final String EMITIR_RELATORIO_ATENDIMENTO_EMERGENCIA_OBSTETRICA = "relatorioAtendEmergObstetricaPdf";
	private final String VISUALIZAR_DOCUMENTO_ASSINADO_RELATORIO_EMERGENCIA_OBSTETRICA = "visualizarDocumentoAssinadoPOL";
	private final String VISUALIZAR_RELATORIO_SUMARIO_ASSISTENCIA_PARTO = "relatorioSumarioAssistenciaPartoPdf";
	private final String VISUALIZAR_RELATORIO_EXAME_FISICO_RN_HIST_OBSTETRICA = "relatorioExameFisicoRNPdf";
	private final String VOLTAR_HISTORIA_OBSTETRICA_GESTACAO = "historiaObstetricaPorGestacaoPacienteListPOL";
	private final String VISUALIZAR_RELATORIO_ADMISSAO_OBSTETRICA = "relatorioAdmissaoObstetricaPdf";
	private final String VISUALIZAR_RELATORIO_RECEM_NASCIDO = "relatorioAtendimentoRecemNascidoPdf";
	
	
	@Inject
	private ConsultarInternacoesPOLController consultarInternacoesPOLController;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		
		acessoAdminPol = securityController.usuarioTemPermissao("acessoAdminPOL", "acessar");
		inicio();
	}
	
	
	
	public void inicio() {
		paciente = this.pacienteFacade.pesquisarPacientePorProntuario(itemPOL.getProntuario());
		this.setSeqp((short) itemPOL.getParametros().get(NodoPOLVO.GSO_SEQP));
		
		botaoConsObs = false;
		botaoAdmObs = false;
		botaoAssParto = false;
		botaoAtoAnestesico = false;
		botaoRnSiParto = false;
	//	botaoExameRnPosParto = false;
		exibirModalRecemNascido = false;

		dataModel.reiniciarPaginator();
		if(registroSelecionado == null){
			registroSelecionado = new HistObstetricaVO(null, null, null, null, null, null, null);
		}
	}
	
	public Boolean getUsuarioAdministrativo(){
		if(acessoAdminPol){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}

	@Override
	public Long recuperarCount() {
		return emergenciaFacade.pesquisarAnamnesesEfsPorGestacoesPaginadaCount(paciente.getCodigo(), seqp);
	}

	@Override
	public List<HistObstetricaVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return histObstetricaVO = emergenciaFacade.pesquisarAnamnesesEfsPorGestacoesPaginada(paciente.getCodigo(),
				seqp, firstResult, maxResult, orderProperty, asc);
	}
	
	public McoGestacoes obterGestacao(McoGestacoes mcoGestacoes){
		if (mcoGestacoes != null) {
		   return this.emergenciaFacade.obterMcoGestacoes(mcoGestacoes.getId());
		}
		return null;
	}
	
	public void selecionaRegistro() {
		
		registroSelecionado.setAssinalaRadio(Boolean.TRUE);
		
		botaoConsObs = getProntuarioOnlineFacade().habilitarBotaoConsObs(
				registroSelecionado);

		botaoAdmObs = getProntuarioOnlineFacade().habilitarBotaoAdmObs(
				registroSelecionado.getOrigemAtendimento(),
				registroSelecionado.getGsoPacCodigo(),
				registroSelecionado.getGsoSeqp(),
				registroSelecionado.getConNumero());

		botaoAssParto = getProntuarioOnlineFacade().habilitarBotaoPartoComRestricaoDeOrigem(
				registroSelecionado.getOrigemAtendimento(),
				registroSelecionado.getGsoPacCodigo(),
				registroSelecionado.getGsoSeqp(), DominioOrigemAtendimento.A, DominioOrigemAtendimento.U);

		botaoAtoAnestesico = getProntuarioOnlineFacade()
				.habilitarBotaoAtoAnestesico(registroSelecionado);

		botaoRnSiParto = getProntuarioOnlineFacade().habilitarBotaoNascimentoComRestricaoDeOrigem(
				registroSelecionado.getOrigemAtendimento(),
				registroSelecionado.getGsoPacCodigo(),
				registroSelecionado.getGsoSeqp(), DominioOrigemAtendimento.A, DominioOrigemAtendimento.U);
		
		/*botaoExameRnPosParto = getProntuarioOnlineFacade()
				.habilitarBotaoExameFisico(
						registroSelecionado.getOrigemAtendimento(),
						registroSelecionado.getGsoPacCodigo(),
						registroSelecionado.getGsoSeqp());*/
		
		if (botaoRnSiParto) {
			listaRecensNascidos = getProntuarioOnlineFacade().pesquisarMcoRecemNascidoPorGestacaoOrdenado(registroSelecionado.getGsoPacCodigo(), registroSelecionado.getGsoSeqp());
			recemNascidoSelecionado = null;
			
			if (listaRecensNascidos != null) {
				if (listaRecensNascidos.size() == 1) {	
					recemNascidoSelecionado = listaRecensNascidos.get(0);
				} else if (listaRecensNascidos.size() > 1) {
					exibirModalRecemNascido = true;
				}
			}
		}
	}

	public void exibirMsgFuncionalidadeNaoImplementada() {

		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_FUNCIONALIDADE_NAO_IMPLEMENTADA");
	}

	// Botoes

	public String emitirRelatorioAtendimentosEmergenciaObstetrica() {
		String retorno = null;
		retorno = obterPrimeiroDocumentoAssinadoPorAtendimento(DominioTipoDocumento.CEO);
		if (StringUtils.isEmpty(retorno)) {
			retorno = EMITIR_RELATORIO_ATENDIMENTO_EMERGENCIA_OBSTETRICA;
			obterLogImpressoesEventoMcorConsultaObs();
			relatorioAtendEmergObstetricaPOLController.setConNumero(getRegistroSelecionado().getConNumero());
			relatorioAtendEmergObstetricaPOLController.setGsoPacCodigo(getRegistroSelecionado().getGsoPacCodigo());
			relatorioAtendEmergObstetricaPOLController.setGsoSeqp(getRegistroSelecionado().getGsoSeqp());			
			relatorioAtendEmergObstetricaPOLController.gerarDados();
		}
		//setOrigem(EnumTargetHistoriaObstetricaPorGestacaoVoltar.HISTORIA_OBSTETRICA_POR_GESTACAO_POL.toString());
		return retorno;
	}
	
	private String obterPrimeiroDocumentoAssinadoPorAtendimento(DominioTipoDocumento tipoDocumento) {
		AghVersaoDocumento documento = certificacaoDigitalFacade.obterPrimeiroDocumentoAssinadoPorAtendimento(getRegistroSelecionado().getSeqAtendimento(), tipoDocumento);
		if (documento != null) {
			setAghVersaoDocumento(documento);
			return VISUALIZAR_DOCUMENTO_ASSINADO_RELATORIO_EMERGENCIA_OBSTETRICA;
		} else {
			return null;
		}
	}
	
	private void obterLogImpressoesEventoMcorConsultaObs() {
		List<McoLogImpressoes> result = emergenciaFacade.pesquisarLogImpressoesEventoMcorConsultaObs(getRegistroSelecionado().getGsoPacCodigo(),getRegistroSelecionado().getGsoSeqp(),getRegistroSelecionado().getConNumero());		
		if (result != null && !result.isEmpty()) {
			McoLogImpressoes mli = result.get(0);
			relatorioAtendEmergObstetricaPOLController.setCriadoEm(mli.getCriadoEm());
			if (mli.getServidor() != null) {
				relatorioAtendEmergObstetricaPOLController.setMatricula(mli.getServidor().getId().getMatricula());
				relatorioAtendEmergObstetricaPOLController.setVinculo(mli.getServidor().getId().getVinCodigo());
			}
		}
	}

	public String buscarAssParto() {
		
		AghVersaoDocumento documento = certificacaoDigitalFacade
				.obterPrimeiroDocumentoAssinadoPorAtendimento(
						getRegistroSelecionado().getSeqAtendimento(),
						DominioTipoDocumento.ARN);
		
		if (documento != null) {
			setAghVersaoDocumento(documento);
			return VISUALIZAR_DOCUMENTO_ASSINADO_RELATORIO_EMERGENCIA_OBSTETRICA;
		}else{
			consultarInternacoesPOLController.setInternacao(new InternacaoVO(
					getRegistroSelecionado().getGsoPacCodigo(),
					getRegistroSelecionado().getConNumero(),
					getRegistroSelecionado().getGsoSeqp()));
			consultarInternacoesPOLController.setOrigem(this.VOLTAR_HISTORIA_OBSTETRICA_GESTACAO);
			
			return VISUALIZAR_RELATORIO_SUMARIO_ASSISTENCIA_PARTO;
		}
	}

	public String buscarExameRnPosParto() {
		AghVersaoDocumento documento = certificacaoDigitalFacade.obterPrimeiroDocumentoAssinadoPorAtendimento(registroSelecionado.getSeqAtendimento(), DominioTipoDocumento.EF);
		if (documento != null) {
			setAghVersaoDocumento(documento);
			return VISUALIZAR_DOCUMENTO_ASSINADO_RELATORIO_EMERGENCIA_OBSTETRICA;
		}

		listaRecensNascidos = prontuarioOnlineFacade.listarSeqpRecemNascido(registroSelecionado.getGsoPacCodigo(), registroSelecionado.getConNumero());
		if(listaRecensNascidos != null && listaRecensNascidos.size() == 1){
			recemNascidoSelecionado = listaRecensNascidos.get(0);
			return VISUALIZAR_RELATORIO_EXAME_FISICO_RN_HIST_OBSTETRICA;
		}else{
			limparParamRecemNascido();
			openDialog("modalExameFisicoRecemNascidoWG");
			return null;
		}
	}
	
	public String visualizarDiretoRelatorioExameFisicoRecemNascido() {
		return VISUALIZAR_RELATORIO_EXAME_FISICO_RN_HIST_OBSTETRICA;
	}
	
	/**
	 * Verifica se o botao exame fisicoRN deverá ser habilitado.
	 * @return
	 */
	public Boolean habilitarExameFisicoRecemNascido() {
		return prontuarioOnlineFacade.habilitarBotaoExameFisico(
				registroSelecionado.getOrigemAtendimento(),
				registroSelecionado.getGsoPacCodigo(),
				registroSelecionado.getGsoSeqp(), DominioOrigemAtendimento.A, DominioOrigemAtendimento.U);
	}
	
	public String verificarSeDocumentoAtoAnestesicoAssinado() {

			/*Boolean docAssinado = getProntuarioOnlineFacade().verificarSeDocumentoAtoAnestesicoAssinado(registroSelecionado.getSeq());
			if(docAssinado){
				
				seqVersaoDoc = getProntuarioOnlineFacade().chamarDocCertifFicha(registroSelecionado.getSeq());
				if (seqVersaoDoc != null){
					return "documentoAssinado";
				}
			}else{*/
			fichaAnestesia = getBlocoCirurgicoFacade().obterMbcFichaAnestesiaByMcoGestacao(registroSelecionado.getMcoGestacoes(), registroSelecionado.getSeqAtendimento(), DominioIndPendenteAmbulatorio.V);
			if(fichaAnestesia != null){
				return RELATORIO_ATOS_ANESTESICOS_PDF;
			}else{
				this.apresentarMsgNegocio(Severity.INFO, "MBC_FICHA_ANESTESIA_NAO_LOCALIZADO");
				return null;
			}
	}
	
	public String abrirRelatorioNascimento() throws ApplicationBusinessException {
		String retorno = obterPrimeiroDocumentoAssinadoPorAtendimento(DominioTipoDocumento.ARN);
		if (retorno == null) {
			relatorioAtendimentoRNController.setPacCodigo(getRegistroSelecionado().getGsoPacCodigo());
			relatorioAtendimentoRNController.setConNumero(getRegistroSelecionado().getConNumero());
			relatorioAtendimentoRNController.setGsoSeqp(getRegistroSelecionado().getGsoSeqp());
			relatorioAtendimentoRNController.setAtdSeq(getRegistroSelecionado().getSeqAtendimento());
			relatorioAtendimentoRNController.setRnaSeqp(recemNascidoSelecionado.getId().getSeqp());
			relatorioAtendimentoRNController.gerarDados();
			relatorioAtendimentoRNController.setOrigem(VOLTAR_HISTORIA_OBSTETRICA_GESTACAO);
			retorno = VISUALIZAR_RELATORIO_RECEM_NASCIDO;			
		}
		return retorno;
	}	

/**
	 * Estoria #15836, botao admissao
	 * @return
	 */
	public String abrirRelatorioAdmissaoObstetrica() {
		
		String retorno = obterPrimeiroDocumentoAssinadoPorAtendimento(DominioTipoDocumento.ACO);
		
		if(retorno == null){
			
			retorno = VISUALIZAR_RELATORIO_ADMISSAO_OBSTETRICA;
			
			// Define os parametros esperados pelo relatorio
			// consulta PARAM 1
			McoNascimentos nascimento = null;
			List<McoNascimentos> nascimentos = emergenciaFacade.listarNascimentos(getRegistroSelecionado().getGsoPacCodigo(), 
																						getRegistroSelecionado().getGsoSeqp());
			if(nascimentos != null && !nascimentos.isEmpty()) {
				nascimento = nascimentos.get(0);
			}
			if(nascimento != null) {
				getRegistroSelecionado().setGsoSeqp(nascimento.getId().getGsoSeqp());
			}
			// consulta PARAM 2
			List<McoLogImpressoes> impressoes = emergenciaFacade.pesquisarLogImpressoesEventoMcorAdmissaoObs(
																									getRegistroSelecionado().getGsoPacCodigo(), 
																									getRegistroSelecionado().getGsoSeqp(), null);
			if(impressoes != null && !impressoes.isEmpty()) {
				McoLogImpressoes logImpressao = impressoes.get(0);
				if(logImpressao != null) {
					setDthrMovimento(logImpressao.getCriadoEm());
					setSerMatricula(logImpressao.getServidor().getId().getMatricula());
					setSerVinCodigo(logImpressao.getServidor().getId().getVinCodigo());
				}
			}
		}
		setOrigem(VOLTAR_HISTORIA_OBSTETRICA_GESTACAO);
		
		relatorioAdmissaoObstetricaController.setPacCodigo(getRegistroSelecionado().getGsoPacCodigo());
		relatorioAdmissaoObstetricaController.setConNumero(getRegistroSelecionado().getConNumero());
		relatorioAdmissaoObstetricaController.setGsoSeqp(getRegistroSelecionado().getGsoSeqp());
		relatorioAdmissaoObstetricaController.setMatricula(getSerMatricula());
		relatorioAdmissaoObstetricaController.setVinculo(getSerVinCodigo());
		relatorioAdmissaoObstetricaController.setDthrMovimento(getDthrMovimento());
		relatorioAdmissaoObstetricaController.setOrigem(VOLTAR_HISTORIA_OBSTETRICA_GESTACAO);
		
		
		return retorno;	
	}

	public void limparParamRecemNascido() {
		recemNascidoSelecionado = null;
	}	
	
	public HistObstetricaVO getRegistroSelecionado() {
		return registroSelecionado;
	}

	public void setRegistroSelecionado(HistObstetricaVO registroSelecionado) {
		this.registroSelecionado = registroSelecionado;
	}

	public Boolean getBotaoAtoAnestesico() {
		return botaoAtoAnestesico;
	}

	public void setBotaoAtoAnestesico(Boolean botaoAtoAnestesico) {
		this.botaoAtoAnestesico = botaoAtoAnestesico;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}


	public McoGestacoes getGestacao() {
		return gestacao;
	}

	public void setGestacao(McoGestacoes gestacao) {
		this.gestacao = gestacao;
	}

	public Boolean getBotaoConsObs() {
		return botaoConsObs;
	}

	public void setBotaoConsObs(Boolean botaoConsObs) {
		this.botaoConsObs = botaoConsObs;
	}

	public Boolean getBotaoAdmObs() {
		return botaoAdmObs;
	}

	public void setBotaoAdmObs(Boolean botaoAdmObs) {
		this.botaoAdmObs = botaoAdmObs;
	}

	public Boolean getBotaoAssParto() {
		return botaoAssParto;
	}

	public void setBotaoAssParto(Boolean botaoAssParto) {
		this.botaoAssParto = botaoAssParto;
	}

	public Boolean getBotaoRnSiParto() {
		return botaoRnSiParto;
	}

	public void setBotaoRnSiParto(Boolean botaoRnSiParto) {
		this.botaoRnSiParto = botaoRnSiParto;
	}

	/*public Boolean getBotaoExameRnPosParto() {
		return botaoExameRnPosParto;
	}

	public void setBotaoExameRnPosParto(Boolean botaoExameRnPosParto) {
		this.botaoExameRnPosParto = botaoExameRnPosParto;
	}*/

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public List<McoGestacoes> getGestacoes() {
		return gestacoes;
	}

	public void setGestacoes(List<McoGestacoes> gestacoes) {
		this.gestacoes = gestacoes;
	}

	public  List<HistObstetricaVO>  getHistObstetricaVO() {
		return histObstetricaVO;
	}

	public void setHistObstetricaVO( List<HistObstetricaVO>  histObstetricaVO) {
		this.histObstetricaVO = histObstetricaVO;
	}
	
	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}

	public IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}

	public void setProntuarioOnlineFacade(
			IProntuarioOnlineFacade prontuarioOnlineFacade) {
		this.prontuarioOnlineFacade = prontuarioOnlineFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public MbcFichaAnestesias getFichaAnestesia() {
		return fichaAnestesia;
	}

	public void setFichaAnestesia(MbcFichaAnestesias fichaAnestesia) {
		this.fichaAnestesia = fichaAnestesia;
	}

	public AghVersaoDocumento getAghVersaoDocumento() {
		return aghVersaoDocumento;
	}

	public void setAghVersaoDocumento(AghVersaoDocumento aghVersaoDocumento) {
		this.aghVersaoDocumento = aghVersaoDocumento;
	}

	public List<McoRecemNascidos> getListaRecensNascidos() {
		return listaRecensNascidos;
	}

	public String getIndImpPrevia() {
		return indImpPrevia;
	}

	public void setIndImpPrevia(String indImpPrevia) {
		this.indImpPrevia = indImpPrevia;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public Date getDthrMovimento() {
		return dthrMovimento;
	}

	public void setDthrMovimento(Date dthrMovimento) {
		this.dthrMovimento = dthrMovimento;
	}

	public Short getGsoSeqp() {
		return gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	public Boolean getExibirModalRecemNascido() {
		return exibirModalRecemNascido;
	}

	public void setExibirModalRecemNascido(Boolean exibirModalRecemNascido) {
		this.exibirModalRecemNascido = exibirModalRecemNascido;
	}
	
	public Boolean verficarMarcacaoRadioButton(HistObstetricaVO registro){
		if(registroSelecionado != null && registro.getConNumero().equals(registroSelecionado.getConNumero())){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
 

	public DynamicDataModel<HistObstetricaVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<HistObstetricaVO> dataModel) {
		this.dataModel = dataModel;
	}



	public String getVoltarHistoriaObstetricaGestacao() {
		return VOLTAR_HISTORIA_OBSTETRICA_GESTACAO;
	}



	public McoRecemNascidos getRecemNascidoSelecionado() {
		return recemNascidoSelecionado;
	}



	public void setRecemNascidoSelecionado(McoRecemNascidos recemNascidoSelecionado) {
		this.recemNascidoSelecionado = recemNascidoSelecionado;
	}
}