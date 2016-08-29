package br.gov.mec.aghu.blococirurgico.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.ByteType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasAgendaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasAgendamentoVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasCedenciaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasVO;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcCedenciaSalaHcpa;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSubstEscalaSala;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MbcSubstEscalaSalaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcSubstEscalaSala> {
	
	private static final long serialVersionUID = -7632457867723447520L;

	public List<MbcSubstEscalaSala> pesquisarSubstitutoPorCaracProfAtuaUniCirurData(Short caracteristica, Date date, MbcProfAtuaUnidCirgs profAtuaUnidCirgs){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSubstEscalaSala.class, "ses");
		criteria.createAlias("ses.".concat(MbcSubstEscalaSala.Fields.MBC_CARACT_SALA_ESPS.toString()), "cas", CriteriaSpecification.LEFT_JOIN);
		criteria.createAlias("ses.".concat(MbcSubstEscalaSala.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString()), "puc", CriteriaSpecification.LEFT_JOIN);
		
		criteria.add(Restrictions.eq("cas.".concat(MbcCaractSalaEsp.Fields.CAS_SEQ.toString()), caracteristica));
		
		criteria.add(Restrictions.eq("puc.".concat(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString()), profAtuaUnidCirgs.getId().getSerMatricula()));
		criteria.add(Restrictions.eq("puc.".concat(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString()), profAtuaUnidCirgs.getId().getSerVinCodigo()));
		criteria.add(Restrictions.eq("puc.".concat(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString()), profAtuaUnidCirgs.getId().getUnfSeq()));

		criteria.add(Restrictions.eq("ses.".concat(MbcSubstEscalaSala.Fields.IND_SITUACAO.toString()), DominioSituacao.A));
		criteria.add(Restrictions.eq("ses.".concat(MbcSubstEscalaSala.Fields.DATA.toString()), date));

		return executeCriteria(criteria);
	}
	
	public List<MbcSubstEscalaSala> pesquisarSubstitutoPorCaracProfAtuaUniCirurData(Short casSeq, Integer matricula, Short vinCodigo, Short unfSeq, Date data){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSubstEscalaSala.class, "ses");
		
		criteria.createAlias("ses.".concat(MbcSubstEscalaSala.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString()), "puc", CriteriaSpecification.INNER_JOIN);

		criteria.add(Restrictions.eq("puc.".concat(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString()), matricula));
		criteria.add(Restrictions.eq("puc.".concat(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString()),vinCodigo));
		criteria.add(Restrictions.eq("puc.".concat(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString()), unfSeq));
		
		criteria.add(Restrictions.eq("ses.".concat(MbcSubstEscalaSala.Fields.IND_SITUACAO.toString()), DominioSituacao.A));
		criteria.add(Restrictions.eq("ses.".concat(MbcSubstEscalaSala.Fields.DATA.toString()), data));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcSubstEscalaSala> listarCedenciaSalasEntreEquipes(
			MbcCedenciaSalaHcpa mbcCedenciaSala, LinhaReportVO equipe,
			Integer firstResult, Integer maxResult, String order, boolean asc){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSubstEscalaSala.class, "ses");
		criarCriteriaListarCedenciaSalaEntreEquipes(mbcCedenciaSala, equipe, criteria);
		
		
		if(order == null){
			if(asc){
				criteria.addOrder(Order.asc(MbcSubstEscalaSala.Fields.DATA.toString()));
			}else{
				criteria.addOrder(Order.desc(MbcSubstEscalaSala.Fields.DATA.toString()));
			}
		}
		
		return executeCriteria(criteria, firstResult, maxResult, null, asc);
		 
	}
	
	public Long listarCedenciaSalasEntreEquipesCount(MbcCedenciaSalaHcpa mbcCedenciaSala, LinhaReportVO equipe){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSubstEscalaSala.class, "ses");
		criarCriteriaListarCedenciaSalaEntreEquipes(mbcCedenciaSala, equipe, criteria);
		
		return executeCriteriaCount(criteria);
	}

	private void criarCriteriaListarCedenciaSalaEntreEquipes(MbcCedenciaSalaHcpa mbcCedenciaSala, LinhaReportVO equipe, DetachedCriteria criteria) {
		criteria.createAlias("ses.".concat(MbcSubstEscalaSala.Fields.MBC_CARACT_SALA_ESPS.toString()), "cse", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ses.".concat(MbcSubstEscalaSala.Fields.AGH_PROF_ESPECIALIDADES.toString()), "prof", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("prof.".concat(AghProfEspecialidades.Fields.ESPECIALIDADE.toString()), "esp1", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString()), "cas");
		criteria.createAlias("cas.".concat(MbcCaracteristicaSalaCirg.Fields.TURNO.toString()), "TURNO"); 
		criteria.createAlias("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString()), "sci");
		criteria.createAlias("sci.".concat(MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL.toString()), "unf");
		criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.AGH_ESPECIALIDADES.toString()), "esp");
		criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString()), "puc");
		criteria.createAlias("puc.".concat(MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString()), "ser1");
		criteria.createAlias("ser1.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "pes1");
		criteria.createAlias("ses.".concat(MbcSubstEscalaSala.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString()), "puc2");
		criteria.createAlias("puc2.".concat(MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString()), "ser");
		criteria.createAlias("ser.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "pes");
		
		if(mbcCedenciaSala.getId().getData() != null){
			criteria.add(Restrictions.eq("ses.".concat(MbcSubstEscalaSala.Fields.DATA.toString()), mbcCedenciaSala.getId().getData()));
		}
		if(mbcCedenciaSala.getUnidade() != null && mbcCedenciaSala.getUnidade().getSeq() != null){
			criteria.add(Restrictions.eq("unf.".concat(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()), mbcCedenciaSala.getUnidade().getSeq()));
		}
		if(equipe != null && equipe.getNumero4() != null && equipe.getNumero11() != null){
			criteria.add(Restrictions.eq("ser.".concat(RapServidores.Fields.MATRICULA.toString()), equipe.getNumero11()));
			criteria.add(Restrictions.eq("ser.".concat(RapServidores.Fields.VIN_CODIGO.toString()), equipe.getNumero4()));
		}
		if(mbcCedenciaSala.getIndSituacao() != null){
			criteria.add(Restrictions.eq("ses.".concat(MbcSubstEscalaSala.Fields.IND_SITUACAO.toString()), mbcCedenciaSala.getIndSituacao()));
		}
		criteria.add(Restrictions.eq("puc2.".concat(MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString()), DominioSituacao.A));
	}
	
	public List<Short> pesquisarCedenciaSala(Date dataBase, Short casSeq, Short espSeq, Short seqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSubstEscalaSala.class, "ssl");
		
		criteria.setProjection(Projections.property("ssl." + MbcSubstEscalaSala.Fields.CSE_CAS_SEQ.toString()));
		
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.DATA.toString(), DateUtil.truncaData(dataBase)));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.CSE_CAS_SEQ.toString(), casSeq));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.CSE_ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.CSE_SEQP.toString(), seqp));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcSubstEscalaSala> pesquisarCedenciaSalaPorDataCasSeqEspSeqSeqp(Date dataBase, Short casSeq, Short espSeq, Short seqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSubstEscalaSala.class, "ssl");
		
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.DATA.toString(), DateUtil.truncaData(dataBase)));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.CSE_CAS_SEQ.toString(), casSeq));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.CSE_ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.CSE_SEQP.toString(), seqp));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Busca SubsEscalaSala por matrícula, código, unidade e função
	 * 
	 * @param matricula
	 * @param codigo
	 * @param unidade
	 * @param funcao
	 * @return
	 */
	public Long pesquisarSubsEscalaSalaPorMatriculaCodigoUnidFuncaoCount(Integer matricula, Short codigo, Short unidade, DominioFuncaoProfissional funcao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSubstEscalaSala.class);
		
		criteria.add(Restrictions.eq(MbcSubstEscalaSala.Fields.PUC_SER_MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq(MbcSubstEscalaSala.Fields.PUC_SER_VIN_CODIGO.toString(), codigo));
		criteria.add(Restrictions.eq(MbcSubstEscalaSala.Fields.PUC_UNF_SEQ.toString(), unidade));
		criteria.add(Restrictions.eq(MbcSubstEscalaSala.Fields.PUC_IND_FUNCAO_PROF.toString(), funcao));
		
		return executeCriteriaCount(criteria);
		
	}
	
	public Boolean verificaEquipeRecebeuOuCedeuSala(MbcProfAtuaUnidCirgs atuaUnidCirgs, Short seqpSala, Short unfSeq, Date data,
			MbcHorarioTurnoCirg turno) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSubstEscalaSala.class, "ssl");
		criteria.createAlias("ssl.".concat(MbcSubstEscalaSala.Fields.MBC_CARACT_SALA_ESPS.toString()), "cse");
		criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString()), "cas");
		criteria.createAlias("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString()), "sci");
		
		criteria.add(Restrictions.or(Restrictions.eq("cse."+MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), atuaUnidCirgs),
				Restrictions.eq("ssl."+MbcSubstEscalaSala.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), atuaUnidCirgs)));
		criteria.add(Restrictions.eq("ssl."+MbcSubstEscalaSala.Fields.DATA.toString(), data));
		criteria.add(Restrictions.eq("ssl."+MbcSubstEscalaSala.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci."+MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("sci."+MbcSalaCirurgica.Fields.ID_SEQP.toString(), seqpSala));
		if(turno != null) {
			criteria.add(Restrictions.eq("cas."+MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString(), turno));
		}
		
		return executeCriteriaExists(criteria);
	}

	public Long recuperarProgramacaoAgendaSalaCount(MbcSubstEscalaSala mbcSubstEscalaSala) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSubstEscalaSala.class, "ses");
		
		criteria.createAlias("ses.".concat(MbcSubstEscalaSala.Fields.MBC_CARACT_SALA_ESPS.toString()), "cse");
		criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString()), "puc", Criteria.LEFT_JOIN);
		criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.AGH_ESPECIALIDADES.toString()), "esp");
		criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString()), "cas");
		criteria.createAlias("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString()), "sci");
		criteria.createAlias("sci.".concat(MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL.toString()), "unf");		
		
		criteria.add(Restrictions.eq("ses." + MbcSubstEscalaSala.Fields.CSE_CAS_SEQ.toString(), mbcSubstEscalaSala.getMbcCaractSalaEsp().getId().getCasSeq()));
		criteria.add(Restrictions.eq("ses." + MbcSubstEscalaSala.Fields.CSE_ESP_SEQ.toString(), mbcSubstEscalaSala.getMbcCaractSalaEsp().getId().getEspSeq()));
		criteria.add(Restrictions.eq("ses." + MbcSubstEscalaSala.Fields.CSE_SEQP.toString(), mbcSubstEscalaSala.getMbcCaractSalaEsp().getId().getSeqp()));
		criteria.add(Restrictions.eq("ses." + MbcSubstEscalaSala.Fields.PUC_SER_MATRICULA.toString(), mbcSubstEscalaSala.getMbcProfAtuaUnidCirgs().getId().getSerMatricula()));
		criteria.add(Restrictions.eq("ses." + MbcSubstEscalaSala.Fields.PUC_SER_VIN_CODIGO.toString(), mbcSubstEscalaSala.getMbcProfAtuaUnidCirgs().getId().getSerVinCodigo()));
		criteria.add(Restrictions.eq("ses." + MbcSubstEscalaSala.Fields.PUC_UNF_SEQ.toString(), mbcSubstEscalaSala.getMbcProfAtuaUnidCirgs().getId().getUnfSeq()));
		criteria.add(Restrictions.eq("ses." + MbcSubstEscalaSala.Fields.PUC_IND_FUNCAO_PROF.toString(), mbcSubstEscalaSala.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf()));
		criteria.add(Restrictions.eq("ses." + MbcSubstEscalaSala.Fields.DATA.toString(), mbcSubstEscalaSala.getId().getData()));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(MbcAgendas.class, "ag");
		subCriteria.add(Restrictions.eqProperty("unf." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), "ag.".concat(MbcAgendas.Fields.SALA_CIRURGICA_UNF_SEQ.toString())));
		subCriteria.add(Restrictions.eqProperty("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), "ag.".concat(MbcAgendas.Fields.SALA_CIRURGICA_SEQP.toString())));
		subCriteria.add(Restrictions.eqProperty("ses." + MbcSubstEscalaSala.Fields.PUC_SER_MATRICULA.toString(), "ag.".concat(MbcAgendas.Fields.PUC_SER_MATRICULA.toString())));
		subCriteria.add(Restrictions.eqProperty("ses." + MbcSubstEscalaSala.Fields.PUC_SER_VIN_CODIGO.toString(), "ag.".concat(MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString())));
		subCriteria.add(Restrictions.eqProperty("ses." + MbcSubstEscalaSala.Fields.PUC_UNF_SEQ.toString(), "ag.".concat(MbcAgendas.Fields.PUC_UNF_SEQ.toString())));
		subCriteria.add(Restrictions.eqProperty("ses." + MbcSubstEscalaSala.Fields.PUC_IND_FUNCAO_PROF.toString(), "ag.".concat(MbcAgendas.Fields.PUC_IND_FUNCAO_PROF.toString())));
		subCriteria.add(Restrictions.eqProperty("ses." + MbcSubstEscalaSala.Fields.DATA.toString(), "ag.".concat(MbcAgendas.Fields.DT_AGENDA.toString())));
		subCriteria.add(Restrictions.gt("ag." + MbcAgendas.Fields.DT_AGENDA.toString(), DateUtil.truncaData(new Date())));
		subCriteria.add(Restrictions.ne("ag." + MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.CA));
		
		subCriteria.setProjection(Projections.property("ag." + MbcAgendas.Fields.SEQ.toString()));
		
		criteria.add(Subqueries.exists(subCriteria));
				
		return executeCriteriaCount(criteria);
			
	}
	
	public List<PortalPlanejamentoCirurgiasCedenciaVO> buscarCedenciasSalas(Date dataIncial, Date dataFinal, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short unfSeq, List<Short> salas) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "sci");
		criteria.createAlias("sci." + MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CARACT_SALA_ESPES.toString(), "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_SUBST_ESCALA_SALAES.toString(), "ssl");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.TURNO.toString(), "htc");
		
		criteria.setProjection(Projections
				.projectionList().add(Projections.property("sci." + MbcSalaCirurgica.Fields.SEQP.toString()), PortalPlanejamentoCirurgiasCedenciaVO.Fields.SALA.toString())
				.add(Projections.property("htc." + MbcTurnos.Fields.TURNO.toString()), PortalPlanejamentoCirurgiasCedenciaVO.Fields.TURNO.toString())
				.add(Projections.property("ssl." + MbcSubstEscalaSala.Fields.DATA.toString()), PortalPlanejamentoCirurgiasCedenciaVO.Fields.DATA.toString())
				.add(Projections.property("ssl." + MbcSubstEscalaSala.Fields.PUC_SERVIDOR.toString()), PortalPlanejamentoCirurgiasCedenciaVO.Fields.EQUIPE_RECEBE.toString()) // RECEBEU
				.add(Projections.property("cse." + MbcCaractSalaEsp.Fields.PUC_SERVIDOR.toString()), PortalPlanejamentoCirurgiasCedenciaVO.Fields.EQUIPE_ORIGINAL.toString()) // ORIGINAL
				.add(Projections.property("ssl." + MbcSubstEscalaSala.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString()), PortalPlanejamentoCirurgiasCedenciaVO.Fields.EQUIPE.toString()) // RECEBEU
				);
		
		Object[] values = { DateUtil.truncaData(dataIncial), DateUtil.truncaData(dataFinal)}; 
		Type[] types = { DateType.INSTANCE, DateType.INSTANCE};
		if(isOracle()){
			criteria.add(Restrictions.sqlRestriction("trunc(ssl3_." + MbcSubstEscalaSala.Fields.DATA.name() + ") BETWEEN ? AND ? ", values, types));
		} else {
			criteria.add(Restrictions.sqlRestriction("date_trunc('day',ssl3_." + MbcSubstEscalaSala.Fields.DATA.name() + ") BETWEEN ? AND ? ", values, types));
		}
		
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.in("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), salas));

		if(atuaUnidCirgs != null) {
			criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_SER_MATRICULA.toString(), atuaUnidCirgs.getId().getSerMatricula()));
			criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_SER_VIN_CODIGO.toString(), atuaUnidCirgs.getId().getSerVinCodigo()));
			criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_UNF_SEQ.toString(), atuaUnidCirgs.getId().getUnfSeq()));
			criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_IND_FUNCAO_PROF.toString(), atuaUnidCirgs.getId().getIndFuncaoProf()));
		}
		
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.setResultTransformer(Transformers.aliasToBean(PortalPlanejamentoCirurgiasCedenciaVO.class));
		
		return executeCriteria(criteria);
	}

	public List<PortalPlanejamentoCirurgiasCedenciaVO> buscarCedenciasProgramadasSalas(Date dataIncial, Date dataFinal, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short unfSeq, List<Short> salas) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "sci");
		criteria.createAlias("sci." + MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CEDENCIA_SALA_HCPAS.toString(), "csh");
		criteria.createAlias("csh." + MbcCedenciaSalaHcpa.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), "puc");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.TURNO.toString(), "htc");
		
		criteria.setProjection(Projections
				.projectionList().add(Projections.property("sci." + MbcSalaCirurgica.Fields.SEQP.toString()), PortalPlanejamentoCirurgiasCedenciaVO.Fields.SALA.toString())
				.add(Projections.property("htc." + MbcTurnos.Fields.TURNO.toString()), PortalPlanejamentoCirurgiasCedenciaVO.Fields.TURNO.toString())
				.add(Projections.property("csh." + MbcCedenciaSalaHcpa.Fields.DATA.toString()), PortalPlanejamentoCirurgiasCedenciaVO.Fields.DATA.toString())
				.add(Projections.property("csh." + MbcCedenciaSalaHcpa.Fields.PUC_SERVIDOR.toString()), PortalPlanejamentoCirurgiasCedenciaVO.Fields.EQUIPE_RECEBE.toString()) // RECEBEU
				.add(Projections.property("csh." + MbcCedenciaSalaHcpa.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString()), PortalPlanejamentoCirurgiasCedenciaVO.Fields.EQUIPE.toString()) // RECEBEU
				);
		
		Object[] values = { DateUtil.truncaData(dataIncial), DateUtil.truncaData(dataFinal)}; 
		Type[] types = { DateType.INSTANCE, DateType.INSTANCE};
		if(isOracle()){
			criteria.add(Restrictions.sqlRestriction("trunc(csh2_." + MbcCedenciaSalaHcpa.Fields.DATA.name() + ") BETWEEN ? AND ? ", values, types));
		} else {
			criteria.add(Restrictions.sqlRestriction("date_trunc('day',csh2_." + MbcCedenciaSalaHcpa.Fields.DATA.name() + ") BETWEEN ? AND ? ", values, types));
		}
		
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.in("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), salas));

		if(atuaUnidCirgs != null) {
			criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_SER_MATRICULA.toString(), atuaUnidCirgs.getId().getSerMatricula()));
			criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_SER_VIN_CODIGO.toString(), atuaUnidCirgs.getId().getSerVinCodigo()));
			criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_UNF_SEQ.toString(), atuaUnidCirgs.getId().getUnfSeq()));
			criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_IND_FUNCAO_PROF.toString(), atuaUnidCirgs.getId().getIndFuncaoProf()));
		}
		
		criteria.add(Restrictions.eq("puc." + MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.setResultTransformer(Transformers.aliasToBean(PortalPlanejamentoCirurgiasCedenciaVO.class));
		
		return executeCriteria(criteria);
	}

	
	public List<PortalPlanejamentoCirurgiasVO> buscarSalasTurnos(Date data, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short unfSeq, Short sciSeqp, Short espSeq) {
		Set<PortalPlanejamentoCirurgiasVO> retorno = new HashSet<PortalPlanejamentoCirurgiasVO>();
		entityManagerClear();

		retorno.addAll(buscarSalasTurnosUnion2(data, atuaUnidCirgs, unfSeq, sciSeqp, espSeq)); //CEDENCIA
		retorno.addAll(buscarSalasTurnosUnion1(data, atuaUnidCirgs, unfSeq, sciSeqp, espSeq));
		retorno.addAll(buscarSalasTurnosUnion3(data, atuaUnidCirgs, unfSeq, sciSeqp, espSeq));

		return new ArrayList<PortalPlanejamentoCirurgiasVO>(retorno);
	}

	public List<PortalPlanejamentoCirurgiasAgendamentoVO> buscarAgendamentosEquipeNaSalaPorDiaSemana(MbcProfAtuaUnidCirgs atuaUnidCirgs, Short unfSeq, Short espSeq, List<Short> salas) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "sci");
		criteria.createAlias("sci." + MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.TURNO.toString(), "htc");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CARACT_SALA_ESPES.toString(), "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.AGH_ESPECIALIDADES.toString(), "esp");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.PUC_SERVIDOR.toString(), "puc");
		criteria.createAlias("puc." + RapServidores.Fields.PESSOA_FISICA.toString(), "pef");
		
		criteria.setProjection(Projections
				.projectionList().add(Projections.groupProperty("sci." + MbcSalaCirurgica.Fields.SEQP.toString()), PortalPlanejamentoCirurgiasAgendamentoVO.Fields.SALA.toString())
				.add(Projections.groupProperty("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString()), PortalPlanejamentoCirurgiasAgendamentoVO.Fields.DIA_SEMANA.toString())
				.add(Projections.groupProperty("htc." + MbcTurnos.Fields.TURNO.toString()), PortalPlanejamentoCirurgiasAgendamentoVO.Fields.TURNO.toString())
				.add(Projections.groupProperty("pef." + RapPessoasFisicas.Fields.NOME.toString()), PortalPlanejamentoCirurgiasAgendamentoVO.Fields.EQUIPE.toString())
				.add(Projections.groupProperty("esp." + AghEspecialidades.Fields.SEQ.toString()), PortalPlanejamentoCirurgiasAgendamentoVO.Fields.ESP_SEQ.toString())
				.add(Projections.groupProperty("esp." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), PortalPlanejamentoCirurgiasAgendamentoVO.Fields.ESPECIALIDADE.toString())
				.add(Projections.groupProperty("cse." + MbcCaractSalaEsp.Fields.HORA_INICIO_EQUIPE.toString()), PortalPlanejamentoCirurgiasAgendamentoVO.Fields.DATA_INICIO.toString())
				.add(Projections.groupProperty("cse." + MbcCaractSalaEsp.Fields.HORA_FIM_EQUIPE.toString()), PortalPlanejamentoCirurgiasAgendamentoVO.Fields.DATA_FIM.toString())
				.add(Projections.groupProperty("cse." + MbcCaractSalaEsp.Fields.PUC_SER_MATRICULA.toString()), PortalPlanejamentoCirurgiasAgendamentoVO.Fields.MATRICULA.toString())
				.add(Projections.groupProperty("cse." + MbcCaractSalaEsp.Fields.PUC_SER_VIN_CODIGO.toString()), PortalPlanejamentoCirurgiasAgendamentoVO.Fields.VINCULO.toString())
				.add(Projections.groupProperty("cse." + MbcCaractSalaEsp.Fields.PUC_IND_FUNCAO_PROF.toString()), PortalPlanejamentoCirurgiasAgendamentoVO.Fields.FUNCAO_PROF.toString())
			);
		
		
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.in("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), salas));
		
		if(atuaUnidCirgs != null) {
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.PUC_SER_MATRICULA.toString(), atuaUnidCirgs.getId().getSerMatricula()));
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.PUC_SER_VIN_CODIGO.toString(), atuaUnidCirgs.getId().getSerVinCodigo()));
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.PUC_UNF_SEQ.toString(), atuaUnidCirgs.getId().getUnfSeq()));
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.PUC_IND_FUNCAO_PROF.toString(), atuaUnidCirgs.getId().getIndFuncaoProf()));
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		}
		
		if(espSeq != null) {
			criteria.add(Restrictions.eq("esp." + AghEspecialidades.Fields.SEQ.toString(), espSeq));
		}
		
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.setResultTransformer(Transformers.aliasToBean(PortalPlanejamentoCirurgiasAgendamentoVO.class));
		
		return executeCriteria(criteria);
	}

	private List<PortalPlanejamentoCirurgiasVO> buscarSalasTurnosUnion1(Date data, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short unfSeq, Short sciSeqp, Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "sci");
		criteria.createAlias("sci." + MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.TURNO.toString(), "htc");
		
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(), DominioDiaSemana.getInstance(StringUtils.stripAccents(DateUtil.dataToString(data, "EE")).toUpperCase())));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));

		if(sciSeqp != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), sciSeqp));
		}
		

		if(atuaUnidCirgs != null) {
			criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CARACT_SALA_ESPES.toString(), "cse");

			criteria.setProjection(Projections
					.projectionList().add(Projections.property("sci." + MbcSalaCirurgica.Fields.SEQP.toString()), PortalPlanejamentoCirurgiasVO.Fields.SALA.toString())
					.add(Projections.property("htc." + MbcTurnos.Fields.TURNO.toString()), PortalPlanejamentoCirurgiasVO.Fields.TURNO.toString())
					.add(Projections.property("cse." + MbcCaractSalaEsp.Fields.HORA_INICIO_EQUIPE.toString()), PortalPlanejamentoCirurgiasVO.Fields.HORA_INICIO.toString())
					.add(Projections.property("cse." + MbcCaractSalaEsp.Fields.HORA_FIM_EQUIPE.toString()),  PortalPlanejamentoCirurgiasVO.Fields.HORA_FIM.toString())
					);

			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.PUC_SER_MATRICULA.toString(), atuaUnidCirgs.getId().getSerMatricula()));
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.PUC_SER_VIN_CODIGO.toString(), atuaUnidCirgs.getId().getSerVinCodigo()));
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.PUC_UNF_SEQ.toString(), atuaUnidCirgs.getId().getUnfSeq()));
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.PUC_IND_FUNCAO_PROF.toString(), atuaUnidCirgs.getId().getIndFuncaoProf()));
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

			if(espSeq != null) {
				criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.ESP_SEQ.toString(), espSeq));
			}
		}
		else {
			criteria.setProjection(Projections
					.projectionList().add(Projections.property("sci." + MbcSalaCirurgica.Fields.SEQP.toString()), PortalPlanejamentoCirurgiasVO.Fields.SALA.toString())
					.add(Projections.property("htc." + MbcTurnos.Fields.TURNO.toString()), PortalPlanejamentoCirurgiasVO.Fields.TURNO.toString())
					);

			if(espSeq != null) {
				criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CARACT_SALA_ESPES.toString(), "cse");
				criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.ESP_SEQ.toString(), espSeq));
				
			}

		}
		
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.setResultTransformer(Transformers.aliasToBean(PortalPlanejamentoCirurgiasVO.class));
		
		return executeCriteria(criteria);
	}

	private List<PortalPlanejamentoCirurgiasVO> buscarSalasTurnosUnion2(Date data, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short unfSeq, Short sciSeqp, Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "sci");
		criteria.createAlias("sci." + MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CARACT_SALA_ESPES.toString(), "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_SUBST_ESCALA_SALAES.toString(), "ssl");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.TURNO.toString(), "htc");
		
		criteria.setProjection(Projections
				.projectionList().add(Projections.property("sci." + MbcSalaCirurgica.Fields.SEQP.toString()), PortalPlanejamentoCirurgiasVO.Fields.SALA.toString())
				.add(Projections.property("htc." + MbcTurnos.Fields.TURNO.toString()), PortalPlanejamentoCirurgiasVO.Fields.TURNO.toString())
				.add(Projections.property("cse." + MbcCaractSalaEsp.Fields.HORA_INICIO_EQUIPE.toString()), PortalPlanejamentoCirurgiasVO.Fields.HORA_INICIO.toString())
				.add(Projections.property("cse." + MbcCaractSalaEsp.Fields.HORA_FIM_EQUIPE.toString()),  PortalPlanejamentoCirurgiasVO.Fields.HORA_FIM.toString())
				.add(Projections.sqlProjection("'S' as " + PortalPlanejamentoCirurgiasVO.Fields.CEDENCIA.toString(), new String[] { PortalPlanejamentoCirurgiasVO.Fields.CEDENCIA.toString() }, new Type[] { StringType.INSTANCE }), PortalPlanejamentoCirurgiasVO.Fields.CEDENCIA.toString())
				);
		
		Object[] values = { DateUtil.truncaData(data)}; 
		Type[] types = { DateType.INSTANCE};
		if(isOracle()){
			criteria.add(Restrictions.sqlRestriction("trunc(ssl3_." + MbcSubstEscalaSala.Fields.DATA.name() + ") = ?", values, types));
		} else {
			criteria.add(Restrictions.sqlRestriction("date_trunc('day',ssl3_." + MbcSubstEscalaSala.Fields.DATA.name() + ") = ?", values, types));
		}
		
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));
		if(sciSeqp != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), sciSeqp));
		}

		if(espSeq != null) {
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.ESP_SEQ.toString(), espSeq));
		}

		if(atuaUnidCirgs != null) {
			criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_SER_MATRICULA.toString(), atuaUnidCirgs.getId().getSerMatricula()));
			criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_SER_VIN_CODIGO.toString(), atuaUnidCirgs.getId().getSerVinCodigo()));
			criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_UNF_SEQ.toString(), atuaUnidCirgs.getId().getUnfSeq()));
			criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_IND_FUNCAO_PROF.toString(), atuaUnidCirgs.getId().getIndFuncaoProf()));
		}
		
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.setResultTransformer(Transformers.aliasToBean(PortalPlanejamentoCirurgiasVO.class));
		
		return executeCriteria(criteria);
	}

	private List<PortalPlanejamentoCirurgiasVO> buscarSalasTurnosUnion3(Date data, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short unfSeq, Short sciSeqp, Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "sci");
		criteria.createAlias("sci." + MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CARACT_SALA_ESPES.toString(), "cse");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CEDENCIA_SALA_HCPAS.toString(), "csh");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.TURNO.toString(), "htc");
		
		criteria.setProjection(Projections
				.projectionList().add(Projections.property("sci." + MbcSalaCirurgica.Fields.SEQP.toString()), PortalPlanejamentoCirurgiasVO.Fields.SALA.toString())
				.add(Projections.property("htc." + MbcTurnos.Fields.TURNO.toString()), PortalPlanejamentoCirurgiasVO.Fields.TURNO.toString())
				.add(Projections.property("cse." + MbcCaractSalaEsp.Fields.HORA_INICIO_EQUIPE.toString()), PortalPlanejamentoCirurgiasVO.Fields.HORA_INICIO.toString())
				.add(Projections.property("cse." + MbcCaractSalaEsp.Fields.HORA_FIM_EQUIPE.toString()),  PortalPlanejamentoCirurgiasVO.Fields.HORA_FIM.toString())
				);
		
		Object[] values = { DateUtil.truncaData(data)}; 
		Type[] types = { DateType.INSTANCE};
		if(isOracle()){
			criteria.add(Restrictions.sqlRestriction("trunc(csh3_." + MbcCedenciaSalaHcpa.Fields.DATA.name() + ") >= ?", values, types));
		} else {
			criteria.add(Restrictions.sqlRestriction("date_trunc('day',csh3_." + MbcCedenciaSalaHcpa.Fields.DATA.name() + ") >= ?", values, types));
		}
		
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));

		if(sciSeqp != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), sciSeqp));
		}
		
		if(espSeq != null) {
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.ESP_SEQ.toString(), espSeq));
		}

		if(atuaUnidCirgs != null) {
			criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_SER_MATRICULA.toString(), atuaUnidCirgs.getId().getSerMatricula()));
			criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_SER_VIN_CODIGO.toString(), atuaUnidCirgs.getId().getSerVinCodigo()));
			criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_UNF_SEQ.toString(), atuaUnidCirgs.getId().getUnfSeq()));
			criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_IND_FUNCAO_PROF.toString(), atuaUnidCirgs.getId().getIndFuncaoProf()));
		}
		
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.setResultTransformer(Transformers.aliasToBean(PortalPlanejamentoCirurgiasVO.class));
		
		return executeCriteria(criteria);
	}

	public List<PortalPlanejamentoCirurgiasAgendaVO> buscarAgendasPorUnfSeqListaSalasDatas(	List<Short> salas,	Short unidade, Date dataInicio, Date dataFim, Short espSeq, RapServidoresId equipe) {
		StringBuffer sbQuery = new StringBuffer(2500);
		
				sbQuery.append("	SELECT agd.seq, agd.sci_seqp sala, htc.turno, agd.dt_agenda dataAgenda, to_char(agd.dthr_prev_inicio,'dd/mm/yyyy hh24mi') inicioAgenda,  " ).
				 append(" to_char(agd.dthr_prev_fim,'dd/mm/yyyy hh24mi') fimAgenda, to_char(agd.tempo_sala,'hh24mi') tempoSala, intervalo_escala intervaloEscala,").
				 append(" esp.NOME_ESPECIALIDADE especialidade, pci.descricao procedimento, pef.nome equipe, pac.prontuario prontuario, pac.nome nome, agd.DTHR_INCLUSAO criadoEm,"
				 		+ " pefAgd.nome criadoPor, agd.regime, agd.ind_gerado_sistema geradoSistema ")
				 .append("FROM agh.mbc_agendas agd ")
				 .append("JOIN agh.MBC_PROCEDIMENTO_CIRURGICOS pci ON (pci.seq = agd.EPR_PCI_SEQ) ")
				 .append("JOIN agh.mbc_horario_turno_cirgs htc ON (agd.unf_seq = htc.unf_seq) ")
				 .append("JOIN agh.aip_pacientes pac ON (agd.pac_codigo = pac.codigo) ")
				 .append("JOIN agh.agh_especialidades esp ON (agd.ESP_SEQ = esp.seq) ")
				 .append("LEFT JOIN agh.MBC_PROF_ATUA_UNID_CIRGS prof ON ( agd.PUC_SER_MATRICULA  = prof.SER_MATRICULA AND agd.PUC_SER_VIN_CODIGO  = prof.SER_VIN_CODIGO ")
				 .append("AND agd.PUC_UNF_SEQ         = prof.UNF_SEQ AND agd.PUC_IND_FUNCAO_PROF = prof.IND_FUNCAO_PROF ) ")
				 .append("LEFT JOIN agh.RAP_SERVIDORES rap ON (prof.SER_MATRICULA  = rap.matricula AND prof.SER_VIN_CODIGO = rap.vin_codigo) ")
				 .append("LEFT JOIN agh.RAP_PESSOAS_FISICAS pef ON (rap.pes_codigo = pef.codigo) ")
				 .append("LEFT JOIN agh.RAP_SERVIDORES rapAgd ON (agd.SER_MATRICULA  = rapAgd.matricula AND agd.SER_VIN_CODIGO = rapAgd.vin_codigo) ")
				 .append("LEFT JOIN agh.RAP_PESSOAS_FISICAS pefAgd ON (rapAgd.pes_codigo = pefAgd.codigo) ")
				 .append("WHERE ").append(	"	agd.dt_agenda BETWEEN " ).append( getSqlData(DateUtil.truncaData(dataInicio))).append(" AND ").append( getSqlData(DateUtil.truncaDataFim(dataFim)))
			.append(	"	and ((agd.dthr_prev_inicio is null and agd.ordem_overbooking is not null) ");
			if(isOracle()){
				StringBuilder horarioTurnoFinal = new StringBuilder("case to_number(to_char(htc.horario_final,'hh24mi')) when 0 then to_number('2359') else to_number(to_char(htc.horario_final,'hh24mi')) end");
				sbQuery.append(	"	or (to_number(to_char(agd.dthr_prev_inicio,'hh24mi')) >= to_number(to_char(htc.horario_inicial,'hh24mi')) and to_number(to_char(agd.dthr_prev_inicio,'hh24mi')) < " ).append( horarioTurnoFinal ).append(") ");
				sbQuery.append(	"	or (to_number(to_char(agd.dthr_prev_inicio,'hh24mi')) <= to_number(to_char(htc.horario_inicial,'hh24mi')) and to_number(to_char(agd.dthr_prev_fim,'hh24mi')) > to_number(to_char(htc.horario_inicial,'hh24mi'))))  ");
			}else{
				StringBuilder horarioTurnoFinalPg = new StringBuilder("case to_number(to_char(htc.horario_final,'hh24mi'), '9999') when 0 then to_number('2359', '9999') else to_number(to_char(htc.horario_final,'hh24mi'), '9999') end");
				sbQuery.append(	"	or (to_number(to_char(agd.dthr_prev_inicio,'hh24mi'),'9999') >= to_number(to_char(htc.horario_inicial,'hh24mi'),'9999')")
				.append(	"	and to_number(to_char(agd.dthr_prev_inicio,'hh24mi'),'9999') < " ).append( horarioTurnoFinalPg ).append(')')
				.append(	"	or (to_number(to_char(agd.dthr_prev_inicio,'hh24mi'),'9999') <= to_number(to_char(htc.horario_inicial,'hh24mi'),'9999')")
				.append(	"	and to_number(to_char(agd.dthr_prev_fim,'hh24mi'),'9999') > to_number(to_char(htc.horario_inicial,'hh24mi'),'9999')))  ");
			}
			
			sbQuery.append(	"	AND 	agd.ind_exclusao 	= 'N' ")
			.append(	"	AND 	agd.ind_situacao in ('AG','ES') ")
			.append(	espSeq != null ? (("	AND 	esp.seq = ") + espSeq + " ") : "")
			.append(	equipe != null ? (("	AND 	agd.PUC_SER_MATRICULA  = ") + equipe.getMatricula() + (" AND agd.PUC_SER_VIN_CODIGO  = ") + equipe.getVinCodigo() + " ") : "")
			.append(	"	AND 	agd.unf_seq 	= " ).append( unidade).append(' ')
			.append(	"	AND 	agd.sci_seqp 	IN (" ).append( StringUtils.join(salas.toArray(), ",")).append(')')
			.append(	"	AND 	htc.unf_seq 	= " ).append( unidade)
			.append(	"	group by agd.seq, agd.sci_seqp, htc.turno, agd.dt_agenda, esp.NOME_ESPECIALIDADE, pci.descricao, pef.nome, pac.prontuario, pac.nome, agd.dthr_prev_inicio, "
					+ " agd.dthr_prev_fim, agd.tempo_sala, intervalo_escala, agd.DTHR_INCLUSAO, pefAgd.nome, agd.regime, agd.ind_gerado_sistema ")
			.append(	"	order by agd.sci_seqp, agd.dt_agenda, agd.dthr_prev_inicio, htc.turno ");
			return createSQLQuery(sbQuery.toString()).addScalar("especialidade", StringType.INSTANCE).addScalar("procedimento", StringType.INSTANCE)
					.addScalar("equipe", StringType.INSTANCE).addScalar("seq", IntegerType.INSTANCE)
					.addScalar("prontuario", IntegerType.INSTANCE).addScalar("nome", StringType.INSTANCE)
					.addScalar("criadoPor", StringType.INSTANCE).addScalar("criadoEm", TimestampType.INSTANCE)
					.addScalar("regime", StringType.INSTANCE).addScalar("sala", ShortType.INSTANCE).addScalar("turno", StringType.INSTANCE)
					.addScalar("dataAgenda", DateType.INSTANCE).addScalar("inicioAgenda", StringType.INSTANCE)
					.addScalar("fimAgenda", StringType.INSTANCE).addScalar("tempoSala", StringType.INSTANCE)
					.addScalar("intervaloEscala", ByteType.INSTANCE).addScalar("geradoSistema", StringType.INSTANCE)
					.setResultTransformer(Transformers.aliasToBean(PortalPlanejamentoCirurgiasAgendaVO.class)).list();
	}

	public List<PortalPlanejamentoCirurgiasAgendaVO> buscarAgendasCirurgiasRealizadasPorUnfSeqListaSalasDatas(List<Integer> agendas, Short unidade) {
		StringBuffer sbQuery = new StringBuffer(2500);
		
				sbQuery.append("	SELECT cir.agd_seq seq, cir.dthr_entrada_sala entradaSala, cir.dthr_saida_sala saidaSala, cir.dthr_inicio_cirg inicioCirurgia, cir.dthr_fim_cirg fimCirurgia " )
				 .append("FROM agh.mbc_cirurgias cir ")
				 .append("WHERE ");
			sbQuery.append(	"	cir.agd_seq IN (" ).append( StringUtils.join(agendas.toArray(), ",")).append(')')
			.append(	"	group by cir.agd_seq, cir.dthr_entrada_sala, cir.dthr_saida_sala, cir.dthr_inicio_cirg, cir.dthr_fim_cirg, cir.situacao ")
			.append(	"	HAVING cir.situacao = '").append(DominioSituacaoCirurgia.RZDA.toString()).append('\'');
			return createSQLQuery(sbQuery.toString())
					.addScalar("seq", IntegerType.INSTANCE)
					.addScalar("entradaSala", TimestampType.INSTANCE)
					.addScalar("saidaSala", TimestampType.INSTANCE)
					.addScalar("inicioCirurgia", TimestampType.INSTANCE)
					.addScalar("fimCirurgia", TimestampType.INSTANCE)
					.setResultTransformer(Transformers.aliasToBean(PortalPlanejamentoCirurgiasAgendaVO.class)).list();
	}

	public List<PortalPlanejamentoCirurgiasAgendaVO> buscarAgendasDescCirurgiasRealizadasPorUnfSeqListaSalasDatas(List<Integer> agendas, Short unidade) {
		StringBuffer sbQuery = new StringBuffer(2500);
		
				sbQuery.append("	SELECT cir.agd_seq seq, MIN(descr.dthr_inicio_cirg) inicioCirurgia, MAX(descr.dthr_fim_cirg) fimCirurgia " )
				 .append("FROM agh.mbc_cirurgias cir  ")
				 .append("JOIN agh.mbc_descricao_itens descr on (descr.DCG_CRG_SEQ = cir.seq) ")
				 .append("JOIN agh.mbc_descricao_cirurgicas dcg on (descr.DCG_CRG_SEQ = dcg.CRG_SEQ and descr.DCG_SEQP = dcg.seqp) ")
				 .append("WHERE ");
			sbQuery.append(	" cir.agd_seq	IN (" ).append( StringUtils.join(agendas.toArray(), ",")).append(')')
			.append(	"	AND 	descr.dthr_inicio_cirg 	>= " ).append( getSqlData(DateUtil.truncaData(DateUtil.obterData(2004, 0, 1))))
			.append(	"	group by cir.agd_seq, dcg.situacao, cir.situacao ")
			.append(	"	HAVING cir.situacao = '").append(DominioSituacaoCirurgia.RZDA.toString()).append('\'')
			.append(	"	and dcg.situacao = '").append(DominioSituacaoDescricaoCirurgia.CON.toString()).append('\'');
			return createSQLQuery(sbQuery.toString())
					.addScalar("seq", IntegerType.INSTANCE)
					.addScalar("inicioCirurgia", TimestampType.INSTANCE)
					.addScalar("fimCirurgia", TimestampType.INSTANCE)
					.setResultTransformer(Transformers.aliasToBean(PortalPlanejamentoCirurgiasAgendaVO.class)).list();
	}

	private static String getSqlData(Date data){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		final String dataFormatada = dateFormat.format(data);
		return "TO_DATE('" + dataFormatada + "', 'dd/mm/yyyy hh24:mi:ss')";
	}

	public List<PortalPlanejamentoCirurgiasAgendaVO> buscarAgendasCirurgiasPlanejadasPorUnfSeqListaSalasDatas(List<Integer> agendas, Short unidade) {
		StringBuffer sbQuery = new StringBuffer(2500);
		
				sbQuery.append("	SELECT cir.agd_seq seq " )
				 .append("FROM agh.mbc_cirurgias cir ")
				 .append("WHERE ");
			sbQuery.append(	" cir.agd_seq	IN (" ).append( StringUtils.join(agendas.toArray(), ",")).append(')')
			.append(	" AND cir.mtc_seq is null ")
			.append(	"	group by cir.agd_seq ");
			return createSQLQuery(sbQuery.toString())
					.addScalar("seq", IntegerType.INSTANCE)
					.setResultTransformer(Transformers.aliasToBean(PortalPlanejamentoCirurgiasAgendaVO.class)).list();
	}
	
	public List<PortalPlanejamentoCirurgiasAgendaVO> buscarAgendasPorUnfSeqSemSala(	Short unidade, Date dataInicio, Date dataFim, Short espSeq, RapServidoresId equipe) {
		StringBuffer sbQuery = new StringBuffer(2500);
		
				sbQuery.append("	SELECT agd.seq, agd.dt_agenda dtAgenda,  " ).
				 append(" to_char(agd.tempo_sala,'hh24mi') tempoSala, intervalo_escala intervaloEscala,").
				 append(" esp.NOME_ESPECIALIDADE especialidade, pci.descricao procedimento, pef.nome equipe, pac.prontuario prontuario, pac.nome nome, agd.DTHR_INCLUSAO criadoEm,"
				 		+ " pefAgd.nome criadoPor, agd.regime, agd.ind_gerado_sistema geradoSistema ")
				 .append("FROM agh.mbc_agendas agd ")
				 .append("JOIN agh.MBC_PROCEDIMENTO_CIRURGICOS pci ON (pci.seq = agd.EPR_PCI_SEQ) ")
				 .append("JOIN agh.aip_pacientes pac ON (agd.pac_codigo = pac.codigo) ")
				 .append("JOIN agh.agh_especialidades esp ON (agd.ESP_SEQ = esp.seq) ")
				 .append("LEFT JOIN agh.MBC_PROF_ATUA_UNID_CIRGS prof ON ( agd.PUC_SER_MATRICULA  = prof.SER_MATRICULA AND agd.PUC_SER_VIN_CODIGO  = prof.SER_VIN_CODIGO ")
				 .append("AND agd.PUC_UNF_SEQ         = prof.UNF_SEQ AND agd.PUC_IND_FUNCAO_PROF = prof.IND_FUNCAO_PROF ) ")
				 .append("LEFT JOIN agh.RAP_SERVIDORES rap ON (prof.SER_MATRICULA  = rap.matricula AND prof.SER_VIN_CODIGO = rap.vin_codigo) ")
				 .append("LEFT JOIN agh.RAP_PESSOAS_FISICAS pef ON (rap.pes_codigo = pef.codigo) ")
				 .append("LEFT JOIN agh.RAP_SERVIDORES rapAgd ON (agd.SER_MATRICULA  = rapAgd.matricula AND agd.SER_VIN_CODIGO = rapAgd.vin_codigo) ")
				 .append("LEFT JOIN agh.RAP_PESSOAS_FISICAS pefAgd ON (rapAgd.pes_codigo = pefAgd.codigo) ")
				 .append("WHERE ").append(	"	agd.dt_agenda BETWEEN " ).append( getSqlData(DateUtil.truncaData(dataInicio))).append(" AND ").append( getSqlData(DateUtil.truncaDataFim(dataFim)))
				 .append(	"	and agd.dthr_prev_inicio is null and agd.dthr_prev_fim is null")
			.append(	espSeq != null ? (("	AND 	esp.seq = ") + espSeq + " ") : "")
			.append(	equipe != null ? (("	AND 	agd.PUC_SER_MATRICULA  = ") + equipe.getMatricula() + (" AND agd.PUC_SER_VIN_CODIGO  = ") + equipe.getVinCodigo() + " ") : "")
			.append(	"	AND 	agd.unf_seq 	= " ).append( unidade).append(' ')
			.append(	"	AND 	agd.ind_exclusao 	= 'N' " ).append(' ')
			.append(	"	AND 	agd.sci_seqp IS NULL	")
			.append(	"	AND 	agd.unf_seq 	= " ).append( unidade)
			.append(	"	group by agd.seq, agd.dt_agenda, esp.NOME_ESPECIALIDADE, pci.descricao, pef.nome, pac.prontuario, pac.nome, "
					+ "  agd.tempo_sala, intervalo_escala, agd.DTHR_INCLUSAO, pefAgd.nome, agd.regime, agd.ind_gerado_sistema ")
			.append(	"	order by agd.dt_agenda");
			return createSQLQuery(sbQuery.toString()).addScalar("especialidade", StringType.INSTANCE).addScalar("procedimento", StringType.INSTANCE)
					.addScalar("equipe", StringType.INSTANCE).addScalar("seq", IntegerType.INSTANCE)
					.addScalar("prontuario", IntegerType.INSTANCE).addScalar("nome", StringType.INSTANCE)
					.addScalar("criadoPor", StringType.INSTANCE).addScalar("criadoEm", TimestampType.INSTANCE)
					.addScalar("regime", StringType.INSTANCE)
					.addScalar("dtAgenda", DateType.INSTANCE).addScalar("tempoSala", StringType.INSTANCE)
					.addScalar("intervaloEscala", ByteType.INSTANCE).addScalar("geradoSistema", StringType.INSTANCE)
					.setResultTransformer(Transformers.aliasToBean(PortalPlanejamentoCirurgiasAgendaVO.class)).list();
	}

}