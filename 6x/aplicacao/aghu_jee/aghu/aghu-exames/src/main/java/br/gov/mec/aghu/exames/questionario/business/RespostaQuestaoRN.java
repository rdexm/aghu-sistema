package br.gov.mec.aghu.exames.questionario.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelRespostaQuestaoDAO;
import br.gov.mec.aghu.model.AelRespostaQuestao;
import br.gov.mec.aghu.model.AelRespostaQuestaoId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RespostaQuestaoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RespostaQuestaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelRespostaQuestaoDAO aelRespostaQuestaoDAO;
	private static final long	serialVersionUID	= -7594260026217706348L;

	public enum RespostaQuestaoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_VALORES_OBRIGATORIOS;
	}

	public void persistir(final AelRespostaQuestao respostaQuestao) throws ApplicationBusinessException {
		if (respostaQuestao.getId() == null) {
			respostaQuestao.setId(this.gerarId(respostaQuestao));
			this.getAelRespostaQuestaoDAO().persistir(respostaQuestao);
		} else {
			this.getAelRespostaQuestaoDAO().atualizar(respostaQuestao);
		}
	}

	private AelRespostaQuestaoId gerarId(final AelRespostaQuestao respostaQuestao) throws ApplicationBusinessException {
		this.verificarDependencias(respostaQuestao);
		final AelRespostaQuestaoId id = new AelRespostaQuestaoId();
		id.setIseSoeSeq(respostaQuestao.getAelItemSolicitacaoExames().getId().getSoeSeq());
		id.setIseSeqp(respostaQuestao.getAelItemSolicitacaoExames().getId().getSeqp());
		id.setEqeEmaExaSigla(respostaQuestao.getAelItemSolicitacaoExames().getExame().getSigla());
		id.setEqeEmaManSeq(respostaQuestao.getAelItemSolicitacaoExames().getMaterialAnalise().getSeq());
		id.setEqeQtnSeq(respostaQuestao.getQuestaoQuestionario().getId().getQtnSeq());
		id.setQquQtnSeq(respostaQuestao.getQuestaoQuestionario().getId().getQtnSeq());
		id.setQquQaoSeq(respostaQuestao.getQuestaoQuestionario().getId().getQaoSeq());
		return id;
	}

	private void verificarDependencias(final AelRespostaQuestao respostaQuestao) throws ApplicationBusinessException {
		if (respostaQuestao.getAelItemSolicitacaoExames() == null || respostaQuestao.getQuestaoQuestionario() == null) {
			throw new ApplicationBusinessException(RespostaQuestaoRNExceptionCode.MENSAGEM_VALORES_OBRIGATORIOS);
		}
	}

	protected AelRespostaQuestaoDAO getAelRespostaQuestaoDAO() {
		return aelRespostaQuestaoDAO;
	}

}
