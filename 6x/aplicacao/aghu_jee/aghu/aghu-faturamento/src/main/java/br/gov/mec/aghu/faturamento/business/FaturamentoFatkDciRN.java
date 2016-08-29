package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.dominio.DominioTipoDocumentoCobrancaAih;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatDocumentoCobrancaAihsDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoClassifSecSaudeDAO;
import br.gov.mec.aghu.faturamento.dao.VFatContaHospitalarPacDAO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerItemSusVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatDocumentoCobrancaAihs;
import br.gov.mec.aghu.model.FatGrupoProcedHospitalar;
import br.gov.mec.aghu.model.FatItemGrupoProcedHosp;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatTipoClassifSecSaude;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * <p>
 * TODO Implementar metodos <br/>
 * Linhas: 632 <br/>
 * Cursores: 0 <br/>
 * Forks de transacao: 1 <br/>
 * Consultas: 18 tabelas <br/>
 * Alteracoes: 13 tabelas <br/>
 * Metodos: 2 <br/>
 * Metodos externos: 1 <br/>
 * </p>
 * <p>
 * ORADB: <code>FATK_CDI_RN</code>
 * </p>
 * @author gandriotti
 *
 */
@Stateless
public class FaturamentoFatkDciRN extends BaseBusiness implements Serializable {


@EJB
private FaturamentoRN faturamentoRN;

@EJB
private FatkCthRN fatkCthRN;

private static final Log LOG = LogFactory.getLog(FaturamentoFatkDciRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatContasHospitalaresDAO fatContasHospitalaresDAO;

@EJB
private IFaturamentoFacade faturamentoFacade;

@Inject
private FatDocumentoCobrancaAihsDAO fatDocumentoCobrancaAihsDAO;

@Inject
private FatCompetenciaDAO fatCompetenciaDAO;

@Inject
private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;

@EJB
private IPacienteFacade pacienteFacade;

@Inject
private FatTipoClassifSecSaudeDAO fatTipoClassifSecSaudeDAO;

@EJB
private IParametroFacade parametroFacade;

@Inject
private VFatContaHospitalarPacDAO vFatContaHospitalarPacDAO;
	private static final long serialVersionUID = -3897339040482004563L;

	private DateFormat mesAnoFormatter = new SimpleDateFormat("MMyyyy");
	
	/**
	 * 
	 * @return
	 */
	protected FatkCthRN getFatkCthRN() {		
		return fatkCthRN;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	/**
	 * <p>
	 * TODO <br/>
	 * Assinatura: ok<br/>
	 * Referencias externas: ok<br/>
	 * Tabelas: ok<br/>
	 * Codigos de erro: ok<br/>
	 * Implementacao: <b>OK</b><br/>
	 * Linhas: 402 <br/>
	 * Cursores: 0 <br/>
	 * Forks de transacao: 0 <br/>
	 * Consultas: 10 tabelas <br/>
	 * Alteracoes: 8 tabelas <br/>
	 * Metodos externos: 0 <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>RN_DCIC_ATU_DCIH</code>
	 * </p>
	 * <p>
	 * <b>INSERT:</b> {@link FatDocumentoCobrancaAihs}<br/>
	 * </p>
	 * <p>
	 * <b>UPDATE:</b> {@link FatDocumentoCobrancaAihs}<br/>
	 * <b>UPDATE:</b> {@link FatContasHospitalares}<br/>
	 * </p>
	 * @param pCthSeq 
	 * @param pClcCodigo 
	 * @return 
	 * @throws BaseException 
	 * @see FatContasHospitalares
	 * @see FatCompetencia
	 * @see FatItensProcedHospitalar
	 * @see FatDocumentoCobrancaAihs
	 * @see VFatContaHospitalarPac
	 * @see VAipEnderecoPaciente
	 * @see FatGrupoProcedHospitalar
	 * @see FatItemGrupoProcedHosp
	 * @see FatTipoClassifSecSaude
	 * @see FatDocumentoCobrancaAihs
	 */
	@SuppressWarnings({"PMD.NcssMethodCount","PMD.CyclomaticComplexity","PMD.ExcessiveMethodLength"})
	public String rnDcicAtuDcih(Integer pCthSeq, Integer pClcCodigo, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		IPacienteFacade pacienteFacade = getPacienteFacade();

		FaturamentoRN faturamentoRN = getFaturamentoRN();
		FatkCthRN fatkCthRN = getFatkCthRN();

		FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
		FatCompetenciaDAO fatCompetenciaDAO = getFatCompetenciaDAO();
		FatTipoClassifSecSaudeDAO fatTipoClassifSecSaudeDAO = getFatTipoClassifSecSaudeDAO();
		FatDocumentoCobrancaAihsDAO fatDocumentoCobrancaAihsDAO = getFatDocumentoCobrancaAihsDAO();
		VFatContaHospitalarPacDAO vFatContaHospitalarPacDAO = getVFatContaHospitalarPacDAO();

		Boolean vCaract = null;
		FatDocumentoCobrancaAihs regDci = null;
		FatCompetencia regCpe = null;
		FatItensProcedHospitalar regIph = null;
		Short vPhoRealiz = null;
		Integer vIphRealiz = null;
		Integer pPacCodigo = null;
		String vSusDcih = null;
		FatTipoClassifSecSaude vTcs = null;
		DominioTipoDocumentoCobrancaAih vTipo = null;
		String vMesAnoAlta = null;
		BigDecimal vSequenciaDcih = null;
		DominioSituacaoCompetencia vSitCpe = null;
		String vUfPaciente = null;
		Boolean flag = null;

		FatContasHospitalares regCth = fatContasHospitalaresDAO.obterPorChavePrimaria(pCthSeq);
		FatContasHospitalares contaOld = null;
		try{
			contaOld = getFaturamentoFacade().clonarContaHospitalar(regCth);
		}catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
		}
		
		// Verificar se a competencia não está em manutenção 
		// passar a propria dcih como retorno
		if (regCth.getDocumentoCobrancaAih() != null) {
			FatDocumentoCobrancaAihs fatDocumentoCobrancaAihs = regCth.getDocumentoCobrancaAih();
			vSitCpe = fatDocumentoCobrancaAihs.getFatCompetencia().getIndSituacao();

			if (DominioSituacaoCompetencia.M.equals(vSitCpe)) {
				FatDocumentoCobrancaAihs oldFatDocumentoCobrancaAihs = null;
				try {
					oldFatDocumentoCobrancaAihs = getFaturamentoRN().clonarFatDocumentoCobrancaAihs(fatDocumentoCobrancaAihs);
				} catch (Exception e) {
					logError("Exceção capturada: ", e);
					FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
				}

				Short auxQuantidade = fatDocumentoCobrancaAihs.getQuantidadeContasHosp();

				fatDocumentoCobrancaAihs.setQuantidadeContasHosp(auxQuantidade != null ? auxQuantidade++ : 1);
				faturamentoRN.atualizarFatDocumentoCobrancaAihs(fatDocumentoCobrancaAihs, oldFatDocumentoCobrancaAihs);

				return fatDocumentoCobrancaAihs.getCodigoDcih();
			}
		}

		if (regCth.getProcedimentoHospitalarInternoRealizado() == null) {
			return null;
		}

		RnCthcVerItemSusVO rnCthcVerItemSusVO = fatkCthRN.rnCthcVerItemSus(DominioOrigemProcedimento.I, regCth
				.getConvenioSaudePlano() != null ? regCth.getConvenioSaudePlano().getId().getCnvCodigo() : null, regCth
				.getConvenioSaudePlano() != null ? regCth.getConvenioSaudePlano().getId().getSeq() : null, (short) 1, regCth
				.getProcedimentoHospitalarInternoRealizado() != null ? regCth.getProcedimentoHospitalarInternoRealizado().getSeq()
				: null, null);

		if (rnCthcVerItemSusVO != null) {
			flag = rnCthcVerItemSusVO.getRetorno();
			vPhoRealiz = rnCthcVerItemSusVO.getPhoSeq();
			vIphRealiz = rnCthcVerItemSusVO.getIphSeq();
		}

		if (!flag) {
			logDebug("erro na ver item sus");
			return null;
		} else {
			flag = false;
		}

		if (regCth.getDtAltaAdministrativa() != null) {
			vMesAnoAlta = mesAnoFormatter.format(regCth.getDtAltaAdministrativa());
		}

		regIph = fatItensProcedHospitalarDAO.obterPorChavePrimaria(new FatItensProcedHospitalarId(vPhoRealiz, vIphRealiz));

		logDebug("CLC " + (regIph.getClinica() != null ? regIph.getClinica().getCodigo() : "") + " SUS " + regIph.getCodTabela());

		if (pClcCodigo == null && regIph.getClinica() == null) {
			logDebug("erro na clc realiz");
			return null;
		} else if (regIph.getClinica() != null && regIph.getClinica().getCodigo() > 10) {
			logDebug("erro na clc realiz");
			return null;
		}

		// Verificar como sera a primeira apresentacao
		if (Boolean.TRUE.equals(regIph.getFaec())) {
			vTipo = DominioTipoDocumentoCobrancaAih.F;
			vSusDcih = "5"; // Alta complexidade
		} else {
			vTipo = DominioTipoDocumentoCobrancaAih.N;
			vSusDcih = "M"; // Média complexidade
		}

		// Estratégico
		if (!flag && Boolean.TRUE.equals(regIph.getDcihTransplante())) {
			vSusDcih = "E"; // Estratégico
			flag = true;
		}

		logDebug("TIPO " + vTipo);

		FatCompetencia competencia = fatCompetenciaDAO.buscarPrimeiraCompetenciasOrdenadoPorDataHoraFimDataHoraInicio( DominioModuloCompetencia.INT, false, DominioSituacaoCompetencia.A);
		if (competencia != null) {
			regCpe = competencia;
		}

		VFatContaHospitalarPac vFatContaHospitalarPac = vFatContaHospitalarPacDAO.buscarPrimeiraContaHospitalarPaciente(pCthSeq);
		if (vFatContaHospitalarPac != null) {
			pPacCodigo = vFatContaHospitalarPac.getPacCodigo();
		}

		vUfPaciente = pacienteFacade.buscaUfPaciente(pPacCodigo);

		AghParametros parametroUF = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_UF_SEDE_HU);
		String ufSede = null;
		if(parametroUF != null && parametroUF.getVlrTexto() != null){
			ufSede = parametroUF.getVlrTexto();
		} else {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.PARAMETRO_INVALIDO, AghuParametrosEnum.P_AGHU_UF_SEDE_HU);
		}
		
