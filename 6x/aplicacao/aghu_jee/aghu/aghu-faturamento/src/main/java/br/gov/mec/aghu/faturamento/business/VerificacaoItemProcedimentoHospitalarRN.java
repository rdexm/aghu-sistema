package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatAtoObrigatorioProcedDAO;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvFaixaEtariaDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvFxEtariaItemDAO;
import br.gov.mec.aghu.faturamento.dao.FatExcecaoPercentualDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoAtoDAO;
import br.gov.mec.aghu.faturamento.dao.FatTiposVinculoDAO;
import br.gov.mec.aghu.faturamento.dao.FatVlrItemProcedHospCompsDAO;
import br.gov.mec.aghu.faturamento.vo.DadosCaracteristicaTratamentoApacVO;
import br.gov.mec.aghu.faturamento.vo.RnIphcVerAtoObriVO;
import br.gov.mec.aghu.faturamento.vo.RnIphcVerExcPercVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatAtoObrigatorioProced;
import br.gov.mec.aghu.model.FatCaractItemProcHospId;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatConvFaixaEtaria;
import br.gov.mec.aghu.model.FatConvFxEtariaItem;
import br.gov.mec.aghu.model.FatConvFxEtariaItemId;
import br.gov.mec.aghu.model.FatExcecaoPercentual;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatTipoAto;
import br.gov.mec.aghu.model.FatTiposVinculo;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * <p>
 * TODO Implementar metodos <br/>
 * Linhas: 1036 <br/>
 * Cursores: 0 <br/>
 * Forks de transacao: 0 <br/>
 * Consultas: 34 tabelas <br/>
 * Alteracoes: 0 tabelas <br/>
 * Metodos: 25 <br/>
 * Metodos externos: 5 <br/>
 * </p>
 * <p>
 * ORADB: <code>FATK_IPH_RN</code>
 * </p>
 * 
 * @author gandriotti
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.HierarquiaONRNIncorreta", "PMD.ExcessiveClassLength"})
@Stateless
public class VerificacaoItemProcedimentoHospitalarRN extends AbstractFatDebugLogEnableRN {


private static final String MSG_IPH_SEQ_NAO_PODE_SER_NULO = "iphSeq não pode ser nulo.";

private static final String MSG_PHO_SEQ_NAO_PODE_SER_NULO = "phoSeq não pode ser nulo.";

@EJB
private CaracteristicaTratamentoApacRN caracteristicaTratamentoApacRN;

@EJB
private VerificacaoFaturamentoSusRN verificacaoFaturamentoSusRN;

@EJB
private VerificaCaracteristicaItemProcedimentoHospitalarRN verificaCaracteristicaItemProcedimentoHospitalarRN;

@EJB
private TipoCaracteristicaItemRN tipoCaracteristicaItemRN;

private static final Log LOG = LogFactory.getLog(VerificacaoItemProcedimentoHospitalarRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatCompetenciaDAO fatCompetenciaDAO;

@Inject
private FatExcecaoPercentualDAO fatExcecaoPercentualDAO;

@Inject
private FatTipoAtoDAO fatTipoAtoDAO;

@Inject
private FatTiposVinculoDAO fatTiposVinculoDAO;

@Inject
private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;

@EJB
private IPacienteFacade pacienteFacade;

@Inject
private FatAtoObrigatorioProcedDAO fatAtoObrigatorioProcedDAO;

@EJB
private IExamesFacade examesFacade;

@Inject
private FatConvFxEtariaItemDAO fatConvFxEtariaItemDAO;

@Inject
private FatConvFaixaEtariaDAO fatConvFaixaEtariaDAO;

@Inject
private FatVlrItemProcedHospCompsDAO fatVlrItemProcedHospCompsDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6813908578712639010L;
	public static final String MAGIC_STRING_I = "I";
	public static final String MAGIC_STRING_A = "A";
	public static final String MAGIC_STRING_B = "B";
	public static final String MAGIC_STRING_S = "S";
	public static final String MAGIC_STRING_N = "N";
	public static final String MAGIC_STRING_IND_ESPECIAL_S = VerificacaoItemProcedimentoHospitalarRN.MAGIC_STRING_S;

	/**
	 * @return
	 */
	protected TipoCaracteristicaItemRN getTipoCaracteristicaItemRN() {

		return tipoCaracteristicaItemRN;
	}

	/**
	 * @return
	 */
	protected CaracteristicaTratamentoApacRN getCaracteristicaTratamentoApacRN() {

		return caracteristicaTratamentoApacRN;
	}

	protected VerificaCaracteristicaItemProcedimentoHospitalarRN getVerificaCaracteristicaItemProcedimentoHospitalarRN() {

		return verificaCaracteristicaItemProcedimentoHospitalarRN;
	}

	/**
	 * @return
	 */
	protected VerificacaoFaturamentoSusRN getVerificacaoFaturamentoSusRN() {

		return verificacaoFaturamentoSusRN;
	}

	protected FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO() {

		return fatItensProcedHospitalarDAO;
	}

	protected FatCompetenciaDAO getFatCompetenciaDAO() {

		return fatCompetenciaDAO;
	}

	protected FatVlrItemProcedHospCompsDAO getFatVlrItemProcedHospCompsDAO() {

		return fatVlrItemProcedHospCompsDAO;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}

	protected FatConvFxEtariaItemDAO getFatConvFxEtariaItemDAO() {

		return fatConvFxEtariaItemDAO;
	}

	protected FatConvFaixaEtariaDAO getFatConvFaixaEtariaDAO() {

		return fatConvFaixaEtariaDAO;
	}

	protected FatAtoObrigatorioProcedDAO getFatAtoObrigatorioProcedDAO() {

		return fatAtoObrigatorioProcedDAO;
	}

	protected FatTiposVinculoDAO getFatTiposVinculoDAO() {

		return fatTiposVinculoDAO;
	}

	protected FatTipoAtoDAO getFatTipoAtoDAO() {

		return fatTipoAtoDAO;
	}

	
	protected FatExcecaoPercentualDAO getFatExcecaoPercentualDAO() {

		return fatExcecaoPercentualDAO;
	}

	protected int obterQuantidadePhiAtivosPorTabelaSemSeq(final Short phoSeq,
			final Integer seqExcluido,
			final Long codTabela) {

		int result = 0;
		FatItensProcedHospitalarDAO dao = null;

		dao = this.getFatItensProcedHospitalarDAO();
		result = dao.obterQuantidadeFatItemProcHospAtivosPorPhoSeqSemSeqPorCodTabela(phoSeq, seqExcluido, codTabela);

		return result;
	}

	/**
	 * <p>
	 * Garantir somente um registro ativo para mesmo codigo tabela
	 * </p>
	 * <p>
	 * ORADB: <code>FATK_IPH_RN.RN_IPHP_VER_REG_ATIV</code>
	 * </p>
	 * 
	 * @param phoSeq
	 * @param seq
	 * @param codTabela
	 * @return
	 * @throws ApplicationBusinessException
	 * @see FaturamentoExceptionCode#FAT_00131
	 * @see FatItensProcedHospitalar
	 */
	@SuppressWarnings("ucd")
	public boolean verificarOutrosRegistrosAtivosParaMesmoCodTabela(final Short phoSeq,
			final Integer seq,
			final Long codTabela) throws ApplicationBusinessException {

		boolean result = false;
		int qtd = 0;

		//check args
		if (phoSeq == null) {
			throw new IllegalArgumentException(MSG_PHO_SEQ_NAO_PODE_SER_NULO);
		}
		if (seq == null) {
			throw new IllegalArgumentException("seq não pode ser nulo.");
		}
		if (codTabela == null) {
			throw new IllegalArgumentException("codTabela não pode ser nulo.");
		}
		//algo
		qtd = this.obterQuantidadePhiAtivosPorTabelaSemSeq(phoSeq, seq, codTabela);
		if (qtd > 0) {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00131);
		}
		result = true;

		return result;
	}

