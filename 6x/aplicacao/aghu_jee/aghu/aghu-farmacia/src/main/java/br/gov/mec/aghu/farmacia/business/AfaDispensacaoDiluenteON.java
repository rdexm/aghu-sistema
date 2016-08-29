package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescritoDispensacaoMdto;
import br.gov.mec.aghu.farmacia.business.AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.view.VMpmDosagem;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;

@Stateless
public class AfaDispensacaoDiluenteON  extends BaseBusiness implements Serializable {

	private static final long serialVersionUID = -4575035016674717750L;
	
	private static final Log LOG = LogFactory.getLog(AfaDispensacaoDiluenteON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public enum AfaDispensacaoDiluenteONExceptionCode implements	BusinessExceptionCode {
		DILUENTE_NAO_PODE_SER_EDITADO_NA_PRESCRICAO
	}
	
	@Inject
	private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO;
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	@Inject
	private AfaDispensacaoMdtosRN afaDispensacaoMdtosRN;
	
	public void processaDispensacaoDiluente(MpmPrescricaoMdto prescricaoMdto, 
			BigDecimal percSeNec, Date pmeData, Date pmeDthrFim, String nomeMicrocomputador, 
			Boolean movimentacao
			) throws ApplicationBusinessException
		{
		if(!this.isHCPA()){
			MpmItemPrescricaoMdto itemPrescricaoMdto = prescricaoMdto.getItensPrescricaoMdtos().get(0);
			
			if(TipoOperacaoEnum.INSERT.equals(prescricaoMdto.getTipoOperacaoBanco())){
				if(prescricaoMdto.getDiluente() != null){
					insereNovaPrescricaoParaDiluente(prescricaoMdto, percSeNec, pmeData,
							pmeDthrFim, nomeMicrocomputador,
							itemPrescricaoMdto, true);
				}
			}else if(TipoOperacaoEnum.DELETE.equals(prescricaoMdto.getTipoOperacaoBanco())){
				if(itemPrescricaoMdto.getPrescricaoMedicamento().getPrescricaoMdtoOrigem() != null){
					excluiDispensacaoDeDiluente(
							prescricaoMdto.getPrescricaoMedica().getId().getAtdSeq(),
							prescricaoMdto.getPrescricaoMedica().getId().getSeq(),
							itemPrescricaoMdto, nomeMicrocomputador,
							itemPrescricaoMdto.getPrescricaoMedicamento().getDiluente());
				}
			}/*else 	if(TipoOperacaoEnum.UPDATE.equals(prescricaoMdto.getTipoOperacaoBanco())
					&&(prescricaoMdto.getPrescricaoMdtoOrigem() != null)
					&&( (prescricaoMdto.getPrescricaoMdtoOrigem().getDiluente() == null
							&& prescricaoMdto.getDiluente() != null)
						|| (prescricaoMdto.getPrescricaoMdtoOrigem().getDiluente() != null
								&& prescricaoMdto.getDiluente() == null)
						|| (prescricaoMdto.getDiluente() != null && !prescricaoMdto.getDiluente().equals(prescricaoMdto.getPrescricaoMdtoOrigem().getDiluente()))
					)){
					throw new AGHUNegocioException(AfaDispensacaoDiluenteONExceptionCode.DILUENTE_NAO_PODE_SER_EDITADO_NA_PRESCRICAO);
				}
			}
		}
		*/
			else if(TipoOperacaoEnum.UPDATE.equals(prescricaoMdto.getTipoOperacaoBanco())){
				if(prescricaoMdto.getPrescricaoMdtoOrigem() != null){
					//Verifica se não tinha diluente e agora tem
					if(prescricaoMdto.getPrescricaoMdtoOrigem().getDiluente() == null
							&& prescricaoMdto.getDiluente() != null){
						insereNovaPrescricaoParaDiluente(prescricaoMdto, percSeNec, pmeData,
								pmeDthrFim, nomeMicrocomputador,
								itemPrescricaoMdto, Boolean.FALSE);
					}else //Verifica tinha diluente e agora não tem
						if(prescricaoMdto.getPrescricaoMdtoOrigem().getDiluente() != null
							&& prescricaoMdto.getDiluente() == null){
							excluiDispensacaoDeDiluente(
									prescricaoMdto.getPrescricaoMedica().getId().getAtdSeq(),
									prescricaoMdto.getPrescricaoMedica().getId().getSeq(),
									itemPrescricaoMdto, nomeMicrocomputador,
									itemPrescricaoMdto.getPrescricaoMedicamento().getPrescricaoMdtoOrigem().getDiluente());
					}else //Verifica diluente foi alterado
						if(prescricaoMdto.getPrescricaoMdtoOrigem().getDiluente() != null
								&& prescricaoMdto.getDiluente() != null
								&& !prescricaoMdto.getDiluente().equals(prescricaoMdto.getPrescricaoMdtoOrigem().getDiluente())){
							excluiDispensacaoDeDiluente(
									prescricaoMdto.getPrescricaoMedica().getId().getAtdSeq(),
									prescricaoMdto.getPrescricaoMedica().getId().getSeq(),
									itemPrescricaoMdto, nomeMicrocomputador, 
									itemPrescricaoMdto.getPrescricaoMedicamento().getPrescricaoMdtoOrigem().getDiluente());
							insereNovaPrescricaoParaDiluente(prescricaoMdto, percSeNec, pmeData,
									pmeDthrFim, nomeMicrocomputador,
									itemPrescricaoMdto, Boolean.FALSE);	
						}else{//Significa que medicamento foi alterado e diluente mantido
							//necessário exclui diluente e incluir novamente no novo item
							//ou atualizar o item da dispensacao
							excluiDispensacaoDeDiluente(
									prescricaoMdto.getPrescricaoMedica().getId().getAtdSeq(),
									prescricaoMdto.getPrescricaoMedica().getId().getSeq(),
									itemPrescricaoMdto, nomeMicrocomputador,
									itemPrescricaoMdto.getPrescricaoMedicamento().getDiluente());
							insereNovaPrescricaoParaDiluente(prescricaoMdto, percSeNec, pmeData,
									pmeDthrFim, nomeMicrocomputador,
									itemPrescricaoMdto, Boolean.FALSE);	
						}
					
				}
			}
		}
	}

	private void insereNovaPrescricaoParaDiluente(MpmPrescricaoMdto prescricaoMdto,
			BigDecimal percSeNec, Date pmeData, Date pmeDthrFim,
			String nomeMicrocomputador, 
			MpmItemPrescricaoMdto itemPrescricaoMdto, Boolean movimentacao)
			throws ApplicationBusinessException {
		
		Short qtd24h = prescricaoMedicaFacade.buscaCalculoQuantidade24Horas(
							prescricaoMdto.getFrequencia(),
							prescricaoMdto.getTipoFreqAprazamento() != null ? prescricaoMdto.getTipoFreqAprazamento().getSeq() : null,
							itemPrescricaoMdto.getDose(),//dosePadrao,
							itemPrescricaoMdto.getFormaDosagem() != null ? itemPrescricaoMdto.getFormaDosagem().getSeq(): null,
							prescricaoMdto.getDiluente() != null ? prescricaoMdto.getDiluente().getMatCodigo() : null
								);
		
		Object[] doseEFormaDosagem= getDoseItemPrescMdto(prescricaoMdto);
		AfaFormaDosagem formaDosagemItemPrescMdto = (AfaFormaDosagem) doseEFormaDosagem[0];
		BigDecimal itemDose = (BigDecimal) doseEFormaDosagem[1];
		
		BigDecimal dose = afaDispensacaoMdtosRN.mpmcCalcDoseDisp(qtd24h, pmeData, pmeDthrFim, itemDose, formaDosagemItemPrescMdto.getSeq());
		
		DominioSituacaoItemPrescritoDispensacaoMdto sitItemPrescr;
		if(movimentacao){
			sitItemPrescr = DominioSituacaoItemPrescritoDispensacaoMdto.GP;
		}else{
			sitItemPrescr = DominioSituacaoItemPrescritoDispensacaoMdto.IS;
		}
		
		try{
			AfaDispensacaoMdtos dispMdto = afaDispensacaoMdtosRN.processaNovaAfaDispMdto(
					itemPrescricaoMdto.getPrescricaoMedicamento().getPrescricaoMedica().getId().getAtdSeq(),
					itemPrescricaoMdto.getPrescricaoMedicamento().getPrescricaoMedica().getId().getSeq(),
					itemPrescricaoMdto.getPrescricaoMedicamento().getId().getAtdSeq(), 
					itemPrescricaoMdto.getPrescricaoMedicamento().getId().getSeq(), 
					itemPrescricaoMdto.getMedicamento().getMatCodigo(),
					itemPrescricaoMdto.getId().getSeqp(),
					prescricaoMdto.getIndSeNecessario(),
					dose,
					percSeNec,
					formaDosagemItemPrescMdto.getSeq(),
					sitItemPrescr, null);
			
			dispMdto.setMedicamento(prescricaoMdto.getDiluente());
			dispMdto.setUnidadeFuncional(afaDispensacaoMdtosRN.processaUnidadeFuncional(
					itemPrescricaoMdto.getPrescricaoMedicamento().getPrescricaoMedica().getId().getAtdSeq(),
					prescricaoMdto.getDiluente().getMatCodigo(), dose,
					formaDosagemItemPrescMdto.getSeq()));
		
			afaDispensacaoMdtosRN.criaDispMdtoTriagemPrescricao(dispMdto, nomeMicrocomputador);
		
		}catch (BaseException e) {
			throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.MPM_01574);
		}
	}
	
