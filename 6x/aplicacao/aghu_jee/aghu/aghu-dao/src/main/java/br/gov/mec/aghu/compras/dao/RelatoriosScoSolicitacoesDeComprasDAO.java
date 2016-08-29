package br.gov.mec.aghu.compras.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;

public class RelatoriosScoSolicitacoesDeComprasDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoSolicitacaoDeCompra>{
	
	private static final long serialVersionUID = -7131871323871344680L;
	
public Integer numeroAF1 (Integer numSolicComp){
		
		StringBuffer hql = null;
		Query query = null;
		
		hql = new StringBuffer();
			
		hql.append("SELECT MAX (AFN.").append(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()).append(") ");
		
		hql.append(" FROM ").append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSC, ");
		hql.append(ScoAutorizacaoForn.class.getSimpleName()).append(" AFN, ");
		hql.append(SceNotaRecebimento.class.getSimpleName()).append(" NRS ");
		hql.append(" WHERE FSC.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(" = :numSolicComp ");
		hql.append(" AND FSC.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString())
			.append(" = AFN.").append(ScoAutorizacaoForn.Fields.NUMERO.toString());
		hql.append(" AND FSC.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString())
			.append(" = NRS.").append(SceNotaRecebimento.Fields.AFN_NUMERO.toString());

		query = createHibernateQuery(hql.toString());
		query.setParameter("numSolicComp", numSolicComp);
	    
		List<Integer> numero1;
		Integer num;
        numero1 = query.list();
        num = numero1.get(0);
		
		return num;
	
	}

public Short numeroAF2 (Integer numSolicComp){
	StringBuffer hql = null;
	Query query = null;
	
	hql = new StringBuffer();
	hql.append("SELECT MAX (AFN.").append(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()).append(')'); 

	hql.append(" FROM ").append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSC, ");
	hql.append(ScoAutorizacaoForn.class.getSimpleName()).append(" AFN, ");
	hql.append(SceNotaRecebimento.class.getSimpleName()).append(" NRS ");
	hql.append(" WHERE FSC.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(" = :numSolicComp ");
	hql.append(" AND FSC.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString())
		.append(" = AFN.").append(ScoAutorizacaoForn.Fields.NUMERO.toString());
	hql.append(" AND FSC.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString())
		.append(" = NRS.").append(SceNotaRecebimento.Fields.AFN_NUMERO.toString());
	
	query = createHibernateQuery(hql.toString());
	query.setParameter("numSolicComp", numSolicComp);
	
	List<Short> numero2;
	Short num2;
    numero2 = query.list();
    num2 = numero2.get(0);
	
	return num2;
	
}

public Date dtEntrada (Integer numSolicComp){
	
	StringBuffer hql = null;
	Query query = null;
	
	hql = new StringBuffer();

	hql.append("SELECT MAX (NRS.").append(SceNotaRecebimento.Fields.DATA_GERACAO.toString()).append(')'); 
	hql.append(" FROM ").append(SceNotaRecebimento.class.getSimpleName()).append(" NRS, ");
	hql.append(SceItemNotaRecebimento.class.getSimpleName()).append(" INR, ");
	hql.append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSC ");
	hql.append(" WHERE FSC.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(" = :numSolicComp ");
	hql.append(" AND   INR.").append(SceItemNotaRecebimento.Fields.IAF_AFN_NUMERO.toString())
		.append(" = FSC.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()).append(' ');
	hql.append(" AND   INR.").append(SceItemNotaRecebimento.Fields.IAF_NUMERO.toString())
		.append(" = FSC.").append(ScoFaseSolicitacao.Fields.IAF_NUMERO.toString()).append(' ');
	hql.append(" AND   NRS.").append(SceNotaRecebimento.Fields.NUMERO_NR.toString())
	.append(" = INR.").append(SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO_SEQ.toString()).append(' ');
	
	query = createHibernateQuery(hql.toString());
	query.setParameter("numSolicComp", numSolicComp);
	
	List<Date> datein;
	Date dataEntr;
	datein = query.list();
	dataEntr = datein.get(0);
	
	return dataEntr;
	
}

public String endereco2 (Integer numSolicComp){
	
	StringBuffer hql = null;
	Query query = null;
	
	hql = new StringBuffer();
	
	hql.append("SELECT EAL1.").append(SceEstoqueAlmoxarifado.Fields.ENDERECO.toString()); 
	hql.append(" FROM ").append(SceEstoqueAlmoxarifado.class.getSimpleName()).append(" EAL1, ");
	hql.append(ScoMaterial.class.getSimpleName()).append(" MAT, ");
	hql.append(ScoSolicitacaoDeCompra.class.getSimpleName()).append(" SLC ");
	hql.append(" WHERE SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(" = :numSolicComp ");
	hql.append(" AND   EAL1.").append(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString())
		.append(" = SLC.").append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()).append(' ');
	hql.append(" AND   EAL1.").append(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString()).append(" = 1 ");
	
	query = createHibernateQuery(hql.toString());
	query.setParameter("numSolicComp", numSolicComp);
	query.setMaxResults(1);
	
	List<String> endereco2s;
	String endereco2 = null;
	endereco2s = query.list();
	if (endereco2s.size() > 0){
    endereco2 = endereco2s.get(0);
	}
	return endereco2;
}


public String responsavel (Short codigo, Integer matricula){
	
	StringBuffer hql = null;
	Query query = null;
	
	hql = new StringBuffer();
	
	hql.append("SELECT PES.").append(RapPessoasFisicas.Fields.NOME.toString()); 
	hql.append(" FROM ").append(RapPessoasFisicas.class.getSimpleName()).append(" PES, ");
	hql.append(RapServidores.class.getSimpleName()).append(" SER ");
	hql.append(" WHERE SER.").append(RapServidores.Fields.CODIGO_VINCULO.toString()).append(" = :codigo ");
	hql.append(" AND   SER.").append(RapServidores.Fields.MATRICULA.toString()).append(" = :matricula ");
	hql.append(" AND   PES.").append(RapPessoasFisicas.Fields.CODIGO.toString()).append(" = SER.").append(RapServidores.Fields.CODIGO_PESSOA_FISICA.toString());

	query = createHibernateQuery(hql.toString());
	query.setParameter("codigo", codigo);
	query.setParameter("matricula", matricula);
	
	List<String> responsavel;
	String resp = null;
	responsavel = query.list();
	if (responsavel.size() > 0){
		resp = responsavel.get(0);
	}
	return resp;
}


public String endereco (Integer numSolicComp){
	
	StringBuffer hql = null;
	Query query = null;
	
	hql = new StringBuffer();

	hql.append("SELECT EAL.").append(SceEstoqueAlmoxarifado.Fields.ENDERECO.toString()); 
	hql.append(" FROM ").append(SceEstoqueAlmoxarifado.class.getSimpleName()).append(" EAL, ");
	hql.append(SceAlmoxarifado.class.getSimpleName()).append(" ALM, ");
	hql.append(ScoSolicitacaoDeCompra.class.getSimpleName()).append(" SLC ");
	hql.append(" WHERE SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(" = :numSolicComp ");
	hql.append(" AND   ALM.").append(SceAlmoxarifado.Fields.CCT_CODIGO.toString())
		.append(" = SLC.").append(ScoSolicitacaoDeCompra.Fields.CCT_CODIGO.toString()).append(' ');
	hql.append(" AND   EAL.").append(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString())
		.append(" = ALM.").append(SceAlmoxarifado.Fields.SEQ.toString()).append(' ');
	hql.append(" AND   EAL.").append(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString())
		.append(" = SLC.").append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()).append(' ');

	query = createHibernateQuery(hql.toString());
	query.setParameter("numSolicComp", numSolicComp);
	query.setMaxResults(1);
	
	List<String> endereco1s;
	String endereco1 = null;
	endereco1s = query.list();
	if (endereco1s.size() > 0){
		endereco1 = endereco1s.get(0);
	}
	return endereco1;
}



}
