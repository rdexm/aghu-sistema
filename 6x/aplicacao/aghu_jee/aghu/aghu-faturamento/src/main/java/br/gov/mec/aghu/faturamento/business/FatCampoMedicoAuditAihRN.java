package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatCampoMedicoAuditAihDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatCampoMedicoAuditAih;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Triggers de <code>FAT_CAMPOS_MEDICO_AUDIT_AIH</code>
 * 
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class FatCampoMedicoAuditAihRN extends AbstractAGHUCrudRn<FatCampoMedicoAuditAih> {
	private static final long serialVersionUID = 1044847923618564298L;
	
	private static final Log LOG = LogFactory.getLog(FatCampoMedicoAuditAihRN.class);

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private FatCampoMedicoAuditAihDAO fatCampoMedicoAuditAihDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	
	/**
	 * ORADB: Trigger FATT_CAH_BRI
	 */
	@Override
	public boolean briPreInsercaoRow(FatCampoMedicoAuditAih entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		this.ajustarDadosAlteracaoEntidade(entidade, dataFimVinculoServidor);
		entidade.setCriadoEm(entidade.getAlteradoEm());
		entidade.setCriadoPor(entidade.getAlteradoPor());
		return true;
	}

	/**
	 * ORADB: Trigger FATT_CAH_BRU
	 */
	@Override
	public boolean bruPreAtualizacaoRow(FatCampoMedicoAuditAih original, FatCampoMedicoAuditAih modificada, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		this.ajustarDadosAlteracaoEntidade(original, dataFimVinculoServidor);
		return true;
	}

	/**
	 * ORADB: Trigger FATT_CAH_ASI
	 */
	@Override
	public boolean asiPosInsercaoStatement(FatCampoMedicoAuditAih entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		this.processCahRows(DominioOperacaoBanco.INS, entidade);
		return true;
	}
	
	/**
	 * ORADB: Procedure PROCESS_CAH_ROWS
	 * 
	 * @param event
	 *  
	 */
	protected void processCahRows(DominioOperacaoBanco event, FatCampoMedicoAuditAih entidade) throws ApplicationBusinessException{
		this.fatPEnforceCahRules(entidade);
	}
	
	/**
	 * ORADB: Procedure FATP_ENFORCE_CAH_RULES
	 * 
	 * @param event
	 *  
	 */
	protected void fatPEnforceCahRules(FatCampoMedicoAuditAih entidade) throws ApplicationBusinessException{
		// Verifica limite de campos no Campo Medico Autor
		rnCahpVerQtdCma(entidade.getId().getEaiSeq(), entidade.getId().getEaiCthSeq());
	}
		
	/**
	 * ORADB: Procedure FATK_CAH_RN.RN_CAHP_VER_QTD_CMA
	 * @param event
	 * @param entidade
	 *  
	 */
	protected void rnCahpVerQtdCma(Integer eaiSeq, Integer eaiCthSeq) throws ApplicationBusinessException{
		// busca a quantidade de registros no CMA por espelho de conta
		// Nro de registros no CMA
		Long vQtdCma = this.getFatCampoMedicoAuditAihDAO().qtdCma(eaiSeq, eaiCthSeq);

	    // Busca quantidade maxima de procedimentos no CMA por espelho de AIH
		AghParametros param = this.buscarAghParametro(AghuParametrosEnum.P_MAX_PROCED_CMA_AIH);
		if (param == null || param.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.PARAMETRO_INVALIDO, AghuParametrosEnum.P_MAX_PROCED_CMA_AIH.toString());
		}
		Integer vMaxQtdCma = param.getVlrNumerico().intValue();

		// Verifica se a quantidade de registros e maior que o valor do parametro
		if(vQtdCma.intValue() > vMaxQtdCma ){
			// Limite de procedimentos no Campo Medico Auditor atingido
			FaturamentoExceptionCode.FAT_00402.throwException();
		}
	}
	
	protected void ajustarDadosAlteracaoEntidade(final FatCampoMedicoAuditAih entidade, final Date dataFimVinculoServidor) throws BaseException  {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		entidade.setAlteradoEm(this.getDataCriacao());
		entidade.setAlteradoPor(servidorLogado != null ? servidorLogado.getUsuario() : null);
	}
	
	protected FatCampoMedicoAuditAihDAO getFatCampoMedicoAuditAihDAO(){
		return fatCampoMedicoAuditAihDAO;
	}
	
	public AghParametros buscarAghParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return this.parametroFacade.buscarAghParametro(parametrosEnum);
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
