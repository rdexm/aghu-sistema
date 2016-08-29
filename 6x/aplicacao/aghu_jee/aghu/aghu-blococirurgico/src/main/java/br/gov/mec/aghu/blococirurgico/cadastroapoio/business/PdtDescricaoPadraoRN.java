package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtDescPadraoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescPadraoJnDAO;
import br.gov.mec.aghu.model.PdtDescPadrao;
import br.gov.mec.aghu.model.PdtDescPadraoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class PdtDescricaoPadraoRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(PdtDescricaoPadraoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private PdtDescPadraoJnDAO pdtDescPadraoJnDAO;

	@Inject
	private PdtDescPadraoDAO pdtDescPadraoDAO;


	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2102595629648868998L;
	protected enum PdtDescricaoPadraoRNExceptionCode implements BusinessExceptionCode {
		PDT_00105
		;
	}

	/**
	 * ORADB 
	 * 
	 * #24717 RN1 
	 * PDTT_DPD_BRI
	 * PDTT_DPD_BRU
	 * 
	 * @throws ApplicationBusinessException 
	 * 
	 */
	public void atualizarServidor() throws ApplicationBusinessException{
		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		if(servidor == null || servidor.getId().getMatricula() == null){
			throw new ApplicationBusinessException(PdtDescricaoPadraoRNExceptionCode.PDT_00105);
		}
	}

	/**
	 * ORADB 
	 * 
	 * #24717  RN1
	 * PDTT_DPD_ARU
	 */
	public void posUpdatePdtDescPadrao(PdtDescPadrao descricaoPadrao){
		PdtDescPadrao original = getPdtDescPadraoDAO().obterOriginal(descricaoPadrao);
		
		if(CoreUtil.modificados(descricaoPadrao.getId().getSeqp(), original.getId().getSeqp()) ||
		   CoreUtil.modificados(descricaoPadrao.getId().getEspSeq(), original.getId().getEspSeq()) ||	
		   CoreUtil.modificados(descricaoPadrao.getDescTecPadrao(), original.getDescTecPadrao()) ||
		   CoreUtil.modificados(descricaoPadrao.getAghEspecialidades(), original.getAghEspecialidades()) ||	
		   CoreUtil.modificados(descricaoPadrao.getPdtProcDiagTerap(), original.getPdtProcDiagTerap()) ||
		   CoreUtil.modificados(descricaoPadrao.getTitulo(), original.getTitulo()) ||
		   CoreUtil.modificados(descricaoPadrao.getCriadoEm(), original.getCriadoEm()) ||
		   CoreUtil.modificados(descricaoPadrao.getRapServidores().getId().getMatricula(), original.getRapServidores().getId().getMatricula()) ||
		   CoreUtil.modificados(descricaoPadrao.getRapServidores().getId().getVinCodigo(), original.getRapServidores().getId().getVinCodigo())){
			
			PdtDescPadraoJn descricaoPadraoJn = BaseJournalFactory.getBaseJournal
				(DominioOperacoesJournal.UPD, PdtDescPadraoJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
			descricaoPadraoJn.setSeqp(original.getId().getSeqp());
			descricaoPadraoJn.setEspSeq(original.getId().getEspSeq());
			descricaoPadraoJn.setDescTecPadrao(original.getDescTecPadrao());
			descricaoPadraoJn.setDptSeq(original.getPdtProcDiagTerap().getSeq());
			descricaoPadraoJn.setTitulo(original.getTitulo());
			descricaoPadraoJn.setCriadoEm(original.getCriadoEm());
			descricaoPadraoJn.setSerMatricula(original.getRapServidores().getId().getMatricula());
			descricaoPadraoJn.setSerVinCodigo(original.getRapServidores().getId().getVinCodigo());
			
			getPdtDescPadraoJnDAO().persistir(descricaoPadraoJn);
		}
	}
	

	/**
	 * ORADB 
	 * 
	 * #24717  RN1
	 * PDTT_DPD_ARD
	 */
	public void posDeletePdtDescPadrao(PdtDescPadrao descricaoPadrao){
		PdtDescPadrao original = getPdtDescPadraoDAO().obterOriginal(descricaoPadrao);
		
			PdtDescPadraoJn descricaoPadraoJn = BaseJournalFactory.getBaseJournal
				(DominioOperacoesJournal.DEL, PdtDescPadraoJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
			descricaoPadraoJn.setSeqp(original.getId().getSeqp());
			descricaoPadraoJn.setEspSeq(original.getId().getEspSeq());
			descricaoPadraoJn.setDescTecPadrao(original.getDescTecPadrao());
			descricaoPadraoJn.setDptSeq(original.getPdtProcDiagTerap().getSeq());
			descricaoPadraoJn.setTitulo(original.getTitulo());
			descricaoPadraoJn.setCriadoEm(original.getCriadoEm());
			descricaoPadraoJn.setSerMatricula(original.getRapServidores().getId().getMatricula());
			descricaoPadraoJn.setSerVinCodigo(original.getRapServidores().getId().getVinCodigo());
			
			getPdtDescPadraoJnDAO().persistir(descricaoPadraoJn);

	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}

	protected PdtDescPadraoDAO getPdtDescPadraoDAO() {
		return pdtDescPadraoDAO;
	}
	
	protected PdtDescPadraoJnDAO getPdtDescPadraoJnDAO() {
		return pdtDescPadraoJnDAO;
	}

	
}
