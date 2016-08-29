package br.gov.mec.aghu.internacao.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfissionaisEspConvenio;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.model.RapServidores;

/**
 * 
 * @author cvagheti
 * 
 */
public class AinEscalasProfissionalIntDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AinEscalasProfissionalInt> {

	private static final long serialVersionUID = 3076394866637961445L;

	/**
	 * Retorna criteria para consulta das escalas com os parâmetros fornecidos.
	 * 
	 * @param vinculo
	 * @param matricula
	 * @param seqEspecialidade
	 * @param codigoConvenio
	 * @return
	 */
	private DetachedCriteria criteria(Short vinculo, Integer matricula, Short seqEspecialidade, Short codigoConvenio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinEscalasProfissionalInt.class);

		if (vinculo != null) {
			criteria.add(Restrictions.eq(AinEscalasProfissionalInt.Fields.ID_SERVIDOR_VINCULO.toString(), vinculo));
		}

		if (matricula != null) {
			criteria.add(Restrictions.eq(AinEscalasProfissionalInt.Fields.ID_SERVIDOR_MATRICULA.toString(), matricula));
		}

		if (seqEspecialidade != null) {
			criteria.add(Restrictions.eq(AinEscalasProfissionalInt.Fields.ID_ESPECIALIDADE_SEQ.toString(), seqEspecialidade));
		}

		if (codigoConvenio != null) {
			criteria.add(Restrictions.eq(AinEscalasProfissionalInt.Fields.ID_CONVENIO_CODIGO.toString(), codigoConvenio));
		}

