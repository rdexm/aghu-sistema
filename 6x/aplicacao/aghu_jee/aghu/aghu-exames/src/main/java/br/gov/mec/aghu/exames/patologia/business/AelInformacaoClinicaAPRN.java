package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelInformacaoClinicaAPJnDAO;
import br.gov.mec.aghu.exames.dao.AelInformacaoClinicaApDAO;
import br.gov.mec.aghu.model.AelInformacaoClinicaAP;
import br.gov.mec.aghu.model.AelInformacaoClinicaAPJn;
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
public class AelInformacaoClinicaAPRN extends BaseBusiness {

	@EJB
	private LaudoUnicoRN laudoUnicoRN;
	
	private static final Log LOG = LogFactory.getLog(AelInformacaoClinicaAPRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelInformacaoClinicaAPJnDAO aelInformacaoClinicaAPJnDAO;
	
	@Inject
	private AelInformacaoClinicaApDAO aelInformacaoClinicaApDAO;

	private static final long serialVersionUID = 889954488351975158L;

	private enum AelInformacaoClinicaAPRNExceptionCode implements BusinessExceptionCode {
		MSG_INFORME_AS_INFORMACOES_CLINICAS
	}
	
	public void persistir(final AelInformacaoClinicaAP aelInformacaoClinicaAP) throws BaseException {
		if (StringUtils.isEmpty(aelInformacaoClinicaAP.getInformacaoClinica())) {
			throw new ApplicationBusinessException(AelInformacaoClinicaAPRNExceptionCode.MSG_INFORME_AS_INFORMACOES_CLINICAS);
		}
		
		if (aelInformacaoClinicaAP.getSeq() == null) {
			this.inserir(aelInformacaoClinicaAP);
			
		} else {
			final AelInformacaoClinicaAP aelInformacaoClinicaAPOld = getAelInformacaoClinicaApDAO().obterOriginal(aelInformacaoClinicaAP.getSeq());
			this.atualizar(aelInformacaoClinicaAP, aelInformacaoClinicaAPOld);
		}
	}

	public void atualizar(final AelInformacaoClinicaAP aelInformacaoClinicaAP, final AelInformacaoClinicaAP aelInformacaoClinicaAPOld) throws BaseException {
		this.executarAposAtualizar(aelInformacaoClinicaAP, aelInformacaoClinicaAPOld);
	}

	public void inserir(final AelInformacaoClinicaAP aelInformacaoClinicaAP) throws BaseException {
		this.executarAntesInserir(aelInformacaoClinicaAP);
		getAelInformacaoClinicaApDAO().persistir(aelInformacaoClinicaAP);
	}
	
	public void excluir(final AelInformacaoClinicaAP aelInformacaoClinicaAP, String usuarioLogado) throws BaseException {
		final AelInformacaoClinicaApDAO dao = getAelInformacaoClinicaApDAO();
		dao.remover(aelInformacaoClinicaAP);
		this.executarAposExcluir(aelInformacaoClinicaAP, usuarioLogado);
	}
	
	private void executarAposExcluir(AelInformacaoClinicaAP aelInformacaoClinicaAP, String usuarioLogado) {
		insereJournal(aelInformacaoClinicaAP, DominioOperacoesJournal.DEL, usuarioLogado);		
	}

	private void insereJournal(AelInformacaoClinicaAP aelInformacaoClinicaAP, DominioOperacoesJournal operacao, String usuarioLogado) {
		
		AelInformacaoClinicaAPJn jn = BaseJournalFactory.getBaseJournal(operacao, AelInformacaoClinicaAPJn.class, usuarioLogado);
		jn.setSeq(aelInformacaoClinicaAP.getSeq());
		jn.setInformacaoClinica(aelInformacaoClinicaAP.getInformacaoClinica());
		jn.setCriadoEm(aelInformacaoClinicaAP.getCriadoEm());
		jn.setRapServidores(aelInformacaoClinicaAP.getRapServidores());
		jn.setAelExameAp(aelInformacaoClinicaAP.getAelExameAp());
		getAelInformacaoClinicaApJnDAO().persistir(jn);
	}
		
	private void executarAntesInserir(final AelInformacaoClinicaAP aelInformacaoClinicaAP) throws BaseException {
		
		aelInformacaoClinicaAP.setCriadoEm(new Date());

		if(aelInformacaoClinicaAP.getRapServidores() == null) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			aelInformacaoClinicaAP.setRapServidores(servidorLogado);
		}
		
		getLaudoUnicoRN().rnLuopVerEtapLau(aelInformacaoClinicaAP.getAelExameAp().getSeq());
		/**
		 * Retirado em função do bug #???? - Esta validação será garantida pelas permissõe/perfis de usuário.
		 * 
		 * getLaudoUnicoRN().rnLuopVerServidor(aelInformacaoClinicaAP.getRapServidores());
		 */
	}

	private void executarAposAtualizar(AelInformacaoClinicaAP aelInformacaoClinicaAP, final AelInformacaoClinicaAP aelInformacaoClinicaAPOld) throws BaseException {
		aelInformacaoClinicaAP = getAelInformacaoClinicaApDAO().merge(aelInformacaoClinicaAP);
		if(CoreUtil.modificados(aelInformacaoClinicaAP.getAelExameAp(), aelInformacaoClinicaAPOld.getAelExameAp()) ||
				CoreUtil.modificados(aelInformacaoClinicaAP.getInformacaoClinica(), aelInformacaoClinicaAPOld.getInformacaoClinica())
		) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AelInformacaoClinicaAPJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelInformacaoClinicaAPJn.class, servidorLogado.getUsuario());
			jn.setSeq(aelInformacaoClinicaAPOld.getSeq());
			jn.setInformacaoClinica(aelInformacaoClinicaAPOld.getInformacaoClinica());
			jn.setCriadoEm(aelInformacaoClinicaAPOld.getCriadoEm());
			jn.setRapServidores(aelInformacaoClinicaAPOld.getRapServidores());
			jn.setAelExameAp(aelInformacaoClinicaAPOld.getAelExameAp());
			getAelInformacaoClinicaApJnDAO().persistir(jn);
		}

	}

	private AelInformacaoClinicaApDAO getAelInformacaoClinicaApDAO() {
		return aelInformacaoClinicaApDAO;
	}

	private LaudoUnicoRN getLaudoUnicoRN() {
		return laudoUnicoRN;
	}
	
	private AelInformacaoClinicaAPJnDAO getAelInformacaoClinicaApJnDAO() {
		return aelInformacaoClinicaAPJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
