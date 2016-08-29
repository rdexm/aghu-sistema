package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcQuestaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcQuestaoJnDAO;
import br.gov.mec.aghu.model.MbcQuestao;
import br.gov.mec.aghu.model.MbcQuestaoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MbcQuestaoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcQuestaoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcQuestaoJnDAO mbcQuestaoJnDAO;

	@Inject
	private MbcQuestaoDAO mbcQuestaoDAO;


	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2889595991320793287L;

	public enum MbcQuestaoRNExceptionCode implements BusinessExceptionCode {
		MBC_00716;
	}

	
	/**
	 * Atualizar um registro na<br>
	 * tabela MBC_QUESTOES
	 * 
	 * @param elemento
	 * @param loginUsuarioLogado
	 * @throws BaseException
	 */
	public void atualizar(MbcQuestao elemento) throws BaseException {
		MbcQuestao oldElemento = this.getMbcQuestaoDAO().obterOriginal(elemento);
		this.preAtualizar(elemento, oldElemento);
		this.getMbcQuestaoDAO().atualizar(elemento);
		this.posAtualizar(elemento, oldElemento);
	}
	
	
	/**
	 * ORADB TRIGGER MBCT_QES_BRU
	 * 
	 * @param elemento
	 * @param oldElemento
	 * @param loginUsuarioLogado
	 * @throws BaseException
	 */
	protected void preAtualizar(MbcQuestao elemento, MbcQuestao oldElemento) throws BaseException {
		//RN1
		if(CoreUtil.modificados(elemento.getDescricao(), oldElemento.getDescricao())){
			throw new ApplicationBusinessException(MbcQuestaoRNExceptionCode.MBC_00716);
		}
		//RN2
		this.atribuirServidor(elemento);
	}
	
	
	
	/**
	 * ORADB TRIGGER MBCT_QES_ARU
	 * 
	 * @param elemento
	 * @param oldElemento
	 * @throws BaseException
	 */
	protected void posAtualizar(MbcQuestao elemento, MbcQuestao oldElemento) throws BaseException {
		if(this.verificarCamposModificados(elemento, oldElemento)) {
			this.inserirJournal(oldElemento, DominioOperacoesJournal.UPD);
		}
	}
	
	
	protected void inserirJournal(MbcQuestao elemento, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		MbcQuestaoJn journal = BaseJournalFactory
			.getBaseJournal(operacao, MbcQuestaoJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		journal.setMtcSeq(elemento.getId().getMtcSeq());
		journal.setSeqp(elemento.getId().getSeqp());
		journal.setDescricao(elemento.getDescricao());
		journal.setIndSituacao(elemento.getSituacao().toString());
		journal.setIndExigeComplemento(elemento.getExigeComplemento() ? "S" : "N");
		journal.setCriadoEm(elemento.getCriadoEm());
		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		if(servidor != null) {
			journal.setSerMatricula(servidor.getId().getMatricula());
			journal.setSerVinCodigo(servidor.getId().getVinCodigo());
		}
		this.getMbcQuestaoJnDAO().persistir(journal);
		this.getMbcQuestaoJnDAO().flush();
	}
	
	
	private Boolean verificarCamposModificados(MbcQuestao elemento, MbcQuestao oldElemento) {
		if(CoreUtil.modificados(this.obterCampoMatriculaServidor(elemento.getServidor()), 
				oldElemento.getServidor().getId().getMatricula())) {
			return Boolean.TRUE;
		}else if(CoreUtil.modificados(this.obterCampoVinCodigoServidor(
				elemento.getServidor()), 
				oldElemento.getServidor().getId().getVinCodigo())) {
			return Boolean.TRUE;
		}else if(CoreUtil.modificados(elemento.getId().getMtcSeq(), 
				oldElemento.getId().getMtcSeq())) {
			return Boolean.TRUE;
		}else if(CoreUtil.modificados(elemento.getId().getSeqp(), 
				oldElemento.getId().getSeqp())) {
			return Boolean.TRUE;
		}else if(CoreUtil.modificados(elemento.getDescricao(), oldElemento.getDescricao())) {
			return Boolean.TRUE;
		}else if(CoreUtil.modificados(elemento.getSituacao(), oldElemento.getSituacao())) {
			return Boolean.TRUE;
		}else if(CoreUtil.modificados(
				elemento.getExigeComplemento(), oldElemento.getExigeComplemento())) {
			return Boolean.TRUE;
		}else if(CoreUtil.modificados(
				elemento.getCriadoEm(), oldElemento.getCriadoEm())) {
			return Boolean.TRUE;
		}
		
		return Boolean.FALSE;
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
	 * Insere um registro na tabela <br>
	 * MBC_QUESTOES
	 * 
	 * @param elemento
	 * @param loginUsuarioLogado
	 * @throws BaseException
	 */
	public void inserir(MbcQuestao elemento) throws BaseException {
		this.preInserir(elemento);
		this.getMbcQuestaoDAO().persistir(elemento);
	}
	
	
	/**
	 * ORADB TRIGGER MBCT_QES_BRI
	 * 
	 * @param elemento
	 * @param loginUsuarioLogado
	 * @throws BaseException
	 */
	protected void preInserir(MbcQuestao elemento) throws BaseException {
		//RN1
		elemento.setCriadoEm(new Date());
		//RN2
		this.atribuirServidor(elemento);
	}
	
	
	/**
	 * ORADB PROCEDURE mbck_mbc_rn.rn_mbcp_atu_servidor
	 * 
	 * @param elemento
	 * @param loginUsuarioLogado
	 * @throws BaseException
	 */
	protected void atribuirServidor(MbcQuestao elemento) throws BaseException {
		elemento.setServidor(
				servidorLogadoFacade.obterServidorLogado());
	}
	
	
	
	/**GET**/
	protected MbcQuestaoDAO getMbcQuestaoDAO() {
		return mbcQuestaoDAO;
	}
	
	protected MbcQuestaoJnDAO getMbcQuestaoJnDAO() {
		return mbcQuestaoJnDAO;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
}
