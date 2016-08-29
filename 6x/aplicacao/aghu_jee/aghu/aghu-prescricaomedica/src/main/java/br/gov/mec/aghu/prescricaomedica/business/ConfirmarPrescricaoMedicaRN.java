package br.gov.mec.aghu.prescricaomedica.business;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Months;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.bancosangue.dao.AbsAmostrasPacientesDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsMotivosCancelaColetasDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsSolicitacoesHemoterapicasDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsValidAmostraProcedDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsValidAmostrasComponentesDAO;
import br.gov.mec.aghu.bancosangue.vo.SitucaoSolicitacaoHemoterapicaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioFormaCalculoAprazamento;
import br.gov.mec.aghu.dominio.DominioImpresso;
import br.gov.mec.aghu.dominio.DominioIndPendenteDiagnosticos;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioResponsavelColeta;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoColeta;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.dominio.DominioTipoValidade;
import br.gov.mec.aghu.dominio.DominioUnidadeTempo;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.estoque.dao.SceItemRmsDAO;
import br.gov.mec.aghu.estoque.dao.SceReqMateriaisDAO;
import br.gov.mec.aghu.estoque.dao.SceTipoMovimentosDAO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.faturamento.vo.SolHemoVO;
import br.gov.mec.aghu.model.AbsAmostrasPacientes;
import br.gov.mec.aghu.model.AbsAmostrasPacientesId;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsMotivosCancelaColetas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicasId;
import br.gov.mec.aghu.model.AbsSolicitacoesPorAmostra;
import br.gov.mec.aghu.model.AbsValidAmostraProced;
import br.gov.mec.aghu.model.AbsValidAmostrasComponentes;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghImpressoraPadraoUnids;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AhdHospitaisDia;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.ItemPrescricaoMedica;
import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmJustificativaTb;
import br.gov.mec.aghu.model.MpmJustificativaUsoMdto;
import br.gov.mec.aghu.model.MpmNotificacaoTb;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.MpmProcedEspecialRm;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.SceItemRmsId;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCidAtendimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmJustificativaTbDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmJustificativaUsoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmNotificacaoTbDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmProcedEspecialRmDAO;
import br.gov.mec.aghu.prescricaomedica.vo.ConfirmacaoPrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * 
 * 
 * as procedures mpmp_conf_sit_hemot, mpmp_confirma_hemo, mpmp_confirma_justif
 * não sderão migradas por não estarem inclusão nesta versão do sistema.
 * 
 * @ORADB: mpmk_confirma
 * 
 * @author gmneto
 * 
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class ConfirmarPrescricaoMedicaRN extends BaseBusiness {

	@EJB
	private ManterPrescricaoMedicaRN manterPrescricaoMedicaRN;
	
	@EJB
	private PrescricaoMedicaRN prescricaoMedicaRN;
	
	@EJB
	private PrescreverProcedimentoEspecialRN prescreverProcedimentoEspecialRN;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
		
	@EJB
	private PrescricaoNptRN prescricaoNptRN;

	@EJB
	private ManterPrescricaoMedicaON manterPrescricaoMedicaON;
	
	@EJB
	private IPermissionService permissionService;
	
	@Inject
	private AbsValidAmostrasComponentesDAO absValidAmostrasComponentesDAO;
	
	@Inject
	private AbsValidAmostraProcedDAO absValidAmostraProcedDAO;
	
	@Inject
	private MpmJustificativaUsoMdtoDAO mpmJustificativaUsoMdtoDAO;
	
	@Inject
	private MpmJustificativaTbDAO mpmJustificativaTbDAO;
	
	@Inject
	private MpmNotificacaoTbDAO mpmNotificacaoTbDAO;

	
	private static final Log LOG = LogFactory.getLog(ConfirmarPrescricaoMedicaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmPrescricaoNptDAO mpmPrescricaoNptDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private MpmCidAtendimentoDAO mpmCidAtendimentoDAO;
	
	@Inject
	private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;
	
	@Inject
	private MpmPrescricaoDietaDAO mpmPrescricaoDietaDAO;
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@Inject
	private MpmProcedEspecialRmDAO mpmProcedEspecialRmDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private MpmPrescricaoCuidadoDAO mpmPrescricaoCuidadoDAO;
	
	@Inject
	private MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO;
	
	@Inject
	private ObjetosOracleDAO objetosOracleDAO;

	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private	SceTipoMovimentosDAO sceTipoMovimentosDAO;
	
	@Inject
	private SceReqMateriaisDAO sceReqMateriaisDAO;
	
	@Inject
	private SceItemRmsDAO sceItemRmsDAO;
	
	@Inject
	private AbsSolicitacoesHemoterapicasDAO absSolicitacoesHemoterapicasDAO;

	@Inject
	private AbsMotivosCancelaColetasDAO absMotivosCancelaColetasDAO;

	@Inject
	private AbsAmostrasPacientesDAO absAmostrasPacientesDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = -5608709767655042538L;

	/**
	 * Constante que guarda o nome do atributo do contexto referente ao sequence
	 * DAO
	 */
	

//	private static final String ENTITY_MANAGER = "entityManager";

	public enum ConfirmarPrescricaoMedicaExceptionCode implements
			BusinessExceptionCode {
		PROCEDIMENTO_SEM_EQUIVALENTE_FATURAMENTO, 
		QUANTIDADE_DIETAS, 
		QUANTIDADE_DIETAS_OPCIONAL, 
		PRESCRICAO_LIVRE, 
		DTHR_MOVIMENTO_NULO, 
		BOMBA_INFUSAO_NAO_PERMITIDA_DIETA, 
		BOMBA_INFUSAO_NAO_PERMITIDA_NPT, 
		BOMBA_INFUSAO_NAO_PERMITIDA_MEDICAMENTO, 
		PREVISAO_ALTA_ANTERIOR_DATA_ATUAL,
		DIAGNOSTICOS_ATENDIMENTO,
		IMPRESSORA_NAO_CADASTRADA,
		ERRO_PARAMETRO_P_TMV_DOC_RM,
		MSG_ERRO_PRESCRICAO_MEDICA_DEVE_CONTER_AO_MENOS_UM_ITEM_DIETA,
		MENSAGEM_PRESCRICAO_MINIMO_ITEM,
		MPM_IME_CK8;
	}

	/**
	 * Método responsável por realizar a lógica relacionada com a ação de
	 * confirmar uma prescrição médica
	 * 
	 * 
	 * 
	 * @ORADB: mpmk_confirma.mpmp_confirma
	 * 
	 * 
	 * 
	 * 
	 * @param prescricao
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public ConfirmacaoPrescricaoVO confirmarPrescricaoMedica(MpmPrescricaoMedica prescricao, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		
		Boolean prescricaoAmbulatorial = getAghuFacade().obterAtendimento(prescricao.getId().getAtdSeq(),null,
				DominioOrigemAtendimento.getOrigensDePrescricaoAmbulatorial()) != null;
		
		MpmPrescricaoMedica prescricaoOld = new MpmPrescricaoMedica();
		
		prescricaoOld.setSituacao(prescricao.getSituacao());
		prescricaoOld.setServidorValida(prescricao.getServidorValida());
		
		if(prescricao.getDthrInicioMvtoPendente() == null) {
			prescricaoOld.setDthrInicioMvtoPendente(prescricao.getDthrMovimento());
		}else { 	
			prescricaoOld.setDthrInicioMvtoPendente(prescricao.getDthrInicioMvtoPendente());
		}
		
		if (prescricao.getDthrMovimento() == null) {
			throw new ApplicationBusinessException(
					ConfirmarPrescricaoMedicaExceptionCode.DTHR_MOVIMENTO_NULO);
		}

		if (prescricao.getSituacao() != DominioSituacaoPrescricao.U) {
			throw new ApplicationBusinessException(
					ConfirmarPrescricaoMedicaExceptionCode.PRESCRICAO_LIVRE);
		}

		Date dataTrabalho = null;

		if (prescricao.getDthrInicioMvtoPendente() != null) {
			dataTrabalho = prescricao.getDthrInicioMvtoPendente();
		} else {
			dataTrabalho = prescricao.getDthrMovimento();
		}

		ConfirmacaoPrescricaoVO confirmacaoPrescricaoVO  = new ConfirmacaoPrescricaoVO();
		confirmacaoPrescricaoVO.setDataMovimento(dataTrabalho);
		confirmacaoPrescricaoVO.setServidorValido(prescricao.getServidorValida());
		
		//Incluida regra para nao testar previsao de alta para unidades com caracteristica Control prev alta (Incidente - AGHU #51238)
		boolean possuiCaracteristicaPrevAlta = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(prescricao.getAtendimento().getUnidadeFuncional().getSeq(), 
				   ConstanteAghCaractUnidFuncionais.CONTROLA_PREVISAO_ALTA);

		
		Date dataPrevisaoAlta = null;
		if (prescricao.getAtendimento().getInternacao() != null) {
			dataPrevisaoAlta = prescricao.getAtendimento().getInternacao()
					.getDtPrevAlta();

			// Verifica previsão de alta
			if (!possuiCaracteristicaPrevAlta && dataPrevisaoAlta != null
					&& dataPrevisaoAlta.before(DateUtil.truncaData(new Date()))) {
				throw new ApplicationBusinessException(
						ConfirmarPrescricaoMedicaExceptionCode.PREVISAO_ALTA_ANTERIOR_DATA_ATUAL);
			}
		}

		validarQuantidadeItensPrescricao(prescricao.getId(), prescricao.getDthrFim(), false);
		
		if(!prescricaoAmbulatorial){
			// verifica diagnóstico do atendimento
			Long numDiagnosticosAtendimento = this.getCidAtendimentoDAO()
					.contarDiagnosticosAtendimento(prescricao.getAtendimento());
			if (numDiagnosticosAtendimento == 0) {
				throw new ApplicationBusinessException(
						ConfirmarPrescricaoMedicaExceptionCode.DIAGNOSTICOS_ATENDIMENTO);
	
			}
			
			// verifica quantidade de dietas.
			List<MpmPrescricaoDieta> dietas = this.getPrescricaoDietaDAO().buscaDietaPorPrescricaoMedica(prescricao.getId(),prescricao.getDthrFim(), false);

			boolean possuiCaracteristica = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(prescricao.getAtendimento().getUnidadeFuncional().getSeq(), 
																							   ConstanteAghCaractUnidFuncionais.DIETA_OPCIONAL_ATEND_URGENCIA);
			
			if (prescricao.getAtendimento().getOrigem() == DominioOrigemAtendimento.U && possuiCaracteristica) {

				if ((dietas.size() > 1) && !permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "altaPacientes_Enfermeiro_Obstetra", "pesquisar")) {
					throw new ApplicationBusinessException(
							ConfirmarPrescricaoMedicaExceptionCode.QUANTIDADE_DIETAS_OPCIONAL);
				}

			} else {
				if ((dietas.size() != 1) && !permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "altaPacientes_Enfermeiro_Obstetra", "pesquisar") ) {
					throw new ApplicationBusinessException(
							ConfirmarPrescricaoMedicaExceptionCode.QUANTIDADE_DIETAS);
				}
			}
		}
		

		LOG.info("Chamando validação para bomba de infusão da prescrição: "
				+ prescricao.getId().getSeq());
		this.validarBombaDeInfusao(prescricao);
		
		LOG.info(
				"Calculando quantidade medicamentos para 24 horas para a prescrição: "
						+ prescricao.getId().getSeq());
		this.calcularQuantidade24Horas(prescricao,dataTrabalho);
		
		LOG.info(
				"Chamando confirmação itens da prescrição: "
						+ prescricao.getId().getSeq());
		this.confirmarItensPrescricao(prescricao, dataTrabalho, nomeMicrocomputador, confirmacaoPrescricaoVO);
		
		LOG.info(
				"Chamando atualização do atendimento paciente Cpa da prescrição: "
						+ prescricao.getId().getSeq());
		this.atualizarAtendimentoPacienteCpa(prescricao, nomeMicrocomputador, dataFimVinculoServidor);		
		

		LOG.info("Retirando de uso a prescrição: "
						+ prescricao.getId().getSeq());

		//Guarda atributos da prescrição que serão alterados quando ela for tirada de uso
		Date dthrMovimentoBackup = prescricao.getDthrMovimento();
		Date dthrInicioMvtoPendenteBackup = prescricao.getDthrInicioMvtoPendente();
		DominioSituacaoPrescricao situacaoBackup = prescricao.getSituacao();
		RapServidores servidorValidacaoBackup = prescricao.getServidorValida();
		String machineBackup = prescricao.getMachine();
		this.tirarPrescricaoDeUso(prescricao, nomeMicrocomputador);
		
			try{
				getPrescricaoMedicaRN().atualizarFaturamento( prescricao, prescricaoOld, DominioOperacaoBanco.UPD, nomeMicrocomputador, new Date());//DominioOperacaoBanco.INS
			}catch(ApplicationBusinessException e){
				//Retorna os valores antigos
				prescricao.setDthrMovimento(dthrMovimentoBackup);
				prescricao.setDthrInicioMvtoPendente(dthrInicioMvtoPendenteBackup);
				prescricao.setSituacao(situacaoBackup);
				prescricao.setServidorValida(servidorValidacaoBackup);
				prescricao.setMachine(machineBackup);
				throw e;
			}

		LOG.info(
				"Chamando confirmação da justificativa da prescrição: "
						+ prescricao.getId().getSeq());
		this.confirmarJustificativa(prescricao, dataTrabalho);

		LOG.info(
				"Chamando confirmação do diagnóstico da prescrição: "
						+ prescricao.getId().getSeq());
		this.confirmarDiagnostico(prescricao);
		
		//#43252
		confirmarPrescricaoNpt(prescricao, nomeMicrocomputador);
		LOG.info("Chamando confirmação do prescricao NPT da prescrição: " + prescricao.getId().getSeq());
		return confirmacaoPrescricaoVO;
	}

	protected void validarQuantidadeItensPrescricao(MpmPrescricaoMedicaId id, Date dthrFim, Boolean listarTodas) throws ApplicationBusinessException {
		List<ItemPrescricaoMedicaVO> itens = getManterPrescricaoMedicaON().buscarItensPrescricaoMedica(id, listarTodas);
		if(itens == null || itens.isEmpty()){
			throw new ApplicationBusinessException(
					ConfirmarPrescricaoMedicaExceptionCode.MENSAGEM_PRESCRICAO_MINIMO_ITEM);
		}
	}
	
	/**
	 * @ORADB MPMP_CONF_CALC_QTDE
	 * @param prescricao
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void calcularQuantidade24Horas(MpmPrescricaoMedica prescricao, Date dataTrabalho) throws ApplicationBusinessException {
		List<MpmPrescricaoMdto> prescricoesMedicamentos = getPrescricaoMdtoDAO()
				.pesquisarMedicamentosPrescricaoCalculoQuantidade(prescricao,dataTrabalho);		
		
		
		AghParametros unidadeMl = this.getParametroFacade().obterAghParametro(AghuParametrosEnum.P_UNIDADE_ML);	
		
		for (MpmPrescricaoMdto prescricaoMedicamento : prescricoesMedicamentos){		
			
			BigDecimal volumeSolucao = BigDecimal.ZERO;
			
			BigDecimal volume24Horas = null;
			
			Integer volume24HorasAjustado = null;
			
			Map<MpmItemPrescricaoMdto, Integer> mapaCalculoSolucao = null;
			
			Map<MpmItemPrescricaoMdto, BigDecimal> mapaDoseMl = null;
			
			Map<MpmItemPrescricaoMdto, AfaFormaDosagem> mapaformaDosagem = null;
			
			if (prescricaoMedicamento.getTipoFreqAprazamento().getIndFormaAprazamento()
					.equals(DominioFormaCalculoAprazamento.C)) {
				
				//TODO: chamar fachada?
				if (prescricaoMedicamento.getGotejo() != null
						&& prescricaoMedicamento.getTipoVelocAdministracao() != null) {
					volume24Horas = prescricaoMedicamento
							.getGotejo()
							.multiply(
									prescricaoMedicamento
											.getTipoVelocAdministracao()
											.getFatorConversaoMlh())
							.multiply(BigDecimal.valueOf(24));
				}
				
			}else if (prescricaoMedicamento.getTipoFreqAprazamento().getIndFormaAprazamento()
					.equals(DominioFormaCalculoAprazamento.I)){
				
				//TODO: chamar fachada?
				if (prescricaoMedicamento.getGotejo() != null
						&& prescricaoMedicamento.getTipoVelocAdministracao() != null
						&& prescricaoMedicamento.getFrequencia() != null) {
					volume24Horas = prescricaoMedicamento
							.getGotejo()
							.multiply(
									prescricaoMedicamento
											.getTipoVelocAdministracao()
											.getFatorConversaoMlh())
							.multiply(BigDecimal.valueOf(24))
							.divide(BigDecimal.valueOf(24).multiply(
									new BigDecimal(prescricaoMedicamento
											.getFrequencia())
											.multiply(prescricaoMedicamento
													.getTipoFreqAprazamento()
													.getFatorConversaoHoras())),10,RoundingMode.HALF_EVEN);
				}
				
			}else if (prescricaoMedicamento.getTipoFreqAprazamento().getIndFormaAprazamento()
					.equals(DominioFormaCalculoAprazamento.V)){
				Long quantidade = null;
				if (prescricaoMedicamento.getTipoFreqAprazamento().getFatorConversaoHoras().compareTo(BigDecimal.ZERO) > 0){
					if (prescricaoMedicamento.getFrequencia() != null) {
						MathContext mc = new MathContext(1, RoundingMode.UP);	
						quantidade = BigDecimal 
								.valueOf(24)
								.multiply(
										new BigDecimal(prescricaoMedicamento
												.getFrequencia())
												.divide(prescricaoMedicamento
														.getTipoFreqAprazamento()
														.getFatorConversaoHoras(),10,RoundingMode.HALF_EVEN),mc)
								.longValue();
					}
				} else{
					quantidade = (long) prescricaoMedicamento.getTipoFreqAprazamento().getAprazamentoFrequencias().size();
				}
				if (quantidade != null && quantidade > 0){
					if (prescricaoMedicamento.getGotejo() != null
							&& prescricaoMedicamento
									.getTipoVelocAdministracao() != null) {
						volume24Horas = prescricaoMedicamento
								.getGotejo()
								.multiply(
										prescricaoMedicamento
												.getTipoVelocAdministracao()
												.getFatorConversaoMlh())
								.multiply(
										BigDecimal.valueOf(24).divide(
												new BigDecimal(quantidade),10,RoundingMode.HALF_EVEN));
					}
				}else{
					volume24Horas = BigDecimal.ZERO;
				}
				
			}
			
			if (volume24Horas != null) {
				volume24HorasAjustado = volume24Horas.intValue() + 1;
			}
			
			
			mapaCalculoSolucao = new HashMap<MpmItemPrescricaoMdto, Integer>();
			
			mapaDoseMl = new HashMap<MpmItemPrescricaoMdto, BigDecimal>();
			
			mapaformaDosagem = new HashMap<MpmItemPrescricaoMdto, AfaFormaDosagem>();
			
			// itera pelos itens da prescrição do medicamento
			for (MpmItemPrescricaoMdto itemPrescricaoMedicamento: prescricaoMedicamento.getItensPrescricaoMdtos()){
				
				if (prescricaoMedicamento.getGotejo() == null){
					if (itemPrescricaoMedicamento.getMedicamento().getIndGeraDispensacao()){
						// ime_calc_soluc = 0 -- faz o cálculo no modo antigo (chama function usando os dados informados pelo usuário)
						mapaCalculoSolucao.put(itemPrescricaoMedicamento, 0);
					}else{
						// ime_calc_soluc = 3 -- por não ter gotejo e é um diluente move zero (0)
						mapaCalculoSolucao.put(itemPrescricaoMedicamento, 3);
					}
				}else{
					if (itemPrescricaoMedicamento.getFormaDosagem().getUnidadeMedidaMedicas()!= null && itemPrescricaoMedicamento.getFormaDosagem().getUnidadeMedidaMedicas().getSeq().equals(unidadeMl.getVlrNumerico().intValue())){
         		
						mapaformaDosagem.put(itemPrescricaoMedicamento, itemPrescricaoMedicamento.getFormaDosagem());
						mapaDoseMl.put(itemPrescricaoMedicamento, itemPrescricaoMedicamento.getDose());
						volumeSolucao =  volumeSolucao.add(itemPrescricaoMedicamento.getDose());
						
						if (itemPrescricaoMedicamento.getMedicamento().getIndGeraDispensacao()){							
							if (volume24HorasAjustado != null && volume24HorasAjustado.equals(0)){
								// ime_calc_soluc = 0 -- faz o cálculo no modo antigo (chama function usando os dados informados pelo usuário)
								mapaCalculoSolucao.put(itemPrescricaoMedicamento, 0);
							}else{
								// ime_calc_soluc = 1 -- calcula usando levando em conta a questão da solução
								mapaCalculoSolucao.put(itemPrescricaoMedicamento, 1);
								
							}
						}else{
							if (volume24HorasAjustado != null && volume24HorasAjustado.equals(0)){
								// ime_calc_soluc = 3 -- por não ter gotejo e é um diluente move zero (0)
								mapaCalculoSolucao.put(itemPrescricaoMedicamento, 3);
							}else{
							//ime_calc_soluc = 2 -- calcula igual ao número 1 só que zera por ser um diluente
								mapaCalculoSolucao.put(itemPrescricaoMedicamento, 2);
							}
						}						
					}else{
						//TODO: chamar fachada?
						for (AfaFormaDosagem formaDosagem : itemPrescricaoMedicamento.getMedicamento().getAfaFormaDosagens()){
							if (formaDosagem.getIndSituacao().equals(DominioSituacao.A) && formaDosagem.getUnidadeMedidaMedicas() != null &&formaDosagem.getUnidadeMedidaMedicas().getSeq().equals(unidadeMl.getVlrNumerico().intValue())){
								if (itemPrescricaoMedicamento.getMedicamento().getIndGeraDispensacao()){
									if (volume24HorasAjustado != null && volume24HorasAjustado.equals(0)){
										// ime_calc_soluc = 0 -- faz o cálculo no modo antigo (chama function usando os dados informados pelo usuário)
										mapaCalculoSolucao.put(itemPrescricaoMedicamento, 0);
									}else{
										// ime_calc_soluc = 1 -- calcula usando levando em conta a questão da solução
										mapaCalculoSolucao.put(itemPrescricaoMedicamento, 1);
									}						
								}else{
									if (volume24HorasAjustado != null && volume24HorasAjustado.equals(0)){
										// ime_calc_soluc = 3 -- por não ter gotejo e é um diluente move zero (0)
										mapaCalculoSolucao.put(itemPrescricaoMedicamento, 3);
									}else{
										//ime_calc_soluc = 2 -- calcula igual ao número 1 só que zera por ser um diluente
										mapaCalculoSolucao.put(itemPrescricaoMedicamento, 2);
									}
								}
								
//								 
								
								mapaformaDosagem.put(itemPrescricaoMedicamento, formaDosagem);
								
								BigDecimal dose_ml = itemPrescricaoMedicamento
								.getDose()
								.divide(itemPrescricaoMedicamento
										.getFormaDosagem()
										.getFatorConversaoUp(),new MathContext(10,RoundingMode.HALF_EVEN))
								.multiply(
										formaDosagem
												.getFatorConversaoUp());
								
								mapaDoseMl.put(itemPrescricaoMedicamento, dose_ml);
								
								volumeSolucao = volumeSolucao
										.add(dose_ml);								
								
								
							}
						}
						
					}				
			
				}		
			}
			
			for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : prescricaoMedicamento
					.getItensPrescricaoMdtos()) {
				if (mapaCalculoSolucao.get(itemPrescricaoMedicamento) != null) {

					if (mapaCalculoSolucao.get(itemPrescricaoMedicamento)
							.equals(3)
							|| mapaCalculoSolucao
									.get(itemPrescricaoMedicamento).equals(2)) {
						itemPrescricaoMedicamento.setQtdeCalcSist24h((short) 0);
					} else if (mapaCalculoSolucao
							.get(itemPrescricaoMedicamento).equals(1)) {

						BigDecimal doseSolucao = BigDecimal.ZERO;
						
						
						
						if (volume24HorasAjustado != null) {
							BigDecimal volume24HorasBigDecimal = new BigDecimal(volume24HorasAjustado);
							doseSolucao = volume24HorasBigDecimal.multiply(mapaDoseMl
									.get(itemPrescricaoMedicamento).divide(
											volumeSolucao,10,RoundingMode.HALF_EVEN));
						}

						AfaFormaDosagem formadosagem = mapaformaDosagem
								.get(itemPrescricaoMedicamento);

						itemPrescricaoMedicamento.setQtdeCalcSist24h(this
								.calcularValor24Horas(prescricaoMedicamento
										.getFrequencia(), prescricaoMedicamento
										.getTipoFreqAprazamento(), doseSolucao,
										formadosagem, itemPrescricaoMedicamento
												.getMedicamento()));
					} else { // calculoSolucao == 0
						itemPrescricaoMedicamento.setQtdeCalcSist24h(this
								.calcularValor24Horas(prescricaoMedicamento
										.getFrequencia(), prescricaoMedicamento
										.getTipoFreqAprazamento(),
										itemPrescricaoMedicamento.getDose(),
										itemPrescricaoMedicamento
												.getFormaDosagem(),
										itemPrescricaoMedicamento
												.getMedicamento()));
					}
				}
				if(itemPrescricaoMedicamento.getQtdeCalcSist24h() != null && itemPrescricaoMedicamento.getQtdeCalcSist24h() < 0) {
					throw new ApplicationBusinessException(
							ConfirmarPrescricaoMedicaExceptionCode.MPM_IME_CK8, itemPrescricaoMedicamento.getMedicamento().getDescricao());
				}
			}
		}// end for prescricao medicamento
	}


	/**
	 * @ORADB MPMC_CALC_QTDE_24H
	 * @param frequencia
	 * @param tipoFrequenciaAprazamento
	 * @param dosePrescrita
	 * @param formaDosagem
	 * @param medicamento
	 * @return
	 */
	private Short calcularValor24Horas(Short frequencia,
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento,
			BigDecimal dosePrescrita, AfaFormaDosagem formaDosagem,
			AfaMedicamento medicamento) throws ApplicationBusinessException {
		
		BigDecimal doseUpPrescrita = dosePrescrita.divide(formaDosagem.getFatorConversaoUp(), 10,RoundingMode.FLOOR);
		int quantidade = 0;
		BigDecimal dose24h = null;
		
		if (! medicamento.getIndCalcDispensacaoFracionad()){	
			doseUpPrescrita =	doseUpPrescrita.setScale(0, RoundingMode.UP);
		}
		
		if (tipoFrequenciaAprazamento.getIndFormaAprazamento().equals(DominioFormaCalculoAprazamento.C)){			
			dose24h = doseUpPrescrita.setScale(0, RoundingMode.UP);			
			return dose24h.shortValue();
		}
		if (tipoFrequenciaAprazamento.getIndFormaAprazamento().equals(DominioFormaCalculoAprazamento.I)){
			BigDecimal vezes = tipoFrequenciaAprazamento.getFatorConversaoHoras().multiply(new BigDecimal(frequencia));
			if (vezes.compareTo(BigDecimal.ONE) <  0){
				return 0;
			}
			quantidade = new BigDecimal(24).divide(vezes,10, RoundingMode.FLOOR).setScale(0, RoundingMode.FLOOR).intValue();
		}
		if (tipoFrequenciaAprazamento.getIndFormaAprazamento().equals(DominioFormaCalculoAprazamento.V)){
			if (tipoFrequenciaAprazamento.getFatorConversaoHoras().compareTo(BigDecimal.ZERO) > 0){
				quantidade = 24 * frequencia;
				quantidade = new BigDecimal(quantidade).divide(tipoFrequenciaAprazamento
								.getFatorConversaoHoras(), 10,RoundingMode.FLOOR).setScale(0, RoundingMode.FLOOR).intValue();
			}else{
				quantidade = tipoFrequenciaAprazamento.getAprazamentoFrequencias().size();
			}
		}
		
		dose24h = doseUpPrescrita.multiply(new BigDecimal(quantidade));			
		
		dose24h = dose24h.setScale(0, RoundingMode.UP);		
		return dose24h.shortValue();
		  
	}


	/**
	 * Método que atualiza campos específicos da Solicitação de Consultoria
	 * 
	 * @param prescricao
	 * @param dataTrabalho
	 */
	private void atualizarSituacaoConsultoria(ItemPrescricaoMedica item) {

		if (item instanceof MpmSolicitacaoConsultoria) {
			MpmSolicitacaoConsultoria solicitacaoConsultoria = (MpmSolicitacaoConsultoria) item;
			if (solicitacaoConsultoria.getIndPendente().equals(
					DominioIndPendenteItemPrescricao.N)) {
				if (item.getDthrFim() == null) {
					solicitacaoConsultoria.setIndSituacao(DominioSituacao.A);
				} else {
					solicitacaoConsultoria.setIndSituacao(DominioSituacao.I);
				}
			}
		}

	}

	/**
	 * Método que valida se uma bomba de infusão está sendo usada indevidamente
	 * em uma unidade funcional.
	 * 
	 * @param prescricao
	 * @param dtHrTrabalho
	 * @throws ApplicationBusinessException
	 */
	private void validarBombaDeInfusao(MpmPrescricaoMedica prescricao) throws ApplicationBusinessException {
		
		boolean possuiCaracteristica = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(prescricao.getAtendimento().getUnidadeFuncional().getSeq(), 
				ConstanteAghCaractUnidFuncionais.PERMITE_PRESCRICAO_BI);
		
		if (!possuiCaracteristica) {

			List<MpmPrescricaoDieta> dietas = this.getPrescricaoDietaDAO()
					.buscaDietaPorPrescricaoMedica(prescricao.getId(),
							prescricao.getDthrFim(), false);

			for (MpmPrescricaoDieta dieta : dietas) {
				if (dieta.getIndBombaInfusao()) {
					throw new ApplicationBusinessException(
							ConfirmarPrescricaoMedicaExceptionCode.BOMBA_INFUSAO_NAO_PERMITIDA_DIETA);
				}
			}

			List<MpmPrescricaoMdto> listaMedicamentos = this
					.getPrescricaoMdtoDAO()
					.obterListaMedicamentosPrescritosPelaChavePrescricao(
							prescricao.getId(), prescricao.getDthrFim());

			boolean bombaInfusaoMedicamento = false;
			StringBuffer nomesMedicamentos = new StringBuffer();
			for (MpmPrescricaoMdto medicamento : listaMedicamentos) {
				if (medicamento.getIndBombaInfusao()) {
					bombaInfusaoMedicamento = true;
					nomesMedicamentos
						.append(medicamento.getDescricaoFormatada()).append(", ");
				}
			}

			if (bombaInfusaoMedicamento) {
				throw new ApplicationBusinessException(
						ConfirmarPrescricaoMedicaExceptionCode.BOMBA_INFUSAO_NAO_PERMITIDA_MEDICAMENTO,
						nomesMedicamentos.toString());
			}

			List<MpmPrescricaoNpt> listaNpts = this.getMpmPrescricaoNptDAO()
					.pesquisarPrescricaoNptPorPME(prescricao.getId(),
							prescricao.getDthrFim(), false);

			for (MpmPrescricaoNpt npt : listaNpts) {
				if (npt.getBombaInfusao()) {
					throw new ApplicationBusinessException(
							ConfirmarPrescricaoMedicaExceptionCode.BOMBA_INFUSAO_NAO_PERMITIDA_NPT);
				}
			}

		}

	}

	private void confirmarItensPrescricao(MpmPrescricaoMedica prescricao,
			Date dataTrabalho, String nomeMicrocomputador,ConfirmacaoPrescricaoVO confirmacaoPrescricaoVO) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		List<ItemPrescricaoMedica> itensConfirmados = getPrescricaoMedicaDAO()
				.listarItensPrescricaoMedicaConfirmacao(prescricao,
						dataTrabalho);
		
		Boolean requisitarMaterial;
		final Date dataFimVinculoServidor = new Date();
		Boolean gerouProvaCruzada = false;
		for (ItemPrescricaoMedica item : itensConfirmados) {
			super.refresh(item);
 			LOG.info(
					"Confirmando item: " + item.getDescricaoFormatada());
 			requisitarMaterial = false;
 			
 			if (item instanceof AbsSolicitacoesHemoterapicas) {
 				this.confirmarSituacaoHemoterapia(item.getPrescricaoMedica().getAtendimento().getSeq(), dataTrabalho, 
 						item.getPrescricaoMedica().getDthrInicio(), item.getPrescricaoMedica().getDthrFim()); 				
 			}
			
			switch (item.getIndPendente()) {
			case B:
				item.setServidorValidacao(servidorLogado);
				item.setDthrValida(new Date());
				if(item instanceof MpmPrescricaoProcedimento) {
					requisitarMaterial = true;
				}
				break;
			case R:
				item.setServidorValidacao(servidorLogado);
				item.setDthrValida(new Date());
				break;

			case P:
//				this.getPrescricaoMedicaDAO().refresh(item);

				if (item.getAnterior() != null) {
					super.refresh(item.getAnterior());
					item.getAnterior().setIndPendente(
							DominioIndPendenteItemPrescricao.N);
					item.getAnterior().setServidorValidaMovimentacao(
							servidorLogado);
					item.getAnterior().setDthrValidaMovimentacao(new Date());

				}
				item.setServidorValidacao(servidorLogado);
				item.setDthrValida(new Date());
				if(item instanceof MpmPrescricaoProcedimento) {
					requisitarMaterial = true;
				}
				break;

			case E:
				item.setServidorValidaMovimentacao(servidorLogado);
				item.setDthrValidaMovimentacao(new Date());
				break;

			case Y:
				item.setServidorValidaMovimentacao(servidorLogado);
				item.setDthrValidaMovimentacao(new Date());
				item.setServidorValidacao(servidorLogado);
				item.setDthrValida(new Date());
				break;

			default:
				break;
			}

			gerouProvaCruzada = this.confirmarHemoterapiaPrescricaoMedica(item, gerouProvaCruzada, dataTrabalho, nomeMicrocomputador);
			
			item.setIndPendente(DominioIndPendenteItemPrescricao.N);

			LOG.info("Marcou o item como coNfirmado");

			this.atualizarSituacaoConsultoria(item);
			if (item.getAnterior() != null) {
				this.atualizarSituacaoConsultoria(item.getAnterior());
			}

			this.atualizarProcedimento(item, nomeMicrocomputador, dataFimVinculoServidor);
			if(requisitarMaterial) {
				Integer atdSeq = null;
				if (item.getPrescricaoMedica().getAtendimento() == null) {
					LOG.warn("Prescrição sem Atendimento");
				} else {
					atdSeq = item.getPrescricaoMedica().getAtendimento().getSeq(); 
				}
				
				Short pedSeq = null;
				if (((MpmPrescricaoProcedimento)item).getProcedimentoEspecialDiverso() == null) {
					LOG.warn("Prescrição sem Procedimento Especial Diverso");
				} else {
					pedSeq = ((MpmPrescricaoProcedimento)item).getProcedimentoEspecialDiverso().getSeq(); 
				}
				
				requisitarMaterial(atdSeq, pedSeq, ((MpmPrescricaoProcedimento)item).getDigitacaoSolicitante());
			}
			
			if (item instanceof MpmPrescricaoMdto) {
				((MpmPrescricaoMdto)item).getItensPrescricaoMdtos();
			}
			
			
			
			item.possuiFilhos();
		}
		
		confirmacaoPrescricaoVO.setItensConfirmados(itensConfirmados);
	}
	
	private Boolean confirmarHemoterapiaPrescricaoMedica(ItemPrescricaoMedica item, Boolean gerouProvaCruzada, Date dataTrabalho,
			String nomeMicrocomputador) throws BaseException {
		if (item instanceof AbsSolicitacoesHemoterapicas) {
			if(Boolean.FALSE.equals(gerouProvaCruzada)) {
			AbsSolicitacoesHemoterapicas hemo = (AbsSolicitacoesHemoterapicas)item;
			
			gerouProvaCruzada = examesFacade.gerarPCT(hemo.getId().getAtdSeq(), 
			hemo.getIndUrgente(), hemo.getIndSituacaoColeta(), hemo.getId().getSeq(), nomeMicrocomputador, 
			getServidorLogadoFacade().obterServidorLogado(), hemo.getServidor());		
			}
		}
		return gerouProvaCruzada;
	}
	
	/**
	* @ORADB RMSP_GERA_RM_PRCS 
	*  */
	public void procedurerNRmspGeraRmPrcs(Integer tAtdSeq, Short tPedSeq, final Integer p1, final Integer pQtdeRequisitada, String vObservacao,
												AghAtendimentos aghAtendimento, RapServidores servidorLogado) throws ApplicationBusinessException {
		
		String vNomeImpressora = "";
		AghParametros aghParametro;
		SceTipoMovimento vTmvComplemento;
		Integer vRmsSeq   = 0;
		int vQtdeRms  = 0;
		Integer vGmtCodigo = 0;
		Integer vGmtCodigoAnt = 0;
		SceReqMaterial sceReqMaterial = new SceReqMaterial();
		String vGerouRm = "N";
		
		List<AghImpressoraPadraoUnids> aghImpressoraPadraoUnids = new ArrayList<AghImpressoraPadraoUnids>();
		List<SceEstoqueAlmoxarifado> listaSceEstoqueAlmoxarifado = new ArrayList<SceEstoqueAlmoxarifado>();
		
		
		//C2
		aghImpressoraPadraoUnids = prescricaoMedicaFacade.buscarImpressoraDestinoRM(TipoDocumentoImpressao.RM_PEDIDO_PRCR);
				
		
		if(!aghImpressoraPadraoUnids.isEmpty()){
			vNomeImpressora = aghImpressoraPadraoUnids.get(0).getNomeImpressora();
		}else{
			throw new ApplicationBusinessException(ConfirmarPrescricaoMedicaExceptionCode.IMPRESSORA_NAO_CADASTRADA);
		}
		
		try {
			aghParametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_RM);
			vTmvComplemento = sceTipoMovimentosDAO.obterSceTipoMovimentosAtivoPorSeq(aghParametro.getVlrNumerico().shortValue());
			//C3
			listaSceEstoqueAlmoxarifado = prescricaoMedicaFacade.buscarsceEstoqueAlmoxarifado(tPedSeq, p1);
			if(!listaSceEstoqueAlmoxarifado.isEmpty()){
				for (SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado : listaSceEstoqueAlmoxarifado) {
					vGmtCodigo = sceEstoqueAlmoxarifado.getMaterial().getGrupoMaterial().getCodigo();
					if(!vGmtCodigo.equals(vGmtCodigoAnt)){
						if(vRmsSeq > 0){
							sceReqMaterial = sceReqMateriaisDAO.buscarSceReqMateriaisPorId(vRmsSeq);
							sceReqMaterial.setIndSituacao(DominioSituacaoRequisicaoMaterial.C);
							sceReqMateriaisDAO.atualizar(sceReqMaterial);
						}
						if(vGerouRm.equals("N")){
							sceReqMaterial.setAlmoxarifado(sceEstoqueAlmoxarifado.getAlmoxarifado());
							sceReqMaterial.setCentroCusto(aghAtendimento.getUnidadeFuncional().getCentroCusto());
							sceReqMaterial.setCentroCustoAplica(aghAtendimento.getUnidadeFuncional().getCentroCusto());
							sceReqMaterial.setGrupoMaterial(sceEstoqueAlmoxarifado.getMaterial().getGrupoMaterial());
							sceReqMaterial.setTipoMovimento(vTmvComplemento);
							sceReqMaterial.setDtGeracao(DateUtil.truncaData(new Date()));
							sceReqMaterial.setIndSituacao(DominioSituacaoRequisicaoMaterial.G);
							sceReqMaterial.setEstorno(Boolean.FALSE);
							sceReqMaterial.setIndImpresso(DominioImpresso.N);
							sceReqMaterial.setNomeImpressora(vNomeImpressora);
							sceReqMaterial.setObservacao(vObservacao);
							
							sceReqMaterial.setServidor(servidorLogado);
							
							sceReqMateriaisDAO.persistir(sceReqMaterial);
							sceReqMateriaisDAO.flush();
							
							vGerouRm = "S";
							vGmtCodigoAnt = vGmtCodigo;
							vQtdeRms = vQtdeRms + 1;
						}
					}
					
					SceItemRms sceItemRms = new SceItemRms();
					SceItemRmsId sceItemRmsId = new SceItemRmsId();
					
					sceItemRmsId.setRmsSeq(sceReqMaterial.getSeq());
					sceItemRmsId.setEalSeq(sceEstoqueAlmoxarifado.getSeq());
					sceItemRms.setId(sceItemRmsId);
					sceItemRms.setScoUnidadeMedida(sceEstoqueAlmoxarifado.getUnidadeMedida());
					sceItemRms.setQtdeRequisitada(pQtdeRequisitada);
					sceItemRms.setIndTemEstoque(Boolean.TRUE);
					
					sceItemRmsDAO.persistir(sceItemRms);
				}
				
				if(vQtdeRms > 0){
					SceReqMaterial sceReqMaterial2 = new SceReqMaterial();
					sceReqMaterial2 = sceReqMateriaisDAO.buscarSceReqMateriaisPorId(sceReqMaterial.getSeq());
					sceReqMaterial2.setIndSituacao(DominioSituacaoRequisicaoMaterial.C);
					sceReqMateriaisDAO.atualizar(sceReqMaterial2);
				}
				
			}
			
		} catch (ApplicationBusinessException e) {
			new ApplicationBusinessException(ConfirmarPrescricaoMedicaExceptionCode.ERRO_PARAMETRO_P_TMV_DOC_RM);
		}
		
	}
	
	

	private void atualizarProcedimento(ItemPrescricaoMedica item, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		if (item instanceof MpmPrescricaoProcedimento) {
			this.getPrescreverProcedimentoEspecialRN()
					.validarAlteracaoProcedimento(
							(MpmPrescricaoProcedimento) item, nomeMicrocomputador, dataFimVinculoServidor);
		}
	}

	/**
	 * @ORADB mpmk_confirma.mpmp_requis_material
	 */
	private void requisitarMaterial(Integer atdSeq, Short pedSeq, Boolean digitacaoSolicitante) throws ApplicationBusinessException {
		StringBuffer observacao = new StringBuffer();
		if(Boolean.TRUE.equals(digitacaoSolicitante) && atdSeq != null) {
			List<MpmProcedEspecialRm> procedimentos = getMpmProcedEspecialRmDAO().listarProcedimentosRmAtivosPeloPedSeq(pedSeq);
			if(procedimentos != null && !procedimentos.isEmpty()) {
				AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdSeq);
				observacao.append("PAC."+atendimento.getProntuario() + " " + atendimento.getPaciente().getNome() + " " + StringUtils.substring(getManterPrescricaoMedicaRN().buscarResumoLocalPaciente(atendimento), 0, 6));
				observacao.append(" CIDS:");
				List<MpmCidAtendimento> cids = getCidAtendimentoDAO().listar(atendimento);
				for(MpmCidAtendimento cid : cids) {
					if(observacao.length() < 114) {
						observacao.append(' ').append(cid.getCid().getCodigo());
					}
					else {
						break;
					}
				}
				RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
				
				procedurerNRmspGeraRmPrcs(atdSeq,pedSeq,0,1,observacao.toString(), atendimento, servidorLogado);
//				getObjetosOracleDAO().gerarRmProcedimento(atdSeq, pedSeq, observacao.toString(), servidorLogado.getUsuario());
			}
		}
		
	}
	
	/**
	 * @ORADB MPMP_CONF_SIT_HEMOT
	 * @param atdSeq
	 * @param dtHrWork
	 * @param dtHrInicio
	 * @param dtHrFim
	 * @throws BaseException
	 */
	private void confirmarSituacaoHemoterapia(Integer atdSeq, Date dataTrabalho, Date dtHrInicio, Date dtHrFim) throws BaseException {
	
		AghParametros pMccCancelaSolic = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_MCC_CANCELA_SOLICITANTE);
		AbsMotivosCancelaColetas motivoCancelaColeta = this.absMotivosCancelaColetasDAO.obterAbsMotivosCancelaColetas(pMccCancelaSolic.getVlrNumerico().shortValue());

		List<SolHemoVO> solicitacoesHemoterapicas = this.absSolicitacoesHemoterapicasDAO.
				buscarSolicitacoesSeqPorAtendimentoPeriodo(atdSeq, dataTrabalho, dtHrInicio, dtHrFim);

		for (SolHemoVO VO : solicitacoesHemoterapicas) {
			AbsSolicitacoesHemoterapicasId SolId = new AbsSolicitacoesHemoterapicasId();
			SolId.setAtdSeq(atdSeq);
			SolId.setSeq(VO.getSeq());
			AbsSolicitacoesHemoterapicas absSolicitacoesHemoterapicas = absSolicitacoesHemoterapicasDAO.obterPorChavePrimaria(SolId);
			if (DominioIndPendenteItemPrescricao.P.equals(absSolicitacoesHemoterapicas.getIndPendente())) {
				
				Short unfSeq = this.examesFacade.mbcLocalPacMbc(atdSeq);
				SitucaoSolicitacaoHemoterapicaVO situacaoSolicHemotVO = 
						this.verificarSituacaoSolicitacaoHemoterapica(absSolicitacoesHemoterapicas.getPrescricaoMedica()
								.getAtendimento().getSeq(), absSolicitacoesHemoterapicas.getId().getSeq(), 
								unfSeq, absSolicitacoesHemoterapicas.getIndUrgente());
				
				if (absSolicitacoesHemoterapicas.getSolicitacaoHemoterapica() != null 
						&& absSolicitacoesHemoterapicas.getSolicitacaoHemoterapica().getPrescricaoMedica().getAtendimento().getSeq() != null
						&& absSolicitacoesHemoterapicas.getSolicitacaoHemoterapica().getId().getSeq() != null
						&& (DominioSituacaoColeta.P.equals(situacaoSolicHemotVO.getIndSituacaoColeta()) 
								|| (DominioSituacaoColeta.E.equals(situacaoSolicHemotVO.getIndSituacaoColeta()) && 
										DominioResponsavelColeta.S.equals(situacaoSolicHemotVO.getIndResponsavelColeta())))) {
					
					List<AbsSolicitacoesHemoterapicas> solicitacoes = this.absSolicitacoesHemoterapicasDAO
							.buscarSolicitacoesConfirmarHemoterapia(absSolicitacoesHemoterapicas.getSolicitacaoHemoterapica());
					for (AbsSolicitacoesHemoterapicas absSolicitacoesHemoterapicas2 : solicitacoes) {
						absSolicitacoesHemoterapicas2.setIndSituacaoColeta(DominioSituacaoColeta.C);
						absSolicitacoesHemoterapicas2.setMotivoCancelaColeta(motivoCancelaColeta);
						this.bancoDeSangueFacade.atualizarSolicitacaoHemoterapica(absSolicitacoesHemoterapicas, this.obterLoginUsuarioLogado());
					}
				}
				
				absSolicitacoesHemoterapicas.setIndResponsavelColeta(situacaoSolicHemotVO.getIndResponsavelColeta());
				absSolicitacoesHemoterapicas.setIndSituacaoColeta(situacaoSolicHemotVO.getIndSituacaoColeta());
				this.bancoDeSangueFacade.atualizarSolicitacaoHemoterapica(absSolicitacoesHemoterapicas, this.obterLoginUsuarioLogado());				
				
				if (DominioSituacaoColeta.D.equals(situacaoSolicHemotVO.getIndSituacaoColeta()) 
						&& situacaoSolicHemotVO.getPacCodigo() != null && situacaoSolicHemotVO.getDtHrAmostra() != null) {
					
					AbsSolicitacoesPorAmostra solicitacaoPorAmostra = new AbsSolicitacoesPorAmostra();
					solicitacaoPorAmostra.setSheAtdSeq(absSolicitacoesHemoterapicas.getPrescricaoMedica().getAtendimento().getSeq());
					solicitacaoPorAmostra.setSheSeq(absSolicitacoesHemoterapicas.getId().getSeq());
					solicitacaoPorAmostra.setIndSituacao(DominioSituacao.A);
					
					AbsAmostrasPacientesId id = new AbsAmostrasPacientesId();
					id.setPacCodigo(situacaoSolicHemotVO.getPacCodigo());
					id.setDthrAmostra(situacaoSolicHemotVO.getDtHrAmostra());
					AbsAmostrasPacientes amostraPaciente = this.absAmostrasPacientesDAO.obterPorChavePrimaria(id);
					solicitacaoPorAmostra.setAmostraPaciente(amostraPaciente);

					this.bancoDeSangueFacade.inserirSolicitacaoPorAmostra(solicitacaoPorAmostra);
				}
				
			}
			
			if (DominioIndPendenteItemPrescricao.E.equals(absSolicitacoesHemoterapicas.getIndPendente())
					&& DominioSituacaoColeta.P.equals(absSolicitacoesHemoterapicas.getIndSituacaoColeta())
					&& DominioResponsavelColeta.C.equals(absSolicitacoesHemoterapicas.getIndResponsavelColeta())) {					
				absSolicitacoesHemoterapicas.setIndSituacaoColeta(DominioSituacaoColeta.C);
				absSolicitacoesHemoterapicas.setMotivoCancelaColeta(motivoCancelaColeta);					
				this.bancoDeSangueFacade.atualizarSolicitacaoHemoterapica(absSolicitacoesHemoterapicas, this.obterLoginUsuarioLogado());
			}
		}
	}

	/**
	 * @ORADB MPMP_SIT_SOL_HEMOTER
	 * @param sheAtdSeq
	 * @param sheSeq
	 * @param unfSeq
	 * @param indUrgente
	 * @return
	 */
	private SitucaoSolicitacaoHemoterapicaVO verificarSituacaoSolicitacaoHemoterapica(Integer sheAtdSeq, Integer sheSeq, 
			Short unfSeq, Boolean indUrgente) {

		SitucaoSolicitacaoHemoterapicaVO situacaoSolicHemotVO = new SitucaoSolicitacaoHemoterapicaVO();
		Boolean vFinalizar = Boolean.FALSE;
		situacaoSolicHemotVO.setIndResponsavelColeta(DominioResponsavelColeta.N);
		situacaoSolicHemotVO.setIndSituacaoColeta(DominioSituacaoColeta.N);
		
		AipPacientes cPac = this.prescricaoMedicaFacade.pesquisarPacientePorAtendimento(sheAtdSeq).get(0);
		
		DateTime inicio = new DateTime(cPac.getDtNascimento());		
		DateTime fim = new DateTime(new Date());
		Short vMeses = (short) Months.monthsBetween(inicio, fim).getMonths();
		
		situacaoSolicHemotVO.setPacCodigo(cPac.getCodigo());
		
		List<AbsItensSolHemoterapicas> cItens = this.bancoDeSangueFacade
				.pesquisarAbsItensSolHemoterapicasPorAtdSeqSheSeq(sheAtdSeq, sheSeq);
		
		if (cItens != null && !cItens.isEmpty()) {
			
			for (AbsItensSolHemoterapicas absItensSolHemoterapicas : cItens) {
				
				if ((absItensSolHemoterapicas.getComponenteSanguineo() != null && absItensSolHemoterapicas.getComponenteSanguineo().getIndAmostra())
						|| (absItensSolHemoterapicas.getProcedHemoterapico() != null && absItensSolHemoterapicas.getProcedHemoterapico().getIndAmostra())) {
					
					Boolean verCaractUnf = examesLaudosFacade.verificarUnidadeFuncionalTemCaracteristica(unfSeq, ConstanteAghCaractUnidFuncionais.AREA_FECHADA_BANCO_DE_SANGUE); 
					if (indUrgente && !verCaractUnf) {						
						situacaoSolicHemotVO.setIndResponsavelColeta(DominioResponsavelColeta.S);
						situacaoSolicHemotVO.setIndSituacaoColeta(DominioSituacaoColeta.E);
					}
					
					if (DominioResponsavelColeta.S.equals(situacaoSolicHemotVO.getIndResponsavelColeta()) 
							|| DominioResponsavelColeta.C.equals(situacaoSolicHemotVO.getIndResponsavelColeta())) {
						vFinalizar = Boolean.TRUE;
						
					} else if (absItensSolHemoterapicas.getComponenteSanguineo() != null
							&& absItensSolHemoterapicas.getComponenteSanguineo().getCodigo() != null) {
						this.mpmpProcessaComponente(situacaoSolicHemotVO, absItensSolHemoterapicas.getComponenteSanguineo().getCodigo(), 
								vMeses.intValue(), cPac.getCodigo(), unfSeq, indUrgente);
						
					} else {
						this.mpmpProcessaProcedimento(situacaoSolicHemotVO, absItensSolHemoterapicas.getProcedHemoterapico().getCodigo(), 
								vMeses, cPac.getCodigo(), unfSeq, indUrgente);					
					}		
				}				
				if (vFinalizar) {
					break;
				}
			}
		}
		return situacaoSolicHemotVO;
	}
	
	/**
	 * @ORADB MPMP_PROCESSA_COMPONENTE
	 * @param situacaoSolicHemotVO
	 * @param csaCodigo
	 * @param vMeses
	 * @param vPacCodigo
	 * @param unfSeq
	 * @param indUrgente
	 */
	private void mpmpProcessaComponente(SitucaoSolicitacaoHemoterapicaVO situacaoSolicHemotVO, String csaCodigo, Integer vMeses,
			Integer vPacCodigo, Short unfSeq, Boolean indUrgente) {
		
		List<AbsValidAmostrasComponentes> cVac = this.absValidAmostrasComponentesDAO
				.pesquisarAbsValidAmostrasComponentesPorCsaCodigoPeriodo(csaCodigo, vMeses);
		
		if (cVac != null && !cVac.isEmpty()) {
			AbsValidAmostrasComponentes validAmostraComponente = cVac.get(0);
			Short nroMaximoSolicitacoes =  cVac.get(0).getNroMaximoSolicitacoes().shortValue();
			
			if (DominioTipoValidade.Q.equals(validAmostraComponente.getTipoValidade())) {
				
				List<AbsAmostrasPacientes> cApaQ = this.absAmostrasPacientesDAO.pesquisarAmostrasPaciente(vPacCodigo);
				this.formatarListaAmostra(cApaQ, nroMaximoSolicitacoes, Boolean.TRUE);
				this.processarDadosSituacaoHemoterapia(cApaQ, situacaoSolicHemotVO, unfSeq, indUrgente);
				
			} else if (DominioTipoValidade.U.equals(validAmostraComponente.getTipoValidade())) {
				
				Integer vDias = this.processarDiasValidAmostraComponente(validAmostraComponente.getUnidValidAmostra(),
						validAmostraComponente.getValidade());			
				List<AbsAmostrasPacientes> cApaU = this.absAmostrasPacientesDAO.pesquisarAmostrasPorCodigoPacienteAtivos(vPacCodigo,
						vDias);
				this.formatarListaAmostra(cApaU, nroMaximoSolicitacoes, Boolean.TRUE);
				this.processarDadosSituacaoHemoterapia(cApaU, situacaoSolicHemotVO, unfSeq, indUrgente);
			}
		}
	}
	
	/**
	 * @ORADB MPMP_PROCESSA_PROCEDIMENTO
	 * @param situacaoSolicHemotVO
	 * @param csaCodigo
	 * @param vMeses
	 * @param vPacCodigo
	 * @param unfSeq
	 * @param indUrgente
	 */
	private void mpmpProcessaProcedimento(SitucaoSolicitacaoHemoterapicaVO situacaoSolicHemotVO, String csaCodigo, Short vMeses,
			Integer vPacCodigo, Short unfSeq, Boolean indUrgente) {
		
		List<AbsValidAmostraProced> cVac = this.absValidAmostraProcedDAO
				.pesquisarAbsValidAmostraProcedPorPheCodigoPeriodo(csaCodigo, vMeses);
		
		if (cVac != null && !cVac.isEmpty()) {
			AbsValidAmostraProced validAmostraProced = cVac.get(0);
			Short nroMaximoSolicitacoes =  cVac.get(0).getNroMaximoSolicitacoes().shortValue();
			
			if (DominioTipoValidade.Q.equals(validAmostraProced.getTipoValidade())) {
				
				List<AbsAmostrasPacientes> cApaQ = this.absAmostrasPacientesDAO.pesquisarAmostrasPaciente(vPacCodigo);
				this.formatarListaAmostra(cApaQ, nroMaximoSolicitacoes, Boolean.TRUE);
				this.processarDadosSituacaoHemoterapia(cApaQ, situacaoSolicHemotVO, unfSeq, indUrgente);
				
			} else if (DominioTipoValidade.U.equals(validAmostraProced.getTipoValidade())) {
				
				Integer vDias = this.processarDiasValidAmostraComponente(validAmostraProced.getUnidValidAmostra(),
						validAmostraProced.getValidade());			
				List<AbsAmostrasPacientes> cApaU = this.absAmostrasPacientesDAO.pesquisarAmostrasPorCodigoPacienteAtivos(vPacCodigo,
						vDias);
				this.formatarListaAmostra(cApaU, nroMaximoSolicitacoes, Boolean.TRUE);
				this.processarDadosSituacaoHemoterapia(cApaU, situacaoSolicHemotVO, unfSeq, indUrgente);
			}
		}
	}
	
	private void formatarListaAmostra(List<AbsAmostrasPacientes> cApa, Short nroMaximoSolicitacoes,
			Boolean qualquerAmostra) {
		if (qualquerAmostra) {
			nroMaximoSolicitacoes = 99;
		}
		List<AbsAmostrasPacientes> aux = new ArrayList<AbsAmostrasPacientes>(cApa);
		for (AbsAmostrasPacientes absAmostrasPacientes : aux) {
			if (absAmostrasPacientes.getNroSolicitacoesAtendidas() != null
					&& absAmostrasPacientes.getNroSolicitacoesAtendidas() > nroMaximoSolicitacoes) {
				cApa.remove(absAmostrasPacientes);
			}
		}
	}
	
	private void processarDadosSituacaoHemoterapia(List<AbsAmostrasPacientes> cApa, 
			SitucaoSolicitacaoHemoterapicaVO situacaoSolicHemotVO, Short unfSeq, Boolean indUrgente) {
		if (cApa != null && !cApa.isEmpty()) {					
			situacaoSolicHemotVO.setIndResponsavelColeta(DominioResponsavelColeta.N);
			situacaoSolicHemotVO.setIndSituacaoColeta(DominioSituacaoColeta.D);
			situacaoSolicHemotVO.setDtHrAmostra(cApa.get(0).getId().getDthrAmostra());				
		} else {
			situacaoSolicHemotVO.setDtHrAmostra(null);
			
			Boolean verCaractUnfBancoSangue = examesLaudosFacade.verificarUnidadeFuncionalTemCaracteristica(unfSeq, 
					ConstanteAghCaractUnidFuncionais.AREA_FECHADA_BANCO_DE_SANGUE);
			Boolean verCaractUnfUnidCTI = examesLaudosFacade.verificarUnidadeFuncionalTemCaracteristica(unfSeq,
					ConstanteAghCaractUnidFuncionais.UNID_CTI);
			if ((verCaractUnfBancoSangue || indUrgente) && !verCaractUnfUnidCTI) {
				situacaoSolicHemotVO.setIndResponsavelColeta(DominioResponsavelColeta.S);
				situacaoSolicHemotVO.setIndSituacaoColeta(DominioSituacaoColeta.E);
			} else {
				situacaoSolicHemotVO.setIndResponsavelColeta(DominioResponsavelColeta.C);
				situacaoSolicHemotVO.setIndSituacaoColeta(DominioSituacaoColeta.P);
			}	
		}			
	}
	
	private Integer processarDiasValidAmostraComponente(DominioUnidadeTempo unidValidAmostra, Integer validade) {			
		switch (unidValidAmostra) {
		case H:
			return validade / 24;
		case D:
			return validade;
		case M:
			return validade * 30;
		case A:
			return validade * 360;
		default:
			return 0;
		}
	}
	
	/**
	 * @ORADB mpmk_confirma.mpmp_confirma_justif
	 * 
	 * 
	 * @param prescricao
	 * @param dataTrabalho
	 */
	private void confirmarJustificativa(MpmPrescricaoMedica prescricao,Date dataTrabalho) {
		MpmPrescricaoMedica entity = mpmPrescricaoMedicaDAO.obterPorChavePrimaria(prescricao.getId());
		List<MpmJustificativaUsoMdto> justificativaList1 = mpmJustificativaUsoMdtoDAO.obterJustificativaParaConfirmacao(entity,dataTrabalho);
		for (MpmJustificativaUsoMdto item : justificativaList1) {
			item.setServidorValida(servidorLogadoFacade.obterServidorLogado());
			mpmJustificativaUsoMdtoDAO.merge(item);
		}

		List<MpmJustificativaTb> justificativaList2 = mpmJustificativaTbDAO.obterJustificativaParaConfirmacao(entity,dataTrabalho);
		for (MpmJustificativaTb item : justificativaList2) {
			item.setServidorValida(servidorLogadoFacade.obterServidorLogado());
			item.setIndLiberacao(true);
			mpmJustificativaTbDAO.merge(item);
		}

		List<MpmNotificacaoTb> notificacaoList = mpmNotificacaoTbDAO.obterJustificativaParaConfirmacao(entity);
		for (MpmNotificacaoTb item : notificacaoList) {
			item.setServidorValidado(servidorLogadoFacade.obterServidorLogado());
			mpmNotificacaoTbDAO.merge(item);
		}
	}

	/**
	 * Atualiza agh_atendimentos com o indicador de paciente em CPA
	 * 
	 * 
	 * @ORADB: mpmk_confirma.mpmp_conf_tira_uso
	 * 
	 * @param prescricao
	 * @throws ApplicationBusinessException
	 */
	private void tirarPrescricaoDeUso(MpmPrescricaoMedica prescricao, String nomeMicrocomputador)
			throws ApplicationBusinessException {

		prescricao.setDthrMovimento(null);
		prescricao.setDthrInicioMvtoPendente(null);
		prescricao.setSituacao(DominioSituacaoPrescricao.L);
		// O if abaixo reproduz o encontrado na trigger BRU da tabela
		// MPM_PRESCRICAO_MEDICAS
		if (prescricao.getServidorValida() == null) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			prescricao.setServidorValida(servidorLogado);
			if(nomeMicrocomputador == null) {
				logError("Não foi possível obter o nome do computador");
			} else {
				prescricao.setMachine(nomeMicrocomputador);
			} 
		}
	}

	/**
	 * Confirma diagnostico do paciente.
	 * 
	 * 
	 * @ORADB: mpmk_confirma.mpmp_conf_diag_pac
	 * 
	 * @param prescricao
	 * @throws ApplicationBusinessException
	 */
	private void confirmarDiagnostico(MpmPrescricaoMedica prescricao)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		List<MpmCidAtendimento> listaCidsAtendimento = this
				.getCidAtendimentoDAO().listarCidAtendimentosPorAtendimento(
						prescricao.getAtendimento());

		IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();

		for (MpmCidAtendimento cidAtendimento : listaCidsAtendimento) {

			if (cidAtendimento.getIncCidPaciente() == null
					&& cidAtendimento.getDthrFim() == null) {

				Boolean criarNovodiagnostico = false;

				List<MamDiagnostico> listaDiagnosticos = ambulatorioFacade
						.listarDiagnosticosPorPacienteCid(prescricao
								.getAtendimento().getPaciente(), cidAtendimento
								.getCid());

				if (listaDiagnosticos == null || listaDiagnosticos.isEmpty()) {
					criarNovodiagnostico = true;
				}

				if (cidAtendimento.getCid().getSituacao() == DominioSituacao.I) {
					criarNovodiagnostico = false;
				}

				if (criarNovodiagnostico) {

					MamDiagnostico novoDiagnostico = new MamDiagnostico();
					novoDiagnostico.setData(cidAtendimento.getCriadoEm());
					novoDiagnostico.setDthrCriacao(new Date());
					novoDiagnostico.setDthrValida(new Date());
					novoDiagnostico.setPaciente(prescricao.getAtendimento()
							.getPaciente());

					novoDiagnostico.setCid(cidAtendimento.getCid());
					novoDiagnostico.setIndSituacao(DominioSituacao.A);
					novoDiagnostico
							.setIndPendente(DominioIndPendenteDiagnosticos.V);
					novoDiagnostico.setServidor(servidorLogado);
					novoDiagnostico.setServidorValida(servidorLogado);
					novoDiagnostico.setComplemento(cidAtendimento
							.getComplemento());
					novoDiagnostico.setCidAtendimento(cidAtendimento);

					this.getAmbulatorioFacade().persistirDiagnostico(
							novoDiagnostico);

				}

				cidAtendimento.setIncCidPaciente(true);

			}

			if (cidAtendimento.getAltCidPaciente() != null
					&& !cidAtendimento.getAltCidPaciente()
					&& cidAtendimento.getDthrFim() != null) {

				for (MamDiagnostico diagnostico : ambulatorioFacade
						.listarDiagnosticosAtivosPorCidAtendimento(cidAtendimento)) {

					diagnostico.setDthrMvto(new Date());
					diagnostico.setDthrValidaMvto(new Date());
					diagnostico.setServidorMovimento(servidorLogado);
					diagnostico.setServidorValidaMovimento(servidorLogado);
					diagnostico.setIndSituacao(DominioSituacao.I);
					diagnostico
							.setIndPendente(DominioIndPendenteDiagnosticos.C);
					this.getAmbulatorioFacade().persistirDiagnostico(
							diagnostico);
				}

				cidAtendimento.setAltCidPaciente(true);

			}

		}

	}

	/**
	 * Método responsável por gravar na AGH atendimento se o paciente está ou
	 * não em CPA
	 * 
	 * @ORADB: mpmk_confirma.mpmp_atu_pac_cpa
	 * 
	 * 
	 * @param prescricaoMedica
	 * @throws BaseException 
	 */
	public void atualizarAtendimentoPacienteCpa(MpmPrescricaoMedica prescricao, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		ArrayList<MpmPrescricaoCuidado> listaPrescricaoCuidados = (ArrayList<MpmPrescricaoCuidado>) this
				.getPrescricaoCuidadoDAO().listarPrescricaoCuidadoPendenteCpa(
						prescricao);

		AghAtendimentos atendimentoOld = null;
		if (listaPrescricaoCuidados.size() != 0) {
			for (MpmPrescricaoCuidado prescricaoCuidado : listaPrescricaoCuidados) {
				if (prescricaoCuidado.getPrescricaoMedica() != null
						&& prescricaoCuidado.getPrescricaoMedica()
								.getAtendimento() != null
						&& prescricaoCuidado.getPrescricaoMedica()
								.getAtendimento().getIndPacAtendimento() != null
						&& prescricaoCuidado.getPrescricaoMedica()
								.getAtendimento().getIndPacAtendimento()
								.equals(DominioPacAtendimento.S)) {

					atendimentoOld = this.getPacienteFacade().clonarAtendimento(
							prescricaoCuidado.getPrescricaoMedica()
									.getAtendimento());
					prescricaoCuidado.getPrescricaoMedica().getAtendimento()
							.setIndPacCpa(true);
					this.getPacienteFacade().persistirAtendimento(
							prescricaoCuidado.getPrescricaoMedica()
									.getAtendimento(), atendimentoOld, nomeMicrocomputador, dataFimVinculoServidor);
				}
			}
		} else {
			atendimentoOld = null;
			// if (prescricao.getAtendimento() != null
			// && prescricao.getAtendimento().getIndPacAtendimento() != null
			// && prescricao.getAtendimento().getIndPacAtendimento().equals(
			// DominioPacAtendimento.S)) {
			atendimentoOld = this.getPacienteFacade().clonarAtendimento(
					prescricao.getAtendimento());
			prescricao.getAtendimento().setIndPacCpa(false);
			this.getPacienteFacade().persistirAtendimento(
					prescricao.getAtendimento(), atendimentoOld, nomeMicrocomputador, dataFimVinculoServidor);
		}

	}

	/**
	 * @ORADB c_proced_mat_codigo da package MPMP_CONF_GERA_LAUDOS
	 * 
	 * @param atendimento
	 * @param internacao
	 * @param atendimentoUrgencia
	 * @param hospitalDia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Map<MpmPrescricaoProcedimento, FatProcedHospInternos> listarProcedimentosGeracaoLaudos(
			MpmPrescricaoMedica prescricao)
			throws ApplicationBusinessException {

		Map<MpmPrescricaoProcedimento, FatProcedHospInternos> retorno = new HashMap<MpmPrescricaoProcedimento, FatProcedHospInternos>();

		List<MpmPrescricaoProcedimento> procedimentos = this
				.getMpmPrescricaoProcedimentoDAO()
				.listarProcedimentosGeracaoLaudos(prescricao);

		IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();

		Iterator<MpmPrescricaoProcedimento> prescricaoProcedimentoIterator = procedimentos
				.iterator();

		while (prescricaoProcedimentoIterator.hasNext()) {

			MpmPrescricaoProcedimento procedimento = prescricaoProcedimentoIterator
					.next();
			if (procedimento == null) {
				continue;
			}

			if (!this.obterIndicadorJustificativaLaudoProcedimento(procedimento
					.getMatCodigo(), procedimento.getProcedimentoCirurgico(),
					procedimento.getProcedimentoEspecialDiverso(), prescricao
							.getAtendimento().getInternacao(), prescricao
							.getAtendimento().getAtendimentoUrgencia(),
					prescricao.getAtendimento().getHospitalDia(), null)) {
				prescricaoProcedimentoIterator.remove();
				continue;
			}

			if (procedimento.getDuracaoTratamentoSolicitado() == null
					|| procedimento.getDuracaoTratamentoSolicitado() == 0) {
				procedimento.setDuracaoTratamentoSolicitado((short) 1);
				procedimento.setPrescricaoMedica(prescricao);
			}

			List<FatProcedHospInternos> listaFatProcedmentosInternos = faturamentoFacade
					.listarFatProcedHospInternosPorMaterial(procedimento
							.getMatCodigo());

			if (listaFatProcedmentosInternos.isEmpty()) {
				throw new ApplicationBusinessException(
						ConfirmarPrescricaoMedicaExceptionCode.PROCEDIMENTO_SEM_EQUIVALENTE_FATURAMENTO,
						procedimento.getMatCodigo().getDescricao());
			}

			FatProcedHospInternos fatProcedHospInternos = listaFatProcedmentosInternos
					.get(0);

			retorno.put(procedimento, fatProcedHospInternos);

		}

		return retorno;
	}

	/**
	 * @ORADB c_proced_ped_seq do FORMS MPMP_CONF_GERA_LAUDOS
	 * 
	 * @param atendimento
	 * @param internacao
	 * @param atendimentoUrgencia
	 * @param hospitalDia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Map<MpmPrescricaoProcedimento, FatProcedHospInternos> listarProcedimentosDiversosGeracaoLaudos(
			MpmPrescricaoMedica prescricao)
			throws ApplicationBusinessException {

		Map<MpmPrescricaoProcedimento, FatProcedHospInternos> retorno = new HashMap<MpmPrescricaoProcedimento, FatProcedHospInternos>();

		List<MpmPrescricaoProcedimento> procedimentos = this
				.getMpmPrescricaoProcedimentoDAO()
				.listarProcedimentosDiversosGeracaoLaudos(prescricao);

		IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();

		Iterator<MpmPrescricaoProcedimento> prescricaoProcedimentoIterator = procedimentos
				.iterator();

		while (prescricaoProcedimentoIterator.hasNext()) {

			MpmPrescricaoProcedimento procedimento = prescricaoProcedimentoIterator
					.next();
			if (procedimento == null) {
				continue;
			}

			if (!this.obterIndicadorJustificativaLaudoProcedimento(procedimento
					.getMatCodigo(), procedimento.getProcedimentoCirurgico(),
					procedimento.getProcedimentoEspecialDiverso(), prescricao
							.getAtendimento().getInternacao(), prescricao
							.getAtendimento().getAtendimentoUrgencia(),
					prescricao.getAtendimento().getHospitalDia(), null)) {
				continue;
			}

			/* #51803
			 * if (procedimento.getDuracaoTratamentoSolicitado() == null
					|| procedimento.getDuracaoTratamentoSolicitado() == 0) {
				procedimento.setDuracaoTratamentoSolicitado((short) 1);
			}*/

			List<FatProcedHospInternos> listaFatProcedmentosInternos = faturamentoFacade
					.listarFatProcedHospInternosPorProcedEspecialDiversos(procedimento
							.getProcedimentoEspecialDiverso());

			if (listaFatProcedmentosInternos.isEmpty()) {
				throw new ApplicationBusinessException(
						ConfirmarPrescricaoMedicaExceptionCode.PROCEDIMENTO_SEM_EQUIVALENTE_FATURAMENTO,
						procedimento.getProcedimentoEspecialDiverso()
								.getDescricao());
			}

			FatProcedHospInternos fatProcedHospInternos = listaFatProcedmentosInternos
					.get(0);

			retorno.put(procedimento, fatProcedHospInternos);

		}

		return retorno;
	}

	/**
	 * @ORADB c_proced_pci_seq da package MPMP_CONF_GERA_LAUDOS
	 * 
	 * @param atendimento
	 * @param internacao
	 * @param atendimentoUrgencia
	 * @param hospitalDia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Map<MpmPrescricaoProcedimento, FatProcedHospInternos> listarProcedimentosCirurgicosGeracaoLaudos(
			MpmPrescricaoMedica prescricao)
			throws ApplicationBusinessException {

		Map<MpmPrescricaoProcedimento, FatProcedHospInternos> retorno = new HashMap<MpmPrescricaoProcedimento, FatProcedHospInternos>();

		List<MpmPrescricaoProcedimento> procedimentos = this
				.getMpmPrescricaoProcedimentoDAO()
				.listarProcedimentosCirurgicosGeracaoLaudos(prescricao);

		IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();

		Iterator<MpmPrescricaoProcedimento> prescricaoProcedimentoIterator = procedimentos
				.iterator();

		while (prescricaoProcedimentoIterator.hasNext()) {

			MpmPrescricaoProcedimento procedimento = prescricaoProcedimentoIterator
					.next();
			if (procedimento == null) {
				continue;
			}

			if (!this.obterIndicadorJustificativaLaudoProcedimento(procedimento
					.getMatCodigo(), procedimento.getProcedimentoCirurgico(),
					procedimento.getProcedimentoEspecialDiverso(), prescricao
							.getAtendimento().getInternacao(), prescricao
							.getAtendimento().getAtendimentoUrgencia(),
					prescricao.getAtendimento().getHospitalDia(), null)) {
				prescricaoProcedimentoIterator.remove();
			}

			if (procedimento.getDuracaoTratamentoSolicitado() == null
					|| procedimento.getDuracaoTratamentoSolicitado() == 0) {
				procedimento.setDuracaoTratamentoSolicitado((short) 1);
				procedimento.setPrescricaoMedica(prescricao);
			}

			List<FatProcedHospInternos> listaFatProcedmentosInternos = faturamentoFacade
					.listarFatProcedHospInternosPorProcedimentoCirurgicos(procedimento
							.getProcedimentoCirurgico());

			if (listaFatProcedmentosInternos.isEmpty()) {
				throw new ApplicationBusinessException(
						ConfirmarPrescricaoMedicaExceptionCode.PROCEDIMENTO_SEM_EQUIVALENTE_FATURAMENTO,
						procedimento.getProcedimentoCirurgico().getDescricao());
			}

			FatProcedHospInternos fatProcedHospInternos = listaFatProcedmentosInternos
					.get(0);

			retorno.put(procedimento, fatProcedHospInternos);

		}

		return retorno;
	}

	/**
	 * ORADB MPMC_VER_IMP_LAUD_PE
	 * 
	 * @param material
	 * @param procedimentoCirurgico
	 * @param procedimentoEspecialDiversos
	 * @param internacao
	 * @param atendimentoUrgencia
	 * @param hospitalDia
	 * @param atendimento
	 * @return
	 */
	private Boolean obterIndicadorJustificativaLaudoProcedimento(
			ScoMaterial material,
			MbcProcedimentoCirurgicos procedimentoCirurgico,
			MpmProcedEspecialDiversos procedimentoEspecialDiversos,
			AinInternacao internacao,
			AinAtendimentosUrgencia atendimentoUrgencia,
			AhdHospitaisDia hospitalDia, AghAtendimentos atendimento) {
		FatProcedHospInternos fatProcedHospInternos = null;

		if (material != null) {
			FatProcedHospInternos fatProcedHospInternosConulta = this
					.getFaturamentoFacade()
					.listarFatProcedHospInternosPorMaterial(material).get(0);
			if (fatProcedHospInternosConulta == null) {
				return false;
			} else {
				fatProcedHospInternos = fatProcedHospInternosConulta;
			}
		} else if (procedimentoCirurgico != null) {
			List<FatProcedHospInternos> listaFatProcedHospInternos = this
					.getFaturamentoFacade()
					.listarFatProcedHospInternosPorProcedimentoCirurgicos(
							procedimentoCirurgico);
			if (listaFatProcedHospInternos.isEmpty()) {
				return false;
			} else {
				fatProcedHospInternos = listaFatProcedHospInternos.get(0);
			}
		} else if (procedimentoEspecialDiversos != null) {
			List<FatProcedHospInternos> listaFatProcedHospInternos = this
					.getFaturamentoFacade()
					.listarFatProcedHospInternosPorProcedEspecialDiversos(
							procedimentoEspecialDiversos);
			if (listaFatProcedHospInternos.isEmpty()) {
				return false;
			} else {
				fatProcedHospInternos = listaFatProcedHospInternos.get(0);
			}
		}

		if (internacao == null && atendimentoUrgencia == null
				&& hospitalDia == null) {
			if (atendimento == null) {
				return false;
			} else {
				internacao = atendimento.getInternacao();
				atendimentoUrgencia = atendimento.getAtendimentoUrgencia();
				hospitalDia = atendimento.getHospitalDia();
			}

		}

		FatConvenioSaudePlano convenioSaudePlano = null;

		if (internacao != null) {
			if (internacao.getConvenioSaudePlano() == null) {
				return false;
			} else {
				convenioSaudePlano = internacao.getConvenioSaudePlano();
			}
		} else if (atendimentoUrgencia != null) {
			if (atendimentoUrgencia.getConvenioSaudePlano() == null) {
				return false;
			} else {
				convenioSaudePlano = atendimentoUrgencia
						.getConvenioSaudePlano();
			}
		} else if (hospitalDia != null) {
			if (hospitalDia.getConvenioSaudePlano() == null) {
				return false;
			} else {
				convenioSaudePlano = hospitalDia.getConvenioSaudePlano();
			}
		}

		if (convenioSaudePlano != null && fatProcedHospInternos != null) {
			return this.getFaturamentoFacade()
					.verificarFatConvGrupoItensProcedExigeJustificativa(
							fatProcedHospInternos, convenioSaudePlano);
		} else {
			return false;
		}

	}

	/**
	 * Verifica se paciente tem conta apac(cap)
	 * 
	 * Não implementada em comum acordo com CGTI por se tratar de lógica
	 * específica para o hospital de clínicas.
	 * 
	 * @ORADB fatk_cap2_rn.rn_capc_ver_cap_pac
	 * 
	 * 
	 * @return
	 */
	public boolean verificarPacienteContaApac() {
		return false;
	}

	/**
	 * Retorna o convênio de um atendimento.
	 * 
	 * @ORADB MPMP_BUSCA_CONVENIO
	 * 
	 * @param atendimento
	 * @return
	 */
	public FatConvenioSaudePlano obterConvenioAtendimento(
			AghAtendimentos atendimento) {

		FatConvenioSaudePlano retorno = null;

		switch (atendimento.getOrigem()) {
		case I:
			if(atendimento.getInternacao() != null && atendimento.getInternacao().getConvenioSaudePlano() != null){
				retorno = atendimento.getInternacao().getConvenioSaudePlano();	
			}
			break;

		case U:
			retorno = atendimento.getAtendimentoUrgencia()
					.getConvenioSaudePlano();
			break;

		case H:
			retorno = atendimento.getHospitalDia().getConvenioSaudePlano();
			break;

		case X:
			retorno = atendimento.getAtendimentoPacienteExterno()
					.getConvenioSaudePlano();
			break;

		case A:
			if (atendimento.getConsulta().getConvenioSaudePlano() != null) {
				retorno = atendimento.getConsulta().getConvenioSaudePlano();
			} else {
				retorno = this.getFaturamentoApoioFacade().obterPlanoPorId((byte) 1,
						(short) 2);
			}
			break;

		case C:
			if (atendimento.getCirurgias().iterator().next()
					.getConvenioSaudePlano() != null) {
				retorno = atendimento.getCirurgias().iterator().next()
						.getConvenioSaudePlano();
			} else {
				retorno = this.getFaturamentoApoioFacade().obterPlanoPorId((byte) 1,
						(short) 2);
			}
			break;

		default:
			break;
		}

		return retorno;

	}
	/**
	 * #43252 - Recuperacao Prescricao NPT
	 * @param prescricaoMedica
	 * @param nomeComputador
	 * @throws ApplicationBusinessException
	 */
	private void confirmarPrescricaoNpt(MpmPrescricaoMedica prescricaoMedica,  String nomeComputador) throws ApplicationBusinessException{
		List<MpmPrescricaoNpt> listaNpts = this.getMpmPrescricaoNptDAO().pesquisarPrescricaoNptPorPME(prescricaoMedica.getId(), prescricaoMedica.getDthrFim(), false);
		for (MpmPrescricaoNpt prescricaoNpt : listaNpts) {
			 confirmarPrescricaoNpt(prescricaoNpt,  nomeComputador);
		}
	}
	
	/**
	 * #43252 - Confirmar prescricao NPT
	 * @param prescricaoNpt
	 * @param nomeComputador
	 * @throws ApplicationBusinessException
	 */
	private void confirmarPrescricaoNpt(MpmPrescricaoNpt prescricaoNpt, String nomeComputador) throws ApplicationBusinessException{
		Date dataFimVinculoServidor = servidorLogadoFacade.obterServidorLogado().getDtFimVinculo();
		if (prescricaoNpt.getIndPendente() == DominioIndPendenteItemPrescricao.P) {
			prescricaoNpt.setIndPendente(DominioIndPendenteItemPrescricao.N);
			prescricaoNpt.setServidorValidacao(getServidorLogadoFacade().obterServidorLogado());
			prescricaoNpt.setDthrValida(new Date());		
			if (prescricaoNpt.getPrescricaoNpts() == null) {
				prescricaoNptRN.atualizar(prescricaoNpt, nomeComputador, dataFimVinculoServidor);
			}else{
				MpmPrescricaoNpt prescricaoNptPai =prescricaoNpt.getPrescricaoNpts();
				prescricaoNptPai.setIndPendente(DominioIndPendenteItemPrescricao.N);
				prescricaoNptPai.setServidorValidaMovimentacao(getServidorLogadoFacade().obterServidorLogado());
				prescricaoNptPai.setDthrValidaMovimentacao(new Date());	
				prescricaoNpt.setPrescricaoNpts(prescricaoNptPai);
				prescricaoNptRN.atualizar(prescricaoNpt, nomeComputador, dataFimVinculoServidor);
			}
			
		}else if(prescricaoNpt.getIndPendente() == DominioIndPendenteItemPrescricao.E){
			prescricaoNpt.setIndPendente(DominioIndPendenteItemPrescricao.N);
			prescricaoNpt.setServidorValidaMovimentacao(getServidorLogadoFacade().obterServidorLogado());
			prescricaoNpt.setDthrValidaMovimentacao(new Date());	
			prescricaoNptRN.atualizar(prescricaoNpt, nomeComputador, dataFimVinculoServidor);
		}
		
	}
	
	public void prescricaoMedicaTemPeloMenosUmaDieta(PrescricaoMedicaVO prescricaoMedicaVO) throws ApplicationBusinessException{
		List<MpmPrescricaoDieta> dietas = mpmPrescricaoDietaDAO.buscaDietaPorPrescricaoMedica(prescricaoMedicaVO.getId(),prescricaoMedicaVO.getDthrFim(), false);
		if (dietas.isEmpty() && validaOrigemObrigatoria(prescricaoMedicaVO)) {
			throw new ApplicationBusinessException(
					ConfirmarPrescricaoMedicaExceptionCode.MSG_ERRO_PRESCRICAO_MEDICA_DEVE_CONTER_AO_MENOS_UM_ITEM_DIETA);

		} else if (dietas.size() > 1) {
			throw new ApplicationBusinessException(
					ConfirmarPrescricaoMedicaExceptionCode.QUANTIDADE_DIETAS);
		}

	}

	/**
	 * #49009 - Validações de obrigatoriedade de prescrição de dieta
	 * */
	private boolean validaOrigemObrigatoria(PrescricaoMedicaVO prescricaoMedicaVO) {
		if(prescricaoMedicaVO != null
				&& prescricaoMedicaVO.getPrescricaoMedica() != null
				&& prescricaoMedicaVO.getPrescricaoMedica().getAtendimento() != null
				&& prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getOrigem() != null){

			boolean possuiCaracteristica = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getUnidadeFuncional().getSeq(), 
					   ConstanteAghCaractUnidFuncionais.DIETA_OPCIONAL_ATEND_URGENCIA);

			return(prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getOrigem() == DominioOrigemAtendimento.I
					|| (prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getOrigem() == DominioOrigemAtendimento.U && !possuiCaracteristica));
		}
		return false;
	}

	/**
	 * Retorna o DAO de prescrição médica.
	 * 
	 * @return
	 */
	protected MpmPrescricaoMedicaDAO getPrescricaoMedicaDAO() {
		return mpmPrescricaoMedicaDAO;
	}

	protected MpmCidAtendimentoDAO getCidAtendimentoDAO() {
		return mpmCidAtendimentoDAO;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	protected MpmPrescricaoDietaDAO getPrescricaoDietaDAO() {
		return mpmPrescricaoDietaDAO;
	}

	protected MpmPrescricaoMdtoDAO getPrescricaoMdtoDAO() {
		return mpmPrescricaoMdtoDAO;

	}

	protected MpmPrescricaoNptDAO getMpmPrescricaoNptDAO() {
		return mpmPrescricaoNptDAO;
	}

	protected MpmPrescricaoCuidadoDAO getPrescricaoCuidadoDAO() {
		return mpmPrescricaoCuidadoDAO;
	}

	protected MpmPrescricaoProcedimentoDAO getMpmPrescricaoProcedimentoDAO() {
		return mpmPrescricaoProcedimentoDAO;
	}

	protected MpmProcedEspecialRmDAO getMpmProcedEspecialRmDAO() {
		return mpmProcedEspecialRmDAO;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IFaturamentoApoioFacade getFaturamentoApoioFacade() {
		return this.faturamentoApoioFacade;
	}

	protected PrescreverProcedimentoEspecialRN getPrescreverProcedimentoEspecialRN() {
		return prescreverProcedimentoEspecialRN;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	private IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected PrescricaoMedicaRN getPrescricaoMedicaRN() {
		return prescricaoMedicaRN;
	}
	
	protected ManterPrescricaoMedicaRN getManterPrescricaoMedicaRN() {
		return manterPrescricaoMedicaRN;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	protected ManterPrescricaoMedicaON getManterPrescricaoMedicaON() {
		return this.manterPrescricaoMedicaON;
	}
}
