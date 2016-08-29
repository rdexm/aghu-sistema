package br.gov.mec.aghu.bancosangue.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPaciente;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsGrupoJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;

public class AbsJustificativaComponenteSanguineoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsJustificativaComponenteSanguineo> {


		
	private static final long serialVersionUID = 5676038989307650765L;



	/**
	 * Metodo para consultar da justificativas padrão, conforme código do componente sanguineo, código do procedimento hemoterapico e o código do grupo de justificativa
	 * @param procedHemoterapicoCodigo
	 * @param componenteSanguineoCodigo
	 * @param seqGrupoJustificativaComponenteSanguineo
	 * @return
	 */
	public List<AbsJustificativaComponenteSanguineo> listarJustificativasPadraoDoComponenteOuProcedimento(String procedHemoterapicoCodigo, String componenteSanguineoCodigo, 
															Short seqGrupoJustificativaComponenteSanguineo, Boolean indPacPediatrico){
		boolean executarCrit = false;
		List<AbsJustificativaComponenteSanguineo> lista = null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsJustificativaComponenteSanguineo.class);
		
		criteria.createAlias(AbsJustificativaComponenteSanguineo.Fields.GRUPO_JUSTIFICATIVA.toString(), AbsJustificativaComponenteSanguineo.Fields.GRUPO_JUSTIFICATIVA.toString(),JoinType.INNER_JOIN);

		if(StringUtils.isNotBlank(procedHemoterapicoCodigo)){
			executarCrit = true;
			criteria.add(Restrictions
				.ilike(AbsJustificativaComponenteSanguineo.Fields.PROCEDIMENTO_HEMOTERAPICO.toString() +"."+ AbsProcedHemoterapico.Fields.CODIGO.toString(), procedHemoterapicoCodigo, MatchMode.EXACT));
		}else if(StringUtils.isNotBlank(componenteSanguineoCodigo)){
			executarCrit = true;
			criteria.add(Restrictions
					.ilike(AbsJustificativaComponenteSanguineo.Fields.COMPONENTE_SANGUINEO.toString() +"."+ AbsComponenteSanguineo.Fields.CODIGO.toString(), componenteSanguineoCodigo, MatchMode.EXACT));
		}
		
		if(executarCrit && indPacPediatrico != null){//Se os dois primeiros argumentos forem passados nulos, não executa a consulta.
			criteria.add(Restrictions.eq(AbsJustificativaComponenteSanguineo.Fields.GRUPO_JUSTIFICATIVA.toString() +"."+ AbsGrupoJustificativaComponenteSanguineo.Fields.SEQ.toString(), seqGrupoJustificativaComponenteSanguineo));
			criteria.add(Restrictions.eq(AbsJustificativaComponenteSanguineo.Fields.SITUACAO.toString(), DominioSituacao.A));

			DominioTipoPaciente arrayTipoPaciente [] = new DominioTipoPaciente[2];
			if(indPacPediatrico){
				arrayTipoPaciente[0] = DominioTipoPaciente.P;
			}else{
				arrayTipoPaciente[0] = DominioTipoPaciente.A;
			}
			arrayTipoPaciente[1] = DominioTipoPaciente.B;  
			criteria.add(Restrictions
					.in(AbsJustificativaComponenteSanguineo.Fields.TIPO_PACIENTE.toString(), arrayTipoPaciente ));
			
			criteria.addOrder(Order.asc(AbsJustificativaComponenteSanguineo.Fields.DESCRICAO.toString()));
			
			lista = executeCriteria(criteria);
		}
		
