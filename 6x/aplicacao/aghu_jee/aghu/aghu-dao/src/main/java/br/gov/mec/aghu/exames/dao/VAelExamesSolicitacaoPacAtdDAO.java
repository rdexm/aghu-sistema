package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioConvenioExameSituacao;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominoOrigemMapaAmostraItemExame;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.VAelExamesSolicitacao;
import br.gov.mec.aghu.model.VAelExamesSolicitacaoPacAtd;
import br.gov.mec.aghu.core.utils.DateUtil;

public class VAelExamesSolicitacaoPacAtdDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAelExamesSolicitacaoPacAtd> {

	private static final long serialVersionUID = 3049237461284163446L;


	public List<VAelExamesSolicitacaoPacAtd> pesquisaExameSolicitacaoPacAtd(AghUnidadesFuncionais unidadeExecutora
			, Date dtHrInicial, Date dtHrFinal, Date dtHrProgramado, DominioConvenioExameSituacao convenio
			, AelSitItemSolicitacoes situacao, VAelExamesSolicitacao nomeExame
			, DominioOrigemAtendimento origemAtendimento,
			DominoOrigemMapaAmostraItemExame origemMapaTrabalho,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelExamesSolicitacaoPacAtd.class);

		this.mountCriteria(unidadeExecutora, dtHrInicial, dtHrFinal, dtHrProgramado, convenio, situacao, nomeExame
				, origemAtendimento, origemMapaTrabalho, criteria);

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/*
	 * Relatório de exames por situação
	 */
	public List<VAelExamesSolicitacaoPacAtd> pesquisaExameSolicitacaoPacAtdRel(AghUnidadesFuncionais unidadeExecutora
			, Date dtHrInicial, Date dtHrFinal, Date dtHrProgramado, DominioConvenioExameSituacao convenio
			, AelSitItemSolicitacoes situacao, VAelExamesSolicitacao nomeExame
			, DominioOrigemAtendimento origemAtendimento,
			DominoOrigemMapaAmostraItemExame origemMapaTrabalho) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VAelExamesSolicitacaoPacAtd.class);
	
		this.mountCriteria(unidadeExecutora, dtHrInicial, dtHrFinal, dtHrProgramado, convenio, situacao, nomeExame
				, origemAtendimento, origemMapaTrabalho, criteria);

		// Ordena a lista
		criteria.addOrder(Order.asc(VAelExamesSolicitacaoPacAtd.Fields.DTHR_EVENTO.toString()));
		
		return executeCriteria(criteria);
	}

	public Long countExameSolicitacaoPacAtend(AghUnidadesFuncionais unidadeExecutora, Date dtHrInicial
			, Date dtHrFinal, Date dtHrProgramado, DominioConvenioExameSituacao convenio, AelSitItemSolicitacoes situacao
			, VAelExamesSolicitacao nomeExame
			, DominioOrigemAtendimento origemAtendimento, DominoOrigemMapaAmostraItemExame origemMapaTrabalho) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelExamesSolicitacaoPacAtd.class);

		mountCriteria(unidadeExecutora, dtHrInicial, dtHrFinal, dtHrProgramado, convenio, situacao, nomeExame
				, origemAtendimento, origemMapaTrabalho, criteria);

		return executeCriteriaCount(criteria);
	}


	@SuppressWarnings({"PMD.NPathComplexity"})
	private void mountCriteria(AghUnidadesFuncionais unidadeExecutora, Date dtHrInicial, Date dtHrFinal, Date dtHrProgramado
			, DominioConvenioExameSituacao convenio, AelSitItemSolicitacoes situacao, VAelExamesSolicitacao nomeExame
			, DominioOrigemAtendimento origemAtendimento, DominoOrigemMapaAmostraItemExame origemMapaTrabalho
			, DetachedCriteria criteria) {

		if (unidadeExecutora != null && unidadeExecutora.getSeq() != null) {
			criteria.add(Restrictions.eq(VAelExamesSolicitacaoPacAtd.Fields.UFE_UNF_SEQ.toString(), unidadeExecutora.getSeq()));
		}

		if (dtHrInicial != null && dtHrFinal != null) {
			criteria.add(Restrictions.between(VAelExamesSolicitacaoPacAtd.Fields.DTHR_EVENTO.toString(), dtHrInicial, dtHrFinal));
		}

		if (dtHrProgramado != null) {
			criteria.add(Restrictions.between(VAelExamesSolicitacaoPacAtd.Fields.DTHR_PROGRAMADA.toString()
					, DateUtil.obterDataComHoraInical(dtHrProgramado), DateUtil.obterDataComHoraFinal(dtHrProgramado)));
		}

		if (convenio != null) {
			if (DominioConvenioExameSituacao.S == convenio) {
				criteria.add(Restrictions.eq(VAelExamesSolicitacaoPacAtd.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.S.toString()));
			} else if (DominioConvenioExameSituacao.NS == convenio) {
				criteria.add(Restrictions.ne(VAelExamesSolicitacaoPacAtd.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.S.toString()));				
			}
		}

		if (situacao != null && StringUtils.isNotBlank(situacao.getCodigo())) {
			criteria.add(Restrictions.eq(VAelExamesSolicitacaoPacAtd.Fields.SITUACAO.toString(), situacao.getCodigo()));
		}

		if (nomeExame != null && nomeExame.getId() != null && StringUtils.isNotBlank(nomeExame.getId().getSigla())) {
			criteria.add(Restrictions.eq(VAelExamesSolicitacaoPacAtd.Fields.UFE_EMA_EXA_SIGLA.toString(), nomeExame.getId().getSigla()));
		}

		if (origemAtendimento != null) {
			criteria.add(Restrictions.eq(VAelExamesSolicitacaoPacAtd.Fields.ORIGEM_ATENDIMENTO.toString(), origemAtendimento));
		}

		if (origemMapaTrabalho != null) {
			criteria.add(Restrictions.eq(VAelExamesSolicitacaoPacAtd.Fields.ORIGEM_MAPA.toString(), origemMapaTrabalho.toString()));			
		}
	}
	
}