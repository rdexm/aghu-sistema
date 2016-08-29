package br.gov.mec.aghu.prescricaomedica.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;
import br.gov.mec.aghu.cups.business.ICupsFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagemId;
import br.gov.mec.aghu.model.MamEstadoPaciente;
import br.gov.mec.aghu.model.MamTipoEstadoPaciente;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaoenfermagem.vo.CuidadoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.PrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumTipoImpressao;
import br.gov.mec.aghu.prescricaomedica.util.PrescricaoMedicaTypes;
import br.gov.mec.aghu.prescricaomedica.vo.ConfirmacaoPrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.EstadoPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.FormularioDispAntimicrobianosVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ParametrosProcedureVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;

public class ManterPrescricaoMedicaController extends ActionController {

	private static final String EXCECAO_CAPUTADA = "Exceção caputada:";
	private static final long serialVersionUID = -1233243248941940558L;
	private static final Log LOG = LogFactory.getLog(ManterPrescricaoMedicaController.class);
	private static final String PAGINA_RELATORIOS_CONFIRMACAO_PRESCRICAO = "prescricaomedica-relatoriosConfirmacaoPrescricao";
	private static final String PAGINA_VERIFICA_PRESCRICAO_MEDICA = "prescricaomedica-verificaPrescricaoMedica";
	private static final String PAGINA_RELATORIO_CONSULTORIA = "prescricaomedica-relatorioConsultoria";
	private static final String PAGINA_ESCOLHER_MODELO_BASICO = "prescricaomedica-escolherModeloBasico";
	private static final String PAGINA_MANTER_DIAGNOSTICOS_ATENDIMENTO = "prescricaomedica-manterDiagnosticosAtendimento";
	private static final String PAGINA_MANTER_PROCEDIMENTO_ESPECIAL = "prescricaomedica-manterProcedimentoEspecial";
	private static final String PAGINA_MANTER_SOLICITACAO_HEMOTERAPIA = "prescricaomedica-manterSolicitacaoHemoterapia";
	private static final String PAGINA_CONSULTORIA_SOLICITAR_CONSULTORIA = "prescricaomedica-solicitarConsultoria";
	private static final String PAGINA_MANTER_PRESCRICAO_SOLUCAO = "prescricaomedica-manterPrescricaoSolucao";
	private static final String PAGINA_MANTER_PRESCRICAO_MEDICAMENTO = "prescricaomedica-manterPrescricaoMedicamento";
	private static final String PAGINA_MANTER_PRESCRICAO_CUIDADO = "prescricaomedica-manterPrescricaoCuidado";
	private static final String PAGINA_MANTER_PRESCRICAO_DIETA = "prescricaomedica-manterPrescricaoDieta";
	private static final String PAGINA_FORMULA_NPT = "prescricaomedica-escolherFormulaNpt";	
	private static final String PAGINA_PRESCREVER_ITEM = "prescricaomedica-prescreverItem";
	private static final String PAGINA_MANTER_PRESCRICAO_PARENTERAL = "prescricaomedica-cadastroPrescricaoNpt";
	private static final String PAGINA_MANTER_PRESCRICAO_ALERGIA = "prescricaomedica-manterPrescricaoAlergia";
    private static final String CONFIRMACAO_PRESCRICAO_SUCESSO = "CONFIRMACAO_PRESCRICAO_SUCESSO";
    private static final String LABEL_ESTADO_PACIENTE_MP="Estado do Paciente : ";
	
