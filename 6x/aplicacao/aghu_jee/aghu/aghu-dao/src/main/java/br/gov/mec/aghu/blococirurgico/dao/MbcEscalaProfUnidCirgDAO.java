package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcEscalaProfUnidCirg;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.persistence.dialect.OrderBySql;

public class MbcEscalaProfUnidCirgDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcEscalaProfUnidCirg> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5750744801827572912L;

	/**
	 * Busca escala de profissionais - menos anestesistas professor e contratado
	 * @param unfSeq
	 * @param seqp
	 * @param dia_semana
	 * @param turno
	 * @return
	 */
	public List<MbcEscalaProfUnidCirg> pesquisarEscalaProfissionais(Short unfSeq, Short seqp, DominioDiaSemana diaSemana, String turno){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcEscalaProfUnidCirg.class);
		criteria.createAlias(MbcEscalaProfUnidCirg.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), "CAS");
		criteria.createAlias("CAS."+MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "SCI");

		DominioFuncaoProfissional[] funcoes = {DominioFuncaoProfissional.ANP, DominioFuncaoProfissional.ANC};
		
		criteria.add(Restrictions.eq("SCI."+MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("SCI."+MbcSalaCirurgica.Fields.ID_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq("CAS."+MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(), diaSemana));
		criteria.add(Restrictions.eq("CAS."+MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS_ID_TURNO.toString(), turno));
		criteria.add(Restrictions.not(Restrictions.in(MbcEscalaProfUnidCirg.Fields.IND_FUNCAO_PROF.toString(), funcoes)));
		return executeCriteria(criteria);
		
	}
	
	
	/**
	 * Busca escala de profissionais por funcao
	 * @param unfSeq
	 * @param seqp
	 * @param diaSemana
	 * @param turno
	 * @param funcao
	 * @return
	 */
	public List<MbcEscalaProfUnidCirg> pesquisarEscalaProfissionalPorFuncao(Short unfSeq, Short seqp, DominioDiaSemana diaSemana, String turno, DominioFuncaoProfissional funcao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcEscalaProfUnidCirg.class);
		criteria.createAlias(MbcEscalaProfUnidCirg.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), "CAS");
		criteria.createAlias("CAS."+MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "SCI");

		DominioFuncaoProfissional[] funcoes = {DominioFuncaoProfissional.ANP, DominioFuncaoProfissional.ANC};
		
		criteria.add(Restrictions.eq("SCI."+MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("SCI."+MbcSalaCirurgica.Fields.ID_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq("CAS."+MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(), diaSemana));
		criteria.add(Restrictions.eq("CAS."+MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS_ID_TURNO.toString(), turno));
		criteria.add(Restrictions.eq(MbcEscalaProfUnidCirg.Fields.IND_FUNCAO_PROF.toString(), funcao));
		return executeCriteria(criteria);
		
	}
	
	/**
	 * Busca escala de profissionais por matrícula, código, unidade e função
	 * 
	 * @param matricula
	 * @param codigo
	 * @param unidade
	 * @param funcao
	 * @return
	 */
	public Long pesquisarEscalaProfissionalPorMatriculaCodigoUnidFuncaoCount(Integer matricula, Short codigo, Short unidade, DominioFuncaoProfissional funcao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcEscalaProfUnidCirg.class);
		
		criteria.add(Restrictions.eq(MbcEscalaProfUnidCirg.Fields.SERVIDOR_MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq(MbcEscalaProfUnidCirg.Fields.SERVIDOR_CODIGO.toString(), codigo));
		criteria.add(Restrictions.eq(MbcEscalaProfUnidCirg.Fields.UNF_SEQ.toString(), unidade));
		criteria.add(Restrictions.eq(MbcEscalaProfUnidCirg.Fields.IND_FUNCAO_PROF.toString(), funcao));
		
		return executeCriteriaCount(criteria);

	}
	
	public Long pesquisarProfissionaisEscalaPorSalaCount(AghUnidadesFuncionais unidadeFuncional,
															MbcSalaCirurgica salaCirurgica,
															DominioDiaSemana diaSemana, MbcTurnos turnos,
															DominioFuncaoProfissional funcaoProfissional,
															RapServidores profissionalServ){
		
		DetachedCriteria criteria = montarCriteriaProfissionaisEscalaPorSala(unidadeFuncional, salaCirurgica, diaSemana, 
																			 turnos, funcaoProfissional, profissionalServ);
		
		return this.executeCriteriaCount(criteria);
	}


	public List<MbcEscalaProfUnidCirg> pesquisarProfissionaisEscalaPorSala(AghUnidadesFuncionais unidadeFuncional,
																			MbcSalaCirurgica salaCirurgica,
																			DominioDiaSemana diaSemana, MbcTurnos turnos,
																			DominioFuncaoProfissional funcaoProfissional,
																			RapServidores profissionalServ, Integer firstResult,
																			Integer maxResult, String orderProperty, boolean asc) {
		
		DetachedCriteria criteria = montarCriteriaProfissionaisEscalaPorSala(unidadeFuncional, salaCirurgica, diaSemana, turnos, funcaoProfissional, profissionalServ);
		criteria.addOrder(Order.asc("CSC." + MbcCaracteristicaSalaCirg.Fields.SCI_SEQP.toString()));
		
		StringBuilder orderBy = new StringBuilder(200);
		orderBy.append(" case DIA_SEMANA ");
		orderBy.append("	when 'SEG' then 1 ");
		orderBy.append("	when 'TER' then 2 ");
		orderBy.append("	when 'QUA' then 3 ");
		orderBy.append("	when 'QUI' then 4 ");
		orderBy.append("	when 'SEX' then 5 ");
		orderBy.append("	when 'SAB' then 6 ");
		orderBy.append("	when 'DOM' then 7 ");
		orderBy.append(" end ");

		criteria.addOrder(OrderBySql.sql(orderBy.toString()));
		criteria.addOrder(Order.asc("TUR." + MbcTurnos.Fields.ORDEM.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	private DetachedCriteria montarCriteriaProfissionaisEscalaPorSala(AghUnidadesFuncionais unidadeFuncional,
																		MbcSalaCirurgica salaCirurgica,
																		DominioDiaSemana diaSemana, MbcTurnos turnos,
																		DominioFuncaoProfissional funcaoProfissional,
																		RapServidores profissionalServ){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcEscalaProfUnidCirg.class, "EPU");
		
		criteria.createAlias("EPU." + MbcEscalaProfUnidCirg.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), "CSC");
		criteria.createAlias("EPU." + MbcEscalaProfUnidCirg.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), "UCS");
		criteria.createAlias("CSC." + MbcCaracteristicaSalaCirg.Fields.TURNO.toString(), "TUR");
		
		
		criteria.createAlias("UCS." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PEF", JoinType.LEFT_OUTER_JOIN);
		
		if(unidadeFuncional != null){
			criteria.add(Restrictions.eq("CSC." + MbcCaracteristicaSalaCirg.Fields.SCI_UNF_SEQ.toString(), unidadeFuncional.getSeq()));
		}
		if(salaCirurgica != null){
			criteria.add(Restrictions.eq("CSC." + MbcCaracteristicaSalaCirg.Fields.SCI_SEQP.toString(), salaCirurgica.getId().getSeqp()));
		}
		if(diaSemana != null){
			criteria.add(Restrictions.eq("CSC." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(), DominioDiaSemana.valueOf(diaSemana.getDescricao().substring(0, 3))));
		}
		if(turnos != null){
			criteria.add(Restrictions.eq("TUR." + MbcTurnos.Fields.TURNO.toString(), turnos.getTurno()));
		}
		if(funcaoProfissional != null){
			criteria.add(Restrictions.eq("EPU." + MbcEscalaProfUnidCirg.Fields.IND_FUNCAO_PROF.toString(), DominioFuncaoProfissional.valueOf(funcaoProfissional.getCodigo())));
		}
		if(profissionalServ != null){
			criteria.add(Restrictions.eq("EPU." + MbcEscalaProfUnidCirg.Fields.SERVIDOR_CODIGO.toString(), profissionalServ.getId().getVinCodigo()));
			criteria.add(Restrictions.eq("EPU." + MbcEscalaProfUnidCirg.Fields.SERVIDOR_MATRICULA.toString(), profissionalServ.getId().getMatricula()));
		}
		
		return criteria;
	}


	public List<MbcEscalaProfUnidCirg> buscarDadosRelatorioEscalaProfUnidCirg(AghUnidadesFuncionais unidadeFuncional, MbcTurnos turnos,
																				DominioFuncaoProfissional funcaoProfissional1,
																				DominioFuncaoProfissional funcaoProfissional2,
																				DominioFuncaoProfissional funcaoProfissional3,
																				DominioFuncaoProfissional funcaoProfissional4) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcEscalaProfUnidCirg.class, "ESC");
		
		criteria.createAlias("ESC." + MbcEscalaProfUnidCirg.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), "CAR");
		criteria.createAlias("CAR." + MbcCaracteristicaSalaCirg.Fields.TURNO.toString(), "TUR");
		criteria.createAlias("ESC." + MbcEscalaProfUnidCirg.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), "PRO");
		criteria.createAlias("ESC." + MbcEscalaProfUnidCirg.Fields.RAP_SERVIDORES.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PEF");
		
		DominioFuncaoProfissional funcao1 = null;
		if(funcaoProfissional1 != null){
			funcao1 = DominioFuncaoProfissional.valueOf(funcaoProfissional1.getCodigo());
		}
		
		DominioFuncaoProfissional funcao2 = null;
		if(funcaoProfissional2 != null){
			funcao2 = DominioFuncaoProfissional.valueOf(funcaoProfissional2.getCodigo());
		}
		
		DominioFuncaoProfissional funcao3 = null;
		if(funcaoProfissional3 != null){
			funcao3 = DominioFuncaoProfissional.valueOf(funcaoProfissional3.getCodigo());
		}
		
		DominioFuncaoProfissional funcao4 = null;
		if(funcaoProfissional4 != null){
			funcao4 = DominioFuncaoProfissional.valueOf(funcaoProfissional4.getCodigo());
		}
		
		criteria.add(Restrictions.in("ESC." + MbcEscalaProfUnidCirg.Fields.IND_FUNCAO_PROF.toString(), new DominioFuncaoProfissional[]{funcao1, funcao2, funcao3, funcao4}));

		if(unidadeFuncional != null){
			criteria.add(Restrictions.eq("PRO." + MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), unidadeFuncional.getSeq()));
		}
		if(turnos != null){
			criteria.add(Restrictions.eq("TUR." + MbcTurnos.Fields.TURNO.toString(), turnos.getTurno()));
		}
		
		criteria.addOrder(Order.asc("ESC." + MbcEscalaProfUnidCirg.Fields.IND_FUNCAO_PROF.toString()));
		
		return executeCriteria(criteria.setResultTransformer(DetachedCriteria.DISTINCT_ROOT_ENTITY));
	}
	
}
