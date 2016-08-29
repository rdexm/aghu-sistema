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
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Triggers de:<br/>
 * ORADB: <code>FAT_ATOS_MEDICOS_AIH</code>
 * 
 * @author gandriotti
 * 
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class AtoMedicoAihRN extends AbstractAGHUCrudRn<FatAtoMedicoAih> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5784073714590947214L;
	

	private static final Log LOG = LogFactory.getLog(AtoMedicoAihRN.class);

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatAtoMedicoAihDAO fatAtoMedicoAihDAO;
	
	@EJB
	private IParametroFacade parametroFacade;

	

	protected FatAtoMedicoAihDAO getFatAtoMedicoAihDAO() {
		return fatAtoMedicoAihDAO;
	}
	
	/**
	 * TODO
	 * ORADB: <code>FATT_AAM_ARI</code>
	 */
	@Override
	public boolean ariPosInsercaoRow(FatAtoMedicoAih entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		return true;
	}

	protected void ajustarAlteracao(final FatAtoMedicoAih entidade) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		entidade.setAlteradoEm(this.getDataCriacao());
		entidade.setAlteradoPor(servidorLogado.getUsuario());
	}

	/**
	 * ORADB: <code>FATT_AAM_BRI</code> <br/>
	 * ORADB: <code>FATT_AAM_ARI</code> <br/>
	 */
	@Override
	public boolean briPreInsercaoRow(final FatAtoMedicoAih entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		boolean result = false;

		this.ajustarAlteracao(entidade);
		entidade.setCriadoEm(entidade.getAlteradoEm());
		entidade.setCriadoPor(entidade.getAlteradoPor());

		result = true;

		return result;
	}

	/**
	 * ORADB: <code>FATT_AAM_BRU</code>
	 */
	@Override
	public boolean bruPreAtualizacaoRow(
			final FatAtoMedicoAih original,
			final FatAtoMedicoAih modificada, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		boolean result = false;

		this.ajustarAlteracao(modificada);
		result = true;

		return result;
	}

	protected long obterQtdFatAtoMedicoAihPorEai(
			final Integer eaiSeq,
			final Integer eaiCthSeq) {

		Long result = null;
		FatAtoMedicoAihDAO dao = null;

		dao = this.getFatAtoMedicoAihDAO();
		result = dao.obterQtdPorEai(
				eaiSeq, eaiCthSeq);

		return (result != null
				? result.longValue()
				: 0l);
	}

	protected int obterAghParametroMaxAtoMedicoAih()
			throws ApplicationBusinessException {

		int result = 0;
		AghParametros param = this.buscarAghParametro(AghuParametrosEnum.P_MAX_ATO_MEDICO_AIH);
		if(param.getVlrNumerico() == null){
			throw new ApplicationBusinessException(FaturamentoExceptionCode.PARAMETRO_INVALIDO, AghuParametrosEnum.P_MAX_ATO_MEDICO_AIH);
		}
		result = param.getVlrNumerico().intValue();

		return result;
	}

	/**
	 * ORADB: <code>FATP_ENFORCE_AAM_RULES</code> <br/>
	 * ORADB: <code>fatK_aam.process_aam_rows</code> <br/>
	 * ORADB: <code>FATK_AAM_RN.RN_AAMP_VER_QTD_ATOS</code> <br/>
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 * @see FaturamentoExceptionCode#FAT_00406
	 */
	protected boolean verificarQtdAtosDentroLimite(
			final FatAtoMedicoAih entidade)
			throws ApplicationBusinessException {

		boolean result = false;
		long qtd = 0;
		int maxQtd = 0;

		// Busca quantidade maxima de atos medicos por página de espelho de AIH
		maxQtd = this.obterAghParametroMaxAtoMedicoAih();
		qtd = this.obterQtdFatAtoMedicoAihPorEai(
				entidade.getId().getEaiSeq(), entidade.getId().getEaiCthSeq());
		if (qtd > maxQtd) {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00406,
					Integer.valueOf(maxQtd), Long.valueOf(qtd));
	}
		result = true;
		return result;	
	}
	


	/**
	 * ORADB: <code>FATT_AAM_BSI</code> <br/>
	 * ORADB: <code>FATT_AAM_ASI</code> <br/>
	 */
	@Override
	public boolean bsiPreInsercaoStatement(final FatAtoMedicoAih entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		boolean result = false;

		result = this.verificarQtdAtosDentroLimite(entidade);

		return result;
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
