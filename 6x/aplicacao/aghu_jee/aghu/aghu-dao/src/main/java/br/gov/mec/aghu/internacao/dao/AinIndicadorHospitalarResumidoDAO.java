package br.gov.mec.aghu.internacao.dao;

import java.io.Serializable;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoIndicador;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinIndicadorHospitalarResumido;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

@SuppressWarnings({ "PMD.ExcessiveClassLength"})
public class AinIndicadorHospitalarResumidoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinIndicadorHospitalarResumido> {

	private static final long serialVersionUID = -3964702346983722306L;

	final static String SEPARADOR = ".";

	/**
	 * Obter Indicadores por tipo de indicador.
	 * 
	 * @param mesCompetencia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<AinIndicadorHospitalarResumido> obterTotaisIndicadoresUnidade(Date mesCompetencia,
			Date mesCompetenciaFim, DominioTipoIndicador tipoIndicador,
			AghUnidadesFuncionais unidadeFuncional) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinIndicadorHospitalarResumido.class);

		criteria.add(Restrictions.eq(
				AinIndicadorHospitalarResumido.Fields.TIPO_INDICADOR.toString(), tipoIndicador));

		List<AinIndicadorHospitalarResumido> res = null;

		switch (tipoIndicador) {
		case G:
			if (mesCompetenciaFim != null) {
				criteria.add(Restrictions.between(
						AinIndicadorHospitalarResumido.Fields.COMPETENCIA_INTERNACAO.toString(),
						mesCompetencia, mesCompetenciaFim));
				criteria.addOrder(Order
						.asc(AinIndicadorHospitalarResumido.Fields.COMPETENCIA_INTERNACAO
								.toString()));
			} else {
				criteria.add(Restrictions.eq(
						AinIndicadorHospitalarResumido.Fields.COMPETENCIA_INTERNACAO.toString(),
						mesCompetencia));
			}

			return executeCriteria(criteria);
		case U:
			criteria.add(Restrictions.eq(
					AinIndicadorHospitalarResumido.Fields.COMPETENCIA_INTERNACAO.toString(),
					mesCompetencia));

			criteria.createAlias(
					AinIndicadorHospitalarResumido.Fields.UNIDADE_FUNCIONAL.toString(),
					AinIndicadorHospitalarResumido.Fields.UNIDADE_FUNCIONAL.toString());

			if (unidadeFuncional != null && unidadeFuncional.getSeq() != null) {
				criteria.add(Restrictions.eq(
						AinIndicadorHospitalarResumido.Fields.UNIDADE_FUNCIONAL.toString(),
						unidadeFuncional));
			}

			criteria.addOrder(Order.asc(AinIndicadorHospitalarResumido.Fields.UNIDADE_FUNCIONAL
					.toString() + SEPARADOR + AghUnidadesFuncionais.Fields.DESCRICAO.toString()));

			res = executeCriteria(criteria);

			Collections.sort(res, new AreaFuncionalIgComparator());

			return res;
		case C:
			criteria.add(Restrictions.eq(
					AinIndicadorHospitalarResumido.Fields.COMPETENCIA_INTERNACAO.toString(),
					mesCompetencia));

			criteria.createAlias(AinIndicadorHospitalarResumido.Fields.CLINICA.toString(),
					AinIndicadorHospitalarResumido.Fields.CLINICA.toString());
			criteria.addOrder(Order.asc(AinIndicadorHospitalarResumido.Fields.CLINICA.toString()
					+ SEPARADOR + AghClinicas.Fields.DESCRICAO.toString()));

			res = executeCriteria(criteria);

			return res;
		case E:
			criteria.add(Restrictions.eq(
					AinIndicadorHospitalarResumido.Fields.COMPETENCIA_INTERNACAO.toString(),
					mesCompetencia));

			criteria.createAlias(AinIndicadorHospitalarResumido.Fields.ESPECIALIDADE.toString(),
					AinIndicadorHospitalarResumido.Fields.ESPECIALIDADE.toString());
			criteria.addOrder(Order.asc(AinIndicadorHospitalarResumido.Fields.ESPECIALIDADE
					.toString()
					+ SEPARADOR
					+ AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));

			res = executeCriteria(criteria);

			return res;
		default:
			break;
		}

		return executeCriteria(criteria);
	}

	/**
	 * Método para obter a capacidade instalada do hospital.
	 * 
	 * HQL
	 * 
	 * @return
	 */
	public Map<Integer, AinIndicadorHospitalarResumido> obterCapacidadeInstaladaGeral(
			Integer quantidadeDiasMes) {

		Map<Integer, AinIndicadorHospitalarResumido> map = new HashMap<Integer, AinIndicadorHospitalarResumido>();

		StringBuilder sb = new StringBuilder(300);
		sb.append("select count(lto.leitoID) ");
		sb.append("	from AinLeitos lto ");
		sb.append("	where lto.indSituacao = '").append(DominioSituacao.A.toString()).append("' ");
		sb.append("	and lto.unidadeFuncional.seq not in ( ");
		sb.append(" select cuf.id.unfSeq from AghCaractUnidFuncionais cuf ");
		sb.append(" where cuf.id.unfSeq = lto.unidadeFuncional.seq and (");
		sb.append(" cuf.id.caracteristica = '").append(
				ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA.getCodigo());
		sb.append("' or cuf.id.caracteristica = '")
				.append(ConstanteAghCaractUnidFuncionais.UNID_PESQUISA.getCodigo()).append("')");
		sb.append(") ");

		Integer capcInst = NumberUtil.getIntegerFromNumericObject(createQuery(
				sb.toString()).getSingleResult());

		AinIndicadorHospitalarResumido indRes = new AinIndicadorHospitalarResumido();
		indRes.setCapacidadeInstalada(capcInst.intValue());
		indRes.setLeitoDia(capcInst.intValue() * quantidadeDiasMes);
		map.put(1, indRes);

		return map;
	}

	/**
	 * Método para obter a capacidade instalada do hospital.
	 * 
	 * HQL
	 * 
	 * @return
	 */
	public Map<Integer, AinIndicadorHospitalarResumido> obterCapacidadeInstaladaPorUnidadeFuncional(
			Integer quantidadeDiasMes) {

		Map<Integer, AinIndicadorHospitalarResumido> map = new HashMap<Integer, AinIndicadorHospitalarResumido>();

		StringBuilder sb = new StringBuilder(400);
		sb.append("select lto.unidadeFuncional.seq, ");
		sb.append("count(lto.leitoID) ");
		sb.append("	from AinLeitos lto ");
		sb.append("	where lto.indSituacao = '").append(DominioSituacao.A.toString()).append("' ");
		sb.append("	and lto.unidadeFuncional.seq not in ( ");
		sb.append(" select cuf.id.unfSeq from AghCaractUnidFuncionais cuf ");
		sb.append(" where cuf.id.unfSeq = lto.unidadeFuncional.seq and (");
		sb.append(" cuf.id.caracteristica = '").append(
				ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA.getCodigo());
		sb.append("' or cuf.id.caracteristica = '")
				.append(ConstanteAghCaractUnidFuncionais.UNID_PESQUISA.getCodigo()).append("')");
		sb.append(") ");
		sb.append("group by lto.unidadeFuncional.seq");

		Iterator<?> it = createQuery(sb.toString()).getResultList().iterator();

		while (it.hasNext()) {
			Object[] item = (Object[]) it.next();
			Short unfSeq = NumberUtil.getShortFromNumericObject(item[0]);
			Integer capcInst = NumberUtil.getIntegerFromNumericObject(item[1]);

			AinIndicadorHospitalarResumido indRes = new AinIndicadorHospitalarResumido();
			indRes.setCapacidadeInstalada(capcInst.intValue());
			indRes.setLeitoDia(capcInst.intValue() * quantidadeDiasMes);

			map.put(unfSeq.intValue(), indRes);
		}

		return map;
	}

