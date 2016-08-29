package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemQuestionario;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoTransporteQuestionario;
import br.gov.mec.aghu.model.AelExQuestionarioOrigens;
import br.gov.mec.aghu.model.AelExamesQuestionario;
import br.gov.mec.aghu.model.AelQuestao;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.AelQuestionariosConvUnid;
import br.gov.mec.aghu.model.AelQuestoesQuestionario;

public class AelQuestionariosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelQuestionarios> {
	
	
	private static final long serialVersionUID = -8778122984206063035L;
	public List<AelQuestionarios> pesquisarGrupoExamePorSeqDescricaoNroViasSitacao(Integer seq, String descricao,
			Byte nroVias, DominioSituacao situacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelQuestionarios.class);
		if(seq!=null){
			criteria.add(Restrictions.eq(AelQuestionarios.Fields.SEQ.toString(), seq));
		}
		if(descricao != null && !descricao.isEmpty()) {
			criteria.add(Restrictions.ilike(AelQuestionarios.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if(nroVias!=null){
			criteria.add(Restrictions.eq(AelQuestionarios.Fields.NRO_VIAS.toString(), nroVias));
		}
		if(situacao!=null){
			criteria.add(Restrictions.eq(AelQuestionarios.Fields.IND_SITUACAO.toString(), situacao));
		}
		return executeCriteria(criteria);
	}

	public List<AelQuestionarios> pesquisarQuestionarioPorItemSolicitacaoExame(final String sigla, final Integer manSeq, final Short codigoConvenioSaude,
			final DominioOrigemAtendimento origem, final DominioTipoTransporteQuestionario tipoTransporte) {
		final List<AelQuestionarios> resultado = executeCriteria(criarCriteriaPesquisarQuestionarioPorItemSolicitacaoExame(sigla, manSeq, codigoConvenioSaude, origem, tipoTransporte));
		return resultado;
	}

	private DetachedCriteria criarCriteriaPesquisarQuestionarioPorItemSolicitacaoExame(final String sigla, final Integer manSeq,
			final Short codigoConvenioSaude, final DominioOrigemAtendimento origem, final DominioTipoTransporteQuestionario tipoTransporte) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelQuestionarios.class, "QTN");
		criteria.createAlias(AelQuestionarios.Fields.AEL_EXAMES_QUESTIONARIO.toString(), "EQE");
		criteria.createAlias(AelQuestionarios.Fields.QUESTIONARIOS_CONV_UNIDS.toString(), "QCU");
		criteria.createAlias("EQE." + AelExamesQuestionario.Fields.QUESTIONARIO_ORIGENS.toString(), "EQO");
		
		criteria.add(Restrictions.eq("EQE."+ AelExamesQuestionario.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("QTN."+ AelQuestionarios.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
//		criteria.add(Restrictions.eq("EQE."+ AelExamesQuestionario.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(
				Restrictions.or(
						Restrictions.isNotNull("QCU." + AelQuestionariosConvUnid.Fields.UNF_SEQ.toString()), 
						Restrictions.and(
								Restrictions.isNotNull("QCU." + AelQuestionariosConvUnid.Fields.CODIGO_CONVENIO_SAUDE.toString()), 
								Restrictions.eq("QCU." + AelQuestionariosConvUnid.Fields.CODIGO_CONVENIO_SAUDE.toString(), codigoConvenioSaude))));
		criteria.add(
				Restrictions.or(
						Restrictions.eq("EQO." + AelExQuestionarioOrigens.Fields.ORIGEM_QUESTIONARIO.toString(), DominioOrigemQuestionario.T),
						Restrictions.eq("EQO." + AelExQuestionarioOrigens.Fields.ORIGEM_QUESTIONARIO.toString(), origem)));
		
		
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(AelQuestoesQuestionario.class, "QQU");
		subCriteria.createAlias(AelQuestoesQuestionario.Fields.QUESTAO.toString(), "QUE");
		subCriteria.setProjection(Projections.property("QQU." + AelQuestoesQuestionario.Fields.SEQ_QUESTIONARIO.toString()));
		subCriteria.add(Restrictions.eq("QQU." + AelQuestoesQuestionario.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		subCriteria.add(Restrictions.eq("QUE." + AelQuestao.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		subCriteria.add(Restrictions.eqProperty("QQU." + AelQuestoesQuestionario.Fields.SEQ_QUESTIONARIO.toString(),
				"QTN." + AelQuestionarios.Fields.SEQ.toString()));

		criteria.add(Subqueries.exists(subCriteria));
		
		criteria.add(Restrictions.eq("EQO." + AelExQuestionarioOrigens.Fields.EQE_EMA_EXA_SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq("EQO." + AelExQuestionarioOrigens.Fields.EQE_EMA_MAN_SEQ.toString(), manSeq));

		if (tipoTransporte == null) {
			criteria.add(Restrictions.eq("EQO." + AelExQuestionarioOrigens.Fields.TIPO_TRANSPORTE.toString(), DominioTipoTransporteQuestionario.T));
		}
		else {
			criteria.add(
					Restrictions.or(
							Restrictions.eq("EQO." + AelExQuestionarioOrigens.Fields.TIPO_TRANSPORTE.toString(), DominioTipoTransporteQuestionario.T),
							Restrictions.eq("EQO." + AelExQuestionarioOrigens.Fields.TIPO_TRANSPORTE.toString(), tipoTransporte)));
		}

		
		
		criteria.setResultTransformer(criteria.DISTINCT_ROOT_ENTITY);
		
		return criteria;
	}
	
	private DetachedCriteria montaCriteriaAelQuestionarios(Object param, Boolean isCount){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelQuestionarios.class);

		if(param != null) {
			if(CoreUtil.isNumeroInteger(param)) {
				criteria.add(Restrictions.eq(AelQuestionarios.Fields.SEQ.toString(), Integer.valueOf(param.toString())));	
			} else {
				criteria.add(Restrictions.ilike(AelQuestionarios.Fields.DESCRICAO.toString(), (String)param, MatchMode.ANYWHERE));
			}
		}
		if(!isCount) {
			criteria.addOrder(Order.asc(AelQuestionarios.Fields.DESCRICAO.toString()));
		}
		return criteria;
	}

	public List<AelQuestionarios> pesquisarAelQuestionarios(Object param) {
		return executeCriteria(this.montaCriteriaAelQuestionarios(param, Boolean.FALSE));
	}

	public Long pesquisarAelQuestionariosCount(String param) {
		return executeCriteriaCount(this.montaCriteriaAelQuestionarios(param, Boolean.TRUE));
	}	

}