package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCompSangProcCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.model.MbcCompSangProcCirg;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcSolicHemoCirgAgendada;
import br.gov.mec.aghu.model.MbcSolicHemoCirgAgendadaId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("ucd")
@Stateless
public class MbcCompSangProcCirgRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcCompSangProcCirgRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	
	@Inject
	private MbcCompSangProcCirgDAO mbcCompSangProcCirgDAO;


	@EJB
	private ICascaFacade iCascaFacade;

	@EJB
	private MbcSolicHemoCirgAgendadaRN mbcSolicHemoCirgAgendadaRN;

	private static final long serialVersionUID = 5493342328674998011L;

	public enum MbcCompSangProcCirgRNExceptionCode implements BusinessExceptionCode {
		MBC_00585;
	}


	public void inserir(MbcCompSangProcCirg elemento) throws ApplicationBusinessException{
		//	this.preInserir(elemento,servidorLogado);
		this.getMbcCompSangProcCirgDAO().persistir(elemento);
		this.getMbcCompSangProcCirgDAO().flush();

	}


	/**
	 * ORADB PROCEDURE RN_CRGP_ATU_SANGUE
	 * @param elemento
	 * @throws BaseException 
	 */
	public Boolean atualizarSangue(Integer crgSeq, Integer pciSeq, Short espSeq) throws BaseException{

		Short v_esp_null = null;
		
		Boolean p_incluiu = Boolean.FALSE;
		
		List<MbcProcEspPorCirurgias> listPorcEspPorCirurgias = getMbcProcEspPorCirurgiasDAO().pesquisarProcEspCirurgicoPrincipalAgendamentoPorCrgSeq(crgSeq, null,Boolean.TRUE);

		if(listPorcEspPorCirurgias==null || listPorcEspPorCirurgias.isEmpty()){
			throw new ApplicationBusinessException(MbcCompSangProcCirgRNExceptionCode.MBC_00585);
		}
		
		 List<MbcCompSangProcCirg> listCompSangProcCirg = this.getMbcCompSangProcCirgDAO().buscarMbcCompSangProcCirg(pciSeq, espSeq);
		
		 if(listCompSangProcCirg!=null && !listCompSangProcCirg.isEmpty()){
			 
			 for(MbcCompSangProcCirg compSangProcCirg :listCompSangProcCirg){
		
				 if((compSangProcCirg.getAghEspecialidades() == null && v_esp_null == null) ||  compSangProcCirg!=null && compSangProcCirg.getAghEspecialidades() !=null){
					 
					 if(compSangProcCirg.getAghEspecialidades() !=null){
						 v_esp_null = 1;
					 }
	
					 MbcSolicHemoCirgAgendada elemento = new MbcSolicHemoCirgAgendada();
					 MbcSolicHemoCirgAgendadaId id = new MbcSolicHemoCirgAgendadaId();
					 id.setCrgSeq(crgSeq);
					 id.setCsaCodigo(compSangProcCirg.getAbsComponenteSanguineo().getCodigo());
					 elemento.setId(id);
					 elemento.setAbsComponenteSanguineo(compSangProcCirg.getAbsComponenteSanguineo());
					 elemento.setQuantidade(compSangProcCirg.getQtdeUnidade());
					 elemento.setIndLavado(Boolean.FALSE);
					 elemento.setQtdeMl(compSangProcCirg.getQtdeMl());
					 elemento.setIndImprLaudo(Boolean.FALSE);
					 elemento.setIndFiltrado(Boolean.FALSE);
					 elemento.setIndIrradiado(Boolean.FALSE);
					 elemento.setIndAutoTransfusao(Boolean.FALSE);
					 elemento.setCriadoEm(new Date());
					 this.getMbcSolicHemoCirgAgendadaRN().inserir(elemento);
					 p_incluiu = Boolean.TRUE;

				 }
				 
			 }
			 
		 }
	
		 return p_incluiu;
	}



	protected ICascaFacade getICascaFacade() {
		return iCascaFacade;
	}

	
	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO(){
		return mbcProcEspPorCirurgiasDAO;
	}

	protected MbcCompSangProcCirgDAO getMbcCompSangProcCirgDAO(){
		return mbcCompSangProcCirgDAO;
	}
	
	protected MbcSolicHemoCirgAgendadaRN getMbcSolicHemoCirgAgendadaRN(){
		return mbcSolicHemoCirgAgendadaRN;
	}

}