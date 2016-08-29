package br.gov.mec.aghu.sig.custos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoClientesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ClienteObjetoCustoON extends BaseBusiness {


@EJB
private ObjetosCustoON objetosCustoON;

private static final Log LOG = LogFactory.getLog(ClienteObjetoCustoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SigObjetoCustoClientesDAO sigObjetoCustoClientesDAO;

	private static final long serialVersionUID = -7652532890062269569L;
	
	public enum ClienteObjetoCustoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CLIENTE_JA_ASSOCIADO_OBJETO_CUSTO, MENSAGEM_TODOS_CC_MARCADO_CLIENTES_JA_ASSOCIADOS,
		MENSAGEM_EXCLUSAO_CLIENTE_ASSOCIADO_OBJETO_CUSTO_ATIVO;
	}

	public void validarInclusaoAlteracao(SigObjetoCustoClientes sigObjetoCustoClientes, List<SigObjetoCustoClientes> listaClientes, boolean alteracao) throws ApplicationBusinessException{
		//se não estiver marcado a opção "Todos centros custo"
		if((sigObjetoCustoClientes.getIndTodosCct() != null && !sigObjetoCustoClientes.getIndTodosCct()) || sigObjetoCustoClientes.getIndTodosCct() == null){
			//FE02
			for(SigObjetoCustoClientes itemLista : listaClientes){
				
				//FE04
				if(itemLista.getSituacao().equals(DominioSituacao.A)) {
					
					//Todos os centros de custo
					if( itemLista.getIndTodosCct() != null 
						&& itemLista.getIndTodosCct().equals(Boolean.TRUE)
						&& itemLista.getDirecionadores().getSeq().equals(sigObjetoCustoClientes.getDirecionadores().getSeq())){
						throw new ApplicationBusinessException(ClienteObjetoCustoONExceptionCode.MENSAGEM_TODOS_CC_MARCADO_CLIENTES_JA_ASSOCIADOS);
					}
					//centro custo
					else if(!alteracao && sigObjetoCustoClientes.getCentroCusto() != null){
						if(itemLista.getCentroCusto()!= null && itemLista.getCentroCusto().getCodigo().equals(sigObjetoCustoClientes.getCentroCusto().getCodigo()) 
							&& itemLista.getDirecionadores().getSeq().equals(sigObjetoCustoClientes.getDirecionadores().getSeq())){
							throw new ApplicationBusinessException(ClienteObjetoCustoONExceptionCode.MENSAGEM_CLIENTE_JA_ASSOCIADO_OBJETO_CUSTO,sigObjetoCustoClientes.getCentroCusto().getDescricao(), sigObjetoCustoClientes.getDirecionadores().getNome());
						}
					//centro producao	
					}else if(!alteracao) {
						if( itemLista.getCentroProducao()!= null && itemLista.getCentroProducao().getSeq().equals(sigObjetoCustoClientes.getCentroProducao().getSeq())
							&& itemLista.getDirecionadores().getSeq().equals(sigObjetoCustoClientes.getDirecionadores().getSeq())){
							throw new ApplicationBusinessException(ClienteObjetoCustoONExceptionCode.MENSAGEM_CLIENTE_JA_ASSOCIADO_OBJETO_CUSTO,sigObjetoCustoClientes.getCentroProducao().getNome(), sigObjetoCustoClientes.getDirecionadores().getNome());
						}
					}
				}
			}
		//se estiver marcado a opção "Todos centros de custo"
		//FE03
		}else if(listaClientes != null && !listaClientes.isEmpty()) {
			for(SigObjetoCustoClientes itemLista : listaClientes){
				if( itemLista.getSituacao().equals(DominioSituacao.A)
					&& (sigObjetoCustoClientes.getSeq() == null || !sigObjetoCustoClientes.getSeq().equals(itemLista.getSeq()))
					&& itemLista.getDirecionadores().getSeq().equals(sigObjetoCustoClientes.getDirecionadores().getSeq()) ){
					throw new ApplicationBusinessException(ClienteObjetoCustoONExceptionCode.MENSAGEM_TODOS_CC_MARCADO_CLIENTES_JA_ASSOCIADOS);
				}
			}
		}	
	}

	public void validarExclusao(SigObjetoCustoVersoes objetoCustoVersoes) throws ApplicationBusinessException {
		if(this.getObjetosCustoON().validarExclusaoObjetoCustoAtivoMaisUmMes(objetoCustoVersoes)){
			throw new ApplicationBusinessException(ClienteObjetoCustoONExceptionCode.MENSAGEM_EXCLUSAO_CLIENTE_ASSOCIADO_OBJETO_CUSTO_ATIVO);
		}
	}
	
	public void persistirCliente(SigObjetoCustoClientes cliente) {
		if(cliente.getSeq() == null){
			this.getSigObjetoCustoClientesDAO().persistir(cliente);
		}else {
			this.getSigObjetoCustoClientesDAO().atualizar(cliente);
		}
		
	}
	
	public void excluirCliente(SigObjetoCustoClientes cliente) {
		cliente = this.getSigObjetoCustoClientesDAO().merge(cliente);
		this.getSigObjetoCustoClientesDAO().remover(cliente);
	}
	
	protected ObjetosCustoON getObjetosCustoON(){
		return objetosCustoON;
	}
	
	protected SigObjetoCustoClientesDAO getSigObjetoCustoClientesDAO() {
		return sigObjetoCustoClientesDAO;
	}
	
}
 