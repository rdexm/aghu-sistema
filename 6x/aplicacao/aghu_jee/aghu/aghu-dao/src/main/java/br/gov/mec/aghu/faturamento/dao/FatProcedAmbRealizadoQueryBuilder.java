package br.gov.mec.aghu.faturamento.dao;

import org.hibernate.Query;

import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedTratamento;
import br.gov.mec.aghu.model.FatTipoTratamentos;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class FatProcedAmbRealizadoQueryBuilder extends QueryBuilder<Query> {

	private static final long serialVersionUID = 3784780540651132087L;
	
	private Integer numeroConsulta;

	private String makeQuery() {
		StringBuilder hql = new StringBuilder("select pmr from ").append(FatProcedAmbRealizado.class.getName()).append(" pmr, ")
				.append(FatConvenioSaude.class.getName()).append(" cnv,")
				.append(FatProcedTratamento.class.getName()).append(" prt, ")
				.append(FatTipoTratamentos.class.getName()).append(" tpt, ")
				.append(AacConsultaProcedHospitalar.class.getName()).append(" prh ")
				.append(" where prh.").append(AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString()).append(" = :numeroConsulta and prh.")
				.append(AacConsultaProcedHospitalar.Fields.IND_CONSULTA.toString()).append(" = :situacaoSimNao and pmr.")
				.append(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.toString()).append(" = prh.").append(AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString())
				.append(" and pmr.").append(FatProcedAmbRealizado.Fields.PRH_PHI_SEQ.toString()).append(" = prh.").append(AacConsultaProcedHospitalar.Fields.PHI_SEQ)
				.append(" and pmr.").append(FatProcedAmbRealizado.Fields.PHI_SEQ.toString()).append(" = prt.")
				.append(FatProcedTratamento.Fields.PHI_SEQ.toString()).append(" and pmr.")
				.append(FatProcedAmbRealizado.Fields.IND_SITUACAO.toString()).append(" = :situacao")
				.append(" and cnv.").append(FatConvenioSaude.Fields.CODIGO.toString())
				.append(" = pmr.").append(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.toString()).append(" and cnv.")
				.append(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString()).append(" = :situacaoConvenio and tpt.")
				.append(FatTipoTratamentos.Fields.CODIGO.toString()).append(" = (select ")
				.append(AghParametros.Fields.VLR_NUMERICO.toString())
				.append(" from ").append(AghParametros.class.getName())
				.append(" where ").append(AghParametros.Fields.NOME.toString())
				.append(" = 'P_TRATAMENTO_OTORRINO' )");
		return hql.toString();
	}
	
	@Override
	protected Query createProduct() {
		final String hql = makeQuery();
		Query query = createHibernateQuery(hql);

		return query;
	}


	
	@Override
	protected void doBuild(Query query) {
		query.setParameter("numeroConsulta", this.numeroConsulta);
		query.setParameter("situacao", DominioSituacaoProcedimentoAmbulatorio.ABERTO);
		query.setParameter("situacaoSimNao", DominioSimNao.S.isSim());
		if (numeroConsulta != null) {
			query.setParameter("situacaoConvenio", DominioGrupoConvenio.S);
		}
	}

	public Query build(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
		return super.build();
	}
}
