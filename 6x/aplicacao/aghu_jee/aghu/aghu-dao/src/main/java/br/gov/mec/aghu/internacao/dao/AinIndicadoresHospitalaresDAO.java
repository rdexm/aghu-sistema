package br.gov.mec.aghu.internacao.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioTipoUnidade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinIndicadoresHospitalares;
import br.gov.mec.aghu.model.AinLeitos;

public class AinIndicadoresHospitalaresDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinIndicadoresHospitalares> {


	private static final long serialVersionUID = 1548360158045125005L;
	public static final String SEPARADOR = ".";

	/**
	 * Remover Indicador por competência.
	 * 
	 * @param anoMesComp
	 */
	public void removerPorCompetencia(Date anoMesComp) {
		Query query = createQuery(
				"delete " + AinIndicadoresHospitalares.class.getName() + " where "
						+ AinIndicadoresHospitalares.Fields.COMPETENCIA_INTERNACAO.toString()
						+ " = :anoMesComp ");
		query.setParameter("anoMesComp", anoMesComp);
		query.executeUpdate();
	}

	/**
	 * @ORADB Cursor capc_unid_priv
	 * 
	 * @return
	 */
	public List<AinLeitos> obterCapacUnidPriv() {

		DetachedCriteria cri = DetachedCriteria.forClass(AinLeitos.class);
		cri.createAlias(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(),
				AinLeitos.Fields.UNIDADE_FUNCIONAL.toString());
		cri.addOrder(Order.asc(AinLeitos.Fields.UNIDADE_FUNCIONAL_SEQ.toString()));

		// Lista dos leitos contendo somente unfSeq, capacidade
		return executeCriteria(cri);
	}

