package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcCedenciaSalaHcpa;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSubstEscalaSala;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MbcCedenciaSalaHcpaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcCedenciaSalaHcpa> {

	private static final long serialVersionUID = 2540488594904855618L;
	
	public Long pesquisarCedenciasProgramadasCount(
			MbcCedenciaSalaHcpa mbcCedenciaSala, LinhaReportVO equipe) {
		StringBuffer hql = new StringBuffer(344);
		adicionaCountPesquisarCedenciasProgramadas(hql);
		
		adicionarFromPesquisarCedenciasProgramadas(hql);
		
		adicionarFiltroPesquisarCedenciasProgramadas(hql, mbcCedenciaSala, equipe);
		
		Query q = createHibernateQuery(hql.toString());
		
		aplicarFiltroPesquisarCedenciasProgramadas(q, mbcCedenciaSala, equipe);
		
		return Long.valueOf(q.list().size());
	}

	public List<MbcCedenciaSalaHcpa> pesquisarCedenciasProgramadas(
			MbcCedenciaSalaHcpa mbcCedenciaSala, LinhaReportVO equipe,
			Integer firstResult, Integer maxResult, boolean asc, String order) {
		StringBuffer hql = new StringBuffer(344);
		//adicionaSelectPesquisarCedenciasProgramadas(hql);
		hql.append(" SELECT DISTINCT csh \n");
		
		adicionarFromPesquisarCedenciasProgramadas(hql);
		
		adicionarFiltroPesquisarCedenciasProgramadas(hql, mbcCedenciaSala, equipe);
		adicionarOrderPesquisarCedenciasProgramadas(hql);
		Query q = createHibernateQuery(hql.toString());
		
		aplicarFiltroPesquisarCedenciasProgramadas(q, mbcCedenciaSala, equipe);
		
		//q.setResultTransformer(Transformers.aliasToBean(CedenciaSalaInstitucionalParaEquipeVO.class));
		q.setFirstResult(firstResult);
		q.setMaxResults(maxResult);
		
		List<MbcCedenciaSalaHcpa> listaResult = q.list();
		
		return listaResult;
	}
/**
 *  select CSH.data 
        , unf.sigla
        , sci.seqp
        , cas.dia_semana
        ,TUR.DESCRICAO 
        ,PES.NOME_USUAL--ps.pes_nome_usual EQUIPE_RECEBEDORA
        ,SER.MATRICULA--ps.ser_matricula MATRICULA
        ,SER.VIN_CODIGO--ps.ser_vin_codigo VIN_CODIGO
        ,csh.ind_situacao SITUACAO
        
  from   
  agh.agh_unidades_funcionais unf,
          agh.mbc_sala_cirurgicas sci,
          agh.mbc_caracteristica_sala_cirgs cas,
 		-- 
         agh.MBC_CEDENCIA_SALAS_HCPA CSH,
         --v_mbc_prof_servidor ps,
        agh.RAP_PESSOAS_FISICAS PES,
    	agh.RAP_SERVIDORES SER,
    	agh.MBC_PROF_ATUA_UNID_CIRGS PUC,
        -- 
        agh.MBC_TURNOS TUR
$$  where  cas.seq = csh.cas_seq--VMC.CAS_SEQ = csh.cas_seq
?--$      AND CSH.puc_ind_funcao_prof = PUC.IND_FUNCAO_PROF--ps.puc_ind_funcao_prof
$$      AND CSH.puc_ser_matricula = SER.MATRICULA--ps.ser_matricula
$$      AND CSH.puc_ser_vin_codigo = SER.VIN_CODIGO--ps.ser_vin_codigo
$$     and cas.htc_turno = TUR.TURNO--AND VMC.CAS_HTC_TURNO = TUR.TURNO
$$     and sci.unf_seq  =  cas.sci_unf_seq
$$     and sci.seqp        =  cas.sci_seqp
$$     and unf.seq          =  sci.unf_seq
	--*******
$$	and PES.CODIGO       = SER.PES_CODIGO
--$  	AND PUC.SER_MATRICULA  = SER.MATRICULA
--$  	AND PUC.SER_VIN_CODIGO = SER.VIN_CODIGO
--$  	AND puc.situacao       = 'A'
  
order by CSH.data desc
 */
		
	/*private Query processarConsultaPesquisarCedenciasProgramadas(
			MbcCedenciaSalaHcpa mbcCedenciaSala, LinhaReportVO equipe, StringBuffer hql) {
		
		adicionarFromPesquisarCedenciasProgramadas(hql);
		
		adicionarFiltroPesquisarCedenciasProgramadas(hql, mbcCedenciaSala, equipe);
		
		Query q = createHibernateQuery(hql.toString());
		
		aplicarFiltroPesquisarCedenciasProgramadas(q, mbcCedenciaSala, equipe);
		
		return q;
	}*/
	
	private void adicionaCountPesquisarCedenciasProgramadas(StringBuffer hql) {
		hql.append(" SELECT \n");
		hql.append(" distinct csh \n ");
		//hql.append(" COUNT(distinct csh) \n "); //#27845
	}

	private void aplicarFiltroPesquisarCedenciasProgramadas(Query q,
			MbcCedenciaSalaHcpa mbcCedenciaSala, LinhaReportVO equipe) {
		
		if(mbcCedenciaSala.getId().getData() != null){
			q.setTimestamp("data", mbcCedenciaSala.getId().getData());	
		}
		if(mbcCedenciaSala.getUnidade() != null){
			q.setShort("unfSeq", mbcCedenciaSala.getUnidade().getSeq());
		}
		if(equipe != null){
			q.setLong("matricula", equipe.getNumero11());
			q.setShort("vinCodigo", equipe.getNumero4());
		}
		if(mbcCedenciaSala.getIndSituacao() != null){			
			q.setString("indSituacao", mbcCedenciaSala.getIndSituacao().name());
		}
		
	}

	private void adicionarFromPesquisarCedenciasProgramadas(StringBuffer hql) {
		hql.append(" FROM \n");
		
		hql.append(MbcCedenciaSalaHcpa.class.getSimpleName()).append(" as csh \n");
		hql.append(" inner join fetch csh.").append(MbcCedenciaSalaHcpa.Fields.MBC_CARACTERISTICA_SALA_CIRGS).append(" as cas \n ");
		hql.append(" inner join fetch cas.").append(MbcCaracteristicaSalaCirg.Fields.TURNO					).append(" as tur \n ");
		hql.append(" inner join fetch cas.").append(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA		).append(" as sci \n ");
		hql.append(" inner join fetch sci.").append(MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL				).append(" as unf \n ");
		hql.append(" inner join fetch csh.").append(MbcCedenciaSalaHcpa.Fields.PUC_SERVIDOR						).append(" as ser \n ");
		hql.append(" inner join fetch ser.").append(RapServidores.Fields.PESSOA_FISICA						).append(" as pes \n ");
		hql.append(" left join fetch csh.").append(MbcCedenciaSalaHcpa.Fields.ESPECIALIDADE ).append(" as esp \n");
		hql.append(" , ");
		hql.append(MbcProfAtuaUnidCirgs.class.getSimpleName()).append(" as puc \n ");
		
		
		hql.append(" WHERE \n");
		hql.append(" puc.").append(MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR).append(" = ");
		hql.append(" csh.").append(MbcCedenciaSalaHcpa.Fields.PUC_SERVIDOR).append(" \n ");
		
		hql.append(" AND \n");
		hql.append(" puc.").append(MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF).append(" = ");
		hql.append(" csh.").append(MbcCedenciaSalaHcpa.Fields.PUC_IND_FUNCAO_PROF).append(" \n ");
		
		hql.append(" AND \n");
		hql.append(" puc.").append(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ).append(" = ");
		hql.append(" csh.").append(MbcCedenciaSalaHcpa.Fields.PUC_UNF_SEQ).append(" \n ");
		
		hql.append(" AND \n");
		hql.append(" puc.").append(MbcProfAtuaUnidCirgs.Fields.SITUACAO).append(" = ");
		hql.append(" '").append(DominioSituacao.A.name()).append("' \n ");
	}

	/*private void adicionaSelectPesquisarCedenciasProgramadas(StringBuffer hql) {
		hql.append(" SELECT \n");
		hql.append(" csh.").append(MbcCedenciaSalaHcpa.Fields.DATA				).append(" as ").append(CedenciaSalaInstitucionalParaEquipeVO.Fields.DATA).append(" \n ");
		hql.append(" , unf.").append(AghUnidadesFuncionais.Fields.SIGLA			).append(" as ").append(CedenciaSalaInstitucionalParaEquipeVO.Fields.SIGLA_UNF_SEQ).append(" \n ");
		hql.append(" , sci.").append(MbcSalaCirurgica.Fields.ID_SEQP			).append(" as ").append(CedenciaSalaInstitucionalParaEquipeVO.Fields.SEQP_SALA_CIRURGICA).append(" \n ");
		hql.append(" , cas.").append(MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA).append(" as ").append(CedenciaSalaInstitucionalParaEquipeVO.Fields.DIA_SEMANA).append(" \n ");
		hql.append(" , tur.").append(MbcTurnos.Fields.DESCRICAO					).append(" as ").append(CedenciaSalaInstitucionalParaEquipeVO.Fields.DESCRICAO_TURNO).append(" \n ");
		hql.append(" , pes.").append(RapPessoasFisicas.Fields.NOME_USUAL		).append(" as ").append(CedenciaSalaInstitucionalParaEquipeVO.Fields.NOME_USUAL).append(" \n ");
		hql.append(" , ser.").append(RapServidores.Fields.MATRICULA				).append(" as ").append(CedenciaSalaInstitucionalParaEquipeVO.Fields.MATRICULA).append(" \n ");
		hql.append(" , ser.").append(RapServidores.Fields.VIN_CODIGO			).append(" as ").append("vinCodigo").append(" \n ");
		hql.append(" , csh.").append(MbcCedenciaSalaHcpa.Fields.IND_SITUACAO	).append(" as ").append(CedenciaSalaInstitucionalParaEquipeVO.Fields.SITUACAO).append(" \n ");
	}*/

	private void adicionarOrderPesquisarCedenciasProgramadas(StringBuffer hql) {
		hql.append(" ORDER BY \n ");
		hql.append(" csh.").append(MbcCedenciaSalaHcpa.Fields.DATA).append(" \n ");
		hql.append(" DESC \n ");
	}

	private void adicionarFiltroPesquisarCedenciasProgramadas(StringBuffer hql,
			MbcCedenciaSalaHcpa mbcCedenciaSala, LinhaReportVO equipe) {

		if(mbcCedenciaSala.getId().getData() != null){
			hql.append(" AND csh.").append(MbcCedenciaSalaHcpa.Fields.DATA).append(" = :data \n");	
		}
		if(mbcCedenciaSala.getUnidade() != null){
			hql.append(" AND unf.").append(AghUnidadesFuncionais.Fields.SEQUENCIAL).append(" = :unfSeq \n");
		}
		if(equipe != null){
			hql.append(" AND ser.").append(RapServidores.Fields.MATRICULA)	.append(" = :matricula \n");
			hql.append(" AND ser.").append(RapServidores.Fields.VIN_CODIGO)	.append(" = :vinCodigo \n");
		}
		if(mbcCedenciaSala.getIndSituacao() != null){			
			hql.append(" AND csh.").append(MbcCedenciaSalaHcpa.Fields.IND_SITUACAO		).append(" = :indSituacao \n");
		}
		
	}

	public List<MbcCedenciaSalaHcpa> pesquisarCedenciasPorDataMbcCaractSalaCirg(
			Date data, MbcCaracteristicaSalaCirg mbcCaracteristicaSalaCirg) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCedenciaSalaHcpa.class, "csh");
		
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.DATA.toString(), DateUtil.truncaData(data)));
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), mbcCaracteristicaSalaCirg));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcCedenciaSalaHcpa> pesquisarCedenciasPorDataTurnoUnidadeSala(
			Date data, MbcHorarioTurnoCirg turno, Short unfSeq, Short sciSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCedenciaSalaHcpa.class, "csh");
		criteria.createAlias("csh." + MbcCedenciaSalaHcpa.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString(), "htc");
		
		criteria.setFetchMode("cas", FetchMode.JOIN);
		criteria.setFetchMode("htc", FetchMode.JOIN);
		
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.DATA.toString(), DateUtil.truncaData(data)));
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if (turno!=null){
			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString(), turno));
		}	
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.HTC_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SCI_SEQP.toString(), sciSeqp));
		
		return executeCriteria(criteria);
	}

	public Long recuperarProgramacaoAgendaSalaInstitucionalCount(MbcCedenciaSalaHcpa cedenciaSelecionada) {
		
			DetachedCriteria criteria = DetachedCriteria.forClass(MbcCedenciaSalaHcpa.class, "csh");
			
			criteria.createAlias("csh.".concat(MbcCedenciaSalaHcpa.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString()), "cas");
			criteria.createAlias("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString()), "sci");
			criteria.createAlias("sci.".concat(MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL.toString()), "unf");	
			
			criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.CAS_SEQ.toString(), cedenciaSelecionada.getMbcCaracteristicaSalaCirg().getSeq()));			
			criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_SER_MATRICULA.toString(), cedenciaSelecionada.getMbcProfAtuaUnidCirgs().getId().getSerMatricula()));
			criteria.add(Restrictions.eq("csh." + MbcSubstEscalaSala.Fields.PUC_SER_VIN_CODIGO.toString(), cedenciaSelecionada.getMbcProfAtuaUnidCirgs().getId().getSerVinCodigo()));
			criteria.add(Restrictions.eq("csh." + MbcSubstEscalaSala.Fields.PUC_UNF_SEQ.toString(), cedenciaSelecionada.getMbcProfAtuaUnidCirgs().getId().getUnfSeq()));
			criteria.add(Restrictions.eq("csh." + MbcSubstEscalaSala.Fields.PUC_IND_FUNCAO_PROF.toString(), cedenciaSelecionada.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf()));
			criteria.add(Restrictions.eq("csh." + MbcSubstEscalaSala.Fields.DATA.toString(), cedenciaSelecionada.getId().getData()));
			
			criteria.add(Subqueries.propertyIn("sci." + MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString(), getSubCriteria(MbcAgendas.Fields.SALA_CIRURGICA_UNF_SEQ.toString())));
			criteria.add(Subqueries.propertyIn("sci." + MbcSalaCirurgica.Fields.ID_SEQP.toString(), getSubCriteria(MbcAgendas.Fields.SALA_CIRURGICA_SEQP.toString())));
			criteria.add(Subqueries.propertyIn("csh." + MbcSubstEscalaSala.Fields.PUC_SER_MATRICULA.toString(), getSubCriteria(MbcAgendas.Fields.PUC_SER_MATRICULA.toString())));
			criteria.add(Subqueries.propertyIn("csh." + MbcSubstEscalaSala.Fields.PUC_SER_VIN_CODIGO.toString(), getSubCriteria(MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString())));
			criteria.add(Subqueries.propertyIn("csh." + MbcSubstEscalaSala.Fields.PUC_UNF_SEQ.toString(), getSubCriteria(MbcAgendas.Fields.PUC_UNF_SEQ.toString())));
			criteria.add(Subqueries.propertyIn("csh." + MbcSubstEscalaSala.Fields.PUC_IND_FUNCAO_PROF.toString(), getSubCriteria(MbcAgendas.Fields.PUC_IND_FUNCAO_PROF.toString())));
			criteria.add(Subqueries.propertyIn("csh." + MbcSubstEscalaSala.Fields.DATA.toString(), getSubCriteria(MbcAgendas.Fields.DT_AGENDA.toString())));		
			
			return executeCriteriaCount(criteria);
		}

		private DetachedCriteria getSubCriteria(String propriedade) {
			DetachedCriteria criteriaAgendas = DetachedCriteria.forClass(MbcAgendas.class, "agendas");
			
			criteriaAgendas.setProjection(Projections.projectionList().add(Projections.property(propriedade)));
			
			criteriaAgendas.add(Restrictions.gt("agendas." + MbcAgendas.Fields.DT_AGENDA.toString(), DateUtil.truncaData(new Date())));
			criteriaAgendas.add(Restrictions.ne("agendas." + MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.CA));		
			
			return criteriaAgendas;		
	}
	
}
