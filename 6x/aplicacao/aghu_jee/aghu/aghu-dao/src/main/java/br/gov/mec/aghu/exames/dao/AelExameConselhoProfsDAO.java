package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.model.AelExameConselhoProfs;
import br.gov.mec.aghu.model.AelExameConselhoProfsId;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * 
 * @author lalegre
 *
 */
public class AelExameConselhoProfsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExameConselhoProfs> {
	
	private static final long serialVersionUID = 3906432416586209413L;

	@Override
	protected void obterValorSequencialId(AelExameConselhoProfs elemento) {
				
		if (elemento.getExamesMaterialAnalise() == null && elemento.getConselhosProfissionais() == null) {
			
			throw new IllegalArgumentException("Exames nao esta associado corretamente.");
			
		}
		
		AelExameConselhoProfsId id = new AelExameConselhoProfsId();
		id.setCprCodigo(elemento.getConselhosProfissionais().getCodigo());
		id.setEmaExaSigla(elemento.getExamesMaterialAnalise().getId().getExaSigla());
		id.setEmaManSeq(elemento.getExamesMaterialAnalise().getId().getManSeq());
		elemento.setId(id);

	}
	
	/**
	 * Retorna uma nova referência ao registro informado por parâmetro com os
	 * valores atuais do banco por emaManSeq e emaExaSigla
	 * 
	 * @param {FatProcedHospInternos} fatProcedHospInternos
	 * @return
	 */
	public void removerAelExameConselhoProfsPorMaterial(AelExamesMaterialAnaliseId exaManId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExameConselhoProfs.class);
		criteria.add(Restrictions.eq(AelExameConselhoProfs.Fields.EMA_EXA_SIGLA.toString(), exaManId.getExaSigla()));
		criteria.add(Restrictions.eq(AelExameConselhoProfs.Fields.EMA_MAN_SEQ.toString(), exaManId.getManSeq()));
		List<AelExameConselhoProfs> aelExameConselhoProfs = this.executeCriteria(criteria);
		
