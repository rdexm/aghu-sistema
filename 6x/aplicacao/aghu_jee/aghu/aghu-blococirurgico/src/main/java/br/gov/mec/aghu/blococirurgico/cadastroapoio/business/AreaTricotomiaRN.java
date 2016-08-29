package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAreaTricotomiaDAO;
import br.gov.mec.aghu.model.MbcAreaTricotomia;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Implementação das regras de negócio da package Oracle MBCK_ATR_RN e de
 * triggers que utilizam estas regras de negócio relacionadas a Área de
 * Tricotomia.
 * 
 * @author dpacheco
 */
@Stateless
public class AreaTricotomiaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AreaTricotomiaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private MbcAreaTricotomiaDAO mbcAreaTricotomiaDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = 4016722537375351496L;
	
	public enum AreaTricotomiaRNExceptionCode implements BusinessExceptionCode {
		MBC_00302
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_ATR_BRI
	 * 
	 * @param newAreaTricotomia
	 */
	private void executarAntesDeInserir(MbcAreaTricotomia newAreaTricotomia) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		newAreaTricotomia.setCriadoEm(new Date());
		newAreaTricotomia.setServidor(servidorLogado);
	}
	
	/**
	 * Insere instância de MbcAreaTricotomia.
	 * 
	 * @param newAreaTricotomia
	 * @param servidorLogado
	 */
	public void inserirAreaTricotomia(MbcAreaTricotomia newAreaTricotomia) {
		executarAntesDeInserir(newAreaTricotomia);
		getMbcAreaTricotomiaDAO().persistir(newAreaTricotomia);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_ATR_BRU
	 * 
	 * @param newAreaTricotomia
	 */
	private void executarAntesDeAtualizar(MbcAreaTricotomia newAreaTricotomia) throws ApplicationBusinessException {
		MbcAreaTricotomiaDAO mbcAreaTricotomiaDAO =  getMbcAreaTricotomiaDAO();
		MbcAreaTricotomia oldAreaTricotomia = mbcAreaTricotomiaDAO.obterOriginal(newAreaTricotomia);
		
		if (CoreUtil.modificados(newAreaTricotomia.getDescricao(), oldAreaTricotomia.getDescricao())) {
			restringirAlteracaoDescricaoTricotomia();
		}
	}
	
	/**
	 *  Atualiza instância de MbcAreaTricotomia.
	 * 
	 * @param newAreaTricotomia
	 * @throws ApplicationBusinessException
	 */
	public void atualizarAreaTricotomia(MbcAreaTricotomia newAreaTricotomia) throws ApplicationBusinessException {
		executarAntesDeAtualizar(newAreaTricotomia);
		getMbcAreaTricotomiaDAO().atualizar(newAreaTricotomia);
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: MBCK_ATR_RN.RN_ATRP_VER_DESC
	 */
	public void restringirAlteracaoDescricaoTricotomia() throws ApplicationBusinessException {
		throw new ApplicationBusinessException(AreaTricotomiaRNExceptionCode.MBC_00302);
	}
	
	protected MbcAreaTricotomiaDAO getMbcAreaTricotomiaDAO() {
		return mbcAreaTricotomiaDAO;
	}

}
