package br.gov.mec.aghu.estoque.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceMotivoMovimentoDAO;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.model.SceMotivoMovimento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterMotivoMovimentoRN extends BaseBusiness {

	private static final long serialVersionUID = 33122238032329194L;

	private static final Log LOG = LogFactory.getLog(ManterMotivoMovimentoRN.class);

	@Inject
	private SceMotivoMovimentoDAO sceMotivoMovimentoDAO;

	@Inject
	private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;

	public enum ManterMotivoMovimentoRNExceptionCode implements BusinessExceptionCode {
		MSG_MOTIVO_MOVIMENTO_M3, MSG_MOTIVO_MOVIMENTO_M7
	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public void salvarMotivoMovimento(SceMotivoMovimento motivoMovimento) throws BaseException {

		if (StringUtils.isEmpty(motivoMovimento.getDescricao()) || motivoMovimento.getIndSituacao() == null
				|| motivoMovimento.getId().getTmvSeq() == null || motivoMovimento.getId().getTmvComplemento() == null) {
			throw new BaseException(ManterMotivoMovimentoRNExceptionCode.MSG_MOTIVO_MOVIMENTO_M3);
		}

		if (motivoMovimento.getId().getTmvSeq() != null && motivoMovimento.getId().getTmvComplemento() != null
				&& motivoMovimento.getId().getNumero() != null) {
			this.updateMotivoMovimento(motivoMovimento);
		} else {
			this.persistirMotivoMovimento(motivoMovimento);
		}
	}

	public void removerMotivoMovimento(SceMotivoMovimento motivoMovimento) throws BaseException {
		if (getSceMovimentoMaterialDAO().motivoMovimentoUsado(motivoMovimento.getId().getTmvSeq(),
				motivoMovimento.getId().getTmvComplemento(), motivoMovimento.getId().getNumero())) {
			throw new BaseException(ManterMotivoMovimentoRNExceptionCode.MSG_MOTIVO_MOVIMENTO_M7);
		}

		this.getSceMotivoMovimentoDAO().remover(motivoMovimento);
	}

	private void persistirMotivoMovimento(SceMotivoMovimento motivoMovimento) {
		this.getSceMotivoMovimentoDAO().persistir(motivoMovimento);
	}

	private void updateMotivoMovimento(SceMotivoMovimento motivoMovimento) {
		this.getSceMotivoMovimentoDAO().atualizar(motivoMovimento);
	}

	private SceMotivoMovimentoDAO getSceMotivoMovimentoDAO() {
		return this.sceMotivoMovimentoDAO;
	}

	private SceMovimentoMaterialDAO getSceMovimentoMaterialDAO() {
		return this.sceMovimentoMaterialDAO;
	}

}
