package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioGrupoConvenioPesquisaLeitos;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAinPesqLeitos;

public class VAinPesqLeitosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAinPesqLeitos> {

	private static final long serialVersionUID = 910775571034612793L;

	/**
	 * Cria a pesquisa de acordo com o Data Block VPL1 do oracle forms
	 * 
	 * @dbtables VAinPesqLeitos select
	 * 
	 * @param status
	 * @param acomodacao
	 * @param clinica
	 * @param convenio
	 * @param unidade
	 * @param leito
	 * @param grupoConvenio
	 * @param ala
	 * @param andar
	 * @param infeccao
	 * @return
	 */
	@SuppressWarnings({"PMD.NPathComplexity"})
	private DetachedCriteria createPesquisaCriteriaVPL1(AinTiposMovimentoLeito status, AinAcomodacoes acomodacao, 
			AghClinicas clinica, FatConvenioSaude convenio, AghUnidadesFuncionais unidade, 
			AinLeitos leito, DominioGrupoConvenioPesquisaLeitos grupoConvenio, AghAla ala, Integer andar,
			DominioSimNao infeccao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VAinPesqLeitos.class);

		criteria.createAlias(VAinPesqLeitos.Fields.INTERNACAO.toString(), VAinPesqLeitos.Fields.INTERNACAO.toString());
		criteria.createAlias(VAinPesqLeitos.Fields.TIPO_MOVIMENTO.toString(), VAinPesqLeitos.Fields.TIPO_MOVIMENTO.toString());
		criteria.createAlias(VAinPesqLeitos.Fields.INTERNACAO.toString() + "." + AinInternacao.Fields.ESPECIALIDADE.toString(),
				AinInternacao.Fields.ESPECIALIDADE.toString());
		criteria.createAlias(VAinPesqLeitos.Fields.PACIENTE.toString(), VAinPesqLeitos.Fields.PACIENTE.toString());
		criteria.createAlias(VAinPesqLeitos.Fields.CONVENIOS.toString(), VAinPesqLeitos.Fields.CONVENIOS.toString());

		if (status != null) {
			criteria.add(Restrictions.eq(
					VAinPesqLeitos.Fields.TIPO_MOVIMENTO.toString() + "." + AinTiposMovimentoLeito.Fields.CODIGO.toString(),
					status.getCodigo()));
		}

		if (acomodacao != null) {
			criteria.add(Restrictions.eq(VAinPesqLeitos.Fields.ACM_SEQ.toString(), acomodacao.getSeq()));
		}

		if (clinica != null) {
			criteria.add(Restrictions.eq(VAinPesqLeitos.Fields.CLC_CODIGO.toString(), clinica.getCodigo().byteValue()));
		}

		if (convenio != null) {
			criteria.add(Restrictions.eq(VAinPesqLeitos.Fields.CNV_CODIGO.toString(), convenio.getCodigo()));
		}

		if (unidade != null) {
			criteria.add(Restrictions.eq(VAinPesqLeitos.Fields.UNF_SEQ.toString(), unidade.getSeq()));
		}

		if (grupoConvenio != null && StringUtils.isNotBlank(grupoConvenio.getDescricao())) {
			criteria.add(Restrictions.or(Restrictions.eq(VAinPesqLeitos.Fields.GRUPO_CONVENIO.toString(), grupoConvenio.getSigla()),
					Restrictions.eq(VAinPesqLeitos.Fields.GRUPO_CONV_PART.toString(), grupoConvenio.getSigla())));
		}

		if (infeccao != null) {
			criteria.add(Restrictions.eq(VAinPesqLeitos.Fields.IND_EXCLUSIV_INFECCAO.toString(), infeccao.toString()));
		}

		if (andar != null) {
			criteria.add(Restrictions.eq(VAinPesqLeitos.Fields.ANDAR.toString(), andar.byteValue()));
		}

		if (ala != null) {
			criteria.add(Restrictions.eq(VAinPesqLeitos.Fields.IND_ALA.toString(), ala.toString()));
		}

