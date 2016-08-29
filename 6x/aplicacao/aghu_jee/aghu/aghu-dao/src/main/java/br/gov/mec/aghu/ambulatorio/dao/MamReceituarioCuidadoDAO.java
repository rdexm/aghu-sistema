package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.ambulatorio.vo.MamReceituarioCuidadoVO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.MamItemReceitCuidado;
import br.gov.mec.aghu.model.MamEspQuestionario;
import br.gov.mec.aghu.model.MamReceituarioCuidado;


public class MamReceituarioCuidadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamReceituarioCuidado> {
	
	private static final long serialVersionUID = 510465055484661400L;

	
	/** Pesquisa Controles por Numero
	 * 
	 * @param numeroConsulta
	 * @return
	 */
	// C1
	public List<MamReceituarioCuidado> pesquisarDocumentoImpresso(Integer numeroConsulta) {
			
		   DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarioCuidado.class,"MRC");
		
			criteria.setProjection(Projections.projectionList()
					.add(Projections.property("MRC."+MamReceituarioCuidado.Fields.SEQ.toString()), MamReceituarioCuidado.Fields.SEQ.toString())
					.add(Projections.property("MRC."+MamReceituarioCuidado.Fields.PENDENTE.toString()), MamReceituarioCuidado.Fields.PENDENTE.toString())
					.add(Projections.property("MRC."+MamReceituarioCuidado.Fields.NRO_VIAS.toString()), MamReceituarioCuidado.Fields.NRO_VIAS.toString())
			);
			criteria.add(Restrictions.eq(MamReceituarioCuidado.Fields.CON_NUMERO.toString(), numeroConsulta));
			criteria.add(Restrictions.isNull(MamReceituarioCuidado.Fields.DTHR_VALIDA_MVTO.toString()));
			criteria.add(Restrictions.or(Restrictions.eq(MamReceituarioCuidado.Fields.IND_PENDENTE.toString(), DominioIndPendenteAmbulatorio.V), Restrictions.eq(MamReceituarioCuidado.Fields.IND_PENDENTE.toString(), DominioIndPendenteAmbulatorio.P)));
			criteria.setResultTransformer(Transformers.aliasToBean(MamReceituarioCuidado.class));
		
			
			
