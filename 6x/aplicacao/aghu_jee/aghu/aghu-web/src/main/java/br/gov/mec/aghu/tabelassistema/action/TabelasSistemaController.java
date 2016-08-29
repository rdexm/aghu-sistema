package br.gov.mec.aghu.tabelassistema.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Menu;
import br.gov.mec.aghu.model.AghCoresTabelasSistema;
import br.gov.mec.aghu.model.AghTabelasSistema;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class TabelasSistemaController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7629999334507381502L;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	private static final String PAGE_LIST = "tabelasSistemaList";
	
	private AghTabelasSistema tabelaSistema = new AghTabelasSistema();
	
	private boolean mostrarSeq = false;
	
	private RapServidoresVO servidorResponsavelVO;
	
	private Integer seqTabela;

		
	/**
	 * Método chamado ao carregar tela para identificar se a mesma está sendo chamada para edição ou
	 * inclusão de um registro. Caso seja para inclusão a mesma vem em branco, caso contrário os campos
	 * são carregados com os valores do objeto.
	 */
	public void iniciar() {
		if (seqTabela != null){
			this.tabelaSistema = this.aghuFacade.obterTabelaSistema(seqTabela);
			RapServidores responsavel = this.tabelaSistema.getServidorResponsavel();
			if (responsavel != null) {
				servidorResponsavelVO = new RapServidoresVO(
						responsavel.getId().getVinCodigo(),
						responsavel.getId().getMatricula(),
						responsavel.getPessoaFisica().getNome());
			}
			this.mostrarSeq = true;
		}	
		else{
			this.tabelaSistema = new AghTabelasSistema();
			this.mostrarSeq = false;
		} 
	}
	
	/**
	 * Método chamado na tela para inserir/editar Tabela quando usuário clicar no botão Salvar.
	 * 
	 * @return String para redirecionamento
	 */
	public String confirmar() {
		try {
					
			//Salva registro de Tabela
			Integer seq = this.tabelaSistema.getSeq();
			
			if (seq == null){
				//Seta a data de criação e o servidor que criou
				tabelaSistema.setCriadoEm(new Date());
				tabelaSistema.setServidorCriacao(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			}
			
			if (servidorResponsavelVO != null){
				//Obtém o servidor responsável informado
				RapServidores responsavel = registroColaboradorFacade.obterRapServidoresPorChavePrimaria(new RapServidoresId(
						this.servidorResponsavelVO.getMatricula(),
						this.servidorResponsavelVO.getVinculo()));
				tabelaSistema.setServidorResponsavel(responsavel);
			}
			
			//Seta a data de última alteração e o servidor que atualizou
			tabelaSistema.setDataUltimaAlteracao(new Date());
			tabelaSistema.setServidorUltAlteracao(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			
			this.aghuFacade.persistirTabelaSistema(this.tabelaSistema);
			
			//Seta mensagem a ser apresentada na tela (para inclusao/edicao)
			if (seq == null) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_TABELA_SISTEMA");
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_TABELA_SISTEMA");
			}
			
			//Retorna string para redirecionar tela para pesquisa de cidade
			limpar();
			return PAGE_LIST;
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public List<RapServidoresVO> pesquisarRapServidoresVOPorCodigoDescricao(String parametro){
		return registroColaboradorFacade.pesquisarRapServidoresVOPorCodigoDescricao(parametro);
	}
	
	public List<AghCoresTabelasSistema> pesquisarCoresTabelasSistema(String parametro){
		return aghuFacade.pesquisarCoresTabelasSistema(parametro);
	}
	
	public List<Menu> pesquisarMenusTabelasSistema(String parametro) {
		return this.cascaFacade.pesquisarMenusTabelasSistema(parametro);
	}
	
	public Long pesquisarCountMenusTabelasSistema(String parametro) {
		return this.cascaFacade.pesquisarCountMenusTabelasSistema(parametro);
	}
	
	/**
	 * Método chamado pelo usuário para cancelar a inserção/edição do registro e retornar a
	 * tela de pesquisa de cidades
	 * 
	 * @return String com redirecionamento
	 */
	public String cancelar() {
		this.limpar();		
		return PAGE_LIST;
	}
	
	/**
	 * Método para limpar conteúdo das variáveis da tela.
	 */
	private void limpar() {
		this.tabelaSistema = new AghTabelasSistema();
	}
	
	public AghTabelasSistema getTabelaSistema() {
		return tabelaSistema;
	}

	public void setTabelaSistema(AghTabelasSistema tabelaSistema) {
		this.tabelaSistema = tabelaSistema;
	}

	public boolean isMostrarSeq() {
		return mostrarSeq;
	}

	public void setMostrarSeq(boolean mostrarSeq) {
		this.mostrarSeq = mostrarSeq;
	}

	public RapServidoresVO getServidorResponsavelVO() {
		return servidorResponsavelVO;
	}

	public void setServidorResponsavelVO(RapServidoresVO servidorResponsavelVO) {
		this.servidorResponsavelVO = servidorResponsavelVO;
	}

	public Integer getSeqTabela() {
		return seqTabela;
	}

	public void setSeqTabela(Integer seqTabela) {
		this.seqTabela = seqTabela;
	}

}
