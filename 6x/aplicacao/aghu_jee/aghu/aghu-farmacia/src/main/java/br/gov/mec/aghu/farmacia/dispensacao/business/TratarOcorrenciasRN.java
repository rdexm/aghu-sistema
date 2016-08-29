package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndExcluidoDispMdtoCbSps;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.farmacia.dao.AfaDispMdtoCbSpsDAO;
import br.gov.mec.aghu.model.AfaDispMdtoCbSps;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class TratarOcorrenciasRN extends BaseBusiness
		implements Serializable {


@EJB
private AfaDispMdtoCbSpsRN afaDispMdtoCbSpsRN;

private static final Log LOG = LogFactory.getLog(TratarOcorrenciasRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IEstoqueFacade estoqueFacade;

@Inject
private AfaDispMdtoCbSpsDAO afaDispMdtoCbSpsDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2824966939088338168L;

	public enum TratarOcorrenciasRNExceptionCode implements	BusinessExceptionCode {
		AFA_01697, AFA_01489, AFA_01504
	}

	public void pesquisarEtiquetaComCbMedicamento(String etiqueta, List<AfaDispensacaoMdtos> listaOcorrenciasMdtosDispensados, String nomeMicrocomputador) throws BaseException {
		/**
		 * 	v_sair  VARCHAR(1):='N';
			v_codMed NUMBER(10):=0;
			v_achou VARCHAR(1):='N';
		 */
		
		AfaDispMdtoCbSps dispMdtoCbSps = getAfaDispMdtoCbSpsDAO().getDispMdtoCbSpsByEtiqueta(etiqueta, DominioIndExcluidoDispMdtoCbSps.I);
		if(dispMdtoCbSps != null){
			throw new ApplicationBusinessException(TratarOcorrenciasRNExceptionCode.AFA_01697);
		}
		SceLoteDocImpressao loteDocImp = getEstoqueFacade().getLoteDocImpressaoByNroEtiqueta(etiqueta);
		
		if(loteDocImp == null){
			throw new ApplicationBusinessException(TratarOcorrenciasRNExceptionCode.AFA_01504);
		}
		Boolean encontrouRegistro = Boolean.FALSE;
		for(AfaDispensacaoMdtos adm: listaOcorrenciasMdtosDispensados){
			if (DominioSituacaoDispensacaoMdto.T.equals(adm.getIndSituacao())
					&& adm.getMedicamento().getMatCodigo().equals(loteDocImp.getMaterial().getCodigo())) {
				getAfaDispMdtoCbSpsRN().validaAtualizaCodigoDeBarras(etiqueta, adm.getPrescricaoMedica().getId().getAtdSeq(), adm.getPrescricaoMedica().getId().getSeq(), adm.getSeq(), nomeMicrocomputador);
				encontrouRegistro = Boolean.TRUE;
				break;
			}
		}
		
		if(!encontrouRegistro){
			throw new ApplicationBusinessException(TratarOcorrenciasRNExceptionCode.AFA_01489);
		}
	}
	
		
	//Getters


	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}
	
	private AfaDispMdtoCbSpsDAO getAfaDispMdtoCbSpsDAO(){
		return afaDispMdtoCbSpsDAO;
	}
	
	private AfaDispMdtoCbSpsRN getAfaDispMdtoCbSpsRN() {
		return afaDispMdtoCbSpsRN;
	}
	
}