	protected FatCompetencia obterPrimeiraCompetenciaAbertaPorModulo(final DominioModuloCompetencia modulo) {
		return this.getFatCompetenciaDAO().buscarCompetenciasDataHoraFimNula(modulo, DominioSituacaoCompetencia.A);
	}

	protected int obterQtdValoresVigentes(final Short codTabela, final Integer codProcedimento, final Date competencia) {

		int result = 0;
		FatVlrItemProcedHospCompsDAO dao = null;

		dao = this.getFatVlrItemProcedHospCompsDAO();
		result = dao.obterQtdValorItemProcHospCompPorPhoIphParaCompetencia(codTabela, codProcedimento, competencia);

		return result;
	}

	protected Date obterDataCompetenciaViaModulo(final DominioModuloCompetencia modulo) {

		Date result = null;
		FatCompetencia fatComp;

		fatComp = this.obterPrimeiraCompetenciaAbertaPorModulo(modulo);
		if (fatComp != null) {
			result = ManipulacaoDatasUtil.getDate(fatComp.getId().getAno().intValue(), fatComp.getId()
					.getMes()
					.intValue(), 1);
		}

		return result;
	}

	protected Date resolverDataCompetencia(final Date dataCompetencia, final DominioModuloCompetencia modulo) throws ApplicationBusinessException {

		Date result = dataCompetencia;

		if (result == null) {
			result = this.obterDataCompetenciaViaModulo(modulo);
			if (result == null) {
				throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00564);
			}
		}

