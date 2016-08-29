package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.enums.ConselhoRegionalMedicinaEnum;
import br.gov.mec.aghu.enums.ConselhoRegionalOdontologiaEnum;
import br.gov.mec.aghu.model.AelPatologistaAps;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.core.utils.DateUtil;


public class AelPatologistaApDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelPatologistaAps> {

	private static final long serialVersionUID = 8899308012111182839L;
	private final String stringSeparator = ".";
	
	public List<Object[]> listarPatologistaComQualificacao(Long luxSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologistaAps.class);

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AelPatologistaAps.Fields.ORDEM_MEDICO_LAUDO.toString()))
				.add(Projections.property(AelPatologistaAps.Fields.SERVIDOR.toString() + stringSeparator + RapServidores.Fields.MATRICULA.toString()))
				.add(Projections.property(AelPatologistaAps.Fields.SERVIDOR.toString() + stringSeparator + RapServidores.Fields.CODIGO_VINCULO.toString()))
				.add(Projections.property(RapPessoasFisicas.Fields.QUALIFICACOES.toString()
										+ stringSeparator + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString())));	

		// rap_servidores
		criteria.createAlias(AelPatologistaAps.Fields.SERVIDOR.toString(),
				AelPatologistaAps.Fields.SERVIDOR.toString());

		// rap_pessoas_fisicas
		criteria.createAlias(AelPatologistaAps.Fields.SERVIDOR.toString() + stringSeparator + RapServidores.Fields.PESSOA_FISICA.toString(),
				RapServidores.Fields.PESSOA_FISICA.toString());
		
		// rap_qualificacoes
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString()
				+ stringSeparator
				+ RapPessoasFisicas.Fields.QUALIFICACOES.toString(),
				RapPessoasFisicas.Fields.QUALIFICACOES.toString());
		
		// rap_tipos_qualificacao
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString()
				+ stringSeparator
				+ RapPessoasFisicas.Fields.QUALIFICACOES.toString()
				+ stringSeparator
				+ RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(),
				RapQualificacao.Fields.TIPO_QUALIFICACAO.toString());
		
		// rap_conselhos_profissionais
		criteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString()
				+ stringSeparator
				+ RapPessoasFisicas.Fields.QUALIFICACOES.toString()
				+ stringSeparator
				+ RapQualificacao.Fields.TIPO_QUALIFICACAO.toString()
				+ stringSeparator
				+ RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(),
				RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString());
		
		criteria.add(Restrictions.or(
				 Restrictions.isNull(AelPatologistaAps.Fields.SERVIDOR.toString() + stringSeparator + RapServidores.Fields.DATA_FIM_VINCULO.toString()),
				 Restrictions.gt(AelPatologistaAps.Fields.SERVIDOR.toString() + stringSeparator + RapServidores.Fields.DATA_FIM_VINCULO.toString(),
						 	  DateUtil.truncaData(new Date()))));
	
		List<String> restricoes = new ArrayList<String>();
		restricoes.add(ConselhoRegionalMedicinaEnum.CREMERS.toString());
		restricoes.add(ConselhoRegionalOdontologiaEnum.CRO.toString());
		restricoes.add("CRFa");
	
		criteria.add(Restrictions.in(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString() 
					+ stringSeparator
					+ RapConselhosProfissionais.Fields.SIGLA.toString(), restricoes));

		criteria.add(Restrictions.isNotNull(RapPessoasFisicas.Fields.QUALIFICACOES.toString()+stringSeparator+RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));
		criteria.add(Restrictions.eq(AelPatologistaAps.Fields.AEL_EXAME_APS_SEQ.toString(), luxSeq));

		criteria.addOrder(Order.asc(AelPatologistaAps.Fields.ORDEM_MEDICO_LAUDO.toString()));

		List<Object[]> lista = executeCriteria(criteria);
		return lista;
	}

	public List<AelPatologistaAps> obterPatologistasPeloExameSeqOrdenadoPelaOrdemMedicoLaudo(Long luxSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologistaAps.class);
		
		criteria.add(Restrictions.eq(AelPatologistaAps.Fields.AEL_EXAME_APS_SEQ.toString(), luxSeq));
		criteria.addOrder(Order.asc(AelPatologistaAps.Fields.ORDEM_MEDICO_LAUDO.toString()));
		criteria.addOrder(Order.desc(AelPatologistaAps.Fields.CRIADO_EM.toString()));
		
		return executeCriteria(criteria);
	}

	public AelPatologistaAps obterPatologistaPeloExameSeqEServidor(Long luxSeq, RapServidores servidor) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologistaAps.class);
		
		criteria.add(Restrictions.eq(AelPatologistaAps.Fields.AEL_EXAME_APS_SEQ.toString(), luxSeq));
		criteria.add(Restrictions.eq(AelPatologistaAps.Fields.SERVIDOR.toString(), servidor));
		
		return (AelPatologistaAps)executeCriteriaUniqueResult(criteria);
	}
	
	
	public List<AelPatologistaAps> listarPatologistaApPorLuxSeq(Long luxSeq) {
		
			final DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologistaAps.class);

			if(luxSeq != null){
				criteria.add(Restrictions.eq(AelPatologistaAps.Fields.AEL_EXAME_APS_SEQ.toString(), luxSeq));
			}
			
			criteria.addOrder(Order.asc(AelPatologistaAps.Fields.ORDEM_MEDICO_LAUDO.toString()));
			
			return executeCriteria(criteria);
	}
	
	public List<AelPatologistaAps> listarPatologistaLaudoPorLuxSeqEOrdemMaior(Long luxSeq, Short ordem) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologistaAps.class);

		if(luxSeq != null){
			criteria.add(Restrictions.eq(AelPatologistaAps.Fields.AEL_EXAME_APS_SEQ.toString(), luxSeq));
		}
		
		if (ordem != null) {
			criteria.add(Restrictions.gt(AelPatologistaAps.Fields.ORDEM_MEDICO_LAUDO.toString(), ordem));
		}
		
		criteria.addOrder(Order.asc(AelPatologistaAps.Fields.ORDEM_MEDICO_LAUDO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<AelPatologistaAps> listarPatologistaLaudoPorLuxSeqEOrdem(Long luxSeq, Short ordem) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologistaAps.class);

		if(luxSeq != null){
			criteria.add(Restrictions.eq(AelPatologistaAps.Fields.AEL_EXAME_APS_SEQ.toString(), luxSeq));
		}
		
		if (ordem != null) {
			criteria.add(Restrictions.eq(AelPatologistaAps.Fields.ORDEM_MEDICO_LAUDO.toString(), ordem));
		}
		
		criteria.addOrder(Order.asc(AelPatologistaAps.Fields.ORDEM_MEDICO_LAUDO.toString()));
		
		return executeCriteria(criteria);
	}	
	
	public Boolean verificaPatologitaPeloExameSeq(Long luxSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelPatologistaAps.class);
		
		criteria.add(Restrictions.eq(AelPatologistaAps.Fields.AEL_EXAME_APS_SEQ.toString(), luxSeq));
		
		Long count = executeCriteriaCount(criteria);
		if(count != null && count >0) {
			return true;
		}
		else {
			return false;
		}
	}
}