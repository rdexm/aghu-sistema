package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.AmbulatorioRN.AmbulatorioRNExceptionCode;
import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamProcedimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ProcedimentoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ProcedimentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private MamProcedimentoDAO mamProcedimentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4162715260148169624L;

	public void inserir(MamProcedimento procedimento) throws BaseException {
		preInsert(procedimento);
		getMamProcedimentoDAO().persistir(procedimento);
		aposInserir(procedimento, null, DominioOperacoesJournal.INS);
	}
	
	/**
	 * Trigger
	 * ORADB: MAMT_PRD_BRI
	 * @throws ApplicationBusinessException 
	 */
	public void preInsert(MamProcedimento procedimento) throws BaseException  {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		procedimento.setCriadoEm(new Date());
		if(procedimento.getServidor() == null) {
			procedimento.setServidor(servidorLogado);
		}
		if(procedimento.getSituacao() == null) {
			procedimento.setSituacao(DominioSituacao.A);
		}
		if(procedimento.getGenerico() == null) {
			procedimento.setGenerico(true);
		}
	}
	
	/**
	 * Trigger
	 * ORADB: MAMT_PRD_ASI
	 */
	public void aposInserir(MamProcedimento procedimento, MamProcedimento procedimentoOld, DominioOperacoesJournal operacao) throws BaseException {
		this.enforceProcedimento(procedimento, procedimentoOld, operacao);
	}

	/**
	 * Enforce
	 * ORADB: MAMP_ENFORCE_PRD_RUL
	 */
	public void enforceProcedimento(MamProcedimento procedimento, MamProcedimento procedimentoOld, DominioOperacoesJournal operacao) throws BaseException {
		if(DominioOperacoesJournal.INS.equals(operacao)) {
			verificaProcedimento(procedimento);
		} else if(DominioOperacoesJournal.UPD.equals(operacao)){
			if(CoreUtil.modificados(procedimento.getProcedEspecialDiverso(), procedimentoOld.getProcedEspecialDiverso())) {
				verificaProcedimento(procedimento);
			}
		}
	}

	/**
	 * Procedure
	 * ORADB: MAMK_PRD_RN.RN_PRDP_VER_PED
	 */
	public void verificaProcedimento(MamProcedimento procedimento) throws ApplicationBusinessException {
		if(procedimento.getProcedEspecialDiverso() != null && procedimento.getProcedEspecialDiverso().getSeq() != null) {
			List<MamProcedimento> lista = getMamProcedimentoDAO().obterProcedimentosPeloProcedimentoEspecialDiverso(procedimento.getSeq(), procedimento.getProcedEspecialDiverso().getSeq());
			if(!lista.isEmpty()) {
				throw new ApplicationBusinessException(AmbulatorioRNExceptionCode.MAM_01160);
			}
		}
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected MamProcedimentoDAO getMamProcedimentoDAO() {
		return mamProcedimentoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
