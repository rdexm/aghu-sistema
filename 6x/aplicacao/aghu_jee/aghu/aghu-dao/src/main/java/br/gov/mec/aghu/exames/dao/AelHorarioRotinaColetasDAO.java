package br.gov.mec.aghu.exames.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.dominio.DominioDiaSemanaFeriado;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelHorarioRotinaColetas;
import br.gov.mec.aghu.model.AelHorarioRotinaColetasId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

public class AelHorarioRotinaColetasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelHorarioRotinaColetas> {

	

	private static final long serialVersionUID = -2283824080593817339L;

	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelHorarioRotinaColetas.class);
	}

	public boolean obterAelHorarioRotinaColetasDAO(AghUnidadesFuncionais unidadeExecutora, AelItemSolicitacaoExames itemSolicitacaoExame){

		DetachedCriteria criteria = obterCriteria();

		Short unfSeq = unidadeExecutora.getSeq();
		Short unfSeqSolicitante = itemSolicitacaoExame.getSolicitacaoExame().getUnidadeFuncional().getSeq();

		Date iseDthrProgramada = itemSolicitacaoExame.getDthrProgramada();

		String dia = CoreUtil.retornaDiaSemana(iseDthrProgramada).toString().substring(0, 3).toUpperCase().trim();

		criteria.add(Restrictions.eq(AelHorarioRotinaColetas.Fields.UNF_SEQ.toString(),unfSeq));
		criteria.add(Restrictions.eq(AelHorarioRotinaColetas.Fields.UNF_SEQ_SOLICITANTE.toString(),unfSeqSolicitante));
		criteria.add(Restrictions.eq(AelHorarioRotinaColetas.Fields.DIA.toString(),DominioDiaSemanaFeriado.getInstance(dia)));

		SimpleDateFormat formatador = new SimpleDateFormat("hh:mm");
		criteria.add(Restrictions.sqlRestriction("To_char(" + "horario" + ",'hh24:mi') = ?",formatador.format(iseDthrProgramada), StringType.INSTANCE));
		return executeCriteriaCount(criteria) == 0;

	}

	public AelHorarioRotinaColetas obterAelHorarioRotinaColetaPorId(AelHorarioRotinaColetasId horarioId){
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelHorarioRotinaColetas.Fields.ID.toString(),horarioId));
		return (AelHorarioRotinaColetas)executeCriteriaUniqueResult(criteria);
	}
	
	public List<AelHorarioRotinaColetas> obterAelHorarioRotinaColetasAtivas(AghUnidadesFuncionais unidadeExecutora, Short unfSeqSolicitante){

		DetachedCriteria criteria = obterCriteria();

		Short unfSeq = unidadeExecutora.getSeq();

		criteria.add(Restrictions.eq(AelHorarioRotinaColetas.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AelHorarioRotinaColetas.Fields.UNF_SEQ_SOLICITANTE.toString(), unfSeqSolicitante));
		criteria.add(Restrictions.eq(AelHorarioRotinaColetas.Fields.SITUACAO.toString(), DominioSituacao.A));

		return executeCriteria(criteria);

	}


	/**
	 * Busca os horários de rotina para os parâmetros informados.
	 * @param unidadeSolicitante Unidade que solicita o exame
	 * @param itemSolicitacaoExame Item de exame sendo solicitado
	 * @param dataHora Data/hora a partir de que se quer buscar.
	 * @return
	 */
	public List<AelHorarioRotinaColetas> obterAelHorarioRotinaColetasDAO(AghUnidadesFuncionais unidadeSolicitante, AghUnidadesFuncionais unfSeqAvisa, Date dataHora){
		if (unfSeqAvisa != null
				&& unidadeSolicitante != null
				&& dataHora != null) {

			DetachedCriteria criteria = getComumCriteria(unfSeqAvisa, unidadeSolicitante, dataHora);

			criteria.add(Restrictions.eq(AelHorarioRotinaColetas.Fields.DIA.toString(),DominioDiaSemanaFeriado.getInstance(getDia(dataHora))));

			return executeCriteria(criteria);
		} else {
			return null;
		}

	}
	
	
	/**
	 * Busca genérica AelHorarioRotinaColetas por parâmetros
	 * @param unidadeColeta
	 * @param unidadeSolicitante
	 * @param dataHora
	 * @param dia
	 * @param situacaoHorario
	 * @return
	 */
	public List<AelHorarioRotinaColetas> obterAelHorarioRotinaColetasPorParametros(Short unidadeColeta, Short unidadeSolicitante, Date dataHora, String dia, DominioSituacao situacaoHorario){
		DetachedCriteria criteria = obterCriteria();

		if(unidadeColeta!=null){
			criteria.add(Restrictions.eq(AelHorarioRotinaColetas.Fields.UNF_SEQ.toString(), unidadeColeta));
		}

		if(unidadeSolicitante!=null){
			criteria.add(Restrictions.eq(AelHorarioRotinaColetas.Fields.UNF_SEQ_SOLICITANTE.toString(),unidadeSolicitante));
		}
		
		if(dataHora!=null){
			SimpleDateFormat formatador = new SimpleDateFormat("HH:mm");
			criteria.add(Restrictions.sqlRestriction("To_char(horario, 'hh24:mi') = ? ",formatador.format(dataHora), StringType.INSTANCE));
		}
		
		if(dia!=null){
			criteria.add(Restrictions.eq(AelHorarioRotinaColetas.Fields.DIA.toString(),DominioDiaSemanaFeriado.getInstance(dia)));
		}
		
		if(situacaoHorario!=null){
			criteria.add(Restrictions.eq(AelHorarioRotinaColetas.Fields.SITUACAO.toString(),situacaoHorario));
		}
		
		criteria.addOrder(Order.asc(AelHorarioRotinaColetas.Fields.HORARIO.toString()));
		criteria.addOrder(Order.asc(AelHorarioRotinaColetas.Fields.UNF_SEQ_SOLICITANTE.toString()));

		return executeCriteria(criteria);


	}

	

	/**
	 * Busca os horários de rotina para os parâmetros informados para Feriados o dia inteiro.
	 * @param unidadeSolicitante Unidade que solicita o exame
	 * @param itemSolicitacaoExame Item de exame sendo solicitado
	 * @param dataHora Data/hora a partir de que se quer buscar.
	 * @return
	 */
	public List<AelHorarioRotinaColetas> obterAelHorarioRotinaColetasFeriado(AghUnidadesFuncionais unidadeSolicitante, AghUnidadesFuncionais unfSeqAvisa, Date dataHora){

		if (unfSeqAvisa != null
				&& unidadeSolicitante != null
				&& dataHora != null) {

			DetachedCriteria criteria = getComumCriteria(unfSeqAvisa, unidadeSolicitante, dataHora);


			criteria.add(Restrictions.eq(AelHorarioRotinaColetas.Fields.DIA.toString(),DominioDiaSemanaFeriado.FER));

			return executeCriteria(criteria);
		} else {
			return null;
		}
	}

	/**
	 * Busca os horários de rotina para os parâmetros informados para feriados pela manhã.
	 * @param unidadeSolicitante Unidade que solicita o exame
	 * @param itemSolicitacaoExame Item de exame sendo solicitado
	 * @param dataHora Data/hora a partir de que se quer buscar.
	 * @return
	 */
	public List<AelHorarioRotinaColetas> obterAelHorarioRotinaColetasFeriadoManha(AghUnidadesFuncionais unidadeSolicitante, AghUnidadesFuncionais unfSeqAvisa, Date dataHora){

		if (unfSeqAvisa != null
				&& unidadeSolicitante != null
				&& dataHora != null) {

			DetachedCriteria criteria = getComumCriteria(unfSeqAvisa, unidadeSolicitante, dataHora);

			Criterion criteria2 = Restrictions.eq(AelHorarioRotinaColetas.Fields.DIA.toString(),DominioDiaSemanaFeriado.getInstance(getDia(dataHora)));
			Criterion criteria3 = Restrictions.sqlRestriction("To_char(" + "horario" + ",'hh24:mi') >= ? ", "'12:00:00'", StringType.INSTANCE);

			criteria.add(Restrictions.or(Restrictions.and(criteria2, criteria3), Restrictions.eq(AelHorarioRotinaColetas.Fields.DIA.toString(),DominioDiaSemanaFeriado.FERM)));

			return executeCriteria(criteria);
		} else {
			return null;
		}

	}

	/**
	 * Busca os horários de rotina para os parâmetros informados para feriados á tarde.
	 * @param unidadeSolicitante Unidade que solicita o exame
	 * @param itemSolicitacaoExame Item de exame sendo solicitado
	 * @param dataHora Data/hora a partir de que se quer buscar.
	 */
	public List<AelHorarioRotinaColetas> obterAelHorarioRotinaColetasFeriadoTarde(AghUnidadesFuncionais unidadeSolicitante, AghUnidadesFuncionais unfSeqAvisa, Date dataHora){
		if (unfSeqAvisa != null
				&& unidadeSolicitante != null
				&& dataHora != null) {

			DetachedCriteria criteria = getComumCriteria(unfSeqAvisa, unidadeSolicitante, dataHora);

			Criterion criteria2 = Restrictions.eq(AelHorarioRotinaColetas.Fields.DIA.toString(),DominioDiaSemanaFeriado.getInstance(getDia(dataHora)));
			Criterion criteria3 = Restrictions.sqlRestriction("To_char(" + "horario" + ",'hh24:mi') < ? ", "'12:00:00'", StringType.INSTANCE);

			criteria.add(Restrictions.or(Restrictions.and(criteria2, criteria3), Restrictions.eq(AelHorarioRotinaColetas.Fields.DIA.toString(),DominioDiaSemanaFeriado.FERT)));

			return executeCriteria(criteria);
		} else {
			return null;
		}

	}
	
	/**
	 * Monta o criteria com as cláusulas comuns para Dia, Feriado, Feriado pela manhã e feriado pela tarde.
	 * @param itemSolicitacaoExame
	 * @param unidadeSolicitante
	 * @param dataHora
	 * @return
	 */
	private DetachedCriteria getComumCriteria(AghUnidadesFuncionais unfSeqAvisa, AghUnidadesFuncionais unidadeSolicitante, Date dataHora) {
		DetachedCriteria criteria = obterCriteria();

		Short unfSeq = unfSeqAvisa.getSeq();
		Short unfSeqSolicitante = unidadeSolicitante.getSeq();
		Date iseDthrProgramada = dataHora;

		criteria.add(Restrictions.eq(AelHorarioRotinaColetas.Fields.UNF_SEQ.toString(),unfSeq));
		criteria.add(Restrictions.eq(AelHorarioRotinaColetas.Fields.UNF_SEQ_SOLICITANTE.toString(),unfSeqSolicitante));
		criteria.add(Restrictions.eq(AelHorarioRotinaColetas.Fields.SITUACAO.toString(),DominioSituacao.A));

		SimpleDateFormat formatador = new SimpleDateFormat("HH:mm");
		criteria.add(Restrictions.sqlRestriction("To_char(" + "horario" + ",'hh24:mi') "+ getComparador(iseDthrProgramada) +" ?",formatador.format(iseDthrProgramada), StringType.INSTANCE));

		return criteria;
	}

	/**
	 * Retorna o dia formatado.
	 * @param iseDthrProgramada
	 * @return
	 */
	private String getDia(Date iseDthrProgramada) {
		return CoreUtil.retornaDiaSemana(iseDthrProgramada).toString().substring(0, 3).toUpperCase().trim();
	}
	
	/**
	 * Busca se deve compara maior ou maior e igual. 
	 * No forms havia uma regra que se fosse a data de hoje sendo buscada deveria comparar se é maior, senão é maior ou igual
	 * @param iseDthrProgramada
	 * @return String.
	 */
	private String getComparador(Date iseDthrProgramada) {
		//Se for o dia de hoje, então não pode comparar o igual, só o maior.
		String sinalComparador =" >= "; 	
		if (DateValidator.validarMesmoDia(new Date(), iseDthrProgramada)) {
			sinalComparador = " > ";
		}
		return sinalComparador;
	}


}