		return lista;
	}
	
	public List<AbsJustificativaComponenteSanguineo> pesquisarJustificativaComponenteSanguineosPorGrupoJustificativaCompomenteSanguineo(Short seqGrupoJustificativaComponenteSanguineo) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "JCS");
		dc.createAlias("JCS.".concat(AbsJustificativaComponenteSanguineo.Fields.GRUPO_JUSTIFICATIVA.toString()), "GJCS", JoinType.INNER_JOIN);
		dc.createAlias("JCS.".concat(AbsJustificativaComponenteSanguineo.Fields.COMPONENTE_SANGUINEO.toString()), "CS", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias("JCS.".concat(AbsJustificativaComponenteSanguineo.Fields.PROCEDIMENTO_HEMOTERAPICO.toString()), "PH", JoinType.LEFT_OUTER_JOIN);
		dc.add(Restrictions.eq("GJCS.".concat(AbsGrupoJustificativaComponenteSanguineo.Fields.SEQ.toString()), seqGrupoJustificativaComponenteSanguineo));
		return executeCriteria(dc);
	}
	
	public List<AbsJustificativaComponenteSanguineo> pesquisarJustificativaComponenteSanguineosPorCodigoProcedimentoHemoterapico(String pheCodigo) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "JCS");
		dc.createAlias("JCS.".concat(AbsJustificativaComponenteSanguineo.Fields.PROCEDIMENTO_HEMOTERAPICO.toString()), "APH", JoinType.INNER_JOIN);
		dc.add(Restrictions.ilike("APH.".concat(AbsProcedHemoterapico.Fields.CODIGO.toString()), pheCodigo, MatchMode.EXACT));
		return executeCriteria(dc);
	}
	
	
	/**
	 * Pesquisa utilizada pela estoria #6399
	 * 
	 * @param seq
	 * @param codigoComponenteSanguineo
	 * @param codigoProcedimentoHemoterapico
	 * @param seqGrupoJustificativaComponenteSanguineo
	 * @param tipoPaciente
	 * @param descricaoLivre
	 * @param situacao
	 * @param descricao
	 * @param firstResult
	 * @param maxResult
	 * @return
	 */
	public List<AbsJustificativaComponenteSanguineo> pesquisarJustificativaUsoHemoterapico(Integer seq,
																				           String codigoComponenteSanguineo,
																						   String codigoProcedimentoHemoterapico,
																						   Short seqGrupoJustificativaComponenteSanguineo,
																						   DominioTipoPaciente tipoPaciente,
																						   DominioSimNao descricaoLivre,
																						   DominioSituacao situacao,
																						   String descricao,
																						   int firstResult, 
																						   int maxResult) {
		
		DetachedCriteria criteria = montarCriteriaPesquisarJustificativaUsoHemoterapico(seq,
																				        codigoComponenteSanguineo,
																						codigoProcedimentoHemoterapico,
																						seqGrupoJustificativaComponenteSanguineo,
																						tipoPaciente,
																						descricaoLivre,
																						situacao,
																						descricao);
		criteria.createAlias(AbsJustificativaComponenteSanguineo.Fields.GRUPO_JUSTIFICATIVA.toString(), "GJ", JoinType.INNER_JOIN);
		
		criteria.addOrder(Order.asc(AbsJustificativaComponenteSanguineo.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc(AbsJustificativaComponenteSanguineo.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, null, true);
	}
	
	
	
	/**
	 * Pesquisa utilizada pela estoria #6399
	 * 
	 * @param seq
	 * @param codigoComponenteSanguineo
	 * @param codigoProcedimentoHemoterapico
	 * @param seqGrupoJustificativaComponenteSanguineo
	 * @param tipoPaciente
	 * @param descricaoLivre
	 * @param situacao
	 * @param descricao
	 * @return
	 */
	public Long pesquisarJustificativaUsoHemoterapicoCount(Integer seq,
														      String codigoComponenteSanguineo,
												   			  String codigoProcedimentoHemoterapico,
												   			  Short seqGrupoJustificativaComponenteSanguineo,
												   			  DominioTipoPaciente tipoPaciente,
												   			  DominioSimNao descricaoLivre,
												   			  DominioSituacao situacao,
												   			  String descricao) {
				
		DetachedCriteria criteria = montarCriteriaPesquisarJustificativaUsoHemoterapico(seq,
																					    codigoComponenteSanguineo,
																						codigoProcedimentoHemoterapico,
																						seqGrupoJustificativaComponenteSanguineo,
																						tipoPaciente,
																						descricaoLivre,
																						situacao,
																						descricao);
		return executeCriteriaCount(criteria);
	}
	
	
	
	/**
	 * Estoria 6399
	 * @param seq
	 * @param codigoComponenteSanguineo
	 * @param codigoProcedimentoHemoterapico
	 * @param seqGrupoJustificativaComponenteSanguineo
	 * @param tipoPaciente
	 * @param descricaoLivre
	 * @param situacao
	 * @param descricao
	 * @return
	 */
	private DetachedCriteria montarCriteriaPesquisarJustificativaUsoHemoterapico(Integer seq,
																			     String codigoComponenteSanguineo,
																			     String codigoProcedimentoHemoterapico,
																			     Short seqGrupoJustificativaComponenteSanguineo,
																			     DominioTipoPaciente tipoPaciente,
																			     DominioSimNao descricaoLivre,
																			     DominioSituacao situacao,
																			     String descricao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsJustificativaComponenteSanguineo.class, "JCS");
		
		if(seq != null && seq != 0) {
			criteria.add(Restrictions.eq("JCS.".concat(AbsJustificativaComponenteSanguineo.Fields.SEQ.toString()), seq));
		}
		if(StringUtils.isNotBlank(codigoComponenteSanguineo)) {
			criteria.add(Restrictions.eq("JCS.".concat(AbsJustificativaComponenteSanguineo.Fields.COMPONENTE_SANGUINEO.toString()) + "." +
													   AbsComponenteSanguineo.Fields.CODIGO.toString(), codigoComponenteSanguineo));
		}
		if(StringUtils.isNotBlank(codigoProcedimentoHemoterapico)) {
			criteria.add(Restrictions.eq("JCS.".concat(AbsJustificativaComponenteSanguineo.Fields.PROCEDIMENTO_HEMOTERAPICO.toString()) + "." + 
													   AbsProcedHemoterapico.Fields.CODIGO.toString(), codigoProcedimentoHemoterapico));
		}
		if(seqGrupoJustificativaComponenteSanguineo != null) {
			criteria.add(Restrictions.eq("JCS.".concat(AbsJustificativaComponenteSanguineo.Fields.GRUPO_JUSTIFICATIVA.toString()) + "." + 
													   AbsGrupoJustificativaComponenteSanguineo.Fields.SEQ.toString(), seqGrupoJustificativaComponenteSanguineo));
		}
		if(tipoPaciente != null) {
			criteria.add(Restrictions.eq("JCS.".concat(AbsJustificativaComponenteSanguineo.Fields.TIPO_PACIENTE.toString()), tipoPaciente));
		}
		if(descricaoLivre != null) {
			if(DominioSimNao.S.equals(descricaoLivre)) {
				criteria.add(Restrictions.eq("JCS.".concat(AbsJustificativaComponenteSanguineo.Fields.DESCRICAO_LIVRE.toString()), Boolean.TRUE));
			} else {
				criteria.add(Restrictions.eq("JCS.".concat(AbsJustificativaComponenteSanguineo.Fields.DESCRICAO_LIVRE.toString()), Boolean.FALSE));
			}
		}
		if(situacao != null) {
			criteria.add(Restrictions.eq("JCS.".concat(AbsJustificativaComponenteSanguineo.Fields.SITUACAO.toString()), situacao));
		}
		if(StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike("JCS.".concat(AbsJustificativaComponenteSanguineo.Fields.DESCRICAO.toString()), descricao, MatchMode.ANYWHERE));
		}
		return criteria;
	}
	
}
