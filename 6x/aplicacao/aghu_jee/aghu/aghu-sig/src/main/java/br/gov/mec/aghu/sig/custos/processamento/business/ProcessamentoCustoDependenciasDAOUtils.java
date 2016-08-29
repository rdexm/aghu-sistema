package br.gov.mec.aghu.sig.custos.processamento.business;

import javax.inject.Inject;

import br.gov.mec.aghu.internacao.dao.ProcEfetDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadeEquipamentosDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadeInsumosDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadePessoasDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadeServicosDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadesDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdProcedimentosDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdReceitaDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoDirecionadorDAO;
import br.gov.mec.aghu.sig.dao.SigCategoriaConsumosDAO;
import br.gov.mec.aghu.sig.dao.SigCategoriaRecursoDAO;
import br.gov.mec.aghu.sig.dao.SigControleVidaUtilDAO;
import br.gov.mec.aghu.sig.dao.SigDetalheFolhaPessoaDAO;
import br.gov.mec.aghu.sig.dao.SigDetalheProducaoDAO;
import br.gov.mec.aghu.sig.dao.SigDirecionadoresDAO;
import br.gov.mec.aghu.sig.dao.SigEquipamentoPatrimonioDAO;
import br.gov.mec.aghu.sig.dao.SigGrupoOcupacoesDAO;
import br.gov.mec.aghu.sig.dao.SigMvtoContaMensalDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoAnaliseDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoCctsDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoClientesDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoComposicoesDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoDirRateiosDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoPhisDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoVersoesDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustosDAO;
import br.gov.mec.aghu.sig.dao.SigPassosDAO;
import br.gov.mec.aghu.sig.dao.SigProcessamentoAlertasDAO;
import br.gov.mec.aghu.sig.dao.SigProcessamentoCustoDAO;
import br.gov.mec.aghu.sig.dao.SigProcessamentoCustoLogDAO;
import br.gov.mec.aghu.sig.dao.SigProcessamentoPassosDAO;
import br.gov.mec.aghu.sig.dao.SigProducaoObjetoCustoDAO;

public class ProcessamentoCustoDependenciasDAOUtils extends ProcessamentoCustoDependenciasCalculoDAOUtils {

	private static final long serialVersionUID = -5173541685761206413L;

	@Inject 
	private SigAtividadeInsumosDAO sigAtividadeInsumosDAO;
	
	@Inject   
	private SigObjetoCustoVersoesDAO sigObjetoCustoVersoesDAO;

	@Inject   
	private SigAtividadesDAO sigAtividadesDAO;

	@Inject   
	private SigAtividadePessoasDAO sigAtividadePessoasDAO;

	@Inject   
	private SigAtividadeEquipamentosDAO sigAtividadeEquipamentosDAO;

	@Inject   
	private SigObjetoCustoComposicoesDAO sigObjetoCustoComposicoesDAO;

	@Inject   
	private SigEquipamentoPatrimonioDAO sigEquipamentoPatrimonioDAO;

	@Inject   
	private SigObjetoCustoAnaliseDAO sigObjetoCustoAnaliseDAO;
	
	@Inject   
	private SigObjetoCustoCctsDAO sigObjetoCustoCctsDAO;

	@Inject   
	private SigCategoriaRecursoDAO sigCategoriaRecursoDAO;
	
	@Inject   
	private SigCategoriaConsumosDAO sigCategoriaConsumosDAO;

	@Inject   
	private SigGrupoOcupacoesDAO sigGrupoOcupacoesDAO;

	@Inject   
	private SigProcessamentoCustoLogDAO sigProcessamentoCustoLogDAO;

	@Inject   
	private SigProcessamentoCustoDAO sigProcessamentoCustoDAO;

	@Inject   
	private SigDetalheProducaoDAO sigDetalheProducaoDAO;

	@Inject   
	private SigProducaoObjetoCustoDAO sigProducaoObjetoCustoDAO;

	@Inject   
	private SigObjetoCustoPhisDAO sigObjetoCustoPhisDAO;

	@Inject   
	private SigControleVidaUtilDAO sigControleVidaUtilDAO;

	@Inject   
	private SigMvtoContaMensalDAO sigMvtoContaMensalDAO;

	@Inject   
	private SigDetalheFolhaPessoaDAO sigDetalheFolhaPessoaDAO;

	@Inject   
	private SigProcessamentoAlertasDAO sigProcessamentoAlertasDAO;

	@Inject   
	private SigPassosDAO sigPassosDAO;

	@Inject   
	private SigAtividadeServicosDAO sigAtividadeServicosDAO;

	@Inject   
	private SigProcessamentoPassosDAO sigProcessamentoPassosDAO;

	@Inject   
	private SigObjetoCustoClientesDAO sigObjetoCustoClientesDAO;
	
	@Inject   
	private SigDirecionadoresDAO sigDirecionadoresDAO;
	
	@Inject  
	private SigCalculoDirecionadorDAO sigCalculoDirecionadorDAO;

	@Inject  
	private SigObjetoCustoDirRateiosDAO sigObjetoCustoDirRateiosDAO;

	@Inject  
	private SigObjetoCustosDAO sigObjetoCustosDAO;
	
