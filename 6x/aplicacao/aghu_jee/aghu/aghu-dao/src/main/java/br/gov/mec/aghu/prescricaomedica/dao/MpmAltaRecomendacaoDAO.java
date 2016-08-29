package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAltaPrincReceitas;
import br.gov.mec.aghu.model.MpmAltaRecomendacao;
import br.gov.mec.aghu.model.MpmAltaRecomendacaoId;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmServRecomendacaoAlta;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmAltaRecomendacaoVO;

/**
 * 
 * @author bsoliveira
 *
 */
public class MpmAltaRecomendacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaRecomendacao> {

	private static final long serialVersionUID = -4214164695621273874L;

	@Override
	protected void obterValorSequencialId(MpmAltaRecomendacao elemento) {
		
		if (elemento == null || elemento.getAltaSumario() == null || elemento.getAltaSumario().getId() == null) {
			
			throw new IllegalArgumentException("Alta Sumário nao foi informado corretamente.");
		
		}
		
		MpmAltaRecomendacaoId id = new MpmAltaRecomendacaoId();

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaRecomendacao.class);
		criteria.add(Restrictions.eq(MpmAltaRecomendacao.Fields.ASU_APA_ATD_SEQ.toString(), elemento.getAltaSumario().getId().getApaAtdSeq()));
		criteria.add(Restrictions.eq(MpmAltaRecomendacao.Fields.ASU_APA_SEQ.toString(), elemento.getAltaSumario().getId().getApaSeq()));
		criteria.add(Restrictions.eq(MpmAltaRecomendacao.Fields.ASU_SEQP.toString(), elemento.getAltaSumario().getId().getSeqp()));
		criteria.setProjection(Projections.max(MpmAltaRecomendacao.Fields.SEQP.toString()));

		Short seqp = (Short) this.executeCriteriaUniqueResult(criteria);
		seqp = seqp == null ? 0 : seqp;
		id.setAsuApaAtdSeq(elemento.getAltaSumario().getId().getApaAtdSeq());
		id.setAsuApaSeq(elemento.getAltaSumario().getId().getApaSeq());
		id.setAsuSeqp(elemento.getAltaSumario().getId().getSeqp());
		id.setSeqp(++seqp);
	