		if (!ufSede.equalsIgnoreCase(vUfPaciente) && Boolean.TRUE.equals(regIph.getFaec())) {
			vSusDcih = "F"; // Alta complexidade fora RS
			flag = true;
		} else if (Boolean.TRUE.equals(regCth.getIndContaReapresentada())) {
			if ("5".equals(vSusDcih)) {
				vSusDcih = "J"; // Reapresentações alta complex
				vTipo = DominioTipoDocumentoCobrancaAih.N;
				flag = true;
			} else if ("M".equalsIgnoreCase(vSusDcih)) {
				vSusDcih = "K"; // Reapresentações media complex
				vTipo = DominioTipoDocumentoCobrancaAih.N;
				flag = true;
			} else {
				vSusDcih = "L"; // Reapresentações estratégico
				vTipo = DominioTipoDocumentoCobrancaAih.N;
				flag = true;
			}
		} else {
			vTipo = DominioTipoDocumentoCobrancaAih.N;
		}

		String auxCodTabela = regIph.getCodTabela().toString();
		if (auxCodTabela.length() > 1 && "39".equals(auxCodTabela.substring(0, 2))) {
			if (Boolean.TRUE.equals(regCth.getIndContaReapresentada())) {
				if ("J".equalsIgnoreCase(vSusDcih)) {
					vSusDcih = "G"; // Reapres ortop alta complex
					vTipo = DominioTipoDocumentoCobrancaAih.N;
					flag = true;
				} else if ("K".equalsIgnoreCase(vSusDcih)) {
					vSusDcih = "H"; // Reapres ortop media complex
					vTipo = DominioTipoDocumentoCobrancaAih.N;
					flag = true;
				} else {
					vSusDcih = "I"; // Reapres ortop estratégico
					vTipo = DominioTipoDocumentoCobrancaAih.N;
					flag = true;
				}
			} else {
				if ("5".equals(vSusDcih)) {
					vSusDcih = "B"; // Ortopedia alta complex
					vTipo = DominioTipoDocumentoCobrancaAih.N;
					flag = true;
				} else if ("M".equalsIgnoreCase(vSusDcih)) {
					vSusDcih = "D"; // Ortopedia media complex
					vTipo = DominioTipoDocumentoCobrancaAih.N;
					flag = true;
				} else {
					vSusDcih = "N"; // Ortopedia estratégico
					vTipo = DominioTipoDocumentoCobrancaAih.N;
					flag = true;
				}
			}
		}

