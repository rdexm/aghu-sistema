package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtProcDiagTerapDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtProcDiagTerapJnDAO;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.model.PdtProcDiagTerapJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;

/**
 * Classe responsável pelas regras de negócio para a entidade PdtProcDiagTerap.
 * Contém a implementação das triggers relacionadas. 
 * 
 * @author dpacheco
 *
 */
@Stateless
public class PdtProcDiagTerapRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtProcDiagTerapRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtProcDiagTerapJnDAO pdtProcDiagTerapJnDAO;

	@Inject
	private PdtProcDiagTerapDAO pdtProcDiagTerapDAO;

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 2618365917973469841L;

	/**
	 * Insere instância de PdtProcDiagTerap.
	 * 
	 * @param newProcDiagTerap
	 * @param servidorLogado
	 */
	public void inserirProcDiagTerap(PdtProcDiagTerap newProcDiagTerap) {
		executarAntesDeInserir(newProcDiagTerap);
		getPdtProcDiagTerapDAO().persistir(newProcDiagTerap);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DPT_BRI
	 * 
	 * @param newProcDiagTerap
	 * @param servidorLogado
	 */
	private void executarAntesDeInserir(PdtProcDiagTerap newProcDiagTerap) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		newProcDiagTerap.setCriadoEm(new Date());
		/* Atualiza servidor que incluiu registro */
		newProcDiagTerap.setServidor(servidorLogado);
	}
	
	/**
	 * Atualiza instância de PdtProcDiagTerap.
	 * 
	 * @param newProcDiagTerap
	 * @param servidorLogado
	 */	
	public void atualizarProcDiagTerap(PdtProcDiagTerap newProcDiagTerap) {
		executarAntesDeAtualizar(newProcDiagTerap);
		PdtProcDiagTerapDAO pdtProcDiagTerapDAO = getPdtProcDiagTerapDAO();
		PdtProcDiagTerap oldProcDiagTerap = pdtProcDiagTerapDAO.obterOriginal(newProcDiagTerap);
		pdtProcDiagTerapDAO.atualizar(newProcDiagTerap);
		executarAposAtualizar(newProcDiagTerap, oldProcDiagTerap);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DPT_BRU
	 * 
	 * @param newProcDiagTerap
	 * @param servidorLogado
	 */
	private void executarAntesDeAtualizar(PdtProcDiagTerap newProcDiagTerap) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		/* Atualiza servidor que atualizou registro */
		newProcDiagTerap.setServidor(servidorLogado);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DPT_ARU
	 * 
	 * @param newProcDiagTerap
	 * @param oldProcDiagTerap
	 * @param servidorLogado
	 */
	private void executarAposAtualizar(PdtProcDiagTerap newProcDiagTerap, PdtProcDiagTerap oldProcDiagTerap) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if (!oldProcDiagTerap.getSeq().equals(newProcDiagTerap.getSeq()) 
				|| !oldProcDiagTerap.getDescricao().equals(newProcDiagTerap.getDescricao())
				|| !oldProcDiagTerap.getSituacao().equals(newProcDiagTerap.getSituacao())
				|| !oldProcDiagTerap.getContaminacao().equals(newProcDiagTerap.getContaminacao())
				|| !oldProcDiagTerap.getTempoMinimo().equals(newProcDiagTerap.getTempoMinimo())
				|| !(CoreUtil.modificados(newProcDiagTerap.getExame(), oldProcDiagTerap.getExame()))
				|| !oldProcDiagTerap.getProcedimentoCirurgico().equals(newProcDiagTerap.getProcedimentoCirurgico())
				|| !oldProcDiagTerap.getCriadoEm().equals(newProcDiagTerap.getCriadoEm())
				|| !oldProcDiagTerap.getServidor().equals(newProcDiagTerap.getServidor())){
			
			PdtProcDiagTerapJn jn = new PdtProcDiagTerapJn();
			jn.setNomeUsuario(servidorLogado.getUsuario());
			jn.setOperacao(DominioOperacoesJournal.UPD);
			jn.setSeq(oldProcDiagTerap.getSeq());
			jn.setDescricao(oldProcDiagTerap.getDescricao());
			jn.setSituacao(oldProcDiagTerap.getSituacao());
			jn.setContaminacao(oldProcDiagTerap.getContaminacao());
			if (oldProcDiagTerap.getExame() != null) {
				jn.setExaSigla(oldProcDiagTerap.getExame().getSigla());
			}
			if (oldProcDiagTerap.getProcedimentoCirurgico() != null) {
				jn.setPciSeq(oldProcDiagTerap.getProcedimentoCirurgico().getSeq());
			}
			jn.setCriadoEm(oldProcDiagTerap.getCriadoEm());
			jn.setSerMatricula(oldProcDiagTerap.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(oldProcDiagTerap.getServidor().getId().getVinCodigo());
			getPdtProcDiagTerapJnDAO().persistir(jn);
		}
	}
	
	protected PdtProcDiagTerapDAO getPdtProcDiagTerapDAO() {
		return pdtProcDiagTerapDAO;
	}
	
	protected PdtProcDiagTerapJnDAO getPdtProcDiagTerapJnDAO() {
		return pdtProcDiagTerapJnDAO;
	}

}