		if (leito != null) {
			criteria.add(Restrictions.eq(VAinPesqLeitos.Fields.LTO_LTO_ID.toString(), leito.getLeitoID()));
		}

		criteria.add(Restrictions.isNotNull(VAinPesqLeitos.Fields.INT_SEQ.toString()));

		criteria.setProjection(Projections.projectionList().add(Projections.property(VAinPesqLeitos.Fields.LTO_LTO_ID.toString()))
				.add(Projections.property(VAinPesqLeitos.Fields.IND_ALA.toString()))
				.add(Projections.property(VAinPesqLeitos.Fields.CLC_CODIGO.toString()))
				.add(Projections.property(AinInternacao.Fields.ESPECIALIDADE.toString() + ".sigla"))
				.add(Projections.property(VAinPesqLeitos.Fields.DTHR_LANCAMENTO.toString()))
				.add(Projections.property(VAinPesqLeitos.Fields.DSP_NOME.toString()))
				.add(Projections.property(VAinPesqLeitos.Fields.DSP_SEXO.toString()))
				.add(Projections.property(VAinPesqLeitos.Fields.DSP_PRONTUARIO.toString()))
				.add(Projections.property(VAinPesqLeitos.Fields.DPS_DESCRICAO.toString()))
				.add(Projections.property(VAinPesqLeitos.Fields.INTERNACAO.toString())));

