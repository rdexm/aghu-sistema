package br.gov.mec.aghu.blococirurgico.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.MbcDescricaoTecnicasRN.MbcDescricaoTecnicasRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoTecnicaJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoTecnicasDAO;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoTecnicaJn;
import br.gov.mec.aghu.model.MbcDescricaoTecnicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MbcDescricaoTecnicasRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcDescricaoTecnicasRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcDescricaoTecnicasDAO mbcDescricaoTecnicasDAO;

	@Inject
	private MbcDescricaoTecnicaJnDAO mbcDescricaoTecnicaJnDAO;


	@EJB
	private ProfDescricoesRN profDescricoesRN;

	private static final long serialVersionUID = -639550022022971803L;
	
	protected enum MbcDescricaoTecnicasRNExceptionCode implements BusinessExceptionCode {
		MBC_00701
	}	
	
	public void excluirMbcDescricaoTecnicas(final MbcDescricaoTecnicas mbcDescricaoTecnicas) throws ApplicationBusinessException, ApplicationBusinessException{
		antesDeExcluir(mbcDescricaoTecnicas);
		getMbcDescricaoTecnicasDAO().remover(mbcDescricaoTecnicas);
		depoisDeExcluir(mbcDescricaoTecnicas);
	}
	
	/**
	 * ORADB: MBCT_DTC_BRD
	 */
	void antesDeExcluir(final MbcDescricaoTecnicas mbcDescricaoTecnicas) throws ApplicationBusinessException, ApplicationBusinessException{
		// Verifica se a descricao técnica está sendo alterada ou excluida pelo usuario que a criou
		verificaDescricaoTecnica(mbcDescricaoTecnicas.getMbcDescricaoCirurgica());
	}

	
	/**
	 * Verifica se a descricao técnica está sendo alterada ou excluida pelo usuario que a criou
	 * 
	 * ORADB: MBCK_DTC_RN.RN_DTCP_VER_USER
	 */
	public void verificaDescricaoTecnica(final MbcDescricaoCirurgica mbcDescricaoCirurgica) throws ApplicationBusinessException{
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if(!CoreUtil.igual(mbcDescricaoCirurgica.getServidor(), servidorLogado)){
			throw new ApplicationBusinessException(MbcDescricaoTecnicasRNExceptionCode.MBC_00701);
		}
	}
	
	/**
	 * ORADB: MBCT_DTC_ARD
	 */
	void depoisDeExcluir(final MbcDescricaoTecnicas mbcDescricaoTecnicas) throws ApplicationBusinessException{
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final MbcDescricaoTecnicaJn jn = createJournal(mbcDescricaoTecnicas, servidorLogado.getUsuario(), DominioOperacoesJournal.DEL);
		getMbcDescricaoTecnicaJnDAO().persistir(jn);
	}
	
	@SuppressWarnings("ucd")
	public void alterarMbcDescricaoTecnicas(final MbcDescricaoTecnicas descricaoTecnica)
			throws ApplicationBusinessException, ApplicationBusinessException {
		
		final MbcDescricaoTecnicasDAO dao = getMbcDescricaoTecnicasDAO();
		final MbcDescricaoTecnicas oldDescricaoTecnica = dao.obterOriginal(descricaoTecnica);
		
		antesDeAlterar(descricaoTecnica);
		dao.atualizar(descricaoTecnica);
		depoisDeAlterar(descricaoTecnica, oldDescricaoTecnica);
	}
	
	/**
	 * ORADB: MBCT_DTC_BRU
	 */
	void antesDeAlterar(final MbcDescricaoTecnicas descricaoTecnica) throws ApplicationBusinessException, ApplicationBusinessException{
		// Verifica se a descricao técnica está sendo alterada ou excluida pelo usuario que a criou
		verificaDescricaoTecnica(descricaoTecnica.getMbcDescricaoCirurgica());
	}

	/**
	 * ORADB: MBCT_DTC_ARU
	 */
	void depoisDeAlterar(final MbcDescricaoTecnicas descricaoTecnica, final MbcDescricaoTecnicas oldDescricaoTecnica) throws ApplicationBusinessException{
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if(!CoreUtil.igual(descricaoTecnica.getId().getCrgSeq(), oldDescricaoTecnica.getId().getCrgSeq()) ||
				!CoreUtil.igual(descricaoTecnica.getId().getSeqp(), oldDescricaoTecnica.getId().getSeqp()) ||
				!CoreUtil.igual(descricaoTecnica.getDescricaoTecnica(), oldDescricaoTecnica.getDescricaoTecnica()) 
				){
			
			final MbcDescricaoTecnicaJn jn = createJournal(oldDescricaoTecnica, servidorLogado.getUsuario(), DominioOperacoesJournal.UPD);
			getMbcDescricaoTecnicaJnDAO().persistir(jn);
		}
	}
	
	private MbcDescricaoTecnicaJn createJournal(final MbcDescricaoTecnicas mbcDescricaoTecnicas,
			final String usuarioLogado, DominioOperacoesJournal dominio) {
		
		final MbcDescricaoTecnicaJn journal =  BaseJournalFactory.getBaseJournal(dominio, 
				MbcDescricaoTecnicaJn.class, usuarioLogado);
		
		journal.setDcgCrgSeq(mbcDescricaoTecnicas.getId().getCrgSeq());
		journal.setDcgSeqp(mbcDescricaoTecnicas.getId().getSeqp());
		journal.setDescricaoTecnica(mbcDescricaoTecnicas.getDescricaoTecnica());
		
		return journal;
	}

	@SuppressWarnings("ucd")
	public void inserirMbcDescricaoTecnicas( final MbcDescricaoTecnicas descricaoTecnica)
												throws ApplicationBusinessException {
		
		executarAntesDeInserir(descricaoTecnica);
		getMbcDescricaoTecnicasDAO().persistir(descricaoTecnica);
	}
	
	/**
	 * ORADB: MBCT_DTC_BRI
	 */
	private void executarAntesDeInserir(final MbcDescricaoTecnicas descricaoTecnica)
			throws ApplicationBusinessException {

		// Verifica se o usuario que está fazendo o insert,é quem criou a descricao cirurgica
		getProfDescricoesRN().verificarServidorLogadoRealizaDescricaoCirurgica( descricaoTecnica.getId().getCrgSeq(),
				  																descricaoTecnica.getId().getSeqp());
	}
	
	protected MbcDescricaoTecnicasDAO getMbcDescricaoTecnicasDAO() {
		return mbcDescricaoTecnicasDAO;
	}
	
	protected ProfDescricoesRN getProfDescricoesRN() {
		return profDescricoesRN;
	}

	protected MbcDescricaoTecnicaJnDAO getMbcDescricaoTecnicaJnDAO() {
		return mbcDescricaoTecnicaJnDAO;
	}
}
