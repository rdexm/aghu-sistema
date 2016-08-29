package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.model.MpmControlPrevAltas;
import br.gov.mec.aghu.prescricaomedica.dao.MpmControlPrevAltasDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ControlPrevAltasON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6316543080605441350L;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmControlPrevAltasDAO mpmControlPrevAltasDAO;
	
	public void persistir(MpmControlPrevAltas controlPrevAltas) {
		if(controlPrevAltas.getSeq() == null) {
			inserir(controlPrevAltas);
		} else {
			atualizar(controlPrevAltas);
		}
	}
	
	protected void inserir(MpmControlPrevAltas controlPrevAltas) {
		controlPrevAltas.setCriadoEm(new Date());
		controlPrevAltas.setServidor(servidorLogadoFacade.obterServidorLogado());
		mpmControlPrevAltasDAO.persistir(controlPrevAltas);
	}
	
	protected void atualizar(MpmControlPrevAltas controlPrevAltas){
		MpmControlPrevAltas entity = mpmControlPrevAltasDAO.obterPorChavePrimaria(controlPrevAltas.getSeq());
		entity.setAtendimento(controlPrevAltas.getAtendimento());
		entity.setCriadoEm(controlPrevAltas.getCriadoEm());
		entity.setDtInicio(controlPrevAltas.getDtInicio());
		entity.setDtFim(controlPrevAltas.getDtFim());
		entity.setResposta(controlPrevAltas.getResposta());
		entity.setSeq(controlPrevAltas.getSeq());
		entity.setServidor(servidorLogadoFacade.obterServidorLogado());
		mpmControlPrevAltasDAO.atualizar(entity);		
	}


	public MpmControlPrevAltasDAO getMpmControlPrevAltasDAO() {
		return mpmControlPrevAltasDAO;
	}

	public void setMpmControlPrevAltasDAO(MpmControlPrevAltasDAO mpmControlPrevAltasDAO) {
		this.mpmControlPrevAltasDAO = mpmControlPrevAltasDAO;
	}

	@Override
	protected Log getLogger() {
		return null;
	}
	
}
