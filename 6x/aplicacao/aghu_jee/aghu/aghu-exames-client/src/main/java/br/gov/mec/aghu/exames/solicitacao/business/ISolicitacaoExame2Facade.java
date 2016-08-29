package br.gov.mec.aghu.exames.solicitacao.business;

import java.io.Serializable;

import br.gov.mec.aghu.model.AelExameApItemSolic;
import br.gov.mec.aghu.core.exception.BaseException;

public interface ISolicitacaoExame2Facade extends Serializable {

	void inserirAelExameApItemSolic(AelExameApItemSolic elemento) throws BaseException;

}