		return result;
	}

	protected boolean verificaExigenciaValoresConformeFlag(final Short phoSeq,
			final Integer iphSeq,
			final Date dataCompetencia,
			final DominioModuloCompetencia modulo,
			final boolean realizarVerificacao,
			final BusinessExceptionCode exceptionCode) throws ApplicationBusinessException {

		boolean result = false;
		Date competencia = null;
		int qtdValoresVigentes = 0;

		if (realizarVerificacao) {
			competencia = this.resolverDataCompetencia(dataCompetencia, modulo);
			// busca quantidade de registros de valores:
			// o valor usado deve ser o vigente na competencia
			// do lancamento do item, ou seja naquela em que o item foi/será cobrado		
			qtdValoresVigentes = this.obterQtdValoresVigentes(phoSeq, iphSeq, competencia);
			if (qtdValoresVigentes == 0) {
				// Procedimento especial necessita de registro de valores sem
				// data de fim de competência em Vlr_Item_Proced_Hosp_Comp
				throw new ApplicationBusinessException(exceptionCode);
			}
		}
		result = true;

		return result;
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_IPH_RN.RN_IPHP_VER_PESP_VLR</code>
	 * </p>
	 * 
	 * @param phoSeq
	 * @param iphSeq
	 * @param dataCompetencia
	 * @param modulo
	 * @param pIndEspecial
	 * @return
	 * @throws ApplicationBusinessException
	 * @see FaturamentoExceptionCode#FAT_00416
	 * @see FaturamentoExceptionCode#FAT_00564
	 */
	@SuppressWarnings("ucd")
	public boolean verificarExistenciaValoresVigentesConformeIndEspecial(final Short phoSeq,
			final Integer iphSeq,
			final Date dataCompetencia,
			final DominioModuloCompetencia modulo,
			final String pIndEspecial) throws ApplicationBusinessException {

		boolean result = false;
		boolean flag = false;

		//check args
		if (phoSeq == null) {
			throw new IllegalArgumentException(MSG_PHO_SEQ_NAO_PODE_SER_NULO);
		}
		if (iphSeq == null) {
			throw new IllegalArgumentException(MSG_IPH_SEQ_NAO_PODE_SER_NULO);
		}
		if (modulo == null) {
			throw new IllegalArgumentException("modulo não pode ser nulo.");
		}
		if (pIndEspecial == null) {
			throw new IllegalArgumentException("pIndEspecial não pode ser nulo.");
		}
		if (pIndEspecial.isEmpty()) {
			throw new IllegalArgumentException();
		}
		//algo
		flag = VerificacaoItemProcedimentoHospitalarRN.MAGIC_STRING_IND_ESPECIAL_S.equals(pIndEspecial);
		result = this.verificaExigenciaValoresConformeFlag(
				phoSeq,
				iphSeq,
				dataCompetencia,
				modulo,
				flag,
				FaturamentoExceptionCode.FAT_00416);

		return result;
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_IPH_RN.RN_IPHP_VER_EXI_VLR</code>
	 * </p>
	 * 
	 * @param phoSeq
	 * @param iphSeq
	 * @param modulo
	 * @param dataCompetencia
	 * @param pIndExigeVlr
	 * @return
	 * @throws ApplicationBusinessException
	 * @see FaturamentoExceptionCode#FAT_00419
	 * @see FaturamentoExceptionCode#FAT_00564
	 */
	@SuppressWarnings("ucd")
	public boolean verificarExistenciaValoresVigentesConformeExigeValor(final Short phoSeq,
			final Integer iphSeq,
			final DominioModuloCompetencia modulo,
			final Date dataCompetencia,
			final String pIndExigeVlr) throws ApplicationBusinessException {

		boolean result = false;
		boolean flag = false;

		//check args
		if (phoSeq == null) {
			throw new IllegalArgumentException(MSG_PHO_SEQ_NAO_PODE_SER_NULO);
		}
		if (iphSeq == null) {
			throw new IllegalArgumentException(MSG_IPH_SEQ_NAO_PODE_SER_NULO);
		}
		if (modulo == null) {
			throw new IllegalArgumentException("modulo não pode ser nulo.");
		}
		if (pIndExigeVlr == null) {
			throw new IllegalArgumentException("pIndExigeVlr não pode ser nulo.");
		}
		if (pIndExigeVlr.isEmpty()) {
			throw new IllegalArgumentException();
		}
		//algo
		flag = VerificacaoItemProcedimentoHospitalarRN.MAGIC_STRING_IND_ESPECIAL_S.equals(pIndExigeVlr);
		result = this.verificaExigenciaValoresConformeFlag(
				phoSeq,
				iphSeq,
				dataCompetencia,
				modulo,
				flag,
				FaturamentoExceptionCode.FAT_00419);

		return result;
	}

	protected FatItensProcedHospitalar obterFatItensProcedHospitalar(final Short phoSeq, final Integer seq) {

		FatItensProcedHospitalar result = null;
		FatItensProcedHospitalarId id = null;
		FatItensProcedHospitalarDAO dao = null;

		dao = this.getFatItensProcedHospitalarDAO();
		id = new FatItensProcedHospitalarId(phoSeq, seq);
		result = dao.obterPorChavePrimaria(id);

		return result;
	}

	protected DadosCaracteristicaTratamentoApacVO obterDadosCaracteristicaVO(final Short phoSeq,
			final Integer seq,
			final Integer tctSeq) {

		DadosCaracteristicaTratamentoApacVO result = null;
		CaracteristicaTratamentoApacRN tarRn = null;
		FatItensProcedHospitalarId iphId = null;
		FatCaractItemProcHospId cihId = null;

		iphId = new FatItensProcedHospitalarId(phoSeq, seq);
		cihId = new FatCaractItemProcHospId(phoSeq, seq, tctSeq);
		tarRn = this.getCaracteristicaTratamentoApacRN();
		result = tarRn.obterDadosCaracteristicaTratamentoApac(iphId, cihId);

		return result;
	}

	protected Integer obterTctSeqParaCobraApac() {

		Integer result = null;
		TipoCaracteristicaItemRN tctRn = null;

		tctRn = this.getTipoCaracteristicaItemRN();
		result = tctRn.obterTipoCaractItemSeq(DominioFatTipoCaractItem.COBRA_APAC);

		return result;
	}

	/**
	 * <p>
	 * TODO converter parametro de entrada para {@link Enum} assim como o
	 * retorno.
	 * </p>
	 * <p>
	 * ORADB: <code>FATK_IPH_RN.RN_IPHC_VER_COBRANCA</code>
	 * </p>
	 * 
	 * @param phoSeq
	 * @param seq
	 * @param pLocalCobranca
	 *            TODO eh um {@link Enum} mas eh preciso saber seu dominio
	 * @return TODO precisa ser convertido para um {@link Enum}
	 */
	public String verificarPossibilidadeCobranca(final Short phoSeq, final Integer seq, final String pLocalCobranca) {

		String result = null;
		FatItensProcedHospitalar itemProcHosp = null;
		VerificaCaracteristicaItemProcedimentoHospitalarRN verCiphRn = null;
		Integer tctSeq = null;
		DadosCaracteristicaTratamentoApacVO dadosCaract = null;
		String cobraApac = null;
		boolean cobraSiscolo = false;
		boolean cobraBpi = false;
		boolean cobraBpa = false;

		//check args
		if (phoSeq == null) {
			throw new IllegalArgumentException(MSG_PHO_SEQ_NAO_PODE_SER_NULO);
		}
		if (seq == null) {
			throw new IllegalArgumentException("seq não pode ser nulo.");
		}
		if (pLocalCobranca == null) {
			throw new IllegalArgumentException("pLocalCobranca não pode ser nulo.");
		}
		if (pLocalCobranca.isEmpty()) {
			throw new IllegalArgumentException();
		}
		//algo
		itemProcHosp = this.obterFatItensProcedHospitalar(phoSeq, seq);
		if (itemProcHosp != null) { // verifica se o procedimento e' cobravel em APAC, Internação e Ambulatório
			// busca dados de COBRANCA DE EXCEDENTE EM APAC
			tctSeq = this.obterTctSeqParaCobraApac();
			// verifica se o proced permite a cobranca de excedente no BPA
			dadosCaract = this.obterDadosCaracteristicaVO(phoSeq, seq, tctSeq);
			if (dadosCaract == null) {
				cobraApac = VerificacaoItemProcedimentoHospitalarRN.MAGIC_STRING_N;
			} else {
				cobraApac = dadosCaract.getValorChar();
			}
			verCiphRn = this.getVerificaCaracteristicaItemProcedimentoHospitalarRN();
			// verifica se procedimento é siscolo ou BPI passa conv = 1 e plano = 2
			// porque a função não utiliza mais estes parâmetros
			cobraSiscolo = verCiphRn.verificarCaracteristicaItemProcHosp(
					phoSeq,
					seq,
					DominioFatTipoCaractItem.COBRA_SISCOLO);
			cobraBpi = verCiphRn.verificarCaracteristicaItemProcHosp(phoSeq, seq, DominioFatTipoCaractItem.COBRA_BPI);
			cobraBpa = verCiphRn.verificarCaracteristicaItemProcHosp(phoSeq, seq, DominioFatTipoCaractItem.COBRA_BPA);
			// Milena incluiu em julho/2008
			result = VerificacaoItemProcedimentoHospitalarRN.MAGIC_STRING_N;
			if (VerificacaoItemProcedimentoHospitalarRN.MAGIC_STRING_B.equals(pLocalCobranca)) { // ambulatorio
				if (cobraBpa || cobraSiscolo || cobraBpi) { // cobravel em Ambulatorio					
					result = VerificacaoItemProcedimentoHospitalarRN.MAGIC_STRING_S;
				}
			} else if (VerificacaoItemProcedimentoHospitalarRN.MAGIC_STRING_A.equals(pLocalCobranca)) { // apac				
				if (!VerificacaoItemProcedimentoHospitalarRN.MAGIC_STRING_N.equals(cobraApac)) { //tem outros valores além de S (A, por exemplo)					
					// cobravel em APAC
					result = cobraApac;
				}
			} else if (VerificacaoItemProcedimentoHospitalarRN.MAGIC_STRING_I.equals(pLocalCobranca) && itemProcHosp.getCobrancaConta().booleanValue()) {
				// internacao				
				// cobravel em Internacao
				result = VerificacaoItemProcedimentoHospitalarRN.MAGIC_STRING_S;
			}
		}

		return result;

	}

	protected Short obterIdadePacientePorCodPaciente(final Integer codPaciente) {

		Short result = null;
		VerificacaoFaturamentoSusRN fatkSusRn = null;

		fatkSusRn = this.getVerificacaoFaturamentoSusRN();
		result = fatkSusRn.obterIdadePacienteParaDataAtual(codPaciente);

		return result;
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_IPH_RN.RN_IPHC_VER_IDMINMAX</code>
	 * </p>
	 * 
	 * @param codPaciente
	 * @param phoSeq
	 * @param seq
	 * @return
	 */
	public boolean verificarFaixaIdadePorCodPaciente(final Integer codPaciente, final Short phoSeq, final Integer seq) {

		boolean result = false;
		FatItensProcedHospitalar iph = null;
		Short idadePac = null;

		iph = this.obterFatItensProcedHospitalar(phoSeq, seq);
		if (iph != null) {
			idadePac = this.obterIdadePacientePorCodPaciente(codPaciente);
			result = idadePac != null;
			result = result && (idadePac.intValue() >= iph.getIdadeMin().intValue());
			result = result && (idadePac.intValue() <= iph.getIdadeMax().intValue());
		}

		return result;
	}

	protected FatVlrItemProcedHospComps obterPrimeiroValorItemProcHospCompPorPhoIphParaCompetencia(final Short iphPhoSeq,
			final Integer iphSeq,
			final Date competencia) {

		return this.getFatVlrItemProcedHospCompsDAO().obterPrimeiroValorItemProcHospCompPorPhoIphParaCompetencia(iphPhoSeq, iphSeq, competencia);
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_IPH_RN.RN_IPHC_VER_VLR_ITEM</code>
	 * </p>
	 * 
	 * @param phoSeq
	 * @param iphSeq
	 * @param modulo
	 * @param dataCompetencia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public FatVlrItemProcedHospComps obterValoresItemProcHospPorModuloCompetencia(final Short phoSeq,
			final Integer iphSeq,
			final DominioModuloCompetencia modulo,
			final Date dataCompetencia) throws ApplicationBusinessException {
		
		logar("valores marina: ");
		logar("P_CPE_COMPETENCIA: {0}", DateUtil.obterDataFormatada(dataCompetencia, "dd/MM/yyyy"));
		// busca competencia: o valor usado deve ser o vigente na competencia
		// do lancamento do item, ou seja naquela em que o item foi/será cobrado
		
		Date vCpeComp = this.resolverDataCompetencia(dataCompetencia, modulo);
		logar("v_cpe_comp: {0}", DateUtil.obterDataFormatada(vCpeComp, "dd/MM/yyyy"));
		
		FatVlrItemProcedHospComps result = this.obterPrimeiroValorItemProcHospCompPorPhoIphParaCompetencia(phoSeq, iphSeq, vCpeComp);
		
		if (result != null) {
			// eEschweigert #20458 alteração no cursor c_valor_item_procedimento --> ,0 --vlr_anestesista
			result.setVlrAnestesista(BigDecimal.ZERO);
			
			logar("p_vlr_serv_hospitalar: {0}", nvl(result.getVlrServHospitalar(), BigDecimal.ZERO));                                                                        
			logar("p_vlr_serv_profissional: {0}", nvl(result.getVlrServProfissional(), BigDecimal.ZERO));                                                                      
			logar("p_vlr_sadt: {0}", nvl(result.getVlrSadt(), BigDecimal.ZERO));                                                                                       
			logar("p_vlr_procedimento: {0}", nvl(result.getVlrProcedimento(), BigDecimal.ZERO));
			
			
			logar("p_vlr_anestesista: {0}", nvl(result.getVlrAnestesista(), BigDecimal.ZERO));			
		}

		return result;
	}

	protected AipPacientes obterPaciente(final Integer codPaciente) {

		AipPacientes result = null;
		result = getPacienteFacade().obterAipPacientesPorChavePrimaria(codPaciente);

		return result;
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_IPH_RN.RN_IPHC_VER_SEXOCOMP</code>
	 * </p>
	 * 
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @param codPaciente
	 * @return
	 */
	public boolean verificarCompatibilidadeSexoPacienteSexoProcedimento(final Short iphPhoSeq,
			final Integer iphSeq,
			final Integer codPaciente) {

		boolean result = false;
		AipPacientes paciente = null;
		FatItensProcedHospitalar iph = null;
		VerificacaoFaturamentoSusRN susRn = null;

		//check args
		if (iphPhoSeq == null) {
			throw new IllegalArgumentException("iphPhoSeq não pode ser nulo.");
		}
		if (iphSeq == null) {
			throw new IllegalArgumentException(MSG_IPH_SEQ_NAO_PODE_SER_NULO);
		}
		if (codPaciente == null) {
			throw new IllegalArgumentException("codPaciente não pode ser nulo.");
		}
		//algo
		// busca paciente
		paciente = this.obterPaciente(codPaciente);
		if (paciente != null) {// paciente existe
			// busca sexo permitido para o procedimento
			iph = this.obterFatItensProcedHospitalar(iphPhoSeq, iphSeq);
			if (iph != null) { // iph existe
				// verifica se o sexo do paciente e' compativel com o sexo
				// permitido para o procedimento (retorna true/false)
				susRn = this.getVerificacaoFaturamentoSusRN();
				result = susRn.verificarCompatibilidadeSexoPacProc(paciente.getSexo(), iph.getSexo());
			}
		}

		return result;
	}

	protected int obterQtdFatConvFxEtariaItemPorIphConvenio(final Short codConvenio,
			final Short iphPhoSeq,
			final Integer iphSeq) {

		int result = 0;
		FatConvFxEtariaItemDAO dao = null;

		dao = this.getFatConvFxEtariaItemDAO();
		result = dao.obterQtdPorIphCnv(codConvenio, iphPhoSeq, iphSeq);

		return result;
	}

	protected FatConvFxEtariaItem obterFatConvFxEtariaItem(final Byte cfeSeqp,
			final Short cfeCnvCodigo,
			final Short iphPhoSeq,
			final Integer iphSeq) {

		FatConvFxEtariaItem result = null;
		FatConvFxEtariaItemId id = null;
		FatConvFxEtariaItemDAO dao = null;

		dao = this.getFatConvFxEtariaItemDAO();
		id = new FatConvFxEtariaItemId(cfeSeqp, cfeCnvCodigo, iphPhoSeq, iphSeq);
		result = dao.obterPorChavePrimaria(id);

		return result;
	}

	protected List<FatConvFaixaEtaria> obterFatConvFaixaEtariaAtivoCorrentePorIdadeConvenio(final Short idade,
			final Short codConvenio) {

		List<FatConvFaixaEtaria> result = null;
		FatConvFaixaEtariaDAO dao = null;

		dao = this.getFatConvFaixaEtariaDAO();
		result = dao.obterListaCorrenteAtivoPorIdadeConvenio(idade, codConvenio);

		return result;
	}

	protected List<FatConvFxEtariaItem> obterFatConvFxEtariaItemPorIphDataIdadeConvenio(final Short iphPhoSeq,
			final Integer iphSeq,
			final Short idade,
			final Short codConvenio) {

		List<FatConvFxEtariaItem> result = null;
		FatConvFxEtariaItem item = null;
		List<FatConvFaixaEtaria> listConvFxEt = null;

		listConvFxEt = this.obterFatConvFaixaEtariaAtivoCorrentePorIdadeConvenio(idade, codConvenio);
		if ((listConvFxEt != null) && !listConvFxEt.isEmpty()) {
			result = new LinkedList<FatConvFxEtariaItem>();
			for (FatConvFaixaEtaria e : listConvFxEt) {
				item = this.obterFatConvFxEtariaItem(e.getId().getSeqp(), e.getId().getCnvCodigo(), iphPhoSeq, iphSeq);
				if (item != null) {
					result.add(item);
				}
			}
		}

		return result;
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_IPH_RN.RN_IPHC_VER_FXETARIA</code>
	 * </p>
	 * 
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @param codPaciente
	 * @param codConvenio
	 * @return
	 */
	public boolean verificarRestricaoFaixaEtariaParaProcedimentoConvenio(final Short iphPhoSeq,
			final Integer iphSeq,
			final Integer codPaciente,
			final Short codConvenio) {

		boolean result = false;
		List<FatConvFxEtariaItem> listaConvFxEtItem = null;
		Short idade = null;
		int qtdConvFxEtItem = 0;

		//check args
		if (iphPhoSeq == null) {
			throw new IllegalArgumentException("iphPhoSeq não pode ser nulo.");
		}
		if (iphSeq == null) {
			throw new IllegalArgumentException(MSG_IPH_SEQ_NAO_PODE_SER_NULO);
		}
		if (codPaciente == null) {
			throw new IllegalArgumentException("codPaciente não pode ser nulo.");
		}
		if (codConvenio == null) {
			throw new IllegalArgumentException("codConvenio não pode ser nulo.");
		}
		//algo
		result = true;
		// verifica se existem faixas etarias cadastradas para o item
		qtdConvFxEtItem = this.obterQtdFatConvFxEtariaItemPorIphConvenio(codConvenio, iphPhoSeq, iphSeq);
		if (qtdConvFxEtItem > 0) {
			// busca idade do paciente			
			idade = this.obterIdadePacientePorCodPaciente(codPaciente);
			// verifica se a idade do paciente se enquadra em alguma
			// das faixas etarias cadastradas para o item
			listaConvFxEtItem = this.obterFatConvFxEtariaItemPorIphDataIdadeConvenio(
					iphPhoSeq,
					iphSeq,
					idade,
					codConvenio);
			if ((listaConvFxEtItem == null) || listaConvFxEtItem.isEmpty()) {// nao encontrou faixa do procedimento para a idade
				result = false;
			}
		}

		return result;
	}

	protected List<FatAtoObrigatorioProced> obterListaFatAtoObrigatorioProcedPorIph(final Short iphPhoSeq,
			final Integer iphSeq) {

		List<FatAtoObrigatorioProced> result = null;
		FatAtoObrigatorioProcedDAO dao = null;

		dao = this.getFatAtoObrigatorioProcedDAO();
		result = dao.obterListaPorIph(iphPhoSeq, iphSeq);

		return result;
	}

	/**
	 * <p>
	 * Obs.: O retorno original eh um {@link Integer} mas que na verdade eh o
	 * tamanho da lista devolvida atualmente
	 * </p>
	 * <p>
	 * ORADB: <code>FATK_IPH_RN.RN_IPHC_VER_ATO_OBRI</code>
	 * </p>
	 * 
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @param qtdProced
	 * @param qtdDiarias
	 * @return
	 * @see FatAtoObrigatorioProced
	 * @see FatTiposVinculo
	 * @see FatTipoAto
	 */
	public List<RnIphcVerAtoObriVO> obterListaTivTaoCodSusIphCobradoQtd(final Short iphPhoSeq,
			final Integer iphSeq,
			final Short qtdProced,
			final Short qtdDiarias) {
		
		
		final FatTipoAtoDAO tipoAtoDao = getFatTipoAtoDAO();
		final FatTiposVinculoDAO tipoVinculoDao = getFatTiposVinculoDAO();

		final List<FatAtoObrigatorioProced> listaAtoOb = this.obterListaFatAtoObrigatorioProcedPorIph(iphPhoSeq, iphSeq);
		if ((listaAtoOb != null) && !listaAtoOb.isEmpty()) {
			final List<RnIphcVerAtoObriVO> result = new LinkedList<RnIphcVerAtoObriVO>();
			for (FatAtoObrigatorioProced regAto : listaAtoOb) {
				

				RnIphcVerAtoObriVO item = new RnIphcVerAtoObriVO( regAto.getId().getIphPhoSeqCobrado(), 
											   					  regAto.getId().getIphSeqCobrado()
											 					);
				
				// verifica quantidade do ato obrigatorio
				switch (regAto.getTipoQuantidade()) {
					case V: item.setQtd(regAto.getQuantidade()); break;
					case P: item.setQtd(qtdProced); break;
					case D: item.setQtd(qtdDiarias); break;
				}
				
				// busca codigo SUS do tipo de vinculo
				if(regAto.getId().getTivSeq() != null){
					final FatTiposVinculo tiv = tipoVinculoDao.obterPorChavePrimaria(regAto.getId().getTivSeq());
					if (tiv.getIndSituacaoRegistro().isAtivo()) {
						item.setTivCodSus(tiv.getCodigoSus());
					} else {
						item.setTivCodSus(Integer.valueOf("0"));
					}
				} else {
					item.setTivCodSus(Integer.valueOf("0"));
				}
				
				// busca codigo SUS do tipo de ato
				if(regAto.getId().getTaoSeq() != null){
					final FatTipoAto tao = tipoAtoDao.obterPorChavePrimaria(regAto.getId().getTaoSeq());
					
					if (tao.getIndSituacaoRegistro().isAtivo()) {
						item.setTaoCodSus(tao.getCodigoSus());
					} else {
						item.setTaoCodSus(Byte.valueOf("0"));
					}
				} else {
					item.setTaoCodSus(Byte.valueOf("0"));
				}
				
				result.add(item);
			}

			return result;
		}
		return null;
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_IPH_RN.RN_IPHC_VER_EXC_PERC</code>
	 * </p>
	 * 
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @return
	 * @see FatExcecaoPercentual
	 */
	public RnIphcVerExcPercVO obterExcecoesPercentuais(final Short iphPhoSeq, final Integer iphSeq) {
		// verifica se o item possui excecoes de percentual
		final List<FatExcecaoPercentual> listPerc = getFatExcecaoPercentualDAO().obterListaPorIph(iphPhoSeq, iphSeq);
		
		if ((listPerc != null) && !listPerc.isEmpty()) {
			return new RnIphcVerExcPercVO(listPerc);
		}
	
		return null;
	}

	protected AelItemSolicitacaoExames obterItemSolExame(final Integer soeSeq, final Short seqp) {
		return getExamesFacade().obteritemSolicitacaoExamesPorChavePrimaria(new AelItemSolicitacaoExamesId(soeSeq, seqp));
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_IPH_RN.RN_IPHC_VER_EXALIBER</code>
	 * </p>
	 * 
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @return
	 * @see FatItensProcedHospitalar
	 * @see AelItemSolicitacaoExames
	 */
	@SuppressWarnings("ucd")
	public boolean verificarProcEspExameLiberado(final Short iphPhoSeq,
			final Integer iphSeq,
			final Integer iseSoeSeq,
			final Short iseSeqp) {
	
		boolean result = false;
		FatItensProcedHospitalar iph = null;
		AelItemSolicitacaoExames ise = null;
	
		// verifica se o procedimento eh especial
		result = true;
		iph = this.obterFatItensProcedHospitalar(iphPhoSeq, iphSeq);
		if ((iph != null) && Boolean.TRUE.equals(iph.getProcedimentoEspecial())) {
			// verifica o status do item de exame
			ise = this.obterItemSolExame(iseSoeSeq, iseSeqp);
			if (ise != null) {
				if(ise.getSituacaoItemSolicitacao() != null) {
					result = DominioSituacaoItemSolicitacaoExame.LI.toString().equals(ise.getSituacaoItemSolicitacao().getCodigo());	
				} else {
					result = false;
				}
			}
		}
	
		return result;
	}

	/**
	 * <p>
	 * nao ha necessidade disso use
	 * {@link FatItensProcedHospitalar#getHcpaCadastrado()} <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>FATK_IPH_RN.RN_IPHC_VER_HOSP_CAD</code>
	 * </p>
	 * 
	 * @param phoSeq
	 * @param seq
	 * @return
	 */
	public Boolean getHcpaCadastrado(final Short phoSeq, final Integer seq) {
		// Busca indicador de HCPA habilitado para executar o procedimento
		FatItensProcedHospitalar itemProcedimentoHospitalar = getFatItensProcedHospitalarDAO().obterPorChavePrimaria(
				new FatItensProcedHospitalarId(phoSeq, seq));

		return itemProcedimentoHospitalar != null
				&& (itemProcedimentoHospitalar.getHcpaCadastrado() == null || Boolean.TRUE.equals(itemProcedimentoHospitalar
						.getHcpaCadastrado()));
	}

	/**
	 * <p>
	 * nao ha necessidade disso use
	 * {@link FatItensProcedHospitalar#getProcedimentoEspecial()} <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>FATK_IPH_RN.RN_IPHC_VER_PROC_ESP</code>
	 * </p>
	 * 
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @return
	 * @see FatItensProcedHospitalar
	 */
	public Boolean getProcedimentoEspecial(final Short iphPhoSeq, final Integer iphSeq) {
		FatItensProcedHospitalar itemProcedimentoHospitalar = getFatItensProcedHospitalarDAO().obterPorChavePrimaria(
				new FatItensProcedHospitalarId(iphPhoSeq, iphSeq));
		return getProcedimentoEspecial(itemProcedimentoHospitalar);
	}
	
	public Boolean getProcedimentoEspecial(FatItensProcedHospitalar itemProcedimentoHospitalar) {
		if (itemProcedimentoHospitalar != null && Boolean.TRUE.equals(itemProcedimentoHospitalar.getProcedimentoEspecial())) {
			logDebug("é especial - P_IPH_PHO_SEQ: " + itemProcedimentoHospitalar.getId().getPhoSeq() + " P_IPH_SEQ: " + itemProcedimentoHospitalar.getId().getSeq());
			return true;
		} else {
			logDebug("não é especial - P_IPH_PHO_SEQ: " + itemProcedimentoHospitalar.getId().getPhoSeq() + " P_IPH_SEQ: " + itemProcedimentoHospitalar.getId().getSeq());
			return false;
		}
	}

	/**
	 * <p>
	 * nao ha necessidade disso use
	 * {@link FatItensProcedHospitalar#getExigeValor()} <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>FATK_IPH_RN.RN_IPHC_VER_EXIGEVLR</code>
	 * </p>
	 * 
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @return
	 * @see FatItensProcedHospitalar
	 */
	public Boolean getExigeValor(final Short iphPhoSeq, final Integer iphSeq) {
		FatItensProcedHospitalar itemProcedimentoHospitalar = getFatItensProcedHospitalarDAO().obterPorChavePrimaria(
				new FatItensProcedHospitalarId(iphPhoSeq, iphSeq));

		return itemProcedimentoHospitalar != null && Boolean.TRUE.equals(itemProcedimentoHospitalar.getExigeValor());
	}

}
