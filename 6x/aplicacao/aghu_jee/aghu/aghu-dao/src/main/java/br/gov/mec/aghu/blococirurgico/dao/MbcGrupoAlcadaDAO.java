package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.blococirurgico.vo.AvalOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.HistoricoAlteracoesGrupoAlcadaVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoConvenioOpms;
import br.gov.mec.aghu.dominio.DominioTipoObrigatoriedadeOpms;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcServidorAvalOpms;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;

public class MbcGrupoAlcadaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcGrupoAlcadaAvalOpms> {

	private static final long serialVersionUID = -506628165399209099L;
	
	public Long buscarMbcAlcadaAval(MbcGrupoAlcadaAvalOpms grupoAlcadaAvalOpms) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAlcadaAvalOpms.class, "aao");
		
		criteria.add(Restrictions.eq(MbcAlcadaAvalOpms.Fields.GRUPO_ALCADA_SEQ.toString(), grupoAlcadaAvalOpms.getSeq()));
		return executeCriteriaCount(criteria);
	}

	public List<MbcGrupoAlcadaAvalOpms> listarGrupoAlcadaFiltro(
			Short grupoAlcadaSeq,
			AghEspecialidades aghEspecialidades,
			DominioTipoConvenioOpms tipoConvenioOpms,
			DominioTipoObrigatoriedadeOpms tipObrigatoriedadeOpms,
			Short versao,
			DominioSituacao situacao) {

		DetachedCriteria criteria = criaPesquisaGrupoAlcada(grupoAlcadaSeq,
															aghEspecialidades,
															tipoConvenioOpms,
															tipObrigatoriedadeOpms,
															versao, 
															situacao);
		criteria.addOrder(Order.asc(MbcGrupoAlcadaAvalOpms.Fields.TIPO_CONVENIO.toString()));
		criteria.addOrder(Order.asc("esp."+ AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));
		criteria.addOrder(Order.asc(MbcGrupoAlcadaAvalOpms.Fields.VERSAO.toString()));
		return executeCriteria(criteria);
	}

	private DetachedCriteria criaPesquisaGrupoAlcada(
			Short grupoAlcadaSeq,
			AghEspecialidades aghEspecialidades,
			DominioTipoConvenioOpms tipoConvenioOpms, 
			DominioTipoObrigatoriedadeOpms tipoObrigatoriedadeOpms,
			Short versao,
			DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcGrupoAlcadaAvalOpms.class, "gao");
		criteria.createAlias(MbcGrupoAlcadaAvalOpms.Fields.AGH_ESPECIALIDADES.toString(),"esp", DetachedCriteria.LEFT_JOIN);
		
		if(tipoConvenioOpms != null){
			criteria.add(Restrictions.eq(MbcGrupoAlcadaAvalOpms.Fields.TIPO_CONVENIO.toString(),tipoConvenioOpms));
		}
		
		if (tipoObrigatoriedadeOpms != null) {
			criteria.add(Restrictions.eq(
					MbcGrupoAlcadaAvalOpms.Fields.TIPO_OBRIGATORIEDADE
							.toString(), tipoObrigatoriedadeOpms));
		}
		if(grupoAlcadaSeq!=null){
			criteria.add(Restrictions.eq(MbcGrupoAlcadaAvalOpms.Fields.SEQ.toString(),grupoAlcadaSeq));
		}
		if (aghEspecialidades != null) {
			criteria.add(Restrictions.eq(
					"esp." + AghEspecialidades.Fields.SEQ.toString(),
					aghEspecialidades.getSeq()));
		}
		if (versao != null) {
			criteria.add(Restrictions.eq(
					MbcGrupoAlcadaAvalOpms.Fields.VERSAO.toString(), versao));
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq(
					MbcGrupoAlcadaAvalOpms.Fields.SITUACAO.toString(), situacao));
		}

		return criteria;
	}
	
	public MbcGrupoAlcadaAvalOpms buscaGrupoAlcadaAtivo(DominioTipoConvenioOpms tipoConvenio,AghEspecialidades aghEspecialidades) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcGrupoAlcadaAvalOpms.class, "gao");
		
		criteria.add(Restrictions.eq(MbcGrupoAlcadaAvalOpms.Fields.TIPO_CONVENIO.toString(),tipoConvenio));
		criteria.add(Restrictions.eq(MbcGrupoAlcadaAvalOpms.Fields.SITUACAO.toString(),DominioSituacao.A));
		
		if (aghEspecialidades != null) {
			criteria.add(Restrictions.eq(MbcGrupoAlcadaAvalOpms.Fields.AGH_ESPECIALIDADES.toString(),aghEspecialidades));
		} else {
			criteria.add(Restrictions.isNull(MbcGrupoAlcadaAvalOpms.Fields.AGH_ESPECIALIDADES.toString()));
		}		
		return (MbcGrupoAlcadaAvalOpms) executeCriteriaUniqueResult(criteria);
	}

	public MbcGrupoAlcadaAvalOpms buscaGrupoAlcada(DominioTipoConvenioOpms tipoConvenio,AghEspecialidades aghEspecialidades) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcGrupoAlcadaAvalOpms.class, "gao");
		criteria.createAlias(MbcGrupoAlcadaAvalOpms.Fields.AGH_ESPECIALIDADES.toString(),"esp", DetachedCriteria.LEFT_JOIN);

		criteria.add(Restrictions.eq(MbcGrupoAlcadaAvalOpms.Fields.TIPO_CONVENIO.toString(),tipoConvenio));
		if (aghEspecialidades != null) {
			criteria.add(Restrictions.eq(
					"esp." + AghEspecialidades.Fields.SEQ.toString(),
					aghEspecialidades.getSeq()));
		} else {
			criteria.add(Restrictions.isNull("esp."
					+ AghEspecialidades.Fields.SEQ.toString()));
		}
		criteria.addOrder(Order.desc(MbcGrupoAlcadaAvalOpms.Fields.VERSAO.toString()));

		List<MbcGrupoAlcadaAvalOpms> grupos = executeCriteria(criteria);
		if (grupos.isEmpty()) {
			return null;
		} else {
			return grupos.get(0);
		}
	}
	
	public Short buscaMaxVersaoGrupoAlcada(DominioTipoConvenioOpms tipoConvenio,AghEspecialidades aghEspecialidades) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcGrupoAlcadaAvalOpms.class, "gao");
		criteria.createAlias(MbcGrupoAlcadaAvalOpms.Fields.AGH_ESPECIALIDADES.toString(),"esp", DetachedCriteria.LEFT_JOIN);
		criteria.setProjection(Projections.max(MbcGrupoAlcadaAvalOpms.Fields.VERSAO.toString()));

		criteria.add(Restrictions.eq(MbcGrupoAlcadaAvalOpms.Fields.TIPO_CONVENIO.toString(),tipoConvenio));
		
		if (aghEspecialidades != null) {
			criteria.add(Restrictions.eq(
					"esp." + AghEspecialidades.Fields.SEQ.toString(),
					aghEspecialidades.getSeq()));
		} else {
			criteria.add(Restrictions.isNull("esp."
					+ AghEspecialidades.Fields.SEQ.toString()));
		}
		Object result = executeCriteriaUniqueResult(criteria);
		return result == null ? 0 :(Short)result;
	}
	
	public List<HistoricoAlteracoesGrupoAlcadaVO> buscarHistoricoGrupoAlcadaUnion1(
			Short grupoAlcadaSeq) {

		StringBuilder hql = new StringBuilder(400);

		hql.append("SELECT '1' as ");
		hql.append(HistoricoAlteracoesGrupoAlcadaVO.Fields.SEQ.toString());
		hql.append(", gao.");
		hql.append(MbcGrupoAlcadaAvalOpms.Fields.TIPO_CONVENIO.toString());
		hql.append(" || \'\' || esp.");
		hql.append(AghEspecialidades.Fields.SIGLA.toString());
		hql.append(" || \' v\' || gao.");
		hql.append(MbcGrupoAlcadaAvalOpms.Fields.VERSAO.toString());
		hql.append(" as descricaoGrupo ");
		hql.append(", gao." + MbcGrupoAlcadaAvalOpms.Fields.SITUACAO.toString()
				+ " as indSituacao ");
		hql.append(", gao." + MbcGrupoAlcadaAvalOpms.Fields.CRIADO_EM
				+ " as criadoEm ");
		hql.append(", pes_gao1." + RapPessoasFisicas.Fields.NOME
				+ " as criacaoNome ");
		hql.append(", gao." + MbcGrupoAlcadaAvalOpms.Fields.MODIFICADO_EM
				+ " as modificadoEm ");
		hql.append(", pes_gao2." + RapPessoasFisicas.Fields.NOME
				+ " as modificadoNome ");
		hql.append(" FROM  MbcGrupoAlcadaAvalOpms as gao ");
		hql.append(" JOIN gao."
				+ MbcGrupoAlcadaAvalOpms.Fields.AGH_ESPECIALIDADES + " as esp ");
		hql.append(" JOIN   gao."
				+ MbcGrupoAlcadaAvalOpms.Fields.RAP_SERVIDORES
				+ " as ser_gao1  ");
		hql.append(" JOIN ser_gao1." + RapServidores.Fields.PESSOA_FISICA
				+ " as pes_gao1 ");
		hql.append(" LEFT JOIN gao."
				+ MbcGrupoAlcadaAvalOpms.Fields.RAP_SERVIDORES_MODIFICACAO
				+ " as ser_gao2  ");
		hql.append(" LEFT JOIN ser_gao2." + RapServidores.Fields.PESSOA_FISICA
				+ " as pes_gao2  ");
		hql.append(" WHERE  gao.seq = :grupoAlcadaSeq ");

		Query query = createHibernateQuery(hql.toString());
		query.setShort("grupoAlcadaSeq", grupoAlcadaSeq);

		query.setResultTransformer(Transformers
				.aliasToBean(HistoricoAlteracoesGrupoAlcadaVO.class));

		return query.list();
	}

	public List<HistoricoAlteracoesGrupoAlcadaVO> buscarHistoricoGrupoAlcadaUnion2(
			Short grupoAlcadaSeq) {

		StringBuilder hql = new StringBuilder(400);

		hql.append("SELECT  2 || '' || aao.");
		hql.append(MbcAlcadaAvalOpms.Fields.NIVEL_ALCADA);
		hql.append(" as seq");
		hql.append(", aao.");
		hql.append(MbcAlcadaAvalOpms.Fields.NIVEL_ALCADA);
		hql.append(" || ' ' || aao.descricao  as descricaoNivel ");
		// hql.append(", 'A' as indSituacao ");
		hql.append(", aao." + MbcGrupoAlcadaAvalOpms.Fields.CRIADO_EM
				+ " as criadoEm ");
		hql.append(", pes_aao1." + RapPessoasFisicas.Fields.NOME
				+ " as criacaoNome ");
		hql.append(", aao." + MbcGrupoAlcadaAvalOpms.Fields.MODIFICADO_EM
				+ " as modificadoEm ");
		hql.append(", pes_aao2." + RapPessoasFisicas.Fields.NOME
				+ " as modificadoNome ");
		hql.append(" FROM  MbcGrupoAlcadaAvalOpms as gao ");
		hql.append(" JOIN gao." + "alcadas" + " as aao ");
		hql.append(" JOIN   aao." + MbcAlcadaAvalOpms.Fields.RAP_SERVIDORES
				+ " as ser_aao1  ");
		hql.append(" JOIN ser_aao1." + RapServidores.Fields.PESSOA_FISICA
				+ " as pes_aao1 ");
		hql.append(" LEFT JOIN aao."
				+ MbcAlcadaAvalOpms.Fields.RAP_SERVIDORES_MODIFICACAO
				+ " as ser_aao2  ");
		hql.append(" LEFT JOIN ser_aao2." + RapServidores.Fields.PESSOA_FISICA
				+ " as pes_aao2  ");
		hql.append(" WHERE  gao.seq = :grupoAlcadaSeq ");

		Query query = createHibernateQuery(hql.toString());
		query.setShort("grupoAlcadaSeq", grupoAlcadaSeq);

		query.setResultTransformer(Transformers
				.aliasToBean(HistoricoAlteracoesGrupoAlcadaVO.class));

		List<HistoricoAlteracoesGrupoAlcadaVO> result = query.list();

		for (HistoricoAlteracoesGrupoAlcadaVO historico : result) {
			historico.setIndSituacao(DominioSituacao.A);
		}

		return result;
	}

	public List<HistoricoAlteracoesGrupoAlcadaVO> buscarHistoricoGrupoAlcadaUnion3(
			Short grupoAlcadaSeq) {

		StringBuilder hql = new StringBuilder(600);

		hql.append("SELECT  2 || '' || aao.");
		hql.append(MbcAlcadaAvalOpms.Fields.NIVEL_ALCADA);
		hql.append(" || sao."); 
		hql.append(MbcServidorAvalOpms.Fields.SEQUENCIA);
		hql.append("  as seq");
		hql.append(", sao.");
		hql.append(MbcServidorAvalOpms.Fields.SEQUENCIA);
		hql.append("||' '||");
		hql.append(" pes_sao0." + RapPessoasFisicas.Fields.NOME
				+ " as descricaoServidor");
		hql.append(", sao." + MbcGrupoAlcadaAvalOpms.Fields.SITUACAO.toString()
				+ " as indSituacao ");
		hql.append(", sao." + MbcGrupoAlcadaAvalOpms.Fields.CRIADO_EM
				+ " as criadoEm ");
		hql.append(", pes_sao1." + RapPessoasFisicas.Fields.NOME
				+ " as criacaoNome ");
		hql.append(", sao." + MbcGrupoAlcadaAvalOpms.Fields.MODIFICADO_EM
				+ " as modificadoEm ");
		hql.append(", pes_sao2." + RapPessoasFisicas.Fields.NOME
				+ " as modificadoNome ");
		hql.append(" FROM  " + MbcGrupoAlcadaAvalOpms.class.getName()
				+ " as gao ");
		hql.append(" JOIN gao." + MbcGrupoAlcadaAvalOpms.Fields.ALCADAS
				+ " as aao ");
		hql.append(" JOIN   aao." + MbcAlcadaAvalOpms.Fields.SERVIDORES_AVAL
				+ " as sao  ");
		hql.append(" JOIN   sao." + MbcServidorAvalOpms.Fields.SERVIDOR
				+ " as ser_sao0  ");
		hql.append(" JOIN ser_sao0." + RapServidores.Fields.PESSOA_FISICA
				+ " as pes_sao0 ");
		hql.append(" LEFT JOIN sao." + MbcServidorAvalOpms.Fields.SERVIDOR_CRIACAO
				+ " as ser_sao1  ");
		hql.append(" LEFT JOIN ser_sao1." + RapServidores.Fields.PESSOA_FISICA
				+ " as pes_sao1 ");
		hql.append(" LEFT JOIN sao." + MbcServidorAvalOpms.Fields.SERVIDOR_MODIFICACAO
				+ " as ser_sao2  ");
		hql.append(" LEFT JOIN ser_sao2." + RapServidores.Fields.PESSOA_FISICA
				+ " as pes_sao2  ");
		hql.append(" WHERE  gao.seq = :grupoAlcadaSeq ");

		Query query = createHibernateQuery(hql.toString());
		query.setShort("grupoAlcadaSeq", grupoAlcadaSeq);

		query.setResultTransformer(Transformers
				.aliasToBean(HistoricoAlteracoesGrupoAlcadaVO.class));

		return query.list();

	}
	
	// C09_VER_OBG - #40466 (Melhoria)	

	@SuppressWarnings("unchecked")
	public List<AvalOPMEVO> verificaObrigRegistroOpmes(MbcAgendas agenda){
		StringBuilder hql = new StringBuilder(2000);
		
		hql.append("SELECT");
//		hql.append(" AGD." + MbcAgendas.Fields.SEQ.toString());
//		hql.append(" AGD." + MbcAgendas.Fields.CONVENIO_SAUDE_PLANO_CSP_CONV_SEQ.toString()+" as cnvCodigo");
//		hql.append(" CNV." + FatConvenioSaude.Fields.DESCRICAO.toString());
//		hql.append(" AGD." + MbcAgendas.Fields.CONVENIO_SAUDE_PLANO_CSP_SEQ.toString()+" as cnvSeq");
//		hql.append(" CSP." + FatConvenioSaudePlano.Fields.DESCRICAO.toString()+"as cnvDescricao");
//		hql.append(" AGD." + MbcAgendas.Fields.ESPECIALIDADE_SEQ.toString());
		hql.append(" GAO." + MbcGrupoAlcadaAvalOpms.Fields.TIPO_OBRIGATORIEDADE.toString()+" as "+ MbcGrupoAlcadaAvalOpms.Fields.TIPO_OBRIGATORIEDADE.toString());
		
		hql.append(" FROM ");
		hql.append(MbcGrupoAlcadaAvalOpms.class.getSimpleName() + 
				" GAO, "+FatConvenioSaude.class.getSimpleName() + 
				" CNV, "+FatConvenioSaudePlano.class.getSimpleName() + 
				" CSP");//, "+MbcAgendas.class.getSimpleName() + " AGD ");

		
		hql.append(" WHERE");
		//hql.append(" AGD." + MbcAgendas.Fields.CONVENIO_SAUDE_PLANO_CSP_CONV_SEQ.toString() + " = CSP." + FatConvenioSaudePlano.Fields.CNV_CODIGO.toString());
		//hql.append(" AND AGD." + MbcAgendas.Fields.CONVENIO_SAUDE_PLANO_CSP_SEQ.toString() + " = CSP." + FatConvenioSaudePlano.Fields.SEQ.toString());
		hql.append(" CSP." + FatConvenioSaudePlano.Fields.CNV_CODIGO.toString() + " = CNV." + FatConvenioSaude.Fields.CODIGO.toString());
		hql.append(" AND GAO." + MbcGrupoAlcadaAvalOpms.Fields.TIPO_CONVENIO.toString() + " = CNV." + FatConvenioSaude.Fields.DESCRICAO.toString());
		
		hql.append(" AND GAO." + MbcGrupoAlcadaAvalOpms.Fields.SITUACAO.toString() + " = '" + DominioSituacao.A.toString()+"' ");
		hql.append(" AND GAO." + MbcGrupoAlcadaAvalOpms.Fields.AGH_ESPECIALIDADES_SEQ.toString() + " = :espSeq");
		if(agenda.getConvenioSaudePlano() != null){
			hql.append(" AND CSP." + FatConvenioSaudePlano.Fields.CNV_CODIGO.toString() +" = :cspConvSeq");
			hql.append(" AND CSP." + FatConvenioSaudePlano.Fields.SEQ.toString() + " = :cspSeq");
		}	
		
		//hql.append(" AND AGD." + MbcAgendas.Fields.SEQ.toString() + " = :agendaSeq");

		Query query = this.createHibernateQuery(hql.toString());
		//query.setInteger("agendaSeq", agendaSeq);
		query.setInteger("espSeq", agenda.getEspecialidade().getSeq());
		if(agenda.getConvenioSaudePlano() != null){
			query.setInteger("cspConvSeq", agenda.getConvenioSaudePlano().getId().getCnvCodigo());
			query.setInteger("cspSeq", agenda.getConvenioSaudePlano().getId().getSeq());
		}

		query.setResultTransformer(Transformers.aliasToBean(AvalOPMEVO.class));

		return query.list();

	}

}