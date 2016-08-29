package br.gov.mec.aghu.exames.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExigenciaExameDAO;
import br.gov.mec.aghu.exames.dao.AelExigenciaExameJnDAO;
import br.gov.mec.aghu.model.AelExigenciaExame;
import br.gov.mec.aghu.model.AelExigenciaExameJn;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
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
public class AelExigenciaExameRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelExigenciaExameRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExigenciaExameDAO aelExigenciaExameDAO;
	
	@Inject
	private AelExigenciaExameJnDAO aelExigenciaExameJnDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -468019951548200837L;

	public enum AelExigenciaExameRNCode implements BusinessExceptionCode {

		UNF_EXEC_EXAMES_DEVE_SER_UNICA;

	}
	
	public void persistir(AelExigenciaExame exigencia) throws BaseException {
		if(exigencia.getSeq() == null) {
			this.inserir(exigencia);
		}
		else {
			this.atualizar(exigencia);
		}
	}
	
	public void inserir(AelExigenciaExame exigencia) throws BaseException {
		this.preInserir(exigencia);
		getAelExigenciaExameDAO().persistir(exigencia);
	}
	
	public void atualizar(AelExigenciaExame exigencia) throws ApplicationBusinessException {
		getAelExigenciaExameDAO().merge(exigencia);
		this.posAtualizar(exigencia);
	}
	
	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB AELT_EEE_ARU
	 */
	public void posAtualizar(AelExigenciaExame exigencia) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelExigenciaExame original = getAelExigenciaExameDAO().obterOriginal(exigencia);
		final AelExigenciaExameJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelExigenciaExameJn.class, servidorLogado.getUsuario());
		if(CoreUtil.modificados(original.getSeq(), exigencia.getSeq())
			|| CoreUtil.modificados(original.getIndPedeInternacao(), exigencia.getIndPedeInternacao())
			|| CoreUtil.modificados(original.getIndPedeAihAssinada(), exigencia.getIndPedeAihAssinada())
			|| CoreUtil.modificados(original.getIndSituacao(), exigencia.getIndSituacao())
			|| CoreUtil.modificados(original.getCriadoEm(), exigencia.getCriadoEm())
			|| CoreUtil.modificados(original.getTipoMensagem(), exigencia.getTipoMensagem())
			|| CoreUtil.modificados(original.getUnfExecutaExames(), exigencia.getUnfExecutaExames())
			|| CoreUtil.modificados(original.getServidor(), exigencia.getServidor())
			|| CoreUtil.modificados(original.getUnidadeFuncional(), exigencia.getUnidadeFuncional())
		) {
			jn.setSeq(original.getSeq());
			jn.setIndPedeInternacao(original.getIndPedeInternacao());
			jn.setIndPedeAihAssinada(original.getIndPedeAihAssinada());
			jn.setIndSituacao(original.getIndSituacao());
			jn.setCriadoEm(original.getCriadoEm());
			jn.setTipoMensagem(original.getTipoMensagem());
			jn.setUfeEmaExaSigla(original.getUnfExecutaExames().getId().getEmaExaSigla());
			jn.setUfeEmaManSeq(original.getUnfExecutaExames().getId().getEmaManSeq());
			jn.setUfeUnfSeq(original.getUnfExecutaExames().getId().getUnfSeq().getSeq());
			jn.setUnfSeq(original.getUnidadeFuncional().getSeq());
			jn.setSerMatricula(original.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(original.getServidor().getId().getVinCodigo());
			getAelExigenciaExameJnDAO().persistir(jn);
		}
	}
	
	
	/**
	 * @ORADB AELT_EEE_BRI
	 */
	public void preInserir(AelExigenciaExame exigencia) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.validarAelUnfExecutaExamesUnica(exigencia.getUnfExecutaExames());
		exigencia.setCriadoEm(new Date());
		if(exigencia.getServidor() == null) {
			exigencia.setServidor(servidorLogado);
		}
	}
	
	public void validarAelUnfExecutaExamesUnica(AelUnfExecutaExames unfExecExame) throws BaseException {
		List<AelExigenciaExame> lista = getAelExigenciaExameDAO().obterPorUnfExecutaExames(unfExecExame);
		if(!lista.isEmpty()) {
			throw new ApplicationBusinessException(AelExigenciaExameRNCode.UNF_EXEC_EXAMES_DEVE_SER_UNICA);
		}
	}
	
	protected AelExigenciaExameJnDAO getAelExigenciaExameJnDAO() {
		return aelExigenciaExameJnDAO;
	}
	
	protected AelExigenciaExameDAO getAelExigenciaExameDAO() {
		return aelExigenciaExameDAO;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
