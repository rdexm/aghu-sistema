package br.gov.mec.aghu.blococirurgico.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.blococirurgico.vo.SubRelatorioNotasDeConsumoDaSalaExamesVO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcNecessidadeCirurgica;
import br.gov.mec.aghu.model.MbcSolicitacaoEspExecCirg;
import br.gov.mec.aghu.model.MbcSolicitacaoEspExecCirgId;

public class MbcSolicitacaoEspExecCirgDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcSolicitacaoEspExecCirg> {

	private static final long serialVersionUID = -8420810747497061875L;
	
	public List<MbcSolicitacaoEspExecCirg> pesquisarMbcSolicitacaoEspExecCirgPorCrgSeqCaractUnf(Integer crgSeq, ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSolicitacaoEspExecCirg.class, "SEC");
		
		criteria.createAlias("SEC." + MbcSolicitacaoEspExecCirg.Fields.MBC_NECESSIDADE_CIRURGICAS.toString(), "NCI");
		criteria.createAlias("NCI." + MbcNecessidadeCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), "UNF");
		criteria.createAlias("UNF." + AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "CARACT");
		
		criteria.add(Restrictions.eq("CARACT." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA, constanteAghCaractUnidFuncionais));
		criteria.add(Restrictions.eq("SEC." + MbcSolicitacaoEspExecCirg.Fields.ID_CRG_SEQ, crgSeq));
		
		return executeCriteria(criteria);	
	}

	//# 24939 C7
	public List<MbcSolicitacaoEspExecCirg> listarMbcSolicitacaoEspExecCirgPorCrgSeq(Integer crgSeq){
		DetachedCriteria criteria = obterCriteriaInicial();
		
		criteria.add(Restrictions.eq("SEC." + MbcSolicitacaoEspExecCirg.Fields.ID_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.isNotNull("NCI." + MbcNecessidadeCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS.toString()));
		
		return executeCriteria(criteria);
	}	
	
	/**
	 * Retorna todas as solicitacoes especiais da cirurgia fornecida.
	 * 
	 * @param cirurgia
	 * @return
	 */
	public List<MbcSolicitacaoEspExecCirg> listarMbcSolicitacaoEspExecCirg(
			MbcCirurgias cirurgia) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MbcSolicitacaoEspExecCirg.class);

		criteria.add(Restrictions.eq(
				MbcSolicitacaoEspExecCirg.Fields.MBC_CIRURGIAS.toString(),
				cirurgia));

		return executeCriteria(criteria);
	}	
	
	/**
	 * Pesquisa MbcSolicitacaoEspExecCirg com unidade funcional disponível para escala cirúrgica
	 * @param crgSeq
	 * @return
	 */
	public List<MbcSolicitacaoEspExecCirg> pesquisarExameSolicitacaoEspecialidadeExecutaEscalaCirurgica(Integer crgSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcSolicitacaoEspExecCirg.class, "SEC");
		
		criteria.createAlias("SEC.".concat(MbcSolicitacaoEspExecCirg.Fields.MBC_NECESSIDADE_CIRURGICAS.toString()), "NCI");
		criteria.createAlias("NCI.".concat(MbcNecessidadeCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS.toString()), "UNF");
		criteria.createAlias("SEC.".concat(MbcSolicitacaoEspExecCirg.Fields.MBC_CIRURGIAS.toString()), "CRG");
		
		criteria.add(Restrictions.eq("CRG.".concat(MbcCirurgias.Fields.SEQ.toString()), crgSeq));

		return  executeCriteria(criteria);
	}
	
	/**
	 * Pesquisa MbcSolicitacaoEspExecCirg sem unidade funcional disponível para escala cirúrgica
	 * @param crgSeq
	 * @return
	 */
	public List<MbcSolicitacaoEspExecCirg> pesquisarMaterialSolicitacaoEspecialidadeExecutaEscalaCirurgica(Integer crgSeq, Short unidExecExames) {

		final DetachedCriteria criteria = obterCriteriaInicial();
		
		
		if(unidExecExames == null){
			criteria.createAlias("SEC.".concat(MbcSolicitacaoEspExecCirg.Fields.MBC_CIRURGIAS.toString()), "CRG");
			criteria.add(Restrictions.isNull("NCI.".concat(MbcNecessidadeCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS.toString())));
		}else{
			criteria.add(Restrictions.eq(("NCI.".concat(MbcNecessidadeCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS.toString().concat(".").concat(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()))), unidExecExames));
		}
		criteria.add(Restrictions.eq("SEC.".concat(MbcSolicitacaoEspExecCirg.Fields.ID_CRG_SEQ.toString()), crgSeq));

		return  executeCriteria(criteria);
	}
	
	/**
	 * Pesquisa MbcSolicitacaoEspExecCirg disponível para escala cirúrgica
	 * @param crgSeq
	 * @return
	 */
	public List<MbcSolicitacaoEspExecCirg> pesquisarSolicitacaoEspecialidadePorCrgSeq(Integer crgSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcSolicitacaoEspExecCirg.class, "SEC");
		
		criteria.createAlias("SEC." + MbcSolicitacaoEspExecCirg.Fields.MBC_NECESSIDADE_CIRURGICAS.toString(), "NEC");
		criteria.createAlias("NEC." + MbcNecessidadeCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), "UND", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("SEC." + MbcSolicitacaoEspExecCirg.Fields.ID_CRG_SEQ.toString(), crgSeq));

		return  executeCriteria(criteria);
	}
	
	/**
	 * Pesquisa MbcSolicitacaoEspExecCirg com necessidade cirúrgica já cadastrada para a cirurgia.
	 * @param crgSeq
	 * @return
	 */
	public MbcSolicitacaoEspExecCirg verificarSolicEspExistente(Integer crgSeq, Short nciSeq, Short nciSeqDiversa) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcSolicitacaoEspExecCirg.class, "SEC");
		
		criteria.add(Restrictions.eq("SEC." + MbcSolicitacaoEspExecCirg.Fields.ID_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq("SEC." + MbcSolicitacaoEspExecCirg.Fields.ID_NCI_SEQ.toString(), nciSeq));
		criteria.add(Restrictions.ne("SEC." + MbcSolicitacaoEspExecCirg.Fields.ID_NCI_SEQ.toString(), nciSeqDiversa));

		return (MbcSolicitacaoEspExecCirg) executeCriteriaUniqueResult(criteria);
	}
	
	public Short obterSolicEspProximoSeqp (Integer crgSeq, Short nciSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcSolicitacaoEspExecCirg.class);
		
		criteria.setProjection(Projections.max(MbcSolicitacaoEspExecCirg.Fields.ID_SEQP.toString()));
		criteria.add(Restrictions.eq(MbcSolicitacaoEspExecCirg.Fields.ID_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcSolicitacaoEspExecCirg.Fields.ID_NCI_SEQ.toString(), nciSeq));
		
		Short result = (Short) executeCriteriaUniqueResult(criteria);
		
		if (result == null) {
			result = (short) 0;
		}
		result++;

		return result;
	}
	
	public List<SubRelatorioNotasDeConsumoDaSalaExamesVO> obterExamesPorCrgSeq(Integer crgSeq){
		List<SubRelatorioNotasDeConsumoDaSalaExamesVO> retorno = new ArrayList<SubRelatorioNotasDeConsumoDaSalaExamesVO>();
		
		retorno.addAll(obterExamesPorCrgSeqUnion1(crgSeq));
		retorno.addAll(obterExamesPorCrgSeqUnion2(crgSeq));
		
		return retorno;
	}
	
	private List<SubRelatorioNotasDeConsumoDaSalaExamesVO> obterExamesPorCrgSeqUnion1(Integer crgSeq){
		DetachedCriteria criteria = obterCriteriaInicial();
		
		StringBuilder sqlProjection = new StringBuilder(100);
		sqlProjection.append("     RPAD(SUBSTR(nci1_.DESCRICAO,1,15),15,'.') || ':S'    " 
			).append( SubRelatorioNotasDeConsumoDaSalaExamesVO.Fields.DESCRICAO_EXAME.toString());
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sqlProjection(sqlProjection.toString(),
						new String[]{SubRelatorioNotasDeConsumoDaSalaExamesVO.Fields.DESCRICAO_EXAME.toString()},
						new Type[] { StringType.INSTANCE })));
		
		criteria.add(Restrictions.eq("SEC." + MbcSolicitacaoEspExecCirg.Fields.ID_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.isNotNull("NCI." + MbcNecessidadeCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(SubRelatorioNotasDeConsumoDaSalaExamesVO.class));
		
		return executeCriteria(criteria);
	}

	private List<SubRelatorioNotasDeConsumoDaSalaExamesVO> obterExamesPorCrgSeqUnion2(Integer crgSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcNecessidadeCirurgica.class, "NCI");
		
		StringBuilder sqlProjection = new StringBuilder(100);
		sqlProjection.append("     RPAD(SUBSTR({alias}.DESCRICAO,1,15),15,'.') || ':_'    "
			).append( SubRelatorioNotasDeConsumoDaSalaExamesVO.Fields.DESCRICAO_EXAME.toString());
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sqlProjection(sqlProjection.toString(),
						new String[]{SubRelatorioNotasDeConsumoDaSalaExamesVO.Fields.DESCRICAO_EXAME.toString()},
						new Type[] { StringType.INSTANCE })));
		
		criteria.add(Restrictions.isNotNull("NCI." + MbcNecessidadeCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS.toString()));
		criteria.add(Restrictions.eq("NCI." + MbcNecessidadeCirurgica.Fields.SITUACAO.toString(), 
									DominioSituacao.A));
		
		DetachedCriteria cp = DetachedCriteria.forClass(MbcSolicitacaoEspExecCirg.class, "SEC");
		criteria.add(Subqueries.notExists(cp.setProjection(Property.forName("SEC." + MbcSolicitacaoEspExecCirg.Fields.ID_NCI_SEQ.toString()))
				.add(Restrictions.eqProperty("SEC." + MbcSolicitacaoEspExecCirg.Fields.ID_NCI_SEQ.toString(),
						"NCI." + MbcNecessidadeCirurgica.Fields.SEQ.toString()))
				.add(Restrictions.eq("SEC." + MbcSolicitacaoEspExecCirg.Fields.ID_CRG_SEQ.toString(), crgSeq))));
		
		/*criteria.getExecutableCriteria(getSession()).setFirstResult(0);
		criteria.getExecutableCriteria(getSession()).setMaxResults(5 - countSolicEspPorCrgSeq(crgSeq));*/
		
		criteria.setResultTransformer(Transformers.aliasToBean(SubRelatorioNotasDeConsumoDaSalaExamesVO.class));
		
		return executeCriteria(criteria, 0, 5 - countSolicEspPorCrgSeq(crgSeq).intValue(), null, false);
	}
	
	private Long countSolicEspPorCrgSeq(Integer crgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcSolicitacaoEspExecCirg.class, "SEC");
		criteria.add(Restrictions.eq("SEC." + MbcSolicitacaoEspExecCirg.Fields.ID_CRG_SEQ.toString(), crgSeq));

		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaInicial() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSolicitacaoEspExecCirg.class, "SEC");
		criteria.createAlias("SEC." + MbcSolicitacaoEspExecCirg.Fields.MBC_NECESSIDADE_CIRURGICAS.toString(), "NCI");
		return criteria;
	}
	
	
	public MbcSolicitacaoEspExecCirg pesquisarMbcSolicitacaoEspNessidadeCirurgicaEUnidadeFuncional(MbcSolicitacaoEspExecCirgId id) { 
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSolicitacaoEspExecCirg.class, "SEC");
		
		criteria.createAlias("SEC." + MbcSolicitacaoEspExecCirg.Fields.MBC_NECESSIDADE_CIRURGICAS.toString(), "NCI");
		criteria.createAlias("NCI." + MbcNecessidadeCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), "UNF", Criteria.LEFT_JOIN);
		criteria.createAlias("UNF." + AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "CARACT");
		
		criteria.add(Restrictions.eq("SEC." + MbcSolicitacaoEspExecCirg.Fields.ID.toString(), id));
		
		return (MbcSolicitacaoEspExecCirg) executeCriteriaUniqueResult(criteria);	
	}

}
