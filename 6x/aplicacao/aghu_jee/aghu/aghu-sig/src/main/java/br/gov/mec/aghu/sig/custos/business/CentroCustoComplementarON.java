package br.gov.mec.aghu.sig.custos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCustoCcts;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoCctsDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class CentroCustoComplementarON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(CentroCustoComplementarON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SigObjetoCustoCctsDAO sigObjetoCustoCctsDAO;

	private static final long serialVersionUID = -908345904034533L;
	
	public void persistirListaObjetoCustoCentroCusto(SigObjetoCustoVersoes objetoCustoVersao, List<SigObjetoCustoCcts> listaObjetoCustoCcts, List<SigObjetoCustoCcts> listaObjetoCustoCctsExclusao){
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		for(SigObjetoCustoCcts objetoCustoCcts :listaObjetoCustoCcts){
			
			objetoCustoCcts.setRapServidores(servidorLogado); //Servidor que fez a alteração
			
			if(objetoCustoCcts.getSeq() == null){
				//Define os atributos para inserir os dados
				objetoCustoCcts.setIndSituacao(DominioSituacao.A); 
				objetoCustoCcts.setCriadoEm(new Date()); 
				objetoCustoCcts.setSigObjetoCustoVersoes(objetoCustoVersao);
				this.getSigObjetoCustoCctsDAO().persistir(objetoCustoCcts);
			}
			else{
				this.getSigObjetoCustoCctsDAO().atualizar(objetoCustoCcts);
			}
		}
		
		//Percorre 
		for(SigObjetoCustoCcts objetoCustoCcts : listaObjetoCustoCctsExclusao){
			if(objetoCustoCcts.getSeq() != null && !objetoCustoCcts.getIndTipo().equals(DominioTipoObjetoCustoCcts.P)){
				this.getSigObjetoCustoCctsDAO().removerPorId(objetoCustoCcts.getSeq());
			}
		}
	}
	
	public SigObjetoCustoCctsDAO getSigObjetoCustoCctsDAO(){
		return sigObjetoCustoCctsDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
