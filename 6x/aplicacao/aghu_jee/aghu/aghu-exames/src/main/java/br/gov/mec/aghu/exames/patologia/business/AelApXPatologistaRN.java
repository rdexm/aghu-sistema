package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.exames.dao.AelApXPatologistaDAO;
import br.gov.mec.aghu.exames.dao.AelApXPatologistaJnDAO;
import br.gov.mec.aghu.exames.dao.AelPatologistaDAO;
import br.gov.mec.aghu.model.AelApXPatologista;
import br.gov.mec.aghu.model.AelApXPatologistasJn;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelApXPatologistaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelApXPatologistaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelPatologistaDAO aelPatologistaDAO;
	
	@Inject
	private AelApXPatologistaJnDAO aelApXPatologistaJnDAO;
	
	@Inject
	private AelApXPatologistaDAO aelApXPatologistaDAO;

	private static final long serialVersionUID = 4879378548867391256L;
	
	
	public enum AelApXPatologistaRNExceptionCode implements
			BusinessExceptionCode {
		AEL_02688,AEL_02689 
	}

	public void inserirAelApXPatologistaRN(final AelApXPatologista aelApXPatologista) throws BaseException {
		this.executarAntesInserirAelApXPatologistaRN(aelApXPatologista);
		this.getAelApXPatologistaDAO().persistir(aelApXPatologista);
		this.executarDepoisInserirAelApXPatologistaRN(aelApXPatologista);
	}
	
	public void atulizarAelApXPatologistaRN(final AelApXPatologista aelApXPatologistaNew, final AelApXPatologista aelApXPatologistaOld) throws BaseException {
		this.getAelApXPatologistaDAO().merge(aelApXPatologistaNew);
		this.executarDepoisAtualizarAelApXPatologistaRN(aelApXPatologistaNew, aelApXPatologistaOld);
	}
	
	public void excluirAelApXPatologista(final AelApXPatologista aelApXPatologista) throws ApplicationBusinessException {
		this.getAelApXPatologistaDAO().remover(aelApXPatologista);
		this.executarDepoisExcluirAelApXPatologistaRN(aelApXPatologista);
	}
	
	// @ORADB AELT_LO5_BRI
	private void executarAntesInserirAelApXPatologistaRN(final AelApXPatologista aelApXPatologista) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelApXPatologista.setCriadoEm(new Date());
		aelApXPatologista.setServidor(servidorLogado);
	}

	// @ORADB AELT_LO5_ASI
	private void executarDepoisInserirAelApXPatologistaRN(final AelApXPatologista aelApXPatologista) throws ApplicationBusinessException {
		this.aelPEnforceLo5Rules(aelApXPatologista, null, DominioOperacaoBanco.INS);
	}
	
	// @ORADB AELT_LO5_ARD
	private void executarDepoisExcluirAelApXPatologistaRN(final AelApXPatologista aelApXPatologista) throws ApplicationBusinessException {
		this.createJournal(aelApXPatologista, DominioOperacoesJournal.DEL);
	}
	
	// @ORADB AELT_LO5_ARU
	private void executarDepoisAtualizarAelApXPatologistaRN(final AelApXPatologista aelApXPatologistaNew, final AelApXPatologista aelApXPatologistaOld) throws ApplicationBusinessException {
		// @ORADB AELT_LO5_ASU
		this.aelPEnforceLo5Rules(aelApXPatologistaNew, aelApXPatologistaOld, DominioOperacaoBanco.UPD);
		if(CoreUtil.modificados(aelApXPatologistaNew.getId().getLumSeq(), aelApXPatologistaOld.getId().getLumSeq())
				|| CoreUtil.modificados(aelApXPatologistaNew.getId().getLuiSeq(), aelApXPatologistaOld.getId().getLuiSeq())
				|| CoreUtil.modificados(aelApXPatologistaNew.getCriadoEm(), aelApXPatologistaOld.getCriadoEm()) 
				|| CoreUtil.modificados(aelApXPatologistaNew.getServidor(), aelApXPatologistaOld.getServidor()) ) {
			createJournal(aelApXPatologistaOld, DominioOperacoesJournal.UPD);
		}
	}
	
	// @ORADB aelP_enforce_lo5_rules
	public void aelPEnforceLo5Rules(final AelApXPatologista aelApXPatologistaNew, final AelApXPatologista aelApXPatologistaOld,
			final DominioOperacaoBanco operacao) throws ApplicationBusinessException {
		if (DominioOperacaoBanco.UPD.equals(operacao)) {
			if (CoreUtil.modificados(aelApXPatologistaNew.getId().getLumSeq(), aelApXPatologistaOld.getId().getLumSeq())
					|| CoreUtil.modificados(aelApXPatologistaNew.getId().getLuiSeq(), aelApXPatologistaOld.getId().getLuiSeq())) {
				this.verificaPatologista(aelApXPatologistaNew.getId().getLumSeq(), aelApXPatologistaNew.getId().getLuiSeq());
			}
		} else if (DominioOperacaoBanco.INS.equals(operacao)) {
			this.verificaPatologista(aelApXPatologistaNew.getId().getLumSeq(), aelApXPatologistaNew.getId().getLuiSeq());
		}
	}

	// @ORADB aelk_lo5_rn.rn_lo5p_ver_patol
	public void verificaPatologista(final Long codigoAnatomoPatologico, final Integer codigoPatologista) throws ApplicationBusinessException {
		final AelPatologistaDAO aelPatologistaDAO = this.getAelPatologistaDAO();
		final AelPatologista aelPatologista = aelPatologistaDAO.obterPatologistaAtivo(codigoPatologista);
		if(DominioFuncaoPatologista.R.equals(aelPatologista.getFuncao())){
			if(this.getAelApXPatologistaDAO().contarPatologistaAtivoPorFuncaoPatologista(codigoAnatomoPatologico, codigoPatologista, DominioFuncaoPatologista.R) > 0){
				throw new ApplicationBusinessException(AelApXPatologistaRNExceptionCode.AEL_02688);
			}
		} else if(DominioFuncaoPatologista.C.equals(aelPatologista.getFuncao()) || DominioFuncaoPatologista.P.equals(aelPatologista.getFuncao())) {
			if(this.getAelApXPatologistaDAO().contarPatologistaAtivoPorFuncaoPatologista(codigoAnatomoPatologico, codigoPatologista, DominioFuncaoPatologista.C, DominioFuncaoPatologista.P) > 0){
				throw new ApplicationBusinessException(AelApXPatologistaRNExceptionCode.AEL_02689);
			}
		}
	}


	protected void createJournal(final AelApXPatologista reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelApXPatologistasJn journal = BaseJournalFactory.getBaseJournal(operacao, AelApXPatologistasJn.class, servidorLogado.getUsuario());

		journal.setLuiSeq(reg.getId().getLuiSeq());
		journal.setLumSeq(reg.getId().getLumSeq());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());

		this.getAelApXPatologistaJnDAO().persistir(journal);
	}
	
	protected AelPatologistaDAO getAelPatologistaDAO() {
		return aelPatologistaDAO;
	}

	protected AelApXPatologistaDAO getAelApXPatologistaDAO() {
		return aelApXPatologistaDAO;
	}

	protected AelApXPatologistaJnDAO getAelApXPatologistaJnDAO() {
		return aelApXPatologistaJnDAO;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