		return criteria;
	}

	/**
	 * Cria a pesquisa de acordo com o Data Block VPL do oracle forms
	 * 
	 * @dbtables VAinPesqLeitos select
	 * 
	 * @param status
	 * @param acomodacao
	 * @param clinica
	 * @param convenio
	 * @param unidade
	 * @param leito
	 * @param grupoConvenio
	 * @param ala
	 * @param andar
	 * @param infeccao
	 * @return
	 */
	private DetachedCriteria createPesquisaCriteriaVPL(AinTiposMovimentoLeito status, AinAcomodacoes acomodacao, AghClinicas clinica, AghUnidadesFuncionais unidade,
			AinLeitos leito, AghAla ala, Integer andar, DominioSimNao infeccao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VAinPesqLeitos.class);

		criteria.createAlias(VAinPesqLeitos.Fields.ACOMODACAO.toString(), VAinPesqLeitos.Fields.ACOMODACAO.toString());
		criteria.createAlias(VAinPesqLeitos.Fields.SERVIDOR.toString(), VAinPesqLeitos.Fields.SERVIDOR.toString());
		criteria.createAlias(VAinPesqLeitos.Fields.TIPO_MOVIMENTO.toString(), VAinPesqLeitos.Fields.TIPO_MOVIMENTO.toString());
		criteria.createAlias(VAinPesqLeitos.Fields.SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString(),
				RapServidores.Fields.PESSOA_FISICA.toString());

		if (status != null) {
			criteria.add(Restrictions.eq(
					VAinPesqLeitos.Fields.TIPO_MOVIMENTO.toString() + "." + AinTiposMovimentoLeito.Fields.CODIGO.toString(),
					status.getCodigo()));
		}

		if (acomodacao != null) {
			criteria.add(Restrictions.eq(VAinPesqLeitos.Fields.ACM_SEQ.toString(), acomodacao.getSeq()));
		}

		if (clinica != null) {
			criteria.add(Restrictions.eq(VAinPesqLeitos.Fields.CLC_CODIGO.toString(), clinica.getCodigo().byteValue()));
		}

		if (unidade != null) {
			criteria.add(Restrictions.eq(VAinPesqLeitos.Fields.UNF_SEQ.toString(), unidade.getSeq()));
		}

		if (infeccao != null) {
			criteria.add(Restrictions.eq(VAinPesqLeitos.Fields.IND_EXCLUSIV_INFECCAO.toString(), infeccao.toString()));
		}

		if (andar != null) {
			criteria.add(Restrictions.eq(VAinPesqLeitos.Fields.ANDAR.toString(), andar.byteValue()));
		}

		if (ala != null) {
			criteria.add(Restrictions.eq(VAinPesqLeitos.Fields.IND_ALA.toString(), ala.toString()));
		}

		if (leito != null) {
			criteria.add(Restrictions.eq(VAinPesqLeitos.Fields.LTO_LTO_ID.toString(), leito.getLeitoID()));
		}

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property(VAinPesqLeitos.Fields.LTO_LTO_ID.toString()))
				.add(Projections.property(VAinPesqLeitos.Fields.IND_ALA.toString()))
				.add(Projections.property(VAinPesqLeitos.Fields.CLC_CODIGO.toString()))
				.add(Projections.property(VAinPesqLeitos.Fields.DPS_ACOM_DESCRICAO.toString()))
				.add(Projections.property(VAinPesqLeitos.Fields.SEXO_OCUPACAO.toString()))
				.add(Projections.property(VAinPesqLeitos.Fields.DTHR_LANCAMENTO.toString()))
				.add(Projections.property(VAinPesqLeitos.Fields.GRUPO_MVTO_LEITO.toString()))
				.add(Projections.property(VAinPesqLeitos.Fields.JUSTIFICATIVA.toString()))
				.add(Projections.property(VAinPesqLeitos.Fields.TCL_DESCRICAO.toString()))
				.add(Projections.property(RapServidores.Fields.PESSOA_FISICA.toString() + "."
						+ RapPessoasFisicas.Fields.NOME.toString()))
				.add(Projections.property(VAinPesqLeitos.Fields.TIPO_MOVIMENTO.toString() + "."
						+ AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString()))
				.add(Projections.property(VAinPesqLeitos.Fields.INTERNACAO.toString())));

		return criteria;
	}

	public List<Object[]> pesquisaCriteriaVPL1OrdemLeito(AinTiposMovimentoLeito status, AinAcomodacoes acomodacao, 
			AghClinicas clinica, FatConvenioSaude convenio, AghUnidadesFuncionais unidade, 
			AinLeitos leito, DominioGrupoConvenioPesquisaLeitos grupoConvenio, AghAla ala, Integer andar,
			DominioSimNao infeccao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		DetachedCriteria criteria = this.createPesquisaCriteriaVPL1(status, acomodacao, clinica, convenio, unidade, leito,
				grupoConvenio, ala, andar, infeccao);
		criteria.addOrder(Order.asc(VAinPesqLeitos.Fields.LTO_LTO_ID.toString()));

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisaCriteriaVPL1Count(AinTiposMovimentoLeito status, AinAcomodacoes acomodacao, 
			AghClinicas clinica, FatConvenioSaude convenio, AghUnidadesFuncionais unidade, 
			AinLeitos leito, DominioGrupoConvenioPesquisaLeitos grupoConvenio, AghAla ala, Integer andar, DominioSimNao infeccao) {

		DetachedCriteria criteria = this.createPesquisaCriteriaVPL1(status, acomodacao, clinica, convenio, unidade, leito,
				grupoConvenio, ala, andar, infeccao);
		return executeCriteriaCount(criteria);
	}
	
	public List<Object[]> pesquisaCriteriaVPLOrdemLeito(AinTiposMovimentoLeito status, AinAcomodacoes acomodacao, AghClinicas clinica, AghUnidadesFuncionais unidade,
			AinLeitos leito, AghAla ala, Integer andar, DominioSimNao infeccao, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {

		DetachedCriteria criteria = this.createPesquisaCriteriaVPL(status, acomodacao, clinica, unidade, leito, ala, andar, infeccao);
		criteria.addOrder(Order.asc(VAinPesqLeitos.Fields.LTO_LTO_ID.toString()));

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisaCriteriaVPLCount(AinTiposMovimentoLeito status, AinAcomodacoes acomodacao, 
			AghClinicas clinica, FatConvenioSaude convenio, AghUnidadesFuncionais unidade, 
			AinLeitos leito, AghAla ala, Integer andar, DominioSimNao infeccao) {

		DetachedCriteria criteria = this.createPesquisaCriteriaVPL(status, acomodacao, clinica, unidade, leito, ala, andar, infeccao);
		return executeCriteriaCount(criteria);
	}

}
