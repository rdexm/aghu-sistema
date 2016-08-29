package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipCidadesDistritoSanitario;
import br.gov.mec.aghu.model.AipCidadesDistritoSanitarioId;
import br.gov.mec.aghu.model.AipDistritoSanitarios;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class DistritoSanitarioController extends ActionController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5150607356497585331L;
	
	private static final String REDIRECIONA_PESQUISAR_DISTRITO_SANITARIO = "distritoSanitarioList";

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	private AipDistritoSanitarios distritoSanitario;
	
	private AipCidades cidade;
	
	private List<AipCidadesDistritoSanitario> cidades;	
	
	private boolean exibirTabelaResultados = false;
	
	private boolean exibirBotaoNovo = false;
	
	private List<AipCidadesDistritoSanitario> cidadesInseridas;	
	private List<AipCidadesDistritoSanitario> cidadesExcluidas;		
	
	
	@PostConstruct
	public void iniciar() {
	 

		cidadesInseridas = new ArrayList<>();
		cidadesExcluidas = new ArrayList<>();
		
		if (distritoSanitario == null) { // Inclusão
			distritoSanitario = new AipDistritoSanitarios();
			this.cidades = new ArrayList<>();
		} else { // Edição
			this.cidades = cadastrosBasicosPacienteFacade.obterAipCidadesDistritoSanitarioPorAipDistritoSanitario(distritoSanitario.getCodigo());
		}
	
	}
	
	public String salvar() {
		try{
			Boolean novoDistrito = Boolean.FALSE;
			if (distritoSanitario.getCodigo() == null){
				novoDistrito = Boolean.TRUE;
			}
			cadastrosBasicosPacienteFacade.persistDistritoSanitario(distritoSanitario, cidadesInseridas, cidadesExcluidas);
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_PERSISTIR_DISTRITO_SANITARIO", this.distritoSanitario.getDescricao());
			
			this.setCidade(null);
			exibirTabelaResultados = true;
			exibirBotaoNovo = false;
			
			if(novoDistrito){
				return null;
			}
			distritoSanitario = new AipDistritoSanitarios();
			return cancelar();
		}
		catch (final ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String cancelar() {
		distritoSanitario = new AipDistritoSanitarios();
		cidadesInseridas = null;
		cidadesExcluidas = null;

		this.setCidade(null);
		exibirTabelaResultados = true;
		exibirBotaoNovo = true;
		return REDIRECIONA_PESQUISAR_DISTRITO_SANITARIO;
	}
	
	public void removerCidade(final AipCidadesDistritoSanitario acds) {
		cidadesExcluidas.add(acds);
		cidadesInseridas.remove(acds);
		cidades.remove(acds);
	}
	
	public void limparCidade() {
		this.setCidade(null);
		this.cidade = null;				
	}
	
	public void associarCidade() {
		
		if (this.cidade == null) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_CIDADE_NAO_ASSOCIADA");
			
		} else { 
			final AipCidadesDistritoSanitarioId id = new AipCidadesDistritoSanitarioId(cidade.getCodigo(), distritoSanitario.getCodigo());
			
			if (cidades.indexOf(new AipCidadesDistritoSanitario(id)) >= 0 ) {
				apresentarMsgNegocio(Severity.ERROR, "CIDADE_JA_ASSOCIADA");
				
			} else {
				AipCidadesDistritoSanitario ads = new AipCidadesDistritoSanitario(id, cidade); 
				cidades.add(ads);
				cidadesInseridas.add(ads);
			}
			cidade = null;
		}
	}
	
	public List<AipCidades> pesquisarCidade(final String nome) {
		return cadastrosBasicosPacienteFacade.pesquisarCidadePorNome(nome);
	}
	
	
	public Long contarCidade(String  str){
		return cadastrosBasicosPacienteFacade.obterCidadeCount(null, null, (String)str, DominioSituacao.A, null, null, null);
	}
	
	public boolean isMostrarLinkExcluirCidade(){
		return this.cidade != null && this.cidade.getCodigo() != null;
	}
		
	
	public String getDescricaoCidade(){
		
		if(this.cidade == null || cidade.getAipUf() == null ) {
			return "";
		}
		return cidade.getNome() + " - " + cidade.getAipUf().getSigla();
	}
	
	
	//GETTERS e SETTERS

	public AipDistritoSanitarios getDistritoSanitario() {
		return this.distritoSanitario;
	}
	
	public void setDistritoSanitario(final AipDistritoSanitarios distritoSanitario) {
		this.distritoSanitario = distritoSanitario;
	}	
	
	public void setCidade(final AipCidades cidade) {
		this.cidade = cidade;
	}

	public AipCidades getCidade() {
		return cidade;
	}
	
	public boolean isExibirTabelaResultados() {
		return exibirTabelaResultados;
	}

	public void setExibirTabelaResultados(final boolean exibirTabelaResultados) {
		this.exibirTabelaResultados = exibirTabelaResultados;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(final boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public String getNomeCidadeSelecionada()	{
		
		if(this.cidade.getNome() == null || StringUtils.isBlank(this.cidade.getNome())){
			return "";
		}
		
		return this.cidade.getNome();
	}

	public List<AipCidadesDistritoSanitario> getCidades() {
		return cidades;
	}

	public void setCidades(List<AipCidadesDistritoSanitario> cidades) {
		this.cidades = cidades;
	}

	public List<AipCidadesDistritoSanitario> getCidadesInseridas() {
		return cidadesInseridas;
	}

	public void setCidadesInseridas(
			List<AipCidadesDistritoSanitario> cidadesInseridas) {
		this.cidadesInseridas = cidadesInseridas;
	}

	public List<AipCidadesDistritoSanitario> getCidadesExcluidas() {
		return cidadesExcluidas;
	}

	public void setCidadesExcluidas(
			List<AipCidadesDistritoSanitario> cidadesExcluidas) {
		this.cidadesExcluidas = cidadesExcluidas;
	}
 
}