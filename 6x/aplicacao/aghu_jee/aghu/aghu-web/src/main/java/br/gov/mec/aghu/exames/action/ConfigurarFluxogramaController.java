package br.gov.mec.aghu.exames.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.ExameDisponivelFluxogramaVO;
import br.gov.mec.aghu.exames.vo.ExameSelecionadoFluxogramaVO;
import br.gov.mec.aghu.model.AelServidorCampoLaudo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class ConfigurarFluxogramaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ConfigurarFluxogramaController.class);


	/**
	 * 
	 */
	private static final long serialVersionUID = 2617281514480525419L;

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;		

	// Variaveis que representam os campos do XHTML
	private RapServidores servidorLogado;

	// Listas
	private List<ExameDisponivelFluxogramaVO> listaExamesDisponiveis = new LinkedList<ExameDisponivelFluxogramaVO>();
	private List<ExameSelecionadoFluxogramaVO> listaExamesSelecionados = new LinkedList<ExameSelecionadoFluxogramaVO>();
	
	/*
	 * Chamado no inicio de "cada conversação"
	 */
	public void inicio() {
	 

		
		// Obtém o Servidor Logado
		this.servidorLogado = null;
		try {
			this.servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
			
			// Popula lista de Exames Disponíveis para o Fluxograma
			this.listaExamesDisponiveis = this.examesFacade.pesquisarExamesDisponiveisFluxograma();
			
			// Popula lista de Exames Selecionados pelo Servidor
			this.listaExamesSelecionados = this.examesFacade.pesquisarExamesSelecionadosPorServidorLogado();
			
		} catch (BaseException e) {
			LOG.error("Não encontrou o servidor logado!", e);
		}
	
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
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ExameSelecionadoFluxogramaVO> carregarExamesSelecionados() throws ApplicationBusinessException {
		
		// Instancia a ordenação da lista de exames selecionados 
		Comparator<ExameSelecionadoFluxogramaVO> ordenacao = new Comparator<ExameSelecionadoFluxogramaVO>() {
			@Override
			public int compare(ExameSelecionadoFluxogramaVO o1, ExameSelecionadoFluxogramaVO o2) {
				return o1.getServidorCampoLaudo().getOrdem().compareTo(o2.getServidorCampoLaudo().getOrdem());
			}
		};
		
		for (ExameSelecionadoFluxogramaVO item : this.listaExamesSelecionados) {	
			if(StringUtils.isEmpty(item.getServidorCampoLaudo().getNomeSumario()) || item.getServidorCampoLaudo().getOrdem() == null){
				return this.listaExamesSelecionados;
			}
		}
		
		// Ordena lista
		Collections.sort(this.listaExamesSelecionados, ordenacao);
		
		return listaExamesSelecionados;
	}
	
	/*
	 * Controla as operações de adição e remoção de exames nas listas
	 */
	
	/**
	 * Adiciona exame selecionado
	 */
	public void adicionarExames() {
		
		boolean isSelecionado = false;
		
		List<ExameDisponivelFluxogramaVO> listaExamesExcluir = new LinkedList<ExameDisponivelFluxogramaVO>();
		
		for (ExameDisponivelFluxogramaVO exameDisponivelFluxogramaVO : this.listaExamesDisponiveis) {
			if(exameDisponivelFluxogramaVO.isSelecionado()){
				
				isSelecionado = true;
				
				final ExameSelecionadoFluxogramaVO exameSelecionadoFluxogramaVO = this.getExameSelecionadoFluxogramaVO(exameDisponivelFluxogramaVO);
				
				if(exameSelecionadoFluxogramaVO.isRemoverLista()){
					listaExamesExcluir.add(exameDisponivelFluxogramaVO);					
				}else{				
					// Verifica se o item não existe na lista
					if (!this.listaExamesSelecionados.contains(exameSelecionadoFluxogramaVO)) {
						this.listaExamesSelecionados.add(exameSelecionadoFluxogramaVO);
					}

					// Por padrão o item não deve ficar selecionado
					exameDisponivelFluxogramaVO.setSelecionado(false);
				}				
			}
		}

		if(!isSelecionado){
			this.apresentarMsgNegocio(Severity.WARN,"MENSAGEM_SELECIONE_MINIMO_UM_ITEM");
		}

		if(listaExamesExcluir.size()>0){
			for (ExameDisponivelFluxogramaVO exameDisponivelFluxogramaVOExcluir : listaExamesExcluir) {
				this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_CAMPO_LAUDO_NAO_PERTENCE_FLUXO", exameDisponivelFluxogramaVOExcluir.getCampoLaudo().getNome());	
				this.listaExamesDisponiveis.remove(exameDisponivelFluxogramaVOExcluir);
			}
		}
	}
	
	/**
	 * Instancia um ExameSelecionadoFluxogramaVO através de uma instância de ExameDisponivelFluxogramaVO
	 * @param exameDisponivelFluxogramaVO
	 * @return
	 */
	public ExameSelecionadoFluxogramaVO getExameSelecionadoFluxogramaVO(ExameDisponivelFluxogramaVO exameDisponivelFluxogramaVO){
		
		ExameSelecionadoFluxogramaVO exameSelecionadoFluxogramaVO = new ExameSelecionadoFluxogramaVO();
		
		exameSelecionadoFluxogramaVO.setSeqCampoLaudo(exameDisponivelFluxogramaVO.getCampoLaudo().getSeq());

		AelServidorCampoLaudo servidorCampoLaudo = new AelServidorCampoLaudo();
		
		servidorCampoLaudo.setServidor(this.servidorLogado);
		
		servidorCampoLaudo.setCampoLaudo(exameDisponivelFluxogramaVO.getCampoLaudo());
		
		servidorCampoLaudo.setNomeSumario(StringUtils.trim(exameDisponivelFluxogramaVO.getCampoLaudo().getNomeSumario().toUpperCase()));
		servidorCampoLaudo.setOrdem(exameDisponivelFluxogramaVO.getCampoLaudo().getOrdem());
		
		exameSelecionadoFluxogramaVO.setServidorCampoLaudo(servidorCampoLaudo);
		exameSelecionadoFluxogramaVO.setSelecionado(false);
		
		//Verifica se o campo laudo pertence ao fluxograma fazendo nova pesquisa em objeto "desatachado"
		boolean pertenceAoFluxoGrama = examesFacade.pertenceAoFluxograma(exameDisponivelFluxogramaVO.getCampoLaudo().getSeq());
		
		if(pertenceAoFluxoGrama){
			exameSelecionadoFluxogramaVO.setRemoverLista(false);
		}else{
			exameSelecionadoFluxogramaVO.setRemoverLista(true);
		}
		
		return exameSelecionadoFluxogramaVO;
	}
	

	/**
	 * Remove exame selecionado
	 */
	public void removerExames() {
		
		boolean isSelecionado = false;
		
		final List<ExameSelecionadoFluxogramaVO> removidos = new ArrayList<ExameSelecionadoFluxogramaVO>();
		
		// Restaura atributos modificados nos Exames Selecionados
		for (ExameSelecionadoFluxogramaVO vo : this.listaExamesSelecionados) {	
			if(vo.isSelecionado()){
				isSelecionado = true;
				removidos.add(vo);
			}
		}
		
		if(!isSelecionado){
			this.apresentarMsgNegocio(Severity.WARN,"MENSAGEM_SELECIONE_MINIMO_UM_ITEM");
		}
		
		this.listaExamesSelecionados.removeAll(removidos);
	}
	
	/**
	 * Adiciona todos exames da lista de Exames Disponíveis para Fluxograma
	 */
	public void adicionarTodosExames() {
		
		List<ExameDisponivelFluxogramaVO> listaExamesExcluir = new LinkedList<ExameDisponivelFluxogramaVO>();

		for (ExameDisponivelFluxogramaVO exameDisponivelFluxogramaVO : this.listaExamesDisponiveis) {

			final ExameSelecionadoFluxogramaVO exameSelecionadoFluxogramaVO = this.getExameSelecionadoFluxogramaVO(exameDisponivelFluxogramaVO);

			if(exameSelecionadoFluxogramaVO.isRemoverLista()){
				listaExamesExcluir.add(exameDisponivelFluxogramaVO);					
			}else{				
				// Verifica se o item não existe na lista
				if (!this.listaExamesSelecionados.contains(exameSelecionadoFluxogramaVO)) {
					this.listaExamesSelecionados.add(exameSelecionadoFluxogramaVO);
				}
				// Por padrão o item não deve ficar selecionado
				exameDisponivelFluxogramaVO.setSelecionado(false);
			}
		}

		if(listaExamesExcluir.size()>0){
			for (ExameDisponivelFluxogramaVO exameDisponivelFluxogramaVOExcluir : listaExamesExcluir) {
				this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_CAMPO_LAUDO_NAO_PERTENCE_FLUXO", exameDisponivelFluxogramaVOExcluir.getCampoLaudo().getNome());
				this.listaExamesDisponiveis.remove(exameDisponivelFluxogramaVOExcluir);
			}
		}
	}
	
	/**
	 * Remove todos exames lista de Exames Selecionados
	 */
	public void removerTodosExames() {
		this.listaExamesSelecionados.clear();
	}

	/**
	 * Grava lista de Exames Selecionados
	 * @return
	 */
	public void gravar() {

		for (ExameSelecionadoFluxogramaVO item : this.listaExamesSelecionados) {	
			if(StringUtils.isEmpty(item.getServidorCampoLaudo().getNomeSumario()) || item.getServidorCampoLaudo().getOrdem() == null){
				this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_CONFIGURAR_FLUXOGRAMA_NOME_SUMARIO_ORDEM_INVALIDOS");
			}
		}

		try {
			this.examesFacade.persistirExamesFluxogramaSelecionados(this.servidorLogado, this.listaExamesSelecionados);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		this.listaExamesDisponiveis = this.examesFacade.pesquisarExamesDisponiveisFluxograma();
		this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_SALVOS_CONFIGURAR_FLUXOGRAMA" );

	}

	
	/*
	 * Getters e setters
	 */

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}
	
	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}
	
	public List<ExameDisponivelFluxogramaVO> getListaExamesDisponiveis() {
		return listaExamesDisponiveis;
	}
	
	public void setListaExamesDisponiveis(List<ExameDisponivelFluxogramaVO> listaExamesDisponiveis) {
		this.listaExamesDisponiveis = listaExamesDisponiveis;
	}
	
	public List<ExameSelecionadoFluxogramaVO> getListaExamesSelecionados() {
		return listaExamesSelecionados;
	}
	
	public void setListaExamesSelecionados(List<ExameSelecionadoFluxogramaVO> listaExamesSelecionados) {
		this.listaExamesSelecionados = listaExamesSelecionados;
	}

}