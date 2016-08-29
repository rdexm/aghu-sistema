package br.gov.mec.aghu.exames.questionario.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelQuestoesQuestionarioDAO;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.AelQuestoesQuestionario;
import br.gov.mec.aghu.model.AelRespostaQuestao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class RespostaQuestaoON extends BaseBusiness {


@EJB
private RespostaQuestaoRN respostaQuestaoRN;

private static final Log LOG = LogFactory.getLog(RespostaQuestaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelQuestoesQuestionarioDAO aelQuestoesQuestionarioDAO;

	private static final long	serialVersionUID	= 6526321716112189288L;

	public void persistir(final AelRespostaQuestao respostaQuestao) throws ApplicationBusinessException {
		this.getRespostaQuestaoRN().persistir(respostaQuestao);
	}
	
	public List<AelRespostaQuestao> criarRespostasQuestionario(final List<AelQuestionarios> questionarios) {
		if(questionarios == null || questionarios.isEmpty()){
			return new ArrayList<AelRespostaQuestao>(0);
		}
		final List<AelRespostaQuestao> respostaQuestaos = new ArrayList<AelRespostaQuestao>();
		final Integer [] qtnSeq = new Integer[questionarios.size()];
		int i = 0;
		for (final AelQuestionarios questionario : questionarios) {
			qtnSeq[i++] = questionario.getSeq();
		}
		final List<AelQuestoesQuestionario> questoesQuestionarios = getAelQuestoesQuestionarioDAO().buscarAelQuestoesQuestionarioOrderByOrdem(qtnSeq);
		if(questoesQuestionarios == null || questoesQuestionarios.isEmpty()){
			return new ArrayList<AelRespostaQuestao>(0);
		}
		for (final AelQuestoesQuestionario aelQuestoesQuestionario : questoesQuestionarios) {
			final AelRespostaQuestao aelRespostaQuestao = new AelRespostaQuestao();
			aelRespostaQuestao.setQuestaoQuestionario(aelQuestoesQuestionario);
			aelRespostaQuestao.setQuestionario(aelQuestoesQuestionario.getAelQuestionarios());
			respostaQuestaos.add(aelRespostaQuestao);
		}
		return respostaQuestaos;
	}

	protected AelQuestoesQuestionarioDAO getAelQuestoesQuestionarioDAO() {
		return aelQuestoesQuestionarioDAO;
	}
	
	protected RespostaQuestaoRN getRespostaQuestaoRN() {
		return respostaQuestaoRN;
	}

}
