package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtInstrumentalDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtInstrumentalJnDAO;
import br.gov.mec.aghu.model.PdtInstrumental;
import br.gov.mec.aghu.model.PdtInstrumentalJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class PdtInstrumentalRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtInstrumentalRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtInstrumentalJnDAO pdtInstrumentalJnDAO;

	@Inject
	private PdtInstrumentalDAO pdtInstrumentalDAO;

	private static final long serialVersionUID = -1974550544218417288L;
	
	protected enum InstrumentosRNExceptionCode implements BusinessExceptionCode {
		PDT_00105
		;
	}

	/**
	 * ORADB PROCEDURE RN_PDTP_ATU_SERVIDOR
	 * 
	 * #24719 RN1 
	 * PDTT_PIN_BRI
	 * PDTT_IEQ_BRI
	 * PDTT_PIN_BRU
	 * 
	 * @throws ApplicationBusinessException 
	 * 
	 */
	public void atualizarServidor() throws ApplicationBusinessException{
		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		if(servidor == null || servidor.getId().getMatricula() == null){
			throw new ApplicationBusinessException(InstrumentosRNExceptionCode.PDT_00105);
		}
	}
	
	/**
	 * ORADB TRIGGER PDTT_PIN_ARU
	 * 
	 * #24719  
	 */
	public void posUpdatePdtInstrumental(PdtInstrumental instrumento){
		PdtInstrumental original = getPdtInstrumentalDAO().obterOriginal(instrumento);
		
		if(CoreUtil.modificados(instrumento.getSeq(), original.getSeq()) ||
				CoreUtil.modificados(instrumento.getDescricao(), original.getDescricao()) ||
				CoreUtil.modificados(instrumento.getIndSituacao(), original.getIndSituacao()) ||
				CoreUtil.modificados(instrumento.getCriadoEm(), original.getCriadoEm()) ||
				CoreUtil.modificados(instrumento.getRapServidores().getId().getMatricula(), original.getRapServidores().getId().getMatricula()) ||
				CoreUtil.modificados(instrumento.getRapServidores().getId().getVinCodigo(), original.getRapServidores().getId().getVinCodigo())){
			
			PdtInstrumentalJn instrumentalJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, PdtInstrumentalJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
			instrumentalJn.setSeq(original.getSeq());
			instrumentalJn.setDescricao(original.getDescricao());
			instrumentalJn.setIndSituacao(original.getIndSituacao().toString());
			instrumentalJn.setCriadoEm(original.getCriadoEm());
			instrumentalJn.setSerMatricula(original.getRapServidores().getId().getMatricula());
			instrumentalJn.setSerVinCodigo(original.getRapServidores().getId().getVinCodigo());
			
			getPdtInstrumentalJnDAO().persistir(instrumentalJn);
		}
	}
	
	/**
	 * ORADB TRIGGER PDTT_PIN_ARD
	 * 
	 * #24719
	 * 
	 * Não foi efetuado testes
	 */
	public void posDeletePdtInstrumental(PdtInstrumental instrumental){
		PdtInstrumental original = getPdtInstrumentalDAO().obterOriginal(instrumental);
		
		PdtInstrumentalJn instrumentalJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, PdtInstrumentalJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		instrumentalJn.setSeq(original.getSeq());
		instrumentalJn.setDescricao(original.getDescricao());
		instrumentalJn.setIndSituacao(original.getIndSituacao().toString());
		instrumentalJn.setCriadoEm(original.getCriadoEm());
		instrumentalJn.setSerMatricula(original.getRapServidores().getId().getMatricula());
		instrumentalJn.setSerVinCodigo(original.getRapServidores().getId().getVinCodigo());
		
		getPdtInstrumentalJnDAO().persistir(instrumentalJn);
	}
		
	private PdtInstrumentalDAO getPdtInstrumentalDAO() {
		return pdtInstrumentalDAO;
	}
	
	private PdtInstrumentalJnDAO getPdtInstrumentalJnDAO() {
		return pdtInstrumentalJnDAO;
	}
}