    @EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	@EJB
	private ICupsFacade cupsFacade;
	@EJB
	private IInternacaoFacade internacaoFacade;
	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	@EJB
	private IAghuFacade aghuFacade;
	@EJB
	private IPacienteFacade pacienteFacade;
	private Integer pmeSeqAtendimento;
	private Integer pmeSeq;
	private PrescricaoMedicaVO prescricaoMedicaVO;
	private Boolean habilitaReimpressao;
	private PrescricaoEnfermagemVO prescEnfVO;
	private String cabecalhoPrescricaoEnfermagem;
	private EnumTipoImpressao tipoImpressao;
	@Inject
	private RelatoriosPrescricaoController relatoriosPrescricaoController;
	private FormularioDispAntimicrobianosVO formulario = new FormularioDispAntimicrobianosVO();
	private Boolean formularioPreenchido = false;
	private List<ItemPrescricaoMedicaVO> listaAntimicrobianos;
	@Inject private RelatorioPrescricaoMedicaController relatorioPrescricaoMedicaController;
	@Inject private PrescricaoMedicaAntimicrobianoController prescricaoMedicaAntimicrobianoController;
	@EJB
	private IParametroFacade parametroFacade;
	private Boolean prescricaoAmbulatorial;
	private Boolean controleModalPlanoPrevAlta;
	private AghParametros horasControlePrevAlta;
	private String tipoConfirmacaoModalPrevPlanoAlta;
	private Boolean confirmacaoPelaModalPrevPlanoAlta;
	private Boolean considerarValorModalPlanoPrevAlta;
	private Boolean cameFromPrescreverItem = false;
	private boolean bloquearData;
	private String estadoPaciente;
	private ParametrosProcedureVO retornoValidacao = new ParametrosProcedureVO();
	private List<MamTipoEstadoPaciente> comboTipoEstadoPaciente;
	private String comboSelecioneEstadoPaciente;
	private MamTipoEstadoPaciente valorEstadoPaciente;
	private Integer idadePaciente;
	private String estadoPacienteLabelTeste;
	@SuppressWarnings("unused")
	private String estadoPacienteLabel;
	private Boolean exibirEstadoPaciente = Boolean.TRUE;
	private Long triagemSeq;
	private Boolean mostrarMensagemAlergia;
	private Boolean exibirModalEstado;
	private Boolean buscarEstado = Boolean.TRUE;
	private String banco;
    private String UrlBaseWebForms;
    private Boolean apresentaModalFormularioAntimicrobianos = Boolean.FALSE;
    private boolean aghuBotoesExameHemoterapia;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String inicio() {
		String returnValue = null;
		this.setControleModalPlanoPrevAlta(true);
		this.setConfirmacaoPelaModalPrevPlanoAlta(false);
		this.setConsiderarValorModalPlanoPrevAlta(false);
		prescricaoAmbulatorial = Boolean.FALSE;
		try {
			buscarParametroDesabilitarBotoesExameHemoterapia();
			setListaAntimicrobianos(new ArrayList<ItemPrescricaoMedicaVO>());
			formularioPreenchido = false;

			if(this.pmeSeqAtendimento != null){
				this.bloquearData = this.prescricaoMedicaFacade.verificarPacienteInternadoCaracteristicaControlePrevisao(pmeSeqAtendimento);
				List<AipPacientes> pacientesPorAtendimento = prescricaoMedicaFacade.pesquisarPacientePorAtendimento(pmeSeqAtendimento);
				pegarIdade(pacientesPorAtendimento);
			}
			if (this.pmeSeqAtendimento != null && this.pmeSeq != null) {
				MpmPrescricaoMedicaId filter = new MpmPrescricaoMedicaId(this.pmeSeqAtendimento, this.pmeSeq);
				this.prescricaoMedicaVO = this.prescricaoMedicaFacade.buscarDadosCabecalhoPrescricaoMedicaVO(filter);
				buscaTriagem();
				List<ItemPrescricaoMedicaVO> itens = null;
				itens = this.prescricaoMedicaFacade.buscarItensPrescricaoMedica(filter, false);
				this.prescricaoMedicaVO.setItens(itens);
				List<EpePrescricaoEnfermagem> listaPrescEnf = this.prescricaoEnfermagemFacade.pesquisarPrescricaoEnfermagemPorAtendimentoEDataFimAteDataAtual(this.pmeSeqAtendimento,prescricaoMedicaVO.getDthrFim());
				EpePrescricaoEnfermagem prescEnfermagem = null;
				if (!listaPrescEnf.isEmpty()) {
					prescEnfermagem = listaPrescEnf.get(0);
				}
				EpePrescricaoEnfermagemId prescEnfId = null;
				if (prescEnfermagem != null && prescEnfermagem.getDthrInicioMvtoPendente() == null) {
					prescEnfId = prescEnfermagem.getId();
					this.prescEnfVO = this.prescricaoEnfermagemFacade.buscarDadosCabecalhoPrescricaoEnfermagemUtilizadoPrescricaoMedicaVO(prescEnfId);
					List<CuidadoVO> listaCuidadoVO = this.prescricaoEnfermagemFacade.buscarCuidadosPrescricaoEnfermagem(prescEnfId, false);
					this.prescEnfVO.setListaCuidadoVO(listaCuidadoVO);
					this.cabecalhoPrescricaoEnfermagem = montarCabecalhoPrescricaoEnfermagem();
				} else {
					prescEnfVO = null;
				}
			}
			AghAtendimentos atdAmbulatorial = aghuFacade.obterAtendimento(pmeSeqAtendimento, null, DominioOrigemAtendimento.getOrigensDePrescricaoAmbulatorial());
			if (atdAmbulatorial != null) {
				prescricaoAmbulatorial = Boolean.TRUE;
			}
			if (cameFromPrescreverItem) {
				cameFromPrescreverItem = false;
				if ("comImpressao".equalsIgnoreCase(this.getTipoConfirmacaoModalPrevPlanoAlta())) {
					returnValue = this.confirmarPrescricaoMedicaComImpressao();
				} else if ("semImpressao".equalsIgnoreCase(this.getTipoConfirmacaoModalPrevPlanoAlta())) {
					returnValue = this.confirmarPrescricaoMedicaSemImpressao();
				} else if ("deixarPendente".equalsIgnoreCase(this.getTipoConfirmacaoModalPrevPlanoAlta())) {
					returnValue = this.deixarPrescricaoPendente();
				}
			}
			if(mostrarMensagemAlergia != null && mostrarMensagemAlergia){
				verificaAlergiaExistente();
				mostrarMensagemAlergia = Boolean.FALSE;
			}
			if(buscarEstado){
				buscarEstadoPaciente();
				buscarEstado = Boolean.FALSE;
			}
			} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return returnValue;
	}
	
