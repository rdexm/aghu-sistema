package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndAbsenteismo;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioItemConsultoriaSumarios;
import br.gov.mec.aghu.dominio.DominioLocalCobrancaProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimento;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoItemContaApac;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoDebugCode;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatContaApacDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasInternacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudePlanoDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaApacDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.vo.BuscarProcedHospEquivalentePhiVO;
import br.gov.mec.aghu.faturamento.vo.FatPeriodoApacVO;
import br.gov.mec.aghu.faturamento.vo.RnFatcVerItprocexcVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelIntervaloColeta;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatContaApacId;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatItemContaApac;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * <p>
 * Linhas: 3400 <br/>
 * Cursores: 3 <br/>
 * Forks de transacao: 9 <br/>
 * Consultas: 64 tabelas <br/>
 * Alteracoes: 89 tabelas <br/>
 * Metodos: 28 <br/>
 * Metodos externos: 37 <br/>
 * </p>
 * <p>
 * ORADB: <code>FATK_SUS_RN</code>
 * </p>
 * 
 * @author gandriotti
 * 
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta", "PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class VerificacaoFaturamentoSusRN extends AbstractFatDebugLogEnableRN {


private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

@EJB
private FaturamentoFatkCpeRN faturamentoFatkCpeRN;

@EJB
private FaturamentoFatkSmaRN faturamentoFatkSmaRN;

@EJB
private ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON;

private static final Log LOG = LogFactory.getLog(VerificacaoFaturamentoSusRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private VerificaCaracteristicaItemProcedimentoHospitalarRNCache verificaCaracteristicaItemProcedimentoHospitalarRNCache;

@Inject
private FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO;

@EJB
private IPacienteFacade pacienteFacade;

@Inject
private FatContaApacDAO fatContaApacDAO;

@EJB
private IExamesFacade examesFacade;

@Inject
private FatItemContaApacDAO fatItemContaApacDAO;

@EJB
private ItemContaApacPersist itemContaApacPersist;

@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final long serialVersionUID = -6077556877979006821L;

	private static final String MAGIC_STRING_SIT_COLETA_EQ_AE = "AE";
	private static final String MAGIC_STRING_SIT_COLETA_EQ_EO = "EO";
	private static final String MAGIC_STRING_SIT_COLETA_EQ_EX = "EX";
	private static final String MAGIC_STRING_SIT_COLETA_EQ_LI = "LI";

	private static final Short MAGIC_NUMBER_75_CNV_CODIGO = Short.valueOf((short) 75);

	/**
	 * 
	 * @return
	 */
	protected FaturamentoFatkSmaRN getFatkSmaRn() {

		return faturamentoFatkSmaRN;
	}

	/**
	 * 
	 * @return
	 */
	protected FaturamentoFatkCpeRN getFatkCpeRn() {

		return faturamentoFatkCpeRN;
	}

	protected FatContaApacDAO getFatContaApacDAO() {

		return fatContaApacDAO;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}

	protected FatProcedAmbRealizadoDAO getFatProcedAmbRealizadoDAO() {
		return fatProcedAmbRealizadoDAO;
	}

	protected FatItemContaApacDAO getFatItemContaApacDAO() {
		return fatItemContaApacDAO;
	}

	protected ProcedimentosAmbRealizadosON getProcedimentosAmbRealizadosON() {
		return procedimentosAmbRealizadosON;
	}

	protected ItemContaApacPersist getItemContaApacPersist() {
		return itemContaApacPersist;
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_SUS_RN.RN_FATC_VER_CONS_PAC</code>
	 * </p>
	 * 
	 * @param codPaciente
	 * @param dataTransplante
	 * @param codTTR
	 * @return
	 */
	@SuppressWarnings("ucd")
	public Integer obterNumeroConsultaPorPacienteTtrCodigoDtTransplante(Integer codPaciente, Date dataTransplante, String codTTR) {
		final List<FatCompetencia> listaComp = getFatCompetenciaDAO().obterListaAtivaPorModulo(DominioModuloCompetencia.APAP);
		
		if (listaComp != null && !listaComp.isEmpty()) {
			final FatCompetencia competencia = listaComp.get(0);
			
			
			Date fimCpe = ManipulacaoDatasUtil.getLastDay(competencia.getId().getAno().intValue(), competencia.getId().getMes().intValue());
			
			final List<Integer> nroConsultas = getAmbulatorioFacade().obterNumeroDasConsultas( codPaciente, dataTransplante, 
																							   competencia.getId().getDtHrInicio(), 
																							   fimCpe, 
																							   codTTR,
																							   DominioIndAbsenteismo.R);
			if(nroConsultas != null && !nroConsultas.isEmpty()){
				return nroConsultas.get(0);
			}
			
		}

		return null;
	}

	/**
	 * <p>
	 * <b>ATENCAO</b> Comportamento modificado: <br/>
	 * <li>Original: retorna o primeiro item {@link FatCompetencia} encontrado
	 * para o <code>modulo</code> <br/>
	 * <li>Modificado: retorna a lista de {@link FatCompetencia} encontrada para
	 * o <code>modulo</code>
	 * </p>
	 * <p>
	 * ORADB: <code>FATK_SUS_RN.RN_FATC_VER_COMP_ABE</code>
	 * </p>
	 * 
	 * @param modulo
	 * @return
	 * @throws ApplicationBusinessException
	 * @see FaturamentoExceptionCode#FAT_00453
	 */
	public List<FatCompetencia> obterCompetenciasAbertasNaoFaturadasPorModulo(DominioModuloCompetencia modulo) throws ApplicationBusinessException {
		final List<FatCompetencia> result = getFatCompetenciaDAO().obterListaAtivaAbertaNaoFaturadaPorModulo(modulo);
		
		if ((result == null) || result.isEmpty()) {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00453);
		}

		return result;
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_SUS_RN.RN_FATC_ASS_CON_APAC</code>
	 * </p>
	 * 
	 * @param sexoPaciente
	 * @param sexoProcedimento
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws BaseException
	 */
	public Integer associarConsultaApac(final FatContaApacId fatContaApacId, final Date cpeDtHrInicio, final DominioModuloCompetencia cpeModulo,
			final Byte cpeMes, final Short cpeAno, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		int count = 0;
		FatContaApacDAO apacDAO = getFatContaApacDAO();
		String parametro = buscarAghParametro(AghuParametrosEnum.P_PAR_DOMINIO_ENCERRAMENTO_AMB).getVlrTexto();
		List<FatPeriodoApacVO> apacVOs = apacDAO.buscarDadosApacAssociacao(fatContaApacId.getAtmNumero(), fatContaApacId.getSeqp(), cpeDtHrInicio,
				cpeModulo, cpeMes, cpeAno, parametro);
		if (apacVOs != null && !apacVOs.isEmpty()) {
			Integer codNefro = buscarAghParametro(AghuParametrosEnum.P_CODIGO_NEFROLOGIA).getVlrNumerico().intValue();
			Short cnvCodigo = buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO).getVlrNumerico().shortValue();
			IAghuFacade aghuFacade = getAghuFacade();
			FatProcedAmbRealizadoDAO ambRealizadoDAO = getFatProcedAmbRealizadoDAO();
			IAmbulatorioFacade ambulatorioFacade = getAmbulatorioFacade();
		
			IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();

			for (FatPeriodoApacVO fatPeriodoApacVO : apacVOs) {
				// ---
				// -- Marina 01/02/2011
				// -- IF r_periodo_apac.dt_inicio_validade < p_cpe_dt_hr_inicio
				// THEN
				// -- v_data_inicio_apac := p_cpe_dt_hr_inicio;
				// -- ELSE
				// -- v_data_inicio_apac := r_periodo_apac.dt_inicio_validade;
				// -- END IF;
				// ---
				if (fatPeriodoApacVO.getIndTipoTratamento() != null && fatPeriodoApacVO.getIndTipoTratamento().getCodigo() == codNefro) {
					// associa_nefro
					List<AacConsultas> aacConsultas = ambulatorioFacade.buscarConsultaPorProcedAmbRealizadoEspecialidade(
							fatPeriodoApacVO.getCodigoPaciente(), cpeDtHrInicio, fatPeriodoApacVO.getDtInicioValidadeContaApac(),
							fatPeriodoApacVO.getDataFimValidade(), cnvCodigo, fatPeriodoApacVO.getIndTipoTratamento().getCodigo(), cpeMes, cpeAno);
					if (aacConsultas != null && !aacConsultas.isEmpty()) {
						for (AacConsultas aacConsulta : aacConsultas) {
							if (aacConsulta.getAtendimento() == null) {
								AacConsultas consultaAnterior = ambulatorioFacade.clonarConsulta(aacConsulta);
								aacConsulta.setAtendimento(aghuFacade.obterAghAtendimentoPorChavePrimaria(fatPeriodoApacVO.getAtdSeq()));
								ambulatorioFacade.atualizarConsulta(aacConsulta, consultaAnterior, nomeMicrocomputador, new Date(), false);
							}
							List<FatProcedAmbRealizado> ambRealizados = ambRealizadoDAO.buscarPorConsultaAtendimento(aacConsulta.getNumero(),
									fatPeriodoApacVO.getAtdSeq());

							if (ambRealizados != null && !ambRealizados.isEmpty()) {
								for (FatProcedAmbRealizado fatProcedAmbRealizado : ambRealizados) {
									FatProcedAmbRealizado oldProcedAmbRealizado = faturamentoFacade
											.clonarFatProcedAmbRealizado(fatProcedAmbRealizado);
									fatProcedAmbRealizado.setAtendimento(aghuFacade.obterAghAtendimentoPorChavePrimaria(fatPeriodoApacVO.getAtdSeq()));
									faturamentoFacade.atualizarProcedimentoAmbulatorialRealizado(fatProcedAmbRealizado, oldProcedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
								}
							}
						}
						count += aacConsultas.size();
						// -- Milena fevereiro/2005 - Os médicos estão lançando
						// as sessões, portanto já estarão na pmr
						// -- alterando o atendimento fica mais fácil de
						// computá-las na estatística
					}

				} else {
					List<AacConsultas> aacConsultas = ambulatorioFacade.buscarApacAssociacao(fatPeriodoApacVO.getCodigoPaciente(), fatPeriodoApacVO
							.getDtInicioValidadeContaApac(), fatPeriodoApacVO.getDataFimValidade(), cnvCodigo, fatPeriodoApacVO
							.getIndTipoTratamento().getCodigo());
					if (aacConsultas != null && !aacConsultas.isEmpty()) {
						for (AacConsultas aacConsulta : aacConsultas) {
							if (aacConsulta.getAtendimento() == null) {
								AacConsultas consultaAnterior = ambulatorioFacade.clonarConsulta(aacConsulta);
								aacConsulta.setAtendimento(aghuFacade.obterAghAtendimentoPorChavePrimaria(fatPeriodoApacVO.getAtdSeq()));
								ambulatorioFacade.atualizarConsulta(aacConsulta, consultaAnterior, nomeMicrocomputador, new Date(), false);
							}
						}
						count += aacConsultas.size();
					}
				}
			}
		}
		return count;
	}

	/**
	 * <p>
	 * TODO {@link DominioSexo} e {@link DominioSexoDeterminante} nao sao
	 * compativeis. Possivel problema.
	 * </p>
	 * <p>
	 * ORADB: <code>FATK_SUS_RN.RN_FATC_VER_SEXOCOMP</code>
	 * </p>
	 * 
	 * @param sexoPaciente
	 * @param sexoProcedimento
	 * @return
	 */
	public boolean verificarCompatibilidadeSexoPacProc(DominioSexo sexoPaciente, DominioSexoDeterminante sexoProcedimento) {

		if (DominioSexoDeterminante.Q.equals(sexoProcedimento) || (sexoPaciente != null && DominioSexoDeterminante.Q.toString().equals(sexoPaciente.toString()))) {
			return true;
		} 

		if (sexoPaciente == null || sexoProcedimento == null) {
			return true;
		}
		
		if (sexoPaciente.toString().equals(sexoProcedimento.toString())) {
			return true;
		} else {
			return false;
		}
	}

	public List<RnFatcVerItprocexcVO> verificarItemProcHosp(Integer phiSeq, Short qtdRealizada, Short cnvCodigo, Byte cnvCspSeq)
			throws BaseException {
		return this.verificarItemProcHosp(phiSeq, qtdRealizada, cnvCodigo, cnvCspSeq, null, null);
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_SUS_RN.RN_FATC_VER_ITPROC</code>
	 * </p>
	 * 
	 * @param phiSeq
	 * @param quantidadeRealizada
	 * @param cnvCodigo
	 * @param cnvSeq
	 * @param origemProc
	 * @return
	 * @throws BaseException
	 * @see FatConvGrupoItemProced
	 * @see FatItensProcedHospitalar
	 * @see FatProcedHospInternos
	 * @see FatConvenioSaudePlano
	 */
	public List<RnFatcVerItprocexcVO> verificarItemProcHosp(Integer phiSeq, Short pQtdRealizada, Short cnvCodigo, Byte cnvCspSeq, Integer maxValues
			, Map<String, List<BuscarProcedHospEquivalentePhiVO>> rnCthcVerItemSusVOMap)
			throws BaseException {
		VerificaCaracteristicaItemProcedimentoHospitalarRNCache verificaCaracteristicaItemProcedimentoHospitalarRNCache = this
				.getVerificaCaracteristicaItemProcedimentoHospitalarRNCache();
		FatConvenioSaudePlanoDAO fatConvenioSaudePlanoDAO = this.getFatConvenioSaudePlanoDAO();

		List<RnFatcVerItprocexcVO> result = new ArrayList<RnFatcVerItprocexcVO>();

		int vIndice = 0;
		Short vQtdFalta = 0;
		String vGrpItmAnt = "XXXX";
		Boolean vCaract = Boolean.FALSE;
		
		// busca tipo convenio incl 290502 ETB
		FatConvenioSaudePlano vTipoPlano = fatConvenioSaudePlanoDAO.obterPorChavePrimaria(new FatConvenioSaudePlanoId(cnvCodigo, cnvCspSeq));
		DominioTipoPlano indTipoPlano = vTipoPlano != null ? vTipoPlano.getIndTipoPlano() : null;
		
		// Tipo grupo conta padrão para convenio SUS
		Short vGrcSus = buscarVlrShortAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		List<BuscarProcedHospEquivalentePhiVO> list = doBuscaProcedHospEquivalentePhi(
				phiSeq, pQtdRealizada,
				cnvCodigo, cnvCspSeq, rnCthcVerItemSusVOMap,
				vGrcSus
		);
		
		if (list != null && !list.isEmpty()) {
			for (BuscarProcedHospEquivalentePhiVO procedHospEquivalentePhiVO : list) {
				// se mudou a "familia" do item, qtd_falta passa a ser qtd realizada
				if (DominioTipoPlano.A.equals(indTipoPlano) || !procedHospEquivalentePhiVO.getGrp().equals(vGrpItmAnt)) {
					vQtdFalta = pQtdRealizada;
					vGrpItmAnt = procedHospEquivalentePhiVO.getGrp();
				}

				// alteração para característica qtde no código etb 28.08.2001
				vCaract = verificaCaracteristicaItemProcedimentoHospitalarRNCache.verificarCaracteristicaItemProcHosp(
						procedHospEquivalentePhiVO.getPho(), procedHospEquivalentePhiVO.getSeq(),
						DominioFatTipoCaractItem.COBRA_EM_CODIGO_REPRES_QTDE);

				if (Boolean.TRUE.equals(vCaract) && vQtdFalta > 0) {
					vIndice++;
					logar("Encontrou característica indice {0} IPH {1}", vIndice, procedHospEquivalentePhiVO.getSeq());
					result.add(new RnFatcVerItprocexcVO(procedHospEquivalentePhiVO.getPho(),
							(procedHospEquivalentePhiVO.getQtd() == null ? pQtdRealizada : (short) 1), procedHospEquivalentePhiVO.getSeq()));

					if (maxValues != null && result.size() >= maxValues) {
						return result;
					}

					break;
				} else {
					// fim alteração
					Short nvlQtd = nvl(procedHospEquivalentePhiVO.getQtd(), pQtdRealizada);
					if (CoreUtil.menorOuIgual(nvlQtd, vQtdFalta)) {
						Short vQtd = (short) (vQtdFalta / nvlQtd);
						vQtdFalta = (short) (vQtdFalta % nvlQtd);
						for (int i = 0; i < vQtd; i++) {
							vIndice++;
							result.add(new RnFatcVerItprocexcVO(procedHospEquivalentePhiVO.getPho(),
									(procedHospEquivalentePhiVO.getQtd() == null ? pQtdRealizada : (short) 1), procedHospEquivalentePhiVO.getSeq()));

							if (maxValues != null && result.size() >= maxValues) {
								return result;
							}
						}
					}
				} // da alteração
			}
		}
		return result;
	}

	private List<BuscarProcedHospEquivalentePhiVO> doBuscaProcedHospEquivalentePhi(
			Integer phiSeq,
			Short pQtdRealizada,
			Short cnvCodigo,
			Byte cnvCspSeq,
			Map<String, List<BuscarProcedHospEquivalentePhiVO>> rnCthcVerItemSusVOMap,
			Short vGrcSus) {
		FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = this.getFatItensProcedHospitalarDAO();
		
		List<BuscarProcedHospEquivalentePhiVO> list;
		if (rnCthcVerItemSusVOMap == null) {
			// busca itens proced hospitalar equivalentes ao proced hospitalar interno
			// ordenados por qtd procedimentos item
			list = fatItensProcedHospitalarDAO.buscarProcedHospEquivalentePhi(
					phiSeq, pQtdRealizada,
					cnvCodigo, cnvCspSeq, vGrcSus
			);
		} else {
			String key = BuscarProcedHospEquivalentePhiVO.getKeyMapBuscaProcedHospEquivalentePhi(phiSeq, pQtdRealizada, cnvCodigo, cnvCspSeq, vGrcSus);
			list = rnCthcVerItemSusVOMap.get(key); 
		}
		return list;
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_SUS_RN.RN_FATC_VER_ITPROCEXC</code>
	 * </p>
	 * 
	 * @param grpItemConta
	 * @param cntHosp
	 * @param pInternacao
	 * @param iphPhoSeqRealiz
	 * @param iphSeqRealiz
	 * @param ttrCodigo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<RnFatcVerItprocexcVO> verificarExcecoesItemProcHosp(Integer phiSeq, Short qtdRealizada, Short cnvCodigo, Byte cnvCspSeq,
			DominioOrigemProcedimento origemProcedimento, Short iphPhoSeqRealizado, Integer iphSeqRealizado, String ttrCodigo)
			throws BaseException {

		List<RnFatcVerItprocexcVO> result = new ArrayList<RnFatcVerItprocexcVO>();

		Short vQtdFalta = 0;
		String vGrpItmAnt = "XXXX";

		// Tipo grupo conta padrão para convenio SUS
		final Short vGrcSus = buscarVlrShortAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);

		// verifica se existe excecao p/ o PHI e SSM enviados
		Long vAux = getFatExcCnvGrpItemProcDAO().obterQtdPorIphPhiGrcCnv(iphPhoSeqRealizado, iphSeqRealizado, phiSeq, vGrcSus, cnvCspSeq, cnvCodigo, ttrCodigo);

		if (CoreUtil.maior(vAux, 0)) {
			
			// existe excecao p/ o PHI e SSM enviados
			// busca itens proced hospitalar equivalentes ao proc hospitalar interno
			// na tabela de excecao ordenados por qtd procedimentos item
			List<BuscarProcedHospEquivalentePhiVO> list = getFatItensProcedHospitalarDAO().buscarProcedHospEquivalentePhiExcecao(iphPhoSeqRealizado,
					iphSeqRealizado, phiSeq, vGrcSus, cnvCspSeq, cnvCodigo, ttrCodigo, qtdRealizada, origemProcedimento);
			if (list != null && !list.isEmpty()) {
				for (BuscarProcedHospEquivalentePhiVO procedHospEquivalentePhiVO : list) {

					// se mudou a "familia" do item, qtd_falta passa a ser qtd realizada
					if ((!procedHospEquivalentePhiVO.getGrp().equals(vGrpItmAnt))) {
						vQtdFalta = qtdRealizada;
						vGrpItmAnt = procedHospEquivalentePhiVO.getGrp();
					}

					Short nvlQtd = nvl(procedHospEquivalentePhiVO.getQtd(), qtdRealizada);
					if (CoreUtil.menorOuIgual(nvlQtd, vQtdFalta)) {
						Short vQtd = BigDecimal.valueOf(vQtdFalta / nvlQtd).shortValue();
						vQtdFalta = (short) (vQtdFalta % nvlQtd);
						for (int i = 0; i < vQtd; i++) {
							result.add(new RnFatcVerItprocexcVO(procedHospEquivalentePhiVO.getPho(),
									(procedHospEquivalentePhiVO.getQtd() == null ? qtdRealizada : (short) 1), procedHospEquivalentePhiVO.getSeq()));
						}
					}
				}
			}
		} else {
			// NAO existe excecao p/ o PHI e SSM enviados
			// busca itens proced hospitalar equivalentes ao proc hospitalar interno
			// ordenados por qtd procedimentos item
			return this.verificarItemProcHosp(phiSeq, qtdRealizada, cnvCodigo, cnvCspSeq);
		}
		return result;
	}

	protected AipPacientes obterAipPacientes(Integer codPaciente) {

		AipPacientes result = null;

		result = getPacienteFacade().obterAipPacientesPorChavePrimaria(codPaciente);

		return result;
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_SUS_RN.RN_FATC_VER_IDADEPAC</code>
	 * </p>
	 * 
	 * @param codPaciente
	 * @return
	 * @see AipPacientes
	 */
	public Short obterIdadePacienteParaDataAtual(Integer codPaciente) {

		Short result = null;
		Integer idade = null;
		AipPacientes pac = null;

		// check args
		if (codPaciente == null) {
			throw new IllegalArgumentException("codPaciente não pode ser nulo.");
		}
		// algo
		pac = this.obterAipPacientes(codPaciente);
		idade = CoreUtil.calculaIdade(pac.getDtNascimento());
		if (idade != null) {
			result = Short.valueOf(idade.shortValue());
		}

		return result;
	}

	protected int obterQtdAghFeriados(Date inicio, Date fim) {

		Long result = null;

		
		result = getAghuFacade().obterQtdFeriadosEntreDatas(inicio, fim);

		return result != null ? result.intValue() : 0;
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_SUS_RN.RN_FATC_VER_DIAUTEIS</code>
	 * </p>
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @param descontarFeriados
	 * @return
	 * @see AghFeriados
	 */
	public Integer obterQuantidadeDiasUteisEntreDatas(Date dataInicio, Date dataFim, boolean descontarFeriados) {

		if (dataInicio == null || dataFim == null) {
			return null;
		}
		
		int result = 0;
		
		// algo
		result = ManipulacaoDatasUtil.getDifferenceInDays(dataFim, dataInicio);
		// desconta dias de fim de semana (sabado/domingo)
		result -= ManipulacaoDatasUtil.getWeekDayAmountBetween(dataInicio, dataFim, Calendar.SATURDAY);
		result -= ManipulacaoDatasUtil.getWeekDayAmountBetween(dataInicio, dataFim, Calendar.SUNDAY);
		// Conta numero de feriados
		if (descontarFeriados) {
			result -= this.obterQtdAghFeriados(dataInicio, dataFim);
		}

		return result;
	}

	protected List<FatProcedAmbRealizado> obterListaFatProcedAmbRealizadoPorSolicitacaoConvenio(Integer soeSeq, Short cnvCodigo, Byte seq) {

		List<FatProcedAmbRealizado> result = null;
		FatProcedAmbRealizadoDAO dao = null;

		dao = this.getFatProcedAmbRealizadoDAO();
		result = dao.obterListaPorSolicitacaoConvenio(soeSeq, cnvCodigo, seq);

		return result;
	}

	protected List<FatItemContaApac> obterListaFatItemContaApacPorSolicitacao(Integer soeSeq) {

		List<FatItemContaApac> result = null;
		FatItemContaApacDAO dao = null;

		dao = this.getFatItemContaApacDAO();
		result = dao.obterListaPorSolicitacao(soeSeq);

		return result;
	}

	protected List<FatItemContaApac> obterListaFatItemContaApacPorSolicitacaoNaoPouE(Integer soeSeq) {

		List<FatItemContaApac> result = null;
		FatItemContaApacDAO dao = null;

		dao = this.getFatItemContaApacDAO();
		result = dao.obterListaPorSolicitacaoNaoPouE(soeSeq);

		return result;
	}

	protected void atualizarSitacaoIcaParaCancelada(Integer soeSeq, String nomeMicrocomputador) throws BaseException {

		List<FatItemContaApac> listaIca = null;
		ItemContaApacPersist icaPersist = null;

		listaIca = this.obterListaFatItemContaApacPorSolicitacaoNaoPouE(soeSeq);
		icaPersist = this.getItemContaApacPersist();
		for (FatItemContaApac ica : listaIca) {
			ica.setIndSituacao(DominioSituacaoItemContaApac.C);
			icaPersist.atualizar(ica, nomeMicrocomputador, new Date());
		}
	}

	protected void estornarItensContaApac(Integer soeSeq, String nomeMicrocomputador) throws BaseException, ApplicationBusinessException {

		List<FatItemContaApac> listaIca = null;
		ItemContaApacPersist icaPersist = null;

		listaIca = this.obterListaFatItemContaApacPorSolicitacao(soeSeq);
		if ((listaIca != null) && !listaIca.isEmpty()) {
			icaPersist = this.getItemContaApacPersist();
			try {
				for (FatItemContaApac ica : listaIca) {
					icaPersist.remover(ica, nomeMicrocomputador, new Date());
				}
			} catch (IllegalStateException e1) {
				logError(EXCECAO_CAPTURADA, e1);
				this.atualizarSitacaoIcaParaCancelada(soeSeq, nomeMicrocomputador);
			} catch (BaseException e2) {
				logError(EXCECAO_CAPTURADA, e2);
				this.atualizarSitacaoIcaParaCancelada(soeSeq, nomeMicrocomputador);
			}
		} else {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.ERRO_ESTORNO_ITEM_CONTA_APAC_VAZIO);
		}
	}

	protected List<FatProcedAmbRealizado> obterListaFatProcedAmbRealizadoPorSolicitacao(Integer soeSeq) {

		List<FatProcedAmbRealizado> result = null;
		FatProcedAmbRealizadoDAO dao = null;

		dao = this.getFatProcedAmbRealizadoDAO();
		result = dao.obterListaPorSolicitacao(soeSeq);

		return result;
	}

	protected List<FatProcedAmbRealizado> obterListaFatProcedAmbRealizadoPorSolicitacaoNaoPouE(Integer soeSeq) {

		List<FatProcedAmbRealizado> result = null;
		FatProcedAmbRealizadoDAO dao = null;

		dao = this.getFatProcedAmbRealizadoDAO();
		result = dao.obterPorSolicitacaoNaoPouE(soeSeq);

		return result;
	}

	protected void atualizarSitacaoPmrParaCancelada(Integer soeSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		List<FatProcedAmbRealizado> listaPmr = null;
		ProcedimentosAmbRealizadosON pmrPersist = null;

		listaPmr = this.obterListaFatProcedAmbRealizadoPorSolicitacaoNaoPouE(soeSeq);
		pmrPersist = this.getProcedimentosAmbRealizadosON();
		IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
		for (FatProcedAmbRealizado pmr : listaPmr) {
			FatProcedAmbRealizado oldAmbRealizado = faturamentoFacade.clonarFatProcedAmbRealizado(pmr);

			pmr.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
			pmrPersist.atualizarProcedimentoAmbulatorialRealizado(pmr, oldAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor, null);
		}
	}

	protected void estornarProcedimentoAmbRealizados(Integer soeSeq,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException,
			ApplicationBusinessException {

		ProcedimentosAmbRealizadosON pmrPersist = null;
		List<FatProcedAmbRealizado> listaPmr = null;

		listaPmr = this.obterListaFatProcedAmbRealizadoPorSolicitacao(soeSeq);
		if ((listaPmr != null) && !listaPmr.isEmpty()) {
			pmrPersist = this.getProcedimentosAmbRealizadosON();
			try {
				for (FatProcedAmbRealizado pmr : listaPmr) {
					pmrPersist.excluirProcedimentoAmbulatorialRealizado(pmr, nomeMicrocomputador, dataFimVinculoServidor, null);
				}
			} catch (IllegalStateException e1) {
				LOG.error(EXCECAO_CAPTURADA, e1);
				this.atualizarSitacaoPmrParaCancelada(soeSeq, nomeMicrocomputador, dataFimVinculoServidor);
			} catch (BaseException e2) {
				LOG.error(EXCECAO_CAPTURADA, e2);
				this.atualizarSitacaoPmrParaCancelada(soeSeq, nomeMicrocomputador, dataFimVinculoServidor);
			}
		} else {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.ERRO_ESTORNO_ITEM_PROC_AMB_VAZIO);
		}
	}

	protected List<FatItemContaHospitalar> obterListaFatItensContaHospitalarPorSolicitacao(Integer soeSeq) {

		List<FatItemContaHospitalar> result = null;
		FatItemContaHospitalarDAO dao = null;

		dao = this.getFatItemContaHospitalarDAO();
		result = dao.obterListaPorSolicitacao(soeSeq);

		return result;
	}

	protected void atualizarSitacaoIchParaCancelada(Integer soeSeq) throws BaseException {

		List<FatItemContaHospitalar> listaIch = null;
		ItemContaHospitalarON ichPersist = null;
		FatItemContaHospitalar ichOrig = null;
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		listaIch = this.obterListaFatItensContaHospitalarPorSolicitacao(soeSeq);
		ichPersist = this.getItemContaHospitalarON();
		final Date dataFimVinculoServidor = new Date();
		for (FatItemContaHospitalar ich : listaIch) {
			try {
				ichOrig = ichPersist.clonarItemContaHospitalar(ich);
			} catch (Exception e) {
				logError(EXCECAO_CAPTURADA, e);
				FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
			}
			ich.setIndSituacao(DominioSituacaoItenConta.C);
			ichPersist.atualizarItemContaHospitalarSemValidacoesForms(ich, servidorLogado, ichOrig, dataFimVinculoServidor);
		}
	}
	
	private IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}	

	protected void estornarItensContaHosp(Integer soeSeq) throws BaseException, ApplicationBusinessException {

		ItemContaHospitalarON ichPersist = null;
		List<FatItemContaHospitalar> listaIch = null;

		listaIch = this.obterListaFatItensContaHospitalarPorSolicitacao(soeSeq);
		ichPersist = this.getItemContaHospitalarON();
		try {
			for (FatItemContaHospitalar ich : listaIch) {
				ichPersist.removerContaHospitalar(ich, null);
			}
		} catch (IllegalStateException e1) {
			logError(EXCECAO_CAPTURADA, e1);
			this.atualizarSitacaoIchParaCancelada(soeSeq);
		} catch (BaseException e2) {
			logError(EXCECAO_CAPTURADA, e2);
			this.atualizarSitacaoIchParaCancelada(soeSeq);
		}
	}

	protected List<AelItemSolicitacaoExames> obterListaAelItemSolicitacaoExamesPorSolicitacaoSitCodigo(Integer soeSeq, String... sitCodigo) {

		List<AelItemSolicitacaoExames> result = null;
		

		
		result = getExamesFacade().obterPorSolicitacaoSitCodigo(soeSeq, sitCodigo);

		return result;
	}

	protected List<FatProcedHospInternos> obterListaFatProcedHospInternosPorExaSiglaMatSeq(String exameSigla, Integer materialAnaliseSeq) {

		List<FatProcedHospInternos> result = null;
		FatProcedHospInternosDAO dao = null;

		dao = this.getFatProcedHospInternosDAO();
		result = dao.obterListaPorExaSiglaMatSeq(exameSigla, materialAnaliseSeq);

		return result;
	}

	protected short obterQuantidade(AelItemSolicitacaoExames ise) {

		short result = 0;
		AelIntervaloColeta ico = null;
		String exaSigla = null;
		Integer manSeq = null;

		exaSigla = ise.getExame().getSigla();
		manSeq = ise.getMaterialAnalise().getSeq();
		ico = ise.getIntervaloColeta();
		// nvl(ise.nro_amostras,nvl(ico.nro_coletas,1)) quantidade,
		if (ise.getNroAmostras() != null) {
			result = ise.getNroAmostras().shortValue();
		} else if ((ico != null) && exaSigla.equals(ico.getEmaExaSigla()) && manSeq.equals(ico.getEmaManSeq())) {
			// and ise.ufe_ema_exa_sigla = ico.ema_exa_sigla(+)
			// and ise.ufe_ema_man_seq = ico.ema_man_seq(+)
			// and ise.ico_seq = ico.seq(+) -- ise.getIntervaloColeta() incluir
			// null e testar sigla + mat;
			result = ico.getNroColetas().shortValue();
		} else {
			result = 1;
		}

		return result;
	}

	protected void inserirItensSolLiberadosEmFatProcAmbRealizados(AelSolicitacaoExames soe, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {

		Date dataHoraLiberada = null;
		Date dataCriacao = null;
		short quantidade = 0;
		AghUnidadesFuncionais unFunc = null;
		AghEspecialidades especialidade = null;
		FatConvenioSaudePlano convenio = null;
		AipPacientes paciente = null;
		List<AelItemSolicitacaoExames> iseList = null;
		AghAtendimentos atd = null;
		List<FatProcedHospInternos> phiList = null;
		String exaSigla = null;
		Integer manSeq = null;
		FatProcedAmbRealizado entidade = null;
		ProcedimentosAmbRealizadosON pmrPersist = null;

		iseList = this.obterListaAelItemSolicitacaoExamesPorSolicitacaoSitCodigo(soe.getSeq(), MAGIC_STRING_SIT_COLETA_EQ_LI);
		if ((iseList != null) && !iseList.isEmpty()) {
			pmrPersist = this.getProcedimentosAmbRealizadosON();
			atd = soe.getAtendimento();
			especialidade = atd.getEspecialidade();
			paciente = atd.getPaciente();
			convenio = soe.getConvenioSaudePlano();
			dataCriacao = DateUtil.adicionaDias(new Date(), Integer.valueOf(-1)); // sysdate
																					// -
																					// 1
			for (AelItemSolicitacaoExames ise : iseList) {
				exaSigla = ise.getExame().getSigla();
				manSeq = ise.getMaterialAnalise().getSeq();
				dataHoraLiberada = ise.getDthrLiberada();
				unFunc = ise.getUnidadeFuncional();
				// nvl(ise.nro_amostras,nvl(ico.nro_coletas,1)) qtd
				quantidade = this.obterQuantidade(ise);
				phiList = this.obterListaFatProcedHospInternosPorExaSiglaMatSeq(exaSigla, manSeq);
				for (FatProcedHospInternos phi : phiList) {
					entidade = new FatProcedAmbRealizado();
					entidade.setDthrRealizado(dataHoraLiberada);
					entidade.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
					entidade.setQuantidade(Short.valueOf(quantidade));
					entidade.setProcedimentoHospitalarInterno(phi);
					entidade.setUnidadeFuncional(unFunc);
					entidade.setEspecialidade(especialidade);
					entidade.setPaciente(paciente);
					entidade.setConvenioSaudePlano(convenio);
					entidade.setIndOrigem(DominioOrigemProcedimentoAmbulatorialRealizado.EXA);
					entidade.setLocalCobranca(DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B);
					entidade.setItemSolicitacaoExame(ise);
					entidade.setCriadoEm(dataCriacao);
					pmrPersist.inserirFatProcedAmbRealizado(entidade, nomeMicrocomputador, dataFimVinculoServidor);
				}
			}
		}
	}

	protected AelExtratoItemSolicitacao obterAelExtratoItemSolicitacaoPorItemSolicitacaoSitCodigoOrdenadoPorDataPrimeiraEntrada(Integer soeSeq,
			Short seqp, String sitCodigo) {

		AelExtratoItemSolicitacao result = null;
		
		result = getExamesFacade().obterListaPorItemSolicitacaoSitCodigoOrdenadoPorDataPrimeiraEntrada(soeSeq, seqp, sitCodigo);

		return result;
	}

	protected FatContasHospitalares obterFatContasHospitalaresPorConvenioIntAltaDataListaSeqListaSitOrdenadoPorSeqPrimeiraEntrada(
			List<Integer> listaCthSeq, Short cnvCodigo, Byte cspSeq, Date data, DominioSituacaoConta... indSituacao) {

		FatContasHospitalares result = null;
		FatContasHospitalaresDAO dao = null;

		dao = this.getFatContasHospitalaresDAO();
		result = dao.obterListaPorConvenioIntAltaDataListaSeqListaSitOrdenadoPorSeqPrimeiraEntrada(listaCthSeq, cnvCodigo, cspSeq, data, indSituacao);

		return result;
	}

	protected List<Integer> obterListaCthSeqViaFatContasInternacaoPorAtendimento(AghAtendimentos atd) {

		List<Integer> result = null;
		FatContasInternacaoDAO dao = null;

		dao = this.getFatContasInternacaoDAO();
		result = dao.obterListaCthSeqPorAtendimento(atd.getInternacao().getSeq());

		return result;
	}

	protected void inserirItensSolLiberadosEmFatItemContaHospitalar(AelSolicitacaoExames soe) throws BaseException {

		List<AelItemSolicitacaoExames> iseList = null;
		AelExtratoItemSolicitacao eis = null;
		List<Integer> listaCthSeq = null;
		FatContasHospitalares conta = null;
		AghUnidadesFuncionais unFunc = null;
		AghAtendimentos atd = null;
		Short cnvCodigo = null;
		Byte cspSeq = null;
		Date dataCriacao = null;
		FatItemContaHospitalar entidade = null;
		Date dataEvento = null;
		List<FatProcedHospInternos> phiList = null;
		String exaSigla = null;
		Integer manSeq = null;
		ItemContaHospitalarON ichPersist = null;
		short quantidade = 0;
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		iseList = this.obterListaAelItemSolicitacaoExamesPorSolicitacaoSitCodigo(soe.getSeq(), new String[] { MAGIC_STRING_SIT_COLETA_EQ_AE,
				MAGIC_STRING_SIT_COLETA_EQ_EO, MAGIC_STRING_SIT_COLETA_EQ_EX, MAGIC_STRING_SIT_COLETA_EQ_LI });
		if ((iseList != null) && !iseList.isEmpty()) {
			atd = soe.getAtendimento();
			cspSeq = soe.getConvenioSaudePlano().getId().getSeq();
			cnvCodigo = soe.getConvenioSaudePlano().getId().getCnvCodigo();
			dataCriacao = soe.getCriadoEm();
			// busca data e hora da entrada em AE
			for (AelItemSolicitacaoExames ise : iseList) {
				// v_dthr_evento
				eis = this.obterAelExtratoItemSolicitacaoPorItemSolicitacaoSitCodigoOrdenadoPorDataPrimeiraEntrada(ise.getId().getSoeSeq(), ise
						.getId().getSeqp(), MAGIC_STRING_SIT_COLETA_EQ_AE);
				dataEvento = (eis != null ? eis.getDataHoraEvento() : null);
				// v_cth_seq
				listaCthSeq = this.obterListaCthSeqViaFatContasInternacaoPorAtendimento(atd);
				exaSigla = ise.getExame().getSigla();
				unFunc = ise.getUnidadeFuncional();
				manSeq = ise.getMaterialAnalise().getSeq();
				phiList = this.obterListaFatProcedHospInternosPorExaSiglaMatSeq(exaSigla, manSeq);
				conta = this.obterFatContasHospitalaresPorConvenioIntAltaDataListaSeqListaSitOrdenadoPorSeqPrimeiraEntrada(listaCthSeq, cnvCodigo,
						cspSeq, dataCriacao, new DominioSituacaoConta[] { DominioSituacaoConta.A, DominioSituacaoConta.F });
				if (conta != null) {
					// nvl(ise.nro_amostras,nvl(ico.nro_coletas,1)) qtd
					quantidade = this.obterQuantidade(ise);
					final Date dataFimVinculoServidor = new Date();
					for (FatProcedHospInternos phi : phiList) {
						entidade = new FatItemContaHospitalar();
						entidade.setContaHospitalar(conta);
						entidade.setDthrRealizado(dataEvento);
						entidade.setIndSituacao(DominioSituacaoItenConta.A);
						entidade.setQuantidade(Short.valueOf(quantidade));
						entidade.setQuantidadeRealizada(Short.valueOf(quantidade));
						entidade.setIchType(DominioItemConsultoriaSumarios.ICH);
						entidade.setProcedimentoHospitalarInterno(phi);
						entidade.setUnidadesFuncional(unFunc);
						entidade.setIndOrigem(DominioIndOrigemItemContaHospitalar.AEL);
						entidade.setIseSeqp(ise.getId().getSeqp());
						entidade.setIseSoeSeq(ise.getId().getSoeSeq());
						ichPersist = this.getItemContaHospitalarON();
						ichPersist.atualizarItemContaHospitalarSemValidacoesForms(entidade, servidorLogado, null,dataFimVinculoServidor);
					}
				} else {
					throw new ApplicationBusinessException(FaturamentoExceptionCode.ERRO_CONTA_HOSP_NAO_ENCONTRADA);
				}
			}
		}
	}

	/**
	 * TODO: Nao foi testado!!!!
	 * <p>
	 * Acerta os registros na troca de convenio Internacao/Ambulatorio<br/>
	 * Ha tres situacoes possiveis de troca de convenio/plano para serem
	 * tratadas <br/>
	 * <ol>
	 * <li> <code>convenio new == SUS && convenio old == SUS</code> <br/>
	 * <ul>
	 * <li>TROCA DE PLANO <br/>
	 * <li>ESTORNAR DO PLANO OLD <br/>
	 * <li>INSERIR NO PLANO NEW <br/>
	 * </ul>
	 * <li> <code>convenio new != SUS && convenio old == SUS</code> <br/>
	 * <ul>
	 * <li>TROCA DE CONVENIO<br/>
	 * <li>ESTORNAR DO FATURAMENTO <br/>
	 * </ul>
	 * <li> <code>convenio new == SUS && convenio old != SUS</code> <br/>
	 * <ul>
	 * <li>TROCA DE CONVENIO <br/>
	 * <li>INSERIR NO FATURAMENTO <br/>
	 * </ul>
	 * </ol>
	 * </p>
	 * <p>
	 * ORADB: <code>FATK_SUS_RN.RN_FATP_ATU_TRCNVAEL</code>
	 * </p>
	 * 
	 * @param modificada
	 * @param original
	 * @param dataFimVinculoServidor 
	 * @return
	 * @throws BaseException
	 * @throws IllegalStateException
	 * @see FaturamentoExceptionCode#ERRO_ESTORNO_ITEM_CONTA_APAC_VAZIO
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public boolean atualizarFaturamentoSolicitacaoExames(AelSolicitacaoExames modificada, AelSolicitacaoExames original, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException {

		boolean result = false;
		Short cnvOrig = null;
		Short cnvMod = null;
		String evento = null;
		Integer soeSeq = null;
		DominioTipoPlano tipoMod = null;
		DominioTipoPlano tipoOrig = null;
		DominioGrupoConvenio grpOrig = null;
		DominioGrupoConvenio grpMod = null;
		ProcedimentosAmbRealizadosON pmrPersist = null;
		List<FatProcedAmbRealizado> listaPmr = null;

		// arg check
		if (original == null) {
			throw new IllegalArgumentException("original não pode ser nulo.");
		}
		if (modificada == null) {
			throw new IllegalArgumentException("modificada não pode ser nulo.");
		}
		if (original.getSeq() == null) {
			throw new IllegalArgumentException();
		}
		if (modificada.getSeq() == null) {
			throw new IllegalArgumentException();
		}
		if (!original.getSeq().equals(modificada.getSeq())) {
			throw new IllegalArgumentException();
		}
		// algo
		if (!original.getConvenioSaudePlano().getId().equals(modificada.getConvenioSaudePlano().getId())) {
			soeSeq = original.getSeq();
			cnvOrig = original.getConvenioSaudePlano().getId().getCnvCodigo();
			cnvMod = modificada.getConvenioSaudePlano().getId().getCnvCodigo();
			grpOrig = original.getConvenioSaude().getGrupoConvenio();
			grpMod = modificada.getConvenioSaude().getGrupoConvenio();
			tipoOrig = original.getConvenioSaudePlano().getIndTipoPlano();
			tipoMod = modificada.getConvenioSaudePlano().getIndTipoPlano();
			if (MAGIC_NUMBER_75_CNV_CODIGO.equals(cnvMod) && !MAGIC_NUMBER_75_CNV_CODIGO.equals(cnvOrig)) {
				// -- Se entrar em 75 cobranca dos dados somente em AMBULATORIO
				// -- exclui da ICH e inclui na PMR, independentemente do CNV
				// anterior
				evento = "EI";
				tipoOrig = DominioTipoPlano.I;
			} else if (!MAGIC_NUMBER_75_CNV_CODIGO.equals(cnvMod) && MAGIC_NUMBER_75_CNV_CODIGO.equals(cnvOrig)) {
				// Se sair do 75 e grupo do novo CNV for SUS AMBULATORIO, acerta
				// PMR
				// Se sair do 75 e grupo do novo CNV for SUS INTERNACAO, limpa
				// PMR e insere ICH
				// Se sair do 75 e grupo do novo CNV nao for SUS INTERNACAO,
				// exclui PMR
				if (DominioGrupoConvenio.S.equals(grpMod)) {
					if (DominioTipoPlano.A.equals(tipoMod)) {
						evento = "A";
					} else if (DominioTipoPlano.I.equals(tipoMod)) {
						evento = "EI";
						tipoOrig = DominioTipoPlano.A;
					}
				} else {
					evento = "E";
					tipoOrig = DominioTipoPlano.A;
				}
			} else if (DominioGrupoConvenio.S.equals(grpMod) && !DominioGrupoConvenio.S.equals(grpOrig)) {
				// nao sendo convenio 75, trata normalmente
				// INSERIR NAS TABELAS DE FATURAMENTO DE ACORDO COM O PLANO
				evento = "I";
			} else if (DominioGrupoConvenio.S.equals(grpMod) && DominioGrupoConvenio.S.equals(grpOrig)) {
				// TROCA DE PLANO NO CONVENIO SUS
				// ESTORNAR DAS TABELAS DO PLANO OLD E INSERIR NAS TABELAS DO
				// PLANO NEW
				evento = "EI";
			} else if (!DominioGrupoConvenio.S.equals(grpMod) && !DominioGrupoConvenio.S.equals(grpOrig)) {
				// ESTORNAR DAS TABELAS DO PLANO OLD
				evento = "E";
			}
			if ("A".equals(evento) && DominioTipoPlano.A.equals(tipoOrig)) {
				// ajusta conv/plano ambulatorio
				// internacao nao precisa pois o cnv/pla e da CTH
				pmrPersist = this.getProcedimentosAmbRealizadosON();
				listaPmr = this.obterListaFatProcedAmbRealizadoPorSolicitacaoConvenio(soeSeq, original.getConvenioSaudePlano().getId()
						.getCnvCodigo(), original.getConvenioSaudePlano().getId().getSeq());
				IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
				for (FatProcedAmbRealizado pmr : listaPmr) {
					FatProcedAmbRealizado oldProcedAmbRealizado = faturamentoFacade.clonarFatProcedAmbRealizado(pmr);

					pmr.setConvenioSaudePlano(modificada.getConvenioSaudePlano());
					pmrPersist.atualizarProcedimentoAmbulatorialRealizado(pmr, oldProcedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor, null);
				}
			}
			if ("E".equals(evento) || "EI".equals(evento)) {
				if (DominioTipoPlano.A.equals(tipoOrig)) {
					// ESTORNAR DE FAT_PROCED_AMB_REALIZADOS OR
					// FAT_ITENS_CONTA_APAC
					this.estornarItensContaApac(soeSeq, nomeMicrocomputador);
					this.estornarProcedimentoAmbRealizados(soeSeq, nomeMicrocomputador, dataFimVinculoServidor);
				} else if (DominioTipoPlano.I.equals(tipoOrig)) {
					// ESTORNAR DE FAT_ITENS_CONTA HOSPITALAR
					this.estornarItensContaHosp(soeSeq);
				}
				if ("I".equals(evento) || "EI".equals(evento)) {
					if (DominioTipoPlano.A.equals(tipoMod)) {
						// INSERIR EM FAT_PROCED_AMB_REALIZADOS
						// OS ITENS DE SOLICITACAO LIBERADOS (AMBULATORIO)
						this.inserirItensSolLiberadosEmFatProcAmbRealizados(modificada, nomeMicrocomputador, dataFimVinculoServidor);
					} else if (DominioTipoPlano.I.equals(tipoMod)) {
						// INSERIR EM FAT_ITENS_CONTA_HOSPITALAR
						this.inserirItensSolLiberadosEmFatItemContaHospitalar(modificada);
					}
				}
			}
		}
		result = true;

		return result;
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_SUS_RN.RN_FATP_ENC_SISMAMA</code>
	 * </p>
	 * 
	 * @param dataFimCompetencia
	 * @param isPrevia
	 * @return
	 * @throws ApplicationBusinessException
	 * @see #getFatkSmaRn()
	 * @see #getFatkCpeRn()
	 */
	@SuppressWarnings("ucd")
	public boolean processarEncerramentoPreviaSismama(Date dataFimCompetencia, boolean isPrevia) throws ApplicationBusinessException {

		boolean result = false;
		boolean statusSma = false;
		boolean statusCpe = false;

		// check args
		if (dataFimCompetencia == null) {
			throw new IllegalArgumentException("dataFimCompetencia não pode ser nulo.");
		}

		DominioModuloCompetencia modulo = DominioModuloCompetencia.MAMA;
		// busca competencias de SISMAMA
		List<FatCompetencia> listaComp = getFatCompetenciaDAO().obterListaAtivaPorModulo(modulo);
		
		if ((listaComp != null) && !listaComp.isEmpty()) {
			this.logDebug(FaturamentoDebugCode.FAT_SUS_ENC_MODULO, modulo.getDescricao(), "Inicio", "OK");
			FatCompetencia competencia = listaComp.get(0);
			statusSma = getFatkSmaRn().rnSmapAtuGeraEsp(competencia.getId().getDtHrInicio(), competencia.getId().getMes(), competencia.getId().getAno(),
					dataFimCompetencia, isPrevia);
			this.logDebug(FaturamentoDebugCode.FAT_SUS_ENC_MODULO, modulo.getDescricao(), "Execução", statusSma ? "OK" : "NOK");
			// MILENA(04/02/2002) - Atualiza competencia para faturada e
			// apresentada(sis)
			if (!isPrevia) {
				statusCpe = getFatkCpeRn().rnCpecAtuFaturado(competencia);
				if (!statusCpe) {
					this.logDebug(FaturamentoDebugCode.FAT_SUS_ENC_MODULO, modulo.getDescricao(), "Atualização para faturada e apresentada", "NOK");
				}
			}
		} else {
			this.logDebug(FaturamentoDebugCode.FAT_SUS_MODULO_SEM_FAT_COMP, modulo.getDescricao());
		}
		result = true;

		return result;
	}

	protected VerificaCaracteristicaItemProcedimentoHospitalarRNCache getVerificaCaracteristicaItemProcedimentoHospitalarRNCache() {
		return verificaCaracteristicaItemProcedimentoHospitalarRNCache;
	}
	
	

	
}
