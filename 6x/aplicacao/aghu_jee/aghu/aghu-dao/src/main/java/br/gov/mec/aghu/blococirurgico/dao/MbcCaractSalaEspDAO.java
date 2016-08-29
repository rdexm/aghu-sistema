package br.gov.mec.aghu.blococirurgico.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcBloqSalaCirurgica;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaractSalaEspId;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcCedenciaSalaHcpa;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSubstEscalaSala;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MbcCaractSalaEspDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcCaractSalaEsp> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4292028579151110753L;
	
	public Short buscarProximoSeqp(Short casSeq, Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		
		criteria.setProjection(Projections.max(MbcCaractSalaEsp.Fields.SEQP.toString()));
		
		criteria.add(Restrictions.eq("cse.".concat(MbcCaractSalaEsp.Fields.CAS_SEQ.toString()), casSeq));
		criteria.add(Restrictions.eq("cse.".concat(MbcCaractSalaEsp.Fields.ESP_SEQ.toString()), espSeq));
		
		Short seqp = (Short) executeCriteriaUniqueResult(criteria);
		if(seqp == null) {
			seqp = 0;
		}
		return ++seqp; 
	}

	public List<MbcCaractSalaEsp> pesquisarCaractSalaEspPorUnidadeSalaTurnoDiaSemana(Short unidade, Short sala, MbcTurnos turno, DominioDiaSemana diaSemana){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString()), "cas", CriteriaSpecification.LEFT_JOIN);
		criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString()), "puc", CriteriaSpecification.LEFT_JOIN);
		criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.AGH_ESPECIALIDADES.toString()), "esp", CriteriaSpecification.LEFT_JOIN);
		criteria.createAlias("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString()), "csi");
		
		criteria.add(Restrictions.eq("csi.".concat(MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString()), unidade));
		criteria.add(Restrictions.eq("csi.".concat(MbcSalaCirurgica.Fields.ID_SEQP.toString()), sala));
		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.TURNO.toString()), turno));
		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString()), diaSemana));
		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString()), Boolean.TRUE));
		criteria.add(Restrictions.eq("csi.".concat(MbcSalaCirurgica.Fields.SITUACAO.toString()), DominioSituacao.A));
		criteria.add(Restrictions.eq("cse.".concat(MbcCaractSalaEsp.Fields.IND_SITUACAO.toString()), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcCaractSalaEsp> pesquisarCaractSalaEspPorCaracProfAtuaUniCirur(Short caracteristica, MbcProfAtuaUnidCirgs profAtuaUnidCirgs){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString()), "cas", CriteriaSpecification.LEFT_JOIN);
		criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString()), "puc", CriteriaSpecification.LEFT_JOIN);
		
		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.SEQ.toString()), caracteristica));
		criteria.add(Restrictions.eq("puc.".concat(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString()), profAtuaUnidCirgs.getId().getSerMatricula()));
		criteria.add(Restrictions.eq("puc.".concat(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString()), profAtuaUnidCirgs.getId().getSerVinCodigo()));
		criteria.add(Restrictions.eq("puc.".concat(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString()), profAtuaUnidCirgs.getId().getUnfSeq()));

		criteria.add(Restrictions.eq("cse.".concat(MbcCaractSalaEsp.Fields.IND_SITUACAO.toString()), DominioSituacao.A));

		return executeCriteria(criteria);
	}
	
	public List<MbcCaractSalaEsp> pesquisarCaractSalaEspPorHorarioTurnoCirg(Short unfSeq, MbcTurnos turno) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString()), "cas");
		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.TURNO.toString()), turno));
		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS_ID_UNF_SEQ.toString()), unfSeq));
		criteria.add(Restrictions.isNotNull("cse.".concat(MbcCaractSalaEsp.Fields.HORA_INICIO_EQUIPE.toString())));
		criteria.add(Restrictions.isNotNull("cse.".concat(MbcCaractSalaEsp.Fields.HORA_FIM_EQUIPE.toString())));
		criteria.add(Restrictions.eq("cse.".concat(MbcCaractSalaEsp.Fields.IND_SITUACAO.toString()), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcCaractSalaEsp> pesquisarCaractSalaEspPorCaractSalaCirg(Short casSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		criteria.add(Restrictions.eq("cse.".concat(MbcCaractSalaEsp.Fields.SEQ_MBC_CARACTERISTICA_SALA_CIRGS.toString()), casSeq));
		criteria.add(Restrictions.isNotNull("cse.".concat(MbcCaractSalaEsp.Fields.HORA_INICIO_EQUIPE.toString())));
		criteria.add(Restrictions.isNotNull("cse.".concat(MbcCaractSalaEsp.Fields.HORA_FIM_EQUIPE.toString())));
		criteria.add(Restrictions.eq("cse.".concat(MbcCaractSalaEsp.Fields.IND_SITUACAO.toString()), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcCaractSalaEsp> pesquisarOutrasEspecialidadesAtivasPorCaract(MbcCaractSalaEsp caractSalaEsp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		criteria.add(Restrictions.eq("cse.".concat(MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString()), caractSalaEsp.getMbcCaracteristicaSalaCirg()));
		criteria.add(Restrictions.eq("cse.".concat(MbcCaractSalaEsp.Fields.IND_SITUACAO.toString()), DominioSituacao.A));
		if(caractSalaEsp.getId() != null) {
			criteria.add(Restrictions.or(Restrictions.ne("cse.".concat(MbcCaractSalaEsp.Fields.SEQP.toString()), caractSalaEsp.getId().getSeqp()),
					Restrictions.ne("cse.".concat(MbcCaractSalaEsp.Fields.ESP_SEQ.toString()), caractSalaEsp.getId().getEspSeq())));
		}
		
		return executeCriteria(criteria);
	}
	
	public Long pesquisarQtdeCaractSalaEspAtivaPorCaractSalaCirg(Short casSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		criteria.add(Restrictions.eq("cse.".concat(MbcCaractSalaEsp.Fields.SEQ_MBC_CARACTERISTICA_SALA_CIRGS.toString()), casSeq));
		criteria.add(Restrictions.eq("cse.".concat(MbcCaractSalaEsp.Fields.IND_SITUACAO.toString()), DominioSituacao.A));
		
		return executeCriteriaCount(criteria);
	}
	
	public List<MbcCaractSalaEsp> buscarOutrosHorariosCaractSala(MbcCaracteristicaSalaCirg carac, Short seqp, Date horaInicio, Date horaFim) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		
		criteria.add(Restrictions.eq("cse.".concat(MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString()), carac));
		criteria.add(Restrictions.isNotNull("cse.".concat(MbcCaractSalaEsp.Fields.HORA_INICIO_EQUIPE.toString())));
		criteria.add(Restrictions.ne("cse.".concat(MbcCaractSalaEsp.Fields.SEQP.toString()), seqp));
		
		SimpleDateFormat formatador = new SimpleDateFormat("HHmm");
		String horaFormatada = formatador.format(horaInicio) + formatador.format(horaFim);
		
		criteria.add(Restrictions.sqlRestriction("(TO_CHAR({alias}.hora_inicio_equipe,'hh24mi')||TO_CHAR({alias}.hora_fim_equipe,'hh24mi') <> " +
				" ?)", horaFormatada, StringType.INSTANCE));
		
		criteria.add(Restrictions.eq("cse.".concat(MbcCaractSalaEsp.Fields.IND_SITUACAO.toString()), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcCaractSalaEsp> pesquisarCaractSalaEspPorCaracteristica(Short casSeq, DominioSituacao situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		
		criteria.add(Restrictions.eq("cse.".concat(MbcCaractSalaEsp.Fields.SEQ_MBC_CARACTERISTICA_SALA_CIRGS.toString()), casSeq));
		if (situacao != null) {
			criteria.add(Restrictions.eq("cse.".concat(MbcCaractSalaEsp.Fields.IND_SITUACAO.toString()), situacao));
		}
		
		criteria.addOrder(Order.asc(MbcCaractSalaEsp.Fields.IND_SITUACAO.toString()));
		
		//Sem cache #27510
		return executeCriteria(criteria);
	}
	
	
	public List<Object[]> pesquisarSiglaEspETurnoPorUnidadeSalaTurnoDiaSemana(
			Short unidade, Short sala, DominioSituacao situacaoSalaCirurgica,
			DominioSituacao situacaoCaracSalaEsp,
			String turno, DominioDiaSemana diaSemana, Boolean indDisponicelMbcCaracSalaCir, 
			String siglaEspecialidade, DominioSituacao situacaoTurno, DominioSituacao situacaoMbcCaractSalaCirg) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		
		addJoinCriteria(Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, criteria);
		
		//criteria.createAlias("cas.".concat(MbcCaracteristicaSalaCirg.Fields.TURNO.toString()), "turno");
		
		addRestrictionsCriteria(unidade, sala, situacaoSalaCirurgica,
				situacaoCaracSalaEsp, turno, diaSemana,
				indDisponicelMbcCaracSalaCir, criteria, siglaEspecialidade, situacaoTurno, situacaoMbcCaractSalaCirg);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(
						Projections.property("esp." + AghEspecialidades.Fields.SIGLA.toString())))
						.add(Projections.property("turno." + MbcTurnos.Fields.TURNO.toString()))
						.add(Projections.property("csi." + MbcSalaCirurgica.Fields.SEQP.toString())));
		
		criteria.addOrder(Order.asc("csi." + MbcSalaCirurgica.Fields.SEQP.toString()));
		criteria.addOrder(Order.asc("esp." + AghEspecialidades.Fields.SIGLA.toString()));
		
		//criteria.setResultTransformer(Transformers.aliasToBean(LinhaReportVO.class));
		
		return executeCriteria(criteria);
	}

	private void addJoinCriteria(Boolean mbcCaracteristica,
			Boolean mbcProfAtuaUnidCirgs, Boolean aghEspecialiade,
			Boolean mbcSalaCirurgica, Boolean mbcTurno, Boolean mbcHorarioTurnoCirgs, DetachedCriteria criteria) {
		if(mbcCaracteristica){
			criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString()), "cas");
		}
		if(mbcProfAtuaUnidCirgs){
			criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString()), "puc");
		}
		if(aghEspecialiade){
			criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.AGH_ESPECIALIDADES.toString()), "esp");
		}
		if(mbcSalaCirurgica){
			criteria.createAlias("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString()), "csi");
		}
		if(mbcTurno){
			criteria.createAlias("cas."+MbcCaracteristicaSalaCirg.Fields.TURNO.toString(), "turno");
		}
		
		if(mbcHorarioTurnoCirgs){
			criteria.createAlias("cas."+MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString(), "htc");
		}
	}

	private void addRestrictionsCriteria(Short unidade, Short sala,
			DominioSituacao situacaoSalaCirurgica,
			DominioSituacao situacaoCaracSalaEsp,
			String turno, DominioDiaSemana diaSemana,
			Boolean indDisponicelMbcCaracSalaCir, DetachedCriteria criteria, 
			String siglaEspecialidade, DominioSituacao situacaoTurno, DominioSituacao situacaoMbcCaractSalaCirg) {
		
		if(unidade != null){
			criteria.add(Restrictions.eq("csi.".concat(MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString()), unidade));
		}
		
		if(sala != null){
			criteria.add(Restrictions.eq("csi.".concat(MbcSalaCirurgica.Fields.ID_SEQP.toString()), sala));
		}
		
		if(turno != null){
			criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS_ID_TURNO.toString()), turno));
		}
		
		if(diaSemana != null){
			criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString()), diaSemana));
		}
		
		addRestrictionsCriteria(situacaoSalaCirurgica, situacaoCaracSalaEsp,
				indDisponicelMbcCaracSalaCir, criteria, siglaEspecialidade,
				situacaoTurno, situacaoMbcCaractSalaCirg);
		
	}

	private void addRestrictionsCriteria(DominioSituacao situacaoSalaCirurgica,
			DominioSituacao situacaoCaracSalaEsp,
			Boolean indDisponicelMbcCaracSalaCir, DetachedCriteria criteria,
			String siglaEspecialidade, DominioSituacao situacaoTurno,
			DominioSituacao situacaoMbcCaractSalaCirg) {
		if(indDisponicelMbcCaracSalaCir != null){
			criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString()), indDisponicelMbcCaracSalaCir));
		}
		
		if(situacaoSalaCirurgica != null){
			criteria.add(Restrictions.eq("csi.".concat(MbcSalaCirurgica.Fields.SITUACAO.toString()), situacaoSalaCirurgica));
		}
		
		if(situacaoCaracSalaEsp != null){
			criteria.add(Restrictions.eq("cse.".concat(MbcCaractSalaEsp.Fields.IND_SITUACAO.toString()), situacaoCaracSalaEsp));
		}
		
		if(siglaEspecialidade != null){
			criteria.add(Restrictions.eq("esp.".concat(AghEspecialidades.Fields.SIGLA.toString()), siglaEspecialidade));
		}
		
		if(situacaoTurno != null){
			criteria.add(Restrictions.eq("turno.".concat(MbcTurnos.Fields.SITUACAO.toString()), situacaoTurno));
		}
		
		if(situacaoMbcCaractSalaCirg != null){
			criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString()), situacaoMbcCaractSalaCirg));
		}
	}
	
	/**
	 * Pesquisa característica sala especialidade ativas para profissionais cirurgicos
	 * 
	 * @param casSeq
	 * @param matricula
	 * @param vinCodigo
	 * @param unfSeq
	 * @return
	 */
	public List<MbcCaractSalaEsp> pesquisarCaractSalaEspAtivasProfissionalCirurgicoUnidade(Short casSeq, Integer matricula, Short vinCodigo, Short unfSeq) {

		if (casSeq == null || matricula == null || vinCodigo == null || unfSeq == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório não informado.");
		}

		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class);

		criteria.createCriteria(MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), "PROF", DetachedCriteria.INNER_JOIN);

		criteria.add(Restrictions.eq(MbcCaractSalaEsp.Fields.CAS_SEQ.toString(), casSeq));

		criteria.add(Restrictions.eq("PROF.".concat(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString()), matricula));
		criteria.add(Restrictions.eq("PROF.".concat(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString()), vinCodigo));
		criteria.add(Restrictions.eq("PROF.".concat(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString()), unfSeq));

		criteria.add(Restrictions.eq(MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		return executeCriteria(criteria);
	}
	
	/**
	 * Pesquisa característica sala especialidade ativas para profissionais cirurgicos
	 * 
	 * @param casSeq
	 * @param matricula
	 * @param vinCodigo
	 * @param unfSeq
	 * @return
	 */
	public List<AghEspecialidades> pesquisarCaractSalaEspAtivasProfAtuaUnidCirgs(MbcProfAtuaUnidCirgs mbc) {

		if (mbc == null || mbc.getRapServidores().getId().getMatricula() == null 
							|| mbc.getRapServidores().getId().getVinCodigo() == null
								|| mbc.getUnidadeFuncional().getSeq() == null 
									|| mbc.getIndFuncaoProf() == null ) {
			throw new IllegalArgumentException("Parâmetro obrigatório não informado.");
		}

		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class);

		criteria.createCriteria(MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), "PROF", JoinType.INNER_JOIN);
		criteria.createCriteria(MbcCaractSalaEsp.Fields.AGH_ESPECIALIDADES.toString(), "ESP", JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq("PROF.".concat(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString()), mbc.getRapServidores().getId().getMatricula()));
		criteria.add(Restrictions.eq("PROF.".concat(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString()), mbc.getRapServidores().getId().getVinCodigo()));
		criteria.add(Restrictions.eq("PROF.".concat(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString()), mbc.getUnidadeFuncional().getSeq()));
		criteria.add(Restrictions.eq("PROF.".concat(MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString()), mbc.getIndFuncaoProf()));

		criteria.add(Restrictions.eq(MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("ESP." + AghEspecialidades.Fields.SIGLA.toString()), AghEspecialidades.Fields.SIGLA.toString())
				.add(Projections.property("ESP." + AghEspecialidades.Fields.SEQ.toString()), AghEspecialidades.Fields.SEQ.toString());
			criteria.setProjection(Projections.distinct(projection));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AghEspecialidades.class));
		return executeCriteria(criteria);
	}

	public List<MbcCaractSalaEsp> pesquisarCaractSalaEsp(MbcBloqSalaCirurgica bloqSalaCirurgica){
			final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
			
			criteria.createCriteria(MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), "cas");
			
			criteria.add(Restrictions.eq(MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			
			if(bloqSalaCirurgica.getDiaSemana() != null) {
				criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString()), bloqSalaCirurgica.getDiaSemana()));
			}
			if(bloqSalaCirurgica.getTurno() != null) {
				criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS_ID_TURNO.toString()), bloqSalaCirurgica.getTurno().getTurno()));
			}
			
			return executeCriteria(criteria);
	}

	public List<MbcCaractSalaEsp> listarMbcCaractSalaEspPorDiaSemana(String pesquisa, DominioDiaSemana diaSemana){
		DetachedCriteria criteria = montaCriteriaMbcCaractSalaEspPorDiaSemana(pesquisa, diaSemana);
	     	criteria.addOrder(Order.asc("unf.".concat(AghUnidadesFuncionais.Fields.SIGLA.toString())));
	        criteria.addOrder(Order.asc("sci.".concat(MbcSalaCirurgica.Fields.SEQP.toString())));
	        criteria.addOrder(Order.asc("cas.".concat(MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString())));
	        criteria.addOrder(Order.asc("cas.".concat(MbcCaracteristicaSalaCirg.Fields.TURNO.toString())));
	        criteria.addOrder(Order.asc("esp.".concat(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString())));
		return executeCriteria(criteria);
	}

    private DetachedCriteria montaCriteriaMbcCaractSalaEspPorDiaSemana(String pesquisa, DominioDiaSemana diaSemana) {
        DetachedCriteria criteria = criarCriteriaListarMbcCaractSalaEspView();
        criteria.createAlias("cas.".concat(MbcCaracteristicaSalaCirg.Fields.TURNO.toString()), "tur");

        criteria.add(Restrictions.isNotNull("puc.".concat(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString())));
        criteria.add(Restrictions.isNotNull("puc.".concat(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString())));
        criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString()), DominioSituacao.A));
        criteria.add(Restrictions.eq("sci.".concat(MbcSalaCirurgica.Fields.SITUACAO.toString()), DominioSituacao.A));
        criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString()), Boolean.TRUE));
        criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.IND_URGENCIA.toString()), Boolean.FALSE));
        criteria.add(Restrictions.eq("cse.".concat(MbcCaractSalaEsp.Fields.IND_SITUACAO.toString()), DominioSituacao.A));
        criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString()), diaSemana));
        if (StringUtils.isNotBlank(pesquisa)) {
            if (CoreUtil.isNumeroShort(pesquisa)) {
                short codigo = Short.parseShort(pesquisa);
                criteria.add(Restrictions.or(Restrictions.eq("sci."+MbcSalaCirurgica.Fields.ID_SEQP.toString(), codigo),
                        Restrictions.eq("sci."+MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString(), codigo)));

            } else {
                criteria.add(Restrictions.or(Restrictions.ilike("sci."+MbcSalaCirurgica.Fields.NOME.toString(), pesquisa, MatchMode.ANYWHERE),
                        Restrictions.ilike("unf."+AghUnidadesFuncionais.Fields.DESCRICAO.toString(), pesquisa, MatchMode.ANYWHERE)));
            }
        }

   
        return criteria;
    }

    public Long pesquisarMbcCaractSalaEspPorDiaSemanaCount(
            String pesquisa, DominioDiaSemana diaSemana){
        DetachedCriteria criteria = montaCriteriaMbcCaractSalaEspPorDiaSemana(pesquisa, diaSemana);
        return executeCriteriaCount(criteria);
    }
	
	/**
	 * VIEW V_MBC_CARACT_SALA_ESP 
	 */
	private DetachedCriteria criarCriteriaListarMbcCaractSalaEspView() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString()), "cas");
		criteria.createAlias("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString()), "sci");
		criteria.createAlias("sci.".concat(MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL.toString()), "unf");
		criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.AGH_ESPECIALIDADES.toString()), "esp");
		criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString()), "puc", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("puc.".concat(MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString()), "ser", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ser.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "pes", JoinType.LEFT_OUTER_JOIN);
		return criteria;
	}
	
	public List<LinhaReportVO> listarEspecialidadePorNomeOuSigla(String parametro) {

		String nomeOuSigla = StringUtils.trimToNull(parametro);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		criarCriteriaListarEspecialidadePorNomeOuSigla(nomeOuSigla, criteria);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("esp.".concat(AghEspecialidades.Fields.SIGLA.toString()))), LinhaReportVO.Fields.TEXTO1.toString())
				.add(Projections.property("esp.".concat(AghEspecialidades.Fields.NOME.toString())),  LinhaReportVO.Fields.TEXTO2.toString())
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(LinhaReportVO.class));
		
		return executeCriteria(criteria);
	}
	
	public Long listarEspecialidadePorNomeOuSiglaCount(String parametro) {

		String nomeOuSigla = StringUtils.trimToNull(parametro);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		criarCriteriaListarEspecialidadePorNomeOuSigla(nomeOuSigla, criteria);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.countDistinct("esp.".concat(AghEspecialidades.Fields.SIGLA.toString())))
		);
		
		return (Long) executeCriteriaUniqueResult(criteria);
	}

	private void criarCriteriaListarEspecialidadePorNomeOuSigla(
			String nomeOuSigla, DetachedCriteria criteria) {
		criteria.createAlias("cse.".concat(MbcCaractSalaEsp.Fields.AGH_ESPECIALIDADES.toString()), "esp");

		if (StringUtils.isNotEmpty(nomeOuSigla)) {
			Criterion sigla = Restrictions.ilike("esp."+AghEspecialidades.Fields.SIGLA.toString(),nomeOuSigla, MatchMode.EXACT);
			Criterion nome = Restrictions.ilike("esp."+AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), nomeOuSigla, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(sigla, nome));
		}
	}
	
	public List<MbcCaractSalaEspId> pesquisarCaractSalaEspIds(Date dtAgenda, MbcProfAtuaUnidCirgs profAtuaUnidCirg, Short unfSeq,
			AghEspecialidades especialidade, Short sciSeqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "sci");
		criteria.createAlias("sci." + MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CARACT_SALA_ESPES.toString(), "cse");
		
		
		ProjectionList projection = Projections.projectionList()
			.add(Projections.property("cse." + MbcCaractSalaEsp.Fields.CAS_SEQ.toString()),MbcCaractSalaEspId.Fields.CAS_SEQ.toString())
			.add(Projections.property("cse." + MbcCaractSalaEsp.Fields.ESP_SEQ.toString()),MbcCaractSalaEspId.Fields.ESP_SEQ.toString())
			.add(Projections.property("cse." + MbcCaractSalaEsp.Fields.SEQP.toString()),MbcCaractSalaEspId.Fields.SEQP.toString());
		criteria.setProjection(projection);
		
		
		if(profAtuaUnidCirg != null) {
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), profAtuaUnidCirg));
		}
		if(especialidade != null){
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.ESP_SEQ.toString(), especialidade.getSeq()));
		}
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));
		if(sciSeqp != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), sciSeqp));
		}
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.HTC_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(), DominioDiaSemana.valueOf(DateFormatUtil.diaDaSemana(dtAgenda))));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MbcCaractSalaEspId.class));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcCaractSalaEsp> buscarTurnosHorariosDisponiveisUnion1(Date data, MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs,
			AghEspecialidades especialidade, Short unfSeq, Short sciSeqp, Integer qtdeDias) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sci");
		
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(),
				DominioDiaSemana.valueOf(DateFormatUtil.diaDaSemana(data))));
		if(mbcProfAtuaUnidCirgs != null) {
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), mbcProfAtuaUnidCirgs));
		}
		if(especialidade != null) {
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.ESP_SEQ.toString(), especialidade.getSeq()));
		}
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));
		if(sciSeqp != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), sciSeqp));
		}
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
//		if(qtdeDias == 1){
//			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_URGENCIA.toString(), Boolean.FALSE)); // inserido para tela de detalhe ficar igual a montagem do horario na inclusão de agenda
//		}
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY));
	}
	
	public List<MbcCaractSalaEsp> buscarTurnosHorariosDisponiveisUnion1(Date data, Short unfSeq, Short sciSeqp, Integer qtdeDias) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class, "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sci");
		
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(),
				DominioDiaSemana.valueOf(DateFormatUtil.diaDaSemana(data))));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));
		if(sciSeqp != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), sciSeqp));
		}
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
//		if(qtdeDias == 1){
//			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_URGENCIA.toString(), Boolean.FALSE)); // inserido para tela de detalhe ficar igual a montagem do horario na inclusão de agenda
//		}
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		List<MbcCaracteristicaSalaCirg> listaCaract = executeCriteria(criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY));
		List<MbcCaractSalaEsp> list = new ArrayList<MbcCaractSalaEsp>();
		for(MbcCaracteristicaSalaCirg caract : listaCaract){
			DetachedCriteria criteria2 = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse"); //busca se sala tem reservas, para adiciona-las
			criteria2.add(Restrictions.eq(MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), caract));
			criteria2.add(Restrictions.eq(MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			List<MbcCaractSalaEsp> listaCaractSalaEsp = executeCriteria(criteria2);
			if(listaCaractSalaEsp != null & !listaCaractSalaEsp.isEmpty()){
				list.addAll(listaCaractSalaEsp);
			}else{
				MbcCaractSalaEsp c = new MbcCaractSalaEsp();
				c.setMbcCaracteristicaSalaCirg(caract);
				c.setId(new MbcCaractSalaEspId(caract.getSeq(),null,null)); //gambi para criar o a caractSalaEsp fake
				list.add(c);
			}
		}
		return list;
	}
	
	public List<MbcCaractSalaEsp> buscarTurnosHorariosDisponiveisUnion2(Date data, MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs,
			Short unfSeq, Short sciSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sci");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_SUBST_ESCALA_SALAES.toString(), "ssl");
		
		criteria.add(Restrictions.ge("ssl." + MbcSubstEscalaSala.Fields.DATA.toString(), DateUtil.truncaData(data)));
		if(mbcProfAtuaUnidCirgs != null) {
			criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), mbcProfAtuaUnidCirgs));
		}
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));
		if(sciSeqp != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), sciSeqp));
		}
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY));
	}
	
	public List<MbcCaractSalaEsp> buscarTurnosHorariosDisponiveisUnion3(Date data, MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs,
			Short unfSeq, Short sciSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sci");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CEDENCIA_SALA_HCPAS.toString(), "csh");
		
		criteria.add(Restrictions.ge("csh." + MbcCedenciaSalaHcpa.Fields.DATA.toString(), DateUtil.truncaData(data)));
		if(mbcProfAtuaUnidCirgs != null) {
			criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), mbcProfAtuaUnidCirgs));
		}
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));
		if(sciSeqp != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), sciSeqp));
		}
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY));
	}
	
	public List<MbcCaractSalaEsp> buscarTurnosHorariosDisponiveisUnion3(Date data, 
			Short unfSeq, Short sciSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class, "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sci");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CEDENCIA_SALA_HCPAS.toString(), "csh");
		
		criteria.add(Restrictions.ge("csh." + MbcCedenciaSalaHcpa.Fields.DATA.toString(), DateUtil.truncaData(data)));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));
		if(sciSeqp != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), sciSeqp));
		}
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));

		List<MbcCaracteristicaSalaCirg> listaCaract = executeCriteria(criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY));
		List<MbcCaractSalaEsp> list = new ArrayList<MbcCaractSalaEsp>();
		for(MbcCaracteristicaSalaCirg caract : listaCaract){
			DetachedCriteria criteria2 = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse"); //busca se sala tem reservas, para adiciona-las
			criteria2.add(Restrictions.eq(MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			criteria2.add(Restrictions.eq(MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), caract));
			List<MbcCaractSalaEsp> listaCaractSalaEsp = executeCriteria(criteria2);
			if(listaCaractSalaEsp != null & !listaCaractSalaEsp.isEmpty()){
				list.addAll(listaCaractSalaEsp);
			}else{
				MbcCaractSalaEsp c = new MbcCaractSalaEsp();
				c.setMbcCaracteristicaSalaCirg(caract);
				c.setId(new MbcCaractSalaEspId(caract.getSeq(),null,null)); //gambi para criar o a caractSalaEsp fake
				list.add(c);
			}
		}
		return list;
	}
	
	/**
	 * Busca MbcCaractSalaEsp por profissional
	 * 
	 * @param profissional
	 * @return
	 */
	public Long pesquisarMbcCaractSalaEspPorMbcProfAtuaUnidCirgsCount(MbcProfAtuaUnidCirgs profissional) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class);
		
		criteria.add(Restrictions.eq(MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), profissional));
		
		return executeCriteriaCount(criteria);
	}

	public List<MbcCaractSalaEsp> pesquisarReservasPorDataEquipeEspUnidSalaTurno(
			Date data, 
			AghEspecialidades especialidadeFiltro, Short unidadeFiltro,
			MbcSalaCirurgica sala, MbcHorarioTurnoCirg turno) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sci");
		
		if(turno != null){
			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString(), turno));
		}
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unidadeFiltro));
		
	//	if(especialidadeFiltro != null) {
	//		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.ESP_SEQ.toString(), especialidadeFiltro.getSeq()));
	//	}
		if(sala != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), sala.getId().getSeqp()));
		}		
		
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(),
				DominioDiaSemana.valueOf(DateFormatUtil.diaDaSemana(data))));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY));
	}
	
}
