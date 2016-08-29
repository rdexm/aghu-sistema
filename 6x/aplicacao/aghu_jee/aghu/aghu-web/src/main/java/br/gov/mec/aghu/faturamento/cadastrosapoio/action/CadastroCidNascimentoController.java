package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioVivoMorto;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.FatCadCidNascimento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar o Cadastro de CIDs por Nascimento
 * 
 * @author rafael.silvestre
 */
public class CadastroCidNascimentoController extends ActionController {

	private static final long serialVersionUID = -4594793930661131068L;

	private static final String PAGE_CADASTRO_CID_NASCIMENTO_PESQUISA = "faturamento-cadastroCidNascimentoList";

	private static final String MENSAGEM_SUCESSO_EDICAO = "MENSAGEM_SUCESSO_EDICAO_CID_NASCIMENTO";
	
	private static final String MENSAGEM_SUCESSO_INCLUSAO = "MENSAGEM_SUCESSO_INCLUSAO_CID_NASCIMENTO";
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	private DominioVivoMorto vivo;
	
	private DominioVivoMorto morto;

	private FatCadCidNascimento fatCadCidNascimento;
	
	private boolean modoEdicao;
	
	@PostConstruct
	protected void inicializar() {
		
		begin(conversation);
	}
	
	/**
	 * Método que define o comportamento inicial da tela
	 */
	public void iniciar() {
		
		if (super.isValidInitMethod()) {
		
			limpar();
		}	
	}

	/**
	 * Preenche os valores dos campos conforme caso de inclusão ou alteração
	 */
	private void limpar() {
		
		if (this.modoEdicao) {
			if (this.fatCadCidNascimento != null && this.fatCadCidNascimento.getVivo() != null) {
				this.vivo = this.fatCadCidNascimento.getVivo();
			}
			if (this.fatCadCidNascimento != null && this.fatCadCidNascimento.getMorto() != null) {
				this.morto = this.fatCadCidNascimento.getMorto();
			}
		} else {
			this.vivo = null;
			this.morto = null;
			this.fatCadCidNascimento = new FatCadCidNascimento();
		}
	}
	
	/**
	 * Ação do botão Gravar.
	 */
	public void gravar() {
		
		try {

			this.fatCadCidNascimento.setVivo(this.vivo);
			this.fatCadCidNascimento.setMorto(this.morto);
			
			this.faturamentoApoioFacade.gravarCidPorNascimento(this.fatCadCidNascimento);
			
			if (this.modoEdicao) {
				
				apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_EDICAO);
				
			} else {
			
				apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_INCLUSAO);
			}
			
			limpar();
			
		} catch (ApplicationBusinessException e) {
			
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Método responsável pela navegação para a página anterior.
	 * 
	 * @return
	 */
	public String voltar() {
		
		this.vivo = null;
		this.morto = null;
		this.fatCadCidNascimento = null;
		this.modoEdicao = Boolean.FALSE;
		
		return PAGE_CADASTRO_CID_NASCIMENTO_PESQUISA;
	}
	
	/**
	 * 
	 * GET's e SET's
	 * 
	 */
	public FatCadCidNascimento getFatCadCidNascimento() {
		return fatCadCidNascimento;
	}

	public void setFatCadCidNascimento(FatCadCidNascimento fatCadCidNascimento) {
		this.fatCadCidNascimento = fatCadCidNascimento;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public DominioVivoMorto getVivo() {
		return vivo;
	}

	public void setVivo(DominioVivoMorto vivo) {
		this.vivo = vivo;
	}

	public DominioVivoMorto getMorto() {
		return morto;
	}

	public void setMorto(DominioVivoMorto morto) {
		this.morto = morto;
	}
}
