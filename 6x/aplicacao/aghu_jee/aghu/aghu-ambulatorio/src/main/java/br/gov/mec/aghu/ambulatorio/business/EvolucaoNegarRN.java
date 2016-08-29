package br.gov.mec.aghu.ambulatorio.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamQuestaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRespQuestEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRespostaEvolucoesDAO;
import br.gov.mec.aghu.model.MamQuestao;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class EvolucaoNegarRN extends BaseBusiness {

	private static final long serialVersionUID = 407719739177892267L;
	private static final Log LOG = LogFactory.getLog(EvolucaoNegarRN.class);

	@Inject
	private MamQuestaoDAO mamQuestaoDAO;
	
	@Inject
	private MamRespostaEvolucoesDAO mamRespostaEvolucoesDAO;
	
	@Inject
	private MamRespQuestEvolucoesDAO mamRespQuestEvolucoesDAO;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * @ORADB MAMC_RESPONDEU_CUST
	 * 
	 * @param tipo
	 * @param qutSeq
	 * @param seqp
	 * @param chave
	 */
	public Boolean verificarCustomizacaoRespondida(Character tipo, Integer qutSeq, Short seqp, Long chave) {

		Boolean retorno = null;

		List<MamQuestao> questoes = getMamQuestaoDAO().listarQuestoesPorCustomQuestaoQuestionario(qutSeq, seqp);
		for (MamQuestao questao : questoes) {
			// TODO conforme informado na procedure P02 da estória #50040, os cursores de anamnese foram removidos e serão adicionados posteriormente.
			/*if (Character.valueOf('A').equals(tipo)) {
				
			} else*/
			if (Character.valueOf('E').equals(tipo)) {
				boolean existe = getMamRespostaEvolucoesDAO().verificarExistenciaRespostaEvolucaoComRespostaOuValorValido(chave, questao.getId().getQutSeq(),
						questao.getId().getSeqp());

				if (existe || retorno == null) {
					retorno = existe;
				}

				if (!retorno) {
					existe = getMamRespQuestEvolucoesDAO().verificarExistenciaRespQuestEvolucaoComRespostaOuValorValido(chave, questao.getId().getQutSeq(),
							questao.getId().getSeqp());

					if (existe || retorno == null) {
						retorno = existe;
					}
				}
			}
		}

		if (retorno == null) {
			retorno = Boolean.FALSE;
		}

		return retorno;
	}

	public MamQuestaoDAO getMamQuestaoDAO() {
		return mamQuestaoDAO;
	}

	public MamRespostaEvolucoesDAO getMamRespostaEvolucoesDAO() {
		return mamRespostaEvolucoesDAO;
	}

	public MamRespQuestEvolucoesDAO getMamRespQuestEvolucoesDAO() {
		return mamRespQuestEvolucoesDAO;
	}

}