	private void excluiDispensacaoDeDiluente(Integer pmeAtdSeq,
			Integer pmeSeq, MpmItemPrescricaoMdto itemPrescricaoMdto,
			String nomeMicrocomputador, AfaMedicamento diluente) throws ApplicationBusinessException
			{
		List<AfaDispensacaoMdtos> dispensacoes = afaDispensacaoMdtosDAO
			.pesquisarAfaDispensacaoMdto(pmeAtdSeq, pmeSeq,
					itemPrescricaoMdto.getId().getPmdAtdSeq(),
					itemPrescricaoMdto.getPrescricaoMedicamento().getPrescricaoMdtoOrigem().getItensPrescricaoMdtos().get(0).getId().getPmdSeq(),
					itemPrescricaoMdto.getId().getMedMatCodigo(),
					itemPrescricaoMdto.getId().getSeqp(),
					null);
		
		for (AfaDispensacaoMdtos adm : dispensacoes) {
			if(adm.getMedicamento().equals(diluente)){
				AfaDispensacaoMdtos admOld = afaDispensacaoMdtosRN.getAfaDispOldDesatachado(adm);
				
				DominioSituacaoItemPrescritoDispensacaoMdto sitItem = adm.getIndSitItemPrescrito();
				
				if(DominioSituacaoItemPrescritoDispensacaoMdto.GP.equals(sitItem)){
					sitItem = DominioSituacaoItemPrescritoDispensacaoMdto.EG;
				}else if(DominioSituacaoItemPrescritoDispensacaoMdto.PG.equals(sitItem)){
					sitItem = DominioSituacaoItemPrescritoDispensacaoMdto.EG;
				}else if(DominioSituacaoItemPrescritoDispensacaoMdto.IS.equals(sitItem)){
					sitItem = DominioSituacaoItemPrescritoDispensacaoMdto.EI;
				}else if(DominioSituacaoItemPrescritoDispensacaoMdto.PI.equals(sitItem)){
					sitItem = DominioSituacaoItemPrescritoDispensacaoMdto.EI;
				}
				adm.setIndSitItemPrescrito(sitItem);
				
				try {
					//getAfaDispensacaoMdtosDAO().atualizarDepreciado(adm);
					afaDispensacaoMdtosRN.atualizaAfaDispMdto(adm, admOld, nomeMicrocomputador);
				}catch (BaseException e) {
					throw new ApplicationBusinessException(
							AfaDispensacaoMdtosRNExceptionCode.MPM_03090);
				}/*catch (MECBaseException e) {
					throw new AGHUNegocioException(
							AfaDispensacaoMdtosRNExceptionCode.MPM_03090);
				}*/
			}
		}
	}
	
	
	private Object[] getDoseItemPrescMdto(MpmPrescricaoMdto prescricaoMdto) {
		List<VMpmDosagem> dosagens = prescricaoMedicaFacade.buscarDosagensMedicamento(prescricaoMdto.getDiluente().getMatCodigo());
		AfaFormaDosagem	formaDosagem = farmaciaFacade.buscarDosagenPadraoMedicamento(prescricaoMdto.getDiluente().getMatCodigo());
		VMpmDosagem unidDosg = null;
		if(formaDosagem != null){
			for(int i = 0; i < dosagens.size(); i++){
				if(formaDosagem.getSeq().equals(dosagens.get(i).getFormaDosagem().getSeq())){
					unidDosg = dosagens.get(i);
				}
			}
		}
		else {
			if(dosagens != null && !dosagens.isEmpty()) {
				unidDosg = dosagens.get(0);
				formaDosagem = dosagens.get(0).getFormaDosagem();
			}
		}
		Object [] retorno = new Object[2];
		retorno[0]= unidDosg != null ? unidDosg.getFormaDosagem():null;
		retorno[1] = (formaDosagem != null && formaDosagem.getFatorConversaoUp()!=null)?formaDosagem.getFatorConversaoUp():prescricaoMdto.getDiluente().getConcentracao();
		
		return retorno;
	}

}