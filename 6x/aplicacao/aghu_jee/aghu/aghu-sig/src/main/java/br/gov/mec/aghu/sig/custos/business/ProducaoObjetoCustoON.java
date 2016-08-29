package br.gov.mec.aghu.sig.custos.business;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioGrupoDetalheProducao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoProcessamentoCusto;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigDetalheProducao;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sig.dao.SigDetalheProducaoDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoClientesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ProducaoObjetoCustoON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(ProducaoObjetoCustoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SigDetalheProducaoDAO sigDetalheProducaoDAO;
	
	@EJB
	private ICustosSigFacade custosSigFacade;
	
	@Inject
	private SigObjetoCustoClientesDAO sigObjetoCustoClientesDAO;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	private static final long serialVersionUID = -8904568965L;

	public enum ProducaoObjetoCustoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_ALTERACAO_PRODUCAO_OBJETO_CUSTO,
		MENSAGEM_ERRO_EXCLUSAO_PRODUCAO_OBJETO_CUSTO,
		MENSAGEM_ERRO_OBJETO_CUSTO_SEM_CLIENTES,
		MENSAGEM_ERRO_VALOR_NENHUM_VALOR_INFORMADO
	}

	public void persistirProducaoObjetoCusto(
			SigObjetoCustoVersoes objetoCustoVersao, SigDirecionadores direcionador, SigProcessamentoCusto competencia,
			List<SigDetalheProducao> listaClientes) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(listaClientes==null || listaClientes.isEmpty()){
			throw new ApplicationBusinessException(ProducaoObjetoCustoONExceptionCode.MENSAGEM_ERRO_OBJETO_CUSTO_SEM_CLIENTES);	
		}
		
		
		List<SigDetalheProducao> listaRemovidos = new ArrayList<SigDetalheProducao>();
		for(SigDetalheProducao detalheProducao :listaClientes){
			
			detalheProducao.setQtde(this.normalizarValorZero(detalheProducao.getQtde()));
			
			if(detalheProducao.getQtde() != null){
				
				detalheProducao.setRapServidores(servidorLogado);
				
				if(detalheProducao.getSeq() == null){
					detalheProducao.setCriadoEm(new Date());
					detalheProducao.setSigObjetoCustoVersoes(objetoCustoVersao);
					detalheProducao.setSigDirecionadores(direcionador);
					detalheProducao.setSigProcessamentoCustos(competencia);
					detalheProducao.setGrupo(DominioGrupoDetalheProducao.PAM);
					this.getSigDetalheProducaoDAO().persistir(detalheProducao);
				}
				else{
					this.getSigDetalheProducaoDAO().atualizar(detalheProducao);
				}	
			}else{
				if(detalheProducao.getSeq() != null){
					listaRemovidos.add(detalheProducao);
					this.getSigDetalheProducaoDAO().remover(detalheProducao);
				}
			}
		}
		
		//Retira da lista os removidos
		for(SigDetalheProducao detalheProducao : listaRemovidos){
			listaClientes.remove(detalheProducao);
		}
	}

	public boolean verificarPreenchimentoValoresClientes (List<SigDetalheProducao> listaClientes) throws ApplicationBusinessException {
		
		boolean alterouValorPeloMenosUmRegistro = false;
		boolean naoInformouValorUmRegistro = false;
		
		//Se existir algum valor não prenchido, deve retornar falso
		for(SigDetalheProducao cliente :listaClientes){
			cliente.setQtde(this.normalizarValorZero(cliente.getQtde()));
			
			if(cliente.getQtde() == null ){
				
				naoInformouValorUmRegistro = true;//Um dos registro não possui valor
				
				if(cliente.getSeq() != null){
					alterouValorPeloMenosUmRegistro = true;//Terá que ser excluído
				}
			}
			else{
				alterouValorPeloMenosUmRegistro = true;//Terá que ser incluído ou atualizado
			}
		}
		
		if(!alterouValorPeloMenosUmRegistro){
			throw new ApplicationBusinessException(ProducaoObjetoCustoONExceptionCode.MENSAGEM_ERRO_VALOR_NENHUM_VALOR_INFORMADO);
		}	
		
		return naoInformouValorUmRegistro;
	}

	public BigDecimal calcularValorTotal(List<SigDetalheProducao> listaClientes) {
		
		BigDecimal valorTotal = BigDecimal.ZERO;
		
		//Se existir algum valor não prenchido, deve retornar falso
		for(SigDetalheProducao cliente :listaClientes){
			cliente.setQtde(this.normalizarValorZero(cliente.getQtde()));
			if(cliente.getQtde() != null){				
				valorTotal = valorTotal.add(cliente.getQtde());
			}
		}
		
		return valorTotal;
	}

	private BigDecimal normalizarValorZero(BigDecimal valor){
		if(valor != null && valor.intValue() != 0){
			return valor;
		}
		return null;
	}
	
	private boolean verificarSituacaoCompetenciaPermiteAcao(DominioSituacaoProcessamentoCusto situacao){
		if(situacao.equals(DominioSituacaoProcessamentoCusto.A) 
				|| situacao.equals(DominioSituacaoProcessamentoCusto.P)
				|| situacao.equals(DominioSituacaoProcessamentoCusto.F)){
			return false;
		}
		else{
			return true;
		}
	}
	
	public void verificarEdicaoDetalheProducao(Integer seq) throws ApplicationBusinessException{
		SigDetalheProducao detalheProducao = this.getSigDetalheProducaoDAO().obterPorChavePrimaria(seq);
		if(!this.verificarSituacaoCompetenciaPermiteAcao(detalheProducao.getSigProcessamentoCustos().getIndSituacao())){
			throw new ApplicationBusinessException(ProducaoObjetoCustoONExceptionCode.MENSAGEM_ERRO_ALTERACAO_PRODUCAO_OBJETO_CUSTO,detalheProducao.getSigProcessamentoCustos().getCompetenciaMesAno(), detalheProducao.getSigProcessamentoCustos().getIndSituacao().getDescricao());	
		}
	}
	
	public void excluirDetalheProducao(Integer seqDetalheProducao) throws ApplicationBusinessException {
		
		SigDetalheProducao detalheProducao = this.getSigDetalheProducaoDAO().obterPorChavePrimaria(seqDetalheProducao);
		
		if(this.verificarSituacaoCompetenciaPermiteAcao(detalheProducao.getSigProcessamentoCustos().getIndSituacao())){
			
			List<Integer> lista = this.getSigDetalheProducaoDAO().pesquisarDetalhesProducoesParaExcluir(detalheProducao.getSigObjetoCustoVersoes().getSeq(),
					detalheProducao.getSigDirecionadores(), detalheProducao.getSigProcessamentoCustos().getSeq());
			
			for(Integer idDetalhe : lista){
				this.getSigDetalheProducaoDAO().remover(this.getSigDetalheProducaoDAO().obterPorChavePrimaria(idDetalhe));
			}
			this.getSigDetalheProducaoDAO().flush();
		}
		else{
			throw new ApplicationBusinessException(ProducaoObjetoCustoONExceptionCode.MENSAGEM_ERRO_EXCLUSAO_PRODUCAO_OBJETO_CUSTO,detalheProducao.getSigProcessamentoCustos().getCompetenciaMesAno(), detalheProducao.getSigProcessamentoCustos().getIndSituacao().getDescricao());	
		}
	}
	
	public List<SigDetalheProducao> listarClientesObjetoCustoVersao(SigObjetoCustoVersoes sigObjetoCustoVersoes, SigDirecionadores sigDirecionadores, SigProcessamentoCusto competencia) {
		
		List<SigDetalheProducao> lista = new ArrayList<SigDetalheProducao>();
		
		List<SigObjetoCustoClientes> listaClientes =  this.getSigObjetoCustoClientesDAO().listarClientesObjetoCustoVersao(sigObjetoCustoVersoes,sigDirecionadores,null);
		
		//Se retornou algum cliente
		if(listaClientes != null && !listaClientes.isEmpty()){ 
			
			List<FccCentroCustos> listaCentroCustos = null;
			
			//possui somente um registro e indica que deve utilizar todos os centros de custo
			if(listaClientes.size() == 1 && listaClientes.get(0).getIndTodosCct() != null && listaClientes.get(0).getIndTodosCct()){
				//Busca todos os centro de custos ativos
				listaCentroCustos = this.getCentroCustoFacade().pesquisarCentroCustosPorCentroProdExcluindoGcc(null, null, DominioSituacao.A);
			}
			//Se possui mais de um cliente
			else{
				listaCentroCustos = new ArrayList<FccCentroCustos>();
				
				//Copia os centros de custo da lista de clientes
				for(SigObjetoCustoClientes cliente : listaClientes){
					listaCentroCustos.add(cliente.getCentroCusto());
				}
			}
		
			//Quando encontrar algum cliente
			if(listaCentroCustos != null && !listaCentroCustos.isEmpty()){
				
				//Para cada cliente
				for(FccCentroCustos centroCusto : listaCentroCustos){
					
					//verifica se o mesmo já foi cadastrado
					SigDetalheProducao detalheProducao = this.getSigDetalheProducaoDAO().obterPorParametrosCliente(centroCusto, sigObjetoCustoVersoes, sigDirecionadores, competencia);
					
					//Quando for um novo registro, instancia um novo objeto
					if(detalheProducao == null){
						detalheProducao = new SigDetalheProducao();
						//detalheProducao.setQtde();
						detalheProducao.setFccCentroCustos(centroCusto);
					}
					
					lista.add(detalheProducao);
				}
			}
		}
		
		return lista;
	}
	
	protected SigObjetoCustoClientesDAO getSigObjetoCustoClientesDAO(){
		return sigObjetoCustoClientesDAO;
	}
	
	protected SigDetalheProducaoDAO getSigDetalheProducaoDAO(){
		return sigDetalheProducaoDAO;
	}
	
	protected ICustosSigFacade getCustosSigFacade(){
		return this.custosSigFacade;
	}
	
	protected ICentroCustoFacade getCentroCustoFacade(){
		return this.centroCustoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
