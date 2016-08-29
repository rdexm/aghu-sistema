package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcMotivoCancelamentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMotivoCancelamentoJnDAO;
import br.gov.mec.aghu.model.MbcMotivoCancelamento;
import br.gov.mec.aghu.model.MbcMotivoCancelamentoJn;
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
public class MbcMotivoCancelamentoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcMotivoCancelamentoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcMotivoCancelamentoJnDAO mbcMotivoCancelamentoJnDAO;

	@Inject
	private MbcMotivoCancelamentoDAO mbcMotivoCancelamentoDAO;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3948374307442840519L;

	public enum MbcMotivoCancelamentoRNExceptionCode implements	BusinessExceptionCode {

		MBC_00209, //
		MBC_00213,//
		CONSTRAINT_MBC_MTC_UK1;
		
	}
	
	@SuppressWarnings("ucd")
	public void inserir(MbcMotivoCancelamento elemento) throws BaseException {
		this.verificarConstraints(elemento);
		this.preInserir(elemento);
		this.getMbcMotivoCancelamentoDAO().persistir(elemento);
	}
	
	
	/**
	 * ORADB TRIGGER MBCT_TAN_BRI
	 * 
	 * @param elemento
	 * @param loginUsuarioLogado
	 * @throws BaseException
	 */
	protected void preInserir(MbcMotivoCancelamento elemento) throws BaseException {
		//RN1
		elemento.setCriadoEm(new Date());
		//RN2
		this.atribuirServidor(elemento);
	}
	
	
	public void atualizar(MbcMotivoCancelamento elemento) throws BaseException {
		MbcMotivoCancelamento oldElemento = 
			this.getMbcMotivoCancelamentoDAO().obterOriginal(elemento);
		this.getMbcMotivoCancelamentoDAO().atualizar(elemento);
		this.getMbcMotivoCancelamentoDAO().flush();
		this.posAtualizar(elemento, oldElemento);
	}

	
	/**
	 * ORADB TRIGGER MBCT_MTC_BRU
	 * @param elemento
	 * @throws BaseException
	 */
	@SuppressWarnings("ucd")
	protected void preAtualizar(MbcMotivoCancelamento elemento) throws BaseException {
		this.verificarAlteracaoDescricao(elemento);
		this.verificarAlteracaoServidorCriadoEm(elemento);
	}
	
	
	/**
	 * ORADB TRIGGER MBCT_MTC_ARU
	 * @param elemento
	 * @throws BaseException
	 */
	protected void posAtualizar(MbcMotivoCancelamento elemento, 
			MbcMotivoCancelamento oldElemento) throws BaseException {
		if(this.verificarCamposModificados(elemento, oldElemento)){
			this.inserirJournal(elemento, DominioOperacoesJournal.UPD);
		}
	}
	
	
	
	protected Boolean verificarCamposModificados(MbcMotivoCancelamento elemento, MbcMotivoCancelamento oldElemento) {
		if(CoreUtil.modificados(elemento.getErroAgendamento(), oldElemento.getErroAgendamento())){
			return Boolean.TRUE;
		}else if(CoreUtil.modificados(elemento.getDestSr(), oldElemento.getDestSr())){
			return Boolean.TRUE;
		}else if(CoreUtil.modificados(elemento.getTipo(), oldElemento.getTipo())){
			return Boolean.TRUE;
		}else if(CoreUtil.modificados(elemento.getSituacao(), oldElemento.getSituacao())){
			return Boolean.TRUE;
		}
		
		return Boolean.FALSE;
	}
	
	
	
	/**
	 * ORADB PROCEDURE mbck_mtc_rn.rn_mtcp_ver_desc
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void verificarAlteracaoDescricao(MbcMotivoCancelamento elemento) throws BaseException {
		MbcMotivoCancelamento oldElemento = 
			this.getMbcMotivoCancelamentoDAO().obterOriginal(elemento);
		
		if(CoreUtil.modificados(elemento.getDescricao(), oldElemento.getDescricao())){
			throw new ApplicationBusinessException(MbcMotivoCancelamentoRNExceptionCode.MBC_00209);
		}
	}
	
	
	/**
	 * ORADB PROCEDURE mbck_mbc_rn.rn_mbcp_ver_update
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void verificarAlteracaoServidorCriadoEm(MbcMotivoCancelamento elemento) throws BaseException {
		MbcMotivoCancelamento oldElemento = 
			this.getMbcMotivoCancelamentoDAO().obterOriginal(elemento);
		
		if(CoreUtil.modificados(elemento.getCriadoEm(), oldElemento.getCriadoEm())
				|| CoreUtil.modificados(elemento.getServidor(), oldElemento.getServidor())){
			throw new ApplicationBusinessException(MbcMotivoCancelamentoRNExceptionCode.MBC_00213);
		}
	}
	
	
	
	/**
	 * ORADB PROCEDURE mbck_mbc_rn.rn_mbcp_atu_servidor
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void atribuirServidor(MbcMotivoCancelamento elemento) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		elemento.setServidor(servidorLogado);
	}
	
	
	protected void inserirJournal(MbcMotivoCancelamento elemento, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		MbcMotivoCancelamentoJn journal = BaseJournalFactory.getBaseJournal(
				operacao,	MbcMotivoCancelamentoJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
	
		journal.setSeq(elemento.getSeq());
		journal.setDescricao(elemento.getDescricao());
		journal.setIndDestSr(elemento.getDestSr() ? "S" : "N");
		journal.setIndErroAgend(elemento.getErroAgendamento() ? "S" : "N");
		if(elemento.getTipo() != null) {
			journal.setTipo(elemento.getTipo().toString());
		}
		journal.setSituacao(elemento.getSituacao().toString());
		journal.setSerMatricula(elemento.getServidor().getId().getMatricula());
		journal.setSerVinCodigo(elemento.getServidor().getId().getVinCodigo());
		journal.setCriadoEm(elemento.getCriadoEm());
		this.getMbcMotivoCancelamentoJnDAO().persistir(journal);
		this.getMbcMotivoCancelamentoJnDAO().flush();
	}
	
	
	
	protected void verificarConstraints(MbcMotivoCancelamento elemento) throws BaseException {
		//mbc_mtc_uk1
		if(!this.getMbcMotivoCancelamentoDAO()
				.listarMotivosCancelamentosCadastrados(elemento.getDescricao()).isEmpty()) {
			throw new ApplicationBusinessException(
					MbcMotivoCancelamentoRNExceptionCode.CONSTRAINT_MBC_MTC_UK1, 
					elemento.getDescricao());
		}
	}
	
	
	/** GET **/
	protected MbcMotivoCancelamentoDAO getMbcMotivoCancelamentoDAO() {
		return mbcMotivoCancelamentoDAO;
	}
	
	protected MbcMotivoCancelamentoJnDAO getMbcMotivoCancelamentoJnDAO() {
		return mbcMotivoCancelamentoJnDAO;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
}
