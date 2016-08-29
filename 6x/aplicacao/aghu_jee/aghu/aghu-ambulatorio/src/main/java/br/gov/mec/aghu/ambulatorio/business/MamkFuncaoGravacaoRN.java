package br.gov.mec.aghu.ambulatorio.business;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamFuncaoGravacaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpAlturasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpPaDiastolicasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpPaSistolicasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpPerimCefalicosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpPesosDAO;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.model.MamTmpAlturas;
import br.gov.mec.aghu.model.MamTmpPaDiastolicas;
import br.gov.mec.aghu.model.MamTmpPaSistolicas;
import br.gov.mec.aghu.model.MamTmpPerimCefalicos;
import br.gov.mec.aghu.model.MamTmpPesos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/** #51886 - PAC2
 *@ORADB MAMK_FUNCAO_GRAVACAO **/
@Stateless
public class MamkFuncaoGravacaoRN extends BaseBusiness {

	private static final long serialVersionUID = 5811581642153607628L;
	
	private static final String MAMC_GRAVA_PESO = "MAMC_GRAVA_PESO";
	private static final String MAMC_GRAVA_ALTURA = "MAMC_GRAVA_ALTURA";
	private static final String MAMC_GRAVA_PER_CEFAL = "MAMC_GRAVA_PER_CEFAL";
	private static final String MAMC_GRAVA_PA_SIST = "MAMC_GRAVA_PA_SIST";
	private static final String MAMC_GRAVA_PA_DIAST = "MAMC_GRAVA_PA_DIAST";
	
	@Inject
	private MamFuncaoGravacaoDAO mamFuncaoGravacaoDAO;
	
	@Inject
	private MamTmpPesosDAO mamTmpPesosDAO;
	
	@EJB
	private MamTmpAlturasRN mamTmpAlturasRN;

	@EJB
	private MamTmpPesosRN mamTmpPesosRN;
	
	@EJB
	private MamTmpPerimCefalicosRN mamTmpPerimCefalicosRN;
	
	@Inject
	private MamTmpPerimCefalicosDAO mamTmpPerimCefalicosDAO;
	
	@Inject
	private MamTmpPaSistolicasDAO mamTmpPaSistolicasDAO;
	
	@EJB
	private MamTmpPaSistolicasRN mamTmpPaSistolicasRN;
	
	@Inject
	private MamTmpPaDiastolicasDAO mamTmpPaDiastolicasDAO;
	
	@EJB
	private MamTmpPaDiastolicasRN mamTmpPaDiastolicasRN;
	
	@Inject
	private	MamTmpAlturasDAO mamTmpAlturasDAO;
	
