package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtAchadoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtAchadoJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtGrupoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtGrupoJnDAO;
import br.gov.mec.aghu.model.PdtAchado;
import br.gov.mec.aghu.model.PdtAchadoJn;
import br.gov.mec.aghu.model.PdtGrupo;
import br.gov.mec.aghu.model.PdtGrupoJn;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class GruposAchadosRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(GruposAchadosRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtGrupoJnDAO pdtGrupoJnDAO;

	@Inject
	private PdtAchadoJnDAO pdtAchadoJnDAO;

	@Inject
	private PdtAchadoDAO pdtAchadoDAO;

	@Inject
	private PdtGrupoDAO pdtGrupoDAO;


	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7440955667377144825L;
	
	protected enum GruposAchadosRNExceptionCode implements BusinessExceptionCode {
		PDT_00109
		;
	}
	
	/**
	 * @ORADB TRIGGER PDTT_DGR_BRI
	 */
	public void setarPdtGrupoCriadoEm(PdtGrupo grupo) {
		grupo.setCriadoEm(new Date());		
	}

	/**
	 * @ORADB TRIGGER PDTT_DGR_BRI
	 * @ORADB TRIGGER PDTT_DGR_BRU
	 */
	public void setarPdtGrupoServidorLogado(PdtGrupo grupo) throws ApplicationBusinessException {
		grupo.setRapServidores(servidorLogadoFacade.obterServidorLogado());		
	} 
	
	/**
	 * @ORADB TRIGGER PDTT_DGR_ARU	
	 */
	public void posUpdatePdtGrupo(PdtGrupo grupo) {
		PdtGrupo original = getPdtGrupoDAO().obterOriginal(grupo);
		
		if(CoreUtil.modificados(grupo.getId().getDptSeq(), original.getId().getDptSeq()) ||
				CoreUtil.modificados(grupo.getId().getSeqp(), original.getId().getSeqp()) ||
				CoreUtil.modificados(grupo.getDescricao(), original.getDescricao()) ||
				CoreUtil.modificados(grupo.getIndSituacao(), original.getIndSituacao()) ||
				CoreUtil.modificados(grupo.getCriadoEm(), original.getCriadoEm()) ||
				CoreUtil.modificados(grupo.getRapServidores().getId().getMatricula(), original.getRapServidores().getId().getMatricula()) ||
				CoreUtil.modificados(grupo.getRapServidores().getId().getVinCodigo(), original.getRapServidores().getId().getVinCodigo())){
			
			PdtGrupoJn grupoJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, PdtGrupoJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
			grupoJn.setDptSeq(original.getId().getDptSeq());
			grupoJn.setSeqp(original.getId().getSeqp());
			grupoJn.setDescricao(original.getDescricao());
			grupoJn.setIndSituacao(original.getIndSituacao().toString());
			grupoJn.setCriadoEm(original.getCriadoEm());
			grupoJn.setSerMatricula(original.getRapServidores().getId().getMatricula());
			grupoJn.setSerVinCodigo(original.getRapServidores().getId().getVinCodigo());
			
			getPdtGrupoJnDAO().persistir(grupoJn);
		}
		
	}	
	
	/**
	 * @ORADB TRIGGER PDTT_DGR_BRU
	 * @ORADB PROCEDURE PDTK_PDT_RN.RN_PDTP_VER_DESC
	 */
	public void verificarDescricaoAlteradaPdtGrupo(PdtGrupo grupo) throws ApplicationBusinessException {
		PdtGrupo original = getPdtGrupoDAO().obterOriginal(grupo);
		
		if(CoreUtil.modificados(grupo.getDescricao(), original.getDescricao())){
			throw new ApplicationBusinessException(GruposAchadosRNExceptionCode.PDT_00109);			
		}		
	}	
	
	/**
	 * @ORADB TRIGGER PDTT_PAH_BRI
	 */
	public void setarPdtAchadoCriadoEm(PdtAchado achado) {
		achado.setCriadoEm(new Date());		
	}
	
	/**
	 * @ORADB TRIGGER PDTT_PAH_BRI
	 * @ORADB TRIGGER PDTT_PAH_BRU
	 */
	public void setarPdtAchadoServidorLogado(PdtAchado achado) throws ApplicationBusinessException {
		achado.setRapServidores(servidorLogadoFacade.obterServidorLogado());		
	} 	

	/**
	 * @ORADB TRIGGER PDTT_PAH_BRU
	 * @ORADB PROCEDURE PDTK_PDT_RN.RN_PDTP_VER_DESC
	 */
	public void verificarDescricaoAlteradaPdtAchado(PdtAchado achado) throws ApplicationBusinessException {
		PdtAchado original = getPdtAchadoDAO().obterOriginal(achado);
		
		if(CoreUtil.modificados(achado.getDescricao(), original.getDescricao())){
			throw new ApplicationBusinessException(GruposAchadosRNExceptionCode.PDT_00109);			
		}		
	}	

	/**
	 * @ORADB TRIGGER PDTT_PAH_ARU	
	 */
	public void posUpdatePdtAchado(PdtAchado achado) {
		PdtAchado original = getPdtAchadoDAO().obterOriginal(achado);
		
		if(CoreUtil.modificados(achado.getId().getDgrDptSeq(), original.getId().getDgrDptSeq()) ||
				CoreUtil.modificados(achado.getId().getDgrSeqp(), original.getId().getDgrSeqp()) ||
				CoreUtil.modificados(achado.getId().getSeqp(), original.getId().getSeqp()) ||
				CoreUtil.modificados(achado.getDescricao(), original.getDescricao()) ||
				CoreUtil.modificados(achado.getIndSituacao(), original.getIndSituacao()) ||
				CoreUtil.modificados(achado.getIndNormal(), original.getIndNormal()) ||
				CoreUtil.modificados(achado.getMensagemAlerta(), original.getMensagemAlerta()) ||
				CoreUtil.modificados(achado.getIndExigeComplemento(), original.getIndExigeComplemento()) ||
				CoreUtil.modificados(achado.getCriadoEm(), original.getCriadoEm()) ||
				CoreUtil.modificados(achado.getRapServidores().getId().getMatricula(), original.getRapServidores().getId().getMatricula()) ||
				CoreUtil.modificados(achado.getRapServidores().getId().getVinCodigo(), original.getRapServidores().getId().getVinCodigo())){
			
			PdtAchadoJn achadoJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, PdtAchadoJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
			achadoJn.setDgrDptSeq(original.getId().getDgrDptSeq());
			achadoJn.setDgrSeqp(original.getId().getDgrSeqp());
			achadoJn.setSeqp(original.getId().getSeqp());
			achadoJn.setDescricao(original.getDescricao());
			achadoJn.setIndSituacao(original.getIndSituacao().toString());
			achadoJn.setIndNormal(original.getIndNormal());
			achadoJn.setIndExigeComplemento(original.getIndExigeComplemento());
			achadoJn.setMensagemAlerta(original.getMensagemAlerta());
			achadoJn.setCriadoEm(original.getCriadoEm());
			achadoJn.setSerMatricula(original.getRapServidores().getId().getMatricula());
			achadoJn.setSerVinCodigo(original.getRapServidores().getId().getVinCodigo());
			
			getPdtAchadoJnDAO().persistir(achadoJn);
		}
		
	}	
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
	

	protected PdtAchadoDAO getPdtAchadoDAO() {
		return pdtAchadoDAO;
	}
	
	protected PdtAchadoJnDAO getPdtAchadoJnDAO() {
		return pdtAchadoJnDAO;
	}

	protected PdtGrupoDAO getPdtGrupoDAO() {
		return pdtGrupoDAO;
	}

	protected PdtGrupoJnDAO getPdtGrupoJnDAO() {
		return pdtGrupoJnDAO;
	}
}
