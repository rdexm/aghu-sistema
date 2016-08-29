package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAreaTricProcCirgDAO;
import br.gov.mec.aghu.model.MbcAreaTricProcCirg;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AreaTricotomiaProcCirgRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(AreaTricotomiaProcCirgRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAreaTricProcCirgDAO mbcAreaTricProcCirgDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = 3222555747490874728L;
	
	
	public enum AreaTricotomiaProcCirgRNExceptionCode implements BusinessExceptionCode {
		MBC_00013;
	}
	
	public void persistir(MbcAreaTricProcCirg area) throws BaseException {
		this.inserir(area);
	}
	
	public void remover(MbcAreaTricProcCirg area) {
		MbcAreaTricProcCirg a = getMbcAreaTricProcCirgDAO().obterPorChavePrimaria(area.getId());
		
		if (a != null) {
			getMbcAreaTricProcCirgDAO().remover(a);
		}
	}
	
	public void inserir(MbcAreaTricProcCirg area) throws BaseException {
		this.validarRegistroJaExistente(area);
		this.preInserir(area);
		this.getMbcAreaTricProcCirgDAO().persistir(area);
	}
	
	/**
	 * ORADB: MBCT_ART_BRI
	 * 
	 * @param area
	 * @param servidorLogado
	 */
	private void preInserir(MbcAreaTricProcCirg area) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		area.setCriadoEm(new Date());
		area.setRapServidores(servidorLogado);
	}


	private void validarRegistroJaExistente(MbcAreaTricProcCirg area) throws BaseException {
		if(getMbcAreaTricProcCirgDAO().obterOriginal(area) != null) {
			throw new ApplicationBusinessException(AreaTricotomiaProcCirgRNExceptionCode.MBC_00013);
		}
	}
	
	protected MbcAreaTricProcCirgDAO getMbcAreaTricProcCirgDAO() {
		return mbcAreaTricProcCirgDAO;
	}
}
