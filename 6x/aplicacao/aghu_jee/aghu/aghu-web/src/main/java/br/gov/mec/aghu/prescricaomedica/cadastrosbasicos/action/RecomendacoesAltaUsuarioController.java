package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmServRecomendacaoAlta;
import br.gov.mec.aghu.model.MpmServRecomendacaoAltaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class RecomendacoesAltaUsuarioController extends ActionController {

	private static final long serialVersionUID = -2185461852245767983L;

	private static final String PAGE_PESQUISA_RECOMENDACOES_ALTA_POR_USUARIO = "recomendacoesAltaPorUsuarioList";

	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private MpmServRecomendacaoAlta recomendacao;

	private DominioSituacao indSituacao;
	
	private boolean desabilitarCodigo;
	private boolean hiddenCodigo;
	private boolean exigeComplemento;
	private boolean outros;

	@PostConstruct
	public void init() {
		this.begin(conversation);
		
		if (recomendacao == null) {
			recomendacao = new MpmServRecomendacaoAlta();
		}
	}

    public void inicio() {
        if (recomendacao.getIndSituacao() == null) {
            recomendacao.setIndSituacao(DominioSituacao.A);
        }
    }

	/**
	 * Testa campos em branco
	 * 
	 * @param unidade
	 * @return
	 */
	private boolean validaCamposRequeridosEmBranco(MpmServRecomendacaoAlta recomendacao) {
		boolean retorno = true;
		if (recomendacao != null && StringUtils.isBlank(recomendacao.getDescricao())) {
			recomendacao.setDescricao(null);
			retorno = false;
			this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Descrição");
		}
		return retorno;
	}

	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de
	 * recomendações na alta
	 */
	public String confirmar() {
		try {

			if (validaCamposRequeridosEmBranco(this.recomendacao)) {
				// Tarefa 659 - deixar todos textos das entidades em caixa alta
				// via toUpperCase()
				// transformarTextosCaixaAlta();
				// Issue 1209 retirar o caixa alta

				boolean create = this.recomendacao.getId() == null || this.recomendacao.getId().getSeqp() == null;
				RapServidores serv;
				try {
					serv = registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
				} catch (ApplicationBusinessException e) {
					serv = null;
				}

				if (create) {
					this.recomendacao.setId(new MpmServRecomendacaoAltaId(serv.getId().getMatricula(), serv.getId().getVinCodigo(), null));
					this.recomendacao.setCriadoEm(new Date());					
				}

				this.recomendacao.setServidor(serv);
				
				this.cadastrosBasicosPrescricaoMedicaFacade.persistRecomendacaoAlta(this.recomendacao);

				if (create) {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_RECOMENDACAO_POR_USUARIO", this.recomendacao.getDescricao());
				} else {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_RECOMENDACAO_POR_USUARIO", this.recomendacao.getDescricao());
				}

				this.desabilitarCodigo = false;
				this.hiddenCodigo = false;
				this.recomendacao = new MpmServRecomendacaoAlta();
			} else {
				return null;
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return PAGE_PESQUISA_RECOMENDACOES_ALTA_POR_USUARIO;
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * Acomodação
	 */
	public String cancelar() {
		this.recomendacao = new MpmServRecomendacaoAlta();
		this.desabilitarCodigo = false;
		this.hiddenCodigo = false;
		return PAGE_PESQUISA_RECOMENDACOES_ALTA_POR_USUARIO;
	}

	public boolean isDesabilitarCodigo() {
		return desabilitarCodigo;
	}

	public void setDesabilitarCodigo(boolean desabilitarCodigo) {
		this.desabilitarCodigo = desabilitarCodigo;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public boolean isExigeComplemento() {
		return exigeComplemento;
	}

	public void setExigeComplemento(boolean exigeComplemento) {
		this.exigeComplemento = exigeComplemento;
	}

	public boolean isOutros() {
		return outros;
	}

	public void setOutros(boolean outros) {
		this.outros = outros;
	}

	public boolean isHiddenCodigo() {
		return hiddenCodigo;
	}

	public void setHiddenCodigo(boolean hiddenCodigo) {
		this.hiddenCodigo = hiddenCodigo;
	}

	public MpmServRecomendacaoAlta getRecomendacao() {
		return recomendacao;
	}

	public void setRecomendacao(MpmServRecomendacaoAlta recomendacao) {
		this.recomendacao = recomendacao;
	}

}
