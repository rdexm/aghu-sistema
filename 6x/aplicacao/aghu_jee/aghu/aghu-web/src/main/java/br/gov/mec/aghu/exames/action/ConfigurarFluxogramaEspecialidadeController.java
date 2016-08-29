package br.gov.mec.aghu.exames.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.ExameDisponivelFluxogramaVO;
import br.gov.mec.aghu.exames.vo.ExameEspecialidadeSelecionadoFluxogramaVO;
import br.gov.mec.aghu.model.AelEspecialidadeCampoLaudo;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ConfigurarFluxogramaEspecialidadeController extends ActionController {

	private static final long serialVersionUID = -9001056452810270132L;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IExamesFacade examesFacade;
	
	// Variaveis que representam os campos do XHTML
	private AghEspecialidades especialidade;
	

	// Listas
	private List<ExameDisponivelFluxogramaVO> listaExamesDisponiveis = new LinkedList<ExameDisponivelFluxogramaVO>();
	private List<ExameEspecialidadeSelecionadoFluxogramaVO> listaExamesEspecialidadeSelecionados = new LinkedList<ExameEspecialidadeSelecionadoFluxogramaVO>();
	private List<ExameEspecialidadeSelecionadoFluxogramaVO> listaExamesRemovidos = new ArrayList<ExameEspecialidadeSelecionadoFluxogramaVO>();
			
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 


		// Popula lista de Exames Disponíveis para o Fluxograma
		this.listaExamesDisponiveis = this.examesFacade.pesquisarExamesDisponiveisFluxograma();
		this.listaExamesRemovidos.clear();
	
	}

	/**
	 * Obtém lista para SuggestionBox de Especialidades
	 */
	public List<AghEspecialidades> pesquisarEspecialidade(String parametro){
		
		String strPesquisa = (String) parametro;

		if (StringUtils.isNotBlank(strPesquisa)) {
			strPesquisa = strPesquisa.trim();
		}
		
		return this.aghuFacade.pesquisarEspecialidadesPorNomeOuSigla(strPesquisa);
	}
	
	/**
	 * Popula lista de Exames Especialidade Selecionados através da Especialidade
	 */
	public void pesquisarExamesEspeciadadeSelecionadosPorEspecialidade(){
		this.listaExamesEspecialidadeSelecionados = this.examesFacade.pesquisarExamesEspeciadadeSelecionadosPorEspecialidade(this.especialidade);
	}
	
	/**
	 * Limpar lista de Exames Especialidade Selecionados através da Especialidade
	 */
	public void limparListaExamesEspecialidadeSelecionados(){
		this.listaExamesEspecialidadeSelecionados.clear();
	}
	
	/*
	 * Controla a seleção nas listas de exames
	 */	
	
	/**
	 * Controla a listagem de Exames Disponíveis para Fluxograma
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ExameDisponivelFluxogramaVO> carregarExamesDisponiveis() {
		return this.listaExamesDisponiveis ;
	}
	

	/**
	 * Controla a listagem de Exames Selecionados
	 */
	public List<ExameEspecialidadeSelecionadoFluxogramaVO> carregarExamesSelecionados() throws ApplicationBusinessException {
		this.listaExamesRemovidos.clear();
		// Instancia a ordenação da lista de exames selecionados 
		Comparator<ExameEspecialidadeSelecionadoFluxogramaVO> ordenacao = new Comparator<ExameEspecialidadeSelecionadoFluxogramaVO>() {
			@Override
			public int compare(ExameEspecialidadeSelecionadoFluxogramaVO o1, ExameEspecialidadeSelecionadoFluxogramaVO o2) {
				return o1.getEspecialidadeCampoLaudo().getOrdem().compareTo(o2.getEspecialidadeCampoLaudo().getOrdem());
			}
		};
		
		for (ExameEspecialidadeSelecionadoFluxogramaVO item : this.listaExamesEspecialidadeSelecionados) {	
			if(StringUtils.isEmpty(item.getEspecialidadeCampoLaudo().getNomeSumario()) || item.getEspecialidadeCampoLaudo().getOrdem() == null){
				return this.listaExamesEspecialidadeSelecionados;
			}
		}
		
		// Ordena lista
		Collections.sort(this.listaExamesEspecialidadeSelecionados, ordenacao);
		
		return listaExamesEspecialidadeSelecionados;
	}
	
	/*
	 * Controla as operações de adição e remoção de exames nas listas
	 */
	
	/**
	 * Adiciona exame selecionado
	 */
	public void adicionarExames() {
		
		boolean isSelecionado = false;
		
		for (ExameDisponivelFluxogramaVO exameDisponivelFluxogramaVO : this.listaExamesDisponiveis) {
			if(exameDisponivelFluxogramaVO.isSelecionado()){
				
				isSelecionado = true;
				
				final ExameEspecialidadeSelecionadoFluxogramaVO exameSelecionadoFluxogramaVO = this.getExameEspecialidadeSelecionadoFluxogramaVO(exameDisponivelFluxogramaVO);
				
				// Verifica se o item não existe na lista
				if (!this.listaExamesEspecialidadeSelecionados.contains(exameSelecionadoFluxogramaVO)) {
					this.listaExamesEspecialidadeSelecionados.add(exameSelecionadoFluxogramaVO);
				}
				if (this.listaExamesRemovidos.contains(exameDisponivelFluxogramaVO)) {
					this.listaExamesRemovidos.remove(exameDisponivelFluxogramaVO);
				}
				
				// Por padrão o item não deve ficar selecionado
				exameDisponivelFluxogramaVO.setSelecionado(false);
				
			}
		}
		
		if(!isSelecionado){
			this.apresentarMsgNegocio(Severity.WARN,"MENSAGEM_SELECIONE_MINIMO_UM_ITEM");
		}
		
	}
	
	/**
	 * Instancia um ExameSelecionadoFluxogramaVO através de uma instância de ExameDisponivelFluxogramaVO
	 * @param exameDisponivelFluxogramaVO
	 * @return
	 */
	public ExameEspecialidadeSelecionadoFluxogramaVO getExameEspecialidadeSelecionadoFluxogramaVO(ExameDisponivelFluxogramaVO exameDisponivelFluxogramaVO){
		
		ExameEspecialidadeSelecionadoFluxogramaVO exameEspecialidadeSelecionadoFluxogramaVO = new ExameEspecialidadeSelecionadoFluxogramaVO();
		
		exameEspecialidadeSelecionadoFluxogramaVO.setSeqCampoLaudo(exameDisponivelFluxogramaVO.getCampoLaudo().getSeq());
		
		AelEspecialidadeCampoLaudo especialidadeCampoLaudo = new AelEspecialidadeCampoLaudo();
		
		especialidadeCampoLaudo.setCampoLaudo(exameDisponivelFluxogramaVO.getCampoLaudo());
		especialidadeCampoLaudo.setEspecialidade(this.especialidade);
		
		especialidadeCampoLaudo.setNomeSumario(StringUtils.trim(exameDisponivelFluxogramaVO.getCampoLaudo().getNomeSumario().toUpperCase()));
		especialidadeCampoLaudo.setOrdem(exameDisponivelFluxogramaVO.getCampoLaudo().getOrdem());
		
		exameEspecialidadeSelecionadoFluxogramaVO.setEspecialidadeCampoLaudo(especialidadeCampoLaudo);
		exameEspecialidadeSelecionadoFluxogramaVO.setSelecionado(false);
		
		return exameEspecialidadeSelecionadoFluxogramaVO;
	}
	

	/**
	 * Remove exame selecionado
	 */
	public void removerExames() {
		boolean isSelecionado = false;
		// Restaura atributos modificados nos Exames Selecionados
		for (ExameEspecialidadeSelecionadoFluxogramaVO vo : this.listaExamesEspecialidadeSelecionados) {	
			if(vo.isSelecionado()){
				isSelecionado = true;
				listaExamesRemovidos.add(vo);
			}
		}
		if(!isSelecionado){
			this.apresentarMsgNegocio(Severity.WARN,"MENSAGEM_SELECIONE_MINIMO_UM_ITEM");
		}
		this.listaExamesEspecialidadeSelecionados.removeAll(listaExamesRemovidos);
	}
	
	/**
	 * Adiciona todos exames da lista de Exames Disponíveis para Fluxograma
	 */
	public void adicionarTodosExames() {

		for (ExameDisponivelFluxogramaVO exameDisponivelFluxogramaVO : this.listaExamesDisponiveis) {
			
			final ExameEspecialidadeSelecionadoFluxogramaVO exameSelecionadoFluxogramaVO = this.getExameEspecialidadeSelecionadoFluxogramaVO(exameDisponivelFluxogramaVO);
			
			if (!this.listaExamesEspecialidadeSelecionados.contains(exameSelecionadoFluxogramaVO)) {
				this.listaExamesEspecialidadeSelecionados.add(exameSelecionadoFluxogramaVO);
			}
			
			exameDisponivelFluxogramaVO.setSelecionado(false);
			
		}
		
	}
	
	/**
	 * Remove todos exames lista de Exames Selecionados
	 */
	public void removerTodosExames() {
		this.listaExamesRemovidos.addAll(listaExamesEspecialidadeSelecionados);
		this.listaExamesEspecialidadeSelecionados.clear();
	}

	/**
	 * Grava lista de Exames Selecionados
	 */
	public String gravar() {

		for (ExameEspecialidadeSelecionadoFluxogramaVO item : this.listaExamesEspecialidadeSelecionados) {	
			if(StringUtils.isEmpty(item.getEspecialidadeCampoLaudo().getNomeSumario()) || item.getEspecialidadeCampoLaudo().getOrdem() == null){
				this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_CONFIGURAR_FLUXOGRAMA_NOME_SUMARIO_ORDEM_INVALIDOS");
				return null;
			}
		}

		try {
			this.examesFacade.persistirExamesFluxogramaSelecionadosPorEspecialidade(this.especialidade, this.listaExamesEspecialidadeSelecionados,
					this.listaExamesRemovidos);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		this.listaExamesDisponiveis = this.examesFacade.pesquisarExamesDisponiveisFluxograma();
		this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_SALVOS_CONFIGURAR_FLUXOGRAMA" );

		return null;
	}

	
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}
	
	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}
	
	public List<ExameDisponivelFluxogramaVO> getListaExamesDisponiveis() {
		return listaExamesDisponiveis;
	}
	
	public void setListaExamesDisponiveis(List<ExameDisponivelFluxogramaVO> listaExamesDisponiveis) {
		this.listaExamesDisponiveis = listaExamesDisponiveis;
	}
	
	public List<ExameEspecialidadeSelecionadoFluxogramaVO> getListaExamesEspecialidadeSelecionados() {
		return listaExamesEspecialidadeSelecionados;
	}
	
	public void setListaExamesEspecialidadeSelecionados(List<ExameEspecialidadeSelecionadoFluxogramaVO> listaExamesEspecialidadeSelecionados) {
		this.listaExamesEspecialidadeSelecionados = listaExamesEspecialidadeSelecionados;
	}

	public List<ExameEspecialidadeSelecionadoFluxogramaVO> getExamesRemovidos() {
		return listaExamesRemovidos;
	}

	public void setExamesRemovidos(List<ExameEspecialidadeSelecionadoFluxogramaVO> examesRemovidos) {
		this.listaExamesRemovidos = examesRemovidos;
	}
}