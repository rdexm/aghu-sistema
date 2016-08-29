package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.comissoes.vo.SolicitacoesUsoMedicamentoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class VMpmItemPrcrMdtosON extends BaseBusiness {

	private static final long serialVersionUID = -8624003621098731768L;

	private static final Log LOG = LogFactory.getLog(VMpmItemPrcrMdtosON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public enum VMpmItemPrcrMdtosONExceptionCode implements BusinessExceptionCode {
		MPM_01554, MPM_01439;
	}
	
	public void validarPesquisaSolicitacoesUsoMedicamentos(SolicitacoesUsoMedicamentoVO filtro) throws ApplicationBusinessException {

		if (filtro.getDataInicio() != null || filtro.getDataFim() != null) {
			validaDatas(filtro);
		} else {
			// Regra RN03, estória #1291
			if (filtro.getCodProntuario() == null) {
				boolean primeiroCampo = false;

				if (filtro.getMedicamento() != null) {
					primeiroCampo = true;
				}

				if (filtro.getTipoUso() != null) {
					if (primeiroCampo) {
						return;
					}

					primeiroCampo = true;
				}

				if (filtro.getGrupoUsoMedicamento() != null) {
					if (primeiroCampo) {
						return;
					}

					primeiroCampo = true;
				}
				
				if (filtro.getvMpmpProfInterna() != null) {
					if (primeiroCampo) {
						return;
					}

					primeiroCampo = true;
				}
				
				if (filtro.getvAghUnidFuncional() != null && primeiroCampo) {
					return;
				}
				
				//Regra adicionada a pedido do análista. Legado AGHWeb. - #49628 
				if(filtro.getMedicamento() == null && filtro.getTipoUso() == null && filtro.getGrupoUsoMedicamento() == null &&
						filtro.getvMpmpProfInterna() == null && filtro.getvAghUnidFuncional() == null){
					return;
				}
				throw new ApplicationBusinessException(VMpmItemPrcrMdtosONExceptionCode.MPM_01554);
			}
		}
	}

	private void validaDatas(SolicitacoesUsoMedicamentoVO filtro)
			throws ApplicationBusinessException {
		if (filtro.getDataInicio() != null && filtro.getDataFim() == null) {
			throw new ApplicationBusinessException(VMpmItemPrcrMdtosONExceptionCode.MPM_01554);
		}

		if (filtro.getDataInicio() == null && filtro.getDataFim() != null) {
			throw new ApplicationBusinessException(VMpmItemPrcrMdtosONExceptionCode.MPM_01554);
		}
		
		if(!filtro.getDataInicio().equals(filtro.getDataFim())){
			if (!filtro.getDataInicio().before(filtro.getDataFim())) {
				throw new ApplicationBusinessException(VMpmItemPrcrMdtosONExceptionCode.MPM_01439);
			}
		}
	}
}
