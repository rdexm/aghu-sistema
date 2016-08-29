package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatCaractItemProcHospDAO;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


@Stateless
public class CaracteristicaItemProcedimentoHospitalarON extends BaseBusiness  {


@EJB
private CaracteristicaItemProcedimentoHospitalarRN caracteristicaItemProcedimentoHospitalarRN;

private static final Log LOG = LogFactory.getLog(CaracteristicaItemProcedimentoHospitalarON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatCaractItemProcHospDAO fatCaractItemProcHospDAO;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1366568085472533650L;

	/**
	 * Metodo para persistir um CaractItemProcedimentoHospitalar.
	 * 
	 * @param FatCaractItemProcHosp
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public void persistirCaractItemProcedimentoHospitalar(FatCaractItemProcHosp ciph, final Date dataFimVinculoServidor) throws ApplicationBusinessException{
		FatCaractItemProcHospDAO ciphDAO = getFatCaractItemProcHospDAO();
		//Foi implementado desta forma pois nao havia como saber se o objeto
		//ja existe devido ao fato de que a chave nao possui uma sequence.
		FatCaractItemProcHosp aux = ciphDAO.obterPorChavePrimaria(ciph.getId());
		if(aux == null){
			this.inserirCaractItemProcedimentoHospitalar(ciph, dataFimVinculoServidor);
		}else{
			this.atualizarCaractItemProcedimentoHospitalar(ciph, dataFimVinculoServidor);
		}
	}
	
	/**
	 * Metodo para inserir um itemProcedimentoHospitalar.
	 * @param FatItensProcedHospitalar
	 * @throws ApplicationBusinessException 
	 *  
	 */
	protected void inserirCaractItemProcedimentoHospitalar(FatCaractItemProcHosp ciph, final Date dataFimVinculoServidor) throws ApplicationBusinessException{
		CaracteristicaItemProcedimentoHospitalarRN ciphRN = this.getCaracteristicaItemProcedimentoHospitalarRN();
		FatCaractItemProcHospDAO ciphDAO = getFatCaractItemProcHospDAO();
		
		ciphRN.executarAntesDeInserirCaracteristicaItemProcedHosp(ciph, dataFimVinculoServidor);
		
		ciphDAO.persistir(ciph);
	}

	
	/**
	 * Metodo para atualizar um CaractItemProcedimentoHospitalar.
	 * Flush é executado ao final do método persistir.
	 * @param FatItensProcedHospitalar
	 * @throws ApplicationBusinessException  
	 */
	protected void atualizarCaractItemProcedimentoHospitalar(FatCaractItemProcHosp ciph, final Date dataFimVinculoServidor) throws ApplicationBusinessException{
		CaracteristicaItemProcedimentoHospitalarRN ciphRN = this.getCaracteristicaItemProcedimentoHospitalarRN();
		
		ciphRN.executarAntesDeAtualizarCaracteristicaItemProcedHosp(ciph, dataFimVinculoServidor);
	}

	protected CaracteristicaItemProcedimentoHospitalarRN getCaracteristicaItemProcedimentoHospitalarRN() {
		return caracteristicaItemProcedimentoHospitalarRN;
	}

	protected FatCaractItemProcHospDAO getFatCaractItemProcHospDAO() {
		return fatCaractItemProcHospDAO;
	}
}