		return criteria;
	}
	
	/**
	 * Método para pesquisar escalar de profissionais.
	 * 
	 * @param matriculaProfessor
	 * @param codigoProfessor
	 * @param seqEspecialidade
	 * @param codigoConvenioSaude
	 * @return
	 */
	public List<AinEscalasProfissionalInt> pesquisarEscalaProfissionalInt(Integer matriculaProfessor, Short codigoProfessor,
			Short seqEspecialidade, Short codigoConvenioSaude) {
		DetachedCriteria criteria = this.criteria(codigoProfessor, matriculaProfessor, seqEspecialidade, codigoConvenioSaude);
		return executeCriteria(criteria);
	}
	
	/**
	 * retorna a query da escala
	 * 
	 * @param vinculo
	 * @param matricula
	 * @param seqEspecialidade
	 * @param codigoConvenio
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public DetachedCriteria montarCriteriaEscala(Short vinculo, Integer matricula, Integer seqEspecialidade, Short codigoConvenio,
			Date dataInicio, Date dataFim) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinEscalasProfissionalInt.class);

		if (vinculo != null) {
			criteria.add(Restrictions.eq(AinEscalasProfissionalInt.Fields.ID_SERVIDOR_VINCULO.toString(), vinculo));
		}
		if (matricula != null) {
			criteria.add(Restrictions.eq(AinEscalasProfissionalInt.Fields.ID_SERVIDOR_MATRICULA.toString(), matricula));
		}
		if (seqEspecialidade != null) {
			criteria.add(Restrictions.eq(AinEscalasProfissionalInt.Fields.ID_ESPECIALIDADE_SEQ.toString(),
					seqEspecialidade.shortValue()));
		}
		if (codigoConvenio != null) {
			criteria.add(Restrictions.eq(AinEscalasProfissionalInt.Fields.ID_CONVENIO_CODIGO.toString(), codigoConvenio));
		}
		if (dataInicio != null) {
			criteria.add(Restrictions.ge(AinEscalasProfissionalInt.Fields.DATA_INICIO.toString(), dataInicio));
		}
		if (dataFim != null) {
			criteria.add(Restrictions.or(Restrictions.isNull(AinEscalasProfissionalInt.Fields.DATA_FIM.toString()),
					Restrictions.le(AinEscalasProfissionalInt.Fields.DATA_FIM.toString(), dataFim)));
		}

		return criteria;
	}

	public List<AinEscalasProfissionalInt> pesquisarEscala(Short vinculo, Integer matricula, Integer seqEspecialidade,
			Short codigoConvenio, Date dataInicio, Date dataFim, Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		DetachedCriteria criteria = montarCriteriaEscala(vinculo, matricula, seqEspecialidade, codigoConvenio, dataInicio, dataFim);

		// adiciona order by
		criteria.addOrder(Order.asc(AinEscalasProfissionalInt.Fields.ID_SERVIDOR_MATRICULA.toString()));
		criteria.addOrder(Order.desc(AinEscalasProfissionalInt.Fields.DATA_INICIO.toString()));

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long pesquisarEscalaCount(Short vinculo, Integer matricula, Integer seqEspecialidade, Short codigoConvenio,
			Date dataInicio, Date dataFim) {
		DetachedCriteria criteria = montarCriteriaEscala(vinculo, matricula, seqEspecialidade,
				codigoConvenio, dataInicio, dataFim);

		return executeCriteriaCount(criteria);

	}
	
	public List<AinEscalasProfissionalInt> obterEscalas(Date date) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinEscalasProfissionalInt.class);

		Calendar hoje = Calendar.getInstance();

		Calendar ontem = Calendar.getInstance();
		ontem.add(Calendar.DAY_OF_YEAR, -1);

		if (date != null) {
			hoje.setTime(date);

			// Calendar ontem = Calendar.getInstance();
			ontem.setTime(date);
			ontem.add(Calendar.DAY_OF_YEAR, -1);
		}

		// epi.dt_fim between TRUNC(sysdate - 1) and TRUNC(sysdate)
		criteria.add(Restrictions.between(
				AinEscalasProfissionalInt.Fields.DATA_FIM.toString(), DateUtils
						.truncate(ontem.getTime(), Calendar.DAY_OF_MONTH),
				DateUtils.truncate(hoje.getTime(), Calendar.DAY_OF_MONTH)));

		criteria.add(Restrictions
				.isNotNull(AinEscalasProfissionalInt.Fields.SER_MATRICULA
						.toString()));

		criteria.add(Restrictions
				.isNotNull(AinEscalasProfissionalInt.Fields.SER_VIN_CODIGO
						.toString()));

		List<AinEscalasProfissionalInt> res = executeCriteria(criteria);

		return res;
	}
	
	public boolean hasSubstituto(AinEscalasProfissionalInt item, Date date) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinEscalasProfissionalInt.class);

		Calendar hoje = Calendar.getInstance();

		Calendar amanha = Calendar.getInstance();
		amanha.add(Calendar.DAY_OF_YEAR, +1);

		if (date != null) {
			hoje.setTime(date);

			// Calendar ontem = Calendar.getInstance();
			amanha.setTime(date);
			amanha.add(Calendar.DAY_OF_YEAR, +1);
		}

		criteria.add(Restrictions.eq(
				AinEscalasProfissionalInt.Fields.ID_SERVIDOR_MATRICULA
						.toString(), item.getServidorProfessor().getId()
						.getMatricula()));

		criteria.add(Restrictions.eq(
				AinEscalasProfissionalInt.Fields.ID_SERVIDOR_VINCULO.toString(),
				item.getServidorProfessor().getId().getVinCodigo()));

		criteria.add(Restrictions.eq(
				AinEscalasProfissionalInt.Fields.ID_CONVENIO_CODIGO.toString(),
				item.getId().getPecCnvCodigo()));

		criteria.add(Restrictions.eq(
				AinEscalasProfissionalInt.Fields.ID_ESPECIALIDADE_SEQ
						.toString(), item.getId().getPecPreEspSeq()));

		// (epi.dt_inicio <= sysdate or epi.dt_inicio = trunc(sysdate + 1))
		criteria.add(Restrictions.or(Restrictions.le(
				AinEscalasProfissionalInt.Fields.DATA_INICIO.toString(), hoje
						.getTime()), Restrictions.eq(
				AinEscalasProfissionalInt.Fields.DATA_INICIO.toString(),
				DateUtils.truncate(amanha.getTime(), Calendar.DAY_OF_MONTH))));

		// (epi.dt_fim is null or epi.dt_fim > sysdate)
		criteria.add(Restrictions.or(Restrictions
				.isNull(AinEscalasProfissionalInt.Fields.DATA_FIM.toString()),
				Restrictions.gt(AinEscalasProfissionalInt.Fields.DATA_FIM
						.toString(), hoje.getTime())));

		List<AinEscalasProfissionalInt> res = executeCriteria(criteria);

		if (res != null && !res.isEmpty()) {
			return true;
		}

		return false;
	}
	
	public List<AinEscalasProfissionalInt> listarEscalasProfissionaisInt(Short idConvenioCodigo, Short idEspecialidadeSeq, Date dataReferencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinEscalasProfissionalInt.class);
  		DetachedCriteria criteriaProfEsp = criteria.createCriteria(AinEscalasProfissionalInt.Fields.PROF_ESPECIALIDADE.toString());
  		DetachedCriteria criteriaServidor = criteriaProfEsp.createCriteria(AghProfEspecialidades.Fields.SERVIDOR.toString());
  		
  		criteria.add(Restrictions.eq(AinEscalasProfissionalInt.Fields.ID_CONVENIO_CODIGO.toString(), idConvenioCodigo));
  		criteria.add(Restrictions.eq(AinEscalasProfissionalInt.Fields.ID_ESPECIALIDADE_SEQ.toString(), idEspecialidadeSeq));
  		
  		Criterion criterionDtFimNull = Restrictions.isNull(AinEscalasProfissionalInt.Fields.DATA_FIM.toString());
  		Criterion criterionDtInicio = Restrictions.le(AinEscalasProfissionalInt.Fields.DATA_INICIO.toString(), dataReferencia);
  		Criterion criterionOr1 = Restrictions.and(criterionDtFimNull, criterionDtInicio);
  		
  		Criterion criterionDtFimMaiorAtual = Restrictions.ge(AinEscalasProfissionalInt.Fields.DATA_FIM.toString(), dataReferencia);
  		Criterion criterionOr2 = Restrictions.and(criterionDtInicio, criterionDtFimMaiorAtual);
  		
  		criteria.add(Restrictions.or(criterionOr1, criterionOr2));
  		criteriaProfEsp.add(Restrictions.gt(AghProfEspecialidades.Fields.CAPAC_REFERENCIAL.toString(), 0));
  		
  		//Este trecho abaixo não existe na procedure original ainp_sugere_prof_esp, porém, foi solicitado pela
  		//Milena que fosse incluído a fim de trazer somente os registros ativos
  		Criterion criterionSitA = Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A);
  		Criterion criterionSitP = Restrictions.eq(RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.P);
  		Criterion criterionDtFim = Restrictions.gt(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date());
  		Criterion criterionAnd = Restrictions.and(criterionSitP, criterionDtFim);
  		criteriaServidor.add(Restrictions.or(criterionSitA, criterionAnd));
  		
  		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna as escalas profissionais que possuem um convênio especialidade
	 * específico
	 * @param convenioEspecialidade
	 * @return
	 */
	public List<AinEscalasProfissionalInt> pesquisarEscalasPorConvenioEspecialidade(
			AghProfissionaisEspConvenio convenioEspecialidade) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AinEscalasProfissionalInt.class);
		criteria.add(Restrictions.eq(
				AinEscalasProfissionalInt.Fields.PROF_ESPECIALIDADE_CONVENIO
						.toString(), convenioEspecialidade));

		return executeCriteria(criteria);
	}
	
}