		if (auxCodTabela.length() > 1 && "48".equals(auxCodTabela.substring(0, 2))) {
			if (Boolean.TRUE.equals(regCth.getIndContaReapresentada())) {
				if ("J".equalsIgnoreCase(vSusDcih)) {
					vSusDcih = "T"; // Reapres ortop alta complex
					vTipo = DominioTipoDocumentoCobrancaAih.N;
					flag = true;
				} else if ("K".equalsIgnoreCase(vSusDcih)) {
					vSusDcih = "U"; // Reapres ortop media complex
					vTipo = DominioTipoDocumentoCobrancaAih.N;
					flag = true;
				} else {
					vSusDcih = "V"; // Reapres ortop estratégico
					vTipo = DominioTipoDocumentoCobrancaAih.N;
					flag = true;
				}
			} else {
				if ("5".equals(vSusDcih)) {
					vSusDcih = "P"; // Ortopedia alta complex
					vTipo = DominioTipoDocumentoCobrancaAih.N;
					flag = true;
				} else if ("M".equalsIgnoreCase(vSusDcih)) {
					vSusDcih = "Q"; // Ortopedia media complex
					vTipo = DominioTipoDocumentoCobrancaAih.N;
					flag = true;
				} else {
					vSusDcih = "S"; // ortopedia estratégico
					vTipo = DominioTipoDocumentoCobrancaAih.N;
					flag = true;
				}
			}
		}

