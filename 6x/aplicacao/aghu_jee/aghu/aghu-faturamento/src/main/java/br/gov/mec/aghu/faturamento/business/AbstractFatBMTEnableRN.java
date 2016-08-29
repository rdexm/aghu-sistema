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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatAgrupItemContaDAO;
import br.gov.mec.aghu.faturamento.dao.FatAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatCampoMedicoAuditAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatCidContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
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
import br.gov.mec.aghu.faturamento.dao.FatLogErrorDAO;
import br.gov.mec.aghu.faturamento.dao.FatPerdaItemContaDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.dao.FatSituacaoSaidaPacienteDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoAtoDAO;
import br.gov.mec.aghu.faturamento.dao.FatTiposVinculoDAO;
import br.gov.mec.aghu.faturamento.dao.FatVlrItemProcedHospCompsDAO;
import br.gov.mec.aghu.faturamento.dao.VFatAssociacaoProcedimentoDAO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
public class AbstractFatBMTEnableRN extends BaseBMTBusiness implements Serializable {

	private static final long serialVersionUID = -7335338670556753310L;
	private static final DateFormat formatadorData = new SimpleDateFormat("dd/MM/yy");
	public final static int TRANSACTION_TIMEOUT_24_HORAS = 60 * 60 * 24; //= 1 dia 
	public final static int TRANSACTION_TIMEOUT_1_HORA = 60 * 60; //= 1 hora
	
	private static final Log LOG = LogFactory.getLog(AbstractFatBMTEnableRN.class);
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private FaturamentoFatkInterfaceAfaRN faturamentoFatkInterfaceAfaRN;
	
	@EJB
	private FaturamentoRN faturamentoRN;
	
	@EJB
	private ItemContaHospitalarON itemContaHospitalarON;
	
	@Inject
	private FatContasHospitalaresDAO fatContasHospitalaresDAO;
	
	@Inject
	private FatPerdaItemContaDAO fatPerdaItemContaDAO;
	
	@Inject
	private FatExcCnvGrpItemProcDAO fatExcCnvGrpItemProcDAO;
	
	@Inject
	private FatVlrItemProcedHospCompsDAO fatVlrItemProcedHospCompsDAO;
	
	@Inject
	private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;
	
	@Inject
	private FatConvGrupoItensProcedDAO fatConvGrupoItensProcedDAO;
	
	@Inject
	private FatAgrupItemContaDAO fatAgrupItemContaDAO;
	
	@Inject
	private FatCompetenciaDAO fatCompetenciaDAO;
	
	@Inject
	private FatItemContaHospitalarDAO fatItemContaHospitalarDAO;
	
	@Inject
	private VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO;
	
	@Inject
	private FatProcedHospInternosDAO fatProcedHospInternosDAO;
	
	@Inject
	private FatLogErrorDAO fatLogErrorDAO;
	
	@EJB
	private FaturamentoFatkPrzRN faturamentoFatkPrzRN;
	
	@EJB
	private FaturamentoFatkIchRN faturamentoFatkIchRN;
	
	@EJB
	private FaturamentoFatkIctRN faturamentoFatkIctRN;

	@EJB
	private FaturamentoFatkCgiRN faturamentoFatkCgiRN;

	@EJB
	private FaturamentoFatkDciRN faturamentoFatkDciRN;
	
	@EJB
	private VerificacaoFaturamentoSusRN verificacaoFaturamentoSusRN;	

	@EJB
	private FaturamentoFatkCap2RN faturamentoFatkCap2RN;
	
	@EJB
	private SaldoUtiAtualizacaoRN saldoUtiAtualizacaoRN;
	
	@EJB
	private SaldoBancoCapacidadeAtulizacaoRN saldoBancoCapacidadeAtulizacaoRN;
	
	@EJB
	private VerificacaoItemProcedimentoHospitalarRN verificacaoItemProcedimentoHospitalarRN;
	
	@Inject
	private FatCampoMedicoAuditAihDAO fatCampoMedicoAuditAihDAO;
	
	@Inject
	private FatEspelhoItemContaHospDAO fatEspelhoItemContaHospDAO;
	
	@Inject
	private FatAtoMedicoAihDAO fatAtoMedicoAihDAO;
	
	@Inject
	private FatEspelhoAihDAO fatEspelhoAihDAO;
	
	@Inject
	private FatAihDAO fatAihDAO;
	
	@Inject
	private FatTipoAihDAO fatTipoAihDAO;
	
	@Inject
	private FatCidContaHospitalarDAO fatCidContaHospitalarDAO;
	
	@Inject
	private FatDiariaUtiDigitadaDAO fatDiariaUtiDigitadaDAO;
	
	@Inject
	private FatContasInternacaoDAO fatContasInternacaoDAO;
	
	@Inject
	private FatExcCaraterInternacaoDAO fatExcCaraterInternacaoDAO;
	
	@Inject
	private FatDocumentoCobrancaAihsDAO fatDocumentoCobrancaAihsDAO;
	
	@Inject
	private FatTiposVinculoDAO fatTiposVinculoDAO;
	
	@Inject
	private FatTipoAtoDAO fatTipoAtoDAO;
	
	@Inject
	private FatContaSugestaoDesdobrDAO fatContaSugestaoDesdobrDAO;
	
	@Inject
	private FatSituacaoSaidaPacienteDAO fatSituacaoSaidaPacienteDAO;
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@EJB
	private FaturamentoON faturamentoON;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;	

	@EJB
	private IParametroFacade parametroFacade;
	
