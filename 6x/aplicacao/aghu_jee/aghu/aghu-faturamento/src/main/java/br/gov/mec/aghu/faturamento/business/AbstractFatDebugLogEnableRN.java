package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoDebugCode;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatAgrupItemContaDAO;
import br.gov.mec.aghu.faturamento.dao.FatAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatAutorizadoCmaDAO;
import br.gov.mec.aghu.faturamento.dao.FatCampoMedicoAuditAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatCaractItemProcHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatCidContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.faturamento.dao.FatContaHospitalarJnDAO;
import br.gov.mec.aghu.faturamento.dao.FatContaSugestaoDesdobrDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasInternacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvGrupoItensProcedDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudeDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudePlanoDAO;
import br.gov.mec.aghu.faturamento.dao.FatDadosContaSemIntDAO;
import br.gov.mec.aghu.faturamento.dao.FatDiariaUtiDigitadaDAO;
import br.gov.mec.aghu.faturamento.dao.FatDocumentoCobrancaAihsDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoItemContaHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatExcCaraterInternacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatExcCnvGrpItemProcDAO;
import br.gov.mec.aghu.faturamento.dao.FatExclusaoCriticaDAO;
import br.gov.mec.aghu.faturamento.dao.FatGrupoProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemGrupoProcedHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemProcHospTranspDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatLogErrorDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoDesdobramentoDAO;
import br.gov.mec.aghu.faturamento.dao.FatMotivoSaidaPacienteDAO;
import br.gov.mec.aghu.faturamento.dao.FatPacienteTransplantesDAO;
import br.gov.mec.aghu.faturamento.dao.FatPendenciaContaHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatPerdaItemContaDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospIntCidDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedimentoCboDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedimentoRegistroDAO;
import br.gov.mec.aghu.faturamento.dao.FatSituacaoSaidaPacienteDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoAtoDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoCaractItensDAO;
import br.gov.mec.aghu.faturamento.dao.FatTiposVinculoDAO;
import br.gov.mec.aghu.faturamento.dao.FatValorContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatValorDiariaInternacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatVlrItemProcedHospCompsDAO;
import br.gov.mec.aghu.faturamento.dao.VFatAssociacaoProcedimentoDAO;
import br.gov.mec.aghu.faturamento.dao.VFatContaHospitalarPacDAO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * @author gandriotti
 */

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CouplingBetweenObjects", "PMD.HierarquiaONRNIncorreta"})
@Stateless
public class AbstractFatDebugLogEnableRN extends BaseBusiness implements Serializable {


@EJB
private FaturamentoFatkPrzRN faturamentoFatkPrzRN;

@EJB
private FaturamentoRN faturamentoRN;

@EJB
private FaturamentoFatkCap2RN faturamentoFatkCap2RN;

@EJB
private FaturamentoON faturamentoON;

@EJB
private SaldoUtiAtualizacaoRN saldoUtiAtualizacaoRN;

@EJB
private FaturamentoFatkIctRN faturamentoFatkIctRN;

@EJB
private FaturamentoFatkCgiRN faturamentoFatkCgiRN;

@EJB
private FaturamentoFatkDciRN faturamentoFatkDciRN;

@EJB
private SaldoBancoCapacidadeAtulizacaoRN saldoBancoCapacidadeAtulizacaoRN;

@EJB
private TipoCaracteristicaItemRN tipoCaracteristicaItemRN;

@EJB
private VerificacaoItemProcedimentoHospitalarRN verificacaoItemProcedimentoHospitalarRN;

@EJB
private ContaHospitalarON contaHospitalarON;

@EJB
private FaturamentoFatkInterfaceMcoRN faturamentoFatkInterfaceMcoRN;

@EJB
private ItemContaHospitalarRN itemContaHospitalarRN;

@EJB
private VerificacaoFaturamentoSusRN verificacaoFaturamentoSusRN;

@EJB
private FaturamentoFatkInterfaceAnuRN faturamentoFatkInterfaceAnuRN;

@EJB
private FatLogErrorON fatLogErrorON;

@EJB
private FaturamentoFatkIchRN faturamentoFatkIchRN;

private static final Log LOG = LogFactory.getLog(AbstractFatDebugLogEnableRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}


	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private FaturamentoFatkInterfaceAfaRN faturamentoFatkInterfaceAfaRN;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private FatCampoMedicoAuditAihDAO fatCampoMedicoAuditAihDAO;
	