		vCaract = faturamentoRN.verificarCaracteristicaExame(vIphRealiz, vPhoRealiz, DominioFatTipoCaractItem.DCIH_MUTIRAO);

		if (Boolean.TRUE.equals(vCaract)) {
			vSusDcih = "X"; // Dcih mutirão
			vTipo = DominioTipoDocumentoCobrancaAih.N;
			vMesAnoAlta = mesAnoFormatter.format(regCpe.getId().getDtHrInicio());
		}

		vTcs = fatTipoClassifSecSaudeDAO.buscarPrimeiraTipoClassifSecSaude(vSusDcih, DominioSituacao.A);
		if (vTcs == null) {
			logDebug("erro tcs " + vSusDcih);
			return null;
		}

		FatDocumentoCobrancaAihs documentosCobrancaAih = null;
		final Short limiteContasDCIH = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_LIMITE_CONTAS_DCIH).getVlrNumerico().shortValue();
		if (Boolean.TRUE.equals(vCaract)) {
			documentosCobrancaAih = fatDocumentoCobrancaAihsDAO.buscarPrimeiroDocumentosCobrancaAihDataFechamentoApresentacaoNulos(
																		(byte) 1, 
																		vTcs.getSeq(), 
																		vTipo, 
																		regCpe.getId().getDtHrInicio(), 
																		regCpe.getId().getMes(), 
																		regCpe.getId().getAno(),
																		DominioModuloCompetencia.INT, 
																		vMesAnoAlta, 
																		limiteContasDCIH);
		} else {
			documentosCobrancaAih = fatDocumentoCobrancaAihsDAO.buscarPrimeiroDocumentosCobrancaAihDataFechamentoApresentacaoNulos(
																		(pClcCodigo != null ? pClcCodigo.byteValue() : (regIph.getClinica() != null ? regIph.getClinica().getCodigo().byteValue() : null)), 
																		vTcs.getSeq(), 
																		vTipo, 
																		regCpe.getId().getDtHrInicio(), 
																		regCpe.getId().getMes(), 
																		regCpe.getId().getAno(), 
																		DominioModuloCompetencia.INT, 
																		vMesAnoAlta, 
																		limiteContasDCIH);
		}

		if (documentosCobrancaAih != null) {
			regDci = documentosCobrancaAih;
		} else {
			regDci = new FatDocumentoCobrancaAihs();
		}
				
		if (regDci.getCodigoDcih() == null) {
			// Fazer insert para nova dcih
			vSequenciaDcih = fatDocumentoCobrancaAihsDAO.buscaMaxSequenciaDcih(pClcCodigo != null ? pClcCodigo.byteValue() : regIph
					.getClinica() != null ? regIph.getClinica().getCodigo().byteValue() : null, regCpe.getId().getDtHrInicio(), regCpe
					.getId().getMes(), regCpe.getId().getAno(), DominioModuloCompetencia.INT);

			if (vSequenciaDcih.compareTo(new BigDecimal(99)) > 0) {
				logDebug("erro seq dcih");
				return null;
			}

			if (pClcCodigo != null ? pClcCodigo.equals(10) : (regIph.getClinica() != null ? regIph.getClinica().getCodigo().equals(10) : false)) {
				String codigoDcih = StringUtils.leftPad(regCpe.getId().getMes().toString(), 2, "0")
						+ regCpe.getId().getAno().toString().substring(2, 4) 
						+ "0"
						+ StringUtils.leftPad(vSequenciaDcih.toString(), 2, "0");
				regDci.setCodigoDcih(codigoDcih);
			} else {
				String codigoDcih = StringUtils.leftPad(regCpe.getId().getMes().toString(), 2, "0")
						+ regCpe.getId().getAno().toString().substring(2, 4)
						+ (pClcCodigo != null ? pClcCodigo.toString() : (regIph.getClinica() != null ? regIph.getClinica().getCodigo().toString() : ""))
						+ StringUtils.leftPad(vSequenciaDcih.toString(), 2, "0");
				regDci.setCodigoDcih(codigoDcih);
			}

			try {
				FatDocumentoCobrancaAihs fatDocumentoCobrancaAihs = new FatDocumentoCobrancaAihs();

				fatDocumentoCobrancaAihs.setCodigoDcih(regDci.getCodigoDcih());
				fatDocumentoCobrancaAihs.setSequencia(vSequenciaDcih);
				fatDocumentoCobrancaAihs.setTipo(vTipo);
				fatDocumentoCobrancaAihs.setFatCompetencia(regCpe);
				fatDocumentoCobrancaAihs.setClcCodigo(pClcCodigo != null ? pClcCodigo.byteValue()
						: regIph.getClinica() != null ? regIph.getClinica().getCodigo().byteValue() : null);
				fatDocumentoCobrancaAihs.setFatTipoClassifSecSaude(vTcs);
				fatDocumentoCobrancaAihs.setQuantidadeContasHosp((short) 1);
				fatDocumentoCobrancaAihs.setMesAnoAlta(vMesAnoAlta);

				faturamentoRN.inserirFatDocumentoCobrancaAihs(fatDocumentoCobrancaAihs, true);
			} catch (Exception e) {
				logDebug("erro insert dcih");
				return null;
			}
		} else {
			// Encontrou dcih
			FatDocumentoCobrancaAihs fatDocumentoCobrancaAihs = fatDocumentoCobrancaAihsDAO.obterPorChavePrimaria(regDci
					.getCodigoDcih());

			FatDocumentoCobrancaAihs oldFatDocumentoCobrancaAihs = null;
			try {
				oldFatDocumentoCobrancaAihs = getFaturamentoRN().clonarFatDocumentoCobrancaAihs(fatDocumentoCobrancaAihs);
			} catch (Exception e) {
				logError("Exceção capturada: ", e);
				FaturamentoExceptionCode.ERRO_AO_ATUALIZAR.throwException();
			}

			fatDocumentoCobrancaAihs
					.setQuantidadeContasHosp((short) (fatDocumentoCobrancaAihs.getQuantidadeContasHosp() != null ? fatDocumentoCobrancaAihs
							.getQuantidadeContasHosp() + 1
							: 1));

			faturamentoRN.atualizarFatDocumentoCobrancaAihs(fatDocumentoCobrancaAihs, oldFatDocumentoCobrancaAihs);
			this.flush();
		}

		regCth.setDocumentoCobrancaAih(regDci.getCodigoDcih() != null ? fatDocumentoCobrancaAihsDAO.obterPorChavePrimaria(regDci.getCodigoDcih()) : null);
		regCth.setTipoClassifSecSaude(vTcs);

		getFaturamentoFacade().persistirContaHospitalar(regCth, contaOld, nomeMicrocomputador, dataFimVinculoServidor);

		return regDci.getCodigoDcih();
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected FaturamentoRN getFaturamentoRN() {
		return faturamentoRN;
	}
	
	protected FatContasHospitalaresDAO getFatContasHospitalaresDAO() {
		return fatContasHospitalaresDAO;
	}
	
	protected FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO() {
		return fatItensProcedHospitalarDAO;
	}
	
	protected VFatContaHospitalarPacDAO getVFatContaHospitalarPacDAO() {
		return vFatContaHospitalarPacDAO;
	}
	
	protected FatCompetenciaDAO getFatCompetenciaDAO() {
		return fatCompetenciaDAO;
	}
	
	protected FatTipoClassifSecSaudeDAO getFatTipoClassifSecSaudeDAO() {
		return fatTipoClassifSecSaudeDAO;
	}
	
	protected FatDocumentoCobrancaAihsDAO getFatDocumentoCobrancaAihsDAO() {
		return fatDocumentoCobrancaAihsDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
}