	@Override
	protected Log getLogger() {
		return LOG;
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
	
	public FaturamentoRN getFaturamentoRN() {
		return faturamentoRN;
	}

	public FaturamentoFatkInterfaceAfaRN getFaturamentoFatkInterfaceAfaRN() {
		return faturamentoFatkInterfaceAfaRN;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}

	public IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public ItemContaHospitalarON getItemContaHospitalarON() {
		return itemContaHospitalarON;
	}

	public FatContasHospitalaresDAO getFatContasHospitalaresDAO() {
		return fatContasHospitalaresDAO;
	}

	public FatPerdaItemContaDAO getFatPerdaItemContaDAO() {
		return fatPerdaItemContaDAO;
	}

	public FatProcedHospInternosDAO getFatProcedHospInternosDAO() {
		return fatProcedHospInternosDAO;
	}

	public FatExcCnvGrpItemProcDAO getFatExcCnvGrpItemProcDAO() {
		return fatExcCnvGrpItemProcDAO;
	}

	public FatVlrItemProcedHospCompsDAO getFatVlrItemProcedHospCompsDAO() {
		return fatVlrItemProcedHospCompsDAO;
	}

	public FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO() {
		return fatItensProcedHospitalarDAO;
	}

	public FatConvGrupoItensProcedDAO getFatConvGrupoItensProcedDAO() {
		return fatConvGrupoItensProcedDAO;
	}

	public FatAgrupItemContaDAO getFatAgrupItemContaDAO() {
		return fatAgrupItemContaDAO;
	}

	public FatCompetenciaDAO getFatCompetenciaDAO() {
		return fatCompetenciaDAO;
	}

	public FatItemContaHospitalarDAO getFatItemContaHospitalarDAO() {
		return fatItemContaHospitalarDAO;
	}

	public VFatAssociacaoProcedimentoDAO getvFatAssociacaoProcedimentoDAO() {
		return vFatAssociacaoProcedimentoDAO;
	}

	public FatLogErrorDAO getFatLogErrorDAO() {
		return fatLogErrorDAO;
	}

	public FaturamentoON getFaturamentoON() {
		return faturamentoON;
	}

	public IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	public FaturamentoFatkPrzRN getFaturamentoFatkPrzRN() {
		return faturamentoFatkPrzRN;
	}

	public FaturamentoFatkIchRN getFaturamentoFatkIchRN() {
		return faturamentoFatkIchRN;
	}

	public FaturamentoFatkIctRN getFaturamentoFatkIctRN() {
		return faturamentoFatkIctRN;
	}

	public FaturamentoFatkCgiRN getFaturamentoFatkCgiRN() {
		return faturamentoFatkCgiRN;
	}

	public FaturamentoFatkDciRN getFaturamentoFatkDciRN() {
		return faturamentoFatkDciRN;
	}
	
	protected VerificacaoFaturamentoSusRN getFaturamentoFatkSusRN() {
		return verificacaoFaturamentoSusRN;
	}
	
	public VerificacaoFaturamentoSusRN getVerificacaoFaturamentoSusRN() {
		return verificacaoFaturamentoSusRN;
	}

	public FaturamentoFatkCap2RN getFaturamentoFatkCap2RN() {
		return faturamentoFatkCap2RN;
	}

	public SaldoUtiAtualizacaoRN getSaldoUtiAtualizacaoRN() {
		return saldoUtiAtualizacaoRN;
	}

	public SaldoBancoCapacidadeAtulizacaoRN getSaldoBancoCapacidadeAtulizacaoRN() {
		return saldoBancoCapacidadeAtulizacaoRN;
	}

	public VerificacaoItemProcedimentoHospitalarRN getVerificacaoItemProcedimentoHospitalarRN() {
		return verificacaoItemProcedimentoHospitalarRN;
	}

	public FatCampoMedicoAuditAihDAO getFatCampoMedicoAuditAihDAO() {
		return fatCampoMedicoAuditAihDAO;
	}

	public FatEspelhoItemContaHospDAO getFatEspelhoItemContaHospDAO() {
		return fatEspelhoItemContaHospDAO;
	}

	public FatAtoMedicoAihDAO getFatAtoMedicoAihDAO() {
		return fatAtoMedicoAihDAO;
	}

	public FatEspelhoAihDAO getFatEspelhoAihDAO() {
		return fatEspelhoAihDAO;
	}

	public FatAihDAO getFatAihDAO() {
		return fatAihDAO;
	}

	public FatTipoAihDAO getFatTipoAihDAO() {
		return fatTipoAihDAO;
	}

	public FatCidContaHospitalarDAO getFatCidContaHospitalarDAO() {
		return fatCidContaHospitalarDAO;
	}

	public FatDiariaUtiDigitadaDAO getFatDiariaUtiDigitadaDAO() {
		return fatDiariaUtiDigitadaDAO;
	}

	public FatContasInternacaoDAO getFatContasInternacaoDAO() {
		return fatContasInternacaoDAO;
	}

	public FatExcCaraterInternacaoDAO getFatExcCaraterInternacaoDAO() {
		return fatExcCaraterInternacaoDAO;
	}

	public FatDocumentoCobrancaAihsDAO getFatDocumentoCobrancaAihsDAO() {
		return fatDocumentoCobrancaAihsDAO;
	}

	public FatTiposVinculoDAO getFatTiposVinculoDAO() {
		return fatTiposVinculoDAO;
	}

	public FatTipoAtoDAO getFatTipoAtoDAO() {
		return fatTipoAtoDAO;
	}

	public FatContaSugestaoDesdobrDAO getFatContaSugestaoDesdobrDAO() {
		return fatContaSugestaoDesdobrDAO;
	}

	public FatSituacaoSaidaPacienteDAO getFatSituacaoSaidaPacienteDAO() {
		return fatSituacaoSaidaPacienteDAO;
	}

	public ICadastrosBasicosPacienteFacade getCadastrosBasicosPacienteFacade() {
		return cadastrosBasicosPacienteFacade;
	}
}