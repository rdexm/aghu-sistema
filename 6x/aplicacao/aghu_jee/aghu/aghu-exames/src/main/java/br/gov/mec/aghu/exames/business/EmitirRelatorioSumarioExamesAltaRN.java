package br.gov.mec.aghu.exames.business;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioObjetoVisual;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelNotaAdicionalDAO;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCaracteristicaDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCodificadoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelNotaAdicional;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MamPcControle;
import br.gov.mec.aghu.model.MamPcPaciente;
import br.gov.mec.aghu.model.MamPcPacienteId;
import br.gov.mec.aghu.model.MamPcSumExameMasc;
import br.gov.mec.aghu.model.MamPcSumExameTab;
import br.gov.mec.aghu.model.MamPcSumLegenda;
import br.gov.mec.aghu.model.MamPcSumMascCampoEdit;
import br.gov.mec.aghu.model.MamPcSumMascLinha;
import br.gov.mec.aghu.model.MamPcSumObservacao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Utilizado estoria #28379 - POL - Gerar Dados para Relatório Sumario Exames Alta
 * 
 * @author Eduardo Giovany Schweigert
 */
@Stateless
public class EmitirRelatorioSumarioExamesAltaRN extends BaseBusiness {


@EJB
private MascaraExamesRN mascaraExamesRN;

@EJB
private EmitirRelatorioSumarioExamesAltaPopListasRN emitirRelatorioSumarioExamesAltaPopListasRN;

private static final Log LOG = LogFactory.getLog(EmitirRelatorioSumarioExamesAltaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@Inject
private AelNotaAdicionalDAO aelNotaAdicionalDAO;


@Inject
private AelResultadoExameDAO aelResultadoExameDAO;

@EJB
private IExamesLaudosFacade examesLaudosFacade;

@EJB
private ISolicitacaoExameFacade solicitacaoExameFacade;

@Inject
private AelResultadoCodificadoDAO aelResultadoCodificadoDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

@Inject
private AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO;

@Inject
private AelResultadoCaracteristicaDAO aelResultadoCaracteristicaDAO;
	
	private static final long serialVersionUID = 970631511259439158L;

	/**
	 * ORADB: MAMK_PC_EXAMES.MAMP_ALTA_GERA_SUMEX
	 * 
	 * Gera as informações para o sumário de exames da alta. 
	 * 
	 * Faz carga nas tabelas de exames do pc
	 *  para impressão do sumário de exames na alta da emergência
	 *  de acordo com os exames selecionados no sumário de alta
	 */
	public VarMampAltaSumExames geraInformacoesSumarioAlta(final Integer apaAtdSeq, final Integer apaSeq, final Short apaSeqp) throws BaseException{
		final IPrescricaoMedicaFacade prescricaoMedicaFacade = getPrescricaoMedicaFacade();
		
		// busca o paciente
		final MpmAltaSumario altaSumario = prescricaoMedicaFacade.obterAltaSumario(new MpmAltaSumarioId(apaAtdSeq, apaSeq, apaSeqp));
		
		// Inclui um controle
		final MamPcControle controle = new MamPcControle();
		controle.setSeq(prescricaoMedicaFacade.obterNextValPleSeq());
		controle.setDtReferencia(DateUtil.obterData(1900, 0, 1));
		controle.setDthrInicio(new Date());
		controle.setDthrFim(new Date());
		
		final MamPcPaciente mamPcPaciente = new MamPcPaciente();
		MamPcPacienteId pacId = new MamPcPacienteId();
		pacId.setPleSeq(controle.getSeq());
		pacId.setSeqp(Integer.valueOf(1));
		mamPcPaciente.setId(pacId);
		
		mamPcPaciente.setPaciente(altaSumario.getPaciente());
		
		mamPcPaciente.setDthrInicio(new Date());
		mamPcPaciente.setDthrFim(new Date());
		mamPcPaciente.setMamPcControle(controle);
		
		// executa a carga das tabelas do plano de contigência
		return processarRotinaSumarioDeExames( mamPcPaciente, apaAtdSeq, apaSeq, apaSeqp);
		
	}
	
	/**
	 * ORADB: MAMK_PC_EXAMES.MAMP_ALTA_SUM_EXAMES
	 * 
	 * Rotina do sumário de exames
	 * 
	 * Diferentemente do AGH o AGHU não insere os registros em tabelas, ao contrário
	 * vai colocando-os em listas e retorna-os para posterior impressão do relatório.
	 */
	public VarMampAltaSumExames processarRotinaSumarioDeExames(final MamPcPaciente mamPcPaciente, 
															   final Integer asuApaAtdSeq, final Integer asuApaSeq, 
															   final Short apeSeqp) throws BaseException{

		/* ------------------------------------------ */
		/*   R O T I N A    P R I N C I P A L  		  */
		/* ------------------------------------------ */
		final Integer pPpeSeqp = mamPcPaciente.getId().getSeqp();
		final Integer pPpePleSeq = mamPcPaciente.getId().getPleSeq();
		
		final EmitirRelatorioSumarioExamesAltaPopListasRN erseaplRN = getEmitirRelatorioSumarioExamesAltaPopListasRN();
		final MascaraExamesRN mascaraExamesRN = getMascaraExamesRN();
		
		final AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO = getAelParametroCamposLaudoDAO();
		final AelResultadoExameDAO aelResultadoExameDAO = getAelResultadoExameDAO();
		final AelResultadoCaracteristicaDAO aelResultadoCaracteristicaDAO = getAelResultadoCaracteristicaDAO();
		final AelResultadoCodificadoDAO aelResultadoCodificadoDAO = getAelResultadoCodificadoDAO();
		final AelNotaAdicionalDAO aelNotaAdicionalDAO = getAelNotaAdicionalDAO();
		
		final IParametroFacade parametrosFacade = this.getParametroFacade();
		final ISolicitacaoExameFacade solFacade = getSolicitacaoExameFacade();
		final IExamesLaudosFacade examesLaudosFacade = getExamesLaudosFacade();
		
		final List<DominioSumarioExame> listSitCodigo = erseaplRN.obterListagemSumario();
		final VarMampAltaSumExames var = new VarMampAltaSumExames(mamPcPaciente);
		
		var.vLiberado = parametrosFacade.buscarValorTexto(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		var.vNaAreaExecutora = parametrosFacade.buscarValorTexto(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		
		// Busca exames liberados para o paciente
		final List<AelItemSolicitacaoExames> cExamesPaciente = getAghuFacade().
											listarAelItemSolicitacaoExamesPorAtdSeqSitCodigo( var.vLiberado, var.vNaAreaExecutora, pPpeSeqp, 
																							  pPpePleSeq, asuApaAtdSeq, asuApaSeq, apeSeqp);
		
		for (AelItemSolicitacaoExames rCursor : cExamesPaciente) {
			final AghAtendimentos rCursorAtendimento = rCursor.getSolicitacaoExame().getAtendimento() != null ? rCursor.getSolicitacaoExame().getAtendimento() : null;
			final String ltoIdRCursor = rCursorAtendimento != null ? solFacade.recuperarLocalPaciente(rCursorAtendimento) : "";
			final Integer rCursor_pacCodigo;
			final Integer rCursor_prontuario;
			
			if(rCursorAtendimento != null){
				rCursor_pacCodigo = rCursorAtendimento.getPaciente().getCodigo();
				rCursor_prontuario = rCursorAtendimento.getProntuario();
			} else {
				rCursor_pacCodigo = null;
				rCursor_prontuario = null;
			}
		
			processaPrntControle(pPpeSeqp, pPpePleSeq, var, rCursor, rCursor_pacCodigo, rCursor_prontuario, erseaplRN);
			
			if (listSitCodigo.contains(rCursor.getAelExameMaterialAnalise().getPertenceSumario())) {
				
				manipularResultadoExamesTab(var, rCursor, pPpeSeqp, pPpePleSeq, erseaplRN, aelResultadoExameDAO, solFacade);
				
				var.vResultadoTab = obterResultadoTabLegenda(var.vLegenda, var.vObservacao);
				var.vLtoLtoId = ltoIdRCursor;
				
				MamPcSumObservacao obj = erseaplRN.obterMamPcSumObservacao(rCursor, var.vResultadoTab, var.vCodObservacao, 
																		   rCursor.getSolicitacaoExame().getRecemNascido(), 
																		   rCursor_prontuario, 
																		   rCursor.getAelExameMaterialAnalise().getPertenceSumario(), 
																		   var.vLtoLtoId, 
																		   rCursor_pacCodigo, pPpePleSeq, pPpeSeqp);
				if(obj != null){
					var.listaMamPcSumObservacao.add(obj);
					var.vCodObservacao = obj.getCodigoMensagem();		
				}
				
				var.vResultadoTab = "";
				var.vLegenda = "";
				var.vObservacao = "";
				
			} else {
				criarMamPcSumExameMasc(var, rCursor, ltoIdRCursor, pPpePleSeq, pPpeSeqp, rCursor_pacCodigo, rCursor_prontuario, erseaplRN);
				
				criarMamPcSumMascLinha(var, rCursor, pPpeSeqp, pPpePleSeq, erseaplRN);
				
				var.vSoeSeqAnt = rCursor.getId().getSoeSeq();
				var.vSeqpAnt = rCursor.getId().getSeqp();
				
				if(rCursor.getItemSolicitacaoExame() != null && rCursor.getItemSolicitacaoExame().getId() != null) {						
					var.vSoeSeqDeptAnt = rCursor.getItemSolicitacaoExame().getId().getSoeSeq();
					var.vSeqDeptAnt = rCursor.getItemSolicitacaoExame().getId().getSeqp();
				}else{
					var.vSoeSeqDeptAnt = null;
					var.vSeqDeptAnt = null;
				}
				
				manipularResultadoExames(var, rCursor, pPpeSeqp, pPpePleSeq,
										 erseaplRN, examesLaudosFacade, aelParametroCamposLaudoDAO,
										 aelResultadoExameDAO, aelResultadoCaracteristicaDAO, 
										  aelResultadoCodificadoDAO, mascaraExamesRN);
				
				var.vPosicaoLinhaImp = Integer.valueOf(0);
				
				if(StringUtils.isNotBlank(var.vResultadoComMascara)) {
					var.vNroLinha++;				
					var.listaMamPcSumMascLinha.add(erseaplRN.obterMamPcSumMascLinha(var.vOrdemRel, var.vNroLinha, var.vResultadoComMascara, pPpeSeqp, pPpePleSeq));
					var.vEstMascLinha++;
					var.vResultadoComMascara = "";
				}

				/* Atualiza a data da impressão do item no sumário de exames - Retirado*/
				/* Inclusão das informações de notas adicionais */
				List<AelNotaAdicional> notasAdicionais = aelNotaAdicionalDAO.pesquisarNotaAdicionalPorSolicitacaoEItem(rCursor.getId().getSoeSeq(), rCursor.getId().getSeqp());
				erseaplRN.manipularNotasAdicionais(notasAdicionais, var, rCursor, ltoIdRCursor, pPpeSeqp, pPpePleSeq);
			}
		}
		
		finalizaProcesso(var, erseaplRN, pPpeSeqp, pPpePleSeq);
		
		return var;
	} 

	/** Insere registro para identificação de existencia de exme sem máscara 
	 *   para que este seja impresso pelo report -  este reg de identif ñ será
	 *    impresso no report */
	private void finalizaProcesso(final VarMampAltaSumExames var, final EmitirRelatorioSumarioExamesAltaPopListasRN erseaplRN,
								  final Integer pPpeSeqp, final Integer pPpePleSeq) {
		
		if(var.vPacCodigoControle != null && var.vPacCodigoContrFim != null && 
				CoreUtil.igual(var.vPacCodigoControle, var.vPacCodigoContrFim)){
			if(var.vNroTabPorPrnt == 0 && var.vNroMascPorPrnt > 0){
				var.listaMamPcSumExameTab.add(erseaplRN.obterMamPcSumExameTab(var, pPpeSeqp, pPpePleSeq));
			}
		}		
	}

	private void processaPrntControle(final Integer pPpeSeqp, final Integer pPpePleSeq, final VarMampAltaSumExames var, 
			final AelItemSolicitacaoExames rCursor, final Integer rCursorPacCodigo, final Integer rCursorProntuario, 
			final EmitirRelatorioSumarioExamesAltaPopListasRN erseaplRN) {
		
		if(var.vPacCodigoControle == null){
			var.vPacCodigoControle = rCursorPacCodigo;
			
		// eschweigert --> null <> 1 no oracle retorna FALSE (OCORRE NA PRIMEIRA VOLTA DO LOOP) IF	v_prnt_controle <> r_cursor.pac_codigo
		} else if(var.vPrntControle != null && !CoreUtil.igual(var.vPrntControle, rCursorPacCodigo)){
			if(CoreUtil.igual(var.vNroTabPorPrnt, Integer.valueOf(0)) &&
					(var.vNroMascPorPrnt.intValue() > 0) ){			// No construtor vNroMascPorPrnt é inicializado com ZERO
				
				var.listaMamPcSumExameTab.add(erseaplRN.obterMamPcSumExameTab(var, pPpeSeqp, pPpePleSeq)); 

				// Atualiza variáveis de controle 
				var.vNroTabPorPrnt = Integer.valueOf(0);
				var.vNroMascPorPrnt = Integer.valueOf(0);
				var.vPrntControle = rCursorProntuario;
				var.vPacCodigoControle = rCursorPacCodigo;
				var.vLtoControle = null;
				var.vRnControle = null;
				var.vExaSiglaControle = null;
				var.vManSeqControle = null;
				var.vUnfSeqControle = null;
				var.vPertSumControle = null;
				var.vSoeSeqControle = null;
				var.vIseSeqpControle = null;
				var.vDthrControle = null;
				var.vPrntContrFim = null;
				var.vPacCodigoContrFim = null;
				var.vDthrFimControle = null;
			
			} else {
				/* Atualiza variáveis de controle */
				var.vNroTabPorPrnt = Integer.valueOf(0);
				var.vNroMascPorPrnt = Integer.valueOf(0);
				var.vPrntControle = rCursorProntuario;
				var.vPacCodigoControle = rCursorPacCodigo;
			}
		}
	}
	
	private void manipularResultadoExames( final VarMampAltaSumExames var, final AelItemSolicitacaoExames rCursor, 
										   final Integer pPpeSeqp, final Integer pPpePleSeq, 
										   final EmitirRelatorioSumarioExamesAltaPopListasRN erseaplRN,
										   final IExamesLaudosFacade examesLaudosFacade,
										   final AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO,
										   final AelResultadoExameDAO aelResultadoExameDAO,
										   final AelResultadoCaracteristicaDAO aelResultadoCaracteristicaDAO,
										   final AelResultadoCodificadoDAO aelResultadoCodificadoDAO,
										   final MascaraExamesRN mascaraExamesRN) throws BaseException{
		
		// Busca versão do laudo do item 
		List<AelResultadoExame> cVersaoLaudo = examesLaudosFacade.listarResultadoVersaoLaudo(rCursor.getId().getSoeSeq(), rCursor.getId().getSeqp());
		
		if(cVersaoLaudo != null) {
			for(AelResultadoExame rVersaoLaudo : cVersaoLaudo) {
				var.vVersaoExaSigla = rVersaoLaudo.getParametroCampoLaudo().getId().getVelEmaExaSigla();
				var.vVersaoManSeq   = rVersaoLaudo.getParametroCampoLaudo().getId().getVelEmaManSeq();
				var.vVersaoSeqp     = rVersaoLaudo.getParametroCampoLaudo().getId().getVelSeqp();
				
				// Busca máscara do laudo e resultados
				List<AelParametroCamposLaudo> cMascaraLaudo = aelParametroCamposLaudoDAO.
									pesquisarParametroCamposLaudoPorVersaoLaudo( var.vVersaoExaSigla, var.vVersaoManSeq, var.vVersaoSeqp);
				
				Collections.sort(cMascaraLaudo,  new Comparator<AelParametroCamposLaudo>() {
					public int compare(AelParametroCamposLaudo o1, AelParametroCamposLaudo o2) {
						Integer sum1 = zeroIfNull(o1.getPosicaoLinhaImpressao()) + zeroIfNull(o1.getAlturaObjetoVisual());
						Integer sum2 = zeroIfNull(o2.getPosicaoLinhaImpressao()) + zeroIfNull(o2.getAlturaObjetoVisual());
						int compare = sum1.compareTo(sum2);
						if (compare==0){
							return o1.getPosicaoColunaImpressao().compareTo(o2.getPosicaoColunaImpressao());
						}
						return compare;
					}
				});
			
				for(AelParametroCamposLaudo rMascaraLaudo : cMascaraLaudo){
					final String textoLivre = rMascaraLaudo.getTextoLivre();
					final DominioTipoCampoCampoLaudo tipoCampo = rMascaraLaudo.getCampoLaudo().getTipoCampo();
					final Integer campoLaudoCalSeq = rMascaraLaudo.getId().getCalSeq();
					
					var.vVersaoExaSigla = rMascaraLaudo.getId().getVelEmaExaSigla();
					var.vVersaoManSeq = rMascaraLaudo.getId().getVelEmaManSeq();
					var.vVersaoSeqp = rMascaraLaudo.getId().getSeqp();
					
					if(DominioObjetoVisual.TEXTO_LONGO.equals(rMascaraLaudo.getObjetoVisual())) {
						if(StringUtils.isNotBlank(var.vResultadoComMascara)) {
							var.vNroLinha++;
							var.listaMamPcSumMascLinha.add(erseaplRN.obterMamPcSumMascLinha( var.vOrdemRel, var.vNroLinha, var.vResultadoComMascara, pPpeSeqp, pPpePleSeq));
							var.vEstMascLinha++;
							var.vResultadoComMascara = null;
						}
						
						var.vPosicaoLinhaImp = 0;
						if(var.vNroLinha == 0) {
							var.vNroLinha++;
							var.listaMamPcSumMascLinha.add(erseaplRN.obterMamPcSumMascLinha( var.vOrdemRel, var.vNroLinha, null, pPpeSeqp, pPpePleSeq));
							var.vEstMascLinha++;
						}
						
						if(StringUtils.isNotBlank(textoLivre) && !DominioTipoCampoCampoLaudo.E.equals(tipoCampo)) {
							var.vNroCampo++;
							var.listaMamPcSumMascCampoEdit.add(erseaplRN.obterMamPcSumMascCampoEdit( var.vOrdemRel, var.vNroLinha, var.vNroCampo, rMascaraLaudo.getTextoLivre(), pPpeSeqp, pPpePleSeq));
							var.vEstCampoEdit++;
							
						} else {
							// Buscar o resultado
							List<AelResultadoExame> resultados = aelResultadoExameDAO.listarResultadosExames(
																			rVersaoLaudo.getItemSolicitacaoExame().getId().getSoeSeq(), 
																			rVersaoLaudo.getItemSolicitacaoExame().getId().getSeqp(), 
																			rMascaraLaudo.getAelVersaoLaudo().getId().getEmaExaSigla(),
																			rMascaraLaudo.getAelVersaoLaudo().getId().getEmaManSeq(), 
																			rMascaraLaudo.getAelVersaoLaudo().getId().getSeqp(), 
																			rMascaraLaudo.getId().getCalSeq(),
																			rMascaraLaudo.getId().getSeqp());
							
							adicionarListaMamPcSumMascCampoEdit(resultados, var, rMascaraLaudo, pPpeSeqp, pPpePleSeq,
																 erseaplRN, aelResultadoCaracteristicaDAO, 
																 aelResultadoCodificadoDAO);
						}
						
					} else {
						Integer posicaoLinha = zeroIfNull(rMascaraLaudo.getPosicaoLinhaImpressao()) + zeroIfNull(rMascaraLaudo.getAlturaObjetoVisual());
						if(var.vPosicaoLinhaImp == null || var.vPosicaoLinhaImp.intValue() == 0) {
							var.vPosicaoLinhaImp = posicaoLinha;
						}
						
						if(var.vPosicaoLinhaImp.equals(posicaoLinha)) {
							// Inclusão do tratamento do novos objetos na máscara dos laudos
							if(!erseaplRN.verificarDominioObjetoVisualOutraPosicao( rMascaraLaudo.getObjetoVisual(), textoLivre, tipoCampo, 
																					campoLaudoCalSeq, var, false, mascaraExamesRN, rCursor)){
								// Buscar o resultado
								List<AelResultadoExame> resultados = aelResultadoExameDAO.listarResultadosExames(
																				rVersaoLaudo.getItemSolicitacaoExame().getId().getSoeSeq(), 
																				rVersaoLaudo.getItemSolicitacaoExame().getId().getSeqp(), 
																				rMascaraLaudo.getAelVersaoLaudo().getId().getEmaExaSigla(),
																				rMascaraLaudo.getAelVersaoLaudo().getId().getEmaManSeq(), 
																				rMascaraLaudo.getAelVersaoLaudo().getId().getSeqp(), 
																				rMascaraLaudo.getId().getCalSeq(),
																				rMascaraLaudo.getId().getSeqp());
								
								adicionarListaMamPcSumMascCampoEdit(resultados, var, rMascaraLaudo, pPpeSeqp, pPpePleSeq,
																	 erseaplRN, aelResultadoCaracteristicaDAO, 
																	 aelResultadoCodificadoDAO);
							}
						} else {		
							
							if(StringUtils.isNotBlank(var.vResultadoComMascara)) {
								var.vNroLinha++;
								var.listaMamPcSumMascLinha.add(erseaplRN.obterMamPcSumMascLinha(var.vOrdemRel, var.vNroLinha, var.vResultadoComMascara,pPpeSeqp,pPpePleSeq));
								var.vEstMascLinha++;
								var.vResultadoComMascara = "";
							}
							
							var.vPosicaoLinhaImp = zeroIfNull(rMascaraLaudo.getPosicaoLinhaImpressao()) + zeroIfNull(rMascaraLaudo.getAlturaObjetoVisual());
							
							// Inclusão do tratamento do novos objetos na máscara dos laudos
							if(!erseaplRN.verificarDominioObjetoVisualOutraPosicao( rMascaraLaudo.getObjetoVisual(),textoLivre, tipoCampo, campoLaudoCalSeq, var, true, mascaraExamesRN, rCursor)){
							
								List<AelResultadoExame> resultados = aelResultadoExameDAO.listarResultadosExames(rVersaoLaudo.getItemSolicitacaoExame().getId().getSoeSeq(), rVersaoLaudo.getItemSolicitacaoExame().getId().getSeqp(), rMascaraLaudo.getAelVersaoLaudo().getId().getEmaExaSigla(), rMascaraLaudo.getAelVersaoLaudo().getId().getEmaManSeq(), rMascaraLaudo.getAelVersaoLaudo().getId().getSeqp(), rMascaraLaudo.getId().getCalSeq(), rMascaraLaudo.getId().getSeqp());									
								
								adicionarListaMamPcSumMascCampoEdit(resultados, var, rMascaraLaudo, pPpeSeqp, pPpePleSeq,
										 				erseaplRN, aelResultadoCaracteristicaDAO,  aelResultadoCodificadoDAO);
							}
						}
					}
				}
				break;
			}
		}
	}
	
	private void adicionarListaMamPcSumMascCampoEdit( final List<AelResultadoExame> resultados, 
													  final VarMampAltaSumExames var, 
													  final AelParametroCamposLaudo parametroCampoLaudo,
													  final Integer pPpeSeqp, final Integer pPpePleSeq, 
													  final EmitirRelatorioSumarioExamesAltaPopListasRN erseaplRN,
													  final AelResultadoCaracteristicaDAO aelResultadoCaracteristicaDAO,
													  final AelResultadoCodificadoDAO aelResultadoCodificadoDAO) {
		
		if(resultados != null) {
			for(AelResultadoExame resultado : resultados) {
				if(resultado.getValor() == null) {
					if(resultado.getResultadoCaracteristica() != null) {
						AelResultadoCaracteristica resultadoCaracteristica = aelResultadoCaracteristicaDAO.obterPorChavePrimaria(resultado.getResultadoCaracteristica().getSeq());
						var.vDescricaoCaracteristica = resultadoCaracteristica.getDescricao();
						var.vDescricaoResultado = var.vDescricaoCaracteristica;
						var.vResultadoAux = var.vDescricaoCaracteristica;
						var.vNroCampo++;
						var.listaMamPcSumMascCampoEdit.add(erseaplRN.obterMamPcSumMascCampoEdit( var.vOrdemRel, var.vNroLinha, var.vNroCampo, 
																								 var.vResultadoAux, pPpeSeqp, pPpePleSeq));
						var.vEstCampoEdit++;
						
					} else if(resultado.getResultadoCodificado() != null) {
						List<AelResultadoCodificado> resultadosCodificados = aelResultadoCodificadoDAO.pesquisarResultadoCodificadoPorGtcSeqSeqp(resultado.getResultadoCodificado().getId().getGtcSeq(), resultado.getResultadoCodificado().getId().getSeqp());
						if(resultadosCodificados != null) {	
							var.vDescricaoCodificado = resultadosCodificados.iterator().next().getDescricao();
							var.vDescricaoResultado = var.vDescricaoCodificado;
							var.vResultadoAux = var.vDescricaoCodificado;
							var.listaMamPcSumMascCampoEdit.add(erseaplRN.obterMamPcSumMascCampoEdit( var.vOrdemRel, var.vNroLinha, var.vNroCampo, 
																									 var.vResultadoAux, pPpeSeqp, pPpePleSeq));
							var.vEstCampoEdit++;
						}
					} else {
						
						
						if(resultado.getDescricao() != null) {
							String textoLaudo = resultado.getDescricao();
							
							if (textoLaudo != null) {
								if (textoLaudo.contains("rtf")) {
									textoLaudo = CoreUtil.rtfToHtml(textoLaudo);
								} else {
									textoLaudo = CoreUtil.converterRTF2Text(textoLaudo);
								}
							}
														
							// Caso a string passada for HTML o texto será 
							// extraído das tags HTML, senão mantém o texto como está
							textoLaudo = CoreUtil.extrairTextoDeHtml(textoLaudo);
							
							// Remove quebras de linha
							textoLaudo = textoLaudo.replaceAll("\r", "");
							
							var.vDescricaoResultado = textoLaudo;
							var.vResultadoAux = var.vDescricaoResultado;
							var.listaMamPcSumMascCampoEdit.add(erseaplRN.obterMamPcSumMascCampoEdit( var.vOrdemRel, var.vNroLinha, var.vNroCampo, 
																									 var.vResultadoAux, pPpeSeqp, pPpePleSeq));
							var.vEstCampoEdit++;
						}
					}
					
				} else {
					if(resultado.getValor() != null && resultado.getParametroCampoLaudo() != null) {
						DecimalFormatSymbols dfs = new DecimalFormatSymbols (new Locale ("pt", "BR"));  
						NumberFormat nf = new DecimalFormat ("######.##", dfs);  
						var.vValor = (resultado.getValor() / Math.pow(10, (Short)CoreUtil.nvl(parametroCampoLaudo.getQuantidadeCasasDecimais(),Short.valueOf("0"))));
						var.vResultadoAux = nf.format(var.vValor);
					}
					
					var.listaMamPcSumMascCampoEdit.add(erseaplRN.obterMamPcSumMascCampoEdit( var.vOrdemRel, var.vNroLinha, var.vNroCampo, 
																						     var.vValor == null ? "" : var.vValor.toString(),
																						     pPpeSeqp, pPpePleSeq));
					var.vEstCampoEdit++;
				}
				break;
			}
		} else {
			var.listaMamPcSumMascCampoEdit.add(erseaplRN.obterMamPcSumMascCampoEdit( var.vOrdemRel, var.vNroLinha, var.vNroCampo, " ", pPpeSeqp, pPpePleSeq));
			var.vEstCampoEdit++;
		}
	}
	
	/** Processa resultado da busca da mascara dos laudos e resultados relacionados a um determinado item 
	 *  Quando o exame possui pertence_sumario in (B,E,G,H)
	 *  AGH: FOR r_pcl_ree IN c_pcl_ree ( r_cursor.soe_seq,r_cursor.seqp) */ 
	private void manipularResultadoExamesTab(final VarMampAltaSumExames var, final AelItemSolicitacaoExames rCursor, 
											 final Integer pPpeSeqp, final Integer pPpePleSeq, 
											 final EmitirRelatorioSumarioExamesAltaPopListasRN erseaplRN,
											 final AelResultadoExameDAO aelResultadoExameDAO,
											 final ISolicitacaoExameFacade solFacade) {
		
		final List<AelResultadoExame> resultadoExames =  aelResultadoExameDAO.listarMascaraResultadosExames(rCursor.getId().getSoeSeq(), rCursor.getId().getSeqp());
		
		Collections.sort(resultadoExames,  new Comparator<AelResultadoExame>() {
			public int compare(AelResultadoExame o1, AelResultadoExame o2) {
				Integer sum1 = o1.getParametroCampoLaudo().getPosicaoLinhaImpressao() + o1.getParametroCampoLaudo().getAlturaObjetoVisual();
				Integer sum2 = o2.getParametroCampoLaudo().getPosicaoLinhaImpressao() + o2.getParametroCampoLaudo().getAlturaObjetoVisual();
				int compare = sum1.compareTo(sum2);
				if (compare==0){
					return o1.getParametroCampoLaudo().getPosicaoColunaImpressao().compareTo(o2.getParametroCampoLaudo().getPosicaoColunaImpressao());
				}
				return compare;
			}
		});
		
		if(!resultadoExames.isEmpty()){
			
			for (AelResultadoExame rPclRee : resultadoExames) {
				
				if(rPclRee.getParametroCampoLaudo().getTextoLivre() == null ||
						DominioTipoCampoCampoLaudo.E.equals(rPclRee.getParametroCampoLaudo().getCampoLaudo().getTipoCampo())) {
					final AghAtendimentos rPclReeAtendimento = rPclRee.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento();
					
					final String rCursor_ltoLtoId;
					final Integer rPclRee_pacCodigo;
					if(rPclReeAtendimento != null){
						rPclRee_pacCodigo = rPclReeAtendimento.getPaciente().getCodigo();
						rCursor_ltoLtoId = solFacade.recuperarLocalPaciente(rPclReeAtendimento);
						
					} else {
						rPclRee_pacCodigo = null;
						rCursor_ltoLtoId = "";
					}
					
					if(rPclRee.getValor() == null){
						final Boolean rPclRee_recemNascido = rPclRee.getItemSolicitacaoExame().getSolicitacaoExame().getRecemNascido();
						final DominioSumarioExame rPclRee_pertenceSumario = rPclRee.getItemSolicitacaoExame()
																					.getAelExameMaterialAnalise().getPertenceSumario();
						
						if(rPclRee.getResultadoCaracteristica() != null){ 	// IF r_pcl_ree.cac_seq IS NOT NULL THEN
							
							var.vDescricaoCaracteristica = rPclRee.getResultadoCaracteristica().getDescricao();
							
							// Busca existencia de legenda igual
							final boolean temLegenda = verificarExisteLegenda( pPpePleSeq, pPpeSeqp,
																			   rPclRee_pacCodigo, rPclRee_recemNascido, rPclRee_pertenceSumario, 
																			   rPclRee.getResultadoCodificado().getId().getSeqp(), 
																			   Integer.valueOf(0), var.vDescricaoCaracteristica, 
																			   var.listaMamPcSumLegenda);
					              
							//OPEN c_legenda (p_ppe_ple_seq, p_ppe_seqp,r_cursor.pac_codigo, r_cursor.recem_nascido, 
							// 			      r_cursor.pertence_sumario,r_pcl_ree.cac_seq, 0, v_descricao_caracteristica);
							if(!temLegenda){
								var.vLegenda = ((var.vLegenda != null) ? var.vLegenda : "") + 
													"0 - "+rPclRee.getResultadoCaracteristica().getSeq() +  "          ";

								var.listaMamPcSumLegenda.add( erseaplRN.obterMamPcSumLegenda( var, rPclRee, rPclReeAtendimento, 
																							  rPclRee_recemNascido, rPclRee_pertenceSumario, 
																							  var.vDescricaoCaracteristica, rCursor, 
																							  pPpeSeqp, pPpePleSeq,
																							  rPclRee.getResultadoCaracteristica().getSeq(),
																							  Integer.valueOf(0), rCursor_ltoLtoId
																							));
								var.vEstLegenda++;
							}
						// ELSIF r_pcl_ree.rcd_gtc_seq IS NOT NULL AND r_pcl_ree.rcd_seqp IS NOT NULL THEN
						} else if(rPclRee.getResultadoCodificado() != null){
							var.vDescricaoCodificado = rPclRee.getResultadoCodificado().getDescricao();
							
							// Busca existencia de legenda igual
							final boolean temLegenda = verificarExisteLegenda( pPpePleSeq, pPpeSeqp,
																			   rPclRee_pacCodigo, 
																			   rPclRee_recemNascido,
																			   rPclRee_pertenceSumario, 
																			   rPclRee.getResultadoCodificado().getId().getSeqp(), 
																			   rPclRee.getResultadoCodificado().getId().getGtcSeq(), 
																			   var.vDescricaoCodificado, 
																			   var.listaMamPcSumLegenda);
							
							// OPEN c_legenda (p_ppe_ple_seq, p_ppe_seqp, r_cursor.pac_codigo, r_cursor.recem_nascido, 
							//				   r_cursor.pertence_sumario, r_pcl_ree.rcd_seqp, r_pcl_ree.rcd_gtc_seq, v_descricao_codificado);
							if(!temLegenda){
								var.vLegenda = ((var.vLegenda != null) ? var.vLegenda : "") +
															  rPclRee.getResultadoCodificado().getId().getGtcSeq() + "-" +  
															  rPclRee.getResultadoCodificado().getId().getSeqp()   + "     ";
								
								var.listaMamPcSumLegenda.add( erseaplRN.obterMamPcSumLegenda( var, rPclRee, rPclReeAtendimento, 
																							  rPclRee_recemNascido, rPclRee_pertenceSumario, 
																							  var.vDescricaoCodificado, rCursor, pPpeSeqp, pPpePleSeq,
																							  rPclRee.getResultadoCodificado().getId().getSeqp(),
																							  rPclRee.getResultadoCodificado().getId().getGtcSeq(), 
																							  rCursor_ltoLtoId
																							));
								var.vEstLegenda++;
							}
						} else {
							// --DBMS_OUTPUT.PUT_LINE('DESCRICAO');
							var.vObservacao =  obterObservacao((rPclRee.getDescricao() != null ? rPclRee.getDescricao() : ""), var.vObservacao);
						}
						
					} else {
						if(rPclRee.getValor() != null) {
							var.vValor = (rPclRee.getValor() / Math.pow(10, (Short)CoreUtil.nvl(rPclRee.getParametroCampoLaudo().getQuantidadeCasasDecimais(),Short.valueOf("0"))));
						}
						
						if(truncate(var.vValor) > 99999999) {
							var.vValor = (var.vValor / 1000000);					
							var.vNome = obterNome(var.vValor, rPclRee);
							var.vNomeSumario = obterNomeSumario(var.vValor, rPclRee);
						} else {
							if(truncate(var.vValor) > 9999) {
								var.vValor = (var.vValor / 1000);
								var.vNome = obterNome(var.vValor, rPclRee);
								var.vNomeSumario = obterNomeSumario(var.vValor, rPclRee);						
							} else {
								var.vNome = obterNome(var.vValor, rPclRee);
								var.vNomeSumario = obterNomeSumario(var.vValor, rPclRee);
							}
						}
						
						var.listaMamPcSumExameTab.add(erseaplRN.obterMamPcSumExameTab(rCursor, rPclRee, var, rCursor_ltoLtoId, pPpeSeqp, pPpePleSeq));
						var.vNroTabPorPrnt++;
						var.vEstExameTab++;
					}
				}
			}
		}
	}
	
	private void criarMamPcSumMascLinha(final VarMampAltaSumExames var, final AelItemSolicitacaoExames rCursor, 
										final Integer pPpeSeqp, final Integer pPpePleSeq,
										final EmitirRelatorioSumarioExamesAltaPopListasRN erseaplRN){

		if(Boolean.TRUE.equals(rCursor.getAelExameMaterialAnalise().getIndDependente())){
			final AelItemSolicitacaoExames examePai = getAelItemSolicitacaoExameDAO().buscarExamePai( rCursor.getItemSolicitacaoExame().getId().getSoeSeq(), 
					  																				  rCursor.getItemSolicitacaoExame().getId().getSeqp()
			  	    																				);
			if(var.vSeqpAnt == null) { 
				var.listaMamPcSumMascLinha.add(erseaplRN.obterMamPcSumMascLinha(examePai, var, rCursor, pPpeSeqp, pPpePleSeq, null));
				var.vEstMascLinha++;
				
			} else { 	
				if(var.vSeqDeptAnt != null) {
					var.listaMamPcSumMascLinha.add(erseaplRN.obterMamPcSumMascLinha(examePai, var, rCursor, pPpeSeqp, pPpePleSeq, null));
					var.vEstMascLinha++;
					
				} else {
					if(rCursor.getItemSolicitacaoExame() != null){
						Integer iseSoeSeq = rCursor.getItemSolicitacaoExame().getId().getSoeSeq();
						Short iseSeqp = rCursor.getItemSolicitacaoExame().getId().getSeqp();
						
						if( (!iseSoeSeq.equals(examePai.getId().getSoeSeq()) || !iseSeqp.equals(examePai.getId().getSeqp())) ) {
							var.listaMamPcSumMascLinha.add(erseaplRN.obterMamPcSumMascLinha(examePai, var, rCursor, pPpeSeqp, pPpePleSeq, "-"));
						}
					}
				}
			}
		}
	}
	
	private void criarMamPcSumExameMasc(final VarMampAltaSumExames var, final AelItemSolicitacaoExames rCursor, 
			final String ltoIdRCursor, final Integer ppePleSeq, final Integer ppeSeqp, 
			final Integer rCursor_pacCodigo, final Integer rCursor_prontuario,
			final EmitirRelatorioSumarioExamesAltaPopListasRN erseaplRN){
		
		var.vNroLinha = Integer.valueOf(0);
		var.vNroCampo = Integer.valueOf(0);

		// if	r_cursor.pac_codigo		=	v_pac_codigo_ant		and
		if( (!CoreUtil.igual(rCursor_pacCodigo, var.vPacCodigoAnt)) ||
				!CoreUtil.igual(rCursor.getSolicitacaoExame().getRecemNascido(), var.vRecemNascido) ||
				!CoreUtil.igual(rCursor.getAelExameMaterialAnalise().getPertenceSumario(), var.vPertenceSumarioAnt) || 
				!CoreUtil.igual(DateUtil.truncaData(rCursor.getDthrLiberada()) , DateUtil.truncaData(var.vDthrEventoLibAnt)) ||
				!CoreUtil.igual(rCursor.getUnidadeFuncional().getSeq(), var.vUfeUnfSeqAnt) ||
				!CoreUtil.igual(rCursor.getMaterialAnalise().getSeq(), var.vUfeUmaManSeqAnt)
				){

			var.vPacCodigoAnt = rCursor_pacCodigo;
			var.vProntuarioAnt = rCursor_prontuario;
			var.vRecemNascidoAnt = rCursor.getSolicitacaoExame().getRecemNascido();
			var.vPertenceSumarioAnt = rCursor.getAelExameMaterialAnalise().getPertenceSumario();
			var.vDthrEventoLibAnt = DateUtil.truncaData(rCursor.getDthrLiberada());
			var.vUfeUnfSeqAnt = rCursor.getUnidadeFuncional().getSeq();
			var.vUfeUmaManSeqAnt = rCursor.getMaterialAnalise().getSeq();
			var.vOrdemAgrup++;	// v_ordem_agrup := v_ordem_agrup + 1;
		}
			
		var.vOrdemRel++;
		
		final Date dataHoraEvento = erseaplRN.obterMaxDataHoraEvento(rCursor.getAelExtratoItemSolicitacao());
		
		// Salva item na lista
		var.listaMamPcSumExameMasc.add(erseaplRN.obterMamPcSumExameMasc(var, rCursor, dataHoraEvento, ltoIdRCursor, ppeSeqp, ppePleSeq));		

		var.vEstExameMasc++;
		var.vNroMascPorPrnt++;
		var.vLtoControle = ltoIdRCursor;
		var.vRnControle = rCursor.getSolicitacaoExame().getRecemNascido();
		var.vExaSiglaControle = rCursor.getExame().getSigla();
		var.vManSeqControle = rCursor.getMaterialAnalise().getSeq();
		var.vUnfSeqControle = rCursor.getUnidadeFuncional().getSeq();
		var.vPertSumControle = rCursor.getAelExameMaterialAnalise().getPertenceSumario();
		var.vSoeSeqControle = rCursor.getId().getSoeSeq();
		var.vIseSeqpControle = rCursor.getId().getSeqp();
		var.vDthrControle = dataHoraEvento;
		var.vPrntContrFim = rCursor_prontuario;
		var.vPacCodigoContrFim = rCursor_pacCodigo;
		if(rCursor.getSolicitacaoExame().getAtendimento() != null){
			var.vDthrFimControle = rCursor.getSolicitacaoExame().getAtendimento().getDthrFim();
		} else {
			var.vDthrFimControle = null;
		}
	}
	
	@SuppressWarnings("ucd")
	protected class VarMampAltaSumExames {
		final List<MamPcSumExameTab> listaMamPcSumExameTab;
		final List<MamPcSumLegenda> listaMamPcSumLegenda;
		final List<MamPcSumObservacao> listaMamPcSumObservacao;
		final List<MamPcSumExameMasc> listaMamPcSumExameMasc;
		final List<MamPcSumMascLinha> listaMamPcSumMascLinha;
		final List<MamPcSumMascCampoEdit> listaMamPcSumMascCampoEdit;	
		final MamPcPaciente mamPcPaciente;
		
		public VarMampAltaSumExames(final MamPcPaciente mamPcPaciente) {
			listaMamPcSumExameTab = new ArrayList<MamPcSumExameTab>();
			listaMamPcSumLegenda = new ArrayList<MamPcSumLegenda>();
			listaMamPcSumObservacao = new ArrayList<MamPcSumObservacao>();
			listaMamPcSumExameMasc = new ArrayList<MamPcSumExameMasc>();
			listaMamPcSumMascLinha = new ArrayList<MamPcSumMascLinha>();
			listaMamPcSumMascCampoEdit = new ArrayList<MamPcSumMascCampoEdit>();
			vNroTabPorPrnt = Integer.valueOf(0);
			vNroMascPorPrnt = Integer.valueOf(0);
			vEstLegenda = Integer.valueOf(0);
			vEstExameTab = Integer.valueOf(0);
			vNroLinha = Integer.valueOf(0);		
			vNroCampo = Integer.valueOf(0);		
			vOrdemAgrup = Integer.valueOf(0);	
			vOrdemRel = Integer.valueOf(0);		
			vEstMascLinha = Integer.valueOf(0);	
			vEstExameMasc = Integer.valueOf(0);	
			vEstCampoEdit = Integer.valueOf(0);
			this.mamPcPaciente = mamPcPaciente;
		}
		
		String  vLiberado;
		String  vNaAreaExecutora;
		Integer vPacCodigo;												 
		Integer vNroTabPorPrnt;
		Integer vNroMascPorPrnt;
		Integer vPrntControle;						
		Integer vPacCodigoControle;					
		String vLtoControle;						
		Boolean vRnControle;						
		String vExaSiglaControle;					
		Integer vManSeqControle;					
		Short vUnfSeqControle;						
		DominioSumarioExame vPertSumControle;		
		Integer vSoeSeqControle;					
		Short vIseSeqpControle;						
		Date vDthrControle;							
		Integer vPrntContrFim;						
		Integer vPacCodigoContrFim;					
		Date vDthrFimControle;						
		Integer vEstLegenda;	
		Double vValor;								
		String vNome;								
		String vNomeSumario;						
		Integer vEstExameTab;	
		DominioSumarioExame vPertenceSumControle;	
		String vResultadoTab;						
		String vObservacao;							
		Short vCodObservacao;						
		Integer vNroLinha;		
		Integer vNroCampo;		
		Integer vPacCodigoAnt;						
		Date vDthrEventoLibAnt;						
		DominioSumarioExame vPertenceSumarioAnt;	
		Short vUfeUnfSeqAnt;						
		Integer vUfeUmaManSeqAnt;					
		Integer vOrdemAgrup;	
		Integer vOrdemRel;		
		Integer vEstExameMasc;	
		Short vSeqpAnt;								
		String vDescricaoPai;						
		Integer vEstMascLinha;	
		String vVersaoExaSigla;						
		Integer vVersaoManSeq;						
		Integer vVersaoSeqp;						
		String vResultadoComMascara;				
		Integer vPosicaoLinhaImp;					
		Integer vEstCampoEdit;	
		String vDescricaoCaracteristica;
		String vDescricaoResultado;
		String vResultadoAux;
		String vDescricaoCodificado;
		String vDescricaoNotasAdicionais;
		String vLegenda;
		String vLtoLtoId;
		Integer vSoeSeqAnt;
		Short vSeqDeptAnt;
		Integer vSoeSeqDeptAnt;
		boolean vRecemNascido;
		Integer vProntuarioAnt;
		boolean vRecemNascidoAnt;
	}

	public boolean verificarExisteLegenda( final Integer ppePleSeq, final Integer ppeSeqp, final Integer pacCodigo, final Boolean recemNascido,
											final DominioSumarioExame pertenceSumario, final Integer numeroLegenda, final Integer grupoLegenda, 
											final String descricao, final List<MamPcSumLegenda> listaMamPcSumLegenda){
		
		for(MamPcSumLegenda legenda : listaMamPcSumLegenda){
			if(CoreUtil.igual(legenda.getId().getPpePleSeq(), ppePleSeq ) &&
					CoreUtil.igual(legenda.getId().getPpeSeqp(), ppeSeqp ) &&
						CoreUtil.igual(legenda.getId().getPacCodigo(), pacCodigo ) &&
							CoreUtil.igual(legenda.getId().getRecemNascido(), recemNascido) &&
								CoreUtil.igual(legenda.getId().getPertenceSumario(), pertenceSumario) &&
									CoreUtil.igual(legenda.getId().getNumeroLegenda(), numeroLegenda) &&
										CoreUtil.igual(legenda.getId().getGrupoLegenda(), grupoLegenda) &&
											CoreUtil.igual(legenda.getId().getDescricao(), descricao)  ){
				return true;
			}
		}
		return false;
	}
	
	private String obterObservacao(String vDescricaoResultado, String vObservacao) {
		StringBuilder strObservacao = new StringBuilder();							
		
		if(StringUtils.isBlank(vObservacao)) {
			strObservacao.append(vDescricaoResultado).append('\n');
		} else {
			strObservacao.append(vObservacao).append(vDescricaoResultado).append('\n');
		}
		return strObservacao.toString();
	}
	
	public double truncate(Double value) {  
	    return Math.round(value * 100) / 100L;  
	}
	
	private String obterNome(Double vValor, AelResultadoExame resultadoExame) {
		StringBuilder sbNome = new StringBuilder();
		if(resultadoExame.getValor() != null && resultadoExame.getParametroCampoLaudo() != null) {			
			vValor = (resultadoExame.getValor() / Math.pow(10, (Short) CoreUtil.nvl(resultadoExame.getParametroCampoLaudo().getQuantidadeCasasDecimais(),Short.valueOf("0"))));
		}
		String nome = "";
		if(resultadoExame != null && resultadoExame.getParametroCampoLaudo() != null && resultadoExame.getParametroCampoLaudo().getCampoLaudo() != null) {
			nome = resultadoExame.getParametroCampoLaudo().getCampoLaudo().getNome();
		}
		if(truncate(vValor) > 99999999) {								
			vValor = (vValor / 1000000);
			if(StringUtils.isNotBlank(nome)) {
				if(nome.length() >= 9) {
					sbNome.append(nome.substring(1,9));
				} else {
					sbNome.append(nome);
				}
			}
			sbNome.append("-Mã");
		} else if(truncate(vValor) > 9999) {
			vValor = (vValor / 1000);
			if(StringUtils.isNotBlank(nome)) {
				if(nome.length() >= 9) {
					sbNome.append(nome.substring(1,9));
				} else {
					sbNome.append(nome);
				}
			}
			sbNome.append("-Mi");								
		} else {
			if(StringUtils.isNotBlank(nome)) {
				if(nome.length() >= 12) {
					sbNome.append(nome.substring(1,12));
				} else {
					sbNome.append(nome);
				}
			}
		}
		return sbNome.toString();
	}
	
	private String obterNomeSumario(Double vValor, AelResultadoExame resultadoExame) {
		if(resultadoExame.getValor() != null && resultadoExame.getParametroCampoLaudo() != null) {
			vValor = (resultadoExame.getValor() / Math.pow(10, (Short) CoreUtil.nvl(resultadoExame.getParametroCampoLaudo().getQuantidadeCasasDecimais(),Short.valueOf("0"))));
		}
		StringBuilder sbNomeSumario = new StringBuilder();
		String nomeSumario = "";
		if(resultadoExame != null && resultadoExame.getParametroCampoLaudo() != null && resultadoExame.getParametroCampoLaudo().getCampoLaudo() != null) {
			nomeSumario = resultadoExame.getParametroCampoLaudo().getCampoLaudo().getNomeSumario();
		}
		if(truncate(vValor) > 99999999) {								
			vValor = (vValor / 1000000);
			if(StringUtils.isNotBlank(nomeSumario)) {
				if(nomeSumario.length() > 9) {
					sbNomeSumario.append(nomeSumario.substring(1,9));
				} else {
					sbNomeSumario.append(nomeSumario);
				}
			}
			sbNomeSumario.append("-Mã");
		} else if(truncate(vValor) > 9999) {
			vValor = (vValor / 1000);
			if(StringUtils.isNotBlank(nomeSumario)) {
				if(nomeSumario.length() > 9) {
					sbNomeSumario.append(nomeSumario.substring(1,9));
				} else {
					sbNomeSumario.append(nomeSumario);
				}
			}
			sbNomeSumario.append("-Mi");									
		} else {
			if(StringUtils.isNotBlank(nomeSumario)) {
				if(nomeSumario.length() > 12) {
					sbNomeSumario.append(nomeSumario.substring(1,12));
				} else {
					sbNomeSumario.append(nomeSumario);
				}
			}
		}
		return sbNomeSumario.toString();
	}
	
	private String obterResultadoTabLegenda(String vLegenda, String vObservacao) {
		String vResultadoTab = null;
		StringBuilder strResultado = new StringBuilder();
		if(StringUtils.isNotBlank(vLegenda)) {
			if(StringUtils.isNotBlank(vObservacao)) {
				strResultado.append(vLegenda).append('\n').append(vObservacao);
				vResultadoTab = strResultado.toString();
			} else {
				strResultado.append(vLegenda);
				vResultadoTab = strResultado.toString();
			}
		} else {
			if(StringUtils.isNotBlank(vObservacao)) {
				strResultado.append(vObservacao);
				vResultadoTab = strResultado.toString();
			} else {
				vResultadoTab = null;
			}
		}
		return vResultadoTab;
	}

	private Integer zeroIfNull(Object valor) {
		if(valor != null){
			return Integer.valueOf(valor.toString());
		}
		return 0;
	}

	protected AelResultadoExameDAO getAelResultadoExameDAO() { return aelResultadoExameDAO; }	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() { return aelItemSolicitacaoExameDAO; }
	protected AelParametroCamposLaudoDAO getAelParametroCamposLaudoDAO(){ return aelParametroCamposLaudoDAO; }
	protected AelResultadoCaracteristicaDAO getAelResultadoCaracteristicaDAO() { return aelResultadoCaracteristicaDAO; }
	protected AelNotaAdicionalDAO getAelNotaAdicionalDAO() { return aelNotaAdicionalDAO; }
	protected AelResultadoCodificadoDAO getAelResultadoCodificadoDAO() { return aelResultadoCodificadoDAO;}
	
	protected EmitirRelatorioSumarioExamesAltaPopListasRN getEmitirRelatorioSumarioExamesAltaPopListasRN() { return emitirRelatorioSumarioExamesAltaPopListasRN; }
	protected MascaraExamesRN getMascaraExamesRN() { return mascaraExamesRN; }
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {return this.prescricaoMedicaFacade;}
	protected IAghuFacade getAghuFacade() { return this.aghuFacade; }
	protected IParametroFacade getParametroFacade() { return parametroFacade; }
	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() { return this.solicitacaoExameFacade; }
	protected IExamesLaudosFacade getExamesLaudosFacade() { return this.examesLaudosFacade;}
}