			return  executeCriteria(criteria);
	}
	
	//P1
	public List<MamReceituarioCuidadoVO> pesquisarDocumentoImpressoSituacao(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarioCuidado.class);
		String texto = "'1' texto";
			criteria.setProjection(Projections.projectionList()
					.add(Projections.property(MamReceituarioCuidado.Fields.SEQ.toString()), MamReceituarioCuidadoVO.Fields.SEQ.toString())
					.add(Projections.property(MamReceituarioCuidado.Fields.PENDENTE.toString()), MamReceituarioCuidadoVO.Fields.PENDENTE.toString())
					.add(Projections.property(MamReceituarioCuidado.Fields.NRO_VIAS.toString()), MamReceituarioCuidadoVO.Fields.NRO_VIAS.toString())
					.add(Projections.property(MamReceituarioCuidado.Fields.IMPRESSO.toString()), MamReceituarioCuidadoVO.Fields.IMPRESSO.toString())
					.add(Projections.sqlProjection(texto, new String [] {"texto"}, new Type[] {new StringType()}))
					);
			criteria.add(Restrictions.eq(MamReceituarioCuidado.Fields.CON_NUMERO.toString(), numeroConsulta));
			criteria.add(Restrictions.isNull(MamReceituarioCuidado.Fields.DTHR_VALIDA_MVTO.toString()));
			criteria.add(Restrictions.in(MamReceituarioCuidado.Fields.IND_PENDENTE.toString(), new DominioIndPendenteAmbulatorio[] {DominioIndPendenteAmbulatorio.V,DominioIndPendenteAmbulatorio.P}));
			criteria.setResultTransformer(Transformers.aliasToBean(MamReceituarioCuidadoVO.class));	
		
		return  executeCriteria(criteria);
	}
	
	//F1
	public List<MamReceituarioCuidadoVO> pesquisarRegistro(Long seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarioCuidado.class,"MRC");
		criteria.createAlias("MRC."+ MamReceituarioCuidado.Fields.SERVIDOR_VALIDA.toString(),"RAP", JoinType.INNER_JOIN);
		criteria.setProjection(Projections.projectionList()
					.add(Projections.property("MRC." + MamReceituarioCuidado.Fields.SER_MATRICULA_VALIDA.toString()), MamReceituarioCuidadoVO.Fields.SER_MATRICULA_VALIDA.toString())
					.add(Projections.property("MRC." + MamReceituarioCuidado.Fields.SER_VIN_CODIGO_VALIDA.toString()), MamReceituarioCuidadoVO.Fields.SER_VIN_CODIGO_VALIDA.toString()));
		criteria.add(Restrictions.eq(MamReceituarioCuidado.Fields.SEQ.toString(), seq));
	    criteria.setResultTransformer(Transformers.aliasToBean(MamReceituarioCuidadoVO.class));
		return  executeCriteria(criteria);
	}
	
	@Override
	public MamReceituarioCuidado obterPorChavePrimaria(Object pk) {
		MamReceituarioCuidado mamReceituarioCuidado= super.obterPorChavePrimaria(pk);
		return mamReceituarioCuidado;
	}

	@Override
	public MamReceituarioCuidado atualizar(MamReceituarioCuidado elemento) {
		MamReceituarioCuidado mamReceituarioCuidado = super.atualizar(elemento);
		return mamReceituarioCuidado;
	}
	
	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	
	public MamReceituarioCuidado buscarMamReceituarioCuidadoPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarioCuidado.class);

		criteria.add(Restrictions.eq(MamReceituarioCuidado.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamReceituarioCuidado> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	
	/**
	 * C1 e C3 #11960
	 * @param numeroConsulta
	 * @param pendente
	 * @return
	 */
	public MamReceituarioCuidado mamReceituarioCuidadoPorNumeroConsulta(Integer numeroConsulta,DominioIndPendenteAmbulatorio pendente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarioCuidado.class);

		criteria.add(Restrictions.eq(MamReceituarioCuidado.Fields.CON_NUMERO.toString(), numeroConsulta));
		criteria.add(Restrictions.eq(MamReceituarioCuidado.Fields.IND_PENDENTE.toString(), pendente));
		criteria.addOrder(Order.desc(MamReceituarioCuidado.Fields.SEQ.toString()));
		List<MamReceituarioCuidado> list = executeCriteria(criteria);
		
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null; 
	}
	
	/**
	 * #49992 P1 - CONSULTA PARA OBTER CUR_QUEST_ESP
	 * @param qutSeq
	 * @return
	 */
	public Boolean obterCurQuestEsp(Integer qutSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEspQuestionario.class);
		criteria.add(Restrictions.eq(MamEspQuestionario.Fields.QUT_SEQ.toString(), qutSeq));
		return executeCriteriaExists(criteria);
	}
	
	/**
	 * #49992 P1 - CONSULTA PARA OBTER CUR_ESP
	 * @param qutSeq
	 * @param espSeq
	 * @return
	 */
	public Boolean obterCurEsp(Integer qutSeq, Short espSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEspQuestionario.class);
		criteria.add(Restrictions.eq(MamEspQuestionario.Fields.QUT_SEQ.toString(), qutSeq));
		criteria.add(Restrictions.eq(MamEspQuestionario.Fields.ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq(MamEspQuestionario.Fields.IND_SITUACAO.toString(), "A"));
		return executeCriteriaExists(criteria);
	}
	
	/**
	 *  #11960 - consulta Auxiliar 
	 * @param numeroConsulta
	 * @param pendente
	 * @return
	 */									
	public MamReceituarioCuidado obterUltimoMamReceituarioCuidadoPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarioCuidado.class);

		criteria.add(Restrictions.eq(MamReceituarioCuidado.Fields.CON_NUMERO.toString(), numeroConsulta));
		criteria.createAlias(MamReceituarioCuidado.Fields.PACIENTE.toString(), "pac",JoinType.LEFT_OUTER_JOIN);
		
		DominioIndPendenteAmbulatorio listaIn[] = new DominioIndPendenteAmbulatorio[2];
		listaIn[0] = DominioIndPendenteAmbulatorio.R;
		listaIn[1] = DominioIndPendenteAmbulatorio.P;
		criteria.add(Restrictions.in(MamReceituarioCuidado.Fields.IND_PENDENTE.toString(),listaIn ));
		
		
		criteria.addOrder(Order.desc(MamReceituarioCuidado.Fields.SEQ.toString()));
		
		List<MamReceituarioCuidado> lista = executeCriteria(criteria);
		if(lista!=null && !lista.isEmpty()){
			return lista.get(0);
		}
		return null;
		
	}
	
	
	/**
	 * C2 #11960
	 * @param numeroConsulta
	 * @param pendente
	 * @return
	 */									
	public MamReceituarioCuidado obterUltimoMamReceituarioCuidadoPorNumeroConsultaSemPendente(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarioCuidado.class);

		criteria.add(Restrictions.eq(MamReceituarioCuidado.Fields.CON_NUMERO.toString(), numeroConsulta));
		criteria.createAlias(MamReceituarioCuidado.Fields.PACIENTE.toString(), "pac",JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.ne(MamReceituarioCuidado.Fields.IND_PENDENTE.toString(),DominioIndPendenteAmbulatorio.R));
		criteria.add(Restrictions.ne(MamReceituarioCuidado.Fields.IND_PENDENTE.toString(),DominioIndPendenteAmbulatorio.P));
		
		criteria.addOrder(Order.desc(MamReceituarioCuidado.Fields.SEQ.toString()));
		
		List<MamReceituarioCuidado> lista = executeCriteria(criteria);
		if(lista!=null && !lista.isEmpty()){
			return lista.get(0);
		}
		return null;
		
	}

	/**
	 * C4 #11960
	 * @param numeroConsulta
	 * @return
	 */
	public List<MamItemReceitCuidado> listarMamItensReceituarioCuidadoPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarioCuidado.class);
		criteria.createAlias(MamReceituarioCuidado.Fields.ITEM_RECEUTARIO_CUIDADOS.toString(), "IRC");

		criteria.add(Restrictions.eq(MamReceituarioCuidado.Fields.CON_NUMERO.toString(), numeroConsulta));
		DominioIndPendenteAmbulatorio listaIn[] = new DominioIndPendenteAmbulatorio[2];
		listaIn[0] = DominioIndPendenteAmbulatorio.R;
		listaIn[1] = DominioIndPendenteAmbulatorio.P;
		criteria.add(Restrictions.in(MamReceituarioCuidado.Fields.IND_PENDENTE.toString(),listaIn ));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("IRC."+MamItemReceitCuidado.Fields.DESCRICAO.toString()),MamItemReceitCuidado.Fields.DESCRICAO.toString())
				.add(Projections.property("IRC."+MamItemReceitCuidado.Fields.ID.toString()),MamItemReceitCuidado.Fields.ID.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(MamItemReceitCuidado.class));
		
		return  executeCriteria(criteria);

	}
	
	
	/**
	 * listar Receituarios por consulta
	 * @param numeroConsulta
	 * @return
	 */
	public List<Long> listarMamReceituarioCuidadoPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarioCuidado.class);
		criteria.createAlias(MamReceituarioCuidado.Fields.ITEM_RECEUTARIO_CUIDADOS.toString(), "IRC");

		criteria.add(Restrictions.eq(MamReceituarioCuidado.Fields.CON_NUMERO.toString(), numeroConsulta));
		DominioIndPendenteAmbulatorio listaIn[] = new DominioIndPendenteAmbulatorio[2];
		listaIn[0] = DominioIndPendenteAmbulatorio.R;
		listaIn[1] = DominioIndPendenteAmbulatorio.P;
		criteria.add(Restrictions.in(MamReceituarioCuidado.Fields.IND_PENDENTE.toString(),listaIn ));
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(MamReceituarioCuidado.Fields.SEQ.toString()),MamReceituarioCuidado.Fields.SEQ.toString())));
		
		return  executeCriteria(criteria);
		
	}
			
}