		for (AelExameConselhoProfs conselhoProfs : aelExameConselhoProfs) {
			
			this.remover(conselhoProfs);
			this.flush();
			
		}
		
	}
	
	/**
	 * Utilizado no suggestionbox 
	 * @param parametro
	 * @return
	 */
	public List<RapConselhosProfissionais> listarConselhosProfissionais(Object parametro) {
	    final String srtPesquisa = (String) parametro;
	    DetachedCriteria criteria = DetachedCriteria.forClass(RapConselhosProfissionais.class);
	    
		if (CoreUtil.isNumeroInteger(parametro)) {
		    
			criteria.add(Restrictions.eq(RapConselhosProfissionais.Fields.CODIGO.toString(), Short.valueOf(srtPesquisa)));
	
		} else if (StringUtils.isNotEmpty(srtPesquisa)) {
			
			Criterion lhs = Restrictions.ilike(RapConselhosProfissionais.Fields.SIGLA.toString(), srtPesquisa, MatchMode.EXACT);
			Criterion rhs = Restrictions.ilike(RapConselhosProfissionais.Fields.NOME.toString(), srtPesquisa, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(lhs, rhs));
		} 
		
		criteria.add(Restrictions.eq(RapConselhosProfissionais.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(RapConselhosProfissionais.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}
	

	
	public List<AelExameConselhoProfs> listaConselhosProfsExame(String sigla, Integer manSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelExameConselhoProfs.class);

		criteria.createAlias(AelExameConselhoProfs.Fields.CONSELHOS_PROFISSIONAIS.toString(), "CPR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExameConselhoProfs.Fields.EXAMES_MATERIAL_ANALISE.toString(), "EMA", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(AelExameConselhoProfs.Fields.EMA_EXA_SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq(AelExameConselhoProfs.Fields.EMA_MAN_SEQ.toString(), manSeq));
		return executeCriteria(criteria);
	}
	
	/**
	 * 
	 * @param servidorRespon
	 * @param exame
	 * @param matAnalise
	 * @return
	 */
	public List<AelExameConselhoProfs> listarConselhosProfissionaisSolicitacaoExame(RapServidores servidorRespon, AelExames exame, AelMateriaisAnalises matAnalise) {
		/* SELECT	ecp.cpr_codigo,  ser.matricula,  ser.vin_codigo, ecp.ema_exa_sigla, ecp.ema_man_seq
		FROM agh.ael_exame_conselho_profs  ecp
		   inner join agh.rap_conselhos_profissionais cpr on cpr.codigo = ecp.cpr_codigo
		      left outer join agh.rap_tipos_qualificacao tql on tql.cpr_codigo = cpr.codigo --(+)
		         left outer join agh.rap_qualificacoes qlf on qlf.tql_codigo = tql.codigo --(+)
		            left outer join agh.rap_pessoas_fisicas pes on pes.codigo = qlf.pes_codigo --(+)
		               left outer join agh.rap_servidores ser on ser.pes_codigo = pes.codigo
		 WHERE (qlf.nro_reg_conselho IS NOT NULL OR qlf.nro_reg_conselho IS NULL)
		   AND  ser.matricula     = 22190--c_matricula
		--   AND  ser.vin_codigo 	  = 900--c_vin_codigo
		--   AND  ecp.ema_exa_sigla = 'DIF' --c_exa_sigla
		--   AND  ecp.ema_man_seq   = 95;
		*/
		if (servidorRespon == null || exame == null || exame.getSigla() == null 
				|| matAnalise == null || matAnalise.getSeq() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		StringBuilder hql = new StringBuilder(400);
		hql.append(" select o ");
		hql.append(" from ");
		hql.append(AelExameConselhoProfs.class.getSimpleName()).append(" o ");
		hql.append("inner join o.").append(AelExameConselhoProfs.Fields.CONSELHOS_PROFISSIONAIS).append(" cpr ");
		hql.append("left outer join cpr.").append(RapConselhosProfissionais.Fields.RAP_TIPOS_QUALIFICACAOS).append(" tql ");
		hql.append("left outer join tql.").append(RapTipoQualificacao.Fields.QUALIFICACAO).append(" qlf ");
		hql.append("left outer join qlf.").append(RapQualificacao.Fields.PESSOA_FISICA_ALIAS).append(" pes ");
		hql.append("left outer join pes.").append(RapPessoasFisicas.Fields.RAP_SERVIDORESES).append(" ser ");
		hql.append("where ( qlf.").append(RapQualificacao.Fields.NRO_REGISTRO_CONSELHO).append(" IS NOT NULL "); 
		hql.append("OR qlf.").append(RapQualificacao.Fields.NRO_REGISTRO_CONSELHO).append(" IS NULL ) ");
		
		hql.append("and ser ").append(" = :servidor ");
		hql.append("and o.").append(AelExameConselhoProfs.Fields.EMA_EXA_SIGLA).append(" = :exaSigla ");
		hql.append("and o.").append(AelExameConselhoProfs.Fields.EMA_MAN_SEQ).append(" = :manSeq ");
		
		Query query = this.createQuery(hql.toString());
		
		query.setParameter("servidor", servidorRespon);
		query.setParameter("exaSigla", exame.getSigla());
		query.setParameter("manSeq", matAnalise.getSeq());
		
		@SuppressWarnings("unchecked")
		List<AelExameConselhoProfs> list = query.getResultList();
		
		return list;
	}
	
	public Boolean buscarNroRegConselhoPorPessoaFisica(Integer matricula, Short vinCodigo, String sigla) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RapConselhosProfissionais.class, "rcp");
		criteria.createAlias("rcp." + RapConselhosProfissionais.Fields.RAP_TIPOS_QUALIFICACAOS.toString(), "tql");
		criteria.createAlias("tql." + RapTipoQualificacao.Fields.QUALIFICACAO.toString(), "qlf");
		criteria.createAlias("qlf." + RapQualificacao.Fields.PESSOA_FISICA_ALIAS.toString(), "pes");
		criteria.createAlias("pes." + RapPessoasFisicas.Fields.MATRICULAS.toString(), "ser");
		
		criteria.add(Restrictions.eq("rcp." + RapConselhosProfissionais.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("rcp." + RapConselhosProfissionais.Fields.SIGLA.toString(), sigla));
		criteria.add(Restrictions.isNotNull("qlf." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));
		criteria.add(Restrictions.eq("ser." + RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq("ser." + RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigo));

		criteria.add(Restrictions.or(Restrictions.eq("ser." + RapConselhosProfissionais.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A),
				                     Restrictions.and(Restrictions.eq("ser." + RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.P),
				                    		          Restrictions.ge("ser." + RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date()))));		
		                                              
		criteria.addOrder(Order.asc("rcp." + RapConselhosProfissionais.Fields.SIGLA.toString()));
 

		
		return executeCriteriaExists(criteria);    
	}
	
}