	@Inject
	private FatVlrItemProcedHospCompsDAO fatVlrItemProcedHospCompsDAO;
	
	@Inject
	private FatCompetenciaDAO fatCompetenciaDAO;
	
	@Inject
	private FatTipoCaractItensDAO fatTipoCaractItensDAO;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@Inject
	private FatGrupoProcedHospitalarDAO fatGrupoProcedHospitalarDAO;
	
	@Inject
	private FatPerdaItemContaDAO fatPerdaItemContaDAO;
	
	@Inject
	private FatSituacaoSaidaPacienteDAO fatSituacaoSaidaPacienteDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private FatProcedHospIntCidDAO fatProcedHospIntCidDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private FatEspelhoItemContaHospDAO fatEspelhoItemContaHospDAO;
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@Inject
	private FatTipoAihDAO fatTipoAihDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private FatPendenciaContaHospDAO fatPendenciaContaHospDAO;
	
	@Inject
	private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;
	
	@Inject
	private FatItemGrupoProcedHospDAO fatItemGrupoProcedHospDAO;
	
	@Inject
	private FatEspelhoAihDAO fatEspelhoAihDAO;
	
	@Inject
	private FatItemContaHospitalarDAO fatItemContaHospitalarDAO;
	
	@Inject
	private FatCidContaHospitalarDAO fatCidContaHospitalarDAO;
	
	@Inject
	private FatItemProcHospTranspDAO fatItemProcHospTranspDAO;
	
	@Inject
	private FatDiariaUtiDigitadaDAO fatDiariaUtiDigitadaDAO;
	
	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private FatProcedimentoRegistroDAO fatProcedimentoRegistroDAO;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@Inject
	private FatValorContaHospitalarDAO fatValorContaHospitalarDAO;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private FatContaSugestaoDesdobrDAO fatContaSugestaoDesdobrDAO;
	
	@Inject
	private FatAtoMedicoAihDAO fatAtoMedicoAihDAO;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private FatMotivoSaidaPacienteDAO fatMotivoSaidaPacienteDAO;
	
	@Inject
	private VFatContaHospitalarPacDAO vFatContaHospitalarPacDAO;
	
	@Inject
	private FatLogErrorDAO fatLogErrorDAO;
	
	@Inject
	private FatAutorizadoCmaDAO fatAutorizadoCmaDAO;
	
	@Inject
	private FatPacienteTransplantesDAO fatPacienteTransplantesDAO;
	
	@Inject
	private VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO;
	
	@Inject
	private FatConvGrupoItensProcedDAO fatConvGrupoItensProcedDAO;
	
	@Inject
	private FatProcedimentoCboDAO fatProcedimentoCboDAO;
	
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@Inject
	private FatTipoAtoDAO fatTipoAtoDAO;
	
	@Inject
	private FatCaractItemProcHospDAO fatCaractItemProcHospDAO;
	
	@Inject
	private FatContaHospitalarJnDAO fatContaHospitalarJnDAO;
	
	@Inject
	private FatDadosContaSemIntDAO fatDadosContaSemIntDAO;
	
	@Inject
	private FatProcedHospInternosDAO fatProcedHospInternosDAO;
	
	@Inject
	private FatAgrupItemContaDAO fatAgrupItemContaDAO;
	
	@Inject
	private FatValorDiariaInternacaoDAO fatValorDiariaInternacaoDAO;
	
	@Inject
	private FatDocumentoCobrancaAihsDAO fatDocumentoCobrancaAihsDAO;
	
	@Inject
	private FatTiposVinculoDAO fatTiposVinculoDAO;
	
	@Inject
	private FatAihDAO fatAihDAO;
	
	@Inject
	private FatContasInternacaoDAO fatContasInternacaoDAO;
	
	@Inject
	private FatExclusaoCriticaDAO fatExclusaoCriticaDAO;
	
	@Inject
	private FatConvenioSaudePlanoDAO fatConvenioSaudePlanoDAO;
	
	@Inject
	private FatMotivoDesdobramentoDAO fatMotivoDesdobramentoDAO;
	
