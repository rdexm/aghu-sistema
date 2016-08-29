package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.vo.AgendaProcedimentoPesquisaProfissionalVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class AgendaProcedimentosProfissionaisON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -556357164295746810L;

	private static final Log LOG = LogFactory.getLog(AgendaProcedimentosProfissionaisON.class);
	
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	
	
	
	//Pesquisa de profissionais para SuggestionBox, adicionando Função do profissinal ao vo 
	public List<AgendaProcedimentoPesquisaProfissionalVO> pesquisarProfissionaisAgendaENotaConsumoProcedimento(String strPesquisa, Short unfSeq, List<String> listaConselhos, boolean agenda) {
		List<AgendaProcedimentoPesquisaProfissionalVO> listaProfissionais = this.getRegistroColaboradorFacade().pesquisarProfissionaisAgendaProcedimentoENotaConsumo(strPesquisa, unfSeq, listaConselhos, agenda);
		
		SortedSet<Integer> listProfMatricula = new TreeSet<Integer>();
		SortedSet<Short> listProfVinculo = new TreeSet<Short>();
		SortedSet<String> listProfCprSigla = new TreeSet<String>();
		for (AgendaProcedimentoPesquisaProfissionalVO vo : listaProfissionais) {
			listProfMatricula.add(vo.getMatricula());
			listProfVinculo.add(vo.getVinCodigo());
			listProfCprSigla.add(vo.getCprSigla());
		}
		
		Map<String, String> mapEspecialidadesPorServidor;
		Map<String, String> mapNroConselhoPorServidor;
		if (!listProfMatricula.isEmpty() && !listProfVinculo.isEmpty()) {
			List<Integer> serMatricula = new ArrayList<Integer>(listProfMatricula.size());
			serMatricula.addAll(listProfMatricula);
			List<Short> serVinCodigo = new ArrayList<Short>(listProfVinculo.size());
			serVinCodigo.addAll(listProfVinculo);
			mapEspecialidadesPorServidor = this.getAghuFacade().obterMapaEspecialidadeConcatenadasProfCirurgiaoPorServidor(serMatricula, serVinCodigo);
			
			if (!listProfCprSigla.isEmpty()) {
				List<String> listSigla = new ArrayList<String>(listProfCprSigla.size());
				listSigla.addAll(listProfCprSigla);
				mapNroConselhoPorServidor = this.getRegistroColaboradorFacade().obterMapRegistroVRapServidorConselho(serMatricula, serVinCodigo, listSigla);
			} else {
				mapNroConselhoPorServidor = new HashMap<String, String>();
			}
		} else {
			mapEspecialidadesPorServidor = new HashMap<String, String>();
			mapNroConselhoPorServidor = new HashMap<String, String>();
		}
				
		for (AgendaProcedimentoPesquisaProfissionalVO vo : listaProfissionais) {
			// Busca especialidade
			vo.setEspecialidade(mapEspecialidadesPorServidor.get(vo.getMatricula() + "-" + vo.getVinCodigo()));
			
			vo.setNroRegConselho(mapNroConselhoPorServidor.get(vo.getMatricula() + "-" + vo.getVinCodigo() + "-" + vo.getCprSigla()));
		}
		
		/*
		for (AgendaProcedimentoPesquisaProfissionalVO vo : listaProfissionais) {
			// Busca especialidade
			vo.setEspecialidade(buscarEspecialidades(vo.getMatricula(), vo.getVinCodigo()));
			
			vo.setNroRegConselho(this.getRegistroColaboradorFacade().obterRegistroVRapServidorConselhoPeloId(
					vo.getMatricula(), vo.getVinCodigo(), vo.getCprSigla()));
		}
		*/
		
		return listaProfissionais;
	}
	
	public Long pesquisarProfissionaisAgendaENotaConsumoProcedimentoCount(String strPesquisa, Short unfSeq, List<String> listaConselhos, boolean agenda) {
		return this.getRegistroColaboradorFacade()
				.pesquisarProfissionaisAgendaProcedimentoENotaConsumoCount(
						strPesquisa, unfSeq, listaConselhos, agenda);
	}
	
	
	protected String buscarEspecialidades(Integer serMatricula, Short serVinCodigo) {
		return this.getAghuFacade().obterEspecialidadeConcatenadasProfCirurgiaoPorServidor(serMatricula, serVinCodigo);
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}
	
	

}