	@Inject
	private ProcEfetDAO procEfetDAO;
	
	@Inject
	private SigCalculoAtdReceitaDAO sigCalculoAtdReceitaDAO;
	
	@Inject
	private SigCalculoAtdProcedimentosDAO sigCalculoAtdProcedimentosDAO;
	
	
	public SigAtividadeInsumosDAO getSigAtividadeInsumosDAO() {
		return sigAtividadeInsumosDAO;
	}

	public SigObjetoCustoVersoesDAO getSigObjetoCustoVersoesDAO() {
		return sigObjetoCustoVersoesDAO;
	}

	public SigObjetoCustoPhisDAO getSigObjetoCustoPhisDAO() {
		return sigObjetoCustoPhisDAO;
	}

	public SigAtividadesDAO getSigAtividadesDAO() {
		return sigAtividadesDAO;
	}

	public SigAtividadePessoasDAO getSigAtividadePessoasDAO() {
		return sigAtividadePessoasDAO;
	}

	public SigAtividadeEquipamentosDAO getSigAtividadeEquipamentosDAO() {
		return sigAtividadeEquipamentosDAO;
	}

	public SigObjetoCustoComposicoesDAO getSigObjetoCustoComposicoesDAO() {
		return sigObjetoCustoComposicoesDAO;
	}

	public SigEquipamentoPatrimonioDAO getSigEquipamentoPatrimonioDAO() {
		return sigEquipamentoPatrimonioDAO;
	}

	public SigObjetoCustoAnaliseDAO getSigObjetoCustoAnaliseDAO() {
		return sigObjetoCustoAnaliseDAO;
	}

	public SigObjetoCustoCctsDAO getSigObjetoCustoCctsDAO() {
		return sigObjetoCustoCctsDAO;
	}
	
	public SigCategoriaRecursoDAO getSigCategoriaRecursoDAO() {
		return sigCategoriaRecursoDAO;
	}

	public SigGrupoOcupacoesDAO getSigGrupoOcupacoesDAO() {
		return sigGrupoOcupacoesDAO;
	}

	public SigProcessamentoCustoLogDAO getSigProcessamentoCustoLogDAO() {
		return sigProcessamentoCustoLogDAO;
	}

	public SigProcessamentoCustoDAO getSigProcessamentoCustoDAO() {
		return sigProcessamentoCustoDAO;
	}

	public SigDetalheProducaoDAO getSigDetalheProducaoDAO() {
		return sigDetalheProducaoDAO;
	}

	public SigProducaoObjetoCustoDAO getSigProducaoObjetoCustoDAO() {
		return sigProducaoObjetoCustoDAO;
	}

	public SigControleVidaUtilDAO getSigControleVidaUtilDAO() {
		return sigControleVidaUtilDAO;
	}

	public SigMvtoContaMensalDAO getSigMvtoContaMensalDAO() {
		return sigMvtoContaMensalDAO;
	}

	public SigDetalheFolhaPessoaDAO getSigDetalheFolhaPessoaDAO() {
		return sigDetalheFolhaPessoaDAO;
	}

	public SigProcessamentoAlertasDAO getSigProcessamentoAlertasDAO() {
		return sigProcessamentoAlertasDAO;
	}

	public SigPassosDAO getSigPassosDAO() {
		return sigPassosDAO;
	}

	public SigAtividadeServicosDAO getSigAtividadeServicosDAO() {
		return sigAtividadeServicosDAO;
	}

	public SigProcessamentoPassosDAO getSigProcessamentoPassosDAO() {
		return sigProcessamentoPassosDAO;
	}

	public SigObjetoCustoClientesDAO getSigObjetoCustoClientesDAO() {
		return sigObjetoCustoClientesDAO;
	}
	
	public SigCalculoDirecionadorDAO getSigCalculoDirecionadorDAO() {
		return sigCalculoDirecionadorDAO;
	}

	public SigObjetoCustoDirRateiosDAO getSigObjetoCustoDirRateiosDAO() {
		return sigObjetoCustoDirRateiosDAO;
	}
	
	public SigObjetoCustosDAO getSigObjetoCustosDAO(){	
		return sigObjetoCustosDAO;
	}
	
	public SigDirecionadoresDAO getSigDirecionadoresDAO() {
		return sigDirecionadoresDAO;
	}

	public SigCategoriaConsumosDAO getSigCategoriaConsumosDAO() {
		return sigCategoriaConsumosDAO;
	}

	public ProcEfetDAO getProcEfetDAO() {
		return procEfetDAO;
	}
	
	public SigCalculoAtdReceitaDAO getSigCalculoAtdReceitaDAO() {
		return sigCalculoAtdReceitaDAO;
	}

	public SigCalculoAtdProcedimentosDAO getSigCalculoAtdProcedimentosDAO() {
		return sigCalculoAtdProcedimentosDAO;
	}

	public void setSigCalculoAtdProcedimentosDAO(
			SigCalculoAtdProcedimentosDAO sigCalculoAtdProcedimentosDAO) {
		this.sigCalculoAtdProcedimentosDAO = sigCalculoAtdProcedimentosDAO;
	}
}