	/**
	 * Método para criar a Criteria base para as consultas de indicadores por
	 * unidade
	 * 
	 * @param tipoUnidade
	 * @param mesCompetencia
	 * @return
	 */
	private DetachedCriteria criarCriteriaIndicadoresUnidade(DominioTipoUnidade tipoUnidade,
			Date mesCompetencia) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinIndicadoresHospitalares.class);

		Calendar dataInicial = Calendar.getInstance();
		dataInicial.setTime(mesCompetencia);
		dataInicial.set(Calendar.DAY_OF_MONTH, 1);
		Calendar dataFinal = Calendar.getInstance();
		dataFinal.setTime(mesCompetencia);
		dataFinal.set(Calendar.DAY_OF_MONTH, dataFinal.getActualMaximum(Calendar.DAY_OF_MONTH));

		criteria.add(Restrictions.between(
				AinIndicadoresHospitalares.Fields.COMPETENCIA_INTERNACAO.toString(),
				dataInicial.getTime(), dataFinal.getTime()));
		criteria.add(Restrictions.isNotNull(AinIndicadoresHospitalares.Fields.UNIDADE_FUNCIONAL
				.toString() + SEPARADOR + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		if (tipoUnidade != null) {
			criteria.add(Restrictions.eq(AinIndicadoresHospitalares.Fields.TIPO_UNIDADE.toString(),
					tipoUnidade));
		}

		return criteria;
	}

	/**
	 * Método para pesquisar os indicadores gerais de unidade
	 * 
	 * @param tipoUnidade
	 * @param mesCompetencia
	 * @return
	 */
	public List<AinIndicadoresHospitalares> pesquisarIndicadoresGeraisUnidade(
			DominioTipoUnidade tipoUnidade, Date mesCompetencia) {

		DetachedCriteria criteria = this.criarCriteriaIndicadoresUnidade(tipoUnidade,
				mesCompetencia);

		criteria.createAlias(AinIndicadoresHospitalares.Fields.UNIDADE_FUNCIONAL.toString(),
				AinIndicadoresHospitalares.Fields.UNIDADE_FUNCIONAL.toString());

		String unidadeFuncional = AinIndicadoresHospitalares.Fields.UNIDADE_FUNCIONAL.toString();
		String orderAndar = unidadeFuncional + SEPARADOR
				+ AghUnidadesFuncionais.Fields.ANDAR.toString();
		String orderAla = unidadeFuncional + SEPARADOR
				+ AghUnidadesFuncionais.Fields.ALA.toString();
		String orderDescricao = unidadeFuncional + SEPARADOR
				+ AghUnidadesFuncionais.Fields.DESCRICAO.toString();

		criteria.addOrder(Order.asc(orderAndar));
		criteria.addOrder(Order.asc(orderAla));
		criteria.addOrder(Order.asc(orderDescricao));

		return this.executeCriteria(criteria);
	}

	/**
	 * Método para pesquisar os indicadores totais de unidade
	 * 
	 * @param tipoUnidade
	 * @param mesCompetencia
	 * @return
	 */
	public List<AinIndicadoresHospitalares> pesquisarIndicadoresTotaisUnidade(
			DominioTipoUnidade tipoUnidade, Date mesCompetencia) {
		DetachedCriteria criteria = this.criarCriteriaIndicadoresUnidade(tipoUnidade,
				mesCompetencia);
		criteria.add(Restrictions.isNull(AinIndicadoresHospitalares.Fields.ESPECIALIDADE.toString()));

		return this.executeCriteria(criteria);
	}

	/**
	 * @param mes
	 * @return
	 */
	public List<AinIndicadoresHospitalares> pesquisarQuery5IndicadoresClinica(Date mes) {
		DetachedCriteria cri = DetachedCriteria.forClass(AinIndicadoresHospitalares.class);
		cri.createAlias(AinIndicadoresHospitalares.Fields.CLINICA.toString(),
				AinIndicadoresHospitalares.Fields.CLINICA.toString());

		cri.add(Restrictions.isNotNull(AinIndicadoresHospitalares.Fields.CLINICA.toString()));
		cri.add(Restrictions.isNull(AinIndicadoresHospitalares.Fields.UNIDADE_FUNCIONAL.toString()
				+ "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		cri.add(Restrictions.isNull(AinIndicadoresHospitalares.Fields.ESPECIALIDADE.toString()));
		cri.add(Restrictions.isNull(AinIndicadoresHospitalares.Fields.SERVIDOR_VIN_CODIGO
				.toString()));
		Object[] s = { DominioTipoUnidade.A, DominioTipoUnidade.U };
		cri.add(Restrictions.in(AinIndicadoresHospitalares.Fields.TIPO_UNIDADE.toString(), s));

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mes);
		int lastDate = calendar.getActualMaximum(Calendar.DATE);
		calendar.set(Calendar.DATE, lastDate);

		cri.add(Restrictions.between(
				AinIndicadoresHospitalares.Fields.COMPETENCIA_INTERNACAO.toString(), mes,
				calendar.getTime()));

		return executeCriteria(cri);
	}

	public List<AinIndicadoresHospitalares> pesquisarQuery4IndicadoresClinica(Date mes) {
		DetachedCriteria cri = DetachedCriteria.forClass(AinIndicadoresHospitalares.class);
		cri.createAlias(AinIndicadoresHospitalares.Fields.CLINICA.toString(),
				AinIndicadoresHospitalares.Fields.CLINICA.toString());

		cri.add(Restrictions.isNotNull(AinIndicadoresHospitalares.Fields.CLINICA.toString()));
		cri.add(Restrictions.isNull(AinIndicadoresHospitalares.Fields.UNIDADE_FUNCIONAL.toString()
				+ "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		cri.add(Restrictions.isNull(AinIndicadoresHospitalares.Fields.ESPECIALIDADE.toString()));
		cri.add(Restrictions.isNull(AinIndicadoresHospitalares.Fields.SERVIDOR_VIN_CODIGO
				.toString()));
		cri.add(Restrictions.eq(AinIndicadoresHospitalares.Fields.TIPO_UNIDADE.toString(),
				DominioTipoUnidade.A));

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mes);
		int lastDate = calendar.getActualMaximum(Calendar.DATE);
		calendar.set(Calendar.DATE, lastDate);

		cri.add(Restrictions.between(
				AinIndicadoresHospitalares.Fields.COMPETENCIA_INTERNACAO.toString(), mes,
				calendar.getTime()));

		return executeCriteria(cri);
	}

	public List<AinIndicadoresHospitalares> pesquisarQuery3IndicadoresClinica(Date mes) {
		DetachedCriteria cri = DetachedCriteria.forClass(AinIndicadoresHospitalares.class);
		cri.createAlias(AinIndicadoresHospitalares.Fields.CLINICA.toString(),
				AinIndicadoresHospitalares.Fields.CLINICA.toString());

		cri.add(Restrictions.isNotNull(AinIndicadoresHospitalares.Fields.CLINICA.toString()));
		cri.add(Restrictions.isNull(AinIndicadoresHospitalares.Fields.UNIDADE_FUNCIONAL.toString()
				+ "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		cri.add(Restrictions.isNull(AinIndicadoresHospitalares.Fields.ESPECIALIDADE.toString()));
		cri.add(Restrictions.isNull(AinIndicadoresHospitalares.Fields.SERVIDOR_VIN_CODIGO
				.toString()));
		cri.add(Restrictions.eq(AinIndicadoresHospitalares.Fields.TIPO_UNIDADE.toString(),
				DominioTipoUnidade.A));

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mes);
		int lastDate = calendar.getActualMaximum(Calendar.DATE);
		calendar.set(Calendar.DATE, lastDate);

		cri.add(Restrictions.between(
				AinIndicadoresHospitalares.Fields.COMPETENCIA_INTERNACAO.toString(), mes,
				calendar.getTime()));

		return executeCriteria(cri);
	}

	public List<AinIndicadoresHospitalares> pesquisarQuery2IndicadoresClinica(Date mes) {
		DetachedCriteria cri = DetachedCriteria.forClass(AinIndicadoresHospitalares.class);
		cri.createAlias(AinIndicadoresHospitalares.Fields.CLINICA.toString(),
				AinIndicadoresHospitalares.Fields.CLINICA.toString());

		cri.add(Restrictions.isNotNull(AinIndicadoresHospitalares.Fields.CLINICA.toString()));
		cri.add(Restrictions.isNull(AinIndicadoresHospitalares.Fields.UNIDADE_FUNCIONAL.toString()
				+ "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		cri.add(Restrictions.isNull(AinIndicadoresHospitalares.Fields.ESPECIALIDADE.toString()));
		cri.add(Restrictions.isNull(AinIndicadoresHospitalares.Fields.SERVIDOR_VIN_CODIGO
				.toString()));
		cri.add(Restrictions.eq(AinIndicadoresHospitalares.Fields.TIPO_UNIDADE.toString(),
				DominioTipoUnidade.U));

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mes);
		int lastDate = calendar.getActualMaximum(Calendar.DATE);
		calendar.set(Calendar.DATE, lastDate);

		cri.add(Restrictions.between(
				AinIndicadoresHospitalares.Fields.COMPETENCIA_INTERNACAO.toString(), mes,
				calendar.getTime()));

		return executeCriteria(cri);
	}

	public List<AinIndicadoresHospitalares> pesquisarQuery1IndicadoresClinica(Date mes) {
		DetachedCriteria cri = DetachedCriteria.forClass(AinIndicadoresHospitalares.class);
		cri.createAlias(AinIndicadoresHospitalares.Fields.CLINICA.toString(),
				AinIndicadoresHospitalares.Fields.CLINICA.toString());

		cri.add(Restrictions.isNotNull(AinIndicadoresHospitalares.Fields.CLINICA.toString()));
		cri.add(Restrictions.isNull(AinIndicadoresHospitalares.Fields.UNIDADE_FUNCIONAL.toString()
				+ "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		cri.add(Restrictions.isNull(AinIndicadoresHospitalares.Fields.ESPECIALIDADE.toString()));
		cri.add(Restrictions.isNull(AinIndicadoresHospitalares.Fields.SERVIDOR_VIN_CODIGO
				.toString()));
		cri.add(Restrictions.eq(AinIndicadoresHospitalares.Fields.TIPO_UNIDADE.toString(),
				DominioTipoUnidade.U));

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mes);
		int lastDate = calendar.getActualMaximum(Calendar.DATE);
		calendar.set(Calendar.DATE, lastDate);

		cri.add(Restrictions.between(
				AinIndicadoresHospitalares.Fields.COMPETENCIA_INTERNACAO.toString(), mes,
				calendar.getTime()));

		return executeCriteria(cri);
	}

	/**
	 * Método para verificar se existem registros para geração do relatório de
	 * Indicadores por Unidade.
	 * 
	 * @param tipoUnidade
	 * @param mesCompetencia
	 * @return
	 */
	public Integer obterNumeroOcorrenciasIndicadoresUnidade(DominioTipoUnidade tipoUnidade,
			Date mesCompetencia) {
		DetachedCriteria criteria = this.criarCriteriaIndicadoresUnidade(tipoUnidade,
				mesCompetencia);

		criteria.setProjection(Projections.projectionList().add(
				Projections.count(AinIndicadoresHospitalares.Fields.SEQUENCE.toString())));

		Integer numeroOcorrencias = (Integer) executeCriteriaUniqueResult(criteria);

		return numeroOcorrencias;
	}

	/**
	 * Método para verificar se existem registros para geração do relatório de
	 * Indicadores por Unidade.
	 * 
	 * @param tipoUnidade
	 * @param mesCompetencia
	 * @return
	 */
	public Integer obterNumeroOcorrenciasIndicadoresGerais(Date mesCompetencia) {
		DetachedCriteria criteria = this.criarCriteriaIndicadoresGerais(mesCompetencia);

		criteria.setProjection(Projections.projectionList().add(
				Projections.count(AinIndicadoresHospitalares.Fields.SEQUENCE.toString())));

		Integer numeroOcorrencias = (Integer) executeCriteriaUniqueResult(criteria);

		return numeroOcorrencias;
	}

	/**
	 * Método para criar a Criteria base para as consultas de indicadores gerais
	 * 
	 * @param tipoUnidade
	 * @param mesCompetencia
	 * @return
	 */
	private DetachedCriteria criarCriteriaIndicadoresGerais(Date mesCompetencia) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinIndicadoresHospitalares.class);

		Calendar dataInicial = Calendar.getInstance();
		dataInicial.setTime(mesCompetencia);
		dataInicial.set(Calendar.DAY_OF_MONTH, 1);
		Calendar dataFinal = Calendar.getInstance();
		dataFinal.setTime(mesCompetencia);
		dataFinal.set(Calendar.DAY_OF_MONTH, dataFinal.getActualMaximum(Calendar.DAY_OF_MONTH));

		criteria.add(Restrictions.between(
				AinIndicadoresHospitalares.Fields.COMPETENCIA_INTERNACAO.toString(),
				dataInicial.getTime(), dataFinal.getTime()));

		return criteria;
	}

}
