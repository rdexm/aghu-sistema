package br.gov.mec.aghu.compras.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoSuprimento;
import br.gov.mec.aghu.model.ScoArquivoAnexo;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class ScoArquivoAnexoDAO extends BaseDao<ScoArquivoAnexo> {
    
	@Inject
    private IPacFacade aIPacFacade;
    
	@Inject
    private ScoLicitacaoDAO aScoLicitacaoDAO;
    
	@Inject
    private ScoSolicitacaoServicoDAO aScoSolicitacaoServicoDAO;
    
	@Inject
    private ScoSolicitacoesDeComprasDAO aScoSolicitacoesDeComprasDAO;

	private static final long serialVersionUID = -3329220074961793537L;
	
		
	public List<ScoArquivoAnexo> pesquisarArquivosPorNumeroOrigem(DominioOrigemSolicitacaoSuprimento origem, Integer numero) {

		if (origem == null || numero == null){
			return null;
		}
		
		List<ScoArquivoAnexo> listaArquivo = new ArrayList<ScoArquivoAnexo>();
						
		if (origem == DominioOrigemSolicitacaoSuprimento.PC){
			List<Integer> numeroPAC = new ArrayList<Integer>();
			numeroPAC.add(numero);
			List<ScoArquivoAnexo> listaArquivoPAC = pesquisarArquivosPAC(numeroPAC);
			if (listaArquivoPAC != null && listaArquivoPAC.size() > 0){
				listaArquivo.addAll(listaArquivoPAC);
			}
		} 
		
		if (origem == DominioOrigemSolicitacaoSuprimento.SC){
			List<Integer> numeroSC = new ArrayList<Integer>();
			numeroSC.add(numero);
			List<ScoArquivoAnexo> listaArquivoSC = pesquisarArquivosSC(numeroSC);
			if (listaArquivoSC != null && listaArquivoSC.size() > 0){
				listaArquivo.addAll(listaArquivoSC);
			}
		} 

		if (origem == DominioOrigemSolicitacaoSuprimento.SS){
			List<Integer> numeroSS = new ArrayList<Integer>();
			numeroSS.add(numero);
			List<ScoArquivoAnexo> listaArquivoSS = pesquisarArquivosSS(numeroSS);
			if (listaArquivoSS != null && listaArquivoSS.size() > 0){
				listaArquivo.addAll(listaArquivoSS);
			}
		}	
			

		if (verificarOrigemPesquisaPadrao(origem)){
			List<Integer> listaNumero = new ArrayList<Integer>();
			listaNumero.add(numero);
			List<ScoArquivoAnexo> listaArquivos = obterArquivos(listaNumero, origem);
     		listaArquivo.addAll(listaArquivos);
		} 
		this.carregarUsuarios(listaArquivo);
		return listaArquivo;
	}
	
	public Boolean verificarOrigemPesquisaPadrao(DominioOrigemSolicitacaoSuprimento origem){
		return (origem == DominioOrigemSolicitacaoSuprimento.CM  ||
				origem == DominioOrigemSolicitacaoSuprimento.CS  ||
				origem == DominioOrigemSolicitacaoSuprimento.PMT ||
				origem == DominioOrigemSolicitacaoSuprimento.PAT || 
				origem == DominioOrigemSolicitacaoSuprimento.PAC ||
				origem == DominioOrigemSolicitacaoSuprimento.PAD ||
				origem == DominioOrigemSolicitacaoSuprimento.POC);
	}
	
	public void carregarUsuarios(List<ScoArquivoAnexo> listaArquivo){
		for (ScoArquivoAnexo scoArquivoAnexo : listaArquivo) {
			if(scoArquivoAnexo.getUsuario() != null && scoArquivoAnexo.getUsuario().getPessoaFisica() != null){
				 scoArquivoAnexo.getUsuario().getPessoaFisica().getNome();
			}
		}
	}
	
	private List<ScoArquivoAnexo> pesquisarArquivosPAC(List<Integer> numPac){
		if (numPac == null){
			return null;
		}
		
		List<ScoArquivoAnexo> listaArquivos = new ArrayList<ScoArquivoAnexo>();
		
		// consulta de arquivos relacionados diretamente ao PAC
		listaArquivos = obterArquivos(numPac, DominioOrigemSolicitacaoSuprimento.PC);
		
   	    ScoLicitacao scoLicitacao = getScoLicitacaoDAO().obterPorChavePrimaria(numPac.get(0));

   	    // consulta de arquivos relacionados às SC e seus materiais deste PAC
   	    List<Integer> listaNumSC = getPacFacade().retornarListaNumeroSolicicaoCompraPorPAC(scoLicitacao);
   	    List<ScoArquivoAnexo> listaArquivosSC = pesquisarArquivosSC(listaNumSC);
		
   	    if (listaArquivosSC != null && listaArquivosSC.size() > 0){
   	    	listaArquivos.addAll(listaArquivosSC);
   	    }
   	    
     	// consulta numero de arquivos relacionados às SS e seus serviços deste PAC
   	    List<Integer> listaNumSS = getPacFacade().retornarListaNumeroSolicicaoServicoPorPAC(scoLicitacao);
   	    List<ScoArquivoAnexo> listaArquivosSS = pesquisarArquivosSS(listaNumSS);	
		if (listaArquivosSS != null && listaArquivosSS.size() > 0){
			listaArquivos.addAll(listaArquivosSS);
		}
   	    
		return listaArquivos;
	}
	
	
	private List<ScoArquivoAnexo> pesquisarArquivosSC(List<Integer> listaNumSC){
		if (listaNumSC == null || listaNumSC.size() == 0){
			return null;
		}		
		
		List<ScoArquivoAnexo> listaArquivosSC = null;
		
		// consulta de arquivos relacionados diretamente à SC
   	    listaArquivosSC = obterArquivos(listaNumSC, DominioOrigemSolicitacaoSuprimento.SC);
   	    if (listaArquivosSC == null){
   	    	listaArquivosSC = new ArrayList<ScoArquivoAnexo>();
   	    }
   	    
     	// consulta aos arquivos relacionados aos materiais da lista de SC
   	    List<Integer> listaNumMaterial = retornarListaMaterialDeSolicitacoesCompra(listaNumSC);
   	    List<ScoArquivoAnexo> listaArquivosMaterial = obterArquivos(listaNumMaterial, DominioOrigemSolicitacaoSuprimento.CM);	
		if (listaArquivosMaterial != null && listaArquivosMaterial.size() > 0){
			listaArquivosSC.addAll(listaArquivosMaterial);
		}
		
		return listaArquivosSC;	
	}
	
	
	private List<ScoArquivoAnexo> pesquisarArquivosSS(List<Integer> listaNumSS){
		if (listaNumSS == null || listaNumSS.size() == 0){
			return null;
		}		
		
		List<ScoArquivoAnexo> listaArquivosSS = null;
		
		// consulta de arquivos relacionados diretamente à SS	
   	    listaArquivosSS = obterArquivos(listaNumSS, DominioOrigemSolicitacaoSuprimento.SS);
   	    
     	// consulta aos arquivos relacionados aos servicos da lista de SS
   	    List<Integer> listaNumServico = retornarListaServicoDeSolicitacoesServico(listaNumSS);
   	    List<ScoArquivoAnexo> listaArquivosServico = obterArquivos(listaNumServico, DominioOrigemSolicitacaoSuprimento.CS);	
		if (listaArquivosServico != null && listaArquivosServico.size() > 0){
			listaArquivosSS.addAll(listaArquivosServico);
		}
		
		return listaArquivosSS;	
	}
	
	public List<ScoArquivoAnexo> obterArquivos(List<Integer> listaID, DominioOrigemSolicitacaoSuprimento origem){
		 List<ScoArquivoAnexo> listaArquivos;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoArquivoAnexo.class);
		criteria.add(Restrictions.in(ScoArquivoAnexo.Fields.NUMERO.toString(), converteListaIntegerToBigInteger(listaID)));
   	    criteria.add(Restrictions.eq(ScoArquivoAnexo.Fields.TPORIGEM.toString(), origem));
		
   	    listaArquivos = executeCriteria(criteria);	
   	    
		return listaArquivos;
	}	
	
	public Boolean verificarExistenciaArquivosPorNumeroOrigem(DominioOrigemSolicitacaoSuprimento origem, Integer numero){
		Long numArquivos = pesquisarArquivosPorNumeroOrigemCount(origem, numero);
		return numArquivos != null && numArquivos > 0;
	}
	
	// Contagem de arquivos de cada Origem
	public Long pesquisarArquivosPorNumeroOrigemCount(DominioOrigemSolicitacaoSuprimento origem, Integer numero) {
		Long numArquivos = 0l;
		if (origem == null || numero == null){
			return null;
		}
				
		if (origem == DominioOrigemSolicitacaoSuprimento.PC){
			List<Integer> numeroPAC = new ArrayList<Integer>();
			numeroPAC.add(numero);			
			numArquivos = pesquisarArquivosPACCount(numeroPAC);
		} 
		
		if (origem == DominioOrigemSolicitacaoSuprimento.SC){
			List<Integer> numeroSC = new ArrayList<Integer>();
			numeroSC.add(numero);
			numArquivos = pesquisarArquivosSCCount(numeroSC);
		} 

		if (origem == DominioOrigemSolicitacaoSuprimento.SS){
			List<Integer> numeroSS = new ArrayList<Integer>();
			numeroSS.add(numero);
			numArquivos = pesquisarArquivosSSCount(numeroSS);
		} 
		
		if (verificarOrigemPesquisaPadrao(origem)){
			List<Integer> listaNumero = new ArrayList<Integer>();
			listaNumero.add(numero);
			numArquivos = pesquisarArquivosSSCount(listaNumero);
		} 
		
		return numArquivos;
	}
	
	public Long obterArquivosCount(List<Integer> listaID, DominioOrigemSolicitacaoSuprimento origem){
		Long numeroArquivos;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoArquivoAnexo.class);
		criteria.add(Restrictions.in(ScoArquivoAnexo.Fields.NUMERO.toString(), converteListaIntegerToBigInteger(listaID)));
 	    criteria.add(Restrictions.eq(ScoArquivoAnexo.Fields.TPORIGEM.toString(), origem));
		
 	    numeroArquivos = executeCriteriaCount(criteria);	
 	    
		return numeroArquivos;
	}	
	
	public Long pesquisarArquivosPACCount(List<Integer> numPac){
		Long numArquivos = 0l;
		
		// consulta numero de arquivos relacionados diretamente ao PAC
		numArquivos =  obterArquivosCount(numPac, DominioOrigemSolicitacaoSuprimento.PC);
		
   	    // consulta numero de arquivos relacionados às SC e seus materiais deste PAC
   	    ScoLicitacao scoLicitacao = getScoLicitacaoDAO().obterPorChavePrimaria(numPac.get(0));
   	    List<Integer> listaNumSC = getPacFacade().retornarListaNumeroSolicicaoCompraPorPAC(scoLicitacao);
   	    Long numArquivosSC = pesquisarArquivosSCCount(listaNumSC);
		
   	    if (numArquivosSC != null && numArquivosSC > 0){
   	    	numArquivos = numArquivos + numArquivosSC;
   	    }
   	    
     	// consulta numero de arquivos relacionados às SS e seus serviços deste PAC
   	    List<Integer> listaNumSS = getPacFacade().retornarListaNumeroSolicicaoServicoPorPAC(scoLicitacao);
   	    Long numArquivosSS = pesquisarArquivosSSCount(listaNumSS);	
		if (numArquivosSS != null && numArquivosSS > 0){
			numArquivos = numArquivos + numArquivosSS;
		}
   	    
		return numArquivos;
	}
	
	public Long pesquisarArquivosSCCount(List<Integer> listaNumSC){		
		if (listaNumSC == null || listaNumSC.size() == 0){
			return 0l;
		}		
		
		Long numArquivos = 0l;
		
		// consulta número de arquivos relacionados diretamente às SC
		numArquivos = obterArquivosCount(listaNumSC, DominioOrigemSolicitacaoSuprimento.SC);
		   	    
	    // consulta ao número da arquivos relacionados aos materiais da lista de SC
   	    List<Integer> listaNumMaterial = retornarListaMaterialDeSolicitacoesCompra(listaNumSC);
   	    Long numArquivosMaterial = obterArquivosCount(listaNumMaterial, DominioOrigemSolicitacaoSuprimento.CM);
   	    if (numArquivosMaterial != null && numArquivosMaterial > 0){
   	    	numArquivos = numArquivos + numArquivosMaterial;
   	    }
   	    
		return numArquivos;
	}
	
	public Long pesquisarArquivosSSCount(List<Integer> listaNumSS){
		if (listaNumSS == null || listaNumSS.size() == 0){
			return 0l;
		}		
		
		Long numArquivos = 0l;
		
		// consulta de arquivos relacionados diretamente à SS	
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoArquivoAnexo.class);
		criteria.add(Restrictions.in(ScoArquivoAnexo.Fields.NUMERO.toString(), converteListaIntegerToBigInteger(listaNumSS)));
   	    criteria.add(Restrictions.eq(ScoArquivoAnexo.Fields.TPORIGEM.toString(), DominioOrigemSolicitacaoSuprimento.SS));
		
   	    numArquivos = executeCriteriaCount(criteria);		
				
     	// consulta ao número da arquivos relacionados aos servicos da lista de SS
   	    List<Integer> listaNumServico = retornarListaServicoDeSolicitacoesServico(listaNumSS);
   	    Long numArquivosServico = obterArquivosCount(listaNumServico, DominioOrigemSolicitacaoSuprimento.CS);
		if (numArquivosServico != null && numArquivosServico > 0){
			numArquivos = numArquivos + numArquivosServico;
		}
		
		return numArquivos;
	}
	
	
	private List<Integer> retornarListaMaterialDeSolicitacoesCompra(List<Integer> listaNumSC){
		List<Integer> listaNumMaterial = new ArrayList<Integer>();
		ScoSolicitacaoDeCompra solicitacaCompra;
		
		for (Integer numSC : listaNumSC){
			solicitacaCompra = getScoSolicitacoesDeComprasDAO().obterPorChavePrimaria(numSC);
			if (solicitacaCompra != null && solicitacaCompra.getMaterial() != null){
				listaNumMaterial.add(solicitacaCompra.getMaterial().getCodigo());
			}			
		}
		
		return listaNumMaterial;
	}
	
	private List<Integer> retornarListaServicoDeSolicitacoesServico(List<Integer> listaNumSS){
		List<Integer> listaNumServico = new ArrayList<Integer>();
		ScoSolicitacaoServico solicitacaServico;
		
		for (Integer numSS : listaNumSS){
			solicitacaServico = getScoSolicitacaoServicoDAO().obterPorChavePrimaria(numSS);
			if (solicitacaServico != null && solicitacaServico.getServico() != null){
				listaNumServico.add(solicitacaServico.getServico().getCodigo());
			}			
		}
		
		return listaNumServico;
	}
	
	private List<BigInteger> converteListaIntegerToBigInteger(List<Integer> listaInteger){
		List<BigInteger> listaBigInteger = new ArrayList<BigInteger>();
		for (Integer inteiro : listaInteger){
			BigInteger bigInteiro = new BigInteger(inteiro.toString());
			listaBigInteger.add(bigInteiro);
		}
		
		return listaBigInteger;
	}
	

	private ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO() {
		return aScoSolicitacoesDeComprasDAO;
	}
	
	private ScoSolicitacaoServicoDAO getScoSolicitacaoServicoDAO() {
		return aScoSolicitacaoServicoDAO;
	}
		
	private ScoLicitacaoDAO getScoLicitacaoDAO() {
		return aScoLicitacaoDAO;
	}
	
	protected IPacFacade getPacFacade() {
		return aIPacFacade;
	}
	
}