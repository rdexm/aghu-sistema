package br.gov.mec.aghu.transplante.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.model.MtxTipoRetorno;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class MtxTipoRetornoON extends BaseBusiness {

	private static final long serialVersionUID = -8883288417265871288L;

	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum MtxTipoRetornoONExceptionCode implements BusinessExceptionCode {
		MSG_DESCRICAO_OBRIGATORIO_TR,
		MSG_TIPO_OBRIGATORIO_TR,
		MSG_TAMANHO_DESCRICAO_TR,
		MSG_SITUACAO_OBRIGATORIO_TR;
	}
	
	public void validarDescricaoTipoRetorno(MtxTipoRetorno mtxTipoRetorno) throws ApplicationBusinessException, BaseListException {
		BaseListException lista = new BaseListException();
		if(mtxTipoRetorno.getDescricao() != null && mtxTipoRetorno.getDescricao().trim().length() > 200){
			lista.add(new ApplicationBusinessException(MtxTipoRetornoONExceptionCode.MSG_TAMANHO_DESCRICAO_TR, Severity.ERROR));
		}
		if(mtxTipoRetorno.getDescricao() == null || mtxTipoRetorno.getDescricao().trim().equals("")){
			lista.add(new ApplicationBusinessException(MtxTipoRetornoONExceptionCode.MSG_DESCRICAO_OBRIGATORIO_TR, Severity.ERROR));
		}
		if(mtxTipoRetorno.getIndTipo() == null){
			lista.add(new ApplicationBusinessException(MtxTipoRetornoONExceptionCode.MSG_TIPO_OBRIGATORIO_TR, Severity.ERROR));
		}
		mtxTipoRetorno.setDescricao(mtxTipoRetorno.getDescricao().trim());
		if (lista.hasException()) {
			throw lista;
		}
	}
	public void validarSituacaoTipoRetorno(MtxTipoRetorno mtxTipoRetorno) throws ApplicationBusinessException, BaseListException {
		if(mtxTipoRetorno.getIndSituacao() == null){
			throw new ApplicationBusinessException(MtxTipoRetornoONExceptionCode.MSG_SITUACAO_OBRIGATORIO_TR, Severity.ERROR);
		}
	}
}