	@Inject
	private FatContasHospitalaresDAO fatContasHospitalaresDAO;
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@Inject
	private FatExcCaraterInternacaoDAO fatExcCaraterInternacaoDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private FatExcCnvGrpItemProcDAO fatExcCnvGrpItemProcDAO;
	
	@EJB
	private ISchedulerFacade schedulerFacade;
	
	@Inject
	private FatConvenioSaudeDAO fatConvenioSaudeDAO;
	
	@EJB
	private ItemContaHospitalarON itemContaHospitalarON;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5376665922438096695L;
	private static final DateFormat formatadorData = new SimpleDateFormat("dd/MM/yy");
	public final static int TRANSACTION_TIMEOUT_24_HORAS = 60 * 60 * 24; //= 1 dia 
	public final static int TRANSACTION_TIMEOUT_1_HORA = 60 * 60; //= 1 hora
//	private static final String ENTITY_MANAGER = "entityManager";
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected ISchedulerFacade getSchedulerFacade() {
		return this.schedulerFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected FaturamentoON getFaturamentoON() {
		return faturamentoON;
	}

	protected FaturamentoRN getFaturamentoRN() {
		
		return faturamentoRN;
	}
	
	protected ContaHospitalarON getContaHospitalarON(){
		return contaHospitalarON;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	protected Date trunc(Date data) {
		return data != null ? DateUtil.truncaData(data) : null;
	}
	
	protected BigDecimal nvl(BigDecimal obj1, BigDecimal obj2) {
		return (BigDecimal) CoreUtil.nvl(obj1, obj2);
	}

	protected Integer nvl(Integer obj1, Number obj2) {
		Integer obj2Integer = obj2 != null ? obj2.intValue() : null;
		return (Integer) CoreUtil.nvl(obj1, obj2Integer);
	}

	protected Long nvl(Long obj1, Number obj2) {
		Long obj2Long = obj2 != null ? obj2.longValue() : null;
		return (Long) CoreUtil.nvl(obj1, obj2Long);
	}

	protected Short nvl(Short obj1, Number obj2) {
		Short obj2Short = obj2 != null ? obj2.shortValue() : null;
		return (Short) CoreUtil.nvl(obj1, obj2Short);
	}

	protected Byte nvl(Byte obj1, Number obj2) {
		Byte obj2Byte = obj2 != null ? obj2.byteValue() : null;
		return (Byte) CoreUtil.nvl(obj1, obj2Byte);
	}

	protected Boolean nvl(Boolean obj1, Boolean obj2) {
		return (Boolean) CoreUtil.nvl(obj1, obj2);
	}

	protected String nvl(String obj1, String obj2) {
		return (String) CoreUtil.nvl(obj1, obj2);
	}

	protected Date nvl(Date obj1, Date obj2) {
		return (Date) CoreUtil.nvl(obj1, obj2);
	}
	
	protected Object nvl(Object obj1, Object obj2) {
		return CoreUtil.nvl(obj1, obj2);
	}
	
	protected AghParametros buscarAghParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return getParametroFacade().buscarAghParametro(parametrosEnum);
	}
	
	protected BigDecimal buscarVlrNumericoAghParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		BigDecimal result = this.buscarAghParametro(parametrosEnum).getVlrNumerico();
		if(result == null){
			throw new ApplicationBusinessException(FaturamentoExceptionCode.PARAMETRO_INVALIDO, parametrosEnum);
		}
		return result;
	}
	
	protected Integer buscarVlrInteiroAghParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {		
		return this.buscarVlrNumericoAghParametro(parametrosEnum).intValue();
	}