	/**
	 * Obter a Numero de saídas do mês.
	 * 
	 * @param mesCompetencia
	 * @return
	 */
	public void obterSaidasGeral(Date mesCompetencia, Map<Integer, AinIndicadorHospitalarResumido> map) {
		DetachedCriteria criteria = this.obterCriteriaTotalSaidas(mesCompetencia);

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AghAtendimentos.Fields.UNF_SEQ.toString()));

		criteria.setProjection(p);

		//Foi retirado o teste "verificarCaracteristicaDaUnidadeFuncional" pois o mesmo jah feito na query
		Long count = this.executeCriteriaCount(criteria);

		AinIndicadorHospitalarResumido ind = map.get(1);
		ind.setTotalSaidas(Integer.valueOf(count.intValue()));// Que caquinha, deveria mudar o tipo do pojo, por enquanto fica assim.

		map.put(1, ind);
	}

	/**
	 * Obter CRITERIA de total de saídas.
	 * 
	 * @param mesCompetencia
	 * @return
	 */
	private DetachedCriteria obterCriteriaTotalSaidas(Date mesCompetencia) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "unf");
		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(), "internacao");
		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(),
				"unidadeFuncional");

		criteria.add(Restrictions.between(AghAtendimentos.Fields.INTERNACAO.toString() + SEPARADOR
				+ AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString(),
				DateUtil.obterDataInicioCompetencia(mesCompetencia),
				DateUtil.obterDataFimCompetencia(mesCompetencia)));

		// SubCriteria
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class);
		subCriteria.setProjection(Property.forName(AghCaractUnidFuncionais.Fields.UNF_SEQ
				.toString()));
		subCriteria.add(Property.forName(AghCaractUnidFuncionais.Fields.UNF_SEQ.toString())
				.eqProperty(AghAtendimentos.Fields.UNF_SEQ.toString()));
		subCriteria.add(Property.forName(
				AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString()).in(
				new ConstanteAghCaractUnidFuncionais[] {
						ConstanteAghCaractUnidFuncionais.UNID_PESQUISA,
						ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA }));

		criteria.add(Property.forName(AghAtendimentos.Fields.UNF_SEQ.toString())
				.notIn(subCriteria));

		return criteria;
	}

	/**
	 * Obter a Numero de saídas do mês separados por clinica.
	 * 
	 * @param mesCompetencia
	 * @return
	 */
	public void obterSaidasPorEspecialidade(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map) {

		DetachedCriteria criteria = this.obterCriteriaTotalSaidas(mesCompetencia);

		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), "esp1");
		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString() + SEPARADOR
				+ AghEspecialidades.Fields.ESPECIALIDADE_GENERICA.toString(), "esp2",
				Criteria.LEFT_JOIN);

		ProjectionList p = Projections.projectionList();
		p.add(Projections.groupProperty("esp1".toString() + SEPARADOR
				+ AghEspecialidades.Fields.SEQ.toString()));
		p.add(Projections.groupProperty("esp2".toString() + SEPARADOR
				+ AghEspecialidades.Fields.SEQ.toString()));

		p.add(Projections.count(AghAtendimentos.Fields.SEQ.toString()));

		criteria.setProjection(p);

		Iterator it = this.executeCriteria(criteria).iterator();

		while (it.hasNext()) {
			Object[] item = (Object[]) it.next();
			Short esp1 = NumberUtil.getShortFromNumericObject(item[0]);
			Short esp2 = NumberUtil.getShortFromNumericObject(item[1]);
			Integer totalSaidas = NumberUtil.getIntegerFromNumericObject(item[2]);

			Short esp = esp2 != null ? esp2 : esp1;

			AinIndicadorHospitalarResumido indRes = map.get(esp.intValue());

			if (indRes == null) {
				indRes = new AinIndicadorHospitalarResumido();
			}

			indRes.setTotalSaidas(totalSaidas != null ? (indRes.getTotalSaidas() != null ? indRes
					.getTotalSaidas() : 0) + totalSaidas.intValue() : 0);

			map.put(esp.intValue(), indRes);
		}
	}

	/**
	 * Obter a Numero de saídas do mês separados por clinica.
	 * 
	 * @param mesCompetencia
	 * @return
	 */
	public void obterSaidasPorClinica(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map) {

		DetachedCriteria criteria = this.obterCriteriaTotalSaidas(mesCompetencia);

		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), "esp1");

		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString() + SEPARADOR
				+ AghEspecialidades.Fields.ESPECIALIDADE_GENERICA.toString(), "esp2",
				Criteria.LEFT_JOIN);

		ProjectionList p = Projections.projectionList();
		p.add(Projections.groupProperty("esp1".toString() + SEPARADOR
				+ AghEspecialidades.Fields.CLINICA_CODIGO.toString()));

		p.add(Projections.groupProperty("esp2".toString() + SEPARADOR
				+ AghEspecialidades.Fields.CLINICA_CODIGO.toString()));

		p.add(Projections.count(AghAtendimentos.Fields.SEQ.toString()));

		criteria.setProjection(p);

		Iterator it = this.executeCriteria(criteria).iterator();

		while (it.hasNext()) {
			Object[] item = (Object[]) it.next();
			Short clinica1 = NumberUtil.getShortFromNumericObject(item[0]);
			Short clinica2 = NumberUtil.getShortFromNumericObject(item[1]);
			Integer totalSaidas = NumberUtil.getIntegerFromNumericObject(item[2]);

			Short clinica = clinica2 != null ? clinica2 : clinica1;

			AinIndicadorHospitalarResumido indRes = map.get(clinica.intValue());

			if (indRes == null) {
				indRes = new AinIndicadorHospitalarResumido();
			}

			indRes.setTotalSaidas(totalSaidas != null ? (indRes.getTotalSaidas() != null ? indRes
					.getTotalSaidas() : 0) + totalSaidas.intValue() : 0);

			map.put(clinica.intValue(), indRes);
		}
	}

	/**
	 * Obter a Numero de saídas do mês separados por área funcional.
	 * 
	 * @param mesCompetencia
	 * @return
	 */
	public void obterSaidasPorAreaFuncional(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map) {
		DetachedCriteria criteria = this.obterCriteriaTotalSaidas(mesCompetencia);

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AghAtendimentos.Fields.UNF_SEQ.toString()));

		criteria.setProjection(p);

		criteria.addOrder(Order.asc(AghAtendimentos.Fields.UNF_SEQ.toString()));

		List<Short> seqsUnidadeFuncional = this.executeCriteria(criteria);
		Map<Short, Integer> totalSaidaUnidade = new HashMap<Short, Integer>();

		Integer count = 0;
		Short unidadeFuncional = null;
		AinIndicadorHospitalarResumido indicadoresResumidos = null;
		for (Short seqUnidadeFuncional : seqsUnidadeFuncional) {
			if (!seqUnidadeFuncional.equals(unidadeFuncional)) {
				if (count > 0) {
					totalSaidaUnidade.put(unidadeFuncional, count);
					indicadoresResumidos = map.get(unidadeFuncional.intValue());
					if (indicadoresResumidos == null) {
						indicadoresResumidos = new AinIndicadorHospitalarResumido();
					}

					indicadoresResumidos.setTotalSaidas(count);
					map.put(unidadeFuncional.intValue(), indicadoresResumidos);

				}
				unidadeFuncional = seqUnidadeFuncional;
				count = 0;
			}
			//Foi retirado o teste "verificarCaracteristicaDaUnidadeFuncional" pois o mesmo jah feito na query
			count++;
		}
		// Insere ultima area funcional
		if (count > 0) {
			indicadoresResumidos = map.get(unidadeFuncional.intValue());
			if (indicadoresResumidos == null) {
				indicadoresResumidos = new AinIndicadorHospitalarResumido();
			}

			indicadoresResumidos.setTotalSaidas(count);
			map.put(unidadeFuncional.intValue(), indicadoresResumidos);
		}

	}

	/**
	 * Obter CRITERIA de Pacientes Dia (Parte I).
	 * 
	 * @param mesCompetencia
	 * @return
	 */
	private DetachedCriteria obterCriteriaPacientesDiaParteI(Date mesCompetencia,
			AghParametros parametroAltaC, AghParametros parametroAltaD) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class, "mvi");

		criteria.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString(),
				AinMovimentosInternacao.Fields.INTERNACAO.toString());

		criteria.createAlias(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString(),
				AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString());

		criteria.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString() + SEPARADOR
				+ AinInternacao.Fields.ATENDIMENTO.toString(),
				AinInternacao.Fields.ATENDIMENTO.toString());

		criteria.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString() + SEPARADOR
				+ AinInternacao.Fields.CONVENIO.toString(),
				AinInternacao.Fields.CONVENIO.toString());

		// Restrictions
		criteria.add(Restrictions.le(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.DT_INTERNACAO.toString(),
				DateUtil.obterDataFimCompetencia(mesCompetencia)));
		
		//TODO remover hardcode do codigo de tipo de internação.
		criteria.add(Restrictions.in(
				AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO.toString(), new Integer[] { 1, 2,
						3, 11, 12, 13, 14, 15 }));

		criteria.add(Restrictions.or(Restrictions.ge(
				AinMovimentosInternacao.Fields.INTERNACAO.toString() + SEPARADOR
						+ AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString(),
				DateUtil.obterDataInicioCompetencia(mesCompetencia)), Restrictions
				.isNull(AinMovimentosInternacao.Fields.INTERNACAO.toString() + SEPARADOR
						+ AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString())));

		criteria.add(Restrictions.lt(
				AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(),
				DateUtil.obterDataFimCompetencia(mesCompetencia)));

		// SubCriteria
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class);
		subCriteria.setProjection(Property.forName(AghCaractUnidFuncionais.Fields.UNF_SEQ
				.toString()));
		subCriteria.add(Property.forName(AghCaractUnidFuncionais.Fields.UNF_SEQ.toString())
				.eqProperty(AghAtendimentos.Fields.UNF_SEQ.toString()));
		subCriteria.add(Property.forName(
				AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString()).in(
				new ConstanteAghCaractUnidFuncionais[] {
						ConstanteAghCaractUnidFuncionais.UNID_PESQUISA,
						ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA }));
		criteria.add(Property.forName(AghAtendimentos.Fields.UNF_SEQ.toString())
				.notIn(subCriteria));

		return criteria;
	}

	/**
	 * Obter CRITERIA de Pacientes Dia (Parte II).
	 * 
	 * @param mesCompetencia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private DetachedCriteria obterCriteriaPacientesDiaParteII(Date mesCompetencia,
			AghParametros parametroAltaC, AghParametros parametroAltaD) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class, "mvi");

		criteria.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString(),
				AinMovimentosInternacao.Fields.INTERNACAO.toString());

		criteria.createAlias(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString(),
				AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString());

		criteria.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString() + SEPARADOR
				+ AinInternacao.Fields.ATENDIMENTO.toString(),
				AinInternacao.Fields.ATENDIMENTO.toString());

		criteria.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString() + SEPARADOR
				+ AinInternacao.Fields.CONVENIO.toString(),
				AinInternacao.Fields.CONVENIO.toString());

		// Restrictions
		criteria.add(Restrictions.between(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.DT_INTERNACAO.toString(),
				DateUtil.obterDataInicioCompetencia(mesCompetencia),
				DateUtil.obterDataFimCompetencia(mesCompetencia)));

		criteria.add(Restrictions.lt(
				AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(),
				DateUtil.obterDataFimCompetencia(mesCompetencia)));
		
		//TODO remover hardcode do codigo de tipo de internação.
		criteria.add(Restrictions.in(
				AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO.toString(), new Integer[] { 1, 2,
						3, 11, 12, 13, 14, 15 }));

		List<String> restricao = new ArrayList<String>();
		restricao.add(parametroAltaC.getVlrTexto());
		restricao.add(parametroAltaD.getVlrTexto());
		criteria.add(Restrictions.in(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.TAM_CODIGO.toString(), restricao));

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class);
		subCriteria.setProjection(Property.forName(AghCaractUnidFuncionais.Fields.UNF_SEQ
				.toString()));
		subCriteria.add(Property.forName(AghCaractUnidFuncionais.Fields.UNF_SEQ.toString())
				.eqProperty(AghAtendimentos.Fields.UNF_SEQ.toString()));
		subCriteria.add(Property.forName(
				AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString()).in(
				new ConstanteAghCaractUnidFuncionais[] {
						ConstanteAghCaractUnidFuncionais.UNID_PESQUISA,
						ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA }));
		criteria.add(Property.forName(AghAtendimentos.Fields.UNF_SEQ.toString())
				.notIn(subCriteria));

		subCriteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		subCriteria.setProjection(Projections.min(AinMovimentosInternacao.Fields.CRIADO_EM
				.toString()));
		subCriteria.add(Property.forName(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString())
				.eqProperty(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString()));
		criteria.add(Property.forName(AinMovimentosInternacao.Fields.CRIADO_EM.toString()).notIn(
				subCriteria));

		return criteria;
	}

	/**
	 * Obter o número de pacientes dia no mes por Area Funcional.
	 * 
	 * @param mesCompetencia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void obterPacientesDiaPorAreaFuncional(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map, AghParametros parametroAltaC,
			AghParametros parametroAltaD) throws ApplicationBusinessException {

		// Parte I
		DetachedCriteria criteria = this.obterCriteriaPacientesDiaParteI(mesCompetencia,
				parametroAltaC, parametroAltaD);

		ProjectionList p = Projections.projectionList();
		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL_SEQ
				.toString()));

		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.SEQ.toString()));

		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString()));

		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO
				.toString()));

		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO
				.toString()));

		criteria.setProjection(p);

		Iterator it = this.executeCriteria(criteria).iterator();

		Date dtInicial = DateUtil.obterDataInicioCompetencia(mesCompetencia);
		Date dtFinal = DateUtil.obterDataFimCompetencia(mesCompetencia);

		while (it.hasNext()) {
			Object[] item = (Object[]) it.next();
			Short unfSeq = NumberUtil.getShortFromNumericObject(item[0]);

			AinIndicadorHospitalarResumido indRes = map.get(unfSeq.intValue());

			if (indRes == null) {
				indRes = new AinIndicadorHospitalarResumido();
			}

			Integer intSeq = NumberUtil.getIntegerFromNumericObject(item[1]);
			Date dtHrAltaMedica = (Date) item[2];
			Short tmiSeq = NumberUtil.getShortFromNumericObject(item[3]);
			Date dtHrLancamento = (Date) item[4];

			Date dtHrFinal = this.obterDtHtFinal(intSeq, dtHrLancamento, dtHrAltaMedica);

			Date date1 = null;
			Date date2 = null;
			Date g = null;
			Date l = null;

			if (DateUtil.validaDataMaior(dtHrFinal, dtFinal)) {
				g = dtHrFinal;
			} else {
				g = dtFinal;
			}

			if (g != null && dtFinal != null && DateValidator.validarMesmoDia(g, dtFinal)) {
				if (DateValidator.validaDataMenor(dtHrFinal, dtInicial)) {
					l = dtHrFinal;
				} else {
					l = dtInicial;
				}

				if (l != null && dtHrFinal != null && DateValidator.validarMesmoDia(l, dtHrFinal)) {
					date1 = dtInicial;
				} else {
					date1 = dtHrFinal;
				}
			}

			if (date1 == null) {
				if (tmiSeq.intValue() == 21) {
					date1 = null;
				} else {
					date1 = DateUtil.adicionaMeses(dtInicial, 1);
				}
			}

			if (DateValidator.validaDataMenor(dtHrLancamento, dtInicial)) {
				l = dtHrLancamento;
			} else {
				l = dtInicial;
			}

			if (l != null && dtHrLancamento != null && DateValidator.validarMesmoDia(l, dtHrLancamento)) {
				date2 = dtInicial;
			} else if (l != null && dtInicial != null && DateValidator.validarMesmoDia(l, dtInicial)) {
				date2 = dtHrLancamento;
			}

			int pacDias = 0;
			pacDias = DateUtil.calcularDiasEntreDatas(DateUtil.truncaData(date2),
					DateUtil.truncaData(date1));

			indRes.setQuantidadePaciente((indRes.getQuantidadePaciente() != null ? indRes
					.getQuantidadePaciente() : 0) + pacDias);

			map.put(unfSeq.intValue(), indRes);
		}

		// Parte II
		criteria = this.obterCriteriaPacientesDiaParteII(mesCompetencia, parametroAltaC,
				parametroAltaD);

		p = Projections.projectionList();
		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL_SEQ
				.toString()));
		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.DT_INTERNACAO));
		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.DATA_HORA_ALTA_MEDICA));

		criteria.setProjection(p);

		it = this.executeCriteria(criteria).iterator();

		while (it.hasNext()) {
			Object[] item = (Object[]) it.next();
			Short unfSeq = NumberUtil.getShortFromNumericObject(item[0]);

			AinIndicadorHospitalarResumido indRes = map.get(unfSeq.intValue());

			if (indRes == null) {
				indRes = new AinIndicadorHospitalarResumido();
			}

			Calendar dtHrInternacao = Calendar.getInstance();
			dtHrInternacao.setTime((Date) item[1]);

			Calendar dtHrAltaMedica = Calendar.getInstance();
			dtHrAltaMedica.setTime((Date) item[2]);

			if (dtHrInternacao.get(Calendar.DAY_OF_MONTH) == dtHrAltaMedica
					.get(Calendar.DAY_OF_MONTH)) {
				indRes.setQuantidadePaciente((indRes.getQuantidadePaciente() != null ? indRes
						.getQuantidadePaciente() : 0) + 1);
			}

			map.put(unfSeq.intValue(), indRes);
		}

	}

	/**
	 * Obter o número de pacientes dia no mes por Area Funcional.
	 * 
	 * @param mesCompetencia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void obterPacientesDiaPorEspecialidade(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map, AghParametros parametroAltaC,
			AghParametros parametroAltaD) throws ApplicationBusinessException {

		// Parte I
		DetachedCriteria criteria = this.obterCriteriaPacientesDiaParteI(mesCompetencia,
				parametroAltaC, parametroAltaD);

		criteria.createAlias(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString(), "esp1");

		criteria.createAlias(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString() + SEPARADOR
				+ AghEspecialidades.Fields.ESPECIALIDADE_GENERICA.toString(), "esp2",
				Criteria.LEFT_JOIN);

		ProjectionList p = Projections.projectionList();
		p.add(Projections.groupProperty("esp1".toString() + SEPARADOR
				+ AghEspecialidades.Fields.SEQ.toString()));
		p.add(Projections.groupProperty("esp2".toString() + SEPARADOR
				+ AghEspecialidades.Fields.SEQ.toString()));

		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.SEQ.toString()));

		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString()));

		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO
				.toString()));

		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO
				.toString()));

		criteria.setProjection(p);

		Iterator it = this.executeCriteria(criteria).iterator();

		Date dtInicial = DateUtil.obterDataInicioCompetencia(mesCompetencia);
		Date dtFinal = DateUtil.obterDataFimCompetencia(mesCompetencia);

		while (it.hasNext()) {
			Object[] item = (Object[]) it.next();
			Short esp1 = NumberUtil.getShortFromNumericObject(item[0]);
			Short esp2 = NumberUtil.getShortFromNumericObject(item[1]);

			Short clinica = esp2 != null ? esp2 : esp1;

			AinIndicadorHospitalarResumido indRes = map.get(clinica.intValue());

			if (indRes == null) {
				indRes = new AinIndicadorHospitalarResumido();
			}

			Integer intSeq = NumberUtil.getIntegerFromNumericObject(item[2]);
			Date dtHrAltaMedica = (Date) item[3];
			Short tmiSeq = NumberUtil.getShortFromNumericObject(item[4]);
			Date dtHrLancamento = (Date) item[5];

			Date dtHrFinal = this.obterDtHtFinal(intSeq, dtHrLancamento, dtHrAltaMedica);

			Date date1 = null;
			Date date2 = null;
			Date g = null;
			Date l = null;

			if (DateUtil.validaDataMaior(dtHrFinal, dtFinal)) {
				g = dtHrFinal;
			} else {
				g = dtFinal;
			}

			if (g != null && dtFinal != null && DateValidator.validarMesmoDia(g, dtFinal)) {
				if (DateValidator.validaDataMenor(dtHrFinal, dtInicial)) {
					l = dtHrFinal;
				} else {
					l = dtInicial;
				}

				if (l != null && dtHrFinal != null && DateValidator.validarMesmoDia(l, dtHrFinal)) {
					date1 = dtInicial;
				} else {
					date1 = dtHrFinal;
				}
			}

			if (date1 == null) {
				if (tmiSeq.intValue() == 21) {
					date1 = null;
				} else {
					date1 = DateUtil.adicionaMeses(dtInicial, 1);
				}
			}

			if (DateValidator.validaDataMenor(dtHrLancamento, dtInicial)) {
				l = dtHrLancamento;
			} else {
				l = dtInicial;
			}

			if (l != null && dtHrLancamento != null && DateValidator.validarMesmoDia(l, dtHrLancamento)) {
				date2 = dtInicial;
			} else if (l != null && dtInicial != null && DateValidator.validarMesmoDia(l, dtInicial)) {
				date2 = dtHrLancamento;
			}

			int pacDias = 0;
			pacDias = DateUtil.calcularDiasEntreDatas(DateUtil.truncaData(date2),
					DateUtil.truncaData(date1));

			indRes.setQuantidadePaciente((indRes.getQuantidadePaciente() != null ? indRes
					.getQuantidadePaciente() : 0) + pacDias);

			map.put(clinica.intValue(), indRes);
		}

		// Parte II
		criteria = this.obterCriteriaPacientesDiaParteII(mesCompetencia, parametroAltaC,
				parametroAltaD);

		criteria.createAlias(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString(), "esp1");

		criteria.createAlias(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString() + SEPARADOR
				+ AghEspecialidades.Fields.ESPECIALIDADE_GENERICA.toString(), "esp2",
				Criteria.LEFT_JOIN);

		p = Projections.projectionList();
		p.add(Projections.groupProperty("esp1".toString() + SEPARADOR
				+ AghEspecialidades.Fields.SEQ.toString()));
		p.add(Projections.groupProperty("esp2".toString() + SEPARADOR
				+ AghEspecialidades.Fields.SEQ.toString()));

		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.DT_INTERNACAO));
		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.DATA_HORA_ALTA_MEDICA));

		criteria.setProjection(p);

		it = this.executeCriteria(criteria).iterator();

		while (it.hasNext()) {
			Object[] item = (Object[]) it.next();
			Short esp1 = NumberUtil.getShortFromNumericObject(item[0]);
			Short esp2 = NumberUtil.getShortFromNumericObject(item[1]);

			Short esp = esp2 != null ? esp2 : esp1;

			AinIndicadorHospitalarResumido indRes = map.get(esp.intValue());

			if (indRes == null) {
				indRes = new AinIndicadorHospitalarResumido();
			}

			Calendar dtHrInternacao = Calendar.getInstance();
			dtHrInternacao.setTime((Date) item[2]);

			Calendar dtHrAltaMedica = Calendar.getInstance();
			dtHrAltaMedica.setTime((Date) item[3]);

			if (dtHrInternacao.get(Calendar.DAY_OF_MONTH) == dtHrAltaMedica
					.get(Calendar.DAY_OF_MONTH)) {
				indRes.setQuantidadePaciente((indRes.getQuantidadePaciente() != null ? indRes
						.getQuantidadePaciente() : 0) + 1);
			}

			map.put(esp.intValue(), indRes);
		}

	}

	/**
	 * Obter o número de pacientes dia no mes por Clinica.
	 * 
	 * @param mesCompetencia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void obterPacientesDiaPorClinica(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map, AghParametros parametroAltaC,
			AghParametros parametroAltaD) throws ApplicationBusinessException {

		// Parte I
		DetachedCriteria criteria = this.obterCriteriaPacientesDiaParteI(mesCompetencia,
				parametroAltaC, parametroAltaD);

		criteria.createAlias(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString(), "esp1");

		criteria.createAlias(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString() + SEPARADOR
				+ AghEspecialidades.Fields.ESPECIALIDADE_GENERICA.toString(), "esp2",
				Criteria.LEFT_JOIN);

		ProjectionList p = Projections.projectionList();
		p.add(Projections.groupProperty("esp1".toString() + SEPARADOR
				+ AghEspecialidades.Fields.CLINICA_CODIGO.toString()));

		p.add(Projections.groupProperty("esp2".toString() + SEPARADOR
				+ AghEspecialidades.Fields.CLINICA_CODIGO.toString()));

		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.SEQ.toString()));

		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString()));

		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO
				.toString()));

		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO
				.toString()));

		criteria.setProjection(p);

		Iterator it = this.executeCriteria(criteria).iterator();

		Date dtInicial = DateUtil.obterDataInicioCompetencia(mesCompetencia);
		Date dtFinal = DateUtil.obterDataFimCompetencia(mesCompetencia);

		while (it.hasNext()) {
			Object[] item = (Object[]) it.next();
			Short clinica1 = NumberUtil.getShortFromNumericObject(item[0]);
			Short clinica2 = NumberUtil.getShortFromNumericObject(item[1]);

			Short clinica = clinica2 != null ? clinica2 : clinica1;

			AinIndicadorHospitalarResumido indRes = map.get(clinica.intValue());

			if (indRes == null) {
				indRes = new AinIndicadorHospitalarResumido();
			}

			Integer intSeq = NumberUtil.getIntegerFromNumericObject(item[2]);
			Date dtHrAltaMedica = (Date) item[3];
			Short tmiSeq = NumberUtil.getShortFromNumericObject(item[4]);
			Date dtHrLancamento = (Date) item[5];

			Date dtHrFinal = this.obterDtHtFinal(intSeq, dtHrLancamento, dtHrAltaMedica);

			Date date1 = null;
			Date date2 = null;
			Date g = null;
			Date l = null;

			if (DateUtil.validaDataMaior(dtHrFinal, dtFinal)) {
				g = dtHrFinal;
			} else {
				g = dtFinal;
			}

			if (g != null && dtFinal != null && DateValidator.validarMesmoDia(g, dtFinal)) {
				if (DateValidator.validaDataMenor(dtHrFinal, dtInicial)) {
					l = dtHrFinal;
				} else {
					l = dtInicial;
				}

				if (l != null && dtHrFinal != null && DateValidator.validarMesmoDia(l, dtHrFinal)) {
					date1 = dtInicial;
				} else {
					date1 = dtHrFinal;
				}
			}

			if (date1 == null) {
				if (tmiSeq.intValue() == 21) {
					date1 = null;
				} else {
					date1 = DateUtil.adicionaMeses(dtInicial, 1);
				}
			}

			if (DateValidator.validaDataMenor(dtHrLancamento, dtInicial)) {
				l = dtHrLancamento;
			} else {
				l = dtInicial;
			}

			if (l != null && dtHrLancamento != null && DateValidator.validarMesmoDia(l, dtHrLancamento)) {
				date2 = dtInicial;
			} else if (l != null && dtInicial != null && DateValidator.validarMesmoDia(l, dtInicial)) {
				date2 = dtHrLancamento;
			}

			int pacDias = 0;
			pacDias = DateUtil.calcularDiasEntreDatas(DateUtil.truncaData(date2),
					DateUtil.truncaData(date1));

			indRes.setQuantidadePaciente((indRes.getQuantidadePaciente() != null ? indRes
					.getQuantidadePaciente() : 0) + pacDias);

			map.put(clinica.intValue(), indRes);
		}

		// Parte II
		criteria = this.obterCriteriaPacientesDiaParteII(mesCompetencia, parametroAltaC,
				parametroAltaC);

		criteria.createAlias(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString(), "esp1");

		criteria.createAlias(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString() + SEPARADOR
				+ AghEspecialidades.Fields.ESPECIALIDADE_GENERICA.toString(), "esp2",
				Criteria.LEFT_JOIN);

		p = Projections.projectionList();
		p.add(Projections.groupProperty("esp1".toString() + SEPARADOR
				+ AghEspecialidades.Fields.CLINICA_CODIGO.toString()));

		p.add(Projections.groupProperty("esp2".toString() + SEPARADOR
				+ AghEspecialidades.Fields.CLINICA_CODIGO.toString()));

		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.DT_INTERNACAO));
		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.DATA_HORA_ALTA_MEDICA));

		criteria.setProjection(p);

		it = this.executeCriteria(criteria).iterator();

		while (it.hasNext()) {
			Object[] item = (Object[]) it.next();
			Short clinica1 = NumberUtil.getShortFromNumericObject(item[0]);
			Short clinica2 = NumberUtil.getShortFromNumericObject(item[1]);

			Short clinica = clinica2 != null ? clinica2 : clinica1;

			AinIndicadorHospitalarResumido indRes = map.get(clinica.intValue());

			if (indRes == null) {
				indRes = new AinIndicadorHospitalarResumido();
			}

			Calendar dtHrInternacao = Calendar.getInstance();
			dtHrInternacao.setTime((Date) item[2]);

			Calendar dtHrAltaMedica = Calendar.getInstance();
			dtHrAltaMedica.setTime((Date) item[3]);

			if (dtHrInternacao.get(Calendar.DAY_OF_MONTH) == dtHrAltaMedica
					.get(Calendar.DAY_OF_MONTH)) {
				indRes.setQuantidadePaciente((indRes.getQuantidadePaciente() != null ? indRes
						.getQuantidadePaciente() : 0) + 1);
			}

			map.put(clinica.intValue(), indRes);
		}

	}

	/**
	 * ORADB PACKAGE AINK_IH_IG FUNCTION DTHR_FINAL_M
	 * 
	 * @param intSeq
	 * @param dtHrLancamento
	 * @param dtHrAltaMedica
	 * @return
	 */
	private Date obterDtHtFinal(Integer intSeq, Date dtHrLancamento, Date dtHrAltaMedica) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class, "mvi");

		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), intSeq));

		criteria.add(Restrictions.gt(
				AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), dtHrLancamento));
		
		//TODO remover hardcode do codigo de tipo de internação.
		criteria.add(Restrictions.in(
				AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO.toString(), new Integer[] { 2, 11,
						12, 13, 14, 15, 21 }));

		// SubCriteria
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		subCriteria.setProjection(Projections
				.min(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));
		subCriteria.add(Property.forName(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString()).eq(
				intSeq));
		subCriteria.add(Property.forName(
				AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()).gt(dtHrLancamento));
		criteria.add(Property.forName(
				AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()).eq(subCriteria));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO.toString()));
		p.add(Projections.property(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));

		criteria.setProjection(p);

		List<Object[]> list = executeCriteria(criteria);

		if (list != null && !list.isEmpty()) {
			Object[] obj = list.get(0);
			Integer tmiSeq = NumberUtil.getIntegerFromNumericObject(obj[0]);
			if (tmiSeq == 21) {
				return dtHrAltaMedica;
			} else {
				return (Date) obj[1];
			}
		}

		return null;
	}

	/**
	 * Obter o número de pacientes dia no mes.
	 * 
	 * @param mesCompetencia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void obterPacientesDiaGeral(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map, AghParametros parametroAltaC,
			AghParametros parametroAltaD) throws ApplicationBusinessException {

		// Parte I
		DetachedCriteria criteria = this.obterCriteriaPacientesDiaParteI(mesCompetencia,
				parametroAltaC, parametroAltaD);

		ProjectionList p = Projections.projectionList();

		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.SEQ.toString()));

		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString()));

		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO
				.toString()));

		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO
				.toString()));

		criteria.setProjection(p);

		Iterator it = this.executeCriteria(criteria).iterator();

		Date dtInicial = DateUtil.obterDataInicioCompetencia(mesCompetencia);
		Date dtFinal = DateUtil.obterDataFimCompetencia(mesCompetencia);

		Integer totPacDias = 0;

		while (it.hasNext()) {
			Object[] item = (Object[]) it.next();

			Integer intSeq = NumberUtil.getIntegerFromNumericObject(item[0]);
			Date dtHrAltaMedica = (Date) item[1];
			Short tmiSeq = NumberUtil.getShortFromNumericObject(item[2]);
			Date dtHrLancamento = (Date) item[3];

			Date dtHrFinal = this.obterDtHtFinal(intSeq, dtHrLancamento, dtHrAltaMedica);

			Date date1 = null;
			Date date2 = null;
			Date g = null;
			Date l = null;

			if (DateUtil.validaDataMaior(dtHrFinal, dtFinal)) {
				g = dtHrFinal;
			} else {
				g = dtFinal;
			}

			if (g != null && dtFinal != null && DateValidator.validarMesmoDia(g, dtFinal)) {
				if (DateValidator.validaDataMenor(dtHrFinal, dtInicial)) {
					l = dtHrFinal;
				} else {
					l = dtInicial;
				}

				if (l != null && dtHrFinal != null && DateValidator.validarMesmoDia(l, dtHrFinal)) {
					date1 = dtInicial;
				} else {
					date1 = dtHrFinal;
				}
			}

			if (date1 == null) {
				if (tmiSeq.intValue() == 21) {
					date1 = null;
				} else {
					date1 = DateUtil.adicionaMeses(dtInicial, 1);
				}
			}

			if (DateValidator.validaDataMenor(dtHrLancamento, dtInicial)) {
				l = dtHrLancamento;
			} else {
				l = dtInicial;
			}

			if (l != null && dtHrLancamento != null && DateValidator.validarMesmoDia(l, dtHrLancamento)) {
				date2 = dtInicial;
			} else if (l != null && dtInicial != null && DateValidator.validarMesmoDia(l, dtInicial)) {
				date2 = dtHrLancamento;
			}

			int pacDias = 0;
			pacDias = DateUtil.calcularDiasEntreDatas(DateUtil.truncaData(date2),
					DateUtil.truncaData(date1));

			totPacDias += pacDias;
		}

		// Parte II
		criteria = this.obterCriteriaPacientesDiaParteII(mesCompetencia, parametroAltaC,
				parametroAltaD);

		p = Projections.projectionList();
		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.DT_INTERNACAO));
		p.add(Projections.groupProperty(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ SEPARADOR + AinInternacao.Fields.DATA_HORA_ALTA_MEDICA));

		criteria.setProjection(p);

		it = this.executeCriteria(criteria).iterator();

		while (it.hasNext()) {
			Object[] item = (Object[]) it.next();

			Calendar dtHrInternacao = Calendar.getInstance();
			dtHrInternacao.setTime((Date) item[0]);

			Calendar dtHrAltaMedica = Calendar.getInstance();
			dtHrAltaMedica.setTime((Date) item[1]);

			if (dtHrInternacao.get(Calendar.DAY_OF_MONTH) == dtHrAltaMedica
					.get(Calendar.DAY_OF_MONTH)) {
				totPacDias += 1;
			}

		}

		// Atualiza objeto.
		AinIndicadorHospitalarResumido ind = map.get(1);
		ind.setQuantidadePaciente(totPacDias);
		map.put(1, ind);
	}

	/**
	 * Obter criteria de total de Obitos.
	 * 
	 * @param mesCompetencia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private DetachedCriteria obterCriteriaTotalObitos(Date mesCompetencia,
			AghParametros parametroAltaC, AghParametros parametroAltaD) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);

		criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(),
				AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString());

		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(),
				AghAtendimentos.Fields.INTERNACAO.toString());

		List<String> restricao = new ArrayList<String>();
		restricao.add(parametroAltaC.getVlrTexto());
		restricao.add(parametroAltaD.getVlrTexto());
		criteria.add(Restrictions.in(AghAtendimentos.Fields.INTERNACAO.toString() + SEPARADOR
				+ AinInternacao.Fields.TAM_CODIGO.toString(), restricao));

		criteria.add(Restrictions.between(AghAtendimentos.Fields.INTERNACAO.toString() + SEPARADOR
				+ AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString(),
				DateUtil.obterDataInicioCompetencia(mesCompetencia),
				DateUtil.obterDataFimCompetencia(mesCompetencia)));

		// TODO: Adicionar restrição para paciente cadáver.
		// and AINC_VER_PAC_CADAVER (int.seq) <> 'S' -- despreza paciente
		// cadáver

		// SubCriteria
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class);
		subCriteria.setProjection(Property.forName(AghCaractUnidFuncionais.Fields.UNF_SEQ
				.toString()));
		subCriteria.add(Property.forName(AghCaractUnidFuncionais.Fields.UNF_SEQ.toString())
				.eqProperty(AghAtendimentos.Fields.UNF_SEQ.toString()));
		subCriteria.add(Property.forName(
				AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString()).in(
				new ConstanteAghCaractUnidFuncionais[] {
						ConstanteAghCaractUnidFuncionais.UNID_PESQUISA,
						ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA }));
		criteria.add(Property.forName(AghAtendimentos.Fields.UNF_SEQ.toString())
				.notIn(subCriteria));

		return criteria;
	}

	/**
	 * Obter a Numero de Obitos em mês agrupados por clínica.
	 * 
	 * @param mesCompetencia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void obterObitosPorEspecialidade(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map, AghParametros parametroAltaC,
			AghParametros parametroAltaD) throws ApplicationBusinessException {

		DetachedCriteria criteria = this.obterCriteriaTotalObitos(mesCompetencia, parametroAltaC,
				parametroAltaD);

		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), "esp1");

		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString() + SEPARADOR
				+ AghEspecialidades.Fields.ESPECIALIDADE_GENERICA.toString(), "esp2",
				Criteria.LEFT_JOIN);

		ProjectionList p = Projections.projectionList();
		p.add(Projections.groupProperty("esp1".toString() + SEPARADOR
				+ AghEspecialidades.Fields.SEQ.toString()));
		p.add(Projections.groupProperty("esp2".toString() + SEPARADOR
				+ AghEspecialidades.Fields.SEQ.toString()));

		p.add(Projections.count(AghAtendimentos.Fields.SEQ.toString()));

		criteria.setProjection(p);

		Iterator it = this.executeCriteria(criteria).iterator();

		while (it.hasNext()) {
			Object[] item = (Object[]) it.next();
			Short esp1 = NumberUtil.getShortFromNumericObject(item[0]);
			Short esp2 = NumberUtil.getShortFromNumericObject(item[1]);
			Integer qtdeObitos = NumberUtil.getIntegerFromNumericObject(item[2]);

			Short esp = esp2 != null ? esp2 : esp1;

			AinIndicadorHospitalarResumido indRes = map.get(esp.intValue());

			if (indRes == null) {
				indRes = new AinIndicadorHospitalarResumido();
			}

			indRes.setQuantidadeObito(qtdeObitos != null ? (indRes.getQuantidadeObito() != null ? indRes
					.getQuantidadeObito() : 0)
					+ qtdeObitos.intValue()
					: 0);

			map.put(esp.intValue(), indRes);
		}

	}

	/**
	 * Obter a Numero de Obitos em mês agrupados por clínica.
	 * 
	 * @param mesCompetencia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void obterObitosPorClinica(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map, AghParametros parametroAltaC,
			AghParametros parametroAltaD) throws ApplicationBusinessException {

		DetachedCriteria criteria = this.obterCriteriaTotalObitos(mesCompetencia, parametroAltaC,
				parametroAltaD);

		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), "esp1");

		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString() + SEPARADOR
				+ AghEspecialidades.Fields.ESPECIALIDADE_GENERICA.toString(), "esp2",
				Criteria.LEFT_JOIN);

		ProjectionList p = Projections.projectionList();
		p.add(Projections.groupProperty("esp1".toString() + SEPARADOR
				+ AghEspecialidades.Fields.CLINICA_CODIGO.toString()));
		p.add(Projections.groupProperty("esp2".toString() + SEPARADOR
				+ AghEspecialidades.Fields.CLINICA_CODIGO.toString()));

		p.add(Projections.count(AghAtendimentos.Fields.SEQ.toString()));

		criteria.setProjection(p);

		Iterator it = this.executeCriteria(criteria).iterator();

		while (it.hasNext()) {
			Object[] item = (Object[]) it.next();
			Short clinica1 = NumberUtil.getShortFromNumericObject(item[0]);
			Short clinica2 = NumberUtil.getShortFromNumericObject(item[1]);
			Integer qtdeObitos = NumberUtil.getIntegerFromNumericObject(item[2]);

			Short clinica = clinica2 != null ? clinica2 : clinica1;

			AinIndicadorHospitalarResumido indRes = map.get(clinica.intValue());

			if (indRes == null) {
				indRes = new AinIndicadorHospitalarResumido();
			}

			indRes.setQuantidadeObito(qtdeObitos != null ? (indRes.getQuantidadeObito() != null ? indRes
					.getQuantidadeObito() : 0)
					+ qtdeObitos.intValue()
					: 0);

			map.put(clinica.intValue(), indRes);
		}

	}

	/**
	 * Obter a Numero de Obitos em mês agrupados por área funcional.
	 * 
	 * @param mesCompetencia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void obterObitosPorAreaFuncional(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map, AghParametros parametroAltaC,
			AghParametros parametroAltaD) throws ApplicationBusinessException {

		DetachedCriteria criteria = this.obterCriteriaTotalObitos(mesCompetencia, parametroAltaC,
				parametroAltaD);

		ProjectionList p = Projections.projectionList();
		p.add(Projections.groupProperty(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString()
				+ SEPARADOR + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		p.add(Projections.count(AghAtendimentos.Fields.SEQ.toString()));
		criteria.setProjection(p);

		Iterator it = this.executeCriteria(criteria).iterator();

		while (it.hasNext()) {
			Object[] item = (Object[]) it.next();
			Short unfSeq = NumberUtil.getShortFromNumericObject(item[0]);
			Integer qtdeObitos = NumberUtil.getIntegerFromNumericObject(item[1]);

			AinIndicadorHospitalarResumido indRes = map.get(unfSeq.intValue());

			if (indRes == null) {
				indRes = new AinIndicadorHospitalarResumido();
			}

			indRes.setQuantidadeObito(qtdeObitos != null ? qtdeObitos.intValue() : 0);

			map.put(unfSeq.intValue(), indRes);
		}

	}

	/**
	 * Obter a Numero de Obitos em mês.
	 * 
	 * @param mesCompetencia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void obterObitosGeral(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map, AghParametros parametroAltaC,
			AghParametros parametroAltaD) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);

		ProjectionList p = Projections.projectionList();
		p.add(Projections.count(AinInternacao.Fields.SEQ.toString()));
		criteria.setProjection(p);

		List<String> restricao = new ArrayList<String>();
		restricao.add(parametroAltaC.getVlrTexto());
		restricao.add(parametroAltaD.getVlrTexto());
		criteria.add(Restrictions.in(AinInternacao.Fields.TAM_CODIGO.toString(), restricao));

		criteria.add(Restrictions.between(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString(),
				DateUtil.obterDataInicioCompetencia(mesCompetencia),
				DateUtil.obterDataFimCompetencia(mesCompetencia)));

		// TODO: Adicionar restrição para paciente cadáver.
		// and AINC_VER_PAC_CADAVER (int.seq) <> 'S' -- despreza paciente
		// cadáver
		// SubCriteria

		// TODO: Adicionar restrição para Caracteristica de Unidade Funcional.

		List<Long> list = this.executeCriteria(criteria);
		Long qtdeObitos = 0l;
		if (list != null && !list.isEmpty()) {
			qtdeObitos = list.get(0);
		}

		AinIndicadorHospitalarResumido ind = map.get(1);
		ind.setQuantidadeObito(qtdeObitos.intValue());

		map.put(1, ind);
	}

	/**
	 * Obter o número de transferencias por Área Funcional.
	 * 
	 * @param mesCompetencia
	 * @param map
	 * @return
	 */
	public Map<Integer, AinIndicadorHospitalarResumido> obterTransferenciasPorAreaFuncional(
			Date mesCompetencia, Map<Integer, AinIndicadorHospitalarResumido> map) {

		// Intervalo de datas
		String dtInicial, dtFinal = null;

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		dtInicial = sdf.format(DateUtil.obterDataInicioCompetencia(mesCompetencia));
		dtFinal = sdf.format(DateUtil.obterDataFimCompetencia(mesCompetencia));

		StringBuilder sb = new StringBuilder(1300);

		sb.append(" select unf.seq, sum(1)");
		sb.append(" from agh.ain_movimentos_internacao mvi ,");
		sb.append(" 	agh.agh_unidades_funcionais unf ");
		sb.append(" where mvi.dthr_lancamento between TO_DATE('");
		sb.append(dtInicial);
		sb.append("', 'DD/MM/YYYY HH24:MI:SS') and TO_DATE('");
		sb.append(dtFinal);
		sb.append("', 'DD/MM/YYYY HH24:MI:SS') ");

		sb.append(" and unf.seq = (select a.unf_seq as unf_seq from agh.ain_movimentos_internacao a where a.int_seq = mvi.int_seq and a.dthr_lancamento < mvi.dthr_lancamento ");
		sb.append(" and a.dthr_lancamento = (select max(b.dthr_lancamento) from agh.ain_movimentos_internacao b where b.int_seq = mvi.int_seq and b.dthr_lancamento < mvi.dthr_lancamento)) ");
		sb.append(" and mvi.unf_seq <> (select a.unf_seq as unf_seq from agh.ain_movimentos_internacao a where a.int_seq = mvi.int_seq and a.dthr_lancamento < mvi.dthr_lancamento ");
		sb.append(" and a.dthr_lancamento = (select max(b.dthr_lancamento) from agh.ain_movimentos_internacao b where b.int_seq = mvi.int_seq and b.dthr_lancamento < mvi.dthr_lancamento)) ");

		sb.append(" and unf.seq not in (SELECT unf_seq from agh.agh_caract_unid_funcionais where unf_seq = unf.seq and (caracteristica = 'Unid Hosp Dia' OR caracteristica = 'Unid Pesquisa')) ");
		sb.append(" and mvi.unf_seq not in (SELECT unf_seq from agh.agh_caract_unid_funcionais where unf_seq = mvi.unf_seq and (caracteristica = 'Unid Hosp Dia' OR caracteristica = 'Unid Pesquisa')) ");

		sb.append(" group by unf.seq ");

		Iterator it = createNativeQuery(sb.toString()).getResultList()
				.iterator();

		while (it.hasNext()) {
			Object[] item = (Object[]) it.next();

			Short unfSeq = NumberUtil.getShortFromNumericObject(item[0]);
			Integer total = NumberUtil.getIntegerFromNumericObject(item[1]);

			AinIndicadorHospitalarResumido indRes = map.get(unfSeq.intValue());

			if (indRes == null) {
				indRes = new AinIndicadorHospitalarResumido();
			}

			indRes.setQuantidadeTransferenciaAreaFuncional(total);

			map.put(unfSeq.intValue(), indRes);
		}

		return map;
	}

	/**
	 * Obter o número de transferencias por Clínica.
	 * 
	 * @param mesCompetencia
	 * @param map
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public Map<Integer, AinIndicadorHospitalarResumido> obterTransferenciasPorClinica(
			Date mesCompetencia, Map<Integer, AinIndicadorHospitalarResumido> map) {

		// Intervalo de datas
		String dtInicial, dtFinal = null;

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		dtInicial = sdf.format(DateUtil.obterDataInicioCompetencia(mesCompetencia));
		dtFinal = sdf.format(DateUtil.obterDataFimCompetencia(mesCompetencia));

		StringBuilder sb = new StringBuilder(3000);

		sb.append(" select ")
		.append("    ( ")
		.append("   select     ")
		.append("     case when esp2.clc_codigo is null then esp1.clc_codigo else esp2.clc_codigo end as clinica ")
		.append("   from 	 ")
		.append("     agh.ain_movimentos_internacao         a, ")
		.append("     agh.agh_especialidades                esp1 left join  ")
		.append("     agh.agh_especialidades                esp2 on (esp1.esp_seq	 = esp2.seq) ")
		.append("   where   ")
		.append("     a.int_seq = mvi.int_seq ")
		.append("   and      ")
		.append("     a.dthr_lancamento < mvi.dthr_lancamento ")
		.append("   and      ")
		.append("     esp1.seq	 = a.esp_seq     ")
		.append("   and  ")
		.append("     a.dthr_lancamento =  ")
		.append("     ( ")
		.append("     select  ")
		.append("       max(b.dthr_lancamento) ")
		.append("     from 	 ")
		.append("       agh.ain_movimentos_internacao         b, ")
		.append("       agh.agh_especialidades                esp1 left join  ")
		.append("       agh.agh_especialidades                esp2 on (esp1.esp_seq = esp2.seq) ")
		.append("     where   ")
		.append("       b.int_seq = mvi.int_seq ")
		.append("     and      ")
		.append("       b.dthr_lancamento < mvi.dthr_lancamento ")
		.append("     and      ")
		.append("       esp1.seq	 = b.esp_seq ")
		.append("     ) ")
		.append(" ), ")
		.append("    SUM(1) ")
		.append(" from  ")
		.append("    agh.ain_movimentos_internacao 	mvi ,  ")
		.append("    agh.agh_especialidades        	esp1 left join ")
		.append("    agh.agh_especialidades        	esp2 on (esp1.esp_seq=esp2.seq), ")
		.append("    agh.agh_unidades_funcionais    unf ")
		.append(" where ")
		.append("    mvi.dthr_lancamento between TO_DATE('")
		.append(dtInicial)
		.append("', 'DD/MM/YYYY HH24:MI:SS') and TO_DATE('")
		.append(dtFinal)
		.append("', 'DD/MM/YYYY HH24:MI:SS') ")
		.append(" and (case when esp2.clc_codigo is null then esp1.clc_codigo else esp2.clc_codigo end) <>  ")
		.append(" ( ")
		.append("   select     ")
		.append("     case when esp2.clc_codigo is null then esp1.clc_codigo else esp2.clc_codigo end as clinica ")
		.append("   from 	 ")
		.append("     agh.ain_movimentos_internacao         a, ")
		.append("     agh.agh_especialidades                esp1 left join  ")
		.append("     agh.agh_especialidades                esp2 on (esp1.esp_seq	 = esp2.seq) ")
		.append("   where   ")
		.append("     a.int_seq = mvi.int_seq ")
		.append("   and      ")
		.append("     a.dthr_lancamento < mvi.dthr_lancamento ")
		.append("   and      ")
		.append("     esp1.seq	 = a.esp_seq     ")
		.append("   and  ")
		.append("     a.dthr_lancamento =  ")
		.append("     ( ")
		.append("     select  ")
		.append("       max(b.dthr_lancamento) ")
		.append("     from 	 ")
		.append("       agh.ain_movimentos_internacao         b, ")
		.append("       agh.agh_especialidades                esp1 left join  ")
		.append("       agh.agh_especialidades                esp2 on (esp1.esp_seq = esp2.seq) ")
		.append("     where   ")
		.append("       b.int_seq = mvi.int_seq ")
		.append("     and      ")
		.append("       b.dthr_lancamento < mvi.dthr_lancamento ")
		.append("     and      ")
		.append("       esp1.seq	 = b.esp_seq ")
		.append("     ) ")
		.append(" ) ")
		.append(" and esp1.seq = mvi.esp_seq    ")
		.append(" and unf.seq = (select a.unf_seq as unf_seq from agh.ain_movimentos_internacao a where a.int_seq = mvi.int_seq and a.dthr_lancamento < mvi.dthr_lancamento ")
		.append("     and a.dthr_lancamento = (select max(b.dthr_lancamento) from agh.ain_movimentos_internacao b where b.int_seq = mvi.int_seq and b.dthr_lancamento < mvi.dthr_lancamento)) ")
		.append(" and unf.seq not in (SELECT unf_seq from agh.agh_caract_unid_funcionais where unf_seq = unf.seq and (caracteristica = 'Unid Hosp Dia' OR caracteristica = 'Unid Pesquisa')) ")
		.append(" and mvi.unf_seq not in (SELECT unf_seq from agh.agh_caract_unid_funcionais where unf_seq = mvi.unf_seq and (caracteristica = 'Unid Hosp Dia' OR caracteristica = 'Unid Pesquisa')) ")
		.append(" group by ")
		.append("     mvi.int_seq, mvi.dthr_lancamento ");

		Iterator it = createNativeQuery(sb.toString()).getResultList()
				.iterator();

		Map<Integer, Integer> tranfs = new HashMap<Integer, Integer>(0);

		while (it.hasNext()) {
			Object[] item = (Object[]) it.next();

			Short clcCod = NumberUtil.getShortFromNumericObject(item[0]);

			tranfs.put(clcCod.intValue(),
					tranfs.get(clcCod.intValue()) != null ? tranfs.get(clcCod.intValue()) + 1 : 1);

		}

		for (Entry<Integer, Integer> item : tranfs.entrySet()) {
			AinIndicadorHospitalarResumido indRes = map.get(item.getKey());

			if (indRes == null) {
				indRes = new AinIndicadorHospitalarResumido();
			}

			indRes.setQuantidadeTransferenciaClinica(item.getValue());
		}

		return map;
	}

	/**
	 * Obter o número de transferencias por Clínica.
	 * 
	 * @param mesCompetencia
	 * @param map
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public Map<Integer, AinIndicadorHospitalarResumido> obterTransferenciasPorEspecialidade(
			Date mesCompetencia, Map<Integer, AinIndicadorHospitalarResumido> map) {

		// Intervalo de datas
		String dtInicial, dtFinal = null;

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		dtInicial = sdf.format(DateUtil.obterDataInicioCompetencia(mesCompetencia));
		dtFinal = sdf.format(DateUtil.obterDataFimCompetencia(mesCompetencia));

		StringBuilder sb = new StringBuilder(3000);

		sb.append(" select ")
		.append("    ( ")
		.append("   select     ")
		.append("     case when esp2.seq is null then esp1.seq else esp2.seq end as clinica ")
		.append("   from 	 ")
		.append("     agh.ain_movimentos_internacao         a, ")
		.append("     agh.agh_especialidades                esp1 left join  ")
		.append("     agh.agh_especialidades                esp2 on (esp1.esp_seq	 = esp2.seq) ")
		.append("   where   ")
		.append("     a.int_seq = mvi.int_seq ")
		.append("   and      ")
		.append("     a.dthr_lancamento < mvi.dthr_lancamento ")
		.append("   and      ")
		.append("     esp1.seq	 = a.esp_seq     ")
		.append("   and  ")
		.append("     a.dthr_lancamento =  ")
		.append("     ( ")
		.append("     select  ")
		.append("       max(b.dthr_lancamento) ")
		.append("     from 	 ")
		.append("       agh.ain_movimentos_internacao         b, ")
		.append("       agh.agh_especialidades                esp1 left join  ")
		.append("       agh.agh_especialidades                esp2 on (esp1.esp_seq = esp2.seq) ")
		.append("     where   ")
		.append("       b.int_seq = mvi.int_seq ")
		.append("     and      ")
		.append("       b.dthr_lancamento < mvi.dthr_lancamento ")
		.append("     and      ")
		.append("       esp1.seq	 = b.esp_seq ")
		.append("     ) ")
		.append(" ), ")
		.append("    SUM(1) ")
		.append(" from  ")
		.append("    agh.ain_movimentos_internacao 	mvi ,  ")
		.append("    agh.agh_especialidades        	esp1 left join ")
		.append("    agh.agh_especialidades        	esp2 on (esp1.esp_seq=esp2.seq), ")
		.append("    agh.agh_unidades_funcionais    unf ")
		.append(" where ")
		.append("    mvi.dthr_lancamento between TO_DATE('")
		.append(dtInicial)
		.append("', 'DD/MM/YYYY HH24:MI:SS') and TO_DATE('")
		.append(dtFinal)
		.append("', 'DD/MM/YYYY HH24:MI:SS') ")
		.append(" and (case when esp2.seq is null then esp1.seq else esp2.seq end) <>  ")
		.append(" ( ")
		.append("   select     ")
		.append("     case when esp2.seq is null then esp1.seq else esp2.seq end as clinica ")
		.append("   from 	 ")
		.append("     agh.ain_movimentos_internacao         a, ")
		.append("     agh.agh_especialidades                esp1 left join  ")
		.append("     agh.agh_especialidades                esp2 on (esp1.esp_seq	 = esp2.seq) ")
		.append("   where   ")
		.append("     a.int_seq = mvi.int_seq ")
		.append("   and      ")
		.append("     a.dthr_lancamento < mvi.dthr_lancamento ")
		.append("   and      ")
		.append("     esp1.seq	 = a.esp_seq     ")
		.append("   and  ")
		.append("     a.dthr_lancamento =  ")
		.append("     ( ")
		.append("     select  ")
		.append("       max(b.dthr_lancamento) ")
		.append("     from 	 ")
		.append("       agh.ain_movimentos_internacao         b, ")
		.append("       agh.agh_especialidades                esp1 left join  ")
		.append("       agh.agh_especialidades                esp2 on (esp1.esp_seq = esp2.seq) ")
		.append("     where   ")
		.append("       b.int_seq = mvi.int_seq ")
		.append("     and      ")
		.append("       b.dthr_lancamento < mvi.dthr_lancamento ")
		.append("     and      ")
		.append("       esp1.seq	 = b.esp_seq ")
		.append("     ) ")
		.append(" ) ")
		.append(" and esp1.seq = mvi.esp_seq    ")
		.append(" and unf.seq = (select a.unf_seq as unf_seq from agh.ain_movimentos_internacao a where a.int_seq = mvi.int_seq and a.dthr_lancamento < mvi.dthr_lancamento ")
		.append("     and a.dthr_lancamento = (select max(b.dthr_lancamento) from agh.ain_movimentos_internacao b where b.int_seq = mvi.int_seq and b.dthr_lancamento < mvi.dthr_lancamento)) ")
		.append(" and unf.seq not in (SELECT unf_seq from agh.agh_caract_unid_funcionais where unf_seq = unf.seq and (caracteristica = 'Unid Hosp Dia' OR caracteristica = 'Unid Pesquisa')) ")
		.append(" and mvi.unf_seq not in (SELECT unf_seq from agh.agh_caract_unid_funcionais where unf_seq = mvi.unf_seq and (caracteristica = 'Unid Hosp Dia' OR caracteristica = 'Unid Pesquisa')) ")
		.append(" group by ")
		.append("     mvi.int_seq, mvi.dthr_lancamento ");

		Iterator it = createNativeQuery(sb.toString()).getResultList()
				.iterator();

		Map<Integer, Integer> tranfs = new HashMap<Integer, Integer>(0);

		while (it.hasNext()) {
			Object[] item = (Object[]) it.next();

			Short espSeq = NumberUtil.getShortFromNumericObject(item[0]);

			tranfs.put(espSeq.intValue(),
					tranfs.get(espSeq.intValue()) != null ? tranfs.get(espSeq.intValue()) + 1 : 1);

		}

		for (Entry<Integer, Integer> item : tranfs.entrySet()) {
			AinIndicadorHospitalarResumido indRes = map.get(item.getKey());

			if (indRes == null) {
				indRes = new AinIndicadorHospitalarResumido();
			}

			indRes.setQuantidadeTransferenciaEspecialidade(item.getValue());
		}

		return map;
	}

	/**
	 * Método responsável por remover registros de indicadores por competência e
	 * tipo de Indicador.
	 * 
	 * @param anoMesCompetencia
	 * @param tipoIndicador
	 */
	public void removerIndicadorPorCompetenciaTipoIndicador(Date anoMesCompetencia,
			DominioTipoIndicador tipoIndicador) {

		Query query = createQuery(
				"delete " + AinIndicadorHospitalarResumido.class.getName() + " where "
						+ AinIndicadorHospitalarResumido.Fields.COMPETENCIA_INTERNACAO.toString()
						+ " = :anoMesComp " + " and "
						+ AinIndicadorHospitalarResumido.Fields.TIPO_INDICADOR.toString()
						+ " = :tipoIndicador ");

		query.setParameter("anoMesComp", anoMesCompetencia);
		query.setParameter("tipoIndicador", tipoIndicador);
		query.executeUpdate();
		this.entityManagerClear();
	}
	
	/**
	 * @param anoMesCompetencia
	 * @return
	 */
	
	public boolean existeIndicadorParaCompetencia(Date anoMesCompetencia, DominioTipoIndicador tipoIndicador) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinIndicadorHospitalarResumido.class);
		criteria.add(Restrictions.between(AinIndicadorHospitalarResumido.Fields.COMPETENCIA_INTERNACAO.toString(),
		DateUtil.obterDataInicioCompetencia(anoMesCompetencia),
		DateUtil.obterDataFimCompetencia(anoMesCompetencia)));

		criteria.add(Restrictions.eq(AinIndicadorHospitalarResumido.Fields.TIPO_INDICADOR.toString(), tipoIndicador));

		List<AinIndicadorHospitalarResumido> indicadores = this.executeCriteria(criteria);

		if (indicadores != null && indicadores.size() > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Método para obter a sequence da entidade.
	 * 
	 * @return int
	 */
	public Integer obterIndicadoresResumidosSeq() {
		Object ultimoRegistro = this.createQuery("select max(seq) from AinIndicadorHospitalarResumido")
				.getSingleResult();

		Integer seq = 1;

		if (ultimoRegistro != null) {
			seq = (Integer) ultimoRegistro;
		}

		return ++seq;
	}

	/**
	 * Método para fazer busca do POJO AinIndicadorHospitalarResumido.
	 * 
	 * @param competenciaInternacao
	 * @param tipoIndicador
	 * @param codigoClinica
	 * @return
	 */
	public List<AinIndicadorHospitalarResumido> pesquisarIndicadorHospitalarEspecialidadePorClinica(
			Date competenciaInternacao, DominioTipoIndicador tipoIndicador,
			Integer codigoClinica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinIndicadorHospitalarResumido.class);
		
		criteria.createAlias(AinIndicadorHospitalarResumido.Fields.ESPECIALIDADE.toString(),
				"especialidade");
		//criteria.createAlias(AinIndicadorHospitalarResumido.Fields.CLINICA.toString(),
		//		"especialidade.clinica");
		
		criteria.add(Restrictions.eq(
				AinIndicadorHospitalarResumido.Fields.COMPETENCIA_INTERNACAO
						.toString(), competenciaInternacao));
		criteria.add(Restrictions.eq(
				AinIndicadorHospitalarResumido.Fields.TIPO_INDICADOR.toString(),
				tipoIndicador));

		if (codigoClinica != null) {
			String nomeCampo = "especialidade.clinica."
					+ AghClinicas.Fields.CODIGO.toString();
			criteria.add(Restrictions.eq(nomeCampo, codigoClinica));
		}
		
		return this.executeCriteria(criteria);
	}
	
	public Date obterUltimoIndicadorHospitalarGerado() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinIndicadorHospitalarResumido.class);
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.max(AinIndicadorHospitalarResumido.Fields.COMPETENCIA_INTERNACAO.toString()));
		criteria.setProjection(p);

		return (Date) executeCriteriaUniqueResult(criteria);
	}

}

class AreaFuncionalIgComparator implements Comparator<AinIndicadorHospitalarResumido>, Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -2183528775967860856L;

	public static String removeDiacriticalMarks(String string) {
		return Normalizer.normalize(string, Form.NFD).replaceAll(
				"\\p{InCombiningDiacriticalMarks}+", "");
	}

	@Override
	public int compare(AinIndicadorHospitalarResumido o1, AinIndicadorHospitalarResumido o2) {
		return removeDiacriticalMarks(o1.getUnidadeFuncional().getDescricaoIg()).compareTo(
				removeDiacriticalMarks(o2.getUnidadeFuncional().getDescricaoIg()));
	}

}