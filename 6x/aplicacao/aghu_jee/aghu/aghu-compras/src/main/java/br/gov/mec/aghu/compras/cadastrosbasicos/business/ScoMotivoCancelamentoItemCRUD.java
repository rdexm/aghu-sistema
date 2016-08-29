package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoMotivoCancelamentoItemDAO;
import br.gov.mec.aghu.model.ScoMotivoCancelamentoItem;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoMotivoCancelamentoItemCRUD extends BaseBusiness {

	private static final long serialVersionUID = 7030325951132867820L;
	private static final Log LOG = LogFactory.getLog(ScoMotivoCancelamentoItemCRUD.class);

	private enum MotivoCancelamentoItemCRUDExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_MOTIVO_CANCELAMENTO_ITEM, CODIGO_MOTIVO_CANCELAMENTO_ITEM_OBRIGATORIO, DESCRICAO_MOTIVO_CANCELAMENTO_ITEM_OBRIGATORIO, CODIGO_MOTIVO_CANCELAMENTO_ITEM_JA_EXISTENTE, DESCRICAO_MOTIVO_CANCELAMENTO_ITEM_JA_EXISTENTE, MENSAGEM_PARAM_OBRIG, MENSAGEM_ERRO_PERSISTIR_DADOS
	};
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private ScoMotivoCancelamentoItemDAO scoMotivoCancelamentoItemDAO;

	/**
	 * Método responsável por incluir um novo motivo de cancelamento.
	 * 
	 * @param motivoCancel
	 * @throws AGHUNegocioException
	 */
	public void incluirMotivoCancelamentoItem(ScoMotivoCancelamentoItem motivoCancel) throws ApplicationBusinessException {

		if (motivoCancel == null) {
			throw new ApplicationBusinessException(MotivoCancelamentoItemCRUDExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		try {
			// Verififcar se a descrição ja existe 
			if (this.getScoMotivoCancelamentoItemDAO().verificarDescricao(motivoCancel)) {
				// Nova unidade de medida
				if (motivoCancel.getVersion() == null) {
					if (!this.getScoMotivoCancelamentoItemDAO().verificarCodigo(motivoCancel)) {
						throw new ApplicationBusinessException(
								MotivoCancelamentoItemCRUDExceptionCode.CODIGO_MOTIVO_CANCELAMENTO_ITEM_JA_EXISTENTE);
					}
					this.getScoMotivoCancelamentoItemDAO().persistir(motivoCancel);
					this.getScoMotivoCancelamentoItemDAO().flush();
				} else {
					this.getScoMotivoCancelamentoItemDAO().atualizar(motivoCancel);
					this.getScoMotivoCancelamentoItemDAO().flush();
				}

			} else {
				throw new ApplicationBusinessException(
						MotivoCancelamentoItemCRUDExceptionCode.DESCRICAO_MOTIVO_CANCELAMENTO_ITEM_JA_EXISTENTE);
			}

		} catch (Exception e) {
			throw new ApplicationBusinessException(MotivoCancelamentoItemCRUDExceptionCode.MENSAGEM_ERRO_PERSISTIR_DADOS, e.getMessage());
		}

	}

	protected ScoMotivoCancelamentoItemDAO getScoMotivoCancelamentoItemDAO() {
		return this.scoMotivoCancelamentoItemDAO;
	}

}