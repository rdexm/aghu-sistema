package br.gov.mec.aghu.blococirurgico.dao;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.Table;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.blococirurgico.vo.DemoFinanceiroOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.InfMateriaisNaoCompativeisVO;
import br.gov.mec.aghu.blococirurgico.vo.ItemProcedimentoHospitalarMaterialVO;
import br.gov.mec.aghu.blococirurgico.vo.MarcaComercialMaterialVO;
import br.gov.mec.aghu.blococirurgico.vo.MaterialHospitalarVO;
import br.gov.mec.aghu.dominio.DominioRequeridoItemRequisicao;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcMateriaisItemOpmes;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.utils.DateUtil;


public class MbcItensRequisicaoOpmesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcItensRequisicaoOpmes> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4756125458047765337L;
		
	public Integer deletarItensRequisicoesOpmePorRequisicaoSeq(final Short requisicaoSeq) {
		StringBuilder sql = new StringBuilder(50);
		sql.append("DELETE " ).append( MbcItensRequisicaoOpmes.class.getName())
		.append(" WHERE ROP_SEQ = :requisicaoSeq");
		
		Query query = createQuery(sql.toString());
		query.setParameter("requisicaoSeq", requisicaoSeq);
		
		return query.executeUpdate();
	}
	
	public List<MbcItensRequisicaoOpmes> pesquisarMatSolicitacaoOrcamento(Short seqRequisicaoOpme){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcItensRequisicaoOpmes.class, "ITO");
		criteria.createAlias("ITO." + MbcItensRequisicaoOpmes.Fields.REQUISICAO_OPMES.toString(), "ROP");
		
		if(seqRequisicaoOpme != null){
			criteria.add(Restrictions.eq("ROP." + MbcRequisicaoOpmes.Fields.ID.toString(), seqRequisicaoOpme.shortValue()));
		}
		
		// TODO ALTERACAO	
		criteria.add(Restrictions.eq("ITO." + MbcItensRequisicaoOpmes.Fields.IND_REQUERIDO .toString(), DominioRequeridoItemRequisicao.NOV));
		
		List<MbcItensRequisicaoOpmes> lista =  executeCriteria(criteria);
		//for (MbcItensRequisicaoOpmes mbcItensRequisicaoOpmes : lista) {
			//super.initialize(mbcItensRequisicaoOpmes.getFatItensProcedHospitalar());
			//super.initialize(mbcItensRequisicaoOpmes.getMateriaisItemOpmes());
			//super.initialize(mbcItensRequisicaoOpmes.getRequisicaoOpmes());
		//}
		return lista;
	}
	
	 public MbcItensRequisicaoOpmes consultarItemRequisicaoOpmeByIphSeqAndIphPhoSeq(Short iphPhoSeq, Integer iphSeq) {
	    DetachedCriteria criteria = DetachedCriteria.forClass(MbcItensRequisicaoOpmes.class);
	    criteria.add(Restrictions.eq(MbcItensRequisicaoOpmes.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
	    criteria.add(Restrictions.eq(MbcItensRequisicaoOpmes.Fields.IPH_SEQ.toString(), iphSeq));
	    return (MbcItensRequisicaoOpmes) executeCriteriaUniqueResult(criteria);
	  }
	 
	// #35483 - C03_CONS_MATS
	@SuppressWarnings("unchecked")
	public List<Object[]> consultaInfMateriaisRequisicao(Short seqRequisicao) {

		String mbcItensRequisicaoOpmes = MbcItensRequisicaoOpmes.class.getAnnotation(Table.class).name();
		String mbcRequisicaoOpmes = MbcRequisicaoOpmes.class.getAnnotation(Table.class).name();
		String fatItensProcedHospitalar = FatItensProcedHospitalar.class.getAnnotation(Table.class).name();
		String scoMaterial = ScoMaterial.class.getAnnotation(Table.class).name();
		String mbcMateriaisItemOpmes = MbcMateriaisItemOpmes.class.getAnnotation(Table.class).name();
	
		StringBuilder hql = new StringBuilder(1000);
		
		hql.append(" SELECT")
		.append(" COALESCE(IRO.SOLC_NOVO_MAT, IPH.COD_TABELA || ' - ' || IPH.DESCRICAO) as " ).append( InfMateriaisNaoCompativeisVO.Fields.CODIGO_DESCRICAO_MATERIAL.toString())
		.append(", CASE WHEN IRO.IND_REQUERIDO = '" ).append( DominioRequeridoItemRequisicao.REQ.toString() ).append( "' THEN 'Sim' else 'Não' END as " ).append(  InfMateriaisNaoCompativeisVO.Fields.LICITADO.toString())
		.append(", IRO.QTD_SOLC as " ).append( InfMateriaisNaoCompativeisVO.Fields.QTDE_SOLICITADO.toString())
		.append(", IRO.QTD_AUTR_SUS as " ).append( InfMateriaisNaoCompativeisVO.Fields.QTDE_SUS.toString())
		.append(", IRO.VLR_UNIT as " ).append( InfMateriaisNaoCompativeisVO.Fields.VLR_UNITARIO.toString())
		.append(", IRO.QTD_SOLC * IRO.VLR_UNIT as " ).append( InfMateriaisNaoCompativeisVO.Fields.VLR_TOTAL_SOLICITADO.toString())
		.append(", IRO.VLR_UNIT as " ).append( InfMateriaisNaoCompativeisVO.Fields.VLR_TABELA_SUS.toString())
		.append(", (IRO.QTD_SOLC * IRO.VLR_UNIT) - IRO.VLR_UNIT as " ).append( InfMateriaisNaoCompativeisVO.Fields.DIFERENCA_VALOR.toString())
		.append(", CASE WHEN IRO.IND_REQUERIDO = '" ).append( DominioRequeridoItemRequisicao.NOV.toString() ).append( "' THEN 1 else 3 END as " ).append(  InfMateriaisNaoCompativeisVO.Fields.MATERIAL_NOVO.toString())
		
		.append(" FROM ")
		.append(" AGH." ).append( mbcRequisicaoOpmes ).append( " ROP")
		.append(" JOIN AGH." ).append( mbcItensRequisicaoOpmes ).append( " IRO ON ROP.SEQ = IRO.ROP_SEQ")
		.append(" LEFT JOIN AGH." ).append( fatItensProcedHospitalar ).append( " IPH ON IRO.IPH_PHO_SEQ = IPH.PHO_SEQ AND IRO.IPH_SEQ = IPH.SEQ") 
		
		.append(" WHERE")
		.append(" IRO.IND_REQUERIDO in ('" ).append( DominioRequeridoItemRequisicao.REQ.toString() ).append( "', '" ).append( DominioRequeridoItemRequisicao.NOV.toString() ).append( "')")
		.append(" AND IRO.IND_COMPATIVEL = 'N'" )
		.append(" AND ROP.SEQ = :seqRequisicao")
		
		.append(" UNION")
		
		.append(" SELECT")
		.append(" 'Material fora SIGPTAP: '|| MAT.CODIGO || ' - ' || MAT.NOME as " ).append( InfMateriaisNaoCompativeisVO.Fields.CODIGO_DESCRICAO_MATERIAL.toString())
		.append(", 'Sim'  as " ).append(  InfMateriaisNaoCompativeisVO.Fields.LICITADO.toString())
		.append(", MIO.QTD_SOLC as " ).append(  InfMateriaisNaoCompativeisVO.Fields.QTDE_SOLICITADO.toString())
		.append(", 0  as " ).append(  InfMateriaisNaoCompativeisVO.Fields.QTDE_SUS.toString())
		.append(", 0  as " ).append(  InfMateriaisNaoCompativeisVO.Fields.VLR_UNITARIO.toString())
		.append(", 0  as " ).append(  InfMateriaisNaoCompativeisVO.Fields.VLR_TOTAL_SOLICITADO.toString())
		.append(", 0  as " ).append(  InfMateriaisNaoCompativeisVO.Fields.VLR_TABELA_SUS.toString())
		.append(", 0  as " ).append(  InfMateriaisNaoCompativeisVO.Fields.DIFERENCA_VALOR.toString())
		.append(", 2  as " ).append(  InfMateriaisNaoCompativeisVO.Fields.MATERIAL_NOVO.toString())
		
		.append(" FROM ")
		.append(" AGH." ).append( mbcRequisicaoOpmes ).append( " ROP")
		.append(" JOIN AGH." ).append( mbcItensRequisicaoOpmes ).append( " IRO ON ROP.SEQ = IRO.ROP_SEQ")
		.append(" JOIN AGH." ).append( mbcMateriaisItemOpmes ).append( " MIO ON IRO.SEQ = MIO.IRO_SEQ")
		.append(" JOIN AGH." ).append( scoMaterial ).append( " MAT ON MIO.MAT_CODIGO = MAT.CODIGO")
		
		.append(" WHERE")
		.append(" IRO.IND_REQUERIDO = '" ).append( DominioRequeridoItemRequisicao.ADC.toString() ).append( '\'')
		.append(" AND IRO.IND_COMPATIVEL = 'N'")
		.append(" AND ROP.SEQ = :seqRequisicao")
		
		.append(" ORDER BY 9, 1");
		
		Query query = this.createNativeQuery(hql.toString());
		query.setParameter("seqRequisicao", seqRequisicao);
		
		return query.getResultList();
	}
	

	public Integer obterQuantidadeSolicitada(Short seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcItensRequisicaoOpmes.class);
		criteria.add(Restrictions.eq(MbcItensRequisicaoOpmes.Fields.ID.toString(), seq));
		ProjectionList proj = Projections.projectionList();
		proj.add(Projections.property(MbcItensRequisicaoOpmes.Fields.QTD_SOLC.toString()));
		criteria.setProjection(proj);
		return (Integer)executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Recupera as marcas dos itens da lista recebida como parâmetro e clona itens com mais de uma marca para 
	 * apresentação individual  
	 * 
	 * @param itens lista ItemProcedimentoHospitalarMaterialVO.java
	 * @return itens lista ItemProcedimentoHospitalarMaterialVO.java
	 */
	public List<ItemProcedimentoHospitalarMaterialVO> recuperarMarcasItensProcedimentoHospitalarMateriais(List<ItemProcedimentoHospitalarMaterialVO> itens, Date dtAgenda) {
		List<ItemProcedimentoHospitalarMaterialVO> novosItensPorMarcas = new ArrayList<ItemProcedimentoHospitalarMaterialVO>();
		ConvertUtils.register(new BigDecimalConverter(null),
				BigDecimal.class);
		for (ItemProcedimentoHospitalarMaterialVO item : itens) {
			if (item.getCodigoMat() != null) {
				List<MarcaComercialMaterialVO> marcasM = recuperaMarcasMaterial(item.getCodigoMat(), dtAgenda);
				if (marcasM != null && !marcasM.isEmpty()) {
					for (MarcaComercialMaterialVO marca : marcasM) {
						ItemProcedimentoHospitalarMaterialVO novoItem = new ItemProcedimentoHospitalarMaterialVO();
						novoItem = copiarPropriedadesItemProcedimentoHospitalarMaterialVO(item);
						novoItem.setMioSeq(null);
						novoItem.setCodigoMarcasComerciais(marca.getCodigo());
						novoItem.setDescricaoMarcasComerciais(marca.getDescricao());
						novoItem.setUnidadeMaterial(marca.getUnidade());
						novoItem.setValorUnitario(marca.getValorUnitario());
						novosItensPorMarcas.add(novoItem);
					}
				}
			} else {
				novosItensPorMarcas.add(item);
			}
		}
		return novosItensPorMarcas;
	}

	private ItemProcedimentoHospitalarMaterialVO copiarPropriedadesItemProcedimentoHospitalarMaterialVO(ItemProcedimentoHospitalarMaterialVO item) {
		ItemProcedimentoHospitalarMaterialVO novoItem = new ItemProcedimentoHospitalarMaterialVO();
		novoItem.setIphCompPho(item.getIphCompPho());
		novoItem.setIphCompSeq(item.getIphCompSeq());
		novoItem.setIphCompDscr(item.getIphCompDscr());
		novoItem.setIphCompCod(item.getIphCompCod());
		novoItem.setQtdMaxima(item.getQtdMaxima());
		novoItem.setMaxQtdConta(item.getMaxQtdConta());
		novoItem.setVlrServHospitalar(item.getVlrServHospitalar());
		novoItem.setVlrServProfissional(item.getVlrServProfissional());
		novoItem.setVlrSadt(item.getVlrSadt());
		novoItem.setVlrProcedimento(item.getVlrProcedimento());
		novoItem.setVlrAnestesia(item.getVlrAnestesia());
		novoItem.setCodigoMat(item.getCodigoMat());
		novoItem.setNomeMat(item.getNomeMat());
		novoItem.setCmpPhiSeq(item.getCmpPhiSeq());
		novoItem.setMioQtdSolic(item.getMioQtdSolic());
		novoItem.setIndRequerido(item.getIndRequerido());
		novoItem.setIndCompativel(item.getIndCompativel());
		novoItem.setIndAutorizado(item.getIndAutorizado());
		novoItem.setIndConsumido(item.getIndConsumido());
		novoItem.setSolcNovoMat(item.getSolcNovoMat());
		novoItem.setRopSeq(item.getRopSeq());
		novoItem.setIroSeq(item.getIroSeq());
		novoItem.setMioSeq(item.getMioSeq());
		novoItem.setCmpPhi(item.getCmpPhi());
		novoItem.setMaterial(item.getMaterial());
		novoItem.setCmp(item.getCmp());
		novoItem.setCmpPhiSituacao(item.getCmpPhiSituacao());
		novoItem.setMatSituacao(item.getMatSituacao());
		novoItem.setCodigoMarcasComerciais(item.getCodigoMarcasComerciais());
		novoItem.setDescricaoMarcasComerciais(item.getDescricaoMarcasComerciais());
		novoItem.setUnidadeMaterial(item.getUnidadeMaterial());
		return novoItem;
	}

	/**
	 * Retorna uma lista com a(s) marca(s) que existem para o codigo de material informado, podendo ser nulo
	 * 
	 * @param matCodigo
	 * @return MarcaComercialMaterialVO.java
	 */
	public List<MarcaComercialMaterialVO> recuperaMarcasMaterial(
			Integer matCodigo, Date dtAgenda) {

		StringBuilder sql = new StringBuilder(1000);
		sql.append(" SELECT DISTINCT  MCM." + MarcaComercialMaterialVO.Fields.CODIGO.toString() +" as "+ MarcaComercialMaterialVO.Fields.CODIGO.toString()
			+" , MCM." + MarcaComercialMaterialVO.Fields.DESCRICAO.toString() +" as "+ MarcaComercialMaterialVO.Fields.DESCRICAO.toString()
			+" , MAT_AF_CUM.umd_codigo as "+ MarcaComercialMaterialVO.Fields.UNIDADE.toString()		
			+" , IAF.VALOR_UNITARIO as "+ MarcaComercialMaterialVO.Fields.VALOR_UNITARIO.toString()		
			+" FROM   AGH.SCO_MATERIAIS               MAT_AF_CUM "
			+" JOIN   AGH.SCO_SOLICITACOES_DE_COMPRAS SLC ON MAT_AF_CUM.CODIGO  = SLC.MAT_CODIGO "
			+" JOIN   AGH.SCO_FASES_SOLICITACOES      FSC ON SLC.NUMERO         = FSC.SLC_NUMERO "
			+" JOIN   AGH.SCO_ITENS_AUTORIZACAO_FORN  IAF ON FSC.IAF_AFN_NUMERO = IAF.AFN_NUMERO AND FSC.IAF_NUMERO = IAF.NUMERO "
			+" JOIN   AGH.SCO_AUTORIZACOES_FORN       AFN ON IAF.AFN_NUMERO     = AFN.NUMERO "
			+" JOIN   AGH.SCO_MARCAS_COMERCIAIS       MCM ON IAF.MCM_CODIGO     = MCM.CODIGO "
			+" WHERE  IAF.IND_SITUACAO IN ('AE', 'PA')  "
			+" AND    NVL(IAF.IND_CONTRATO,'N') = 'S' "
			+" AND    AFN.IND_SITUACAO IN ('AE', 'PA') "
			+" AND NVL(afn.DT_VENCTO_CONTRATO, \n"
			+"  '"+DateUtil.dataToString(dtAgenda,"dd-MM-YYYY")+"' ) >=  '"+DateUtil.dataToString(dtAgenda,"dd-MM-YYYY")+"' "
			+" AND    FSC.IND_EXCLUSAO         = 'N'  "
			+" AND    MAT_AF_CUM.CODIGO        =     :matCodigo  ");

		SQLQuery query = createSQLQuery(sql.toString());
		query.addScalar(MarcaComercialMaterialVO.Fields.CODIGO.toString(), IntegerType.INSTANCE);
		query.addScalar(MarcaComercialMaterialVO.Fields.DESCRICAO.toString(), StringType.INSTANCE);
		query.addScalar(MarcaComercialMaterialVO.Fields.UNIDADE.toString(), StringType.INSTANCE);
		query.addScalar(MarcaComercialMaterialVO.Fields.VALOR_UNITARIO.toString(), DoubleType.INSTANCE);
		query.setInteger("matCodigo", matCodigo);
		query.setResultTransformer(Transformers.aliasToBean(MarcaComercialMaterialVO.class));
		return (List<MarcaComercialMaterialVO>) query.list();
	}
	
	public List<MaterialHospitalarVO> pesquisarMaterialHospitalar(
			String matNome) {

		StringBuilder sql = new StringBuilder(1000);
		sql.append(" select DISTINCT ");
		sql.append("       mat.CODIGO     matCodigo ");
		sql.append("      ,mat.NOME       matNome ");
		sql.append("      ,mat.UMD_CODIGO umdCodigo ");
		sql.append(" from   SCO_MATERIAIS mat ");
		sql.append(" join agh.SCO_SOLICITACOES_DE_COMPRAS slc on mat.CODIGO         = slc.MAT_CODIGO ");
		sql.append(" join agh.SCO_FASES_SOLICITACOES      fsc on slc.NUMERO         = fsc.SLC_NUMERO ");
		sql.append(" join agh.SCO_ITENS_AUTORIZACAO_FORN  iaf on fsc.IAF_AFN_NUMERO = iaf.AFN_NUMERO and "); 
		sql.append(" fsc.IAF_NUMERO = iaf.NUMERO ");
		sql.append(" join agh.SCO_AUTORIZACOES_FORN       afn on iaf.AFN_NUMERO     = afn.NUMERO ");
		sql.append(" where  mat.IND_SITUACAO = 'A' ");
		sql.append(" and    mat.GMT_CODIGO   = (select vlr_numerico from   agh_parametros where  nome ='GRPO_MAT_ORT_PROT') ");
		sql.append(" and    iaf.IND_SITUACAO IN ('AE', 'PA')  ");
		sql.append(" and    afn.IND_SITUACAO IN ('AE', 'PA')  ");
		sql.append(" and    fsc.IND_EXCLUSAO = 'N' ");
		if(matNome != null & !matNome.isEmpty()){
			sql.append(" and UPPER(mat.NOME)  like UPPER(:matNome) ");
		}
		sql.append(" order by mat.CODIGO ");

		SQLQuery query = createSQLQuery(sql.toString());
		query.addScalar(MaterialHospitalarVO.Fields.MAT_CODIGO.toString(), IntegerType.INSTANCE);
		query.addScalar(MaterialHospitalarVO.Fields.MAT_NOME.toString(), StringType.INSTANCE);
		query.addScalar(MaterialHospitalarVO.Fields.UMD_CODIGO.toString(), StringType.INSTANCE);
		if(matNome != null & !matNome.isEmpty()){
			query.setString("matNome", "%"+matNome+"%");
		}
		query.setResultTransformer(Transformers.aliasToBean(MaterialHospitalarVO.class));
		return (List<MaterialHospitalarVO>) query.list();
	}
	
	public List<DemoFinanceiroOPMEVO> pesquisaDemonstrativoFinanceiroOpmes(
			Date competenciaInicial, Date competenciaFinal, Short especialidadeSeq, Integer prontuario,
			Integer matCodigo) {
		
		if (especialidadeSeq == null) {
			especialidadeSeq = 0;
		}
		if (prontuario == null) {
			prontuario = 0;
		}
		if (matCodigo == null) {
			matCodigo = 0;
		}
		
		StringBuilder sql = new StringBuilder(3000);
		
		sql.append(" select ESP_SIGLA especialidade");
		sql.append("       ,PACIENTE paciente");
		sql.append("       ,MATERIAL materialHospitalar");
		sql.append("       ,sum(QTD_SOLC_COMP * VLR_UNIT)  valorCompativel");
		sql.append("       ,sum(QTD_SOLC_INCP * VLR_UNIT)  valorIncompativel");
		sql.append(" from ");
		sql.append(" ( ");
		sql.append("   select case  ");
		sql.append(especialidadeSeq);
		sql.append("               when 0 then null ");
		sql.append("               else esp.SIGLA ");
		sql.append("          end ESP_SIGLA ");
		sql.append("         ,case  ");
		sql.append(prontuario);
		sql.append("               when 0 then null ");
		sql.append("               else pac.PRONTUARIO||' - '||pac.NOME ");
		sql.append("          end PACIENTE ");
		sql.append("         ,case  ");
		sql.append(matCodigo);
		sql.append("               when 0 then null ");
		sql.append("               else CASE iro.IND_REQUERIDO ");
		sql.append("                    when 'NOV' then '(Nova Solicitação de Material) '|| ");
		sql.append("                         case when COALESCE(mat.CODIGO, -1) = -1 ");
		sql.append("                              then COALESCE(iro.ESPEC_NOVO_MAT, iro.SOLC_NOVO_MAT) ");
		sql.append("                              else mat.CODIGO||' - '||mat.NOME ");
		sql.append("                         end ");
		sql.append("                    when 'ADC' then '(Material Adicionado pelo Usuário) '||mat.CODIGO||' - '||mat.NOME ");
		sql.append("                    else '(ROMP '||iph.COD_TABELA||')'|| ");
		sql.append("                         case when COALESCE(mat.CODIGO,-1) = -1 ");
		sql.append("                              then ' '||iph.DESCRICAO ");
		sql.append("                              else ' | '||mat.CODIGO ||' - '||mat.NOME ");
		sql.append("                         end ");
		sql.append("                    end ");
		sql.append("          end MATERIAL ");
		sql.append("         ,case iro.IND_COMPATIVEL ");
		sql.append("               when 'S' then COALESCE(mio.QTD_SOLC, iro.QTD_SOLC)  ");
		sql.append("               else 0 ");
		sql.append("          end QTD_SOLC_COMP ");
		sql.append("         ,case iro.IND_COMPATIVEL ");
		sql.append("               when 'N' then COALESCE(mio.QTD_SOLC, iro.QTD_SOLC)  ");
		sql.append("               else 0 ");
		sql.append("          end QTD_SOLC_INCP ");
		sql.append("         ,iro.VLR_UNIT                         VLR_UNIT ");
		sql.append("   from  agh.MBC_CIRURGIAS                   crg ");
		sql.append("   join  agh.MBC_AGENDAS                     agd on crg.AGD_SEQ     = agd.SEQ ");
		sql.append("   join  agh.MBC_REQUISICAO_OPMES            rop on agd.SEQ         = rop.AGD_SEQ ");
		sql.append("   join  agh.MBC_ITENS_REQUISICAO_OPMES      iro on rop.SEQ         = iro.ROP_SEQ ");
		sql.append("   join  agh.AGH_WF_TEMPLATE_ETAPAS          wte on wte.CODIGO      = rop.IND_SITUACAO ");
		sql.append("   join  agh.AIP_PACIENTES                   pac on pac.CODIGO      = agd.PAC_CODIGO ");
		sql.append("   join  agh.AGH_ESPECIALIDADES              esp on esp.SEQ         = agd.ESP_SEQ ");
		sql.append("   left join agh.FAT_ITENS_PROCED_HOSPITALAR iph on iro.IPH_PHO_SEQ = iph.PHO_SEQ and iro.IPH_SEQ = iph.SEQ ");
		sql.append("   left join agh.MBC_MATERIAIS_ITEM_OPMES    mio on iro.SEQ         = mio.IRO_SEQ and mio.QTD_SOLC > 0 ");
		sql.append("   left join agh.SCO_MATERIAIS               mat on mio.MAT_CODIGO  = mat.CODIGO ");
		sql.append("   where  agd.dt_agenda >= :compIni ");
		sql.append("   and    agd.dt_agenda < :compFim ");
		sql.append("   and    crg.SITUACAO = 'RZDA' ");
		sql.append("   and    iro.QTD_SOLC  > 0 ");
		sql.append("   and     ( :espSeq = -1 or :espSeq = 0 or :espSeq = esp.SEQ ) ");
		sql.append("   and     ( :pacPro = -1 or :pacPro = 0 or :pacPro = pac.PRONTUARIO ) ");
		sql.append("   and     ( :matCod = -1 or :matCod = 0 or :matCod = mat.CODIGO ) ");
		sql.append(" ) ");
		sql.append(" group by ESP_SIGLA ");
		sql.append("       ,PACIENTE ");
		sql.append("       ,MATERIAL ");
		sql.append(" order by 1,2,3 ");

		

		SQLQuery query = createSQLQuery(sql.toString());
		
		query.addScalar(DemoFinanceiroOPMEVO.Fields.ESPECIALIDADE.toString(), StringType.INSTANCE);
		query.addScalar(DemoFinanceiroOPMEVO.Fields.PACIENTE.toString(), StringType.INSTANCE);
		query.addScalar(DemoFinanceiroOPMEVO.Fields.MATERIALHOSPITALAR.toString(), StringType.INSTANCE);
		query.addScalar(DemoFinanceiroOPMEVO.Fields.VALORCOMPATIVEL.toString(), DoubleType.INSTANCE);
		query.addScalar(DemoFinanceiroOPMEVO.Fields.VALORINCOMPATIVEL.toString(), DoubleType.INSTANCE);
		
		query.setInteger("espSeq", especialidadeSeq);
		query.setInteger("pacPro", prontuario);
		query.setInteger("matCod", matCodigo);
		query.setDate("compIni", competenciaInicial);
		query.setDate("compFim", competenciaFinal);

		query.setResultTransformer(Transformers.aliasToBean(DemoFinanceiroOPMEVO.class));
		return (List<DemoFinanceiroOPMEVO>) query.list();
		
	}


}