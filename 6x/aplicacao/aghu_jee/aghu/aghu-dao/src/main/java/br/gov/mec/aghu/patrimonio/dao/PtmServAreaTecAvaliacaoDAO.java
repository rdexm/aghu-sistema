package br.gov.mec.aghu.patrimonio.dao;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmServAreaTecAvaliacao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.vo.UsuarioTecnicoVO;



public class PtmServAreaTecAvaliacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PtmServAreaTecAvaliacao>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 423587L;

	private DetachedCriteria montarConsultaObterUsuariosTecnicos(PtmAreaTecAvaliacao areaTecnica){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmServAreaTecAvaliacao.class, "SATA");
		criteria.createAlias("SATA."+PtmServAreaTecAvaliacao.Fields.SERVIDOR_TECNICO.toString(), "SER");
		criteria.createAlias("SER."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.createAlias("SATA."+PtmServAreaTecAvaliacao.Fields.AREA_TEC_AVALIACAO.toString(), "ATA");
		if(areaTecnica != null && areaTecnica.getSeq() != null){
			criteria.add(Restrictions.eq("ATA."+PtmAreaTecAvaliacao.Fields.SEQ.toString(), areaTecnica.getSeq()));
		}
		if(areaTecnica != null && areaTecnica.getFccCentroCustos() != null){
			criteria.add(Restrictions.eq("ATA."+PtmAreaTecAvaliacao.Fields.FCC_CENTRO_CUSTOS.toString(), areaTecnica.getFccCentroCustos()));
		}
		return criteria;
	}
	
	public List<UsuarioTecnicoVO> obterUsuariosTecnicosList(PtmAreaTecAvaliacao areaTecnica){
		DetachedCriteria criteria = montarConsultaObterUsuariosTecnicos(areaTecnica);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("SATA."+PtmServAreaTecAvaliacao.Fields.SEQ_AREA_TEC_AVALIACAO.toString()), UsuarioTecnicoVO.Fields.SEQ_AREA_TEC_AVALIACAO.toString())
				.add(Projections.property("SATA."+PtmServAreaTecAvaliacao.Fields.MAT_RAP_TECNICO.toString()), UsuarioTecnicoVO.Fields.MAT_RAP_TECNICO.toString())
				.add(Projections.property("SATA."+PtmServAreaTecAvaliacao.Fields.SER_VIN_CODIGO_TECNICO.toString()), UsuarioTecnicoVO.Fields.SER_VIN_CODIGO_TECNICO.toString())
				.add(Projections.property("SATA."+PtmServAreaTecAvaliacao.Fields.TECNICO_PADARO.toString()), UsuarioTecnicoVO.Fields.TECNICO_PADRAO.toString())
				.add(Projections.property("SATA."+PtmServAreaTecAvaliacao.Fields.MAT_RAP_CRIACAO.toString()), UsuarioTecnicoVO.Fields.MAT_RAP_CRIACAO.toString())
				.add(Projections.property("SATA."+PtmServAreaTecAvaliacao.Fields.SER_VIN_CODIGO_CRIACAO.toString()), UsuarioTecnicoVO.Fields.SER_VIN_CODIGO_CRIACAO.toString())
				.add(Projections.property("PES."+RapPessoasFisicas.Fields.NOME.toString()), UsuarioTecnicoVO.Fields.NOME.toString())
				
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(UsuarioTecnicoVO.class));
		
		addOrder(criteria);
		
		return executeCriteria(criteria);
	}

	/**
	 * Obtém o Técnico Padrão de uma Área Técnica a partir do código da Área.
	 * 
	 * @param seqArea - Código da Área
	 * @return Técnico Padrão da Área Técnica de Avaliação
	 */
	public PtmServAreaTecAvaliacao obterTecnicoPadraoAreaPorSeqArea(Integer seqArea) {

		DetachedCriteria criteria = DetachedCriteria.forClass(PtmServAreaTecAvaliacao.class, "SAT");

		criteria.createAlias("SAT." + PtmServAreaTecAvaliacao.Fields.SERVIDOR_TECNICO.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		
		criteria.setFetchMode("SAT." + PtmServAreaTecAvaliacao.Fields.SERVIDOR_TECNICO.toString(), FetchMode.JOIN);
		criteria.setFetchMode("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), FetchMode.JOIN);
		
		criteria.add(Restrictions.eq("SAT." + PtmServAreaTecAvaliacao.Fields.SEQ_AREA_TEC_AVALIACAO.toString(), seqArea));
		criteria.add(Restrictions.eq("SAT." + PtmServAreaTecAvaliacao.Fields.TECNICO_PADARO.toString(), DominioSimNao.S.isSim()));
		
		return (PtmServAreaTecAvaliacao) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Adicionar ordem a uma Detached Criteria.
	 * 
	 * @param criteria
	 * @param orderProperty
	 * @param asc
	 */
	private void addOrder(final DetachedCriteria criteria) {
		criteria.addOrder(Order.asc(UsuarioTecnicoVO.Fields.NOME.toString()));
	}
	
	public PtmServAreaTecAvaliacao consultarTecnicoPadrao(PtmAreaTecAvaliacao areaSelecionada){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmServAreaTecAvaliacao.class, "SAT");
		criteria.createAlias("SAT." + PtmServAreaTecAvaliacao.Fields.SERVIDOR_TECNICO.toString(), "RAP", JoinType.INNER_JOIN);
		criteria.createAlias("RAP." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("SAT." + PtmServAreaTecAvaliacao.Fields.TECNICO_PADARO.toString(), DominioSimNao.S.isSim()));
		criteria.add(Restrictions.eq("SAT." + PtmServAreaTecAvaliacao.Fields.SEQ_AREA_TEC_AVALIACAO.toString(), areaSelecionada.getSeq()));
		
		return (PtmServAreaTecAvaliacao)executeCriteriaUniqueResult(criteria);
	}

}
