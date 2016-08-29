package br.gov.mec.aghu.sig.custos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.SigAtividadeEquipamentos;
import br.gov.mec.aghu.sig.dao.SigAtividadeEquipamentosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterEquipamentosAtividadeON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterEquipamentosAtividadeON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SigAtividadeEquipamentosDAO sigAtividadeEquipamentosDAO;

	private static final long serialVersionUID = 1391866428292896131L;

	public enum ManterServicosAtividadeONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_EQUIPAMENTO_JA_ASSOCIADO_ATIVIDADE, MENSAGEM_SIG_DIRECIONADORES_OBRIGATORIO;
	}

	public void validarInclusaoEquipamentoAtividade(SigAtividadeEquipamentos equipamento, List<SigAtividadeEquipamentos> listaEquipamentos)
			throws ApplicationBusinessException {

		for (SigAtividadeEquipamentos sigAtividadeEquipamentos : listaEquipamentos) {
			if(equipamento.getCodPatrimonio().equalsIgnoreCase(sigAtividadeEquipamentos.getCodPatrimonio())){
				throw new ApplicationBusinessException(ManterServicosAtividadeONExceptionCode.MENSAGEM_EQUIPAMENTO_JA_ASSOCIADO_ATIVIDADE);
			}
		}

		if (equipamento.getSigDirecionadores() == null){
			throw new ApplicationBusinessException(ManterServicosAtividadeONExceptionCode.MENSAGEM_SIG_DIRECIONADORES_OBRIGATORIO);
		}
	}

	public void persistirEquipamento(SigAtividadeEquipamentos sigAtividadeEquipamentos) {
		if (sigAtividadeEquipamentos.getSeq() != null) {
			this.getSigAtividadeEquipamentosDAO().atualizar(sigAtividadeEquipamentos);
		} else {
			this.getSigAtividadeEquipamentosDAO().persistir(sigAtividadeEquipamentos);
		}
	}

	protected SigAtividadeEquipamentosDAO getSigAtividadeEquipamentosDAO() {
		return sigAtividadeEquipamentosDAO;
	}

}
