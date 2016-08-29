package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoExame;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaProcedimento;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MbcFichaProcedimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaProcedimento> {

	private static final long serialVersionUID = -6041074057215788289L;

	public List<MbcFichaProcedimento> obterFichaProcedimentoComProcedimentoCirurgicoByFichaAnestesia(
			Long seqMbcFichaAnestesia, DominioSituacaoExame situacaoProcedimento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaProcedimento.class);
		
		addFichaAnestesia(seqMbcFichaAnestesia, criteria);
		
		criteria.add(Restrictions.eq(MbcFichaProcedimento.Fields.SITUACAO_PROCEDIMENTO.toString(), situacaoProcedimento));
		
		return executeCriteria(criteria);
	}

	private void addFichaAnestesia(Long seqMbcFichaAnestesia,
			DetachedCriteria criteria) {
		criteria.createAlias(MbcFichaProcedimento.Fields.MBC_PROCEDIMENTO_CIRURGICOS.toString(), "pci");
		criteria.createAlias(MbcFichaProcedimento.Fields.MBC_FICHA_ANESTESIAS.toString(), "ffa");
		
		criteria.add(Restrictions.eq("ffa." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
	}
	
	public List<MbcFichaProcedimento> pesquisarFichaAnestesia(Integer pacCodigo, Short unfSeq, Date data, Integer pciSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaProcedimento.class);
		
		criteria.createAlias(MbcFichaProcedimento.Fields.MBC_FICHA_ANESTESIAS.toString(), "fic");
		
		criteria.add(Restrictions.eq("fic."+MbcFichaAnestesias.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq("fic."+MbcFichaAnestesias.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.isNull("fic."+MbcFichaAnestesias.Fields.DTHR_MVTO.toString()));
		final DominioIndPendenteAmbulatorio[] dominios = {DominioIndPendenteAmbulatorio.R, DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.V};
		criteria.add(Restrictions.in("fic."+MbcFichaAnestesias.Fields.PENDENTE.toString(), dominios));
		criteria.add(Restrictions.isNull("fic."+MbcFichaAnestesias.Fields.CRG_SEQ.toString()));
		Date dataAnterior = DateUtil.adicionaDias(data, -1);
		criteria.add(Restrictions.between("fic."+MbcFichaAnestesias.Fields.DATA.toString(), dataAnterior, data));
		criteria.add(Restrictions.eq(MbcFichaProcedimento.Fields.SEQ.toString(), pciSeq));
		
		return executeCriteria(criteria);
	}
	

}
