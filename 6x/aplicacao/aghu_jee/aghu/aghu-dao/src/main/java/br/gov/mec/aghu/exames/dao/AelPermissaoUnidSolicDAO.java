package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNaoRotina;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelPermissaoUnidSolic;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

public class AelPermissaoUnidSolicDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelPermissaoUnidSolic> {

	private static final long serialVersionUID = -8509343413283081410L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelPermissaoUnidSolic.class);
		return criteria;
    }
	

	public List<AelPermissaoUnidSolic> buscaListaAelPermissoesUnidSolicPorEmaExaSiglaEmaManSeqUnfSeq(
			String emaExaSigla, Integer emaManSeq, Short unfSeq) {
		
		DetachedCriteria dc = obterCriteria();
		dc.createAlias(AelPermissaoUnidSolic.Fields.UNF_SOLICITANTE.toString(), "sol");
		dc.createAlias(AelPermissaoUnidSolic.Fields.UNF_EXECUTA_EXAMES.toString(), "exe");
		dc.createAlias("exe."+AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), "mat");
		dc.add(Restrictions.eq(AelPermissaoUnidSolic.Fields.UFE_EMA_EXA_SIGLA.toString(), emaExaSigla));
		dc.add(Restrictions.eq(AelPermissaoUnidSolic.Fields.UFE_EMA_MAN_SEQ.toString(), emaManSeq));
		dc.add(Restrictions.eq(AelPermissaoUnidSolic.Fields.UFE_UNF.toString(), unfSeq));
		
		dc.addOrder(Order.asc("sol."+AghUnidadesFuncionais.Fields.ANDAR.toString()));
		dc.addOrder(Order.asc("sol."+AghUnidadesFuncionais.Fields.ALA_CODIGO.toString()));
		dc.addOrder(Order.asc("sol."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		
		return executeCriteria(dc);
	}

	public AelPermissaoUnidSolic buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(
			String emaExaSigla, Integer emaManSeq, Short unfSeq,
			Short unfSeqSolicitante) {
		DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AelPermissaoUnidSolic.Fields.UFE_EMA_EXA_SIGLA.toString(), emaExaSigla));
		dc.add(Restrictions.eq(AelPermissaoUnidSolic.Fields.UFE_EMA_MAN_SEQ.toString(), emaManSeq));
		dc.add(Restrictions.eq(AelPermissaoUnidSolic.Fields.UFE_UNF.toString(), unfSeq));
		dc.add(Restrictions.eq(AelPermissaoUnidSolic.Fields.UNF_SEQ_SOLICITANTE.toString(), unfSeqSolicitante));
		
		return (AelPermissaoUnidSolic)executeCriteriaUniqueResult(dc);
	}

	public AelPermissaoUnidSolic buscarAelPermissaoUnidSolicPor(AelExames exame, AelMateriaisAnalises materialAnalise, AghUnidadesFuncionais unidadeFuncional, AghUnidadesFuncionais unidadeFuncionalSolicitante) {
		if (exame == null || exame.getSigla() == null 
				|| materialAnalise == null || materialAnalise.getSeq() == null
				|| unidadeFuncional == null || unidadeFuncional.getSeq() == null
				|| unidadeFuncionalSolicitante == null || unidadeFuncionalSolicitante.getSeq() == null) {
			throw new IllegalArgumentException("Parametro obritorio nao informado!!!!");
		}
		
		return buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(
				exame.getSigla(),
				materialAnalise.getSeq(),
				unidadeFuncional.getSeq(),
				unidadeFuncionalSolicitante.getSeq()
		);
	}

	public AelPermissaoUnidSolic buscarAelPermissaoUnidSolicPorSigla(String exameSigla, AelMateriaisAnalises materialAnalise, AghUnidadesFuncionais unidadeFuncional, AghUnidadesFuncionais unidadeFuncionalSolicitante) {
		if (exameSigla == null 
				|| materialAnalise == null || materialAnalise.getSeq() == null
				|| unidadeFuncional == null || unidadeFuncional.getSeq() == null
				|| unidadeFuncionalSolicitante == null || unidadeFuncionalSolicitante.getSeq() == null) {
			throw new IllegalArgumentException("Parametro obritorio nao informado!!!!");
		}
		
		return buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(
				exameSigla,
				materialAnalise.getSeq(),
				unidadeFuncional.getSeq(),
				unidadeFuncionalSolicitante.getSeq()
		);
	}
	
	
	
	public List<AelPermissaoUnidSolic> buscarAelPermissaoUnidSolicPorUnfSeqSolicitanteSimNaoRotina(Short unfSeqSolicitante, DominioSimNaoRotina SimNaoRotina) {
		DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AelPermissaoUnidSolic.Fields.UNF_SEQ_SOLICITANTE.toString(), unfSeqSolicitante));
		dc.add(Restrictions.eq(AelPermissaoUnidSolic.Fields.IND_PERMITE_PROGRAMAR_EXAMES.toString(), SimNaoRotina));

		return executeCriteria(dc);
	}
}