package br.gov.mec.aghu.faturamento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioTipoNutricaoParenteral;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * ORADB FATK_INTERFACE_ANU
 *
 */

@SuppressWarnings("PMD.HierarquiaONRNIncorreta")
@Stateless
public class FaturamentoFatkInterfaceAnuRN extends AbstractFatDebugLogEnableRN {

private static final Log LOG = LogFactory.getLog(FaturamentoFatkInterfaceAnuRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2234915568626336940L;

	/**
	 * ORADB FATK_INTERFACE_ANU.RN_FATP_INS_NUTR_ENT
	 * @param atdSeq
	 * @param dataInt
	 * @param dataAlta
	 * @param cthSeq
	 * @param opcao
	 *  
	 * 
	 * TODO: Importante: Esta procedure foi migrada PARCIAMENTE, conforme solicitação do analista. 
	 * Não foram migrados os trechos de códigos referentes à outros módulos (ANU, etc).
	 * 
	 */
	public void rnFatpInsNutrEnt(Integer atdSeq, Date dataInt, Date dataAlta, Integer cthSeq, Integer opcao) throws ApplicationBusinessException {
		Integer idade = null;
		Integer verifIdade = null;
		DominioTipoNutricaoParenteral tipoNutricaoEnteral = null;
		FatItemContaHospitalarDAO itemContaHospitalarDAO = this.getFatItemContaHospitalarDAO();
		
		final Integer P_OPCAO_1 = 1; //EXCLUI PHIS EXISTENTES NA CONTA
		 					//2 =      NÃO EXCLUI PARA OS CASOS DE REINTERNACAO
		
		if(P_OPCAO_1.equals(opcao)){
			/*Obtem Idade do Paciente a partir da data de internação*/
			idade = this.getItemContaHospitalarRN().fatcBuscaIdadePac(cthSeq);
			
			/*Busca parametro idade*/
			verifIdade = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_LIMITE_IDADE_PEDIATR_ADULTO);
			
			if(idade.equals(0)){
				tipoNutricaoEnteral = DominioTipoNutricaoParenteral.N;//TODO criar domínio???
			}else if(idade <= verifIdade){
				tipoNutricaoEnteral = DominioTipoNutricaoParenteral.P;
			}else{
				tipoNutricaoEnteral = DominioTipoNutricaoParenteral.A;
			}
			
			Integer seqsPhi = this.getFatProcedHospInternosDAO().buscarPrimeiroSeqProcedHospInternoPorTipoNutricaoEnteral(tipoNutricaoEnteral);
			if(seqsPhi != null){
				List<FatItemContaHospitalar> listaIch = itemContaHospitalarDAO.listarItensContaHospitalarComOrigemAnuFiltrandoPorContaHospitalarEProcedHospInt(cthSeq, seqsPhi);
				for (FatItemContaHospitalar ich : listaIch) {
					itemContaHospitalarDAO.remover(ich);
					itemContaHospitalarDAO.flush();
				}
			}
		}
	}	
}
