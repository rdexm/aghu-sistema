package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcExtratoCirurgiaDAO;
import br.gov.mec.aghu.model.MbcExtratoCirurgia;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class MbcExtratoCirurgiaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcExtratoCirurgiaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcExtratoCirurgiaDAO mbcExtratoCirurgiaDAO;


	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6011346013828403023L;
	
	
	/**
	 * Insere um registro <br>
	 * na tabela MBC_EXTRATO_CIRURGIAS.
	 * 
	 * @param elemento
	 * @param loginUsuarioLogado
	 * @throws BaseException
	 */
	public void inserir(MbcExtratoCirurgia elemento, String loginUsuarioLogado) throws BaseException {
		this.preInserir(elemento, loginUsuarioLogado);
		this.getMbcExtratoCirurgiaDAO().persistir(elemento);
		this.getMbcExtratoCirurgiaDAO().flush();
	}
	
	
	
	/**
	 * ORADB TRIGGER MBCT_ECR_BRI 
	 * 
	 * @param elemento
	 * @param loginUsuarioLogado
	 * @throws BaseException
	 */
	public void preInserir(MbcExtratoCirurgia elemento, String loginUsuarioLogado) throws BaseException {
		//RN1
		elemento.setCriadoEm(new Date());
		//RN2
		this.atribuirServidor(elemento, loginUsuarioLogado);
	}
	
	
	protected void atribuirServidor(MbcExtratoCirurgia elemento, String loginUsuarioLogado) throws BaseException {
		elemento.setServidor(
				this.getRegistroColaboradorFacade().obterServidorPorUsuario(loginUsuarioLogado));
	}
	
	
	/**GET**/
	protected MbcExtratoCirurgiaDAO getMbcExtratoCirurgiaDAO() {
		return mbcExtratoCirurgiaDAO;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
}
