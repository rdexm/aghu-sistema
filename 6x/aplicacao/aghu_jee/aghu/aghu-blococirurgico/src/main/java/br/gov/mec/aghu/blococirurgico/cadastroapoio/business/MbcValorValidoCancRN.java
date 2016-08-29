package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcValorValidoCancDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcValorValidoCancJnDAO;
import br.gov.mec.aghu.model.MbcValorValidoCanc;
import br.gov.mec.aghu.model.MbcValorValidoCancJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MbcValorValidoCancRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcValorValidoCancRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcValorValidoCancDAO mbcValorValidoCancDAO;

	@Inject
	private MbcValorValidoCancJnDAO mbcValorValidoCancJnDAO;


	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1923044523470360115L;

	
	
	public void atualizar(MbcValorValidoCanc elemento) throws BaseException {
		MbcValorValidoCanc oldElemento = this.getMbcValorValidoCancDAO().obterOriginal(elemento);
		this.preAtualizar(elemento);
		this.getMbcValorValidoCancDAO().atualizar(elemento);
		this.posAtualizar(elemento, oldElemento);
	}
	
	
	public void inserir(MbcValorValidoCanc elemento) throws BaseException {
		this.preInserir(elemento);
		this.getMbcValorValidoCancDAO().persistir(elemento);
	}
	
	
	
	protected void preInserir(MbcValorValidoCanc elemento) throws BaseException {
		//RN1
		elemento.setCriadoEm(new Date());
		//RN2
		this.atribuirServidor(elemento);
	}
	
	
	
	protected void preAtualizar(MbcValorValidoCanc elemento) throws BaseException {
		//RN1
		this.atribuirServidor(elemento);
	}
	
	
	
	protected void posAtualizar(MbcValorValidoCanc elemento, MbcValorValidoCanc oldElemento) throws BaseException {
		//RN1
		if(verificarCamposModificados(elemento, oldElemento)){
			this.inserirJournal(oldElemento, DominioOperacoesJournal.UPD);
		}
	}
	
	
	
	protected Boolean verificarCamposModificados(MbcValorValidoCanc elemento, MbcValorValidoCanc oldElemento) {
		if(CoreUtil.modificados(
				this.obterCampoMatriculaServidor(elemento.getServidor()), 
				oldElemento.getServidor().getId().getMatricula())){
			return Boolean.TRUE;
		}else if(CoreUtil.modificados(
				this.obterCampoVinCodigoServidor(elemento.getServidor()), 
				oldElemento.getServidor().getId().getVinCodigo())){
			return Boolean.TRUE;
		}else if(CoreUtil.modificados(
				elemento.getId().getQesMtcSeq(), 
				oldElemento.getId().getQesMtcSeq())){
			return Boolean.TRUE;
		}else if(CoreUtil.modificados(
				elemento.getId().getQesSeqp(), 
				oldElemento.getId().getQesSeqp())){
			return Boolean.TRUE;
		}else if(CoreUtil.modificados(
				elemento.getId().getSeqp(), 
				oldElemento.getId().getSeqp())){
			return Boolean.TRUE;
		}else if(CoreUtil.modificados(
				elemento.getValor(), 
				oldElemento.getValor())){
			return Boolean.TRUE;
		}else if(CoreUtil.modificados(
				elemento.getSituacao(), 
				oldElemento.getSituacao())){
			return Boolean.TRUE;
		}else if(CoreUtil.modificados(
				elemento.getCriadoEm(), 
				oldElemento.getCriadoEm())){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	
	
	protected void inserirJournal(MbcValorValidoCanc elemento, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		MbcValorValidoCancJn journal = BaseJournalFactory.getBaseJournal(operacao, MbcValorValidoCancJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		
		journal.setQesMtcSeq(elemento.getId().getQesMtcSeq());
		journal.setQesSeqp(elemento.getId().getQesSeqp());
		journal.setSeqp(elemento.getId().getSeqp());
		journal.setValor(elemento.getValor());
		journal.setIndSituacao(elemento.getSituacao().toString());
		journal.setCriadoEm(elemento.getCriadoEm());
		
		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		if(servidor != null) {
			journal.setSerMatricula(servidor.getId().getMatricula());
			journal.setSerVinCodigo(servidor.getId().getVinCodigo());
		}
		
		this.getMbcValorValidoCancJnDAO().persistir(journal);
		this.getMbcValorValidoCancJnDAO().flush();
	}
	
	
	private Integer obterCampoMatriculaServidor(RapServidores servidor) {
		if(servidor != null && servidor.getId() != null) {
			servidor.getId().getMatricula();
		}
		return null;
	}
	
	
	private Short obterCampoVinCodigoServidor(RapServidores servidor) {
		if(servidor != null && servidor.getId() != null) {
			servidor.getId().getVinCodigo();
		}
		return null;
	}
	
	
	/**
	 * ORADB PROCEDURE mbck_mbc_rn.rn_mbcp_atu_servidor
	 * 
	 * @param elemento
	 * @param loginUsuarioLogado
	 * @throws BaseException
	 */
	protected void atribuirServidor(MbcValorValidoCanc elemento) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		elemento.setServidor(
				servidorLogado);
	}
	
	
	/**GET**/
	protected MbcValorValidoCancDAO getMbcValorValidoCancDAO() {
		return mbcValorValidoCancDAO;
	}
	
	protected MbcValorValidoCancJnDAO getMbcValorValidoCancJnDAO() {
		return mbcValorValidoCancJnDAO;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
	
}
