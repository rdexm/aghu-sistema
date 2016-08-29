package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultasDeOutrosConveniosVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class PesquisarConsultasDeOutrosConveniosON extends BaseBusiness  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3861521385572729032L;


	private static final Log LOG = LogFactory.getLog(PesquisarConsultasDeOutrosConveniosON.class);
	
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	public enum PesquisarConsultasDeOutrosConveniosONExceptionCode implements BusinessExceptionCode {
		AAC_00216
	}
	
	/**
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param mesAno
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ConsultasDeOutrosConveniosVO> pesquisarConsultasDeOutrosConvenios(
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Date mesAno) throws ApplicationBusinessException  {
		
		validaObrigatoriedade(mesAno);
		
		return aacConsultasDAO.pesquisarConsultasDeOutrosConvenios(firstResult, maxResult, orderProperty, asc, mesAno);
	}

	/**
	 * 
	 * @param mesAno
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarConsultasDeOutrosConveniosCount(Date mesAno) throws ApplicationBusinessException  {
		
		validaObrigatoriedade(mesAno);
		
		return aacConsultasDAO.pesquisarConsultasDeOutrosConveniosCount(mesAno);
	}
	
	/**
	 * 
	 * 
	 * @param mesAno
	 * @throws ApplicationBusinessException
	 */
	private void validaObrigatoriedade(Date mesAno) throws ApplicationBusinessException{
		
		if(mesAno==null){
			throw new ApplicationBusinessException(PesquisarConsultasDeOutrosConveniosONExceptionCode.AAC_00216);
		}
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}
