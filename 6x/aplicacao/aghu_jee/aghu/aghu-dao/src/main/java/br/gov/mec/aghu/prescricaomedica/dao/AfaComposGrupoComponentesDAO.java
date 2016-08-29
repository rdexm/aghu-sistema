package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaCompoGrupoComponente;
import br.gov.mec.aghu.model.AfaGrupoComponenteNpt;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultaCompoGrupoComponenteVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * @author thiago.cortes
 *
 */
public class AfaComposGrupoComponentesDAO  extends BaseDao<AfaCompoGrupoComponente> {

	/**#3506 C2
	 * Lista de grupos vinculados ao tipo de composição selecionada
	 * @param pesquisa
	 * @return 
	 */
	public List<ConsultaCompoGrupoComponenteVO> pesquisarGrupoComponente(Short seq){
		String aliasCgc = "CGC";
		String ponto = ".";
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaCompoGrupoComponente.class,aliasCgc);
		criteria.createAlias(AfaCompoGrupoComponente.Fields.AFA_GRUPO_COMPONENTE_NPTS.toString(),"AGC");

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(aliasCgc+ponto+AfaCompoGrupoComponente.Fields.GCN_SEQ.toString()),
						ConsultaCompoGrupoComponenteVO.Fields.SEQ.toString())
				.add(Projections.property(aliasCgc+ponto+AfaCompoGrupoComponente.Fields.TIC_SEQ.toString()),
						ConsultaCompoGrupoComponenteVO.Fields.TIC_SEQ.toString())
				.add(Projections.property(aliasCgc+ponto+AfaCompoGrupoComponente.Fields.IND_SITUACAO.toString()),
						ConsultaCompoGrupoComponenteVO.Fields.IND_SITUACAO.toString())
				.add(Projections.property(aliasCgc+ponto+AfaCompoGrupoComponente.Fields.CRIADO_EM.toString()),
						ConsultaCompoGrupoComponenteVO.Fields.CRIADO_EM.toString())
				.add(Projections.property("AGC."+AfaGrupoComponenteNpt.Fields.DESCRICAO.toString()),
						ConsultaCompoGrupoComponenteVO.Fields.DESCRICAO.toString()));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AfaGrupoComponenteNpt.class, "GCN");
		subCriteria.setProjection(Projections.property("GCN."+AfaGrupoComponenteNpt.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(aliasCgc+ponto+AfaCompoGrupoComponente.Fields.GCN_SEQ.toString(), 
				"GCN."+AfaGrupoComponenteNpt.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.eq("GCN."+AfaGrupoComponenteNpt.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Subqueries.exists(subCriteria));
		
		if(seq != null){
			criteria.add(Restrictions.eq(AfaCompoGrupoComponente.Fields.TIC_SEQ.toString(), seq));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(ConsultaCompoGrupoComponenteVO.class));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * #3506 C6
	 * @param seq
	 * @return Verificar Vinculo com Grupo de componente
	 */
	public Long pesquisaGrupoComponente(Short seq){
		String aliasCgc = "CGC";
		String ponto = ".";
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaCompoGrupoComponente.class,aliasCgc);
		criteria.createAlias(aliasCgc+ponto+AfaCompoGrupoComponente.Fields.AFA_GRUPO_COMPONENTE_NPTS.toString(), "AGC");
		
		criteria.add(Restrictions.eq(aliasCgc+ponto+AfaCompoGrupoComponente.Fields.IND_SITUACAO.toString(),DominioSituacao.A));
		if(seq != null){
			criteria.add(Restrictions.eq(aliasCgc+ponto+AfaCompoGrupoComponente.Fields.AFA_TIPO_COMPOSICOES_SEQ.toString(),seq));
		}
		return executeCriteriaCount(criteria);
	}
	
	/**#3506
	 * Informações do usuário que criou o registro.
	 * @param seq
	 * @return
	 */
	public String pesquisaInfoCriacaoGrupoComponente(Short gcnSeq, Short ticSeq){
		String aliasCgc = "CGC";
		String ponto = ".";
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaCompoGrupoComponente.class,aliasCgc);
		criteria.createAlias(aliasCgc+ponto+AfaCompoGrupoComponente.Fields.RAP_SERVIDORES_BY_AFA_TCG_SER_FK1.toString(), "RSE");
		criteria.createAlias("RSE."+RapServidores.Fields.PESSOA_FISICA.toString(), "PEF");
		criteria.setProjection(Projections.distinct(Projections.property("PEF."+RapPessoasFisicas.Fields.NOME.toString())));
		if(ticSeq != null){
			criteria.add(Restrictions.eq(aliasCgc+ponto+AfaCompoGrupoComponente.Fields.AFA_TIPO_COMPOSICOES_SEQ.toString(), ticSeq));
		}
		if(gcnSeq != null){
			criteria.add(Restrictions.eq(aliasCgc+ponto+AfaCompoGrupoComponente.Fields.AFA_GRUPO_COMPONENTE_NPTS_SEQ.toString(), gcnSeq));
		}
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
}
