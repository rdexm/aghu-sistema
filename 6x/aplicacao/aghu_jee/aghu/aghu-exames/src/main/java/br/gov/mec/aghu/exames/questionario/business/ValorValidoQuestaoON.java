package br.gov.mec.aghu.exames.questionario.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelValorValidoQuestaoDAO;
import br.gov.mec.aghu.model.AelValorValidoQuestao;
import br.gov.mec.aghu.model.AelValorValidoQuestaoId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ValorValidoQuestaoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ValorValidoQuestaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelValorValidoQuestaoDAO aelValorValidoQuestaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1717868513495480521L;
	
	public enum ValorValidoQuestaoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_REMOVER_DEPENDENCIAS;
	}
	
	/**
	 * Valida se o valor válido possui relacionamento com AEL_RESPOSTAS_QUESTOES
	 * 
	 * @param aelValorValidoQuestaoId
	 * @throws ApplicationBusinessException
	 */
	public void validarRelacionamentosValorValidoBeforeDelete(Integer qaoSeq, Short seqP) throws ApplicationBusinessException {
		AelValorValidoQuestaoId id = new AelValorValidoQuestaoId();
		id.setQaoSeq(qaoSeq);
		id.setSeqp(seqP);
		AelValorValidoQuestao valorValido = getAelValorValidoQuestaoDAO().obterPorChavePrimaria(id);
		
		if (valorValido == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		if(valorValido.getAelRespostaQuestoes() != null && valorValido.getAelRespostaQuestoes().size() > 0) {
			throw new ApplicationBusinessException(ValorValidoQuestaoONExceptionCode.MENSAGEM_ERRO_REMOVER_DEPENDENCIAS,"AEL_RESPOSTAS_QUESTOES");
		}
		getAelValorValidoQuestaoDAO().remover(valorValido);
	}
	
	public void persistirValorValidoQuestao(AelValorValidoQuestao valorValidoQuestao) {
		Short seqp = this.getAelValorValidoQuestaoDAO().obterSeqValorValidoQuestao(valorValidoQuestao.getAelQuestao().getSeq());
		AelValorValidoQuestaoId id = new AelValorValidoQuestaoId();
		id.setQaoSeq(valorValidoQuestao.getAelQuestao().getSeq());
		id.setSeqp((short) (seqp + 1));
		valorValidoQuestao.setId(id);
		this.getAelValorValidoQuestaoDAO().persistir(valorValidoQuestao);
	}
	
	protected AelValorValidoQuestaoDAO getAelValorValidoQuestaoDAO() {
		return aelValorValidoQuestaoDAO;
	}
}