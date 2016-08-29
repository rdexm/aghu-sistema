package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.AutorizacaoFornecimentoEmailAtrasoVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.AutorizacaoFornecimentoEmailVO;
import br.gov.mec.aghu.compras.vo.ParcelasAFPendEntVO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class AutorizacaoFornecimentoEmailAtrasoController extends ActionController  {

	private static final String CONSULTAR_PARCELAS_AF_PEND_ENTREGA = "consultarParcelasAFPendEntrega";

	private static final long serialVersionUID = 6810844663694302340L;

	//parametros
	private Integer numeroAutorizacaoFornecedor;
	
	private Boolean enviarFornecedor = Boolean.FALSE;
	private Boolean enviarUsuarioLogado = Boolean.FALSE;
	private Boolean enviarCopia = Boolean.FALSE;
	private Boolean anexarPlanilha = Boolean.FALSE;
	private Boolean exibirEmailCopia = Boolean.FALSE;	
	private AutorizacaoFornecimentoEmailAtrasoVO autorizacaoFornecimentoEmailAtrasoVO;
	private AutorizacaoFornecimentoEmailVO dadosEmail;
	private List<ParcelasAFPendEntVO> afsSelecionadas;
	private String emailFornecedor;
	private String emailCopia;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {

		setEnviarFornecedor(true);
		setEnviarUsuarioLogado(true);
		setEnviarCopia(false);
		setAnexarPlanilha(false);
		setExibirEmailCopia(false);
		
		RapServidores servidorLogado;
		try {
			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			
			numeroAutorizacaoFornecedor =afsSelecionadas.get(0).getNumeroAFN(); //20564;
			autorizacaoFornecimentoEmailAtrasoVO = autFornecimentoFacade.carregarLabelsParaTelaAutFornEmailAtraso(numeroAutorizacaoFornecedor);
			dadosEmail = autFornecimentoFacade.montarCorpoEmail(afsSelecionadas);
			dadosEmail.setEmailUsuarioLogado(servidorLogado.getEmail());

		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	
	}
	
	
	public void enviarEmailFornecedor(){
		dadosEmail.setEmailCopia(emailCopia);
		dadosEmail.setEmailFornecedor(autorizacaoFornecimentoEmailAtrasoVO.getContatoFornecedor().getEMail());
		dadosEmail.setAnexarPlanilha(anexarPlanilha);
		dadosEmail.setEnviarCopia(enviarCopia);
		dadosEmail.setEnviarFornecedor(enviarFornecedor);
		dadosEmail.setEnviarUsuarioLogado(enviarUsuarioLogado);
		dadosEmail.setMostrarMensagem(true);
		try {
			autFornecimentoFacade.enviarEmailFornecedor(dadosEmail);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void enviarEmailMultiplosFornecedores() {
		try {
			this.autFornecimentoFacade.enviarEmailMultiplosFornecedores(afsSelecionadas);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public String voltar() {
		return CONSULTAR_PARCELAS_AF_PEND_ENTREGA;
	}
	
	public void exibirEnviarEmailCopia() {
		setExibirEmailCopia(!exibirEmailCopia);
	}
	
	public Boolean getEnviarUsuarioLogado() {
		return enviarUsuarioLogado;
	}

	public void setEnviarUsuarioLogado(Boolean enviarUsuarioLogado) {
		this.enviarUsuarioLogado = enviarUsuarioLogado;
	}

	public Boolean getEnviarCopia() {
		return enviarCopia;
	}

	public void setEnviarCopia(Boolean enviarCopia) {
		this.enviarCopia = enviarCopia;
	}

	public Boolean getAnexarPlanilha() {
		return anexarPlanilha;
	}

	public void setAnexarPlanilha(Boolean anexarPlanilha) {
		this.anexarPlanilha = anexarPlanilha;
	}

	public void setEnviarFornecedor(Boolean enviarFornecedor) {
		this.enviarFornecedor = enviarFornecedor;
	}

	public Boolean getEnviarFornecedor() {
		return enviarFornecedor;
	}

	public AutorizacaoFornecimentoEmailAtrasoVO getAutorizacaoFornecimentoEmailAtrasoVO() {
		return autorizacaoFornecimentoEmailAtrasoVO;
	}

	public void setAutorizacaoFornecimentoEmailAtrasoVO(
			AutorizacaoFornecimentoEmailAtrasoVO autorizacaoFornecimentoEmailAtrasoVO) {
		this.autorizacaoFornecimentoEmailAtrasoVO = autorizacaoFornecimentoEmailAtrasoVO;
	}

	public Integer getNumeroAutorizacaoFornecedor() {
		return numeroAutorizacaoFornecedor;
	}

	public void setNumeroAutorizacaoFornecedor(Integer numeroAutorizacaoFornecedor) {
		this.numeroAutorizacaoFornecedor = numeroAutorizacaoFornecedor;
	}



	public Boolean getExibirEmailCopia() {
		return exibirEmailCopia;
	}



	public void setExibirEmailCopia(Boolean exibirEmailCopia) {
		this.exibirEmailCopia = exibirEmailCopia;
	}

	
	
	public String getEmailFornecedor() {
		return emailFornecedor;
	}

	public void setEmailFornecedor(String emailFornecedor) {
		this.emailFornecedor = emailFornecedor;
	}

	public String getEmailCopia() {
		return emailCopia;
	}

	public void setEmailCopia(String emailCopia) {
		this.emailCopia = emailCopia;
	}

	public AutorizacaoFornecimentoEmailVO getDadosEmail() {
		return dadosEmail;
	}

	public void setDadosEmail(AutorizacaoFornecimentoEmailVO dadosEmail) {
		this.dadosEmail = dadosEmail;
	}

	public List<ParcelasAFPendEntVO> getAfsSelecionadas() {
		return afsSelecionadas;
	}

	public void setAfsSelecionadas(List<ParcelasAFPendEntVO> afsSelecionadas) {
		this.afsSelecionadas = afsSelecionadas;
	}
}
