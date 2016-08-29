package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.exames.vo.AelGrpTecnicaUnfExamesVO;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelGrpTecnicaUnfExames;
import br.gov.mec.aghu.model.AelGrpTecnicaUnfExamesId;
import br.gov.mec.aghu.model.AelGrupoExameTecnicas;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

public class AelGrpTecnicaUnfExamesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrpTecnicaUnfExames>{

	
	
	private static final long serialVersionUID = 162663422113934317L;



	@Override
	protected void obterValorSequencialId(AelGrpTecnicaUnfExames elemento) {
		if (elemento == null 
				|| elemento.getAelGrupoExameTecnicas() == null 
					&& elemento.getAelUnfExecutaExames() == null) {
			throw new IllegalArgumentException(
					"Parametro obrigatorio nao informado!!!");
		}
		
		AelGrpTecnicaUnfExamesId id = new AelGrpTecnicaUnfExamesId();
		id.setGrtSeq(elemento.getAelGrupoExameTecnicas().getSeq());
		id.setUfeEmaExaSigla(elemento.getAelUnfExecutaExames().getId().getEmaExaSigla());
		id.setUfeEmaManSeq(elemento.getAelUnfExecutaExames().getId().getEmaManSeq());
		id.setUfeUnfSeq(elemento.getAelUnfExecutaExames().getId().getUnfSeq().getSeq());
		
		elemento.setId(id);
	}
	
	
	
