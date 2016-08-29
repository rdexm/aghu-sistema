package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.MpmUnidadeTempo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmUnidadeTempoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MpmUnidadeTempoRN  extends BaseBusiness{
	
	private static final Log LOG = LogFactory.getLog(MpmUnidadeTempoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmUnidadeTempoDAO mpmUnidadeTempoDAO;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;

	private static final long serialVersionUID = 5356545448693425111L;
	public enum MpmUnidadeTempoRNExceptionCode implements BusinessExceptionCode{
		RAP_00175,
		OFG_00007,
		OFG_00006
	}
	
	public void atualizaMpmUnidadeTempo(MpmUnidadeTempo mpmUnidadeTempo) throws BaseException{
		preAtualizarMpmUnidadeTempo(mpmUnidadeTempo);
		getMpmUnidadeTempoDAO().atualizar(mpmUnidadeTempo);
	}
	
	public void insereMpmUnidadeTempo(MpmUnidadeTempo mpmUnidadeTempo) throws BaseException{
		preInsertMpmUnidadeTempo(mpmUnidadeTempo);
		getMpmUnidadeTempoDAO().persistir(mpmUnidadeTempo);
	}
	
	/**
	 * ORADB mpmt_utp_bri
	 * @param mpmUnidadeTempo
	 * @throws ApplicationBusinessException  
	 * 
	 */
	public void preInsertMpmUnidadeTempo(MpmUnidadeTempo mpmUnidadeTempo) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		mpmUnidadeTempo.setCriadoEm(new Date());
		mpmUnidadeTempo.setServidor(servidorLogado);
	}
	
	/**
	 * ORADB mpmt_utp_bru
	 * @param MpmUnidadeTempo
	 * @throws ApplicationBusinessException 
	 */
	
	public void preAtualizarMpmUnidadeTempo(MpmUnidadeTempo mpmUnidadeTempo) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		MpmUnidadeTempo original = getMpmUnidadeTempoDAO().obterOriginal(mpmUnidadeTempo);
		
		if (!(original.getSeq()).equals(mpmUnidadeTempo.getSeq())){
			if(servidorLogado.getMatriculaVinculo() == null){
				 throw new ApplicationBusinessException(MpmUnidadeTempoRNExceptionCode.RAP_00175);
			} else {
				mpmUnidadeTempo.setServidor(servidorLogado);
				getMpmUnidadeTempoDAO().atualizar(mpmUnidadeTempo);
			}
		}				
	}
	
	public void excluirUnidadeTempo(MpmUnidadeTempo unidadeTempo) throws ApplicationBusinessException {
		if(getFarmaciaFacade().verificaExclusao(unidadeTempo)){
			throw new ApplicationBusinessException(MpmUnidadeTempoRNExceptionCode.OFG_00007);
		} 
		if(getFarmaciaFacade().verificaExclusaoJN(unidadeTempo)){
			throw new ApplicationBusinessException(MpmUnidadeTempoRNExceptionCode.OFG_00006);
		}
		else {
			getMpmUnidadeTempoDAO().remover(unidadeTempo);
		}
	}
	
	protected MpmUnidadeTempoDAO getMpmUnidadeTempoDAO(){
		return mpmUnidadeTempoDAO;
	}
		
	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
			
}
