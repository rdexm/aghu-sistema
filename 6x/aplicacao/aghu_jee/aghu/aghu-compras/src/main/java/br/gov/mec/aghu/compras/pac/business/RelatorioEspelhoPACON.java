package br.gov.mec.aghu.compras.pac.business;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.vo.RelatorioEspelhoPACVO;
import br.gov.mec.aghu.core.business.BaseBusiness;


@Stateless
public class RelatorioEspelhoPACON extends BaseBusiness implements Serializable{

	private static final Log LOG = LogFactory.getLog(RelatorioEspelhoPACON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}


	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4216766104835407921L;
		

	/**
	 * Cria uma lista de itens para o relatório espelho de licitação.<br>
	 * Se o itemInicial for nulo, a pesquisa considera desde o primeiro até o itemFinal.<br>
	 * Se o itemFinal for nulo, a pesquisa considera desde o itemInicial até o último item.<br>
	 * Se ambos forem preenchidos, a pesquisa considera apenas os itens contidos entre o itemInicial e o itemFinal.<br>
	 */
	public Set<RelatorioEspelhoPACVO> gerarDadosRelatorioEspelhoPAC(Integer numLicitacao){

		Set<RelatorioEspelhoPACVO> dadosRelatorio = new TreeSet<RelatorioEspelhoPACVO>(new OrdenadorUnionRelatorioEspelhoPAC());		
		List<RelatorioEspelhoPACVO> listaMateriaisLicitacao = null,
										  listaServicosLicitacao = null;
		
		listaMateriaisLicitacao = getScoLicitacaoDAO()
			.pesquisarDadosRelatorioEspelhoPACParaCompras(numLicitacao);
		dadosRelatorio.addAll(listaMateriaisLicitacao);
		
		listaServicosLicitacao = getScoLicitacaoDAO()
			.pesquisarDadosRelatorioEspelhoPACParaServicos(numLicitacao);
		dadosRelatorio.addAll(listaServicosLicitacao);
		
		return dadosRelatorio;
	}
	
	
	private ScoLicitacaoDAO getScoLicitacaoDAO(){
		return scoLicitacaoDAO;
	}
}