	/**
	 * Obtem uma criteria padrao/default
	 * @return
	 */
	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrpTecnicaUnfExames.class);
		return criteria;
    }
	
	
	/**
	 * Busca AelGrpTecnicaUnfExames por id
	 * @param id
	 * @return
	 */
	public AelGrpTecnicaUnfExames buscarAelGrpTecnicaUnfExamesPorId( AelGrpTecnicaUnfExamesId id) {
		return this.buscarAelGrpTecnicaUnfExamesPorId(id.getGrtSeq(), id.getUfeEmaExaSigla(), id.getUfeEmaManSeq(), id.getUfeUnfSeq());
	}
	
	/**
	 * Busca AelGrpTecnicaUnfExames por id
	 * @param grtSeq
	 * @param ufeEmaExaSigla
	 * @param ufeEmaManSeq
	 * @param ufeUnfSeq
	 * @return
	 */
	public AelGrpTecnicaUnfExames buscarAelGrpTecnicaUnfExamesPorId(Integer grtSeq, String ufeEmaExaSigla, Integer ufeEmaManSeq, Short ufeUnfSeq) {
		
		DetachedCriteria dc = obterCriteria();
		dc.createCriteria(AelGrpTecnicaUnfExames.Fields.EXAMES.toString(),Criteria.LEFT_JOIN);
		dc = this.obterRestricaoBuscarAelGrpTecnicaUnfExamesPorId(dc, grtSeq, ufeEmaExaSigla, ufeEmaManSeq, ufeUnfSeq);
		
		return (AelGrpTecnicaUnfExames) executeCriteriaUniqueResult(dc);
	}
	
	private DetachedCriteria obterRestricaoBuscarAelGrpTecnicaUnfExamesPorId(DetachedCriteria dc, Integer grtSeq, String ufeEmaExaSigla, Integer ufeEmaManSeq, Short ufeUnfSeq) {
		dc.add(Restrictions.eq(AelGrpTecnicaUnfExames.Fields.GRT_SEQ.toString(), grtSeq));
		dc.add(Restrictions.eq(AelGrpTecnicaUnfExames.Fields.UFE_EMA_EXA_SIGLA.toString(), ufeEmaExaSigla));
		dc.add(Restrictions.eq(AelGrpTecnicaUnfExames.Fields.UFE_EMA_MAN_SEQ.toString(), ufeEmaManSeq));
		dc.add(Restrictions.eq(AelGrpTecnicaUnfExames.Fields.UFE_UFE_UNF_SEQ.toString(), ufeUnfSeq));
		
		return dc;
	}

	
	/**
	 * Busca AelGrpTecnicaUnfExames por AelGrupoExameTecnica
	 * @param grupoExameTecnicas
	 * @return
	 */
	public List<AelGrpTecnicaUnfExames> buscarAelGrpTecnicaUnfExamesPorAelGrupoExameTecnica(AelGrupoExameTecnicas grupoExameTecnicas) {
		DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AelGrpTecnicaUnfExames.Fields.GRT_SEQ.toString(), grupoExameTecnicas.getSeq()));
		return executeCriteria(dc);
	}
	
	/**
	 * Verifica a existencia de registros em AelGrpTecnicaUnfExames por AelGrupoExameTecnica
	 * @param grupoExameTecnicas
	 * @return
	 */
	public boolean existeAelGrpTecnicaUnfExamesPorAelGrupoExameTecnica(AelGrupoExameTecnicas grupoExameTecnicas) {
		DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AelGrpTecnicaUnfExames.Fields.GRT_SEQ.toString(), grupoExameTecnicas.getSeq()));
		return executeCriteriaCount(dc) > 0;
	}
	
	
	
	/**
	 * Retorna uma lista de AelGrpTecnicaUnfExamesVO <br>
	 * conforme filtros.
	 * 
	 * @param grtSeq
	 * @param ufeEmaExaSigla
	 * @param ufeEmaManSeq
	 * @param ufeUnfSeq
	 * @return
	 */
	public AelGrpTecnicaUnfExamesVO obterAelGrpTecnicaUnfExamesVO(Integer grtSeq, String ufeEmaExaSigla, Integer ufeEmaManSeq, Short ufeUnfSeq) {
		DetachedCriteria criteria = obterCriteria();
		
		criteria.createCriteria(AelGrpTecnicaUnfExames.Fields.AEL_GRUPO_EXAME_TECNICAS.toString(), "GRT");
		criteria.createCriteria(AelGrpTecnicaUnfExames.Fields.EXAMES.toString(), "EXA");
		criteria.createCriteria(AelGrpTecnicaUnfExames.Fields.MATERIAL_ANALISE.toString(), "MAT");
		criteria.createCriteria(AelGrpTecnicaUnfExames.Fields.UNF_EXECUTA_EXAMES.toString(), "UEE");
		criteria.createCriteria("UEE.".concat(AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL_OBJ.toString()), 
				"UNF", Criteria.LEFT_JOIN);
		
		criteria = this.obterRestricaoBuscarAelGrpTecnicaUnfExamesPorId(
				criteria, grtSeq, ufeEmaExaSigla, ufeEmaManSeq, ufeUnfSeq);
		
		ProjectionList projection = Projections.projectionList();
		
		projection.add(Property.forName(AelGrpTecnicaUnfExames.Fields.GRT_SEQ.toString()), 
				AelGrpTecnicaUnfExamesVO.Fields.GRT_SEQ.toString());
		projection.add(Property.forName("GRT.".concat(AelGrupoExameTecnicas.Fields.DESCRICAO.toString())), 
				AelGrpTecnicaUnfExamesVO.Fields.DESCRICAO_GRUPO_EXAME_TECNICA.toString());
		projection.add(Property.forName(AelGrpTecnicaUnfExames.Fields.UFE_EMA_MAN_SEQ.toString()), 
				AelGrpTecnicaUnfExamesVO.Fields.UFE_EMA_MAN_SEQ.toString());
		projection.add(Property.forName(AelGrpTecnicaUnfExames.Fields.UFE_EMA_EXA_SIGLA.toString()), 
				AelGrpTecnicaUnfExamesVO.Fields.UFE_EMA_EXA_SIGLA.toString());
		projection.add(Property.forName("EXA.".concat(AelExames.Fields.DESCRICAO.toString())), 
				AelGrpTecnicaUnfExamesVO.Fields.DESCRICAO_EXAME.toString());
		projection.add(Property.forName("MAT.".concat(AelMateriaisAnalises.Fields.DESCRICAO.toString())), 
				AelGrpTecnicaUnfExamesVO.Fields.DESCRICAO_MAT_ANALISE.toString());
		projection.add(Property.forName(AelGrpTecnicaUnfExames.Fields.UFE_UFE_UNF_SEQ.toString()), 
				AelGrpTecnicaUnfExamesVO.Fields.UFE_UNF_SEQ.toString());
		projection.add(Property.forName("UNF.".concat(AghUnidadesFuncionais.Fields.DESCRICAO.toString())), 
				AelGrpTecnicaUnfExamesVO.Fields.DESCRICAO_UNIDADE_EXECUTORA.toString());
						
		criteria.setProjection(projection);	

		criteria.setResultTransformer(Transformers.aliasToBean(AelGrpTecnicaUnfExamesVO.class));
		
		List<AelGrpTecnicaUnfExamesVO> result = this.executeCriteria(criteria);
		
		if(result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}

}