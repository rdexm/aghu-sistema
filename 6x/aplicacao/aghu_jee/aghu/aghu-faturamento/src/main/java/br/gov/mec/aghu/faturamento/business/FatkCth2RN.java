package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioAtualizacaoSaldo;
import br.gov.mec.aghu.dominio.DominioCobrancaDiaria;
import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioIndAbsenteismo;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioItemConsultoriaSumarios;
import br.gov.mec.aghu.dominio.DominioLocalCobranca;
import br.gov.mec.aghu.dominio.DominioLocalSoma;
import br.gov.mec.aghu.dominio.DominioModoCobranca;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimento;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioTipoAltaUTI;
import br.gov.mec.aghu.dominio.DominioTipoIdadeUTI;
import br.gov.mec.aghu.dominio.DominioTipoItemEspelhoContaHospitalar;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatAgrupItemContaDAO;
import br.gov.mec.aghu.faturamento.dao.FatAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatCampoMedicoAuditAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatCidContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatContaSugestaoDesdobrDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasInternacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvGrupoItensProcedDAO;
import br.gov.mec.aghu.faturamento.dao.FatDiariaUtiDigitadaDAO;
import br.gov.mec.aghu.faturamento.dao.FatDocumentoCobrancaAihsDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoItemContaHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatExcCaraterInternacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatExcCnvGrpItemProcDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoAtoDAO;
import br.gov.mec.aghu.faturamento.dao.FatTiposVinculoDAO;
import br.gov.mec.aghu.faturamento.dao.FatVlrItemProcedHospCompsDAO;
import br.gov.mec.aghu.faturamento.vo.BuscarProcedHospEquivalentePhiVO;
import br.gov.mec.aghu.faturamento.vo.CursorIchPhiMatVO;
import br.gov.mec.aghu.faturamento.vo.FatVariaveisVO;
import br.gov.mec.aghu.faturamento.vo.ItemContaHospitalarVO;
import br.gov.mec.aghu.faturamento.vo.ListarItensContaHospitalarRnCthcAtuAgrupichVO;
import br.gov.mec.aghu.faturamento.vo.MenorDataValidacaoApacVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcAtuRegrasVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcBuscaDadosProfVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcValidaCboRespVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerItemSusVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerPacCtaVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerRegraespVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerUtimesesVO;
import br.gov.mec.aghu.faturamento.vo.RnCthpVerInsCidVO;
import br.gov.mec.aghu.faturamento.vo.RnFatcVerItprocexcVO;
import br.gov.mec.aghu.faturamento.vo.RnIchcAtuRegrasVO;
import br.gov.mec.aghu.faturamento.vo.RnIphcVerAtoObriVO;
import br.gov.mec.aghu.faturamento.vo.RnSutcAtuSaldoVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinDiariasAutorizadas;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;
import br.gov.mec.aghu.model.AipCepLogradouros;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatAgrupItemConta;
import br.gov.mec.aghu.model.FatAgrupItemContaId;
import br.gov.mec.aghu.model.FatAih;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatDiariaUtiDigitada;
import br.gov.mec.aghu.model.FatDocumentoCobrancaAihs;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatEspelhoAihId;
import br.gov.mec.aghu.model.FatEspelhoItemContaHosp;
import br.gov.mec.aghu.model.FatEspelhoItemContaHospId;
import br.gov.mec.aghu.model.FatExcCaraterInternacao;
import br.gov.mec.aghu.model.FatExcCnvGrpItemProc;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.model.FatMensagemLog;
import br.gov.mec.aghu.model.FatPerdaItemConta;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatSituacaoSaidaPaciente;
import br.gov.mec.aghu.model.FatTipoAih;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceItemRmps;
import br.gov.mec.aghu.model.SceItemRmpsId;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

@SuppressWarnings({"PMD.NcssTypeCount", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.HierarquiaONRNIncorreta"})

/**
 * Refere-se a Package FATK_CTH2_RN_UN
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class FatkCth2RN extends AbstractFatBMTEnableRN {

	private static final String RN_CTHC_ATU_LANCAUTI = "RN_CTHC_ATU_LANCAUTI";

	private static final String AGHU_ERRO_AO_ENCERRAR_MOTIVO = "##### AGHU - ERRO AO ENCERRAR ##### Motivo: ";

	private static final String AGHU_CONTA_NOT_OK_MOTIVO = "##### AGHU - CONTA NOT OK ##### Motivo: ";

	private static final String VINC = " VINC: ";

	private static final String NAO_FOI_ENCONTRADO_CBO_MATR = "CBO NAO ENCONTRADO OU INCOMPATIVEL - MATR: ";
	
	private static final String NAO_FOI_ENCONTRADO_CBO = "CBO NAO ENCONTRADO OU INCOMPATIVEL";

	private static final String NUMERO_DE_AIH_NAO_FOI_INFORMADO = "NUMERO DE AIH NAO FOI INFORMADO.";

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	@EJB
	private FatkCth6RN fatkCth6RN;
	
	@EJB
	private FatSeparaItensPorCompRN fatSeparaItensPorCompRN;
	
	@EJB
	private FatkCthRN fatkCthRN;
	
	@EJB
	private FatkCth5RN fatkCth5RN;
	
	@EJB
	private FatkCth4RN fatkCth4RN;
	
	@EJB
	private FatkCthnRN fatkCthnRN;
	
	@EJB
	private FaturamentoInternacaoRN faturamentoInternacaoRN;
	
	private static final Log LOG = LogFactory.getLog(FatkCth2RN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	private static final long serialVersionUID = 344239158923904625L;

	final static Integer INICIO_PORTARIA_203_08 =  201108;
	final static Integer INICIO_PORTARIA_203_04 =  201104;
	final static String INT = DominioModuloCompetencia.INT.toString();
	final static String RN_CTHC_ATU_ENC_PRV = "RN_CTHC_ATU_ENC_PRV";
	final static Long EXAMES_PRE_TRANSFUSIONAIS_I  = 212010026L;
	final static Long EXAMES_PRE_TRANSFUSIONAIS_II = 212010034L;
	final static Short CODIGO_TIPO_LOGRADOUROS = 197;
	
	/**
	 * ORADB Procedure FATK_CTH2_RN_UN.RN_CTHC_ATU_GERA_ESP
	 */
	public Boolean rnCthcAtuGeraEsp(Integer pCthSeq, Boolean pPrevia,
			String nomeMicrocomputador, final Date dataFimVinculoServidor, boolean refresh)
		throws BaseException {
		try {

			Boolean retorno = null;
			getFaturamentoFacade().clear();
			
			this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
			
			if (pPrevia) {
				atualizaUsuarioPrevia(pCthSeq, nomeMicrocomputador,
						dataFimVinculoServidor, refresh);
			}
			retorno = rnCthcAtuSelecssm(pCthSeq, pPrevia, nomeMicrocomputador,
					dataFimVinculoServidor);
			
			this.commitTransaction();
			
			return retorno;

		} catch(EJBTransactionRolledbackException e) {
			this.rollbackTransaction();
			Throwable exception = this.unrollException(e, BaseOptimisticLockException.class);
			
			if(exception instanceof BaseOptimisticLockException) {
				BaseOptimisticLockException erroConcorrencia = (BaseOptimisticLockException) exception;
				throw erroConcorrencia;
			}
			return false;
		} catch (BaseException e) { //NOPMD
			this.rollbackTransaction();
			throw e;
		} catch (Exception e){
			this.rollbackTransaction();
			LOG.error(e.getMessage(), e);
			return false;
		}
	}

	public Throwable unrollException(Throwable exception,
			Class<? extends Throwable> expected) {

		while (exception != null && exception != exception.getCause()) {
			if (expected.isInstance(exception)) {
				return exception;
			}
			exception = exception.getCause();
		}
		return null;
	}
	
	private void atualizaUsuarioPrevia(Integer pCthSeq,
			String nomeMicrocomputador, final Date dataFimVinculoServidor, boolean refresh) throws BaseException {
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		FatContasHospitalares contaHospitalar = null;
		// Atualiza o usuário que solicitou a prévia
		if(refresh){
			contaHospitalar = getFatContasHospitalaresDAO()
					.obterRefreshContaHospitalar(pCthSeq);
		}else{
			contaHospitalar = getFatContasHospitalaresDAO()
				.obterPorChavePrimaria(pCthSeq);
		}

		FatContasHospitalares contaOld = null;
		try {
			contaOld = getFaturamentoFacade()
					.clonarContaHospitalar(contaHospitalar);
		} catch (Exception e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			throw new BaseException(
					FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
		}
		contaHospitalar.setUsuarioPrevia(servidorLogado != null ? servidorLogado.getUsuario() : null);
		contaHospitalar.setDataPrevia(new Date());
		getFaturamentoFacade().persistirContaHospitalar(contaHospitalar,
				contaOld, nomeMicrocomputador, dataFimVinculoServidor);
		this.commitTransaction();
		this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
	}

	

	/**
	 * ORADB Procedure FATK_CTH2_RN_UN.RN_CTHC_ATU_AGRUPICH
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	protected Boolean rnCthcAtuAgrupich(Integer pCthSeq, Short pCnvCodigo, Byte pCspSeq, Short pPhoRealiz, Integer pIphRealiz,
			Integer pPhiTcc) throws BaseException {

		final FaturamentoON faturamentoON = getFaturamentoON();
		final FatExcCnvGrpItemProcDAO fatExcCnvGrpItemProcDAO = getFatExcCnvGrpItemProcDAO();
		final FatVlrItemProcedHospCompsDAO fatVlrItemProcedHospCompsDAO = getFatVlrItemProcedHospCompsDAO();
		final FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
		final FatConvGrupoItensProcedDAO fatConvGrupoItensProcedDAO = getFatConvGrupoItensProcedDAO();

		Boolean result = Boolean.TRUE;
		final DateFormat df = new SimpleDateFormat("ddMMyyyyHHmmss");

		Date vCpe = null;
		BigDecimal vValor = null;
		Short vIphPhoSeq = null;
		Integer vIphSeq = null;
		FatVlrItemProcedHospComps regIpc = null;
		ListarItensContaHospitalarRnCthcAtuAgrupichVO regIch = null;

		final Integer[] phiSeqsTomografia = this.buscarVlrIntegerArrayAghParametro(AghuParametrosEnum.P_PHI_AGRUPADOR_TOMOGRAFIA);
		
		logar("cth: {0} cnv: {1} csp: {2} phi_tomo: {3}", pCthSeq, pCnvCodigo, pCspSeq, pPhiTcc);

		List<FatAgrupItemConta> agrupamentos = getFatAgrupItemContaDAO().pesquisarPorCodigoContaHospitalar(pCthSeq);
		getFatAgrupItemContaDAO().removerPorCodigoContaHospitalar(pCthSeq);
		getFatAgrupItemContaDAO().flush();

		for(FatAgrupItemConta agrupamento : agrupamentos) {
			getFaturamentoFacade().evict(agrupamento);
		}
		
		// Tipo grupo conta padrão para convenio SUS
		final Short vGrcSus = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		
		// Busca competencia atual
		final FatCompetencia fatCompetencia = getFatCompetenciaDAO().buscarCompetenciasDataHoraFimNula(DominioModuloCompetencia.INT, DominioSituacaoCompetencia.A);
		if (fatCompetencia != null) {

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, fatCompetencia.getId().getMes()-1);
			cal.set(Calendar.YEAR, fatCompetencia.getId().getAno());
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			vCpe = cal.getTime();
		}

		logar("grc: {0} cpe: {1}", vGrcSus, df.format(vCpe));

		List<ListarItensContaHospitalarRnCthcAtuAgrupichVO> listaItensContaHospitalar = getFatItemContaHospitalarDAO()
				.listarItensContaHospitalarRnCthcAtuAgrupich(pCthSeq, pPhiTcc, DominioSituacaoItenConta.A, phiSeqsTomografia);
		listaItensContaHospitalar = agrupa(listaItensContaHospitalar);
		if (listaItensContaHospitalar != null && !listaItensContaHospitalar.isEmpty()) {
			
			/* eSchweigert utilizado para validar resultado agrupado com os valroes originais do Oracle 
			 * testado e validado OK em 09/11/2012
			 * 
			logar("PHI_SEQ                QUANTIDADE             MATRICULA ISE_SEQP    DTHR_REALIZADO            DT_ALTA_ADMINISTRATIVA    CSP_CNV_CODIGO");
			logar("---------------------- ---------------------- --------- ----------- ------------------------- ------------------------- ----------------------"); 

			for (ListarItensContaHospitalarRnCthcAtuAgrupichVO vo : listaItensContaHospitalar) {
				logar("{0}                    {1}                      {2}             {3}                  {4}                  {5}                      ",
						vo.getPhiSeq(), vo.getQuantidadeRealizada(), vo.getMatricula(), 
						DateUtil.obterDataFormatada(vo.getDthrRealizado(), "dd/MM/yyyy"),
						DateUtil.obterDataFormatada(vo.getDtAltaAdministrativa(), "dd/MM/yyyy"),
						vo.getCspCnvCodigo()
				);

			}
			*/
			
			for (int i = 0; i < listaItensContaHospitalar.size(); i++) {
				regIch = listaItensContaHospitalar.get(i);

				logar("phi: {0} qtd: {1}", regIch.getPhiSeq(), regIch.getQuantidadeRealizada());

				// Busca um IPH associado ao PHI
				vIphPhoSeq = null;
				vIphSeq = null;

				// Verifica se existe excecao p/ o PHI e SSM enviado
				Long count = fatExcCnvGrpItemProcDAO.buscaExcecaoPhiSsmEnviado(regIch.getPhiSeq(), vGrcSus, pPhoRealiz, pIphRealiz,
						pCspSeq, pCnvCodigo);
				if (count != null && count.intValue() >  0) {
					// Existe excecao p/ o PHI e SSM enviado
					// Busca item proced hospitalar equivalente ao proc
					// hospitalar interno na tabela de excecao
					final FatExcCnvGrpItemProc fatExcCnvGrpItemProc = fatExcCnvGrpItemProcDAO
							.buscaItemProcedimentoHospitalarEquivalenteProcedimentoHospitalarInternoTabelaExcecao(regIch.getPhiSeq(),
									regIch.getQuantidadeRealizada() != null ? regIch.getQuantidadeRealizada().shortValue() : null, vGrcSus, DominioSituacao.A, pPhoRealiz, pIphRealiz, pCspSeq,
									pCnvCodigo);
					if (fatExcCnvGrpItemProc != null) {
						vIphPhoSeq = fatExcCnvGrpItemProc.getIphPhoSeq();
						vIphSeq = fatExcCnvGrpItemProc.getIphSeq();
					}
				} else {
					// NAO existe excecao p/ o PHI e SSM enviado
					// Busca item proced hospitalar equivalente ao proc
					// hospitalar interno
					FatConvGrupoItemProced fatConvGrupoItemProced = fatConvGrupoItensProcedDAO
							.buscaItemProcedimentoHospitalarEquivalenteProcedimentoHospitalarInternoTabelaExcecao(regIch.getPhiSeq(),
									regIch.getQuantidadeRealizada(), vGrcSus, DominioSituacao.A, pCspSeq, pCnvCodigo);
					if (fatConvGrupoItemProced != null) {
						vIphPhoSeq = fatConvGrupoItemProced.getId().getIphPhoSeq();
						vIphSeq = fatConvGrupoItemProced.getId().getIphSeq();
					}
				}

				logar("pho: {0} iph: {1}", vIphPhoSeq, vIphSeq);

				// Busca valor do IPH				
				vValor = BigDecimal.ZERO;
				regIpc = fatVlrItemProcedHospCompsDAO.buscaValorItemProcedimentoHospitalar(vIphPhoSeq, vIphSeq, vCpe);
				if (regIpc != null) {
					vValor = (regIpc.getVlrServHospitalar() != null ? regIpc.getVlrServHospitalar() : BigDecimal.ZERO).add(
							regIpc.getVlrServProfissional() != null ? regIpc.getVlrServProfissional() : BigDecimal.ZERO).add(
							regIpc.getVlrSadt() != null ? regIpc.getVlrSadt() : BigDecimal.ZERO).add(
							regIpc.getVlrAnestesista() != null ? regIpc.getVlrAnestesista() : BigDecimal.ZERO);
				}
				
				logar("valor: {0}", vValor);

				try {
					String matriculaRPad = StringUtils.rightPad(regIch.getMatricula() != null ? regIch.getMatricula() : "", 10, " ");
					String solicitacaoRPad = StringUtils.rightPad(regIch.getSolicitacao() != null ? regIch.getSolicitacao() : "",12, " ");

					FatAgrupItemConta fatAgrupItemConta = new FatAgrupItemConta();
					
					
					
					fatAgrupItemConta.setId(new FatAgrupItemContaId(pCthSeq, regIch.getPhiSeq(), regIch.getDthrRealizado()));
					fatAgrupItemConta.setQuantidadeRealizada(regIch.getQuantidadeRealizada() != null ? regIch.getQuantidadeRealizada().shortValue() : null);
					fatAgrupItemConta.setItemProcedimentoHospitalar(fatItensProcedHospitalarDAO.obterPorChavePrimaria(new FatItensProcedHospitalarId(vIphPhoSeq, vIphSeq)));
					fatAgrupItemConta.setValor(vValor);
					
					String mat1 = matriculaRPad.substring(0, 3).trim();
					if(mat1!= null && mat1.length()>0){
						fatAgrupItemConta.setSerVinCodigoResp(Short.valueOf(mat1));
					}
					String mat2 = matriculaRPad.substring(3, 10).trim();
					if(mat2!= null && mat2.length()>0){
						fatAgrupItemConta.setSerMatriculaResp(Integer.valueOf(mat2));
					}
											
					String sol1 = solicitacaoRPad.substring(0, 8).trim();
					if(sol1!= null && sol1.length()>0){
						fatAgrupItemConta.setIseSoeSeq(Integer.valueOf(sol1));
					}
					
					String sol2 = solicitacaoRPad.substring(8, 12).trim();
					if(sol2!= null && sol2.length()>0){
						fatAgrupItemConta.setIseSeqp(Short.valueOf(sol2));
					}											

					getFatAgrupItemContaDAO().persistir(fatAgrupItemConta);
					getFatAgrupItemContaDAO().flush();
					// getFaturamentoFacade().evict(fatAgrupItemConta);
				} catch (Exception e) {
					LOG.error(EXCECAO_CAPTURADA, e);
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(61);
					faturamentoON.criarFatLogErrors("ERRO AO INSERIR AGRUPAMENTO ITEM CONTA: " + e.getMessage(), INT, pCthSeq,
							null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
							null, null, null, null, null, null, null, "RN_CTHC_ATU_AGRUPICH", null, null,fatMensagemLog);

					result = Boolean.FALSE;
				}
			}
		}

		return result;
	}

	
	private List<ListarItensContaHospitalarRnCthcAtuAgrupichVO> agrupa(
			List<ListarItensContaHospitalarRnCthcAtuAgrupichVO> listaItensContaHospitalar) throws ApplicationBusinessException {

		List<ListarItensContaHospitalarRnCthcAtuAgrupichVO> listaRetorno = new ArrayList<ListarItensContaHospitalarRnCthcAtuAgrupichVO>();
		boolean achou = false;
		for (ListarItensContaHospitalarRnCthcAtuAgrupichVO vo : listaItensContaHospitalar) {
			achou = false;
			
			//verifica data
			Date dthrRealizado = null;
			Date dataResult = null;
			
			if(this.verificarIndDiariaPhi(vo.getPhiSeq(), vo.getCspCnvCodigo())) {
				dataResult = vo.getDtAltaAdministrativa();
				// #47742 - Correção Alta Administrativa nula, corrigido pela Marina e replicado no AGHU.
				if(dataResult==null) {
					dataResult = vo.getDthrRealizado();
				}
			} else {
				dataResult = vo.getDthrRealizado();
			}
			
			if(CoreUtil.isMenorMesAno(dataResult,INICIO_PORTARIA_203_04)){
				dthrRealizado = DateUtil.obterData(2011, 03, 1);
			} else {
				Calendar cal = DateUtil.getCalendarBy(DateUtil.truncaData(dataResult));
				cal.set(Calendar.DAY_OF_MONTH, 1);
				dthrRealizado = cal.getTime();
			}			
			//fim verifica data
			
			
			for (ListarItensContaHospitalarRnCthcAtuAgrupichVO novo : listaRetorno) {
				if (novo.getPhiSeq().equals(vo.getPhiSeq()) && 
						DateUtil.truncaData(novo.getDthrRealizado()).equals(DateUtil.truncaData(dthrRealizado))) {
					achou = true;
					novo.setDthrRealizado(dthrRealizado);
					if (novo.getMatricula() != null && vo.getMatricula() != null) {
						if (Long.parseLong(novo.getMatricula()) > Long.parseLong(vo.getMatricula())) {
							novo.setMatricula(vo.getMatricula());
						}
					}
					if (novo.getSolicitacao() != null && vo.getSolicitacao() != null) {
						if (Long.parseLong(novo.getSolicitacao()) > Long.parseLong(vo.getSolicitacao())) {
							novo.setSolicitacao(vo.getSolicitacao());
						}
					}
					novo.setQuantidadeRealizada(novo.getQuantidadeRealizada() + vo.getQuantidadeRealizada());
				}
			}
			if (!achou) { //se nao achou adiciona
				ListarItensContaHospitalarRnCthcAtuAgrupichVO novo = new ListarItensContaHospitalarRnCthcAtuAgrupichVO();
				novo.setPhiSeq(vo.getPhiSeq());
				novo.setDthrRealizado(dthrRealizado);
				novo.setQuantidadeRealizada(vo.getQuantidadeRealizada());
				novo.setMatricula(vo.getMatricula());
				novo.setSolicitacao(vo.getSolicitacao());
				listaRetorno.add(novo);
			}
			
		}
		
		Collections.sort (listaRetorno, new ListarItensContaHospitalarRnCthcAtuAgrupichComparator()); 
		
		return listaRetorno; 
	}

	class ListarItensContaHospitalarRnCthcAtuAgrupichComparator implements Comparator<ListarItensContaHospitalarRnCthcAtuAgrupichVO> {  
		@Override
		public int compare(ListarItensContaHospitalarRnCthcAtuAgrupichVO p1,
				ListarItensContaHospitalarRnCthcAtuAgrupichVO p2) {
			return p1.getPhiSeq().compareTo(p2.getPhiSeq());
		}
	}
	
	/**
	 * ORADB FATC_get_ind_diaria_phi
	 * 
	 * @param phiSeq
	 * @param cnvCodigo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected Boolean verificarIndDiariaPhi(final Integer phiSeq, final Short cnvCodigo) throws ApplicationBusinessException {
		final Short valorGrupoContaSUS = buscarVlrShortAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		
		Map<String, Object> map = this.getvFatAssociacaoProcedimentoDAO().obterCodTabelaParaIPH(
				phiSeq, cnvCodigo, valorGrupoContaSUS); 
	
		Boolean indCobrancaDiaria = null;
		
		if(map == null) {
			indCobrancaDiaria = false;
		} else {
			final Short iphPhoSeq = (Short) map.get("iphPhoSeq");
			final Integer iphSeq = (Integer) map.get("iphSeq");
			
			if(getFaturamentoRN().verificarCaracteristicaExame(iphSeq, iphPhoSeq, DominioFatTipoCaractItem.ADMITE_LIBERACAO_DE_QTD)) {
				indCobrancaDiaria = false;
			} else {
				indCobrancaDiaria = (Boolean) map.get("indCobrancaDiaria");
			}
		}
		
		return indCobrancaDiaria;
	}
	
	
	/**
	 * ORADB Procedure FATK_CTH2_RN_UN.RN_CTHC_ATU_SELECSSM
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount", "PMD.NPathComplexity"})
	protected Boolean rnCthcAtuSelecssm(final Integer pCthSeq, final Boolean pPrevia, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final FatkCthRN fatkCthRN = getFatkCthRN();		
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		final FatItemContaHospitalarDAO fatItemContaHospitalarDAO = getFatItemContaHospitalarDAO();
		final FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
		final FatProcedHospInternosDAO fatProcedHospInternosDAO = getFatProcedHospInternosDAO();
		
		FatContasHospitalares regCthIni = null;
		FatContasHospitalares regCth = null;
		Short vPho = null;
		Integer vIph = null;
		Long vCodSus = null;
		Integer vClinica = null;
		Boolean vCirmultPolitr = null;
		Boolean vAids = null;
		Boolean vBuscaDoador = null;
		Boolean ssmOk = Boolean.FALSE;
		Byte vQtdClinicaMedica = 0;
		Byte vQtdClinicaCirurgica = 0;
		BigDecimal vVlrMax = BigDecimal.ZERO;
		int vQtdSsms = 0;
		int vIndiceSsm;
		int vIndiceIch = 0;
		int vIndMax = 0;
		List<Short> vIchSeq = new ArrayList<Short>();
		List<Integer> vPhiSeq = new ArrayList<Integer>();
		List<Short> vIphPhoSeq = new ArrayList<Short>();
		List<Integer> vIphSeq = new ArrayList<Integer>();
		List<Long> vIphCodSus = new ArrayList<Long>();
		List<BigDecimal> vValor = new ArrayList<BigDecimal>();
		List<Boolean> vPreviaOk = new ArrayList<Boolean>();
		
		Boolean result = Boolean.FALSE;
		
		FatVariaveisVO fatVariaveisVO = new FatVariaveisVO();

		// Busca dados da conta
		//atribuirContextoSessao(VariaveisSessaoEnum.FATK_CTH2_RN_UN_V_MAIOR_VALOR, false);
		fatVariaveisVO.setvMaiorValor(Boolean.FALSE);

		regCthIni = fatContasHospitalaresDAO.obterPorChavePrimaria(pCthSeq);
		FatContasHospitalares contaIniOld = null;
		try{
			contaIniOld = faturamentoFacade.clonarContaHospitalar(regCthIni);
		}catch (Exception e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
		}

		if (regCthIni != null && regCthIni.getProcedimentoHospitalarInternoRealizado() != null) {
			vPho = null;
			vIph = null;
			vCodSus = null;

			RnCthcVerItemSusVO rnCthcVerItemSusVO = fatkCthRN.rnCthcVerItemSus(DominioOrigemProcedimento.I,
					regCthIni.getConvenioSaudePlano() != null ? regCthIni.getConvenioSaudePlano().getId().getCnvCodigo() : null,
					regCthIni.getConvenioSaudePlano() != null ? regCthIni.getConvenioSaudePlano().getId().getSeq() : null, (short) 1,
					regCthIni.getProcedimentoHospitalarInternoRealizado().getSeq(), null);
			if (rnCthcVerItemSusVO != null) {
				vPho = rnCthcVerItemSusVO.getPhoSeq();
				vIph = rnCthcVerItemSusVO.getIphSeq();
				vCodSus = rnCthcVerItemSusVO.getCodSus();
				ssmOk = rnCthcVerItemSusVO.getRetorno();
			}

			// Verifica SSM IPH associado ao PHI
			if (CoreUtil.igual(Boolean.TRUE,ssmOk)) {
				// Busca indicadores de procedimento realizado de cirurgia
				// multipla ou de AIDS/politraumatizado/busca ativa doador:
				FatItensProcedHospitalar fatItensProcedHospitalar = fatItensProcedHospitalarDAO.obterPorChavePrimaria(
						new FatItensProcedHospitalarId(vPho, vIph));
				if (fatItensProcedHospitalar != null) {
					vCirmultPolitr = fatItensProcedHospitalar.getCirurgiaMultipla();
					vAids = fatItensProcedHospitalar.getAidsPolitraumatizado();
					vBuscaDoador = fatItensProcedHospitalar.getBuscaDoador();
				}

				vCirmultPolitr = vCirmultPolitr != null ? vCirmultPolitr : false;
				vAids = vAids != null ? vAids : false;
				vBuscaDoador = vBuscaDoador != null ? vBuscaDoador : false;

				if (!vCirmultPolitr && !vAids && !vBuscaDoador) {
					// Nao é SSM cirurgia multila/politrumatizado
					// Nao é SSM de tratamento de AIDS
					// Nao é SSM de busca ativa doador de orgaos

					// Se nao é nenhum dos casos, limpa SSM Realizado
					// p/escolher o melhor
					regCthIni.setProcedimentoHospitalarInternoRealizado(null);
					faturamentoFacade.persistirContaHospitalar(regCthIni, contaIniOld, nomeMicrocomputador, dataFimVinculoServidor);
					//faturamentoFacade.commit(TRANSACTION_TIMEOUT_24_HORAS);
					this.commitTransaction(); // 2 vez
					this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
					//getFaturamentoFacade().evict(regCthIni);
				}
			}
		}

		// Busca dados da conta novamente
		regCthIni = fatContasHospitalaresDAO.obterPorChavePrimaria(pCthSeq);

		if (regCthIni != null && regCthIni.getProcedimentoHospitalarInternoRealizado() != null) {
			//atribuirContextoSessao(VariaveisSessaoEnum.FATK_CTH2_RN_UN_V_MAIOR_VALOR, true);
			fatVariaveisVO.setvMaiorValor(Boolean.TRUE);

			logar("phi pre definido");
			// Executa previa/encerramento da conta com o SSM pre-definido
			if (this.rnCthcAtuEncPrv(pCthSeq, pPrevia, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO)) {
				result = Boolean.TRUE;
			} else {
				result = Boolean.FALSE;
			}
		} else {
			logar("cnv {0} csp {1} phi {2}",
				(regCthIni.getConvenioSaudePlano() != null ? regCthIni.getConvenioSaudePlano().getId().getCnvCodigo() : ""),
				(regCthIni.getConvenioSaudePlano() != null ? regCthIni.getConvenioSaudePlano().getId().getSeq() : ""),
				(regCthIni.getProcedimentoHospitalarInternoRealizado() != null ? regCthIni.getProcedimentoHospitalarInternoRealizado().getSeq() : ""));
			
			// Busca itens ativos da conta
			vQtdClinicaMedica = 0;
			vQtdClinicaCirurgica = 0;

			List<FatItemContaHospitalar> listaItensContaHospitalar = fatItemContaHospitalarDAO.listarItensContaHospitalar(
					pCthSeq, DominioSituacaoItenConta.A);

			if (listaItensContaHospitalar != null && !listaItensContaHospitalar.isEmpty()) {
				final Integer codigoClinicaCirurgica = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_CLINICA_CIRURGICA);
				
				Map<String, List<BuscarProcedHospEquivalentePhiVO>> rnCthcVerItemSusVOMap = fatkCthRN.rnCthcVerItemSusLoadAll(DominioOrigemProcedimento.I,
						regCthIni.getConvenioSaudePlano() != null ? regCthIni.getConvenioSaudePlano().getId().getCnvCodigo()
								: null, regCthIni.getConvenioSaudePlano() != null ? regCthIni.getConvenioSaudePlano().getId()
								.getSeq() : null, (short) 1, listaItensContaHospitalar);
				
				for (FatItemContaHospitalar regIch : listaItensContaHospitalar) {
					vPho = null;
					vIph = null;
					vCodSus = null;

					RnCthcVerItemSusVO rnCthcVerItemSusVO = fatkCthRN.rnCthcVerItemSus(DominioOrigemProcedimento.I,
							regCthIni.getConvenioSaudePlano() != null ? regCthIni.getConvenioSaudePlano().getId().getCnvCodigo()
									: null, regCthIni.getConvenioSaudePlano() != null ? regCthIni.getConvenioSaudePlano().getId()
									.getSeq() : null, (short) 1, regIch.getProcedimentoHospitalarInterno().getSeq(), rnCthcVerItemSusVOMap);

					if (rnCthcVerItemSusVO != null) {
						vPho = rnCthcVerItemSusVO.getPhoSeq();
						vIph = rnCthcVerItemSusVO.getIphSeq();
						vCodSus = rnCthcVerItemSusVO.getCodSus();
						ssmOk = rnCthcVerItemSusVO.getRetorno();
					}

					if (CoreUtil.igual(Boolean.TRUE,ssmOk)) {
						vQtdSsms++;
						logar("eh SSM, qtd SSMs {0}", vQtdSsms);

						vIchSeq.add(regIch.getId().getSeq());
						vPhiSeq.add(regIch.getProcedimentoHospitalarInterno().getSeq());
						vIphPhoSeq.add(vPho);
						vIphSeq.add(vIph);
						vIphCodSus.add(vCodSus);

						// Verifica clinica para o ssm incluido
						FatItensProcedHospitalar aux = fatItensProcedHospitalarDAO.obterPorChavePrimaria( new FatItensProcedHospitalarId(vPho,vIph) );
						vClinica = (aux != null && aux.getClinica() != null) ? aux.getClinica().getCodigo() : Integer.valueOf(0);
						
						if (CoreUtil.igual(codigoClinicaCirurgica,vClinica)) {
							vQtdClinicaCirurgica++;
						} else {
							vQtdClinicaMedica++;
						}
					}
				}
			}

			logar("qtd total SSMs {0}", vQtdSsms);
			logar("qtd total Clinicos {0}", vQtdClinicaMedica);
			logar("qtd total Cirurgicos {0}", vQtdClinicaCirurgica);

			// Verifica info de ssms clinicos e cirurgicos
			if (vQtdClinicaMedica != null && !CoreUtil.igual(vQtdClinicaMedica,0) && vQtdClinicaCirurgica != null
					&& !CoreUtil.igual(vQtdClinicaCirurgica,0)) {
				try {
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(44);
					FatLogError fatLogError = new FatLogError(new Date(), servidorLogado != null ? servidorLogado.getUsuario() : null,
							"CONTA POSSUI SSMS CLíNICO E CIRURGICO. ", INT, regCthIni.getSeq(), null, null, vCodSus, null,
							new Date(), null, null, null, vPho, null, vIph, null,
							regCthIni.getProcedimentoHospitalarInterno() != null ? regCthIni.getProcedimentoHospitalarInterno()
									.getSeq() : null, regCthIni.getProcedimentoHospitalarInternoRealizado() != null ? regCthIni
									.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, null, vCodSus, null, vPho,
							null, vIph, null, null, null, "RN_CTHC_ATU_SELECSSM", null, null,fatMensagemLog);

					faturamentoFacade.persistirLogError(fatLogError);
					//faturamentoFacade.commit(TRANSACTION_TIMEOUT_24_HORAS);
					//getFaturamentoFacade().evict(fatLogError);
					this.commitTransaction();
					this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
				} catch (Exception e) {
					logar("A seguinte exceção deve ser ignorada, seguindo a lógica migrada do PL/SQL: ", e);
				}

				return false;
			}

			if (vQtdSsms == 0) {
				// Executa previa/encerramento da conta com o SSM pre-definido
				result = this.rnCthcAtuEncPrv(pCthSeq, pPrevia, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
			} else if (vQtdSsms == 1) {
				// Quando houver apenas 1 ssm poupa processamento coloca
				// como realizado o unico ssm disponivel nos itens e executa
				// previa/encerramento
				logar("TEM APENAS 1 SSM {0}", vPhiSeq.get(0));
			//	atribuirContextoSessao(VariaveisSessaoEnum.FATK_CTH2_RN_UN_V_MAIOR_VALOR, true);
				fatVariaveisVO.setvMaiorValor(Boolean.TRUE);

				FatContasHospitalares contaHospitalar = fatContasHospitalaresDAO.obterPorChavePrimaria(pCthSeq);
				FatContasHospitalares contaOld = null;
				try{
					contaOld = faturamentoFacade.clonarContaHospitalar(contaHospitalar);
				}catch (Exception e) {
					LOG.error(EXCECAO_CAPTURADA, e);
					throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
				}
				contaHospitalar.setProcedimentoHospitalarInternoRealizado(fatProcedHospInternosDAO.obterPorChavePrimaria(vPhiSeq.get(0)));
				faturamentoFacade.persistirContaHospitalar(contaHospitalar, contaOld, nomeMicrocomputador, dataFimVinculoServidor);
				//faturamentoFacade.commit(TRANSACTION_TIMEOUT_24_HORAS);
				this.commitTransaction();
				this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
				//getFaturamentoFacade().evict(contaHospitalar);

				result = this.rnCthcAtuEncPrv(pCthSeq, pPrevia, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
			} else {
				vIndiceSsm = 1;

				do {
					logar("indice SSM {0} phi(ind SSM) {1}", vIndiceSsm, vPhiSeq.get(vIndiceSsm - 1));

					// Altera o SSM realizado da conta
					FatContasHospitalares contaHospitalar = fatContasHospitalaresDAO.obterPorChavePrimaria(pCthSeq);
					FatContasHospitalares contaOld = null;
					try{
						contaOld = faturamentoFacade.clonarContaHospitalar(contaHospitalar);
					}catch (Exception e) {
						LOG.error(EXCECAO_CAPTURADA, e);
						throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
					}
					contaHospitalar.setProcedimentoHospitalarInternoRealizado(fatProcedHospInternosDAO
							.obterPorChavePrimaria(vPhiSeq.get(vIndiceSsm - 1)));
					faturamentoFacade.persistirContaHospitalar(contaHospitalar, contaOld, nomeMicrocomputador, dataFimVinculoServidor);
				    //faturamentoFacade.commit(TRANSACTION_TIMEOUT_24_HORAS);
					this.commitTransaction();
					this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
					//getFaturamentoFacade().evict(contaHospitalar);
					logar("upd SSm Real cta {0}", vPhiSeq.get(vIndiceSsm - 1));

					// Cancela os outros SSMs da conta
					vIndiceIch = 1;

					alterarSituacaoItensContaHospitalar(pCthSeq, pPrevia,
							dataFimVinculoServidor, vQtdSsms, vIndiceSsm, vIndiceIch, vIchSeq, vPhiSeq, DominioSituacaoItenConta.N);
				    //faturamentoFacade.commit(TRANSACTION_TIMEOUT_24_HORAS);
					this.commitTransaction();
					this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);

					// Executa previa da conta com o novo SSM
					if (this.rnCthcAtuEncPrv(pCthSeq, true, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO)) {
						vPreviaOk.add(true);
						logar("prv ok {0}", vPhiSeq.get(vIndiceSsm - 1));
					} else {
						vPreviaOk.add(false);
						logar("prv falhou {0}", vPhiSeq.get(vIndiceSsm - 1));
					}

					// Busca novos dados da conta
					if (vPreviaOk.get(vIndiceSsm - 1)) {
						regCth = fatContasHospitalaresDAO.obterPorChavePrimaria(pCthSeq);

						vValor.add((regCth.getValorSh() != null ? regCth.getValorSh() : BigDecimal.ZERO).add(
								regCth.getValorSp() != null ? regCth.getValorSp() : BigDecimal.ZERO).add(
								regCth.getValorSadt() != null ? regCth.getValorSadt() : BigDecimal.ZERO).add(
								regCth.getValorOpm() != null ? regCth.getValorOpm() : BigDecimal.ZERO).add(
								regCth.getValorHemat() != null ? regCth.getValorHemat() : BigDecimal.ZERO).add(
								regCth.getValorRn() != null ? regCth.getValorRn() : BigDecimal.ZERO).add(
								regCth.getValorTransp() != null ? regCth.getValorTransp() : BigDecimal.ZERO).add(
								regCth.getValorUti() != null ? regCth.getValorUti() : BigDecimal.ZERO).add(
								regCth.getValorUtie() != null ? regCth.getValorUtie() : BigDecimal.ZERO).add(
								regCth.getValorAcomp() != null ? regCth.getValorAcomp() : BigDecimal.ZERO));
					} else {
						vValor.add(BigDecimal.ZERO);
					}

					// Reativa os demais SSMs da conta
					vIndiceIch = 1;
										
					alterarSituacaoItensContaHospitalar(pCthSeq, pPrevia,
							dataFimVinculoServidor, vQtdSsms, vIndiceSsm, vIndiceIch, vIchSeq, vPhiSeq, DominioSituacaoItenConta.A);					
					//faturamentoFacade.commit(TRANSACTION_TIMEOUT_24_HORAS);
					this.commitTransaction();
					this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);

					vIndiceSsm++;
				} while (vIndiceSsm <= vQtdSsms);

				// Verifica qual SSM fechou a conta com maior valor
				vIndiceSsm = 1;
				vIndMax = vIndiceSsm;
				vVlrMax = vValor.get(vIndiceSsm - 1);

				do {
					logar("ind SSM {0} ICH {1} PHI {2} PHO {3} IPH {4} COD {5} POK {6} VLR {7}",
							vIndiceSsm, 
							vIchSeq.get(vIndiceSsm - 1), 
							vPhiSeq.get(vIndiceSsm - 1),
							vIphPhoSeq.get(vIndiceSsm - 1),
							vIphSeq.get(vIndiceSsm - 1),
							vIphCodSus.get(vIndiceSsm - 1),
							vPreviaOk.get(vIndiceSsm - 1),
							vValor.get(vIndiceSsm - 1));

					if (vValor.get(vIndiceSsm - 1).compareTo(vVlrMax) > 0) {
						vIndMax = vIndiceSsm;
						vVlrMax = vValor.get(vIndiceSsm - 1);
					}

					vIndiceSsm++;
				} while (vIndiceSsm <= vQtdSsms);

				logar("ind SSM {0} VLR {1}", vIndMax, vValor.get(vIndMax - 1));

				// Coloca o SSM escolhido como SSM Realizado da conta
				FatContasHospitalares contaHospitalar = fatContasHospitalaresDAO.obterPorChavePrimaria(pCthSeq);
				FatContasHospitalares contaOld = null;
				try{
					contaOld = faturamentoFacade.clonarContaHospitalar(contaHospitalar);
				}catch (Exception e) {
					LOG.error(EXCECAO_CAPTURADA, e);
					throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
				}
				contaHospitalar.setProcedimentoHospitalarInternoRealizado(fatProcedHospInternosDAO.obterPorChavePrimaria(vPhiSeq.get(vIndMax - 1)));
				faturamentoFacade.persistirContaHospitalar(contaHospitalar, contaOld, nomeMicrocomputador, dataFimVinculoServidor);
				//faturamentoFacade.commit(TRANSACTION_TIMEOUT_24_HORAS);
				this.commitTransaction();
				this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
				//faturamentoFacade.evict(contaHospitalar);

				// Cancela os outros SSMs da conta
				vIndiceIch = 1;
				
				alterarSituacaoItensContaHospitalar(pCthSeq, pPrevia,
						dataFimVinculoServidor, vQtdSsms, vIndMax, vIndiceIch, vIchSeq, vPhiSeq, DominioSituacaoItenConta.N);				
				//faturamentoFacade.commit(TRANSACTION_TIMEOUT_24_HORAS);
				this.commitTransaction();
				this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);

				// Executa previa/encerramento da conta com o novo SSM
				//atribuirContextoSessao(VariaveisSessaoEnum.FATK_CTH2_RN_UN_V_MAIOR_VALOR, true);
				fatVariaveisVO.setvMaiorValor(Boolean.TRUE);

				if (this.rnCthcAtuEncPrv(pCthSeq, pPrevia, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO)) {
					result = Boolean.TRUE;
				} else {
					result = Boolean.FALSE;
				}

				if ((!pPrevia && !result) || pPrevia) {
					// Reativa os demais SSMs da conta
					vIndiceIch = 1;					
					
					alterarSituacaoItensContaHospitalar(pCthSeq, pPrevia,
							dataFimVinculoServidor, vQtdSsms, vIndMax, vIndiceIch, vIchSeq, vPhiSeq, DominioSituacaoItenConta.A);
					//faturamentoFacade.commit(TRANSACTION_TIMEOUT_24_HORAS);
					this.commitTransaction();
					this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
				}
			}
		}
			
		return result;
	}

	private void alterarSituacaoItensContaHospitalar(final Integer pCthSeq,
			final Boolean pPrevia, final Date dataFimVinculoServidor,
			int vQtdSsms, int vIndiceMax, int vIndiceIch, List<Short> vIchSeq,
			List<Integer> vPhiSeq, final DominioSituacaoItenConta dominioSituacaoItenConta) throws BaseException {		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		do {
			switch (dominioSituacaoItenConta) {
				case N:
					logar("indice ICH {0} ich(ind ich) {1} phi(ind ich) {2}",
							vIndiceIch, vIchSeq.get(vIndiceIch - 1), vPhiSeq.get(vIndiceIch - 1));
					break;
				case A:
					logar("indice ICH {0}", vIndiceIch);
					break;
				default:
					break;
			}

			if (vPhiSeq.get(vIndiceIch - 1) != null && !CoreUtil.igual(vPhiSeq.get(vIndiceIch - 1),vPhiSeq.get(vIndiceMax - 1))) {
				switch (dominioSituacaoItenConta) {
					case N:
						logar("vai fazer upd CANC ICH {0}", vIchSeq.get(vIndiceIch - 1));
						break;					
					default:
						break;
				}
				
				FatItemContaHospitalar itemContaHospitalar = getFatItemContaHospitalarDAO()
						.obterPorChavePrimaria(new FatItemContaHospitalarId(pCthSeq, vIchSeq.get(vIndiceIch - 1)));
				
				FatItemContaHospitalar itemContaHospitalarOld = null;
				try{
					itemContaHospitalarOld = getFaturamentoFacade().clonarItemContaHospitalar(itemContaHospitalar);
				}catch (Exception e) {
					LOG.error(EXCECAO_CAPTURADA, e);
					throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
				}
				
				itemContaHospitalar.setIndSituacao(dominioSituacaoItenConta);
				getItemContaHospitalarON().atualizarItemContaHospitalarSemValidacoesForms( itemContaHospitalar, itemContaHospitalarOld,
																					  true,
																					  servidorLogado, dataFimVinculoServidor, pPrevia, null);
				//getFaturamentoFacade().evict(itemContaHospitalar);
				
				switch (dominioSituacaoItenConta) {
					case N:
						logar("fez upd CANC ICH {0}", vIchSeq.get(vIndiceIch - 1));
						break;
					case A:
						logar("fez upd ativando ICH {0}", vIchSeq.get(vIndiceIch - 1));
						break;
					default:
						break;
				}
			}

			vIndiceIch++;
		} while (vIndiceIch <= vQtdSsms);
	}
	
	/**
	 * ORADB Procedure FATK_CTH2_RN_UN.RN_CTHC_ATU_ENC_PRV
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount"})
	protected Boolean rnCthcAtuEncPrv(Integer pCthSeq, Boolean pPrevia, String nomeMicrocomputador, final Date dataFimVinculoServidor, final FatVariaveisVO fatVariaveisVO) throws BaseException {
		Boolean result = Boolean.TRUE;
		Date dataPrevia = null;

		FatContasHospitalares regConta = null;
		Integer vPhiTomo = null;
		
		Date dthrRealizado = null;
		
		FatItemContaHospitalar regIchPhiCbo = null;
		FatItemContaHospitalar regIchPhi = null;
		Integer vQtdItens = null;
		Short vIphQtdItem = null;
		Short vPhoSolic = null;
		Integer vIphSolic = null;
		Long vCodSusSolic = null;
		Boolean solicitadoOk = Boolean.FALSE;
		Short vPhoRealiz = null;
		Integer vIphRealiz = null;
		Long vCodSusRealiz = null;
		Boolean realizadoOk = Boolean.FALSE;
		Boolean vRealizMae = Boolean.FALSE;
		Short vPhoRealizMae = null;
		Integer vIphRealizMae = null;
		Integer vPacCodigo = null;
		Integer vPacProntuario = null;
		Integer vIntSeq = null;
		Boolean vCaract = null;
		Integer vPhiCateterismo = null;
		String vCidIni = null;
		String vCidSec = null;
		String pErroCid = null;
		Long vQtdCidsSec = null;
		Short vDiariasConta = 0;
		Integer vDiasConta = 0;
		Short vDiariasAcomp = 0;
		Byte vMaxUtiAih = 0;
		Byte vDiasMesIniUti = 0;
		Byte vDiasMesAntUti = 0;
		Byte vDiasMesAltaUti = 0;
		Integer vDiasUtiTotal = 0;
		DominioTipoAltaUTI vTipoUti = null;
		DominioTipoIdadeUTI vIdadeUti = null;
		Boolean vUtiNeo = false;
		Byte vUtiDigIni = null;
		Byte vUtiDigAnt = null;
		Byte vUtiDigAlta = null;
		DominioTipoIdadeUTI vUtiDigTipo = null;
		FatEspelhoItemContaHosp regItemEspelho = null;
		Long vCodSus = null;
		Long vPtosAnest = null;
		Long vPtosCirurgiao = null;
		Long vPtosSadt = null;
		Integer vTivSeq = null;
		Byte vTaoSeq = null;
		Integer vNfFalsa = null;
		Short vQtdProced = null;
		Integer vTivCodSus = null;
		Byte vTaoCodSus = null;
		Boolean contaOk  = Boolean.FALSE;
		Boolean contaOkk = Boolean.FALSE;
		Boolean contaDepoisOk = Boolean.FALSE;
		Boolean vConsistente = null;
		Short regrasIch = null;
		Boolean encerrar = Boolean.FALSE;
		DominioTipoItemEspelhoContaHospitalar vTipoItem = null;
		Integer vNotaFiscal = null;
		Long vCgcNf = null;
		String vSerie = null;
		String vLote = null;
		String vRegAnvisa = null;
		Long vCnpjRegAnvisa = null;
		BigDecimal vVlrServHosp = BigDecimal.ZERO;
		BigDecimal vVlrServProf = BigDecimal.ZERO;
		BigDecimal vVlrSadt = BigDecimal.ZERO;
		BigDecimal vVlrProced = BigDecimal.ZERO;
		BigDecimal vVlrAnest = BigDecimal.ZERO;
		BigDecimal vVlrServHospAto = BigDecimal.ZERO;
		BigDecimal vVlrServProfAto = BigDecimal.ZERO;
		BigDecimal vVlrSadtAto = BigDecimal.ZERO;
		BigDecimal vVlrProcedAto = BigDecimal.ZERO;
		BigDecimal vVlrAnestAto = BigDecimal.ZERO;
		BigDecimal vVlrShRealiz = BigDecimal.ZERO;
		BigDecimal vVlrSpRealiz = BigDecimal.ZERO;
		BigDecimal vVlrSadtRealiz = BigDecimal.ZERO;
		BigDecimal vVlrAnestRealiz = BigDecimal.ZERO;
		BigDecimal vVlrProcedRealiz = BigDecimal.ZERO;
		Short vEicSeqp = null;
		Boolean vCirMul = null;
		Boolean vAids = null;
		Boolean vPolit = null;
		Integer vQtdAtosObrig = null;
		int vIndiceAto;
		Boolean vAtoObrigConsistente = null;
		Long vCodSusAto = null;
		Integer vPtosAnestAto = null;
		Integer vPtosCirurgiaoAto = null;
		Integer vPtosSadtAto = null;
		Integer vEaiSeqp = null;
		Short vTahCodSus = null;
		String vNroDcih = null;
		AipPacientes regPac = null;
		Integer vNacCodigoInvalido = null;
		Integer vNacCodigoPadrao = null;
		String vDocumentoPac = null;
		Byte vIndDocPac = null;
		AinResponsaveisPaciente regRespPac = null;
		VAipEnderecoPaciente regPacEnder = null;
		Byte vMspCodSus = null;
		Byte vMspDesdobr = null;
		Byte vSiaCodSus = null;
		StringBuilder vMotivoCobranca = new StringBuilder();
		Boolean vCobraDias = null;
		Integer vCodUnidCorreio = null;
		Integer vCepPadrao = null;
		Long vCpfMedicoSolicRespons = null;
		Integer vMatriculaRespInt = null;
		Short vVinculoRespInt = null;
		Long vCpfMedicoSolicRespons2 = null;
		Byte vTciEci = null;
		Byte vTciCodSus = null;
		Byte vTciInt = null;
		Byte vTciDcs = null;
		String vCidTciIni = null;
		String vCidTciFin = null;
		String vIndInfeccao = null;
		String vEnfermaria = null;
		String vLeito = null;
		Integer vClinicaRealiz = null;
		Integer vClinicaRealizHdiaCir = null;
		Long vCpfMedicoAuditor = null;
		Date vAihDthrEmissao = null;
		Long vNroAihAnterior = null;
		DominioSituacaoConta vIndSituacao = null;
		Date vDtIntAdministrativa = null;
		FatProcedHospInternos vPhiRzdoMae = null;
		Long vNroAihPosterior = null;
		Short vPermPrevista = null;
		Short vMaxUtiProc = null;
		DominioCobrancaDiaria vCobrancaDiaria = null;
		Short vMaxQtdConta = null;
		Integer vCodExclusaoCritica = null;
		MbcProcedimentoCirurgicos vIchPci = null;
		Short vEspMbc = null;
		Integer vDiasUteis = null;
		Long vCodigoNeutro = null;
		String vMsg = null;
		Boolean vRealizAih5 = null;
		Boolean vRealizAih5Mae = null;
		FatItemContaHospitalar regIch = null;
		Integer vChaveCirg = null;
		String vExclusaoCritica = null;
		Integer vCodCentral = null;
		String vUf = null;
		String vCnrac = null;
		Integer vAtdSeq = null;
		Integer vAtdSeqMae = null;
		Long vNroAih = null;
		Long vNroAihAut = null;
		Integer vConta = null;
		Short vQtdeMax = null;
		DominioModoCobranca vModoCobranca = null;
		DominioModoCobranca vModoCobrancaAto = null;
		Short vIchRealiz = null;
		Date vDthrPci = null;
		Short vQtdeRealizada = null;
		Short vPerda = null;
		Integer vIph = null;
		Short vPhoEl = null;
		Integer vIphEl = null;
		Integer vQtdSsms = 0;
		Boolean vSucesso = Boolean.FALSE;
		Boolean ssmOk = Boolean.FALSE;
		Boolean vHospDiaCirg = Boolean.FALSE;
		String vMensagemIni = null;
		String vMensagemAnt = null;
		String vMensagemAlta = null;
		String vSenhaCerih = null;
		Long vCpfProf = null;
		Short vIndEquipe = null;
		String vCbo = null;
		Boolean vErroCBO = Boolean.FALSE;
		Short vPhoErro = null;
		Integer vIphErro = null;
		Integer vMatriculaProf = null;
		Short vVinculoProf = null;
		FatDocumentoCobrancaAihs regDci = null;
		RnFatcVerItprocexcVO rnFatcVerItprocexcVO = null;
		Long vMultProc = null;
		List<ItemContaHospitalarVO> tabAnatoPat = new ArrayList<ItemContaHospitalarVO>();

		final IAghuFacade aghuFacade = this.getAghuFacade();
		final IPacienteFacade pacienteFacade = getPacienteFacade();
		final ICadastroPacienteFacade cadastroPacienteFacade = getCadastroPacienteFacade();
		final IInternacaoFacade internacaoFacade = getInternacaoFacade();
		final IBlocoCirurgicoFacade blocoCirugicoFacade = getBlocoCirurgicoFacade();
		final FatkCth4RN fatkCth4RN = getFatkCth4RN();
		final FatkCth5RN fatkCth5RN = getFatkCth5RN();
		final FaturamentoON faturamentoON = getFaturamentoON();
		
		final FaturamentoRN faturamentoRN = getFaturamentoRN();
		final FatSeparaItensPorCompRN separaItensPorCompRN = getFatSeparaItensPorCompRN();
		final FatkCthRN fatkCthRN = getFatkCthRN();
		final FatkCthnRN fatkCthnRN = getFatkCthnRN();
		final FatkCth6RN fatkCth6RN = getFatkCth6RN();
		final FaturamentoFatkPrzRN faturamentoFatkPrzRN = getFaturamentoFatkPrzRN();
		final FaturamentoFatkIchRN faturamentoFatkIchRN = getFaturamentoFatkIchRN();
		final FaturamentoFatkDciRN faturamentoFatkDciRN = getFaturamentoFatkDciRN();
		final VerificacaoFaturamentoSusRN faturamentoFatkSusRN = getFaturamentoFatkSusRN();
		final FaturamentoFatkCap2RN faturamentoFatkCap2RN = getFaturamentoFatkCap2RN();
		final FaturamentoFatkIctRN faturamentoFatkIctRN = getFaturamentoFatkIctRN();
		final FaturamentoFatkCgiRN faturamentoFatkCgiRN = getFaturamentoFatkCgiRN();
		final SaldoUtiAtualizacaoRN saldoUtiAtualizacaoRN = getSaldoUtiAtualizacaoRN();
		final SaldoBancoCapacidadeAtulizacaoRN saldoBancoCapacidadeAtulizacaoRN = getSaldoBancoCapacidadeAtulizacaoRN();
		final VerificacaoItemProcedimentoHospitalarRN verificacaoItemProcedimentoHospitalarRN = getVerificacaoItemProcedimentoHospitalarRN();

		final FatContasHospitalaresDAO fatContasHospitalaresDAO = getFatContasHospitalaresDAO();
		final FatAgrupItemContaDAO fatAgrupItemContaDAO = getFatAgrupItemContaDAO();
		final FatCampoMedicoAuditAihDAO fatCampoMedicoAuditAihDAO = getFatCampoMedicoAuditAihDAO();
		final FatEspelhoItemContaHospDAO fatEspelhoItemContaHospDAO = getFatEspelhoItemContaHospDAO();
		final FatAtoMedicoAihDAO fatAtoMedicoAihDAO = getFatAtoMedicoAihDAO();
		final FatEspelhoAihDAO fatEspelhoAihDAO = getFatEspelhoAihDAO();
		final FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
		final FatAihDAO fatAihDAO = getFatAihDAO();
		final FatTipoAihDAO fatTipoAihDAO = getFatTipoAihDAO();
		final FatItemContaHospitalarDAO fatItemContaHospitalarDAO = getFatItemContaHospitalarDAO();
		final FatCidContaHospitalarDAO fatCidContaHospitalarDAO = getFatCidContaHospitalarDAO();
		final FatDiariaUtiDigitadaDAO fatDiariaUtiDigitadaDAO = getFatDiariaUtiDigitadaDAO();
		final FatContasInternacaoDAO fatContasInternacaoDAO = getFatContasInternacaoDAO();
		final FatExcCaraterInternacaoDAO fatExcCaraterInternacaoDAO = getFatExcCaraterInternacaoDAO();
		final FatDocumentoCobrancaAihsDAO fatDocumentoCobrancaAihsDAO = getFatDocumentoCobrancaAihsDAO();
		final FatTiposVinculoDAO fatTiposVinculoDAO = getFatTiposVinculoDAO();
		final FatTipoAtoDAO fatTipoAtoDAO = getFatTipoAtoDAO();
		final FatContaSugestaoDesdobrDAO fatContaSugestaoDesdobrDAO = getFatContaSugestaoDesdobrDAO();
		final IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		final ItemContaHospitalarON itemContaHospitalarON = getItemContaHospitalarON();
		
		final Integer[] phiSeqsTomografia = this.buscarVlrIntegerArrayAghParametro(AghuParametrosEnum.P_PHI_AGRUPADOR_TOMOGRAFIA);
		
		final String ufSede = buscarVlrTextoAghParametro(AghuParametrosEnum.P_AGHU_UF_SEDE_HU);
		
		logar("..............Inicio PREVIA/ENCERRAMENTO às "+DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date())+" ..............");
		
		if (CoreUtil.igual(Boolean.TRUE, pPrevia)) {
			dataPrevia = new Date();
		}

		final Short phoSeq = buscarVlrShortAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);		
		
		contaOk = Boolean.TRUE;
		contaDepoisOk = Boolean.TRUE;
		
		// Busca dados da conta hospitalar
		FatContasHospitalares regContaIni = fatContasHospitalaresDAO.pesquisaFatContasHospitalaresAbertaOuFechadaGrupoSUS(pCthSeq);
		FatContasHospitalares contaOld = null;
		if (regContaIni != null) {			
			try{
				contaOld = faturamentoFacade.clonarContaHospitalar(regContaIni);
			}catch (Exception e) {
				LOG.error(EXCECAO_CAPTURADA, e);
				throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
			}	
			
			//fatLogErrorDAO.removerPorCthModulo(pCthSeq, DominioModuloCompetencia.INT);
			List<FatEspelhoItemContaHosp> itensEspelho = fatEspelhoItemContaHospDAO.listarFatEspelhoItemContaHospRnCthcAtuEncPrv(pCthSeq);
			fatEspelhoItemContaHospDAO.removerRnCthcAtuEncPrv(pCthSeq);
			fatCampoMedicoAuditAihDAO.removerRnCthcAtuEncPrv(pCthSeq);			
			fatAtoMedicoAihDAO.removerRnCthcAtuEncPrv(pCthSeq);
			List<FatEspelhoAih> espelhos = fatEspelhoAihDAO.listarPorCthDataPreviaNotNull(regContaIni.getSeq());
			fatEspelhoAihDAO.removerRnCthcAtuEncPrv(regContaIni.getSeq());
			fatEspelhoAihDAO.flush();

			for(FatEspelhoAih espelho : espelhos) {
				faturamentoFacade.evict(espelho);
			}
			
			for(FatEspelhoItemContaHosp itemEspelho : itensEspelho) {
				faturamentoFacade.evict(itemEspelho);
			}

			if (regContaIni.getValorContaHospitalar() != null) {
				faturamentoRN.excluirFatValorContaHospitalar(regContaIni.getValorContaHospitalar());
				//getFatValorContaHospitalarDAO().remover(regContaIni.getValorContaHospitalar());
				regContaIni.setValorContaHospitalar(null);
			}

			if (regContaIni.getPerdaItensConta() != null && !regContaIni.getPerdaItensConta().isEmpty()) {
				for (FatPerdaItemConta fatPerdaItemConta : regContaIni.getPerdaItensConta()) {
					faturamentoRN.excluirFatPerdaItemConta(fatPerdaItemConta);
					//getFatPerdaItemContaDAO().remover(fatPerdaItemConta);
					//this.flush();
					//faturamentoFacade.evict(fatPerdaItemConta);
				}				
				regContaIni.getPerdaItensConta().clear();
			}
			
			/* #20458 - eSchweigert 30/08/2012
			  
			// Marina 23/05/2011
			List<FatItemContaHospitalar> listaFatItensContasHospitalaresAfa = fatItemContaHospitalarDAO.listarItensContaHospitalarComOrigemAfaEPorContaHospitalarSeq(pCthSeq);
			if (listaFatItensContasHospitalaresAfa != null && !listaFatItensContasHospitalaresAfa.isEmpty()) {
				for (FatItemContaHospitalar fatItensContaHospitalar : listaFatItensContasHospitalaresAfa) {
					FatItemContaHospitalar oldFatItensContaHospitalar = null;
					try{
						oldFatItensContaHospitalar = getFaturamentoFacade().clonarItemContaHospitalar(fatItensContaHospitalar);
					}catch (Exception e) {
						logError("Exceção capturada: ", e);
						throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
					}
					fatItensContaHospitalar.setIndSituacao(DominioSituacaoItenConta.A);
					getItemContaHospitalarON().atualizarItemContaHospitalarSemValidacoesForms(fatItensContaHospitalar, oldFatItensContaHospitalar, true);
					getFaturamentoFacade().evict(fatItensContaHospitalar);
				}
			}
			
			AghAtendimentos vAtendimento = aghuFacade.obterPrimeiroAtendimentoDeContasInternacaoPorContaHospitalar(pCthSeq);
			Integer vAtdSeqAfa = null; 
			if(vAtendimento != null){
				vAtdSeqAfa = vAtendimento.getSeq();
			}
			
			logar("v_atd_seq_afa: {0}", vAtdSeqAfa);
			logar("reg_conta.dt_int_administrativa: {0}", regContaIni.getDataInternacaoAdministrativa());
			logar("reg_conta.dt_alta_administrativa: {0}", regContaIni.getDtAltaAdministrativa());
		    
			getFaturamentoFatkInterfaceAfaRN().rnFatpInsDisp(nvl(vAtdSeqAfa, 0), regContaIni.getDataInternacaoAdministrativa(), regContaIni.getDtAltaAdministrativa(), pCthSeq, 1);
		    // fim Marina 23/05/2011
			*/

			faturamentoON.criarFatLogErrors("ENCERRAMENTO", null, pCthSeq, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,null);
			
			String[] sitCodigos = {		buscarVlrTextoAghParametro(AghuParametrosEnum.P_AGHU_SIT_ITEM_SOLIC_CANCEL),
										buscarVlrTextoAghParametro(AghuParametrosEnum.P_AGHU_SIT_ITEM_SOLIC_LIBER) };
			final Integer[] phisCExameAnato = {
					this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_AGHU_PHI_SEQ_IMUNOHISTOQUIMICA),
					this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_AGHU_PHI_SEQ_IMUNO_DEP_AP) };
			DominioSituacaoItenConta[] arraySituacaoItenConta = { DominioSituacaoItenConta.C, DominioSituacaoItenConta.D };
			
			// MARINA 22/10/2012 - CHAMADO 81434
			final List<FatItemContaHospitalar> cExameAnato = getFatItemContaHospitalarDAO()
					.listarExamesAnatos(pCthSeq, arraySituacaoItenConta,
							sitCodigos, phisCExameAnato);
			// -- Marina 07/05/2013
			Integer vInd = 0;
			Boolean vResultado = Boolean.TRUE;
			
			// -- Marina 07/05/2013
			logar("EXAMES ANATOPATOLOGICOS");
			if(Boolean.FALSE.equals(pPrevia)) {
				if (!cExameAnato.isEmpty()) {
					for (FatItemContaHospitalar rExameAnato : cExameAnato) {
						vInd++;
						AelItemSolicitacaoExamesId itemSolicitacaoExameId = rExameAnato.getItemSolicitacaoExame().getId();
						Integer iseSoeSeq = itemSolicitacaoExameId.getSoeSeq();
						Short iseSeqp = itemSolicitacaoExameId.getSeqp();
						Integer cthSeq = rExameAnato.getContaHospitalar().getSeq();
						logar("v_ind: {0}", vInd);
						logar("ACHOU EXAMES ANATOPATOLOGICOS");
						logar("r_exame_anato.ind_situacao: {0}", rExameAnato.getIndSituacao());
						logar("r_exame_anato.ise_soe_seq: {0}",	itemSolicitacaoExameId.getSoeSeq());
						logar("r_exame_anato.ise_seqp: {0}", itemSolicitacaoExameId.getSeqp());
						// -- Cria tabela temporária
						ItemContaHospitalarVO itemContaHospitalarVO = new ItemContaHospitalarVO();
						itemContaHospitalarVO.setCthSeq(cthSeq);
						itemContaHospitalarVO.setIndSituacao(rExameAnato.getIndSituacao());
						itemContaHospitalarVO.setIseSoeSeq(iseSoeSeq);
						itemContaHospitalarVO.setIseSeqp(iseSeqp);
						tabAnatoPat.add(itemContaHospitalarVO);
						
						List<FatItemContaHospitalar> listaFatItemContaHospitalar = getFatItemContaHospitalarDAO()
								.listarPorContaHospitalarItemSolicitacaoExame(
										iseSeqp, iseSoeSeq, cthSeq);
						for (FatItemContaHospitalar itemContaHospitalar : listaFatItemContaHospitalar) {
							if (itemContaHospitalar.getId().getCthSeq().equals(cthSeq)
									&& itemContaHospitalar.getItemSolicitacaoExame().getId()
											.getSeqp().equals(iseSeqp)
									&& itemContaHospitalar.getItemSolicitacaoExame().getId()
											.getSoeSeq().equals(iseSoeSeq)) {
								FatItemContaHospitalar itemContaHospitalarOld = null;
								try {
									itemContaHospitalarOld = faturamentoFacade
											.clonarItemContaHospitalar(itemContaHospitalar);
								} catch (Exception e) {
									LOG.error(EXCECAO_CAPTURADA, e);
									throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
								}
								itemContaHospitalar.setIndSituacao(DominioSituacaoItenConta.C);
								itemContaHospitalarON
										.atualizarItemContaHospitalarSemValidacoesForms(
												itemContaHospitalar,
												itemContaHospitalarOld, true,
												servidorLogado, dataFimVinculoServidor, pPrevia, null);
							}
						}
					
					FatMensagemLog fatMensagemLog= new FatMensagemLog();
					fatMensagemLog.setCodigo(87);
						faturamentoON.criarFatLogErrors("EXAME IMUNOHISTOQUIMICA NÃO LIBERADO",
										INT, regContaIni.getSeq(), null, null, null, null,
										dataPrevia,	null, null, vPhoSolic, vPhoRealiz,
										vIphSolic, vIphRealiz, vPacCodigo, 
										regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni.getProcedimentoHospitalarInterno().getSeq() : null,
										regContaIni.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni.getProcedimentoHospitalarInternoRealizado().getSeq() : null,
										null, vPacProntuario, vCodSusRealiz, vCodSusSolic, null, null,
									null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);
						if (Boolean.FALSE.equals(pPrevia) && !CoreUtil.igual(CoreUtil.nvl(regContaIni.getIndAutorizadoSms(), "N"), "N")) {
							vResultado = Boolean.FALSE;
							contaOk = Boolean.FALSE;
						}
					}
				}
				result = vResultado;
			}
			// -- FIM MARINA 07/05/2013

			if (regContaIni.getConvenioSaudePlano() != null) {
				logar("CNV: {0} CSP: {1}", regContaIni.getConvenioSaudePlano().getId().getCnvCodigo(), regContaIni.getConvenioSaudePlano().getId().getSeq());
			}

			// Busca paciente da conta hospitalar
			RnCthcVerPacCtaVO rnCthcVerPacCtaVO = fatkCthRN.rnCthcVerPacCta(regContaIni.getSeq());
			if (rnCthcVerPacCtaVO != null) {
				vPacCodigo = rnCthcVerPacCtaVO.getPacCodigo();
				vPacProntuario = rnCthcVerPacCtaVO.getPacProntuario();
				vIntSeq = rnCthcVerPacCtaVO.getIntSeq();

				if (!rnCthcVerPacCtaVO.getRetorno()) {
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(172);
					faturamentoON.criarFatLogErrors("NAO FOI POSSIVEL IDENTIFICAR O PACIENTE DA CONTA.", INT, regContaIni.getSeq(),
							null, null, null, null, dataPrevia, null, null, null, null, null, null, null, null, null, null, null,
							null, null, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

					contaOk = Boolean.FALSE;
					logar("##### AGHU - CONTA NOT OK ##### Motivo: NAO FOI POSSIVEL IDENTIFICAR O PACIENTE DA CONTA. (A)");
				}
			}

			logar("PAC: {0} PRONT: {1} INT: {2}", vPacCodigo, vPacProntuario, vIntSeq);

			// Motivo cobranca
			FatSituacaoSaidaPaciente fatSituacaoSaidaPaciente = regContaIni.getSituacaoSaidaPaciente();			
			if (fatSituacaoSaidaPaciente == null) {

				logar("MOTIVO E SITUACAO DE SAIDA DO PACIENTE NAO ENCONTRADO");
				FatMensagemLog fatMensagemLog = new FatMensagemLog();
				fatMensagemLog.setCodigo(169);
				faturamentoON.criarFatLogErrors("NAO FOI POSSIVEL IDENTIFICAR O MOTIVO E SITUACAO DE SAIDA DO PACIENTE.", INT,
						regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, null, null, null, null, vPacCodigo,
						null, null, null, vPacProntuario, null, null, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null,
						null,fatMensagemLog);

				if (!pPrevia) {
					contaOk = Boolean.FALSE;
					logar("##### AGHU - CONTA NOT OK ##### Motivo: MOTIVO E SITUACAO DE SAIDA DO PACIENTE NAO ENCONTRADO (B)");
					result = Boolean.FALSE;
					logar("##### AGHU - ERRO AO ENCERRAR ##### Motivo: NAO FOI POSSIVEL IDENTIFICAR O MOTIVO E SITUACAO DE SAIDA DO PACIENTE. (1)");
				}
			} else {
				vMspCodSus = fatSituacaoSaidaPaciente.getMotivoSaidaPaciente().getCodigoSus();
				vSiaCodSus = fatSituacaoSaidaPaciente.getCodigoSus();
				vCobraDias = fatSituacaoSaidaPaciente.getCobrancaDias();
				
				if (vMspCodSus != null) {
					vMotivoCobranca.append(String.valueOf(vMspCodSus).substring(0, 1));
				}

				if (vSiaCodSus != null) {
					vMotivoCobranca.append(String.valueOf(vSiaCodSus).substring(0, 1));
				}

				vCobraDias = vCobraDias != null ? vCobraDias : false;

				if (vMotivoCobranca.length() == 0) {
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(169);
					faturamentoON.criarFatLogErrors("NAO FOI POSSIVEL IDENTIFICAR O MOTIVO E SITUACAO DE SAIDA DO PACIENTE.", INT,
							regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, null, null, null, null, vPacCodigo,
							null, null, null, vPacProntuario, null, null, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV,
							null, null,fatMensagemLog);

					if (!pPrevia) {
						contaOk = Boolean.FALSE;
						logar("##### AGHU - CONTA NOT OK ##### Motivo: NAO FOI POSSIVEL IDENTIFICAR O MOTIVO E SITUACAO DE SAIDA DO PACIENTE. (C)");
						result = Boolean.FALSE;
						logar("##### AGHU - ERRO AO ENCERRAR ##### Motivo: NAO FOI POSSIVEL IDENTIFICAR O MOTIVO E SITUACAO DE SAIDA DO PACIENTE. (2)");
					}
				} else if (regContaIni.getDtAltaAdministrativa() != null
						&& DateValidator.validarMesmoDia(regContaIni.getDtAltaAdministrativa(), regContaIni
								.getDataInternacaoAdministrativa()) && !CoreUtil.igual("S", regContaIni.getIndAutorizaFat())) {
					
					final Short aux = Short.valueOf( vMotivoCobranca.substring(0, 1) );
					
					/* 2 */ final Short pMotivoSaidaPacientePermanencia = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_PERMANENCIA);
					/* 4 */ final Short pCodMotivoSaidaObito = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_COD_MOTIVO_SAIDA_OBITO);
					/* 5 */ final Short pCodMotivoSaidaOutros = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_COD_MOTIVO_SAIDA_OUTROS);
					/* 6 */ final Short pMotivoSaidaPacienteAltaParturiente = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_ALTA_PARTURIENTE);

					if (!CoreUtil.igual(aux, pMotivoSaidaPacientePermanencia) && 
							!CoreUtil.igual(aux, pCodMotivoSaidaObito) && 
								!CoreUtil.igual(aux, pCodMotivoSaidaOutros) &&
									!CoreUtil.igual(aux, pMotivoSaidaPacienteAltaParturiente)) {
						
						// Busca iph do realizado
						RnCthcVerItemSusVO rnCthcVerPtemSusVO = fatkCthRN.rnCthcVerItemSus(DominioOrigemProcedimento.I, regContaIni
								.getConvenioSaudePlano().getId().getCnvCodigo(), regContaIni.getConvenioSaudePlano().getId().getSeq(),
								(short) 1, regContaIni.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
										.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null);

						realizadoOk = rnCthcVerPtemSusVO.getRetorno();
						vPhoRealiz = rnCthcVerPtemSusVO.getPhoSeq();
						vIphRealiz = rnCthcVerPtemSusVO.getIphSeq();
						vCodSusRealiz = rnCthcVerPtemSusVO.getCodSus();

						vCaract = faturamentoRN.verificarCaracteristicaExame(vIphRealiz, vPhoRealiz,
								DominioFatTipoCaractItem.HOSPITAL_DIA_CIRURGICO);

						if (vCaract == null || !vCaract) {
							
							FatMensagemLog fatMensagemLog = new FatMensagemLog();
							fatMensagemLog.setCodigo(52);
							faturamentoON.criarFatLogErrors(
									"DATA INTERNACAO E ALTA IGUAIS COM MOTIVO SAIDA DIFERENTE DE OBITO OU DESDOBRAMENTO.", INT,
									regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, null, null, null, null,
									vPacCodigo, null, null, null, vPacProntuario, null, null, null, null, null, null, null, null,
									RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

							contaOk = Boolean.FALSE;
							logar("##### AGHU - CONTA NOT OK ##### Motivo: DATA INTERNACAO E ALTA IGUAIS COM MOTIVO SAIDA DIFERENTE DE OBITO OU DESDOBRAMENTO. (D)");
						} else {
							vHospDiaCirg = Boolean.TRUE;
						}
					}
				}
			}

			// Marina 12/03/2012   - chamado 65477
			// Verifica se tem Nutrição enteral lança na conta, se sim,
			// verifica se o motivo da alta pode cobrar o dia da alta, se não pode, recalcular as diarias lançadas na conta.
			logar("v_msp_cod_sus:  {0}", vMspCodSus);
			logar("v_sia_cod_sus:  {0}", vSiaCodSus);
			logar("v_motivo_cobranca:  {0}", vMotivoCobranca);
			
			final Short pMotivoSPacPermanencia = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_PERMANENCIA);
			final Short pMotivoSPacTransf = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_TRANSFERENCIA);
			final Short pMotivoSPacObito = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_POR_OBITO);
			final Short pMotivoSPacOutrosMot = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_POR_OUTROS_MOTIVOS);
			
			// open c_busca_motivo (v_msp_cod_sus, v_sia_cod_sus);
			final FatSituacaoSaidaPaciente ssp = getFatSituacaoSaidaPacienteDAO().obterFatMotivoSaidaPacientePorCodigoSusSituacaoAberto
																					(vMspCodSus,
																					 vSiaCodSus, 
																					 pMotivoSPacPermanencia,
																					 pMotivoSPacTransf,
																					 pMotivoSPacObito,
																					 pMotivoSPacOutrosMot
																					);
			
			String motivo = null;
			
			// nvl(substr(to_char(msp.codigo_sus),1,1) || substr(to_char(sia.codigo_sus),1,1),0) motivo
			if(ssp != null && ssp.getCodigoSus() != null && 
					ssp.getMotivoSaidaPaciente() != null && ssp.getMotivoSaidaPaciente().getCodigoSus() != null){
				motivo = StringUtils.substring(ssp.getMotivoSaidaPaciente().getCodigoSus().toString(), 0,1) +
				  		 StringUtils.substring(ssp.getCodigoSus().toString(), 0,1); 
			} else {
				motivo = "0";
			}
			
			Short vSomaUm = Short.valueOf(motivo);
			logar("1 - v_soma_um:  {0}", vSomaUm);
			
			if(vSomaUm.shortValue() == 0){
				
				// Marina 12/03/2012

				final Integer[] phis = { this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_NUTRICAO_ENTERAL_PEDIATRIA),
										 this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_NUTRICAO_ENTERAL_NEONATOLOGIA),
										 this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SEQ_NUTRICAO_ENTERAL_ADULTO)
									   };
				
				final List<Short> rBuscaNutricao = fatItemContaHospitalarDAO.listarItensContaHospitalar(pCthSeq, phis);
				
				if (rBuscaNutricao != null && !rBuscaNutricao.isEmpty()) {
					
					// Chamado 83636
	                // Não processar novamente os itens cujo a origem é ANU no encerramento da Conta
	                // Se não pode cobrar o dia da alta, estou cancelando este lançamento que corresponde a este dia.

					logar("tem lançamento de nutrição - dt_alta: {0}", regContaIni.getDtAltaAdministrativa());
					
					final List<FatItemContaHospitalar> lst =  fatItemContaHospitalarDAO.obterFatItemContaHospitalarPorIndOrigem( pCthSeq, 
																														   		 DominioIndOrigemItemContaHospitalar.ANU,
																														   		 regContaIni.getDtAltaAdministrativa());
					for (FatItemContaHospitalar item : lst) {
						item.setIndSituacao(DominioSituacaoItenConta.C);
						itemContaHospitalarON.atualizarItemContaHospitalarSemValidacoesForms(item, true, dataFimVinculoServidor, servidorLogado, pPrevia);
						// faturamentoFacade.evict(item);
					}
					
					/* eSchweigert, comentado em 08/11/2012 #Chamado 83636
					AghAtendimentos vAtendimento = aghuFacade.obterPrimeiroAtendimentoDeContasInternacaoPorContaHospitalar(pCthSeq);
					Integer vAtdSeqAfa = null; 
					if(vAtendimento != null){
						vAtdSeqAfa = vAtendimento.getSeq();
					}
					
					vDataAlta = (Date) CoreUtil.nvl(regConta != null ? regConta.getDtAltaAdministrativa() : null, DateUtil.adicionaDias(new Date(), -1));
					vDataAlta = DateUtil.adicionaDias(vDataAlta, -1);
					
					logar("v_data_alta:  {0}", vDataAlta);
					logar("v_atd_seq_afa:  ", vAtdSeqAfa);
					
					// Chama a interface do ANU para recalcular as diarias quando não pode cobrar o dia da alta.
					final Integer opcao = 1; 
					getFaturamentoFatkInterfaceAnuRN().rnFatpInsNutrEnt(vAtdSeqAfa, regConta != null ? regConta.getDataInternacaoAdministrativa() : null, vDataAlta, pCthSeq, opcao);
					*/    
				}
			}
			
			logar("MTV: {0}", vMotivoCobranca.toString());

			// Verifica se a conta possui numero de AIH
			if (regContaIni.getAih() == null) {
				if (regContaIni.getProcedimentoHospitalarInternoRealizado() != null) {
					FatConvenioSaudePlano fatConvenioSaudePlano = regContaIni.getConvenioSaudePlano();
					
					realizadoOk = Boolean.FALSE;
					vPhoRealiz = null;
					vIphRealiz = null;
					vCodSusRealiz = null;

					RnCthcVerItemSusVO rnCthcVerPtemSusVO = fatkCthRN.rnCthcVerItemSus(DominioOrigemProcedimento.I,
							fatConvenioSaudePlano != null ? fatConvenioSaudePlano.getId().getCnvCodigo() : null,
							fatConvenioSaudePlano != null ? fatConvenioSaudePlano.getId().getSeq() : null, (short) 1, regContaIni
									.getProcedimentoHospitalarInternoRealizado().getSeq(), null);

					if (rnCthcVerPtemSusVO != null) {
						realizadoOk = rnCthcVerPtemSusVO.getRetorno();
						vPhoRealiz = rnCthcVerPtemSusVO.getPhoSeq();
						vIphRealiz = rnCthcVerPtemSusVO.getIphSeq();
						vCodSusRealiz = rnCthcVerPtemSusVO.getCodSus();
					}

					if (realizadoOk) {
						// ORADB FATK_CTH2_RN_UN.C_AIH5
						FatItensProcedHospitalar  aux = fatItensProcedHospitalarDAO.obterPorChavePrimaria( new FatItensProcedHospitalarId(vPhoRealiz,vIphRealiz) );
						vRealizAih5 = (aux != null) ? aux.getTipoAih5() : Boolean.FALSE;
						
						// if nvl(v_realiz_aih5,'N') = 'S' then
						if ( Boolean.TRUE.equals(vRealizAih5) ){
							if (regContaIni.getContaHospitalar() == null) {
								FatMensagemLog fatMensagemLog = new FatMensagemLog();
								fatMensagemLog.setCodigo(193);
								faturamentoON.criarFatLogErrors("NUMERO DE AIH NAO FOI INFORMADO", INT, regContaIni.getSeq(),
										null, null, null, null, dataPrevia, null, null, null, null, null, null, vPacCodigo, null,
										null, null, vPacProntuario, null, null, null, null, null, null, null, null,
										RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

								if (Boolean.FALSE.equals(pPrevia)
										&& !CoreUtil.igual(CoreUtil.nvl(regContaIni.getIndAutorizadoSms(), "N"), "N")) {
									contaOk = Boolean.FALSE;
									logar("##### AGHU - CONTA NOT OK ##### Motivo: NUMERO DE AIH NAO FOI INFORMADO (E)");
								}
								
							} else {
								// É aih5 mas não é a primeira conta
								FatContasHospitalares contaHospitalarAnterior = regContaIni.getContaHospitalar();

								if(contaHospitalarAnterior != null){
									vIndSituacao =         contaHospitalarAnterior.getIndSituacao();
									vDtIntAdministrativa = contaHospitalarAnterior.getDataInternacaoAdministrativa();
									vPhiRzdoMae =          contaHospitalarAnterior.getProcedimentoHospitalarInternoRealizado();
									vNroAihAnterior = contaHospitalarAnterior.getAih() != null ? contaHospitalarAnterior.getAih().getNroAih() : null;
								}

								if (!CoreUtil.igual(DominioSituacaoConta.C, vIndSituacao)
										&& (contaHospitalarAnterior == null || vNroAihAnterior == null)) {
									FatMensagemLog fatMensagemLog = new FatMensagemLog();
									fatMensagemLog.setCodigo(193);
									faturamentoON.criarFatLogErrors(NUMERO_DE_AIH_NAO_FOI_INFORMADO, INT, regContaIni
											.getSeq(), null, null, null, null, dataPrevia, null, null, null, null, null, null,
											vPacCodigo, null, null, null, vPacProntuario, null, null, null, null, null, null,
											null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

									if (!pPrevia && regContaIni.getIndAutorizadoSms() != null && !CoreUtil.igual("N", regContaIni.getIndAutorizadoSms())) {
										contaOk = Boolean.FALSE;
										logar("##### AGHU - CONTA NOT OK ##### Motivo: NUMERO DE AIH NAO FOI INFORMADO (F)");
									}
								} else {
									rnCthcVerPtemSusVO = fatkCthRN.rnCthcVerItemSus(DominioOrigemProcedimento.I, (short) 1, (byte) 1, (short) 1,
											vPhiRzdoMae != null ? vPhiRzdoMae.getSeq() : null, null);

									if (rnCthcVerPtemSusVO != null) {
										vRealizMae = rnCthcVerPtemSusVO.getRetorno();
										vPhoRealizMae = rnCthcVerPtemSusVO.getPhoSeq();
										vIphRealizMae = rnCthcVerPtemSusVO.getIphSeq();
										
										if (vRealizMae) {

											// ORADB FATK_CTH2_RN_UN.C_AIH5
											aux = fatItensProcedHospitalarDAO.obterPorChavePrimaria( new FatItensProcedHospitalarId(vPhoRealizMae,vIphRealizMae) );
											vRealizAih5Mae = (aux != null) ? aux.getTipoAih5() : Boolean.FALSE;
											
											//if nvl(v_realiz_aih5_mae,'N') = 'S' then
											if ( Boolean.TRUE.equals(vRealizAih5Mae) ) {
												
												if (Boolean.TRUE.equals(contaOk) && 
														Boolean.FALSE.equals(pPrevia)) {
													// Não é prévia - atualiza aih = anterior

													// TODO Criar um parâmetro. P_AIH_CONTINUACAO
													Byte tahSeq = 5;

													regContaIni.setAih(vNroAihAnterior != null ? fatAihDAO
															.obterPorChavePrimaria(vNroAihAnterior) : null);
													regContaIni.setTipoAih(fatTipoAihDAO.obterPorChavePrimaria(tahSeq));
												}
											}
										} else {
											faturamentoON.criarFatLogErrors(
													"NAO FOI POSSIVEL IDENTIFICAR REALIZADO DA CONTA MÃE.", INT, regContaIni
															.getSeq(), null, null, null, null, dataPrevia, null, null, null, null,
													null, null, vPacCodigo, null, null, null, vPacProntuario, null, null, null,
													null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null, null);

											if (!pPrevia && regContaIni.getIndAutorizadoSms() != null && !CoreUtil.igual("N", regContaIni.getIndAutorizadoSms())) {
												contaOk = Boolean.FALSE;
												logar("##### AGHU - CONTA NOT OK ##### Motivo: NAO FOI POSSIVEL IDENTIFICAR REALIZADO DA CONTA MAE. (G)");
											}
										}
									}
								}
							}
						} else {
							FatMensagemLog fatMensagemLog = new FatMensagemLog();
							fatMensagemLog.setCodigo(193);
							faturamentoON.criarFatLogErrors(NUMERO_DE_AIH_NAO_FOI_INFORMADO, INT, regContaIni.getSeq(), null,
									null, null, null, dataPrevia, null, null, null, null, null, null, vPacCodigo, null, null,
									null, vPacProntuario, null, null, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV,
									null, null,fatMensagemLog);

							if (!pPrevia && regContaIni.getIndAutorizadoSms() != null && !CoreUtil.igual("N", regContaIni.getIndAutorizadoSms())) {
								contaOk = Boolean.FALSE;
								logar("##### AGHU - CONTA NOT OK ##### Motivo: NUMERO DE AIH NAO FOI INFORMADO. (H)");
							}
						}
					} else {
						// Realizado não OK
						FatMensagemLog fatMensagemLog = new FatMensagemLog();
						fatMensagemLog.setCodigo(193);
						faturamentoON.criarFatLogErrors(NUMERO_DE_AIH_NAO_FOI_INFORMADO, INT, regContaIni.getSeq(), null,
								null, null, null, dataPrevia, null, null, null, null, null, null, vPacCodigo, null, null, null,
								vPacProntuario, null, null, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

						if (!pPrevia && regContaIni.getIndAutorizadoSms() != null && !CoreUtil.igual("N", regContaIni.getIndAutorizadoSms())) {
							contaOk = Boolean.FALSE;
							logar("##### AGHU - CONTA NOT OK ##### Motivo: NUMERO DE AIH NAO FOI INFORMADO. (I)");
						}
					}					
					
				} else {
					// Realizado não existe na conta (is null)
					FatMensagemLog fatMensagemLogE = new FatMensagemLog();
					fatMensagemLogE.setCodigo(193);
					faturamentoON.criarFatLogErrors(NUMERO_DE_AIH_NAO_FOI_INFORMADO, INT, regContaIni.getSeq(), null, null,
							null, null, dataPrevia, null, null, null, null, null, null, vPacCodigo, null, null, null,
							vPacProntuario, null, null, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLogE);

					if (!pPrevia && regContaIni.getIndAutorizadoSms() != null && !CoreUtil.igual("N", regContaIni.getIndAutorizadoSms())) {
						contaOk = Boolean.FALSE;
						logar("##### AGHU - CONTA NOT OK ##### Motivo: NUMERO DE AIH NAO FOI INFORMADO. (J)");
					}
				}
			}

			// Verifica se a conta possui data de alta
			if (regContaIni.getDtAltaAdministrativa() == null) {
				
				FatMensagemLog fatMensagemLog = new FatMensagemLog();
				fatMensagemLog.setCodigo(49);
				faturamentoON.criarFatLogErrors("DATA DE ALTA ADMINISTRATIVA NAO ESTA PREENCHIDA.", INT, regContaIni.getSeq(), null,
						null, null, null, dataPrevia, null, null, null, null, null, null, vPacCodigo, null, null, null,
						vPacProntuario, null, null, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

				if (!pPrevia) {
					contaOk = Boolean.FALSE;
					logar("##### AGHU - CONTA NOT OK ##### Motivo: DATA DE ALTA ADMINISTRATIVA NAO ESTA PREENCHIDA. (K)");
					result = Boolean.FALSE;
					logar("##### AGHU - ERRO AO ENCERRAR ##### Motivo: DATA DE ALTA ADMINISTRATIVA NAO ESTA PREENCHIDA. (3)");
				}
			}

			// Verifica se a conta esta aberta ou fechada
			if (!CoreUtil.igual(DominioSituacaoConta.A, regContaIni.getIndSituacao())
					&& !CoreUtil.igual(DominioSituacaoConta.F, regContaIni.getIndSituacao())) {
				FatMensagemLog fatMensagemLog = new FatMensagemLog();
				fatMensagemLog.setCodigo(253);
				faturamentoON.criarFatLogErrors("SITUACAO DA CONTA HOSPITALAR DIFERENTE DE ABERTA/FECHADA.", INT, regContaIni
						.getSeq(), null, null, null, null, dataPrevia, null, null, null, null, null, null, vPacCodigo, null, null,
						null, vPacProntuario, null, null, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

				result = Boolean.FALSE;
				logar("##### AGHU - ERRO AO ENCERRAR ##### Motivo: SITUACAO DA CONTA HOSPITALAR DIFERENTE DE ABERTA/FECHADA. (4)");
			}

			logar("AIH: {0} SITU: {1}", (regContaIni.getAih() != null ? regContaIni.getAih().getNroAih() : "") ,regContaIni.getIndSituacao());

			// Verifica o item procedimento SOLICITADO da conta hospitalar
			if (regContaIni.getProcedimentoHospitalarInterno() != null) {
				RnCthcVerItemSusVO rnCthcVerPtemSusVO = fatkCthRN.rnCthcVerItemSus(DominioOrigemProcedimento.I,
						regContaIni.getConvenioSaudePlano() != null ? regContaIni.getConvenioSaudePlano().getId().getCnvCodigo()
								: null, regContaIni.getConvenioSaudePlano() != null ? regContaIni.getConvenioSaudePlano().getId()
								.getSeq() : null, (short) 1, regContaIni.getProcedimentoHospitalarInterno().getSeq(), null);

				if (rnCthcVerPtemSusVO != null) {
					solicitadoOk = rnCthcVerPtemSusVO.getRetorno();
					vPhoSolic = rnCthcVerPtemSusVO.getPhoSeq();
					vIphSolic = rnCthcVerPtemSusVO.getIphSeq();
					vCodSusSolic = rnCthcVerPtemSusVO.getCodSus();

					logar("SOLIC: PHI: {0} PHO: {1} IPH: {2} SUS:{3}", regContaIni.getProcedimentoHospitalarInterno().getSeq(), vPhoSolic, vIphSolic, vCodSusSolic);
					if (!solicitadoOk) {
						FatMensagemLog fatMensagemLog = new FatMensagemLog();
						fatMensagemLog.setCodigo(181);
						faturamentoON
								.criarFatLogErrors(
										"NAO FOI POSSIVEL IDENTIFICAR PROCEDIMENTO SOLICITADO, PROBLEMA DE CORRESPONDENCIA COM PROCED INTERNO.",
										INT, regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, null, null, null,
										null, vPacCodigo, regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
												.getProcedimentoHospitalarInterno().getSeq() : null, null, null, vPacProntuario, null,
										null, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

						result = Boolean.FALSE;
						logar("##### AGHU - ERRO AO ENCERRAR ##### Motivo: NAO FOI POSSIVEL IDENTIFICAR PROCEDIMENTO SOLICITADO, PROBLEMA DE CORRESPONDENCIA COM PROCED INTERNO. (5)");
					}
				}
			} else {
				if (regContaIni.getAih() != null) {
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(226);
					faturamentoON.criarFatLogErrors("PROCEDIMENTO SOLICITADO NAO INFORMADO.", INT, regContaIni.getSeq(), null, null,
							null, null, dataPrevia, null, null, null, null, null, null, vPacCodigo, null, null, null, vPacProntuario,
							null, null, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);
				}

				result = Boolean.FALSE;
				logar("##### AGHU - ERRO AO ENCERRAR ##### Motivo: PROCEDIMENTO SOLICITADO NAO INFORMADO. (6)");
			}

			// Verifica o item procedimento REALIZADO da conta hospitalar
			if (regContaIni.getProcedimentoHospitalarInternoRealizado() != null) {
				RnCthcVerItemSusVO rnCthcVerPtemSusVO = fatkCthRN.rnCthcVerItemSus(DominioOrigemProcedimento.I,
						regContaIni.getConvenioSaudePlano() != null ? regContaIni.getConvenioSaudePlano().getId().getCnvCodigo()
								: null, regContaIni.getConvenioSaudePlano() != null ? regContaIni.getConvenioSaudePlano().getId()
								.getSeq() : null, (short) 1, regContaIni.getProcedimentoHospitalarInternoRealizado().getSeq(), null);

				if (rnCthcVerPtemSusVO != null) {
					realizadoOk = rnCthcVerPtemSusVO.getRetorno();
					vPhoRealiz = rnCthcVerPtemSusVO.getPhoSeq();
					vIphRealiz = rnCthcVerPtemSusVO.getIphSeq();
					vCodSusRealiz = rnCthcVerPtemSusVO.getCodSus();

					logar("REALIZ: PHI: {0} PHO: {1} IPH: {2} SUS:{3} autorizado={4}",
							regContaIni.getProcedimentoHospitalarInternoRealizado().getSeq(),
							vPhoRealiz, vIphRealiz, vCodSusRealiz,
							regContaIni.getCodSusAut());

					if (realizadoOk) {
						// Busca item da conta que corresponde ao realizado
						// ETB 071004 verifica se realizado = autorizado pela sms
						//Boolean vMaiorValor = (Boolean) obterContextoSessao("FATK_CTH2_RN_UN_V_MAIOR_VALOR");
						Boolean vMaiorValor = fatVariaveisVO.getvMaiorValor();
						if (vMaiorValor) {
							if (!CoreUtil.igual(vCodSusRealiz, (regContaIni.getCodSusAut() != null ? regContaIni.getCodSusAut() : vCodSusRealiz))) {
								contaOk = Boolean.FALSE;
								logar("##### AGHU - CONTA NOT OK ##### Motivo: SSM DIFERE DO AUTORIZADO PELA SMS (L)");
								FatMensagemLog fatMensagemLog = new FatMensagemLog();
								fatMensagemLog.setCodigo(255);
								faturamentoON.criarFatLogErrors("SSM DIFERE DO AUTORIZADO PELA SMS", INT, regContaIni.getSeq(),
										null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz, vIphSolic, vIphRealiz,
										vPacCodigo, regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
												.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
												.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
												.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
										vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null,
										null,fatMensagemLog);
							}
						}

						vIchRealiz = fatItemContaHospitalarDAO.buscaMinSeq(regContaIni.getSeq(), regContaIni
								.getProcedimentoHospitalarInternoRealizado().getSeq());

						if (vIchRealiz != null) {							
							if (vMaiorValor) {
								logar(" valida CID PHI {0}", regContaIni.getProcedimentoHospitalarInternoRealizado().getSeq());
								// Valida / busca no sumário o cid primário
								RnCthpVerInsCidVO rnCthpVerInsCidVO = fatkCth5RN.rnCthpVerInsCid(regContaIni.getSeq(),
										regContaIni.getProcedimentoHospitalarInternoRealizado().getSeq(), contaOkk, 
										nomeMicrocomputador, dataFimVinculoServidor);
								if (rnCthpVerInsCidVO != null) {
									pErroCid = rnCthpVerInsCidVO.getErroCid();
									vCidIni = rnCthpVerInsCidVO.getCidIni();
								}

								if (pErroCid != null) {
									FatMensagemLog fatMsgLog = null;
									
									if(rnCthpVerInsCidVO.getCodigo() != null){
										fatMsgLog = new FatMensagemLog(rnCthpVerInsCidVO.getCodigo());
									}
									
									faturamentoON.criarFatLogErrors(pErroCid, INT, regContaIni.getSeq(), null, null, null, null,
											dataPrevia, null, null, vPhoSolic, vPhoRealiz, vIphSolic, vIphRealiz, vPacCodigo,
											regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
													.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
													.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
													.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null,
											vPacProntuario, vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null,
											RN_CTHC_ATU_ENC_PRV, null, null, fatMsgLog);

									contaOk = Boolean.FALSE;
									logar(AGHU_CONTA_NOT_OK_MOTIVO + pErroCid + " (M)");
									result = Boolean.FALSE;
									logar(AGHU_ERRO_AO_ENCERRAR_MOTIVO + pErroCid + " (6)");
								}

								// Ver se phi é DIG e PCI p/ obter especialidade da Cirurgia
								FatItemContaHospitalar aux = fatItemContaHospitalarDAO
										.obterItensContasHospitalaresEspecialidadeCirurgia(regContaIni.getSeq(), regContaIni
												.getProcedimentoHospitalarInternoRealizado().getSeq());

								if (aux != null) {
									vIchPci = aux.getProcedimentoHospitalarInterno().getProcedimentoCirurgico();
									vDthrPci = aux.getDthrRealizado();
								}

								if (vIchPci != null) {
									// É cirurgico
									// Busca especialidade da cirurgia
									vEspMbc = blocoCirugicoFacade.buscarRnCthcAtuEncPrv(vPacCodigo, vDthrPci, DominioIndRespProc.NOTA);
									
									if (vEspMbc == null) {
										// Não achou cirurgia
										
										FatMensagemLog fatMensagemLog = new FatMensagemLog();
										fatMensagemLog.setCodigo(24);
										
										faturamentoON.criarFatLogErrors("CONTA COM SSM CIRURGICO E SEM CIRURGIA CORRESPONDENTE",
												INT, regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null,
												vPhoSolic, vPhoRealiz, vIphSolic, vIphRealiz, vPacCodigo, regContaIni
														.getProcedimentoHospitalarInterno() != null ? regContaIni
														.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
														.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
														.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null,
												vPacProntuario, vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null,
												RN_CTHC_ATU_ENC_PRV, null, null, fatMensagemLog);
									} else {
										if (regContaIni.getEspecialidade() != null
												&& !CoreUtil.igual(regContaIni.getEspecialidade().getSeq(), vEspMbc)) {
											regContaIni.setEspecialidade(aghuFacade.obterAghEspecialidadesPorChavePrimaria(vEspMbc));
											faturamentoFacade.persistirContaHospitalar(regContaIni, contaOld, nomeMicrocomputador, dataFimVinculoServidor);
											try{
												contaOld = faturamentoFacade.clonarContaHospitalar(regContaIni);
											}catch (Exception e) {
												LOG.error(EXCECAO_CAPTURADA, e);
												throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
											}
										}
									}
								}
							}

							// Verifica quantos cids secundarios a conta tem(só pode ter 1)
							vQtdCidsSec = fatCidContaHospitalarDAO.buscaCountQtdCids(regContaIni.getSeq(), DominioPrioridadeCid.S);

							if (vQtdCidsSec == null) {
								vQtdCidsSec = 0l;
							}

							if (CoreUtil.maior(vQtdCidsSec, 1)) {
								
								FatMensagemLog fatMensagemLog = new FatMensagemLog();
								fatMensagemLog.setCodigo(31);
								
								faturamentoON.criarFatLogErrors("CONTA NAO PODE TER MAIS DE UM CID SECUNDARIO.", INT, regContaIni
										.getSeq(), null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz, vIphSolic,
										vIphRealiz, vPacCodigo, regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
												.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
												.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
												.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
										vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null,
										null,fatMensagemLog);

								result = Boolean.FALSE;
								logar("##### AGHU - ERRO AO ENCERRAR ##### Motivo: CONTA NAO PODE TER MAIS DE UM CID SECUNDARIO. (7)");
							} else if (CoreUtil.igual(vQtdCidsSec, 1)) {
								// Busca CID secundario da conta
								vCidSec = aghuFacade.buscaCodigoCidSecundarioConta(regContaIni.getSeq(),DominioPrioridadeCid.S);

								if (vCidSec == null) {
									
									FatMensagemLog fatMensagemLog = new FatMensagemLog();
									fatMensagemLog.setCodigo(34);
									faturamentoON.criarFatLogErrors("CONTA NAO POSSUI CID SECUNDARIO VALIDO.", INT, regContaIni
											.getSeq(), null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz,
											vIphSolic, vIphRealiz, vPacCodigo,
											regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
													.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
													.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
													.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null,
											vPacProntuario, vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null,
											RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

									if (!pPrevia && regContaIni.getIndAutorizadoSms() != null && !CoreUtil.igual("N", regContaIni.getIndAutorizadoSms())) {
										result = Boolean.FALSE;
										logar("##### AGHU - ERRO AO ENCERRAR ##### Motivo: CONTA NAO POSSUI CID SECUNDARIO VALIDO. (8)");
									}
								}

								vCidSec = StringUtils.replace(vCidSec, ".", "");
							}

							logar("CID P: {0} CID S: {1}", vCidIni, vCidSec);

							// Verifica possibilidades do Realizado

							if (!faturamentoFatkPrzRN.rnPrzcVerPossib(regContaIni.getSeq(), vPhoRealiz, vIphRealiz)) {
								FatMensagemLog fatMensagemLog = new FatMensagemLog();
								fatMensagemLog.setCodigo(37);
								faturamentoON.criarFatLogErrors(
										"CONTA NAO POSSUI POSSIBILIDADE DE FATURAMENTO.VERIFIQUE REGRAS DE COBRANÇA.", INT,
										regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz,
										vIphSolic, vIphRealiz, vPacCodigo,
										regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
												.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
												.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
												.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
										vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null,
										null,fatMensagemLog);

								if (!pPrevia) {
									result = Boolean.FALSE;
									logar("##### AGHU - ERRO AO ENCERRAR ##### Motivo: CONTA NAO POSSUI POSSIBILIDADE DE FATURAMENTO.VERIFIQUE REGRAS DE COBRANÇA. (9)");
								}
							}
						} else {
							logar("nao achou item conta correspondente ao phi do realiz");
							FatMensagemLog fatMensagemLog = new FatMensagemLog();
							fatMensagemLog.setCodigo(218);

							faturamentoON.criarFatLogErrors("PROCEDIMENTO REALIZADO NAO FOI LANCADO NOS ITENS DA CONTA.", INT,
									regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, null, null, null, null,
									vPacCodigo, regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
											.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
											.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
											.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
									vCodSusRealiz, null, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

							result = Boolean.FALSE;
							logar("##### AGHU - ERRO AO ENCERRAR ##### Motivo: PROCEDIMENTO REALIZADO NAO FOI LANCADO NOS ITENS DA CONTA. (10)");
						}
					} else {
						FatMensagemLog fatMensagemLog = new FatMensagemLog();
						fatMensagemLog.setCodigo(180);
						faturamentoON
								.criarFatLogErrors(
										"NAO FOI POSSIVEL IDENTIFICAR PROCEDIMENTO REALIZADO, PROBLEMA DE CORRESPONDENCIA  COM PROCED INTERNO.",
										INT, regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, null, null, null,
										null, vPacCodigo, regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
												.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
												.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
												.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
										null, null, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

						result = Boolean.FALSE;
						logar("##### AGHU - ERRO AO ENCERRAR ##### Motivo: NAO FOI POSSIVEL IDENTIFICAR PROCEDIMENTO REALIZADO, PROBLEMA DE CORRESPONDENCIA  COM PROCED INTERNO. (11)");
					}
				}
			} else {
				if (vMotivoCobranca.length() > 0) {
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(219);
					faturamentoON.criarFatLogErrors("PROCEDIMENTO REALIZADO NAO INFORMADO.", INT, regContaIni.getSeq(), null, null,
							null, null, dataPrevia, null, null, vPhoSolic, null, vIphSolic, null, vPacCodigo, regContaIni
									.getProcedimentoHospitalarInterno() != null ? regContaIni.getProcedimentoHospitalarInterno()
									.getSeq() : null, null, null, vPacProntuario, null, vCodSusSolic, null, null, null, null, null,
							null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);
				}

				result = Boolean.FALSE;
				logar("##### AGHU - ERRO AO ENCERRAR ##### Motivo: PROCEDIMENTO REALIZADO NAO INFORMADO. (12)");
			}

			// Ver se é ACTP e se possui item = cateterismo
			vPhiCateterismo = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_CATETERISMO);
			
			if (faturamentoRN.verificarCaracteristicaExame(vIphRealiz, vPhoRealiz, DominioFatTipoCaractItem.ACTP)) {
				if (faturamentoRN.verificaProcedimentoHospitalarInterno(regContaIni.getSeq(), vPhiCateterismo)) {
					if (CoreUtil.menor(vQtdCidsSec, 1)) {
						if (pPrevia) {
							FatMensagemLog fatMensagemLog = new FatMensagemLog();
							fatMensagemLog.setCodigo(26);
							faturamentoON.criarFatLogErrors("CONTA DE ACTP POSSUI CATETERISMO E NECESSITA CID SECUNDARIO", INT,
									regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, vPhoSolic, null, vIphSolic,
									null, vPacCodigo, regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
											.getProcedimentoHospitalarInterno().getSeq() : null, null, null, vPacProntuario, null,
									vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

							contaOk = Boolean.FALSE;
							logar("##### AGHU - CONTA NOT OK ##### Motivo: CONTA DE ACTP POSSUI CATETERISMO E NECESSITA CID SECUNDARIO (N)");
						} else {
							// Encerramento
							// Rotina de cancelamento do item de cateterismo sem cid secundário
							faturamentoFatkIchRN.rnIchpAtuSituacao(false, DominioSituacaoItenConta.N, regContaIni.getSeq(),
									vPhiCateterismo != null ? vPhiCateterismo.shortValue() : null, dataFimVinculoServidor);
						}
					}
				}
			}

		
			// Marina 08/08/2012 - Chamado 76507
			// Quando houver, nos itens da conta, mais de um procedimento cirúrgico com mesma data e hora e mesma unidade realizadora
			// , só permitir o encerramento da conta se o realizado da conta possuir uma caracterítica específica
			
			vMultProc = Long.valueOf(0);
			
			final Short cpgCphCspCnvCodigo = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
			final Byte cpgCphCspSeq = this.buscarVlrByteAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO);
			final Short iphPhoSeq = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
			final Short fogSgrGrpSeq = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_AGHU_COD_GRUPO_PROC_CIRURGICOS);
			
			final List<Long> counts = getFatItemContaHospitalarDAO().
										obterQtdeProcCirurgicosMesmaDataUnidRealiz( pCthSeq, 
																				    DominioSituacaoItenConta.A, 
																				    DominioSituacao.A, 
																				    cpgCphCspCnvCodigo, 
																				    cpgCphCspSeq, 
																				    iphPhoSeq, 
																				    fogSgrGrpSeq, 
																				    true, 				// Marina 28/08/2012
																				    DominioIndOrigemItemContaHospitalar.BCC,
																				    DominioIndOrigemItemContaHospitalar.DIG
																				   );
			
			if(counts != null && !counts.isEmpty()){
				vMultProc = counts.get(0);
			}
			
			logar("v_mult_proc: {0}", vMultProc);
			
			if(vMultProc >= 2){
				vCaract = faturamentoRN.verificarCaracteristicaExame(vIphRealiz, vPhoRealiz, DominioFatTipoCaractItem.FATURAMENTO_MULTIPLOS_PROCEDIMENTOS_CIRURGICOS);
				
				if(Boolean.FALSE.equals(vCaract)){
					logar("Não tem caracteristica ");
					FatMensagemLog fatMensagemLog= new FatMensagemLog();
					fatMensagemLog.setCodigo(88);
					faturamentoON.criarFatLogErrors("FATURAMENTO DE MULTIPLOS PROCEDIMENTOS CIRURGICOS", INT,
							regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, null, null, null, null,
							vPacCodigo, regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
									.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
									.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
									.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
							vCodSusRealiz, null, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);
					contaOk = Boolean.FALSE;
				}
			}
			// FIM Marina 08/08/2012 - Chamado 76507
			
			
			// ETB 06.01.03 fim ver se é ACTP e se possui item = cateterismo
			
			if (contaOk || pPrevia) {
				// Se a conta nao foi gerada por desdobramento, nem por reapresentacao
				if (regContaIni.getContaHospitalar() == null && !CoreUtil.igual(Boolean.TRUE, regContaIni.getIndContaReapresentada())) {
					// Verifica se é reinternacao, nesse caso nao pode encerrar a conta
					if (fatkCthRN.rnCthcVerReintern(regContaIni.getSeq(), regContaIni.getDataInternacaoAdministrativa(),
							vPacCodigo, regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
									.getProcedimentoHospitalarInterno().getSeq() : null, vPhoSolic, vIphSolic, vCodSusSolic,
							regContaIni.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
									.getProcedimentoHospitalarInternoRealizado().getSeq() : null, vPhoRealiz, vIphRealiz,
							vCodSusRealiz)) {
						// log de erro é inserido na propria funcao
						contaOk = Boolean.FALSE;
						logar("##### AGHU - CONTA NOT OK ##### Motivo: DADOS DE PARTO NÃO CADASTRADOS. (O)");
					}
				}
			}

			// Busca dados paciente
			if(vPacCodigo != null){
				regPac = pacienteFacade.obterPacientePorCodigo(vPacCodigo);
			}

			if (regPac == null) {
				logar("DADOS DO PACIENTE NAO ENCONTRADOS");
				FatMensagemLog fatMensagemLog = new FatMensagemLog();
				fatMensagemLog.setCodigo(46);


				faturamentoON
						.criarFatLogErrors("DADOS DO PACIENTE NAO ENCONTRADOS.", INT, regContaIni.getSeq(), null, null, null, null,
								dataPrevia, null, null, vPhoSolic, vPhoRealiz, vIphSolic, vIphRealiz, vPacCodigo, regContaIni
										.getProcedimentoHospitalarInterno() != null ? regContaIni.getProcedimentoHospitalarInterno()
										.getSeq() : null,
								regContaIni.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
										.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
								vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

				contaOk = Boolean.FALSE;
				logar("##### AGHU - CONTA NOT OK ##### Motivo: DADOS DO PACIENTE NAO ENCONTRADOS (P)");
			} else {
				logar("PAC: {0} dt nasc: {1}", regPac.getNome(), regPac.getDtNascimento());

				vNacCodigoInvalido = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_NACIONALIDADE_INVALIDA);
				vNacCodigoPadrao = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_NACIONALIDADE_PADRAO);
				
				// Verifica nacionalidade
				if (regPac.getAipNacionalidades() != null && CoreUtil.igual(regPac.getAipNacionalidades().getCodigo(), vNacCodigoInvalido)) {
					regPac.setAipNacionalidades(pacienteFacade.obterNacionalidade(vNacCodigoPadrao));
				}

				// Busca responsavel pelo paciente
				regRespPac = internacaoFacade.buscaResponsaveisPaciente(vIntSeq);					
				if (regPac.getNumeroPis() != null) {
					// Se o paciente tem CNS, vai a CNS dele
					String aux = regPac.getNumeroPis().toString();

					vDocumentoPac = aux.length() > 11 ? aux.substring(0, 11) : regPac.getNumeroPis().toString();
					vIndDocPac = 1;
				} else if (regRespPac != null && regRespPac.getNroCartaoSaude() != null) {
					// CNS do responsavel
					vDocumentoPac = regRespPac.getNroCartaoSaude().toString();
					vIndDocPac = 1;
				} else if (StringUtils.isNotEmpty(regPac.getRg())) {
					// RG do paciente
					vDocumentoPac = regPac.getRg();
					vIndDocPac = 2;
				} else if (regPac.getRegNascimento() != null) {
					// Registro nascimento do paciente
					// Se o campo registro de nascimento for maior que 30,
					// significa que é a nova certidão de nascimento.
					vDocumentoPac = regPac.getRegNascimento().toString();
					
					String aux = regPac.getRegNascimento().toString();
					if (aux.length() >= 30) {
						vIndDocPac = 6;
					} else {
						vIndDocPac = 3;
					}
				} else if (regPac.getCpf() != null) {
					// CPF paciente
					vDocumentoPac = regPac.getCpf().toString();
					vIndDocPac = 4;
				} else if (regRespPac != null && regRespPac.getRg() != null) {
					// RG do responsavel
					vDocumentoPac = regRespPac.getRg().toString();
					vIndDocPac = 2;
				} else if (regRespPac != null && regRespPac.getRegNascimento() != null) {
					// Registro nascimento do responsavel
					vDocumentoPac = regRespPac.getRegNascimento().toString();
					vIndDocPac = 3;
				} else if (regRespPac != null && regRespPac.getCpf() != null) {
					// CPF do responsavel
					vDocumentoPac = regRespPac.getCpf().toString();
					vIndDocPac = 4;
				} else {
					vDocumentoPac = BigDecimal.ZERO.toString();
					vIndDocPac = 5;
				}
				
				logar("RSP: {0}", regRespPac!=null?regRespPac.getCpf():null);
				
				logar("DOC: {0} IND: {1}", vDocumentoPac, vIndDocPac);
			}

			// Busca endereco pelo paciente
			regPacEnder = null;
			if(vPacCodigo != null){
				regPacEnder = cadastroPacienteFacade.obterEndecoPaciente(vPacCodigo);
			}

			if (regPacEnder!= null && regPacEnder.getCodIbge() == null) {
				regPacEnder.setCodIbge(BigDecimal.ZERO);
			}

			String auxCodIbge = regPacEnder != null && regPacEnder.getCodIbge() != null ? regPacEnder.getCodIbge().toString() : "";
			if (auxCodIbge.length() > 6) {
				regPacEnder.setCodIbge(new BigDecimal(auxCodIbge.substring(0, 6)));
			}

			if (regPacEnder == null || regPacEnder.getLogradouro() == null || regPacEnder.getNroLogradouro() == null || regPacEnder.getCep() == null
					|| regPacEnder.getUf() == null || CoreUtil.igual(regPacEnder.getCodIbge(), BigDecimal.ZERO)) {
				logar("ENDERECO DO PACIENTE INCOMPLETO OU NAO ENCONTRADO");
				FatMensagemLog fatMensagemLog = new FatMensagemLog();
				fatMensagemLog.setCodigo(58);
				faturamentoON.criarFatLogErrors("ENDERECO DO PACIENTE INCOMPLETO OU NAO ENCONTRADO.", INT, regContaIni.getSeq(),
						null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz, vIphSolic, vIphRealiz, vPacCodigo,
						regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni.getProcedimentoHospitalarInterno()
								.getSeq() : null, regContaIni.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
								.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario, null, null, null,
						null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

				contaOk = Boolean.FALSE;
				logar("##### AGHU - CONTA NOT OK ##### Motivo: ENDERECO DO PACIENTE INCOMPLETO OU NAO ENCONTRADO (Q)");
			} else {
				if (regPacEnder.getCddCodigo() != null) {
					AipCidades cidade = getCadastrosBasicosPacienteFacade().obterCidade(regPacEnder.getCddCodigo());

					if (cidade != null && CoreUtil.igual(DominioSimNao.S, cidade.getIndLogradouro())) {
						// É uma cidade que possui CEPs abertos por logradouros
						List<AipCepLogradouros> listaCepLogradouros = getCadastrosBasicosPacienteFacade().listarCepLogradourosPorCEP(regPacEnder.getCep().intValue());

						if (listaCepLogradouros != null && !listaCepLogradouros.isEmpty()) {
							// É um CEP cadastrado por logradouro
							// Valida CEP de Unidade de Correio: busca codigo
							// tipo logradouro unidade correio (AIP)
							vCodUnidCorreio = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_UNI_CORREIO);
							
						} else {
							// Nao é um CEP cadastrado por logradouro
							logar("CIDADE TEM CEPs POR LOGRADOURO");
							FatMensagemLog fatMensagemLog = new FatMensagemLog();
							fatMensagemLog.setCodigo(18);
							faturamentoON.criarFatLogErrors("CIDADE DO ENDERECO DO PACIENTE NAO ADMITE CEP GENERICO.", INT,
									regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz,
									vIphSolic, vIphRealiz, vPacCodigo,
									regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
											.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
											.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
											.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
									vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null,
									null,fatMensagemLog);

							contaOk = Boolean.FALSE;
							logar("##### AGHU - CONTA NOT OK ##### Motivo: CIDADE DO ENDERECO DO PACIENTE NAO ADMITE CEP GENERICO. (R)");
						}
					}
				} else {
					// Valida CEP de Unidade de Correio:
					// Busca codigo tipo logradouro unidade correio (AIP)
					vCodUnidCorreio = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_UNI_CORREIO);
					
					List<Integer> listaLogradouros = getCadastrosBasicosPacienteFacade().listarLogradouros(regPacEnder.getCep().intValue(), vCodUnidCorreio);

					if (listaLogradouros != null && !listaLogradouros.isEmpty()) {
						// É um CEP de unidade de correio
						// Verifica CEP padrao para enviar ao SUS (CEP do HCPA)
						vCepPadrao = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_CEP_PADRAO);
						regPacEnder.setCep(BigDecimal.valueOf(vCepPadrao));
					}
				}

				String auxCEP = regPacEnder.getCep() != null ? regPacEnder.getCep().toString() : null;

				if (auxCEP != null && auxCEP.length() > 1 && CoreUtil.igual("0000000", auxCEP.substring(1, auxCEP.length()))) {
					faturamentoON.criarFatLogErrors("CEP NÃO VALIDADO PELO DATASUS-"
							+ (regPacEnder != null ? regPacEnder.getCep() : null), INT, regContaIni.getSeq(), null, null, null,
							null, dataPrevia, null, null, vPhoSolic, vPhoRealiz, vIphSolic, vIphRealiz, vPacCodigo, regContaIni
									.getProcedimentoHospitalarInterno() != null ? regContaIni.getProcedimentoHospitalarInterno()
									.getSeq() : null, regContaIni.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
									.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario, vCodSusRealiz,
							vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null, null);

					contaOk = Boolean.FALSE;
					logar("##### AGHU - CONTA NOT OK ##### Motivo: CEP NÃO VALIDADO PELO DATASUS. (S)");
				}
			}

			// UF Fora do Estado do RS
			// Consiste na prévia ou encerramento, porém deixa encerrar			
			if (regPacEnder != null && !ufSede.equalsIgnoreCase(regPacEnder.getUf())) {
				FatMensagemLog fatMensagemLog = new FatMensagemLog();
				fatMensagemLog.setCodigo(197);
				faturamentoON
						.criarFatLogErrors("PACIENTE FORA DO ESTADO - " + (regPacEnder != null ? regPacEnder.getUf() : ""), INT,
								regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz,
								vIphSolic, vIphRealiz, vPacCodigo,
								regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
										.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
										.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
										.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
								vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);
			}

			logar("CEP: {0} CIDIBGE: {1} CDD: {2}", 
					(regPacEnder != null ? regPacEnder.getCep() : null), 
					(regPacEnder != null ? regPacEnder.getCodIbge(): null), 
					(regPacEnder != null ? regPacEnder.getCddCodigo():null)
			);

			// Verifica diarias da conta
			Date auxDate = new Date();

			/* #20458 - eSchweigert 30/08/2012

			// MARINA 07/10/2011
			logar("v_msp_cod_sus:  {0}", vMspCodSus);
			logar("v_sia_cod_sus:  {0}", vSiaCodSus);
			logar("v_motivo_cobranca:  {0}", vMotivoCobranca.toString());
			
			final Short pMotivoSPacPermanencia = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_PERMANENCIA);
			final Short pMotivoSPacTransf = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_TRANSFERENCIA);
			final Short pMotivoSPacObito = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_POR_OBITO);
			final Short pMotivoSPacOutrosMot = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_POR_OUTROS_MOTIVOS);
			
			// open c_busca_motivo (v_msp_cod_sus, v_sia_cod_sus);
			final FatSituacaoSaidaPaciente ssp = getFatSituacaoSaidaPacienteDAO().obterFatMotivoSaidaPacientePorCodigoSusSituacaoAberto
																					(vMspCodSus,
																					 vSiaCodSus, 
																					 pMotivoSPacPermanencia,
																					 pMotivoSPacTransf,
																					 pMotivoSPacObito,
																					 pMotivoSPacOutrosMot
																					);
			
			String motivo = null;
			
			// nvl(substr(to_char(msp.codigo_sus),1,1) || substr(to_char(sia.codigo_sus),1,1),0) motivo
			if(ssp != null && ssp.getCodigoSus() != null && 
					ssp.getMotivoSaidaPaciente() != null && ssp.getMotivoSaidaPaciente().getCodigoSus() != null){
				motivo = StringUtils.substring(ssp.getMotivoSaidaPaciente().getCodigoSus().toString(), 0,1) +
				  		 StringUtils.substring(ssp.getCodigoSus().toString(), 0,1); 
			} else {
				motivo = "0";
			}
			
			Short vSomaUm = Short.valueOf(motivo);
			*/
			
			logar("v_soma_um:  {0}", vSomaUm);

			vDiariasConta = DateUtil.obterQtdDiasEntreDuasDatasTruncadas(regContaIni.getDataInternacaoAdministrativa(), regContaIni.getDtAltaAdministrativa() != null ? regContaIni.getDtAltaAdministrativa() : auxDate).shortValue(); 
			vDiasConta = vDiariasConta + 1;
			
			//if (trunc(nvl(reg_conta_ini.dt_alta_administrativa,sysdate)) = trunc(reg_conta_ini.dt_int_administrativa)) or v_soma_um > 0 then
			if (DateValidator.validarMesmoDia(regContaIni.getDtAltaAdministrativa() != null ? regContaIni.getDtAltaAdministrativa()
					: auxDate, regContaIni.getDataInternacaoAdministrativa()) || vSomaUm > 0
			) {
				vDiariasConta++;
				logar("MTV: {0} v_diarias_conta={1}", vMotivoCobranca, vDiariasConta);
			}

			//Marina 24/04/2013 - Portaria 1379
		    logar("******** PORTARIA 1379 *********");
		    logar("v_diarias_conta: {0}", vDiariasConta);

Integer vClicRealizado = this.getvFatAssociacaoProcedimentoDAO().obterClinica(regContaIni.getPhiSeqRealizado());
		    
		    logar("v_clc_realiz: {0}", vClicRealizado);
		    
		    Integer vClinicaPsiquiatrica = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_CLINICA_PSIQUIATRICA);
		    
		    if (vClicRealizado != null && vClicRealizado.equals(vClinicaPsiquiatrica)) {
		    	//Busca PHI para incluir na conta do paciente		    	
		    	Integer vPhiDiaria;
		    	
				if (vDiariasConta <= 7) {
					vPhiDiaria = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_ATE_SETE);
				} else if (vDiariasConta <= 15) {
					vPhiDiaria = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_ATE_QUINZE);
				} else {
					vPhiDiaria = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_SUP_QUINZE);
				}
				
				//Garante que o item não seja duplicado na conta
				this.getFatItemContaHospitalarDAO().removerPorPHI(vPhiDiaria);
				//this.getFaturamentoFacade().flush();
					
				//Insere código correspondente na itens da conta
				Short vIchSeqp = this.getFatItemContaHospitalarDAO().obterProximoSeq(regContaIni.getSeq());
				logar("vai inserir phi da psiquiatria v_phi_diaria: {0} ", vPhiDiaria);
				
				FatItemContaHospitalar newItemContaHospitalar = new FatItemContaHospitalar();
				newItemContaHospitalar.setContaHospitalar(regContaIni);
				newItemContaHospitalar.setId(new FatItemContaHospitalarId(regContaIni.getSeq(), vIchSeqp));
				newItemContaHospitalar.setIchType(DominioItemConsultoriaSumarios.ICH);
				
				FatProcedHospInternos phi = this.getFatProcedHospInternosDAO().obterPorChavePrimaria(vPhiDiaria);
				newItemContaHospitalar.setProcedimentoHospitalarInterno(phi);
				
				newItemContaHospitalar.setValor(BigDecimal.ZERO);
				newItemContaHospitalar.setIndSituacao(DominioSituacaoItenConta.A);
				newItemContaHospitalar.setIndOrigem(DominioIndOrigemItemContaHospitalar.DIG);
				newItemContaHospitalar.setQuantidadeRealizada(vDiariasConta);
				newItemContaHospitalar.setLocalCobranca(DominioLocalCobranca.I);
				newItemContaHospitalar.setDthrRealizado(regContaIni.getDtAltaAdministrativa());
				
				try {
					this.getItemContaHospitalarON().inserirItemContaHospitalarSemValidacoesForms(newItemContaHospitalar, true, servidorLogado, dataFimVinculoServidor, pPrevia);
				} catch(BaseException e) {
					 logar("ERRO ao inserir phi psiquiatria de acordo com as diarias {0}", e.getMessage());
				}
		    }
		    
			// verifica/calcula diarias de uti
			if (contaOk || pPrevia) {
				if (regContaIni.getDtAltaAdministrativa() != null) {
					// Verifica se foi lancado diarias de UTI manualmente
					FatDiariaUtiDigitada fatDiariaUtiDigitada = fatDiariaUtiDigitadaDAO.obterPorChavePrimaria(regContaIni.getSeq());

					if (fatDiariaUtiDigitada != null) {
						vUtiDigIni = fatDiariaUtiDigitada.getDiasMesInicialNew();
						vUtiDigAnt = fatDiariaUtiDigitada.getDiasMesAnteriorNew();
						vUtiDigAlta = fatDiariaUtiDigitada.getDiasMesAltaNew();
						vUtiDigTipo = fatDiariaUtiDigitada.getTipoUtiNew();
					}

					if (fatDiariaUtiDigitada != null && regContaIni.getIndTipoUti() != null) {
						logar("tinha diarias de uti digitada");
						vTipoUti = regContaIni.getIndTipoUti();
						if (CoreUtil.igual(DominioTipoIdadeUTI.N, vUtiDigTipo)) {
							vUtiNeo = true;
						}

						vIdadeUti = vUtiDigTipo != null ? vUtiDigTipo : regContaIni.getIndIdadeUti();
						vDiasMesIniUti = vUtiDigIni != null ? vUtiDigIni : 0;
						vDiasMesAntUti = vUtiDigAnt != null ? vUtiDigAnt : 0;
						vDiasMesAltaUti = vUtiDigAlta != null ? vUtiDigAlta : 0;
						vDiasUtiTotal = vDiasMesIniUti + vDiasMesAntUti + vDiasMesAltaUti;

						regContaIni.setDiasUtiMesInicial(vDiasMesIniUti);
						regContaIni.setDiasUtiMesAnterior(vDiasMesAntUti);
						regContaIni.setDiasUtiMesAlta(vDiasMesAltaUti);
						regContaIni.setIndIdadeUti(vIdadeUti);

						faturamentoFacade.persistirContaHospitalar(regContaIni, contaOld, nomeMicrocomputador, dataFimVinculoServidor);
						try{
							contaOld = faturamentoFacade.clonarContaHospitalar(regContaIni);
						}catch (Exception e) {
							LOG.error(EXCECAO_CAPTURADA, e);
							throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
						}
					} else {
						// Verifica se paciente passou pela UTI Neo
						logar("nao tinha UTI DIGITADA ou TIPO, vai calcular UTI");

						// Calcula diarias UTI
						RnCthcVerUtimesesVO rnCthcVerUtimesesVO = fatkCth4RN.rnCthcVerUtimeses(regContaIni.getSeq());

						if (rnCthcVerUtimesesVO != null) {
							vDiasMesIniUti = rnCthcVerUtimesesVO.getDiariasUtiMesIni();
							vDiasMesAntUti = rnCthcVerUtimesesVO.getDiariasUtiMesAnt();
							vDiasMesAltaUti = rnCthcVerUtimesesVO.getDiariasUtiMesAlta();
							vTipoUti = rnCthcVerUtimesesVO.getTipoUti();
							vUtiNeo = rnCthcVerUtimesesVO.getUtiNeo();

							if (CoreUtil.igual(Boolean.TRUE, rnCthcVerUtimesesVO.getRetorno())) {
								logar("calculou diarias de uti e vai fazer update na conta");

								vDiasUtiTotal = vDiasMesIniUti + vDiasMesAntUti + vDiasMesAltaUti;

								logar("Diárias UTI: {0}", vDiasUtiTotal);

								regContaIni.setDiasUtiMesInicial(vDiasMesIniUti);
								regContaIni.setDiasUtiMesAnterior(vDiasMesAntUti);
								regContaIni.setDiasUtiMesAlta(vDiasMesAltaUti);
								regContaIni.setIndTipoUti(vTipoUti);

								faturamentoFacade.persistirContaHospitalar(regContaIni, contaOld, nomeMicrocomputador, dataFimVinculoServidor);
								try{
									contaOld = faturamentoFacade.clonarContaHospitalar(regContaIni);
								}catch (Exception e) {
									LOG.error(EXCECAO_CAPTURADA, e);
									throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
								}
							} else {
								logar("ERRO AO CALCULAR UTI");

								regContaIni.setDiasUtiMesInicial((byte) 0);
								regContaIni.setDiasUtiMesAnterior((byte) 0);
								regContaIni.setDiasUtiMesAlta((byte) 0);
								regContaIni.setIndTipoUti(null);

								faturamentoFacade.persistirContaHospitalar(regContaIni, contaOld, nomeMicrocomputador, dataFimVinculoServidor);
								try{
									contaOld = faturamentoFacade.clonarContaHospitalar(regContaIni);
								}catch (Exception e) {
									LOG.error(EXCECAO_CAPTURADA, e);
									throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
								}

								// Propria funcao insere log erro
								contaOk = Boolean.FALSE;
								logar("##### AGHU - CONTA NOT OK ##### Motivo: NAO FOI POSSIVEL IDENTIFICAR PACIENTE DA CONTA PARA CALCULAR DIARIAS DE UTI. (T)");
							}
						}
					}
				}
			}

			logar("DIARIAS UTI: {0} INI: {1} ANT: {2} ALTA: {3} TIPO: {4}", 
					vDiasUtiTotal, vDiasMesIniUti, vDiasMesAntUti, vDiasMesAltaUti, (vTipoUti!=null?vTipoUti.getCodigo():null));

			// Busca permanencia prevista e max UTI do realizado
			if (contaOk || pPrevia) {
				
				FatVlrItemProcedHospComps viphc = null;
				
				// Ney 15/07/2011 Portaria 203 Fase 2
				if(regContaIni.getDtAltaAdministrativa() != null){	// pode ser NULA na prévia
					viphc = getFatVlrItemProcedHospCompsDAO().obterPrimeiroValorItemProcHospCompPorPhoIphParaCompetencia(
																											  vPhoRealiz, 
																											  vIphRealiz,
																											  DateUtil.truncaData(regContaIni.getDtAltaAdministrativa())
																											);
				}

				if(viphc != null){
					final FatItensProcedHospitalar fatItensProcedHospitalar = viphc.getFatItensProcedHospitalar();
					
					if (fatItensProcedHospitalar != null) {
						vPermPrevista = fatItensProcedHospitalar.getQuantDiasFaturamento();
						vMaxUtiProc = fatItensProcedHospitalar.getMaxDiariaUti();
						vCobrancaDiaria = fatItensProcedHospitalar.getCobrancaDiarias();
						//vMaxQtdConta = fatItensProcedHospitalar.getMaxQtdConta();
					}
					vMaxQtdConta =  viphc.getQtdMaximaExecucao();
				}

				vPermPrevista = (short) (vPermPrevista != null ? vPermPrevista : vDiariasConta);
				vMaxUtiProc = (short) (vMaxUtiProc != null ? vMaxUtiProc : vDiasUtiTotal);
				vCobrancaDiaria = vCobrancaDiaria != null ? vCobrancaDiaria : DominioCobrancaDiaria.N;
			}

			logar("perman prevista ssm: {0} max uti ssm: {1} cobra diarias: {2}", vPermPrevista, vMaxUtiProc, vCobrancaDiaria);

			// Valida diarias de UTI:
			if (contaOk || pPrevia) {
				logar("1");
				if (regContaIni.getDtAltaAdministrativa() != null) {
					logar("2");
					logar("DIARIAS UTI: {0} INI: {1} ANT: {2} ALTA: {3} TIPO: {4}", vDiasUtiTotal, vDiasMesIniUti, vDiasMesAntUti, vDiasMesAltaUti, (vTipoUti!=null?vTipoUti.getCodigo():null));
					if (vTipoUti != null
							&& ((vDiasMesIniUti != null && vDiasMesIniUti > 0) || (vDiasMesAntUti != null && vDiasMesAntUti > 0) || (vDiasMesAltaUti != null && vDiasMesAltaUti > 0))) {
						logar("3");
						// Verifica qtde maxima cobravel de UTI em AIH
						vMaxUtiAih = this.buscarVlrByteAghParametro(AghuParametrosEnum.P_MAX_UTI_CONTA_SUS);
						
						// Verifica qtde maxima cobravel de UTI para o realizado
						logar("vai verificar max UTI p/ssm realizado");
						if (CoreUtil.maior(vDiasUtiTotal, vMaxUtiProc)) {
							logar("UTI P/ O REALIZ NAO PODE ULTRAPASSAR {0}", vMaxUtiProc);
							if (CoreUtil.maior(vMaxUtiProc, 0)) {
								FatMensagemLog fatMensagemLog = new FatMensagemLog();
								fatMensagemLog.setCodigo(233);
								faturamentoON.criarFatLogErrors("QUANTIDADE MAXIMA DE DIARIAS DE UTI EXCEDIDA PARA O REALIZADO.",
										INT, regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, vPhoSolic,
										vPhoRealiz, vIphSolic, vIphRealiz, vPacCodigo,
										regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
												.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
												.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
												.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
										vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null,
										null,fatMensagemLog);

								contaOk = Boolean.FALSE;
								logar("##### AGHU - CONTA NOT OK ##### Motivo: QUANTIDADE MAXIMA DE DIARIAS DE UTI EXCEDIDA PARA O REALIZADO. (U)");
							} else {
								logar("vai zerar diarias de UTI em funcao do max UTI do ssm");

								regContaIni.setDiasUtiMesInicial((byte) 0);
								regContaIni.setDiasUtiMesAnterior((byte) 0);
								regContaIni.setDiasUtiMesAlta((byte) 0);
								regContaIni.setIndTipoUti(null);

								faturamentoFacade.persistirContaHospitalar(regContaIni, contaOld, nomeMicrocomputador, dataFimVinculoServidor);
								try{
									contaOld = faturamentoFacade.clonarContaHospitalar(regContaIni);
								}catch (Exception e) {
									LOG.error(EXCECAO_CAPTURADA, e);
									throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
								}

								vTipoUti = null;
								vDiasMesIniUti = 0;
								vDiasMesAntUti = 0;
								vDiasMesAltaUti = 0;
								FatMensagemLog fatMensagemLog = new FatMensagemLog();
								fatMensagemLog.setCodigo(240);
								faturamentoON.criarFatLogErrors("REALIZADO NAO PERMITE COBRANCA DE UTI: " + vDiasUtiTotal
										+ " DIARIAS TIPO " + vTipoUti, INT, regContaIni.getSeq(), null, null, null, null,
										dataPrevia, null, null, vPhoSolic, vPhoRealiz, vIphSolic, vIphRealiz, vPacCodigo, regContaIni
												.getProcedimentoHospitalarInterno() != null ? regContaIni
												.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
												.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
												.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
										vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null,
										null,fatMensagemLog);
							}
						}

						if (CoreUtil.maior(vDiasUtiTotal, vMaxUtiAih)) {
							logar("UTI NAO PODE ULTRAPASSAR {0}", vMaxUtiAih);
							if (CoreUtil.maior(vMaxUtiAih, 0)) {
								FatMensagemLog fatMensagemLog = new FatMensagemLog();
								fatMensagemLog.setCodigo(232);
								faturamentoON.criarFatLogErrors("QUANTIDADE MAXIMA DE DIARIAS DE UTI EM AIH EXCEDIDA.", INT,
										regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz,
										vIphSolic, vIphRealiz, vPacCodigo,
										regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
												.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
												.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
												.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
										vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null,
										null,fatMensagemLog);

								contaOk = Boolean.FALSE;
								logar("##### AGHU - CONTA NOT OK ##### Motivo: UTI NAO PODE ULTRAPASSAR " + vMaxUtiAih + " (V)");
							} else {
								logar("vai zerar diarias de UTI em funcao do max UTI AIH");

								regContaIni.setDiasUtiMesInicial((byte) 0);
								regContaIni.setDiasUtiMesAnterior((byte) 0);
								regContaIni.setDiasUtiMesAlta((byte) 0);
								regContaIni.setIndTipoUti(null);

								faturamentoFacade.persistirContaHospitalar(regContaIni, contaOld, nomeMicrocomputador, dataFimVinculoServidor);
								try{
									contaOld = faturamentoFacade.clonarContaHospitalar(regContaIni);
								}catch (Exception e) {
									LOG.error(EXCECAO_CAPTURADA, e);
									throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
								}

								vTipoUti = null;
								vDiasMesIniUti = 0;
								vDiasMesAntUti = 0;
								vDiasMesAltaUti = 0;
							}
						}
					}
				}
			}

			// Faz lancamento das diarias de UTI
			if (contaOk || pPrevia) {
				logar("4");
				if (regContaIni.getDtAltaAdministrativa() != null) {
					logar("5");
					logar("DIARIAS UTI: {0} INI: {1} ANT: {2} ALTA: {3} TIPO: {4}", vDiasUtiTotal, vDiasMesIniUti, vDiasMesAntUti, vDiasMesAltaUti, (vTipoUti!=null?vTipoUti.getCodigo():null));

					if (vTipoUti != null
							&& ((vDiasMesIniUti != null && vDiasMesIniUti > 0) || (vDiasMesAntUti != null && vDiasMesAntUti > 0) || (vDiasMesAltaUti != null && vDiasMesAltaUti > 0))) {
						
						logar("vai lancar uti... cth {0} phi realiz {1} uti neo {2}",
								regContaIni.getSeq(), 
								(regContaIni.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni.getProcedimentoHospitalarInternoRealizado().getSeq() : ""),
								vUtiNeo);
						
						if (!fatkCth4RN.rnCthcAtuLancauti(dataPrevia, pPrevia, regContaIni.getSeq(), vPacCodigo,
								vPacProntuario, regContaIni.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
										.getProcedimentoHospitalarInternoRealizado().getSeq() : null, vPhoRealiz, vIphRealiz,
								vCodSusRealiz, regPac != null ? regPac.getDtNascimento() : null, regContaIni.getDtAltaAdministrativa(), vDiasMesIniUti,
								vDiasMesAntUti, vDiasMesAltaUti, vTipoUti, vUtiNeo, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO)) {
							logar("6");
							// Gerou log de erro na propria funcao
							contaOk = Boolean.FALSE;
							logar("##### AGHU - CONTA NOT OK ##### Motivo: !fatkCth4RN.rnCthcAtuLancauti (W)");
						}
					}
				}
			}

			// Verifica se houve lancamento de diarias de acompanhante
			if (contaOk || pPrevia) {
				logar("antes acomp ");

				vDiariasAcomp = regContaIni.getDiariasAcompanhante() != null ? regContaIni.getDiariasAcompanhante() : 0;

				logar("DT INT: {0} DT ALTA: {1}", regContaIni.getDataInternacaoAdministrativa(), regContaIni.getDtAltaAdministrativa());
				
				logar("DIARIAS ACOMP: {0} DIAS: {1} ACOMP: {2}",
						vDiariasConta, vDiasConta, vDiariasAcomp);	

				if (CoreUtil.maior(vDiariasAcomp, 0)) {

					// Verifica se o realizado permite lancamento diarias de acompanhante
					// ORADB FATK_IPH_RN.RN_IPHC_VER_DIAACOMP.

					FatItensProcedHospitalar aux = fatItensProcedHospitalarDAO.obterPorChavePrimaria( new FatItensProcedHospitalarId(vPhoRealiz,vIphRealiz) );
					Boolean diariaAcompanhante = (aux != null) ? aux.getDiariaAcompanhante() : Boolean.TRUE;
					
					if (Boolean.TRUE.equals(diariaAcompanhante)) {
						// Verifica qtd acompanhante c/ qtd diarias conta
						if (vDiasUtiTotal != null && CoreUtil.igual(vDiasUtiTotal, 0) && vDiariasAcomp > vDiariasConta) {
							
							FatMensagemLog fatMensagemLog = new FatMensagemLog();
							fatMensagemLog.setCodigo(55);
							faturamentoON.criarFatLogErrors("DIARIAS DE ACOMPANHANTE NAO PODE ULTRAPASSAR DIARIAS DA CONTA.", INT,
									regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz,
									vIphSolic, vIphRealiz, vPacCodigo,
									regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
											.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
											.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
											.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
									vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null,
									null,fatMensagemLog);

							if (!pPrevia && regContaIni.getIndAutorizadoSms() != null && !CoreUtil.igual("N", regContaIni.getIndAutorizadoSms())) {
								contaOk = Boolean.FALSE;
								logar("##### AGHU - CONTA NOT OK ##### Motivo: DIARIAS DE ACOMPANHANTE NAO PODE ULTRAPASSAR DIARIAS DA CONTA. (X)");
							}
						} else if (CoreUtil.maior(vDiariasAcomp, (vDiariasConta - vDiasUtiTotal))) {
							
							FatMensagemLog fatMensagemLog = new FatMensagemLog();
							fatMensagemLog.setCodigo(56);
							faturamentoON.criarFatLogErrors(
									"DIARIAS DE ACOMPANHANTE NAO PODE ULTRAPASSAR DIARIAS DA CONTA MENOS DIARIAS DE UTI.", INT,
									regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz,
									vIphSolic, vIphRealiz, vPacCodigo,
									regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
											.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
											.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
											.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
									vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null,
									null,fatMensagemLog);

							if (!pPrevia && regContaIni.getIndAutorizadoSms() != null && !CoreUtil.igual("N", regContaIni.getIndAutorizadoSms())) {
								contaOk = Boolean.FALSE;
								logar("##### AGHU - CONTA NOT OK ##### Motivo: DIARIAS DE ACOMPANHANTE NAO PODE ULTRAPASSAR DIARIAS DA CONTA MENOS DIARIAS DE UTI. (Y)");
							}
						}
					} else {
						FatMensagemLog fatMensagemLog = new FatMensagemLog();
						fatMensagemLog.setCodigo(220);
						faturamentoON.criarFatLogErrors("PROCEDIMENTO REALIZADO NAO PERMITE COBRANCA DE DIARIA DE ACOMPANHANTE.",
								INT, regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz,
								vIphSolic, vIphRealiz, vPacCodigo,
								regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
										.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
										.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
										.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
								vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

						contaOk = Boolean.FALSE;
						logar("##### AGHU - CONTA NOT OK ##### Motivo: PROCEDIMENTO REALIZADO NAO PERMITE COBRANCA DE DIARIA DE ACOMPANHANTE. (Z)");
					}
				}

				logar("v_diarias_acomp: {0}", vDiariasAcomp);
				if (CoreUtil.maior(vDiariasAcomp, 0)) {
					if (!fatkCth5RN.rnCthcAtuLancacomp(dataPrevia, pPrevia, regContaIni.getSeq(), vPacCodigo, vPacProntuario,
							regContaIni.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
									.getProcedimentoHospitalarInternoRealizado().getSeq() : null, vPhoRealiz, vIphRealiz,
							vCodSusRealiz, regPac != null ? regPac.getDtNascimento() : null, regContaIni.getDtAltaAdministrativa(), vDiariasAcomp)) {
						// Gerou log de erro na propria funcao
						contaOk = Boolean.FALSE;
						logar("##### AGHU - CONTA NOT OK ##### Motivo: !fatkCth5RN.rnCthcAtuLancacomp (AA)");
					}

					vDiariasAcomp = 0;// zera para não ter valor na espelho_aih
				}
			}

			if (contaOk || pPrevia) {
				logar("v_pac_prontuario: {0}", vPacProntuario);
				logar("v_pac_codigo: {0}", vPacCodigo);
				logar("v_int_seq: {0}", vIntSeq);
				logar("v_iph_solic: {0}", vIphSolic);
				logar("v_cod_sus_solic: {0}", vCodSusSolic);
				logar("v_pho_realiz: {0}", vPhoRealiz);
				logar("v_iph_realiz: {0}", vIphRealiz);
				logar("v_cod_sus_realiz: {0}", vCodSusRealiz);
				logar("reg_pac.dt_nascimento: {0}", regPac != null ? regPac.getDtNascimento() : null);
				
				if (vPacProntuario != null && vPacCodigo != null && vPhoSolic != null && vIphSolic != null && vCodSusSolic != null
						&& vPhoRealiz != null && vIphRealiz != null && vCodSusRealiz != null && regPac != null && regPac.getDtNascimento() != null) {
					// Aplicar REGRAS de CONTA hospitalar
					logar("aplicar ");

					RnCthcAtuRegrasVO rnCthcAtuRegrasVO = fatkCthRN.rnCthcAtuRegras(pPrevia, dataPrevia, regContaIni.getSeq(),
							vPacCodigo, vPacProntuario, vIntSeq, regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
									.getProcedimentoHospitalarInterno().getSeq() : null, vPhoSolic, vIphSolic, vCodSusSolic,
							regContaIni.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
									.getProcedimentoHospitalarInternoRealizado().getSeq() : null, vPhoRealiz, vIphRealiz,
							vCodSusRealiz, vClinicaRealiz, vDiasConta, regPac != null ? regPac.getDtNascimento() : null, 
									nomeMicrocomputador, dataFimVinculoServidor);

					if (rnCthcAtuRegrasVO != null) {
						vCodExclusaoCritica = rnCthcAtuRegrasVO.getCodExclusaoCritica();
						vClinicaRealiz = rnCthcAtuRegrasVO.getClcRealiz();

						if (!rnCthcAtuRegrasVO.getRetorno()) {
							logar("conta não ok");
							contaOk = Boolean.FALSE;
							logar("##### AGHU - CONTA NOT OK ##### Motivo: !fatkCthRN.rnCthcAtuRegras (AB)");
						}
					}
				}
			}

			logar("CLINICA: {0}", vClinicaRealiz);

			if (vClinicaRealiz == null) {
				if (vPhoRealiz != null && vIphRealiz != null) {

					FatItensProcedHospitalar aux = fatItensProcedHospitalarDAO.obterPorChavePrimaria( new FatItensProcedHospitalarId(vPhoRealiz,vIphRealiz) );
					vClinicaRealiz = (aux != null && aux.getClinica() != null) ? aux.getClinica().getCodigo() : null;
					
					if(vClinicaRealiz != null){
						logar("CLINICA DO IPH APOS NULO: {0}", vClinicaRealiz);
					}
				}
			}

			vClinicaRealizHdiaCir = null;

			if (vClinicaRealiz != null) {
				Integer auxCodigoClinica = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_CLINICA_CIRURGICA);
				
				if (CoreUtil.igual(auxCodigoClinica, vClinicaRealiz)) {
					Short auxBuscaModalidade = faturamentoRN.buscaModalidade(vPhoRealiz, vIphRealiz, regContaIni.getDataInternacaoAdministrativa(),
							regContaIni.getDtAltaAdministrativa());
					if (auxBuscaModalidade != null && CoreUtil.igual(auxBuscaModalidade, 3)) {
						
						auxCodigoClinica = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_CLINICA_HDIA_CIR);						

						if (auxCodigoClinica != null) {
							vClinicaRealizHdiaCir = auxCodigoClinica;
						}

						logar("CLINICA DO IPH hosp dia: {0}", vClinicaRealiz);
					}
				}
			}

			// Busca tipo de aih
			FatTipoAih fatTipoAih = regContaIni.getTipoAih();

			if (fatTipoAih != null && CoreUtil.igual(DominioSituacao.A, fatTipoAih.getSituacaoRegistro())) {
				vTahCodSus = fatTipoAih.getCodigoSus();
			} else {
				logar("TIPO DE AIH NAO ENCONTRADO");
				FatMensagemLog fatMensagemLog = new FatMensagemLog();
				fatMensagemLog.setCodigo(173);
				faturamentoON
						.criarFatLogErrors("NAO FOI POSSIVEL IDENTIFICAR O TIPO DE AIH DA CONTA.", INT, regContaIni.getSeq(), null,
								null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz, vIphSolic, vIphRealiz, vPacCodigo,
								regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
										.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
										.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
										.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
								vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

				contaOk = Boolean.FALSE;
				logar("##### AGHU - CONTA NOT OK ##### Motivo: NAO FOI POSSIVEL IDENTIFICAR O TIPO DE AIH DA CONTA. (AC)");
			}

			logar("TAH: {0}", vTahCodSus);

			// Busca CPF do medico solicitante/responsavel
			AinInternacao auxInternacao = null;
			if(vIntSeq!=null){
				auxInternacao = internacaoFacade.obterInternacao(vIntSeq);
			}

			if (auxInternacao != null) {
				RapServidores auxServidor = auxInternacao.getServidorProfessor();

				vCpfMedicoSolicRespons = auxServidor.getPessoaFisica().getCpf();
				vMatriculaRespInt = auxServidor.getId().getMatricula();
				vVinculoRespInt = auxServidor.getId().getVinCodigo();
				
				if (auxServidor.getPessoaFisica().getCpf() == null) {
					logar("##### AGHU - MEDICO SEM CPF, MATRICULA = {0} VINCULO = {1}", auxServidor.getId().getMatricula(), auxServidor.getId().getVinCodigo());
					logar("##### AGHU - MEDICO SEM CPF, MATRICULA = "+auxServidor.getId().getMatricula()+ " VINCULO = "+auxServidor.getId().getVinCodigo());
				}				
			}

			if (auxInternacao == null || vCpfMedicoSolicRespons == null || CoreUtil.igual(vCpfMedicoSolicRespons, 0)) {
				logar("CPF DO MEDICO DA INTERNACAO NAO ENCONTRADO");

				FatContasInternacao contaInternacao = fatContasInternacaoDAO.obterPrimeiraContaInternacao(regContaIni.getSeq());
				
				if (contaInternacao != null && contaInternacao.getDadosContaSemInt() != null) {
					RapServidores auxServidor = contaInternacao.getDadosContaSemInt().getServidor();
					if (auxServidor != null) {
						if (auxServidor.getPessoaFisica().getCpf() == null) {
							logar("##### AGHU - MEDICO SEM CPF, MATRICULA = {0} VINCULO = {1}", auxServidor.getId().getMatricula(), auxServidor.getId().getVinCodigo());
							logar("##### AGHU - MEDICO SEM CPF, MATRICULA = "+auxServidor.getId().getMatricula()+ " VINCULO = "+auxServidor.getId().getVinCodigo());
						}
						vCpfMedicoSolicRespons2 = auxServidor.getPessoaFisica().getCpf();
						vMatriculaRespInt = auxServidor.getId().getMatricula();
						vVinculoRespInt = auxServidor.getId().getVinCodigo();
					}
				}

				if (contaInternacao == null || contaInternacao.getDadosContaSemInt() == null || vCpfMedicoSolicRespons2 == null
						|| CoreUtil.igual(vCpfMedicoSolicRespons2, 0)) {
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(167);
					faturamentoON.criarFatLogErrors("NAO FOI POSSIVEL IDENTIFICAR O CPF DO MEDICO DA INTERNACAO.", INT, regContaIni
							.getSeq(), null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz, vIphSolic, vIphRealiz,
							vPacCodigo, regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
									.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
									.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
									.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario, vCodSusRealiz,
							vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

					contaOk = Boolean.FALSE;
					logar("##### AGHU - CONTA NOT OK ##### Motivo: NAO FOI POSSIVEL IDENTIFICAR O CPF DO MEDICO DA INTERNACAO. (AD)");
				}

				vCpfMedicoSolicRespons = vCpfMedicoSolicRespons2;
			}

			logar("MED SOL: {0}", vCpfMedicoSolicRespons);

			logar("conta ok");

			// Busca carater internacao default
			vTciCodSus = this.buscarVlrByteAghParametro(AghuParametrosEnum.P_CARATER_INTERN_PADRAO_SUS);
			
			// Busca carater da int cfe Ruth 071103
			logar("v_int_seq {0}", vIntSeq);

			if(vIntSeq!=null){
				auxInternacao = internacaoFacade.obterInternacao(vIntSeq);
			}			
			if (auxInternacao != null) {
				vTciInt = auxInternacao.getTipoCaracterInternacao().getCodSus().byteValue();

				logar("v_tci_int {0}", vTciInt);
				vTciCodSus = vTciInt;
			} else {
				final Integer codSus = fatContasInternacaoDAO.buscarContaInternacaoCursorTci2(regContaIni.getSeq());
				if (codSus != null) {
					vTciDcs = codSus.byteValue();
					logar("v_tci_dcs {0}", vTciDcs);
					vTciCodSus = vTciDcs;
				}
			}

			logar("CARATER INTERNACAO POS INT {0}", vTciCodSus);

			// Verifica se o realizado possui excecao de carater internacao
			List<FatExcCaraterInternacao> listaExtCaraterInternacoes = fatExcCaraterInternacaoDAO.listarExtCaraterInternacoes(
					vPhoRealiz, vIphRealiz, (regPacEnder!=null?regPacEnder.getUf():null));

			if (listaExtCaraterInternacoes != null && !listaExtCaraterInternacoes.isEmpty()) {
				vTciEci = vTciCodSus = vTciEci;
			} else {
				// Busca CID inicial da faixa de CIDs p/os quais o carater de
				// internacao da conta deve ser buscado na internacao
				vCidTciIni = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_CID_INI_CARATER_INTERN);
				
				// Busca CID final da faixa de CIDs p/os quais o carater de
				// internacao da conta deve ser buscado na internacao
				vCidTciFin = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_CID_FIN_CARATER_INTERN);
				
				if (vCidIni != null && vCidTciIni != null && vCidTciFin != null && vCidIni.compareToIgnoreCase(vCidTciIni) >= 0
						&& vCidIni.compareToIgnoreCase(vCidTciFin) <= 0) {
					
					// Busca carater internacao (da internacao)
					Integer codSus = null;
					if(vIntSeq!=null){
						codSus = internacaoFacade.obterCodigoCaraterInternacaoPorAinInternacao(vIntSeq);
					}
					
					if (codSus != null) {
						vTciInt = codSus.byteValue();
						vTciCodSus = vTciInt;

					} else {
						codSus = fatContasInternacaoDAO.buscarContaInternacaoCursorTci2(regContaIni.getSeq());
						if (codSus != null) {
							vTciDcs = codSus.byteValue();
							vTciCodSus = vTciDcs;
						} else {
							logar("CARATER INTERNACAO NAO ENCONTRADO");
							FatMensagemLog fatMensagemLog = new FatMensagemLog();
							fatMensagemLog.setCodigo(165);
							faturamentoON.criarFatLogErrors("NAO FOI POSSIVEL IDENTIFICAR O CARATER DE INTERNACAO.", INT,
									regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz,
									vIphSolic, vIphRealiz, vPacCodigo,
									regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
											.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
											.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
											.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
									vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null,
									null,fatMensagemLog);

							contaOk = Boolean.FALSE;
							logar("##### AGHU - CONTA NOT OK ##### Motivo: NAO FOI POSSIVEL IDENTIFICAR O CARATER DE INTERNACAO. (AD)");
						}
					}
				}
			}

			if (vHospDiaCirg) {
				if (vTciInt != null && CoreUtil.igual(vTciInt, 5)) {
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(8);
					faturamentoON.criarFatLogErrors("CARATER INTERNAÇÃO INVALIDO:ORIGEM É URGENCIA E É HOSPITAL DIA.", INT,
							regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz, vIphSolic,
							vIphRealiz, vPacCodigo, regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
									.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
									.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
									.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario, vCodSusRealiz,
							vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

					contaOk = Boolean.FALSE;
					logar("##### AGHU - CONTA NOT OK ##### Motivo: CARATER INTERNAÇÃO INVALIDO:ORIGEM É URGENCIA E É HOSPITAL DIA. (AE)");
				}
			}

			logar("TCI: {0}", vTciCodSus);
			
			
			// Marina 20/01/2012
			// Verifica se o paciente tem CNS informado
			logar("reg_pac.cns: {0}", regPac.getNroCartaoSaude());
			
			if(regPac.getNroCartaoSaude() == null){
				FatMensagemLog fatMensagemLog = new FatMensagemLog();
				fatMensagemLog.setCodigo(121);
				faturamentoON.criarFatLogErrors("NAO ENCONTROU CNS PACIENTE.", INT,
						regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz,
						vIphSolic, vIphRealiz, vPacCodigo,
						regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
								.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
								.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
								.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
						vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null,
						null,fatMensagemLog);
				
				// conta_ok := false;-- Marina 21/03/2012 - A pedido da Ruth, voltei a regra antiga.
				// Solicitação da Ruth em 16/03/2012 - para todas as contas deverá ter validado se o paciente tem cns
		        // SE FOR DIFERENTE DE ELETIVA, APENAS COLOCA NA LOG, NÃO IMPEDE O ENCERRAMENTO
				
				logar(" ******************************** v_tci_cod_sus: {0}", vTciCodSus); 
				
				// Marina - chamado 74989
				if(!CoreUtil.igual(vTciCodSus, Byte.valueOf("1"))){
					final Integer exclusaoCritica = fatkCthRN.rnFatpExclusaoCritica( DominioSituacao.I, DominioSituacao.I,
																					 DominioSituacao.I, DominioSituacao.I, 
																					 DominioSituacao.I, pCthSeq, DominioSituacao.A,
																					 nomeMicrocomputador,
																					 dataFimVinculoServidor);
					
					vExclusaoCritica = (exclusaoCritica != null) ? exclusaoCritica.toString() : null;
				} else {
					contaOk = Boolean.FALSE;
				}
				
			} // fim Marina 20/01/2012
			
			logar("MED AUD: {0} DT EMISSAO AIH: {1}", vCpfMedicoAuditor, vAihDthrEmissao);

			// Busca nro AIH anterior (no caso de conta originada de desdobram)
			if (regContaIni.getContaHospitalar() != null) {
				FatContasHospitalares contaHospitalarAnt = regContaIni.getContaHospitalar();
				if (contaHospitalarAnt != null) {
					vNroAihAnterior = contaHospitalarAnt.getAih() != null ? contaHospitalarAnt.getAih().getNroAih() : null;
					vIndSituacao = contaHospitalarAnt.getIndSituacao();
					vDtIntAdministrativa = contaHospitalarAnt.getDataInternacaoAdministrativa();
					vPhiRzdoMae = contaHospitalarAnt.getProcedimentoHospitalarInternoRealizado();
				}

				logar("Conta Mãe {0} V_DT_INT_ADMIN: {1}", (contaHospitalarAnt != null ? contaHospitalarAnt.getSeq() : null), vDtIntAdministrativa);

				if ((contaHospitalarAnt == null || vNroAihAnterior == null) && !CoreUtil.igual(DominioSituacaoConta.C, vIndSituacao)) {
					logar("NUMERO DA AIH ANTERIOR NAO ENCONTRADO");
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(170);
					faturamentoON.criarFatLogErrors("NAO FOI POSSIVEL IDENTIFICAR O NUMERO DA AIH ANTERIOR DA CONTA.", INT,
							regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz, vIphSolic,
							vIphRealiz, vPacCodigo, regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
									.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
									.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
									.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario, vCodSusRealiz,
							vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);
					
					// Temporariamente permite encerrar conta nesta situacao, a pedido de Ruth - Ney, em 23032011
					// if (!pPrevia && regContaIni.getIndAutorizadoSms() != null && !CoreUtil.igual("N", regContaIni.getIndAutorizadoSms())) {
					//	contaOk = Boolean.FALSE;
					//	logar("##### AGHU - CONTA NOT OK ##### Motivo: NAO FOI POSSIVEL IDENTIFICAR O NUMERO DA AIH ANTERIOR DA CONTA. (AF)");
					// }
				}
			}

			logar("AIH ANT: {0}", vNroAihAnterior);

			if (contaOk || pPrevia) {
				// Busca motivo saida que exige que a conta seja desdobrada
				vMspDesdobr = this.buscarVlrByteAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_DESDOBR);
				
				if (vMotivoCobranca.length() > 0 && CoreUtil.igual(Byte.valueOf(vMotivoCobranca.substring(0, 1)), vMspDesdobr)) {
					// Busca nro AIH posterior (no caso de conta desdobrada)
					FatAih aihPosterior = regContaIni.getContaHospitalar() != null
							&& regContaIni.getContaHospitalar().getAih() != null ? regContaIni.getContaHospitalar().getAih() : null;

					if (aihPosterior != null) {
						vNroAihPosterior = aihPosterior.getNroAih();

						// Conta foi desdobrada mas a conta filha nao tem nro de AIH
						logar("NUMERO DA AIH POSTERIOR NAO ENCONTRADO");
						FatMensagemLog fatMensagemLog = new FatMensagemLog();
						fatMensagemLog.setCodigo(171);
						faturamentoON.criarFatLogErrors("NAO FOI POSSIVEL IDENTIFICAR O NUMERO DA AIH POSTERIOR DA CONTA.", INT,
								regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz,
								vIphSolic, vIphRealiz, vPacCodigo,
								regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
										.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
										.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
										.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
								vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

						if (!pPrevia && regContaIni.getIndAutorizadoSms() != null && !CoreUtil.igual("N", regContaIni.getIndAutorizadoSms())) {
							contaOk = Boolean.FALSE;
							logar("##### AGHU - CONTA NOT OK ##### Motivo: NAO FOI POSSIVEL IDENTIFICAR O NUMERO DA AIH POSTERIOR DA CONTA. (AG)");
						}
					} else {
						// Motivo saida exige desdobramento mas conta nao foi desdobrada
						logar("CONTA POSTERIOR NAO ENCONTRADA");
						FatMensagemLog fatMensagemLog = new FatMensagemLog();
						fatMensagemLog.setCodigo(161);
						faturamentoON.criarFatLogErrors("NAO FOI POSSIVEL IDENTIFICAR CONTA POSTERIOR.", INT, regContaIni.getSeq(),
								null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz, vIphSolic, vIphRealiz,
								vPacCodigo, regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
										.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
										.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
										.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
								vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

						// Temporariamente permite encerrar conta nesta situacao, a pedido de Ruth - Ney, em 23032011
						//if (!pPrevia) {
						//	contaOk = Boolean.FALSE;
						//	logar("##### AGHU - CONTA NOT OK ##### Motivo: NAO FOI POSSIVEL IDENTIFICAR CONTA POSTERIOR. (AH)");
						//}
					}
				}
			}

			logar("AIH POS: {0}", vNroAihPosterior);

			// INICIO - Marina 30/09/2010
			// Portaria nº 384, de 12 de agosto de 2010
			// Busca origem da Conta
			logar("PORTARIA 384");

			// Verifica se a conta é de parto
			AghAtendimentos auxAtendimento = aghuFacade.buscarAtendimentoContaParto(regContaIni.getSeq());
			
			final Short vlr_6 = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_MOTIVO_SAIDA_PACIENTE_ALTA_PARTURIENTE);
			final Short vlr_2 = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_SITUACAO_SAIDA_PACIENTE_DIVERSOS);
			final Short vlr_7 = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_AGHU_SIT_SAIDA_PAC_OBITO_MAE_PERMANENCIA_RN);
			final Integer vlr_62 = Integer.valueOf(vlr_6.toString() + vlr_2.toString());
			final Integer vlr_67 = Integer.valueOf(vlr_6.toString() + vlr_7.toString());
			
			Object[] vals = {vlr_62, vlr_67};
			if (auxAtendimento != null 
				// Ney 16032011
				&& !StringUtils.isEmpty(vMotivoCobranca.toString()) && CoreUtil.in(Integer.valueOf(vMotivoCobranca.toString()), vals)
				) {
				vAtdSeq = auxAtendimento.getSeq();
				vAtdSeqMae = auxAtendimento.getAtendimentoMae() != null ? auxAtendimento.getAtendimentoMae().getSeq() : null;
				vNroAih = regContaIni.getAih() != null ? regContaIni.getAih().getNroAih() : null;

				// Verifica se o Bebê está internado
				Long countBebeInternado = aghuFacade.verificaBebeInternadoCount(vAtdSeq);

				if (countBebeInternado != null && countBebeInternado > 0) {
					logar("verifica se a conta é do RN ou da mãe ");

					if (vAtdSeqMae == null) {
						logar("conta da mae: {0}", vAtdSeq);

						Long[] codigosTabela = this.buscarVlrLongArrayAghParametro(AghuParametrosEnum.P_PROCEDIMENTO_PARTO);
						
						// Verifica se o SSM é de Parto
						Integer ssmPartoCount = aghuFacade.verificaSSMPartoCount(regContaIni.getSeq(), codigosTabela);

						if (ssmPartoCount != null && ssmPartoCount > 0) {
							logar("é ssm de parto ");

							// Busca conta do Bebê para colocar o número da Aih
							// da mãe
							FatContasHospitalares contaHospitalar = fatContasHospitalaresDAO.buscaContaBebe(vAtdSeq);
							if (contaHospitalar != null) {
								vConta = contaHospitalar.getSeq();
								vNroAihAut = contaHospitalar.getAih() != null ? contaHospitalar.getAih().getNroAih() : null;
							}

							logar("vConta rn: {0}", vConta);
							logar("vNroAihAut rn: {0}", vNroAihAut);

							if (vNroAihAut == null) {
								
								FatMensagemLog fatMensagemLog = new FatMensagemLog();
								fatMensagemLog.setCodigo(5);
								faturamentoON.criarFatLogErrors("ALTA DA MÃE/PUÉRPERA E PERMANÊNCIA DO RN", INT, regContaIni
										.getSeq(), null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz, vIphSolic,
										vIphRealiz, vPacCodigo, regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
												.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
												.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
												.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
										null, null, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

								if (!pPrevia) {
									logar("vNroAih: {0}", vNroAih);
									logar("vNroAihAut: {0}", vNroAihAut);
									if (vNroAih == null && vNroAihAut == null) {
										contaOk = Boolean.TRUE;
									} else {
										logar("##### AGHU - CONTA NOT OK ##### Motivo: ALTA DA MÃE/PUÉRPERA E PERMANÊNCIA DO RN (e vNroAih != null) (AI)");
										contaOk = Boolean.FALSE;
									}
								}
							} else {
								vNroAihPosterior = vNroAihAut;

								// Atualiza a conta do RN com o número da AIH da
								// mãe
								if (vNroAih != null && vConta != null) {
									try {
										List<FatEspelhoAih> listaEspelhosAih = fatEspelhoAihDAO.listarPorCthSeqpDataPreviaNotNull(vConta,
												1);

										for (FatEspelhoAih fatEspelhoAih : listaEspelhosAih) {
											fatEspelhoAih.setNumeroAihAnterior(vNroAih);

											faturamentoFacade.atualizarFatEspelhoAih(fatEspelhoAih);
											//faturamentoFacade.evict(fatEspelhoAih);
										}
									} catch (Exception e) {
										logar("A seguinte exceção deve ser ignorada, seguindo a lógica migrada do PL/SQL: ", e);
									}
								}
							}
						}
					} else {
						logar("conta do RN");

						// Busca conta da mãe para colocar o número da Aih do RN
						FatContasHospitalares fatContaHospitalar = fatContasHospitalaresDAO.buscaContaMae(vAtdSeqMae);
						if (fatContaHospitalar != null) {
							vConta = fatContaHospitalar.getSeq();
							vNroAihAut = fatContaHospitalar.getAih() != null ? fatContaHospitalar.getAih().getNroAih() : null;
						}

						logar("v_conta mae: {0}", vConta);
						logar("v_nro_aih_aut_mae: {0}", vNroAihAut);

						if (vNroAihAut == null) {
							
							FatMensagemLog fatMensagemLog = new FatMensagemLog();
							fatMensagemLog.setCodigo(4);
							faturamentoON.criarFatLogErrors("ALTA DA MÃE/PUÉRPERA E DO RN", INT, regContaIni.getSeq(), null, null,
									null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz, vIphSolic, vIphRealiz, vPacCodigo,
									regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
											.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
											.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
											.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
									vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null,
									null,fatMensagemLog);

							if (!pPrevia) {
								logar("v_nro_aih: {0}", vNroAih);
								logar("v_nro_aih_aut: {0}", vNroAihAut);
								if (vNroAih == null) {
									contaOk = Boolean.TRUE;
								} else {
									logar("##### AGHU - CONTA NOT OK ##### Motivo: ALTA DA MÃE/PUÉRPERA E DO RN (e vNroAih != null) (AI)");
									contaOk = Boolean.FALSE;
								}
							}
						} else {
							vNroAihAnterior = vNroAihAut;

							// Atualiza a conta da mãe com o número da AIH do RN
							if (vConta != null && vNroAih != null) {
								List<FatEspelhoAih> listaEspelhosAih = fatEspelhoAihDAO.listarPorCthSeqpDataPreviaNotNull(vConta, 1);

								for (FatEspelhoAih fatEspelhoAih : listaEspelhosAih) {
									fatEspelhoAih.setNumeroAihPosterior(vNroAih);

									faturamentoFacade.atualizarFatEspelhoAih(fatEspelhoAih);
									//faturamentoFacade.evict(fatEspelhoAih);
								}
							}
						}
					}
				}
			}

			if (contaOk || pPrevia) {
				// Infeccao hospitalar
				if (Boolean.TRUE.equals(regContaIni.getIndInfeccao())) {
					vIndInfeccao = "1";
				} else {
					vIndInfeccao = "0";
				}

				auxAtendimento = aghuFacade.buscarAtendimentosPorCodigoInternacao(vIntSeq);
				if (auxAtendimento != null) {

					// Busca enfermaria e leito
					if (auxAtendimento.getLeito() != null) {
						AinLeitos leito = auxAtendimento.getLeito();
						
						//////////////////////////////////////////////////////////////////////////////////////////////
						//				ATENÇÂO: Ficou definido no ref. de quartos e leitos:						//
						//-----------------------------------------------------------------------------------------	//
						// vEnfermaria = campo DESCRICAO da AIN_QUARTOS, se este não possuir até 4 caracteres.		//
						//                Senão, campo NUMERO.														//
						//																							//
						// vLeito = campo LEITO da AIN_LEITOS.														//
						//////////////////////////////////////////////////////////////////////////////////////////////
						
						if(leito.getQuarto().getDescricao().length() <= 4){
							vEnfermaria =  StringUtils.leftPad(leito.getQuarto().getDescricao(), 4, "0");
						} else {
							vEnfermaria =  StringUtils.leftPad(String.valueOf(leito.getQuarto().getNumero()), 4, "0");
						}
						vLeito = leito.getLeito();
						
					} else if (auxAtendimento.getUnidadeFuncional() != null) {
						vEnfermaria = StringUtils.leftPad(auxAtendimento.getUnidadeFuncional().getAndar().toString(), 4, "0");
						vLeito = "0099";
					}
				}
				
				if (vEnfermaria == null || CoreUtil.igual(vEnfermaria, "0000")) {
					vEnfermaria = "0001";
				}
				if (vLeito == null || CoreUtil.igual(vLeito, "0000")) {
					vLeito = "0099";
				}
				
				logar("ENF: {0} LTO: {1} INFEC: {2}", vEnfermaria, vLeito, vIndInfeccao);
			}

			// Chama rotina que decide em qual DCIH a conta deve entrar:
			if (contaOk && !pPrevia) {
				// nao atualiza a DCIH se for previa
				vNroDcih = faturamentoFatkDciRN.rnDcicAtuDcih(regContaIni.getSeq(),
						vClinicaRealizHdiaCir != null ? vClinicaRealizHdiaCir : vClinicaRealiz, nomeMicrocomputador, dataFimVinculoServidor);

				if (vNroDcih != null) {
					// Busca dados da DCIH
					regDci = fatDocumentoCobrancaAihsDAO.obterPorChavePrimaria(vNroDcih);

					if (regDci == null) {
						logar("DADOS DO DCIH NAO ENCONTRADOS");
						FatMensagemLog fatMensagemLog = new FatMensagemLog();
						fatMensagemLog.setCodigo(45);

						faturamentoON.criarFatLogErrors("DADOS DA DCIH NAO ENCONTRADOS.", INT, regContaIni.getSeq(), null, null,
								null, null, dataPrevia, null, null, null, vPhoRealiz, null, vIphRealiz, vPacCodigo, regContaIni
										.getProcedimentoHospitalarInterno() != null ? regContaIni.getProcedimentoHospitalarInterno()
										.getSeq() : null,
								regContaIni.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
										.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
								vCodSusRealiz, null, null, null, null, null, null, null, "RN_CTHP_ATU_GERA_ESP", null, null,fatMensagemLog);

						contaOk = Boolean.FALSE;
						logar("##### AGHU - CONTA NOT OK ##### Motivo: DADOS DO DCIH NAO ENCONTRADOS (AJ)");
					}
				} else {
					logar("NAO ACHOU DCIH PRA CONTA");
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(183);
					faturamentoON.criarFatLogErrors("NAO FOI POSSIVEL INCLUIR A CONTA EM UMA DCIH.", INT, regContaIni.getSeq(),
							null, null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz, vIphSolic, vIphRealiz, vPacCodigo,
							regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni.getProcedimentoHospitalarInterno()
									.getSeq() : null, regContaIni.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
									.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario, vCodSusRealiz,
							vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

					contaOk = Boolean.FALSE;
					logar("##### AGHU - CONTA NOT OK ##### Motivo: NAO FOI POSSIVEL INCLUIR A CONTA EM UMA DCIH. (AK)");
				}
			}

			logar("DCIH: {0}", (regContaIni.getDocumentoCobrancaAih() != null ? regContaIni.getDocumentoCobrancaAih().getCodigoDcih() : null));

			// Verifica se existem SSMs lancados nos itens de conta:
			vQtdSsms = 0;

			List<FatItemContaHospitalar> listaItensContaHospitalarAtivos = fatItemContaHospitalarDAO
					.listarItensContaHospitalarAtivos(regContaIni.getSeq());

			if (listaItensContaHospitalarAtivos != null && !listaItensContaHospitalarAtivos.isEmpty()) {
				for (FatItemContaHospitalar fatItemContaHospitalar : listaItensContaHospitalarAtivos) {
					vIph = null;
					vCodSus = null;

					RnCthcVerItemSusVO rnCthcVerPtemSusVO = fatkCthRN.rnCthcVerItemSus(DominioOrigemProcedimento.I, regContaIni
							.getConvenioSaudePlano() != null ? regContaIni.getConvenioSaudePlano().getId().getCnvCodigo() : null,
							regContaIni.getConvenioSaudePlano() != null ? regContaIni.getConvenioSaudePlano().getId().getSeq() : null,
									(short) 1, fatItemContaHospitalar.getProcedimentoHospitalarInterno().getSeq(), null);
					if (rnCthcVerPtemSusVO != null) {
						vIph = rnCthcVerPtemSusVO.getIphSeq();
						
						if (rnCthcVerPtemSusVO.getRetorno()) {
							vQtdSsms++;
							logar("eh SSM, qtd SSMs {0}", vQtdSsms);
							logar("eh SSM, qtd SSMs {0}", vIph);
						}
					}
				}
			}

			logar("qtd total SSMs {0}", vQtdSsms);

			if (vQtdSsms == null || CoreUtil.igual(vQtdSsms, 0)) {
				// Conta nao possui nenhum SSM lancado nos itens
				FatMensagemLog fatMensagemLog = new FatMensagemLog();
				fatMensagemLog.setCodigo(36);
				faturamentoON
						.criarFatLogErrors("CONTA NAO POSSUI NENHUM SSM LANCADO NOS ITENS.", INT, regContaIni.getSeq(), null, null,
								null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz, vIphSolic, vIphRealiz, vPacCodigo,
								regContaIni.getProcedimentoHospitalarInterno() != null ? regContaIni
										.getProcedimentoHospitalarInterno().getSeq() : null, regContaIni
										.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
										.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
								vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

				contaOk = Boolean.FALSE;
				logar("##### AGHU - CONTA NOT OK ##### Motivo: CONTA NAO POSSUI NENHUM SSM LANCADO NOS ITENS. (AL)");
			}

			// VERIFICA SE CONTA POSSUI PENDÊNCIAS ADMINISTRATIVAS
			if (!fatkCth5RN.rnCthcVerPendAdm(regContaIni.getSeq())) {
				
				FatMensagemLog fatMensagemLog = new FatMensagemLog();
				fatMensagemLog.setCodigo(43);
				faturamentoON.criarFatLogErrors("CONTA POSSUI PENDÊNCIAS ADMINISTRATIVAS.", INT, regContaIni.getSeq(), null, null,
						null, null, dataPrevia, null, null, null, null, null, null, vPacCodigo, null, null, null, vPacProntuario,
						null, null, null, null, null, null, null, null, "RN_CTHC_VER_PEND_ADM", null, null,fatMensagemLog);

				contaOk = Boolean.FALSE;
				logar("##### AGHU - CONTA NOT OK ##### Motivo: CONTA POSSUI PENDÊNCIAS ADMINISTRATIVAS. (AM)");
			}

			// Vai verificar itens da conta:
			if (contaOk || pPrevia) {
				// As verificacoes anteriores validam e atualizam colunas na
				// tabela conta hospitalar por isso é necessário buscar o
				// registro novamente

				regConta = fatContasHospitalaresDAO.obterPorChavePrimaria(pCthSeq);

				// Busca codigo neutro para procedimentos nao faturaveis

				vCodigoNeutro = this.buscarVlrLongAghParametro(AghuParametrosEnum.P_COD_SUS_NAO_FATUR);
				
				//Ney, 09/06/2011:
				// Busca itens de TOMOGRAFIA DE CRÂNIO E COLUNA na conta
//				List<FatItensContaHospitalar> auxListaItensContaHospitalar = fatItemContaHospitalarDAO.listarItensContaHospitalar(
//						regConta.getSeq(), phiSeqsTomografia);
//
//				if (auxListaItensContaHospitalar != null && !auxListaItensContaHospitalar.isEmpty()) {
//					vPhiTomo = auxListaItensContaHospitalar.get(0).getProcedimentoHospitalarInterno().getSeq();
//				}

				// Agrupa itens de conta por PHI, busca valor de um dos IPHs associados
				if (!this.rnCthcAtuAgrupich(regConta.getSeq(), regConta.getConvenioSaudePlano() != null ? regConta
						.getConvenioSaudePlano().getId().getCnvCodigo() : null, regConta.getConvenioSaudePlano() != null ? regConta
						.getConvenioSaudePlano().getId().getSeq() : null, vPhoRealiz, vIphRealiz, vPhiTomo)) {
					logar("erro no agrupamento ICH para buscar VALOR PHI");
					contaOk = Boolean.FALSE;
					logar("##### AGHU - CONTA NOT OK ##### Motivo: erro no agrupamento ICH para buscar VALOR PHI. (AN)");
				}
			}

			// Busca itens conta agrupados por PHI em ordem descrescente de valor
			if (contaOk || pPrevia) { //esse if falha
				// Busca ITENS da conta hospitalar
				logar("COMECA A PEGAR ITENS DA CONTA");

				List<FatAgrupItemConta> listaAgrupItensContaHospitalar = fatAgrupItemContaDAO.buscaItensContaHospitalarOrdemValorDescDthrRealizadoAsc(regConta.getSeq());

				if (listaAgrupItensContaHospitalar != null && !listaAgrupItensContaHospitalar.isEmpty()) {
					for(FatAgrupItemConta regItem : listaAgrupItensContaHospitalar){
						// faturamentoFacade.evict(regItem);

						logar("--> PHI: {0} QTD: {1} Matricula Resp: {2} Vinculo: {3}", regItem.getId().getPhiSeq(), 
								regItem.getQuantidadeRealizada(), regItem.getSerMatriculaResp(), regItem.getSerVinCodigoResp());

						vModoCobranca = null;
						vTipoItem = null;
						vConsistente = Boolean.TRUE; 
						encerrar = Boolean.FALSE;
						dthrRealizado = regItem == null ? null : regItem.getId().getDthrRealizado();

						List<RnFatcVerItprocexcVO> rnFatcVerItprocexcVOList = getVerificacaoFaturamentoSusRN().verificarExcecoesItemProcHosp(regItem.getId().getPhiSeq(),
																																			 regItem.getQuantidadeRealizada(),
																																			 regConta.getConvenioSaudePlano()!=null?regConta.getConvenioSaudePlano().getId().getCnvCodigo():null,
																																			 regConta.getConvenioSaudePlano()!=null?regConta.getConvenioSaudePlano().getId().getSeq():null,
																																			 DominioOrigemProcedimento.I, 
																																			 vPhoRealiz, 
																																			 vIphRealiz, 
																																			 null);
						if (rnFatcVerItprocexcVOList != null && !rnFatcVerItprocexcVOList.isEmpty()) {
							vQtdItens = rnFatcVerItprocexcVOList.size();
							vIphQtdItem = rnFatcVerItprocexcVOList.get(0).getQtdItem();
						} else {
							vQtdItens = 0;
						}
							
						if(vQtdItens > 0){
							logar("rn_fatc_ver_itprocexc - V_QTD_ITENS: {0} v_iph_qtd_item={1}", vQtdItens,vIphQtdItem);
						} else {
							logar("Retornou zerado.");
						}
						
						int vIndice = 0;
						do {
							// Nao é uma prévia e houve erro anteriormente
							if (!pPrevia && !vConsistente) {
								break;
							}
							
							if(!rnFatcVerItprocexcVOList.isEmpty() && vIndice >0 && vIndice <= rnFatcVerItprocexcVOList.size()){
								vIphQtdItem = rnFatcVerItprocexcVOList.get(vIndice).getQtdItem();
							}
							
							// ---- INICIO DO PROCESSAMENTO DO ITEM:

							if (CoreUtil.menorOuIgual(vQtdItens, 0)) {
								// Nao encontrou item procedimento hospitalar
								List<FatItemContaHospitalar> auxListaItensContaHospitalar = fatItemContaHospitalarDAO
										.listarPorContaHospitalarProcedimentoHospitalarClassificacaoBrasileiraOcupacoes(
												regConta.getSeq(), regItem.getId().getPhiSeq(), vPhiTomo, phiSeqsTomografia);

								if (auxListaItensContaHospitalar != null && !auxListaItensContaHospitalar.isEmpty()) {
									for (int j = 0; j < auxListaItensContaHospitalar.size(); j++) {
										regIchPhi = auxListaItensContaHospitalar.get(j);
										FatMensagemLog fatMensagemLog = new FatMensagemLog();
										fatMensagemLog.setCodigo(141);
										faturamentoON
												.criarFatLogErrors(
														"NAO ENCONTROU ITEM PROCEDIMENTO HOSPITALAR CORRESPONDENTE AO PROCED HOSP INTERNO.",
														INT,
														regConta.getSeq(),
														null,
														null, 
														null, // coditemsus1
														null, // coditemsus2
														dataPrevia,
														null,
														regIchPhi.getId().getSeq(), // ichSeqp		,reg_ich_phi.seq
														vPhoSolic, //iphphoseq 						v_pho_solic
														vPhoRealiz, //iphphoseqRealizado			v_pho_realiz
														vIphSolic, // iphseq						v_iph_solic
														vIphRealiz, // iphSeqRealizado				v_iph_realiz
														vPacCodigo,
														regConta.getProcedimentoHospitalarInterno() != null ? regConta
																.getProcedimentoHospitalarInterno().getSeq() : null, //phiSeq
																
														regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta
																.getProcedimentoHospitalarInternoRealizado().getSeq()
																: null, // phiSeqRealizado 
																
														null, // pmrSeq
														vPacProntuario, 
														vCodSusRealiz, //codItemSusRealizado 		v_cod_sus_realiz
														vCodSusSolic, //codItemSusSolicitado 		v_cod_sus_solic
														null, //iphphoseqItem1 
														null, //iphphoseqItem2
														null, //iphSeqItem1
														null, //iphSeqItem2
														regItem.getId().getPhiSeq(), //phiSeqItem1
														null, //phiSeqItem2
														RN_CTHC_ATU_ENC_PRV, 
														null, //serVinCodigoProf
														null, //serMatriculaProf
														fatMensagemLog
													);
									}
								}

								vConsistente = Boolean.FALSE;
							}

							if (!pPrevia && !vConsistente) {
								break;
							}

							if (CoreUtil.maior(vQtdItens, 0)) {
								// ---- INICIO DA VALIDACAO DO ITEM:
								rnFatcVerItprocexcVO = rnFatcVerItprocexcVOList.get(vIndice);
								
								//(vIndice+1) pro log ficar igual ao do AGH
								logar("ind: {0} PHO: {1} IPH: {2} QTD: {3}", (vIndice+1), rnFatcVerItprocexcVO.getPhoSeq(), rnFatcVerItprocexcVO.getSeq(), rnFatcVerItprocexcVO.getQtdItem());

								vNotaFiscal = null;
								vNfFalsa = null;
								vPtosAnest = null;
								vPtosCirurgiao = null;
								vPtosSadt = null;
								vCodSus = null;
								vTivCodSus = null;
								vTaoCodSus = null;
								vCgcNf = null;
								vSerie = null;
								vLote = null;
								vRegAnvisa = null;
								vCnpjRegAnvisa = null;

								// Busca dados item proced hospialar
								FatItensProcedHospitalar fatItensProcedHospitalar = fatItensProcedHospitalarDAO
										.obterPorChavePrimaria(new FatItensProcedHospitalarId(rnFatcVerItprocexcVO.getPhoSeq(),
												rnFatcVerItprocexcVO.getSeq()));

								if (fatItensProcedHospitalar != null) {
									vCodSus = fatItensProcedHospitalar.getCodTabela();
									vPtosAnest = fatItensProcedHospitalar.getPontoAnestesista() == null ? null : fatItensProcedHospitalar.getPontoAnestesista().longValue();
									vPtosCirurgiao = fatItensProcedHospitalar.getPontosCirurgiao() == null ? null : fatItensProcedHospitalar.getPontosCirurgiao().longValue();
									vPtosSadt = fatItensProcedHospitalar.getPontosSadt() == null ? null : fatItensProcedHospitalar.getPontosSadt().longValue();
									vTivSeq = fatItensProcedHospitalar.getTiposVinculo() != null ? fatItensProcedHospitalar
											.getTiposVinculo().getSeq() : null;
									vTaoSeq = fatItensProcedHospitalar.getTipoAto() != null ? fatItensProcedHospitalar
											.getTipoAto().getSeq() : null;
									vNfFalsa = fatItensProcedHospitalar.getNotaFiscalFalsa() != null ? Integer.valueOf(StringUtils
											.leftPad(fatItensProcedHospitalar.getNotaFiscalFalsa().toString(), 7, "0")
											.substring(1, 6)) : null;
									vQtdProced = fatItensProcedHospitalar.getQtdProcedimentosItem();
								}

								vPtosAnest = vPtosAnest != null ? vPtosAnest : 0;
								vPtosCirurgiao = vPtosCirurgiao != null ? vPtosCirurgiao : 0;
								vPtosSadt = vPtosSadt != null ? vPtosSadt : 0;
								vCodSus = vCodSus != null ? vCodSus : 0;

								logar("COD_SUS: {0}", vCodSus);
								logar(" PONTOS: ANEST: {0} CIRUR: {1} SADT: {2}", vPtosAnest, vPtosCirurgiao, vPtosSadt);

								// Verifica se o procedimento e' o codigo generico p/itens nao faturaveis
								if (CoreUtil.igual(vCodSus, vCodigoNeutro)) {
									logar("codigo neutro de procedimento nao relacionado");
									vConsistente = Boolean.FALSE;
								}

								if (!vConsistente) {
									break;
								}

								// Tipo de vinculo
								if(vTivSeq != null){
									vTivCodSus = fatTiposVinculoDAO.obterCodigoSusPorFatTiposVinculoESituacao(vTivSeq, DominioSituacao.A);
								}

								vTivCodSus = vTivCodSus != null ? vTivCodSus : 0;

								// Tipo de ato
								if(vTaoSeq != null){ 
									vTaoCodSus = fatTipoAtoDAO.obterCodigoSusPorFatTipoAtoESituacao(vTaoSeq, DominioSituacao.A);
								}

								vTaoCodSus = nvl(vTaoCodSus, 0);

								logar("TIV: {0} TAO: {1} TIPO ITEM: {2}", vTivCodSus, vTaoCodSus, vTipoItem);

								// Verifica se o item representa o procedimento realizado
								// (deve somar valor)
								Integer regItemPhiSeq = regItem != null ? regItem.getId().getPhiSeq() : null;
								Integer regContaPhiSeq = regConta != null && regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta.getProcedimentoHospitalarInternoRealizado().getSeq() : null;
								
								if(CoreUtil.igual(regItemPhiSeq, regContaPhiSeq) && CoreUtil.igual(vCodSus, vCodSusRealiz)){												
									logar("xxx item corresponde ao realizado!!!!!!!!!!!!!!!!!!!!");

									vTipoItem = DominioTipoItemEspelhoContaHospitalar.R;
									vModoCobranca = DominioModoCobranca.V;
								} else {
									vTipoItem = DominioTipoItemEspelhoContaHospitalar.D;
								}

								// Se o paciente tem apac no periodo,
								// nao cobra o item cfv (20/12/2001)
								vCaract = faturamentoRN.verificarCaracteristicaExame(rnFatcVerItprocexcVO.getSeq(), rnFatcVerItprocexcVO
										.getPhoSeq(), DominioFatTipoCaractItem.NAO_COBRA_SE_HOUVER_TRAT_AMB);

								if (vCaract) {
									// Procurar apac de nefro(27)
									final List<MenorDataValidacaoApacVO> apacVOs = getFatItemContaHospitalarDAO()
																.obterMenorDataValidacaoApac( pCthSeq, 
																							  DominioSituacaoItenConta.A, 
																							  regItem.getId().getPhiSeq(), 
																							  dthrRealizado);
									
									Date regIchPhiApac_dthrRealizado = (apacVOs != null && !apacVOs.isEmpty() ? apacVOs.get(0).getDthrRealizado() : null);

									logar("verifica apac nefro! data realizado={0} data pesquisa={1}",DateUtil.obterDataFormatada(dthrRealizado, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO)
																									 ,DateUtil.obterDataFormatada(regIchPhiApac_dthrRealizado, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
									Long aux = faturamentoFatkCap2RN.rnCapcVerCapPac( vPacCodigo, 
																						 (Date) CoreUtil.nvl(regIchPhiApac_dthrRealizado, regItem.getId().getDthrRealizado()), 
																						 (byte) 27, 
																						 DominioModuloCompetencia.INT);
									if (CoreUtil.maior(aux, 0)) {
										FatMensagemLog fatMensagemLog = new FatMensagemLog();
										fatMensagemLog.setCodigo(195);
										faturamentoON.criarFatLogErrors(
												"PAC. TEM APAC. ESTE PROCEDIMENTO NAO PODE SER COBRADO EM AIH", INT,
												regConta.getSeq(), null, null, vCodSus, null, dataPrevia, null,
												regIchPhi != null ? regIchPhi.getId().getSeq() : null, vPhoSolic, vPhoRealiz,
												vIphSolic, vIphRealiz, vPacCodigo,
												regConta.getProcedimentoHospitalarInterno() != null ? regConta
														.getProcedimentoHospitalarInterno().getSeq() : null, regConta
														.getProcedimentoHospitalarInternoRealizado() != null ? regConta
														.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null,
												vPacProntuario, vCodSusRealiz, vCodSusSolic, rnFatcVerItprocexcVO.getPhoSeq(), null,
												rnFatcVerItprocexcVO.getSeq(), null,
												regItem.getId().getPhiSeq(), null,
												RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

										vConsistente = Boolean.FALSE;
										logar("Possui apac nefro!");
									}
								}

								int auxQtdProced = vQtdProced != null ? vQtdProced : 0;

								// ORADB: FATK_IPH_RN.RN_IPHC_VER_MAIORINT
								FatItensProcedHospitalar aux = fatItensProcedHospitalarDAO.obterPorChavePrimaria( new FatItensProcedHospitalarId(rnFatcVerItprocexcVO.getPhoSeq(),rnFatcVerItprocexcVO.getSeq()) );
								Boolean indQtdMaiorInt = (aux != null) ? aux.getQuantidadeMaiorInternacao() : Boolean.TRUE;
								
								if (Boolean.FALSE.equals(indQtdMaiorInt) && CoreUtil.maior(auxQtdProced, vDiasConta)) {
									FatMensagemLog fatMensagemLog = new FatMensagemLog();
									fatMensagemLog.setCodigo(228);
									faturamentoON.criarFatLogErrors("QTD PROCED. ITEM > NUMERO DE DIAS DA CONTA", INT,
											regConta.getSeq(), null, null, vCodSus, null, dataPrevia, null,
											regIchPhi != null ? regIchPhi.getId().getSeq() : null, vPhoSolic, vPhoRealiz,
											vIphSolic, vIphRealiz, vPacCodigo,
											regConta.getProcedimentoHospitalarInterno() != null ? regConta
													.getProcedimentoHospitalarInterno().getSeq() : null, regConta
													.getProcedimentoHospitalarInternoRealizado() != null ? regConta
													.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null,
											vPacProntuario, vCodSusRealiz, vCodSusSolic, rnFatcVerItprocexcVO.getPhoSeq(), null,
											rnFatcVerItprocexcVO.getSeq(), null, regIchPhi != null ? regIchPhi
													.getProcedimentoHospitalarInterno().getSeq() : null, null,
											"RN_IPHC_VER_MAIORINT", null, null,fatMensagemLog);

									if (!pPrevia) { // se for previa continua processo
										vConsistente = Boolean.FALSE;
										result = Boolean.FALSE;
										logar("##### AGHU - ERRO AO ENCERRAR ##### Motivo: QTD PROCED. ITEM > NUMERO DE DIAS DA CONTA (13)");
									}
								}

								// Valida regras de negocio para o item
								RnIchcAtuRegrasVO rnIchcAtuRegrasVO = faturamentoFatkIchRN.rnIchcAtuRegras(regConta.getSeq(),
										dataPrevia, vPacCodigo, vPacProntuario, regContaIni
												.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
												.getProcedimentoHospitalarInternoRealizado().getSeq() : null, vPhoRealiz,
										vIphRealiz, vCodSusRealiz, regItem.getId().getPhiSeq(),
										rnFatcVerItprocexcVO.getPhoSeq(), rnFatcVerItprocexcVO.getSeq(), vCodSus, vModoCobranca, vVlrServHosp,
										vVlrSadt, vVlrProced, vVlrAnest, vVlrServProf, regPac != null ? regPac.getDtNascimento() : null,
										vCodExclusaoCritica,
										// Ney, em 06/06/2010 (Parametro novo incluido para obter os valores a partir da data de realização) Portaria 203
										DateUtil.truncaData(dthrRealizado));

								if (rnIchcAtuRegrasVO != null) {
									regrasIch = rnIchcAtuRegrasVO.getRetorno();
									vModoCobranca = rnIchcAtuRegrasVO.getModoCobranca();
									vVlrServHosp = rnIchcAtuRegrasVO.getVlrSh();
									vVlrSadt = rnIchcAtuRegrasVO.getVlrSadt();
									vVlrProced = rnIchcAtuRegrasVO.getVlrProc();
									vVlrAnest = rnIchcAtuRegrasVO.getVlrAnest();
									vVlrServProf = rnIchcAtuRegrasVO.getVlrSp();
									vCodExclusaoCritica = rnIchcAtuRegrasVO.getCodExclusaoCritica();
								}

								// a insercao de erros quando não se encontra regra
								// é feita na rotina de validacao das regras
								logar("regras_ich: {0}", regrasIch);
								vVlrProced = BigDecimal.ZERO;
								if (regrasIch != null && CoreUtil.igual(regrasIch, 0)) {
									// Nao passou pelas regras de validação do item
									vConsistente = Boolean.FALSE;

									// #34548
									//-- MARINA 27/12/2013 - CHAMADO 117657
									
									FatMensagemLog fatMensagemLog= new FatMensagemLog();
									fatMensagemLog.setCodigo(104);
									faturamentoON.criarFatLogErrors(
											"ITEM INCOMPATIVEL COM PROCEDIMENTO REALIZADO.",
											INT,
											regConta.getSeq(),
											null, 
											null, 
											vCodSus, 
											null, 
											dataPrevia, 
											null,
											regIchPhi != null ? regIchPhi.getId().getSeq() : null, 
											vPhoSolic, 
											vPhoRealiz,
											vIphSolic,
											vIphRealiz, 
											vPacCodigo,
											regConta.getProcedimentoHospitalarInterno() != null ? regConta.getProcedimentoHospitalarInterno().getSeq() : null, 
											regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta.getProcedimentoHospitalarInternoRealizado().getSeq() : null, 
											null,
											vPacProntuario, vCodSusRealiz, vCodSusSolic, rnFatcVerItprocexcVO.getPhoSeq(),
											null,
											rnFatcVerItprocexcVO.getSeq(),
											null, 
											regIchPhi != null ? regIchPhi.getProcedimentoHospitalarInterno().getSeq() : null, 
											null,
											"RN_CTHC_ATU_ENC_PRV", 
											null, 
											null,fatMensagemLog
											);
									// - FIM MARINA - CHAMADO 117657 
									
								} else if (regrasIch != null && CoreUtil.igual(regrasIch, -1)) {
									// Nao passou pelas regras de
									// validação do item e nao pode
									// encerrar a conta
									vConsistente = Boolean.FALSE;
									result = Boolean.FALSE;
									logar("##### AGHU - ERRO AO ENCERRAR ##### Motivo: regrasIch != null && CoreUtil.igual(regrasIch, -1) (14)");
								}

								logar("VLRS: SH: {0} SADT: {1} PROC: {2} ANEST: {3} SP: {4} MODO_COBR: {5}",
										vVlrServHosp, vVlrSadt, vVlrProced, vVlrAnest, vVlrServProf, vModoCobranca);

								vCaract = faturamentoRN.verificarCaracteristicaExame(rnFatcVerItprocexcVO.getSeq(), rnFatcVerItprocexcVO
										.getPhoSeq(), DominioFatTipoCaractItem.COBRA_EM_CODIGO_REPRES_QTDE);

								auxQtdProced = vQtdProced != null ? vQtdProced : 0;
								if (vCaract && !CoreUtil.igual(auxQtdProced, regItem.getQuantidadeRealizada())) {
									BigDecimal auxBigDecimal = new BigDecimal(auxQtdProced
											* (regItem.getQuantidadeRealizada() - auxQtdProced));
									getFaturamentoInternacaoRN().rnCthcAtuInsPit(regConta.getSeq(), rnFatcVerItprocexcVO.getPhoSeq(), rnFatcVerItprocexcVO
											.getSeq(), vCodSus, (long) (regItem.getQuantidadeRealizada() - auxQtdProced),
											regItem.getQuantidadeRealizada() == null ? null : regItem.getQuantidadeRealizada().longValue(), vVlrServHosp.divide(auxBigDecimal), vVlrServProf
													.divide(auxBigDecimal), vVlrSadt.divide(auxBigDecimal), vVlrProced
													.divide(auxBigDecimal), vVlrAnest.divide(auxBigDecimal), vPtosAnest,
											vPtosCirurgiao, vPtosSadt);
									FatMensagemLog fatMensagemLog = new FatMensagemLog();
									fatMensagemLog.setCodigo(235);
									faturamentoON.criarFatLogErrors("QUANTIDADE REALIZADA > MÁX FATURÁVEL POR CÓDIGO.", INT,
											regConta.getSeq(), null, null, vCodSus, null, dataPrevia, null,
											regIchPhi != null ? regIchPhi.getId().getSeq() : null, vPhoSolic, vPhoRealiz,
											vIphSolic, vIphRealiz, vPacCodigo,
											regConta.getProcedimentoHospitalarInterno() != null ? regConta
													.getProcedimentoHospitalarInterno().getSeq() : null, regConta
													.getProcedimentoHospitalarInternoRealizado() != null ? regConta
													.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null,
											vPacProntuario, vCodSusRealiz, vCodSusSolic, rnFatcVerItprocexcVO.getPhoSeq(), null,
											rnFatcVerItprocexcVO.getSeq(), null, regIchPhi.getProcedimentoHospitalarInterno().getSeq(),
											null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);
								}

								if (!vConsistente) {
									getFaturamentoInternacaoRN().rnCthcAtuInsPit(regConta.getSeq(), rnFatcVerItprocexcVO.getPhoSeq(), rnFatcVerItprocexcVO
											.getSeq(), vCodSus, rnFatcVerItprocexcVO.getQtdItem() == null ? null : rnFatcVerItprocexcVO.getQtdItem().longValue(), rnFatcVerItprocexcVO.getQtdItem() == null ? null : rnFatcVerItprocexcVO.getQtdItem().longValue(),
											vVlrServHosp, vVlrServProf, vVlrSadt, vVlrProced, vVlrAnest, vPtosAnest,
											vPtosCirurgiao, vPtosSadt);
								}

								if (!pPrevia && !vConsistente) {
									break;
								}

								// Verifica compatibilidade do item c/procedimento realizado
								logar("### inicio compatibilidade ####");

								FatItensProcedHospitalar auxItemProcedimentoHospitalar = fatItensProcedHospitalarDAO
										.obterPorChavePrimaria(new FatItensProcedHospitalarId(vPhoRealiz, vIphRealiz));

								if (auxItemProcedimentoHospitalar != null) {
									vCirMul = auxItemProcedimentoHospitalar.getCirurgiaMultipla();
									vAids = auxItemProcedimentoHospitalar.getAidsPolitraumatizado();
									vPolit = auxItemProcedimentoHospitalar.getBuscaDoador();
								}

								// busca para identificar procedimentos que poder cobrar junto outros procedimentos (como multiplas)
								final Long vCodProcMultipla = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_PROC_MULTIPLA).longValue();
								
								logar(" cirurgia multipla {0} aids {1} v_cod_proc_multipla:{2} v_cod_sus_realiz:{3}", vCirMul, vAids, vCodProcMultipla, vCodSusRealiz);

								// Ney, 2012 03 29
								if(CoreUtil.igual(vCodSusRealiz, vCodProcMultipla)){
									vCirMul = Boolean.FALSE;
									vAids   = Boolean.FALSE;
								}
								
								Date competencia = regConta.getDtAltaAdministrativa() != null ? DateUtil.obterDataInicioCompetencia(regConta.getDtAltaAdministrativa()) : null;
								
								if (!CoreUtil.igual(Boolean.TRUE, vCirMul) && !CoreUtil.igual(Boolean.TRUE, vAids)) {
									if (!faturamentoFatkIctRN.rnIctcVerCmpexcR(vPhoRealiz, vIphRealiz, rnFatcVerItprocexcVO.getPhoSeq(),
											rnFatcVerItprocexcVO.getSeq(), null, competencia)) {
										
										logar("não é multipla e não compatível {0} {1}", regConta.getSeq(), (regItem != null ? regItem.getId().getPhiSeq() : ""));

										List<FatItemContaHospitalar> auxListaItensContaHospitalar = fatItemContaHospitalarDAO
												.listarPorContaHospitalarProcedimentoHospitalarClassificacaoBrasileiraOcupacoes(
														regConta.getSeq(),
														regItem.getId().getPhiSeq(), vPhiTomo,
														phiSeqsTomografia);

										if (auxListaItensContaHospitalar != null && !auxListaItensContaHospitalar.isEmpty()) {
											for (int j = 0; j < auxListaItensContaHospitalar.size(); j++) {
												regIchPhi = auxListaItensContaHospitalar.get(j);

												FatMensagemLog fatMensagemLog= new FatMensagemLog();
												fatMensagemLog.setCodigo(104);
												faturamentoON
														.criarFatLogErrors( 
																"ITEM INCOMPATIVEL COM PROCEDIMENTO REALIZADO.",
																INT,
																regConta.getSeq(),
																null,
																null,
																vCodSus,
																null,
																dataPrevia,
																null,
																regIchPhi.getId().getSeq(),
																vPhoSolic,
																vPhoRealiz,
																vIphSolic,
																vIphRealiz,
																vPacCodigo,
																regConta.getProcedimentoHospitalarInterno() != null ? regConta
																		.getProcedimentoHospitalarInterno().getSeq() : null,
																regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta
																		.getProcedimentoHospitalarInternoRealizado().getSeq()
																		: null, null, vPacProntuario, vCodSusRealiz,
																vCodSusSolic, rnFatcVerItprocexcVO.getPhoSeq(), null, rnFatcVerItprocexcVO
																		.getSeq(), null, regIchPhi
																		.getProcedimentoHospitalarInterno().getSeq(), null,
																"RN_ICTC_VER_CMPEXC", null, null,fatMensagemLog);
											}
										}

										vConsistente = Boolean.FALSE;
									}
								} else {
									vSucesso = Boolean.FALSE;
									logar("--- >>> compatib cir mult ou aids {0} {1}", vCirMul, vAids);

									listaItensContaHospitalarAtivos = fatItemContaHospitalarDAO
											.listarItensContaHospitalarAtivos(regContaIni.getSeq());
									if (listaItensContaHospitalarAtivos != null && !listaItensContaHospitalarAtivos.isEmpty()) {
										for (FatItemContaHospitalar fatItemContaHospitalar : listaItensContaHospitalarAtivos) {
											if (vSucesso) {
												break;
											}

											regIch = fatItemContaHospitalar;
											vPhoEl = null;
											vIphEl = null;
											ssmOk = Boolean.FALSE;

											RnCthcVerItemSusVO rnCthcVerItemSus = fatkCthRN
													.rnCthcVerItemSus(DominioOrigemProcedimento.I, (short) 1, (byte) 1, (short) 1, regIch
															.getProcedimentoHospitalarInterno().getSeq(), null);

											if (rnCthcVerItemSus != null) {
												ssmOk = rnCthcVerItemSus.getRetorno();
												vPhoEl = rnCthcVerItemSus.getPhoSeq();
												vIphEl = rnCthcVerItemSus.getIphSeq();
											}

											logar("--- >>> encontrou ssm {0}", regIch.getProcedimentoHospitalarInterno().getSeq());
											if (ssmOk) {
												// Compatibiliza
												logar("--- >>> ssm ok {0} phi {1}", vIph, regIch.getProcedimentoHospitalarInterno().getSeq());
												logar("--- >>> pho_el {0} {1} proc {2} {3}", vPhoEl, vIphEl, rnFatcVerItprocexcVO.getPhoSeq(), rnFatcVerItprocexcVO.getSeq());
												//-- Marina 24/07/2013
												if (faturamentoFatkIctRN.rnIctcVerCmpexcR(vPhoEl, vIphEl, rnFatcVerItprocexcVO
														.getPhoSeq(), rnFatcVerItprocexcVO.getSeq(), null, regConta.getDtAltaAdministrativa())) {
													vSucesso = Boolean.TRUE;
													logar("--- >>> compatibilizou ssm {0} item  {1} phi {2}",
															vIph, rnFatcVerItprocexcVO.getSeq(), regIch.getProcedimentoHospitalarInterno().getSeq());
												}
											}
										}
									}

									if (!vSucesso) {
										logar("saida insucesso ");

										List<FatItemContaHospitalar> auxListaItensContaHospitalar = fatItemContaHospitalarDAO
												.listarPorContaHospitalarProcedimentoHospitalarClassificacaoBrasileiraOcupacoes(
														regConta.getSeq(),
														regItem.getId().getPhiSeq(), vPhiTomo,
														phiSeqsTomografia);

										if (auxListaItensContaHospitalar != null && !auxListaItensContaHospitalar.isEmpty()) {
											for (int j = 0; j < auxListaItensContaHospitalar.size(); j++) {
												regIchPhi = auxListaItensContaHospitalar.get(j);
												
												FatMensagemLog fatMensagemLog= new FatMensagemLog();
												fatMensagemLog.setCodigo(104);
												faturamentoON
														.criarFatLogErrors(
																"ITEM INCOMPATIVEL COM PROCEDIMENTO REALIZADO.",
																INT,
																regConta.getSeq(),
																null,
																null,
																vCodSus,
																null,
																dataPrevia,
																null,
																regIchPhi.getId().getSeq(),
																vPhoSolic,
																vPhoRealiz,
																vIphSolic,
																vIphRealiz,
																vPacCodigo,
																regConta.getProcedimentoHospitalarInterno() != null ? regConta
																		.getProcedimentoHospitalarInterno().getSeq() : null,
																regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta
																		.getProcedimentoHospitalarInternoRealizado().getSeq()
																		: null, null, vPacProntuario, vCodSusRealiz,
																vCodSusSolic, rnFatcVerItprocexcVO.getPhoSeq(), null, rnFatcVerItprocexcVO
																		.getSeq(), null, regIchPhi
																		.getProcedimentoHospitalarInterno().getSeq(), null,
																RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);
											}
										}

										vConsistente = Boolean.FALSE;
									}
								}

								if (!vConsistente) {
									getFaturamentoInternacaoRN().rnCthcAtuInsPit(regConta.getSeq(), rnFatcVerItprocexcVO.getPhoSeq(), rnFatcVerItprocexcVO
											.getSeq(), vCodSus, rnFatcVerItprocexcVO.getQtdItem() == null ? null : rnFatcVerItprocexcVO.getQtdItem().longValue(), rnFatcVerItprocexcVO.getQtdItem()== null? null : rnFatcVerItprocexcVO.getQtdItem().longValue(),
											vVlrServHosp, vVlrServProf, vVlrSadt, vVlrProced, vVlrAnest, vPtosAnest,
											vPtosCirurgiao, vPtosSadt);
								}

								if (!pPrevia && !vConsistente) {
									break;
								}

								// Verifica compatibilidade c/itens
								// conta ja incluidos no espelho
								List<FatEspelhoItemContaHosp> listaEspelhosItensContaHospitalar = fatEspelhoItemContaHospDAO
										.listarEspelhosItensContaHospitalar(regConta.getSeq());

								if (listaEspelhosItensContaHospitalar != null && !listaEspelhosItensContaHospitalar.isEmpty()) {
									// Buscas apenas para colocar em cache os Objetos, de uma unica vez.
									Map<String, FatCompatExclusItem> mapRnIctcVerCmpexcILoad = faturamentoFatkIctRN
											.rnIctcVerCmpexcILoad(regConta.getDtAltaAdministrativa());
									
									for (int j = 0; j < listaEspelhosItensContaHospitalar.size(); j++) {
										regItemEspelho = listaEspelhosItensContaHospitalar.get(j);

										Integer auxIphSeq = regItemEspelho.getItemProcedimentoHospitalar() != null ? regItemEspelho.getItemProcedimentoHospitalar().getId().getSeq() : null;
										Short auxPhoSeq = regItemEspelho.getItemProcedimentoHospitalar() != null ? regItemEspelho.getItemProcedimentoHospitalar().getId().getPhoSeq() : null;
										
										if (!faturamentoFatkIctRN.rnIctcVerCmpexcI(rnFatcVerItprocexcVO.getPhoSeq(),
												rnFatcVerItprocexcVO.getSeq(), auxPhoSeq, auxIphSeq, mapRnIctcVerCmpexcILoad)) {
											List<FatItemContaHospitalar> auxListaItensContaHospitalar = fatItemContaHospitalarDAO
													.listarPorContaHospitalarProcedimentoHospitalarClassificacaoBrasileiraOcupacoes(
															regConta.getSeq(), regItem.getId().getPhiSeq(), vPhiTomo, phiSeqsTomografia);

											if (auxListaItensContaHospitalar != null
													&& !auxListaItensContaHospitalar.isEmpty()) {
												for (int k = 0; k < auxListaItensContaHospitalar.size(); k++) {
													regIchPhi = auxListaItensContaHospitalar.get(k);
													FatMensagemLog fatMensagemLog= new FatMensagemLog();
													fatMensagemLog.setCodigo(102);
													faturamentoON
															.criarFatLogErrors(
																	"ITEM INCOMPATIVEL COM OUTRO ITEM JA LANCADO NO ESPELHO DA CONTA.",
																	INT,
																	regConta.getSeq(),
																	null,
																	null,
																	vCodSus,
																	regItemEspelho.getProcedimentoHospitalarSus(),
																	dataPrevia,
																	null,
																	regIchPhi.getId().getSeq(),
																	vPhoSolic,
																	vPhoRealiz,
																	vIphSolic,
																	vIphRealiz,
																	vPacCodigo,
																	regConta.getProcedimentoHospitalarInterno() != null ? regConta
																			.getProcedimentoHospitalarInterno().getSeq()
																			: null,
																	regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta
																			.getProcedimentoHospitalarInternoRealizado()
																			.getSeq()
																			: null, null, vPacProntuario, vCodSusRealiz,
																	vCodSusSolic, rnFatcVerItprocexcVO.getPhoSeq(), auxPhoSeq,
																	rnFatcVerItprocexcVO.getSeq(), auxIphSeq, regIchPhi
																			.getProcedimentoHospitalarInterno().getSeq(),
																	null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);
												}
											}

											vConsistente = Boolean.FALSE;
											break;
										}
									}
								}

								if (!vConsistente) {
									getFaturamentoInternacaoRN().rnCthcAtuInsPit(regConta.getSeq(), rnFatcVerItprocexcVO.getPhoSeq(), rnFatcVerItprocexcVO
											.getSeq(), vCodSus, rnFatcVerItprocexcVO.getQtdItem() == null ? null : rnFatcVerItprocexcVO.getQtdItem().longValue(), rnFatcVerItprocexcVO.getQtdItem() == null ? null : rnFatcVerItprocexcVO.getQtdItem().longValue(),
											vVlrServHosp, vVlrServProf, vVlrSadt, vVlrProced, vVlrAnest, vPtosAnest,
											vPtosCirurgiao, vPtosSadt);
								}

								if (!pPrevia && !vConsistente) {									
									break;
								}

								logar("savepoint nf");
								
								logar("{0} ver material {1}", vConsistente, regItem.getId().getPhiSeq());

								if (vConsistente) {
									List<CursorIchPhiMatVO> listaItensContaHospitalarMat = fatItemContaHospitalarDAO
											.obterItensContasHospitalaresMat(regConta.getSeq(), regItem.getId().getPhiSeq());

									if (listaItensContaHospitalarMat != null && !listaItensContaHospitalarMat.isEmpty()) {
										for (CursorIchPhiMatVO regIchPhiMat : listaItensContaHospitalarMat) {
											
											// Se e' material vai verificar nota fiscal
											if(regIchPhiMat.getIpsRmpSeq() != null && regIchPhiMat.getIpsNumero() != null){
												logar("RMP: {0} IPS: {1} PHI: {2} PHO: {3} IPH: {4}",
														regIchPhiMat.getIpsRmpSeq(),
														regIchPhiMat.getIpsNumero(),
														regIchPhiMat.getPhiSeq(),
														rnFatcVerItprocexcVO.getPhoSeq(),
														rnFatcVerItprocexcVO.getSeq());
												
												// Verifica se o item exige nota fiscal e, neste caso, 
												// busca a nota fiscal correspondente
												if (faturamentoFatkCgiRN.rnCgicVerExigenf(regIchPhiMat.getPhiSeq(), rnFatcVerItprocexcVO.getPhoSeq(),
														rnFatcVerItprocexcVO.getSeq(),
														regConta.getConvenioSaudePlano() != null ? regConta
																.getConvenioSaudePlano().getId().getCnvCodigo() : null,
														regConta.getConvenioSaudePlano() != null ? regConta
																.getConvenioSaudePlano().getId().getSeq() : null)
														|| faturamentoFatkCgiRN
																.rnEgicVerExigenf(regIchPhiMat.getPhiSeq(),
																		vPhoRealiz, vIphRealiz, regConta
																				.getConvenioSaudePlano() != null ? regConta
																				.getConvenioSaudePlano().getId()
																				.getCnvCodigo() : null, regConta
																				.getConvenioSaudePlano() != null ? regConta
																				.getConvenioSaudePlano().getId().getSeq()
																				: null)) {
													logar("ITEM EXIGE NF");
	
													// Verifica se o item possui NF falsa (fixa) 
													// ou se precisa busca a NF real no sistema de materiais
													if (vNfFalsa != null) {
														logar("v_nf_falsa={0}", vNfFalsa);
														vNotaFiscal = vNfFalsa;
													} else {
														SceItemRmps sceItemRmps = getEstoqueFacade().obterSceItemRmpsPorChavePrimaria(new SceItemRmpsId(regIchPhiMat.getIpsRmpSeq(), regIchPhiMat.getIpsNumero()));
														if (sceItemRmps != null && sceItemRmps.getNotaFiscal() != null) {
															vNotaFiscal = Integer.valueOf(StringUtils.leftPad(sceItemRmps.getNotaFiscal().toString(), 7, "0").substring(1, 7));
															vCgcNf = sceItemRmps.getSceRmrPaciente().getScoFornecedor() != null ? sceItemRmps.getSceRmrPaciente().getScoFornecedor().getCgc(): null;
															vSerie = sceItemRmps.getSerie();
															vLote = sceItemRmps.getLote();
															vRegAnvisa = sceItemRmps.getRegistroAnvisa();
															vCnpjRegAnvisa = sceItemRmps.getCnpjRegistroAnvisa();
														}
														logar("cursor c_ips v_nota_fiscal={0} v_cgc_nf={1}",vNotaFiscal,vCgcNf);
	
														if (vNotaFiscal == null) {
															sceItemRmps = getEstoqueFacade().buscarItemRmpsPorRmpSeq(regIchPhiMat.getIpsRmpSeq());
															if (sceItemRmps != null) {
																vNotaFiscal = sceItemRmps.getNotaFiscal() != null ? Integer.valueOf(StringUtils.leftPad(sceItemRmps.getNotaFiscal().toString(),7, "0").substring(1, 6)) : null;
																vCgcNf = sceItemRmps.getSceRmrPaciente().getScoFornecedor() != null ? sceItemRmps.getSceRmrPaciente().getScoFornecedor().getCgc(): null;
																vSerie = sceItemRmps.getSerie();
																vLote = sceItemRmps.getLote();
																vRegAnvisa = sceItemRmps.getRegistroAnvisa();
																vCnpjRegAnvisa = sceItemRmps.getCnpjRegistroAnvisa();
															}
														}
	
														if (vNotaFiscal == null) {
															logar("NAO ACHOU NF NO SCE");
	
															// Se item exige NF e nao encontrou NF
															// , a conta nao pode ser encerrada
															
															FatMensagemLog fatMensagemLog = new FatMensagemLog();
															fatMensagemLog.setCodigo(160);
															faturamentoON
																	.criarFatLogErrors(
																			"NAO FOI POSSIVEL IDENTIFICAR A NOTA FISCAL DO ITEM.",
																			INT,
																			regConta.getSeq(),
																			null,
																			null,
																			vCodSus,
																			null,
																			dataPrevia,
																			null,
																			regIchPhiMat.getSeq(),
																			vPhoSolic,
																			vPhoRealiz,
																			vIphSolic,
																			vIphRealiz,
																			vPacCodigo,
																			regConta.getProcedimentoHospitalarInterno() != null ? 
																					regConta.getProcedimentoHospitalarInterno().getSeq() : null,
																			regConta.getProcedimentoHospitalarInternoRealizado() != null ? 
																					regConta.getProcedimentoHospitalarInternoRealizado().getSeq() : null, 
																			null, vPacProntuario,
																			vCodSusRealiz, vCodSusSolic, 
																			rnFatcVerItprocexcVO.getPhoSeq(), null, 
																			rnFatcVerItprocexcVO.getSeq(), null, 
																			regIchPhiMat.getPhiSeq(), null, 
																			RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);
															//																					
															if (Boolean.FALSE.equals(pPrevia)
																	&& !CoreUtil.igual(CoreUtil.nvl(regContaIni.getIndAutorizadoSms(), "N"), "N")) {
																vConsistente = Boolean.FALSE;
																result = Boolean.FALSE;
																logar("##### AGHU - ERRO AO ENCERRAR ##### Motivo: !pPrevia && !CoreUtil.igual(N, regContaIni.getIndAutorizadoSms()) (15)");
															}
														}
													}
												}
											}
										}
									}
								}

								if (!vConsistente) {
									getFaturamentoInternacaoRN().rnCthcAtuInsPit(regConta.getSeq(), rnFatcVerItprocexcVO.getPhoSeq(), rnFatcVerItprocexcVO
											.getSeq(), vCodSus, rnFatcVerItprocexcVO.getQtdItem() == null ? null : rnFatcVerItprocexcVO.getQtdItem().longValue(), rnFatcVerItprocexcVO.getQtdItem() == null ? null : rnFatcVerItprocexcVO.getQtdItem().longValue(),
											vVlrServHosp, vVlrServProf, vVlrSadt, vVlrProced, vVlrAnest, vPtosAnest,
											vPtosCirurgiao, vPtosSadt);
								}

								if (!pPrevia && !vConsistente) {
									break;
								}
								// ---- FIM DA VALIDACAO DO ITEM. ----

								// ---- INICIO DA VERIFICACAO DOS ATOS OBRIGATORIOS DO ITEM: ----
								logar("savepoint obrig");
								
								// eSchweigert 13/11/2012, removi o ind_internacao, pois não é mais usado
					             // FGi 01/03/01 Verificacao antiga
					             // so busca atos obrigatorios se for SSM realizado
					             // ou nao for SSM
								//--            or (v_ind_internacao = 'N') then  -- o item nao e' um SSM

								// Busca atos obrigatorios do item
								List<RnIphcVerAtoObriVO> listaRnIphcVerAtoObriVO = verificacaoItemProcedimentoHospitalarRN
										.obterListaTivTaoCodSusIphCobradoQtd(rnFatcVerItprocexcVO.getPhoSeq(), rnFatcVerItprocexcVO.getSeq(),
												rnFatcVerItprocexcVO.getQtdItem(), vDiariasConta.shortValue());

								vQtdAtosObrig = listaRnIphcVerAtoObriVO != null ? listaRnIphcVerAtoObriVO.size() : 0;

								logar("QTD ATOS OBRIG: {0}", vQtdAtosObrig);

								// Consistencia: qualquer ssm realizado(exceto multi noso) deve ter atos obrigatorios
								FatItensProcedHospitalar auxFatItemProcedHospitalar = fatItensProcedHospitalarDAO
										.obterPorChavePrimaria(new FatItensProcedHospitalarId(rnFatcVerItprocexcVO.getPhoSeq(),
												rnFatcVerItprocexcVO.getSeq()));

								if (auxFatItemProcedHospitalar != null) {
									vCirMul = auxFatItemProcedHospitalar.getCirurgiaMultipla();
									vAids = auxFatItemProcedHospitalar.getAidsPolitraumatizado();
									vPolit = auxFatItemProcedHospitalar.getBuscaDoador();
								}

								logar("testa atos obrigatorios");
								logar(" v_tipo_item={0} v_cir_mul={1} v_aids={2} v_polit={3} v_qtd_atos_obrig={4}",
										vTipoItem, vCirMul, vAids, vPolit, vQtdAtosObrig);
								
								if (CoreUtil.igual(DominioTipoItemEspelhoContaHospitalar.R, vTipoItem) && !vCirMul && !vAids && !vPolit
										&& vQtdAtosObrig != null && CoreUtil.igual(vQtdAtosObrig, 0)) {
									
									FatMensagemLog fatMensagemLog = new FatMensagemLog();
									fatMensagemLog.setCodigo(149);
									faturamentoON.criarFatLogErrors(
											"NAO FOI ENCONTRADO ATOS OBRIGATORIOS PARA O SSM REALIZADO", INT, regConta
													.getSeq(), null, null, null, null, dataPrevia, null, null, null,
											vPhoRealiz, null, vIphRealiz, vPacCodigo, null, regConta
													.getProcedimentoHospitalarInternoRealizado() != null ? regConta
													.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null,
											vPacProntuario, vCodSusRealiz, null, null, null, null, null, null, null,
											RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);
								}

								vIndiceAto = 1;
								vAtoObrigConsistente = true;

								if (listaRnIphcVerAtoObriVO != null && !listaRnIphcVerAtoObriVO.isEmpty()) {
									for (int j = 0; j < listaRnIphcVerAtoObriVO.size(); j++) {
										RnIphcVerAtoObriVO rnIphcVerAtoObriVO = listaRnIphcVerAtoObriVO.get(j);

										vAtoObrigConsistente = true;
										if (CoreUtil.maior(vQtdAtosObrig, 0)) {
											logar("ind_o: {0} PHO: {1} IPH: {2} QTD: {3} TIV: {4} TAO: {5}",
													vIndiceAto,
													rnIphcVerAtoObriVO.getPhoCobradoSeq(),
													rnIphcVerAtoObriVO.getIphCobradoSeq(),
													rnIphcVerAtoObriVO.getQtd(),
													rnIphcVerAtoObriVO.getTivCodSus(),
													rnIphcVerAtoObriVO.getTaoCodSus());

											// Verifica se o procedimento é especial
											boolean procEspecial = verificacaoItemProcedimentoHospitalarRN.getProcedimentoEspecial(rnIphcVerAtoObriVO.getPhoCobradoSeq(), rnIphcVerAtoObriVO.getIphCobradoSeq());
											if (procEspecial) {
												vModoCobrancaAto = DominioModoCobranca.V;

												// Verifica se o procedimento possui valor 
												// cadastrado p/competencia
												FatVlrItemProcedHospComps auxFatVlrItemProcedHospComps = verificacaoItemProcedimentoHospitalarRN
														.obterValoresItemProcHospPorModuloCompetencia(rnIphcVerAtoObriVO
																.getPhoCobradoSeq(), rnIphcVerAtoObriVO.getIphCobradoSeq(),
																DominioModuloCompetencia.INT,
																// Ney em 06/06/2011  Portaria 203
																DateUtil.truncaData(dthrRealizado));

												if (auxFatVlrItemProcedHospComps == null) {
													vVlrServHospAto = BigDecimal.ZERO;
													vVlrSadtAto = BigDecimal.ZERO;
													vVlrProcedAto = BigDecimal.ZERO;
													vVlrAnestAto = BigDecimal.ZERO;
													vVlrServProfAto = BigDecimal.ZERO;
												} else {
													vVlrServHospAto = auxFatVlrItemProcedHospComps.getVlrServHospitalar();
													vVlrSadtAto = auxFatVlrItemProcedHospComps.getVlrSadt();
													vVlrProcedAto = auxFatVlrItemProcedHospComps.getVlrProcedimento();
													vVlrAnestAto = auxFatVlrItemProcedHospComps.getVlrAnestesista();
													vVlrServProfAto = auxFatVlrItemProcedHospComps.getVlrServProfissional();
												}
												vVlrProcedAto = BigDecimal.ZERO;
											} else {
												auxFatItemProcedHospitalar = fatItensProcedHospitalarDAO.obterPorChavePrimaria(new FatItensProcedHospitalarId(vPhoRealiz,vIphRealiz));
												vCirMul = false;
												vAids = false;
												vPolit = false;
												if (auxFatItemProcedHospitalar != null) {
													vCirMul = auxFatItemProcedHospitalar.getCirurgiaMultipla();
													vAids = auxFatItemProcedHospitalar.getAidsPolitraumatizado();
													vPolit = auxFatItemProcedHospitalar.getBuscaDoador();
												}

												vCirMul = vCirMul != null ? vCirMul : false;
												logar("v_cir_mul: {0}", vCirMul);

												if (vCirMul) {
													FatVlrItemProcedHospComps auxFatVlrItemProcedHospComps = verificacaoItemProcedimentoHospitalarRN
															.obterValoresItemProcHospPorModuloCompetencia(rnIphcVerAtoObriVO
																	.getPhoCobradoSeq(),
																	rnIphcVerAtoObriVO.getIphCobradoSeq(),
																	DominioModuloCompetencia.INT,
																	// Ney em 06/06/2011  Portaria 203
																	DateUtil.truncaData(dthrRealizado));

													if (auxFatVlrItemProcedHospComps == null) {
														vVlrServHospAto = BigDecimal.ZERO;
														vVlrSadtAto = BigDecimal.ZERO;
														vVlrProcedAto = BigDecimal.ZERO;
														vVlrAnestAto = BigDecimal.ZERO;
														vVlrServProfAto = BigDecimal.ZERO;
													} else {
														vVlrServHospAto = auxFatVlrItemProcedHospComps.getVlrServHospitalar();
														vVlrSadtAto = auxFatVlrItemProcedHospComps.getVlrSadt();
														vVlrProcedAto = auxFatVlrItemProcedHospComps.getVlrProcedimento();
														vVlrAnestAto = auxFatVlrItemProcedHospComps.getVlrAnestesista();
														vVlrServProfAto = auxFatVlrItemProcedHospComps
																.getVlrServProfissional();
													}
													
													vModoCobrancaAto = DominioModoCobranca.V;	// Marina 13/06/2012  - chamado 72340
													vVlrProcedAto = BigDecimal.ZERO;			// Ney 09/10/2012, chamado 81315
													
												} else {
													vModoCobrancaAto = DominioModoCobranca.P;
													vVlrServHospAto = BigDecimal.ZERO;
													vVlrSadtAto = BigDecimal.ZERO;
													vVlrProcedAto = BigDecimal.ZERO;
													vVlrAnestAto = BigDecimal.ZERO;
													vVlrServProfAto = BigDecimal.ZERO;
												}
											}

											// Busca dados item proced hospialar
											auxFatItemProcedHospitalar = fatItensProcedHospitalarDAO.obterPorChavePrimaria(new FatItensProcedHospitalarId(rnIphcVerAtoObriVO.getPhoCobradoSeq(), rnIphcVerAtoObriVO.getIphCobradoSeq()));
											if (auxFatItemProcedHospitalar != null) {
												vCodSusAto = auxFatItemProcedHospitalar.getCodTabela();
												vPtosAnestAto = auxFatItemProcedHospitalar.getPontoAnestesista();
												vPtosCirurgiaoAto = auxFatItemProcedHospitalar.getPontosCirurgiao();
												vPtosSadtAto = auxFatItemProcedHospitalar.getPontosSadt();
											}

											vPtosAnestAto = vPtosAnestAto != null ? vPtosAnestAto : 0;
											vPtosCirurgiaoAto = vPtosCirurgiaoAto != null ? vPtosCirurgiaoAto : 0;
											vPtosSadtAto = vPtosSadtAto != null ? vPtosSadtAto : 0;
											vCodSusAto = vCodSusAto != null ? vCodSusAto : 0;
										}

										if (CoreUtil.maior(vQtdAtosObrig, 0)) {
											try {
												// Busca proxima seq da tabela de espelho
												vEicSeqp = fatEspelhoItemContaHospDAO.buscaProximaSeqTabelaEspelho(regConta.getSeq());

												// ETB 06012008 - Busca do CBO
												logar("Por aqui {0} phi {1}",
														(regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta.getProcedimentoHospitalarInternoRealizado().getSeq() : null),
														regItem.getId().getPhiSeq());

												vIndEquipe = null;
												vCbo = null;
												vCpfProf = vCpfMedicoSolicRespons;
												//logar("*** Marina - v_ind_equipe : {0}", vIndEquipe);

												if (EXAMES_PRE_TRANSFUSIONAIS_I.equals(vCodSusAto) || EXAMES_PRE_TRANSFUSIONAIS_II.equals(vCodSusAto)) {
													vIndEquipe = null;
													vCbo = null;
													vCpfProf = null;
												} else if (CoreUtil.igual(rnIphcVerAtoObriVO.getTaoCodSus(), 12)) { // -- hemoterapia -- alterações 31012008
													vIndEquipe = 0;
													
													if(CoreUtil.isMenorMesAno(dthrRealizado,INICIO_PORTARIA_203_08)){
														//vCbo = "223134";
														vCbo = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_CBO_MEDICO_HEMOTERAPEUTA).toString();
													} else {
														//vCbo = "225190";
														vCbo = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_CBO_MEDICO_HEMOTERAPEUTA_PORTARIA_203_2011).toString();
													}
													
													vCpfProf = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_CPF_MEDICO_HEMOTERAPEUTA).longValue();
												} else if (CoreUtil.igual(rnIphcVerAtoObriVO.getTaoCodSus(), 36)) {
													vIndEquipe = 0;

													// Ney 01/09/2011
													if(CoreUtil.isMenorMesAno(dthrRealizado,INICIO_PORTARIA_203_08)){
														// vCbo = "223149";
														vCbo = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_CBO_MEDICO_PEDIATRA_ATENDIMENTO_RN).toString();
													} else {
														// vCbo = "225124";
														vCbo = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_CBO_MEDICO_PEDIATRA_ATENDIMENTO_RN_PORTARIA_203_2011).toString();
													}
													
													vCpfProf = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_CPF_MEDICO_PEDIATRA_ATENDIMENTO).longValue();
												} else if (CoreUtil.igual(vCodSusAto, 310010020)) { // Neo
													vIndEquipe = 0;
													
													if(CoreUtil.isMenorMesAno(dthrRealizado,INICIO_PORTARIA_203_08)){
														//vCbo = "223149";
														vCbo = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_CBO_MEDICO_PEDIATRA_ATENDIMENTO_RN).toString();
													} else {
														//vCbo = "225124";
														vCbo = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_COD_CBO_MEDICO_PEDIATRA_ATENDIMENTO_RN_PORTARIA_203_2011).toString();
													}
													
													vCpfProf = buscarVlrNumericoAghParametro(AghuParametrosEnum.P_CPF_MEDICO_PEDIATRA_ATENDIMENTO_SALA_PARTO).longValue();
												} else {
													List<FatItemContaHospitalar> auxListaItensContaHospitalar = fatItemContaHospitalarDAO.listarPorContaHospitalarProcedimentoHospitalarClassificacaoBrasileiraOcupacoes(
																																				regConta.getSeq(),
																																				(regItem != null ? regItem.getId().getPhiSeq() : null), 
																																				vPhiTomo, 
																																				phiSeqsTomografia);

													if (auxListaItensContaHospitalar != null && !auxListaItensContaHospitalar.isEmpty()) {
														regIchPhiCbo = auxListaItensContaHospitalar.get(0);
													} else {
														regIchPhiCbo = null;
													}
													
													logar("resp {0} - {1} anest {2} - {3}",
															(regIchPhiCbo != null && regIchPhiCbo.getServidor() != null ? regIchPhiCbo.getServidor().getId().getVinCodigo() : null),
															(regIchPhiCbo != null && regIchPhiCbo.getServidor() != null ? regIchPhiCbo.getServidor().getId().getMatricula() : null),
															(regIchPhiCbo != null && regIchPhiCbo.getServidorAnest() != null ? regIchPhiCbo.getServidorAnest().getId().getVinCodigo() : null),
															(regIchPhiCbo != null && regIchPhiCbo.getServidorAnest() != null ? regIchPhiCbo.getServidorAnest().getId().getMatricula() : null));
															

													RnCthcBuscaDadosProfVO rnCthcBuscaDadosProf = fatkCth6RN
															.rnCthcBuscaDadosProf(
																	regConta.getSeq(),
																	regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta.getProcedimentoHospitalarInternoRealizado().getSeq() : null,
																	vClinicaRealiz, rnIphcVerAtoObriVO.getPhoCobradoSeq(),
																	rnIphcVerAtoObriVO.getIphCobradoSeq(),
																	rnIphcVerAtoObriVO.getTaoCodSus(), 
																	vMatriculaRespInt,
																	vVinculoRespInt,
																	(regIchPhiCbo != null && regIchPhiCbo.getServidor() != null ? regIchPhiCbo.getServidor().getId().getVinCodigo() : null),
																	(regIchPhiCbo != null && regIchPhiCbo.getServidor() != null ? regIchPhiCbo.getServidor().getId().getMatricula() : null),
																	(regIchPhiCbo != null && regIchPhiCbo.getServidorAnest() != null ? regIchPhiCbo.getServidorAnest().getId().getVinCodigo(): null),
																	(regIchPhiCbo != null && regIchPhiCbo.getServidorAnest() != null ? regIchPhiCbo.getServidorAnest().getId().getMatricula(): null)
																	//Ney 26/08/2011 Cbo por competencia
																	, dthrRealizado, nomeMicrocomputador, dataFimVinculoServidor
																);

													if (rnCthcBuscaDadosProf != null) {
														vIndEquipe = rnCthcBuscaDadosProf.getEquipe();
														vCpfProf = rnCthcBuscaDadosProf.getCpfCns();
														vCbo = rnCthcBuscaDadosProf.getCbo();
														vMatriculaProf = rnCthcBuscaDadosProf.getMatriculaProf();
														vVinculoProf = rnCthcBuscaDadosProf.getVinculoProf();

														if (!rnCthcBuscaDadosProf.getRetorno()) {
															vErroCBO = Boolean.TRUE;
															vPhoErro = rnIphcVerAtoObriVO.getPhoCobradoSeq();
															vIphErro = rnIphcVerAtoObriVO.getIphCobradoSeq();
															logar("Inconsistência cbo passou erro");
															logar("TESTE MARINA 31/08/2012 - XXXX ");
															/*
															  IF P_CTH_SEQ = 462192 THEN
							                                     v_matricula_prof:= NULL;
							                                     v_vinculo_prof := NULL;
							                                  END IF; 
															 */
															FatMensagemLog fatMensagemLog = new FatMensagemLog();
															fatMensagemLog.setCodigo(151);
															
															faturamentoON
																	.criarFatLogErrors(
																			NAO_FOI_ENCONTRADO_CBO_MATR
																					+ vMatriculaProf + VINC
																					+ vVinculoProf,
																			INT,
																			regConta.getSeq(),
																			null,
																			null,
																			null,
																			null,
																			dataPrevia,
																			null,
																			null,
																			null,
																			vPhoRealiz,
																			null,
																			vIphRealiz,
																			vPacCodigo,
																			null,
																			regConta
																					.getProcedimentoHospitalarInternoRealizado() != null ? regConta
																					.getProcedimentoHospitalarInternoRealizado()
																					.getSeq()
																					: null, null, vPacProntuario,
																			vCodSusRealiz, null, null, vPhoErro, null,
																			vIphErro, null, null, RN_CTHC_ATU_ENC_PRV,
																			null, null, fatMensagemLog);

															logar("Inconsistência cbo inseriu erro");

															if (!pPrevia) {
																// Se previa continua
																vConsistente = Boolean.FALSE;
																result = Boolean.FALSE;
																logar("Inconsistência cbo por aqui");
																logar("##### AGHU - ERRO AO ENCERRAR ##### Motivo: Inconsistência cbo por aqui (16)");
															}
														}
													}
												}

												logar("v_ind_equipe {0}", vIndEquipe);

												if (faturamentoRN.verificarCaracteristicaExame(rnIphcVerAtoObriVO
														.getIphCobradoSeq(), rnIphcVerAtoObriVO.getPhoCobradoSeq(),
														DominioFatTipoCaractItem.POSSUI_INDICADOR_DE_EQUIPE)) {
													vIndEquipe = 1;
												}
												
												logar(" ************   1  ********************* ");
												logar("insert 1 into fat_espelhos_itens_conta_hosp:");
												
												logar("v_cod_sus_ato:{0} v_iph_qtd_item={1}",vCodSusAto,vIphQtdItem);
												logar("v_iph_ato: {0}", rnIphcVerAtoObriVO.getIphCobradoSeq());
												logar(" V_IND_EQUIPE: {0}", vIndEquipe);
												logar(" v_cod_sus_ato: {0}", vCodSusAto);
												logar(" ************************************* ");

												FatEspelhoItemContaHosp fatEspelhoItemContaHosp = new FatEspelhoItemContaHosp();

												fatEspelhoItemContaHosp.setId(new FatEspelhoItemContaHospId(regConta.getSeq(),vEicSeqp));
												fatEspelhoItemContaHosp.setIchSeq(regIchPhiCbo != null ? regIchPhiCbo.getId().getSeq() : null);
												fatEspelhoItemContaHosp
														.setItemProcedimentoHospitalar(rnIphcVerAtoObriVO.getPhoCobradoSeq() != null
																&& rnIphcVerAtoObriVO.getIphCobradoSeq() != null ? fatItensProcedHospitalarDAO
																.obterPorChavePrimaria(new FatItensProcedHospitalarId(
																		rnIphcVerAtoObriVO.getPhoCobradoSeq(),
																		rnIphcVerAtoObriVO.getIphCobradoSeq()))
																: null);
												fatEspelhoItemContaHosp.setTivSeq(rnIphcVerAtoObriVO.getTivCodSus()
														.byteValue());
												fatEspelhoItemContaHosp.setTaoSeq(rnIphcVerAtoObriVO.getTaoCodSus());
												fatEspelhoItemContaHosp.setQuantidade(rnIphcVerAtoObriVO.getQtd());
												fatEspelhoItemContaHosp
														.setPontosAnestesista((vPtosAnestAto != null ? vPtosAnestAto : 0)
																* (rnIphcVerAtoObriVO.getQtd() != null ? rnIphcVerAtoObriVO
																		.getQtd() : 0));
												fatEspelhoItemContaHosp
														.setPontosCirurgiao((vPtosCirurgiaoAto != null ? vPtosCirurgiaoAto : 0)
																* (rnIphcVerAtoObriVO.getQtd() != null ? rnIphcVerAtoObriVO
																		.getQtd() : 0));
												fatEspelhoItemContaHosp
														.setPontosSadt((vPtosSadtAto != null ? vPtosSadtAto : 0)
																* (rnIphcVerAtoObriVO.getQtd() != null ? rnIphcVerAtoObriVO
																		.getQtd() : 0));
												fatEspelhoItemContaHosp.setProcedimentoHospitalarSus(vCodSusAto);
												fatEspelhoItemContaHosp.setIndConsistente(vAtoObrigConsistente);
												fatEspelhoItemContaHosp.setIndModoCobranca(vModoCobrancaAto);
												fatEspelhoItemContaHosp
														.setValorAnestesista((vVlrAnestAto != null ? vVlrAnestAto
																: BigDecimal.ZERO)
																.multiply(rnIphcVerAtoObriVO.getQtd() != null ? new BigDecimal(
																		rnIphcVerAtoObriVO.getQtd())
																		: BigDecimal.ZERO));
												fatEspelhoItemContaHosp
														.setValorProcedimento((vVlrProcedAto != null ? vVlrProcedAto
																: BigDecimal.ZERO)
																.multiply(rnIphcVerAtoObriVO.getQtd() != null ? new BigDecimal(
																		rnIphcVerAtoObriVO.getQtd())
																		: BigDecimal.ZERO));
												fatEspelhoItemContaHosp.setValorSadt((vVlrSadtAto != null ? vVlrSadtAto
														: BigDecimal.ZERO)
														.multiply(rnIphcVerAtoObriVO.getQtd() != null ? new BigDecimal(
																rnIphcVerAtoObriVO.getQtd()) : BigDecimal.ZERO));
												fatEspelhoItemContaHosp
														.setValorServHosp((vVlrServHospAto != null ? vVlrServHospAto
																: BigDecimal.ZERO)
																.multiply(rnIphcVerAtoObriVO.getQtd() != null ? new BigDecimal(
																		rnIphcVerAtoObriVO.getQtd())
																		: BigDecimal.ZERO));
												
												fatEspelhoItemContaHosp
														.setValorServProf((vVlrServProfAto != null ? vVlrServProfAto
																: BigDecimal.ZERO)
																.multiply(rnIphcVerAtoObriVO.getQtd() != null ? new BigDecimal(
																		rnIphcVerAtoObriVO.getQtd())
																		: BigDecimal.ZERO));

												fatEspelhoItemContaHosp.setDataPrevia(dataPrevia);
												fatEspelhoItemContaHosp
														.setIndTipoItem(DominioTipoItemEspelhoContaHospitalar.O);
												fatEspelhoItemContaHosp.setIndGeradoEncerramento(true);
												fatEspelhoItemContaHosp.setIndLocalSoma(DominioLocalSoma.D);
												fatEspelhoItemContaHosp.setIndEquipe(vIndEquipe);
												fatEspelhoItemContaHosp.setCbo(vCbo);
												fatEspelhoItemContaHosp.setCpfCns(vCpfProf);
												// Ney, em 06/06/2010  Portaria 203
												fatEspelhoItemContaHosp.setCompetenciaUti(DateUtil.obterDataFormatada(dthrRealizado, "yyyyMM"));
												
												getFaturamentoRN().inserirFatEspelhoItemContaHosp(fatEspelhoItemContaHosp, true);
												//faturamentoFacade.evict(fatEspelhoItemContaHosp);
											} catch (Exception e) {
												logar("ERRO NO INSERT de ato obrigatorio EM fat_espelhos_itens_conta_hosp");

												vMsg = e.getMessage();

												List<FatItemContaHospitalar> auxListaItensContaHospitalar = fatItemContaHospitalarDAO
														.listarPorContaHospitalarProcedimentoHospitalarClassificacaoBrasileiraOcupacoes(
																regConta.getSeq(), regItem.getId().getPhiSeq(), vPhiTomo, phiSeqsTomografia);

												if (auxListaItensContaHospitalar != null
														&& !auxListaItensContaHospitalar.isEmpty()) {
													for (int k = 0; k < auxListaItensContaHospitalar.size(); k++) {
														regIchPhi = auxListaItensContaHospitalar.get(k);
														FatMensagemLog fatMensagemLog = new FatMensagemLog();
														fatMensagemLog.setCodigo(63);
														faturamentoON
																.criarFatLogErrors(
																		"ERRO AO INSERIR ATO OBRIGATORIO DO ITEM NO ESPELHO: " + vMsg,
																		INT,
																		regConta.getSeq(),
																		null,
																		null,
																		vCodSus,
																		vCodSusAto,
																		dataPrevia,
																		null,
																		regIchPhi.getId().getSeq(),
																		vPhoSolic,
																		vPhoRealiz,
																		vIphSolic,
																		vIphRealiz,
																		vPacCodigo,
																		regConta.getProcedimentoHospitalarInterno() != null ? regConta
																				.getProcedimentoHospitalarInterno().getSeq()
																				: null,
																		regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta
																				.getProcedimentoHospitalarInternoRealizado()
																				.getSeq()
																				: null,
																		null,
																		vPacProntuario,
																		vCodSusRealiz,
																		vCodSusSolic,
																		rnFatcVerItprocexcVO.getPhoSeq(),
																		rnIphcVerAtoObriVO.getPhoCobradoSeq(),
																		rnFatcVerItprocexcVO.getSeq(),
																		rnIphcVerAtoObriVO.getIphCobradoSeq(),
																		regIchPhi.getProcedimentoHospitalarInterno() != null ? regIchPhi
																				.getProcedimentoHospitalarInterno().getSeq()
																				: null, null, RN_CTHC_ATU_ENC_PRV, null,
																		null,fatMensagemLog);
													}
												}

												vAtoObrigConsistente = false;
											}
											logar("gravou v_ind_equipe {0}", vIndEquipe);
										}

										if ((vQtdAtosObrig != null && CoreUtil.igual(vQtdAtosObrig, 0)) || CoreUtil.igual(vQtdAtosObrig, vIndiceAto)
												|| (!pPrevia && !vAtoObrigConsistente)) {
											break;
										}

										vIndiceAto++;
									}
								}

								if (vAtoObrigConsistente) {
									// So insere o item se deu certo a insercao 
									// dos atos obrigatorios do item
									if (!vConsistente && CoreUtil.maior(vQtdAtosObrig, 0)) {
										// Desfaz a insercao dos atos orbrigatorios que foram
										// incluidos em funcao do item que agora esta inconsistente
										logar("rollback obrig 1");
										logar("MARINA TESTE 31/08/2012 - v_matricula_prof: {0}", vMatriculaProf);
										
										/*
										IF P_CTH_SEQ = 462192 THEN
	                                     v_matricula_prof:= NULL;
	                                     v_vinculo_prof := NULL;
	                                  END IF;*/
										
										if (vErroCBO) {
											faturamentoON.criarFatLogErrors(
													"ERRO CBO NOS ATOS OBRIGATÓRIOS - MEDICO INTERNACAO/CIRURGIA - MATR:"
															+ vMatriculaProf + VINC + vVinculoProf, INT, regConta
															.getSeq(), null, null, null, null, dataPrevia, null, null, null,
													vPhoRealiz, null, vIphRealiz, vPacCodigo, null, regConta
															.getProcedimentoHospitalarInternoRealizado() != null ? regConta
															.getProcedimentoHospitalarInternoRealizado().getSeq() : null,
													null, vPacProntuario, vCodSusRealiz, null, null, vPhoErro, null, vIphErro,
													null, null, RN_CTHC_ATU_ENC_PRV, null, null, null);
										}
									}

									// Verifica se o item representa o procedimento realizado
									if (CoreUtil.igual(vCodSus, vCodSusRealiz)) {
										logar("item corresponde ao realizado!!!!!!!!!!!!!");

										// Verifica hospital dia (cobranca diarias por qtd lancada)

										// ORADB: FATK_IPH_RN.RN_IPHC_VER_HOSP_DIA

										aux = fatItensProcedHospitalarDAO.obterPorChavePrimaria( new FatItensProcedHospitalarId(rnFatcVerItprocexcVO.getPhoSeq(),rnFatcVerItprocexcVO.getSeq()) );
										Boolean hospDia = (aux != null) ? aux.getHospDia() : Boolean.FALSE;
										
										if (Boolean.FALSE.equals(hospDia)) {
											// Ney 06/09/2011
											Short tmpVMaxQtdConta = vMaxQtdConta;
											if(tmpVMaxQtdConta == null){
												tmpVMaxQtdConta = 1;
											}
											
											if(rnFatcVerItprocexcVO.getQtdItem() > tmpVMaxQtdConta){
												rnFatcVerItprocexcVO.setQtdItem(tmpVMaxQtdConta);
												vIphQtdItem = tmpVMaxQtdConta;
											}
										}

										logar("v_dias_conta: {0} v_diarias_conta={1} v_iph_qtd_item={2}",vDiasConta,vDiariasConta,vIphQtdItem);
										logar("v_motivo_cobranca: {0}", vMotivoCobranca.toString());

										logar("v_cobra_diarias{0}", vCobrancaDiaria);
										logar("v_cobra_dias{0}", vCobraDias);
										logar("************ XIXIXIXIXIXIXIX *********{0}",rnFatcVerItprocexcVO.getSeq());
										
										
										// Verifica cobranca por diarias/dias
										if (CoreUtil.igual(DominioCobrancaDiaria.S, vCobrancaDiaria)
												|| CoreUtil.igual(DominioCobrancaDiaria.D, vCobrancaDiaria)) {
											
											// Inicio Marina 23/10/2013
											logar("**  INICIO PORTARIA 420 **");
											logar("é hospital dia, verifica caracteristica");
											
											if (faturamentoRN.verificarCaracteristicaExame(rnFatcVerItprocexcVO.getSeq(),
													rnFatcVerItprocexcVO.getPhoSeq(),
													DominioFatTipoCaractItem.COBRA_DIARIA_POR_ITEM_LANCADO)) {
												// Marina 06/12/2013 - Portaria 1151 - chamado: 115247
												Integer phiSeqR = (regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta.getProcedimentoHospitalarInternoRealizado().getSeq(): null);
												
												logar("achou caracteristica, busca a quantidade lançada na conta e a quantidade máxima: {0} {1} {2}",
														(phiSeqR != null ? phiSeqR : ""),
														vCodSusRealiz,
														pCthSeq);
												
												Short auxPhoSeq = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
												
												// -- Marina 29/08/2013 - chamado 104835
												DominioSituacaoItenConta[] arrayDominioSituacaoItemConta = { DominioSituacaoItenConta.C, DominioSituacaoItenConta.D };
																								
												// Ney 15/07/2011 Portaria 203 Fase 2
												Object[] qtdLancamento = fatItensProcedHospitalarDAO.buscaQtdeLanc( auxPhoSeq, pCthSeq,
																													arrayDominioSituacaoItemConta,
																													vCodSusRealiz, phiSeqR,
																													DateUtil.truncaData(regContaIni.getDtAltaAdministrativa())
																												);
												
												if (qtdLancamento != null) {
													vQtdeMax = (Short) qtdLancamento[0];
													vQtdeRealizada = (Short) qtdLancamento[1];
												} else {
													vQtdeMax = 0;
													vQtdeRealizada = 0; 
												}
														
												logar("v_qtde_max: {0}", vQtdeMax);
												
												logar("v_qtde_realizada: {0}", vQtdeRealizada);
												
												if (CoreUtil.menorOuIgual(vQtdeRealizada, vQtdeMax)) {
													
													vVlrShRealiz = vVlrShRealiz.add((nvl(vVlrServHosp, BigDecimal.ZERO)).multiply(new BigDecimal(vQtdeRealizada)));
													vVlrSpRealiz = vVlrSpRealiz.add((nvl(vVlrServProf, BigDecimal.ZERO)).multiply(new BigDecimal(vQtdeRealizada)));
													vVlrSadtRealiz = vVlrSadtRealiz.add((nvl(vVlrSadt, BigDecimal.ZERO)).multiply(new BigDecimal(vQtdeRealizada)));
													vVlrAnestRealiz = vVlrAnestRealiz.add((nvl(vVlrAnest, BigDecimal.ZERO)).multiply(new BigDecimal(vQtdeRealizada)));
													vVlrProcedRealiz = BigDecimal.ZERO;
													
												} else {
													// Joga para perda o que excedou o limite.
													vPerda = (short) (vQtdeRealizada - vQtdeMax);

													logar("vPerda: {0}", vPerda);
													getFaturamentoInternacaoRN().rnCthcAtuInsPit(pCthSeq, rnFatcVerItprocexcVO.getPhoSeq(),
															rnFatcVerItprocexcVO.getSeq(), vCodSus, vPerda == null ? null : vPerda.longValue(), vQtdeRealizada == null ? null : vQtdeRealizada.longValue(),
															vVlrServHosp, vVlrServProf, vVlrSadt, vVlrProced, vVlrAnest,
															vPtosAnest, vPtosCirurgiao, vPtosSadt);
													
												}
												
												// Fim Marina 23/10/2013
											} else if (DominioCobrancaDiaria.D.equals(vCobrancaDiaria) &&
													Boolean.TRUE.equals(vCobraDias)) {
												// moti/situ saida pac cobra x nro dias da conta
												// soma nos valores totais do realizado:
												vVlrShRealiz = vVlrShRealiz.add((nvl(vVlrServHosp, BigDecimal.ZERO)).multiply(new BigDecimal(vDiasConta)));
												vVlrSpRealiz = vVlrSpRealiz.add((nvl(vVlrServProf, BigDecimal.ZERO)).multiply(new BigDecimal(vDiasConta)));
												vVlrSadtRealiz = vVlrSadtRealiz.add((nvl(vVlrSadt, BigDecimal.ZERO)).multiply(new BigDecimal(vDiasConta)));
												vVlrAnestRealiz = vVlrAnestRealiz.add((nvl(vVlrAnest, BigDecimal.ZERO)).multiply(new BigDecimal(vDiasConta)));
												vVlrProcedRealiz = BigDecimal.ZERO;
												
											} else {
												// soma nos valores totais do realizado:
												vVlrShRealiz = vVlrShRealiz.add((nvl(vVlrServHosp, BigDecimal.ZERO)).multiply(new BigDecimal(vDiariasConta)));
												vVlrSpRealiz = vVlrSpRealiz.add((nvl(vVlrServProf, BigDecimal.ZERO)).multiply(new BigDecimal(vDiariasConta)));
												vVlrSadtRealiz = vVlrSadtRealiz.add((nvl(vVlrSadt, BigDecimal.ZERO)).multiply(new BigDecimal(vDiariasConta)));
												vVlrAnestRealiz = vVlrAnestRealiz.add((nvl(vVlrAnest, BigDecimal.ZERO)).multiply(new BigDecimal(vDiariasConta)));
												vVlrProcedRealiz = BigDecimal.ZERO;
											}

										} else {
											logar("**  INICIO PORTARIA 420 **");
											
											// Marina 01/11/2010
											// Portaria 420
											
											logar("é hospital dia, verifica caracteristica");

											if (faturamentoRN.verificarCaracteristicaExame(rnFatcVerItprocexcVO.getSeq(), rnFatcVerItprocexcVO
													.getPhoSeq(), DominioFatTipoCaractItem.COBRA_DIARIA_POR_ITEM_LANCADO)) {

												logar("achou caracteristica, busca a quantidade lançada na conta e a quantidade máxima: {0} {1} {2}",
														(regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta.getProcedimentoHospitalarInternoRealizado().getSeq(): ""),
														vCodSusRealiz,
														pCthSeq);

												
												Short auxPhoSeq = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
												
												// -- Marina 29/08/2013 - chamado 104835
												DominioSituacaoItenConta[] arrayDominioSituacaoItemConta = { DominioSituacaoItenConta.C, DominioSituacaoItenConta.D };												
												
												// Ney 15/07/2011 Portaria 203 Fase 2
												Object[] qtdLancamento = fatItensProcedHospitalarDAO.buscaQtdeLanc(
																											auxPhoSeq,
																											pCthSeq,
																											arrayDominioSituacaoItemConta,
																											vCodSusRealiz,
																											regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta.getProcedimentoHospitalarInternoRealizado().getSeq() : null
																										    ,DateUtil.truncaData(regContaIni.getDtAltaAdministrativa())
																											);

												if (qtdLancamento != null) {
													vQtdeMax = (Short) qtdLancamento[0];
													vQtdeRealizada = (Short) qtdLancamento[1];
												} else {
													vQtdeMax = 0;
													vQtdeRealizada = 0; 
												}

												logar("v_qtde_max: {0}", vQtdeMax);
												logar("v_qtde_realizada: {0}", vQtdeRealizada);

												if (CoreUtil.menorOuIgual(vQtdeRealizada, vQtdeMax)) {
													vVlrShRealiz = vVlrShRealiz.add((nvl(vVlrServHosp, BigDecimal.ZERO)).multiply(new BigDecimal(vQtdeRealizada)));

													vVlrSpRealiz = vVlrSpRealiz.add((nvl(vVlrServProf, BigDecimal.ZERO)).multiply(new BigDecimal(vQtdeRealizada)));

													vVlrSadtRealiz = vVlrSadtRealiz.add((nvl(vVlrSadt, BigDecimal.ZERO)).multiply(new BigDecimal(vQtdeRealizada)));

													vVlrAnestRealiz = vVlrAnestRealiz.add((nvl(vVlrAnest, BigDecimal.ZERO)).multiply(new BigDecimal(vQtdeRealizada)));

													vVlrProcedRealiz = BigDecimal.ZERO;
												} else {
													// Joga para perda o que excedou o limite.
													vPerda = (short) (vQtdeRealizada - vQtdeMax);

													logar("vPerda: {0}", vPerda);
													getFaturamentoInternacaoRN().rnCthcAtuInsPit(pCthSeq, rnFatcVerItprocexcVO.getPhoSeq(),
															rnFatcVerItprocexcVO.getSeq(), vCodSus, vPerda == null ? null : vPerda.longValue(), vQtdeRealizada == null ? null : vQtdeRealizada.longValue(),
															vVlrServHosp, vVlrServProf, vVlrSadt, vVlrProced, vVlrAnest,
															vPtosAnest, vPtosCirurgiao, vPtosSadt);
												}
											} else {
												logar("não tem caracteristica, cai na situacao antiga");

												// Soma nos valores totais do realizado:
												BigDecimal qtdeItem = (rnFatcVerItprocexcVO.getQtdItem() != null ? new BigDecimal(rnFatcVerItprocexcVO.getQtdItem()) : BigDecimal.ZERO);
												vVlrShRealiz = vVlrShRealiz.add((nvl(vVlrServHosp, BigDecimal.ZERO)).multiply(qtdeItem));
												vVlrSpRealiz = vVlrSpRealiz.add((nvl(vVlrServProf, BigDecimal.ZERO)).multiply(qtdeItem));
												vVlrSadtRealiz = vVlrSadtRealiz.add((nvl(vVlrSadt, BigDecimal.ZERO)).multiply(qtdeItem));
												vVlrAnestRealiz = vVlrAnestRealiz.add((nvl(vVlrAnest, BigDecimal.ZERO)).multiply(qtdeItem));
												vVlrProcedRealiz = BigDecimal.ZERO;
											} // Fim marina - portaria 420
										} // Marina - 21/10/2013 - chamado: 113111

										logar("VLRS REALIZ: SH: {0} SADT: {1} PROC: {2} ANEST: {3} SP: {4}",
												vVlrShRealiz, vVlrSadtRealiz, vVlrProcedRealiz, vVlrAnestRealiz, vVlrSpRealiz);

										// Verifica hospital dia (cobranca diarias por qtd lancada)
										
										// ORADB: FATK_IPH_RN.RN_IPHC_VER_HOSP_DIA
										aux = fatItensProcedHospitalarDAO.obterPorChavePrimaria( new FatItensProcedHospitalarId(rnFatcVerItprocexcVO.getPhoSeq(),rnFatcVerItprocexcVO.getSeq()) );
										hospDia = (aux != null) ? aux.getHospDia() : Boolean.FALSE;
										
										if (Boolean.TRUE.equals(hospDia)) {
										
											// Calcula dias uteis do periodo da conta
											vDiasUteis = faturamentoFatkSusRN.obterQuantidadeDiasUteisEntreDatas(regConta
													.getDataInternacaoAdministrativa(), regConta.getDtAltaAdministrativa(),
													true);

											logar("dias uteis periodo da conta: {0}", vDiasUteis);

											// Verifica se qtd hospital dia e' maior que dias uteis da conta
											if (CoreUtil.menorOuIgual(rnFatcVerItprocexcVO.getQtdItem(), vDiasUteis)) {
												// Ja calculou valor do item usando a qtd 
												// lancada, entao muda qtd pra lancar o item
												// uma vez apenas
												rnFatcVerItprocexcVO.setQtdItem((short) 1);
											} else {
												List<FatItemContaHospitalar> auxListaItensContaHospitalar = fatItemContaHospitalarDAO
														.listarPorContaHospitalarProcedimentoHospitalarClassificacaoBrasileiraOcupacoes(
																regConta.getSeq(), regItem.getId().getPhiSeq(), vPhiTomo, phiSeqsTomografia);

												if (auxListaItensContaHospitalar != null
														&& !auxListaItensContaHospitalar.isEmpty()) {
													for (int j = 0; j < auxListaItensContaHospitalar.size(); j++) {
														regIchPhi = auxListaItensContaHospitalar.get(j);

														logar("insert msg hosp dia");
														FatMensagemLog fatMensagemLog = new FatMensagemLog();
														fatMensagemLog.setCodigo(57);

														faturamentoON
																.criarFatLogErrors(
																		"DIARIAS DE HOSPITAL DIA MAIOR QUE DIAS UTEIS DO PERIODO DA CONTA: "
																				+ vDiasUteis + ".",
																		INT,
																		regConta.getSeq(),
																		null,
																		null,
																		vCodSus,
																		null,
																		dataPrevia,
																		null,
																		regIchPhi != null ? regIchPhi.getId().getSeq() : null,
																		vPhoSolic,
																		vPhoRealiz,
																		vIphSolic,
																		vIphRealiz,
																		vPacCodigo,
																		regConta.getProcedimentoHospitalarInterno() != null ? regConta
																				.getProcedimentoHospitalarInterno().getSeq()
																				: null,
																		regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta
																				.getProcedimentoHospitalarInternoRealizado()
																				.getSeq()
																				: null, null, vPacProntuario, vCodSusRealiz,
																		vCodSusSolic, rnFatcVerItprocexcVO.getPhoSeq(), null, rnFatcVerItprocexcVO
																				.getSeq(), null, regIchPhi
																				.getProcedimentoHospitalarInterno().getSeq(),
																		null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);
													}
												}

												vConsistente = Boolean.FALSE;
											}
										}
									}

									// ---- INICIO DA INSERCAO DO ITEM:
									// ----
									// Busca proxima seq da tabela de espelho
									vEicSeqp = fatEspelhoItemContaHospDAO.buscaProximaSeqTabelaEspelho(regConta.getSeq());

									vIndEquipe = 0;
									vCbo = null;
									vCpfProf = null;
									
									logar(" MARINA TESTE 31/08/2012 ");
									logar(" V_MATRICULA_PROF: {0}", vMatriculaProf);

									Integer idRealiz =  regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta.getProcedimentoHospitalarInternoRealizado().getSeq() : null; 
									if (CoreUtil.igual(regItem.getId().getPhiSeq(), idRealiz)) {
										// o PHI do realizado
										vCpfProf = vCpfMedicoSolicRespons;

										// Busca dados de responsável e
										// anestesista na conta para CBO
										List<FatItemContaHospitalar> auxListaItensContaHospitalar = fatItemContaHospitalarDAO
												.listarPorContaHospitalarProcedimentoHospitalarClassificacaoBrasileiraOcupacoes(
														regConta.getSeq(),
														regItem.getId().getPhiSeq(), vPhiTomo,
														phiSeqsTomografia);

										if (auxListaItensContaHospitalar != null && !auxListaItensContaHospitalar.isEmpty()) {
											regIchPhiCbo = auxListaItensContaHospitalar.get(0);
										}

										// Busca dados de responsável e anestesista na conta para CBO
										logar("phi do realizado {0} phi {1} iph {2} {3}",
												regItem.getId().getPhiSeq(),
												regItem.getId().getPhiSeq(),
												rnFatcVerItprocexcVO.getPhoSeq(),
												rnFatcVerItprocexcVO.getSeq());
										
										logar(" resp {0} - {1} anest {2} - {3}",
												(regIchPhiCbo != null && regIchPhiCbo.getServidor() != null ? regIchPhiCbo.getServidor().getId().getVinCodigo() : null),
												(regIchPhiCbo != null && regIchPhiCbo.getServidor() != null ? regIchPhiCbo.getServidor().getId().getMatricula() : null),
												(regIchPhiCbo != null && regIchPhiCbo.getServidorAnest() != null ? regIchPhiCbo.getServidorAnest().getId().getVinCodigo() : null),
												(regIchPhiCbo != null && regIchPhiCbo.getServidorAnest() != null ? regIchPhiCbo.getServidorAnest().getId().getMatricula() : null));
												
										logar(" ----- Chama dados Prof --------- ");

										RnCthcBuscaDadosProfVO rnCthcBuscaDadosProfVO = 
											fatkCth6RN.rnCthcBuscaDadosProf(
													regConta.getSeq(), 
													regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta.getProcedimentoHospitalarInternoRealizado().getSeq() : null,
													vClinicaRealiz, 
													rnFatcVerItprocexcVO.getPhoSeq(), 
													rnFatcVerItprocexcVO.getSeq(), 
													(byte) 1,
													vMatriculaRespInt, 
													vVinculoRespInt,
													(regIchPhiCbo != null && regIchPhiCbo.getServidor() != null ? regIchPhiCbo.getServidor().getId().getVinCodigo() : null),
													(regIchPhiCbo != null && regIchPhiCbo.getServidor() != null ? regIchPhiCbo.getServidor().getId().getMatricula() : null), 
													(regIchPhiCbo != null && regIchPhiCbo.getServidorAnest() != null ? regIchPhiCbo.getServidorAnest().getId().getVinCodigo() : null), 
													(regIchPhiCbo != null && regIchPhiCbo.getServidorAnest() != null ? regIchPhiCbo.getServidorAnest().getId().getMatricula() : null)
													// Ney 26/08/2011 Cbo por competencia
													,dthrRealizado, nomeMicrocomputador, dataFimVinculoServidor);

										Boolean auxRetorno = null;
										if (rnCthcBuscaDadosProfVO != null) {
											auxRetorno = rnCthcBuscaDadosProfVO.getRetorno();
											vIndEquipe = rnCthcBuscaDadosProfVO.getEquipe();
											vCpfProf = rnCthcBuscaDadosProfVO.getCpfCns();
											vCbo = rnCthcBuscaDadosProfVO.getCbo();
											vMatriculaProf = rnCthcBuscaDadosProfVO.getMatriculaProf();
											vVinculoProf = rnCthcBuscaDadosProfVO.getVinculoProf();
										}

										if (CoreUtil.igual(Boolean.FALSE, auxRetorno)) {
											vErroCBO = Boolean.TRUE;
											logar("phi do realizado erro {0} iph {1} {2}",
													regItem.getId().getPhiSeq(),
													rnFatcVerItprocexcVO.getPhoSeq(),
													rnFatcVerItprocexcVO.getSeq());
											logar(" IPH passado para dados prof: {0}", rnFatcVerItprocexcVO.getSeq());
											logar(" PHO passado para dados prof: {0}", rnFatcVerItprocexcVO.getPhoSeq());
											logar(" Insert Fat_log IPH: {0}", vIphRealiz);
											logar(" Insert Fat_log PHO: {0}", vPhoRealiz);
											vPhoErro = null;
											vIphErro = null;
											
											FatMensagemLog fatMensagemLog = new FatMensagemLog();
											fatMensagemLog.setCodigo(151);

											faturamentoON.criarFatLogErrors(NAO_FOI_ENCONTRADO_CBO_MATR
													+ vMatriculaProf + VINC + vVinculoProf, INT, regConta.getSeq(), null,
													null, null, null, dataPrevia, null, null, null, vPhoRealiz, null,
													vIphRealiz, vPacCodigo, null, regConta
															.getProcedimentoHospitalarInternoRealizado() != null ? regConta
															.getProcedimentoHospitalarInternoRealizado().getSeq() : null,
													null, vPacProntuario, vCodSusRealiz, null, null, vPhoErro, null, vIphErro,
													null, null, RN_CTHC_ATU_ENC_PRV, null, null, fatMensagemLog);

											logar("Inconsistência cbo inseriu erro");

											if (!pPrevia) {
												// Se previa continua
												vConsistente = Boolean.FALSE;
												result = Boolean.FALSE;
												logar("Inconsistência cbo por aqui");
												logar("##### AGHU - ERRO AO ENCERRAR ##### Motivo: Inconsistência cbo por aqui (17)");
											}
										}
									} else {
										logar("------------------ELSE ------------------");
										logar("v_iph_pho_seq(v_indice): {0}", rnFatcVerItprocexcVO.getPhoSeq());
										logar("v_iph_seq(v_indice): {0}", rnFatcVerItprocexcVO.getSeq());

										if (fatkCth5RN.rnCthcObrigaCbo(rnFatcVerItprocexcVO.getPhoSeq(), rnFatcVerItprocexcVO.getSeq())) {
											logar("Busca CBO {0}", vCodSus);
											logar("reg_item.ser_matricula_resp {0}", regItem.getSerMatriculaResp());
											logar("reg_item.dthr_realizado {0}", dthrRealizado);
											
											RnCthcValidaCboRespVO rnCthcValidaCboRespVO = fatkCth6RN
													.rnCthcValidaCboResp(pCthSeq, regItem.getSerMatriculaResp(), regItem
															.getSerVinCodigoResp(), null, null, null, null, "C", rnFatcVerItprocexcVO
															.getPhoSeq(), rnFatcVerItprocexcVO.getSeq(), null
															// Ney 26/08/2011 Cbo por competencia
															,dthrRealizado, nomeMicrocomputador, dataFimVinculoServidor
													);

											Boolean auxRetorno = null;
											if (rnCthcValidaCboRespVO != null) {
												auxRetorno = rnCthcValidaCboRespVO.getRetorno();
												vCbo = rnCthcValidaCboRespVO.getCbo();
												vCpfProf = rnCthcValidaCboRespVO.getCpf();
												vMatriculaProf = rnCthcValidaCboRespVO.getMatriculaProf();
												vVinculoProf = rnCthcValidaCboRespVO.getVinculoProf();
											}

											if (CoreUtil.igual(Boolean.FALSE, auxRetorno)) {
												vCbo = null;
												vCpfProf = null;
											}

											logar("CBO {0} do PHI: {1} MATR: {2} - {3}",
													vCbo, 
													regItem.getId().getPhiSeq(), 
													regItem.getSerVinCodigoResp(), 
													regItem.getSerMatriculaResp());
											
											if (vCbo == null && regItem.getIseSoeSeq() != null) {
												rnCthcValidaCboRespVO = fatkCth6RN.rnCthcValidaCboResp(pCthSeq, null,
														null, regItem.getIseSoeSeq(), regItem.getIseSeqp(), null, null, "C",
														rnFatcVerItprocexcVO.getPhoSeq(), rnFatcVerItprocexcVO.getSeq(), null
														// Ney 26/08/2011 Cbo por competencia
														,dthrRealizado, nomeMicrocomputador, dataFimVinculoServidor	
												);

												auxRetorno = null;
												if (rnCthcValidaCboRespVO != null) {
													auxRetorno = rnCthcValidaCboRespVO.getRetorno();
													vCbo = rnCthcValidaCboRespVO.getCbo();
													vCpfProf = rnCthcValidaCboRespVO.getCpf();
													vMatriculaProf = rnCthcValidaCboRespVO.getMatriculaProf();
													vVinculoProf = rnCthcValidaCboRespVO.getVinculoProf();
												}

												if (CoreUtil.igual(Boolean.FALSE, auxRetorno)) {
													vCbo = null;
													vCpfProf = null;
												}
												logar("CBO do Exame {0} - {1} do PHI: {2} solic: {3} - {4}",
														vCbo,
														vCpfProf,
														regItem.getId().getPhiSeq(),
														regItem.getIseSoeSeq(),
														regItem.getIseSeqp());
											}

											if (vCbo == null) {
												// Busca chave cirurgia
												FatItemContaHospitalar auxItensContaHospitalar = fatItemContaHospitalarDAO.buscarItemContaHospitalarChaveCirurgia(regConta.getSeq(), regItem.getId().getPhiSeq());
												if (auxItensContaHospitalar != null) {
													vChaveCirg = auxItensContaHospitalar.getCirurgia() != null ? auxItensContaHospitalar.getCirurgia().getSeq() : null;
												}

												if (vChaveCirg != null) {
													rnCthcValidaCboRespVO = fatkCth6RN.rnCthcValidaCboResp(pCthSeq,
															null, null, null, null, null, vChaveCirg, "C", rnFatcVerItprocexcVO
																	.getPhoSeq(), rnFatcVerItprocexcVO.getSeq(), null
																	// Ney 26/08/2011 Cbo por competencia
																	,dthrRealizado, nomeMicrocomputador, dataFimVinculoServidor
													);

													auxRetorno = null;
													if (rnCthcValidaCboRespVO != null) {
														auxRetorno = rnCthcValidaCboRespVO.getRetorno();
														vCbo = rnCthcValidaCboRespVO.getCbo();
														vCpfProf = rnCthcValidaCboRespVO.getCpf();
														vMatriculaProf = rnCthcValidaCboRespVO.getMatriculaProf();
														vVinculoProf = rnCthcValidaCboRespVO.getVinculoProf();
													}

													if (CoreUtil.igual(Boolean.FALSE, auxRetorno)) {
														vCbo = null;
														vCpfProf = null;
													}
												}
											}

											if (vCbo == null) {
												List<FatItemContaHospitalar> auxListaItensContaHospitalar = fatItemContaHospitalarDAO
														.listarPorContaHospitalarProcedimentoHospitalarClassificacaoBrasileiraOcupacoes(
																regConta.getSeq(), regItem.getId().getPhiSeq(), vPhiTomo, phiSeqsTomografia);

												if (auxListaItensContaHospitalar != null
														&& !auxListaItensContaHospitalar.isEmpty()) {
													regIchPhiCbo = auxListaItensContaHospitalar.get(0);
												}

												if (regIchPhiCbo != null && regIchPhiCbo.getServidorAnest() != null) {
													rnCthcValidaCboRespVO = fatkCth6RN.rnCthcValidaCboResp(pCthSeq,
															regIchPhiCbo.getServidorAnest().getId().getMatricula(),
															regIchPhiCbo.getServidorAnest().getId().getVinCodigo(), null,
															null, null, null, "C", rnFatcVerItprocexcVO.getPhoSeq(),
															rnFatcVerItprocexcVO.getSeq(), null
															// Ney 26/08/2011 Cbo por competencia
															,dthrRealizado, nomeMicrocomputador, dataFimVinculoServidor
													);

													auxRetorno = null;
													if (rnCthcValidaCboRespVO != null) {
														auxRetorno = rnCthcValidaCboRespVO.getRetorno();
														vCbo = rnCthcValidaCboRespVO.getCbo();
														vCpfProf = rnCthcValidaCboRespVO.getCpf();
														vMatriculaProf = rnCthcValidaCboRespVO.getMatriculaProf();
														vVinculoProf = rnCthcValidaCboRespVO.getVinculoProf();
													}

													if (CoreUtil.igual(Boolean.FALSE, auxRetorno)) {
														vCbo = null;
														vCpfProf = null;
													}
												}
											}

											if (vCbo == null) {
												// Marina 03/05/2011
												// Para determinados procedimentos, não deve ser validado o CBO do médico
												logar("Testa caracteristica");
												
												if(!faturamentoRN.verificarCaracteristicaExame(rnFatcVerItprocexcVO.getSeq(),
														rnFatcVerItprocexcVO.getPhoSeq(), DominioFatTipoCaractItem.NAO_CONSISTE_CBO)){
													// Inicio Eliane
													FatMensagemLog fatMensagemLog = new FatMensagemLog();
													fatMensagemLog.setCodigo(151);
													
													faturamentoON.criarFatLogErrors(
															NAO_FOI_ENCONTRADO_CBO+" -  MATR: " + vMatriculaProf + VINC + vVinculoProf,
															INT,
															regConta.getSeq(),
															null,
															null,
															vCodSus,
															null,
															dataPrevia,
															null,
															null,
															null,
															vPhoRealiz,
															null,
															vIphRealiz,
															vPacCodigo,
															regItem.getId().getPhiSeq(),
															regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta
																	.getProcedimentoHospitalarInternoRealizado().getSeq()
																	: null, null, vPacProntuario, vCodSusRealiz, null,
																	rnFatcVerItprocexcVO.getPhoSeq(), null, rnFatcVerItprocexcVO.getSeq(), null, null,
																	null, RN_CTHC_ATU_ENC_PRV, null, null, fatMensagemLog);
													
													if (!pPrevia) {
														// Se previa continua
														vConsistente = Boolean.FALSE;
														result = Boolean.FALSE;
														logar(AGHU_ERRO_AO_ENCERRAR_MOTIVO + NAO_FOI_ENCONTRADO_CBO+" -  MATR: " + vMatriculaProf + VINC + vVinculoProf + " (18)");
													}
												}
											}
										}
									}

									if (faturamentoRN.verificarCaracteristicaExame(rnFatcVerItprocexcVO.getSeq(),
											rnFatcVerItprocexcVO.getPhoSeq(), DominioFatTipoCaractItem.POSSUI_INDICADOR_DE_EQUIPE)) {
										vIndEquipe = 1;
									}

									try {
										
										logar("insert 2 into fat_espelhos_itens_conta_hosp:");
										logar("v_cod_sus:{0} v_iph_qtd_item={1}",vCodSus,vIphQtdItem);
										logar(" **************    2  ***************");
										logar(" v_tao_cod_sus  : {0}",vTaoCodSus);
										logar(" v_cbo  : {0}",vCbo);
										logar(" V_IND_EQUIPE : {0}",vIndEquipe);
										logar(" v_cod_sus : {0}",vCodSus);
										logar(" v_qtd_atos_obrig : {0}",vQtdAtosObrig);
										logar(" v_iph_qtd_item(v_indice) : {0}", vIphQtdItem);
										logar(" ************************************");
										logar(" v_nota_fiscal={0} v_cgc_nf={1}", vNotaFiscal, vCgcNf);
										
										BigDecimal auxMultiplicando = rnFatcVerItprocexcVO.getQtdItem() != null ? new BigDecimal(
												rnFatcVerItprocexcVO.getQtdItem()) : BigDecimal.ZERO;

										FatEspelhoItemContaHosp fatEspelhoItemContaHosp = new FatEspelhoItemContaHosp();

										fatEspelhoItemContaHosp.setId(new FatEspelhoItemContaHospId(regConta.getSeq(),
												vEicSeqp));
										fatEspelhoItemContaHosp.setIchSeq(regIchPhiCbo != null ? regIchPhiCbo.getId().getSeq(): null);
										fatEspelhoItemContaHosp.setItemProcedimentoHospitalar(fatItensProcedHospitalarDAO
												.obterPorChavePrimaria(new FatItensProcedHospitalarId(rnFatcVerItprocexcVO.getPhoSeq(),
														rnFatcVerItprocexcVO.getSeq())));
										
										fatEspelhoItemContaHosp.setTivSeq(vTivCodSus != null ? vTivCodSus.byteValue() : null);
										fatEspelhoItemContaHosp.setTaoSeq(vTaoCodSus);
										fatEspelhoItemContaHosp.setQuantidade(rnFatcVerItprocexcVO.getQtdItem());
										fatEspelhoItemContaHosp.setNotaFiscal(vNotaFiscal);
										fatEspelhoItemContaHosp.setPontosAnestesista(auxMultiplicando.intValue() * (vPtosAnest != null ? vPtosAnest.intValue() : 0));
										fatEspelhoItemContaHosp.setPontosCirurgiao(auxMultiplicando.intValue() * (vPtosCirurgiao != null ? vPtosCirurgiao.intValue() : 0));
										fatEspelhoItemContaHosp.setPontosSadt(auxMultiplicando.intValue() * (vPtosSadt != null ? vPtosSadt.intValue() : 0));
										fatEspelhoItemContaHosp.setProcedimentoHospitalarSus(vCodSus);
										fatEspelhoItemContaHosp.setIndConsistente(vConsistente);
										fatEspelhoItemContaHosp.setIndModoCobranca(vModoCobranca);
										fatEspelhoItemContaHosp.setValorAnestesista((vVlrAnest != null ? vVlrAnest : BigDecimal.ZERO).multiply(auxMultiplicando));
										fatEspelhoItemContaHosp.setValorProcedimento((vVlrProced != null ? vVlrProced : BigDecimal.ZERO).multiply(auxMultiplicando));
										fatEspelhoItemContaHosp.setValorSadt((vVlrSadt != null ? vVlrSadt : BigDecimal.ZERO).multiply(auxMultiplicando));
										fatEspelhoItemContaHosp.setValorServHosp((vVlrServHosp != null ? vVlrServHosp : BigDecimal.ZERO).multiply(auxMultiplicando));
										fatEspelhoItemContaHosp.setValorServProf((vVlrServProf != null ? vVlrServProf : BigDecimal.ZERO).multiply(auxMultiplicando));
										fatEspelhoItemContaHosp.setDataPrevia(dataPrevia);
										fatEspelhoItemContaHosp.setIndTipoItem(vTipoItem);
										fatEspelhoItemContaHosp.setIndGeradoEncerramento(false);
										fatEspelhoItemContaHosp.setCgc(vCgcNf);
										fatEspelhoItemContaHosp.setIndEquipe(vCbo == null ? null : vIndEquipe);
										fatEspelhoItemContaHosp.setCbo(vCbo);
										fatEspelhoItemContaHosp.setCpfCns(vCpfProf);
										fatEspelhoItemContaHosp.setSerieOpm(vSerie);
										fatEspelhoItemContaHosp.setLoteOpm(vLote);
										fatEspelhoItemContaHosp.setRegAnvisaOpm(vRegAnvisa);
										fatEspelhoItemContaHosp.setCnpjRegAnvisa(vCnpjRegAnvisa);
										
										// Ney, em 06/06/2010 Portaria 203
										fatEspelhoItemContaHosp.setCompetenciaUti(DateUtil.obterDataFormatada(dthrRealizado, "yyyyMM"));

										//faturamentoRN.inserirFatEspelhoItemContaHosp(fatEspelhoItemContaHosp, true);
										getFatEspelhoItemContaHospDAO().persistir(fatEspelhoItemContaHosp);
										getFatEspelhoItemContaHospDAO().flush();
										//faturamentoFacade.evict(fatEspelhoItemContaHosp);
										logar("- V_CONSISTENTE: {0}", vConsistente);
										if (vConsistente) {
											encerrar = Boolean.TRUE;
										}
									} catch (Exception e) {
										vMsg = e.getMessage();
										logar("ERRO NO INSERT EM fat_espelhos_itens_conta_hosp {0}", vMsg);

										List<FatItemContaHospitalar> auxListaItensContaHospitalar = fatItemContaHospitalarDAO
												.listarPorContaHospitalarProcedimentoHospitalarClassificacaoBrasileiraOcupacoes(
														regConta.getSeq(),
														regItem.getId().getPhiSeq(), vPhiTomo,
														phiSeqsTomografia);

										if (auxListaItensContaHospitalar != null && !auxListaItensContaHospitalar.isEmpty()) {
											for (int j = 0; j < auxListaItensContaHospitalar.size(); j++) {
												regIchPhi = auxListaItensContaHospitalar.get(j);
												FatMensagemLog fatMensagemLog = new FatMensagemLog();
												fatMensagemLog.setCodigo(68);
												faturamentoON
														.criarFatLogErrors(
																"ERRO AO INSERIR ESPELHO ITEM CONTA HOSPITALAR: " + vMsg,
																INT,
																regConta.getSeq(),
																null,
																null,
																vCodSus,
																null,
																dataPrevia,
																null,
																regIchPhi.getId().getSeq(),
																vPhoSolic,
																vPhoRealiz,
																vIphSolic,
																vIphRealiz,
																vPacCodigo,
																regConta.getProcedimentoHospitalarInterno() != null ? regConta
																		.getProcedimentoHospitalarInterno().getSeq() : null,
																regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta
																		.getProcedimentoHospitalarInternoRealizado().getSeq()
																		: null, null, vPacProntuario, vCodSusRealiz,
																vCodSusSolic, rnFatcVerItprocexcVO.getPhoSeq(), null, rnFatcVerItprocexcVO
																		.getSeq(), null, regIchPhi
																		.getProcedimentoHospitalarInterno().getSeq(), null,
																RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);
											}
										}

										vConsistente = Boolean.FALSE;

										// Desfaz a insercao dos
										// atos
										// orbrigatorios que foram
										// incluidos em funcao do
										// item
										// que agora esta
										// inconsistente
										logar("rollback obrig 2");
									}
									// ---- FIM DA INSERCAO DO ITEM.
									// ----
								} else {
									// Nao conseguiu inserir atos
									// obrigatorios do item

									List<FatItemContaHospitalar> auxListaItensContaHospitalar = fatItemContaHospitalarDAO
											.listarPorContaHospitalarProcedimentoHospitalarClassificacaoBrasileiraOcupacoes(
													regConta.getSeq(), regItem.getId().getPhiSeq(),
													vPhiTomo, phiSeqsTomografia);

									if (auxListaItensContaHospitalar != null && !auxListaItensContaHospitalar.isEmpty()) {
										for (int j = 0; j < auxListaItensContaHospitalar.size(); j++) {
											regIchPhi = auxListaItensContaHospitalar.get(j);
											FatMensagemLog fatMensagemLog = new FatMensagemLog();
											fatMensagemLog.setCodigo(185);
											faturamentoON.criarFatLogErrors(
													"NAO FOI POSSIVEL LANCAR OS ATOS OBRIGATORIOS DO ITEM.", INT, regConta
															.getSeq(), null, null, vCodSus, null, dataPrevia, null, regIchPhi
															.getId().getSeq(), vPhoSolic, vPhoRealiz, vIphSolic, vIphRealiz,
													vPacCodigo, regConta.getProcedimentoHospitalarInterno() != null ? regConta
															.getProcedimentoHospitalarInterno().getSeq() : null, regConta
															.getProcedimentoHospitalarInternoRealizado() != null ? regConta
															.getProcedimentoHospitalarInternoRealizado().getSeq() : null,
													null, vPacProntuario, vCodSusRealiz, vCodSusSolic, rnFatcVerItprocexcVO.getPhoSeq(),
													null, rnFatcVerItprocexcVO.getSeq(), null, regIchPhi
															.getProcedimentoHospitalarInterno().getSeq(), null,
													RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);
										}
									}

									vConsistente = Boolean.FALSE;
								}

								if (!vConsistente) {
									getFaturamentoInternacaoRN().rnCthcAtuInsPit(regConta.getSeq(), rnFatcVerItprocexcVO.getPhoSeq(), rnFatcVerItprocexcVO
											.getSeq(), vCodSus, rnFatcVerItprocexcVO.getQtdItem() == null ? null : rnFatcVerItprocexcVO.getQtdItem().longValue(), rnFatcVerItprocexcVO.getQtdItem() == null ? null : rnFatcVerItprocexcVO.getQtdItem().longValue(),
											vVlrServHosp, vVlrServProf, vVlrSadt, vVlrProced, vVlrAnest, vPtosAnest,
											vPtosCirurgiao, vPtosSadt);
								}

								if (!pPrevia && !vConsistente) {
									break;
								}
								// FIM DA VERIFICACAO DOS ATOS OBRIGATORIOS DO ITEM ----
							}

							if ((vQtdItens != null && CoreUtil.igual(vQtdItens, 0)) || (vQtdItens != null && CoreUtil.igual(vQtdItens, vIndice)) || (!pPrevia && !vConsistente) || (!pPrevia && !result)) {
								break;
							}

							vIndice++;

							// ---- FIM DO PROCESSAMENTO DO ITEM. ----
						} while (vIndice < vQtdItens.intValue());

						logar("reg_item.phi_seq  {0}", regItem.getId().getPhiSeq());
						logar("reg_conta.phi_seq_realizado {0}", regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta.getProcedimentoHospitalarInternoRealizado().getSeq() : null);
						logar("v_consistente  {0}", vConsistente);
						
						
						// Verifica se o item representa o procedimento realizado
						if (!vConsistente
								&& CoreUtil.igual(regItem.getId().getPhiSeq(), 
										(regConta.getProcedimentoHospitalarInternoRealizado()!=null?regConta.getProcedimentoHospitalarInternoRealizado().getSeq():null))) {
							
							// Se o realizado estiver inconsistente nao pode encerrar a conta
							logar("------------------------realizado nao consistente!!!");
							FatMensagemLog fatMensagemLog= new FatMensagemLog();
							fatMensagemLog.setCodigo(100);
							faturamentoON.criarFatLogErrors(
									"ITEM CORRESPONDENTE AO PROCEDIMENTO REALIZADO DA CONTA ESTA INCONSISTENTE.", INT, 
									regConta.getSeq(), 
									null, 
									null, 
									vCodSus, 
									null, 
									dataPrevia, 
									null, 
									(regIchPhi != null ? regIchPhi.getId().getSeq() : null),
									vPhoSolic, 
									vPhoRealiz, 
									vIphSolic, 
									vIphRealiz, 
									vPacCodigo, 
									(regConta.getProcedimentoHospitalarInterno() != null ? regConta.getProcedimentoHospitalarInterno().getSeq() : null), 
									(regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta.getProcedimentoHospitalarInternoRealizado().getSeq() : null), 
									null, 
									vPacProntuario,
									vCodSusRealiz, 
									vCodSusSolic, 
									(rnFatcVerItprocexcVO != null ? rnFatcVerItprocexcVO.getPhoSeq() : null),
									null,
									(rnFatcVerItprocexcVO != null ? rnFatcVerItprocexcVO.getSeq() : null), 
									null, 
									(regIchPhi != null && regIchPhi.getProcedimentoHospitalarInterno() != null ? regIchPhi.getProcedimentoHospitalarInterno().getSeq() : null), 
									null, 
									"RN_CTHP_ATU_GERA_ESP", 
									null, 
									null,fatMensagemLog);

							result = Boolean.FALSE;
							logar(AGHU_ERRO_AO_ENCERRAR_MOTIVO + "ITEM CORRESPONDENTE AO PROCEDIMENTO REALIZADO DA CONTA ESTA INCONSISTENTE." + " (19)");
						}

						// Se nao houve erros na conta
						if (result) {
							// Atualiza situacao do item conta hospitalar
							if (!pPrevia) {
								if (encerrar) {
									if (CoreUtil.igual(DominioModoCobranca.V, vModoCobranca) || CoreUtil.igual(DominioModoCobranca.P, vModoCobranca)) {
										List<FatItemContaHospitalar> auxListaItensContaHospitalar = fatItemContaHospitalarDAO
												.listarPorContaHospitalarProcedimentoHospitalarClassificacaoBrasileiraOcupacoes(
														regConta.getSeq(), regItem.getId().getPhiSeq(),
														vPhiTomo, phiSeqsTomografia);

										if (auxListaItensContaHospitalar != null && !auxListaItensContaHospitalar.isEmpty()) {
											for (int j = 0; j < auxListaItensContaHospitalar.size(); j++) {
												regIchPhi = auxListaItensContaHospitalar.get(j);

												DominioSituacaoItenConta auxDominioSituacaoItenConta = null;
												if (vModoCobranca != null) {
													for (DominioSituacaoItenConta dominioSituacaoItenConta : DominioSituacaoItenConta
															.values()) {
														if ((vModoCobranca.toString().equalsIgnoreCase(dominioSituacaoItenConta
																.toString()))) {
															auxDominioSituacaoItenConta = dominioSituacaoItenConta;
															break;
														}
													}
												}

												faturamentoFatkIchRN.rnIchpAtuSituacao(pPrevia, auxDominioSituacaoItenConta, regConta
														.getSeq(), regIchPhi.getId().getSeq(), dataFimVinculoServidor);
												logar("cancelou item lin 3141{0} {1}", regIchPhi.getId().getSeq(), vModoCobranca);
											}
										}
									}
								} else {
									List<FatItemContaHospitalar> auxListaItensContaHospitalar = fatItemContaHospitalarDAO
											.listarPorContaHospitalarProcedimentoHospitalarClassificacaoBrasileiraOcupacoes(regConta
													.getSeq(), regItem.getId().getPhiSeq(), vPhiTomo,
													phiSeqsTomografia);

									if (auxListaItensContaHospitalar != null && !auxListaItensContaHospitalar.isEmpty()) {
										for (int j = 0; j < auxListaItensContaHospitalar.size(); j++) {
											regIchPhi = auxListaItensContaHospitalar.get(j);

											faturamentoFatkIchRN.rnIchpAtuSituacao(pPrevia, DominioSituacaoItenConta.N, regConta
													.getSeq(), regIchPhi.getId().getSeq(), dataFimVinculoServidor);

											logar("cancelou item lin 3151{0}", regIchPhi.getId().getSeq());
										}
									}
								}
							}							
						}
						
						if (vConsistente) {
							if (CoreUtil.igual(DominioModoCobranca.V, vModoCobranca)) {
								logar(">- ITEM DA CONTA DEVE SER ENCERRADO POR VALOR");
							} else if (CoreUtil.igual(DominioModoCobranca.P, vModoCobranca)) {
								logar(">- ITEM DA CONTA DEVE SER ENCERRADO POR PONTOS");
							} 
						} else if (!vConsistente) {
							logar(">- ITEM DA CONTA DEVE SER CANCELADO");
						} 
						if (!result) {
							// Se houve erro na conta
							break;
						}
					}
				}
				
				// Busca senha CERIH
				vSenhaCerih = faturamentoRN.buscaSenhaCerih(regContaIni.getSeq());

				logar("v_senha_cerih: {0}", vSenhaCerih);

				// Se nao houve erros na conta
				if (result) {
					// Valida demais regras da conta que dependem dos itens lancados
					// Lanca Atendim.RN em sala de parto
					if (!fatkCthRN.rnCthcAtuRegrapos(pPrevia, dataPrevia, regConta.getSeq(), vPacCodigo, vPacProntuario,
							vIntSeq, regConta.getProcedimentoHospitalarInterno() != null ? regConta.getProcedimentoHospitalarInterno()
									.getSeq() : null, vPhoSolic, vIphSolic, vCodSusSolic, regConta
									.getProcedimentoHospitalarInternoRealizado() != null ? regConta
									.getProcedimentoHospitalarInternoRealizado().getSeq() : null, vPhoRealiz, vIphRealiz,
							vCodSusRealiz
							// Ney 01/09/2011
							, dthrRealizado , fatVariaveisVO
							)) {
						
						contaDepoisOk = Boolean.FALSE;
					}

					if (contaDepoisOk || pPrevia) {
						// Verifica cirurgia multipla, politraumatizado, AIDS, Busca Doador, 
						// Permanencia Maior, permanencia menor:
						RnCthcVerRegraespVO rnCthcVerRegraespVO = fatkCthRN.rnCthcVerRegraesp(pPrevia, dataPrevia, regConta
								.getSeq(), vPacCodigo, vPacProntuario, vIntSeq,
								regConta.getProcedimentoHospitalarInterno() != null ? regConta.getProcedimentoHospitalarInterno()
										.getSeq() : null, vPhoSolic, vIphSolic, vCodSusSolic, regConta
										.getProcedimentoHospitalarInternoRealizado() != null ? regConta
										.getProcedimentoHospitalarInternoRealizado().getSeq() : null, vPhoRealiz, vIphRealiz,
								vCodSusRealiz, vDiariasConta, vDiasUtiTotal, vMotivoCobranca.toString(), 
								vCodExclusaoCritica, nomeMicrocomputador, dataFimVinculoServidor);
						Boolean auxRetorno = null;
						if (rnCthcVerRegraespVO != null) {
							auxRetorno = rnCthcVerRegraespVO.getRetorno();
							vCodExclusaoCritica = rnCthcVerRegraespVO.getCodExclusaoCritica();
						}

						if (CoreUtil.igual(Boolean.FALSE, auxRetorno)) {
							contaDepoisOk = Boolean.FALSE;
						}
					}

					/*
					Marina 25/08/2014 - a pedido da Eliane
					Não precisamos mais deste código. 
					Na tabela anterior a dispensação do sangue exigia 2 lançamentos. Agora a associação é direta.
					if (contaDepoisOk || pPrevia) {
						// Verifica e atualiza qtd de modulos transfusionais
						// (94020019) em funcao das quantidades de
						// hemoterapias lancadas/cobraveis
						if (!fatkCthRN.rnCthcAtuQtdmodul(pPrevia, dataPrevia, regConta.getSeq(), vPacCodigo, vPacProntuario,
								vIntSeq, regConta.getProcedimentoHospitalarInterno() != null ? regConta
										.getProcedimentoHospitalarInterno().getSeq() : null, vPhoSolic, vIphSolic, vCodSusSolic,
								regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta
										.getProcedimentoHospitalarInternoRealizado().getSeq() : null, vPhoRealiz, vIphRealiz,
								vCodSusRealiz)) {
							logar("falso");
							contaDepoisOk = Boolean.FALSE;
						}
					} */

					FatContasHospitalares auxContaHospitalar = fatContasHospitalaresDAO.obterPorChavePrimaria(pCthSeq);

					if (auxContaHospitalar != null) {
						vExclusaoCritica = auxContaHospitalar.getExclusaoCritica() != null ? auxContaHospitalar.getExclusaoCritica()
								.getCodigo() : null;
					}

					// A informação do Código da Central passará a ser grava
					// no espelho da conta para que o Gilberto e a Ruth
					// possam ver essa informação no relatório do espelho da conta.
					vPhiCateterismo = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_CAA_CONSULTA);
					
					Short cnvCodigo = (short) 1;
					vCodCentral = fatContasHospitalaresDAO.buscaCodigoCentral(pCthSeq, cnvCodigo, DominioIndAbsenteismo.R);

					logar("Cód Central:{0}", vCodCentral);

					// ORADB FATK_CTH2_RN_UN.C_AIH5
					FatItensProcedHospitalar aux = fatItensProcedHospitalarDAO.obterPorChavePrimaria( new FatItensProcedHospitalarId(vPhoRealiz,vIphRealiz) );
					vRealizAih5 = (aux != null) ? aux.getTipoAih5() : Boolean.FALSE;
					
					if (Boolean.TRUE.equals(contaDepoisOk)) {
						// Gera espelho aih
						logar("VAI GERAR 1o. ESPELHO AIH {0} AIH5 {1}",
								regConta.getNroSeqaih5(), (vRealizAih5 == null ? "" : vRealizAih5));
						logar("v_exclusao_critica {0}", vExclusaoCritica);
						logar(" v_nro_aih_posterior {0}", vNroAihPosterior);
						logar("v_nro_aih_anterior {0}", vNroAihAnterior);
						vEaiSeqp = 1;
						try {
							String auxPacCor = null;
							
							FatEspelhoAih fatEspelhoAih = new FatEspelhoAih();
							
							fatEspelhoAih.setId(new FatEspelhoAihId(regConta.getSeq(), vEaiSeqp));
							
							if(regPac != null){
								
								if  (DominioCor.B.equals(regPac.getCor())) {
									auxPacCor = String.valueOf(DominioCor.B.getCodigo());
								} else if (DominioCor.P.equals(regPac.getCor())) {
									auxPacCor = String.valueOf(DominioCor.P.getCodigo());
								} else if (DominioCor.M.equals(regPac.getCor())) {
									auxPacCor = String.valueOf(DominioCor.M.getCodigo());
								} else if (DominioCor.A.equals(regPac.getCor())) {
									auxPacCor = String.valueOf(DominioCor.A.getCodigo());
								} else if (DominioCor.I.equals(regPac.getCor())) {
									auxPacCor = String.valueOf(DominioCor.I.getCodigo());
								} else {
									auxPacCor = String.valueOf(DominioCor.O.getCodigo());
								}
								
								fatEspelhoAih.setPacNome(StringUtils.substring(regPac.getNome(), 0,60));
								fatEspelhoAih.setPacDtNascimento(regPac.getDtNascimento());

								if(regPac.getSexo() != null){
									fatEspelhoAih.setPacSexo(CoreUtil.igual(DominioSexo.F, regPac.getSexo()) ? "3" : "1");
								}

								fatEspelhoAih.setGrauInstrucaoPac(regPac.getGrauInstrucao());

								// Marina07/11/2013 - Melhoria AGHU 31577
								fatEspelhoAih.setPacRG(regPac.getRg());

								if(regPac.getAipNacionalidades() != null){
									fatEspelhoAih.setNacionalidadePac(regPac.getAipNacionalidades().getCodigo().shortValue());
								}

								fatEspelhoAih.setPacNomeMae(regPac.getNomeMae());
							}

							if(regPacEnder != null){
								if(regPacEnder.getTipCodigo() != null){
									fatEspelhoAih.setEndTipCodigo(regPacEnder.getTipCodigo().shortValue());
								} else {
									fatEspelhoAih.setEndTipCodigo(CODIGO_TIPO_LOGRADOUROS);
								}
	

								fatEspelhoAih.setEndBairroPac(StringUtils.substring(regPacEnder.getBairro(), 0, 30));
								fatEspelhoAih.setEndLogradouroPac(StringUtils.substring(regPacEnder.getLogradouro(), 0, 25));
								fatEspelhoAih.setEndNroLogradouroPac(regPacEnder.getNroLogradouro());
								fatEspelhoAih.setEndCmplLogradouroPac(StringUtils.substring(regPacEnder.getComplLogradouro(), 0, 15));
								fatEspelhoAih.setEndCidadePac(StringUtils.substring(regPacEnder.getCidade(), 0, 20));
								fatEspelhoAih.setEndUfPac(regPacEnder.getUf());

								if(regPacEnder.getCep() != null){
									fatEspelhoAih.setEndCepPac(regPacEnder.getCep().intValue());
								}

								if(regPacEnder.getCodIbge() != null){
									fatEspelhoAih.setCodIbgeCidadePac(regPacEnder.getCodIbge().intValue());
								}
							}
							
							fatEspelhoAih.setContaHospitalar(regConta);
							fatEspelhoAih.setNroDiasMesInicialUti(CoreUtil.igual(DominioTipoAltaUTI.POSITIVO_1, vTipoUti) ? vDiasMesIniUti : 0);
							fatEspelhoAih.setNroDiasMesAnteriorUti(CoreUtil.igual(DominioTipoAltaUTI.POSITIVO_1, vTipoUti) ? vDiasMesAntUti : 0);
							fatEspelhoAih.setNroDiasMesAltaUti(CoreUtil.igual(DominioTipoAltaUTI.POSITIVO_1, vTipoUti) ? vDiasMesAltaUti : 0);
							fatEspelhoAih.setNroDiariasAcompanhante((byte) (CoreUtil.igual(vDiariasAcomp, 0) ? 0 : regConta.getDiariasAcompanhante()));
							fatEspelhoAih.setTahSeq(CoreUtil.igual(Boolean.TRUE, vRealizAih5) ? (regConta.getNroSeqaih5() == null ? vTahCodSus: Short.valueOf("5")) : vTahCodSus);
							fatEspelhoAih.setDataPrevia(dataPrevia);
							fatEspelhoAih.setDciCodigoDcih(regConta.getDocumentoCobrancaAih() != null ? regConta.getDocumentoCobrancaAih().getCodigoDcih() : null);
							
							if(regDci != null && regDci.getFatCompetencia()!= null){
								fatEspelhoAih.setDciCpeDtHrInicio(regDci.getFatCompetencia().getId().getDtHrInicio());
								fatEspelhoAih.setDciCpeModulo(regDci.getFatCompetencia().getId().getModulo());
								fatEspelhoAih.setDciCpeMes(regDci.getFatCompetencia().getId().getMes().byteValue());
								fatEspelhoAih.setDciCpeAno(regDci.getFatCompetencia().getId().getAno().shortValue());
								fatEspelhoAih.setEspecialidadeDcih(regDci.getClcCodigo());
							}							
							fatEspelhoAih.setPacProntuario(vPacProntuario);
							fatEspelhoAih.setNumeroAih(regConta.getAih() != null ? regConta.getAih().getNroAih() : null);
							fatEspelhoAih.setEnfermaria(vEnfermaria);
							fatEspelhoAih.setLeito(vLeito);
							if (regRespPac != null) {
								fatEspelhoAih.setNomeResponsavelPac(regRespPac.getNome() != null ? regRespPac.getNome() : (regPac != null ? regPac.getNome() : null));
							}

							if(StringUtils.isNotEmpty(vDocumentoPac) && CoreUtil.isNumeroInteger(vDocumentoPac)){
								if(vDocumentoPac.length() > 11){
									fatEspelhoAih.setPacCpf(Long.valueOf(vDocumentoPac.substring(0, 11)));
								} else {
									fatEspelhoAih.setPacCpf(Long.valueOf(vDocumentoPac));
								}

								fatEspelhoAih.setPacNroCartaoSaude(new BigInteger(vDocumentoPac));
							}
							
							fatEspelhoAih.setCpfMedicoSolicRespons(vCpfMedicoSolicRespons);
							fatEspelhoAih.setIphPhoSeqSolic(vPhoSolic);
							fatEspelhoAih.setIphSeqSolic(vIphSolic);
							fatEspelhoAih.setIphCodSusSolic(vCodSusSolic);
							fatEspelhoAih.setIphPhoSeqRealiz(vPhoRealiz);
							fatEspelhoAih.setIphSeqRealiz(vIphRealiz);
							fatEspelhoAih.setIphCodSusRealiz(vCodSusRealiz);
							fatEspelhoAih.setTciCodSus(vTciCodSus);
							fatEspelhoAih.setAihDthrEmissao(vRealizAih5 ? (regConta.getNroSeqaih5() == null ? regConta.getDataInternacaoAdministrativa() : vDtIntAdministrativa) : regContaIni.getDataInternacaoAdministrativa());
							fatEspelhoAih.setEspecialidadeAih(vClinicaRealizHdiaCir != null ? vClinicaRealizHdiaCir.byteValue() : (vClinicaRealiz != null ? vClinicaRealiz.byteValue() : null));
							fatEspelhoAih.setDataInternacao(vRealizAih5 ? (regConta.getNroSeqaih5() == null ? regConta.getDataInternacaoAdministrativa() : vDtIntAdministrativa) : regContaIni.getDataInternacaoAdministrativa());
							fatEspelhoAih.setDataSaida(regConta.getDtAltaAdministrativa());
							fatEspelhoAih.setCidPrimario(vCidIni);
							fatEspelhoAih.setCidSecundario(vCidSec);
							fatEspelhoAih.setNascidosVivos(regConta.getRnNascidoVivo() != null ? regConta.getRnNascidoVivo() : 0);
							fatEspelhoAih.setNascidosMortos(regConta.getRnNascidoMorto() != null ? regConta.getRnNascidoMorto() : 0);
							fatEspelhoAih.setSaidasAlta(regConta.getRnSaidaAlta() != null ? regConta.getRnSaidaAlta() : 0);
							fatEspelhoAih.setSaidasTransferencia(regConta.getRnSaidaTransferencia() != null ? regConta.getRnSaidaTransferencia() : 0);
							fatEspelhoAih.setSaidasObito(regConta.getRnSaidaObito() != null ? regConta.getRnSaidaObito() : 0);
							if (!(vNroAihPosterior == null && regConta.getAih() == null)&& !(regConta.getAih() != null && CoreUtil.igual(regConta.getAih().getNroAih(), vNroAihPosterior))) {
								fatEspelhoAih.setNumeroAihPosterior(vNroAihPosterior);
							}
							if (!(vNroAihAnterior == null && regConta.getAih() == null) && !(regConta.getAih() != null && CoreUtil.igual(regConta.getAih().getNroAih(), vNroAihAnterior))) {
								fatEspelhoAih.setNumeroAihAnterior(vNroAihAnterior);
							}
							
							
							fatEspelhoAih.setInfeccaoHospitalar(vIndInfeccao);
							fatEspelhoAih.setMotivoCobranca(vMotivoCobranca.toString());
							fatEspelhoAih.setExclusaoCritica(vExclusaoCritica);
							fatEspelhoAih.setCpfMedicoAuditor(regConta.getCpfMedicoAuditor());
							fatEspelhoAih.setIndDocPac(vIndDocPac);
							fatEspelhoAih.setValorShRealiz(vVlrShRealiz);
							fatEspelhoAih.setValorSpRealiz(vVlrSpRealiz);
							fatEspelhoAih.setValorSadtRealiz(vVlrSadtRealiz);
							fatEspelhoAih.setValorAnestRealiz(vVlrAnestRealiz);
							fatEspelhoAih.setValorProcedRealiz(vVlrProcedRealiz);
							fatEspelhoAih.setNroSeqaih5(regConta.getNroSeqaih5());
							fatEspelhoAih.setNroSisprenatal(regConta.getNroSisprenatal());
							fatEspelhoAih.setPacCor(auxPacCor);
							fatEspelhoAih.setConCodCentral(vCodCentral);
							fatEspelhoAih.setDauSenha(vSenhaCerih);
							
							// marina 17/01/2012
							fatEspelhoAih.setCnsMedicoAuditor(regConta.getCnsMedicoAuditor());
							faturamentoFacade.inserirFatEspelhoAih(fatEspelhoAih, true, nomeMicrocomputador, dataFimVinculoServidor);
							//faturamentoFacade.evict(fatEspelhoAih);
						} catch (Exception e) {
							vMsg = e.getMessage();
							LOG.debug(e.getMessage(),e);							
							FatMensagemLog fatMensagemLog = new FatMensagemLog();
							fatMensagemLog.setCodigo(66);
							faturamentoON.criarFatLogErrors("ERRO AO INSERIR ESPELHO AIH: " + vMsg, INT, regConta.getSeq(), null,
									null, null, null, dataPrevia, null, null, vPhoSolic, vPhoRealiz, vIphSolic, vIphRealiz,
									vPacCodigo, regConta.getProcedimentoHospitalarInterno() != null ? regConta
											.getProcedimentoHospitalarInterno().getSeq() : null, regConta
											.getProcedimentoHospitalarInternoRealizado() != null ? regConta
											.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
									vCodSusRealiz, vCodSusSolic, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null,
									null,fatMensagemLog);

							result = Boolean.FALSE;
							logar(AGHU_ERRO_AO_ENCERRAR_MOTIVO + "ERRO AO INSERIR ESPELHO AIH:" + " (20)");
							
						}

						logar("vai agrupar itens, gerar espelho AIH, campo medico, serv profissionais... conta={0} " +
							  "phi_realizado={1} v_cod_sus_realiz={2}"
								,regConta.getSeq(), regConta.getPhiSeq(),vCodSusRealiz);

							
						/*
						 if reg_conta.seq in (42327400) then
				             -- Ney 31/08/20011
				             -- Gerada uma versão N da CTH3 para teste de alteração para compatibilidade por competencia
				             IF NOT fatk_cth3_rn_un.rn_cthc_atu_gera_aih(
				                reg_conta.seq     ,v_dias_conta       ,p_previa
				               ,v_data_previa     ,v_pac_codigo       ,v_pac_prontuario
				    	       ,v_int_seq  	      ,reg_conta.phi_seq  ,v_pho_solic
				    	       ,v_iph_solic       ,v_cod_sus_solic    ,reg_conta.phi_seq_realizado
				    	       ,v_pho_realiz      ,v_iph_realiz       ,v_cod_sus_realiz
				               ) THEN
				               dbms_output.put_line('resunt false');
				              result := FALSE;
				             END IF;          
				          else  
				          	IF NOT fatk_cthn_rn_un.rn_cthc_atu_gera_aih(
						 */
						if (!fatkCthnRN.rnCthcAtuGeraAih(
								regConta.getSeq(), vDiasConta, pPrevia,
								dataPrevia, 	   vPacCodigo, vPacProntuario, 
								vIntSeq,	 	   regConta.getProcedimentoHospitalarInterno() != null ? regConta.getProcedimentoHospitalarInterno().getSeq() : null, vPhoSolic, 
								vIphSolic, 		   vCodSusSolic, regConta.getProcedimentoHospitalarInternoRealizado() != null ? regConta.getProcedimentoHospitalarInternoRealizado().getSeq() : null, 
								vPhoRealiz, 	   vIphRealiz,   vCodSusRealiz, nomeMicrocomputador,
								dataFimVinculoServidor, fatVariaveisVO)) {
							
							logar("return false");
							result = Boolean.FALSE;
						}
						/*
						  END IF;
          				end if; 
						 */
					} else {
						result = Boolean.FALSE;
						logar(AGHU_ERRO_AO_ENCERRAR_MOTIVO + "contaDepoisOk" + " (22)");
					}
				}
				if (Boolean.FALSE.equals(contaOk) && Boolean.FALSE.equals(pPrevia)) {
					result = Boolean.FALSE; 
					logar(AGHU_ERRO_AO_ENCERRAR_MOTIVO + "!contaOk && !pPrevia" + " (28)");
				}
			} else {
				result = Boolean.FALSE;
				logar(AGHU_ERRO_AO_ENCERRAR_MOTIVO + "contaOk || pPrevia" + " (29)");
			}

			// Valida senha CERIH para o grupo Ortopedia/Traumatologia
			if (faturamentoRN.verificarCaracteristicaExame(vIphRealiz, vPhoRealiz, DominioFatTipoCaractItem.EXIGE_CERIH_INTERNACAO)) {
				vSenhaCerih = faturamentoRN.buscaSenhaCerih(regContaIni.getSeq());
				if (vSenhaCerih == null) {
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(188);
					faturamentoON.criarFatLogErrors("NAO FOI POSSIVEL LOCALIZAR SENHA CERIH PARA TRAUMATO/ORTOPEDIA", INT,
							regContaIni.getSeq(), null, null, null, null, dataPrevia, null, null, null, vPhoRealiz, null, vIphRealiz,
							vPacCodigo, null, regContaIni.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
									.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario, vCodSusRealiz,
							null, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

					result = Boolean.FALSE;
					logar(AGHU_ERRO_AO_ENCERRAR_MOTIVO + "NAO FOI POSSIVEL LOCALIZAR SENHA CERIH PARA TRAUMATO/ORTOPEDIA" + " (23)");
				}
			}

			// Valida CNRAC para pacientes fora do estado do RS
			FatContasInternacao contaInternacao = fatContasInternacaoDAO.buscarPrimeiraContaInternacao(pCthSeq);
			if (contaInternacao != null) {
				AinInternacao internacao = contaInternacao.getInternacao();

				if (internacao != null) {
					AghAtendimentos atendimento = internacao.getAtendimento();

					if (atendimento != null) {
						VAipEnderecoPaciente vAipEnderecoPaciente = cadastroPacienteFacade.obterEndecoPaciente(atendimento.getPaciente()
								.getCodigo());
						vUf = vAipEnderecoPaciente != null ? vAipEnderecoPaciente.getUf() : null;
					}
				}
			}

			if (vUf != null && !vUf.equalsIgnoreCase(ufSede)) {
				// Verifica se foi informado o número do CNRAC
				AinDiariasAutorizadas diariasAutorizadas = internacaoFacade.buscarPrimeiraDiariaAutorizada(pCthSeq);
				if (diariasAutorizadas != null) {
					vCnrac = diariasAutorizadas.getCnrac();
				}

				if (vCnrac == null
						&& faturamentoRN.verificarCaracteristicaExame(vIphRealiz, vPhoRealiz, DominioFatTipoCaractItem.EXIGE_CNRAC)) {
					FatMensagemLog fatMensagemLog = new FatMensagemLog();
					fatMensagemLog.setCodigo(194);
					faturamentoON.criarFatLogErrors("NÚMERO DO CNRAC NÃO INFORMADO PARA PACIENTE FORA DO ESTADO", INT, regContaIni
							.getSeq(), null, null, null, null, dataPrevia, null, null, null, vPhoRealiz, null, vIphRealiz, vPacCodigo,
							null, regContaIni.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
									.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario, vCodSusRealiz,
							null, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

					result = Boolean.FALSE;
					logar(AGHU_ERRO_AO_ENCERRAR_MOTIVO + "NÚMERO DO CNRAC NÃO INFORMADO PARA PACIENTE FORA DO ESTADO" + " (24)");
				}
			}

			if (!pPrevia) {
				// ATUALIZA SALDO NO BANCO DE UTI:
				if (Boolean.FALSE.equals(pPrevia) && 
						Boolean.TRUE.equals(contaOk) && 
							Boolean.TRUE.equals(result)) {
					// Se for ENCERRAMENTO
					// Atualiza saldo UTI se a conta possuir procedimento
					// autorizado/pago pelo sus
					if (fatkCth5RN.fatcValidaProcUti(pCthSeq)) {
						if (((vDiasMesIniUti != null ? vDiasMesIniUti : 0) + (vDiasMesAntUti != null ? vDiasMesAntUti : 0) + (vDiasMesAltaUti != null ? vDiasMesAltaUti
								: 0)) > 0) {
							vSenhaCerih = faturamentoRN.buscaSenhaCerih(regContaIni.getSeq());
							if (vSenhaCerih == null) {
								FatMensagemLog fatMensagemLog = new FatMensagemLog();
								fatMensagemLog.setCodigo(187);
								faturamentoON.criarFatLogErrors("NAO FOI POSSIVEL LOCALIZAR SENHA CERIH", INT, regContaIni.getSeq(),
										null, null, null, null, dataPrevia, null, null, null, vPhoRealiz, null, vIphRealiz,
										vPacCodigo, null,
										regContaIni.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
												.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
										vCodSusRealiz, null, null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

								result = Boolean.FALSE;
								logar(AGHU_ERRO_AO_ENCERRAR_MOTIVO + "NAO FOI POSSIVEL LOCALIZAR SENHA CERIH" + " (25)");
							}
						}

						final DominioTipoIdadeUTI idadeBlocoUti = fatVariaveisVO.getIdadeBlocoUti();
						
						RnSutcAtuSaldoVO rnSutcAtuSaldoVO = saldoUtiAtualizacaoRN.atualizarSaldoDiariasUti(DominioAtualizacaoSaldo.I,
								regContaIni.getDtAltaAdministrativa(), vDiasMesIniUti, vDiasMesAntUti, vDiasMesAltaUti, idadeBlocoUti, nomeMicrocomputador);

						Boolean retorno = null;
						if (rnSutcAtuSaldoVO != null) {
							retorno = rnSutcAtuSaldoVO.isRetorno();
							vMensagemIni = rnSutcAtuSaldoVO.getMsgInicial();
							vMensagemAnt = rnSutcAtuSaldoVO.getMsgAnterior();
							vMensagemAlta = rnSutcAtuSaldoVO.getMsgAlta();
						}

						if (CoreUtil.igual(Boolean.FALSE, retorno)) {
							FatMensagemLog fatMensagemLog = new FatMensagemLog();
							fatMensagemLog.setCodigo(155);
							faturamentoON.criarFatLogErrors("NAO FOI POSSIVEL ATUALIZAR BANCO DE UTI", INT, regContaIni.getSeq(),
									null, null, null, null, dataPrevia, null, null, null, vPhoRealiz, null, vIphRealiz, vPacCodigo,
									null, regContaIni.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
											.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
									vCodSusRealiz, null, null, null, null, null, null, null, RN_CTHC_ATU_LANCAUTI, null, null,fatMensagemLog);

							logar("vMensagemINI: {0}", vMensagemIni);
							logar("vMensagemANT: {0}", vMensagemAnt);
							logar("vMensagemALTA: {0}", vMensagemAlta);

							// Erro no mes inicial
							if (vMensagemIni != null) {
								faturamentoON.criarFatLogErrors(vMensagemIni, INT, regContaIni.getSeq(), null, null, null, null,
										dataPrevia, null, null, null, vPhoRealiz, null, vIphRealiz, vPacCodigo, null, regContaIni
												.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
												.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
										vCodSusRealiz, null, null, null, null, null, null, null, RN_CTHC_ATU_LANCAUTI, null, null, null);
							}

							// Erro no mes anterior
							if (vMensagemAnt != null) {
								faturamentoON.criarFatLogErrors(vMensagemAnt, INT, regContaIni.getSeq(), null, null, null, null,
										dataPrevia, null, null, null, vPhoRealiz, null, vIphRealiz, vPacCodigo, null, regContaIni
												.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
												.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
										vCodSusRealiz, null, null, null, null, null, null, null, RN_CTHC_ATU_LANCAUTI, null, null, null);
							}

							// Erro no mes alta
							if (vMensagemAlta != null) {
								faturamentoON.criarFatLogErrors(vMensagemAlta, INT, regContaIni.getSeq(), null, null, null, null,
										dataPrevia, null, null, null, vPhoRealiz, null, vIphRealiz, vPacCodigo, null, regContaIni
												.getProcedimentoHospitalarInternoRealizado() != null ? regContaIni
												.getProcedimentoHospitalarInternoRealizado().getSeq() : null, null, vPacProntuario,
										vCodSusRealiz, null, null, null, null, null, null, null, RN_CTHC_ATU_LANCAUTI, null, null, null);
							}

							result = Boolean.FALSE;
							logar(AGHU_ERRO_AO_ENCERRAR_MOTIVO + vMensagemAlta + " (26)");
						}
					}

					// Popula banco capacidades, sem testar se estourou ou não o
					// total de diárias
					if (CoreUtil.igual("S", regConta.getIndAutorizadoSms())) {
						saldoBancoCapacidadeAtulizacaoRN.atualizarSaldoDiariasBancoCapacidade(regConta
								.getDataInternacaoAdministrativa(), regConta.getDtAltaAdministrativa(), vClinicaRealizHdiaCir != null ? vClinicaRealizHdiaCir : vClinicaRealiz,
								pCthSeq, DominioAtualizacaoSaldo.I, true, nomeMicrocomputador, dataFimVinculoServidor);
					}
				}

				logar("retorno ");

				// Se nao houve erros encerra a conta
				if (result) {
					// Encerra conta hospitalar
					fatkCthRN.rnCthpAtuSituacao(pPrevia, DominioSituacaoConta.E, pCthSeq, nomeMicrocomputador, dataFimVinculoServidor);

					// Limpa sugestoes de desdobramento da conta
					fatContaSugestaoDesdobrDAO.removerPorCth(pCthSeq);
					fatContaSugestaoDesdobrDAO.flush();
					
					// Marina 23/05/2011
					// Separa os itens lançados na conta por competência quando data da internação for diferente da data da alta
					
					logar("ENTROU NA NEW");
					separaItensPorCompRN.fatpSeparaItensPorComp(pCthSeq, nomeMicrocomputador, dataFimVinculoServidor);
					
					// Reordena sequencia dos itens na conta considerando
					// compatibilidades/financiamento/complexidade/valores e
					// aplica percentuais nos valores
					
					logar("fatp_gera_sequencia_atos_new");
					faturamentoRN.geraSequenciaAtos(pCthSeq, phoSeq, nomeMicrocomputador, dataFimVinculoServidor);
			
					logar("CONTA foi ENCERRADA");
				} else {
					// Desfaz tudo e nao encerra a conta
					faturamentoFacade.clear();
					if (!fatkCthRN.rnCthcAtuReabre(pCthSeq, nomeMicrocomputador, dataFimVinculoServidor, pPrevia)) {
						result = Boolean.FALSE;
						logar(AGHU_ERRO_AO_ENCERRAR_MOTIVO + vMensagemAlta + " (26)");
					}
					logar("CONTA nao foi ENCERRADA");
				}
				
				// -- Marina 07/05/2013 -- volta os itens de exames anatopatologicos para a situacao em que eles estavam
		        // -- antes do encerramento.
				if (Boolean.FALSE.equals(pPrevia)) {
					if (!tabAnatoPat.isEmpty()) {
						for (ItemContaHospitalarVO itemContaHospitalarVO : tabAnatoPat) {
							Integer iseSoeSeq = itemContaHospitalarVO.getIseSoeSeq();
							Short iseSeqp = itemContaHospitalarVO.getIseSeqp();
							Integer cthSeq = itemContaHospitalarVO.getCthSeq();
							DominioSituacaoItenConta situacaoItemConta = itemContaHospitalarVO.getIndSituacao();
							
							logar("tab_anatopat(v_ind).ise_soe_seq: {0}", iseSoeSeq);
							logar("tab_anatopat(v_ind).ise_seqp: {0}", iseSeqp);
							logar("tab_anatopat(v_ind).ind_situacao: {0}", itemContaHospitalarVO.getIndSituacao());
							
							List<FatItemContaHospitalar> listaFatItemContaHospitalar = getFatItemContaHospitalarDAO()
									.listarPorContaHospitalarItemSolicitacaoExame(
											iseSeqp, iseSoeSeq, cthSeq);
							for (FatItemContaHospitalar itemContaHospitalar : listaFatItemContaHospitalar) {
								if (itemContaHospitalar.getId().getCthSeq().equals(cthSeq)
										&& itemContaHospitalar.getItemSolicitacaoExame().getId()
												.getSeqp().equals(iseSeqp)
										&& itemContaHospitalar.getItemSolicitacaoExame().getId()
												.getSoeSeq().equals(iseSoeSeq)) {
									FatItemContaHospitalar itemContaHospitalarOld = null;
									try {
										itemContaHospitalarOld = faturamentoFacade
												.clonarItemContaHospitalar(itemContaHospitalar);
									} catch (Exception e) {
										LOG.error(EXCECAO_CAPTURADA, e);
										throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
									}
									itemContaHospitalar.setIndSituacao(situacaoItemConta);
									itemContaHospitalarON
											.atualizarItemContaHospitalarSemValidacoesForms(
													itemContaHospitalar,
													itemContaHospitalarOld,
													true, servidorLogado, dataFimVinculoServidor, pPrevia, null);
								}
							}						
						}
					}
				}
				//-- FIM MARINA 07/05/2013
			} else {

				if (result) {
					logar("PREVIA OK");
				} else {
					logar("PREVIA NAO OK");
				}
			}
		} else {
			// Conta hospitalar nao encontrada ou com convenio que nao é SUS ou
			// nao fechada
			
			FatMensagemLog fatMensagemLog = new FatMensagemLog();
			fatMensagemLog.setCodigo(35);
			faturamentoON.criarFatLogErrors("CONTA NAO POSSUI CONVENIO DO GRUPO SUS OU NAO ESTA ABERTA / FECHADA.", INT, pCthSeq,
					null, null, null, null, dataPrevia, null, null, null, null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, RN_CTHC_ATU_ENC_PRV, null, null,fatMensagemLog);

			result = Boolean.FALSE;
			logar(AGHU_ERRO_AO_ENCERRAR_MOTIVO + "CONTA NAO POSSUI CONVENIO DO GRUPO SUS OU NAO ESTA ABERTA / FECHADA." + " (27)");
		}

		// Limpa tabela auxiliar de agrupamento de itens
		if (pCthSeq != null) {
			List<FatAgrupItemConta> agrupamentos = getFatAgrupItemContaDAO().pesquisarPorCodigoContaHospitalar(pCthSeq);
			fatAgrupItemContaDAO.removerPorCodigoContaHospitalar(pCthSeq);
			fatAgrupItemContaDAO.flush();
			
			for(FatAgrupItemConta agrupamento : agrupamentos) {
				getFaturamentoFacade().evict(agrupamento);
			}
		}
		
		//faturamentoFacade.commit(TRANSACTION_TIMEOUT_24_HORAS);
		this.commitTransaction();
		this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
		
		return result;
	}
	
	protected FatkCthRN getFatkCthRN() {
		return fatkCthRN;
	}	
	protected FatkCthnRN getFatkCthnRN() {
		return fatkCthnRN;
	}
	protected FatkCth4RN getFatkCth4RN() {
		return fatkCth4RN;
	}
	protected FatkCth5RN getFatkCth5RN() {
		return fatkCth5RN;
	}
	protected FatkCth6RN getFatkCth6RN() {
		return fatkCth6RN;
	}	
	
	protected FatSeparaItensPorCompRN getFatSeparaItensPorCompRN() {
		return fatSeparaItensPorCompRN;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	public FaturamentoInternacaoRN getFaturamentoInternacaoRN() {
		return faturamentoInternacaoRN;
	}	
}