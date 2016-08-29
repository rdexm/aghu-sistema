package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.MbcPerfilCancelamentoRN.MbcPerfilCancelamentoRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcPerfilCancelamentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcPerfilCancelamentoJnDAO;
import br.gov.mec.aghu.model.MbcPerfilCancelamento;
import br.gov.mec.aghu.model.MbcPerfilCancelamentoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MbcPerfilCancelamentoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcPerfilCancelamentoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MbcPerfilCancelamentoJnDAO mbcPerfilCancelamentoJnDAO;

	@Inject
	private MbcPerfilCancelamentoDAO mbcPerfilCancelamentoDAO;


	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 721899098591240625L;

	
	public enum MbcPerfilCancelamentoRNExceptionCode implements	BusinessExceptionCode {
		PERFIL_JA_CADASTRADO_PARA_MOTIVO;
	}
	
	
	public void inserir(MbcPerfilCancelamento elemento) throws BaseException {
		this.verificarConstraints(elemento);
		this.preInserir(elemento);
		this.getMbcPerfilCancelamentoDAO().persistir(elemento);
	}
	
	
	public void remover(MbcPerfilCancelamento elemento) throws BaseException {
		MbcPerfilCancelamento elementoAux = this.getMbcPerfilCancelamentoDAO().obterPorChavePrimaria(elemento.getId());
		this.getMbcPerfilCancelamentoDAO().remover(elementoAux);
		this.getMbcPerfilCancelamentoDAO().flush();
		this.posRemover(elementoAux);
	}
	
	
	/**
	 * ORADB TRIGGER MBCT_PIC_BRI
	 * 
	 * @param elemento
	 * @param loginUsuarioLogado
	 * @throws BaseException
	 */
	protected void preInserir(MbcPerfilCancelamento elemento) throws BaseException {
		elemento.setCriadoEm(new Date());
		this.atribuirServidor(elemento);
	}
	
	
	/**
	 * ORADB TRIGGER MBCT_PIC_ARD
	 * 
	 * @param elemento
	 * @param loginUsuarioLogado
	 * @throws BaseException
	 */
	protected void posRemover(MbcPerfilCancelamento elemento) throws BaseException {
		this.inserirJournal(elemento, DominioOperacoesJournal.DEL);
	}
	
	
	protected void inserirJournal(MbcPerfilCancelamento elemento, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		MbcPerfilCancelamentoJn journal = BaseJournalFactory.getBaseJournal(
				operacao,	MbcPerfilCancelamentoJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		
		journal.setMtcSeq(elemento.getId().getMtcSeq());
		journal.setPerNome(elemento.getId().getPerNome());
		journal.setSerMatricula(elemento.getRapServidores().getId().getMatricula());
		journal.setSerVinCodigo(elemento.getRapServidores().getId().getVinCodigo());
		journal.setCriadoEm(elemento.getCriadoEm());
		this.getMbcPerfilCancelamentoJnDAO().persistir(journal);
		this.getMbcPerfilCancelamentoJnDAO().flush();
	}
	
	
	/**
	 * ORADB PROCEDURE mbck_mbc_rn.rn_mbcp_atu_servidor
	 * 
	 * @param elemento
	 * @param loginUsuarioLogado
	 * @throws BaseException
	 */
	protected void atribuirServidor(MbcPerfilCancelamento elemento) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		elemento.setRapServidores(servidorLogado);
	}
	
	
	protected void verificarConstraints(MbcPerfilCancelamento elemento) throws BaseException {
		if(this.getMbcPerfilCancelamentoDAO().obterPerfilCancelamento(
				elemento.getMbcMotivoCancelamento().getSeq(), elemento.getPerfil().getNome()) != null) {
			throw new ApplicationBusinessException(MbcPerfilCancelamentoRNExceptionCode.
					PERFIL_JA_CADASTRADO_PARA_MOTIVO, elemento.getPerfil().getNome());
		}
	}
	
	
	/**GET**/
	protected MbcPerfilCancelamentoDAO getMbcPerfilCancelamentoDAO() {
		return mbcPerfilCancelamentoDAO;
	}
	
	protected MbcPerfilCancelamentoJnDAO getMbcPerfilCancelamentoJnDAO() {
		return mbcPerfilCancelamentoJnDAO;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
}
