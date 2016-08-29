package br.gov.mec.aghu.configuracao.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.internacao.business.vo.ProfissionaisEscalaIntenacaoVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfissionaisEspConvenio;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class AghProfissionaisEspConvenioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghProfissionaisEspConvenio> {
	
	private static final long serialVersionUID = 2683119020392040289L;

	/**
	 * Adiciona os filtros necessários para a pesquisa da escala de
	 * profissionais da internação à query básica da view
	 * 
	 * @param vinculo
	 * @param matricula
	 * @param conselhoProfissional
	 * @param nome
	 * @param siglaEspecialidade
	 * @param convenio
	 * @return criteria
	 * @throws ApplicationBusinessException
	 */
	private DetachedCriteria montarProfissionaisEscala(Short vinculo, Integer matricula, String conselhoProfissional,
			String nomeServidor, String siglaEspecialidade, Short codigoConvenio, String descricaoConvenio, Integer[] tipoQualificacao)
			throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria.forClass(AghProfissionaisEspConvenio.class);

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AghProfissionaisEspConvenio.Fields.ID_PRE_VIN_CODIGO.toString()), "vinculoServidor")
				.add(Projections.property(AghProfissionaisEspConvenio.Fields.ID_PRE_MATRICULA.toString()), "matriculaServidor")
				.add(Projections.property("QLF." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()), "numeroRegistroConselho")
				.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()), "nomeServidor")
				.add(Projections.property("ESP." + AghEspecialidades.Fields.SIGLA.toString()), "siglaEspecialidade")
				.add(Projections.property(AghProfissionaisEspConvenio.Fields.CONVENIO.toString()), "codigoConvenio")
				.add(Projections.property("CNV." + FatConvenioSaude.Fields.DESCRICAO.toString()), "descricaoConvenio")
				.add(Projections.property("PES." + RapPessoasFisicas.Fields.CODIGO.toString()), "codigoPessoa")
				.add(Projections.property("QLF." + RapQualificacao.Fields.CODIGO.toString()), "codigoQualificacao")
				.add(Projections.property("ESP." + AghEspecialidades.Fields.SEQ.toString()), "seqEspecialidade")
				.add(Projections.property("ESC." + AinEscalasProfissionalInt.Fields.DATA_INICIO.toString()), "dataInicio")
				.add(Projections.property("ESC." + AinEscalasProfissionalInt.Fields.DATA_FIM.toString()), "dataFim"));

		// aghProfissionaisEspConvenio->aghProfEspecialidades->rapServidores->rapPessoasFisicas->rapQualificacoes->rapTipoQualificacao
		criteria.createAlias(AghProfissionaisEspConvenio.Fields.PROF_ESPECIALIDADE.toString(), "PRE", Criteria.INNER_JOIN);
		criteria.createAlias(AghProfissionaisEspConvenio.Fields.FAT_CONVENIO_SAUDE.toString(), "CNV");
		criteria.createAlias("PRE." + AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "SER", Criteria.INNER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", Criteria.INNER_JOIN);
		criteria.createAlias("PES." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "QLF", Criteria.INNER_JOIN);
		criteria.createAlias("QLF." + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), "TQF", Criteria.INNER_JOIN);
		// aghProfissionaisEspConvenio->aghProfEspecialidades->aghEspecialidades
		criteria.createAlias("PRE." + AghProfEspecialidades.Fields.ESPECIALIDADE.toString(), "ESP", Criteria.INNER_JOIN);

		// devido a melhoria na estória escala internação, foi adicionado este
		// relacionamento que não existe na view migrada.
		criteria.createAlias("escalas", "ESC", Criteria.LEFT_JOIN);

		// adicionado criterio para data inicio equal max(dataInicio) or is null
		DetachedCriteria criteriaMaxDataInicio = DetachedCriteria.forClass(AinEscalasProfissionalInt.class, "esc2");
		// maior data de inicio
		criteriaMaxDataInicio.setProjection(Projections.max(AinEscalasProfissionalInt.Fields.DATA_INICIO.toString()));
		// relacionamento com a tabela da query externa
		criteriaMaxDataInicio.add(Restrictions.eqProperty(AinEscalasProfissionalInt.Fields.ID_SERVIDOR_MATRICULA.toString(), "ESC."
				+ AinEscalasProfissionalInt.Fields.ID_SERVIDOR_MATRICULA.toString()));
		criteriaMaxDataInicio.add(Restrictions.eqProperty(AinEscalasProfissionalInt.Fields.ID_SERVIDOR_VINCULO.toString(), "ESC."
				+ AinEscalasProfissionalInt.Fields.ID_SERVIDOR_VINCULO.toString()));
		criteriaMaxDataInicio.add(Restrictions.eqProperty(AinEscalasProfissionalInt.Fields.ID_ESPECIALIDADE_SEQ.toString(), "ESC."
				+ AinEscalasProfissionalInt.Fields.ID_ESPECIALIDADE_SEQ.toString()));
		criteriaMaxDataInicio.add(Restrictions.eqProperty(AinEscalasProfissionalInt.Fields.ID_CONVENIO_CODIGO.toString(), "ESC."
				+ AinEscalasProfissionalInt.Fields.ID_CONVENIO_CODIGO.toString()));
		Criterion dataInicioEqSubqueryMax = Subqueries.propertyEq("ESC." + AinEscalasProfissionalInt.Fields.DATA_INICIO.toString(),
				criteriaMaxDataInicio);
		Criterion dataInicioIsNull = Restrictions.isNull("ESC." + AinEscalasProfissionalInt.Fields.DATA_INICIO.toString());

		criteria.add(Restrictions.disjunction().add(dataInicioEqSubqueryMax).add(dataInicioIsNull));

		// RETRIÇÕES FIXAS NA VIEW
		criteria.add(Restrictions.eq(AghProfissionaisEspConvenio.Fields.IND_INTERNA.toString(), true));

		criteria.add(Restrictions.or(Restrictions.eq("SER." + RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.A),
				Restrictions.and(Restrictions.eq("SER." + RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.P),
						Restrictions.ge("SER." + RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date()))));

		criteria.add(Restrictions.in("TQF." + RapTipoQualificacao.Fields.CODIGO.toString(), tipoQualificacao));

		criteria.add(Restrictions.isNotNull("QLF." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));

		if (vinculo != null) {
			criteria.add(Restrictions.eq(AghProfissionaisEspConvenio.Fields.ID_PRE_VIN_CODIGO.toString(), vinculo));
		}

		if (matricula != null) {
			criteria.add(Restrictions.eq(AghProfissionaisEspConvenio.Fields.ID_PRE_MATRICULA.toString(), matricula));
		}

		if (StringUtils.isNotBlank(conselhoProfissional)) {
			criteria.add(Restrictions.eq("QLF." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString(), conselhoProfissional));

		}

		if (StringUtils.isNotBlank(nomeServidor)) {
			criteria.add(Restrictions.ilike("PES." + RapPessoasFisicas.Fields.NOME.toString(), nomeServidor, MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(siglaEspecialidade)) {
			criteria.add(Restrictions.eq("ESP." + AghEspecialidades.Fields.SIGLA.toString(), siglaEspecialidade));
		}

		if (codigoConvenio != null) {
			criteria.add(Restrictions.eq(AghProfissionaisEspConvenio.Fields.CONVENIO.toString(), codigoConvenio));
		}

		if (StringUtils.isNotBlank(descricaoConvenio)) {
			criteria.add(Restrictions.ilike("CNV." + FatConvenioSaude.Fields.DESCRICAO.toString(), descricaoConvenio,
					MatchMode.ANYWHERE));
		}

		criteria.setResultTransformer(Transformers.aliasToBean(ProfissionaisEscalaIntenacaoVO.class));

		return criteria;
	}

	public List<ProfissionaisEscalaIntenacaoVO> pesquisarProfissionaisEscala(Short vinculo, Integer matricula,
			String conselhoProfissional, String nomeServidor, String siglaEspecialidade, Short codigoConvenio,
			String descricaoConvenio, Integer[] tiposQualificacao, Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) throws ApplicationBusinessException {

		DetachedCriteria criteria = montarProfissionaisEscala(vinculo, matricula, conselhoProfissional, nomeServidor,
				siglaEspecialidade, codigoConvenio, descricaoConvenio, tiposQualificacao);

		// adiciona order by
		criteria.addOrder(Order.desc("ESC." + AinEscalasProfissionalInt.Fields.DATA_FIM));
		criteria.addOrder(Order.asc(AghProfissionaisEspConvenio.Fields.ID_PRE_VIN_CODIGO.toString()));
		criteria.addOrder(Order.asc(AghProfissionaisEspConvenio.Fields.ID_PRE_MATRICULA.toString()));

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarProfissionaisEscalaCount(Short vinculo, Integer matricula, String conselhoProfissional,
			String nomeServidor, String siglaEspecialidade, Short codigoConvenio, String descricaoConvenio, Integer[] tiposQualificacao)
			throws ApplicationBusinessException {
		DetachedCriteria criteria = this.montarProfissionaisEscala(vinculo, matricula, conselhoProfissional, nomeServidor,
				siglaEspecialidade, codigoConvenio, descricaoConvenio, tiposQualificacao);
		return executeCriteriaCount(criteria);
	}
	
	public List<AghProfissionaisEspConvenio> obterAghProfissionaisEspConvenioPorProfissionalEsp(
			Integer preSerMatricula, Short preSerVinCodigo, Short preEspSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghProfissionaisEspConvenio.class);
		
		criteria.add(Restrictions.eq(AghProfissionaisEspConvenio.Fields.ID_PRE_MATRICULA.toString(), preSerMatricula));
		criteria.add(Restrictions.eq(AghProfissionaisEspConvenio.Fields.ID_PRE_VIN_CODIGO.toString(), preSerVinCodigo));
		criteria.add(Restrictions.eq(AghProfissionaisEspConvenio.Fields.ID_PRE_ESPECIALIDADE_SEQ.toString(), preEspSeq));
		
		return executeCriteria(criteria);
	}
}
