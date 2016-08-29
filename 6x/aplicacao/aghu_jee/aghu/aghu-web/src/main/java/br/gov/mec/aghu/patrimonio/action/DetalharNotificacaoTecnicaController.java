package br.gov.mec.aghu.patrimonio.action;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;


public class DetalharNotificacaoTecnicaController extends ActionController{
	
	private static final long serialVersionUID = 4504741579253343972L;
	
	private static final String PAGINA_CONSULTAR_NOTIFICACOES_TECNICAS = "patrimonio-listaNotificacaoTecnica";
	private static final String PAGINA_IMPRESSAO_NOTIFICACAO_TECNICA = "patrimonio-imprimirNotificacaoTecnica";
	private static final String PAGINA_DETALHAR_NOTIFICACAO_TECNICA = "patrimonio-detalharNotificacaoTecnica";
	
	@Inject
	private RelatorioNotificacaoTecnicaController relatorioNotificacaoTecnicaController;
	
	private String tipo;
	private String dataCriacao;
	private String descricao;	
	
	private Long seq;
	
	@PostConstruct
	protected void inicializar() {
		begin(conversation);		
	}	
	
	public String voltar(){
		return PAGINA_CONSULTAR_NOTIFICACOES_TECNICAS;
	}
	
	/**
	 * Ação do botão Visualizar.
	 */
	public String visualizar() {
		relatorioNotificacaoTecnicaController.setPntSeq(seq);
		relatorioNotificacaoTecnicaController.setVoltarPara(PAGINA_DETALHAR_NOTIFICACAO_TECNICA);
		return PAGINA_IMPRESSAO_NOTIFICACAO_TECNICA;
	}
	
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(String dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	
}