	private static final Log LOG = LogFactory.getLog(MamkFuncaoGravacaoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public enum MamkFuncaoGravacaoRNExceptionCode implements BusinessExceptionCode {
		MAM_00323, MAM_00324, MAM_00325, MAM_00326,	MAM_01850, MAM_01851, MAM_01852, MAM_01853,MAM_01854_01857, MAM_01855, MAM_01856,
		MAM_01858, MAM_01859, MAM_01837_01839_01843_01849_02360, MAM_01838_01840_01844_01866_01868, MAM_01860_01861_01865_01867, MAM_01862,
		MAM_02036_02040_02042_02048, MAM_02037_02041_02043, MAM_02051_02052_02056_02058_02078_02079_02083_02085, MAM_02053_02057_02059_02080_02084_02086,
		MAM_02358, MAM_02359_02361,	MAM_02362, MAM_02363, MAM_02205
	}
	
	/** @ORADB FUNCTION MAMC_EXEC_FNC_GRAVA **/
	public String mamcExecFncGrava(Integer pFgrSeq, String pTipo, Long pChave, Integer pQusQutSeq, Short pQusSeqP, Short pseq,
			String pResposta, DominioOperacaoBanco pOperacao, Integer pPacCodigo, Date pCriadoEm) throws ApplicationBusinessException{
		
		String vTexto = StringUtils.EMPTY;
		String vFuncao = StringUtils.EMPTY;
		
		//CURSOR cur_fgr(c_fgr_seq	mam_funcao_gravacoes.seq%type)
		vFuncao = this.mamFuncaoGravacaoDAO.obterComandoFuncaoGravacaoPorSeq(pFgrSeq);
		if(vFuncao != null){
			vTexto = verificarComando(vFuncao, pTipo, pChave, pQusQutSeq, pQusSeqP, pseq, pResposta, pOperacao, pPacCodigo, pCriadoEm);
		}else{
			vTexto = StringUtils.EMPTY;
		}
		return vTexto;
	}
	
	public String verificarComando(String vFuncao, String pTipo, Long pChave, Integer pQusQutSeq, Short pQusSeqP, Short pseq,
			String pResposta, DominioOperacaoBanco pOperacao, Integer pPacCodigo, Date pCriadoEm) throws ApplicationBusinessException{
		
		if(vFuncao.equals(MAMC_GRAVA_PESO)){
			return mamcGravaPeso(pTipo, pChave, pQusQutSeq, pQusSeqP, pseq, pResposta, pOperacao, pPacCodigo, pCriadoEm);
		}else if(vFuncao.equals(MAMC_GRAVA_ALTURA)){
			return mamcGravaAltura(pTipo, pChave, pQusQutSeq, pQusSeqP, pseq, pResposta, pOperacao, pPacCodigo, pCriadoEm);
		}else if(vFuncao.equals(MAMC_GRAVA_PER_CEFAL)){
			return mamcGravaPerCefal(pTipo, pChave, pQusQutSeq, pQusSeqP, pseq, pResposta, pOperacao, pPacCodigo, pCriadoEm);
		}else if(vFuncao.equals(MAMC_GRAVA_PA_SIST)){
			return mamcGravaPaSist(pTipo, pChave, pQusQutSeq, pQusSeqP, pseq, pResposta, pOperacao, pPacCodigo, pCriadoEm);
		}else if(vFuncao.equals(MAMC_GRAVA_PA_DIAST)){
			return mamcGravaPaDiast(pTipo, pChave, pQusQutSeq, pQusSeqP, pseq, pResposta, pOperacao, pPacCodigo, pCriadoEm);
		}
		return null;
	}
	
	/** @ORADB FUNCTION MAMC_GRAVA_PESO **/
	/* Grava o peso do paciente na tabela temporÃ¡ria */
	public String mamcGravaPeso(String pTipo, Long pChave, Integer pQusQutSeq, Short pQusSeqP, Short pseq, String pResposta,
			DominioOperacaoBanco pOperacao, Integer pPacCodigo, Date pCriadoEm) throws ApplicationBusinessException {
		
		String vErro = StringUtils.EMPTY;
		String vOperacao =  StringUtils.EMPTY;
		BigDecimal vPeso = BigDecimal.ZERO;
		BigDecimal resp = BigDecimal.ZERO;
		
		if(DominioOperacaoBanco.INS.equals(pOperacao) && pResposta == null){
			vOperacao = "X";
		}else if(DominioOperacaoBanco.UPD.equals(pOperacao) && pResposta == null){
			vOperacao = "D";
		}else {
			vOperacao = pOperacao.toString();
		}
		
		if(DominioOperacaoBanco.INS.toString().equals(vOperacao) || DominioOperacaoBanco.UPD.toString().equals(vOperacao)){
			if(CoreUtil.isNumeroLong(pResposta)){
				vPeso = BigDecimal.valueOf(Long.valueOf(pResposta));
			}
			if(!CoreUtil.igual(BigDecimal.valueOf(Float.valueOf(pResposta)), vPeso)){
				throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_01837_01839_01843_01849_02360);
			}
		}
		
		if(DominioOperacaoBanco.INS.toString().equals(vOperacao)){
			if(pQusQutSeq == null){
				 Date data = (Date) CoreUtil.nvl(pCriadoEm, new Date());
				if(this.mamTmpPesosDAO.obterPesosExistentePorPorChaveTipoData(pChave, pTipo, data)){
					resp = verificarRepostaParaValorPeso(pResposta, resp);
					MamTmpPesos mamTmpPesos = this.mamTmpPesosRN.atualizarPeso(pChave, resp, pTipo);
					if(mamTmpPesos == null){
						throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_02359_02361);
					}
				}else{
					resp = verificarRepostaParaValorPeso(pResposta, resp);
					inserirValidarPeso(pTipo, pQusQutSeq, pQusSeqP, pseq, pPacCodigo, resp);
				}
			}
		}else if(DominioOperacaoBanco.UPD.toString().equals(vOperacao)) {
			if(this.mamTmpPesosDAO.obterPesosExistentePorPorChaveTipoSeq(
					pChave.longValue(), pQusQutSeq.shortValue(), pQusSeqP, pseq, pTipo)){
				resp = verificarRepostaParaValorPeso(pResposta, resp);
				MamTmpPesos mamTmpPesos = this.mamTmpPesosRN.atualizarPeso(pChave, resp, pTipo);
				if(mamTmpPesos == null){
					throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_01838_01840_01844_01866_01868);
				}
			}else{
				resp = verificarRepostaParaValorPeso(pResposta, resp);
				this.mamTmpPesosRN.preInsert(pPacCodigo, pCriadoEm);
				inserirValidarPeso(pTipo, pQusQutSeq, pQusSeqP, pseq, pPacCodigo, resp);
			}
		}else if ("D".equals(vOperacao)){
			this.mamTmpPesosRN.deletarMamTpmPeso(pChave, resp, pTipo);
		}
		return vErro;
	}

	private void inserirValidarPeso(String pTipo, Integer pQusQutSeq, Short pQusSeqP, Short pseq, Integer pPacCodigo, BigDecimal resp)
			throws ApplicationBusinessException {
		MamTmpPesos mamTmpPesos = this.mamTmpPesosRN.persistirMamTmpPeso(pTipo, pQusQutSeq, pQusSeqP, pseq, resp, pPacCodigo);
		if(mamTmpPesos == null){
			throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_01838_01840_01844_01866_01868);
		}
	}

	private BigDecimal verificarRepostaParaValorPeso(String pResposta, BigDecimal resp) throws ApplicationBusinessException {
		if(!CoreUtil.isNumeroLong(pResposta)){
			throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_01837_01839_01843_01849_02360);
		}else{
			 resp = BigDecimal.valueOf(Long.valueOf(pResposta), 0);
		}
		return resp;
	}
	
	/* Grava a altura do paciente na tabela temporÃ¡ria */
	/** @ORADB FUNCTION MAMC_GRAVA_ALTURA **/
	public String mamcGravaAltura(String pTipo, Long pChave, Integer pQusQutSeq, Short pQusSeqP, Short pseq, String pResposta,
			DominioOperacaoBanco pOperacao, Integer pPacCodigo, Date pCriadoEm) throws ApplicationBusinessException{
		
		String vErro = StringUtils.EMPTY;
		String vOperacao =  StringUtils.EMPTY;
		BigDecimal vAltura = BigDecimal.ZERO;
		BigDecimal resp = BigDecimal.ZERO;
		
		if(DominioOperacaoBanco.INS.equals(pOperacao) && pResposta == null){
			vOperacao = "X";
		}else if(DominioOperacaoBanco.UPD.equals(pOperacao) && pResposta == null){
			vOperacao = "D";
		}else {
			vOperacao = pOperacao.toString();
		}
		
		if(DominioOperacaoBanco.INS.toString().equals(vOperacao) || DominioOperacaoBanco.UPD.toString().equals(vOperacao)){
			vAltura = BigDecimal.valueOf(Float.valueOf(pResposta));
			if(!CoreUtil.isNumeroLong(pResposta) && !CoreUtil.igual(BigDecimal.valueOf(Float.valueOf(pResposta)), vAltura)){
				throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_01860_01861_01865_01867);
			}
		}
		if(DominioOperacaoBanco.INS.toString().equals(vOperacao)){
			if(pQusQutSeq == null){
				 Date data = (Date) CoreUtil.nvl(pCriadoEm, new Date());
				if(this.mamTmpAlturasDAO.obterMamTmpAlturaPorChaveSeqTipoData(pChave, pTipo, data)){
					resp = verificarRepostaParaValorAltura(pResposta, resp);
					MamTmpAlturas mamTmpAlturas = atualizarMamTmpAltura(pTipo, pChave, resp, pQusQutSeq, pQusSeqP);
					if(mamTmpAlturas == null){
						throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_02359_02361);
					}
				}else{
					resp = verificarRepostaParaValorAltura(pResposta, resp);
					inserirValidarAltura(pTipo, pQusQutSeq, pQusSeqP, pseq, pPacCodigo, resp, pCriadoEm);
				}
			}
		}else if(DominioOperacaoBanco.UPD.toString().equals(vOperacao)) {
			
			if(this.mamTmpAlturasDAO.obterMamTmpAlturaPorChaveSeqTipoResp(pChave, pQusQutSeq, pQusSeqP,	pseq, pTipo)){
				resp = verificarRepostaParaValorAltura(pResposta, resp);
				MamTmpAlturas mamTmpAlturas = atualizarMamTmpAltura(pTipo, pChave, resp, pQusQutSeq, pQusSeqP);
				if(mamTmpAlturas == null){
					throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_01838_01840_01844_01866_01868);
				}
			}else{
				resp = verificarRepostaParaValorAltura(pResposta, resp);
				inserirValidarAltura(pTipo, pQusQutSeq, pQusSeqP, pseq, pPacCodigo, resp, pCriadoEm);
			}
		}else if ("D".equals(vOperacao)){
			this.mamTmpAlturasRN.deletarMamTpmAlturas(pChave, pQusQutSeq, pQusSeqP, pTipo, resp);
		}
		
		return vErro;
	}

	private void inserirValidarAltura(String pTipo, Integer pQusQutSeq, Short pQusSeqP, Short pseq, Integer pPacCodigo, BigDecimal resp, Date pCriadoEm)
			throws ApplicationBusinessException {
		MamTmpAlturas mamTmpAlturas = this.mamTmpAlturasRN.inserirMamTpmAlturas(pTipo, pQusQutSeq, pQusSeqP, pseq, resp, pPacCodigo, pCriadoEm);
		if(mamTmpAlturas == null){
			throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_01862);
		}
	}

	private MamTmpAlturas atualizarMamTmpAltura(String pTipo, Long pChave, BigDecimal resp, Integer pQusQutSeq, Short pQusSeqP) {
		MamTmpAlturas mamTmpAlturas = this.mamTmpAlturasRN.atualizarMamTpmAlturas(pChave, pQusQutSeq, pQusSeqP, pTipo, resp);
		return mamTmpAlturas;
	}
	
	private BigDecimal verificarRepostaParaValorAltura(String pResposta, BigDecimal resp) throws ApplicationBusinessException {
		if(!CoreUtil.isNumeroLong(pResposta)){
			throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_02358);
		}else{
			 resp = BigDecimal.valueOf(Long.valueOf(pResposta), 0);
		}
		return resp;
	}
	
	/** @ORADB FUNCTION MAMC_GRAVA_PER_CEFAL **/
	public String mamcGravaPerCefal(String pTipo, Long pChave, Integer pQusQutSeq, Short pQusSeqP, Short pseq, String pResposta,
			DominioOperacaoBanco pOperacao, Integer pPacCodigo, Date pCriadoEm) throws ApplicationBusinessException{
		
		String vErro = StringUtils.EMPTY;
		String vOperacao = null;
		BigDecimal vPerimetroCefalico = BigDecimal.ZERO;
		BigDecimal resp = BigDecimal.ZERO;
		
		if(DominioOperacaoBanco.INS.equals(pOperacao) && pResposta == null){
			vOperacao = "X";
		}else if(DominioOperacaoBanco.UPD.equals(pOperacao) && pResposta == null){
			vOperacao = "D";
		}else {
			vOperacao = pOperacao.toString();
		}
		
		if(DominioOperacaoBanco.INS.toString().equals(vOperacao) || DominioOperacaoBanco.UPD.toString().equals(vOperacao)){
			vPerimetroCefalico = BigDecimal.valueOf(Float.valueOf(pResposta));
			if(!CoreUtil.isNumeroLong(pResposta) && !CoreUtil.igual(BigDecimal.valueOf(Float.valueOf(pResposta)), vPerimetroCefalico)){
				throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_01837_01839_01843_01849_02360);
			}
		}
		
		if(DominioOperacaoBanco.INS.toString().equals(vOperacao)){
			if(pQusQutSeq == null){
				Date data = (Date) CoreUtil.nvl(pCriadoEm, new Date());
				if (this.mamTmpPerimCefalicosDAO.obterPerimCefalicosExistentePorPorChaveTipoData(pChave, pTipo, data)){
					resp = verificarRepostaParaValorPeso(pResposta, resp);
					MamTmpPerimCefalicos mamTmpPerimCefalicos = atualizarPerimCefalicos(pTipo, pChave, pQusQutSeq, pQusSeqP, resp);
		            if(mamTmpPerimCefalicos == null){
		            	throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_02359_02361);
		            }
				}else{
					inserirValidarPerimCefalicos(pTipo, pQusQutSeq, pQusSeqP, pseq,	pResposta, pPacCodigo, resp);
				}
			}
		}else if(DominioOperacaoBanco.UPD.toString().equals(vOperacao)) {
			if(this.mamTmpPerimCefalicosDAO.obterPerimCefalicosExistentePorPorChaveTipoSeq(pChave, pQusQutSeq, pQusSeqP, pseq, pTipo)){
				resp = verificarRepostaParaValorPeso(pResposta, resp);
				MamTmpPerimCefalicos mamTmpPerimCefalicos = atualizarPerimCefalicos(pTipo, pChave, pQusQutSeq, pQusSeqP, resp);
				if(mamTmpPerimCefalicos == null){
					throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_01838_01840_01844_01866_01868);
				}
			}else{
				this.mamTmpPerimCefalicosRN.preInsert(pPacCodigo, pCriadoEm);
				inserirValidarPerimCefalicos(pTipo, pQusQutSeq, pQusSeqP, pseq,	pResposta, pPacCodigo, resp);
			}
		}else if ("D".equals(vOperacao)){
			this.mamTmpPerimCefalicosRN.deletarMamTpmPerimCefalicos(pChave, pQusQutSeq, pQusSeqP, pTipo);
		}
		return vErro;
	}

	private void inserirValidarPerimCefalicos(String pTipo, Integer pQusQutSeq,	Short pQusSeqP, Short pseq, String pResposta,
			Integer pPacCodigo, BigDecimal resp)throws ApplicationBusinessException {
		resp = verificarRepostaParaValorPeso(pResposta, resp);
		MamTmpPerimCefalicos mamTmpPerimCefalicos = this.mamTmpPerimCefalicosRN.inserirMamTmpPerimCefalicos(pTipo, pQusQutSeq, pQusSeqP, pseq, resp, pPacCodigo);
		if(mamTmpPerimCefalicos == null){
			throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_01838_01840_01844_01866_01868);
		}
	}

	private MamTmpPerimCefalicos atualizarPerimCefalicos(String pTipo, Long pChave, Integer pQusQutSeq, Short pQusSeqP, BigDecimal resp) {
		MamTmpPerimCefalicos mamTmpPerimCefalicos = this.mamTmpPerimCefalicosRN.atualizarPerimCefalicos(
				pChave, resp, pQusQutSeq, pQusSeqP, pTipo);
		return mamTmpPerimCefalicos;
	}
	
	/* Grava a pressÃ£o arterial sistÃ³lica do paciente na tabela temporÃ¡ria */
	/** @ORADB FUNCTION MAMC_GRAVA_PA_SIST */
	public String mamcGravaPaSist(String pTipo, Long pChave, Integer pQusQutSeq, Short pQusSeqP, Short pseq, String pResposta,
			DominioOperacaoBanco pOperacao, Integer pPacCodigo, Date pCriadoEm) throws ApplicationBusinessException{
		
		String vErro = StringUtils.EMPTY;
		String vOperacao = null;
		Short vSistolica = 0;
		
		if(DominioOperacaoBanco.INS.equals(pOperacao) && pResposta == null){
			vOperacao = "X";
		}else if(DominioOperacaoBanco.UPD.equals(pOperacao) && pResposta == null){
			vOperacao = "D";
		}else {
			vOperacao = pOperacao.toString();
		}

		if(DominioOperacaoBanco.INS.toString().equals(vOperacao) || DominioOperacaoBanco.UPD.toString().equals(vOperacao)){
			vSistolica = Short.valueOf(pResposta);
			if(!CoreUtil.isNumeroShort(pResposta) && !CoreUtil.igual(Short.valueOf(pResposta), vSistolica)){
				throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_02051_02052_02056_02058_02078_02079_02083_02085);
			}
		}
		
		if(DominioOperacaoBanco.INS.toString().equals(vOperacao)){
			vSistolica = verificarRespostaSistolica(pResposta, vSistolica);
			MamTmpPaSistolicas mamTmpPaSistolicas = inserirPaSistolicas(pTipo, pChave, pQusQutSeq, pQusSeqP, pseq, pPacCodigo, pCriadoEm, vSistolica);
			if(mamTmpPaSistolicas == null){
				throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_02053_02057_02059_02080_02084_02086);
			}
		}else if(DominioOperacaoBanco.UPD.toString().equals(vOperacao)) {
			if(this.mamTmpPaSistolicasDAO.obterPaSistolicasExistentePorPorChaveTipoSeq(pChave.longValue(),
					pQusQutSeq, pQusSeqP.shortValue(), pseq.shortValue(), pTipo)){
				vSistolica = verificarRespostaSistolica(pResposta, vSistolica);
				MamTmpPaSistolicas mamTmpPaSistolicas = this.mamTmpPaSistolicasRN.atualizarPaSistolicas(pChave, vSistolica, pQusQutSeq, pQusSeqP, pseq, pTipo);
				if(mamTmpPaSistolicas == null){
					throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_02053_02057_02059_02080_02084_02086);
				}
			}else{
				if(!CoreUtil.isNumeroShort(pResposta)){
					throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_01837_01839_01843_01849_02360);
				}else{
					vSistolica = Short.valueOf(pResposta);
				}
				MamTmpPaSistolicas mamTmpPaSistolicas = inserirPaSistolicas(pTipo, pChave, pQusQutSeq, pQusSeqP, pseq, pPacCodigo, pCriadoEm, vSistolica);
				if(mamTmpPaSistolicas == null){
					throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_01838_01840_01844_01866_01868);
				}
			}
		}else if ("D".equals(vOperacao)){
			this.mamTmpPaSistolicasRN.deletarMamTpmPaSistolicas(pChave, pResposta, pQusQutSeq, pQusSeqP, pseq, pTipo);
		}
		return vErro;
	}

	private MamTmpPaSistolicas inserirPaSistolicas(String pTipo, Long pChave, Integer pQusQutSeq, Short pQusSeqP, Short pseq, Integer pPacCodigo, Date pCriadoEm, Short vSistolica) {
		MamTmpPaSistolicas mamTmpPaSistolicas = this.mamTmpPaSistolicasRN.inserirMamTmpPaSistolicas(pTipo, pChave, pQusQutSeq, pQusSeqP, pseq, vSistolica, pPacCodigo, pCriadoEm);
		return mamTmpPaSistolicas;
	}

	private Short verificarRespostaSistolica(String pResposta, Short vSistolica) throws ApplicationBusinessException {
		if(!CoreUtil.isNumeroShort(pResposta)){
			throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_02051_02052_02056_02058_02078_02079_02083_02085);
		}else{
			vSistolica = Short.valueOf(pResposta);
		}
		return vSistolica;
	}
	
	/* Grava a pressÃ£o arterial diastÃ³lica do paciente na tabela temporÃ¡ria */
	/** @ORADB FUNCTION MAMC_GRAVA_PA_DIAST */
	public String mamcGravaPaDiast(String pTipo, Long pChave, Integer pQusQutSeq, Short pQusSeqP, Short pseq, String pResposta,
			DominioOperacaoBanco pOperacao, Integer pPacCodigo, Date pCriadoEm) throws ApplicationBusinessException{
		
		String vErro = StringUtils.EMPTY;
		String vOperacao = StringUtils.EMPTY;
		Short vDiastolica = 0;
		
		if(DominioOperacaoBanco.INS.equals(pOperacao) && pResposta == null){
			vOperacao = "X";
		}else if(DominioOperacaoBanco.UPD.equals(pOperacao) && pResposta == null){
			vOperacao = "D";
		}else {
			vOperacao = pOperacao.toString();
		}
		
		if(DominioOperacaoBanco.INS.toString().equals(vOperacao) || DominioOperacaoBanco.UPD.toString().equals(vOperacao)){
			vDiastolica = Short.valueOf(pResposta);
			if(!CoreUtil.isNumeroShort(pResposta) && !CoreUtil.igual(Short.valueOf(pResposta), vDiastolica)){
				throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_02051_02052_02056_02058_02078_02079_02083_02085);
			}
		}

		if(DominioOperacaoBanco.INS.toString().equals(vOperacao)){
			vDiastolica = verificarRespostaDiastolica(pResposta, vDiastolica);
			MamTmpPaDiastolicas mamTmpPaDiastolicas = inserirPaDiastolica(pTipo, pChave, pQusQutSeq, pQusSeqP, pseq, pPacCodigo, pCriadoEm, vDiastolica);
			if(mamTmpPaDiastolicas == null){
				throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_02053_02057_02059_02080_02084_02086);
			}
			
		}else if(DominioOperacaoBanco.UPD.toString().equals(vOperacao)) {
			if(this.mamTmpPaDiastolicasDAO.obterPaDiastolicasExistentePorPorChaveTipoSeq(Long.valueOf(pChave.toString()),
					Short.valueOf(pQusQutSeq.toString()), Short.valueOf(pQusSeqP.toString()), Short.valueOf(pseq.toString()), pTipo)){
				vDiastolica = verificarRespostaDiastolica(pResposta, vDiastolica);
				MamTmpPaDiastolicas mamTmpPaDiastolicas = this.mamTmpPaDiastolicasRN.atualizarMamTmpPaDiastolicas(pChave, vDiastolica, pQusQutSeq,
						pQusSeqP, pseq, pTipo);
				if(mamTmpPaDiastolicas == null){
					throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_02053_02057_02059_02080_02084_02086);
				}
			}else{
				if(!CoreUtil.isNumeroShort(pResposta)){
					throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_02051_02052_02056_02058_02078_02079_02083_02085);
				}else{
					vDiastolica = Short.valueOf(pResposta);
				}
				MamTmpPaDiastolicas mamTmpPaDiastolicas = inserirPaDiastolica(pTipo, pChave, pQusQutSeq, pQusSeqP, pseq, pPacCodigo, pCriadoEm, vDiastolica);
				if(mamTmpPaDiastolicas == null){
					throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_02053_02057_02059_02080_02084_02086);
				}
			}
		}else if ("D".equals(vOperacao)){
			this.mamTmpPaDiastolicasRN.deletarMamTmpPaDiastolicas(pChave, pResposta, pQusQutSeq, pQusSeqP, pseq, pTipo);
		}
		return vErro;
	}
	
	private MamTmpPaDiastolicas inserirPaDiastolica(String pTipo, Long pChave, Integer pQusQutSeq, Short pQusSeqP, Short pseq,
			Integer pPacCodigo, Date pCriadoEm, Short vDiastolica) {
		MamTmpPaDiastolicas mamTmpPaSistolicas = this.mamTmpPaDiastolicasRN.inserirMamTmpPaDiastolicas(pTipo, pChave, pQusQutSeq, pQusSeqP, pseq, vDiastolica, pPacCodigo, pCriadoEm);
		return mamTmpPaSistolicas;
	}
	
	private Short verificarRespostaDiastolica(String pResposta, Short vDiastolica) throws ApplicationBusinessException {
		if(!CoreUtil.isNumeroShort(pResposta)){
			throw new ApplicationBusinessException(MamkFuncaoGravacaoRNExceptionCode.MAM_02051_02052_02056_02058_02078_02079_02083_02085);
		}else{
			vDiastolica = Short.valueOf(pResposta);
		}
		return vDiastolica;
	}
}