		elemento.setId(id);
		
	}
	
	/**
	 * Busca um VO com as informacoes que estao atualmente salva na base de dados
	 * para o Objeto informado no parametro.<br>
	 * 
	 * <code>MpmAltaRecomendacao</code>
	 * 
	 * @param altaRecomendacao
	 * @return a MpmAltaRecomendacao
	 */
	public MpmAltaRecomendacaoVO obterAltaRecomendacaoOriginal(MpmAltaRecomendacao altaRecomendacao) {
		if (altaRecomendacao == null || altaRecomendacao.getId() == null 
				|| altaRecomendacao.getId().getAsuApaAtdSeq() == null 
				|| altaRecomendacao.getId().getAsuApaSeq() == null
				|| altaRecomendacao.getId().getAsuSeqp() == null
				|| altaRecomendacao.getId().getSeqp() == null) {
			throw new IllegalArgumentException("Parametro Invalido!!");
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaRecomendacao.class);
		criteria.createAlias(MpmAltaRecomendacao.Fields.SERVIDOR_RECOMENDACAO.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER."+MpmServRecomendacaoAlta.Fields.SERVIDOR.toString(), "RAP", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MpmAltaRecomendacao.Fields.ASU_APA_ATD_SEQ.toString(), altaRecomendacao.getId().getAsuApaAtdSeq()));
		criteria.add(Restrictions.eq(MpmAltaRecomendacao.Fields.ASU_APA_SEQ.toString(), altaRecomendacao.getId().getAsuApaSeq()));
		criteria.add(Restrictions.eq(MpmAltaRecomendacao.Fields.ASU_SEQP.toString(), altaRecomendacao.getId().getAsuSeqp()));
		criteria.add(Restrictions.eq(MpmAltaRecomendacao.Fields.SEQP.toString(), altaRecomendacao.getId().getSeqp()));

		List<MpmAltaRecomendacao> listaRetorno = executeCriteria(criteria);
		MpmAltaRecomendacaoVO vo = null;
		
		if (listaRetorno != null && !listaRetorno.isEmpty()) {
			// Pelo criterio de Pesquisa deve ter apenas um elemento na lista.
			vo = new MpmAltaRecomendacaoVO();
			
			for (MpmAltaRecomendacao recomendacao : listaRetorno) {
				vo.setDescricaoSistema(recomendacao.getDescricaoSistema());
				vo.setId(recomendacao.getId());
				vo.setPcuAtdSeq(recomendacao.getPcuAtdSeq());
				vo.setPcuSeq(recomendacao.getPcuSeq());
				vo.setPdtAtdSeq(recomendacao.getPdtAtdSeq());
				vo.setPdtSeq(recomendacao.getPdtSeq());
				vo.setPmdAtdSeq(recomendacao.getPmdAtdSeq());
				vo.setPmdSeq(recomendacao.getPmdSeq());
				vo.setServidorRecomendacaoAlta(recomendacao.getServidorRecomendacaoAlta());	
			}
		}
		
		return vo;
	}
	
	/**
	 * Busca alta recomendação do sumário ativo
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public List<MpmAltaRecomendacao> obterMpmAltaRecomendacao(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp, DominioSituacao situacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaRecomendacao.class);
		criteria.add(Restrictions.eq(MpmAltaRecomendacao.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaRecomendacao.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaRecomendacao.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		if (situacao != null) {
			criteria.add(Restrictions.eq(MpmAltaRecomendacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		}
		return executeCriteria(criteria);
	}
	
	/**
	 * Busca alta recomendação do sumário ativo
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public List<MpmAltaRecomendacao> listarMpmAltaRecomendacao(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaRecomendacao.class);
		criteria.add(Restrictions.eq(MpmAltaRecomendacao.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaRecomendacao.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaRecomendacao.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		criteria.add(Restrictions.eq(MpmAltaRecomendacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.isNull(MpmAltaRecomendacao.Fields.DESCRICAO_SISTEMA.toString()));
		return executeCriteria(criteria);
	}
	
	
	public List<LinhaReportVO> obterRelatorioSumario(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		String hql="select new br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO(" +
			MpmAltaRecomendacao.Fields.DESCRICAO.toString() +
		   ") from MpmAltaRecomendacao " + 
		   " where "+ MpmAltaRecomendacao.Fields.ASU_APA_ATD_SEQ.toString() + "=:asuApaAtdSeq " +
		   " and " + MpmAltaRecomendacao.Fields.ASU_APA_SEQ.toString() + "=:asuApaSeq " +
		   " and " + MpmAltaRecomendacao.Fields.ASU_SEQP.toString() + "=:asuSeqp "+
		   " and " + MpmAltaRecomendacao.Fields.IND_SITUACAO.toString() + "=:dominioSituacao " +
		   " order by " + MpmAltaRecomendacao.Fields.DESCRICAO.toString();
		Query query = createQuery(hql);
		query.setParameter("asuApaAtdSeq", asuApaAtdSeq);
		query.setParameter("asuApaSeq", asuApaSeq);
		query.setParameter("asuSeqp", asuSeqp);
		query.setParameter("dominioSituacao", DominioSituacao.A);
		return query.getResultList();		
	}  	
	
	public List<LinhaReportVO> buscaMedicamentosAlta(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		String hql="select new br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO(" 
				+ MpmAltaPrincReceitas.Fields.DESC_RECEITA.toString() + ") "
				+ " from " + MpmAltaPrincReceitas.class.getSimpleName() + " "  
				+ " where "
				+ MpmAltaPrincReceitas.Fields.ASU_APA_ATD_SEQ.toString() + "=:asuApaAtdSeq " 
				+ " and " + MpmAltaPrincReceitas.Fields.ASU_APA_SEQ.toString() + "=:asuApaSeq " 
				+ " and " + MpmAltaPrincReceitas.Fields.ASU_SEQP.toString() + "=:asuSeqp "
				+ " and " + MpmAltaPrincReceitas.Fields.IND_SITUACAO.toString() + "= 'A' " 
				+ " and " + MpmAltaPrincReceitas.Fields.IND_CARGA.toString() + "= 'S' ";
			Query query = createQuery(hql);
			query.setParameter("asuApaAtdSeq", asuApaAtdSeq);
			query.setParameter("asuApaSeq", asuApaSeq);
			query.setParameter("asuSeqp", asuSeqp);
			return query.getResultList();		
	}
	
	

	/**
	 * Busca os objetos MpmAltaRecomendacao associados ao MpmAltaSumario que sejam de Dieta, Cuidado e Medicamento. 
	 * Utilizado em: Itens Recomendados no Plano PosAlta a partir da Ultima Prescricao.
	 * 
	 * @param altaSumario
	 * @return
	 */
	public List<MpmAltaRecomendacao> buscaItensAltaRecomendacaoPrescricaoMedica(MpmAltaSumario altaSumario) {
		if (altaSumario == null || altaSumario.getId() == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}
		
		StringBuilder hql = new StringBuilder(200);
		
		hql.append("select ar \n");
		hql.append("from ").append(MpmAltaRecomendacao.class.getSimpleName()).append(" ar \n");
		hql.append("where ar.").append(MpmAltaRecomendacao.Fields.IND_SITUACAO.toString()).append(" = :indSituacao ");
		hql.append("and ar.").append(MpmAltaRecomendacao.Fields.ALTA_SUMARIO_ID.toString()).append(" = :idAltaSumario ");
		hql.append("and ( \n");
		hql.append("ar.").append(MpmAltaRecomendacao.Fields.PDT_ATD_SEQ.toString()).append(" is not null \n");
		hql.append("or ar.").append(MpmAltaRecomendacao.Fields.PCU_ATD_SEQ.toString()).append(" is not null \n");
		hql.append("or ar.").append(MpmAltaRecomendacao.Fields.PMD_ATD_SEQ.toString()).append(" is not null \n");
		hql.append(") \n");
		hql.append(" order by ar.").append(MpmAltaRecomendacao.Fields.DESCRICAO.toString()).append(" \n");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("idAltaSumario", altaSumario.getId());
		query.setParameter("indSituacao", DominioSituacao.A);
		
		return query.getResultList();
	}

}