	private void buscarParametroDesabilitarBotoesExameHemoterapia() throws ApplicationBusinessException {
		AghParametros aghParametroDesabilitarBotoes = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DESABILITAR_BOTOES_EXAME_HEMOTERAPIA);
		if(aghParametroDesabilitarBotoes != null && aghParametroDesabilitarBotoes.getVlrTexto() != null){
			if(aghParametroDesabilitarBotoes.getVlrTexto().equalsIgnoreCase("S")){
				setAghuBotoesExameHemoterapia(true);
			}else{
				setAghuBotoesExameHemoterapia(false);
			}
		}
	}
	
	public void pegarIdade(List<AipPacientes> pacientesPorAtendimento) {
		if(pacientesPorAtendimento != null && pacientesPorAtendimento.size() == 1){
			AipPacientes paciente = pacientesPorAtendimento.get(0);
			idadePaciente = getIdade(paciente.getDtNascimento());
		}
	}
	
	public void buscaTriagem() {
		if(this.prescricaoMedicaVO != null){
			triagemSeq = this.prescricaoMedicaFacade.obterTriagemPorPaciente(pmeSeqAtendimento);
		}
	}
	private void buscarEstadoPaciente() throws ApplicationBusinessException {
		estadoPaciente = null;
		prescricaoMedicaFacade.verificarParametros(obterLoginUsuarioLogado(), this.prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getSeq(),
				this.prescricaoMedicaVO.getPrescricaoMedica().getId().getSeq(), this.retornoValidacao);
		if(retornoValidacao.getvMostraEstPac().equals(DominioSimNao.S.toString())){
			estadoPaciente = prescricaoMedicaFacade.buscarEstadoPaciente(this.pmeSeqAtendimento, this.pmeSeq);
			comboTipoEstadoPaciente = getComboTipoEstadoPaciente();
			if(comboTipoEstadoPaciente != null && !comboTipoEstadoPaciente.isEmpty()){
				for(MamTipoEstadoPaciente obj : comboTipoEstadoPaciente){
					if(obj.getDescricao().equals(estadoPaciente)){
						this.valorEstadoPaciente = obj;
					}
				}
				if(this.valorEstadoPaciente == null && estadoPaciente != null){
					this.valorEstadoPaciente = prescricaoMedicaFacade.obterTipoEstadoPacientePorDescricao(estadoPaciente);
				}
			}
			
			this.exibirModalEstado = Boolean.TRUE;
		} else {
			this.exibirModalEstado = Boolean.FALSE;
		}
		// #18810
		if(prescricaoMedicaVO != null){
			for (ItemPrescricaoMedicaVO vo : prescricaoMedicaVO.getItens()) {
				if (Boolean.TRUE.equals(vo.getReprescrito())) {
					apresentarMsgNegocio(Severity.INFO, "MPM_03035");
					break;
				}
			}
		}
	}

	/**
	 * Retorna cabeçalho (descrição) formatado para identificar tabela de itens
	 * de Prescrição Enfermagem.
	 * @return
	 */
	public String montarCabecalhoPrescricaoEnfermagem() {
		StringBuilder sb = new StringBuilder();
		if (prescEnfVO != null) {
			String descricao = WebUtil.initLocalizedMessage("LABEL_PRESCRICAO_MEDICA_CABECALHO_PRESCRICAO_ENFERMAGEM", null);
			descricao = descricao.replace("{0}", DateFormatUtil.fomataDiaMesAno(prescEnfVO.getDthrInicio()) + " " + DateFormatUtil.formataHoraMinuto(prescEnfVO.getDthrInicio()));
			descricao = descricao.replace("{1}", DateFormatUtil.fomataDiaMesAno(prescEnfVO.getDthrFim()) + " " + DateFormatUtil.formataHoraMinuto(prescEnfVO.getDthrFim()));
			sb.append(descricao);
		}
		return sb.toString();
	}
	
	/**
	 * bsoliveira
	 * 07/10/2010
	 * Chama método que verifica se existe algum item na lista de itens da
	 * prescrição que devem ser excluido, cas exista chama o metodo de exclusão
	 * e remove o objeto da lista.
	 * @throws ApplicationBusinessException
	 */
	public void excluirSelecionados() {
		try {
			List<ItemPrescricaoMedicaVO> itens = prescricaoMedicaVO.getItens();

			String nomeMicrocomputador = obtemNomeDoMicroComputador();

			prescricaoMedicaFacade.excluirSelecionados(prescricaoMedicaVO.getPrescricaoMedica(), itens, nomeMicrocomputador);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	/**
	 * Método que obtem o estilo da coluna dos ítens da tabela
	 * 
	 */
	public String obterEstiloColuna(ItemPrescricaoMedicaVO item) {
		String retorno = "";
		if (item.getExcluir() != null && item.getExcluir()) {
			retorno = "background-color:#FF6347;";
		}
		return retorno + "max-width: 300px; word-wrap: break-word;";
	}
	/**
	 * Atualiza as informações de Previsão de alta e Paciente Pediátrico
	 */
	public void atualizarPrevAltaPacPed() {
		try {
			atualizaPrevisaoAltaInternacaoSeTemInternacao();
			String nomeMicrocomputador = obtemNomeDoMicroComputador();
			verificaAlergiaExistente();
			this.pacienteFacade.atualizarPacPediatrico(this.pmeSeqAtendimento, this.prescricaoMedicaVO.getIndPacPediatrico(), nomeMicrocomputador, new Date());
            inicio();
			this.apresentarMsgNegocio("ATUALIZACAO_PREVISAO_ALTA");
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	public void verificaAlergiaExistente() {
		if(this.pmeSeqAtendimento != null){
			if(this.prescricaoMedicaFacade.verificaExistenciaAlergiaCadastradaPaciente(this.pmeSeqAtendimento)){
				this.apresentarMsgNegocio(Severity.INFO, "MS02_POSSUI_ALERGIA");
			}
		}
	}
	public Boolean analisarConfirmacaoPrescricaoMedica() throws ApplicationBusinessException {
		if (this.prescricaoMedicaVO.getHasInternacao()) {
			if (exibirModalPrevAlta()) {
				openDialog("modalPlanoPrevAltaWG");
				this.setControleModalPlanoPrevAlta(false);
				return false;
			} 
			if(!this.bloquearData){
				if (!DateValidator.validaDataTruncadaMaiorIgual(this.prescricaoMedicaVO.getDtPrevAlta(), new Date())) {
					this.apresentarMsgNegocio(Severity.ERROR, "ERRO_PREV_ALTA_MENOR");
					return false;
				}
			}
		}
		return true;
	}
	public Boolean exibirModalPrevAlta() {
		if (this.prescricaoMedicaVO.getHasInternacao() && 
				this.prescricaoMedicaVO.getPrescricaoMedica().getServidorValida() == null) {
			try {
				return this.internacaoFacade.exibirModalPlanoPrevAlta(this.prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getInternacao()) && this.getControleModalPlanoPrevAlta();
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e);
			}
		}
		return false;
	}
	public String getMsgPlanoPrevAlta() {
		try {
			this.setHorasControlePrevAlta(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HORAS_CONTROLE_PREV_ALTA));
			if(this.getHorasControlePrevAlta().getVlrNumerico() == null){
				this.getHorasControlePrevAlta().setVlrNumerico(BigDecimal.valueOf(48));	
			}
			return this.getHorasControlePrevAlta().getVlrNumerico().toString();
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return null;
	}
	public String deixarPrescricaoPendente() {
		try {
			String nomeMicrocomputador = obtemNomeDoMicroComputador();
			this.setControleModalPlanoPrevAlta(true);
			this.prescricaoMedicaFacade.atualizarPrescricaoPendente(this.pmeSeqAtendimento, this.pmeSeq, nomeMicrocomputador);
			return this.redirecionaVerificaPrescricaoMedica();
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			this.apresentarExcecaoNegocio(e);
			return null;
		}
	}
	/**
	 * Método principal invocado após a confirmação da Prescrição.
	 * @return String - Redireciona para tela que abre abas com PDFs.
	 */
	public String confirmarPrescricaoMedica(boolean bImprimir, Boolean isconfirmarPrescricaoMedicaComImpressao, Boolean isconfirmarPrescricaoMedicaSemImpressao) {
		try {
			//#44179 & #1378
			if(this.retornoValidacao != null && this.retornoValidacao.getvMostraEstPac().equals(DominioSimNao.S.toString())){
				prescricaoMedicaFacade.validarCIDAtendimento(this.retornoValidacao, pmeSeqAtendimento, pmeSeq);
				if(this.retornoValidacao.getRgtSeq() != null) {
					if (exibirModalEstado){
						openDialog("modalEstadoPacienteWG");
						exibirModalEstado = Boolean.FALSE;
						return null;
					} else {
						onChangeManterEstadoPaciente();
					}
				}
			}
			atualizaPrevisaoAltaInternacaoSeTemInternacao();
			
			String nomeMicrocomputador = obtemNomeDoMicroComputador();
			
			prescricaoMedicaTemPeloMenosUmaDieta();
			
			List<AfaMedicamento> listaMed = pacienteFacade.obterListaMedicamentosTubercolostaticos();
			Boolean isMedTubercolostatico = Boolean.FALSE;
			for(ItemPrescricaoMedicaVO obj : prescricaoMedicaVO.getItens()){
				if(PrescricaoMedicaTypes.MEDICAMENTO.equals(obj.getTipo())){
					for(AfaMedicamento objMed : listaMed){
						if(obj.getDescricao().contains(objMed.getDescricao())){
							isMedTubercolostatico = Boolean.TRUE;
						}
					}
				}
			}
			if(isMedTubercolostatico){
				this.apresentarMsgNegocio("MSG_AVISO_MEDICAMENTO_TUBERCOLOSTATICO_PRESCRITO");
			}
			this.pacienteFacade.atualizarPacPediatrico(this.pmeSeqAtendimento, this.prescricaoMedicaVO.getIndPacPediatrico(), nomeMicrocomputador, new Date());
			ConfirmacaoPrescricaoVO confirmacaoPrescricaoVO = this.getPrescricaoMedicaFacade()
					.confirmarPrescricaoMedica(this.prescricaoMedicaVO.getPrescricaoMedica(), nomeMicrocomputador, new Date());
			LOG.info("Prescrição "+ this.prescricaoMedicaVO.getPrescricaoMedica().getId() + " confirmada");
			LOG.info("Levantando evento para impressão direta.");
			this.apresentarMsgNegocio(CONFIRMACAO_PRESCRICAO_SUCESSO);
			this.relatoriosPrescricaoController.setConfirmacaoPrescricaoVO(confirmacaoPrescricaoVO);
			this.relatoriosPrescricaoController.setTipoImpressao(tipoImpressao);
			this.relatoriosPrescricaoController.setPrescricaoMedicaVO(prescricaoMedicaVO);
			limparValorEstadoPaciente();
			if(cupsFacade.isCupsAtivo()) {
				relatoriosPrescricaoController.iniciarRelatorios(Boolean.FALSE);
				return this.redirecionaVerificaPrescricaoMedica();
			}else {
				relatoriosPrescricaoController.iniciarRelatorios(Boolean.TRUE);
				if(relatoriosPrescricaoController.getRelatorioSemAlteracao()){
					return this.redirecionaVerificaPrescricaoMedica();
				}
				return PAGINA_RELATORIOS_CONFIRMACAO_PRESCRICAO;
			}
		} catch (ApplicationBusinessException e) {
			if(e.getCode().toString().equals("DIAGNOSTICOS_ATENDIMENTO")){
				return redirecionarManterDiagnosticosAtendimento();
			} else if (e.getCode().toString().equals("MSG_ERRO_PRESCRICAO_MEDICA_DEVE_CONTER_AO_MENOS_UM_ITEM_DIETA")
					|| e.getCode().toString().equals("QUANTIDADE_DIETAS")) {
				apresentarMsgNegocio(e.getCode().toString());
				return null;
			} else {
				this.apresentarExcecaoNegocio(e);
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
		}
		return null;
	}

	/**
	 * @return
	 */
	private String obtemNomeDoMicroComputador() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error(EXCECAO_CAPUTADA, e);
		}
		return nomeMicrocomputador;
	}

	/**
	 * @throws BaseException
	 */
	private void atualizaPrevisaoAltaInternacaoSeTemInternacao()
			throws BaseException {
		if (this.prescricaoMedicaVO.getHasInternacao()) {
			this.internacaoFacade.atualizaPrevisaoAltaInternacao(this.prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getInternacao(), this.prescricaoMedicaVO.getDtPrevAlta(),
					this.getConfirmacaoPelaModalPrevPlanoAlta(), this.getConsiderarValorModalPlanoPrevAlta(), bloquearData);
		}
	}
	private void prescricaoMedicaTemPeloMenosUmaDieta() throws ApplicationBusinessException {
		prescricaoMedicaFacade.prescricaoMedicaTemPeloMenosUmaDieta(prescricaoMedicaVO);
	}
	
	/**
	 * Ação do botão "Confirmar COM Impressão"
	 */
	public String confirmarPrescricaoMedicaComImpressao() throws ApplicationBusinessException {
		try {
			prescricaoMedicaFacade.verificarPrescricaoCancelada(this.prescricaoMedicaVO.getPrescricaoMedica());
		} catch(ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		Boolean isconfirmarPrescricaoMedicaComImpressao = Boolean.FALSE;
		if(analisarConfirmacaoPrescricaoMedica()){		
			tipoImpressao = EnumTipoImpressao.IMPRESSAO; 
			if (isExibirModalFormularioAntimicrobianos()){
				HttpSession session = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getSession();
				
				if (!formularioPreenchido){
					formularioPreenchido = true;
					return null;
				}
				formulario.setListaAntimicrobianos(getListaAntimicrobianos());
				formulario.setNomePaciente(getPrescricaoMedicaVO().getNome());
				formulario.setReg(getPrescricaoMedicaVO().getProntuario().toString());
				formulario.setSetorLeito(getPrescricaoMedicaVO().getLocal());
				formulario.setData(Calendar.getInstance().getTime());
				session.setAttribute("formulario", formulario);  
			}		
			isconfirmarPrescricaoMedicaComImpressao = Boolean.TRUE;
			return this.confirmarPrescricaoMedica(true, isconfirmarPrescricaoMedicaComImpressao, false);
		}
		return null;
	}
	
	public String confirmarPrescricaoMedicaModalPrevPlanoAlta(Boolean considerarValorParam) throws ApplicationBusinessException {
		try {
			prescricaoMedicaFacade.verificarPrescricaoCancelada(this.prescricaoMedicaVO.getPrescricaoMedica());
		} catch(ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		this.setConsiderarValorModalPlanoPrevAlta(considerarValorParam);
		this.setControleModalPlanoPrevAlta(false);
		Integer qtdHoras; 
		if(considerarValorParam){
			qtdHoras = this.getHorasControlePrevAlta().getVlrNumerico().intValue();
			this.prescricaoMedicaVO.setDtPrevAlta(DateUtil.adicionaHoras(new Date(),qtdHoras));
		}else{
			AghParametros param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_PADRAO_CONTROL_PREV_ALTA);
			qtdHoras = param != null ? param.getVlrNumerico().intValue() * 24 : Integer.getInteger("72");
			this.prescricaoMedicaVO.setDtPrevAlta(DateUtil.adicionaHoras(new Date(),qtdHoras));
		}		
		switch (this.getTipoConfirmacaoModalPrevPlanoAlta()) {
		case "comImpressao":
			return confirmarPrescricaoMedicaComImpressao();
		case "semImpressao":
			return confirmarPrescricaoMedicaSemImpressao();
		default:
			return null;
		}
	}
	/**
	 * Ação do botão "Confirmar COM ReImpressão"
	 */
	public String confirmarPrescricaoMedicaComReimpressao() throws BaseException, JRException, SystemException, IOException {
		try {
			prescricaoMedicaFacade.verificarPrescricaoCancelada(this.prescricaoMedicaVO.getPrescricaoMedica());
		} catch(ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

			try {	
				prescricaoMedicaTemPeloMenosUmaDieta();
			} catch (ApplicationBusinessException e) {
				if (e.getCode().toString().equals("MSG_ERRO_PRESCRICAO_MEDICA_DEVE_CONTER_AO_MENOS_UM_ITEM_DIETA")
						|| e.getCode().toString().equals("QUANTIDADE_DIETAS")) {
					apresentarMsgNegocio(e.getCode().toString());
					return null;
				}
			}
			
			String confirmacao = executaConfirmarPrescricaoMedicaComReimpressao();// gerar pendencia de
			if (confirmacao != null) {
				prescricaoMedicaVO.setPrescricaoMedica(prescricaoMedicaVO.getPrescricaoMedica());
				tipoImpressao = EnumTipoImpressao.REIMPRESSAO;
				relatorioPrescricaoMedicaController.setTipoImpressao(EnumTipoImpressao.REIMPRESSAO);
				relatorioPrescricaoMedicaController.setDataMovimento(new Date());
				relatorioPrescricaoMedicaController.setServidorValido(prescricaoMedicaVO.getPrescricaoMedica().getServidorValida());
				relatorioPrescricaoMedicaController.setPrescricaoMedicaVO(prescricaoMedicaVO);
			if (cupsFacade.isCupsAtivo()) {// na v5, raiseCupsEvent só e disparado caso cups esteja ativo
					relatorioPrescricaoMedicaController.observarEventoReimpressaoPrescricao();
				}
				relatorioPrescricaoMedicaController.setTipoImpressao(null);
				relatorioPrescricaoMedicaController.setDataMovimento(null);
				relatorioPrescricaoMedicaController.setServidorValido(null);
				relatorioPrescricaoMedicaController.setPrescricaoMedicaVO(null);
				buscarEstado = Boolean.TRUE;
				return PAGINA_VERIFICA_PRESCRICAO_MEDICA;
			}
			return null;
	
	}
	public String prescreverJustificativaUsoMedicamento() {
        return PAGINA_PRESCREVER_ITEM;
	}
	/**
	 * Ação do botão "Confirmar SEM Impressão"
	 */
	public String confirmarPrescricaoMedicaSemImpressao() throws ApplicationBusinessException {
		try {
			prescricaoMedicaFacade.verificarPrescricaoCancelada(this.prescricaoMedicaVO.getPrescricaoMedica());
		} catch(ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		tipoImpressao = EnumTipoImpressao.SEM_IMPRESSAO;
		Boolean isconfirmarPrescricaoMedicaSemImpressao = Boolean.FALSE;
		if(analisarConfirmacaoPrescricaoMedica()){
			isconfirmarPrescricaoMedicaSemImpressao = Boolean.TRUE;
			return this.confirmarPrescricaoMedica(false, false, isconfirmarPrescricaoMedicaSemImpressao);
		}
		return null;			
	}
	
	public String executaConfirmarPrescricaoMedicaComReimpressao() throws ApplicationBusinessException {
		try {
			prescricaoMedicaFacade.verificarPrescricaoCancelada(this.prescricaoMedicaVO.getPrescricaoMedica());
		} catch(ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		tipoImpressao = EnumTipoImpressao.REIMPRESSAO;
		Boolean isconfirmarPrescricaoMedicaReimpressao = Boolean.FALSE;
		if(analisarConfirmacaoPrescricaoMedica()){
			isconfirmarPrescricaoMedicaReimpressao = Boolean.TRUE;
			return this.confirmarPrescricaoMedica(false, false, isconfirmarPrescricaoMedicaReimpressao);
		}
		return null;			
	}
	

	public Boolean getHabilitaReimpressao() {
		if (this.pmeSeqAtendimento != null && this.pmeSeq != null) {
			MpmPrescricaoMedica prescricaoMedica = prescricaoMedicaFacade.obterPrescricaoPorId(this.pmeSeqAtendimento, this.pmeSeq);
			
			if (prescricaoMedica != null && prescricaoMedica.getServidorValida() != null && prescricaoMedica.getServidorValida().getId() != null
					&& prescricaoMedica.getServidorValida().getId().getMatricula() != null && prescricaoMedica.getServidorValida().getId().getVinCodigo() != null) {
				habilitaReimpressao = true;
			} else {
				habilitaReimpressao = false;
			}
		} else {
			habilitaReimpressao = false;
		}
		return habilitaReimpressao;
	}
	public void setHabilitaReimpressao(Boolean habilitaReimpressao) {
		this.habilitaReimpressao = habilitaReimpressao;
	}
	/**
	 * Cancela a prescrição
	 */
	public String cancelarPrescricao() {
		try {
			String nomeMicrocomputador = obtemNomeDoMicroComputador();
			this.getPrescricaoMedicaFacade().cancelarPrescricaoMedica(this.pmeSeqAtendimento, this.pmeSeq, nomeMicrocomputador);
			this.prescricaoMedicaVO = null;
			limparValorEstadoPaciente();
			return redirecionaVerificaPrescricaoMedica();
		} catch (BaseException e) {
			LOG.error("Erro", e);
			this.apresentarExcecaoNegocio(e);
			return null;
		}
	}
	/**
	 * #1378 p_registrar_manter
	 */
	public void manterEstadoPaciente(){
		if(estadoPaciente != null && !estadoPaciente.trim().equals("")){
			if(this.pmeSeqAtendimento != null){
				this.prescricaoMedicaFacade.manterEstadoPaciente(this.prescricaoMedicaFacade.obterMamEstadoPacienteAtdSeq(this.pmeSeqAtendimento.longValue()), retornoValidacao.getRgtSeq());
			}
		}
	}
	/**
	 * #1378 on change de combo estado Paciente
	 */
	public void onChangeManterEstadoPaciente() throws ApplicationBusinessException{
		EstadoPacienteVO estadoAnterior = null;
		if(pmeSeqAtendimento != null){
			estadoAnterior = this.prescricaoMedicaFacade.obterEstadoPacientePeloAtdSeq(pmeSeqAtendimento.longValue());
		} else {
			estadoAnterior = this.prescricaoMedicaFacade.obterEstadoPacientePeloTrgSeq(triagemSeq);
		}
		MamEstadoPaciente novoEstado = new MamEstadoPaciente();
		novoEstado.setMamTipoEstadoPaciente(this.valorEstadoPaciente);
		novoEstado.setPrescricaoMedica(prescricaoMedicaVO.getPrescricaoMedica());
		novoEstado.setAghAtendimentos(new AghAtendimentos());
		novoEstado.getAghAtendimentos().setSeq(pmeSeqAtendimento);
		if(retornoValidacao.getRgtSeq() == null){
			prescricaoMedicaFacade.validarCIDAtendimento(this.retornoValidacao, pmeSeqAtendimento, pmeSeq);			
		}
		novoEstado.setMamRegistro(prescricaoMedicaFacade.obterRegistroPorSeq(retornoValidacao.getRgtSeq()));
		if (estadoAnterior == null || (novoEstado.getMamTipoEstadoPaciente() != null && estadoAnterior != null &&
				!novoEstado.getMamTipoEstadoPaciente().getTitulo().equals(estadoAnterior.getTitulo()))) {
			prescricaoMedicaFacade.alterarEstadoPaciente(estadoAnterior,novoEstado,pmeSeqAtendimento.longValue());
			exibirModalEstado = Boolean.FALSE;
		} else {
			manterEstadoPaciente();
			exibirModalEstado = Boolean.FALSE;
		}
	}
	public void limparValorEstadoPaciente(){
		this.valorEstadoPaciente = null;
		this.estadoPaciente = null;
		this.buscarEstado = Boolean.TRUE;
		this.retornoValidacao = new ParametrosProcedureVO();
	}
	public String redirecionaCadastroPrescricaoNpt(){
		return PAGINA_MANTER_PRESCRICAO_PARENTERAL;
	}
	public String redirecionaVerificaPrescricaoMedica(){
        //            #48346 Solução sugerida pela arquitetura para o problema de redirecionamento após uma prescrição médica bem sucedida.
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("verificaprescricao/verificaPrescricaoMedica.xhtml?cid="+super.conversation.getId());
        } catch (IOException e) {
            LOG.error(EXCECAO_CAPUTADA, e);
        }
		return null;
	}
	public String verificarMedicamentosAntimicrobianos() throws ApplicationBusinessException {
		apresentaModalFormularioAntimicrobianos =
				prescricaoMedicaAntimicrobianoController.doVerificarMedicamentosAntimicrobianos(
						this.prescricaoMedicaVO
						, this.getListaAntimicrobianos()
				);
		return confirmarPrescricaoMedicaComImpressao();
	}	
	public String redirecionarManterPrescricaoDieta(){
		return PAGINA_MANTER_PRESCRICAO_DIETA;
	}
	public String redirecionarManterPrescricaoCuidado(){
		return PAGINA_MANTER_PRESCRICAO_CUIDADO;
	}
	public String redirecionarManterPrescricaoMedicamento(){
		return PAGINA_MANTER_PRESCRICAO_MEDICAMENTO;
	}
	public String redirecionarManterPrescricaoSolucao(){
		return PAGINA_MANTER_PRESCRICAO_SOLUCAO;
	}
	public String redirecionarSolicitarConsultoria(){
		return PAGINA_CONSULTORIA_SOLICITAR_CONSULTORIA;
	}
	public String redirecionarManterSolicitacaoHemoterapia(){
		return PAGINA_MANTER_SOLICITACAO_HEMOTERAPIA;
	}
	public String redirecionarManterProcedimentoEspecial(){
		return PAGINA_MANTER_PROCEDIMENTO_ESPECIAL;
	}
	public String redirecionarManterDiagnosticosAtendimento(){
		return PAGINA_MANTER_DIAGNOSTICOS_ATENDIMENTO;
	}
	public String redirecionarEscolherModeloBasico(){
		return PAGINA_ESCOLHER_MODELO_BASICO;
	}
	public String redirecionarRelatorioConsultoria(){
		return PAGINA_RELATORIO_CONSULTORIA;
	}
	public String redirecionarEscolherFormulaNpt(){
		return PAGINA_FORMULA_NPT;
	}	public String redirecionarManterPrescricaoAlergia(){
		try{
			prescricaoMedicaFacade.atualizarIndPacientePediatrico(prescricaoMedicaVO);
 		} catch (ApplicationBusinessException e) {
 			apresentarExcecaoNegocio(e);
 		}
		return PAGINA_MANTER_PRESCRICAO_ALERGIA;
	}
	
	public FormularioDispAntimicrobianosVO getFormulario() {
		return formulario;
	}
	public void setFormulario(FormularioDispAntimicrobianosVO formulario) {
		this.formulario = formulario;
	}
	public boolean isExibirModalFormularioAntimicrobianos() {
		return !getListaAntimicrobianos().isEmpty();
	}
	public String getCid(){
		return this.conversation.getId();
	}
	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return this.prescricaoMedicaFacade;
	}
	public void setPrescricaoMedicaFacade(IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}
	public IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade() {
		return prescricaoEnfermagemFacade;
	}
	public void setPrescricaoEnfermagemFacade(IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade) {
		this.prescricaoEnfermagemFacade = prescricaoEnfermagemFacade;
	}
	public Integer getPmeSeqAtendimento() {
		return this.pmeSeqAtendimento;
	}
	public void setPmeSeqAtendimento(Integer pmeSeqAtendimento) {
		this.pmeSeqAtendimento = pmeSeqAtendimento;
	}
	public Integer getPmeSeq() {
		return this.pmeSeq;
	}
	public void setPmeSeq(Integer pmeSeq) {
		this.pmeSeq = pmeSeq;
	}
	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return this.prescricaoMedicaVO;
	}
	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}
	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}
	public PrescricaoEnfermagemVO getPrescEnfVO() {
		return prescEnfVO;
	}
	public void setPrescEnfVO(PrescricaoEnfermagemVO prescEnfVO) {
		this.prescEnfVO = prescEnfVO;
	}
	public List<ItemPrescricaoMedicaVO> getListaAntimicrobianos() {
		return listaAntimicrobianos;
	}
	public void setListaAntimicrobianos(List<ItemPrescricaoMedicaVO> listaAntimicrobianos) {
		this.listaAntimicrobianos = listaAntimicrobianos;
	}
	public String getCabecalhoPrescricaoEnfermagem() {
		return cabecalhoPrescricaoEnfermagem;
	}
	public void setCabecalhoPrescricaoEnfermagem(String cabecalhoPrescricaoEnfermagem) {
		this.cabecalhoPrescricaoEnfermagem = cabecalhoPrescricaoEnfermagem;
	}
	public Boolean getPrescricaoAmbulatorial() {
		return prescricaoAmbulatorial;
	}
	public void setPrescricaoAmbulatorial(Boolean prescricaoAmbulatorial) {
		this.prescricaoAmbulatorial = prescricaoAmbulatorial;
	}
	public EnumTipoImpressao getTipoImpressao() {
		return tipoImpressao;
	}
	public void setTipoImpressao(EnumTipoImpressao tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}
	public Integer getIdadePaciente() {
		return idadePaciente;
	}
    public int getIdade(Date data) {  
        Calendar cData = Calendar.getInstance();  
        Calendar cHoje= Calendar.getInstance();  
        cData.setTime(data);  
        cData.set(Calendar.YEAR, cHoje.get(Calendar.YEAR));  
        int idade = cData.after(cHoje) ? -1 : 0;  
        cData.setTime(data);  
        idade += cHoje.get(Calendar.YEAR) - cData.get(Calendar.YEAR);  
        return idade;  
    }
	public String getComboSelecioneEstadoPaciente() {
		if(estadoPaciente == null){
			this.comboSelecioneEstadoPaciente = WebUtil.initLocalizedMessage("LABEL_COMBO_ESTADO_PACIENTE_NULO", null);
		}else{
			this.comboSelecioneEstadoPaciente =  WebUtil.initLocalizedMessage("LABEL_COMBO_ESTADO_PACIENTE_EXISTENTE", null);
		}
		return comboSelecioneEstadoPaciente;
	}
	public List<MamTipoEstadoPaciente> getComboTipoEstadoPaciente() {
		comboTipoEstadoPaciente = prescricaoMedicaFacade.obterListaTipoEstadoPacientePrescricao(this.pmeSeqAtendimento);
		return comboTipoEstadoPaciente;
	}
	public MamTipoEstadoPaciente getValorEstadoPaciente() {
		return valorEstadoPaciente;
	}
	public void setValorEstadoPaciente(MamTipoEstadoPaciente valorEstadoPaciente) {
		this.valorEstadoPaciente = valorEstadoPaciente;
	}
	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	public void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}
	public AghParametros getHorasControlePrevAlta() {
		return horasControlePrevAlta;
	}
	public void setHorasControlePrevAlta(AghParametros horasControlePrevAlta) {
		this.horasControlePrevAlta = horasControlePrevAlta;
	}
	public String getTipoConfirmacaoModalPrevPlanoAlta() {
		return tipoConfirmacaoModalPrevPlanoAlta;
	}
	public void setTipoConfirmacaoModalPrevPlanoAlta(String tipoConfirmacaoModalPrevPlanoAlta) {
		this.tipoConfirmacaoModalPrevPlanoAlta = tipoConfirmacaoModalPrevPlanoAlta;
	}
	public Boolean getControleModalPlanoPrevAlta() {
		return controleModalPlanoPrevAlta;
	}
	public void setControleModalPlanoPrevAlta(Boolean controleModalPlanoPrevAlta) {
		this.controleModalPlanoPrevAlta = controleModalPlanoPrevAlta;
	}
	public Boolean getConfirmacaoPelaModalPrevPlanoAlta() {
		return confirmacaoPelaModalPrevPlanoAlta;
	}
	public void setConfirmacaoPelaModalPrevPlanoAlta(Boolean confirmacaoPelaModalPrevPlanoAlta) {
		this.confirmacaoPelaModalPrevPlanoAlta = confirmacaoPelaModalPrevPlanoAlta;
	}
	public Boolean getConsiderarValorModalPlanoPrevAlta() {
		return considerarValorModalPlanoPrevAlta;
	}
	public void setConsiderarValorModalPlanoPrevAlta(Boolean considerarValorModalPlanoPrevAlta) {
		this.considerarValorModalPlanoPrevAlta = considerarValorModalPlanoPrevAlta;
	}
	public Boolean getCameFromPrescreverItem() {
		return cameFromPrescreverItem;
	}
	public void setCameFromPrescreverItem(Boolean cameFromPrescreverItem) {
		this.cameFromPrescreverItem = cameFromPrescreverItem;
	}
	public boolean isBloquearData() {
		return bloquearData;
	}
	public void setBloquearData(boolean bloquearData) {
		this.bloquearData = bloquearData;
	}
	public String getEstadoPaciente() {
		return estadoPaciente;
	}
	public void setEstadoPaciente(String estadoPaciente) {
		this.estadoPaciente = estadoPaciente;
	}
	public ParametrosProcedureVO getRetornoValidacao() {
		return retornoValidacao;
	}
	public void setRetornoValidacao(ParametrosProcedureVO retornoValidacao) {
		this.retornoValidacao = retornoValidacao;
	}
	public Boolean getExibirEstadoPaciente() {
		return exibirEstadoPaciente;
	}
	public void setExibirEstadoPaciente(Boolean exibirEstadoPaciente) {
		this.exibirEstadoPaciente = exibirEstadoPaciente;
	}
	public String getEstadoPacienteLabel() {
		EstadoPacienteVO aux =  prescricaoMedicaFacade.obterEstadoPacientePeloAtdSeq(pmeSeqAtendimento.longValue());
		if(aux != null){
			this.estadoPacienteLabel = aux.getTitulo();
			estadoPaciente = aux.getTitulo();
			return LABEL_ESTADO_PACIENTE_MP + aux.getTitulo();
		}
		return null;
	}
	public void setEstadoPacienteLabel(String estadoPacienteLabel) {
		this.estadoPacienteLabel = estadoPacienteLabel;
	}
	public String getEstadoPacienteLabelTeste() {
		return estadoPacienteLabelTeste;
	}
	public void setEstadoPacienteLabelTeste(String estadoPacienteLabelTeste) {
		this.estadoPacienteLabelTeste = estadoPacienteLabelTeste;
	}
	public Boolean getMostrarMensagemAlergia() {
		return mostrarMensagemAlergia;
	}
	public void setMostrarMensagemAlergia(Boolean mostrarMensagemAlergia) {
		this.mostrarMensagemAlergia = mostrarMensagemAlergia;
	}
	public Long getTriagemSeq() {
		return triagemSeq;
	}
	public void setTriagemSeq(Long triagemSeq) {
		this.triagemSeq = triagemSeq;
	}
	public Boolean getBuscarEstado() {
		return buscarEstado;
	}
	public void setBuscarEstado(Boolean buscarEstado) {
		this.buscarEstado = buscarEstado;
	}
	public String getUrlBaseWebForms() {
		if (StringUtils.isBlank(UrlBaseWebForms)) {
		    try {
				AghParametros aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_URL_BASE_AGH_ORACLE_WEBFORMS);
		
				if (aghParametros != null) {
				    UrlBaseWebForms = aghParametros.getVlrTexto();
				}
		    } catch (BaseException e) {
		    	apresentarExcecaoNegocio(e);
		    }
		}
		return UrlBaseWebForms;
    }
    public String getBanco() {
		if (StringUtils.isBlank(banco)) {
		    AghParametros aghParametrosBanco;
			try {
				aghParametrosBanco = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_BANCO_AGHU_AGHWEB);
				if (aghParametrosBanco != null) {
					banco = aghParametrosBanco.getVlrTexto();
				}
			} catch (ApplicationBusinessException e) {
		    	apresentarExcecaoNegocio(e);
			}
		}
		return banco;
    }
	public Boolean getApresentaModalFormularioAntimicrobianos() {
		return apresentaModalFormularioAntimicrobianos;
	}
	public void setApresentaModalFormularioAntimicrobianos(
			Boolean apresentaModalFormularioAntimicrobianos) {
		this.apresentaModalFormularioAntimicrobianos = apresentaModalFormularioAntimicrobianos;
	}

	public boolean isAghuBotoesExameHemoterapia() {
		return aghuBotoesExameHemoterapia;
	}

	public void setAghuBotoesExameHemoterapia(boolean aghuBotoesExameHemoterapia) {
		this.aghuBotoesExameHemoterapia = aghuBotoesExameHemoterapia;
	}
}