	protected Byte buscarVlrByteAghParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return this.buscarVlrNumericoAghParametro(parametrosEnum).byteValue();
	}

	protected Short buscarVlrShortAghParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return this.buscarVlrNumericoAghParametro(parametrosEnum).shortValue();
	}

	protected Long buscarVlrLongAghParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return this.buscarVlrNumericoAghParametro(parametrosEnum).longValue();
	}

	protected String buscarVlrTextoAghParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		String result = this.buscarAghParametro(parametrosEnum).getVlrTexto();
		if(result == null){
			throw new ApplicationBusinessException(FaturamentoExceptionCode.PARAMETRO_INVALIDO, parametrosEnum);
		}
		return result;
	}
	
	protected String[] buscarVlrArrayAghParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		String result = this.buscarVlrTextoAghParametro(parametrosEnum);
		return result.split("\\,");
	}
	
	protected Integer[] buscarVlrIntegerArrayAghParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		String[] tmp = this.buscarVlrArrayAghParametro(parametrosEnum);
		Integer[] result = new Integer[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			result[i] = Integer.valueOf(tmp[i]);
		}
		return result;
	}
	
	protected Short[] buscarVlrShortArrayAghParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		String[] tmp = this.buscarVlrArrayAghParametro(parametrosEnum);
		Short[] result = new Short[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			result[i] = Short.valueOf(tmp[i]);
		}
		return result;
	}
	
	protected Long[] buscarVlrLongArrayAghParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		String[] tmp = this.buscarVlrArrayAghParametro(parametrosEnum);
		Long[] result = new Long[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			result[i] = Long.valueOf(tmp[i]);
		}
		return result;
	}
	
	
	
	protected void logar(String message, Object...values){
		if(LOG.isDebugEnabled()){
			try{
				if(values != null){
					for (int i = 0; i < values.length; i++) {
						if(values[i] == null){
							values[i] = "";
						} else if(values[i] instanceof Boolean){
							values[i] = ((Boolean)values[i]).booleanValue() ? "S" : "N";
						} else if(values[i] instanceof Date){
							Date dt = (Date) values[i];
							values[i] = formatadorData.format(dt);
						} else if(values[i] instanceof Long || values[i] instanceof Integer || values[i] instanceof Short || values[i] instanceof Byte){
							values[i] = values[i].toString().replaceAll("\\,", "");
						} else if(values[i] instanceof Number){
							values[i] = values[i].toString().replaceAll("\\,", "").replaceAll("\\.", ",");							
						}
					}
					message = MessageFormat.format(message, values);
				}
			} catch (Exception e) {
				LOG.debug(String.format("ERRO AO GERAR LOG: %s", e.getMessage()));
			}
			LOG.debug(message);
		}
	}
	
	/**
	 * Divide <code>valor</code> pela <code>quantidade</code>  e multiplica pela <code>novaQuantidade</code>
	 * 
	 * @param valor
	 * @param quantidade
	 * @param newQuantidade
	 * @return
	 */
	protected BigDecimal correcaoValorQtd(Number valor, Number quantidade, Number novaQuantidade) {
		valor = (Number)CoreUtil.nvl(valor, 0);
		quantidade = (Number)CoreUtil.nvl(quantidade, 0);
		novaQuantidade = (Number)CoreUtil.nvl(novaQuantidade, 0);
		double val = novaQuantidade.doubleValue() * (valor.doubleValue() / quantidade.doubleValue());
		return BigDecimal.valueOf(val);
	}
	
	/**
	 * Multiplica <code>valor</code> pela <code>quantidade</code>
	 * 
	 * @param valor
	 * @param quantidade
	 * @param newQuantidade
	 * @return
	 */
	protected BigDecimal correcaoValorQtd(Number valor, Number quantidade) {
		valor = (Number)CoreUtil.nvl(valor, 0);
		quantidade = (Number)CoreUtil.nvl(quantidade, 0);
		double val = valor.doubleValue() * quantidade.doubleValue();
		return BigDecimal.valueOf(val);
	}
	

	/*
	 * Precisa ser um metodo de classe para testes unitarios, assim eh possivel sobre-escrever 
	 */
	protected String getMessageFromBundle(final String key, final Object... params) {
		String result = null;
		
		String value = null;

		result = value + " " + params.toString();

		return result;
	}

	protected String logDebug(final FaturamentoDebugCode code, final Object... params) {

		String result = null;

		result = this.getMessageFromBundle(code.name(), params);
		LOG.debug(result);
		
		return result;
	}		
	

	/** Obter userTransaction
	 * 
	 * @param userTransaction
	 * @return
	 */
	protected UserTransaction obterUserTransaction(UserTransaction userTransaction) {
		/*
		if (userTransaction == null) {
			userTransaction = (UserTransaction) org.jboss.seam.org.jboss.seam.transaction.transaction;
			try {
				userTransaction.setTransactionTimeout(TRANSACTION_TIMEOUT_24_HORAS); // um dia
			} catch (Exception e) {
				logError(e);
			}
		}
		try {
			if (userTransaction.isNoTransaction()) {
				userTransaction.begin();
			}
			if (userTransaction.isActive()) {
				getEntityManager().joinTransaction();
			}
		} catch (Exception e) {
			logError(e);
		}
		*/
		return userTransaction;
	}		

	/** Commit do userTransaction
	 * 
	 * @param userTransaction
	 * @param tentativas
	 * @return
	 *  
	 */
	protected UserTransaction commitUserTransaction(UserTransaction userTransaction) throws ApplicationBusinessException {
		/*
		try {
			getFaturamentoFacade().flush();
			userTransaction.commit();
		} catch (Exception e) {
			userTransaction = obterUserTransaction(null);
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String stringException = sw.toString();								
			
			String logTxt = "##### AGHU - Exception - erro no commit do userTransaction nivel 1 " + DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()) + " <br /> " 
				   + " <br /> "
				   + stringException;
			logar("erro: {0}", logTxt);
			
			logError(e);
			try {
				userTransaction.commit();
			} catch (Exception e1) {
				
				sw = new StringWriter();
				pw = new PrintWriter(sw);
				e1.printStackTrace(pw);
				stringException = sw.toString();								
				
				logTxt = "##### AGHU - Exception - erro no commit do userTransaction nivel 2 " + DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()) + " <br /> " 
					   + " <br /> "
					   + stringException;
				logar("erro: {0}", logTxt);

				logError(e1);
			}
		}
		userTransaction = obterUserTransaction(null);
		*/
		return userTransaction;
	}

	/** Reinicia userTransaction
	 * 
	 * @param userTx
	 * @return
	 */
	protected UserTransaction reIniciarTransacao(UserTransaction userTx) {
		/*
		if (userTx != null) {
			try {
				userTx.rollback();
			} catch (Exception e1) {
				logError("ERRO ATUALIZANDO FatProcedAmbRealizado EM " + this.getClass(), e1);
			}
		}
		*/
		return userTx;
	}
	
	public AbstractFatDebugLogEnableRN() {
		super();
	}
	
	
	protected FatLogErrorON getFatLogErrorON() {
		return fatLogErrorON;
	}
		
	
	protected FaturamentoFatkInterfaceMcoRN getFaturamentoFatkInterfaceMcoRN() {
		return faturamentoFatkInterfaceMcoRN;
	}

	protected FaturamentoFatkInterfaceAnuRN getFaturamentoFatkInterfaceAnuRN() {
		return faturamentoFatkInterfaceAnuRN;
	}

	protected TipoCaracteristicaItemRN getTipoCaracteristicaItemRN() {
		return tipoCaracteristicaItemRN;
	}

	protected ItemContaHospitalarRN getItemContaHospitalarRN() {
		return itemContaHospitalarRN;
	}
	
	protected VerificacaoFaturamentoSusRN getVerificacaoFaturamentoSusRN() {
		return verificacaoFaturamentoSusRN;
	}
	
	protected IExamesLaudosFacade getExamesLaudosFacade() {
		return this.examesLaudosFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
			
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}
	
	protected IControleInfeccaoFacade getControleInfeccaoFacade() {
		return controleInfeccaoFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IBlocoCirurgicoProcDiagTerapFacade getBlocoCirurgicoProcDiagTerapFacade() {
		return blocoCirurgicoProcDiagTerapFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected FatConvenioSaudeDAO getFatConvenioSaudeDAO() {
		return fatConvenioSaudeDAO;
	}

	protected VFatAssociacaoProcedimentoDAO getVFatAssociacaoProcedimentoDAO() {
		return vFatAssociacaoProcedimentoDAO;
	}

	protected FatMotivoSaidaPacienteDAO getFatMotivoSaidaPacienteDAO() {
		return fatMotivoSaidaPacienteDAO;
	}

	protected FatContaHospitalarJnDAO getFatContaHospitalarJnDAO() {
		return fatContaHospitalarJnDAO;
	}

	protected FatCaractItemProcHospDAO getFatCaractItemProcHospDAO() {
		return fatCaractItemProcHospDAO;
	}

	protected FatTipoCaractItensDAO getFatTipoCaractItensDAO() {
		return fatTipoCaractItensDAO;
	}

	protected FatItemProcHospTranspDAO getFatItemProcHospTranspDAO() {
		return fatItemProcHospTranspDAO;
	}

	protected FatPacienteTransplantesDAO getFatPacienteTransplantesDAO() {
		return fatPacienteTransplantesDAO;
	}

	protected FatProcedimentoCboDAO getFatProcedimentoCboDAO() {
		return fatProcedimentoCboDAO;
	}

	protected FatProcedimentoRegistroDAO getFatProcedimentoRegistroDAO() {
		return fatProcedimentoRegistroDAO;
	}

	protected FatPendenciaContaHospDAO getFatPendenciaContaHospDAO() {
		return fatPendenciaContaHospDAO;
	}

	protected FatItemGrupoProcedHospDAO getFatItemGrupoProcedHospDAO() {
		return fatItemGrupoProcedHospDAO;
	}

	protected FatGrupoProcedHospitalarDAO getFatGrupoProcedHospitalarDAO() {
		return fatGrupoProcedHospitalarDAO;
	}

	protected FatCompetenciaDAO getFatCompetenciaDAO() {
		return fatCompetenciaDAO;
	}

	protected FatProcedHospIntCidDAO getFatProcedHospIntCidDAO() {
		return fatProcedHospIntCidDAO;
	}

	protected FatProcedHospInternosDAO getFatProcedHospInternosDAO() {
		return fatProcedHospInternosDAO;
	}

	protected FatExcCnvGrpItemProcDAO getFatExcCnvGrpItemProcDAO() {
		return fatExcCnvGrpItemProcDAO;
	}

	protected FatConvGrupoItensProcedDAO getFatConvGrupoItensProcedDAO() {
		return fatConvGrupoItensProcedDAO;
	}

	protected FatVlrItemProcedHospCompsDAO getFatVlrItemProcedHospCompsDAO() {
		return fatVlrItemProcedHospCompsDAO;
	}

	protected FatMotivoDesdobramentoDAO getFatMotivoDesdobramentoDAO() {
		return fatMotivoDesdobramentoDAO;
	}

	protected FatExclusaoCriticaDAO getFatExclusaoCriticaDAO() {
		return fatExclusaoCriticaDAO;
	}

	protected VFatContaHospitalarPacDAO getVFatContaHospitalarPacDAO() {
		return vFatContaHospitalarPacDAO;
	}

	protected FatDadosContaSemIntDAO getFatDadosContaSemIntDAO() {
		return fatDadosContaSemIntDAO;
	}

	protected FatAutorizadoCmaDAO getFatAutorizadoCmaDAO() {
		return fatAutorizadoCmaDAO;
	}

	protected FatValorDiariaInternacaoDAO getFatValorDiariaInternacaoDAO() {
		return fatValorDiariaInternacaoDAO;
	}	
	
	protected FatConvenioSaudePlanoDAO getFatConvenioSaudePlanoDAO() {
		return fatConvenioSaudePlanoDAO;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected ICadastroPacienteFacade getCadastroPacienteFacade(){
		return cadastroPacienteFacade;
	}
	
	protected ICadastrosBasicosPacienteFacade getCadastrosBasicosPacienteFacade() {
		return cadastrosBasicosPacienteFacade;
	}
	
	protected ICadastrosBasicosFacade getCadastrosBasicosFacade() {
		return cadastrosBasicosFacade;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}

	protected FaturamentoFatkPrzRN getFaturamentoFatkPrzRN() {
		return faturamentoFatkPrzRN;
	}

	protected FaturamentoFatkIchRN getFaturamentoFatkIchRN() {
		return faturamentoFatkIchRN;
	}
	
	protected FaturamentoFatkDciRN getFaturamentoFatkDciRN() {
		return faturamentoFatkDciRN;
	}
	
	protected VerificacaoItemProcedimentoHospitalarRN getVerificacaoItemProcedimentoHospitalarRN() {
		return verificacaoItemProcedimentoHospitalarRN;
	}
	
	protected VerificacaoFaturamentoSusRN getFaturamentoFatkSusRN() {
		return verificacaoFaturamentoSusRN;
	}
	
	protected FaturamentoFatkCap2RN getFaturamentoFatkCap2RN() {
		return faturamentoFatkCap2RN;
	}
	
	protected FaturamentoFatkIctRN getFaturamentoFatkIctRN() {
		return faturamentoFatkIctRN;
	}
	
	protected FaturamentoFatkCgiRN getFaturamentoFatkCgiRN() {
		return faturamentoFatkCgiRN;
	}
	
	protected SaldoUtiAtualizacaoRN getSaldoUtiAtualizacaoRN() {
		return saldoUtiAtualizacaoRN;
	}
	
	protected SaldoBancoCapacidadeAtulizacaoRN getSaldoBancoCapacidadeAtulizacaoRN() {
		return saldoBancoCapacidadeAtulizacaoRN;
	}
	
	protected FatContasHospitalaresDAO getFatContasHospitalaresDAO() {
		return fatContasHospitalaresDAO;
	}

	protected FatAgrupItemContaDAO getFatAgrupItemContaDAO() {
		return fatAgrupItemContaDAO;
	}

	protected FatLogErrorDAO getFatLogErrorDAO() {
		return fatLogErrorDAO;
	}

	protected FatEspelhoItemContaHospDAO getFatEspelhoItemContaHospDAO() {
		return fatEspelhoItemContaHospDAO;
	}

	protected FatPerdaItemContaDAO getFatPerdaItemContaDAO() {
		return fatPerdaItemContaDAO;
	}

	protected FatValorContaHospitalarDAO getFatValorContaHospitalarDAO() {
		return fatValorContaHospitalarDAO;
	}

	protected FatEspelhoAihDAO getFatEspelhoAihDAO() {
		return fatEspelhoAihDAO;
	}

	protected FatAtoMedicoAihDAO getFatAtoMedicoAihDAO() {
		return fatAtoMedicoAihDAO;
	}

	protected FatCampoMedicoAuditAihDAO getFatCampoMedicoAuditAihDAO() {
		return fatCampoMedicoAuditAihDAO;
	}

	protected FatSituacaoSaidaPacienteDAO getFatSituacaoSaidaPacienteDAO() {
		return fatSituacaoSaidaPacienteDAO;
	}

	protected FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO() {
		return fatItensProcedHospitalarDAO;
	}

	protected FatAihDAO getFatAihDAO() {
		return fatAihDAO;
	}

	protected FatTipoAihDAO getFatTipoAihDAO() {
		return fatTipoAihDAO;
	}

	protected FatItemContaHospitalarDAO getFatItemContaHospitalarDAO() {
		return fatItemContaHospitalarDAO;
	}

	protected FatCidContaHospitalarDAO getFatCidContaHospitalarDAO() {
		return fatCidContaHospitalarDAO;
	}

	protected FatDiariaUtiDigitadaDAO getFatDiariaUtiDigitadaDAO() {
		return fatDiariaUtiDigitadaDAO;
	}

	protected FatContasInternacaoDAO getFatContasInternacaoDAO() {
		return fatContasInternacaoDAO;
	}

	protected FatExcCaraterInternacaoDAO getFatExcCaraterInternacaoDAO() {
		return fatExcCaraterInternacaoDAO;
	}

	protected FatDocumentoCobrancaAihsDAO getFatDocumentoCobrancaAihsDAO() {
		return fatDocumentoCobrancaAihsDAO;
	}
	
	protected FatTiposVinculoDAO getFatTiposVinculoDAO() {
		return fatTiposVinculoDAO;
	}
	
	protected FatTipoAtoDAO getFatTipoAtoDAO() {
		return fatTipoAtoDAO;
	}
		
	protected FatContaSugestaoDesdobrDAO getFatContaSugestaoDesdobrDAO() {
		return fatContaSugestaoDesdobrDAO;
	}
			
	protected ItemContaHospitalarON getItemContaHospitalarON() {
		return itemContaHospitalarON;
	}
	
	protected FaturamentoFatkInterfaceAfaRN getFaturamentoFatkInterfaceAfaRN() {
		return faturamentoFatkInterfaceAfaRN;
	}

		
}
