package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.BinaryType;
import org.hibernate.type.BooleanType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.blococirurgico.vo.MateriaisProcedimentoOPMEVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.vo.OPMENaoUtilizado;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcMateriaisItemOpmes;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;


public class MbcMateriaisItemOpmesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcMateriaisItemOpmes> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7120695736387897632L;
	
	public List<MbcMateriaisItemOpmes> obterMateriaisItemOpmes(Object objPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMateriaisItemOpmes.class);
		return executeCriteria(criteria);
	}
	
	public List<MbcMateriaisItemOpmes> buscarItemMaterialPorItemRequisicao(MbcItensRequisicaoOpmes itemReq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMateriaisItemOpmes.class);
		criteria.add(Restrictions.eq(MbcMateriaisItemOpmes.Fields.IRO_SEQ.toString(), itemReq.getSeq()));
		return executeCriteria(criteria);
	}
	
	public MbcRequisicaoOpmes consultaJustificativasOPME(Integer crgSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class, "ROP");
		criteria.createAlias("ROP."+MbcRequisicaoOpmes.Fields.AGENDA.toString(), "AGD");
		criteria.createAlias("AGD."+MbcAgendas.Fields.CIRURGIAS.toString(), "CRG");
		criteria.createAlias("ROP."+MbcRequisicaoOpmes.Fields.FLUXO.toString(), "FLUXO");
		
		criteria.add(Restrictions.eq("CRG."+MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		
		List<MbcRequisicaoOpmes> lista = this.executeCriteria(criteria);
		
		if(lista.size() > 0){
			return lista.get(0);
		} else {
			return null;
		}
	}
	
	public List<OPMENaoUtilizado> obterDescricao(Short requisicaoSeq){
		//DOCUMENTO SEM ESPECIFICAÇÕES GENÉRICAS PARA O SQL ANSI(FUNCIONANDO SOMENTE NO POSTGRES)
		if(isOracle()){
			return null;
		} else {
			
			StringBuffer sql = new StringBuffer()
			.append(" select case COALESCE(mio.PHI_SEQ,0) ")
	    	.append(" when 0 then ")
	    	.append(" 'Material: '||mat.CODIGO||' - '||mat.NOME||' Qtde.Solc: '||mio.QTD_SOLC||' Qtde.Util: '||COALESCE(mio.QTD_CONS,0) ")
	        .append(" else ")
	        .append(" 'Item PHI: '||phi.SEQ||' - '||phi.DESCRICAO||' Item SUS: '||iph.COD_TABELA||' - '||iph.DESCRICAO||' Qtde.Solc: '||mio.QTD_SOLC||' Qtde.Util: '||COALESCE(mio.QTD_CONS,0) ")
	        .append(" end DESC_ITENS ")
			.append(" from   agh.MBC_REQUISICAO_OPMES           rop ")
			.append(" join   agh.MBC_ITENS_REQUISICAO_OPMES     iro on rop.SEQ         = iro.ROP_SEQ ")
			.append(" join   agh.MBC_MATERIAIS_ITEM_OPMES       mio on iro.SEQ         = mio.IRO_SEQ ")
			.append(" left join agh.FAT_ITENS_PROCED_HOSPITALAR iph on iro.IPH_PHO_SEQ = iph.PHO_SEQ and iro.IPH_SEQ = iph.SEQ ")
			.append(" left join agh.FAT_PROCED_HOSP_INTERNOS    phi on mio.PHI_SEQ     = phi.SEQ ")
			.append(" left join agh.SCO_MATERIAIS               mat on mio.MAT_CODIGO  = mat.CODIGO ")
			.append(" where  COALESCE(iro.QTD_SOLC,0) > 0 ")
			.append(" and    COALESCE(mio.QTD_SOLC,0) > 0 ")
			.append(" and    COALESCE(mio.QTD_CONS,0) < COALESCE(mio.QTD_SOLC,0) ")
			.append(" and    rop.SEQ = "+requisicaoSeq)
			.append(" order by rop.SEQ ")
			.append(" ,iro.SEQ ")
			.append(" ,mio.SEQ ");
			
			SQLQuery query = createSQLQuery(sql.toString());
			query.addScalar(OPMENaoUtilizado.Fields.DESCRICAO.toString(), StringType.INSTANCE);
			
			query.setResultTransformer(Transformers.aliasToBean(OPMENaoUtilizado.class));
			return query.list();
		}
	}
	
	public List<MateriaisProcedimentoOPMEVO> obterMateriais(Short requisicaoSelecionada, DominioSimNao simNao){
		
		StringBuffer sql = new StringBuffer()
		
		.append(" select iph.PHO_SEQ           	 ").append(MateriaisProcedimentoOPMEVO.Fields.IPH_PHO_SEQ.toString())
	    .append("  ,iph.SEQ              		 ").append(MateriaisProcedimentoOPMEVO.Fields.IPH_SEQ.toString())
	    .append("  ,iph.COD_TABELA        		 ").append(MateriaisProcedimentoOPMEVO.Fields.COD_TABELA.toString())
	    .append("  ,iph.DESCRICAO         		 ").append(MateriaisProcedimentoOPMEVO.Fields.DESCRICAO.toString())
	    .append("  ,case iro.IND_COMPATIVEL  when 'S' then 1 else 0 end as ").append(MateriaisProcedimentoOPMEVO.Fields.IND_COMPATIVEL.toString())
	    .append("  ,iro.IND_REQUERIDO			").append(MateriaisProcedimentoOPMEVO.Fields.REQUERIDO_STRING.toString())
	    .append("  ,iro.QTD_AUTR_HSP      		 ").append(MateriaisProcedimentoOPMEVO.Fields.QUANTIDADE_AUTORIZADA_HOSPITAL.toString())
	    .append("  ,iro.VLR_UNIT          		 ").append(MateriaisProcedimentoOPMEVO.Fields.VALOR_UNITARIO_IPH.toString())
	    .append("  ,mat.CODIGO           	 	 ").append(MateriaisProcedimentoOPMEVO.Fields.MATERIAL_CODIGO.toString())
	    .append("  ,mat.NOME              		 ").append(MateriaisProcedimentoOPMEVO.Fields.NOME.toString())
	    .append("  ,mat.UMD_CODIGO        		 ").append(MateriaisProcedimentoOPMEVO.Fields.UNIDADE_MEDIDA_CODIGO.toString())
	    .append("  ,iro.SOLC_NOVO_MAT     		 ").append(MateriaisProcedimentoOPMEVO.Fields.SOLICITACAO_NOVO_MATERIAL.toString())
	    .append("  ,coalesce(mio.QTD_SOLC, iro.QTD_SOLC)  ").append(MateriaisProcedimentoOPMEVO.Fields.QUANTIDADE_SOLICITADA.toString())
	    .append("  ,iro.ANEXO_ORCAMENTO   ").append(MateriaisProcedimentoOPMEVO.Fields.ANEXO_ORCAMENTO.toString())
	    .append("  ,mio.SLC_NUMERO   ").append(MateriaisProcedimentoOPMEVO.Fields.SLC_NUMERO.toString())
	    .append("  ,mio.SEQ   ").append(MateriaisProcedimentoOPMEVO.Fields.MATERIAL_ITEM_SEQ.toString())
	    .append("  ,case iro.IND_REQUERIDO ") 
	    .append("  when 'NOV' then 1 ") 
	    .append("  when 'ADC' then 2 ") 
	    .append("  else 3 ")
	    .append("  end as reqOrder ")
	    .append(" ,mcm.CODIGO    ").append(MateriaisProcedimentoOPMEVO.Fields.MATERIAL_QUANTIDADE.toString())
   	    .append(" ,mcm.DESCRICAO    ").append(MateriaisProcedimentoOPMEVO.Fields.MATERIAL_MARCA.toString())

	    .append("  from  AGH.MBC_REQUISICAO_OPMES            rop ")
	    .append("  join  AGH.MBC_ITENS_REQUISICAO_OPMES      iro ON rop.SEQ = iro.ROP_SEQ ")

	    .append("  left join agh.FAT_ITENS_PROCED_HOSPITALAR iph  ")
	    .append("  on iro.IPH_PHO_SEQ = iph.PHO_SEQ and iro.IPH_SEQ = iph.SEQ ")

	    .append("  left join agh.MBC_MATERIAIS_ITEM_OPMES    mio  ")
	    .append("  on iro.SEQ = mio.IRO_SEQ  ")

	    .append("  left join AGH.SCO_MATERIAIS               mat  ")
	    .append("  ON mio.MAT_CODIGO = mat.CODIGO ")
	    
	    .append("  left join AGH.SCO_MARCAS_COMERCIAIS     mcm  ")
	    .append("  ON mio.MCM_CODIGO = mcm.CODIGO ")
  
	    .append("  where iro.IND_REQUERIDO  <> 'NRQ' ")
	    .append("  and   iro.IND_AUTORIZADO = 'S' ")
	    .append("  and   rop.SEQ           =  ").append(requisicaoSelecionada);
	
		if(DominioSimNao.N == simNao){
			//<Se filtro "Licitado?" tenha sido selecionado como 'N'>
			sql.append(" and   iro.IND_REQUERIDO  = 'NOV' ");
		} else if(DominioSimNao.S == simNao){
			//<Senão se filtro "Licitado?" tenha sido selecionado como 'S'>
			sql.append(" and   iro.IND_REQUERIDO  in ('REQ', 'ADC') ");
		}		
		 
		sql.append(" order by reqOrder ")
			      .append(" ,iph.COD_TABELA ")
			      .append(" ,mat.CODIGO ");
		
		SQLQuery query = createSQLQuery(sql.toString());
		query.addScalar(MateriaisProcedimentoOPMEVO.Fields.IPH_PHO_SEQ.toString(), ShortType.INSTANCE);
		query.addScalar(MateriaisProcedimentoOPMEVO.Fields.IPH_SEQ.toString(), IntegerType.INSTANCE);
		query.addScalar(MateriaisProcedimentoOPMEVO.Fields.COD_TABELA.toString(), LongType.INSTANCE);
		query.addScalar(MateriaisProcedimentoOPMEVO.Fields.DESCRICAO.toString(), StringType.INSTANCE);
		query.addScalar(MateriaisProcedimentoOPMEVO.Fields.IND_COMPATIVEL.toString(), BooleanType.INSTANCE);
		query.addScalar(MateriaisProcedimentoOPMEVO.Fields.REQUERIDO_STRING.toString(), StringType.INSTANCE);
		query.addScalar(MateriaisProcedimentoOPMEVO.Fields.QUANTIDADE_AUTORIZADA_HOSPITAL.toString(), IntegerType.INSTANCE);
		query.addScalar(MateriaisProcedimentoOPMEVO.Fields.VALOR_UNITARIO_IPH.toString(), BigDecimalType.INSTANCE);
		query.addScalar(MateriaisProcedimentoOPMEVO.Fields.MATERIAL_CODIGO.toString(), IntegerType.INSTANCE);
		query.addScalar(MateriaisProcedimentoOPMEVO.Fields.NOME.toString(), StringType.INSTANCE);
		query.addScalar(MateriaisProcedimentoOPMEVO.Fields.UNIDADE_MEDIDA_CODIGO.toString(), StringType.INSTANCE);
		query.addScalar(MateriaisProcedimentoOPMEVO.Fields.SOLICITACAO_NOVO_MATERIAL.toString(), StringType.INSTANCE);
		query.addScalar(MateriaisProcedimentoOPMEVO.Fields.QUANTIDADE_SOLICITADA.toString(), IntegerType.INSTANCE);
		query.addScalar(MateriaisProcedimentoOPMEVO.Fields.ANEXO_ORCAMENTO.toString(), BinaryType.INSTANCE);
		query.addScalar(MateriaisProcedimentoOPMEVO.Fields.MATERIAL_QUANTIDADE.toString(), IntegerType.INSTANCE);
		query.addScalar(MateriaisProcedimentoOPMEVO.Fields.MATERIAL_MARCA.toString(), StringType.INSTANCE);
		query.addScalar(MateriaisProcedimentoOPMEVO.Fields.SLC_NUMERO.toString(), IntegerType.INSTANCE);
		query.addScalar(MateriaisProcedimentoOPMEVO.Fields.MATERIAL_ITEM_SEQ.toString(), ShortType.INSTANCE);
		
		query.setResultTransformer(Transformers.aliasToBean(MateriaisProcedimentoOPMEVO.class));
		
		return query.list();
	}

}
