package br.gov.mec.aghu.compras.dao;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.Table;

import br.gov.mec.aghu.estoque.vo.MarcaModeloMaterialVO;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMarcaModeloMaterial;
import br.gov.mec.aghu.model.ScoMaterial;

public class ScoMarcaModeloMaterialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoMarcaModeloMaterial> {

	private static final long serialVersionUID = -2750032623433378609L;

	@SuppressWarnings("unchecked")
	public List<Object[]> pesquisarMarcaModeloMaterial(Integer codigoMaterial, Integer codigoMarca, Integer codigoModelo){
		
		String scoMaterial = ScoMaterial.class.getAnnotation(Table.class).name();
		String scoMarcaComercial = ScoMarcaComercial.class.getAnnotation(Table.class).name();
		String scoMarcaModelo = ScoMarcaModelo.class.getAnnotation(Table.class).name();
		String scoMarcaModeloMaterial = ScoMarcaModeloMaterial.class.getAnnotation(Table.class).name();
		
		StringBuilder hql = new StringBuilder(500);
		
		hql.append("SELECT MAT.CODIGO as " ).append( MarcaModeloMaterialVO.Fields.CODIGO_MATERIAL.toString());
		hql.append(", MAT.NOME as " ).append( MarcaModeloMaterialVO.Fields.NOME_MATERIAL.toString());
		hql.append(", MCM.CODIGO as " ).append( MarcaModeloMaterialVO.Fields.CODIGO_MARCA_COMERCIAL.toString());
		hql.append(", MCM.DESCRICAO as " ).append( MarcaModeloMaterialVO.Fields.DESCRICAO_MARCA_COMERCIAL.toString());
		hql.append(", MOM.SEQP as " ).append( MarcaModeloMaterialVO.Fields.SEQP_MARCA_MODELO.toString());
		hql.append(", MOM.DESCRICAO as " ).append( MarcaModeloMaterialVO.Fields.DESCRICAO_MARCA_MODELO.toString());
		hql.append(", MOM.MCM_CODIGO as " ).append( MarcaModeloMaterialVO.Fields.CODIGO_MARCA_MODELO.toString());
		
		hql.append(" FROM AGH." ).append( scoMarcaModeloMaterial ).append( " MMM");
		
		hql.append(" INNER JOIN AGH." ).append( scoMaterial ).append( " MAT ON MMM.MAT_CODIGO = MAT.CODIGO");
		hql.append(" INNER JOIN AGH." ).append( scoMarcaModelo ).append( " MOM ON MOM.MCM_CODIGO = MMM.MOM_MCM_CODIGO AND MOM.SEQP = MMM.MOM_SEQP");
		hql.append(" INNER JOIN AGH." ).append( scoMarcaComercial ).append( " MCM ON MOM.MCM_CODIGO = MCM.CODIGO");
		
		hql.append(" WHERE MMM.MAT_CODIGO = :codigoMaterial");
		
		if(codigoMarca != null){
			hql.append(" AND MMM.MOM_MCM_CODIGO = :codigoMarca");
		}
		
		if(codigoModelo != null){
			hql.append(" AND MMM.MOM_SEQP = :codigoModelo");
		}
		
		Query query = this.createNativeQuery(hql.toString());
		
		query.setParameter("codigoMaterial", codigoMaterial);
		
		if(codigoMarca != null){
			query.setParameter("codigoMarca", codigoMarca);
		}
		
		if(codigoModelo != null){
			query.setParameter("codigoModelo", codigoModelo);
		}

		return query.getResultList();
		
	}
	
}
