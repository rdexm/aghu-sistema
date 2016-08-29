package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.PdtDadoImg;
import br.gov.mec.aghu.model.PdtDadoImgId;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.PdtImg;

public class PdtImgDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtImg> {
	
	private static final long serialVersionUID = -216361426811519808L;


	/**
	 * verifica se tem imagem
	 * 
	 * @author Angela Gallassini
	 * @param GLOBAL
	 *            CG$DDT_SEQ (pDdtSeq)
	 * @return Integer
	 */
	public Long verificarSeTemImagem(Integer ddtSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtImg.class);
		criteria.add(Restrictions.eq(PdtImg.Fields.ID_DDT_SEQ.toString(),
				ddtSeq));
		return executeCriteriaCount(criteria);
	}

	public PdtImg obterPdtImgPorChavePrimaria(Short seqp, Integer ddtSeq) {
		PdtDadoImgId id = new PdtDadoImgId(ddtSeq, seqp);
		
		return this.obterPorChavePrimaria(id);
	}

	/**
	 * pesquisar lista de imagens por descricao
	 * #16639
	 * PDT_IMGS IMG, 
	 * PDT_DADOS_IMGS FPT, 
	 * PDT_DESCRICOES DDT
	 * 
	 *  DDT.crg_seq = CIR_SEQ and
		IMG.fpt_ddt_seq = DDT.seq and
		IMG.fpt_ddt_seq = FPT.ddt_seq and
		IMG.fpt_seqp = FPT.seqp 
	 */
	public List<PdtImg> pesquisarImagens(Integer cirSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtImg.class, "IMG");
		
		criteria.createAlias("IMG." + PdtImg.Fields.PDT_DADOS_IMGS.toString(), "FDT");
		criteria.createAlias("FDT." + PdtDadoImg.Fields.PDT_DESCRICAO.toString(), "DDT");
		
		criteria.add(Restrictions.eq("DDT." + PdtDescricao.Fields.CRG_SEQ.toString(), cirSeq));
		
		return  executeCriteria(criteria);
	}
	
	
}