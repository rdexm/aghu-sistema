package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.faturamento.dao.FatContasInternacaoDAO;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Triggers de <code>FAT_CONTAS_INTERNACAO</code>
 * 
 */
@SuppressWarnings({ "PMD.HierarquiaONRNIncorreta" })
@Stateless
public class FatContaInternacaoRN extends AbstractAGHUCrudRn<FatContasInternacao> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8157913022025254153L;

	private static final Log LOG = LogFactory.getLog(FatContaInternacaoRN.class);

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private FatContasInternacaoDAO fatContasInternacaoDAO;

	@Inject
	private ObjetosOracleDAO objetosOracleDAO;
	
	/**
	 * ORADB: Trigger FATT_COI_BRI
	 */
	@Override
	public boolean briPreInsercaoRow(FatContasInternacao entidade,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		/* Sequence setada na entidade */
		return true;
	}

	/**
	 * ORADB: Trigger FATT_COI_ASI
	 */
	@Override
	public boolean asiPosInsercaoStatement(FatContasInternacao entidade,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		this.processCoiRows(DominioOperacaoBanco.INS, entidade);
		return true;
	}

	/**
	 * ORADB: Procedure PROCESS_COI_ROWS
	 * 
	 * @param event
	 * 
	 */
	protected void processCoiRows(DominioOperacaoBanco event,
			FatContasInternacao entidade) throws ApplicationBusinessException {
		this.fatPEnforceCoiRules(event, entidade);
	}

	/**
	 * ORADB: Procedure FATP_ENFORCE_COI_RULES
	 * 
	 * @param event
	 * 
	 */
	protected void fatPEnforceCoiRules(DominioOperacaoBanco event, FatContasInternacao entidade)
			throws ApplicationBusinessException {
		if (DominioOperacaoBanco.INS.equals(event)) {
			Integer intSeq = entidade.getInternacao() != null ? entidade.getInternacao().getSeq() : null;
			Integer cthSeq = entidade.getContaHospitalar() != null ? entidade.getContaHospitalar().getSeq() : null;
			
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			getObjetosOracleDAO().geraContaInternacaoFaturamentoConvenios(intSeq, cthSeq, servidorLogado);
		}
	}

	public void buscarSangue(final Integer seqContaInternacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		getFatContasInternacaoDAO().buscarSangue(seqContaInternacao, servidorLogado.getUsuario());
	}

	protected FatContasInternacaoDAO getFatContasInternacaoDAO() {
		return fatContasInternacaoDAO;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}
}
