package br.gov.mec.aghu.prescricaomedica.modelobasico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterModeloBasicoController extends ActionController {

	private static final long serialVersionUID = 8418460090113378292L;
	
	private static final String PAGE_MANTER_MODELO_BASICO = "manterModeloBasico";
	private static final String PAGE_PESQUISAR_MODELO_BASICO_PUBLICO = "pesquisarModeloBasicoPublico";
	private static final String PAGE_MANTER_ITENS_MODELO_BASICO = "manterItensModeloBasico";

	@EJB
	private IModeloBasicoFacade modeloBasicoFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private RapServidores servidor;

	/**
	 * Objeto do formulário para edição, inclusão ou exclusão.
	 */
	private MpmModeloBasicoPrescricao modeloBasico = new MpmModeloBasicoPrescricao();

	private List<MpmModeloBasicoPrescricao> modelos;

	/**
	 * Parâmetro para exclusão/edição.
	 */
	private Integer modeloBasicoSeq;

	/**
	 * Indica que está em modo de alteração.
	 */
	private boolean alterar = false;

	private String origem;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public String iniciar() {
		this.modeloBasico = new MpmModeloBasicoPrescricao();
		setServidor(servidorLogadoFacade.obterServidorLogadoSemCache());
		this.modelos = this.modeloBasicoFacade.listarModelosBasicos();
		
		return null;
	}
	
	/**
	 * Método que apresenta a tela de edição para alteração ou inclusão.
	 * 
	 * @return
	 */
	public void editar(Integer modeloBasicoSeq) {
		this.alterar = Boolean.TRUE;
		this.modeloBasico = this.modeloBasicoFacade		
				.obterModeloBasico(modeloBasicoSeq);
	}

	
	/**
	 * Salva inclusão ou alteração de registro.
	 */
	public void salvar() {

		try {
			// coloca a string de descrição em uppercase
			this.modeloBasico.setDescricao(this.modeloBasico.getDescricao()
					.toUpperCase().trim());

			if (isAlterar()) {
				this.modeloBasicoFacade.alterar(this.modeloBasico);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_ALTERACAO_MODELO_BASICO");

			} else {
				this.modeloBasicoFacade.inserir(this.modeloBasico);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_INCLUSAO_MODELO_BASICO");
			}

			this.modeloBasico = new MpmModeloBasicoPrescricao();
			this.alterar = false;

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		this.limpar();
	}

	/**
	 * Exclui o objeto do banco de dados.
	 */
	public void excluir(Integer modeloBasicoSeq) {
		try {
			MpmModeloBasicoPrescricao modelo = this.modeloBasicoFacade
					.obterModeloBasico(modeloBasicoSeq);

			if (modelo != null) {
				this.modeloBasicoFacade.excluirModeloBasico(modeloBasicoSeq);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EXCLUSAO_MODELO_BASICO_PRESCRICAO",
						modelo.getSeq());
				this.limpar();
			} else {
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_MODELO_BASICO_PRESCRICAO_INEXISTENTE");
				this.limpar();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método para limpar o formulário da tela.
	 */
	public String limpar() {
		this.modeloBasico = new MpmModeloBasicoPrescricao();
		this.alterar = false;
		this.modelos = this.modeloBasicoFacade.listarModelosBasicos();
		
		return PAGE_MANTER_MODELO_BASICO;
	}

	/**
	 * Redireciona para a página de modelos públicos
	 * 
	 * @return
	 */
	public String copiarModelo() {
		return PAGE_PESQUISAR_MODELO_BASICO_PUBLICO;

	}
	
	public String visualizarItens() {
		return PAGE_MANTER_ITENS_MODELO_BASICO;
	}

	// getters & setters

	public MpmModeloBasicoPrescricao getModeloBasico() {
		return modeloBasico;
	}

	public void setModeloBasico(MpmModeloBasicoPrescricao modeloBasico) {
		this.modeloBasico = modeloBasico;
	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

	public Integer getModeloBasicoSeq() {
		return modeloBasicoSeq;
	}

	public void setModeloBasicoSeq(Integer modeloBasicoSeq) {
		this.modeloBasicoSeq = modeloBasicoSeq;
	}

	public List<MpmModeloBasicoPrescricao> getModelos() {
		return modelos;
	}

	public void setModelos(List<MpmModeloBasicoPrescricao> modelos) {
		this.modelos = modelos;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
   
	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}
	

}
